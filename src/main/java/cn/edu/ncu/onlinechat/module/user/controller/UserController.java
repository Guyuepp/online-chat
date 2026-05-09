package cn.edu.ncu.onlinechat.module.user.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.user.dto.PasswordResetDTO;
import cn.edu.ncu.onlinechat.module.user.dto.PasswordUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.dto.UserUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.service.UserService;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取自己的资料")
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.getProfile(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "搜索用户")
    @GetMapping("/search")
    public Result<List<UserVO>> search(@RequestParam String keyword) {
        return Result.success(userService.search(keyword));
    }

    @Operation(summary = "修改资料")
    @PutMapping("/me")
    public Result<Void> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        userService.updateProfile(SecurityUtil.currentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "修改密码（需旧密码）")
    @PutMapping("/me/password")
    public Result<Void> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto) {
        userService.updatePassword(SecurityUtil.currentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "通过邮箱验证码重置密码")
    @PutMapping("/password/reset")
    public Result<Void> resetPassword(@RequestBody @Valid PasswordResetDTO dto) {
        userService.resetPasswordByEmail(dto);
        return Result.success();
    }
}
