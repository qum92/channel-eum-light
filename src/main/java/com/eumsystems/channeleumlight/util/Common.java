package com.eumsystems.channeleumlight.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class Common {

	public static String getCurrentTime(String format) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(now);
		return dateString;
	}
	
	private static final String[] IP_HEADER_CANDIDATES = {
	        "X-Forwarded-For",
	        "Proxy-Client-IP",
	        "WL-Proxy-Client-IP",
	        "HTTP_X_FORWARDED_FOR",
	        "HTTP_X_FORWARDED",
	        "HTTP_X_CLUSTER_CLIENT_IP",
	        "HTTP_CLIENT_IP",
	        "HTTP_FORWARDED_FOR",
	        "HTTP_FORWARDED",
	        "HTTP_VIA",
	        "REMOTE_ADDR"
	    };

	    public static String getClientIpAddressIfServletRequestExist() {

	        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
	            return "0.0.0.0";
	        }

	        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	        for (String header: IP_HEADER_CANDIDATES) {
	            String ipFromHeader = request.getHeader(header);
	            if (Objects.nonNull(ipFromHeader) && ipFromHeader.length() != 0 && !"unknown".equalsIgnoreCase(ipFromHeader)) {
	                String ip = ipFromHeader.split(",")[0];
	                return ip;
	            }
	        }
	        return request.getRemoteAddr();
	    }
}
