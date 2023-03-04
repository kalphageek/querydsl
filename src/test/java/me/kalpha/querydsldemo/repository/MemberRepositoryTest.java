package me.kalpha.querydsldemo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertEquals(findMember.getId(), member.getId());

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).extracting("id").containsExactly(member.getId());

        List<Member> result2 = memberRepository.findByUsername(member.getUsername());
        assertThat(result2).extracting("id").containsExactly(member.getId());
    }

}