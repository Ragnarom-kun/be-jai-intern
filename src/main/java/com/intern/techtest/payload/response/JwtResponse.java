package com.intern.techtest.payload.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String access;
    private String refresh;
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, String refreshToken,
                       String username, List<String> roles) {
        this.access = accessToken;
        this.refresh = refreshToken;
        this.username = username;
        this.roles = roles;
    }
}
