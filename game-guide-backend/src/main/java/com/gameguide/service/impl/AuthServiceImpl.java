package com.gameguide.service.impl;

import com.gameguide.dao.UserDao;
import com.gameguide.dto.LoginDTO;
import com.gameguide.dto.RegisterDTO;
import com.gameguide.entity.User;
import com.gameguide.exception.BusinessException;
import com.gameguide.security.JwtUtil;
import com.gameguide.service.AuthService;
import com.gameguide.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        User existingUser = userDao.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("USER");
        userDao.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
            User user = userDao.selectByUsername(loginDTO.getUsername());
            if (user == null) {
                throw new BusinessException("用户名或密码错误");
            }
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            return new LoginVO(token, user.getUsername(), user.getRole());
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            throw new BusinessException("用户名或密码错误");
        }
    }
}
