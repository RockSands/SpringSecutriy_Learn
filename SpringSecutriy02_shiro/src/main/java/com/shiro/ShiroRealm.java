package com.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.model.Permission;
import com.model.Role;
import com.model.User;
import com.service.UserService;

/**
 * 
 * 权限校验类
 * 
 * @author Administrator
 *
 */
public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	/**
	 * 授权用户权限 授权的方法是在碰到<shiro:hasPermission name=''></shiro:hasPermission>标签的时候调用的
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
		// 从session获取对象
		System.out.println("=doGetAuthorizationInfo=>");
		User user = (User) principals.fromRealm(this.getClass().getName()).iterator().next();
		List<String> permissionList = new ArrayList<>();
		List<String> roleNameList = new ArrayList<>();
		Set<Role> roleSet = user.getRoles();
		if (CollectionUtils.isNotEmpty(roleSet)) {
			for (Role role : roleSet) {
				roleNameList.add(role.getRname());
				Set<Permission> permissionSet = role.getPermissions();
				if (CollectionUtils.isNotEmpty(permissionSet)) {
					for (Permission permission : permissionSet) {
						permissionList.add(permission.getName());
					}
				}
			}
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(permissionList);
		info.addRoles(roleNameList);
		return info;
	}

	// 密码验证
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
		User user = userService.findByUsername(username);
		return new SimpleAuthenticationInfo(user, user.getPassword(), this.getClass().getName());
	}
}
