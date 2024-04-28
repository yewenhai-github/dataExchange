package com.easy.api.utility;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.api.entity.ActiveXmlBean;
import com.easy.app.constants.ExsConstants;
import com.easy.app.core.AbstractCalculateEngine;
import com.easy.app.core.MysqlCalculateEngine;
import com.easy.app.core.OracleCalculateEngine;
import com.easy.app.core.SqlserverCalculateEngine;
import com.easy.app.utility.ApiClientUtility;
import com.easy.app.utility.ConvertUtility;
import com.easy.app.utility.ExsUtility;
import com.easy.constants.Constants;
import com.easy.entity.ServicesBean;
import com.easy.exception.ErrorCode;
import com.easy.exception.LegendException;
import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.security.AESGenerator;
import com.easy.utility.EntityUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.utility.ZipUtil;

public class QuartzApiUtility {
	private static AbstractCalculateEngine engine;
	public QuartzApiUtility() {
		String dbType = SysUtility.GetSetting("System", "DBType");
		if (Constants.Oracle.equalsIgnoreCase(dbType)){
			engine = new OracleCalculateEngine();
		} else if (Constants.Mysql.equalsIgnoreCase(dbType)) {
			engine = new MysqlCalculateEngine();
		} else if (Constants.Sqlserver.equalsIgnoreCase(dbType)) {
			engine = new SqlserverCalculateEngine();
		} 
	}
	
	//ApiToC C:代表一种承载介质，可以是MQ、文件夹、数据库等。
	public static void ApiToC(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.ApiPollingToInvoke.equals(serviceMode)){
			ApiPollingToClientCore(DataAccess);//隐藏接口
		}else if(ExsConstants.ApiPollingToDB.equals(serviceMode)){
			ApiPollingToDBCore(DataAccess);
		}else if(ExsConstants.ApiPollingToPush.equals(serviceMode)){
			ApiPollingToPushCore(DataAccess);
		}
	}
	
	public static void ApiPollingToClientCore(IDataAccess DataAccess)throws Exception{
		List list = getExsConfigApiPolling("EXS_CONFIG_APITOCLIENT");
		for (int i = 0; i < list.size(); i++) {
			/***********1.参数处理***************************************/
			HashMap map = (HashMap)list.get(i);
			ServicesBean bean = new ServicesBean();
			bean.setServiceMode(ExsConstants.ApiPollingToInvoke);
			EntityUtility.hashMapToEntity(bean, map);
			ExsUtility.InitServicesBeanPath(bean);
			/***********2.方法调用*******************/
			if(ExsConstants.push.equals(bean.getMethodName())){
				List<Object[]> fileList = FileUtility.GetSourceFileList(10000, bean.getSourcePath(), bean.getErrorPath());
				if(SysUtility.isEmpty(fileList) || fileList.size() <=0){
					return;
				}
				
				for (int j = 0; j < fileList.size(); j++) {
					try {
						Object[] obj = (Object[])fileList.get(j);
						File file = (File)obj[1];
						bean.setSourcePath((String)obj[0]);
						bean.setFileName(file.getName());
						bean.setXmlData(new String(SysUtility.InputStreamToByte(new FileInputStream(file)),"UTF-8"));
						
						Map<String, String> postParam = new HashMap<String, String>();
						postParam.put("FILE_NAME", bean.getFileName());
						postParam.put("XML_DATA", ZipUtil.zip(bean.getXmlData()));
						postParam.put("MESSAGE_TYPE", bean.getMessageType());
						postParam.put("CLIENT_IP", SysUtility.getCurrentHostIPAddress());
						postParam.put("ZIP_FLAG", "1");
						Request request = new Request(postParam, bean.getUrl());
						request.setClientIp(postParam.get("CLIENT_IP"));
						 
						request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
						Response response = ProtocolUtil.execute(request);
						String StringResult = ZipUtil.unZip(response.getStringResult("UTF-8"));
						
						if(response.isSuccess() && ErrorCode.ErrorCode00.equals(StringResult)){
							LogUtil.printLog("ApiClient数据发送成功："+bean.getFileName(), Level.INFO);
							ExsUtility.AddLogSuccess(DataAccess, bean, "ApiClient数据发送成功");
							FileUtility.copyFile(bean.getSourcePath(), bean.getTargetPath(), bean.getFileName());
						}else {
							LogUtil.printLog("ApiClient数据发送失败："+bean.getFileName()+"，ErrorCode:"+StringResult+"，FailureMessage="+response.getFailureMessage(), Level.ERROR);
							ExsUtility.AddLogFailDesc(DataAccess, bean, "ApiClient数据发送失败，"+"，ErrorCode:"+StringResult+"，FailureMessage="+response.getFailureMessage());
							FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
						}
					} catch (Exception e) {
						FileUtility.copyFile(bean.getSourcePath(), bean.getTargetPath(), bean.getFileName());
						LogUtil.printLog("Http请求发生错误！"+e.getMessage(), Level.ERROR);
					} finally{
						FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());//删除本地文件
						SysUtility.ComitTrans();
						Thread.sleep(Constants.limitMillis);
					}
				}
			}else if(ExsConstants.pull.equals(bean.getMethodName())){
				String XmlData = bean.getXmlData();
				String Url = bean.getUrl();
				String SourcePath = bean.getSourcePath();
				String Encoding = bean.getEncoding();
				
				for (int l = 0; l < 10000; l++) {
					try {
						String StringResult = ApiClientUtility.getMessageByClient(Url,XmlData,"");//1、获取字符串
						if(StringResult.indexOf("The requested resource is not available.") >= 0){
							LogUtil.printLog(Url+" Error:The requested resource is not available.", Level.ERROR);
							break;
						}
						boolean rt1 = ApiClientUtility.xmlToLocalByClient(StringResult, SourcePath, XmlData, Encoding);//2、将报文落地
						boolean rt2 = ApiClientUtility.deleteMessageByClient(Url, XmlData, StringResult);//3、删除数据
						if(rt1 && rt2){
							String[] strs = XmlData.split("\\|");
							for (int j = 0; j < strs.length; j++) {
								String[] tmpStrs = strs[j].split("\\=");
								if(tmpStrs[0].equalsIgnoreCase("MESSAGE_TYPE")){
									bean.setMessageType(tmpStrs[1]);
								}else if(tmpStrs[0].equalsIgnoreCase("MESSAGE_SOURCE")){
									bean.setMessageSource(tmpStrs[1]);
								}else if(tmpStrs[0].equalsIgnoreCase("MESSAGE_DEST")){
									bean.setMessageDest(tmpStrs[1]);
								}
							}
//							ExsUtility.AddLogSuccess(DataAccess, bean, "ApiClient数据接收成功");
						}else{
							break;
						}
					} catch (Exception e) {
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					} 
				}
			}
			/***********3.框架类更新***************************************/
			if(!SysUtility.getDBPoolClose()){
				String UpdateSQL = "UPDATE EXS_CONFIG_APIPOLLING SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+map.get("INDX");
	        	DataAccess.ExecSQL(UpdateSQL);
	        	DataAccess.ComitTrans();
			}
		}
	}
	
	public static void ApiPollingToDBCore(IDataAccess DataAccess)throws Exception{
		List list = getExsConfigApiPolling("EXS_CONFIG_API_POLLINGTODB");
		
		for (int i = 0; i < list.size(); i++) {
			/********************1.设置服务端参数**********************************/
			HashMap apiMap = (HashMap)list.get(i);
			ServicesBean bean = new ServicesBean();
			bean.setServiceMode(ExsConstants.ApiPollingToDB);
			EntityUtility.hashMapToEntity(bean, apiMap);
			ExsUtility.InitServicesBeanPath(bean);
			bean.setMqSubNo(Integer.parseInt(SysUtility.isEmpty(apiMap.get("MQ_SUB_NO"))?"1":(String)apiMap.get("MQ_SUB_NO")));
			bean.setConsumerUser((String)apiMap.get("MQ_USER"));
			bean.setConsumerPwd((String)apiMap.get("MQ_PWD"));
			bean.setConsumerUrl((String)apiMap.get("MQ_URL"));
			bean.setSourceMq((String)apiMap.get("MQ_NAME"));
			/********************2.调用业务逻辑**********************************/
			ConsumerPushMsgMQ(bean,DataAccess);//1.从MQ取数据入库
			ConsumerPushMsgLocal(bean,DataAccess);//2.从本地取数据入库
			/***********3.框架类更新***************************************/
			String UpdateSQL = "UPDATE EXS_CONFIG_API_POLLINGTODB SET REC_VER=REC_VER+1,MODIFY_TIME=sysdate WHERE INDX = "+apiMap.get("INDX");
        	DataAccess.ExecSQL(UpdateSQL);
        	DataAccess.ComitTrans();
		}
	}
	
	public static void ApiPollingToPushCore(IDataAccess DataAccess)throws Exception{
		List list = getExsConfigApiPolling("EXS_CONFIG_API_POLLINGTOPUSH");
		for (int i = 0; i < list.size(); i++) {
			/***********1.参数处理*******************/
			HashMap map = (HashMap)list.get(i);
			ServicesBean bean = new ServicesBean();
			bean.setServiceMode(ExsConstants.ApiPollingToPush);
			EntityUtility.hashMapToEntity(bean, map);
			ExsUtility.InitServicesBeanPath(bean);
			/***********2.方法调用*******************/
			String url = bean.getUrl();
			if(SysUtility.isEmpty(url)) {
				continue;
			}
			
			Datas datas = new Datas();
			datas.SetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE, bean.getMessageSource());
			datas.SetTableValue(ExsConstants.MessageHead, "RECEIVED_MODE", "100");
			
			Map db2xmlMap = Utility.getdb2xmlMap(bean.getMessageType());
			ServicesBean tempBean = new ServicesBean();
			EntityUtility.hashMapToEntity(tempBean, db2xmlMap);
			engine.setDBToXmlMap(tempBean,(String)db2xmlMap.get("INDX"),datas);
			/*****************打印sql日志**************************/
			HashMap sqlmap = tempBean.getMapSql();
			Iterator<Map.Entry<Integer, Integer>> entries = sqlmap.entrySet().iterator(); 
			while (entries.hasNext()) { 
				  Map.Entry<Integer, Integer> entry = entries.next(); 
				  LogUtil.printLog(entry.getKey() + ":" + entry.getValue(), Level.INFO);
			}
			
			List MainDatas = engine.GetMainDatas(tempBean, null, new HashMap());
			for (int j = 0; j < MainDatas.size(); j++) {
				HashMap MainData = (HashMap)MainDatas.get(j);
				String SerialNo = SysUtility.isNotEmpty(MainData.get(tempBean.getSerialName()))?(String)MainData.get(tempBean.getSerialName()):(String)MainData.get(tempBean.getIndxName());
				tempBean.setSerialNo(SerialNo);
				try {
					List tempMainDatas = new ArrayList<>();
					tempMainDatas.add(MainData);
					String fileData = engine.GetAnyJsonData(tempBean, tempMainDatas, new HashMap());
					
					Map<String, String> postParam = new HashMap<String, String>();
					postParam.put("messageData", ZipUtil.zip(fileData));
					postParam.put("messageType", bean.getMessageType());
					postParam.put("clientIp", SysUtility.getCurrentHostIPAddress());
					postParam.put("compress", "1");
					Request request = new Request(postParam, url);
					request.setClientIp(postParam.get("clientIp"));
					request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
					Response response = ProtocolUtil.execute(request);
					String stringResult = response.getStringResult("UTF-8");
					if(SysUtility.isNotEmpty(stringResult)) {
						JSONObject result = new JSONObject(stringResult);
						stringResult = ZipUtil.unZip(SysUtility.getJsonField(result, "result"));
					}
					if(response.isSuccess() && ErrorCode.ErrorCode00.equals(stringResult)){
						ExsUtility.updateSuccessSenderHandle(bean.getMessageType(), SerialNo);
						ExsUtility.AddLogSuccess(DataAccess, bean, "数据推送成功");
						LogUtil.printLog("数据推送成功："+stringResult, Level.INFO);
					}else {
						ExsUtility.updateFailSenderHandle(bean.getMessageType(), SerialNo);
						ExsUtility.AddLogFailDesc(DataAccess, bean, "数据推送失败："+response.getFailureMessage());
						LogUtil.printLog("数据推送失败："+response.getFailureMessage()+" "+stringResult, Level.ERROR);
					}
				} catch (Exception e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
					ExsUtility.updateFailSenderHandle(bean.getMessageType(), SerialNo);
					ExsUtility.AddLogFailDesc(DataAccess, bean, "数据推送失败："+e.getMessage());
				} finally {
					SysUtility.ComitTrans();
				}
			}
			/***********3.框架类更新*****************/
			if(!SysUtility.getDBPoolClose()){
				String UpdateSQL = "UPDATE EXS_CONFIG_API_POLLINGTOPUSH SET REC_VER=REC_VER+1,MODIFY_TIME=SYSDATE WHERE INDX = '"+map.get("INDX")+"'";
	        	DataAccess.ExecSQL(UpdateSQL);
	        	DataAccess.ComitTrans();
			}
		}
	}
	
	private static List getExsConfigApiPolling(String tableName) throws LegendException, JSONException {
		List list = new ArrayList();
		if(!SysUtility.getDBPoolClose()){
			list = SQLExecUtils.query4List("SELECT * FROM "+tableName+" WHERE IS_ENABLED = '1' AND QUARTZ_NAME = ?", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getCurrentCronJobName());
				}
			});
		}else{
			Datas datas = new Datas();
			SysUtility.setConfigDatas(datas, FileUtility.GetSourceFileList(),tableName);
			JSONArray rows = datas.GetTables(tableName);
			
			JSONArray tempRows = new JSONArray();
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = (JSONObject)rows.get(i);
				String quartzName = SysUtility.getJsonField(row, "QUARTZ_NAME");
				if(SysUtility.getCurrentCronJobName().equals(quartzName)){
					tempRows.put(row);
				}
			}
			list = SysUtility.JSONArrayToList(tempRows);
		}
		return list;
	}
	
	private static HashMap<String,Boolean> errorIp = new HashMap<String,Boolean>();
	
	public static String ConsumerPushMsgMQ(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		final String MessageType = bean.getMessageType();
		String ConsumerUser = bean.getConsumerUser();
		String ConsumerPwd = bean.getConsumerPwd();
		String ConsumerUrl = bean.getConsumerUrl();
		String SourceMq = SysUtility.isEmpty(bean.getSourceMq())?bean.getMessageType():bean.getSourceMq();
		SourceMq = SourceMq+"_push";
		String AesKey = bean.getAesKey();
		String Encoding = bean.getEncoding();
		int SubMqNo = bean.getMqSubNo();
		
		SysUtility.OutDate5MinuteReset(errorIp);
		if(SysUtility.isEmpty(ConsumerUrl)){
			return ErrorCode.ErrorCode38+"IP或MQ名称无效！";
		}
		if(SysUtility.isNotEmpty(errorIp.get(ConsumerUrl)) && !errorIp.get(ConsumerUrl)){
			return ErrorCode.ErrorCode38+"MQ连接失败！";
		}
		//执行解析MQ
		Connection connection = null;
		int count = 0;
        try {
        	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConsumerUser, ConsumerPwd, ConsumerUrl);  
            connection = connectionFactory.createConnection();  
            connection.start();  
            Session session = connection.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
            for (int i = 0; i < SubMqNo; i++) {
            	Destination destination = session.createQueue(SourceMq+(i+1));  
                MessageConsumer consumer = session.createConsumer(destination);
                while (true) {
                	try {
                		String FileData = "";
                		Message msg = consumer.receiveNoWait();
    					if (null == msg) {
    						break;
    					}
    					count++;
    					if(count > 10000){
    						return "";
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
    					if(SysUtility.isNotEmpty(AesKey)){
    						FileData = AESGenerator.decrypt(FileData, AesKey);
    					}
    					ServicesBean xmltodbBean = new ServicesBean();
    					if(SysUtility.isNotEmpty(FileData) && FileData.startsWith("{")) {
							xmltodbBean.setXmlData(ConvertUtility.jsonToXml(FileData, xmltodbBean.getEncoding()));
    					}else {
    						xmltodbBean.setXmlData(FileData);
    					}
    					setXmlToDBBean(xmltodbBean, MessageType);
    					engine.SaveToDBForAny(xmltodbBean, DataAccess);
    				} catch (Exception e) {
    					LogUtil.printLog("Active Received To Xml"+e.getMessage(), Level.ERROR);
    					errorIp.put(ConsumerUrl, false);
    				} finally {
    					Utility.commitJmsSession(session);
    				}
                }
			}
        } catch (Exception e) {  
            LogUtil.printLog("Received ActiveMq Error:"+e.getMessage(), Level.INFO);
            return ErrorCode.ErrorCode38+"IP或MQ名称无效！";
        } finally {
        	Utility.closeJmsConnection(connection);
        }
        return "";
	}
	public static String ConsumerPushMsgLocal(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		if(SysUtility.isEmpty(bean.getSourcePath())) {
			return "";
		}
		
		List<Object[]> fileList = FileUtility.GetSourceFileList(10000, bean.getSourcePath(), bean.getErrorPath());
		if(SysUtility.isEmpty(fileList) || fileList.size() <=0){
			return "";
		}
		for (int j = 0; j < fileList.size(); j++) {
			try {
				long start = System.currentTimeMillis();
				Object[] obj = (Object[])fileList.get(j);
				File file = (File)obj[1];
				bean.setSourcePath((String)obj[0]);
				bean.setFileName(file.getName());
				
				ServicesBean xmltodbBean = new ServicesBean();
				xmltodbBean.setFile(file);
				setXmlToDBBean(xmltodbBean, bean.getMessageType());
				engine.SaveToDBForAny(xmltodbBean, DataAccess);
				
				LogUtil.printLog(bean.getFileName()+" cost:"+(System.currentTimeMillis()-start), Level.INFO);
			} catch (Exception e) {
				FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
				LogUtil.printLog("PutMessage:"+e.getMessage(), Level.ERROR);
			} finally{
				FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());//删除本地文件
				SysUtility.ComitTrans();
			}
		}
		return "";
	}
	private static void setXmlToDBBean(ServicesBean xmltodbBean, String MessageType)throws Exception{
		String XmlToDBSQL = "select * from exs_config_xmltodb where nvl(is_enabled,'1') = '1' and message_type = ? and rownum = 1";
		Map map = SQLExecUtils.query4Map(XmlToDBSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, MessageType);
			}
		});
		if(SysUtility.isEmpty(map)) {
			return;
		}
		EntityUtility.hashMapToEntity(xmltodbBean, map);
		xmltodbBean.setDataType((String)map.get("DATA_TYPE"));
		xmltodbBean.setExistsReturn("1".equals(map.get("EXISTS_RETURN"))?true:false);
		xmltodbBean.setBlobProcess(SysUtility.isEmpty(map.get("BLOB_PROCESS"))?0:Integer.parseInt((String)map.get("BLOB_PROCESS")));
		final String Indx = (String)map.get("INDX");
		String SelectSQL = "SELECT S.* FROM exs_config_xmltodb_MAP S WHERE nvl(IS_ENABLED,'1') = '1' AND P_INDX = ? ORDER BY seq_no,indx";
		List listSql = SQLExecUtils.query4List(SelectSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(Indx));
			}
		});
		xmltodbBean.setListSql(listSql);
	}
	
	
	
	private static Connection ProducerConn = null;
	private static Session ProducerSession = null;
	private static MessageProducer producer = null;
	public static String ProducerPullMsgMQ(ServicesBean bean,String messageSource,String FileName,String FileData)throws Exception{
		String ProducerUser = bean.getProducerUser();
		String ProducerPwd = bean.getProducerPwd();
		String ProducerUrl = bean.getProducerUrl();
		if(SysUtility.isEmpty(ProducerUrl)) {
			return "";
		}
		String TargetMq = SysUtility.isEmpty(bean.getTargetMq())?bean.getMessageType():bean.getTargetMq();
		TargetMq = TargetMq+"_"+messageSource+"_pull";
		int SubMqNo = bean.getMqSubNo();
		String ActiveMqMode = bean.getActiveMqMode();
		String AesKey = bean.getAesKey();
		String SourcePath = bean.getSourcePath();
//		ProducerUrl = "tcp://127.0.0.1:61616";
		
		SysUtility.OutDate5MinuteReset(errorIp);
		if(SysUtility.isEmpty(ProducerUrl)){
			return ErrorCode.ErrorCode38+"IP或MQ名称无效！";
		}
		if(SysUtility.isNotEmpty(errorIp.get(ProducerUrl)) && !errorIp.get(ProducerUrl)){
			return ErrorCode.ErrorCode38+"MQ连接失败！";
		}
//		String host = ProducerUrl.substring(ProducerUrl.indexOf("//")+2, ProducerUrl.lastIndexOf(":"));
//		String port = ProducerUrl.substring(ProducerUrl.lastIndexOf(":")+1);
//		boolean rt = SysUtility.isHostReachable(host, 20);
		
        try {
        	//连接ActiveMQ
        	if(SysUtility.isEmpty(ProducerConn)) {
        		ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(ProducerUser,ProducerPwd,ProducerUrl);  
    			ProducerConn = ProducerConnFactory.createConnection();  
    			ProducerConn.start();  
        	}
        	if(SysUtility.isEmpty(ProducerSession)) {
        		ProducerSession = ProducerConn.createSession(Boolean.TRUE,javax.jms.Session.SESSION_TRANSACTED);
        	}
        	
        	Destination ProducerDes = ProducerSession.createQueue(TargetMq+SysUtility.MathRandom(SubMqNo));  
    		producer = ProducerSession.createProducer(ProducerDes);  
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			
			try {
    			if("1".equals(ActiveMqMode)){//ObjectMessage
    				ActiveXmlBean objBean = new ActiveXmlBean();
    				objBean.setFileName(FileName);
    				if(SysUtility.isNotEmpty(AesKey)){
    					objBean.setXmlData(AESGenerator.encrypt(FileData, AesKey));
    				}else{
    					objBean.setXmlData(FileData);
    				}
    				objBean.setSourcePath(SourcePath);
    				ObjectMessage message = ProducerSession.createObjectMessage(objBean);
    				producer.send(message);
    			}else if("2".equals(ActiveMqMode)){//MapMessage
    				MapMessage message = ProducerSession.createMapMessage();
    				message.setStringProperty("FileName",FileName);
    				message.setStringProperty("FileData",FileData);
    				message.setStringProperty("SourcePath",SourcePath);
    				producer.send(message);
    			}else{//TextMessage
    				TextMessage message = ProducerSession.createTextMessage(FileData);
    				message.setStringProperty("FileName",FileName);
    				message.setStringProperty("SourcePath",SourcePath);
    				producer.send(message);
    			}
			} catch (Exception e) {
    			LogUtil.printLog("Send ActiveMq Error:"+FileName, Level.ERROR);
    		} finally{
    			Utility.commitJmsSession(ProducerSession);
    		}
        } catch (Exception e) {
            LogUtil.printLog("Send ActiveMq Error:"+e.getMessage(), Level.INFO);
            errorIp.put(ProducerUrl, false);
            return ErrorCode.ErrorCode38+"IP或MQ名称无效！";
        } finally{
        	Utility.closeJmsProducer(producer);
        	if(SysUtility.OutDate5Second()) {
        		ProducerSession.commit();
        	}
//        	closeJmsSession(ProducerSession);
//        	closeJmsConnection(ProducerConn);
        }
        return "";
	}
	
	
	
	
}
