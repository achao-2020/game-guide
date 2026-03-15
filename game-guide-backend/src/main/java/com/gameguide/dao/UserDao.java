package com.gameguide.dao;

import com.gameguide.entity.User;

/**
 * 用户数据访问层接口
 */
public interface UserDao {

    int insert(User user);

    User selectByUsername(String username);

    User selectById(Long id);
}


