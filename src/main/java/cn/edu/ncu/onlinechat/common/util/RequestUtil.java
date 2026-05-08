package cn.edu.ncu.onlinechat.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class RequestUtil {

    private RequestUtil() {
    }

    public static String clientIp() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return null;
        }
        HttpServletRequest request = servletAttrs.getRequest();
        if (request == null) {
            return null;
        }
        String ip = headerIp(request, "X-Forwarded-For");
        if (ip == null) {
            ip = headerIp(request, "X-Real-IP");
        }
        if (ip == null) {
            ip = headerIp(request, "Proxy-Client-IP");
        }
        if (ip == null) {
            ip = headerIp(request, "WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = headerIp(request, "HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = headerIp(request, "HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return normalizeIp(ip);
    }

    private static String headerIp(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if ("unknown".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }

    private static String normalizeIp(String ip) {
        if (ip == null) {
            return null;
        }
        String trimmed = ip.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        int commaIndex = trimmed.indexOf(',');
        if (commaIndex > 0) {
            return trimmed.substring(0, commaIndex).trim();
        }
        return trimmed;
    }
}
