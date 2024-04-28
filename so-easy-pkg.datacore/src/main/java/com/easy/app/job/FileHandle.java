package com.easy.app.job;

import java.io.File;
import java.util.HashMap;

import com.easy.access.IDataAccess;
import com.easy.thread.JobContainer;
import com.easy.utility.SysUtility;

public class FileHandle {
	int totalCount = 2;
	int UseCount = 0;
	File[] ChildFiles = new File[totalCount];
	
	public int getUseCount() {
		return UseCount;
	}
	public void setUseCount(int useCount) {
		UseCount = useCount;
	}
	public File[] getChildFiles() {
		return ChildFiles;
	}
	public void setChildFiles(File[] childFiles) {
		ChildFiles = childFiles;
	}
	
	public void addToFiles(File file,HashMap<String,String> params,IDataAccess DataAccess){
		if(UseCount >= totalCount){
			File[] tempFiles = new File[totalCount];
			for (int i = 0; i < tempFiles.length; i++) {
				tempFiles[i] = ChildFiles[i];
			}
			HashMap<String, Object> fileMap = new HashMap<String, Object>();
			fileMap.put("Files", tempFiles);
			fileMap.put("Params", params);
			fileMap.put("DataAccess", DataAccess);
			JobContainer.getInstance().addToList(new FileJob(fileMap));
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				LogUtil.printLog("线程休眠出错："+e.getMessage(), Level.ERROR);
//			}
			//重新初始化
			ChildFiles = new File[totalCount];
			UseCount = 0;
			ChildFiles[UseCount] = file;
			UseCount ++;
		}else{
			ChildFiles[UseCount] = file;
			UseCount ++;
		}
	}
	
	public void ExecLastJob(HashMap<String,String> params,IDataAccess DataAccess){
		if(SysUtility.isEmpty(ChildFiles) || ChildFiles.length < 0 || SysUtility.isEmpty(ChildFiles[0])){
			return;
		}
		
		HashMap<String, Object> fileMap = new HashMap<String, Object>();
		fileMap.put("Files", ChildFiles);
		fileMap.put("Params", params);
		fileMap.put("DataAccess", DataAccess);
		JobContainer.getInstance().addToList(new FileJob(fileMap));
	}
}
