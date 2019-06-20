package com.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mapper.UserMapper;
import com.model.User;

/**
 * 
 * @Cacheable是基于Spring AOP切面，必须走代理才有效
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Override
	public User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}
}
