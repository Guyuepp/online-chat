package cn.edu.ncu.onlinechat.module.user.service.impl;

import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import cn.edu.ncu.onlinechat.module.auth.service.VerifyCodeService;
import cn.edu.ncu.onlinechat.module.user.dto.PasswordResetDTO;
import cn.edu.ncu.onlinechat.module.user.dto.PasswordUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.dto.UserUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerifyCodeService verifyCodeService;

    @InjectMocks
    private UserServiceImpl userService;

    private User createUser(Long id, String username, String nickname) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword("encoded-password");
        user.setEmail(username + "@example.com");
        user.setStatus(0);
        return user;
    }

    @Test
    void getProfileShouldReturnUserVO() {
        User user = createUser(1L, "alice", "Alice");
        when(userMapper.selectById(1L)).thenReturn(user);

        UserVO vo = userService.getProfile(1L);

        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getUsername()).isEqualTo("alice");
        assertThat(vo.getNickname()).isEqualTo("Alice");
        assertThat(vo.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void getProfileShouldThrowWhenUserNotFound() {
        when(userMapper.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> userService.getProfile(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void searchShouldReturnLimitedResults() {
        User user1 = createUser(1L, "alice", "Alice");
        User user2 = createUser(2L, "bob", "Bob");
        when(userMapper.searchByKeyword("al")).thenReturn(List.of(user1, user2));

        List<UserVO> results = userService.search("al");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getUsername()).isEqualTo("alice");
    }

    @Test
    void searchShouldReturnEmptyListWhenNoMatch() {
        when(userMapper.searchByKeyword("xyz")).thenReturn(List.of());

        List<UserVO> results = userService.search("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void updateProfileShouldUpdateNonNullFields() {
        User user = createUser(1L, "alice", "Alice");
        when(userMapper.selectById(1L)).thenReturn(user);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("NewName");
        dto.setSignature("hello world");

        userService.updateProfile(1L, dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).update(captor.capture());
        User updated = captor.getValue();
        assertThat(updated.getNickname()).isEqualTo("NewName");
        assertThat(updated.getSignature()).isEqualTo("hello world");
        assertThat(updated.getEmail()).isEqualTo("alice@example.com"); // unchanged
    }

    @Test
    void updateProfileShouldThrowWhenUserNotFound() {
        when(userMapper.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> userService.updateProfile(99L, new UserUpdateDTO()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void updatePasswordShouldSucceed() {
        User user = createUser(1L, "alice", "Alice");
        user.setPassword("encoded-old-pass");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("old-pass", "encoded-old-pass")).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");

        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("old-pass");
        dto.setNewPassword("new-pass");

        userService.updatePassword(1L, dto);

        verify(userMapper).updatePassword(1L, "encoded-new-pass");
    }

    @Test
    void updatePasswordShouldThrowWhenOldPasswordWrong() {
        User user = createUser(1L, "alice", "Alice");
        user.setPassword("encoded-old-pass");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("wrong-pass", "encoded-old-pass")).thenReturn(false);

        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("wrong-pass");
        dto.setNewPassword("new-pass");

        assertThatThrownBy(() -> userService.updatePassword(1L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void updatePasswordShouldThrowWhenUserNotFound() {
        when(userMapper.selectById(99L)).thenReturn(null);

        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("old-pass");
        dto.setNewPassword("new-pass");

        assertThatThrownBy(() -> userService.updatePassword(99L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void resetPasswordByEmailShouldSucceed() {
        User user = createUser(1L, "alice", "Alice");
        when(userMapper.selectByEmail("alice@example.com")).thenReturn(user);
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");

        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setEmail("alice@example.com");
        dto.setCode("123456");
        dto.setNewPassword("new-pass");

        userService.resetPasswordByEmail(dto);

        verify(verifyCodeService).checkCode("alice@example.com", "123456");
        verify(userMapper).updatePassword(1L, "encoded-new-pass");
    }

    @Test
    void resetPasswordByEmailShouldThrowWhenUserNotFound() {
        when(userMapper.selectByEmail("unknown@example.com")).thenReturn(null);

        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setEmail("unknown@example.com");
        dto.setCode("123456");
        dto.setNewPassword("new-pass");

        assertThatThrownBy(() -> userService.resetPasswordByEmail(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");

        verify(verifyCodeService).checkCode("unknown@example.com", "123456");
        verify(userMapper, never()).updatePassword(any(), anyString());
    }

    @Test
    void resetPasswordByEmailShouldThrowWhenCodeInvalid() {
        doThrow(new BusinessException(ResultCode.BAD_REQUEST, "验证码错误"))
                .when(verifyCodeService).checkCode("alice@example.com", "wrong-code");

        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setEmail("alice@example.com");
        dto.setCode("wrong-code");
        dto.setNewPassword("new-pass");

        assertThatThrownBy(() -> userService.resetPasswordByEmail(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("验证码错误");

        verify(userMapper, never()).updatePassword(any(), anyString());
    }
}
