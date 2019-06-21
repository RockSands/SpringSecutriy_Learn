package com.shiro;

import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

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
@EnableCaching
public class ShiroConfiguration {

	/**
	 * 解决： 无权限页面不跳转 shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized")
	 * 无效 shiro的源代码ShiroFilterFactoryBean.Java定义的filter必须满足filter instanceof
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
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager,Shiro的核心安全接口
		bean.setSecurityManager(manager);
		// 配置登陆接口路径，如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		bean.setLoginUrl("/login");
		// 配置登陆成功接口路径,登录成功后要跳转的链接
		bean.setSuccessUrl("/index");
		// 配置未授权界面,用于不满足权限
		bean.setUnauthorizedUrl("/unauthorized");

		/*
		 * 定义拦截器
		 */
		// // 自定义拦截器限制并发人数
		// LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
		// // 限制同一帐号同时在线的个数
		// filtersMap.put("kickout", kickoutSessionControlFilter());
		// shiroFilterFactoryBean.setFilters(filtersMap);

		/*
		 * 该Map 的Key为URL的正则 Value为对应的拦截
		 * 
		 * anon 表示资源都可以匿名访问 authc 表示需要认证才能进行访问
		 */
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 表单拦截验证,authc
		filterChainDefinitionMap.put("/index", "authc");
		// 匿名拦截验证
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/loginUser", "anon");
		// 角色拦截验证,要求roles必须是admin
		filterChainDefinitionMap.put("/admin", "roles[admin]");
		// 权限拦截验证,要求perms必须是edit
		filterChainDefinitionMap.put("/view", "perms[view]");
		filterChainDefinitionMap.put("/druid/**", "anon");
		// 用户拦截验证
		filterChainDefinitionMap.put("/**", "user");
		// logout是shiro提供的过滤器
		filterChainDefinitionMap.put("/logout", "logout");
		bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return bean;
	}

	/**
	 * 配置核心安全事务管理器
	 * 
	 * @param shiroRealm
	 * @return
	 */
	@Bean("securityManager")
	public SecurityManager securityManager(@Qualifier("authRealm") ShiroRealm authRealm, EhCacheManager ehCacheManager,
			@Qualifier("rememberMeManager") RememberMeManager rememberMeManager) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 设置自定义realm
		manager.setRealm(authRealm);
		// 激活Cookie
		manager.setRememberMeManager(rememberMeManager);

		// 配置ehcache缓存管理器 , 此处增加缓存为核心,表示shiro全面使用缓存
		manager.setCacheManager(ehCacheManager);

		// 配置自定义session管理，使用redis 参考博客：
		// securityManager.setSessionManager(sessionManager());
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
	public ShiroRealm authRealm(@Qualifier("credentialMatcher") CredentialMatcher matcher) {
		// ShiroRealm authRealm = new ShiroRealm();
		// 内存缓存
		// authRealm.setCacheManager(new MemoryConstrainedCacheManager());
		// authRealm.setCredentialsMatcher(matcher);
		ShiroRealm shiroRealm = new ShiroRealm();
		shiroRealm.setCredentialsMatcher(matcher);
		/*
		 * 在SecurityManager 启动缓存时, shiroRealm默认开启缓存
		 * 这里主要是指定缓存空间
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

	@Bean("credentialMatcher")
	public CredentialMatcher credentialMatcher(@Qualifier("ehCacheManager") EhCacheManager ehCacheManager) {
		return new CredentialMatcher(ehCacheManager);
	}

	/**
	 * Shiro的Cookie操作 cookie对象;会话Cookie模板 ,默认为: JSESSIONID 问题:
	 * 与SERVLET容器名冲突,重新定义为sid或rememberMe，自定义
	 * 
	 * @return
	 */
	@Bean("rememberMeCookie")
	public SimpleCookie rememberMeCookie() {
		// 这个参数是cookie的名称
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		// setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数
		// 安全操作包括:
		// setcookie()的第七个参数
		// 设为true后，只能通过http访问，javascript无法访问
		// 防止xss读取cookie
		simpleCookie.setHttpOnly(true);
		simpleCookie.setPath("/");
		// <!-- 记住我cookie生效时间30天 ,单位秒;-->
		simpleCookie.setMaxAge(2592000);
		return simpleCookie;
	}

	/**
	 * cookie管理对象;记住我功能,rememberMe管理器
	 * 
	 * @return
	 */
	@Bean("rememberMeManager")
	public CookieRememberMeManager rememberMeManager(
			@Qualifier("rememberMeCookie") org.apache.shiro.web.servlet.Cookie cookie) {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(cookie);
		// rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
		cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
		return cookieRememberMeManager;
	}

	/**
	 * FormAuthenticationFilter 过滤器 过滤记住我
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

	@Bean
	public EhCacheManager ehCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		EhCacheManager em = new EhCacheManager();
		// 将ehcacheManager转换成shiro包装后的ehcacheManager对象
		em.setCacheManager(cacheManager);
		return em;
	}

	/**
	 * 定时清理缓存 让某个实例的某个方法的返回值注入为Bean的实例 Spring静态注入
	 * 
	 * @return
	 */
	@Bean
	public MethodInvokingFactoryBean getMethodInvokingFactoryBean(
			@Qualifier("securityManager") SecurityManager securityManager) {
		MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
		factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		factoryBean.setArguments(new Object[] { securityManager });
		return factoryBean;
	}

	/**
	 * 开启shiro 注解模式 可以在controller中的方法前加上注解
	 * 如 @RequiresPermissions("userInfo:add")
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
