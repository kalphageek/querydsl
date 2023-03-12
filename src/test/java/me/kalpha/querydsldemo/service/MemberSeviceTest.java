package me.kalpha.querydsldemo.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.QMember;
import me.kalpha.querydsldemo.entity.Team;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static me.kalpha.querydsldemo.entity.QMember.member;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.StringUtils.hasText;

/**
 * QMember.member -> member (static import)
 * StringUtils.hasText -> hasText (static import)
 */
@SpringBootTest
@Transactional
public class MemberSeviceTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @DisplayName("(BooleanBuilder) Dynamic Query 생성 테스트")
    @Test
    public void booleanBuilderTest() {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> results = searchMember1(usernameParam, ageParam);
        assertEquals(results.get(0).getUsername(), usernameParam);
    }

    /**
     * booleanBuilderTest 용
     * @param usernameParam
     * @param ageParam
     * @return
     */
    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder();
        // StringUtils.hasText는 null과 empty를 체크한다
        if (hasText(usernameParam)) {
            builder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();
    }

    @DisplayName("Where Param Dynamic Query 생성 테스트")
    @Test
    public void whereParamTest() {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> members = searchMember2(usernameParam, ageParam);
        assertEquals(members.get(0).getUsername(), usernameParam);
    }

    /**
     * whereParamTest 용
     * Main 코드가 깔끔하고, 조건의 자유로운 조합이 가능해서 이 방식이 선호된다
     * 재사용도 가능하다.
     * 코드의 가독성이 높아진다
     * @param usernameParam
     * @param ageParam
     * @return
     */
    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory.selectFrom(member)
//                .where(usernameEq(usernameParam), ageEq(ageParam)) // and 조건
                .where(allEq(usernameParam, ageParam)) //사용자 정의
                .fetch();
    }

    private BooleanExpression allEq(String usernameParam, Integer ageParam) {
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }

    private BooleanExpression usernameEq(String usernameParam) {
        return hasText(usernameParam) ? member.username.eq(usernameParam) : null ;
    }

    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam == null ? null : member.age.eq(ageParam);
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
