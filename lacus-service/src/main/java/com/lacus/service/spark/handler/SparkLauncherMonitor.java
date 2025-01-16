package com.lacus.service.spark.handler;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lacus.enums.SparkStatusEnum;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lacus.common.constant.Constants.DEFAULT_SUBMIT_TIMEOUT_MS;

public class SparkLauncherMonitor extends Thread {
    private static final Logger logger = LogManager.getLogger(SparkLauncherMonitor.class);

    private final SparkAppHandler handle;
    private final Process process;

    private long submitTimeoutMs;
    private boolean isStop;
    private OutputStream outputStream;

    public static SparkLauncherMonitor createLogMonitor(SparkAppHandler handle) {
        return new SparkLauncherMonitor(handle);
    }

    public SparkLauncherMonitor(SparkAppHandler handle) {
        this.handle = handle;
        this.process = handle.getProcess();
        this.isStop = false;
        setSubmitTimeoutMs(DEFAULT_SUBMIT_TIMEOUT_MS);
    }

    public long getSubmitTimeoutMs() {
        return submitTimeoutMs;
    }

    public void setSubmitTimeoutMs(long submitTimeoutMs) {
        this.submitTimeoutMs = submitTimeoutMs;
    }

    public void setRedirectLogPath(String redirectLogPath) throws IOException {
        this.outputStream = new FileOutputStream(new File(redirectLogPath), false);
        this.handle.setLogPath(redirectLogPath);
    }

    @Override
    public void run() {
        if (handle.getState() == SparkStatusEnum.KILLED) {
            process.destroyForcibly();
            return;
        }
        BufferedReader outReader = null;
        String line;
        long startTime = System.currentTimeMillis();
        try {
            outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (!isStop && (line = outReader.readLine()) != null) {
                if (outputStream != null) {
                    outputStream.write((line + "\n").getBytes());
                }
                SparkStatusEnum oldState = handle.getState();
                SparkStatusEnum newState = oldState;
                // parse state and appId
                if (line.contains("state")) {
                    // 1. state
                    String state = regexGetState(line);
                    if (state != null) {
                        YarnApplicationState yarnState = YarnApplicationState.valueOf(state);
                        newState = fromYarnState(yarnState);
                        if (newState != oldState) {
                            handle.setState(newState);
                        }
                    }
                    // 2. appId
                    String appId = regexGetAppId(line);
                    if (appId != null) {
                        if (!appId.equals(handle.getAppId())) {
                            handle.setAppId(appId);
                        }
                    }

                    logger.info("spark appId that handle get is {}, state: {}", handle.getAppId(), handle.getState().toString());
                    switch (newState) {
                        case UNKNOWN:
                        case CONNECTED:
                        case SUBMITTED:
                            if (System.currentTimeMillis() - startTime > submitTimeoutMs) {
                                isStop = true;
                                handle.kill();
                            }
                            break;
                        case RUNNING:
                        case FINISHED:
                            isStop = true;
                            break;
                        case KILLED:
                        case FAILED:
                        case LOST:
                            isStop = true;
                            handle.kill();
                            break;
                        default:
                            logger.error("wrong spark app state");
                    }
                } else if (line.contains("queue") || line.contains("start time") || line.contains("final status")
                        || line.contains("tracking URL") || line.contains("user")) { // parse other values
                    String value = getValue(line);
                    if (!Strings.isNullOrEmpty(value)) {
                        try {
                            if (line.contains("queue")) {
                                handle.setQueue(value);
                            } else if (line.contains("start time")) {
                                handle.setStartTime(Long.parseLong(value));
                            } else if (line.contains("final status")) {
                                handle.setFinalStatus(FinalApplicationStatus.valueOf(value));
                            } else if (line.contains("tracking URL")) {
                                handle.setUrl(value);
                            } else if (line.contains("user")) {
                                handle.setUser(value);
                            }
                        } catch (IllegalArgumentException e) {
                            logger.warn("parse log encounter an error, line: {}, msg: {}", line, e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Exception monitoring process.", e);
        } finally {
            try {
                if (outReader != null) {
                    outReader.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.warn("close buffered reader error", e);
            }
        }
    }

    private static String regexGetState(String line) {
        String result = null;
        Matcher stateMatcher = Pattern.compile("(?<=\\(state: )(.+?)(?=\\))").matcher(line);
        if (stateMatcher.find()) {
            result = stateMatcher.group();
        }
        return result;
    }

    private static String regexGetAppId(String line) {
        String result = null;
        Matcher appIdMatcher = Pattern.compile("application_[0-9]+_[0-9]+").matcher(line);
        if (appIdMatcher.find()) {
            result = appIdMatcher.group();
        }
        return result;
    }

    // input: "final status: SUCCEEDED"
    // output: "SUCCEEDED"
    private static String getValue(String line) {
        String result = null;
        List<String> entry = Splitter.onPattern(":").trimResults().limit(2).splitToList(line);
        if (entry.size() == 2) {
            result = entry.get(1);
        }
        return result;
    }

    private static SparkStatusEnum fromYarnState(YarnApplicationState yarnState) {
        switch (yarnState) {
            case SUBMITTED:
            case ACCEPTED:
                return SparkStatusEnum.SUBMITTED;
            case RUNNING:
                return SparkStatusEnum.RUNNING;
            case FINISHED:
                return SparkStatusEnum.FINISHED;
            case FAILED:
                return SparkStatusEnum.FAILED;
            case KILLED:
                return SparkStatusEnum.KILLED;
            default:
                return SparkStatusEnum.UNKNOWN;
        }
    }
}
