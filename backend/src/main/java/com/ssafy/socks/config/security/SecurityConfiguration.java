package com.ssafy.socks.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic()
			.disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
			.csrf()
			.disable() // rest api 이므로 csrf 보안이 필요없으므로 disable
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token 으로 인증하므로 세션은 필요없으므로 생성안함.
			.and()
			.authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
			.antMatchers("/*/signin/**", "/*/signup/**")
			.permitAll() // 가입 및 인증 주소는 누구나 접근가능
			.antMatchers(HttpMethod.GET, "/exception/**", "/docs/**", "/api-docs/**")
			.permitAll() // exception 시작하는 GET요청 리소스는 누구나 접근가능
			.antMatchers("/*/users")
			.hasRole("ADMIN") // api 시작하는 리소스는 관리자만 접근가능
			.anyRequest()
			.hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
			.and()
			.exceptionHandling()
			.accessDeniedHandler(new CustomAccessDeniedHandler())    // 유저의 권한이 없을 때
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(new CustomAuthenticationEntryPoint())    // jwt token 필터 전에 토큰 유효성 검사에 대해 예외 처리
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 넣는다

	}

}
