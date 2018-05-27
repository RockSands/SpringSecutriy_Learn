package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by jimin on 2017/8/24.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserService myUserService;

    @Override
    /*
     * 对权限控制进行配置
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	// 内存用户验证
//        auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
//        auth.inMemoryAuthentication().withUser("zhangsan").password("zhangsan").roles("ADMIN");
//        auth.inMemoryAuthentication().withUser("demo").password("demo").roles("USER");
//
        auth.userDetailsService(myUserService).passwordEncoder(new MyPasswordEncoder());

        auth.jdbcAuthentication().usersByUsernameQuery("").authoritiesByUsernameQuery("").passwordEncoder(new MyPasswordEncoder());
    }

    @Override
    // 定义那些请求被拦截
    /*
     * 对于请求进行配置
     */
    protected void configure(HttpSecurity http) throws Exception {
        // 根目录访问放行
        // 其他的拦截
	// 注销、表单登录放行
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout().permitAll()
                .and()
                .formLogin();
        // csrf请求关闭
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
	// 对于页面的js css images不进行权限拦截
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }
}
