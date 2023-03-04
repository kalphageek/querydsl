package me.kalpha.querydsldemo.repository;

import me.kalpha.querydsldemo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(String username);
}
