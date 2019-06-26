package com.shiro.filter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;

import com.mapper.UserMapper;
import com.model.Permission;
import com.model.Role;
import com.model.User;

/**
 * 根据Url的权限,判断
 * 
 * @author Administrator
 *
 */
public class URLPathMatchingFilter extends PathMatchingFilter {

	private String outUrl;

	private UserMapper userMapper;

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		// 请求的url
		String requestURL = getPathWithinApplication(request);
		System.out.println("请求的url :" + requestURL);
		List<String> comUrls = Arrays.asList("/login","/logout");
		for(String comUrl : comUrls) {
			if(super.pathsMatch(comUrl, requestURL)) {
				return true;
			}
		}
		Subject subject = SecurityUtils.getSubject();
		if (subject == null || !subject.isAuthenticated()) {
			// 如果没有登录, 直接返回true 进入登录流程
			return true;
		}
		
		// 这里获取的User是实体 因为我在 自定义ShiroRealm中的doGetAuthenticationInfo方法中
		// new SimpleAuthenticationInfo(user, password, getName()); 传的是 User实体
		// 所以这里拿到的也是实体,如果传的是userName 这里拿到的就是userName
		String username = ((User) subject.getPrincipal()).getUsername();
		User user = userMapper.findByUserName(username);
		for (Role role : user.getRoles()) {
			for (Permission permission : role.getPermissions()) {
				if (super.pathsMatch(permission.getUrl(), requestURL)) {
					return true;
				}
			}
		}
		UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径" + requestURL + "的权限");
		subject.getSession().setAttribute("ex", ex);
		WebUtils.issueRedirect(request, response, "/login?URL_NOT_MATCH");
		return false;
	}

	public String getOutUrl() {
		return outUrl;
	}

	public void setOutUrl(String outUrl) {
		this.outUrl = outUrl;
	}

	public UserMapper getUserMapper() {
		return userMapper;
	}

	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
}
