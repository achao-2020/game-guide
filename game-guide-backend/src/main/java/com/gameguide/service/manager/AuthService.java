package com.gameguide.service.manager;

import com.gameguide.dto.LoginDTO;
import com.gameguide.dto.RegisterDTO;
import com.gameguide.vo.LoginVO;

public interface AuthService {
    
    void register(RegisterDTO registerDTO);
    
    LoginVO login(LoginDTO loginDTO);
}



