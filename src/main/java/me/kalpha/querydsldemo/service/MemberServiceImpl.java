package me.kalpha.querydsldemo.service;

import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return memberRepository.search(condition);
    }

    @Override
    public MemberTeamDto findByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(memberId + " : MemberId 값이 없습니다"));

        MemberTeamDto memberTeamDto = new MemberTeamDto(member.getId(), member.getUsername(), member.getAge(), member.getTeam().getId(), member.getTeam().getName());
        return  memberTeamDto;
    }
}
