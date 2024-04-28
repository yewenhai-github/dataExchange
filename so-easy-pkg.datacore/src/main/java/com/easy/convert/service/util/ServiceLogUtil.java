package com.easy.convert.service.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;

import com.easy.app.constants.ExsConstants;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


public class ServiceLogUtil {


	public static boolean addLogSuccessFile(String fileFlag, String data) {
		return addLogFile(ExsConstants.appLogPath, SysUtility.getSysDateWithoutTime()+"-"+SysUtility.getHourOfDay()+"-success-"+fileFlag+".log", data);
	}
	
	public static boolean addLogFailFile(String fileFlag, String data) {
		return addLogFile(ExsConstants.appLogPath, SysUtility.getSysDateWithoutTime()+"-"+SysUtility.getHourOfDay()+"-fail-"+fileFlag+".log", data);
	}
	
	public static boolean addLogFile(String tempPath,String fileName,String data) {
		if(SysUtility.isEmpty(tempPath)){
			return false;
		}
		File file = new File(tempPath);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+tempPath, Level.INFO);
		   }
		}
		String path = tempPath + File.separator + fileName;

		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		byte tempByte[] = null;
		try {
			fo = new FileOutputStream(path, true);
			bo = new BufferedOutputStream(fo);
			tempByte = data.getBytes("UTF-8");
			bo.write(tempByte, 0, tempByte.length);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			try {
				bo.flush();
				bo.close();
				fo.close();
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		return false;
	}
}
