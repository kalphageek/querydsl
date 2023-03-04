package me.kalpha.querydsldemo.local;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.Team;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Local 환경 실행을 위한 초기 데이터 생성작업
 * @PostConstuct와 @Transactional을 함께 사용할 수 없다. 그래서 Class가 분리되어 있음
 */
@Profile("local")
@RequiredArgsConstructor
@Component
public class InitMember {
    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for(int i=0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member"+i, i, selectedTeam));
            }
        }
    }
}
