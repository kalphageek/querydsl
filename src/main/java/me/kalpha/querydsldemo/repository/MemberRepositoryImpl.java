package me.kalpha.querydsldemo.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.dto.QMemberTeamDto;
import me.kalpha.querydsldemo.entity.QMember;
import me.kalpha.querydsldemo.entity.QTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static me.kalpha.querydsldemo.entity.QMember.member;
import static me.kalpha.querydsldemo.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * MemberRepositoryCustomImpl로 하면 안된다. 규칙이다
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

//    public MemberRepositoryImpl(JPAQueryFactory queryFactory) {
//        this.queryFactory = queryFactory;
//    }

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression teamNameEq(String teamNameParam) {
        return hasText(teamNameParam) ? team.name.eq(teamNameParam) : null;
    }

    private BooleanExpression usernameEq(String usernameParam) {
        return hasText(usernameParam) ? member.username.eq(usernameParam) : null;
    }

    private BooleanExpression ageGoe(Integer ageParam) {
        return ageParam == null ? null : member.age.goe(ageParam);
    }

    private BooleanExpression ageLoe(Integer ageParam) {
        return ageParam == null ? null : member.age.loe(ageParam);
    }

}
