package com.easy.transfer.active;

import javax.jms.MapMessage;
import javax.jms.Message;

public class TestJMSConsumer {

	
	public static void main(String[] args) throws Exception {
		
		//**  JMSConsumer 可以设置成全局的静态变量，只需实例化一次即可使用,禁止循环重复实例化JMSConsumer(因为其内部存在一个线程池)

		final JMSConsumer consumer = new JMSConsumer();
		consumer.setBrokerUrl("tcp://localhost:61616");
		consumer.setQueue("test");
		consumer.setUserName("system");
		consumer.setPassword("manager");
		consumer.setQueuePrefetch(500);
		consumer.setMessageListener(new MultiThreadMessageListener(50,new MessageHandler() {
			public void handle(Message message) {
				try {
					System.out.println("name is " + ((MapMessage)message).getString("name"));
					//处理时间
					Thread.sleep(5000);
					consumer.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
		consumer.start();
		
		//Thread.sleep(5000);
		//consumer.shutdown();
		
	}
	
	
}
