package org.anonymous.member;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.anonymous.member.constants.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 매핑되지않은 필드가 있으면 예외발생하지 않게 배제
public class Member implements UserDetails { // 구현체

    private Long seq; // 회원번호
    private String email;
    private String name;

    @JsonAlias("authorities")
    private List<Authority> _authorities; // 권한

    /**
     * 권한 체크 부분
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return _authorities == null || _authorities.isEmpty()
                ? List.of() // 오류방지
                : _authorities.stream().map( s -> new SimpleGrantedAuthority(s.name())).toList();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 회원인증관련은 Member쪽에서 통제하기 때문에 여기서는 true로 처리하여 활성화함.
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