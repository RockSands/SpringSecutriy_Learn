package com.shiro;

import java.util.LinkedHashMap;
import java.util.Properties;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.shiro.cache.ShiroCacheConfig;
import com.shiro.cookie.ShiroCookieConfig;
import com.shiro.session.KickoutSessionControlFilter;
import com.shiro.session.ShiroSessionConfig;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

/**
 * Subject 其实代表的就是当前正在执行操作的用户 SecurityManager 是Shiro的核心，用于管理所有的Subject
 * ，它主要用于协调Shiro内部各种安全组件. 一般不用额外配置 Realm
 * 用于连接Shiro和客户系统的用户数据的桥梁。一旦Shiro真正需要判别安全相关的数据,则会调用Realm数据. 用户需要实现接口.
 * Authentication 用于用户登陆校验用户名/密码 Authorization 用户获取用户的权限数据,包括角色、能力
 * SessionManager Session管理 SessionDAO 当使用数据库管理Session时，替代SessionManager
 * CacheManager shiro的缓存实现,可以提供ehcache和Redis Cryptography 加密
 * 
 * @author Administrator
 *
 */
@Configuration
@AutoConfigureAfter(value = { ShiroCacheConfig.class, ShiroSessionConfig.class, ShiroCookieConfig.class })
public class ShiroConfiguration {

	/**
	 * 解决： 无权限页面不跳转 shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized") 无效
	 * shiro的源代码ShiroFilterFactoryBean.Java定义的filter必须满足filter instanceof
	 * AuthorizationFilter，
	 * 只有perms，roles，ssl，rest，port才是属于AuthorizationFilter，而anon，authcBasic，auchc，user是AuthenticationFilter，
	 * 所以unauthorizedUrl设置后页面不跳转 Shiro注解模式下，登录失败与没有权限都是通过抛出异常。
	 * 并且默认并没有去处理或者捕获这些异常。在SpringMVC下需要配置捕获相应异常来通知用户信息
	 * 
	 * @return
	 */
	@Bean
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
		SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
		Properties properties = new Properties();
		properties.setProperty("org.apache.shiro.authz.AuthorizationException", "unauthorized");
		resolver.setExceptionMappings(properties);
		return resolver;
	}

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。
	 * 注意：初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
	 * Web应用中,Shiro可控制的Web请求必须经过Shiro主过滤器的拦截
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager,
			KickoutSessionControlFilter kickoutSessionControlFilter) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager,Shiro的核心安全接口
		shiroFilterFactoryBean.setSecurityManager(manager);
		// 配置登陆接口路径，如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/");
		// 配置登陆成功接口路径,登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index");
		// 配置未授权界面,用于不满足权限
		shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

		/*
		 * 定义拦截器
		 */
		// 自定义拦截器限制并发人数
		LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
		// 限制同一帐号同时在线的个数
		filtersMap.put("kickout", kickoutSessionControlFilter);
		shiroFilterFactoryBean.setFilters(filtersMap);

		/*
		 * 该Map 的Key为URL的正则 Value为对应的拦截
		 * 
		 * anon 表示资源都可以匿名访问 authc 表示需要认证才能进行访问
		 */
		// 配置访问权限 必须是LinkedHashMap，因为它必须保证有序
		// 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 --> : 这是一个坑，一不小心代码就不好使了
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 增加验证码
		filterChainDefinitionMap.put("/Captcha.jpg", "anon");
		// 配置不登录可以访问的资源， 表示资源都可以匿名访问
		filterChainDefinitionMap.put("/login", "kickout,anon");
		// 表单拦截验证,authc
		filterChainDefinitionMap.put("/index", "authc");
		// 匿名拦截验证
		filterChainDefinitionMap.put("/loginUser", "anon");
		// 角色拦截验证,要求roles必须是admin
		filterChainDefinitionMap.put("/admin", "roles[admin]");
		// 权限拦截验证,要求perms必须是edit
		filterChainDefinitionMap.put("/view", "perms[view]");
		filterChainDefinitionMap.put("/druid/**", "anon");
		// logout是shiro提供的过滤器
		filterChainDefinitionMap.put("/logout", "logout");
		// 用户拦截验证
		filterChainDefinitionMap.put("/**", "kickout,user");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}

	/**
	 * 配置核心安全事务管理器
	 * 
	 * @param shiroRealm
	 * @return
	 */
	@Bean("securityManager")
	public SecurityManager securityManager(ShiroRealm authRealm, EhCacheManager shiroEhCacheManager,
			RememberMeManager rememberMeManager, SessionManager sessionManager) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 设置自定义realm
		manager.setRealm(authRealm);
		// 激活Cookie
		manager.setRememberMeManager(rememberMeManager);

		// 配置ehcache缓存管理器 , 此处增加缓存为核心,表示shiro全面使用缓存
		manager.setCacheManager(shiroEhCacheManager);

		// 配置自定义session管理
		manager.setSessionManager(sessionManager);
		return manager;
	}

	/**
	 * 配置Shiro生命周期处理器
	 * 
	 * @return
	 */
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean("authRealm")
	public ShiroRealm authRealm(@Qualifier("credentialsMatcher") RetryLimitHashedCredentialsMatcher matcher) {
		// ShiroRealm authRealm = new ShiroRealm();
		// 内存缓存
		// authRealm.setCacheManager(new MemoryConstrainedCacheManager());
		// authRealm.setCredentialsMatcher(matcher);
		ShiroRealm shiroRealm = new ShiroRealm();
		shiroRealm.setCredentialsMatcher(matcher);
		/*
		 * 在SecurityManager 启动缓存时, shiroRealm默认开启缓存 这里主要是指定缓存空间
		 */
		shiroRealm.setCachingEnabled(true);
		// 启用身份验证缓存，即缓存AuthenticationInfo信息
		shiroRealm.setAuthenticationCachingEnabled(true);
		// 缓存AuthenticationInfo信息的缓存名称 在ehcache-shiro.xml中有对应缓存的配置
		shiroRealm.setAuthenticationCacheName("authenticationCache");
		// 启用授权缓存，即缓存AuthorizationInfo信息
		shiroRealm.setAuthorizationCachingEnabled(true);
		// 缓存AuthorizationInfo信息的缓存名称 在ehcache-shiro.xml中有对应缓存的配置
		shiroRealm.setAuthorizationCacheName("authorizationCache");
		return shiroRealm;
	}

	/**
	 * 普通
	 * 
	 * @param shiroEhCacheManager
	 * @return
	 */
	// @Bean("credentialMatcher")
	// public CredentialMatcher credentialMatcher(EhCacheManager
	// shiroEhCacheManager) {
	// return new CredentialMatcher(shiroEhCacheManager);
	// }

	/**
	 * 功能性
	 * 
	 * @param shiroEhCacheManager
	 * @return
	 */
	@Bean("credentialsMatcher")
	public RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher(EhCacheManager shiroEhCacheManager) {
		RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(
				shiroEhCacheManager);
		// 如果密码加密,可以打开下面配置
		// 加密算法的名称
		// retryLimitHashedCredentialsMatcher.setHashAlgorithmName("MD5");
		// 配置加密的次数
		// retryLimitHashedCredentialsMatcher.setHashIterations(1024);
		// 是否存储为16进制
		// retryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
		return retryLimitHashedCredentialsMatcher;
	}

	/**
	 * 开启shiro 注解模式 可以在controller中的方法前加上注解 如 @RequiresPermissions("userInfo:add")
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	/**
	 * 权限验证,开发cookie FormAuthenticationFilter 过滤器 过滤记住我
	 * 
	 * formAuthentication 权限过滤器, 所有的authc 即权限过滤请求,都会进行formAuthenticationFilter过滤
	 * 
	 * 
	 * @return
	 */
	@Bean
	public FormAuthenticationFilter formAuthenticationFilter() {
		FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
		// 对应前端的checkbox的name = rememberMe
		formAuthenticationFilter.setRememberMeParam("rememberMe");
		return formAuthenticationFilter;
	}

	/**
	 * thymeleaf生效
	 * 
	 * @return
	 */
	@Bean(name = "shiroDialect")
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setProxyTargetClass(true);
		return creator;
	}

}
