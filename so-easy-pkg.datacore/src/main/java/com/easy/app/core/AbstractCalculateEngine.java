package com.easy.app.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.session.SessionManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.entity.ActiveXmlBean;
import com.easy.app.entity.RabbitBasicProperties;
import com.easy.app.utility.ConvertUtility;
import com.easy.app.utility.ExsUtility;
import com.easy.app.utility.MQUtility;
import com.easy.app.utility.MutiUtility;
import com.easy.constants.Constants;
import com.easy.context.AppContext;
import com.easy.entity.ClusterBean;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.file.FileFilterHandle;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLHolder;
import com.easy.query.SQLMap;
import com.easy.query.SQLParser;
import com.easy.query.SimpleParamSetter;
import com.easy.query.SqlParamList;
import com.easy.query.SqlParameter;
import com.easy.rule.ElecDocsUtil;
import com.easy.rule.FieldCodeUtil;
import com.easy.rule.FieldFormatUtil;
import com.easy.security.AESGenerator;
import com.easy.utility.CacheUtility;
import com.easy.utility.EntityUtility;
import com.easy.utility.FileCompratorByLastModified;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.rabbitmq.client.BasicProperties;

public class AbstractCalculateEngine implements ICalculateEngine{
	public  final String DELETE_TEMP_ID = "DELETE FROM EXS_TEMP_ID WHERE MSG_TYPE IS NULL OR MSG_TYPE = ?";
	public  final String INSERT_TEMP_ID5 = "INSERT INTO EXS_TEMP_ID T(ID,ID1,ID2,ID3,ID4,ID5) VALUES(?,?,?,?,?,?)";
	public  final String UPDATE_HANDLE_SENDER = "UPDATE exs_handle_sender SET MSG_FLAG = ?,REC_VER=REC_VER+1 WHERE MSG_TYPE = ? AND MSG_NO = ?";
	public  final String UPDATE_HANDLE_RECEIVED = "UPDATE exs_handle_received SET MSG_FLAG = ? WHERE MSG_TYPE = ? AND MSG_NO = ?";
	public  final int singleProcessCount = 10000;//单线程模式：单任务的总报文处理量。

	public void DBToXml(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String DBToXmlSQL = "SELECT * FROM exs_config_dbtoxml WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ?";
		List DBToXmlList = SQLExecUtils.query4List(DBToXmlSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		for (int i = 0; i < DBToXmlList.size(); i++) {
			HashMap map = (HashMap)DBToXmlList.get(i);
			final String Indx = SysUtility.getMapField(map, "INDX");
			ServicesBean entity = new ServicesBean();
			entity.setServiceMode(bean.getServiceMode());
			setDBToXml2Bean(entity, map);
			setDBToXmlMap(entity,Indx,null);
			ExsUtility.InitServicesBeanPath(entity);
			ExsUtility.InitServicesBeanIP(entity);

			java.sql.Connection conn = null;
			try {
				conn = SysUtility.CreateDynamicProxoolConnection(entity.getIndx(), entity.getDbType(), entity.getDbDriverUrl(), entity.getDbUser(), entity.getDbPassword());
				SessionManager.setAttribute(entity.getIndx(), conn);
				if(SysUtility.isEmpty(conn)) {
					LogUtil.printLog("dbtoxml数据库连接初始化错误："+entity.getIndx()+"|"+entity.getDbType()+"|"+entity.getDbDriverUrl()+"|"+entity.getDbUser()+"|"+entity.getDbPassword(), LogUtil.ERROR);
					continue;
				}

				if(SysUtility.isNotEmpty(entity.getClassInvoke())){
					ExsUtility.MethodInvoke(entity.getClassInvoke(), entity);
				}else{
					ToXmlFromAny(entity, DataAccess);
				}

				if(SysUtility.IsOracleDB()) {
					DataAccess.ExecSQL("UPDATE exs_config_dbtoxml SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = ?",Indx);
				}else if(SysUtility.IsMySqlDB()) {
					DataAccess.ExecSQL("UPDATE exs_config_dbtoxml SET REC_VER=REC_VER+1,MODIFY_TIME=now() WHERE INDX = ?",Indx);
				}
				DataAccess.ComitTrans();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SysUtility.closeActiveCN(conn);
			}
		}
	}

	public void XmlToDB(ServicesBean bean,IDataAccess DataAccess, String messageType)throws Exception{
		List XmlToDBList = new ArrayList();
		if(SysUtility.isEmpty(messageType)){
			XmlToDBList = SQLExecUtils.query4List("SELECT * FROM exs_config_xmltodb WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ?", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getCurrentCronJobName());
				}
			});
		}else{
			XmlToDBList = SQLExecUtils.query4List("SELECT * FROM exs_config_xmltodb WHERE IS_ENABLED = '1' AND MESSAGE_TYPE = ?", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, messageType);
				}
			});
		}

		for (int i = 0; i < XmlToDBList.size(); i++) {
			HashMap map = (HashMap)XmlToDBList.get(i);
			final String Indx = SysUtility.getMapField(map, "INDX");

			ServicesBean entity = new ServicesBean();
			entity.setServiceMode(bean.getServiceMode());
			setXmlToDB2Bean(entity, map);
			setXmlToDBMap(entity, Indx);
			ExsUtility.InitServicesBeanPath(entity);
			ExsUtility.InitServicesBeanIP(entity);

			java.sql.Connection conn = null;
			try {
				conn = SysUtility.CreateDynamicProxoolConnection(entity.getIndx(), entity.getDbType(), entity.getDbDriverUrl(), entity.getDbUser(), entity.getDbPassword());
				SessionManager.setAttribute(entity.getIndx(), conn);
				if(SysUtility.isEmpty(conn)) {
					LogUtil.printLog("xmltodb数据库连接初始化错误："+entity.getIndx()+"|"+entity.getDbType()+"|"+entity.getDbDriverUrl()+"|"+entity.getDbUser()+"|"+entity.getDbPassword(), LogUtil.ERROR);
					continue;
				}

				if(SysUtility.isEmpty(entity.getClassInvoke())){
					XmlToDBForAny(entity, DataAccess);
				}else{
					ExsUtility.MethodInvoke(entity.getClassInvoke(), entity);
				}

				if(SysUtility.IsOracleDB()) {
					DataAccess.ExecSQL("UPDATE exs_config_xmltodb SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+Indx);
				}else if(SysUtility.IsMySqlDB()) {
					DataAccess.ExecSQL("UPDATE exs_config_xmltodb SET REC_VER=REC_VER+1,MODIFY_TIME=now() WHERE INDX = "+Indx);
				}
				bean.setResponseMessage(entity.getResponseMessage());
				DataAccess.ComitTrans();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SysUtility.closeActiveCN(conn);
			}
		}
		return;
	}
	
	public void UpdateToDBForXml(IDataAccess DataAccess,ServicesBean bean) throws Exception{
		String SourcePath = bean.getSourcePath();
		File files[] = new File(SourcePath).listFiles(new FileFilterHandle());
		Arrays.sort(files, new FileCompratorByLastModified());
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()){
				try {
					File file = files[i];
					bean.setSourcePath(SourcePath);
					bean.setFileName(file.getName());
					bean.setFile(file);
					UpdateToDBForAny(bean, DataAccess);
				} catch (LegendException e) {
					LogUtil.printLog("UpdateToDBForXml出错："+e.getMessage(), Level.ERROR);
				}
			}else{
				String folderName = files[i].getName();
				String tempSourcePath = ExsUtility.createFolder(bean.getSourcePath(), folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				Arrays.sort(files2, new FileCompratorByLastModified());
				for (int k = 0; k < files2.length; k++) {
					try {
						File file = files2[i];
						bean.setSourcePath(tempSourcePath);
						bean.setFileName(file.getName());
						bean.setFile(file);
						UpdateToDBForAny(bean, DataAccess);
					} catch (Exception e) {
						LogUtil.printLog("UpdateToDBForXml出错："+e.getMessage(), Level.ERROR);
					}
				}
			}
		}
	}

	public void UpdateToDBForDB(IDataAccess DataAccess,ServicesBean bean) throws Exception{
		String UpdateDBSQL = "SELECT * FROM EXS_CONFIG_UPDATEDB WHERE nvl(IS_ENABLED,'1') = '1' AND QUARTZ_NAME = ? ORDER BY SEQ_NO";
		List UpdateDBList = SQLExecUtils.query4List(UpdateDBSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		for (int i = 0; i < UpdateDBList.size(); i++) {
			HashMap map = (HashMap)UpdateDBList.get(i);
			final String Indx = SysUtility.getMapField(map, "INDX");
			String tableName = (String)map.get("TABLE_NAME");
			String messageType = (String)map.get("MESSAGE_TYPE");
			String updateCondition = (String)map.get("UPDATE_CONDITION");
			String updateSql = (String)map.get("UPDATE_SQL");
			if(SysUtility.isEmpty(updateCondition) && SysUtility.isEmpty(updateSql)) {
				continue;//配置错误
			}
			
			StringBuffer strSQL = new StringBuffer();
			strSQL.append("select * from "+tableName);
			strSQL.append(" where msg_flag = 0 and msg_type = '"+messageType+"'");
			if(SysUtility.isNotEmpty(updateCondition)) {
				strSQL.append(" and "+updateCondition);
			}
			strSQL.append(" and rownum < 5000");
			strSQL.append(" order by create_time");
			
			List lst = SQLExecUtils.query4List(strSQL.toString());
			for (int j = 0; j < lst.size(); j++) {
				HashMap data = (HashMap)lst.get(j);
				try {
					/*****************业务SQL执行 Begin**********************/
					SQLHolder holder = SQLParser.parse(updateSql, SysUtility.MapToJSONObject(data));
					String sql = holder.getSql();
					SqlParamList paramList = holder.getParamList();
					SQLExecUtils.executeUpdate(sql, new SimpleParamSetter(paramList));
					/*****************业务SQL执行 End**********************/
					DataAccess.ExecSQL("update "+tableName+" set msg_flag = 1 where INDX = ?",data.get("INDX"));
					DataAccess.ComitTrans();
				} catch (Exception e) {
					//e.printStackTrace();
					DataAccess.RoolbackTrans();
					DataAccess.ExecSQL("update "+tableName+" set msg_flag = 2 where INDX = ?",data.get("INDX"));
					DataAccess.ComitTrans();
				}
			}
			
			String UpdateSQL = "UPDATE EXS_CONFIG_UPDATEDB SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+Indx;
        	DataAccess.ExecSQL(UpdateSQL);
        	DataAccess.ComitTrans();
		}
		
	}
	
	public void XmlToDBForAny(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		if(ExsConstants.Local.equals(bean.getMqType())) {
			XmlToDBForLocal(bean, DataAccess);
		}else if(ExsConstants.ActiveMQ.equals(bean.getMqType())) {
			XmlToDBForActiveMQ(bean, DataAccess);
		}else if(ExsConstants.IBMMQ.equals(bean.getMqType())){
			XmlToDBForIBMMQ(bean, DataAccess);
		}else if(ExsConstants.RabbitMQ.equals(bean.getMqType())){
			XmlToDBForRabbitMQ(bean, DataAccess);
		}else if(ExsConstants.FTP.equals(bean.getMqType())){
			XmlToDBForFTP(bean, DataAccess);
		}else if(ExsConstants.HTTP.equals(bean.getMqType())){
			XmlToDBForHTTP(bean, DataAccess);
		}
		return;
	}
	
	
	public void XmlToDBForLocal(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		bean.setServiceMode(ExsConstants.LocalToDB);
		ExsUtility.InitServicesBeanPath(bean);
		//多线程
		if(MutiUtility.IsMutiProcess(bean)){
			MutiUtility.MutiProcessRequestDBLocal(bean, DataAccess);
			return;
		}
		//单线程
		int inProcessCount = 0;//正在处理的报文数量
		String SourcePath = bean.getSourcePath();
		File files[] = new File(SourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			bean.setSourcePath(SourcePath);
			if (!files[i].isDirectory()){
				inProcessCount++;
				if(inProcessCount > singleProcessCount){
					return;
				}
				try {
					File file = files[i];
					bean.setFileName(file.getName());
					bean.setFile(file);
					String processMsg = SaveToDBForAny(bean, DataAccess);
					if(SysUtility.isEmpty(processMsg)){
						FileUtility.copyFile(bean.getSourcePath(), bean.getTargetPath(), bean.getFileName());
					}else{
						FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
					}
				} catch (LegendException e) {
					LogUtil.printLog("SaveToDBForXml出错："+e.getMessage(), Level.ERROR);
				} finally {
					FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
					DataAccess.ComitTrans();
				}
			}else{
				String folderName = files[i].getName();
				String tempSourcePath = ExsUtility.createFolder(bean.getSourcePath(), folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					inProcessCount++;
					if(inProcessCount > singleProcessCount){
						return;
					}
					try {
						File file = files2[k];
						bean.setSourcePath(tempSourcePath);
						bean.setFileName(file.getName());
						bean.setFile(file);
						String processMsg = SaveToDBForAny(bean, DataAccess);
						if(SysUtility.isEmpty(processMsg)){
							FileUtility.copyFile(bean.getSourcePath(), bean.getTargetPath(), bean.getFileName());
						}else{
							FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
						}
					} catch (Exception e) {
						LogUtil.printLog("XmlToDBForLocal　Error："+e.getMessage(), Level.ERROR);
					} finally {
						FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
						DataAccess.ComitTrans();
					}
				}
			}
		}
		return;
	}
	public void XmlToDBForActiveMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		bean.setServiceMode(ExsConstants.ActiveMQToDB);
		if(SysUtility.isEmpty(bean.getConsumerUrl()) || SysUtility.isEmpty(bean.getSourceMq())) {
			LogUtil.printLog("exs_config_xmltodb配置错误:MqType="+bean.getMqType()+",ConsumerUrl="+bean.getConsumerUrl()+",SourceMq="+bean.getSourceMq(), Level.ERROR);
			return;
		}
		Connection connection = null;
		Session session = null;
		MessageConsumer consumer = null;
        try {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bean.getConsumerUser(), bean.getConsumerPwd(), bean.getConsumerUrl());
//			((ActiveMQConnectionFactory) connectionFactory).setTrustAllPackages(true);//这里设置为true 具体原因应该是安全设置问题
            connection = connectionFactory.createConnection();  
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(bean.getSourceMq());
            consumer = session.createConsumer(destination);
            while (true) {
				String FileData = "";
				Message msg = null;
				try {
            		msg = consumer.receive(10);//为0会导致无法读取第2个MQ
					if (null == msg) {
						break;
					}
					if (msg instanceof ObjectMessage){
						ObjectMessage message = (ObjectMessage)msg;
						ActiveXmlBean objBean = (ActiveXmlBean)message.getObject();
						FileData = objBean.getXmlData();
					}else if (msg instanceof MapMessage){
						MapMessage message = (MapMessage)msg;
						FileData = message.getStringProperty("FileName");
					}else if (msg instanceof TextMessage){
						TextMessage message = (TextMessage)msg;
						FileData = message.getText();
					}
					if(SysUtility.isNotEmpty(bean.getAesKey())){
						FileData = AESGenerator.decrypt(FileData, bean.getAesKey());
					}
					//数据入库
					bean.setXmlData(FileData);
					String processMsg = SaveToDBForAny(bean, DataAccess);
					if(SysUtility.isNotEmpty(processMsg)) {
						LogUtil.printLog("ActiveMQToDB数据入库失败:"+SysUtility.getCurrentCronJobName()+"|"+bean.getConsumerUrl()+bean.getSourceMq()+"|"+"|"+bean.getSerialNo()+"|"+processMsg, Level.WARN);
					}
				} catch (Exception e) {
					DataAccess.RoolbackTrans();
					LogUtil.printLog("ActiveMQToDB Error:"+SysUtility.getCurrentCronJobName()+"|"+bean.getConsumerUrl()+"|"+bean.getSourceMq()+"|"+bean.getSerialNo()+"|"+e.getMessage(), Level.ERROR);
					break;
				} finally {
					DataAccess.ComitTrans();
					SysUtility.ComitTrans(SysUtility.getCurrentDynamicConnection(bean.getIndx()));
					if(SysUtility.isNotEmpty(msg)){
						msg.acknowledge();
					}
//					LogUtil.printLog("数据入库成功:"+bean.getConsumerUrl()+"."+bean.getMessageType()+"."+bean.getSerialNo(), Level.WARN);
				}
            }
        } catch (Exception e) {  
            LogUtil.printLog("ActiveMQToDB Received Error:"+bean.getConsumerUrl()+"|"+bean.getSourceMq()+"|"+e.getMessage(), Level.ERROR);
            Thread.sleep(10000);
        } finally {
			MQUtility.closeJmsConsumer(consumer);
			MQUtility.closeJmsSession(session);
        	MQUtility.closeJmsConnection(connection);
        }
		return;
	}
	public void XmlToDBForIBMMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		//TODO
	}
	public void XmlToDBForRabbitMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		bean.setServiceMode(ExsConstants.RabbitMQToDB);
		if(SysUtility.isEmpty(bean.getConsumerUrl()) || SysUtility.isEmpty(bean.getSourceMq())) {
			LogUtil.printLog("exs_config_xmltodb配置错误:MqType="+bean.getMqType()+",ConsumerUrl="+bean.getConsumerUrl()+",SourceMq="+bean.getSourceMq(), Level.ERROR);
			return;
		}
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
				try {
					long start = System.currentTimeMillis();
					com.rabbitmq.client.QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					String FileData = new String(delivery.getBody());
					BasicProperties Properties = delivery.getProperties();
					Map map = Properties.getHeaders();
					//数据入库
					bean.setXmlData(FileData);
					String processMsg = SaveToDBForAny(bean, DataAccess);
					if(SysUtility.isNotEmpty(processMsg)) {
						LogUtil.printLog("RabbitMQToDB Error:"+processMsg, Level.ERROR);
					}
					LogUtil.printLog(bean.getFileName()+" cost:"+(System.currentTimeMillis()-start), Level.INFO);
				} catch (Exception e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
					break;
				} finally{
					DataAccess.ComitTrans();
				}
			}
		} catch (Exception e) {  
			LogUtil.printLog("Received Error:"+e.getMessage(), Level.ERROR);
		} finally {
			connection.close();
			channel.close();
		}
		return;
	}
	public void XmlToDBForFTP(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		bean.setServiceMode(ExsConstants.FTPToDB);
		if(SysUtility.isEmpty(bean.getConsumerUrl()) || SysUtility.isEmpty(bean.getSourceMq())) {
			LogUtil.printLog("exs_config_xmltodb配置错误:MqType="+bean.getMqType()+",ConsumerUrl="+bean.getConsumerUrl()+",SourceMq="+bean.getSourceMq(), Level.ERROR);
			return;
		}
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
							//数据入库
							bean.setXmlData(SysUtility.InputStream2String(is, bean.getEncoding()));
							String processMsg = SaveToDBForAny(bean, DataAccess);
							if(SysUtility.isNotEmpty(processMsg)) {
								LogUtil.printLog("FTPToDB Error:"+processMsg, Level.ERROR);
							}
						} catch (Exception e) {
							LogUtil.printLog("Ftp解析文件出错:"+fileName, Level.ERROR);
							break;
						} finally{
							DataAccess.ComitTrans();
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
		return;
	}

	public void XmlToDBForHTTP(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String processMsg = SaveToDBForAny(bean, DataAccess);
		if(SysUtility.isNotEmpty(processMsg)) {
			LogUtil.printLog("XmlToDBForHTTP Error:"+processMsg, Level.ERROR);
		}
		bean.setResponseMessage(processMsg);
		return;
	}

	static boolean middlewareConn = true;
	public void ToXmlFromAny(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		HashMap childsMap = new HashMap();
		List MainDatas = GetMainDatas(bean, DataAccess, childsMap);
		if(SysUtility.isEmpty(MainDatas)) {
			return;
		}
		if(SysUtility.OutDate5Minute()) {
			middlewareConn = true;//报错后，5分钟重连一次，防止频率过快
		}
		if(middlewareConn == false){
			return;
		}

		String[] producerUrls = bean.getProducerUrl().split(",");
		String[] ProducerUsers = SysUtility.isNotEmpty(bean.getProducerUser())?bean.getProducerUser().split(","):new String[] {};
		String[] ProducerPwds = SysUtility.isNotEmpty(bean.getProducerPwd())?bean.getProducerPwd().split(","):new String[] {};
		String[] TargetMqs = SysUtility.isNotEmpty(bean.getTargetMq())?bean.getTargetMq().split(","):new String[] {};
		for (int x = 0; x < producerUrls.length; x++) {
			boolean ack = (x+1)==producerUrls.length ? true : false;
			bean.setProducerUrl(producerUrls.length != 0?producerUrls[x]:"");
			bean.setProducerUser(ProducerUsers.length != 0?ProducerUsers[x]:"");
			bean.setProducerPwd(ProducerPwds.length != 0?ProducerPwds[x]:"");
			bean.setTargetMq(TargetMqs.length != 0?TargetMqs[x]:"");

			if(ExsConstants.Local.equalsIgnoreCase(bean.getMqType())) {
				ToXmlFromAnyByLocal(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.EdiTab.equals(bean.getMqType())){
				ToXmlFromAnyByLocalTab(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.ActiveMQ.equalsIgnoreCase(bean.getMqType())) {
				ToXmlFromAnyByActiveMQ(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.IBMMQ.equalsIgnoreCase(bean.getMqType())){
				ToXmlFromAnyByIBMMQ(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.RabbitMQ.equalsIgnoreCase(bean.getMqType())){
				ToXmlFromAnyByRabbitMQ(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.FTP.equalsIgnoreCase(bean.getMqType())){
				ToXmlFromAnyByFTP(MainDatas, childsMap, bean, DataAccess, ack);
			}else if(ExsConstants.HTTP.equalsIgnoreCase(bean.getMqType())){
				ToXmlFromAnyByHTTP(MainDatas, childsMap, bean, DataAccess, ack);
			}
		}
	}

	public void ToXmlFromAnyByLocal(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap MainData = (HashMap)MainDatas.get(i);
			String logDesc = "";
			String serialNo = (String)MainData.get(bean.getSerialName());

			String[] datas = ToXmlFromAnyGenerateData(MainData, childsMap, bean, DataAccess);
			String FileData = datas[0];
			String FileName = datas[1];

			ExsUtility.ToLocalFile(bean, FileData, FileName);

			/****4.serder flag*****/
			UpdateHandleSerder(bean.getMessageType(), serialNo, true);
			/****5.log*****/
			ExsUtility.addHandleLog(DataAccess, ExsConstants.DBToLocal, serialNo, bean.getMessageType(), "1", "文件生成成功");
		}
	}

	public void ToXmlFromAnyByLocalTab(List MainDatas, HashMap childsMap, ServicesBean entity1, IDataAccess DataAccess, boolean ack)throws Exception{
//		for (int i = 0; i < MainDatas.size(); i++) {
//			HashMap MainData = (HashMap)MainDatas.get(i);
//			String serialNo = (String)MainData.get(bean.getSerialName());
//
//			List tempDatas = new ArrayList();
//			tempDatas.add(MainData);
//			String xmlData = GetAnyXmlData(bean, tempDatas, childsMap);
//			DBToXmlToDB(bean.getServiceMode(), bean.getMessageType(), bean.getPartId(), xmlData, DataAccess);
//
//			/****4.serder flag*****/
//			UpdateHandleSerder(bean.getMessageType(), serialNo, true);
//			/****5.log*****/
//			ExsUtility.addHandleLog(DataAccess, ExsConstants.EdiTab, serialNo, bean.getMessageType(), "1", "edi数据传输成功");
//		}

		//1. source源数据库数据读取 List MainDatas

		//2. target目标库数据入库
		String messageType = entity1.getMessageType();
		String XmlToDBSQL = "select * from exs_config_xmltodb where is_enabled = '1' and message_type = ? ";
		Map map = SQLExecUtils.query4Map(XmlToDBSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, messageType);
			}
		});
		if(SysUtility.isEmpty(map)){
			LogUtil.printLog("xmltodb配置无效,MessageType="+messageType, Level.ERROR);
			return;
		}
		final String Indx = SysUtility.getMapField(map, "INDX");
		ServicesBean entity2 = new ServicesBean();
		setDBToXml2Bean(entity2, map);
		setXmlToDBMap(entity2, Indx);

		java.sql.Connection targetConn = null;
		try {
			targetConn = SysUtility.getCurrentDynamicConnection(entity2.getIndx(), entity2.getDbType(), entity2.getDbDriverUrl(), entity2.getDbUser(), entity2.getDbPassword());
			if(SysUtility.isEmpty(targetConn)) {
				return;
			}
			for (int i = 0; i < MainDatas.size(); i++) {
				try {
					HashMap MainData = (HashMap)MainDatas.get(i);

					String SerialNo = (String)MainData.get(entity1.getSerialName());
					entity1.setSerialNo(SerialNo);

					List tempDatas = new ArrayList();
					tempDatas.add(MainData);
					String xmlData = GetAnyXmlData(entity2, tempDatas, childsMap);
					entity2.setXmlData(xmlData);
					InputStream is = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
					//数据入库
					HashMap<String,String> rtMap = SaveToDBAnyXmlCore(entity2, is, DataAccess);
					String processMsg = rtMap.get("processMsg");

					if(SysUtility.isNotEmpty(processMsg)){
						DataAccess.RoolbackTrans();
					}
					java.sql.Connection conn1 = SysUtility.getCurrentDynamicConnection(entity1.getIndx());
					UpdateHandleSerder(conn1,  entity1.getMessageType(), SerialNo,true);
					ExsUtility.AddLogSuccess(DataAccess, entity1, "数据库直连处理成功");
				} catch (Exception e) {
					LogUtil.printLog("数据库直连处理失败:"+entity1.getMessageType()+","+entity1.getSerialNo()+" "+e.getMessage(), Level.ERROR);
					ExsUtility.AddLogFailDesc(DataAccess, entity1, "数据库直连处理失败");
				} finally {
					SysUtility.ComitTrans(SysUtility.getCurrentConnection());
					SysUtility.ComitTrans(SysUtility.getCurrentDynamicConnection(entity1.getIndx()));
					SysUtility.ComitTrans(SysUtility.getCurrentDynamicConnection(entity2.getIndx()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SysUtility.closeActiveCN(targetConn);
		}

	}

	public void ToXmlFromAnyByActiveMQ(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
		Connection ProducerConn = null;
		Session ProducerSession = null;
		MessageProducer producer = null;

		//连接ActiveMQ
		try {
			ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(bean.getProducerUser(),bean.getProducerPwd(),bean.getProducerUrl());
			ProducerConn = ProducerConnFactory.createConnection();
			ProducerConn.start();
			ProducerSession = ProducerConn.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			Destination ProducerDes = ProducerSession.createQueue(bean.getTargetMq());
			producer = ProducerSession.createProducer(ProducerDes);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			for (int i = 0; i < MainDatas.size(); i++) {
				HashMap MainData = (HashMap)MainDatas.get(i);
				String serialNo = (String)MainData.get(bean.getSerialName());
				bean.setSerialNo(serialNo);

				String[] datas = ToXmlFromAnyGenerateData(MainData, childsMap, bean, DataAccess);
				String FileData = datas[0];
				String FileName = datas[1];

				if("1".equals(bean.getActiveMqMode())){//ObjectMessage
					ActiveXmlBean objBean = new ActiveXmlBean();
					objBean.setFileName(FileName);
					if(SysUtility.isNotEmpty(bean.getAesKey())){
						objBean.setXmlData(AESGenerator.encrypt(FileData, bean.getAesKey()));
					}else{
						objBean.setXmlData(FileData);
					}
					ObjectMessage message = ProducerSession.createObjectMessage(objBean);
					message.setStringProperty("msg_no", bean.getSerialNo());
					message.setStringProperty("msg_type", bean.getMessageType());
					producer.send(message);
				}else if("2".equals(bean.getActiveMqMode())){//MapMessage
					MapMessage message = ProducerSession.createMapMessage();
					message.setStringProperty("FileName",FileName);
					message.setStringProperty("FileData",FileData);
					message.setStringProperty("msg_no", bean.getSerialNo());
					message.setStringProperty("msg_type", bean.getMessageType());
					producer.send(message);
				}else{//TextMessage
					TextMessage message = ProducerSession.createTextMessage(FileData);
					message.setStringProperty("FileName",FileName);
					message.setStringProperty("msg_no", bean.getSerialNo());
					message.setStringProperty("msg_type", bean.getMessageType());
					producer.send(message);
				}
				/****4.serder flag*****/
				if(ack){
					java.sql.Connection conn = SysUtility.getCurrentDynamicConnection(bean.getIndx());
					UpdateHandleSerder(conn, bean.getMessageType(), serialNo, true);
					SysUtility.ComitTrans(conn);
				}
				/****5.log*****/
				ExsUtility.addHandleLog(DataAccess, ExsConstants.DBToActiveMQ, serialNo, bean.getMessageType(), "1", "数据发送成功："+bean.getProducerUrl());
				/****5.提交事务*****/
				SysUtility.ComitTrans();
				LogUtil.printLog("数据发送成功:"+bean.getProducerUrl()+"."+bean.getTargetMq()+"."+bean.getSerialNo(), Level.WARN);
			}
		} catch (Exception e) {
			LogUtil.printLog("ActiveMQ 连接失败 "+bean.getIndx()+"|"+bean.getProducerUrl()+"|"+bean.getTargetMq()+"|"+e.getMessage(), Level.ERROR);
			middlewareConn = false;
		} finally {
			MQUtility.closeJmsProducer(producer);
			MQUtility.closeJmsSession(ProducerSession);
			MQUtility.closeJmsConnection(ProducerConn);
		}
	}

	public void ToXmlFromAnyByIBMMQ(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
		//TODO
	}

	public void ToXmlFromAnyByRabbitMQ(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
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

			for (int i = 0; i < MainDatas.size(); i++) {
				HashMap MainData = (HashMap)MainDatas.get(i);
				String serialNo = (String)MainData.get(bean.getSerialName());

				String[] datas = ToXmlFromAnyGenerateData(MainData, childsMap, bean, DataAccess);
				String FileData = datas[0];
				String FileName = datas[1];

				RabbitBasicProperties Properties = new RabbitBasicProperties();
				Properties.setMessageId(SysUtility.GetUUID());
				Map map = new HashMap();
				map.put("FileName", (String)bean.getFileName());
				Properties.setHeaders(map);
				channel.basicPublish("", bean.getTargetMq(), Properties, FileData.getBytes());

				/****4.serder flag*****/
				UpdateHandleSerder(bean.getMessageType(), serialNo, true);
				/****5.log*****/
				ExsUtility.addHandleLog(DataAccess, ExsConstants.DBToRabbitMQ, serialNo, bean.getMessageType(), "1", "数据发送成功");
			}
		} catch (Exception e) {
			LogUtil.printLog("RabbitMQ 连接失败"+e.getMessage(), Level.ERROR);
			middlewareConn = false;
		} finally{
			MQUtility.closeRabbitmqChannel(channel);
			MQUtility.closeRabbitmqConnection(connection);
		}
	}

	public void ToXmlFromAnyByFTP(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
		FTPClient ftp = null;
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
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();// 连接FTP服务器失败
				LogUtil.printLog("FTP服务器连接失败:"+ftp_url+":"+ftp_port+":"+ftp_username+":"+ftp_pwd,LogUtil.ERROR);
				middlewareConn = false;
			}

			for (int i = 0; i < MainDatas.size(); i++) {
				HashMap MainData = (HashMap)MainDatas.get(i);
				String serialNo = (String)MainData.get(bean.getSerialName());

				String[] datas = ToXmlFromAnyGenerateData(MainData, childsMap, bean, DataAccess);
				String FileData = datas[0];
				String FileName = datas[1];
				//为Ftp时，TargetMq存储的是目标报文路径
				FileUtility.createFile(bean.getTargetMq(), FileName, FileData, bean.getEncoding());
				/****4.serder flag*****/
				UpdateHandleSerder(bean.getMessageType(), serialNo, true);
				/****5.log*****/
				ExsUtility.addHandleLog(DataAccess, ExsConstants.DBToFTP, serialNo, bean.getMessageType(), "1", "数据发送成功");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			MQUtility.closeFtpConnect(ftp);
		}

	}

	public void ToXmlFromAnyByHTTP(List MainDatas, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess, boolean ack)throws Exception{
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap MainData = (HashMap)MainDatas.get(i);
			String serialNo = (String)MainData.get(bean.getSerialName());

			String[] datas = ToXmlFromAnyGenerateData(MainData, childsMap, bean, DataAccess);
			String FileData = datas[0];
			String FileName = datas[1];

			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("FileData", FileData);
			postParam.put("FileName", FileName);
			Request request = new Request(postParam, bean.getProducerUrl());
			request.setDataType(ProtocolConstant.DataType.INPUTSTREAM.getValue());
			Response response = ProtocolUtil.execute(request);
			String result = new String(response.getByteResult(),"UTF-8");
			JSONObject rt = new JSONObject(result);
			String msgStatus = SysUtility.getJsonField(rt, "msg_status");
			String msgDesc = SysUtility.getJsonField(rt, "msg_desc");

			/****4.serder flag*****/
			UpdateHandleSerder(bean.getMessageType(), serialNo, true);
			/****5.log*****/
			ExsUtility.addHandleLog(DataAccess, ExsConstants.DBToFTP, serialNo, bean.getMessageType(), msgStatus, msgDesc);
		}
	}

	public String[] ToXmlFromAnyGenerateData(HashMap MainData, HashMap childsMap, ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String FileData = "";
		String FileName = "";
		if(ExsConstants.AnyXml.equals(bean.getDataType())){//AnyXml
			List tempDatas = new ArrayList();
			tempDatas.add(MainData);
			FileData = GetAnyXmlData(bean, tempDatas, childsMap);
			FileName = ExsUtility.getFileName(bean,MainData,".xml");
		}else if(ExsConstants.AnyJson.equals(bean.getDataType())){
			List tempDatas = new ArrayList();
			tempDatas.add(MainData);
			FileData = GetAnyJsonData(bean, tempDatas, childsMap);
			FileName = ExsUtility.getFileName(bean,MainData,".xml");
		}else if(ExsConstants.EdiFact.equals(bean.getDataType())){
			List tempDatas = new ArrayList();
			tempDatas.add(MainData);
			String XmlData = GetAnyXmlData(bean, tempDatas, childsMap);
			HashMap parentMap = new HashMap();
			List list = bean.getListSql();
			for (int j = 0; j < list.size(); j++) {
				HashMap map = (HashMap)list.get(j);
				parentMap.put(map.get("XML_DOCUMENT_NAME"), map.get("XML_PARENT_DOCUMENT"));
			}
			FileData = ConvertUtility.xmlToEdiFact(XmlData, "UTF-8");
			FileName = ExsUtility.getFileName(bean,MainData,".edi");
		}
		return new String[]{FileData, FileName};
	}

	public void DBToXmlToDB(String ServiceMode,final String MessageType,final String PartId,String XmlData,IDataAccess DataAccess)throws Exception{
		String XmlToDBSQL = "select * from exs_config_xmltodb where nvl(is_enabled,'1') = '1' and message_type = ? ";
		if(SysUtility.isNotEmpty(PartId)){
			XmlToDBSQL = XmlToDBSQL + "and part_id = ?";
		}
		Map map = SQLExecUtils.query4Map(XmlToDBSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, MessageType);
				if(SysUtility.isNotEmpty(PartId)){
					ps.setString(2, PartId);
				}
			}
		});
		if(SysUtility.isEmpty(map)){
			LogUtil.printLog("xmltodb配置无效,MessageType="+MessageType+",PartId="+PartId, Level.ERROR);
		}else{
			ServicesBean tempBean = new ServicesBean();
			EntityUtility.hashMapToEntity(tempBean, map);
			
			final String Indx = SysUtility.getMapField(map, "INDX");
			String SelectSQL = "select s.* from exs_config_xmltodb_map s where nvl(is_enabled,'1') = '1' and p_indx = ? order by seq_no,indx";
			List listSql = SQLExecUtils.query4List(SelectSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setInt(1, Integer.parseInt(Indx));
				}
			});
			tempBean.setListSql(listSql);
			tempBean.setXmlData(XmlData);
			InputStream is = new ByteArrayInputStream(XmlData.getBytes("UTF-8"));
			HashMap<String,String> rtMap = SaveToDBAnyXmlCore(tempBean, is, DataAccess);
			String processMsg = rtMap.get("processMsg");
			
			if(SysUtility.isNotEmpty(processMsg)){
				DataAccess.RoolbackTrans();
			}
		}
	}

	public  void UpdateHandleSerder(String messageType, String serialNo, boolean flag)throws Exception{
		UpdateHandleSerder(SysUtility.getCurrentConnection(), messageType, serialNo,  flag);
	}

	public  void UpdateHandleSerder(java.sql.Connection conn, String messageType, String serialNo,boolean flag)throws Exception{
		if(flag){
			SQLExecUtils.executeUpdate(conn, UPDATE_HANDLE_SENDER, new String[]{"1",messageType,serialNo});
		}else{
			SQLExecUtils.executeUpdate(conn, UPDATE_HANDLE_SENDER, new String[]{"2",messageType,serialNo});
		}
	}
	
	public  String SaveToDBForAny(ServicesBean bean,IDataAccess DataAccess) throws Exception{
		String ErrorMsg = "";
		try {
			LogUtil.printLog("文件名："+bean.getFileName()+" "+bean.getServiceMode()+" SaveToDBForAny解析开始..", Level.INFO);
			DataAccess.BeginTrans();
			HashMap<String,String> rtMap = new HashMap<String,String>();
			
			InputStream is = null;
			if(ExsConstants.AnyXml.equals(bean.getDataType())){
				is = ExsUtility.GetExsXmlIs(bean);
			}else if(ExsConstants.AnyJson.equals(bean.getDataType())){
				bean.setXmlData(ConvertUtility.jsonToXml(bean.getXmlData(), bean.getEncoding()));
				is = ExsUtility.GetExsXmlIs(bean);
			}else if(ExsConstants.AnsiX12.equals(bean.getDataType())){
				
			}else if(ExsConstants.EdiFact.equals(bean.getDataType())){
				InputStream ediIs = ExsUtility.GetExsXmlIs(bean);
				String XmlData = ConvertUtility.ediFactToXml(ediIs,bean.getMessageType());
				is = new ByteArrayInputStream(XmlData.getBytes(bean.getEncoding()));
			}
			rtMap = SaveToDBAnyXmlCore(bean, is, DataAccess);
			ErrorMsg = rtMap.get("processMsg");
			/******************处理校验结果，通过验证将插入数据库*************************/
			if(SysUtility.isNotEmpty(ErrorMsg)){
				DataAccess.RoolbackTrans();
				SysUtility.RoolbackTrans(SysUtility.getCurrentDynamicConnection(bean.getIndx()));
			}
			ExsUtility.InsertReceived(DataAccess, bean,SysUtility.isEmpty(ErrorMsg)?"S":"F", SysUtility.isEmpty(ErrorMsg)?"入库成功":ErrorMsg);
			ExsUtility.AddLogSuccess(DataAccess, bean, SysUtility.isEmpty(ErrorMsg)?"入库成功":ErrorMsg);
			LogUtil.printLog("文件名："+bean.getFileName()+" "+bean.getServiceMode()+" SaveToDBForAny解析结束..", Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg = "SaveToDBForAny Error:message_type="+bean.getMessageType()+",Message="+e.getMessage();
			DataAccess.RoolbackTrans();
			SysUtility.RoolbackTrans(SysUtility.getCurrentDynamicConnection(bean.getIndx()));
			ExsUtility.AddLogFailDesc(DataAccess, bean, "入库失败");
		}
		bean.setResponseMessage(ErrorMsg);
		return ErrorMsg;
	}
	
	public  HashMap<String,String> SaveToDBAnyXmlCore(ServicesBean bean,InputStream is,IDataAccess DataAccess) throws Exception{
		String processMsg = "";
		Datas datas = new Datas();
		
		try {
			HashMap hmSourceData = new HashMap();
			List listSql = bean.getListSql();
			SetBeanField(bean,listSql);
			String[] RootNames = bean.getRootNames().split(",");
			String RootName = RootNames[0];
			String[] DBTableNames = bean.getDbTableNames().split(",");
			String[] IndxNames = bean.getIndxName().split(",");
			try {
				/*********1.数据预处理：构造JavaBean对象：Datas************/
				String XmlRootName = FileUtility.GetExsFileRootName(bean.getFile());
				if(SysUtility.isEmpty(XmlRootName)){
					XmlRootName = FileUtility.GetExsFileRootName(bean.getXmlData());
				}
				hmSourceData = FileUtility.xmlParse(is,XmlRootName);
				XmlToDatas(bean, datas, hmSourceData);
				bean.setSerialNo(datas.GetTableValue(RootName, bean.getSerialName()));//用于2、3、4校验不通过时预先赋值
				bean.setMessageSource(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE));//用于2、3、4校验不通过时预先赋值
				bean.setMessageDest(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST));//用于2、3、4校验不通过时预先赋值
				/*********2.嵌入报文处理的反射方法调用************/
				processMsg = ExsUtility.MethodInvoke(bean.getPreMethodInvoke(), bean, datas);
				if(SysUtility.isNotEmpty(processMsg)){
					return ExsUtility.ReturnProcessMsg(processMsg, "");
				}
				/*********3.字段校验***********************/
				processMsg = FieldFormatUtil.checkFieldCode(datas, bean.getPointCode());
				if(SysUtility.isNotEmpty(processMsg)){
					return ExsUtility.ReturnProcessMsg(processMsg, "");
				}
				/*********4.code校验***********************/
				processMsg = FieldCodeUtil.checkFieldCode(datas, bean.getPointCode());
				if(SysUtility.isNotEmpty(processMsg)){
					return ExsUtility.ReturnProcessMsg(processMsg, "");
				}
				/*********5.设置ServicesBean参数：流水号值,发送方代码,接收方代码***********************/
				RootNames = bean.getRootNames().split(",");
				RootName = RootNames[0];
				DBTableNames = bean.getDbTableNames().split(",");
				IndxNames = bean.getIndxName().split(",");
				ExsUtility.SetRootField(bean, RootName, datas);
				bean.setSerialNo(datas.GetTableValue(RootName, bean.getSerialName()));
				bean.setMessageSource(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE));
				bean.setMessageDest(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST));
				bean.setIndxNameValue(datas.GetTableValue(RootName, bean.getIndxName()));
				bean.setChildFolder(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE));
			} catch (Exception e) {
				e.printStackTrace();
				DataAccess.RoolbackTrans();
				LogUtil.printLog("报文入库失败："+e.getMessage(), Level.ERROR);
				processMsg = "报文入库失败："+e.getMessage();
				return ExsUtility.ReturnProcessMsg(processMsg, "");
			} finally{
				/*********6.新增/更新主表 反射方法***********************/
				bean.setResponseMessage(processMsg);
				processMsg = ExsUtility.MethodInvoke(bean.getRootMethodInvoke(), bean, datas, RootNames[0]);
				if(SysUtility.isNotEmpty(processMsg)){
					return ExsUtility.ReturnProcessMsg(processMsg, "");
				}
			}
			/*********7.新增/更新主表信息***********************/
			if(bean.isExistsReturn()){
				return ExsUtility.ReturnProcessMsg(processMsg, "");
			}else if(SysUtility.isNotEmpty(bean.getUpdateSql())){//修改逻辑
				boolean UpdateResult = ExecUpdateData(DataAccess, bean.getIndx(), bean.getUpdateSql(), datas.GetTable(RootName));
				if(!UpdateResult){
					return ExsUtility.ReturnProcessMsg("Update失败："+bean.getUpdateSql(), "");
				}
				DeleteChildDB(bean.getIndx(), RootNames, listSql, datas, hmSourceData, DBTableNames, IndxNames);
			}else if(ExistsDataDB(bean.getIndx(), RootNames, datas, DBTableNames, IndxNames)){//新增逻辑
				DeleteMainDB(bean.getIndx(), bean, DBTableNames[0]);
				DeleteChildDB(bean.getIndx(), RootNames, listSql, datas, hmSourceData, DBTableNames, IndxNames);
				/*********8.新增主表信息***********************/
				datas.InsertDB(DataAccess, SysUtility.getCurrentDynamicConnection(bean.getIndx()), RootNames[0], DBTableNames[0],IndxNames[0]);
			}else {
				datas.InsertDB(DataAccess, SysUtility.getCurrentDynamicConnection(bean.getIndx()), RootNames[0], DBTableNames[0],IndxNames[0]);
			}
			/*********9.插入子表信息，并调用反射方法***********************/
			for (int i = 1; i < RootNames.length; i++) {
				processMsg = ExsUtility.MethodInvoke(bean.getChildMethodInvoke(), bean, datas, RootNames[i]);
				if(SysUtility.isNotEmpty(processMsg)){
					return ExsUtility.ReturnProcessMsg(processMsg, "");
				}
				datas.InsertDB(DataAccess, SysUtility.getCurrentDynamicConnection(bean.getIndx()), RootNames[i], DBTableNames[i],IndxNames[i]);
			}
			LogUtil.printLog("数据入库成功:"+bean.getConsumerUrl()+"."+bean.getSourceMq()+"."+bean.getSerialNo(), Level.WARN);
		} catch (Exception e) {
			e.printStackTrace();
			DataAccess.RoolbackTrans();
			LogUtil.printLog("报文入库失败："+e.getMessage(), Level.ERROR);
			processMsg = "报文入库失败："+e.getMessage();
			return ExsUtility.ReturnProcessMsg(processMsg, "");
		} finally{
			bean.setResponseMessage(processMsg);
		}
		return ExsUtility.ReturnProcessMsg(processMsg, "");
	}
	
	public void SetBeanField(ServicesBean bean,List listSql) throws LegendException{
		StringBuffer strXmlDocumentName = new StringBuffer();
		StringBuffer strTableName = new StringBuffer();
		StringBuffer strIndxName = new StringBuffer();
		for (int i = 0; i < listSql.size(); i++) {
			HashMap temp = (HashMap)listSql.get(i);
			strXmlDocumentName.append(SysUtility.getMapField(temp, "XML_DOCUMENT_NAME")).append(",");
			strTableName.append(temp.get("TABLE_NAME")).append(",");
			strIndxName.append(temp.get("INDX_NAME")).append(",");
		}
		if(SysUtility.isNotEmpty(strXmlDocumentName)){
			bean.setRootNames(strXmlDocumentName.toString());
		}
		if(SysUtility.isNotEmpty(strTableName)){
			bean.setDbTableNames(strTableName.toString());
		}
		if(SysUtility.isNotEmpty(strIndxName)){
			bean.setIndxName(strIndxName.toString());
		}
	}

	public  boolean ExistsDataDB(String uuid, String[] RootNames,Datas datas,String[] DBTableNames,String[] IndxNames) throws LegendException{
		for (int i = 0; i < RootNames.length; i++) {
			String RootName = RootNames[i];
			String IndxName = IndxNames[i];
			for (int j = 0; j < datas.GetTableRows(RootName); j++) {
				final String selId = datas.GetTableValue(RootName, IndxName,j);
				String selSQL = "select 0 from "+DBTableNames[i]+" where "+IndxName+" = ? ";
				String rt = SQLExecUtils.query4String(SysUtility.getCurrentDynamicConnection(uuid), selSQL,new Callback() {
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, selId);
					}
				});
				if(SysUtility.isNotEmpty(rt)){
					return true;
				}
			}
		}
		return false;
	}

	public  void DeleteMainDB(String uuid, ServicesBean bean,String MainTableName) throws LegendException{
		String delSQL = "delete from "+MainTableName+" where "+bean.getSerialName()+" = '"+bean.getSerialNo()+"'";
		SQLExecUtils.executeUpdate(SysUtility.getCurrentDynamicConnection(uuid), delSQL, new String[] {});
	}

	public  void DeleteChildDB(String uuid, String[] RootNames,List listSql,Datas datas,HashMap hmSourceData,String[] DBTableNames,String[] IndxNames) throws LegendException{
		for (int i = 1; i < RootNames.length; i++) {
			for (int j = 0; j < datas.GetTableRows(RootNames[i]); j++) {
				String delId = datas.GetTableValue(RootNames[i], IndxNames[i],j);
				String delSQL = "delete from "+DBTableNames[i]+" where "+IndxNames[i]+" = '"+delId+"'";
				SQLExecUtils.executeUpdate(SysUtility.getCurrentDynamicConnection(uuid), delSQL, new String[] {});
			}
		}
	}
	
	
	
	
	public void UpdateToDBForAny(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String FileName = bean.getFileName();
		String XmlData = bean.getXmlData();
		try {
			LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" UpdateToDBForAny解析开始..", Level.INFO);
			DataAccess.BeginTrans();
			
			InputStream is = null;
			if(SysUtility.isNotEmpty(bean.getFile())){
				is = new FileInputStream(bean.getFile());
			}
			if(SysUtility.isEmpty(is)){
				is = new ByteArrayInputStream(XmlData.getBytes("UTF-8"));
			}
			HashMap hmSourceData = FileUtility.xmlParse(is,ExsConstants.RequestMessage);
			HashMap rmHashMap = (HashMap)((List)hmSourceData.get(ExsConstants.RequestMessage)).get(0);
			HashMap MessageHead = (HashMap)((List)rmHashMap.get(ExsConstants.MessageHead)).get(0);
			List MessageBody = (List)rmHashMap.get(ExsConstants.MessageBody);
			HashMap documentData = (HashMap)MessageBody.get(0);
			/******获取主表的数据对象*******/
			bean.setChildFolder((String)MessageHead.get(ExsConstants.MESSAGE_SOURCE));
			bean.setMessageDestName((String)MessageHead.get(ExsConstants.MESSAGE_SOURCE));
			HashMap saveTable = getSaveTableMap(documentData, bean.getXmlDocument(), bean.getXmlHead());
			String messageType = (String)MessageHead.get("MESSAGE_TYPE");
			JSONObject root = (JSONObject)saveTable.get(bean.getXmlHead());
			UpdateReceived(bean, DataAccess,messageType, root);
			bean.setSerialNo(SysUtility.getJsonField(root, bean.getSerialName()));
			bean.setLogPath(bean.getSourcePath());
			ExsUtility.AddLogSuccess(DataAccess, bean, "状态更新成功！");
			DataAccess.ComitTrans();
			//删除本地文件
			if(SysUtility.isNotEmpty(bean.getSourcePath())){
				FileUtility.deleteFile(bean.getSourcePath(), FileName);
			}
			//备份本地文件
			if(SysUtility.isEmpty(XmlData)){
				XmlData = ExsUtility.parseRequestXml(MessageHead, documentData, bean.getXmlDocument(),bean.getEncoding());
			}
			if(SysUtility.isNotEmpty(bean.getTargetPath())){
				FileUtility.createFile(bean.getTargetPath(), FileName, XmlData);
			}
			LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" UpdateToDBForAny解析结束..", Level.INFO);
		} catch (LegendException e) {
			LogUtil.printLog("文件名："+FileName+" UpdateToDBForAny解析出错.."+e.getMessage(), Level.ERROR);
			SysUtility.RoolbackTrans();
			if(SysUtility.isNotEmpty(bean.getErrorPath())){
				if(SysUtility.isEmpty(XmlData)){
					FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(),bean.getFileName());
				}else{
					FileUtility.createFile(bean.getErrorPath(), FileName, XmlData);
				}
			}
			//删除本地文件
			if(SysUtility.isNotEmpty(bean.getSourcePath())){
				FileUtility.deleteFile(bean.getSourcePath(), FileName);
			}
			ExsUtility.ServicesErrorNotice("文件名："+FileName+" UpdateToDBForAny解析出错..", "ExceptionMessage:"+e.getMessage(), FileName);
			ExsUtility.AddLogFail(DataAccess, bean, e);
			DataAccess.ComitTrans();
		}
	}
	
	public void ElecDocsForAny(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String FileName = bean.getFileName();
		String XmlData = bean.getXmlData();
		String rtMsg = "";
		try {
			LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" ElecDocsForAny解析开始..", Level.INFO);
			DataAccess.BeginTrans();
			InputStream is = null;
			if(SysUtility.isNotEmpty(bean.getFile())){
				is = new FileInputStream(bean.getFile());
			}
			if(SysUtility.isEmpty(is)){
				is = new ByteArrayInputStream(XmlData.getBytes("UTF-8"));
			}
			HashMap hmSourceData = FileUtility.xmlParse(is,ExsConstants.RequestMessage);
			HashMap rmHashMap = (HashMap)((List)hmSourceData.get(ExsConstants.RequestMessage)).get(0);
			HashMap MessageHead = (HashMap)((List)rmHashMap.get(ExsConstants.MessageHead)).get(0);
			List MessageBody = (List)rmHashMap.get(ExsConstants.MessageBody);
			HashMap documentData = (HashMap)MessageBody.get(0);
			HashMap CheckMap = FileUtility.parseHashMap(documentData, bean.getXmlDocument(), true);
			HashMap headMap = (HashMap)((List)CheckMap.get(bean.getXmlHead())).get(0);//表头
			/**********操作对象新增值***********/
			bean.setSerialNo((String)headMap.get(bean.getSerialName()));
			bean.setMessageType((String)MessageHead.get(ExsConstants.MESSAGE_TYPE));
			/**********字段规则校验***********/
			if(SysUtility.isEmpty(rtMsg) && SysUtility.isNotEmpty(bean.getFieldPointCode())){
				String FieldPointCode = ExsUtility.GetFormatPointCode(bean.getFieldPointCode(), headMap);
				rtMsg = FieldFormatUtil.checkField(CheckMap, FieldPointCode);
			}
			/**********电子审单规则校验***********/
			if(SysUtility.isEmpty(rtMsg) && SysUtility.isNotEmpty(bean.getMappingPointCode())){// && SysUtility.isNotEmpty(bean.getRulePointCode())
				String MappingPointCode = ExsUtility.GetMappingPointCode(bean.getMappingPointCode(), headMap);
				ElecDocsUtil.checkRule(CheckMap, MappingPointCode, MappingPointCode);
			}
			/********重新生成Xml******/
			XmlData = ExsUtility.parseRequestXml(MessageHead, documentData, bean.getXmlDocument(),bean.getEncoding());
			if(SysUtility.isEmpty(rtMsg)){
				FileUtility.createFile(bean.getPassPath(), FileName, XmlData);
				ExsUtility.AddLogSuccess(DataAccess, bean, "审单成功！");
			}else{
				ExsUtility.InsertReceivedFail(DataAccess, bean, rtMsg);
				ExsUtility.AddLogFailDesc(DataAccess, bean, rtMsg);
			}
			FileUtility.createFile(bean.getTargetPath(), FileName, XmlData);
			DataAccess.ComitTrans();
			LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" ElecDocsForAny解析结束..", Level.INFO);
		} catch (LegendException e) {
			LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" ElecDocsForAny解析出错.."+e.getMessage(), Level.ERROR);
			try {
				DataAccess.RoolbackTrans();
			} catch (LegendException e1) {
				LogUtil.printLog("文件名："+FileName+" "+bean.getServiceMode()+" 回滚事务出错！"+e1.getMessage(), Level.ERROR);
			}
			if(SysUtility.isNotEmpty(bean.getErrorPath())){
				if(SysUtility.isEmpty(XmlData)){
					FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(),bean.getFileName());
				}else{
					FileUtility.createFile(bean.getErrorPath(), FileName, XmlData);
				}
			}
			ExsUtility.ServicesErrorNotice("文件名："+FileName+" "+bean.getServiceMode()+" ElecDocsForAny解析出错..", "ExceptionMessage:"+e.getMessage(), FileName);
			ExsUtility.AddLogFail(DataAccess, bean, e);
		} finally{
			DataAccess.ComitTrans();
			FileUtility.deleteFile(bean.getSourcePath(), FileName);//删除本地文件
		}
	}
	
	
	
	
	
	

	
	
	public void UpdateToDBForDB(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String[] searchIds = bean.getSearchId().split(",");
		String MainSQL = SQLMap.getSelect(searchIds[0]);
		JSONObject param = bean.getSearchParam();
		SQLHolder holder = SQLParser.parse(MainSQL, param);
		List mainTable = SQLExecUtils.query4List(holder.getSql(),new SimpleParamSetter(holder.getParamList()));
		if(SysUtility.isEmpty(mainTable)){
			return;
		}
		for (int i = 0; i < mainTable.size(); i++) {
			HashMap tempMap = (HashMap)mainTable.get(i);
			bean.setSerialNo((String)tempMap.get(bean.getSerialName()));
			try {
				//更新业务表
				UpdateReceived(DataAccess, tempMap);
				//更新任务状态
				DataAccess.Exec(UPDATE_HANDLE_RECEIVED, new String[]{"1",bean.getMessageType(),bean.getSerialNo()});
				//日志记录
				ExsUtility.AddLogSuccess(DataAccess, bean, "企业端更新成功！");
			} catch (Exception e) {
				DataAccess.Exec(UPDATE_HANDLE_RECEIVED, new String[]{"2",bean.getMessageType(),bean.getSerialNo()});
				String errContent = "报文生成异常"+e.getMessage();
				ExsUtility.ServicesErrorNotice("UpdateToDBForDB错误通知！", errContent, null);
				LogUtil.printLog(errContent, Level.ERROR);
			}finally{
				DataAccess.ComitTrans();
			}
		}
	}
	
	public void UpdateReceived(IDataAccess DataAccess,HashMap data)throws LegendException{
		String messageType = (String)data.get("MSG_TYPE");
		String msgCode = (String)data.get("MSG_CODE");//SysUtility.getJsonField(root, "MSG_CODE");
        String msgGenDate = (String)data.get("MSG_GEN_DATE");//SysUtility.getJsonField(root, "MSG_GEN_DATE");
        String msgDesc = (String)data.get("MSG_DESC");//SysUtility.getJsonField(root, "MSG_DESC");
        String msgRef = (String)data.get("MSG_REF");//SysUtility.getJsonField(root, "MSG_REF");
        
        StringBuffer SQL = new StringBuffer();
        List<String> paramList = new ArrayList<String>();
        String msgName = "";
        if("200".equals(messageType)){
    		SQL.append("UPDATE MF_GOODS_DECL SET BILL_TYPE_CODE=?,BILL_TYPE_NAME=?,APPROVAL_TIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),APPROVAL_REMARK=? WHERE DECL_NO = ?");
    		if("03".equals(msgCode)){
    			msgName = "电子审单未通过";
    		}else if("0".equals(msgCode)){
    			msgName = "待受理";
    		}else if("1".equals(msgCode)){
    			msgName = "电子放行";
    		}else if("2".equals(msgCode)){
    			msgName = "已放行";
    		}else if("3".equals(msgCode)){
    			msgName = "撤销放行";
    		}else if("4".equals(msgCode)){
    			msgName = "退单";
    		}else if("5".equals(msgCode)){
    			msgName = "删单";
    		}else{
    			msgName = "--";
    		}
    		paramList.add(msgCode);
            paramList.add(msgName);
    		paramList.add(msgGenDate);
    		paramList.add(msgDesc);
    		paramList.add(msgRef);
        }else if("201".equals(messageType)){
    		SQL.append("UPDATE MF_GOODS_DECL SET QF_TYPE_CODE=?,QF_TYPE_NAME=?,QF_TIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),QF_RESULT=? WHERE DECL_NO = ?");
    		if("0".equals(msgCode)){
    			msgName = "无需处理";
    		}else if("1".equals(msgCode)){
    			msgName = "待处理";
    		}else if("2".equals(msgCode)){
    			msgName = "已完成";
    		}else{
    			msgName = "--";
    		}
    		paramList.add(msgCode);
            paramList.add(msgName);
    		paramList.add(msgGenDate);
    		paramList.add(msgDesc);
    		paramList.add(msgRef);
        }else if("202".equals(messageType)){
        	SQL.append("UPDATE MF_GOODS_DECL SET CF_TYPE_CODE=?,CF_TYPE_NAME=?,CF_TIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),CF_RESULT=? WHERE DECL_NO = ?");
    		if("0".equals(msgCode)){
    			msgName = "无需处理";
    		}else if("1".equals(msgCode)){
    			msgName = "待查验";
    		}else if("2".equals(msgCode)){
    			msgName = "已完成";
    		}else{
    			msgName = "--";
    		}
    		paramList.add(msgCode);
            paramList.add(msgName);
    		paramList.add(msgGenDate);
    		paramList.add(msgDesc);
    		paramList.add(msgRef);
        }else if("203".equals(messageType)){
        	SQL.append("UPDATE MF_GOODS_DECL SET DECL_LAW_CODE=?,DECL_LAW_NAME=? WHERE DECL_NO = ?");
    		if("1".equals(msgCode)){
    			msgName = "法检";
    		}else if("2".equals(msgCode)){
    			msgName = "非法检";
    		}else if("3".equals(msgCode)){
    			msgName = "混报";
    		}else{
    			msgName = "--";
    		}
    		paramList.add(msgCode);
            paramList.add(msgName);
    		paramList.add(msgRef);
        }else if("205".equals(messageType)){
        	
        }
    	DataAccess.ExecSQL(SQL.toString(), SysUtility.ListToArray(paramList));
	}
	
	public void UpdateReceived(ServicesBean bean,IDataAccess DataAccess,String messageType,JSONObject data)throws LegendException{
		String UpdateSQL = bean.getUpdateSql();
		if(SysUtility.isNotEmpty(UpdateSQL)){
			
		}else{
			
		}
	}
	
	public  HashMap getDocumentRequestDataMap(InputStream is)throws LegendException{
		HashMap hmSourceData = FileUtility.xmlParse(is,ExsConstants.RequestMessage);
		HashMap rmHashMap = (HashMap)((List)hmSourceData.get(ExsConstants.RequestMessage)).get(0);
		HashMap MessageHead = (HashMap)((List)rmHashMap.get(ExsConstants.MessageHead)).get(0);
		List MessageBody = (List)rmHashMap.get(ExsConstants.MessageBody);
		HashMap documentData = (HashMap)MessageBody.get(0);
		return documentData;
	}
	
	public  HashMap getRequestHeadMap(HashMap documentData,String xmlDocumentName,String xmlHeadName)throws LegendException{
		List documentList = (List)documentData.get(xmlDocumentName);
		HashMap documentMap = (HashMap)documentList.get(0);//业务表头只能只取第一条。
		HashMap revevieHead = (HashMap)((List)documentMap.get(xmlHeadName)).get(0);
		return revevieHead;
	}
	
	public  HashMap getDocumentResponseDataMap(InputStream is)throws LegendException{
		HashMap hmSourceData = FileUtility.xmlParse(is,ExsConstants.ResponseMessage);
		HashMap rmHashMap = (HashMap)((List)hmSourceData.get(ExsConstants.ResponseMessage)).get(0);
		HashMap MessageHead = (HashMap)((List)rmHashMap.get(ExsConstants.MessageHead)).get(0);
		List MessageBody = (List)rmHashMap.get(ExsConstants.MessageBody);
		HashMap documentData = (HashMap)MessageBody.get(0);
		return documentData;
	}
	
	public  HashMap getRevevieHeadMap(InputStream is,String xmlDocumentName,String xmlHeadName)throws LegendException{
		HashMap hmSourceData = FileUtility.xmlParse(is,ExsConstants.ResponseMessage);
		HashMap rmHashMap = (HashMap)((List)hmSourceData.get(ExsConstants.ResponseMessage)).get(0);
		HashMap MessageHead = (HashMap)((List)rmHashMap.get(ExsConstants.MessageHead)).get(0);
		List MessageBody = (List)rmHashMap.get(ExsConstants.MessageBody);
		HashMap documentData = (HashMap)MessageBody.get(0);
		List documentList = (List)documentData.get(xmlDocumentName);
		HashMap documentMap = (HashMap)documentList.get(0);//业务表头只能只取第一条。
		HashMap revevieHead = (HashMap)((List)documentMap.get(xmlHeadName)).get(0);
		return revevieHead;
	}
	
	public  HashMap getSaveTableMap(HashMap documentData,String xmlDocumentName,String xmlHeadName)throws JSONException{
		HashMap saveTable = new HashMap();
		List documentList = (List)documentData.get(xmlDocumentName);
		HashMap documentMap = (HashMap)documentList.get(0);//业务表头只能只取第一条。
		Set set = documentMap.entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
	        Object key = entry.getKey();
	        Object value = entry.getValue();
	        if(value instanceof List) {
	        	List temList = (List)value;
				for (int j = 0; j < temList.size(); j++) {
					HashMap map = (HashMap)temList.get(j);
					Set set2 = map.entrySet();
					JSONObject mainTable = new JSONObject();//主表
					for (Iterator it2 = set2.iterator(); it2.hasNext();) {
						Entry entry2 = (Entry)it2.next();
						Object key2 = entry2.getKey();
				        Object value2 = entry2.getValue();
				        if(key.equals(xmlHeadName) && !key2.toString().equalsIgnoreCase(SysUtility.KeyFieldDefault)
				        		&& !key2.toString().equalsIgnoreCase("CREATE_TIME")
				        		&& !key2.toString().equalsIgnoreCase("REC_VER")){
				        	mainTable.put(key2.toString(), value2);//主表的字段
				        }else if(value2 instanceof List) {
				        	List temList2 = (List)value2;//子表
				        	JSONArray childs = new JSONArray();
				        	for (int k = 0; k < temList2.size(); k++) {
				        		JSONObject child = new JSONObject();
				        		Object obj = temList2.get(k);
				        		if(SysUtility.isEmpty(obj)){
				        			continue;
				        		}
				        		HashMap map2 = (HashMap)temList2.get(k);
				        		Set set3 = map2.entrySet();
				        		for (Iterator it3 = set3.iterator(); it3.hasNext();) {
				        			Entry entry3 = (Entry)it3.next();
				        			Object key3 = entry3.getKey();
							        Object value3 = entry3.getValue();
							        if(value3 instanceof List) {
							        	continue;//第三层子表，暂时不处理。
							        }else{
							        	if(!key3.toString().equalsIgnoreCase(SysUtility.KeyFieldDefault)
							        			&& !key2.toString().equalsIgnoreCase("CREATE_TIME")
								        		&& !key2.toString().equalsIgnoreCase("REC_VER")){
							        		child.put(key3.toString(), value3);
							        	}
							        }
				        		}
				        		childs.put(child);
							}
				        	saveTable.put(key, childs);
				        }
					}
					if(key.equals(xmlHeadName)){
						saveTable.put(key, mainTable);
					}
				}
	        }else{
	        	continue;//报文不符合规范
	        }
		}
		return saveTable;
	}

	
	public  String createRequestXml(HashMap HeadMap, ServicesBean bean,String xmlDocumentName,String xmlHeadName,String TARGET_PATH){
		HashMap MessageHead = getMessageHead(bean,xmlDocumentName);
		HashMap xmlRootMap = getXmlRootMap(HeadMap, xmlDocumentName, xmlHeadName);
		String xmlData = ExsUtility.parseRequestXml(MessageHead, xmlRootMap, xmlDocumentName,bean.getEncoding());
		String fileName = bean.getFileName();
		if(SysUtility.isEmpty(fileName)){
			fileName = SysUtility.getMilliSeconds() + ".xml";
		}
		String ChildFolder = bean.getChildFolder();
		if(SysUtility.isNotEmpty(ChildFolder)){
			TARGET_PATH = ExsUtility.createFolder(TARGET_PATH, ChildFolder);
		}
		FileUtility.createFile(TARGET_PATH, fileName, xmlData);
		return fileName;
	}
			
	public  HashMap getXmlRootMap(HashMap HeadMap,String xmlDocumentName,String xmlHeadName){
		HashMap rootMap = new HashMap();
		List HeadList = new ArrayList();
		HeadList.add(HeadMap);
		rootMap.put(xmlHeadName, HeadList);
		HashMap xmlRootMap = new HashMap();
		List xmlRootList = new ArrayList();
		xmlRootList.add(rootMap);
		xmlRootMap.put(xmlDocumentName, xmlRootList);
		return xmlRootMap;
	}
	
	public  HashMap getMessageHead(ServicesBean bean,String xmlDocumentName){
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_handle_sender where msg_flag = 0");
		sqlBuild.append("and msg_type = ?", bean.getMessageType());
		sqlBuild.append("and msg_no = ?", bean.getSerialNo());
		Map senderMap = sqlBuild.query4Map();
		bean.setMessageSource(SysUtility.getMapField(senderMap, "MESSAGE_SOURCE"));
		bean.setMessageDest(SysUtility.getMapField(senderMap, "MESSAGE_DEST"));
		
		HashMap MessageHead = new HashMap();
		MessageHead.put(ExsConstants.MESSAGE_TYPE, bean.getMessageType());
		MessageHead.put(ExsConstants.MESSAGE_ID, SysUtility.GetUUID());
		MessageHead.put(ExsConstants.MESSAGE_TIME, SysUtility.getSysDate());
		MessageHead.put(ExsConstants.MESSAGE_SOURCE, bean.getMessageSource());
		MessageHead.put(ExsConstants.MESSAGE_DEST, bean.getMessageDest());
//		MessageHead.put(ExsConstants.MESSAGE_SERIAL_NAME, bean.getSerialName());//表头流水号字段名称，记录日志使用
		return MessageHead;
	}
	
	public void GenerateRequestForSocket(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		InetAddress ip = InetAddress.getLocalHost();  
        int port = 7778;  // 确定发送方的IP地址及端口号，地址为本地机器地址   
				
		String SourcePath = bean.getSourcePath();
		File files[] = new File(SourcePath).listFiles(new FileFilterHandle());
		DatagramSocket sendSocket = new DatagramSocket();  // 创建发送方的套接字，IP默认为本地，端口号随机   
		for (int i = 0; i < files.length; i++) {
			bean.setSourcePath(SourcePath);
			if (!files[i].isDirectory()){
				try {
					File file = files[i];
					bean.setFileName(file.getName());
					bean.setFile(file);
					byte[] buf = SysUtility.ObjectToByte(bean);   // 由于数据报的数据是以字符数组传的形式存储的，所以传转数据   
					DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, ip,port);   // 创建发送类型的数据报：
			        sendPacket.setAddress(ip);
			        sendPacket.setPort(port);
			        sendSocket.send(sendPacket);  // 通过套接字发送数据： 
			        
			        /************创建发送方收到的参数对象*******************/
		            byte[] getBuf = new byte[1024];  // 确定接受反馈数据的缓冲存储器，即存储数据的字节数组   
		            DatagramPacket getPacket = new DatagramPacket(getBuf, getBuf.length);   // 创建接受类型的数据报   
		            sendSocket.receive(getPacket);   // 通过套接字接受数据  
		            String backMes = new String(getBuf, 0, getPacket.getLength());  // 解析反馈的消息，并打印
		            if(SysUtility.isNotEmpty(backMes)){
		            	System.out.println("接受方返回的消息：" + backMes);
		            	//删除文件。
		            }
				} catch (Exception e) {
					LogUtil.printLog("SaveToDBForXml出错："+e.getMessage(), Level.ERROR);
				}
			}else{
				String folderName = files[i].getName();
				String tempSourcePath = ExsUtility.createFolder(bean.getSourcePath(), folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					try {
						File file = files[k];
						bean.setSourcePath(tempSourcePath);
						bean.setFileName(file.getName());
						bean.setFile(file);
						
					} catch (Exception e) {
						LogUtil.printLog("SaveToDBForXml出错："+e.getMessage(), Level.ERROR);
					}
				}
			}
		}
	}

	public void DBToXmlForLocal(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		ExsUtility.InitServicesBeanPath(bean);
		ToXmlFromAny(bean, DataAccess);
	}



	public void ElecDocsForXmlDefault(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String XmlToRuleSQL = "SELECT * FROM EXS_CONFIG_XMLTORULE WHERE nvl(IS_ENABLED,'1') = '1' AND QUARTZ_NAME = ?";
		List XmlToRuleList = SQLExecUtils.query4List(XmlToRuleSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		for (int i = 0; i < XmlToRuleList.size(); i++) {
			HashMap map = (HashMap)XmlToRuleList.get(i);
			String Indx = SysUtility.getMapField(map, "INDX");
			String xmlDocument = (String)map.get("XML_DOCUMENT");
			String xmlHead = (String)map.get("XML_HEAD");
			String SerialName = (String)map.get("SERIAL_NAME");
			String SourcePath = (String)map.get("SOURCE_PATH");
			String PassPath = (String)map.get("PASS_PATH");
			String TargetPath = (String)map.get("TARGET_PATH");
			String ErrorPath = (String)map.get("ERROR_PATH");
			String FieldPointCode = (String)map.get("FIELD_POINT_CODE");
			String MappingPointCode = (String)map.get("MAPPING_POINT_CODE");
			String RulePointCode = (String)map.get("RULE_POINT_CODE");
			String threadCount = (String)map.get("THREAD_COUNT");
			if(SysUtility.isEmpty(xmlDocument)||SysUtility.isEmpty(xmlHead)){
				LogUtil.printLog("EXS_CONFIG_XMLTORULE配置出错:INDX="+Indx, Level.ERROR);
				continue;
			}
			bean.setXmlDocument(xmlDocument);
			bean.setXmlHead(xmlHead);
			bean.setSerialName(SerialName);
			bean.setSourcePath(SourcePath);
			bean.setPassPath(PassPath);
			bean.setTargetPath(TargetPath);
			bean.setErrorPath(ErrorPath);
			bean.setFieldPointCode(FieldPointCode);
			bean.setMappingPointCode(MappingPointCode);
			bean.setRulePointCode(RulePointCode);
			bean.setThreadCount(threadCount);
			ExsUtility.InitServicesBeanPath(bean);
			ElecDocsForXml(bean, DataAccess);
		}
	}
	
	public void ElecDocsForXml(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		String SourcePath = bean.getSourcePath();
		File files[] = new File(SourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			bean.setSourcePath(SourcePath);
			if (!files[i].isDirectory()){
				try {
					File file = files[i];
					bean.setFileName(file.getName());
					bean.setFile(file);
					ElecDocsForAny(bean, DataAccess);
				} catch (LegendException e) {
					LogUtil.printLog("ElecDocsForXml出错："+e.getMessage(), Level.ERROR);
				}
			}else{
				String folderName = files[i].getName();
				String tempSourcePath = ExsUtility.createFolder(bean.getSourcePath(), folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					try {
						File file = files2[k];
						bean.setSourcePath(tempSourcePath);
						bean.setFileName(file.getName());
						bean.setFile(file);
						ElecDocsForAny(bean, DataAccess);
					} catch (Exception e) {
						LogUtil.printLog("ElecDocsForXml出错："+e.getMessage(), Level.ERROR);
					}
				}
			}
		}
	}
	
	
	
	public  boolean DeleteExistData(IDataAccess DataAccess,ServicesBean bean,JSONObject root)throws Exception{
		String[] TableNames = bean.getRootNames().split(",");
		HashMap DBTableNames = SysUtility.StrsToHashMap(bean.getRootNames(), bean.getDbTableNames());
		String DBTableName = DBTableNames.get(bean.getXmlHead()).toString();
		
		String serialNo = bean.getSerialNo();
		if(SysUtility.isEmpty(serialNo)){
			return false;
		}
		String indxName = bean.getIndxName();
		StringBuffer SQL = new StringBuffer();
		SQL.append("select "+bean.getIndxName()+" from "+DBTableName+" where "+bean.getSerialName()+" = ?");
		Datas dates = DataAccess.GetTableDatas("T", SQL.toString(), serialNo);
		if(dates.GetTableRows("T") > 0){
			if(bean.isExistsReturn()){
				return false;
			}
			
			String indxValue = dates.GetTableValue("T", bean.getIndxName());
			root.put(bean.getIndxName(), indxValue);
			for (int j = 1; j < TableNames.length; j++) {
				if(DBTableNames.get(TableNames[j]).equals("EDI_RECEIVE_INDEX_TABLE"))
				{
					String delSQL = "delete from EDI_RECEIVE_INDEX_TABLE WHERE CONDITION = '"+bean.getPindxName().split(",")[0]+"=''"+indxValue+"''' and REPORT_TYPE='"+bean.getMessageType()+"' ";
					DataAccess.ExecSQL(delSQL);
				}else{
					String delSQL = "delete from "+DBTableNames.get(TableNames[j])+" where "+bean.getPindxName().split(",")[0]+" = ? ";
					DataAccess.ExecSQL(delSQL, indxValue);
				}
			}
			String delSQL = "delete from "+DBTableNames.get(TableNames[0])+" where "+bean.getIndxName()+" = ? ";
			DataAccess.ExecSQL(delSQL, indxValue);
		}
		return true;
	}

	public  boolean ExecUpdateData(IDataAccess DataAccess,String uuid, String UpdateSQL,JSONObject root) throws LegendException {
		if(SysUtility.isEmpty(DataAccess) || SysUtility.isEmpty(UpdateSQL) ||SysUtility.isEmpty(root)){
			return false;
		}
		List pList = new  ArrayList();
		int i = 0;
		while(true){
			int bIndx = UpdateSQL.indexOf("#");
			if(bIndx > -1){
				int eIndx = UpdateSQL.indexOf("#", bIndx+1);
				String param = UpdateSQL.substring(bIndx+1, eIndx);
				if(SysUtility.isEmpty(SysUtility.getJsonField(root, param))){
					String partStr=UpdateSQL.substring(0,bIndx).trim();
					partStr=partStr.substring(0,  partStr.length()-1).trim();
					String field="";
					if(partStr.lastIndexOf(',')>-1){
						field=partStr.substring(partStr.lastIndexOf(',')+1).trim();
					}
					else{
						field=partStr.substring(partStr.lastIndexOf(' ')+1) ;
					}
					UpdateSQL = UpdateSQL.replaceAll("#"+param+"#", field);//如果回执报文中不存在这个字段需要的值就不更新
				}else{
					UpdateSQL = UpdateSQL.replaceAll("#"+param+"#", "?");
					pList.add(SysUtility.getJsonField(root, param));
				}
			}else{
				break;//没有需要替换的参数
			}
		}
		String[] params = new String[pList.size()];
		for (int j = 0; j < pList.size(); j++) {
			params[j] = (String)pList.get(j);
		}
		return DataAccess.ExecSQL(UpdateSQL, params, null, new StringBuffer(), SysUtility.getCurrentDynamicConnection(uuid));
	}
	
	
	
	public void XmlToFloderForMerge(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String XmlToMergeSQL = "SELECT * FROM exs_config_xmltomerge WHERE nvl(IS_ENABLED,'1') = '1' AND QUARTZ_NAME = ?";
		List XmlToMergeList = SQLExecUtils.query4List(XmlToMergeSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		for (int i = 0; i < XmlToMergeList.size(); i++) {
			HashMap map = (HashMap)XmlToMergeList.get(i);
			String Indx = SysUtility.getMapField(map, "INDX");
			
			bean = new ServicesBean();
			bean.setTargetSourcePath((String)map.get("TARGET_SOURCE_PATH"));
			bean.setSourcePath((String)map.get("SOURCE_PATH"));
			bean.setTargetPath((String)map.get("TARGET_PATH"));
			bean.setErrorPath((String)map.get("ERROR_PATH"));
			bean.setThreadCount((String)map.get("THREAD_COUNT"));
			bean.setAesKey((String)map.get("AES_KEY"));//消息内容 软key
			bean.setClassInvoke((String)map.get("CLASS_INVOKE"));//消息内容 软key
			if(SysUtility.isEmpty(bean.getSourcePath())){
				LogUtil.printLog("exs_config_xmltomerge配置出错:INDX="+Indx, Level.ERROR);
				continue;
			}
			
			ExsUtility.InitServicesBeanPath(bean);
			
			if(SysUtility.isEmpty(bean.getClassInvoke())){
				LocalXmlToSortLocal(bean, DataAccess);//执行分拣合并逻辑
			}else{
				ExsUtility.MethodInvoke(bean.getClassInvoke(), bean);
			}
			
			if(SysUtility.isNotEmpty(Indx)){
	        	String UpdateSQL = "UPDATE exs_config_xmltomerge SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+Indx;
	        	DataAccess.ExecSQL(UpdateSQL);
	        	DataAccess.ComitTrans();
	        }
		}
	}
	
	public void XmlToFloderForSplit(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String XmlToMergeSQL = "SELECT * FROM exs_config_xmltosplit WHERE nvl(IS_ENABLED,'1') = '1' AND QUARTZ_NAME = ?";
		List XmlToMergeList = SQLExecUtils.query4List(XmlToMergeSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		//将所有配置依据XmlToMergeList放到listBean中，供判断同一个文件夹下包含多种报文类型的文件使用。
		List<ServicesBean> listBean = new ArrayList<ServicesBean>();
		for (int i = 0; i < XmlToMergeList.size(); i++) {
			HashMap map = (HashMap)XmlToMergeList.get(i);
			ServicesBean tempBean = new ServicesBean();
			tempBean.setMessageType((String)map.get("MESSAGE_TYPE"));
			tempBean.setTargetSourcePath((String)map.get("TARGET_SOURCE_PATH"));
			tempBean.setSourcePath((String)map.get("SOURCE_PATH"));
			ExsUtility.InitServicesBeanPath(tempBean);
			listBean.add(tempBean);
		}
		
		String serviceMode = bean.getServiceMode();
		for (int i = 0; i < XmlToMergeList.size(); i++) {
			HashMap map = (HashMap)XmlToMergeList.get(i);
			String Indx = SysUtility.getMapField(map, "INDX");
			
			bean = new ServicesBean();
			bean.setServiceMode(serviceMode);
			bean.setMessageType((String)map.get("MESSAGE_TYPE"));
			bean.setTargetSourcePath((String)map.get("TARGET_SOURCE_PATH"));
			bean.setSourcePath((String)map.get("SOURCE_PATH"));
			bean.setTargetPath((String)map.get("TARGET_PATH"));
			bean.setErrorPath((String)map.get("ERROR_PATH"));
			bean.setThreadCount((String)map.get("THREAD_COUNT"));
			bean.setAesKey((String)map.get("AES_KEY"));//消息内容 软key
			bean.setClassInvoke((String)map.get("CLASS_INVOKE"));//消息内容 软key
			if(SysUtility.isEmpty(bean.getMessageType())){
				LogUtil.printLog("exs_config_xmltosplit 配置出错:INDX="+Indx, Level.ERROR);
				continue;
			}
			bean.setAllBeanList(listBean);
			ExsUtility.InitServicesBeanPath(bean);
			
			if(SysUtility.isEmpty(bean.getClassInvoke())){
				LocalXmlToSortLocal(bean, DataAccess);//执行分拣拆分逻辑
			}else{
				ExsUtility.MethodInvoke(bean.getClassInvoke(), bean);
			}
			
			if(SysUtility.isNotEmpty(Indx)){
	        	String UpdateSQL = "UPDATE exs_config_xmltosplit SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+Indx;
	        	DataAccess.ExecSQL(UpdateSQL);
	        	DataAccess.ComitTrans();
	        }
		}
	}

	public void LocalXmlToSortLocal(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		int inProcessCount = 0;//正在处理的报文数量
		String sourcePath = bean.getSourcePath();
		File files[] = new File(sourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()){
				inProcessCount++;
				if(inProcessCount > singleProcessCount){
					return;
				}
				bean.setSourcePath(sourcePath);
				XmlToSortLocal(bean, DataAccess, files[i]);
			}else{
				String folderName = files[i].getName();
				if("Temps".equals(folderName) ||"Backup".equals(folderName) || "Error".equals(folderName)){
					continue;
				}
				String tempSourcePath = ExsUtility.createFolder(sourcePath, folderName);
				bean.setSourcePath(tempSourcePath);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					if (!files2[k].isDirectory()){
						inProcessCount ++;
						if(inProcessCount > singleProcessCount){
							return;
						}
						XmlToSortLocal(bean, DataAccess, files2[k]);
					}
				}
			}
		}
	}
	
	public void XmlToSortLocal(ServicesBean bean,IDataAccess DataAccess,File file)throws Exception{
		String fileName = file.getName();//文件名
		if(!ExsFileFilter(file, bean, DataAccess)){
			return;//过滤出交换框架的文件
		}
		try {
			bean.setFileName(fileName);
			HashMap<String,String> rtMap = ExsUtility.getFileNameElements(bean,fileName);
			bean.setSerialNo(rtMap.get(ExsConstants.SerialNo));
			String messageType = rtMap.get(ExsConstants.MessageType);
			
			/****************分拣拆分才走此逻辑*************************/
			List<ServicesBean> lst = bean.getAllBeanList();
			if(SysUtility.isNotEmpty(lst)){
				for (int i = 0; i < lst.size(); i++) {
					ServicesBean tempBean = lst.get(i);
					if(messageType.equals(tempBean.getMessageType()) && bean.getSourcePath().equals(tempBean.getSourcePath())){
						bean.setTargetSourcePath(tempBean.getTargetSourcePath());
						break;
					}
				}
			}
			/*********拷贝源文件***********/ 
			String[] paths = bean.getTargetSourcePath().split(";");
			for (int j = 0; j < paths.length; j++) {
				FileUtility.copyFile(bean.getSourcePath(),paths[j],fileName);
			}   
			/*********备份源文件***********/
			FileUtility.copyFile(bean.getSourcePath(),bean.getTargetPath(),fileName);
			/*********删除源文件***********/
			FileUtility.deleteFile(bean.getSourcePath(), fileName);
			/*******日志记录*******/
			ExsUtility.AddLogSuccess(DataAccess, bean, "分拣成功");
			DataAccess.ComitTrans();
		} catch (Exception e) {
			FileUtility.copyFile(bean.getSourcePath(),bean.getErrorPath(),fileName);
			FileUtility.deleteFile(bean.getSourcePath(), fileName);
			ExsUtility.AddLogFailDesc(DataAccess, bean, "分拣失败");
			LogUtil.printLog("文件名："+fileName+" XmlToSortLocal出错.."+e.getMessage(), Level.INFO);
		} finally{
			DataAccess.ComitTrans();
		}
	}
	
	
	
	public void SaveToDBForSocket(IDataAccess DataAccess)throws Exception{
		String clusterSQL = "select t.indx,t.quartz_name,t.main_pc_name,t.main_pc_port,t.cluster_ip,t.cluster_port,t.cluster_app_name from exs_config_cluster t where t.nvl(IS_ENABLED,'1') = '1' and t.cluster_ip = ? and t.cluster_app_name = ?";
		List clusterList = SQLExecUtils.query4List(clusterSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentHostIPAddress());
				ps.setString(2, SysUtility.getCurrentHostAppName());
			}
		});
		for (int i = 0; i < clusterList.size(); i++) {
			HashMap map = (HashMap)clusterList.get(i);
			String quartzName = (String)map.get("QUARTZ_NAME");
			String mainPcName = (String)map.get("MAIN_PC_NAME");
			String mainPcPort = (String)map.get("MAIN_PC_PORT");
			String clusterIP = (String)map.get("CLUSTER_IP");
			String clusterPort = (String)map.get("CLUSTER_PORT");
			String clusterAppName = (String)map.get("CLUSTER_APP_NAME");
			
			ClusterBean cbean = new ClusterBean();
			cbean.setQuartzName(quartzName);
			cbean.setMainPcName(mainPcName);
			cbean.setMainPcPort(mainPcPort);
			cbean.setClusterIP(clusterIP);
			cbean.setClusterPort(clusterPort);
			cbean.setClusterAppName(clusterAppName);
			/************************************/
			DatagramSocket getSocket = null;
	    	try {
	    		getSocket = new DatagramSocket(Integer.parseInt(cbean.getMainPcPort()), SysUtility.getInetAddressByName(cbean.getMainPcName()));   // 创建接收方的套接字,并制定端口号和IP地址   
	            byte[] buf = new byte[1024]; // 确定数据报接受的数据的数组大小  
	            DatagramPacket getPacket = new DatagramPacket(buf, buf.length);  // 创建接受类型的数据报，数据将存储在buf中   
	            getSocket.receive(getPacket);  // 通过套接字接收数据   
	            
	            ServicesBean bean = (ServicesBean)SysUtility.ByteToObject(buf);
	            SaveToDBForAny(bean, DataAccess);
	            
	            String msg = "处理成功，集群实例名称="+cbean.getClusterAppName()+",集群IP="+cbean.getClusterIP()+"集群端口"+cbean.getClusterPort();
	            byte[] backBuf = msg.getBytes();  // 确定要反馈发送方的消息内容，并转换为字节数组   
	            DatagramPacket sendPacket = new DatagramPacket(backBuf,backBuf.length, getPacket.getSocketAddress());  // 创建发送类型的数据包
	            getSocket.send(sendPacket);  // 通过套接字发送数据   
	        } catch (Exception e) {  
	            LogUtil.printLog("数据接收处理失败："+e.getMessage(), Level.ERROR);
	        } finally{
	        	if(SysUtility.isNotEmpty(getSocket)){
	        		getSocket.close();  // 关闭套接字
	        	}
	        }
		}
	}
	
	/**
	 * @return
	 * "" 文件不是以.temp、.exs结尾
	 * A 文件以.temp、.exs结尾，时间未超过2小时
	 * B 文件以.temp、.exs结尾，时间超过2小时
	 * C 文件以不是.temp、.exs结尾，且文件第一行或第二行没有RequestMessage或ResponseMessage的关键字
	 * */
	public  boolean ExsFileFilter(File file,ServicesBean bean,IDataAccess DataAccess) throws LegendException{
		String fileName = file.getName();
		String FilterRT = FileUtility.FilterFileName(file);
		if("A".equals(FilterRT)){
			return false;
		}else if("B".equals(FilterRT)){
			FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath() ,fileName);
			FileUtility.deleteFile(bean.getSourcePath(), fileName);
			ExsUtility.AddLogSuccess(DataAccess, bean, "文件格式不正确！");
			return false;
		}else if("C".equals(FilterRT)){
			//TODO 暂时不限制C类型
		}
		return true;
	}
	
	public  Map GetAnyXmlRootMap(ServicesBean bean,List MainDatas,HashMap childsMap) throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnsupportedEncodingException{
		String[] RootNames = bean.getRootNames().split(",");
		List ListSql = bean.getListSql();
		HashMap SQLMap = bean.getMapSql();
		HashMap MappingColumns = bean.getMappingColumns();
		
		
		List xmlRootList = new ArrayList();
		/**********************1  填充Root节点数据***************************************/
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap MainData = (HashMap)MainDatas.get(i);
			List tempXmlRootList = new ArrayList();
			String XmlRootName = "";
			for (int j = 0; j < ListSql.size(); j++) {
				HashMap sqlMap = (HashMap)ListSql.get(j);
				String SeqNo = SysUtility.getMapField(sqlMap, "SEQ_NO");
				String XmlDocumentName = SysUtility.getMapField(sqlMap, "XML_DOCUMENT_NAME");
				String XmlDisplayName = SysUtility.getMapField(sqlMap, "XML_DISPLAY_NAME");
				String XmlParentDocument = SysUtility.getMapField(sqlMap, "XML_PARENT_DOCUMENT");
				String ShowStr = SysUtility.getMapField(sqlMap, "XML_COULMN_SHOW");
				String HiddenStr = SysUtility.getMapField(sqlMap, "XML_COULMN_SHOW");
				HashMap MappingMap = ExsUtility.GetMappingMap(sqlMap);//获取Mapping字段集合
				String xmlSql = SysUtility.getMapField(sqlMap, "XML_SQL");
				String DeleteSql = SysUtility.getMapField(sqlMap, XmlDocumentName+"_DELETE");
				String rootMethod = SysUtility.getMapField(sqlMap, "ROOT_METHOD_INVOKE");
				String childMethod = SysUtility.getMapField(sqlMap, "CHILD_METHOD_INVOKE");
				
				if("0".equals(SeqNo) && SysUtility.isEmpty(XmlParentDocument)){//任意报文自定义Root，子表第一条配置为根节点,并默认读取出第2条配置取出组装主表数据。
					XmlRootName = XmlDocumentName;
					String[] mappingStrs = SysUtility.getTableColumns(xmlSql);
//					if(SysUtility.isEmpty(xmlRootMap.get(XmlRootName))){
//						xmlRootMap.put(XmlRootName, tempXmlRootList);
//					}else{
//						tempXmlRootList = (List)xmlRootMap.get(XmlRootName);
//					}
					HashMap mainMap = (HashMap)ListSql.get(++j);
					String mainXmlDocumentName = (String)mainMap.get("XML_DOCUMENT_NAME");
					HiddenStr = (String)mainMap.get("XML_COULMN_HIDDEND");
					MappingMap = ExsUtility.GetMappingMap(mainMap);//获取Mapping字段集合
					
					List RootList = new ArrayList();
					HashMap RootMap = new HashMap();
					List HeadList = new ArrayList();
					HeadList.add(MainData);
					ExsUtility.CoulmnRootInvoke(rootMethod, HeadList);
					ExsUtility.CoulmnShowInvoke(HeadList, ShowStr);
					ExsUtility.CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
					ExsUtility.CoulmnMappingInvoke(HeadList, MappingMap, mappingStrs);//字段Mapping转换
					RootMap.put(mainXmlDocumentName, HeadList);
					tempXmlRootList.add(RootMap);
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
					MappingColumns.put(XmlDocumentName, mappingStrs);
				}else if("0".equals(SeqNo) && SysUtility.isNotEmpty(XmlParentDocument)){//任意报文自定义Root，子表第一条配置的XmlParentDocument为root名称并将组装主表数据
					XmlRootName = XmlParentDocument;
					String[] mappingStrs = SysUtility.getTableColumns(xmlSql);
					HashMap RootMap = new HashMap();
					List HeadList = new ArrayList();
					HeadList.add(MainData);
					ExsUtility.CoulmnRootInvoke(rootMethod, HeadList);
					ExsUtility.CoulmnShowInvoke(HeadList, ShowStr);
					ExsUtility.CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
					ExsUtility.CoulmnMappingInvoke(HeadList, MappingMap, mappingStrs);//字段Mapping转换
					RootMap.put(XmlDocumentName, HeadList);
					tempXmlRootList.add(RootMap);
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
					MappingColumns.put(XmlDocumentName, mappingStrs);
				}else if(SysUtility.isEmpty(XmlParentDocument)){//无父级节点逻辑
					String[] mappingStrs = SysUtility.getTableColumns(xmlSql);
					XmlRootName = XmlDocumentName;
					HashMap RootMap = new HashMap();
					if(XmlDocumentName.equals(RootNames[0])){//主表
						List HeadList = new ArrayList();
						HeadList.add(MainData);
						ExsUtility.CoulmnRootInvoke(rootMethod, HeadList);
						ExsUtility.CoulmnShowInvoke(HeadList, ShowStr);
						ExsUtility.CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
						ExsUtility.CoulmnMappingInvoke(HeadList, MappingMap, mappingStrs);//字段Mapping转换
						RootMap.put(XmlDocumentName, HeadList);
					}else{
						List tempList = ExsUtility.getDataList(MainData,childsMap, RootNames, XmlDocumentName,XmlDisplayName, (String)MainData.get(bean.getIndxName()));
						if(SysUtility.isEmpty(tempList) && SysUtility.isNotEmpty(xmlSql)){
							JSONObject param2 = SysUtility.MapToJSONObject(MainData);
							SQLHolder holder2 = SQLParser.parse(xmlSql, param2);
							tempList = SQLExecUtils.query4List(SysUtility.getCurrentDynamicConnection(bean.getIndx()), holder2.getSql(),new SimpleParamSetter(holder2.getParamList()),bean.getBlobProcess());
							mappingStrs = SysUtility.getTableColumns(holder2.getSql(),holder2.getParamList());
						}
						ExsUtility.CoulmnRootInvoke(rootMethod, tempList);
						ExsUtility.CoulmnShowInvoke(tempList, ShowStr);
						ExsUtility.CoulmnHiddenInvoke(tempList, HiddenStr);//字段Hidden
						ExsUtility.CoulmnMappingInvoke(tempList, MappingMap, mappingStrs);//字段Mapping转换
						if(SysUtility.isNotEmpty(XmlDisplayName)){
							HashMap tempChildMap = new HashMap();
							tempChildMap.put(XmlDisplayName, tempList);
							RootMap.put(XmlDocumentName, tempChildMap);
						}else{
							RootMap.put(XmlDocumentName, tempList);
						}
					}
					/**********************2  填充一级节点数据***************************************/
					tempXmlRootList.add(RootMap);
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
					MappingColumns.put(XmlDocumentName, mappingStrs);
				}else{//有父级节点逻辑
					/**********************3  填充二级以下节点数据***************************************/
					Object obj = ExsUtility.getXmlParentDocumentObj(tempXmlRootList, XmlParentDocument);
					if(obj instanceof Map){
						Map ParentMap = (Map)obj;
						
					}else if(obj instanceof List){
						List ParentLst = (List)obj;
						for (int k = 0; k < ParentLst.size(); k++) {
							HashMap tempMap = (HashMap)ParentLst.get(k);
							List childList = ExsUtility.getDataList(MainData,childsMap, RootNames, XmlDocumentName,XmlDisplayName, (String)MainData.get(bean.getIndxName()));
							String[] mappingStrs = null;
							if(SysUtility.isEmpty(childList) && SysUtility.isNotEmpty(xmlSql)){
								JSONObject param2 = SysUtility.MapToJSONObject(tempMap);
								SQLHolder holder2 = SQLParser.parse(xmlSql, param2);
								childList = SQLExecUtils.query4List(SysUtility.getCurrentDynamicConnection(bean.getIndx()), holder2.getSql(),new SimpleParamSetter(holder2.getParamList()),bean.getBlobProcess());
								mappingStrs = SysUtility.getTableColumns(holder2.getSql(),holder2.getParamList());
								DeleteChildData(bean, DeleteSql, param2);//接口表模式删除子表数据
							}
							ExsUtility.CoulmnChildInvoke(childMethod, XmlDocumentName, childList);
							ExsUtility.CoulmnShowInvoke(childList, ShowStr);
							ExsUtility.CoulmnHiddenInvoke(childList, HiddenStr);//字段Hidden
							ExsUtility.CoulmnMappingInvoke(childList, MappingMap, mappingStrs);//字段Mapping转换
							if(SysUtility.isEmpty(childList)){//填充空集合
								HashMap childMap = new HashMap();
								childList.add(childMap);
							}
							if(SysUtility.isNotEmpty(XmlDisplayName)){
								HashMap tempChildMap = new HashMap();
								tempChildMap.put(XmlDocumentName, childList);
								tempMap.put(XmlDisplayName, tempChildMap);
							}else{
								tempMap.put(XmlDocumentName, childList);
							}
							MappingColumns.put(XmlDocumentName, mappingStrs);
						}
					}
				}
			}
			if(SysUtility.isNotEmpty(tempXmlRootList)){
				xmlRootList.add(tempXmlRootList.get(0));
			}
			bean.setXmlRoot(XmlRootName);
		}
		Map xmlRootMap = new HashMap();
		xmlRootMap.put(bean.getXmlRoot(), xmlRootList);
		return xmlRootMap;
	}
	
	public  String GetAnyJsonData(ServicesBean bean,List MainDatas,HashMap childsMap) throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnsupportedEncodingException{
		if(SysUtility.isEmpty(MainDatas)){
			return "";
		}
		Map xmlRootMap = GetAnyXmlRootMap(bean, MainDatas, childsMap);
		if(SysUtility.isNotEmpty(bean.getRootDefault())){
			Map senderMap = GetSenderMap(bean, (HashMap)MainDatas.get(0));
			String messageTime = SysUtility.getSysDate();
			String messageSource = SysUtility.getMapField(senderMap, "MESSAGE_SOURCE");
			String messageDest = SysUtility.getMapField(senderMap, "MESSAGE_DEST");
			String techRegCode = SysUtility.getMapField(senderMap, "TECH_REG_CODE");
			String extendXml = SysUtility.getMapField(senderMap, "EXTEND_XML");
			
			Map customerMap = CacheUtility.getCustomerMap(bean.getMessageType(), messageSource, techRegCode);
			String signData = (String)customerMap.get("MESSAGE_SIGN");
			
			JSONObject MessageHead = new JSONObject();
			MessageHead.put("MESSAGE_ID", SysUtility.GetUUID());
			MessageHead.put("MESSAGE_TYPE", bean.getMessageType());
			MessageHead.put("MESSAGE_TIME", messageTime);
			MessageHead.put("MESSAGE_SOURCE", bean.getMessageSource());
			MessageHead.put("MESSAGE_DEST", bean.getMessageDest());
//			MessageHead.put("MESSAGE_VERSION", "11");
//			MessageHead.put("MESSAGE_CATEGORY", "1.0");
			if(SysUtility.isNotEmpty(signData)){
				MessageHead.put("MESSAGE_SIGN_DATA", SysUtility.EncryptKeys(messageSource, messageTime,signData));
			}
			if(SysUtility.isNotEmpty(techRegCode)){
				MessageHead.put("TECH_REG_CODE", techRegCode);
			}
			MessageHead.put("ATTRIBUTE1", bean.getRootDefault());//扩展字段
			JSONArray rows = new JSONArray();
			List list = (List)xmlRootMap.get(bean.getXmlRoot());
			for (int i = 0; i < list.size(); i++) {
				HashMap RootMap = (HashMap)list.get(i);
				JSONObject mainRow = SysUtility.MapToJSONObject(RootMap);
				Iterator<?> keys = mainRow.keys();
				String rootName = keys.next().toString();
				JSONArray mainRows = mainRow.getJSONArray(rootName);
				rows.put(mainRows.get(0));
			}
			JSONObject MessageBody = new JSONObject();
			MessageBody.put(bean.getXmlRoot(), rows);
			
			/*JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			JSONObject RootMessage = new JSONObject();
			RootMessage.put(bean.getRootDefault(), Root);
			return RootMessage.toString();*/
			JSONArray Root = new JSONArray();
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			Root.put(row);
			return Root.toString();
		}else{
			JSONArray rows = new JSONArray();
			List list = (List)xmlRootMap.get(bean.getXmlRoot());
			for (int i = 0; i < list.size(); i++) {
				HashMap RootMap = (HashMap)list.get(i);
				JSONObject mainRow = SysUtility.MapToJSONObject(RootMap);
				Iterator<?> keys = mainRow.keys();
				String rootName = keys.next().toString();
				JSONArray mainRows = mainRow.getJSONArray(rootName);
				rows.put(mainRows.get(0));
			}
			JSONObject MessageBody = new JSONObject();
			MessageBody.put(bean.getXmlRoot(), rows);
			return MessageBody.toString();
		}
	}
	
	public  String GetAnyXmlData(ServicesBean bean,List MainDatas,HashMap childsMap) throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnsupportedEncodingException{
		if(SysUtility.isEmpty(MainDatas)){
			return "";
		}
		
		Map xmlRootMap = GetAnyXmlRootMap(bean, MainDatas, childsMap);
		
		if(SysUtility.isNotEmpty(bean.getRootDefault())){
			Map senderMap = GetSenderMap(bean, (HashMap)MainDatas.get(0));
			String messageTime = SysUtility.getSysDate();
			String messageSource = SysUtility.getMapField(senderMap, "MESSAGE_SOURCE");
			String messageDest = SysUtility.getMapField(senderMap, "MESSAGE_DEST");
			String messageVersion = (String)senderMap.get("MESSAGE_VERSION");
			String messageCategory = (String)senderMap.get("MESSAGE_CATEGORY");
			String signData = (String)senderMap.get("SIGN_DATA");
			String techRegCode = SysUtility.getMapField(senderMap, "TECH_REG_CODE");
			String extendXml = SysUtility.getMapField(senderMap, "EXTEND_XML");
			
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>").append("\n");
			xml.append("<"+bean.getRootDefault()+">").append("\n");
			xml.append("<MessageHead>").append("\n");
			xml.append("    <MESSAGE_ID>"+SysUtility.GetUUID()+"</MESSAGE_ID> ").append("\n");
			xml.append("    <MESSAGE_TYPE>"+bean.getMessageType()+"</MESSAGE_TYPE> ").append("\n");
			xml.append("    <MESSAGE_TIME>"+messageTime+"</MESSAGE_TIME> ").append("\n");
			xml.append("    <MESSAGE_SOURCE>"+bean.getMessageSource()+"</MESSAGE_SOURCE> ").append("\n");
			xml.append("    <MESSAGE_DEST>"+bean.getMessageDest()+"</MESSAGE_DEST> ").append("\n");
			if(SysUtility.isNotEmpty(messageVersion)){
				xml.append("    <MESSAGE_VERSION>"+messageVersion+"</MESSAGE_VERSION> ").append("\n");
			}
			if(SysUtility.isNotEmpty(messageCategory)){
				xml.append("    <MESSAGE_CATEGORY>"+messageCategory+"</MESSAGE_CATEGORY> ").append("\n");
			}
			if(SysUtility.isNotEmpty(signData)){
				xml.append("    <MESSAGE_SIGN_DATA>"+SysUtility.EncryptKeys(messageSource, messageTime,signData)+"</MESSAGE_SIGN_DATA> ").append("\n");
			}
			if(SysUtility.isNotEmpty(techRegCode)){
				xml.append("    <TECH_REG_CODE>"+techRegCode+"</TECH_REG_CODE> ").append("\n");
			}
			if(SysUtility.isNotEmpty(extendXml)){
				xml.append(extendXml).append("\n");
			}
			xml.append("  </MessageHead>").append("\n");
			xml.append("  <MessageBody>").append("\n");
			
			List list = (List)xmlRootMap.get(bean.getXmlRoot());
			for (int i = 0; i < list.size(); i++) {
				HashMap RootMap = (HashMap)list.get(i);
				xml.append(FileUtility.hashMapToAnyXml(bean.getXmlRoot(),RootMap, 1, bean.getMappingColumns()));
			}
			
			xml.append("  </MessageBody>").append("\n");
			xml.append("</"+bean.getRootDefault()+">").append("\n");
			return xml.toString();
		}else{
			return "<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(bean.getXmlRoot(),xmlRootMap, 0, bean.getMappingColumns());
		}
	}
	
	public  Map GetSenderMap(ServicesBean bean,HashMap MainData) throws LegendException, JSONException {
		Map senderMap = new HashMap();
		SQLBuild sqlBuild = SQLBuild.getInstance();
		if(SysUtility.isNotEmpty(bean.getMessageHeadSql())){
			JSONObject param = SysUtility.MapToJSONObject(MainData);
			SQLHolder holder = SQLParser.parse(bean.getMessageHeadSql(), param);
			senderMap = SQLExecUtils.query4Map(holder.getSql(),new SimpleParamSetter(holder.getParamList()));
		}else if("2".equals(bean.getServiceType())){
			//TODO 接口表模式，待扩展
		}else if("3".equals(bean.getServiceType())){
			//TODO api轮询模式，待扩展
		}else{
			sqlBuild.append("select * from exs_handle_sender where msg_flag = 0 ");
			sqlBuild.append("and msg_type = ?", bean.getMessageType());
			sqlBuild.append("and msg_no = ?", bean.getSerialNo());
			senderMap = sqlBuild.query4Map();
		}
		String messageSource = SysUtility.getMapField(senderMap, "MESSAGE_SOURCE");
		String messageDest = SysUtility.getMapField(senderMap, "MESSAGE_DEST");
		if(SysUtility.isNotEmpty(messageSource)){
			bean.setMessageSource(messageSource);
		}
		if(SysUtility.isNotEmpty(messageDest)){
			bean.setMessageDest(messageDest);
		}
		return senderMap;
	}
	
	public void DeleteMainData(ServicesBean bean,HashMap SQLMap,HashMap MainData) throws LegendException, JSONException {
		if("2".equals(bean.getServiceType())){
			List ListSql = bean.getListSql();
			HashMap sqlMap2 = (HashMap)ListSql.get(0);
			String DeleteSql2 = (String)SQLMap.get(SysUtility.getMapField(sqlMap2, "XML_DOCUMENT_NAME")+"_DELETE");
			DeleteData(DeleteSql2, MainData);
		}
	}
	
	public void DeleteChildData(ServicesBean bean,String DeleteSql,JSONObject params) throws LegendException, JSONException {
		if("2".equals(bean.getServiceType())){
			DeleteData(DeleteSql, params);
		}
	}
	
	public  boolean DeleteData(String DeleteSQL,Object obj) throws LegendException, JSONException {
		JSONObject root = null;
		if(obj instanceof JSONObject){
			root = (JSONObject)obj;
		}else if(obj instanceof Map){
			root = SysUtility.MapToJSONObject((HashMap)obj);
		}
		
		List pList = new  ArrayList();
		int i = 0;
		while(true){
			int bIndx = DeleteSQL.indexOf("#");
			if(bIndx > -1){
				int eIndx = DeleteSQL.indexOf("#", bIndx+1);
				String param = DeleteSQL.substring(bIndx+1, eIndx);
				if(SysUtility.isEmpty(SysUtility.getJsonField(root, param))){
					String partStr=DeleteSQL.substring(0,bIndx).trim();
					partStr=partStr.substring(0,  partStr.length()-1).trim();
					String field="";
					if(partStr.lastIndexOf(',')>-1){
						field=partStr.substring(partStr.lastIndexOf(',')+1).trim();
					}
					else{
						field=partStr.substring(partStr.lastIndexOf(' ')+1) ;
					}
					DeleteSQL = DeleteSQL.replaceAll("#"+param+"#", field);//如果回执报文中不存在这个字段需要的值就不更新
				}else{
					DeleteSQL = DeleteSQL.replaceAll("#"+param+"#", "?");
					pList.add(SysUtility.getJsonField(root, param));
				}
			}else{
				break;//没有需要替换的参数
			}
		}
		String[] params = new String[pList.size()];
		for (int j = 0; j < pList.size(); j++) {
			params[j] = (String)pList.get(j);
		}
		return SQLExecUtils.executeUpdate(SysUtility.getCurrentConnection(), DeleteSQL, params);
	}
	
	public  HashMap CacheChildData(ServicesBean bean, IDataAccess DataAccess,List MainDatas) throws LegendException{
		if(SysUtility.isEmpty(DataAccess)) {
			return new HashMap();
		}
		
		String[] RootNames = bean.getRootNames().split(",");
		HashMap<String,String> mapSql = bean.getMapSql();
		String Indx = bean.getIndxName().toUpperCase();
		String Pindx = bean.getPindxName().toUpperCase();
		
		DataAccess.BeginTrans();
		HashMap childsMap = new HashMap();
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap map = (HashMap)MainDatas.get(i); 
			if(SysUtility.isEmpty(map.get(bean.getSerialName()))){
				LogUtil.printLog("主sql查询中中未返回关键字段："+bean.getSerialName()+"的值", Level.INFO);
				continue;
			}
			String indexValue = (String)map.get(Indx);
			DataAccess.ExecSQL(INSERT_TEMP_ID5,new String[]{indexValue,(String)map.get("ID1"),(String)map.get("ID2"),(String)map.get("ID3"),(String)map.get("ID4"),(String)map.get("ID5")});
		}
		for (int i = 1; i < RootNames.length; i++) {
			String ChildSQL = mapSql.get(RootNames[i]);
			if(SysUtility.isEmpty(ChildSQL)){
				ChildSQL = SQLMap.getSelect(RootNames[i]);
			}
			if(SysUtility.isEmpty(ChildSQL)){
				continue;
			}
			if(ChildSQL.toUpperCase().indexOf("EXS_TEMP_ID") < 0){
				String p_indx = "p_indx";
				String PindxName = bean.getPindxName();
				if(SysUtility.isNotEmpty(PindxName) && PindxName.split(",").length >= RootNames.length - 1){
					p_indx = PindxName.split(",")[i-1];
				}
				ChildSQL = "select x_.* from("+ChildSQL+")x_,exs_temp_id g where g.id = x_."+p_indx;
			}
			List childList = SQLExecUtils.query4List(ChildSQL);
			if(childList.size() <= 0 ){
				continue;
			}
			HashMap childMap = new HashMap();
			ExsUtility.groupChild(childList, childMap, Pindx);
			childsMap.put(RootNames[i], childMap);
		}
		//缓存
		HashMap MappingColumns = bean.getMappingColumns();
		HashMap sqlMap = bean.getMapSql();
		for (int i = 0; i < RootNames.length; i++) {
			String xmlSql = (String)sqlMap.get(RootNames[i]);
			String[] mappingStrs = SysUtility.getTableColumns(xmlSql);
			MappingColumns.put(RootNames[i], mappingStrs);
		}
		return childsMap;
	}

	public List GetMainDatas(ServicesBean bean,IDataAccess DataAccess,HashMap childsMap) throws Exception{
		if(SysUtility.isEmpty(bean.getRootNames())) {
			LogUtil.printLog("RootNames不能为空，请配置！", Level.ERROR);
			return new ArrayList<>();
		}
		if(SysUtility.isEmpty(bean.getDbType())) {
			LogUtil.printLog("DbType不能为空，请配置！", Level.ERROR);
			return new ArrayList<>();
		}
		String[] RootNames = bean.getRootNames().split(",");
		String MainNames = RootNames[0];
		HashMap<String,String> mapSql = bean.getMapSql();
		String MainSQL = SysUtility.isEmpty(mapSql.get(MainNames))?SQLMap.getSelect(MainNames):mapSql.get(MainNames);
		if(SysUtility.isEmpty(MainSQL)){
			LogUtil.printLog("MainSQL不能为空，请配置！", Level.ERROR);
			return new ArrayList<>();
		}

		SQLHolder holder = SQLParser.parse(MainSQL, bean.getSearchParam());
		java.sql.Connection conn = SysUtility.getCurrentDynamicConnection(bean.getIndx());
		if(SysUtility.isEmpty(conn)) {
			LogUtil.printLog("数据库连接获取失败，请检查！"+bean.getDbDriverUrl(), Level.ERROR);
			return new ArrayList<>();
		}
		List MainDatas = SQLExecUtils.query4List(conn, holder.getSql(),new SimpleParamSetter(holder.getParamList()),bean.getBlobProcess());
		return MainDatas;
	}
	
	public  String XmlToDatas(ServicesBean bean,Datas datas,Map hmSourceData) throws LegendException, IOException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		String processMsg = "";
		String RootMethodInvoke = bean.getRootMethodInvoke();
		String ChildMethodInvoke = bean.getChildMethodInvoke();
		
		List listSql = bean.getListSql();
		String[] RootNames = bean.getRootNames().split(",");
		String RootName = RootNames[0];//主表节点
		String[] DBTableNames = bean.getDbTableNames().split(",");
		
		datas.MapToDatas(ExsConstants.MessageHead, hmSourceData);
		datas.MapToDatas(RootName,hmSourceData, ExsUtility.GetMappingMap(listSql, RootName));
		ExsUtility.BlobProcess(bean.getBlobProcess(),datas,RootNames[0],DBTableNames[0]);
		for (int i = 1; i < RootNames.length; i++) {
			/*********填充子表数据***********************/
			datas.MapToDatas(RootNames[i],hmSourceData, ExsUtility.GetMappingMap(listSql, RootNames[i]));
			/*********处理子表Blob字段信息***********************/
			ExsUtility.BlobProcess(bean.getBlobProcess(),datas,RootNames[i],DBTableNames[i]);
		}
		return processMsg;
	}
	
	public void setDBToXmlMap(ServicesBean bean,final String Indx,Datas datas) throws LegendException{
		setDBToXmlMap(bean, Indx, datas, null);
	}
	
	public void setDBToXmlMap(ServicesBean bean,final String Indx,Datas datas,List SQLList) throws LegendException{
		if(SysUtility.isEmpty(SQLList)) {
			String SelectSQL = "SELECT S.* FROM exs_config_dbtoxml_sql S WHERE IS_ENABLED = '1' AND P_INDX = ? ORDER BY SEQ_NO";
			SQLList = SQLExecUtils.query4List(SelectSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, Indx);
				}
			});
		}
		if(SysUtility.isEmpty(SQLList) || SQLList.size() == 0){
			LogUtil.printLog("exs_config_dbtoxml配置出错:exs_config_dbtoxml.INDX"+Indx, Level.INFO);
			return;
		}

		HashMap<String,String> SQLMap = new HashMap<String,String>();
		StringBuffer strRootName = new StringBuffer();
		for (int j = 0; j < SQLList.size(); j++) {
			HashMap temp = (HashMap)SQLList.get(j);
			String TableName = SysUtility.getMapField(temp, "TABLE_NAME");
			String IndxName = SysUtility.getMapField(temp, "INDX_NAME");
			String PIndxName = SysUtility.getMapField(temp, "PINDX_NAME");

			String PIndxNameValue = "";
			if(SysUtility.isNotEmpty(PIndxName)) {
				HashMap tempPre = (HashMap)SQLList.get(j-1);
				String xmlCoulmnMapping = SysUtility.getMapField(tempPre, "XML_COULMN_MAPPING");
				PIndxNameValue = SysUtility.getMapField(tempPre, "INDX_NAME");
				//转换
				if(SysUtility.isNotEmpty(xmlCoulmnMapping)) {
					String[] xmlCoulmnMappings = xmlCoulmnMapping.split(",");
					for (int z = 0; z < xmlCoulmnMappings.length; z++) {
						String[] temp2 = xmlCoulmnMappings[z].split("=");
						if(PIndxNameValue.equals(temp2[0])) {
							PIndxNameValue = PIndxNameValue.replace(temp2[0], temp2[1]);
							break;
						}
					}
				}
			}

			if(SysUtility.isEmpty(bean.getServiceType())) {//SQL模式--驱动表
				if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_SQL"))) {//SQL配置模式
					temp.put("XML_SQL", getMainChildSql_2(bean, temp, strRootName));
				}else if(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL")) && SysUtility.isEmpty(PIndxName)) {//Table配置模式-主表
					if(SysUtility.isNotEmpty(bean.getPartId())){//主表，以part_id区分配置，从索引表exs_handle_sender读取数据
						temp.put("XML_SQL", getTableMainSql_2(bean, TableName, IndxName));
					}else{//主表，从索引表exs_handle_sender读取数据
						temp.put("XML_SQL", getTableMainSql_1(bean, TableName, IndxName));
					}
				}else if(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL")) && SysUtility.isNotEmpty(PIndxName)) {//Table配置模式-子表
					if(SysUtility.isNotEmpty(TableName)){
						temp.put("XML_SQL", getTableChildSql_1(TableName, PIndxName, PIndxNameValue));
					}else if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_SQL"))){
						temp.put("XML_SQL", getChildSql_1(temp));
					}
				}
			}else if("1".equals(bean.getServiceType())){//SQL模式--api
				if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_SQL"))) {//sql形式
					if(j == 0){//主表
						temp.put("XML_SQL", getOpenApiMainSql(bean, datas, temp));
					}else {
						temp.put("XML_SQL", getMainChildSql_2(bean, temp, strRootName));
					}
				}else {//TableName形式
					if(SysUtility.isEmpty(PIndxName)){//主表
						temp.put("XML_SQL", getOpenApiMainSql(bean, datas, temp));
					}else{//子表
						if(SysUtility.isNotEmpty(TableName)){
							temp.put("XML_SQL", getTableChildSql_1(TableName, PIndxName, PIndxNameValue));
						}else if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_SQL"))){
							temp.put("XML_SQL", getChildSql_1(temp));
						}
					}
				}
			}else if("2".equals(bean.getServiceType())){//SQL模式--元数据取后删除
				if(SysUtility.isEmpty(PIndxName)){
					temp.put("XML_SQL", "select c.* from "+TableName+" c ");
					temp.put("XML_DELETE_SQL", "delete from "+TableName+" c where "+IndxName+" = #"+IndxName+"#");
				}else{
					temp.put("XML_SQL", getTableChildSql_1(TableName, PIndxName, null));
					temp.put("XML_DELETE_SQL", "delete from "+TableName+" where "+PIndxName+" = #"+PIndxName+"#");
				}
			}
			SQLMap.put(SysUtility.getMapField(temp, "XML_DOCUMENT_NAME"), (String)SysUtility.getMapField(temp, "XML_SQL"));
			SQLMap.put(SysUtility.getMapField(temp, "XML_DOCUMENT_NAME")+"_DELETE", (String)temp.get("XML_DELETE_SQL"));

			if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_DOCUMENT_NAME"))){
				strRootName.append(SysUtility.getMapField(temp, "XML_DOCUMENT_NAME")).append(",");
			}
		}
		if(SysUtility.isNotEmpty(strRootName)){
			bean.setRootNames(strRootName.toString());//Edi模式，RootNames从子表拼接
		}
		bean.setListSql(SQLList);
		bean.setMapSql(SQLMap);//取自定义配置的SQL
	}
	
	public void InitExsDbtoxmlSql(ServicesBean bean,HashMap temp,StringBuffer strRootName,String TableName,String IndxName,String PIndxName,String PIndxNameValue){
		if(SysUtility.isEmpty(TableName)){//SQL配置模式
			temp.put("XML_SQL", getMainChildSql_2(bean, temp, strRootName));
		}else{//Table配置模式
			if(SysUtility.isEmpty(PIndxName)){//主表
				if(SysUtility.isNotEmpty(bean.getPartId())){//主表，以part_id区分配置，从索引表exs_handle_sender读取数据
					temp.put("XML_SQL", getTableMainSql_2(bean, TableName, IndxName));
				}else{//主表，从索引表exs_handle_sender读取数据
					temp.put("XML_SQL", getTableMainSql_1(bean, TableName, IndxName));
				}
			}else{//子表
				if(SysUtility.isNotEmpty(TableName)){
					temp.put("XML_SQL", getTableChildSql_1(TableName, PIndxName, PIndxNameValue));
				}else if(SysUtility.isNotEmpty(SysUtility.getMapField(temp, "XML_SQL"))){
					temp.put("XML_SQL", getChildSql_1(temp));
				}
			}
		}
	}
	
	public  String getMainChildSql_1(ServicesBean bean,HashMap temp,int j){
		StringBuffer xmlSql = new StringBuffer();
		xmlSql.append(SysUtility.getMapField(temp, "XML_SQL")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL1"))?"":SysUtility.getMapField(temp, "XML_SQL1")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL2"))?"":SysUtility.getMapField(temp, "XML_SQL2")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL3"))?"":SysUtility.getMapField(temp, "XML_SQL3"));
		if(SysUtility.isNotEmpty(xmlSql.toString()) && j == 0){
			//主表SQL处理
			xmlSql.insert(0, "select x_.* from(");
			xmlSql.append(")x_,exs_handle_sender y_ where x_.").append(bean.getSerialName()).append(" = y_.msg_no and y_.msg_flag = '0' and y_.msg_type = '").append(bean.getMessageType()).append("' and rownum < 5000");
		}
		return xmlSql.toString();
	}
	
	public  String getMainChildSql_2(ServicesBean bean,HashMap temp,StringBuffer strRootName){
		StringBuffer xmlSql = new StringBuffer();
		xmlSql.append(SysUtility.getMapField(temp, "XML_SQL")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL1"))?"":SysUtility.getMapField(temp, "XML_SQL1")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL2"))?"":SysUtility.getMapField(temp, "XML_SQL2")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL3"))?"":SysUtility.getMapField(temp, "XML_SQL3"));
		if(SysUtility.isNotEmpty(xmlSql.toString()) && SysUtility.isEmpty(strRootName) && xmlSql.toString().toUpperCase().indexOf("exs_handle_sender") < 0 && !ExsConstants.EdiItowNet.equals(bean.getServiceMode())){
			xmlSql.insert(0, "select x_.* from(");
			xmlSql.append(")x_,exs_handle_sender y_ where x_.").append(bean.getSerialName()).append(" = y_.msg_no and y_.msg_flag = '0' and y_.msg_type = '").append(bean.getMessageType()).append("'");
			if(SysUtility.IsOracleDB()) {
				xmlSql.append(" and rownum < 5000");
			}else if(SysUtility.IsMySqlDB()) {
				xmlSql.append(" limit 5000");
			}
		}
		return xmlSql.toString();
	}
	
	public  String getChildSql_1(HashMap temp){
		return ""+(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL"))?"":SysUtility.getMapField(temp, "XML_SQL"))+(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL1"))?"":SysUtility.getMapField(temp, "XML_SQL1"))+(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL2"))?"":SysUtility.getMapField(temp, "XML_SQL2"))+(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL3"))?"":SysUtility.getMapField(temp, "XML_SQL3"));
	}
	
	public  String getTableMainSql_1(ServicesBean bean,String TableName,String IndxName){
		if(SysUtility.IsMySqlDB()) {
			return "select c.* from "+TableName+" c,exs_handle_sender e where e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName+" limit 5000";
		}else if(SysUtility.IsSQLServerDB()) {
			return "select top 5000 c.* from "+TableName+" c,exs_handle_sender e where e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName;
		}else {
			return "select c.* from "+TableName+" c,exs_handle_sender e where e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName+" and rownum <= 5000";
		}
	}
	
	public  String getTableMainSql_2(ServicesBean bean,String TableName,String IndxName){
		if(SysUtility.IsMySqlDB()) {
			return "select c.* from "+TableName+" c,exs_handle_sender e where e.part_id = '"+bean.getPartId()+"' and e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName+" limit 5000";
		}else if (Constants.Sqlserver.equalsIgnoreCase(SysUtility.GetSetting("System", "DBType"))) {
			return "select top 5000 c.* from "+TableName+" c,exs_handle_sender e where e.part_id = '"+bean.getPartId()+"' and e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName;
		}else {
			return "select c.* from "+TableName+" c,exs_handle_sender e where e.part_id = '"+bean.getPartId()+"' and e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName+" and rownum <= 5000";
		}
	}
	
	public  String getTableChildSql_1(String TableName,String PIndxName,String PIndxNameValue){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from "+TableName+" where 1 = 1");
		String[] PIndxNames = PIndxName.split(",");
		for (int i = 0; i < PIndxNames.length; i++) {
			String tempPIndx = PIndxNames[i];
			sql.append(" and "+tempPIndx+" = #"+PIndxNameValue+"#");
		}
		return sql.toString();
	}
	
	public  String getOpenApiMainSql(ServicesBean bean,Datas datas,HashMap temp){
		StringBuffer mainSQL = new StringBuffer();
		try {
			String dataFilterReg = bean.getDataFilterReg();
			String receivedMode = datas.GetTableValue(ExsConstants.MessageHead, "RECEIVED_MODE");
			String messageSource = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
			String messageType = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_TYPE");
			
			String DBType = SysUtility.GetSetting("System", "DBType");
			if (Constants.Sqlserver.equalsIgnoreCase(DBType)) {
				
			}else {
				
			}
			
			String TableName = (String)temp.get("TABLE_NAME");
			bean.setMainTableName(TableName);
			if(SysUtility.isNotEmpty(TableName)){
				mainSQL.append("select * from "+TableName+" where 1 = 1");
			}else{
				mainSQL.append(SysUtility.getMapField(temp, "XML_SQL")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL1"))?"":SysUtility.getMapField(temp, "XML_SQL1")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL2"))?"":SysUtility.getMapField(temp, "XML_SQL2")).append(SysUtility.isEmpty(SysUtility.getMapField(temp, "XML_SQL3"))?"":SysUtility.getMapField(temp, "XML_SQL3"));
				if(mainSQL.toString().toLowerCase().indexOf("where") < 0) {
					mainSQL.append(" where 1 = 1");
				}
			}
			mainSQL.append(" and message_count < 10 ");
			mainSQL.append(" and message_source = '"+datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE)+"'");
			if(SysUtility.isEmpty(receivedMode)) {
				mainSQL.append(" and rownum = 1 ");
			}else{
				mainSQL.append(" and rownum <= "+(50*Integer.parseInt(receivedMode))+" ");
			}
			if(SysUtility.isNotEmpty(dataFilterReg)){
				JSONObject mainData = datas.GetTable("MessageBody");
				if(SysUtility.isEmpty(mainData)) {
					mainData = datas.GetTable("SEARCH_TABLE");
				}
				if(dataFilterReg.indexOf("and") >= 0 || dataFilterReg.indexOf("AND") >= 0 || dataFilterReg.indexOf("And") >= 0){
					SQLHolder holder = SQLParser.parse(" "+dataFilterReg+" ", mainData);
					String tempSql = holder.getSql();
					SqlParamList paramList = holder.getParamList();
					for (int i = 0; i < paramList.size(); i++) {
						SqlParameter param =  paramList.get(i);
						String value = param.getParamValue();
						tempSql = tempSql.replaceFirst("\\?", "\'"+value+"\'");
					}
					mainSQL.append(tempSql);
				}else{
					Iterator<?> keys = mainData.keys();
					while(keys.hasNext()){
						String key = keys.next().toString();
						if(SysUtility.isNotEmpty(dataFilterReg) && dataFilterReg.indexOf(key) >= 0){
							mainSQL.append(" and "+key+ " = '"+SysUtility.getJsonField(mainData, key)+"'");
						}
					}
				}
			}
		} catch (LegendException e) {
			mainSQL.delete(0, mainSQL.length());
			LogUtil.printLog("OpenApi MainSql初始化出错！"+e.getMessage(), Level.ERROR);
		}
		return mainSQL.toString();
	}
	
	public  List getLocalDBToXmlConfig() {
		List LocalList = new ArrayList();
		
		HashMap push = new HashMap();
		push.put("INDX", "1");
		push.put("QUARTZ_NAME", "Exs_DBToXml");
		push.put("DATA_TYPE", "AnyXml");
		push.put("MESSAGE_TYPE", "PushReceived");
		push.put("ROOT_DEFAULT", "OBORMessage");
		push.put("SERIAL_NAME", "INDX");
		push.put("SOURCE_PATH", "{MainFolder}/PushReceived/Source");
		HashMap pushMap = new HashMap();
		pushMap.put("INDX", "1");
		pushMap.put("P_INDX", "1");
		pushMap.put("SEQ_NO", "1");
		pushMap.put("XML_DOCUMENT_NAME", "PushReceived");
		pushMap.put("TABLE_NAME", "EXS_PUSH_RECEIVED");
		pushMap.put("INDX_NAME", "INDX");
		pushMap.put("XML_COULMN_MAPPING", "MSG_ID=MsgId,MSG_TYPE=MsgType,MSG_NO=MsgNo,MSG_CODE=MsgCode,MSG_NAME=MsgName,MSG_DESC=MsgDesc,MSG_TIME=MsgTime");
		pushMap.put("XML_COULMN_HIDDEND", "MSG_FLAG,CREATE_TIME,REC_VER,MESSAGE_SOURCE,MESSAGE_DEST");
		List pushLst = new ArrayList();
		pushLst.add(pushMap);
		push.put("CHILD",pushLst);
		LocalList.add(push);
		
		HashMap pull = new HashMap();
		pull.put("INDX", "2");
		pull.put("QUARTZ_NAME", "Exs_DBToXml");
		pull.put("DATA_TYPE", "AnyXml");
		pull.put("MESSAGE_TYPE", "PullReceived");
		pull.put("ROOT_DEFAULT", "OBORMessage");
		pull.put("SERIAL_NAME", "INDX");
		pull.put("SOURCE_PATH", "{MainFolder}/PullReceived/Source");
		HashMap pullMap = new HashMap();
		pullMap.put("INDX", "2");
		pullMap.put("P_INDX", "2");
		pullMap.put("SEQ_NO", "1");
		pullMap.put("XML_DOCUMENT_NAME", "PullReceived");
		pullMap.put("XML_PARENT_DOCUMENT", "");
		pullMap.put("TABLE_NAME", "EXS_PULL_RECEIVED");
		pullMap.put("INDX_NAME", "INDX");
		pullMap.put("XML_COULMN_MAPPING", "MSG_ID=MsgId,MSG_TYPE=MsgType,MSG_NO=MsgNo,MSG_CODE=MsgCode,MSG_NAME=MsgName,MSG_DESC=MsgDesc,MSG_TIME=MsgTime");
		pullMap.put("XML_COULMN_HIDDEND", "MSG_FLAG,CREATE_TIME,REC_VER,MESSAGE_SOURCE,MESSAGE_DEST");
		List pullLst = new ArrayList();
		pullLst.add(pullMap);
		pull.put("CHILD",pullLst);
		LocalList.add(pull);
		
		return LocalList;
	}
	
	public  List getLocalXmlToDBConfig() throws LegendException {
		String XmlToDBSQL = "SELECT DISTINCT RECEIPT_TYPE FROM exs_config_xmltodb WHERE nvl(IS_ENABLED,'1') = '1' AND QUARTZ_NAME = ?";
		List receiptTypeList = SQLExecUtils.query4StringList(XmlToDBSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SysUtility.getCurrentCronJobName());
			}
		});
		
		List LocalList = new ArrayList();
		if(!receiptTypeList.contains("PushReceived")) {
			HashMap push = new HashMap();
			push.put("INDX", "1");
			push.put("QUARTZ_NAME", "Exs_XmlToDB");
			push.put("DATA_TYPE", "AnyXml");
			push.put("MESSAGE_TYPE", "PushReceived");
			push.put("SERIAL_NAME", "INDX");
			push.put("SOURCE_PATH", "{MainFolder}/PushReceived/Source");
//			push.put("MQ_TYPE", "");
//			push.put("CONSUMER_URL", "");
//			push.put("CONSUMER_USER", "");
//			push.put("CONSUMER_PWD", "");
//			push.put("SOURCE_MQ", "");
			HashMap pushMap = new HashMap();
			pushMap.put("INDX", "1");
			pushMap.put("P_INDX", "1");
			pushMap.put("XML_DOCUMENT_NAME", "PushReceived");
			pushMap.put("TABLE_NAME", "EXS_PUSH_RECEIVED");
			pushMap.put("INDX_NAME", "INDX");
			pushMap.put("XML_COULMN_MAPPING", "MSG_ID=MsgId,MSG_TYPE=MsgType,MSG_NO=MsgNo,MSG_CODE=MsgCode,MSG_NAME=MsgName,MSG_DESC=MsgDesc,MSG_TIME=MsgTime");
			List pushLst = new ArrayList();
			pushLst.add(pushMap);
			push.put("CHILD",pushLst);
			LocalList.add(push);
		}
		
		if(!receiptTypeList.contains("PullReceived")) {
			HashMap pull = new HashMap();
			pull.put("INDX", "2");
			pull.put("QUARTZ_NAME", "Exs_XmlToDB");
			pull.put("DATA_TYPE", "AnyXml");
			pull.put("MESSAGE_TYPE", "PullReceived");
			pull.put("SERIAL_NAME", "INDX");
			pull.put("SOURCE_PATH", "{MainFolder}/PullReceived/Source");
			HashMap pullMap = new HashMap();
			pullMap.put("INDX", "2");
			pullMap.put("P_INDX", "2");
			pullMap.put("XML_DOCUMENT_NAME", "PullReceived");
			pullMap.put("TABLE_NAME", "EXS_PULL_RECEIVED");
			pullMap.put("INDX_NAME", "INDX");
			pullMap.put("XML_COULMN_MAPPING", "MSG_ID=MsgId,MSG_TYPE=MsgType,MSG_NO=MsgNo,MSG_CODE=MsgCode,MSG_NAME=MsgName,MSG_DESC=MsgDesc,MSG_TIME=MsgTime");
			List pullLst = new ArrayList();
			pullLst.add(pullMap);
			pull.put("CHILD",pullLst);
			LocalList.add(pull);
		}
		
		
		return LocalList;
	}
	
	
	//exs_cluster_app(indx,ip_address,app_context,app_name,status,create_time,modify_time)
	//每个app服务轮询当前表，0、app_context获取到的数据，ip_address如果与当前机器不相同，不启动当前应用定时。 1、如果不存在则创建，2、如果存在则更新修改时间，3、如果当前时间超过修改时间10分钟，则自动删除此类数据。
	
	//exs_cluster_task(indx,exs_type,app_context,source_path,file_name,process_flag,create_time)
	//
	public void XmlToClusterTask(ServicesBean bean,IDataAccess DataAccess) throws LegendException, IOException{
		String XmlToDBSQL = "SELECT * FROM exs_config_xmltodb WHERE nvl(IS_ENABLED,'1') = '1' AND NVL(DB_TYPE,'Exs') = 'Exs'";
		
		String ExistSQL = "select 0 from exs_cluster_task where exs_type = ? and app_context = ? and source_path = ? and file_name = ?";
		final String ExsType = "XmlToDB";
		final String appContext = AppContext.getContextPath();
		
		List XmlToDBList = SQLExecUtils.query4List(XmlToDBSQL);
		for (int i = 0; i < XmlToDBList.size(); i++) {
			HashMap map = (HashMap)XmlToDBList.get(i);
			final String SourcePath = ExsUtility.replaceBeanPath((String)map.get("SOURCE_PATH"));
			
			long s1 = Runtime.getRuntime().freeMemory();
			System.out.println("已分配内存中的剩余空间 (前) = " + s1);
			File files[] = new File(SourcePath).listFiles(new FileFilterHandle());
			long s2 = Runtime.getRuntime().freeMemory();
			System.out.println("已分配内存中的剩余空间 (后) = " + s2);
			System.out.println("消耗量 = " + ((s1 - s2)/1024/1024)+ "MB");
			
			for (int j = 0; j < files.length; j++) {
				if (!files[j].isDirectory()){
					try {
						final String FileName = files[j].getName();
						String rt = SQLExecUtils.query4String(ExistSQL, new Callback() {
							@Override
							public void doIn(PreparedStatement ps) throws SQLException {
								ps.setString(1, ExsType);
								ps.setString(2, appContext);
								ps.setString(3, SourcePath);
								ps.setString(4, FileName);
							}
						});
						if(SysUtility.isEmpty(rt)){
							JSONObject row = new JSONObject();
							row.put("EXS_TYPE", ExsType);
							row.put("APP_CONTEXT", appContext);
							row.put("SOURCE_PATH", SourcePath);
							row.put("FILE_NAME", FileName);
							DataAccess.Insert("EXS_CLUSTER_TASK", row);
							DataAccess.ComitTrans();
						}
					} catch (Exception e) {
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					}
				}/*else{
					String folderName = files[i].getName();
					String tempSourcePath = createFolder(SourcePath, folderName);
					File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
					for (int k = 0; k < files2.length; k++) {
						try {
							
						} catch (Exception e) {
							LogUtil.printLog(e.getMessage(), Level.ERROR);
						}
					}
				}*/
			}
			
		}
	}



	private void setDBToXml2Bean(ServicesBean tempbean, Map map)throws Exception{
		tempbean.setIndx(SysUtility.getMapField(map, "INDX"));
		tempbean.setDataType(SysUtility.getMapField(map, "DATA_TYPE"));
		tempbean.setRootDefault(SysUtility.getMapField(map, "ROOT_DEFAULT"));
		tempbean.setMessageType(SysUtility.getMapField(map, "MESSAGE_TYPE"));
		tempbean.setSerialName(SysUtility.getMapField(map, "SERIAL_NAME"));
		tempbean.setMqType(SysUtility.getMapField(map, "MQ_TYPE"));
		tempbean.setProducerUrl(SysUtility.getMapField(map, "PRODUCER_URL"));
		tempbean.setProducerUser(SysUtility.getMapField(map, "PRODUCER_USER"));
		tempbean.setProducerPwd(SysUtility.getMapField(map, "PRODUCER_PWD"));
		tempbean.setTargetMq(SysUtility.getMapField(map, "TARGET_MQ"));
		tempbean.setActiveMqMode(SysUtility.getMapField(map, "MQ_MODE"));
		tempbean.setClassInvoke(SysUtility.getMapField(map, "CLASS_INVOKE"));
		tempbean.setRootMethodInvoke(SysUtility.getMapField(map, "ROOT_METHOD_INVOKE"));
		tempbean.setChildMethodInvoke(SysUtility.getMapField(map, "CHILD_METHOD_INVOKE"));
		tempbean.setSourcePath(SysUtility.getMapField(map, "SOURCE_PATH"));
		tempbean.setFileNameReg(SysUtility.getMapField(map, "FILE_NAME_REG"));
		tempbean.setEncoding(SysUtility.getMapField(map, "ENCODING"));
		tempbean.setMessageHeadSql(SysUtility.getMapField(map, "MESSAGE_HEAD_SQL"));
		tempbean.setBlobProcess(Integer.parseInt(SysUtility.getMapField(map, "BLOB_PROCESS")));
		tempbean.setReceivedPath(SysUtility.getMapField(map, "RECEIVED_PATH"));
		tempbean.setTempPath(SysUtility.getMapField(map, "TEMP_PATH"));
		tempbean.setDbType(SysUtility.getMapField(map, "DB_TYPE"));
		tempbean.setDbDriverUrl(SysUtility.getMapField(map, "DB_DRIVER_URL"));
		tempbean.setDbUser(SysUtility.getMapField(map, "DB_USER"));
		tempbean.setDbPassword(SysUtility.getMapField(map, "DB_PASSWORD"));
	}

	private void setXmlToDB2Bean(ServicesBean tempbean, Map map)throws Exception{
		tempbean.setIndx(SysUtility.getMapField(map, "INDX"));
		tempbean.setDataType(SysUtility.getMapField(map, "DATA_TYPE"));
		tempbean.setMessageType(SysUtility.getMapField(map, "MESSAGE_TYPE"));
		tempbean.setSerialName(SysUtility.getMapField(map, "SERIAL_NAME"));
		tempbean.setRootNames(SysUtility.getMapField(map, "ROOT_NAMES"));
		tempbean.setDbTableNames(SysUtility.getMapField(map, "DB_TABLE_NAMES"));
		tempbean.setMqType(SysUtility.getMapField(map, "MQ_TYPE"));
		tempbean.setConsumerUrl(SysUtility.getMapField(map, "CONSUMER_URL"));
		tempbean.setConsumerUser(SysUtility.getMapField(map, "CONSUMER_USER"));
		tempbean.setConsumerPwd(SysUtility.getMapField(map, "CONSUMER_PWD"));
		tempbean.setSourceMq(SysUtility.getMapField(map, "SOURCE_MQ"));
		tempbean.setThreadCount(SysUtility.getMapField(map, "THREAD_COUNT"));
		tempbean.setClassInvoke(SysUtility.getMapField(map, "CLASS_INVOKE"));
		tempbean.setRootMethodInvoke(SysUtility.getMapField(map, "ROOT_METHOD_INVOKE"));
		tempbean.setChildMethodInvoke(SysUtility.getMapField(map, "CHILD_METHOD_INVOKE"));
		tempbean.setPointCode(SysUtility.getMapField(map, "POINT_CODE"));
		tempbean.setSourcePath(SysUtility.getMapField(map, "SOURCE_PATH"));
		tempbean.setTargetPath(SysUtility.getMapField(map, "TARGET_PATH"));
		tempbean.setErrorPath(SysUtility.getMapField(map, "ERROR_PATH"));
		tempbean.setHitPath(SysUtility.getMapField(map, "HIT_PATH"));
		tempbean.setTempPath(SysUtility.getMapField(map, "TEMP_PATH"));
		tempbean.setReceivedPath(SysUtility.getMapField(map, "RECEIVED_PATH"));
		tempbean.setUpdateSql(SysUtility.getMapField(map, "UPDATE_SQL"));
		tempbean.setIndxName(SysUtility.getMapField(map, "INDX_NAME"));
		tempbean.setPindxName(SysUtility.getMapField(map, "PINDX_NAME"));
		tempbean.setReceiptType(SysUtility.getMapField(map, "RECEIPT_TYPE"));
		tempbean.setEncoding(SysUtility.getMapField(map, "ENCODING"));
		tempbean.setExistsReturn("1".equals(SysUtility.getMapField(map, "EXISTS_RETURN"))?true:false);
		tempbean.setBlobProcess(Integer.parseInt(SysUtility.getMapField(map, "BLOB_PROCESS")));
		tempbean.setDbType(SysUtility.getMapField(map, "DB_TYPE"));
		tempbean.setDbDriverUrl(SysUtility.getMapField(map, "DB_DRIVER_URL"));
		tempbean.setDbUser(SysUtility.getMapField(map, "DB_USER"));
		tempbean.setDbPassword(SysUtility.getMapField(map, "DB_PASSWORD"));
	}

	private void setXmlToDBMap(ServicesBean entity, String Indx)throws Exception{
		String SelectSQL = "select s.* from exs_config_xmltodb_map s where is_enabled = '1' and p_indx = ? order by seq_no,indx";
		List listSql = SQLExecUtils.query4List(SelectSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, Indx);
			}
		});
		entity.setListSql(listSql);

		StringBuffer strXmlDocumentName = new StringBuffer();
		StringBuffer dbTableNames = new StringBuffer();
		StringBuffer dbIndxNames = new StringBuffer();
		for (int i = 0; i < listSql.size(); i++) {
			HashMap map = (HashMap)listSql.get(i);
			strXmlDocumentName.append(SysUtility.getMapField(map, "XML_DOCUMENT_NAME")).append(",");
			dbTableNames.append(SysUtility.getMapField(map, "TABLE_NAME")).append(",");
			dbIndxNames.append(SysUtility.getMapField(map, "INDX_NAME")).append(",");
		}
		if(SysUtility.isNotEmpty(strXmlDocumentName)){
			entity.setRootNames(strXmlDocumentName.toString());
		}
		if(SysUtility.isNotEmpty(dbTableNames)) {
			entity.setDbTableNames(dbTableNames.toString());
		}
		if(SysUtility.isNotEmpty(dbIndxNames)) {
			entity.setIndxName(dbIndxNames.toString());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
