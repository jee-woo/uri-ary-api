package com.diary.shared_diary.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final String email;

    public CustomUserDetails(String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 아래는 기본적으로 false 아니면 true로 채워도 무방
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 권한 필요 시 채우기
    }

    @Override public String getPassword() {
        return null; // 비밀번호 기반 인증이 아니므로 null
    }
}
