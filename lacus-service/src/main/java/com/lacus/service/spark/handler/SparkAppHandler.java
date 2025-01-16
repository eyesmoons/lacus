package com.lacus.service.spark.handler;

import com.google.common.collect.Lists;
import com.lacus.enums.SparkStatusEnum;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;

import java.io.Serializable;
import java.util.List;

public class SparkAppHandler implements Serializable {
    private Process process;

    private String appId;
    private SparkStatusEnum sparkState;
    private String queue;
    private long startTime;
    private FinalApplicationStatus finalStatus;
    private String trackingUrl;
    private String user;
    private String logPath;

    private List<Listener> listeners;

    public interface Listener {
        void stateChanged(SparkAppHandler handle);

        void infoChanged(SparkAppHandler handle);
    }

    public SparkAppHandler(Process process) {
        this.sparkState = SparkStatusEnum.UNKNOWN;
        this.process = process;
    }

    public SparkAppHandler() {
        this.sparkState = SparkStatusEnum.UNKNOWN;
    }

    public void addListener(Listener listener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }

        this.listeners.add(listener);
    }

    public void stop() {
    }

    public void kill() {
        this.setState(SparkStatusEnum.KILLED);
        if (this.process != null) {
            if (this.process.isAlive()) {
                this.process.destroyForcibly();
            }
            this.process = null;
        }
    }

    public SparkStatusEnum getState() {
        return this.sparkState;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getQueue() {
        return this.queue;
    }

    public Process getProcess() {
        return this.process;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public FinalApplicationStatus getFinalStatus() {
        return this.finalStatus;
    }

    public String getUrl() {
        return this.trackingUrl;
    }

    public String getUser() {
        return this.user;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void setState(SparkStatusEnum sparkState) {
        this.sparkState = sparkState;
        this.fireEvent(false);
    }

    public void setAppId(String appId) {
        this.appId = appId;
        this.fireEvent(true);
    }

    public void setQueue(String queue) {
        this.queue = queue;
        this.fireEvent(true);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        this.fireEvent(true);
    }

    public void setFinalStatus(FinalApplicationStatus status) {
        this.finalStatus = status;
        this.fireEvent(true);
    }

    public void setUrl(String url) {
        this.trackingUrl = url;
        this.fireEvent(true);
    }

    public void setUser(String user) {
        this.user = user;
        this.fireEvent(true);
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
        this.fireEvent(true);
    }

    private void fireEvent(boolean isInfoChanged) {
        if (this.listeners != null) {
            for (Listener listener : this.listeners) {
                if (isInfoChanged) {
                    listener.infoChanged(this);
                } else {
                    listener.stateChanged(this);
                }
            }
        }
    }
}
