package cn.edu.ncu.onlinechat.module.friend.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FriendGroupVO implements Serializable {

    private Long id;
    private String name;
    private Integer sortOrder;
    private List<FriendVO> friends;
}
