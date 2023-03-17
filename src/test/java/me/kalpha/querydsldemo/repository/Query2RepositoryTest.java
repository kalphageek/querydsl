package me.kalpha.querydsldemo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class Query2RepositoryTest {


    @Autowired
    EntityManager em;
    @Autowired
    Query2Repository queryRepository;

    @DisplayName("QueryRepository with SearchCondition 테스트")
    @Test
    public void searchTest() {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(25);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = queryRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4", "member8");
    }

    @DisplayName("QueryRepository with Simple Pageable 테스트")
    @Test
    public void searchSimplePageTest() {
        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest request = PageRequest.of(2, 3);

        Page<MemberTeamDto> result = queryRepository.searchSimplePage(condition, request);

        assertEquals(result.getContent().size(), 2);
        assertThat(result.getContent()).extracting("username").containsExactly("member7", "member8");
    }

    @DisplayName("QueryRepository with CountQuery Pageable 테스트")
    @Test
    public void searchComplexPageTest() {
        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest request = PageRequest.of(1, 3);

        Page<MemberTeamDto> result = queryRepository.searchComplexPage(condition, request);

        assertEquals(result.getContent().size(), 3);
        assertThat(result.getContent()).extracting("username").containsExactly("member4", "member5", "member6");
    }

    @BeforeEach
    private void initData() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 30, teamA);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member5", 10, teamA);
        Member member6 = new Member("member6", 30, teamA);
        Member member7 = new Member("member7", 20, teamB);
        Member member8 = new Member("member8", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(member6);
        em.persist(member7);
        em.persist(member8);
    }
}