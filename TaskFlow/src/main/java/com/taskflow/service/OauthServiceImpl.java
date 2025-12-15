package com.taskflow.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.taskflow.dto.Member;
import com.taskflow.repositories.MemberMapper;
import com.taskflow.security.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {
    
    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client-secret}")
    private String clientSecret;
    @Value("${google.redirect-uri}")
    private String redirectUri;
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    
    // Naver OAuth 설정
    @Value("${oauth.naver.client-id}")
    private String naverClientId;
    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;
    @Value("${oauth.naver.redirect-uri}")
    private String naverRedirectUri;
    private static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";

    // Kakao OAuth 설정
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    
    private final JWTUtil jwtUtil;
    private final MemberMapper memberMapper;
    private final RestTemplate restTemplate;
    
    
    @Override
    public Member googleLogin(String code) {
        try {
            String accessToken = getGoogleAccessToken(code);
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);
            
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String picture = (String) userInfo.get("picture");
            String googleId = (String) userInfo.get("sub");
            
            log.info("구글 사용자 정보 조회 성공: email={}, name={}, googleId={}", email, name, googleId);
            
            Optional<Member> existingMember = memberMapper.findByProviderAndProviderId("google", googleId);
            
            Member member;
            if (existingMember.isEmpty()) {
                if (memberMapper.existsByEmail(email)) {
                    log.error("이미 해당 이메일로 가입된 계정이 존재합니다: {}", email);
                    throw new RuntimeException("이미 해당 이메일로 가입된 계정이 존재합니다.");
                }
                
                member = Member.builder()
                    .username(name)
                    .email(email)
                    .profileImage(picture)
                    .provider("google")
                    .providerId(googleId)
                    .role("ROLE_USER")
                    .isActive(true)
                    .build();
                
                memberMapper.registerMember(member);
                log.info("신규 회원 가입 완료: email={}, provider=google", email);
            } else {
                member = existingMember.get();
                log.info("기존 회원 로그인: email={}, provider=google", email);
            }
            
            // JWT 토큰 생성
            String jwtAccessToken = jwtUtil.generateAccessToken(member);
            String jwtRefreshToken = jwtUtil.generateRefreshToken(member);
            
            // Refresh Token을 DB에 저장 (7일 만료)
            LocalDateTime tokenExpiresAt = LocalDateTime.now().plusDays(7);
            memberMapper.updateRefreshToken(member.getId(), jwtRefreshToken, tokenExpiresAt);
            
            // 마지막 로그인 시간 업데이트
            memberMapper.updateLastLoginAt(member.getId());
            
            member.setAccessToken(jwtAccessToken);
            member.setRefreshToken(jwtRefreshToken);
            return member;
                
        } catch (Exception e) {
            log.error("구글 로그인 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("구글 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }

    
    /**
     * 구글 서버로부터 액세스 토큰 발급
     */
    private String getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            GOOGLE_TOKEN_URL, 
            request, 
            Map.class
        );
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            log.error("구글 액세스 토큰 발급 실패");
            throw new RuntimeException("구글 액세스 토큰을 받을 수 없습니다.");
        }
        
        return (String) responseBody.get("access_token");
    }
    
    /**
     * 액세스 토큰으로 구글 사용자 정보 조회
     */
    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            GOOGLE_USER_INFO_URL,
            HttpMethod.GET,
            request,
            Map.class
        );
        
        Map<String, Object> userInfo = response.getBody();
        if (userInfo == null || !userInfo.containsKey("email")) {
            log.error("구글 사용자 정보 조회 실패");
            throw new RuntimeException("구글 사용자 정보를 받을 수 없습니다.");
        }
        
        return userInfo;
    }

    @Override
    public Member naverLogin(String code, String state) {
        try {
        		String accessToken = getNaverAccessToken(code, state);
        } catch(Exception e) {
        	 log.error("네이버 로그인 실패: {}", e.getMessage(), e);
             throw new RuntimeException("네이버 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 네이버 서버로부터 액세스 토큰 발급
     */
    private String getNaverAccessToken(String code, String state) {
    		HttpHeaders headers = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    		
    		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    		params.add("grant_type", "authorization_code");
    		params.add("client_id", naverClientId);
    		params.add("client_secret", naverClientSecret);
    		params.add("code", code);
    		params.add("state", state);
    		
    		HttpEntity<MultiValueMap<String, String>> request = restTemplate.postForEntity(NAVER_TOKEN_URL, request, JsonNode.class);
    }

    @Override
    public Member kakaoLogin(String code) {
        // TODO: 카카오 로그인 구현
        return null;
    }
}
