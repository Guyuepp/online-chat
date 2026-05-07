package cn.edu.ncu.onlinechat.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已被注册"),
    PASSWORD_INCORRECT(1003, "用户名或密码错误"),

    FRIEND_NOT_FOUND(2001, "好友关系不存在"),
    FRIEND_ALREADY_EXISTS(2002, "已经是好友"),
    FRIEND_REQUEST_INVALID(2003, "好友申请状态异常"),
    FRIEND_GROUP_NOT_FOUND(2004, "分组不存在"),

    GROUP_NOT_FOUND(3001, "群聊不存在"),
    GROUP_MEMBER_NOT_FOUND(3002, "群成员不存在"),
    GROUP_PERMISSION_DENIED(3003, "群操作权限不足"),

    MESSAGE_NOT_FOUND(4001, "消息不存在"),
    MESSAGE_SEND_FAIL(4002, "消息发送失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
