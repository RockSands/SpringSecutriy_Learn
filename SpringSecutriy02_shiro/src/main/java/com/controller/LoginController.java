package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.User;
import com.shiro.RetryLimitHashedCredentialsMatcher;

@Controller
public class LoginController {
	@Autowired
	private RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher;

	/**
	 * 访问项目根路径
	 * 
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(Model model) {
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		if (user == null) {
			return "redirect:/login";
		} else {
			return "redirect:/index";
		}
	}

	/**
	 * 跳转到login页面
	 * 
	 * @return
	 */
	// @RequestMapping(value = "/login", method = RequestMethod.POST)
	// public String login(Model model) {
	// Subject subject = SecurityUtils.getSubject();
	// User user = (User) subject.getPrincipal();
	// if (user == null) {
	// return "login";// 直接返回对应的模板
	// } else {
	// return "redirect:index";// 使用模板,调整需要redirect
	// }
	// }

	@RequestMapping(value = "/login", method = { RequestMethod.POST,RequestMethod.GET})
	public String loginUser(HttpServletRequest request, String username, String password, boolean rememberMe,
			String captcha, Model model) {

		// 校验验证码
		// session中的验证码
		// String sessionCaptcha = (String) SecurityUtils.getSubject().getSession()
		// .getAttribute(CaptchaController.KEY_CAPTCHA);
		// if (null == captcha || !captcha.equalsIgnoreCase(sessionCaptcha)) {
		// model.addAttribute("msg", "验证码错误！");
		// return "login";
		// }

		// 对密码进行加密
		// password=new SimpleHash("md5", password,
		// ByteSource.Util.bytes(username.toLowerCase() + "shiro"),2).toHex();
		// 如果有点击 记住我
		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password, rememberMe);
		// UsernamePasswordToken usernamePasswordToken = new
		// UsernamePasswordToken(username,password);
		Subject subject = SecurityUtils.getSubject();
		try {
			// 登录操作
			subject.login(usernamePasswordToken);
			return "redirect:index";
		} catch (Exception e) {
			// 登录失败从request中获取shiro处理的异常信息 shiroLoginFailure:就是shiro异常类的全类名
			String exception = (String) request.getAttribute("shiroLoginFailure");

			if (e instanceof UnknownAccountException) {
				model.addAttribute("msg", "用户名或密码错误！");
			}

			if (e instanceof IncorrectCredentialsException) {
				model.addAttribute("msg", "用户名或密码错误！");
			}

			if (e instanceof LockedAccountException) {
				model.addAttribute("msg", "账号已被锁定,请联系管理员！");
			}

			// 返回登录页面
			return "login";
		}
	}

	@RequestMapping("/index")
	public String index(HttpSession session, Model model) {
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		if (user == null) {
			return "login";
		} else {
			model.addAttribute("user", user);
			return "index";
		}
	}

	/**
	 * 登出 这个方法没用到,用的是shiro默认的logout
	 * 
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session, Model model) {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		model.addAttribute("msg", "安全退出！");
		return "login";
	}

	/**
	 * 跳转到无权限页面
	 * 
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/unauthorized")
	public String unauthorized(HttpSession session, Model model) {
		return "unauthorized";
	}

	/**
	 * 解除admin 用户的限制登录 写死的 方便测试
	 * 
	 * @return
	 */
	@RequestMapping("/unlockAccount")
	public String unlockAccount(Model model) {
		model.addAttribute("msg", "用户解锁成功");
		retryLimitHashedCredentialsMatcher.unlockAccount("admin");
		return "login";
	}

	@RequestMapping("/admin")
	@ResponseBody
	public String admin() {
		return "admin success";
	}

	@RequestMapping("/test0")
	@RequiresRoles("admin")
	@ResponseBody
	public String test0() {
		return "test0 success";
	}

	@RequestMapping("/test1")
	@RequiresAuthentication
	@ResponseBody
	public String test1() {
		return "test1 success";
	}

	@RequestMapping("/test2")
	@RequiresUser
	@ResponseBody
	public String test2() {
		return "test2 success";
	}

	@RequestMapping("/test3")
	@RequiresGuest
	@ResponseBody
	public String test3() {
		return "test3 success";
	}

	@RequestMapping("/test4")
	@RequiresPermissions("edit")
	@ResponseBody
	public String test4() {
		return "test4 success";
	}
}
