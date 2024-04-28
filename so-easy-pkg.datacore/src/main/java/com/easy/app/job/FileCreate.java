package com.easy.app.job;

import java.util.HashMap;

import com.easy.exception.LegendException;
import com.easy.thread.JobDetail;
import com.easy.utility.FileUtility;

public class FileCreate extends JobDetail{
	HashMap<String, Object> fileMap = new HashMap<String, Object>();
	public FileCreate(HashMap paramData){
		this.fileMap = paramData;
	}
	
	public void run() throws LegendException{
		String SourcePath = (String)fileMap.get("SourcePath");
		String FileName = (String)fileMap.get("FileName");
		String XmlData = (String)fileMap.get("XmlData");
		
		FileUtility.createFile(SourcePath, FileName, XmlData);//接入信息落地到文件服务器上（当前应用所属服务器）
	}
}
