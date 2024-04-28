package com;

import com.easy.config.tools.TemplateGenerator;

public class AppMappingGenerator {
	public static void main(String[] args) {
		try {
			String packageName = "com.easy.config";
			String className = "MappingController";
			new TemplateGenerator().generateMapping("config","templates",packageName,className);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
