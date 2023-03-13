package me.kalpha.querydsldemo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.entity.QTeam;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static me.kalpha.querydsldemo.entity.QTeam.team;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    JPAQueryFactory jpaQueryFactory;
    @Autowired
    EntityManager em;
    @Autowired
    TeamRepository teamRepository;

    @DisplayName("PredicateExcutor Test")
    @Test
    public void querydslPredicateExcutorTest() {
        Iterable<Team> result = teamRepository.findAll(team.name.eq("teamA").or(team.name.eq("teamB")));
        result.forEach(System.out::println);
    }

    @BeforeEach
    private void initData() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");
        Team teamD = new Team("teamD");
        em.persist(teamA);
        em.persist(teamB);
        em.persist(teamC);
        em.persist(teamD);
    }

}