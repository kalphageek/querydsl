package me.kalpha.querydsldemo.controller;

import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMembers(@RequestBody MemberSearchCondition condition) {
        return memberService.search(condition);
    }

    @GetMapping("/v1/member/{memberId}")
    public MemberTeamDto findById(@PathVariable Long memberId) {
        return memberService.findByMemberId(memberId);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMembersPageSimple(@RequestBody MemberSearchCondition condition, Pageable pageable) {
        return memberService.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMembersPageComplex(@RequestBody MemberSearchCondition condition, Pageable pageable) {
        return memberService.searchPageComplex(condition, pageable);
    }
}
