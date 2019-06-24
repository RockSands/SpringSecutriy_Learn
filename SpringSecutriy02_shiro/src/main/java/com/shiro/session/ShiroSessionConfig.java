package com.shiro.session;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shiro提供了完整的企业级会话管理功能，不依赖于底层容器（如Tomcat），
 * 不管是J2SE还是J2EE环境都可以使用，提供了会话管理，会话事件监听，会话存储/持久化，容器无关的集群，失效/过期支持，
 * 对Web的透明支持，SSO单点登录的支持等特性。即直接使用 Shiro 的会话管理可以直接替换如 Web 容器的会话管理
 * 
 * 会话相关API Subject subject = SecurityUtils.getSubject(); Session session =
 * subject.getSession();
 * 
 * Shiro得DefaultSecurityManager都是继承自SessionManager Shiro提供了三个默认实现：
 * DefaultSessionManager：DefaultSecurityManager使用的默认实现，用于JavaSE环境；
 * ServletContainerSessionManager：DefaultWebSecurityManager使用的默认实现，用于Web环境，其直接使用Servlet容器的会话；
 * DefaultWebSessionManager：用于Web环境的实现，可以替代ServletContainerSessionManager，自己维护着会话，直接废弃了Servlet容器的会话管理。
 * 
 * @author Administrator
 *
 */
@Configuration
public class ShiroSessionConfig {

	@Autowired
	@Qualifier("shiroEhCacheManager")
	private EhCacheManager shiroEhCacheManager;
	
	@Autowired
	@Qualifier("sessionListener")
	private ShiroSessionListener shiroSessionListener;

	/**
	 * 配置会话ID生成器
	 * 
	 * @return
	 */
	@Bean
	public SessionIdGenerator sessionIdGenerator() {
		return new JavaUuidSessionIdGenerator();
	}

	/**
	 * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件 MemorySessionDAO
	 * 直接在内存中进行会话维护 EnterpriseCacheSessionDAO
	 * 提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
	 * 
	 * @return
	 */
	@Bean
	public SessionDAO sessionDAO() {
		EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
		// 使用ehCacheManager
		enterpriseCacheSessionDAO.setCacheManager(shiroEhCacheManager);
		// 设置session缓存的名字 默认为 shiro-activeSessionCache
		enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
		// sessionId生成器
		enterpriseCacheSessionDAO.setSessionIdGenerator(sessionIdGenerator());
		return enterpriseCacheSessionDAO;
	}
	
	/**
	 * 配置保存sessionId的cookie 
	 * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie session管理 也需要自己的cookie
	 * @return
	 */
	@Bean("sessionIdCookie")
	public SimpleCookie sessionIdCookie(){
	    //这个参数是cookie的名称
	    SimpleCookie simpleCookie = new SimpleCookie("sid");
	    //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

	    //setcookie()的第七个参数
	    //设为true后，只能通过http访问，javascript无法访问
	    //防止xss读取cookie
	    simpleCookie.setHttpOnly(true);
	    simpleCookie.setPath("/");
	    //maxAge=-1表示浏览器关闭时失效此Cookie
	    simpleCookie.setMaxAge(-1);
	    return simpleCookie;
	}
	
	/**
	 * 配置会话管理器，设定会话超时及保存
	 * @return
	 */
	@Bean("sessionManager")
	public SessionManager sessionManager() {

	    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
	    Collection<SessionListener> listeners = new ArrayList<SessionListener>();
	    //配置监听
	    listeners.add(shiroSessionListener);
	    sessionManager.setSessionListeners(listeners);
	    sessionManager.setSessionIdCookie(sessionIdCookie());
	    sessionManager.setSessionDAO(sessionDAO());
	    sessionManager.setCacheManager(shiroEhCacheManager);

	    return sessionManager;

	}
}