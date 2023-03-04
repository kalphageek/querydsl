package me.kalpha.querydsldemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberTeamDto {
    Long memberId;
    String username;
    int age;
    Long teamId;
    String teamName;
}
