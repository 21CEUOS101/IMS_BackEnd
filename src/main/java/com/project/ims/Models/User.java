package com.project.ims.Models;

import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = List.of(() -> this.role);
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email; // email is used as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // account never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // credentials never expires
    }

    @Override
    public boolean isEnabled() {
        return true; // account always enabled
    }
    
}
