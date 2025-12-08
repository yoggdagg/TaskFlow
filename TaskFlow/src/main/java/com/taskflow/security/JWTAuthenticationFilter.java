package com.taskflow.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;
    private UserDetailsService userDetailsService;
    
    // JWT 검증을 건너뛸 경로 목록
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/api/member/login",
        "/api/member/regist",
        "/error",
        "/oauth/google",
        "/oauth/naver",
        "/oauth/kakao"
    );

   
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // WHITELIST에 포함된 경로는 필터를 건너뜀
        boolean shouldSkip = WHITELIST_PATHS.stream()
            .anyMatch(whitePath -> path.equals(whitePath) || path.startsWith(whitePath));
        
        if (shouldSkip) {
            log.info("JWT 필터 건너뛰기: {}", path);
        } else {
            log.debug("JWT 필터 실행: {}", path);
        }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // JWT 토큰 추출
            String jwt = getJWTFromRequest(request);
            
            // JWT가 존재하고 유효한 경우
            if (jwt != null && jwtUtil.validateJWT(jwt)) {
                String username = jwtUtil.getUsernameFromJWT(jwt);
                
                // 사용자 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 인증 객체 생성 및 SecurityContext에 저장
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ JWT 인증 성공: {} ({})", username, request.getRequestURI());
                
            } else if (jwt != null) {
                log.warn("❌ JWT 토큰이 유효하지 않음: {}", request.getRequestURI());
            } else {
                log.debug("ℹ️ JWT 토큰이 없음: {} (인증 필요 없는 요청일 수 있음)", request.getRequestURI());
            }
            
        } catch (Exception e) {
            log.error("❌ JWT 처리 중 오류 발생: {} - {}", request.getRequestURI(), e.getMessage());
            // 예외가 발생해도 필터 체인은 계속 진행
        }
        
        // 다음 필터로 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰 추출
     */
    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
