package com.easy;

import org.apache.log4j.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.easy.utility.LogUtil;

@SpringBootApplication
@ServletComponentScan
public class AppMainRun  extends SpringBootServletInitializer  {  //extends SpringBootServletInitializer    发布式部署使用
	
	public static void main(String[] args) {
		SpringApplication.run(AppMainRun.class, args);
		LogUtil.printLog("启动成功。", Level.WARN);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
	
	private static Class<AppMainRun> applicationClass = AppMainRun.class;
}
