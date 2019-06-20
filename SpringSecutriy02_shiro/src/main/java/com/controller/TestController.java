package com.controller;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.User;

@Controller
public class TestController {

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
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		if (user == null) {
			return "login";
		} else {
			return "redirect:index";
		}
	}

	@RequestMapping("/loginUser")
	public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
			boolean rememberMe, Model model, HttpSession session) {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			session.setAttribute("user", user);
			model.addAttribute("user", user);
			return "index";
		} catch (Exception e) {
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

	@RequestMapping("/admin")
	@ResponseBody
	public String admin() {
		return "admin success";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public String edit() {
		return "edit success";
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
