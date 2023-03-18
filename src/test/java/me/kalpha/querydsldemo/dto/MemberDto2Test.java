package me.kalpha.querydsldemo.dto;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.QMember;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Projection을 이용해 Querydsl Dto를 생성하는 예제
 */
@SpringBootTest
@Transactional
class MemberDto2Test {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    /**
     * Compile 시점에 오류체크가 가능
     * IntelliJ의 기능을 활용할 수 있다.
     */
    @DisplayName("queryDsl Proejection을 이용한 Dto 사용")
    @Test
    public void queryDslProjectionDto() {
        samples();
        QMember qMember = QMember.member;
        List<MemberDto2> results = queryFactory.select(new QMemberDto2(qMember.username, qMember.age))
                .from(qMember)
                .fetch();

        for (MemberDto2 result : results) {
            System.out.println("result = " + result);
        }

        assertEquals(results.size(), 7);
    }
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