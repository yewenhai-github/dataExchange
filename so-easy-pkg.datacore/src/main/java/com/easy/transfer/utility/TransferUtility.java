package com.easy.transfer.utility;

import java.io.*;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.*;

import com.easy.app.utility.MQUtility;
import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.transfer.active.JMSConsumer;
import com.easy.transfer.active.MessageHandler;
import com.easy.transfer.active.MultiThreadMessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Level;
import org.apache.poi.ss.formula.ThreeDEval;
import org.json.JSONArray;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.entity.ActiveXmlBean;
import com.easy.app.utility.ExsUtility;
import com.easy.entity.ServicesBean;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.security.AESGenerator;
import com.easy.transfer.active.RabbitBasicProperties;
import com.easy.utility.EntityUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.rabbitmq.client.BasicProperties;
import org.json.JSONObject;

public class TransferUtility {

	public static void XmlToMQ(IDataAccess DataAccess)throws Exception{
		List XmlToMqList = new ArrayList();
		if(!SysUtility.getDBPoolClose()){
			String XmlToMqSQL = "SELECT * FROM exs_config_xmltomq WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ?";
			XmlToMqList = SQLExecUtils.query4List(XmlToMqSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getCurrentCronJobName());
				}
			});
		}else{
			String tableName = "exs_config_xmltomq";
			Datas datas = new Datas();
			SysUtility.setConfigDatas(datas, FileUtility.GetSourceFileList(),tableName);
			JSONArray rows = datas.GetTables(tableName);
			XmlToMqList = SysUtility.JSONArrayToList(rows);
		}

		for (int i = 0; i < XmlToMqList.size(); i++) {
			HashMap map = (HashMap)XmlToMqList.get(i);
			ServicesBean bean = new ServicesBean();
			EntityUtility.hashMapToEntity(bean, map);

			ExsUtility.InitServicesBeanPath(bean);
			if(ExsConstants.FTP.equals(bean.getMqType())){
				LocalXmlToFtp(bean, DataAccess);
			}else if(ExsConstants.IBMMQ.equals(bean.getMqType())){
				LocalXmlToIBMMQ(bean, DataAccess);//IBMMQ
			}else if(ExsConstants.RabbitMQ.equals(bean.getMqType())){
				LocalXmlToRabbitMQ(bean, DataAccess);
			}else {
				LocalXmlToActiveMQ(bean);
			}

			if(!SysUtility.getDBPoolClose()){
				String UpdateSQL = "UPDATE exs_config_xmltomq SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+(String)map.get("INDX");
				DataAccess.ExecSQL(UpdateSQL);
				DataAccess.ComitTrans();
			}
		}
	}

	public static void MQToXml(IDataAccess DataAccess)throws Exception{
		List XmlToMqList = new ArrayList();
		if(!SysUtility.getDBPoolClose()){
			String XmlToMqSQL = "SELECT * FROM exs_config_mqtoxml WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ?";
			XmlToMqList = SQLExecUtils.query4List(XmlToMqSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getCurrentCronJobName());
				}
			});
		}else{
			String tableName = "exs_config_mqtoxml";
			Datas datas = new Datas();
			SysUtility.setConfigDatas(datas, FileUtility.GetSourceFileList(),tableName);
			JSONArray rows = datas.GetTables(tableName);
			XmlToMqList = SysUtility.JSONArrayToList(rows);
		}

		for (int i = 0; i < XmlToMqList.size(); i++) {
			HashMap map = (HashMap)XmlToMqList.get(i);
			ServicesBean bean = new ServicesBean();
			EntityUtility.hashMapToEntity(bean, map);
			ExsUtility.InitServicesBeanPath(bean);

			if(ExsConstants.FTP.equals(bean.getMqType())){
				FtpToXml(bean, DataAccess);
			}else if(ExsConstants.IBMMQ.equals(bean.getMqType())){
				IBMMQToXml(bean);
			}else if(ExsConstants.RabbitMQ.equals(bean.getMqType())){
				RabbitMQToXml(bean);
			}else {
				ActiveMQToXml(bean);
			}

			if(!SysUtility.getDBPoolClose()){
				String UpdateSQL = "UPDATE exs_config_mqtoxml SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+(String)map.get("INDX");
				DataAccess.ExecSQL(UpdateSQL);
				DataAccess.ComitTrans();
			}
		}
	}

	public static void MQToMQ(IDataAccess DataAccess)throws Exception{
		List MqToMqList = new ArrayList();
		if(!SysUtility.getDBPoolClose()){
			String XmlToMqSQL = "SELECT * FROM EXS_CONFIG_MQTOMQ WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ? ";//AND source_mq = 'business'
			MqToMqList = SQLExecUtils.query4List(XmlToMqSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getCurrentCronJobName());
				}
			});
		}else{
			String tableName = "EXS_CONFIG_MQTOMQ";
			Datas datas = new Datas();
			SysUtility.setConfigDatas(datas, FileUtility.GetSourceFileList(),tableName);
			JSONArray rows = datas.GetTables(tableName);
			MqToMqList = SysUtility.JSONArrayToList(rows);
		}

		for (int i = 0; i < MqToMqList.size(); i++) {
			HashMap map = (HashMap)MqToMqList.get(i);
			ServicesBean bean = new ServicesBean();
			bean.setMqType((String)map.get("mq_type"));
			bean.setProducerUrl(SysUtility.replaceIP((String)map.get("producer_url")));
			bean.setProducerUser((String)map.get("producer_user"));
			bean.setProducerPwd((String)map.get("producer_pwd"));
			bean.setConsumerUrl(SysUtility.replaceIP((String)map.get("consumer_url")));
			bean.setConsumerUser((String)map.get("consumer_user"));
			bean.setConsumerPwd((String)map.get("consumer_pwd"));
			bean.setSourceMq((String)map.get("source_mq"));
			bean.setTargetMq((String)map.get("target_mq"));
//			EntityUtility.hashMapToEntity(bean, map);
//			ExsUtility.InitServicesBeanPath(bean);

			if(ExsConstants.ActiveMQ.equals(bean.getMqType())){
				ActiveMQToMQ(DataAccess, bean);
				Thread.sleep(100);
			}else if(ExsConstants.IBMMQ.equals(bean.getMqType())){

			}else if(ExsConstants.RabbitMQ.equals(bean.getMqType())){

			}else {

			}

			if(!SysUtility.getDBPoolClose()){
				if(SysUtility.IsOracleDB()) {
					DataAccess.ExecSQL("UPDATE EXS_CONFIG_MQTOMQ SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+(String)map.get("indx"));
				}else if(SysUtility.IsMySqlDB()) {
					DataAccess.ExecSQL("UPDATE EXS_CONFIG_MQTOMQ SET REC_VER=REC_VER+1,MODIFY_TIME=now() WHERE INDX = "+(String)map.get("indx"));
				}
				DataAccess.ComitTrans();
			}
		}
	}

	public static void LocalXmlToFtp(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		ExsUtility.InitServicesBeanPath(bean);
		List<Object[]> fileList = FileUtility.GetSourceFileList(10000, bean.getSourcePath(), bean.getErrorPath());
		if(SysUtility.isEmpty(fileList) || fileList.size() <=0){
			return;
		}

		FTPClient ftp = new FTPClient();
		int reply;
		try {
			String ProducerUrl = bean.getProducerUrl();
			String ftp_url = ProducerUrl.split(":")[0];
			String ftp_port = ProducerUrl.split(":")[1];
			String ftp_username = bean.getProducerUser();
			String ftp_pwd = bean.getProducerPwd();
			if (SysUtility.isNotEmpty(ftp_port)) {
				ftp.connect(ftp_url,Integer.parseInt(ftp_port));
			} else {
				ftp.connect(ftp_url);
			}
			ftp.login(ftp_username, ftp_pwd);// 登录
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();// 连接FTP服务器失败
				LogUtil.printLog("FTP服务器连接失败:"+ftp_url+":"+ftp_port+":"+ftp_username+":"+ftp_pwd,LogUtil.ERROR);
			} else {// 连接成功
				for (int i = 0; i < fileList.size(); i++) {
					try {
						long start = System.currentTimeMillis();
						Object[] obj = (Object[])fileList.get(i);
						File file = (File)obj[1];
						bean.setSourcePath((String)obj[0]);
						bean.setFileName(file.getName());

						String FileData = FileUtility.readFile(file,false,bean.getEncoding());
						FileData = CustUtility.xmlDocPro(file, bean, DataAccess, FileData);//xml文本特殊处理
						//为Ftp时，TargetMq存储的是目标报文路径
						FileUtility.createFile(bean.getTargetMq(), bean.getFileName(), FileData, bean.getEncoding());
						FileUtility.createFile(bean.getTargetPath(), bean.getFileName(), FileData, bean.getEncoding());
						addLogSuccessFile("ftp", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
						LogUtil.printLog(bean.getFileName()+" cost:"+(System.currentTimeMillis()-start), Level.INFO);
					} catch (Exception e) {
						FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
						addLogFailFile("ftp", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
						LogUtil.printLog("Send ftp Error:"+bean.getFileName(), Level.ERROR);
					} finally{
						FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
					}
				}
			}
		} catch (SocketException e) {
			LogUtil.printLog("FTP访问失败:" + e.getMessage(), LogUtil.ERROR);
		} catch (IOException e) {
			LogUtil.printLog("FTP访问失败:" + e.getMessage(), LogUtil.ERROR);
		} catch (Exception e) {
			LogUtil.printLog("FTP访问失败:" + e.getMessage(), LogUtil.ERROR);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					LogUtil.printLog("FTP服务器关闭连接失败:" + ioe.getMessage(),LogUtil.ERROR);
				}
			}
		}
	}

	public static void FtpToXml(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String ConsumerUrl = bean.getConsumerUrl();
		String ftp_url = ConsumerUrl.split(":")[0];
		String ftp_port = ConsumerUrl.split(":")[1];
		String ftp_username = bean.getConsumerUser();
		String ftp_pwd = bean.getConsumerPwd();

		FTPClient ftp = new FTPClient();
		int reply;
		try {
			if (SysUtility.isNotEmpty(ftp_port)) {
				ftp.connect(ftp_url,Integer.parseInt(ftp_port));
			} else {
				ftp.connect(ftp_url);
			}
			ftp.login(ftp_username, ftp_pwd);// 登录
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();// 连接FTP服务器失败
				LogUtil.printLog("FTP服务器连接失败:"+ftp_url+":"+ftp_port+":"+ftp_username+":"+ftp_pwd,LogUtil.ERROR);
			} else {// 连接成功
				String path = bean.getSourceMq();
				FTPFile[] ftpFiles = ftp.listFiles(path);
				for (int i = 0; i < ftpFiles.length; i++) {
					if (ftpFiles[i].isFile()) {
						String fileName = ftpFiles[i].getName();
						try {
							String filePath = path + "/" +ftpFiles[i].getName();
							InputStream is = ftp.retrieveFileStream(filePath);
							if(is == null){
								LogUtil.printLog("Ftp解析文件出错:"+fileName, Level.ERROR);
								continue;
							}
							//生成本地文件
							String[] SourcePaths = bean.getSourcePath().split(";");
							for (int j = 0; j < SourcePaths.length; j++) {
								String createSourcePath = SourcePaths[j];
								if(SysUtility.isNotEmpty(bean.getMessageDest()) && "true".equals(SysUtility.GetProperty("System.properties", "MessageDestFolder"))){
									createSourcePath = bean.getSourcePath()+File.separator+bean.getMessageDest();
								}
								FileUtility.createFile(createSourcePath, bean.getFileName(), is);
								addLogSuccessFile("ftpr", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
							}
						} catch (Exception e) {
							addLogFailFile("ftpr", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
							LogUtil.printLog("Ftp解析文件出错:"+fileName, Level.ERROR);
						} finally{
							ftp.completePendingCommand();
						}
					}
				}
			}
		} catch (SocketException e) {
			LogUtil.printLog("FTP访问失败:" + e.getMessage(), LogUtil.ERROR);
		} catch (IOException e) {
			LogUtil.printLog("FTP访问失败:" + e.getMessage(), LogUtil.ERROR);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					LogUtil.printLog("FTP服务器关闭连接失败:" + ioe.getMessage(),LogUtil.ERROR);
				}
			}
		}
	}

	public static void LocalXmlToIBMMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		//TODO
	}

	public static void LocalXmlToRabbitMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		ExsUtility.InitServicesBeanPath(bean);
		List<Object[]> fileList = FileUtility.GetSourceFileList(10000, bean.getSourcePath(), bean.getErrorPath());
		if(SysUtility.isEmpty(fileList) || fileList.size() <=0){
			return;
		}

		com.rabbitmq.client.Connection connection = null;
		com.rabbitmq.client.Channel channel = null;
		try {
			String TargetMQ = bean.getTargetMq();
			String ProducerUrl = bean.getProducerUrl();
			String ProducerUser = bean.getProducerUser();//"guest"
			String ProducerPwd = bean.getProducerPwd();
			String host = ProducerUrl.split(":")[0];
			int port = Integer.valueOf(ProducerUrl.split(":")[1]);//5672
			/* 使用工厂类建立Connection和Channel，并且设置参数 */
			com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
			factory.setHost(host);// MQ的IP
			factory.setPort(port);// MQ端口
			factory.setUsername(ProducerUser);// MQ用户名
			factory.setPassword(ProducerPwd);// MQ密码
			connection = factory.newConnection();
			channel = connection.createChannel();
			/* 创建消息队列，并且发送消息 */
			channel.queueDeclare(TargetMQ, false, false, false, null);

			for (int i = 0; i < fileList.size(); i++) {
				try {
					long start = System.currentTimeMillis();
					Object[] obj = (Object[])fileList.get(i);
					File file = (File)obj[1];
					bean.setSourcePath((String)obj[0]);
					bean.setFileName(file.getName());

					String FileData = FileUtility.readFile(file,false,bean.getEncoding());
					FileData = CustUtility.xmlDocPro(file, bean, DataAccess, FileData);//xml文本特殊处理

					RabbitBasicProperties Properties = new RabbitBasicProperties();
					Properties.setMessageId(SysUtility.GetUUID());
					Map map = new HashMap();
					map.put("FileName", (String)bean.getFileName());
					Properties.setHeaders(map);
					channel.basicPublish("", TargetMQ, Properties, FileData.getBytes());

					FileUtility.createFile(bean.getTargetPath(), bean.getFileName(), FileData, bean.getEncoding());
					addLogSuccessFile("rabbitmqs", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
					LogUtil.printLog(bean.getFileName()+" cost:"+(System.currentTimeMillis()-start), Level.INFO);
				} catch (Exception e) {
					FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
					addLogFailFile("rabbitmqs", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
					LogUtil.printLog("Send RabbitMq Error:"+bean.getFileName(), Level.ERROR);
				} finally{
					FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("Send RabbitMq Error:"+e.getMessage(), Level.ERROR);
		} finally{
			/* 关闭连接 */
			channel.close();
			connection.close();
		}
	}

	@SuppressWarnings("deprecation")
	private static void IBMMQToXml(ServicesBean bean) {
		//TODO
	}

	@SuppressWarnings("deprecation")
	private static void RabbitMQToXml(ServicesBean bean) throws IOException {
		com.rabbitmq.client.Connection connection = null;
		com.rabbitmq.client.Channel channel = null;
		try {
			String ConsumerUrl = bean.getConsumerUrl();
			String ConsumerUser = bean.getConsumerUser();
			String ConsumerPwd = bean.getConsumerPwd();
			String SourceMQ = bean.getSourceMq();
			String host = ConsumerUrl.split(":")[0];
			int port = Integer.valueOf(ConsumerUrl.split(":")[1]);

			/* 建立连接 */
			com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
			factory.setHost(host);// MQ的IP
			factory.setPort(port);// MQ端口
			factory.setUsername(ConsumerUser);// MQ用户名
			factory.setPassword(ConsumerPwd);// MQ密码
			connection = factory.newConnection();
			channel = connection.createChannel();

			/* 声明要连接的队列 */
			channel.queueDeclare(SourceMQ, false, false, false, null);
			/* 创建消费者对象，用于读取消息 */
			com.rabbitmq.client.QueueingConsumer consumer = new com.rabbitmq.client.QueueingConsumer(channel);
			channel.basicConsume(SourceMQ, true, consumer);

			/* 读取队列，并且阻塞，即在读到消息之前在这里阻塞，直到等到消息，完成消息的阅读后，继续阻塞循环 */
			long begin = System.currentTimeMillis();
			while (System.currentTimeMillis() - begin < 10*1000) {
				long start = System.currentTimeMillis();
				com.rabbitmq.client.QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String FileData = new String(delivery.getBody());

				BasicProperties Properties = delivery.getProperties();
				Map map = Properties.getHeaders();
				Object fileName = map.get("FileName");
				bean.setFileName(fileName.toString());

				//生成本地文件
				String[] SourcePaths = bean.getSourcePath().split(";");
				for (int j = 0; j < SourcePaths.length; j++) {
					String createSourcePath = SourcePaths[j];
					if(SysUtility.isNotEmpty(bean.getMessageDest()) && "true".equals(SysUtility.GetProperty("System.properties", "MessageDestFolder"))){
						createSourcePath = bean.getSourcePath()+File.separator+bean.getMessageDest();
					}
					createSourcePath = getSubfolderPath(bean.getSubfolderNo(), createSourcePath);//子文件新增
					FileUtility.createFile(createSourcePath, bean.getFileName(), FileData, bean.getEncoding());
					addLogSuccessFile("rabbitmqr", SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
				}
				LogUtil.printLog(bean.getFileName()+" cost:"+(System.currentTimeMillis()-start), Level.INFO);
			}
		} catch (Exception e) {
			LogUtil.printLog("Received Error:"+e.getMessage(), Level.ERROR);
		} finally {
			connection.close();
			channel.close();
		}
	}

	public static void LocalXmlToActiveMQ(ServicesBean bean)throws Exception{
		List<Object[]> fileList = FileUtility.GetSourceFileList(10000, bean.getSourcePath(), bean.getErrorPath());
		if(SysUtility.isEmpty(fileList) || fileList.size() <=0){
			return;
		}
		Connection ProducerConn = null;
		Session ProducerSession = null;
		MessageProducer producer = null;
		try {
			//连接ActiveMQ
			ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(bean.getProducerUser(),bean.getProducerPwd(),bean.getProducerUrl());
			ProducerConn = ProducerConnFactory.createConnection();
			ProducerConn.start();
			ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
			Destination ProducerDes = ProducerSession.createQueue(bean.getTargetMq());
			producer = ProducerSession.createProducer(ProducerDes);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			for (int i = 0; i < fileList.size(); i++) {
				try {
					Object[] obj = (Object[])fileList.get(i);
					File file = (File)obj[1];
					bean.setSourcePath((String)obj[0]);
					String FileName = file.getName();
					String FileData = FileUtility.readFile(file,false,bean.getEncoding());
					bean.setFileName(FileName);

					if("1".equals(bean.getActiveMqMode())){//ObjectMessage
						ActiveXmlBean objBean = new ActiveXmlBean();
						objBean.setFileName(FileName);
						if(SysUtility.isNotEmpty(bean.getAesKey())){
							objBean.setXmlData(AESGenerator.encrypt(FileData, bean.getAesKey()));
						}else{
							objBean.setXmlData(FileData);
						}
						ObjectMessage message = ProducerSession.createObjectMessage(objBean);
						producer.send(message);
					}else if("2".equals(bean.getActiveMqMode())){//MapMessage
						MapMessage message = ProducerSession.createMapMessage();
						message.setStringProperty("FileName",FileName);
						message.setStringProperty("FileData",FileData);
						producer.send(message);
					}else{//TextMessage
						TextMessage message = ProducerSession.createTextMessage(FileData);
						message.setStringProperty("FileName",FileName);
						message.setStringProperty("IPNET_FILENAME",FileName);
						producer.send(message);
					}
					FileUtility.createFile(bean.getTargetPath(), FileName, FileData, bean.getEncoding());
					addLogSuccessFile("activemq", "localtomq "+SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
				} catch (Exception e) {
					LogUtil.printLog("Send ActiveMq Error:"+bean.getFileName(), Level.ERROR);
					FileUtility.copyFile(bean.getSourcePath(),bean.getErrorPath(),bean.getFileName());
					addLogFailFile("activemq", "localtomq "+SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
				} finally{
					FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
					AuxiliUtility.commitJmsSession(ProducerSession);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("Send ActiveMq Error:"+e.getMessage(), Level.ERROR);
		} finally{
			AuxiliUtility.closeJmsProducer(producer);
			AuxiliUtility.closeJmsSession(ProducerSession);
			AuxiliUtility.closeJmsConnection(ProducerConn);
		}
	}

	public static void ActiveMQToXml(ServicesBean bean)throws Exception{
		//执行解析MQ
		Connection connection = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
			Destination destination = session.createQueue(bean.getSourceMq());
			MessageConsumer consumer = session.createConsumer(destination);
			while (true) {
				try {
					String FileName = "";
					String FileData = "";
					Message msg = consumer.receiveNoWait();
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
					if(SysUtility.isNotEmpty(bean.getAesKey())){
						FileData = AESGenerator.decrypt(FileData, bean.getAesKey());
					}
					bean.setFileName(FileName);
					//生成本地文件
					String[] SourcePaths = bean.getSourcePath().split(";");
					for (int j = 0; j < SourcePaths.length; j++) {
						String createSourcePath = SourcePaths[j];
						if(SysUtility.isNotEmpty(bean.getMessageDest()) && "true".equals(SysUtility.GetProperty("System.properties", "MessageDestFolder"))){
							createSourcePath = bean.getSourcePath()+File.separator+bean.getMessageDest();
						}
						createSourcePath = getSubfolderPath(bean.getSubfolderNo(), createSourcePath);//子文件新增
						FileUtility.createFile(createSourcePath, bean.getFileName(), FileData, bean.getEncoding());
						addLogSuccessFile("activemq", "mqtolocal "+SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
					}
				} catch (Exception e) {
					LogUtil.printLog("MQToXml：Active Received To Xml"+e.getMessage(), Level.ERROR);
				} finally {
					bean.setFileName("");
					AuxiliUtility.commitJmsSession(session);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("Received ActiveMq Error:"+e.getMessage(), Level.ERROR);
			AuxiliUtility.ThreadSleep(60*1000);
		} finally {
			AuxiliUtility.closeJmsConnection(connection);
		}
	}

	public static void main(String[] args) {
//		LinkedBlockingQueue<String> dataResult = new LinkedBlockingQueue<String>();
//		dataResult.add("aa");
//		dataResult.add("bb");
//		while(true){
//			String item = dataResult.poll();
//			if(SysUtility.isEmpty(item)){
//				break;
//			}
//			System.out.println(item);
//		}
//		while(true){
//			String item = dataResult.poll();
//			if(SysUtility.isEmpty(item)){
//				break;
//			}
//			System.out.println(item);
//		}
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("center","o4JdHlE26eaGmhHU","tcp://172.16.32.14:61616");
		Thread.currentThread().getThreadGroup().list();


	}

	public static Map mqMap = new HashMap();
	public static Map dataMap = new HashMap();



	public static void ActiveMQToMQ(IDataAccess DataAccess, ServicesBean bean)throws Exception {
		List<Message> result = new ArrayList<Message>();//临时方案，后续考虑切换为Redis方案
		//1. 收集源MQ的数据
//		String key = bean.getConsumerUrl()+"."+bean.getConsumerUser()+"."+bean.getConsumerPwd()+"."+bean.getSourceMq();
//		Object obj = mqMap.get(key);
//		if(SysUtility.isEmpty(obj)){
//			final JMSConsumer consumer = new JMSConsumer();
//			consumer.setBrokerUrl(bean.getConsumerUrl());
//			consumer.setUserName(bean.getConsumerUser());
//			consumer.setPassword(bean.getConsumerPwd());
//			consumer.setQueue(bean.getSourceMq());
//			consumer.setQueuePrefetch(500);
//			consumer.setMessageListener(new MultiThreadMessageListener(50,new MessageHandler() {
//				public void handle(Message message) {
//					try {
//						LinkedBlockingQueue<Message> queue = (LinkedBlockingQueue<Message>)dataMap.get(key);
//						queue.add(message);
//
//						message.acknowledge();
//
//						LogUtil.printLog("开始消费:queue="+queue.size()+"       "+message.getJMSMessageID(), Level.WARN);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}));
//			consumer.start();
//			mqMap.put(key, consumer);
//			dataMap.put(key, new LinkedBlockingQueue<Message>());
//		}
//		LinkedBlockingQueue<Message> dataResult = (LinkedBlockingQueue<Message>)dataMap.get(key);
//		while(true){
//			Message item = dataResult.poll();
//			if(SysUtility.isEmpty(item)){
//				break;
//			}
//			result.add(item);
//		}

		//1. 收集源MQ的数据
		Connection connection = null;
		Session session = null;
		MessageConsumer consumer = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
			Destination destination = session.createQueue(bean.getSourceMq());
			consumer = session.createConsumer(destination);
			while (true) {
				try {
					Message msg = consumer.receive(10);
//					if("business".equals(bean.getSourceMq())){
//						LogUtil.printLog(bean.getConsumerUrl()+"."+bean.getConsumerUser()+"."+bean.getConsumerPwd()+"."+bean.getSourceMq()+"开始消费:"+msg, Level.WARN);
//					}
					if (null == msg) {
						break;
					}
					result.add(msg);
				} catch (Exception e) {
					LogUtil.printLog("ActiveMQToMQ Error1:"+e.getMessage(), Level.ERROR);
					break;
				} finally {
					MQUtility.commitJmsSession(session);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("ActiveMQToMQ Error2:"+e.getMessage(), Level.ERROR);
			Thread.sleep(10000);
		} finally {
			MQUtility.closeJmsConsumer(consumer);
			MQUtility.closeJmsSession(session);
			MQUtility.closeJmsConnection(connection);
		}

		//2. 检验：没有数据
		if(SysUtility.isEmpty(result)){
			return;
		}
//		LogUtil.printLog("发送数据开始..."+bean.getConsumerUrl()+" "+bean.getSourceMq()+"  报文量："+result.size(),LogUtil.WARN);
		//3. 发送MQ数据
		String[] producerUrls = bean.getProducerUrl().split(",");
		String[] ProducerUsers = SysUtility.isNotEmpty(bean.getProducerUser())?bean.getProducerUser().split(","):new String[] {};
		String[] ProducerPwds = SysUtility.isNotEmpty(bean.getProducerPwd())?bean.getProducerPwd().split(","):new String[] {};
		String[] TargetMqs = SysUtility.isNotEmpty(bean.getTargetMq())?bean.getTargetMq().split(","):new String[] {};
		for (int i = 0; i < producerUrls.length; i++) {
			String producerUrl = producerUrls[i];
			String ProducerUser = ProducerUsers.length != 0?ProducerUsers[i]:"";
			String ProducerPwd = ProducerPwds.length != 0?ProducerPwds[i]:"";
			String TargetMq = TargetMqs.length != 0?TargetMqs[i]:"";

			//执行发送MQ
			Connection ProducerConn = null;
			Session ProducerSession = null;
			MessageProducer producer = null;
			try {
				ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(ProducerUser,ProducerPwd,producerUrl);
				ProducerConn = ProducerConnFactory.createConnection();
				ProducerConn.start();
				ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
				Destination ProducerDes = ProducerSession.createQueue(TargetMq);
				producer = ProducerSession.createProducer(ProducerDes);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);

				for (int j = 0; j < result.size(); j++) {
					Message msg =  result.get(j);
					producer.send(msg);
					AuxiliUtility.commitJmsSession(ProducerSession);
				}
			} catch (Exception e) {
				LogUtil.printLog("数据转发失败("+producerUrl+"|"+ProducerUser+"|"+ProducerPwd+"):"+e.getMessage(), LogUtil.ERROR);
//				e.printStackTrace();
			} finally {
				AuxiliUtility.closeJmsProducer(producer);
				AuxiliUtility.closeJmsSession(ProducerSession);
				AuxiliUtility.closeJmsConnection(ProducerConn);
			}
		}

		//4. 消费MQ数据，根据messageid消费
//		try {
//			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
//			ConsumerConn = connectionFactory.createConnection();
//			ConsumerConn.start();
//			ConsumerSession = ConsumerConn.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
//			Destination destination = ConsumerSession.createQueue(bean.getSourceMq());
//			consumer = ConsumerSession.createConsumer(destination);
//			while (true) {
//				try {
//					Message msg = consumer.receiveNoWait();
//					if (null == msg) {
//						break;//没有消息，不做处理
//					}
//					if(ids.contains(msg.getJMSMessageID())){
//
//						try {
//							JSONObject Log = new JSONObject();
//							Log.put("MSG_MODE", "ActiveMQToMQ");
//							Log.put("MSG_STATUS", "1");
//							Log.put("MSG_TYPE", msg.getStringProperty("msg_type"));
//							Log.put("MSG_NO", msg.getStringProperty("msg_no"));
//							Log.put("MSG_DESC", "数据转发成功！"+bean.getProducerUrl()+" "+bean.getTargetMq());
//							Log.put("CREATE_TIME", SysUtility.getSysDate());
//							Log.put("IS_ENABLED", "1");
//							DataAccess.Insert("exs_handle_log", Log);
//						} catch (Exception e) {
//							DataAccess.RoolbackTrans();
//							LogUtil.printLog("日志写入失败："+e.getMessage(), Level.ERROR);
//						} finally {
//							DataAccess.ComitTrans();
//						}
//						AuxiliUtility.commitJmsSession(ConsumerSession);//消费
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} catch (JMSException e) {
//			e.printStackTrace();
//		} finally {
//			AuxiliUtility.closeJmsConsumer(consumer);
//			AuxiliUtility.closeJmsSession(ConsumerSession);
//			AuxiliUtility.closeJmsConnection(ConsumerConn);
//		}
		LogUtil.printLog("发送数据结束!!!("+result.size()+") 源："+bean.getConsumerUrl()+"-"+bean.getSourceMq()+" 末："+bean.getProducerUrl()+"-"+bean.getTargetMq(),LogUtil.WARN);
	}

	public static void ActiveMQToMQ_OLD(IDataAccess DataAccess, ServicesBean bean)throws Exception{
		String[] producerUrls = bean.getProducerUrl().split(",");
		String[] ProducerUsers = SysUtility.isNotEmpty(bean.getProducerUser())?bean.getProducerUser().split(","):new String[] {};
		String[] ProducerPwds = SysUtility.isNotEmpty(bean.getProducerPwd())?bean.getProducerPwd().split(","):new String[] {};

		//先进行一次连接，如果有目标mq宕机，则不发送数据


		//执行解析MQ，如果有mq无法连接，则不发送数据
		Connection TestConsumerConn = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
			TestConsumerConn = connectionFactory.createConnection();
			TestConsumerConn.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			AuxiliUtility.closeJmsConnection(TestConsumerConn);
		}
		for (int i = 0; i < producerUrls.length; i++) {
			String producerUrl = producerUrls[i];
			String ProducerUser = ProducerUsers.length != 0?ProducerUsers[i]:"";
			String ProducerPwd = ProducerPwds.length != 0?ProducerPwds[i]:"";
			Connection TestProducerConn = null;
			try {
				ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(ProducerUser,ProducerPwd,producerUrl);
				TestProducerConn = ProducerConnFactory.createConnection();
				TestProducerConn.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				AuxiliUtility.closeJmsConnection(TestProducerConn);
			}
		}


		for (int i = 0; i < producerUrls.length; i++) {
			//执行解析MQ
			Connection ConsumerConn = null;
			//执行发送MQ
			Connection ProducerConn = null;
			Session ProducerSession = null;
			MessageProducer producer = null;
			try {
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
				ConsumerConn = connectionFactory.createConnection();
				ConsumerConn.start();
				Session ConsumerSession = ConsumerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
				Destination destination = ConsumerSession.createQueue(bean.getSourceMq());
				MessageConsumer consumer = ConsumerSession.createConsumer(destination);

				String producerUrl = producerUrls[i];
				String ProducerUser = ProducerUsers.length != 0?ProducerUsers[i]:"";
				String ProducerPwd = ProducerPwds.length != 0?ProducerPwds[i]:"";

				ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(ProducerUser,ProducerPwd,producerUrl);
				ProducerConn = ProducerConnFactory.createConnection();
				ProducerConn.start();
				ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
				Destination ProducerDes = ProducerSession.createQueue(bean.getTargetMq());
				producer = ProducerSession.createProducer(ProducerDes);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);

				while (true) {
					try {
						Message msg = consumer.receiveNoWait();
						if (null == msg) {
							break;
						}
						producer.send(msg);


						String messageType = "";
						String serialNo = "";
						String FileName = "";
						if (msg instanceof ObjectMessage){
							ObjectMessage message = (ObjectMessage)msg;
							ActiveXmlBean objBean = (ActiveXmlBean)message.getObject();
							FileName = objBean.getFileName();
						}else if (msg instanceof MapMessage){
							MapMessage message = (MapMessage)msg;
							FileName = message.getStringProperty("FileName");
						}
						if(SysUtility.isNotEmpty(FileName) && FileName.indexOf(".") > 0){
							serialNo = FileName;
							FileName = FileName.substring(0, FileName.lastIndexOf("."));
							String[] names = FileName.split("_");
							if(names.length == 2){
								messageType = names[0];
								serialNo = names[1];
							}
						}

						try {
							JSONObject Log = new JSONObject();
							Log.put("MSG_MODE", "ActiveMQToMQ");
							Log.put("MSG_STATUS", "1");
							Log.put("MSG_TYPE", messageType);
							Log.put("MSG_NO", serialNo);
							Log.put("MSG_DESC", producerUrl);
							Log.put("CREATE_TIME", SysUtility.getSysDate());
							Log.put("IS_ENABLED", "1");
							DataAccess.Insert("exs_handle_log", Log);
						} catch (Exception e) {
							DataAccess.RoolbackTrans();
							LogUtil.printLog("日志写入失败："+e.getMessage(), Level.ERROR);
						}
//						addLogSuccessFile("activemq", "mqtomq "+SysUtility.getSysDate()+" "+bean.getFileName()+"\n");
					} catch (Exception e) {
						//The Consumer is closed
						LogUtil.printLog("ActiveMQToMQ：Active Received To Xml"+e.getMessage(), Level.ERROR);
						e.printStackTrace();
						Thread.sleep(10000);//报错了，先休眠10秒，防止错误日志太多
					} finally {
						AuxiliUtility.commitJmsSession(ProducerSession);
						if((i+1) == producerUrls.length) {
							AuxiliUtility.commitJmsSession(ConsumerSession);
						}
						SysUtility.ComitTrans();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				AuxiliUtility.closeJmsProducer(producer);
				AuxiliUtility.closeJmsSession(ProducerSession);
				AuxiliUtility.closeJmsConnection(ProducerConn);
				AuxiliUtility.closeJmsConnection(ConsumerConn);
			}
		}
	}

	public static boolean addLogSuccessFile(String fileFlag, String data) {
		return addLogFile(ExsConstants.appLogPath, SysUtility.getSysDateWithoutTime()+"-"+SysUtility.getSysDayWeek()+"-success-"+fileFlag+".log", data);
	}

	public static boolean addLogFailFile(String fileFlag, String data) {
		return addLogFile(ExsConstants.appLogPath, SysUtility.getSysDateWithoutTime()+"-"+SysUtility.getSysDayWeek()+"-fail-"+fileFlag+".log", data);
	}

	public static boolean addLogFile(String tempPath,String fileName,String data) {
		if(SysUtility.isEmpty(tempPath)){
			return false;
		}
		File file = new File(tempPath);
		if (!file.exists()) {
			if(file.mkdirs()){
				LogUtil.printLog("文件夹创建成功："+tempPath, Level.INFO);
			}
		}
		String path = tempPath + File.separator + fileName;

		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		byte tempByte[] = null;
		try {
			fo = new FileOutputStream(path, true);
			bo = new BufferedOutputStream(fo);
			tempByte = data.getBytes("UTF-8");
			bo.write(tempByte, 0, tempByte.length);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			try {
				bo.flush();
				bo.close();
				fo.close();
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		return false;
	}

	public static String getSubfolderPath(int subfolderNo,String SourcePath)throws Exception{
		if(SysUtility.isNotEmpty(subfolderNo) && subfolderNo > 0){
			for (int i = 1; i < subfolderNo+1; i++) {
				FileUtility.createFolder(SourcePath, "auto"+i);
			}
			String SubfolderName = "auto"+SysUtility.MathRandom(subfolderNo);
			return SourcePath+File.separator+SubfolderName;
		}else{
			return SourcePath;
		}
	}
}
