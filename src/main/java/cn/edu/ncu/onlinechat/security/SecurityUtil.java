package cn.edu.ncu.onlinechat.security;

import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static SecurityUser current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser su)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return su;
    }

    public static Long currentUserId() {
        return current().getId();
    }
}
