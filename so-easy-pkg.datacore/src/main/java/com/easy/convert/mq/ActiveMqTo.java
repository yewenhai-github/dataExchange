package com.easy.convert.mq;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.app.entity.ActiveXmlBean;
import com.easy.convert.service.util.ServiceLogUtil;
import com.easy.convert.service.util.Util;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;





public class ActiveMqTo {
	
	private static String SERIAL_NO = "";
	/*
	 * public static void main(String[] args) {
	 * LocalToActiveMQ(null,"tcp://172.16.0.18:61616","","","zfd"); }
	 */
	
	public static void LocalToActiveMQ(IDataAccess dataAccess,String ip,String UserName,String Pwd,String QueueName){
		//执行解析MQ
		Connection connection = null;  
		Session session = null;
        try {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(UserName, Pwd,ip);  
            connection = connectionFactory.createConnection();  
            connection.start();  
            session = connection.createSession(Boolean.TRUE,javax.jms.Session.AUTO_ACKNOWLEDGE);  
            Destination destination = session.createQueue(QueueName);  
            MessageConsumer consumer = session.createConsumer(destination);
            String FileData = "";
            String FileName = "";
            InputStream inputStream = null;
            HashMap logMap = new HashMap();
            while (true) {
            	SERIAL_NO = "";
            	try {
            		Message msg = (Message) consumer.receive(10);  
					if (null == msg) {
						break;
					}
					if (msg instanceof ObjectMessage){
						ObjectMessage message = (ObjectMessage)msg;
						ActiveXmlBean objBean = (ActiveXmlBean)message.getObject();
						FileName = objBean.getFileName();
						FileData = objBean.getXmlData();
					}else if (msg instanceof MapMessage){
						MapMessage message = (MapMessage)msg;
						FileData = message.getStringProperty("FileName");
						FileName = message.getStringProperty("FileData");
					}else if (msg instanceof TextMessage){
						TextMessage message = (TextMessage)msg;
						FileData = message.getText();
						FileName = message.getStringProperty("IPNET_FILENAME");
						
						if(SysUtility.isEmpty(FileName)){
							FileName = message.getStringProperty("FileName");
						}
						if(SysUtility.isEmpty(FileName)){
							FileName = SysUtility.getMilliSeconds()+".xml";
						}
					}else if(msg instanceof ActiveMQBlobMessage) {
						ActiveMQBlobMessage message =(ActiveMQBlobMessage) msg;
						FileName = message.getStringProperty("FILE_NAME");
						inputStream = message.getInputStream();
					}
					
					if(SysUtility.isNotEmpty(FileData) || SysUtility.isNotEmpty(inputStream)) {
						String[] fileSplit = FileName.split("_");
						if(fileSplit.length<4) {
							continue;
						}
						String regNo =SERIAL_NO =  fileSplit[0];
						final String fileType = fileSplit[1];
						String fileDate = fileSplit[2];
						String no =fileSplit[3];
						String filename =fileSplit[4];
						String sql = "SELECT * FROM exs_convert_config_path C LEFT JOIN s_auth_user S ON C.ORG_ID=S.ORG_ID WHERE C.CONFIGNAME=? AND S.REGISTER_NO=?";
						List query4List = SQLExecUtils.query4List(sql,new Callback() {
							@Override
							public void doIn(PreparedStatement ps) throws SQLException {
								ps.setString(1, fileType);
								ps.setString(2, SERIAL_NO);
							}
						});
						HashMap pathData = (HashMap) query4List.get(0);
						String functionType = (String) pathData.get("FUNCTIONTYPE");
						if(SysUtility.isNotEmpty(functionType)) {
							if(functionType.equals("2")) {//只发送过滤掉
								String TARGETFILEPATH = (String) pathData.get("TARGETFILEPATH");
								String SOURCE_BACK_PATH = (String) pathData.get("SOURCE_BACK_PATH");
								String SUCCESS_BACK_PATH = (String) pathData.get("SUCCESS_BACK_PATH");
								FileUtility.createFile(TARGETFILEPATH, FileName, FileData, "UTF-8");
								FileUtility.createFile(SOURCE_BACK_PATH, FileName, FileData, "UTF-8");
								FileUtility.createFile(SUCCESS_BACK_PATH, FileName, FileData, "UTF-8");
								logMap.put("DATA_SOURCE", "ActiveMq");
								logMap.put("SERIAL_NO", regNo);
								logMap.put("TARGET_FILE_NAME", FileName);
								logMap.put("FILE_PATH", TARGETFILEPATH);
								logMap.put("TRANSFORMATION_CODE", "1");
								logMap.put("TRANSFORMATION_NAME", "无需");
								logMap.put("SOURCE_BACK_PATH", SOURCE_BACK_PATH+File.separator+FileName);
								logMap.put("SUCCESS_BACK_PATH", SUCCESS_BACK_PATH+File.separator+FileName);
								logMap.put("PROCESS_MSG", "文件已接收 待发送");
								Util.AddMessLog(dataAccess, logMap);
								return;
							}
						}
						String MainFolder_Source = SysUtility.GetSetting("System", "MainFolder_Source");
						String Source = MainFolder_Source +File.separator+regNo+File.separator+fileType+File.separator+"SOURCE";
						String createSourcePath = Source;
						String SouceBak = MainFolder_Source +File.separator+regNo+File.separator+fileType+File.separator+"SOURCEBACK";
						if(SysUtility.isNotEmpty(inputStream)) {
							FileUtility.createFile(SouceBak, FileName, inputStream);
							FileUtility.createFile(createSourcePath, FileName, inputStream);
						}else if(SysUtility.isNotEmpty(FileData)) {
							FileUtility.createFile(SouceBak, FileName, FileData, "UTF-8");
							FileUtility.createFile(createSourcePath, FileName, FileData, "UTF-8");
						}
						logMap.put("DATA_SOURCE", "ActiveMq");
						logMap.put("SERIAL_NO", regNo);
						logMap.put("TARGET_FILE_NAME", FileName);
						logMap.put("FILE_PATH", createSourcePath);
						logMap.put("SOURCE_BACK_PATH", SouceBak+File.separator+FileName);
						logMap.put("PROCESS_MSG", "文件已接收 待转换");
						Util.AddMessLog(dataAccess, logMap);
						ServiceLogUtil.addLogSuccessFile("activemq-r", SysUtility.getSysDate()+" "+FileName+"\n");
					}
					
            	} catch (Exception e) {
            		LogUtil.printLog(e.getMessage(), Level.ERROR);            		
					ServiceLogUtil.addLogFailFile("activemq-r", SysUtility.getSysDate()+" "+FileName+"\n");
					if(SysUtility.isNotEmpty(SERIAL_NO) && SysUtility.isNotEmpty(FileName)) {
						logMap.put("DATA_SOURCE", "ActiveMq");
						logMap.put("SERIAL_NO", SERIAL_NO);
						logMap.put("TARGET_FILE_NAME", FileName);
						logMap.put("PROCESS_MSG", "文件已接收失败："+ e.getMessage());
						Util.AddMessLog(dataAccess, logMap);
					}
            	}finally {
            		session.commit();
				}
            }
        }catch(Exception e){
        	 LogUtil.printLog("Received ActiveMq Error:"+e.getMessage(), Level.ERROR);
        }finally {
        	try {
        		if(session != null) {
        			session.close();
        		}
        		if(connection != null) {
        			connection.close();
        		}
			} catch (JMSException e) {
				LogUtil.printLog("Close ActiveMq Error:"+e.getMessage(), Level.ERROR);
				e.printStackTrace();
			}
		}
	}


}
