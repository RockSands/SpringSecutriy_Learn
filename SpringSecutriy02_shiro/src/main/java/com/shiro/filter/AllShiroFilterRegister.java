package com.shiro.filter;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mapper.UserMapper;

@Configuration
public class AllShiroFilterRegister {
	
	/**
	 * 并发登录控制
	 * 
	 * @return
	 */
	@Bean
	public KickoutSessionControlFilter kickoutSessionControlFilter(SessionManager sessionManager,
			CacheManager shiroCacheManager) {
		KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
		// 用于根据会话ID，获取会话进行踢出操作的；
		kickoutSessionControlFilter.setSessionManager(sessionManager);
		// 使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
		kickoutSessionControlFilter.setCacheManager(shiroCacheManager);
		// 是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
		kickoutSessionControlFilter.setKickoutAfter(false);
		// 同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
		kickoutSessionControlFilter.setMaxSession(1);
		// 被踢出后重定向到的地址；
		kickoutSessionControlFilter.setKickoutUrl("/login?kickout=1");
		return kickoutSessionControlFilter;
	}

	@Bean
	public URLPathMatchingFilter uRLPathMatchingFilter(UserMapper userMapper) {
		URLPathMatchingFilter uRLPathMatchingFilter = new URLPathMatchingFilter();
		uRLPathMatchingFilter.setOutUrl("/login?out=1");
		uRLPathMatchingFilter.setUserMapper(userMapper);
		return uRLPathMatchingFilter;
	}

}
