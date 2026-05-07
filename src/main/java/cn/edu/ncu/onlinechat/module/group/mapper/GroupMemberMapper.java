package cn.edu.ncu.onlinechat.module.group.mapper;

import cn.edu.ncu.onlinechat.module.group.entity.GroupMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMemberMapper {

    GroupMember selectById(@Param("id") Long id);

    GroupMember selectByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId);

    List<GroupMember> selectByGroupId(@Param("groupId") Long groupId);

    List<GroupMember> selectByUserId(@Param("userId") Long userId);

    int insert(GroupMember member);

    int updateRole(@Param("id") Long id, @Param("role") String role);

    int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);

    int deleteById(@Param("id") Long id);

    int deleteByGroupId(@Param("groupId") Long groupId);
}
