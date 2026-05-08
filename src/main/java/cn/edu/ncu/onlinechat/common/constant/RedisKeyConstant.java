package cn.edu.ncu.onlinechat.common.constant;

public final class RedisKeyConstant {

    public static final String LOGIN_TOKEN_PREFIX = "chat:login:token:";
    public static final String ONLINE_USER_PREFIX = "chat:online:user:";
    public static final String OFFLINE_MESSAGE_PREFIX = "chat:offline:msg:";
    public static final String MAIL_SEND_INTERVAL_PREFIX = "chat:mail:send:interval:";
    public static final String MAIL_VERIFY_CODE_PREFIX = "chat:mail:code:";

    private RedisKeyConstant() {
    }
}
