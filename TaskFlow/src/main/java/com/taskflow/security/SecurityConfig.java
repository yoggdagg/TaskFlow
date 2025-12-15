package com.taskflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private JWTAuthenticationFilter JWTAuthenticationFilter;
	private static final String[] WHITE_LIST = { "/api/member/login", "/api/member/logout", "/api/member/regist", "/error/**" };
	private final CorsConfig corsConfig;

//	SecurityConfig(CustomUserDetailsService customUserDetailsService) {
//		this.customUserDetailsService = customUserDetailsService;
//	}

	// 필터 체인 상세 로그 설정
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		log.info("Enabling Spring Security Debug Mode");
		return (web) -> web.debug(true);
	}

	@Bean
	@Order(1) // JWT를 활용한 일반 인증
	public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
		log.info("=== Configuring Default Filter Chain (API & Public) ===");

		http.securityMatcher("/api/**", "/public/**")
				.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())) // CORS 설정 적용
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(WHITE_LIST).permitAll()
						.requestMatchers("/api/admin").hasRole("ADMIN").anyRequest().authenticated())
				// jwt를 사용하기 위해 무상태로 설정
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// JWT필터를 UsernamePasswordAuthenticationFilter 앞에 추가해야한다.
				.addFilterBefore(JWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		log.info("Default Filter Chain configured successfully");
		return http.build();
	}

	@Bean
	@Order(2) // Oauth인증을 통한 인증(google, naver, kakao)
	public SecurityFilterChain oauthFilterChain(HttpSecurity http) throws Exception {
		log.info("=== Configuring OAuth Filter Chain ===");

		http
			.securityMatcher("/oauth/**").csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())) // CORS 설정 적용
			.authorizeHttpRequests(
					authorize -> authorize.requestMatchers("/oauth/google/**", "/oauth/naver/**", "/oauth/kakao/**")
							.permitAll().anyRequest().authenticated())
			// jwt를 사용하기 위해 무상태로 설정
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// JWT필터를 UsernamePasswordAuthenticationFilter 앞에 추가해야한다.
			.addFilterBefore(JWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		log.info("OAuth Filter Chain configured successfully");
		return http.build();
	}



	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
