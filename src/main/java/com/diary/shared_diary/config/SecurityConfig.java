package com.diary.shared_diary.config;

import com.diary.shared_diary.auth.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final OAuth2Properties oAuth2Properties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable()
                )
                .headers(headers -> headers
                        .disable()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/**", "/oauth2/**", "/login/oauth2/**", "/h2-console/**", "/dev/**").permitAll()
                        .requestMatchers("/api/groups/**", "/api/diaries/**").authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getPrincipal() == null) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 인증 정보가 없습니다.");
                                return;
                            }
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            Map<String, Object> kakaoAccount = oauthUser.getAttribute("kakao_account");
                            String email = (String) kakaoAccount.get("email");

                            String token = jwtUtil.generateToken(email);
                            String redirectUri = oAuth2Properties.getRedirectUri();
                            response.sendRedirect(redirectUri + "?token=" + token);
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                        })
                )

        ;

        return http.build();
    }

}
