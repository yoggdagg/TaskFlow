package com.taskflow.repositories;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.taskflow.dto.Member;

@Mapper
public interface MemberMapper {

    Optional<Member> findByEmail(String email);

    void registerMember(Member member);

    // OAuth 사용자 조회
    Optional<Member> findByProviderAndProviderId(@Param("provider") String provider, 
                                                   @Param("providerId") String providerId);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // Provider + ProviderId 중복 체크
    boolean existsByProviderAndProviderId(@Param("provider") String provider, 
                                           @Param("providerId") String providerId);

    Member login(@Param("email") String email, @Param("pw") String pw);
    
    // Refresh Token 업데이트
    void updateRefreshToken(@Param("id") Long id, 
                            @Param("refreshToken") String refreshToken,
                            @Param("tokenExpiresAt") java.time.LocalDateTime tokenExpiresAt);
    
    // Refresh Token으로 회원 조회
    Optional<Member> findByRefreshToken(String refreshToken);
    
    // Refresh Token 삭제 (로그아웃)
    void deleteRefreshToken(@Param("id") Long id);
    
    // 마지막 로그인 시간 업데이트
    void updateLastLoginAt(@Param("id") Long id);
}
