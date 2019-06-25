package com.shiro.cache;

import org.apache.shiro.cache.
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ShiroCacheConfig {

//	@Bean
//	public EhCacheManager shiroCacheManager(net.sf.ehcache.CacheManager cacheManager) {
//		EhCacheManager em = new EhCacheManager();
//		// 将ehcacheManager转换成shiro包装后的ehcacheManager对象
//		em.setCacheManager(cacheManager);
//		return em;
//	}
	
	@Bean
	public RedisCacheManager shiroCacheManager(RedisManager redisManager) {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        //redis中针对不同用户缓存
        redisCacheManager.setPrincipalIdFieldName("username");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
	}
}
