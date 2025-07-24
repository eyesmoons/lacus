package com.lacus.domain.dig.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacus.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TaskOptionUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T extends TransformOptions> T getTransformOption(
            Transform transform, String transformOptionsStr) throws IOException {
        switch (transform) {
            case FIELDMAPPER:
                return convertTransformStrToOptions(
                        transformOptionsStr, FieldMapperTransformOptions.class);
            case MULTIFIELDSPLIT:
                return convertTransformStrToOptions(
                        transformOptionsStr, SplitTransformOptions.class);
            case COPY:
                return convertTransformStrToOptions(
                        transformOptionsStr, CopyTransformOptions.class);
            case SQL:
                return convertTransformStrToOptions(transformOptionsStr, SQLTransformOptions.class);
            case FILTERROWKIND:
            case REPLACE:
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends TransformOptions> T convertTransformStrToOptions(
            String transformOptionsStr, Class<? extends TransformOptions> optionClass)
            throws IOException {
        if (StringUtils.isEmpty(transformOptionsStr)) {
            throw new CustomException(optionClass.getName() + " transformOptions can not be empty");
        }
        return (T) OBJECT_MAPPER.readValue(transformOptionsStr, optionClass);
    }
}
