package com.easy.app.utility;

import java.io.IOException;

import javax.jms.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class MQUtility {
	
	public static void commitJmsSession(Session session) {
		if(session != null){
			try {
				session.commit();;
			} catch (JMSException e) {
				LogUtil.printLog("commitJmsSession Error："+e.getMessage(), Level.ERROR);
			}
			session = null;
		}
	}
	
	public static void closeJmsProducer(MessageProducer producer) {
		if(producer != null){
			try {
				producer.close();
			} catch (JMSException e) {
				LogUtil.printLog("closeJmsProducer Error："+e.getMessage(), Level.ERROR);
			}
			producer = null;
		}
	}

	public static void closeJmsConsumer(MessageConsumer consumer) {
		if(consumer != null){
			try {
				consumer.close();
			} catch (JMSException e) {
				LogUtil.printLog("closeJmsConsumer Error："+e.getMessage(), Level.ERROR);
			}
			consumer = null;
		}
	}
	
	public static void closeJmsSession(Session jmsSession) {
		if(jmsSession != null){
			try {
				jmsSession.close();
			} catch (JMSException e) {
				LogUtil.printLog("closeJmsSession Error："+e.getMessage(), Level.ERROR);
			}
			jmsSession = null;
		}
	}
	
	public static void closeJmsConnection(Connection jmsConnection) {
		if(jmsConnection != null){
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				LogUtil.printLog("closeJmsConnection Error："+e.getMessage(), Level.ERROR);
			}
			jmsConnection = null;
		}
	}

	public static void closeRabbitmqChannel(com.rabbitmq.client.Channel channel) {
		if(channel != null){
			try {
				channel.close();
			} catch (IOException e) {
				LogUtil.printLog("closeRabbitmqChannel Error："+e.getMessage(), Level.ERROR);
			}
		}
	}
	
	public static void closeRabbitmqConnection(com.rabbitmq.client.Connection connection) {
		if(connection != null){
			try {
				connection.close();
			} catch (IOException e) {
				LogUtil.printLog("closeRabbitmqConnection Error："+e.getMessage(), Level.ERROR);
			}
		}
	}
	
	public static void closeFtpConnect(FTPClient ftp) {
		if (SysUtility.isNotEmpty(ftp) && ftp.isConnected()) {
			try {
				ftp.disconnect();
			} catch (IOException ioe) {
				LogUtil.printLog("FTP服务器关闭连接失败:" + ioe.getMessage(),LogUtil.ERROR);
			}
		}
	}
}
