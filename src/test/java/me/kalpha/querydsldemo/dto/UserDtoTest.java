package me.kalpha.querydsldemo.dto;

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
 * 다양한 컬럼을 추가한 Dto를 Querydsl에서 생성하는 예제
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserDtoTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    /**
     * Projections.fields로 적용하는 경우 Dto에 Getter/Setter가 없어도 된다.
     * 대신 컬러명-필드명이 동일하거나 alias로 동일하게 맟춰야 한다.
     */
    @DisplayName("Querydsl 컬럼명이 다른 Dto 적용")
    @Test
    public void queryDslUserDto()  {
        QMember qMember = QMember.member;
        List<UserDto> results = queryFactory
                .select(Projections.fields(UserDto.class,
                        qMember.username.as("name"), //UserDto.name에 맞춤
                        qMember.age))
                .from(qMember)
                .fetch();

        for (UserDto result : results) {
            System.out.println("result = " + result.toString());
        }
    }

    @DisplayName("Querydsl Sub Query Dto 적용")
    @Test
    public void queryDslSubQueryDto() {
        QMember qMember = QMember.member;
        QMember subMember = QMember.member;

        List<UserDto> results = queryFactory
                .select(Projections.fields(UserDto.class,
                        qMember.username.as("name"),
                        qMember.age,
                        ExpressionUtils.as(JPAExpressions
                                .select(subMember.age.max())
                                .from(subMember)
                                .where(qMember.team.id.eq(subMember.team.id)), "magAge")
                ))
                .from(qMember)
                .fetch();

        for (UserDto result : results) {
            System.out.println("result = " + result);
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