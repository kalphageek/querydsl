package me.kalpha.querydsldemo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberDto2 {
    private String username;
    private int age;

    /**
     * @QueryProjection : Maven Compile하면 Q파일이 생성되도록 한다.
     * @param username
     * @param age
     */
    @QueryProjection
    public MemberDto2(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
