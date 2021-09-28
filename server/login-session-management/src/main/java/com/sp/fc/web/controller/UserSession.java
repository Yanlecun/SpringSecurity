package com.sp.fc.web.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private String username;
    private List<SessionInfo> sessions;
    
    public int getCount() {
        return sessions.size(); //세션 갯수
    }
}
