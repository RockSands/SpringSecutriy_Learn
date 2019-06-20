package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 参考:https://blog.csdn.net/qq_34021712/article/details/80294417
 * @author Administrator
 *
 */
@EnableAutoConfiguration
@ComponentScan
@MapperScan(basePackages = {"com.mapper"})
public class Demo2Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo2Application.class, args);
	}
}
