package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("deprecation")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String query = "SELECT username, password, enabled FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username}, (rs, rowNum) -> {
            boolean enabled = rs.getBoolean("enabled");
            String uname = rs.getString("username");
            String pwd = rs.getString("password");
            
            return User.builder()
                    .username(uname)
                    .password(pwd)
                    .disabled(!enabled)
                    .build();
        });
    }
}