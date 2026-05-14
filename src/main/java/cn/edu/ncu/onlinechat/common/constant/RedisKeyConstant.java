package cn.edu.ncu.onlinechat.common.constant;

public final class RedisKeyConstant {

    public static final String LOGIN_TOKEN_PREFIX = "chat:login:token:";
    public static final String ONLINE_USER_PREFIX = "chat:online:user:";
    public static final String OFFLINE_MESSAGE_PREFIX = "chat:offline:msg:";
    public static final String MAIL_SEND_INTERVAL_PREFIX = "chat:mail:send:interval:";
    public static final String MAIL_SEND_IP_INTERVAL_PREFIX = "chat:mail:send:interval:ip:";
    public static final String MAIL_VERIFY_CODE_PREFIX = "chat:mail:code:";
    public static final String MAIL_VERIFY_FAIL_PREFIX = "chat:mail:verify:fail:";
    public static final String MAIL_VERIFY_LOCK_PREFIX = "chat:mail:verify:lock:";

    private RedisKeyConstant() {
    }
}
