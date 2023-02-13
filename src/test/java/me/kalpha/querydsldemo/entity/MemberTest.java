package me.kalpha.querydsldemo.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory ;

    @Test
    public void memberTest() {
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        assertEquals(members.get(1).getTeam().getName(), "Team 1");
        assertEquals(members.get(1).getTeam().getMembers().size(), 3);
    }

    @Test
    public void querydslTest() {
        QMember qMember = QMember.member;
        Member findMember = queryFactory.select(qMember)
                .from(qMember)
                .where(qMember.username.eq("member1")) //Prepared Statement로 만들어진다
                .fetchOne();

        assertEquals(findMember.getUsername(), "member1");
    }

    @BeforeEach
    private void samples() {
        queryFactory = new JPAQueryFactory(em);

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("member1", 10, team1);
        Member member2 = new Member("member2", 20, team1);
        Member member3 = new Member("member3", 30, team2);
        Member member4 = new Member("member4", 20, team2);
        member4.changeTeam(team1);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
}