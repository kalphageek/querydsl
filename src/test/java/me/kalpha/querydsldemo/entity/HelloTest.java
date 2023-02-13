package me.kalpha.querydsldemo.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
// Test의 @Transactional은 모두 Rollback하게 한다. @Commit을 이용할 수 있다.
@Transactional
class HelloTest {
    @Autowired
    EntityManager em;

    @Test
    public void qHelloTest() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QHello qHello = QHello.hello;
        Hello result = queryFactory
                .selectFrom(qHello)
                .fetchOne();

        System.out.println(result.toString());
        // Querydsl 작동 확인
        assertEquals(result, hello);
        // Lombok 작동 확인
        assertEquals(result.getId(), hello.getId());
    }
}