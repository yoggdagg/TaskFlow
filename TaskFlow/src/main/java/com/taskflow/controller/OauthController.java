package com.taskflow.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.LoginResponse;
import com.taskflow.dto.Member;
import com.taskflow.security.JWTUtil;
import com.taskflow.service.OauthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {
    
    private final OauthService oauthService;
    private final JWTUtil jwtUtil;

    
    /**
     * 구글 OAuth 콜백 처리
     */
    @PostMapping("/google/callback")
    public ResponseEntity<Member> googleCallback(
        @RequestBody Map<String, String> request,
        HttpServletResponse response
    ) {
        try {
            String code = request.get("code");
            
            if (code == null || code.trim().isEmpty()) {
                log.error("구글 인증 코드가 누락되었습니다");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            Member member = oauthService.googleLogin(code);
            
            // JWTUtil을 사용하여 토큰 유효성 검증
            if (!jwtUtil.validateJWT(member.getAccessToken()) || 
                !jwtUtil.validateJWT(member.getRefreshToken())) {
                log.error("생성된 JWT 토큰이 유효하지 않습니다");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            
            // Refresh Token을 HttpOnly, Secure 쿠키로 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", member.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // 나중에 가능하면 https등록
            //refreshTokenCookie.setDomain("localhost");
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
            response.addCookie(refreshTokenCookie);
            
            // 응답 body에서 refreshToken 제거 (쿠키로만 전달)
            member.setRefreshToken(null);
            
            log.info("구글 로그인 성공: {}", jwtUtil.getEmailFromJWT(member.getAccessToken()));
            
            // Access Token과 사용자 정보만 응답
            return ResponseEntity.ok(member);
            
        } catch (Exception e) {
            log.error("구글 OAuth 콜백 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    

    /**
     * 네이버 OAuth 콜백 처리
     */
    @PostMapping("/naver/callback")
    public ResponseEntity<Member> naverCallback(
        @RequestBody Map<String, String> request,
        HttpServletResponse response
    ) {
    		try {
    			String code = request.get("code");
    			// csrf공격을 방지하기 위해 네이버는 state값도 요구한다....
    			String state = request.get("state");
    			
    			if(code == null || code.isEmpty()) {
    				log.error("네이버 인증 코드 누락");
    				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    			}
    			if(state ==  null || state.isEmpty()) {
    				log.error("네이버 state 파라미터 누락");
    				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    			}
    			
    			Member member = oauthService.naverLogin(code, state);
    			
    			// JWT 유효성 검증
    			if(!jwtUtil.validateJWT(member.getAccessToken()) || !jwtUtil.validateJWT(member.getRefreshToken())) {
    				log.error("JWT가 유효하지 않습니다.");
    				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    			}
    			
    			Cookie refreshTokenCookie = new Cookie("refreshToken", member.getRefreshToken());
    			refreshTokenCookie.setHttpOnly(true);
    			
    			refreshTokenCookie.setPath("/");
    			refreshTokenCookie.setMaxAge(7*24*60*60);
    			response.addCookie(refreshTokenCookie);
    			
    			member.setRefreshToken(null);
    			
    			log.info("네이버 로그인 성공: {}", jwtUtil.getEmailFromJWT(member.getAccessToken()));
    			
    			return ResponseEntity.ok(member);
    		} catch(Exception e) {
    			log.error("네이버 OAuth 콜백 처리 실패: {}", e.getMessage());
    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    		}
    }
    /*
     * 카카오 Oauth 콜백처리
     */
    @PostMapping("/kakao/callback")
    public ResponseEntity<Member> kakaoCallback(
    		@RequestBody Map<String, String> request, HttpServletResponse response
    	) {
    		try {
    			String code = request.get("code");
    			
    			if(code == null || code.isEmpty()) {
    				log.error("카카오 인증 코드 누락");
    				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    			}
    			
    			Member member = oauthService.kakaoLogin(code);
    			
    			if(!jwtUtil.validateJWT(member.getAccessToken()) || !jwtUtil.validateJWT(member.getRefreshToken())) {
    				log.error("jwt가 유효하지 않음");
    				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    			}
    			
    			Cookie refreshTokenCookie = new Cookie("refreshToken", member.getRefreshToken());
    			refreshTokenCookie.setHttpOnly(true);
    			refreshTokenCookie.setSecure(false);
    			refreshTokenCookie.setPath("/");
    			refreshTokenCookie.setMaxAge(7*24*60*60);
    			response.addCookie(refreshTokenCookie);
    			
    			member.setRefreshToken(null);
    			
    			log.info("네이버 로그인 성공: {}", jwtUtil.getEmailFromJWT(member.getAccessToken()));
    			return ResponseEntity.ok(member);
    		} catch(Exception e) {
    			log.error("카카오 OAuth 콜백 처리 실패: {}", e.getMessage());
    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    		}
    }
    
}
