package com.easy.transfer.utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.entity.ServicesBean;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class CustUtility {

	/**
	 * 发送海关的报文做报文头处理
	 * 
	 * @param file
	 * @param bean
	 * @param dataAccess
	 * @param fileData
	 * @param FolderName
	 *            文件所在文件夹的名称
	 * @return
	 * @throws IOException
	 * 
	 */
	public static String xmlDocPro(File file, ServicesBean bean,IDataAccess dataAccess, String fileData) {
		// TODO 处理报文头数据
		
		String FolderName = file.getParentFile().getName();//当前文件所在的目录的名称		
		try {
			String Folder_Source = SysUtility.GetProperty("System.properties","Folder_Source");// 要处理的源文件夹的名称
			if (SysUtility.isEmpty(Folder_Source)
					|| !Folder_Source.contains(FolderName)) {
				return fileData;
			}
			StringBuffer sb = new StringBuffer();
			String fileName = bean.getFileName();// 报文类型_接收方_报文的流水号
			String[] strArr = fileName.substring(0, fileName.indexOf('.'))
					.split("_");// 文件名解析
			String MessageReceiver = SysUtility.GetProperty("System.properties",
					"MessageReceiver").trim();// 接收方				
			String Messageversion = SysUtility.GetProperty("System.properties",
					"Messageversion").trim();// 报文的版本号
			String MessageType = SysUtility.GetProperty("System.properties",
					"MessageType").trim();// 报文的类型
			String MessageSender = SysUtility.GetProperty("System.properties",
					"MessageSender").trim();// 发送方
			String senderDate = "";// 发送方时间
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"YYYY-MM-dd hh:mm:ss");
			senderDate = formatter.format(currentTime);
			
			String IsolationDate=GetDateStr(formatter,currentTime,1);//隔离区时间点
			String SharedNodeDate=GetDateStr(formatter,currentTime,2);//共享节点时间
			String ReceiveDate=GetDateStr(formatter,currentTime,3);//接收时间
			String Keyinfor="22";//主键信息
			
			LinkedHashMap<String, Integer> MyStrMap = new LinkedHashMap<String, Integer>();
			MyStrMap.put(Messageversion, 32);// key 拼接的名称 value值所占的长度
			MyStrMap.put(MessageType, 16);
			MyStrMap.put(MessageSender, 16);
			MyStrMap.put(MessageReceiver, 16);
			MyStrMap.put(fileName, 64);
			MyStrMap.put(senderDate, 19);
			MyStrMap.put(IsolationDate, 19);
			MyStrMap.put(SharedNodeDate, 19);
			MyStrMap.put(ReceiveDate, 19);
			MyStrMap.put(Keyinfor, 64);
			sb = GetHeadString(sb, MyStrMap);
			sb.append(fileData);
			return sb.toString();
		} catch (Exception e) {
			LogUtil.printLog(
					"IOException:System.properties读取出错" + e.getMessage(),
					Level.ERROR);
		}

		return null;
	}
	private static String GetDateStr(SimpleDateFormat formatter, Date currentTime,int num) {
		currentTime=new Date(currentTime.getTime() + num*1000);
		return formatter.format(currentTime);
	}

	/**
	 * 拼接海关报文头部的字符串
	 * 
	 * @param sb
	 * @param myStrMap
	 * @return
	 */
	private static StringBuffer GetHeadString(StringBuffer sb,
			LinkedHashMap<String, Integer> myStrMap) {
		Iterator<Entry<String, Integer>> enty = myStrMap.entrySet().iterator();
		while (enty.hasNext()) {
			Entry<String, Integer> keyValue = enty.next();
			String nameString = keyValue.getKey();
			int lenth = keyValue.getValue();
			while (nameString.length() < lenth) {
				nameString += " ";
			}
			if (nameString.length() > lenth) {
				nameString = nameString.substring(0, lenth);
			}
			sb.append(nameString);// 版本号
		}

		return sb;
	}

	/***
	 * 
	 * @param DataBye 报文字节数据
	 * @param fileData 报文文本数据
	 * @param bean 
	 * @param queue 队列名称
	 * @return
	 */
	public static String ProcessReceCustFile(byte[] DataBye ,String fileData, ServicesBean bean,String  queue) {
		try {
			String ReceQueueName= SysUtility.GetProperty("System.properties", "ReceQueue");
			String HeadLength= SysUtility.GetProperty("System.properties", "HeadLength");
			String  FileName="";
			if(SysUtility.isNotEmpty(ReceQueueName)&&ReceQueueName.contains(queue)){//如果是指定队列则进行字符串处理
    			int headStrLen=SysUtility.isEmpty(HeadLength)?0:Integer.valueOf(HeadLength);
    			fileData=fileData.substring(headStrLen);
    			FileName=new String(DataBye, 80,64, "UTF-8").trim();
    			bean.setFileName(FileName);
    		}
			
		} catch (IOException e) {
			LogUtil.printLog(
					"处理接收海关报文 IOException:System.properties读取出错" + e.getMessage(),
					Level.ERROR);
		}

		return fileData;
	}
	

}
