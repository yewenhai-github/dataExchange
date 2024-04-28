package com.easy.config.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractGenerator {
	protected final Log log = LogFactory.getLog(getClass());
	protected Map<String, Object> dataModel = new HashMap<String, Object>();
	private boolean promptOverwrite = true;
	private int count = 0;
	
	protected void generateFile(String fileName, String templateName) throws Exception {
		log.info("Generating " + fileName + "...");
		File file = new File(fileName);

		String content = TemplateUtils.process(templateName, dataModel);
		FileUtils.writeStringToFile(file, content, "UTF-8");
		log.info("Done");
	}
	
	protected void generateJava(String packageName, String className, String templateName) throws Exception {
		String fullClassName = packageName + "." + className;
		String fileName = "src/main/java" + "/" + fullClassName.replace(".", "/") + ".java";

		this.generateFile(fileName, templateName);
	}
	
	
	public void reset() {
		this.count = 0;
		this.dataModel.clear();
	}
	
	public int getCount() {
		return this.count;
	}

	public Map<String, Object> getDataModel() {
		return dataModel;
	}

	public void setDataModel(Map<String, Object> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isPromptOverwrite() {
		return promptOverwrite;
	}

	public void setPromptOverwrite(boolean promptOverwrite) {
		this.promptOverwrite = promptOverwrite;
	}
}
