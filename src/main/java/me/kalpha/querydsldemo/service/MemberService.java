package me.kalpha.querydsldemo.service;

import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {
    public List<MemberTeamDto> search(MemberSearchCondition condition);
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
    public MemberTeamDto findByMemberId(Long id);
}
