package com.easy.transfer.utility;

import javax.jms.*;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;

public class AuxiliUtility {
	
	public static void commitJmsSession(Session session) {
		if(session != null){
			try {
				session.commit();
			} catch (JMSException e) {
				LogUtil.printLog("commitJmsSession Error："+e.getMessage(), Level.ERROR);
			}
			session = null;
		}
	}
	public static void rollbackJmsSession(Session session) {
		if(session != null){
			try {
				session.rollback();
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

	public static void closeMessageProducer(MessageProducer producer) {
		if(producer != null){
			try {
				producer.close();
			} catch (JMSException e) {
				LogUtil.printLog("closeMessageProducer Error："+e.getMessage(), Level.ERROR);
			}
			producer = null;
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
	
	public static void ThreadSleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
