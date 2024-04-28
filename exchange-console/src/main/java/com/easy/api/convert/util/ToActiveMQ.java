package com.easy.api.convert.util;

import java.io.File;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


public class ToActiveMQ {
	private static String Msg = "";
	public static void main(String[] args) throws JMSException {
		for (int i = 0; i <1; i++) {
//			LocalXmlToActiveMQ(new File("E:\\B\\6QT-7A023Bbc3eaa02-4df7-4ba9-bff9-a35d653309fd.xml"));
		}
	}
	
	public static boolean LocalXmlToActiveMQ(String ip,String USERNAME,String QueueName,String PWS,String FileName,String FileData) throws JMSException{
		Msg = "";
		Connection ProducerConn = null;
        Session ProducerSession = null;
        MessageProducer producer = null;
        try {
        	//连接ActiveMQ
        	ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(USERNAME, PWS,ip);  
			ProducerConn = ProducerConnFactory.createConnection();  
			ProducerConn.start();  
			ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
			Destination ProducerDes = ProducerSession.createQueue(QueueName);  
			producer = ProducerSession.createProducer(ProducerDes);  
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			if(FileName.substring(FileName.lastIndexOf(".")+1).equals("xml")||FileName.substring(FileName.lastIndexOf(".")+1).equals("XML")){
				TextMessage message = ProducerSession.createTextMessage(FileData);
				message.setStringProperty("FileName",FileName);
				message.setStringProperty("IPNET_FILENAME",FileName);
				message.setStringProperty("DATA",FileData);
				producer.send(message);
				ProducerSession.commit();
				Msg+="上传成功:"+FileName;
			}
			LogUtil.printLog("activemq-s "+ SysUtility.getSysDate()+" "+FileName+"\n",Level.INFO);
			return true;
        }catch (Exception e) {
        	 Msg+= "上传失败:"+e.getMessage();
        	 LogUtil.printLog("Send ActiveMq Error:"+e.getMessage(), Level.ERROR);
        	return false;
		}finally {
			producer.close();
			ProducerSession.close();
			ProducerConn.close();
		}
	}
	
	
	public static boolean LocalXmlToActiveMQ(String ip,String USERNAME,String QueueName,String PWS,String FileName,File File) throws JMSException{
		Connection ProducerConn = null;
        Session ProducerSession = null;
        MessageProducer producer = null;
        ActiveMQSession session = null;
        try {
        	//连接ActiveMQ
        	ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(USERNAME, PWS,ip);  
			ProducerConn = ProducerConnFactory.createConnection();  
			ProducerConn.start();  
			session = (ActiveMQSession) ProducerConn.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);  
			StreamMessage createStreamMessage = session.createStreamMessage();
			// 创建 Destination  
			Destination destination = session.createQueue(QueueName);
	        // 创建 Producer  
	        producer = session.createProducer(destination);  
	        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);// 设置为久性非持
	        BlobMessage blobMessage = session.createBlobMessage(File);  
	        blobMessage.setStringProperty("FILE_NAME", FileName);
	        blobMessage.setLongProperty("FILE_SIZE", File.length()); 
	        // 7. 发送文件  
	        producer.send(blobMessage);  
	        session.commit();
			LogUtil.printLog("activemq-s "+ SysUtility.getSysDate()+" "+FileName+"\n",Level.INFO);
			return true;
        }catch (Exception e) {
        	 Msg+= "上传失败:"+e.getMessage();
        	 LogUtil.printLog("Send ActiveMq Error:"+e.getMessage(), Level.ERROR);
        	 return false;
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
		}
	}
	
	

	public static String getMsg() {
		return Msg;
	}

	
	
}
