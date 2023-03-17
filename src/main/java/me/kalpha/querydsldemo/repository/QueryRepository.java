package me.kalpha.querydsldemo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.dto.QMemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static me.kalpha.querydsldemo.entity.QMember.member;
import static me.kalpha.querydsldemo.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * Query 전용 Repository
 * Entity 없이 복합 Query 전용으로 사용할 경우에는 MemberRepository, MemberRepositoryCustom, MemberRepositoryImpl 없이
 * QueryRepository를 클래스로 직접 생성해서 사용할 수 있다.
 */
@Repository
public class QueryRepository {
    private final JPAQueryFactory queryFactory;

    public QueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

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

    public Page<MemberTeamDto> searchSimplePage(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> fetch = queryFactory
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
                .orderBy(member.username.asc().nullsFirst())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(fetch, pageable, fetch.stream().count());
    }

    /**
     * 첫페이지이면서 Pagesize보다 Recordcount가 적거나, 마지막페이지 등 일부의 경우 Total Query가 필요없다.
     * 필요한 경우만 countQuery가 실행 된다.
     * @param condition
     * @param pageable
     * @return
     */
    public Page<MemberTeamDto> searchComplexPage(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> fetch = queryFactory
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
                .orderBy(member.username.asc().nullsFirst())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()));

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery.stream()::count);
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
