package com.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken; 
    private MemberDto member; 
}

@Getter
@Builder
@AllArgsConstructor
class MemberDto {
    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String provider; // OAuth 제공자
    
 
    public static MemberDto from(Member member) {
        return MemberDto.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getUsername()) 
            .profileImage(member.getProfileImage())
            .provider(member.getProvider()) 
            .build();
    }
}

