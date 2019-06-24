package com.shiro.cookie;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroCookieConfig {

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
}
