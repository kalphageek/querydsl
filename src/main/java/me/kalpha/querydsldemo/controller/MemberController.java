package me.kalpha.querydsldemo.controller;

import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.repository.MemberRepository;
import me.kalpha.querydsldemo.repository.MemberRepositoryCustom;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMember(MemberSearchCondition condition) {
        return memberRepository.search(condition);
    }

    @GetMapping("/v1/member/{memberId}")
    public MemberTeamDto findById(@PathVariable Long memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        MemberTeamDto memberTeamDto = new MemberTeamDto();

        memberOptional.ifPresent(o -> {
            memberTeamDto.setMemberId(o.getId());
            memberTeamDto.setUsername(o.getUsername());
            memberTeamDto.setAge(o.getAge());
            memberTeamDto.setTeamId(o.getTeam().getId());
            memberTeamDto.setTeamName(o.getTeam().getName());
        });
        return memberTeamDto;
    }
}
