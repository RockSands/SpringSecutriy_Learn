package com.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * 
 * Shiro提供了完整的企业级会话管理功能，不依赖于底层容器（如Tomcat），
 * 不管是J2SE还是J2EE环境都可以使用，提供了会话管理，会话事件监听，会话存储/持久化，容器无关的集群，失效/过期支持，
 * 对Web的透明支持，SSO单点登录的支持等特性。即直接使用 Shiro 的会话管理可以直接替换如 Web 容器的会话管理
 * 
 * 会话相关API
 *  Subject subject = SecurityUtils.getSubject();
 *  Session session = subject.getSession();
 *  
 *  Shiro得DefaultSecurityManager都是继承自SessionManager
 *  Shiro提供了三个默认实现：   
 *		DefaultSessionManager：DefaultSecurityManager使用的默认实现，用于JavaSE环境； 
 *		ServletContainerSessionManager：DefaultWebSecurityManager使用的默认实现，用于Web环境，其直接使用Servlet容器的会话； 
 *		DefaultWebSessionManager：用于Web环境的实现，可以替代ServletContainerSessionManager，自己维护着会话，直接废弃了Servlet容器的会话管理。
 * 
 * @author Administrator
 *
 */
public class ShiroSessionListener implements SessionListener {
	/**
	 * 统计在线人数 juc包下线程安全自增
	 */
	private final AtomicInteger sessionCount = new AtomicInteger(0);

	/**
	 * 会话创建时触发
	 * 
	 * @param session
	 */
	@Override
	public void onStart(Session session) {
		// 会话创建，在线人数加一
		sessionCount.incrementAndGet();
	}

	/**
	 * 退出会话时触发
	 * 
	 * @param session
	 */
	@Override
	public void onStop(Session session) {
		// 会话退出,在线人数减一
		sessionCount.decrementAndGet();
	}

	/**
	 * 会话过期时触发
	 * 
	 * @param session
	 */
	@Override
	public void onExpiration(Session session) {
		// 会话过期,在线人数减一
		sessionCount.decrementAndGet();
	}

	/**
	 * 获取在线人数使用
	 * 
	 * @return
	 */
	public AtomicInteger getSessionCount() {
		return sessionCount;
	}
}
