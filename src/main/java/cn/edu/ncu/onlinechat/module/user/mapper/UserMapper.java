package cn.edu.ncu.onlinechat.module.user.mapper;

import cn.edu.ncu.onlinechat.module.user.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    User selectByPhone(@Param("phone") String phone);

    List<User> searchByKeyword(@Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
