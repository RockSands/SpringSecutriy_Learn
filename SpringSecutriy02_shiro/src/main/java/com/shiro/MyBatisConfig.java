package com.shiro;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan("com.mapper")
public class MyBatisConfig {

	@Autowired
	private DataSource druidDataSource;

	/**
	 * 根据数据源创建SqlSessionFactory
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory(@Value("${mybatis.typeAliasesPackage}") String typeAliasesPackage,
			@Value("${mybatis.mapperLocations}") String mapperLocations) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(druidDataSource);// 指定数据源(这个必须有，否则报错)
		// 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
		factoryBean.setTypeAliasesPackage(typeAliasesPackage);// 指定基包
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));//
		return factoryBean.getObject();
	}

}
