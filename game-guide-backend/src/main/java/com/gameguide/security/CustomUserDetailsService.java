package com.gameguide.security;

import com.gameguide.entity.User;
import com.gameguide.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("========== CustomUserDetailsService.loadUserByUsername ==========");
        log.info("加载用户: {}", username);
        
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        log.info("用户找到，角色: {}", user.getRole());
        log.info("数据库密码前缀: {}...", user.getPassword().substring(0, Math.min(20, user.getPassword().length())));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
        
        log.info("UserDetails 创建完成");
        return userDetails;
    }
}



