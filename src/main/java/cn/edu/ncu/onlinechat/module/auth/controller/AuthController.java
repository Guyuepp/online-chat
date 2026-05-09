package cn.edu.ncu.onlinechat.module.auth.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.auth.dto.EmailSendDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginPasswordDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.AuthService;
import cn.edu.ncu.onlinechat.module.auth.service.VerifyCodeService;
import cn.edu.ncu.onlinechat.module.auth.vo.EmailSendVO;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
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
    private final VerifyCodeService verifyCodeService;

    @Operation(summary = "邮箱登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "密码登录")
    @PostMapping("/login/password")
    public Result<LoginVO> loginByPassword(@RequestBody @Valid LoginPasswordDTO dto) {
        return Result.success(authService.loginByPassword(dto));
    }

    @Operation(summary = "邮箱注册")
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody @Valid RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/email/send")
    public Result<EmailSendVO> sendEmail(@RequestBody @Valid EmailSendDTO dto) {
        return Result.success(verifyCodeService.sendCode(dto.getEmail()));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout(SecurityUtil.currentUserId());
        return Result.success();
    }
}
