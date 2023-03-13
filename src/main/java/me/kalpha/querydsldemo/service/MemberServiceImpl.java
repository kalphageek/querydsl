package me.kalpha.querydsldemo.service;

import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.repository.MemberRepository;
import me.kalpha.querydsldemo.repository.QueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final QueryRepository queryRepository;

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return memberRepository.search(condition);
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        return queryRepository.searchSimplePage(condition, pageable);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        return queryRepository.searchComplexPage(condition, pageable);
    }

    @Override
    public MemberTeamDto findByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(memberId + " : MemberId 값이 없습니다"));

        MemberTeamDto memberTeamDto = new MemberTeamDto(member.getId(), member.getUsername(), member.getAge(), member.getTeam().getId(), member.getTeam().getName());
        return  memberTeamDto;
    }
}
