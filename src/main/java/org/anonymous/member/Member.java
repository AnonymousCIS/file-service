package org.anonymous.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.anonymous.member.constants.Authority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements UserDetails {

    private Long seq;

    private String email;

    private String name;

    private List<Authority> authorities;

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}