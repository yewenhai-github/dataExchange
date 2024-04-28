package com;

import com.easy.utility.SysUtility;
import org.apache.log4j.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

import com.easy.utility.LogUtil;

@SpringBootApplication
@ServletComponentScan
@Configuration
public class AppMainRun extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(AppMainRun.class, args);
		LogUtil.printLog("exchange-console staring... Plese Open your web browser and navigate to http://\"+ SysUtility.getHostIp() +\":\" + 8080 + \"/login.html\"", Level.WARN);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	private static Class<AppMainRun> applicationClass = AppMainRun.class;
}
