package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.constant.Constants;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.SmsVerifyService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.friend.entity.FriendGroup;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendGroupMapper;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    private SmsVerifyService smsVerifyService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerCreatesUserAndDefaultGroup() {
        RegisterDTO dto = new RegisterDTO();
        dto.setPhone("13800138000");
        dto.setCode("1234");
        dto.setNickname("Nick");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userMapper.selectByPhone(dto.getPhone())).thenReturn(null);
        when(userMapper.selectByUsername(anyString())).thenReturn(null);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        when(friendGroupMapper.insert(any(FriendGroup.class))).thenReturn(1);
        when(jwtUtil.generateToken(100L, "u" + dto.getPhone())).thenReturn("token");

        LoginVO vo = authService.register(dto);

        verify(smsVerifyService).checkCode(dto.getPhone(), dto.getCode());
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
        dto.setPhone("13800138000");
        dto.setCode("1234");

        User existing = new User();
        existing.setId(200L);
        existing.setUsername("u13800138000");
        existing.setPhone(dto.getPhone());

        when(userMapper.selectByPhone(dto.getPhone())).thenReturn(existing);
        when(jwtUtil.generateToken(existing.getId(), existing.getUsername())).thenReturn("token");

        LoginVO vo = authService.login(dto);

        verify(smsVerifyService).checkCode(dto.getPhone(), dto.getCode());
        verify(userMapper, never()).insert(any(User.class));
        verify(friendGroupMapper, never()).insert(any(FriendGroup.class));
        assertThat(vo.getToken()).isEqualTo("token");
        assertThat(vo.getUser().getId()).isEqualTo(200L);
    }
}
