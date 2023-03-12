package me.kalpha.querydsldemo.controller;

import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.repository.MemberRepository;
import me.kalpha.querydsldemo.repository.MemberRepositoryCustom;
import me.kalpha.querydsldemo.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMember(MemberSearchCondition condition) {
        return memberService.search(condition);
    }

    @GetMapping("/v1/member/{memberId}")
    public MemberTeamDto findById(@PathVariable Long memberId) {
        return memberService.findByMemberId(memberId);
    }
}
