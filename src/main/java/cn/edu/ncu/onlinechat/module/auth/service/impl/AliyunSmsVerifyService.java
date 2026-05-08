package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import cn.edu.ncu.onlinechat.config.AliyunDypnsapiProperties;
import cn.edu.ncu.onlinechat.module.auth.service.SmsVerifyService;
import cn.edu.ncu.onlinechat.module.auth.vo.SmsSendVO;
import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponseBody;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AliyunSmsVerifyService implements SmsVerifyService {

    private final Client client;
    private final AliyunDypnsapiProperties properties;

    @Override
    public SmsSendVO sendCode(String phone) {
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phone)
                .setSchemeName(properties.getSchemeName())
                .setSignName(properties.getSignName())
                .setTemplateCode(properties.getTemplateCode())
                .setCountryCode(properties.getCountryCode())
                .setCodeLength(properties.getCodeLength())
                .setValidTime(properties.getValidTime())
                .setInterval(properties.getInterval())
                .setReturnVerifyCode(properties.getReturnVerifyCode());
        SendSmsVerifyCodeResponse response;
        try {
            response = client.sendSmsVerifyCode(request);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "sms send failed");
        }
        SendSmsVerifyCodeResponseBody body = response.getBody();
        ensureSendSuccess(body);

        SmsSendVO vo = new SmsSendVO();
        if (body != null && body.getModel() != null) {
            vo.setBizId(body.getModel().getBizId());
        }
        if (body != null) {
            vo.setRequestId(body.getRequestId());
        }
        return vo;
    }

    @Override
    public void checkCode(String phone, String code) {
        CheckSmsVerifyCodeRequest request = new CheckSmsVerifyCodeRequest()
                .setPhoneNumber(phone)
                .setVerifyCode(code)
                .setSchemeName(properties.getSchemeName())
                .setCountryCode(properties.getCountryCode());
        CheckSmsVerifyCodeResponse response;
        try {
            response = client.checkSmsVerifyCode(request);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "sms verify failed");
        }
        CheckSmsVerifyCodeResponseBody body = response.getBody();
        ensureCheckSuccess(body);

        String result = body != null && body.getModel() != null ? body.getModel().getVerifyResult() : null;
        if (!isVerifyPass(result)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "sms code invalid or expired");
        }
    }

    private void ensureSendSuccess(SendSmsVerifyCodeResponseBody body) {
        if (body == null || !Boolean.TRUE.equals(body.getSuccess())) {
            String msg = body != null && body.getMessage() != null && !body.getMessage().isBlank()
                    ? body.getMessage()
                    : "sms send failed";
            throw new BusinessException(ResultCode.BAD_REQUEST, msg);
        }
    }

    private void ensureCheckSuccess(CheckSmsVerifyCodeResponseBody body) {
        if (body == null || !Boolean.TRUE.equals(body.getSuccess())) {
            String msg = body != null && body.getMessage() != null && !body.getMessage().isBlank()
                    ? body.getMessage()
                    : "sms verify failed";
            throw new BusinessException(ResultCode.BAD_REQUEST, msg);
        }
    }

    private boolean isVerifyPass(String result) {
        if (result == null) {
            return false;
        }
        String normalized = result.trim().toLowerCase();
        return "pass".equals(normalized) || "success".equals(normalized) || "true".equals(normalized) || "1".equals(normalized);
    }
}
