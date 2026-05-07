package cn.edu.ncu.onlinechat.module.group.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class InviteMemberDTO {

    @NotEmpty
    private List<Long> userIds;
}
