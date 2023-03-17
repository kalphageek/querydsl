package me.kalpha.querydsldemo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.dto.QMemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.repository.support.PagenationRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.util.StringUtils.isNullOrEmpty;
import static me.kalpha.querydsldemo.entity.QMember.member;
import static me.kalpha.querydsldemo.entity.QTeam.team;

/**
 * Paging이 필요한 Query를 간편하게 할 수 있도록 개선된 코드
 * QueryRepository.class의 PagingRepositorySupport 버전
 */
@Repository
public class Query2Repository extends PagenationRepositorySupport {
    public Query2Repository() {
        super(Member.class);
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return getQueryFactory()
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
        return applyPagination(pageable, query -> query
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
                .orderBy(member.username.asc().nullsFirst()));
    }

    public Page<MemberTeamDto> searchComplexPage(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
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
                        .orderBy(member.username.asc().nullsFirst()),
               countQuery -> countQuery
                        .select(member.count())
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())));
    }

    private BooleanExpression usernameEq(String username) {
        return isNullOrEmpty(username) ? null : member.username.eq(username);
    }
    private BooleanExpression teamNameEq(String teamName) {
        return isNullOrEmpty(teamName) ? null : member.team.name.eq(teamName);
    }
    private BooleanExpression ageGoe(Integer ageGeo) {
        return ageGeo == null ? null : member.age.goe(ageGeo);
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }
}
