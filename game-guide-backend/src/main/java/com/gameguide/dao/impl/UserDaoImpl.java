package com.gameguide.dao.impl;

import com.gameguide.dao.UserDao;
import com.gameguide.entity.User;
import com.gameguide.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserMapper userMapper;

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User selectById(Long id) {
        return userMapper.selectById(id);
    }
}


