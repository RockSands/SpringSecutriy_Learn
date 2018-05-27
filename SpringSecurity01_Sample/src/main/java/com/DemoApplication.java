package com;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
<mirrors>
	<mirror>
		<id>alimaven</id>
		<name>aliyun maven</name>
		<url>http://maven.aliyun.com/nexus/content/groups/public/</url>
		<mirrorOf>central</mirrorOf>
	</mirror>
</mirrors>
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
// 只有开启下面的注解,才能使用@PreAuthorize生效
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "hello spring boot";
	}

	@RequestMapping("/hello")
	public String hello() {
		return "hello world";
	}

	/*
	 * 要求请求角色必须是ADMIN,否则拒绝
	 * ROLE_  表示告知Security是角色校验
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CKW')")
	@RequestMapping("/roleAuth")
	public String role() {
		return "admin auth";
	}

/*
 * 以下如果校验不成功,则直接抛出角色异常
 * @PreAuthorize 调用前
 * @PostAuthorize 调用后
 * @PreFilter  校验前,处理请求集合值
 * @PostFilter 校验后,处理返回值,
 * # id为请求ID必须小于10
 * returnObject 表示返回值
 */
	@PreAuthorize("#id<10 and principal.username.equals(#username) and #user.username.equals('abc')")
	@PostAuthorize("returnObject%2==0")
	@RequestMapping("/test")
	public Integer test(Integer id, String username, User user) {
		// ...
		return id;
	}

	@PreFilter("filterObject%2==0")
	@PostFilter("filterObject%4==0")
	@RequestMapping("/test2")
	public List<Integer> test2(List<Integer> idList) {
		// ...
		return idList;
	}

}
