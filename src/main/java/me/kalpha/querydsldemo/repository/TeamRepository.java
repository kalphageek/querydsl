package me.kalpha.querydsldemo.repository;

import me.kalpha.querydsldemo.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * QuerydslPredicateExecutor : Querydsl에서 JPA Interface를 사용할 수 있도록 해준다.
 * TeamRepositoryTest 확인.
 * Pageable 지원한다.
 *
 * 단일 테이블에만 사용 가능하다. (조인 안됨)
 */
public interface TeamRepository extends JpaRepository<Team, Long>, QuerydslPredicateExecutor<Team> {
}
