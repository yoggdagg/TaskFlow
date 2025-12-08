package com.taskflow.repositories;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.taskflow.dto.Member;

@Mapper
public interface MemberMapper {

	Optional<Member> findByEmail(String email);

	void registerMember(Member member);

	// OAuth 사용자 조회
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // Provider + ProviderId 중복 체크
    boolean existsByProviderAndProviderId(String provider, String providerId);
}
