package com.shiro.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.shiro.ShiroRealm;

public class ShiroUtils {
	/**
	 * 重新赋值权限(在比如:给一个角色临时添加一个权限,需要调用此方法刷新权限,否则还是没有刚赋值的权限)
	 * 
	 * @param username
	 *            用户名
	 */
	public static void cleanAuthorizing(String username) {
		// 添加成功之后 清除缓存
		DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
		ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();
		for (Object key : shiroRealm.getAuthorizationCache().keys()) {
			System.out.println(key + ":" + key.toString());
		}
		/*
		 * 自定义的shiroRealm  覆盖getAuthorizationCacheKey() 即可
		 */
		shiroRealm.getAuthorizationCache().remove(username);
	}
}
