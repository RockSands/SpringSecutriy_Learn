package com.shiro.cache;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ShiroCacheConfig {

	@Bean("ehCacheManager")
	public EhCacheManager ehCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		EhCacheManager em = new EhCacheManager();
		// 将ehcacheManager转换成shiro包装后的ehcacheManager对象
		em.setCacheManager(cacheManager);
		return em;
	}
}
