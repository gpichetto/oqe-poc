package com.example.oqdpoc.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {
    
    private final String apiKey;

    public ApiKeyAuthentication(String key) {
        super(null);
        this.apiKey = key;
        setAuthenticated(true);
    }

    public ApiKeyAuthentication(String key, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = key;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return apiKey;
    }
}
