package com.service;

import java.util.concurrent.atomic.AtomicInteger;

import com.model.User;

public interface UserService {

	User findByUsername(String username);

	public AtomicInteger getCacheCounter(String userName);

	/**
	 * 由于@Cacheable 有PUT的能力，所以CachePut与Cacheable的返回类型必须一致
	 * 
	 * @param userName
	 * @param value
	 * @return
	 */
	public AtomicInteger setCacheCounter(String userName, AtomicInteger value);

	/**
	 * 由于@Cacheable 有PUT的能力，所以CachePut与Cacheable的返回类型必须一致
	 * 
	 * @param userName
	 * @param value
	 * @return
	 */
	public void cleanCacheCounter(String userName);
}
