package com.easy.api.utility;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class Utility {
	
	
	
	public static Map getdb2xmlMap(String messageType) throws LegendException {
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("SELECT * FROM exs_config_dbtoxml WHERE nvl(IS_ENABLED,'1') = '1' and rownum = 1 ");
		sqlBuild.append(" AND MESSAGE_TYPE = ?",messageType);
		sqlBuild.append(" AND PART_ID like ?","api%");
		Map db2xmlMap = sqlBuild.query4Map();
		if(SysUtility.isEmpty(db2xmlMap)){
			sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("SELECT * FROM exs_config_dbtoxml WHERE nvl(IS_ENABLED,'1') = '1' and rownum = 1 ");
			sqlBuild.append(" AND MESSAGE_TYPE = ?",messageType);
			sqlBuild.append(" AND PART_ID is null");
			db2xmlMap = sqlBuild.query4Map();
		}
		return db2xmlMap;
	}
	
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
}
