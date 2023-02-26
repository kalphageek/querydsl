package me.kalpha.querydsldemo.dto;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.QMember;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Querydsl의 기본적인 사용법 예제
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberDtoTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    /**
     * 생성자만 사용가능
     */
    @DisplayName("JPQL를 이용해서 Dto 조회")
    @Test
    public void jpqlQueryDto() {
        List<MemberDto> results = em.createQuery("select new me.kalpha.querydsldemo.dto.MemberDto(m.username, m.age) from Member m " +
                        "where m.team.name = 'Team 1'", MemberDto.class)
                .getResultList();

        results.forEach(System.out::println);
    }

    @DisplayName("Querydsl Getter/Setter를 이용해서 Dto 조회")
    @Test
    public void queryDslBeanQueryDto() {
        QMember qMember = QMember.member;

        List<MemberDto> results = queryFactory
                .select(Projections.bean(MemberDto.class,
                        qMember.username,
                        qMember.age))
                .from(qMember)
                .where(qMember.team.name.eq("Team 1"))
                .fetch();

        results.forEach(System.out::println);
    }

    /**
     * Getter/Setter가 없어도 됨
     */
    @DisplayName("Querydsl field에 직접 적용해서 Dto 조회")
    @Test
    public void queryDslFieldsQueryDto() {
        QMember qMember = QMember.member;

        List<MemberDto> results = queryFactory
                .select(Projections.fields(MemberDto.class,
                        qMember.username,
                        qMember.age))
                .from(qMember)
                .where(qMember.team.name.eq("Team 1"))
                .fetch();

        results.forEach(System.out::println);
    }

    /**
     * Compile 시점에 오류체크 불가능
     */
    @DisplayName("Querydsl 생성자 이용 Dto 조회")
    @Test
    public void queryDslConstructorDto() {
        QMember qMember = QMember.member;

        List<MemberDto> results = queryFactory.select(Projections.constructor(MemberDto.class, qMember.username, qMember.age))
                .from(qMember)
                .fetch();

        for (MemberDto memberDto: results) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @BeforeEach
    private void samples() {
        queryFactory = new JPAQueryFactory(em);

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");
        em.persist(team1);
        em.persist(team2);
        em.persist(team3);

        Member member1 = new Member("member1", 10, team1);
        Member member2 = new Member("member2", 20, team1);
        Member member3 = new Member("member3", 20, team2);
        Member member4 = new Member("member4", 30, team2);
        Member member5 = new Member("member5", 30, team2);
        Member member6 = new Member(null, 30, team2);
        Member member7 = new Member("member7", 40, null);
        member4.changeTeam(team1);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(member6);
        em.persist(member7);
    }
}