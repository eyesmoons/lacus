package com.lacus.function;

import com.lacus.exception.CustomException;
import com.lacus.utils.BeUtil;
import com.lacus.utils.HttpClientUtil;
import com.lacus.utils.PasswordUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Data
@Slf4j
public class DorisStreamLoad implements Serializable {

    private static final long serialVersionUID = -7541222854019372396L;

    private static String loadUrlPattern = "http://%s/api/%s/%s/_stream_load?";
    private String hostPort;
    private String db;
    private String tbl;
    private String user;
    private String passwd;
    private Map<String, String> conf;
    private Map<String, Integer> beConf;


    public DorisStreamLoad(String hostPort, String db, String tbl, String user, String passwd, Map<String, String> conf, Map<String, Integer> beConf) {
        this.hostPort = hostPort;
        this.db = db;
        this.tbl = tbl;
        this.user = user;
        this.passwd = passwd;
        this.conf = conf;
        this.beConf = beConf;
    }

    public List<Header> buildHeaders() {
        List<Header> headers = new ArrayList<>();
        String authEncoding = Base64.getEncoder().encodeToString(String.format("%s:%s", user, PasswordUtil.decryptPwd(passwd)).getBytes(StandardCharsets.UTF_8));
        headers.add(new BasicHeader("Authorization", "Basic " + authEncoding));
        headers.add(new BasicHeader("Expect", "100-continue"));
        // 自定义参数
        if (ObjectUtils.isNotEmpty(conf)) {
            for (Map.Entry<String, String> entry : conf.entrySet()) {
                headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }
        return headers;
    }

    public String buildLabel() {
        return String.format("rtc_%s_%s", tbl, UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public static class LoadResponse {
        public int status;
        public String respMsg;
        public String respContent;
        public String be;

        public LoadResponse(int status, String respMsg, String respContent, String be) {
            this.status = status;
            this.respMsg = respMsg;
            this.respContent = respContent;
            this.be = be;
        }

        @Override
        public String toString() {
            return "status: " + status +
                    ", resp msg: " + respMsg +
                    ", resp content: " + respContent +
                    ", load be: " + be;
        }
    }

    public LoadResponse doLoad(String data) {
        if (ObjectUtils.isEmpty(beConf)) {
            throw new CustomException("empty BE nodes");
        }
        String loadBe = BeUtil.getBeRoundRobin(beConf);
        String beLoadUrl = String.format(loadUrlPattern, loadBe, db, tbl);
        log.debug("selected BE node： {}", loadBe);

        List<Header> headers = buildHeaders();
        String label = buildLabel();
        headers.add(new BasicHeader("label", label));
        try {
            String beResp = HttpClientUtil.putJson(beLoadUrl, data, headers);
            beResp = beResp.replaceAll("\r|\n", "");
            //log.info("StreamLoader load with label: {}, content: {}",label, JSON.toJSONString(beResp));
            return new LoadResponse(HttpStatus.SC_OK, "ok", beResp, loadBe);
        } catch (Exception e) {
            String err = "failed to execute stream load with label: " + label;
            log.error(err, e);
            return new LoadResponse(-1, e.getMessage(), err, loadBe);
        }
    }
}
