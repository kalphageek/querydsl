package me.kalpha.querydsldemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Commit
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("Get /v1/members Test")
    @Test
    public void findSearchTest() throws Exception {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        mockMvc.perform(get("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @DisplayName("Get /v2/members Test")
    @Test
    public void findSearchSimpleTest() throws Exception {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamA");

        mockMvc.perform(get("/v2/members?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @DisplayName("Get /v1/member Test")
    @Test
    public void findByIdTest() throws Exception {
        Long memberId = 4l;
        mockMvc.perform(get("/v1/member/{memberId}", memberId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @BeforeEach
    private void initData() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 30, teamA);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
}