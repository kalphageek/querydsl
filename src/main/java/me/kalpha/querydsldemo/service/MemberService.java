package me.kalpha.querydsldemo.service;

import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;

import java.util.List;

public interface MemberService {
    public List<MemberTeamDto> search(MemberSearchCondition condition);
    public MemberTeamDto findByMemberId(Long id);
}
