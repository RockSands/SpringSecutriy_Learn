package com.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 
 * 密码验证 自定义类 CredentialsMatcher是一个接口，功能就是用来匹配用户登录使用的令牌和数据库中保存的用户信息是否匹配
 * 
 * 
 * @author Administrator
 *
 */
@Component()
public class CredentialMatcher extends SimpleCredentialsMatcher {
	// 声明一个缓存接口，这个接口是Shiro缓存管理的一部分，它的具体实现可以通过外部容器注入
	private Cache<String, AtomicInteger> passwordRetryCache;

	public CredentialMatcher(CacheManager cacheManager) {
		passwordRetryCache = cacheManager.getCache("passwordRetryCache");
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String password = new String(usernamePasswordToken.getPassword());
		String dbPassword = (String) info.getCredentials();
		AtomicInteger retryCount = passwordRetryCache.get(usernamePasswordToken.getUsername());
		if (retryCount == null) {
			retryCount = new AtomicInteger(0);
			passwordRetryCache.put(usernamePasswordToken.getUsername(), retryCount);
		}
		// 自定义一个验证过程：当用户连续输入密码错误5次以上禁止用户登录一段时间
		if (retryCount.incrementAndGet() > 5) {
			throw new RuntimeException();// 抛出异常,禁止登陆,等待缓存自动清除
		}
		boolean isEqual = this.equals(password, dbPassword);
		if (isEqual) {
			passwordRetryCache.remove(usernamePasswordToken.getUsername());// 清除缓存
		}
		return isEqual;
	}
}
