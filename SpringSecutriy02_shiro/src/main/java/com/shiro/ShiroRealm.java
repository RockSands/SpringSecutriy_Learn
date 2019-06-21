package com.shiro;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.mapper.PermissionMapper;
import com.mapper.RoleMapper;
import com.mapper.UserMapper;
import com.model.Permission;
import com.model.Role;
import com.model.User;

/**
 * 
 * 权限校验类
 * 
 * 在开启缓存后,doGetAuthorizationInfo  会进入缓存,  在之后的情况下回走缓存
 * 
 * @author Administrator
 *
 */
public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 授权用户权限
	 * 授权的方法是在碰到<shiro:hasPermission name=''></shiro:hasPermission>标签的时候调用的
	 * 它会去检测shiro框架中的权限(这里的permissions)是否包含有该标签的name值,如果有,里面的内容显示
	 * 如果没有,里面的内容不予显示(这就完成了对于权限的认证.)
	 *
	 * shiro的权限授权是通过继承AuthorizingRealm抽象类，重载doGetAuthorizationInfo();
	 * 当访问到页面的时候，链接配置了相应的权限或者shiro标签才会执行此方法否则不会执行
	 * 所以如果只是简单的身份认证没有权限的控制的话，那么这个方法可以不进行实现，直接返回null即可。
	 *
	 * 在这个方法中主要是使用类：SimpleAuthorizationInfo 进行角色的添加和权限的添加。
	 * authorizationInfo.addRole(role.getRole());
	 * authorizationInfo.addStringPermission(p.getPermission());
	 *
	 * 当然也可以添加set集合：roles是从数据库查询的当前用户的角色，stringPermissions是从数据库查询的当前用户对应的权限
	 * authorizationInfo.setRoles(roles);
	 * authorizationInfo.setStringPermissions(stringPermissions);
	 *
	 * 就是说如果在shiro配置文件中添加了filterChainDefinitionMap.put("/add", "perms[权限添加]");
	 * 就说明访问/add这个链接必须要有“权限添加”这个权限才可以访问
	 *
	 * 如果在shiro配置文件中添加了filterChainDefinitionMap.put("/add",
	 * "roles[100002]，perms[权限添加]"); 就说明访问/add这个链接必须要有 "权限添加" 这个权限和具有 "100002"
	 * 这个角色才可以访问
	 * 
	 * @param principalCollection
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("====我未走缓存=====");
		// 从session获取对象
		// 获取用户
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		System.out.println("=doGetAuthorizationInfo=>" + user);
		// 获取用户角色
		Set<Role> roles = this.roleMapper.findRolesByUserId(user.getUid());
		// 添加角色
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		for (Role role : roles) {
			authorizationInfo.addRole(role.getRole());
		}

		// 获取用户权限
		Set<Permission> permissions = this.permissionMapper.findPermissionsByRoleId(roles);
		// 添加权限
		for (Permission permission : permissions) {
			authorizationInfo.addStringPermission(permission.getPermission());
		}

		return authorizationInfo;
	}

	// 获取账号密码
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获取用户名密码 第一种方式
		// String username = (String) token.getPrincipal();
		// String password = new String((char[]) token.getCredentials());

		System.out.println("=doGetAuthenticationInfo=>");
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String username = usernamePasswordToken.getUsername();

		if (StringUtils.isEmpty(username)) {
			return new SimpleAuthenticationInfo();
		}
		User user = userMapper.findByUserName(username);
		return new SimpleAuthenticationInfo(user, user.getPassword(), this.getClass().getName());
	}
	
	/* 重载缓存的Key
	 * @see org.apache.shiro.realm.AuthorizingRealm#getAuthorizationCacheKey(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
		User user = (User)principals.getPrimaryPrincipal();
		return user.getUsername();
    }
}
