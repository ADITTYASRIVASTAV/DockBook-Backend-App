package com.Aditya.DocBookApp.Security;

import com.Aditya.DocBookApp.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails
{
    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }
    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }
    @Override
    public boolean isEnabled() {
        return userEntity.isEmailVerified();
    }
}
