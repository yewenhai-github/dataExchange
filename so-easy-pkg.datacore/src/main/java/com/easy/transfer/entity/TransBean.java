package com.easy.transfer.entity;

import java.io.Serializable;

import javax.jms.MessageProducer;
import javax.jms.Session;

public class TransBean implements Serializable{
	private static final long serialVersionUID = 1564067533219132801L;
	
	Session jmsSession;
	MessageProducer jmsMessageProducer;
	public Session getJmsSession() {
		return jmsSession;
	}
	public void setJmsSession(Session jmsSession) {
		this.jmsSession = jmsSession;
	}
	public MessageProducer getJmsMessageProducer() {
		return jmsMessageProducer;
	}
	public void setJmsMessageProducer(MessageProducer jmsMessageProducer) {
		this.jmsMessageProducer = jmsMessageProducer;
	}
	
	
	
	
}
