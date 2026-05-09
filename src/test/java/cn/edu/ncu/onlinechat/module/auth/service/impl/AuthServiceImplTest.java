package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.constant.Constants;
import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginPasswordDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.VerifyCodeService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.friend.entity.FriendGroup;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendGroupMapper;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.security.JwtProperties;
import cn.edu.ncu.onlinechat.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private FriendGroupMapper friendGroupMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private VerifyCodeService verifyCodeService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerCreatesUserAndDefaultGroup() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("nick@example.com");
        dto.setCode("1234");
        dto.setNickname("Nick");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(null);
        when(userMapper.selectByUsername(anyString())).thenReturn(null);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        when(friendGroupMapper.insert(any(FriendGroup.class))).thenReturn(1);
        when(jwtUtil.generateToken(100L, "nick")).thenReturn("token");

        LoginVO vo = authService.register(dto);

        verify(verifyCodeService).checkCode(dto.getEmail(), dto.getCode());
        assertThat(vo.getToken()).isEqualTo("token");
        assertThat(vo.getUser()).isNotNull();
        assertThat(vo.getUser().getId()).isEqualTo(100L);

        ArgumentCaptor<FriendGroup> groupCaptor = ArgumentCaptor.forClass(FriendGroup.class);
        verify(friendGroupMapper).insert(groupCaptor.capture());
        FriendGroup group = groupCaptor.getValue();
        assertThat(group.getUserId()).isEqualTo(100L);
        assertThat(group.getName()).isEqualTo(Constants.DEFAULT_FRIEND_GROUP_NAME);
    }

    @Test
    void loginUsesExistingUserWithoutCreatingGroup() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setCode("1234");

        User existing = new User();
        existing.setId(200L);
        existing.setUsername("user");
        existing.setEmail(dto.getEmail());

        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(existing);
        when(jwtUtil.generateToken(existing.getId(), existing.getUsername())).thenReturn("token");

        LoginVO vo = authService.login(dto);

        verify(verifyCodeService).checkCode(dto.getEmail(), dto.getCode());
        verify(userMapper, never()).insert(any(User.class));
        verify(friendGroupMapper, never()).insert(any(FriendGroup.class));
        assertThat(vo.getToken()).isEqualTo("token");
        assertThat(vo.getUser().getId()).isEqualTo(200L);
    }

    @Test
    void loginByPasswordSuccess() {
        LoginPasswordDTO dto = new LoginPasswordDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("mypassword");

        User existing = new User();
        existing.setId(200L);
        existing.setUsername("user");
        existing.setEmail(dto.getEmail());
        existing.setPassword("encoded-password");

        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(existing);
        when(passwordEncoder.matches(dto.getPassword(), existing.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(existing.getId(), existing.getUsername())).thenReturn("token");

        LoginVO vo = authService.loginByPassword(dto);

        assertThat(vo.getToken()).isEqualTo("token");
        assertThat(vo.getUser().getId()).isEqualTo(200L);
    }

    @Test
    void loginByPasswordWrongPassword() {
        LoginPasswordDTO dto = new LoginPasswordDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("wrongpass");

        User existing = new User();
        existing.setId(200L);
        existing.setPassword("encoded-password");

        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(existing);
        when(passwordEncoder.matches(dto.getPassword(), existing.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.loginByPassword(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void loginByPasswordUserNotFound() {
        LoginPasswordDTO dto = new LoginPasswordDTO();
        dto.setEmail("nobody@example.com");
        dto.setPassword("somepass");

        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(null);

        assertThatThrownBy(() -> authService.loginByPassword(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void registerWithPasswordEncodesProvidedPassword() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("user@example.com");
        dto.setCode("1234");
        dto.setPassword("my-password");

        when(passwordEncoder.encode("my-password")).thenReturn("encoded-my-password");
        when(userMapper.selectByEmail(dto.getEmail())).thenReturn(null);
        when(userMapper.selectByUsername(anyString())).thenReturn(null);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(300L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        when(friendGroupMapper.insert(any(FriendGroup.class))).thenReturn(1);
        when(jwtUtil.generateToken(300L, "user")).thenReturn("token");

        authService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-my-password");
    }

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void logoutShouldBlacklistTokenInRedis() {
        String token = "valid.jwt.token";
        String jti = "abc-123";
        long now = System.currentTimeMillis();
        Date expiration = new Date(now + 3_600_000); // 1 hour remaining

        when(jwtProperties.getHeader()).thenReturn("Authorization");
        when(jwtProperties.getPrefix()).thenReturn("Bearer ");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.parseJti(token)).thenReturn(jti);
        when(jwtUtil.parseExpiration(token)).thenReturn(expiration);

        authService.logout(1L);

        verify(stringRedisTemplate).opsForValue();
        verify(valueOperations).set(
                eq(RedisKeyConstant.LOGIN_TOKEN_PREFIX + jti),
                eq("1"),
                any(Duration.class));
    }

    @Test
    void logoutShouldSkipWhenNoAuthHeader() {
        when(jwtProperties.getHeader()).thenReturn("Authorization");
        when(request.getHeader("Authorization")).thenReturn(null);

        authService.logout(1L);

        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    void logoutShouldSkipWhenTokenExpired() {
        String token = "expired.jwt.token";
        String jti = "expired-jti";
        Date expiration = new Date(System.currentTimeMillis() - 60_000); // already expired

        when(jwtProperties.getHeader()).thenReturn("Authorization");
        when(jwtProperties.getPrefix()).thenReturn("Bearer ");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.parseJti(token)).thenReturn(jti);
        when(jwtUtil.parseExpiration(token)).thenReturn(expiration);

        authService.logout(1L);

        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void logoutShouldSkipWhenHeaderPrefixWrong() {
        when(jwtProperties.getHeader()).thenReturn("Authorization");
        when(jwtProperties.getPrefix()).thenReturn("Bearer ");
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        authService.logout(1L);

        verify(stringRedisTemplate, never()).opsForValue();
    }
}
