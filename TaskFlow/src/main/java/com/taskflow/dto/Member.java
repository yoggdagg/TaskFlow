package com.taskflow.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private Long id;
    
    @JsonProperty("name")  // 프론트에서 name이란 key로 전송하고 있음.
    private String username;
    private String email;
    
    @JsonProperty("password") // 프론트에서 password란 key로 전달하고 있음.
    private String pw;
    
    private String address;
    private String phone;
    
    // OAuth 관련 필드 추가
    private String provider;        // "LOCAL", "GOOGLE", "NAVER", "KAKAO"
    private String providerId;      // OAuth 제공자로부터 받은 고유 ID
    private String profileImage;    // 프로필 이미지 URL
    
    private String role;            // "ROLE_USER", "ROLE_ADMIN"
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
