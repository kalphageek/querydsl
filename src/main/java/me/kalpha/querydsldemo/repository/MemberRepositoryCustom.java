package me.kalpha.querydsldemo.repository;

import me.kalpha.querydsldemo.dto.MemberDto;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    public List<MemberTeamDto> search(MemberSearchCondition condition);
}
