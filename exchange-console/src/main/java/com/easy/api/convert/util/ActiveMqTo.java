package com.easy.api.convert.util;

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
import org.apache.log4j.Level;

import com.easy.api.convert.entity.ActiveXmlBean;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


public class ActiveMqTo {
	
	public static void main(String[] args) {
		LocalToActiveMQ();
	}
	
	public static void LocalToActiveMQ(){
		//执行解析MQ
		Connection connection = null;  
		Session session = null;
        try {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,ActiveMQConnection.DEFAULT_BROKER_URL);  
            connection = connectionFactory.createConnection();  
            connection.start();  
            session = connection.createSession(Boolean.TRUE,javax.jms.Session.AUTO_ACKNOWLEDGE);  
            Destination destination = session.createQueue("MessXml");  
            MessageConsumer consumer = session.createConsumer(destination);
            String FileData = "";
            String FileName = "";
            while (true) {
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
					}
					String createSourcePath = "E:\\MQ";
					FileUtility.createFile(createSourcePath, FileName, FileData, "GBK");
					LogUtil.printLog("activemq-r "+ SysUtility.getSysDate()+" "+FileName+"\n", Level.INFO);
            	} catch (Exception e) {
            		LogUtil.printLog(e.getMessage(), Level.ERROR);            		
					FileUtility.copyFile("E:\\MQCOPY","E:\\MQERROR",FileName);
					LogUtil.printLog("activemq-r "+ SysUtility.getSysDate()+" "+FileName+"\n", Level.INFO);
            	}finally {
            		session.commit();
				}
            }
        }catch(Exception e){
        	 LogUtil.printLog("Received ActiveMq Error:"+e.getMessage(), Level.ERROR);
        }finally {
        	try {
        		session.close();
				connection.close();
			} catch (JMSException e) {
				LogUtil.printLog("Close ActiveMq Error:"+e.getMessage(), Level.ERROR);
				e.printStackTrace();
			}
		}
	}

}
