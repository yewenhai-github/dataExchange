package com.easy.api.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.api.entity.ActiveXmlBean;
import com.easy.app.constants.ExsConstants;
import com.easy.app.core.AbstractCalculateEngine;
import com.easy.app.core.MysqlCalculateEngine;
import com.easy.app.core.OracleCalculateEngine;
import com.easy.app.core.SqlserverCalculateEngine;
import com.easy.app.utility.ExsUtility;
import com.easy.constants.Constants;
import com.easy.entity.ServicesBean;
import com.easy.exception.ErrorCode;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.security.AESGenerator;
import com.easy.security.MD5Utility;
import com.easy.utility.CacheUtility;
import com.easy.utility.EntityUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.utility.ZipUtil;

public class OpenApiUtility {
	private static AbstractCalculateEngine engine;
	public OpenApiUtility() {
		String dbType = SysUtility.GetSetting("System", "DBType");
		if (Constants.Oracle.equalsIgnoreCase(dbType)){
			engine = new OracleCalculateEngine();
		} else if (Constants.Mysql.equalsIgnoreCase(dbType)) {
			engine = new MysqlCalculateEngine();
		} else if (Constants.Sqlserver.equalsIgnoreCase(dbType)) {
			engine = new SqlserverCalculateEngine();
		} 
	}
	
	public static String apiInvoking(String clientIp,String messageType,String messageData,String compress,String methodName) {
		ServicesBean bean = new ServicesBean();
		try {
			/*******************1.填充与转换请求参数*********************************/
    		Map<String, String> postParam = getAndConvertOpenApiDatas(clientIp, messageType, messageData, compress, methodName);
			/*******************2.校验请求参数*********************************/
			String ErrorMsg = validatePostParam(postParam);
			if(SysUtility.isNotEmpty(ErrorMsg)){
				return getApiResult(messageType, ErrorMsg, compress);
			}
			/*******************3.填充api配置*********************************/
			fill2ServicesBean(bean, postParam);
			/*******************4.执行业务*********************************/
			if(ExsConstants.push.equals(bean.getMethodName())){
				pushData(bean);
			}else if(ExsConstants.pull.equals(bean.getMethodName())){
				pullData(bean);
			}else if(ExsConstants.destory.equals(bean.getMethodName())){
				destoryData(bean);
			}
			return getApiResult(messageType, bean.getResponseMessage(), compress);
		} catch (Exception e) {
			return getApiResult(messageType, ErrorCode.ErrorCode99+e.getMessage(), compress);
		}
	}
	
	private static String getApiResult(String messageType, String rtMsg, String compress){
		JSONObject rtObj = new JSONObject();
		try {
			rtMsg = "1".equals(compress)?ZipUtil.zip(rtMsg):rtMsg;
			
			if(SysUtility.isEmpty(rtMsg)) {
				rtObj.put("code", getResultCode(ErrorCode.ErrorCode99));
				rtObj.put("result", ErrorCode.ErrorCode99);
			}else {
				rtObj.put("code", getResultCode(rtMsg));
				rtObj.put("result", rtMsg);
			}
			rtObj.put("messageType", messageType);
			rtObj.put("time", SysUtility.getSysDateWithMilliseconds());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rtObj.toString();
	}
	
	private static HashMap<String,Boolean> errorIp = new HashMap<String,Boolean>();
	private static Connection ProducerConn = null;
	private static Session ProducerSession = null;
	private static MessageProducer producer = null;
	private static void pushData(ServicesBean bean)throws Exception{
		/***************1.将数据推送到MQ****************************/
		String FileName = bean.getFileName();
		String FileData = SysUtility.isNotEmpty(bean.getJsonData())?bean.getJsonData():bean.getXmlData();
		
		String ProducerUser = bean.getProducerUser();
		String ProducerPwd = bean.getProducerPwd();
		String ProducerUrl = bean.getProducerUrl();
		String TargetMq = SysUtility.isEmpty(bean.getTargetMq())?bean.getMessageType():bean.getTargetMq();
		TargetMq = TargetMq+"_push";
		int SubMqNo = bean.getMqSubNo();
		String ActiveMqMode = bean.getActiveMqMode();
		String AesKey = bean.getAesKey();
		String SourcePath = bean.getSourcePath();
//		ProducerUrl = "tcp://127.0.0.1:61616";
		
		SysUtility.OutDate5MinuteReset(errorIp);
		if(SysUtility.isEmpty(ProducerUrl)){
			bean.setResponseMessage(ErrorCode.ErrorCode38+"IP或MQ名称无效！");
			return;
		}
		if(SysUtility.isNotEmpty(errorIp.get(ProducerUrl)) && !errorIp.get(ProducerUrl)){
			bean.setResponseMessage(ErrorCode.ErrorCode38+"MQ连接失败！");
			return;
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
            bean.setResponseMessage(ErrorCode.ErrorCode38+"IP或MQ名称无效！");
			return;
        } finally{
        	Utility.closeJmsProducer(producer);
        }
		bean.setResponseMessage(ErrorCode.ErrorCode00);
	}
	
	private static void pullData(ServicesBean bean)throws Exception{
		Datas datas = bean.getDatas();
		/***************1.带查询条件，从数据库获取数据****************************/
		if(datas.has(ExsConstants.MessageBody)) {
			StringBuffer rtMsg = new StringBuffer();
			try {
				Map db2xmlMap = Utility.getdb2xmlMap(datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_TYPE));
				if(SysUtility.isEmpty(db2xmlMap)){
					bean.setResponseMessage(ErrorCode.ErrorCode95+"dbToXml未配置");
					return;
				}
				ServicesBean tempBean = new ServicesBean();
				EntityUtility.hashMapToEntity(tempBean, db2xmlMap);
				tempBean.setMessageSource(datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_DEST"));
				tempBean.setMessageDest(datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE"));
				tempBean.setDataFilterReg(bean.getDataFilterReg());
				tempBean.setServiceType("1");
				engine.setDBToXmlMap(tempBean,(String)db2xmlMap.get("INDX"),datas);
				
				List MainDatas = engine.GetMainDatas(tempBean, null, new HashMap());
				if(SysUtility.isEmpty(MainDatas)){
					bean.setResponseMessage(ErrorCode.ErrorCode01);
					return;
				}
				
				HashMap MainData = (HashMap)MainDatas.get(0);
				tempBean.setSerialNo(SysUtility.isNotEmpty(MainData.get(tempBean.getSerialName()))?(String)MainData.get(tempBean.getSerialName()):(String)MainData.get(tempBean.getIndxName()));
				if(SysUtility.isNotEmpty(bean.getJsonData())){//Json格式的参数
					rtMsg.append(engine.GetAnyJsonData(tempBean, MainDatas, new HashMap()));
				}else if(SysUtility.isNotEmpty(bean.getXmlData())){//Xml格式的参数
					rtMsg.append(engine.GetAnyXmlData(tempBean, MainDatas, new HashMap()));
				}
				
				String MainTableName = tempBean.getMainTableName();
				String SerialName = tempBean.getSerialName();
				String tempSerialName = getSerialName(tempBean);
				String messageSource = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
				String deleteFlag = datas.GetTableValue(ExsConstants.MessageHead, "DELETE_FLAG");
				for (int i = 0; i < MainDatas.size(); i++) {
					MainData = (HashMap)MainDatas.get(i);
					String SerialNo = SysUtility.isNotEmpty(MainData.get(tempSerialName))?(String)MainData.get(tempSerialName):(String)MainData.get(tempBean.getIndxName());
					try {
						updateOpenApiData(MainTableName, SerialName, SerialNo, messageSource, deleteFlag);
						SysUtility.ComitTrans();
					} catch (Exception e) {
						SysUtility.RoolbackTrans();
						LogUtil.printLog(ErrorCode.ErrorCode99+e.getMessage(), Level.ERROR);
						rtMsg.append(ErrorCode.ErrorCode99+e.getMessage());
						bean.setResponseMessage(rtMsg.toString());
						return;
					}
				}
			} catch (Exception e) {
				LogUtil.printLog(ErrorCode.ErrorCode99+e.getMessage(), Level.ERROR);
				rtMsg.append(ErrorCode.ErrorCode99+e.getMessage());
				bean.setResponseMessage(rtMsg.toString());
				return;
			}
		}else {
			/***************2.无查询条件，从MQ获取数据******************************/
			String ConsumerUser = bean.getConsumerUser();
			String ConsumerPwd = bean.getConsumerPwd();
			String ConsumerUrl = bean.getConsumerUrl();
			String SourceMq = SysUtility.isEmpty(bean.getSourceMq())?bean.getMessageType():bean.getSourceMq();
			String messageSource = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE);
			SourceMq = SourceMq+"_"+messageSource+"_pull";
			String AesKey = bean.getAesKey();
			int SubMqNo = bean.getMqSubNo();
			
			SysUtility.OutDate5MinuteReset(errorIp);
			if(SysUtility.isEmpty(ConsumerUrl)){
				bean.setResponseMessage(ErrorCode.ErrorCode38+"IP或MQ名称无效！");
				return;
			}
			if(SysUtility.isNotEmpty(errorIp.get(ConsumerUrl)) && !errorIp.get(ConsumerUrl)){
				bean.setResponseMessage(ErrorCode.ErrorCode38+"MQ连接失败！");
				return;
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
	    					if(count > 100){
	    						bean.setResponseMessage(ErrorCode.ErrorCode40+"取数上限100！");
	    						return;
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
	    					bean.setResponseMessage(FileData);
	    					Utility.commitJmsSession(session);
	    					break;
	    				} catch (Exception e) {
	    					LogUtil.printLog("Active Received To Xml"+e.getMessage(), Level.ERROR);
	    					errorIp.put(ConsumerUrl, false);
	    				}
	                }
				}
	        } catch (Exception e) {  
	            LogUtil.printLog("Received ActiveMq Error:"+e.getMessage(), Level.INFO);
	            bean.setResponseMessage(ErrorCode.ErrorCode38+"IP或MQ名称无效！");
				return;
	        } finally {
	        	Utility.closeJmsConnection(connection);
	        }
		}
		/***************3.未有返回的数据******************************/
		bean.setResponseMessage(ErrorCode.ErrorCode01);
	}
	
	private static void destoryData(ServicesBean bean)throws Exception{
		Datas datas = bean.getDatas();
		String messageSource = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
		String messageType = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_TYPE");
		JSONObject searchTable = datas.GetTable("MessageBody");
		if(SysUtility.isEmpty(searchTable)){
			bean.setResponseMessage(ErrorCode.ErrorCode97);
			return;
		}
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select s.serial_name,f.table_name from exs_config_dbtoxml s,exs_config_dbtoxml_sql f");
		sqlBuild.append(" where s.indx = f.p_indx ");
		sqlBuild.append(" and s.message_type = ?",messageType);
		sqlBuild.append(" and s.part_id like ?","api%");
		sqlBuild.append(" order by f.seq_no");
		Map map = sqlBuild.query4Map();
		if(SysUtility.isEmpty(map)){
			sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select s.serial_name,f.table_name from exs_config_dbtoxml s,exs_config_dbtoxml_sql f");
			sqlBuild.append(" where s.indx = f.p_indx ");
			sqlBuild.append(" and s.message_type = ?",messageType);
			sqlBuild.append(" order by f.seq_no");
		}
		
		if(SysUtility.isEmpty(map)){
			bean.setResponseMessage(ErrorCode.ErrorCode95+"任意报文生成未配置！");
			return;
		}
		String dataFilterReg = (String)map.get("SERIAL_NAME");
		String MainTableName = (String)map.get("TABLE_NAME");
		if(SysUtility.isEmpty(dataFilterReg) ||SysUtility.isEmpty(MainTableName)){
			bean.setResponseMessage(ErrorCode.ErrorCode95+"获取数据表名或条件未设置！");
			return;
		}
		
		try {
			sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select 0 from "+MainTableName+" where nvl(api_pull_flag,'0') = '0'");
			sqlBuild.append(" and message_source = ?",messageSource);
			sqlBuild.append(" and message_count < 10 ");
			if(SysUtility.isNotEmpty(dataFilterReg)){
				JSONObject mainData = datas.GetTable("MessageBody");
				if(SysUtility.isEmpty(mainData)) {
					mainData = datas.GetTable("SEARCH_TABLE");
				}
				Iterator<?> keys = mainData.keys();
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(SysUtility.isNotEmpty(dataFilterReg) && dataFilterReg.indexOf(key) >= 0){
						String[] strs = ((String)SysUtility.getJsonField(mainData, key)).split(",");
						sqlBuild.append(key, "in", strs);
					}
				}
			}
			
			boolean rt = sqlBuild.query4Exists();
			if(rt){
				//deleteOpenApiData(MainTableName, SerialName, SerialValues, messageSource);
				SQLBuild updateBuild = SQLBuild.getInstance();
				updateBuild.append("update "+MainTableName+" set message_count=message_count+10");
				updateBuild.append(" where message_source = ?",messageSource);
				if(SysUtility.isNotEmpty(dataFilterReg)){
					JSONObject mainData = datas.GetTable("MessageBody");
					if(SysUtility.isEmpty(mainData)) {
						mainData = datas.GetTable("SEARCH_TABLE");
					}
					Iterator<?> keys = mainData.keys();
					while(keys.hasNext()){
						String key = keys.next().toString();
						if(SysUtility.isNotEmpty(dataFilterReg) && dataFilterReg.indexOf(key) >= 0){
							String[] strs = ((String)SysUtility.getJsonField(mainData, key)).split(",");
							updateBuild.append(key, "in", strs);
						}
					}
				}
				updateBuild.execute4Update();
				
				SysUtility.ComitTrans();
			}else{
				bean.setResponseMessage(ErrorCode.ErrorCode02);
				return;
			}
		} catch (Exception e) {
			SysUtility.RoolbackTrans();
			LogUtil.printLog(ErrorCode.ErrorCode99+e.getMessage(), Level.ERROR);
			bean.setResponseMessage(ErrorCode.ErrorCode99+e.getMessage());
			return;
		}
		bean.setResponseMessage(ErrorCode.ErrorCode98);
	}
	
	private static String getSerialName(ServicesBean tempBean) throws LegendException, JSONException{
		String SerialName = tempBean.getSerialName();
		
		HashMap map = (HashMap)((List)tempBean.getListSql()).get(0);
		StringBuffer strMapping = new StringBuffer();
		if(SysUtility.isNotEmpty(map.get("XML_COULMN_MAPPING"))){
			strMapping.append((String)map.get("XML_COULMN_MAPPING"));
		}
		if(SysUtility.isNotEmpty(map.get("XML_COULMN_MAPPING1"))){
			strMapping.append((String)map.get("XML_COULMN_MAPPING1"));
		}
		if(SysUtility.isNotEmpty(map.get("XML_COULMN_MAPPING2"))){
			strMapping.append((String)map.get("XML_COULMN_MAPPING2"));
		}
		String[] Mappings = strMapping.toString().split(",");
		for (int k = 0; k < Mappings.length; k++) {
			String[] strTemp = Mappings[k].split("=");
			if(SerialName.equals(strTemp[0])) {
				return strTemp[1];
			}
		}
		return SerialName;
	}
	
	private static void updateOpenApiData(String MainTableName,String SerialName,String SerialNo,String messageSource,String deleteFlag)throws Exception{
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("update "+MainTableName+" set message_count=message_count+1");
		sqlBuild.append(" where 1 = 1 ");
		sqlBuild.append(" and message_source = ?",messageSource);
		sqlBuild.append(SerialName, "in", SerialNo.split(","));
		sqlBuild.execute4Update();
	}
	
	private static String ApiToServerValidate(ServicesBean bean,Datas datas)throws Exception{
		StringBuffer ErrorMsg = new StringBuffer();
		String messageQuartz = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_QUARTZ");
		String messageSource = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
		String techRegCode = datas.GetTableValue(ExsConstants.MessageHead, "TECH_REG_CODE");
		if("1".equals(messageQuartz)){
			bean.setQuartzFlag(messageQuartz);
			//TODO 定时程序发送过来的报文暂时不校验
		}else{
			/********************1 验证报文头********************************/
			ErrorMsg.append(ValidateMessageHead(datas));
			if(SysUtility.isNotEmpty(ErrorMsg)){
				return ErrorCode.ErrorCode65+ErrorMsg.toString();
			}
			/********************2 验证请求毫秒数调用********************************/
			ErrorMsg.append(ValidateHttpRequest(bean));
			if(SysUtility.isNotEmpty(ErrorMsg)){
				return ErrorCode.ErrorCode65+ErrorMsg.toString();
			}
			/********************3 验证接入配置********************************/
			if(SysUtility.isEmpty(bean.getCustomerMap())){
				ErrorMsg.append(ErrorCode.ErrorCode59+"接入方代码:"+messageSource+",企业唯一标识:"+techRegCode);
				return ErrorMsg.toString();
			}
			/********************4  验证报文头加密********************************/
			ErrorMsg.append(ValidateSign(datas, bean.getCustomerMap()));
			if(SysUtility.isNotEmpty(ErrorMsg)){
				return ErrorCode.ErrorCode65+ErrorMsg.toString();
			}
		}
		return "";
	}
	
	
	public static HashMap httpLimitMap = new HashMap();
	public static String ValidateHttpRequest(ServicesBean bean) throws IOException{
		long limitMillis = bean.getLimitMillis();
		String clientIp = bean.getClientIP();
		String MethodName = bean.getMethodName();
		String messageType = bean.getMessageType();
		
		if(SysUtility.isEmpty(clientIp)){
			return "参数CLIENT_IP，客户端IP不能为空。";
		}
		String key = clientIp+MethodName+messageType;
		
		if(SysUtility.isEmpty(httpLimitMap.get(key))){
			httpLimitMap.put(key, System.currentTimeMillis());
		}else{
			long lastTime = (Long)httpLimitMap.get(key);
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastTime < limitMillis){
				return limitMillis+"毫秒内不能重复请求!";
			}else{
				httpLimitMap.put(key, currentTime);
			}
		}
		return "";
	}
	
	private static String ValidateMessageHead(Datas datas) throws Exception{
		StringBuffer ErrorMsg = new StringBuffer();
		try {
			String MESSAGE_SOURCE = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
			String MESSAGE_DEST = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_DEST");
			String MESSAGE_ID = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_ID");
			String MESSAGE_TYPE = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_TYPE");
			String MESSAGE_TIME = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_TIME");
			
			String TECH_REG_CODE = datas.GetTableValue(ExsConstants.MessageHead, "TECH_REG_CODE");
			String MESSAGE_VERSION = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_VERSION");
			String MESSAGE_CATEGORY = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_CATEGORY");
			String MESSAGE_SIGN_DATA = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SIGN_DATA");
			
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				ErrorMsg.append("MESSAGE_SOURCE不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_DEST)){
				ErrorMsg.append("MESSAGE_DEST不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_ID)){
				ErrorMsg.append("MESSAGE_ID不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_TYPE)){
				ErrorMsg.append("MESSAGE_TYPE不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_TIME)){
				ErrorMsg.append("MESSAGE_TIME不能为空|");
			}
			if(SysUtility.isNotEmpty(ErrorMsg)){
				return ErrorMsg.toString();
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return e.getMessage();
		}
		return "";
	}
	
	private static String ValidateSign(Datas datas,Map CustomerMap) throws Exception{
		StringBuffer ErrorMsg = new StringBuffer();
		
		String MESSAGE_SOURCE = (String)datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
		String MESSAGE_TIME = (String)datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_TIME");
		String MESSAGE_SIGN_DATA = (String)datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SIGN_DATA");
		
		String EncryptCode = EncryptKeys(MESSAGE_SOURCE, MESSAGE_TIME,(String)CustomerMap.get("MESSAGE_SIGN")); 
		if(!MESSAGE_SIGN_DATA.equalsIgnoreCase(EncryptCode)){
			ErrorMsg.append("加密串匹配错误（MESSAGE_SOURCE+MESSAGE_TIME+平台提供的密钥）!");
		}
		return ErrorMsg.toString();
	}
	
	private static String EncryptKeys(String messageSource,String messageTime,String keys) throws LegendException{
		return MD5Utility.encrypt(messageSource+messageTime+keys);
	}
	
	public static void fill2ServicesBean(ServicesBean bean, Map<String, String> EnvDatas)throws Exception{
		bean.setLimitMillis(Constants.limitMillis);
		bean.setServerName(SysUtility.isEmpty(EnvDatas.get("SERVER_NAME"))?(String)EnvDatas.get("METHOD_NAME"):(String)EnvDatas.get("SERVER_NAME"));
		bean.setMessageType((String)EnvDatas.get("MESSAGE_TYPE"));
		bean.setMethodName((String)EnvDatas.get("METHOD_NAME"));
		bean.setClientIP((String)EnvDatas.get("CLIENT_IP"));
		bean.setEncoding(SysUtility.isEmpty(EnvDatas.get("ENCODING"))?"utf-8":(String)EnvDatas.get("ENCODING"));
		bean.setZipFlag("1".equals((String)EnvDatas.get("ZIP_FLAG"))?true:false);
		if(bean.isZipFlag()){
			bean.setXmlData(ZipUtil.unZip(new String(((String)(SysUtility.isEmpty(EnvDatas.get("XML_DATA"))?"":EnvDatas.get("XML_DATA"))).getBytes(bean.getEncoding()),bean.getEncoding())));
			bean.setJsonData(ZipUtil.unZip((String)EnvDatas.get("JSON_DATA")));
		}else{
			bean.setXmlData(new String(((String)(SysUtility.isEmpty(EnvDatas.get("XML_DATA"))?"":EnvDatas.get("XML_DATA"))).getBytes(bean.getEncoding()),bean.getEncoding()));
			bean.setJsonData((String)EnvDatas.get("JSON_DATA"));
		}
		bean.setFileName(SysUtility.isEmpty((String)EnvDatas.get("FILE_NAME"))?SysUtility.GetUUID()+".xml":bean.getFileName());
		
		Map apiMap = CacheUtility.getApiToServerMap(bean.getMessageType());
		bean.setMessageType((String)apiMap.get("MESSAGE_TYPE"));
		bean.setClassInvoke((String)apiMap.get("CLASS_INVOKE"));
		bean.setSourcePath((String)apiMap.get("SOURCE_PATH"));
		bean.setDataFilterReg((String)apiMap.get("DATA_FILTER_REG"));
		bean.setMainTableName((String)apiMap.get("MAIN_TABLE_NAME"));
		bean.setSerialName((String)apiMap.get("SERIAL_NAME"));
		bean.setProducerUser((String)apiMap.get("MQ_USER"));
		bean.setProducerPwd((String)apiMap.get("MQ_PWD"));
		bean.setProducerUrl((String)apiMap.get("MQ_URL"));
		bean.setConsumerUser((String)apiMap.get("MQ_USER"));
		bean.setConsumerPwd((String)apiMap.get("MQ_PWD"));
		bean.setConsumerUrl((String)apiMap.get("MQ_URL"));
		bean.setTargetMq((String)apiMap.get("MQ_NAME"));
		int mqSubNo = Integer.parseInt(SysUtility.isEmpty(apiMap.get("MQ_SUB_NO"))?"1":(String)apiMap.get("MQ_SUB_NO"));
		bean.setMqSubNo(mqSubNo);
		String XmlRootName = "";
		HashMap RequestMessage = new HashMap();
		if(SysUtility.isNotEmpty(bean.getJsonData())){//Json格式的参数
			JSONObject JsonData = new JSONObject(bean.getJsonData());
			Iterator<?> keys = JsonData.keys();
			XmlRootName = keys.next().toString();
			RequestMessage = SysUtility.JSONObjectToHashMap(JsonData, XmlRootName);
		}else if(SysUtility.isNotEmpty(bean.getXmlData())){//Xml格式的参数
			InputStream is = new ByteArrayInputStream(bean.getXmlData().getBytes("UTF-8"));
			XmlRootName = FileUtility.GetExsFileRootName(bean.getXmlData());
			RequestMessage = FileUtility.xmlParse(is,XmlRootName);
		}
		Datas datas = new Datas();
		datas.MapToDatas(ExsConstants.MessageHead, RequestMessage);
		datas.MapToDatas(ExsConstants.MessageBody, RequestMessage);
		if(ExsConstants.push.equals(bean.getMethodName())){
			bean.setXmlData("<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(XmlRootName,RequestMessage, 0));
		}else{
			datas.MapToDatas("SEARCH_TABLE", RequestMessage);
		}
		String messageSource = datas.GetTableValue(ExsConstants.MessageHead, "MESSAGE_SOURCE");
		String techRegCode = datas.GetTableValue(ExsConstants.MessageHead, "TECH_REG_CODE");
		Map customerMap = CacheUtility.getCustomerMap(bean.getMessageType(), messageSource, techRegCode);
		bean.setDatas(datas);
		bean.setCustomerMap(customerMap);
		
		ExsUtility.InitServicesBeanPath(bean);
	}
	

	
	
	
	/********************DispatcherManager迁移至此******************************/
	private static Map<String, String> getAndConvertOpenApiDatas(String clientIp,String messageType,String messageData,String compress,String methodName) throws LegendException, JSONException, UnsupportedEncodingException{
		Map<String, String> postParam = new HashMap<String, String>();
    	postParam.put("METHOD_NAME", methodName);
    	postParam.put("CLIENT_IP", clientIp);
    	postParam.put("MESSAGE_TYPE", messageType);
    	postParam.put("ZIP_FLAG", String.valueOf("1".equals(compress)?"1":"0"));
    	if(SysUtility.isNotEmpty(messageData)) {
    		if(messageData.trim().startsWith("<?")) {
    			postParam.put("XML_DATA", messageData);
    		}else {
    			postParam.put("JSON_DATA", messageData);
    		}
    	}
		
		if(SysUtility.isEmpty(postParam.get("MESSAGE_TYPE")) && SysUtility.isNotEmpty(SysUtility.GetSetting("System", "MESSAGE_TYPE"))){
			postParam.put("MESSAGE_TYPE", SysUtility.GetSetting("System", "MESSAGE_TYPE"));
		}
		if(SysUtility.isEmpty(postParam.get("SERVER_NAME")) && SysUtility.isNotEmpty(postParam.get("MESSAGE_TYPE"))){
			postParam.put("SERVER_NAME", postParam.get("MESSAGE_TYPE"));
		}
		
		if(SysUtility.isNotEmpty(postParam.get("JSON_DATA"))){
			String zipFlag = postParam.get("ZIP_FLAG");
			String jsonData = "1".equals(zipFlag)?ZipUtil.unZip(postParam.get("JSON_DATA")):postParam.get("JSON_DATA");
			if(jsonData.trim().startsWith("[{") && jsonData.trim().endsWith("}]")){
				JSONArray rows = new JSONArray(jsonData.trim());
				JSONObject row = (JSONObject)rows.get(0);
				
				JSONObject OBORMessage = new JSONObject();
				JSONArray Root = new JSONArray();
				if(row.has("MessageHead")){
					Root.put(new JSONObject().put("MessageHead", (JSONObject)row.get("MessageHead")));
				}
				if(row.has("MessageBody")){
					JSONObject MessageBody = new JSONObject();
					JSONObject head = (JSONObject)row.get("MessageBody");
					if(row.has("BaseTransfer")){
						JSONArray BaseTransfers = new JSONArray();
						BaseTransfers.put((JSONObject)row.get("BaseTransfer"));
						head.put("BaseTransfer", BaseTransfers);
					}
					MessageBody.put("MessageBody", head);
					Root.put(MessageBody);
				}
				String RootName = (String)postParam.get("ROOT_NAME");
				if(SysUtility.isEmpty(RootName)){
					RootName = "OBORMessage";
				}
				OBORMessage.put(RootName, Root);
				
				String resultData = "1".equals(zipFlag)?ZipUtil.zip(OBORMessage.toString()):OBORMessage.toString();
				postParam.put("JSON_DATA", resultData);
			}else if(jsonData.trim().startsWith("{") && jsonData.trim().endsWith("}")){
				JSONObject row = new JSONObject(jsonData);
				JSONArray rows = new JSONArray();
				rows.put(row);
				JSONObject resultData = new JSONObject();
				resultData.put("OBORMessage", rows);
				postParam.put("JSON_DATA", resultData.toString());
			}
		}
		
		return postParam;
	}
	
	private static Datas GetDatas(Map<String, String> postParam,StringBuffer ErrorMsg){
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return new Datas();
		}
		
		Datas datas = new Datas();
		String tempStr = "";
		try {
			if(SysUtility.isNotEmpty(postParam.get("JSON_DATA"))){//Json格式的参数
				tempStr = "1".equals(postParam.get("ZIP_FLAG"))?ZipUtil.unZip(postParam.get("JSON_DATA")):postParam.get("JSON_DATA");
				if(tempStr.trim().startsWith("[{") && tempStr.trim().endsWith("}]")){
					JSONArray rows = new JSONArray(tempStr.trim());
					JSONObject row = (JSONObject)rows.get(0);
					
					JSONObject OBORMessage = new JSONObject();
					JSONArray Root = new JSONArray();
					if(row.has("MessageHead")){
						Root.put(new JSONObject().put("MessageHead", (JSONObject)row.get("MessageHead")));
					}
					String RootName = postParam.get("ROOT_NAME");
					if(SysUtility.isEmpty(RootName)){
						RootName = "OBORMessage";
					}
					OBORMessage.put(RootName, Root);
					tempStr = OBORMessage.toString();
				}
				
				JSONObject JsonData = new JSONObject(tempStr);
				Iterator<?> keys = JsonData.keys();
				String rootName = keys.next().toString();
				HashMap RequestMessage = SysUtility.JSONObjectToHashMap(JsonData, rootName);
				datas.MapToDatas("MessageHead", RequestMessage);
			}else if(SysUtility.isNotEmpty(postParam.get("XML_DATA"))){//Xml格式的参数
				tempStr = "1".equals(postParam.get("ZIP_FLAG"))?ZipUtil.unZip(postParam.get("XML_DATA")):postParam.get("XML_DATA");
				String XmlRootName = FileUtility.GetExsFileRootName(tempStr);
				HashMap RequestMessage = FileUtility.xmlParse(new ByteArrayInputStream((tempStr).getBytes("UTF-8")),XmlRootName);
				datas.MapToDatas("MessageHead", RequestMessage);
			}
			if(SysUtility.isEmpty(datas.GetTableValue("MessageHead", "MESSAGE_ID"))){
				ErrorMsg.append(ErrorCode.ErrorCode32+"MessageHead解析为空！");
			}
		} catch (Exception e) {
			LogUtil.printLog(ErrorCode.ErrorCode99+e.getMessage()+"\nclientIp:"+postParam.get("CLIENT_IP")+"\nData:"+tempStr, Level.ERROR);
			ErrorMsg.append(ErrorCode.ErrorCode99+"MessageHead解析失败！"+e.getMessage());
		}
		return datas;
	}

	private static String validatePostParam(Map<String, String> postParam){
		StringBuffer ErrorMsg = new StringBuffer();
		/************1.验证参数格式***************************/
		Datas datas = GetDatas(postParam, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************2.验证请求频率:校验请求频率，同一报文类型+同一方法名+同一个接入方10毫秒不能重复访问***************************/
		ValidateHttpLimit(postParam, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************3.验证请求参数必填项:校验请求参数是否为空***************************/
		validateHttpParam(postParam,ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************4.验证消息头字段必填项:验证datas.MessageHead中必填项***************************/
		ValidateMessageHead(datas, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************5.验证接入方配置:获取接入方配置并填充到customerMap中。***************************/
		ValidateAccessCustomer(datas, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************6.验证消息头加密串:验证datas.MessageHead中加密串***************************/
		ValidateCustomerSign(datas, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		/************7.验证请求IP黑名单:校验请求ip黑名单***************************/
		ValidateAccessLimit(postParam, datas, ErrorMsg);
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return ErrorMsg.toString();
		}
		return "";
	}
	
	private Map<String,String> getAccessServerMap(String serverName){
		Map<String,String> map = new HashMap<String,String>(); 
		try {
			List list = GetDispatcherConfigList("dispatcher_config.xml");
			for (int i = 0; i < list.size(); i++) {
				HashMap tempMap = (HashMap)list.get(i);
				if(serverName.equals(tempMap.get("SERVER_NAME"))){
					map.put("API_SERVER_URL", (String)tempMap.get("API_SERVER_URL"));
					break;
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return map;
	}
	
	private static void validateHttpParam(Map<String, String> postParam,StringBuffer ErrorMsg){
		if(SysUtility.isNotEmpty(ErrorMsg)){
			return;
		}
		
		String ClientIP = postParam.get("CLIENT_IP");
		String XmlData = postParam.get("XML_DATA");
		String JsonData = postParam.get("JSON_DATA");
		String MethodName = postParam.get("METHOD_NAME");
		String MessageType = postParam.get("MESSAGE_TYPE");
		
		StringBuffer tempErrorMsg = new StringBuffer();
		if(SysUtility.isEmpty(XmlData) && SysUtility.isEmpty(JsonData)){
			tempErrorMsg.append("报文内容不能为空;");
		}
		if(SysUtility.isEmpty(MethodName)){
			tempErrorMsg.append("METHOD_NAME不能为空;");
		}
		if(SysUtility.isEmpty(ClientIP)){
			tempErrorMsg.append("CLIENT_IP不能为空;");
		}
		if(SysUtility.isEmpty(MessageType)){
			tempErrorMsg.append("MESSAGE_TYPE不能为空;");
		}
		if(SysUtility.isNotEmpty(tempErrorMsg)){
			ErrorMsg.append(ErrorCode.ErrorCode37+tempErrorMsg);
		} 
		return;
	}

	private static HashMap httpLimit2Map = new HashMap();
	
	private static void ValidateHttpLimit(Map<String, String> postParam,StringBuffer ErrorMsg){
		String ClientIP = postParam.get("CLIENT_IP");
		String MethodName = postParam.get("METHOD_NAME");
		String MessageType = postParam.get("MESSAGE_TYPE");
		
		String key = ClientIP+MethodName+MessageType;
		if(SysUtility.isEmpty(httpLimit2Map.get(key))){
			httpLimit2Map.put(key, System.currentTimeMillis());
		}else{
			long lastTime = (Long)httpLimit2Map.get(key);
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastTime < Constants.limitMillis){
				ErrorMsg.append(ErrorCode.ErrorCode35+Constants.limitMillis+"毫秒内不能重复请求!");
				return;
			}else{
				httpLimit2Map.put(key, currentTime);
			}
		}
	}
	
	private static void ValidateAccessCustomer(Datas datas,StringBuffer ErrorMsg){
		try {
			String messageSource = datas.GetTableValue("MessageHead", "MESSAGE_SOURCE");
			String techRegCode = datas.GetTableValue("MessageHead", "TECH_REG_CODE");
			String messageType = datas.GetTableValue("MessageHead", "MESSAGE_TYPE");
			
			Map customerMap = CacheUtility.getCustomerMap(messageType, messageSource, techRegCode);
			if(SysUtility.isEmpty(customerMap)){
				ErrorMsg.append(ErrorCode.ErrorCode39+"接入方代码:"+messageSource+",报文类型"+messageType+",企业唯一标识:"+techRegCode);
			}else{
				String messageSign = (String)customerMap.get("MESSAGE_SIGN");
				datas.SetTableValue("MessageHead", "CUSTOMER_SIGN", messageSign);
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			ErrorMsg.append(ErrorCode.ErrorCode99+e.getMessage());
		}
	}
	
	private static void ValidateCustomerSign(Datas datas,StringBuffer ErrorMsg){
		try {
			String MESSAGE_SOURCE = (String)datas.GetTableValue("MessageHead", "MESSAGE_SOURCE");
			String MESSAGE_TIME = (String)datas.GetTableValue("MessageHead", "MESSAGE_TIME");
			String MESSAGE_SIGN_DATA = (String)datas.GetTableValue("MessageHead", "MESSAGE_SIGN_DATA");
			String customerSign = (String)datas.GetTableValue("MessageHead", "CUSTOMER_SIGN");
			
			String EncryptCode = MD5Utility.encrypt(MESSAGE_SOURCE+MESSAGE_TIME+customerSign); 
			if(!MESSAGE_SIGN_DATA.equalsIgnoreCase(EncryptCode)){
				ErrorMsg.append(ErrorCode.ErrorCode35+"加密串匹配错误（MESSAGE_SOURCE+MESSAGE_TIME+平台提供的密钥）!");
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			ErrorMsg.append(ErrorCode.ErrorCode99+e.getMessage());
		}
		return;
	}
	
	private static void ValidateAccessLimit(Map<String, String> postParam,Datas datas,StringBuffer ErrorMsg){
		try {
			String clientIp = postParam.get("CLIENT_IP");
			String messageSource = datas.GetTableValue("MessageHead", "MESSAGE_SOURCE");
			String messageType = datas.GetTableValue("MessageHead", "MESSAGE_TYPE");
			
			List list = CacheUtility.getAccessLimitList(clientIp);
			for (int i = 0; i < list.size(); i++) {
				Map map = (HashMap)list.get(i);
				String limitLevel = (String)map.get("LIMIT_LEVEL");
				if("1".equals(limitLevel)){//ip级别
					ErrorMsg.append(ErrorCode.ErrorCode36+"ip级(高):IP:"+clientIp);
					return;
				}else if("2".equals(limitLevel) && messageSource.equals(map.get("MESSAGE_SOURCE"))){//接入方级
					ErrorMsg.append(ErrorCode.ErrorCode36+"接入方级(中):IP="+clientIp+",接入方代码="+messageSource);
					return;
				}else if("3".equals(limitLevel) && messageType.equals(map.get("MESSAGE_TYPE"))){//报文类型级
					ErrorMsg.append(ErrorCode.ErrorCode36+"报文类型级(低):IP="+clientIp+",接入方代码="+messageSource+",报文类型"+messageType);
					return;
				}
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			ErrorMsg.append(ErrorCode.ErrorCode99+e.getMessage());
		}
	}
	
	private static void ValidateMessageHead(Datas datas,StringBuffer ErrorMsg){
		StringBuffer tempErrorMsg = new StringBuffer();
		try {
			String MESSAGE_SOURCE = datas.GetTableValue("MessageHead", "MESSAGE_SOURCE");
			String MESSAGE_DEST = datas.GetTableValue("MessageHead", "MESSAGE_DEST");
			String MESSAGE_ID = datas.GetTableValue("MessageHead", "MESSAGE_ID");
			String MESSAGE_TYPE = datas.GetTableValue("MessageHead", "MESSAGE_TYPE");
			String MESSAGE_TIME = datas.GetTableValue("MessageHead", "MESSAGE_TIME");
			
			String TECH_REG_CODE = datas.GetTableValue("MessageHead", "TECH_REG_CODE");
			String MESSAGE_VERSION = datas.GetTableValue("MessageHead", "MESSAGE_VERSION");
			String MESSAGE_CATEGORY = datas.GetTableValue("MessageHead", "MESSAGE_CATEGORY");
			String MESSAGE_SIGN_DATA = datas.GetTableValue("MessageHead", "MESSAGE_SIGN_DATA");
			
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				tempErrorMsg.append("MESSAGE_SOURCE不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_DEST)){
				tempErrorMsg.append("MESSAGE_DEST不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_ID)){
				tempErrorMsg.append("MESSAGE_ID不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_TYPE)){
				tempErrorMsg.append("MESSAGE_TYPE不能为空|");
			}
			if(SysUtility.isEmpty(MESSAGE_TIME)){
				tempErrorMsg.append("MESSAGE_TIME不能为空|");
			}
			if(SysUtility.isNotEmpty(tempErrorMsg)){
				ErrorMsg.append(ErrorCode.ErrorCode35+tempErrorMsg);
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			ErrorMsg.append(ErrorCode.ErrorCode99+e.getMessage());
		}
		return;
	}
	
	public static List GetDispatcherConfigList(String FileName) {
		List rtlist = new ArrayList();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Object obj = cl.getResource(FileName);
			if(SysUtility.isEmpty(obj)){
				return rtlist;
			}
			InputStream is = cl.getResourceAsStream(FileName);
			Document doc = new SAXReader().read(is);
			List list = doc.selectNodes("/ActionProfile/Action");
			for (Iterator it = list.iterator(); it.hasNext();) {
				Element e = (Element) it.next();
				String server_name = e.valueOf("@server_name");
				String api_server_url = e.valueOf("@api_server_url");
				String message_type = e.valueOf("@message_type");
				String api_type = e.valueOf("@api_type");
				String desc = e.valueOf("@desc");

				HashMap map = new HashMap();
				map.put("SERVER_NAME", server_name);
				map.put("API_SERVER_URL", api_server_url);
				map.put("MESSAGE_TYPE", message_type);
				map.put("API_TYPE", api_type);
				map.put("DESC", desc);
				rtlist.add(map);
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rtlist;
	}
	
	private static String getResultCode(String msg) {
		if(SysUtility.isNotEmpty(msg) && msg.indexOf("代码:") >= 0) {
			return msg.substring(msg.indexOf("代码:")+3, msg.indexOf("代码:")+5);
		}
		return "";
	}
	
}
