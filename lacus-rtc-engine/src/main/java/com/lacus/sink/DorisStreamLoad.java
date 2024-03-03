package com.lacus.sink;

import com.lacus.common.exception.CustomException;
import com.lacus.common.utils.BeUtil;
import com.lacus.common.utils.HttpClientUtil;
import com.lacus.common.utils.PasswordUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public DorisStreamLoad(String hostPort, String db, String tbl, String user, String passwd, Map<String, String> conf) {
        this.hostPort = hostPort;
        this.db = db;
        this.tbl = tbl;
        this.user = user;
        this.passwd = passwd;
        this.conf = conf;
    }


    private HttpURLConnection getConnection(String urlStr, String label) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("PUT");
        String authEncoding = Base64.getEncoder().encodeToString(String.format("%s:%s", user, passwd).getBytes(StandardCharsets.UTF_8));
        conn.setRequestProperty("Authorization", "Basic " + authEncoding);
        conn.addRequestProperty("Expect", "100-continue");
        conn.addRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        conn.addRequestProperty("label", label);
        //默认严格模式
//        conn.addRequestProperty("max_filter_ratio", "0");
//        conn.addRequestProperty("strict_mode", "true");
        //自定义参数
        if (ObjectUtils.isNotEmpty(conf)) {
            for (Map.Entry<String, String> entry : conf.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        return conn;
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

    public LoadResponse loadBatch(String data) {
        if (ObjectUtils.isEmpty(beConf)) {
            throw new CustomException("BE 配置为空");
        }
        String beRoundRobin = BeUtil.getBeRoundRobin(beConf);
        String beLoadUrl = String.format(loadUrlPattern, beRoundRobin, db, tbl);
        Calendar calendar = Calendar.getInstance();
        String label = String.format("datacenter_%s_%s_%s%02d%02d_%02d%02d%02d_%s", db, tbl,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                UUID.randomUUID().toString().replaceAll("-", ""));

        HttpURLConnection beConn = null;
        InputStream stream = null;
        try {
            int status;
            // build request and send to new be location
            beConn = getConnection(beLoadUrl, label);
            // send data to be
            BufferedOutputStream bos = new BufferedOutputStream(beConn.getOutputStream());
            bos.write(data.getBytes());
            bos.close();

            // get respond
            status = beConn.getResponseCode();
            String respMsg = beConn.getResponseMessage();
            stream = (InputStream) beConn.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            log.info("AuditLoader plugin load with label: {}, response code: {}, msg: {}, content: {}", label, status, respMsg, response.toString());
            return new LoadResponse(status, respMsg, response.toString(), beRoundRobin);
        } catch (Exception e) {
            e.printStackTrace();
            String err = "failed to load audit via AuditLoader plugin with label: " + label;
            log.warn(err, e);
            return new LoadResponse(-1, e.getMessage(), err, beRoundRobin);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("stream关闭失败:", e);
                }
            }
            if (beConn != null) {
                beConn.disconnect();
            }
        }
    }

    public LoadResponse loadBatchV2(String data) {
        if (ObjectUtils.isEmpty(beConf)) {
            throw new CustomException("BE 配置为空");
        }
        String loadBe = BeUtil.getBeRoundRobin(beConf);
        String beLoadUrl = String.format(loadUrlPattern, loadBe, db, tbl);
        log.debug("streamload to be: {}", loadBe);

        List<Header> headers = buildHeaders();
        String label = buildLabel();
        headers.add(new BasicHeader("label", label));
        try {
            String beResp = HttpClientUtil.putJson(beLoadUrl, data, headers);
            beResp = beResp.replaceAll("\r|\n", "");
            //log.info("StreamLoader load with label: {}, content: {}",label, JSON.toJSONString(beResp));
            return new LoadResponse(HttpStatus.SC_OK, "ok", beResp, loadBe);
        } catch (Exception e) {
            String err = "failed to load data streamload with label: " + label;
            log.error(err, e);
            return new LoadResponse(-1, e.getMessage(), err, loadBe);
        }
    }
}
