package com.lacus.utils.ip;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.lacus.common.config.LacusConfig;
import com.lacus.utils.jackson.JacksonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * query geography address from ip
 */
@Slf4j
public class OnlineIpRegionUtil {

    /**
     * website for query geography address from ip
     */
    public static final String ADDRESS_QUERY_SITE = "http://whois.pconline.com.cn/ipJson.jsp";


    public static IpRegion getIpRegion(String ip) {
        if(StrUtil.isBlank(ip) || IpUtil.isValidIpv6(ip) || !IpUtil.isValidIpv4(ip)) {
            return null;
        }

        // no need to query address for inner ip
        if (NetUtil.isInnerIP(ip)) {
            return new IpRegion("internal", "IP");
        }
        if (LacusConfig.isAddressEnabled()) {
            try {
                String rspStr = HttpUtil.get(ADDRESS_QUERY_SITE + "?ip=" + ip + "&json=true",
                    CharsetUtil.CHARSET_GBK);

                if (StrUtil.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return null;
                }

                String province = JacksonUtil.getAsString(rspStr, "pro");
                String city = JacksonUtil.getAsString(rspStr, "city");
                return new IpRegion(province, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return null;
    }

}
