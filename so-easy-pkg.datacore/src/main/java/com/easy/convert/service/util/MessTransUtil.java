package com.easy.convert.service.util;

import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.convert.mq.ActiveMqTo;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


public class MessTransUtil {
	
	public static void ZipMQToXml(IDataAccess DataAccess)throws Exception{
		String send = SysUtility.GetSetting("System", "SEND");
		if(SysUtility.isNotEmpty(send)) {
			String[] SendSplit = send.split(",");
			if(SendSplit.length < 4) {
				LogUtil.printLog("发送MQ 系统配置有误  请维护!", Level.ERROR);
				return;
			}
			String SENDTYPE = SendSplit[0];//发送类型
			String SENDADRESS = SendSplit[1];//发送地址
			String SENDUSERNAME = SendSplit[2];//发送用户名
			String SENDUSERPWD = SendSplit[3];//发送密码
			String QUEUENAME = SendSplit[4];//队列
			String[] ADRESSSplit = SENDADRESS.split(":");
			int port =21; 
			
			if("1".equals(SENDTYPE)) {//FTP
				
			}else if("2".equals(SENDTYPE)) {//WebService
				
			}else if("3".equals(SENDTYPE)) {//MS MQ
				
			}else if("4".equals(SENDTYPE)) {//ActiveMq
				ActiveMqTo.LocalToActiveMQ(DataAccess,SENDADRESS, SENDUSERNAME, SENDUSERPWD, QUEUENAME);
			}else if("5".equals(SENDTYPE)) {//IBMMQ
				
			}
			
		}else {
			return;
		}
		
	}

}
