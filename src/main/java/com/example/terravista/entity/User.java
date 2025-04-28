package com.example.terravista.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users") // 避免与数据库关键字冲突
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 使用数据库自增主键
    private Long id;

    @Column(unique = true, nullable = false) // 用户名唯一且非空
    private String username;

    @Column(nullable = false) // 密码非空
    private String password;

    @NotBlank
    private String role;

    @Column(unique = true) // API Key 唯一
    private String apiKey;

    @Column(unique = true) // email可选，但如果提供则必须唯一
    private String email;

    // 实现 UserDetails 接口的方法
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 这里将角色转换为 GrantedAuthority
        return Collections.singletonList(() -> "ROLE_" + role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账户是否未过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 账户是否未锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证是否未过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账户是否启用
    }
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}