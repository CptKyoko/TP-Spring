package com.nagiel.tp.payload.response;

public class JwtResponse {
    private String token;
    private UserInfoResponse userInfo;

    public JwtResponse(String token, UserInfoResponse userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public UserInfoResponse getUserInfo() {
        return userInfo;
    }
}