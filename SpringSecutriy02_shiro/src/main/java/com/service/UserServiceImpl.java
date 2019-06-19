package com.service;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "passwordRetryCache")
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Override
	public User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}

	@Cacheable(key = "#userName")
	public AtomicInteger getCacheCounter(String userName) {
		System.out.println("==" + userName + "==");
		return new AtomicInteger(0);
	}

	/**
	 * CachePut与Cacheable的返回类型必须一致
	 * 
	 * @param userName
	 * @param value
	 * @return
	 */
	@CachePut(key = "#userName")
	public AtomicInteger setCacheCounter(String userName, AtomicInteger value) {
		return value;
	}

	/**
	 * 由于@Cacheable 有PUT的能力，所以CachePut与Cacheable的返回类型必须一致
	 * 
	 * @param userName
	 * @param value
	 * @return
	 */
	@CacheEvict(key = "#userName")
	public void cleanCacheCounter(String userName) {
	}
}
