package com.lacus.job.flink.function;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
public class MySqlDateTimeConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_TIME;
    private DateTimeFormatter datetimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_DATE_TIME;
 
    private ZoneId timestampZoneId = ZoneId.systemDefault();
 
    @Override
    public void configure(Properties props) {
        readProps(props, "format.date", p -> dateFormatter = DateTimeFormatter.ofPattern(p));
        readProps(props, "format.time", p -> timeFormatter = DateTimeFormatter.ofPattern(p));
        readProps(props, "format.datetime", p -> datetimeFormatter = DateTimeFormatter.ofPattern(p));
        readProps(props, "format.timestamp", p -> timestampFormatter = DateTimeFormatter.ofPattern(p));
        readProps(props, "format.timestamp.zone", z -> timestampZoneId = ZoneId.of(z));
    }
 
    private void readProps(Properties properties, String settingKey, Consumer<String> callback) {
        String settingValue = (String) properties.get(settingKey);
        if (settingValue == null || settingValue.isEmpty()) {
            return;
        }
        try {
            callback.accept(settingValue.trim());
        } catch (IllegalArgumentException | DateTimeException e) {
            log.error("The {} setting is illegal: {}",settingKey,settingValue);
            throw e;
        }
    }

    @Override
    public void converterFor(RelationalColumn column, ConverterRegistration<SchemaBuilder> registration) {
        String sqlType = column.typeName().toUpperCase();
        SchemaBuilder schemaBuilder = null;
        Converter converter = null;
        if ("DATE".equals(sqlType)) {
            schemaBuilder = SchemaBuilder.string().optional().name("com.darcytech.debezium.date.string");
            converter = this::convertDate;
        }
        if ("TIME".equals(sqlType)) {
            schemaBuilder = SchemaBuilder.string().optional().name("com.darcytech.debezium.time.string");
            converter = this::convertTime;
        }
        if ("DATETIME".equals(sqlType)) {
            schemaBuilder = SchemaBuilder.string().optional().name("com.darcytech.debezium.datetime.string");
            converter = this::convertDateTime;
        }
        if ("TIMESTAMP".equals(sqlType)) {
            schemaBuilder = SchemaBuilder.string().optional().name("com.darcytech.debezium.timestamp.string");
            converter = this::convertTimestamp;
        }
        if (schemaBuilder != null) {
            registration.register(schemaBuilder, converter);
        }
    }
 
    private String convertDate(Object input) {
        if (input instanceof LocalDate) {
            return dateFormatter.format((LocalDate) input);
        }
        if (input instanceof Integer) {
            LocalDate date = LocalDate.ofEpochDay((Integer) input);
            return dateFormatter.format(date);
        }
        return null;
    }
 
    private String convertTime(Object input) {
        if (input instanceof Duration) {
            Duration duration = (Duration) input;
            long seconds = duration.getSeconds();
            int nano = duration.getNano();
            LocalTime time = LocalTime.ofSecondOfDay(seconds).withNano(nano);
            return timeFormatter.format(time);
        }
        return null;
    }
 
    private String convertDateTime(Object input) {
        if (input instanceof LocalDateTime) {
            return datetimeFormatter.format((LocalDateTime) input);
        } else if (input instanceof Timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = sdf.parse(input.toString());
                 return sdf.format(parse);
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }
 
    private String convertTimestamp(Object input) {
        if (input instanceof ZonedDateTime) {
            // mysql的timestamp会转成UTC存储，这里的zonedDatetime都是UTC时间
            ZonedDateTime zonedDateTime = (ZonedDateTime) input;
            LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(timestampZoneId).toLocalDateTime();
            return timestampFormatter.format(localDateTime);
        }
        return null;
    }
}