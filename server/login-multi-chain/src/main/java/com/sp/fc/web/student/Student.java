package com.sp.fc.web.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    private String id;
    private String username;
    @JsonIgnore // json으로 나타내기 어려워서 무시하기
    private Set<GrantedAuthority> role;  // student의 권한 설정


    private String teacherId; // 선생님 id
}
