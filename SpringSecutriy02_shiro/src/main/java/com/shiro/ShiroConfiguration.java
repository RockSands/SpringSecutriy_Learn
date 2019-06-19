package com.shiro;

import java.util.LinkedHashMap;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ShiroConfiguration {

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(manager);
		// 设定登录页面
		bean.setLoginUrl("/login");
		// 登录成功页面
		bean.setSuccessUrl("/index");
		// 无权限页面
		bean.setUnauthorizedUrl("/unauthorized");
		/*
		 * 该Map 的Key为URL的正则 Value为对应的拦截
		 * 
		 * DefaultFilter可以查看authc anon等定义
		 */
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 表单拦截验证,authc
		filterChainDefinitionMap.put("/index", "authc");
		// 匿名拦截验证
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/loginUser", "anon");
		// 角色拦截验证
		filterChainDefinitionMap.put("/admin", "roles[admin]");
		// 权限拦截验证
		filterChainDefinitionMap.put("/edit", "perms[edit]");
		filterChainDefinitionMap.put("/druid/**", "anon");
		// 用户拦截验证
		filterChainDefinitionMap.put("/**", "user");
		bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return bean;
	}

	@Bean("securityManager")
	public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm, EhCacheManager ehCacheManager) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(authRealm);
		// 使用缓存
		// manager.setCacheManager(ehCacheManager);
		return manager;
	}

	// @Bean("securityManager")
	// public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm
	// authRealm) {
	// DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
	// manager.setRealm(authRealm);
	// return manager;
	// }

	@Bean("authRealm")
	public AuthRealm authRealm(@Qualifier("credentialMatcher") CredentialMatcher matcher) {
		AuthRealm authRealm = new AuthRealm();
		authRealm.setCacheManager(new MemoryConstrainedCacheManager());
		authRealm.setCredentialsMatcher(matcher);
		return authRealm;
	}

	@Bean("credentialMatcher")
	public CredentialMatcher credentialMatcher(@Qualifier("ehCacheManager") EhCacheManager ehCacheManager) {
		return new CredentialMatcher(ehCacheManager);
	}

	// @Bean("credentialMatcher")
	// public CredentialMatcher credentialMatcher() {
	// return new CredentialMatcher();
	// }

	@Bean
	public EhCacheManager ehCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		EhCacheManager em = new EhCacheManager();
		// 将ehcacheManager转换成shiro包装后的ehcacheManager对象
		em.setCacheManager(cacheManager);
		// em.setCacheManagerConfigFile("classpath:ehcache.xml");
		return em;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setProxyTargetClass(true);
		return creator;
	}
}
