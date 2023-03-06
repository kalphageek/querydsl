package me.kalpha.querydsldemo.repository;

import me.kalpha.querydsldemo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA가 제공하는 기본적인 Entity의 메소드와 Custom Query를 함께 사용하는 경우는
 * MemberRepository, MemberRepositoryCustom, MemberRepositoryImpl 를 생성해서 사용할 수 있다.
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByUsername(String username);
}
