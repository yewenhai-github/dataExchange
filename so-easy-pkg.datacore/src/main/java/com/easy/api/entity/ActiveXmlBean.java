package com.easy.api.entity;

import java.io.Serializable;

public class ActiveXmlBean implements Serializable{
	private static final long serialVersionUID = 1L; 
	
	String xmlData;
	String fileName;
	String sourcePath;
	
	public String getXmlData() {
		return xmlData;
	}

	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	
}
