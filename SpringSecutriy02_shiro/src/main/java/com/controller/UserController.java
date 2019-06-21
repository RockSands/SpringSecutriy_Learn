package com.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapper.UserMapper;
import com.mapper.batch.BatchVo;
import com.mapper.batch.SqlType;
import com.model.Role;
import com.model.User;
import com.service.BatchExcuteService;
import com.service.ShiroCacheService;
import com.shiro.utils.ShiroUtils;

@Controller
@RequestMapping("userInfo")
public class UserController {
	@Autowired
	private BatchExcuteService batchExcuteService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ShiroCacheService shiroCacheService;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@ResponseBody
	public String add() {
		User user = new User();
		user.setUid(255);
		user.setName("陈魁武");
		user.setId_card_num("177777777777777777");
		user.setUsername("chenkuiwu");
		user.setPassword("123456");
		Role role = new Role();
		role.setId(3);
		user.getRoles().add(role);
		List<BatchVo> vos = new ArrayList<BatchVo>();
		vos.add(new BatchVo(SqlType.INSERT, "com.mapper.UserMapper.insert", user));
		vos.add(new BatchVo(SqlType.INSERT, "com.mapper.UserMapper.insertRoles", user));
		batchExcuteService.excuteBatch(vos);
		return "创建用户成功";
	}

	/**
	 * 删除固定写死的用户
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.GET)
	@ResponseBody
	public String del(Model model) {
		User user = userMapper.findByUserName("chenkuiwu");
		if (user == null) {
			return "删除用户名为chenkuiwu用户成功! 他不存在!";
		}
		List<BatchVo> vos = new ArrayList<BatchVo>();
		vos.add(new BatchVo(SqlType.DELETE, "com.mapper.UserMapper.del", user.getUid()));
		vos.add(new BatchVo(SqlType.DELETE, "com.mapper.UserMapper.delUserRole", user.getUid()));
		batchExcuteService.excuteBatch(vos);
		return "删除用户名为chenkuiwu用户成功!";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@ResponseBody
	public String view(Model model) {
		return "这是用户列表页";
	}

	/**
	 * 删除admin用户 userInfo:del 权限
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/cleanUserCache", method = RequestMethod.GET)
	@ResponseBody
	public String cleanUserCache(Model model) {
		 ShiroUtils.cleanAuthorizing("chenkuiwu");
		shiroCacheService.removeUserAuthorization("chenkuiwu");
		return "清理用户[chenkuiwu]权限成功";

	}
}
