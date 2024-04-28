package com.easy.convert.mq;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.log4j.Level;
import org.json.JSONException;

import com.easy.access.IDataAccess;
import com.easy.convert.service.util.ServiceLogUtil;
import com.easy.convert.service.util.Util;
import com.easy.exception.LegendException;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class ToActiveMQ {
	//public static void main(String[] args) throws JMSException, JSONException, LegendException {
	//	LocalXmlToActiveMQ(null, new File("D:\\Zxsnz28G_o_180307_0000011.xml"), "tcp://47.94.217.98:61616?jms.blobTransferPolicy.defaultUploadUrl=http://localhost:8161/fileserver/connection", "", "");
	//}
	
	
	public static void LocalXmlToActiveMQ(IDataAccess iDataAccess,File file,String RECEIVEADRESS,String user,String pwd) throws JMSException, JSONException, LegendException{
		String host = "";
		int port = 61616; 
		String queueName = "MESS";
		String ip = "";
		if(SysUtility.isEmpty(file)){
			return;
		}
		if(SysUtility.isEmpty(RECEIVEADRESS)) {
			throw new RuntimeException("地址不可为空!");
		}
		String[] ipsplit = RECEIVEADRESS.split(":");
		if(ipsplit.length==1) {
			host = ipsplit[0];
			ip = "tcp://"+host+":"+port;
		}else if(ipsplit.length==2) {
			host = ipsplit[0];
			port = Integer.parseInt(ipsplit[1]);
			ip = "tcp://"+host+":"+port;
		}else if(ipsplit.length==3) {
			host = ipsplit[0];
			port = Integer.parseInt(ipsplit[1]);
			queueName = ipsplit[2];
			ip = "tcp://"+host+":"+port;
		}else {
			throw new RuntimeException("地址配置有误！格式:(ip:端口:队列名称)");
		}
		Connection ProducerConn = null;
        Session ProducerSession = null;
        MessageProducer producer = null;
        ActiveMQSession session = null;
		HashMap logMap = new HashMap();
        try {
        	//连接ActiveMQ
        	ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(user, pwd,ip);  
			ProducerConn = ProducerConnFactory.createConnection();  
			ProducerConn.start();  
			String FileName = file.getName();
			if(FileName.substring(FileName.lastIndexOf(".")+1).equals("xml")||FileName.substring(FileName.lastIndexOf(".")+1).equals("XML")){
				ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
				Destination ProducerDes = ProducerSession.createQueue(queueName);  
				producer = ProducerSession.createProducer(ProducerDes);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	    		String FileData = FileUtility.readFile(file,false,"UTF-8");
				TextMessage message = ProducerSession.createTextMessage(FileData);
				message.setStringProperty("FileName",FileName);
				message.setStringProperty("IPNET_FILENAME",FileName);
				producer.send(message);
				ProducerSession.commit();
			}else if(FileName.substring(FileName.lastIndexOf(".")+1).equals("xls")||FileName.substring(FileName.lastIndexOf(".")+1).equals("XLS")){
				session = (ActiveMQSession) ProducerConn.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);  
				StreamMessage createStreamMessage = session.createStreamMessage();
				// 创建 Destination  
				Destination destination = session.createQueue(queueName);
		        // 创建 Producer  
		        producer = session.createProducer(destination);  
		        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);// 设置为久性非持
		        BlobMessage blobMessage = session.createBlobMessage(file);  
		        blobMessage.setStringProperty("FILE.NAME", file.getName());
		        blobMessage.setLongProperty("FILE.SIZE", file.length()); 
		        // 7. 发送文件  
		        producer.send(blobMessage);  
		        session.commit();
			}
			
			ServiceLogUtil.addLogSuccessFile("activemq-s", SysUtility.getSysDate()+" "+FileName+"\n");
			
			String[] fileSplit = file.getName().split("_");
			if(fileSplit.length>=4) {
				final String regNo = fileSplit[0];
				final String fileType = fileSplit[1];
				String fileDate = fileSplit[2];
				String no =fileSplit[3];
				logMap.put("DATA_SOURCE", "ActiveMq");
				logMap.put("SERIAL_NO", regNo);
				logMap.put("TARGET_FILE_NAME", FileName);
				logMap.put("FILE_PATH", file.getPath());
				logMap.put("SEND_CODE", 2);
				logMap.put("SEND_NAME", "成功");
				logMap.put("SEND_TIME", "1");
				logMap.put("PROCESS_MSG", "已发送");
				Util.AddMessLog(iDataAccess, logMap);
			}
			file.delete();
        }catch (Exception e) {
        	String[] fileSplit = file.getName().split("_");
			if(fileSplit.length>=4) {
				final String regNo = fileSplit[0];
				final String fileType = fileSplit[1];
				String fileDate = fileSplit[2];
				String no =fileSplit[3];
				logMap.put("DATA_SOURCE", "ActiveMq");
				logMap.put("SERIAL_NO", regNo);
				logMap.put("TARGET_FILE_NAME", file.getName());
				logMap.put("FILE_PATH", file.getPath());
				logMap.put("SEND_CODE", 3);
				logMap.put("SEND_NAME", "失败");
				logMap.put("SEND_TIME", "1");
				logMap.put("PROCESS_MSG", e.getMessage());
				Util.AddMessLog(iDataAccess, logMap);
			}
        	 LogUtil.printLog("Send ActiveMq Error:"+e.getMessage(), Level.ERROR);
        	 file.delete();
		}finally {
			if(SysUtility.isNotEmpty(producer)) {
				producer.close();
			}
			if(SysUtility.isNotEmpty(session)) {
				session.close();
			}
			if(SysUtility.isNotEmpty(ProducerSession)) {
				ProducerSession.close();
			}
			if(SysUtility.isNotEmpty(ProducerConn)) {
				ProducerConn.close();
			}
//			AuxiliUtility.commitJmsSession(ProducerSession);
//			AuxiliUtility.closeJmsProducer(producer);
//        	AuxiliUtility.closeJmsSession(ProducerSession);
//        	AuxiliUtility.closeJmsConnection(ProducerConn);
		}
	}

	
	
}
