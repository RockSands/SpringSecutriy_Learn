package com.service;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ShiroCacheService {
	@Autowired
	@Qualifier("shiroCacheManager")
	private CacheManager shiroCacheManager;

	public void removeUserAuthorization(String userName) {
		Cache<SimplePrincipalCollection, Object> cache = shiroCacheManager.getCache("authorizationCache");
		if (cache == null) {
			return;
		}
		for(Object key : cache.keys()){
			System.out.println(key + ":" + key.toString());
		}
		cache.remove(new SimplePrincipalCollection(userName, "com.shiro.ShiroRealm"));
	}

}
