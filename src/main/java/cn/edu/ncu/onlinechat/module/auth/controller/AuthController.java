package cn.edu.ncu.onlinechat.module.auth.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.SmsSendDTO;
import cn.edu.ncu.onlinechat.module.auth.service.AuthService;
import cn.edu.ncu.onlinechat.module.auth.service.SmsVerifyService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.auth.vo.SmsSendVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SmsVerifyService smsVerifyService;

    @Operation(summary = "短信登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "短信注册")
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody @Valid RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public Result<SmsSendVO> sendSms(@RequestBody @Valid SmsSendDTO dto) {
        return Result.success(smsVerifyService.sendCode(dto.getPhone()));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout(SecurityUtil.currentUserId());
        return Result.success();
    }
}
