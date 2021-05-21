package com.eumsystems.channeleumlight.config;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import lombok.extern.log4j.Log4j;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages= {"com.eumsystems.channeleumlight"})
@MapperScan("com.eumsystems.channeleumlight.mapper")
@Log4j
public class AppConfig {
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource());
		factory.setMapperLocations(new PathMatchingResourcePatternResolver()
			   .getResources("classpath:com/eumsystems/channeleumlight/mapper/*.xml"));
		factory.setConfiguration(mybatisSessionConfiguration());
		return factory.getObject();
	}
	
	@Bean
	public org.apache.ibatis.session.Configuration mybatisSessionConfiguration() {
		org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
		config.setMapUnderscoreToCamelCase(true);
		config.setJdbcTypeForNull(JdbcType.NULL);
		config.getTypeAliasRegistry().registerAliases("com.eumsystems.channeleumlight.model");
		return config;
    }
	
	@Bean
	public DataSource dataSource() throws Exception {
		String dsName = "jdbc/BFSDB";
		DataSource ds = null;
		Context ctx = new InitialContext();
		Context envContext  = (Context)ctx.lookup("java:/comp/env");
		ds = (DataSource)envContext.lookup(dsName);
		return ds;
	}
	
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver view = new InternalResourceViewResolver();
		view.setViewClass(JstlView.class);
		view.setPrefix("/WEB-INF/views/");
		view.setSuffix(".jsp");
		return view;
	}
}
