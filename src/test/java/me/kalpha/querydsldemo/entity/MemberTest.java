package me.kalpha.querydsldemo.entity;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static me.kalpha.querydsldemo.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory ;

    @DisplayName("jpql")
    @Test
    public void jpqlTest() {
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        assertEquals(members.get(1).getTeam().getName(), "Team 1");
        assertEquals(members.get(1).getTeam().getMembers().size(), 3);
    }

    @DisplayName("Querydsl 기본")
    @Test
    public void querydslTest() {
        QMember qMember = QMember.member;
        Member findMember = queryFactory.select(qMember)
                .from(qMember)
                .where(qMember.username.eq("member1")) //Prepared Statement로 만들어진다
                .fetchOne();

        assertEquals(findMember.getUsername(), "member1");
    }
    @DisplayName("and : Chain 방식")
    @Test
    public void andTest() {
        QMember qMember = QMember.member;
        Member findMember = queryFactory.selectFrom(qMember)
                .where(qMember.username.eq("member1")
                        .and(qMember.age.eq(10)))
                .fetchOne();
        assertEquals(findMember.getUsername(), "member1");
    }

    /**
     * andTest와 같은 의미
     */
    @DisplayName("and : 배열방식")
    @Test
    public void andTest2() {
        QMember qMember = QMember.member;
        Member findMember = queryFactory.selectFrom(qMember)
                .where(
                        qMember.username.eq("member1"),
                        qMember.age.eq(10)
                )
                .fetchOne();
        assertEquals(findMember.getUsername(), "member1");
    }

    /**
     * fetchOne은 2건 이상이면 com.querydsl.core.NonUniqueResultException 발생
     */
    @DisplayName("fetchOne NonUniqueResultException")
    @Test
    public void fetchOneTest() {
        QMember qMember = QMember.member;
        Member findMember = null;
        try {
            findMember = queryFactory.selectFrom(qMember)
                    .where(
                            qMember.team.name.eq("Team 1")
                    )
                    .fetchOne();
            assertEquals(1,2);
        } catch (Exception e) {
            assertEquals(e.getClass().getName(), "com.querydsl.core.NonUniqueResultException");
        }
    }
    /**
     * limit(1).fetchOne과 같은 의미
     */
    @DisplayName("1건 fetch")
    @Test
    public void fetchFirstTest() {
        QMember qMember = QMember.member;
        Member findMember = queryFactory.selectFrom(qMember)
                    .where(
                            qMember.age.eq(20)
                    )
                    .fetchFirst();
        assertEquals(findMember.getAge(), 20);
    }
    @Test
    public void fetchTest() {
        QMember qMember = QMember.member;
        List<Member> findMembers = queryFactory.selectFrom(qMember)
                .where(
                        qMember.age.eq(20)
                )
                .fetch();
        assertEquals(findMembers.size(), 2);
    }

    /**
     *
     */
    @DisplayName("order by : null위치 변경")
    @Test
    public void fetchOrderbyTest() {
        QMember qMember = QMember.member;
        List<Member> findMembers = queryFactory.selectFrom(qMember)
                .where(
                        qMember.age.eq(30)
                )
                .orderBy(
                        qMember.username.asc().nullsLast()
                )
                .fetch();

        Member member0 = findMembers.get(0);
        Member member1 = findMembers.get(1);
        Member member2 = findMembers.get(2);

        assertEquals(member0.getUsername(), "member4");
        assertEquals(member1.getUsername(), "member5");
        assertNull(member2.getUsername());
    }
    @DisplayName("paging")
    @Test
    public void pagingTest() {
        QMember qMember = QMember.member;
        List<Member> findMembers = queryFactory.selectFrom(qMember)
                .orderBy(
                        qMember.username.asc().nullsLast()
                )
                .offset(2) // 2번째부터
                .limit(2)  // 2개 fetch
                .fetch();

        Member member0 = findMembers.get(0);

        assertEquals(member0.getUsername(), "member3");
        assertEquals(findMembers.size(), 2);
    }

    @DisplayName("group by")
    @Test
    public void groupbyTest() {
        QMember qMember = QMember.member;
        QTeam qTeam = QTeam.team;

        List<Tuple> results = queryFactory
                .select(
                        qTeam.name,
                        qMember.age.avg(),
                        qMember.count()
                )
                .from(qMember)
                .join(qMember.team, qTeam)
                .groupBy(qTeam.name)
                .having(qMember.age.avg().goe(10))
                .fetch();

        Tuple team1 = results.get(0);

        assertEquals(team1.get(qTeam.name), "Team 1");
        assertEquals(team1.get(qMember.count()), 3);
    }

    /**
     * 관계가 있는 테이블간 ArrayList 테스트하기
     */
    @DisplayName("left join")
    @Test
    public void leftJoinTest() {
        QMember qMember = QMember.member;
        QTeam qTeam = QTeam.team;

        List<Member> findMembers = queryFactory.selectFrom(qMember)
                .leftJoin(qMember.team, qTeam)
                .where(qTeam.name.eq("Team 2"))
                .fetch();

        assertThat(findMembers)
                .extracting("username")
                .containsExactly("member3", "member5", null);
    }

    /**
     * from절에 fk가 없는 여러테이블을 선택해서 조인
     */
    @DisplayName("fk 없는 join")
    @Test
    public void joinNoRelationTest() {
        QMember qMember = QMember.member;
        QTeam qTeam = QTeam.team;

        List<Tuple> fetch = queryFactory.select(qMember, qTeam)
                .from(qMember)
                .leftJoin(qTeam).on(qMember.team.id.eq(qTeam.id))
                .where(qMember.age.eq(30))
                .fetch();
        for (Tuple tuple: fetch) {
            System.out.println("Tuple : " + tuple);
        }
    }

    /**
     * getPersistenceUnitUtil()을 사용하기 위해 선언한다. 이를통해 member.getTeam()가 loaded되었는지 확인한다.
     * Teaam은 Lazy정책에 따라 실제 사용될 때 가져온다.
     */
    @PersistenceUnit
    EntityManagerFactory emf;
    @DisplayName("member.getTeam()이 실행되지 않도록 Query")
    @Test
    public void noFetchJoinTest() {
        em.flush();
        em.clear();

        QMember qMember = QMember.member;
        QTeam qTeam = QTeam.team;

        Member member = queryFactory
                .selectFrom(qMember)
                .join(qMember.team, qTeam)
                .where(qMember.username.eq("member1"))
                .fetchFirst();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member.getTeam());
        assertEquals(loaded, false);
    }
    /**
     * getPersistenceUnitUtil()을 사용하기 위해 선언한다. 이를통해 member.getTeam()가 loaded되었는지 확인한다.
     * Member, Teaam을 한꺼번에 가져온다
     */
    @DisplayName("member.getTeam() 실행되도록 Query")
    @Test
    public void fetchJoinTest() {
        em.flush();
        em.clear();

        QMember qMember = QMember.member;
        QTeam qTeam = QTeam.team;

        Member member = queryFactory
                .selectFrom(qMember)
                .join(qMember.team, qTeam).fetchJoin()
                .where(qMember.username.eq("member1"))
                .fetchFirst();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member.getTeam());
        assertEquals(loaded, true);
    }
    @DisplayName("where에서 JPAExpressions.select -> select")
    @Test
    public void subQueryEqTest()  {
        QMember qMember = QMember.member;
        QMember subMember = QMember.member;

        List<Member> members = queryFactory
                .selectFrom(qMember)
                .where(qMember.age.eq(
                        select(subMember.age.max())
                                .from(subMember))
                ).fetch();

        assertThat(members).extracting("age")
                .containsExactly(40);
    }
    @DisplayName("select에서 JPAExpressions.select -> select")
    @Test
    public void selectSubQueryTest() {
        QMember qMember = QMember.member;
        QMember subMember = QMember.member;

        List<Tuple> result = queryFactory
                .select(
                        qMember.username,
                        select(subMember.age.max())
                                .from(subMember)
                                .where(qMember.team.id.eq(subMember.team.id))
                )
                .from(qMember)
                .fetch();

        for (Tuple tuple: result) {
            System.out.println("Tuple : " + tuple);
        }
    }
    @DisplayName("case basic")
    @Test
    public void caseBasicTest() {
        QMember qMember = QMember.member;

        List<Tuple> result = queryFactory
                .select(qMember.username, qMember.age
                        .when(10).then("10살")
                        .when(20).then("20살")
                        .when(30).then("30살")
                        .otherwise("많음"))
                .from(qMember)
                .fetch();

        for (Tuple t: result) {
            System.out.println(t);
        }
    }
    @DisplayName("case CaseBuilder, Tuple")
    @Test
    public void caseCaseBuilderTest() {
        QMember qMember = QMember.member;

        List<Tuple> result = queryFactory
                .select(qMember.username, new CaseBuilder()
                        .when(qMember.age.between(0, 20)).then("어림")
                        .when(qMember.age.between(21, 40)).then("젊음")
                        .otherwise("많음").as("ageRange"))
                .from(qMember)
                .fetch();

        for (Tuple t: result) {
            String username = t.get(qMember.username);
            System.out.println("username = " + username);
        }
    }
    @DisplayName("constant와 concat, stringValue 사용")
    @Test
    public void constantConcatTest() {
        QMember qMember = QMember.member;

        List<Tuple> fetch = queryFactory.select(qMember.username.concat("_").concat(qMember.age.stringValue()),
                        Expressions.constant("A"))
                .from(qMember)
                .fetch();

        for (Tuple t: fetch) {
            System.out.println(t);
        }
    }

    /**
     * 데이터 :
     * Member member1 = new Member("member1", 10, team1);
     */
    @DisplayName("bulk update 처리 테스트 : No flush")
    @Test
    public void bulkUpdateNoflushTest() {
        // Persistance Context를 무시하고 DB에 SQL을 실행한다.
        // 따라서 Persistance Context와 DB의 값이 달라진다.
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        // Persistance Context는 변경되지 않아서 DB와 달라지 상태이다.

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.lt(28))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
        assertEquals(result.get(0).getUsername(), "member1");
    }

    /**
     * 데이터 :
     * Member member1 = new Member("member1", 10, team1);
     */
    @DisplayName("bulk update 처리 테스트 : Flush")
    @Test
    public void bulkUpdateFlushTest() {
        // Persistance Context를 무시하고 DB에 SQL을 실행한다.
        // 따라서 Persistance Context와 DB의 값이 달라진다.
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        // Persistance Context를 삭제한다.
        em.flush();
        em.clear();

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.lt(28))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
        assertEquals(result.get(0).getUsername(), "비회원");
    }
    @DisplayName("기존값 기준으로 Update 테스트")
    @Test
    public void updateAddVerify() {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(-1))
                .where(member.age.eq(10))
                .execute();
//        em.flush();
//        em.clear();

        List<Member> result = queryFactory.select(member)
                .from(member)
                .where(member.age.eq(9))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    /**
     * 시용자 Function을 사용하는 경우 :
     * H2Dialect를 상속해서, 새로운 H2Dialect를 생성한 후 application.yml에 해당 H2Dialect를 등록한다.
     * 상속된 H2Dialect에는 사용하려는 Function이 registerFunction되어 있어야 한다.
     */
    @DisplayName("SQL Funtion 실행 검증")
    @Test
    public void sqlStringFunctionVerify() {
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('ucase', {0})",
                        member.username))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    @DisplayName("SQL Funtion 실행 검증 (ANSI Standard)")
    @Test
    public void sqlAnsiFunctionVerify() {
        List<String> result = queryFactory
                .select(member.username.upper())
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
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