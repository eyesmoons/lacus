package com.lacus.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Slf4j
public class SeatunnelUtils {

    private static final Logger logger = Logger.getLogger(SeatunnelUtils.class.getName());

    @Getter
    public static class SeatunnelCommand {
        private final List<String> command;

        public SeatunnelCommand(List<String> command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return String.join(" ", command);
        }
    }

    @Getter
    public static class SeatunnelExecutionResult {
        private final int exitCode;
        private final String output;

        public SeatunnelExecutionResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

    }

    public static SeatunnelCommand buildCommand(String seatunnelHome, String startupScript, String configFilePath, String otherArgs) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(Paths.get(seatunnelHome, "bin", startupScript).toString());
        command.add("--config");
        command.add(configFilePath);

        if (ObjectUtils.isNotEmpty(otherArgs)) {
            command.addAll(convertJsonToArgs(otherArgs));
        }
        return new SeatunnelCommand(command);
    }

    public static List<String> convertJsonToArgs(String jsonString) {
        List<String> result = new ArrayList<>();
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                result.add("--" + key + " " + value);
            }
        } catch (Exception e) {
            log.error("转换出错", e);
        }
        return result;
    }

    public static SeatunnelExecutionResult executeCommand(SeatunnelCommand seatunnelCommand) throws IOException, InterruptedException {
        logger.info("Executing command: " + seatunnelCommand.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(seatunnelCommand.getCommand());
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info(line);
            }
        }

        int exitCode = process.waitFor();
        logger.info("Command exited with code: " + exitCode);

        return new SeatunnelExecutionResult(exitCode, output.toString());
    }

    public static CompletableFuture<SeatunnelExecutionResult> executeCommandAsync(SeatunnelCommand seatunnelCommand,
                                                                                  Consumer<String> logConsumer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing command: " + seatunnelCommand.toString());
                ProcessBuilder processBuilder = new ProcessBuilder(seatunnelCommand.getCommand());
                Process process = processBuilder.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                        if (logConsumer != null) {
                            logConsumer.accept(line);
                        }
                    }
                }

                int exitCode = process.waitFor();
                logger.info("Command exited with code: " + exitCode);

                return new SeatunnelExecutionResult(exitCode, output.toString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to execute command", e);
            }
        });
    }

    private static String createTempConfigFile(String content) throws IOException {
        File tempFile = File.createTempFile("seatunnel-", ".conf");
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
        return tempFile.getAbsolutePath();
    }
}
