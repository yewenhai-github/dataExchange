package com.easy.app.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.core.AbstractCalculateEngine;
import com.easy.app.core.MysqlCalculateEngine;
import com.easy.app.core.OracleCalculateEngine;
import com.easy.app.core.SqlserverCalculateEngine;
import com.easy.constants.Constants;
import com.easy.context.AppContext;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.mail.MailSender;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.CacheUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class ExsUtility {
	private static AbstractCalculateEngine engine;
	
	//CToXml C:代表一种承载介质，可以是MQ、文件夹、数据库等。
	public static void CToXml(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.Local.equals(serviceMode)){
			engine.DBToXmlForLocal(bean, DataAccess);//本地入口类传输ServicesBean对象调用
		}else if(ExsConstants.DBToXml.equals(serviceMode)){
			engine.DBToXml(bean, DataAccess);//exs_config_dbtoxml、exs_config_dbtoxml_sql //信城通Edi，exs_config_dbtoxml、exs_config_dbtoxml_sql
		}
	}
	//XmlToC C:代表一种承载介质，可以是MQ、文件夹、数据库等。
	public static void XmlToC(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.Local.equals(serviceMode)){
			engine.XmlToDBForLocal(bean, DataAccess);//本地入口类传输ServicesBean对象调用
		}else if(ExsConstants.XmlToDB.equals(serviceMode)){
			engine.XmlToDB(bean, DataAccess, null);//exs_config_xmltodb
		}else if(ExsConstants.XmlToFloderSplit.equals(serviceMode)){
			engine.XmlToFloderForSplit(bean, DataAccess);//exs_config_xmltosplit
		}else if(ExsConstants.XmlToFloderMerge.equals(serviceMode)){
			engine.XmlToFloderForMerge(bean, DataAccess);//exs_config_xmltomerge
		}
	}

	public static void ApiToC(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		engine.XmlToDB(bean, DataAccess, bean.getMessageType());//exs_config_xmltodb
	}

	//数据库对接数据库
	public static void DBToDB(ServicesBean bean, IDataAccess DataAccess)throws Exception{
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.UpdateDBToDB.equals(serviceMode)){
			engine.UpdateToDBForDB(DataAccess,bean);
		}
	}

	public static void ElecDocs(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		ExsUtility.InitServicesBeanPath(bean);
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.Local.equals(serviceMode)){
			engine.ElecDocsForXml(bean, DataAccess);
		}else if(ExsConstants.ElecDocs.equals(serviceMode)){
			engine.ElecDocsForXmlDefault(bean, DataAccess);
		}
	}
	
	
	
	
	
	public static String MethodInvoke(String MethodInvoke,Map map) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {Map.class};
		Object[] objs = new Object[] {map};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,String param1,HashMap param2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {String.class, HashMap.class};
		Object[] objs = new Object[] {param1, param2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,List param1) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {List.class};
		Object[] objs = new Object[] {param1};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,String param1,List param2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {param1, param2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,String param1,JSONObject param2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {String.class, JSONObject.class};
		Object[] objs = new Object[] {param1, param2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,String param1,JSONObject param2,JSONArray param3) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {String.class, JSONObject.class, JSONArray.class};
		Object[] objs = new Object[] {param1, param2, param3};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,JSONObject param1,JSONArray param2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {JSONObject.class, JSONArray.class};
		Object[] objs = new Object[] {param1, param2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class};
		Object[] objs = new Object[] {bean};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean,Datas datas) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class,Datas.class};
		Object[] objs = new Object[] {bean,datas};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean,Datas datas,String TableName) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class,Datas.class,String.class};
		Object[] objs = new Object[] {bean,datas,TableName};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean,IDataAccess DataAccess) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class,IDataAccess.class};
		Object[] objs = new Object[] {bean,DataAccess};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean,HashMap map1,HashMap map2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class, HashMap.class, HashMap.class };
		Object[] objs = new Object[] {bean, map1, map2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvoke(String MethodInvoke,ServicesBean bean,String str1,String str2) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		Class[] classes = new Class[] {ServicesBean.class, String.class, String.class };
		Object[] objs = new Object[] {bean, str1, str2};
		return MethodInvokeCore(MethodInvoke, classes, objs);
	}
	
	public static String MethodInvokeCore(String MethodInvoke,Class[] objClass,Object[] objs)throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		return SysUtility.MethodInvokeCore(MethodInvoke, objClass, objs);
	}
	
	public static boolean HasMethodName(String MethodInvoke) throws ClassNotFoundException{
		if(SysUtility.isEmpty(MethodInvoke)){
			return false;
		}
		
		String methodName = MethodInvoke.substring(MethodInvoke.lastIndexOf(".")+1);
		return HasMethodName(MethodInvoke, methodName);
	}
	
	public static boolean HasMethodName(String MethodInvoke,Object obj) throws ClassNotFoundException{
		//判断类文件是否存在
		StringBuffer path = new StringBuffer();
		path.append(AppContext.getAbsolutePath()+File.separator+"WEB-INF"+File.separator+"classes");
		String[] packages = MethodInvoke.split("\\.");
		for (int i = 0; i < packages.length - 1; i++) {
			path.append(File.separator+packages[i]);
		}
		path.append(".class");
		File file = new File(path.toString());
		boolean rt = file.exists();
		if(!rt){
			return false;
		}
		//判断类中是否存在方法
		String[] methodNames = null; 
		if(obj instanceof String){
			methodNames = new String[]{(String)obj};
		}else if(obj instanceof String[]){
			methodNames = (String[])obj;
		}
		
		String className = MethodInvoke.substring(0, MethodInvoke.lastIndexOf("."));
		Class cController = Class.forName(className);
		Method[] methods = cController.getMethods();
		for (int i = 0; i < methods.length; i++) {
			for (int j = 0; j < methodNames.length; j++) {
				if (methods[i].getName().equals(methodNames[j])) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void CoulmnRootInvoke(String MethodInvoke,List dateList) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		MethodInvoke(MethodInvoke, dateList);
	}
	
	public static void CoulmnChildInvoke(String MethodInvoke,String XmlDocumentName,List dateList) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		MethodInvoke(MethodInvoke, XmlDocumentName, dateList);
	}
	
	public static void CoulmnShowInvoke(List dateList,String ShowStr){
		if(SysUtility.isEmpty(dateList) || SysUtility.isEmpty(ShowStr)){
			return;
		}
		//在Show中配置的字段，集合
		String[] ShowStrs = ShowStr.split(",");
		List ShowList = new ArrayList();
		for (int i = 0; i < ShowStrs.length; i++) {
			ShowList.add(ShowStrs[i]);
		}
		
		for (int i = 0; i < dateList.size(); i++) {
			HashMap rowMap = (HashMap)dateList.get(i);
			//查找不在Show中的字段，集合
			List noList = new ArrayList();
			Set mapSet = rowMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				Object key = entry.getKey();
				if(!ShowList.contains(key)){
					noList.add(key);
				}
			}
			//移除不在Show中的字段
			for (int j = 0; j < noList.size(); j++) {
				rowMap.remove(noList.get(j));
			}
		}
	}
	
	public static void CoulmnHiddenInvoke(List dateList,String HiddenStr){
		if(SysUtility.isEmpty(dateList) || SysUtility.isEmpty(HiddenStr)){
			return;
		}
		String[] HiddenStrs = HiddenStr.split(",");
		
		for (int i = 0; i < dateList.size(); i++) {
			HashMap rowMap = (HashMap)dateList.get(i);
			for (int j = 0; j < HiddenStrs.length; j++) {
				rowMap.remove(HiddenStrs[j]);
			}
		}
	}
	
	public static HashMap GetMappingMap(HashMap sqlMap){
		HashMap MappingMap = new HashMap();
		try {
			String XmlCoulmnMapping = (SysUtility.isEmpty(SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING"))?"":(String)SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING"))+(SysUtility.isEmpty(SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING1"))?"":(String)SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING1"))+(SysUtility.isEmpty(SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING2"))?"":(String)SysUtility.getMapField(sqlMap, "XML_COULMN_MAPPING2"));
			if(SysUtility.isNotEmpty(XmlCoulmnMapping)){
				String[] XmlCoulmnMappings = XmlCoulmnMapping.split(",");
				for (int i = 0; i < XmlCoulmnMappings.length; i++) {
					MappingMap.put(XmlCoulmnMappings[i].split("=")[0], XmlCoulmnMappings[i].split("=")[1]);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("Mapping Reg Error："+e.getMessage(), Level.ERROR);
		}
		return MappingMap;
	}
	
	
	public static void CoulmnMappingInvoke(List childList,HashMap MappingMap,String[] mappingStrs){
		if(SysUtility.isEmpty(childList) || SysUtility.isEmpty(MappingMap)){
			return;
		}
		
		for (int i = 0; i < childList.size(); i++) {
			HashMap rowMap = (HashMap)childList.get(i);
			HashMap tempMap = new HashMap();
			Set mapSet = rowMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
		        if(SysUtility.isNotEmpty(MappingMap.get(key))){
		        	tempMap.put(MappingMap.get(key), value);
		        	if(SysUtility.isNotEmpty(mappingStrs)){
		        		for (int j = 0; j < mappingStrs.length; j++) {
							if(mappingStrs[j].equals(key)){
								mappingStrs[j] = (String)MappingMap.get(key);
								break;
							}
						}
		        	}
		        }
			}
			//填充转换后的字段
			rowMap.putAll(tempMap);
			//移除转换前的字段
			Set mapSet2 = MappingMap.entrySet();
			for (Iterator it = mapSet2.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				Object key = entry.getKey();
				rowMap.remove(key);
			}
		}
	}
	
	
	public static void AddLogFailDesc(IDataAccess DataAccess,ServicesBean bean,String msgDesc) throws LegendException{
		AddLog(DataAccess, bean, msgDesc,"0");
	}
	
	public static void AddLogFail(IDataAccess DataAccess,ServicesBean bean,Exception e) throws LegendException{
		AddLog(DataAccess, bean, e.getMessage(),"0");
	}
	
	public static void AddLogSuccess(IDataAccess DataAccess,ServicesBean bean,String msgDesc) throws LegendException{
		AddLog(DataAccess, bean, msgDesc, "1");
	}
	
	public static void AddLog(IDataAccess DataAccess,ServicesBean bean,String msgDesc,String successFlag) throws LegendException{
		if(SysUtility.getDBPoolClose()){
			return;
		}
		
		try {
			JSONObject Log = new JSONObject();
			Log.put("MSG_TYPE", bean.getMessageType());
			Log.put("MSG_NO", bean.getSerialNo());
			Log.put("MSG_DESC", SysUtility.getStrByLength(msgDesc, 1999));
			Log.put("MSG_STATUS", successFlag);
			Log.put("MSG_NAME", bean.getFileName());
			Log.put("MSG_PATH", SysUtility.getStrByLength(bean.getLogPath(), 199));
			Log.put("MSG_MODE", bean.getServiceMode());
			if(SysUtility.isEmpty(SysUtility.getCurrentCronClassName())){
				Log.put("CLASS_NAME", "多线程");
			}else{
				Log.put("CLASS_NAME", SysUtility.getCurrentCronClassName()+"."+SysUtility.getCurrentCronMethodName());
			}
			Log.put("SENDER_IP_ADDRESS", "");//发送方地址
			Log.put("RECEIVED_IP_ADDRESS", SysUtility.getCurrentHostIPAddress());
			Log.put("RECEIVED_CONTEXT", AppContext.getContextPath());
			Log.put("RECEIVED_PORT", AppContext.getContextPort());
			Log.put("SENDER_NAME", bean.getMessageSourceName());
			Log.put("RECEIVED_NAME", bean.getMessageDestName());
			Log.put("PART_ID", bean.getMessageSource());
			Log.put("PART_ID_SOURCE", bean.getMessageDest());
			Log.put("CREATE_TIME", SysUtility.getSysDate());
			
			Log.put("IS_ENABLED", "1");
			Log.put("SOURCE_PATH", bean.getSourcePath());
			Log.put("TARGET_PATH", bean.getTargetPath());
			Log.put("ERROR_PATH", bean.getErrorPath());
			Log.put("MESSAGE_SOURCE", bean.getMessageSource());
			Log.put("MESSAGE_DEST", bean.getMessageDest());
			
			DataAccess.Insert("exs_handle_log", Log);
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			LogUtil.printLog("日志写入失败："+e.getMessage(), Level.ERROR);
		}
	}

	public static void addHandleLog(IDataAccess DataAccess,String serviceMode, String serialNo,String messageType,String msgStatus,String msgDesc) throws LegendException{
		try {
			JSONObject Log = new JSONObject();
			Log.put("MSG_TYPE", messageType);
			Log.put("MSG_NO", serialNo);
			Log.put("MSG_DESC", SysUtility.getStrByLength(msgDesc, 1999));
			Log.put("MSG_STATUS", msgStatus);
			Log.put("MSG_MODE", serviceMode);
			Log.put("RECEIVED_IP_ADDRESS", SysUtility.getCurrentHostIPAddress());
			Log.put("RECEIVED_CONTEXT", AppContext.getContextPath());
			Log.put("RECEIVED_PORT", AppContext.getContextPort());
			Log.put("CREATE_TIME", SysUtility.getSysDate());
			Log.put("IS_ENABLED", "1");
			DataAccess.Insert("exs_handle_log", Log);
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			LogUtil.printLog("日志写入失败："+e.getMessage(), Level.ERROR);
		}
	}

	public static void updateSuccessSenderHandle(String messageType,String SerialNo) throws LegendException{
		updateSenderHandle(messageType, SerialNo, "1");
	}
	
	public static void updateFailSenderHandle(String messageType,String SerialNo) throws LegendException{
		updateSenderHandle(messageType, SerialNo, "2");
	}

	public static void updateSenderHandle(String messageType,String SerialNo,String successFlag) throws LegendException{
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("update exs_handle_sender set msg_flag = "+successFlag+" where 1 = 1 ");
		sqlBuild.append(" and msg_type = ? ", messageType);
		sqlBuild.append(" and msg_no = ? ", SerialNo);
		sqlBuild.execute4Update();
	}
	
	public static void addLogSuccessFile(ServicesBean bean,String msgDesc) throws LegendException{
		addLogFile(bean, msgDesc, "1");
	}
	
	public static void AddLogFailFile(ServicesBean bean,String msgDesc) throws LegendException{
		addLogFile(bean, msgDesc,"0");
	}
	
	/**新增本地日志
	 * @paramdata:MSG_MODE|MSG_TYPE|MSG_NAME|MSG_DESC|SOURCE_PATH|TARGET_PATH|ERROR_PATH|RECEIVED_IP_ADDRESS|CREATE_TIME
	 * @return true/false
	 * */
	public static boolean addLogFile(ServicesBean bean,String msgDesc,String flag) {
		String tempPath = ExsConstants.appLogPath;
		String fileName = SysUtility.getSysDateWithoutTime()+"-"+SysUtility.getHourOfDay()+"-0-"+bean.getServiceMode()+"-"+bean.getMessageType()+".log";
		
		StringBuffer data = new StringBuffer();
		data.append(bean.getServiceMode()).append("|")
			.append(bean.getMessageType()).append("|")
			.append(bean.getFileName()).append("|")
			.append(SysUtility.getStrByLength(msgDesc, 1999)).append("|")
			.append(bean.getSourcePath()).append("|")
			.append(bean.getTargetPath()).append("|")
			.append(bean.getErrorPath()).append("|")
			.append(SysUtility.getCurrentHostIPAddress()).append("|")
			.append(SysUtility.getSysDate());
		
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
			tempByte = data.toString().getBytes("UTF-8");
			bo.write(tempByte, 0, tempByte.length);
			return true;
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
	
	public static HashMap<String,String> getStructureParams(String param){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(ExsConstants.ServiceMode, ExsConstants.Local);
		map.put(ExsConstants.MESSAGE_SOURCE, ExsConstants.MessageSourceDefault);
		map.put(ExsConstants.MESSAGE_DEST, ExsConstants.MessageDestDefault);
		if(SysUtility.isNotEmpty(param)){
			String[] params = param.split(",");
			if(params.length >= 1){
				map.put(ExsConstants.ServiceMode, params[0]);
			}
		}
		return map;
	}
	
	public static String CreateFolderWithTime(String SourcePath){
		String SOURCE_PATH = SourcePath+File.separator+SysUtility.getSysDateWithoutTime();
		File file = new File(SOURCE_PATH);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+SOURCE_PATH, Level.INFO);
		   }
		}
		return SOURCE_PATH;
	}
	
	public static void ErrorPathProcess(HashMap<String, String> params){
		if(SysUtility.isNotEmpty(params.get(ExsConstants.ERROR_PATH))){
			String ERROR_PATH = params.get(ExsConstants.ERROR_PATH)+File.separator+SysUtility.getSysDateWithoutTime();
			File file = new File(ERROR_PATH);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+ERROR_PATH, Level.INFO);
			   }
			}
			params.put(ExsConstants.ERROR_PATH, ERROR_PATH);
		}
	}
	
	public static String replaceBeanPath(String path) throws IOException{
		return SysUtility.replacePath(path);
	}
	
	public static void InitServicesBeanPath(ServicesBean bean)throws IOException{
		bean.setTempPath(replaceBeanPath(bean.getTempPath()));
		bean.setReceivedPath(replaceBeanPath(bean.getReceivedPath()));
		bean.setSourcePath(replaceBeanPath(bean.getSourcePath()));
		bean.setTargetPath(replaceBeanPath(bean.getTargetPath()));
		bean.setErrorPath(replaceBeanPath(bean.getErrorPath()));
		bean.setHitPath(replaceBeanPath(bean.getHitPath()));
		bean.setPassPath(replaceBeanPath(bean.getPassPath()));
		bean.setTargetSourcePath(replaceBeanPath(bean.getTargetSourcePath()));
		
		String TempPath = bean.getTempPath();
		String ReceivedPath = bean.getReceivedPath();
		String SourcePath = bean.getSourcePath();
		String TargetPath = bean.getTargetPath();
		String ErrorPath = bean.getErrorPath();
		String HitPath = bean.getHitPath();
		String PassPath = bean.getPassPath();
		String TargetSourcePath = bean.getTargetSourcePath();
		
		if(SysUtility.isNotEmpty(TempPath)){
			File file = new File(TempPath);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+TempPath, Level.INFO);
			   }
			}
		}
		if(SysUtility.isNotEmpty(ReceivedPath)){
			File file = new File(ReceivedPath);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+ReceivedPath, Level.INFO);
			   }
			}
		}
		
		if(SysUtility.isNotEmpty(SourcePath)){
			String[] SourcePaths = SourcePath.split(";");
			for (int i = 0; i < SourcePaths.length; i++) {
				File file = new File(SourcePaths[i]);
				if (!file.exists()) {
				   if(file.mkdirs()){
					   LogUtil.printLog("文件夹创建成功："+SourcePaths[i], Level.INFO);
				   }
				}
			}
		}
		if(SysUtility.isNotEmpty(PassPath)){
			File file = new File(PassPath);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+PassPath, Level.INFO);
			   }
			}
		}
		if(SysUtility.isNotEmpty(TargetSourcePath)){
			String[] paths = TargetSourcePath.split(";");  
			for (int i = 0; i < paths.length; i++) {
				File file = new File(paths[i]);
				if (!file.exists()) {
				   if(file.mkdirs()){
					   LogUtil.printLog("文件夹创建成功："+paths[i], Level.INFO);
				   }
				}
			}
		}
		if(SysUtility.isNotEmpty(TargetPath) && TargetPath.endsWith("Target")){
			String TEMP_PATH = TargetPath+File.separator+SysUtility.getSysDateWithoutTime();
			File file = new File(TEMP_PATH);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+TEMP_PATH, Level.INFO);
			   }
			}
			bean.setTargetPath(TEMP_PATH);
		}
		if(SysUtility.isNotEmpty(ErrorPath) && ErrorPath.endsWith("Error")){
			String TEMP_PATH = ErrorPath+File.separator+SysUtility.getSysDateWithoutTime();
			File file = new File(TEMP_PATH);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+TEMP_PATH, Level.INFO);
			   }
			}
			bean.setErrorPath(TEMP_PATH);
		}
		if(SysUtility.isNotEmpty(HitPath) && HitPath.endsWith("Hit")){
			String TEMP_PATH = HitPath+File.separator+SysUtility.getSysDateWithoutTime();
			File file = new File(TEMP_PATH);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+TEMP_PATH, Level.INFO);
			   }
			}
			bean.setHitPath(TEMP_PATH);
		}
	}

	public static void InitServicesBeanIP(ServicesBean bean)throws IOException{
		bean.setConsumerUrl(SysUtility.replaceIP(bean.getConsumerUrl()));
		bean.setProducerUrl(SysUtility.replaceIP(bean.getProducerUrl()));
		bean.setDbDriverUrl(SysUtility.replaceIP(bean.getDbDriverUrl()));
	}

	
	public static String InitPathAddDayFolder(String path)throws IOException{
		if(SysUtility.isEmpty(path)){
			return "";
		}
		
		String TempPath = path+File.separator+SysUtility.getSysDateWithoutTime();
		File file = new File(TempPath);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+TempPath, Level.INFO);
		   }
		}
		return TempPath;
	}
	
	public static String InitPathBySystem(String winMain,String linuxMain,String path)throws IOException{
		if(SysUtility.isEmpty(path)){
			return "";
		}
		
		if(SysUtility.Windows.equals(SysUtility.GetOsName())){
			return winMain+path;
		}else{
			return linuxMain+path;
		}
	}
	
	public static void ServicesErrorNotice(String subject,String content, Object body){
		try {
			if("true".equals(SysUtility.GetProperty("mail.properties","mail.open"))){
				ServicesErrorNotice(subject, null, null, content, body);
			}
		} catch (IOException e) {
			LogUtil.printLog("IOException:mail.properties读取出错"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void ServicesErrorNotice(String subject,String mailFrom, String mailTO,String content, Object body){
		try {	
			if("true".equals(SysUtility.GetProperty("mail.properties","mail.open"))){
				String system = SysUtility.GetProperty("mail.properties","mail.system");
				if(SysUtility.isEmpty(mailFrom)){
					mailFrom = SysUtility.GetProperty("mail.properties","mail.mailFrom");
				}
				if(SysUtility.isEmpty(mailTO)){
					mailTO = SysUtility.GetProperty("mail.properties","mail.mailTO");
				}
				MailSender.send(system+subject, mailFrom,mailTO, null, content, body);
			}
		} catch (MessagingException e) {
			LogUtil.printLog("MessagingException"+e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog("LegendException"+e.getMessage(), Level.ERROR);
		} catch (Exception e) {
			LogUtil.printLog("Exception"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static String createFolder(String Path, String folderName){
		Path = Path + File.separator + folderName;
		File file = new File(Path);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+Path, Level.INFO);
		   }
		}
		return Path;
	}
	
	public static String getFileName(ServicesBean bean,HashMap MainData){
		return getFileName(bean, MainData, ".xml");
	}
	
	//系统标识_报文类型_接收方代码_流水号.xml
	public static String getFileName(ServicesBean bean,HashMap MainData,String suffix){
		StringBuffer fileName = new StringBuffer();
		//报文名称生成规则
		if(SysUtility.isNotEmpty(bean.getFileNameReg()) && SysUtility.isNotEmpty(MainData) ){
			String[] FileNameRegs = bean.getFileNameReg().split("\\|\\|");
			for (int i = 0; i < FileNameRegs.length; i++) {
				if(SysUtility.isNotEmpty(MainData.get(FileNameRegs[i]))){
					fileName.append(MainData.get(FileNameRegs[i]));
				}else{
					fileName.append(FileNameRegs[i]);
				}
			}
		}else{
//			if(SysUtility.isNotEmpty(bean.getSystemFlag())){
//				fileName.append(bean.getSystemFlag()+"_");
//			}
			if(SysUtility.isNotEmpty(bean.getMessageType())){
				fileName.append(bean.getMessageType()+"_");
			}
//			if(SysUtility.isNotEmpty(bean.getMessageDest())){
//				fileName.append(bean.getMessageDest()+"_");
//			}
			if(SysUtility.isNotEmpty(bean.getSerialNo())){
				fileName.append(bean.getSerialNo());
			}
			fileName.append(suffix);
		}
		return fileName.toString();
	}
	
	
	public static Object getXmlParentDocumentObj(List xmlRootList,String XmlParentDocument){
		Object obj = new Object();
		for (int i = 0; i < xmlRootList.size(); i++) {
			HashMap RootMap = (HashMap)xmlRootList.get(i);
			Set mapSet = RootMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
		        Object key = entry.getKey();
		        Object value = entry.getValue();
				if(key.equals(XmlParentDocument)){
					if(value instanceof Map){
						return (Map)value;
					}else if(value instanceof List){
						return (List)value;
					}
				}
				if(value instanceof Map) {
					HashMap map = (HashMap)value;
					Set mapSet2 = map.entrySet();
					for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
						Entry entry2 = (Entry)it2.next();
						Object key2 = entry2.getKey();
				        Object value2 = entry2.getValue();
				        if(key2.equals(XmlParentDocument) && value2 instanceof List){
				        	return (List)value2;
				        }
					}
				}else if(value instanceof List) {
					obj = getXmlParentDocumentObj((List)value, XmlParentDocument);
					if(obj instanceof Map){
						return (Map)obj;
					}else if(obj instanceof List){
						return (List)obj;
					}
				}
			}
		}
		return obj;
	}
	
	public static List getDataList(HashMap HeadMap,HashMap childsMap,String[] RootNames,String xmlDocumentName,String XmlDisplayName,String IndxValue){
		if(RootNames[0].equals(xmlDocumentName) || RootNames[0].equals(XmlDisplayName)){
			List lst = new ArrayList();
			lst.add(HeadMap);
			return lst;
		}
		for (int k = 1; k < RootNames.length; k++) {
			if(RootNames[k].equals(xmlDocumentName) || RootNames[k].equals(XmlDisplayName)){
				if(SysUtility.isEmpty(childsMap.get(RootNames[k]))){
					break;
				}
				HashMap childMap = (HashMap)childsMap.get(RootNames[k]);
				return (List)childMap.get(IndxValue);
			}
		}
		return new ArrayList();
	}
	
	public static void BlobProcess(int blobProcessFlag,Datas datas,String RootNames,String DBTableNames) throws LegendException, IOException{
		if(!"1".equals(String.valueOf(blobProcessFlag))){
			return;
		}
		List<String> blobCoulmnList = SysUtility.getTableColumnBlobs(SysUtility.getCurrentConnection(), DBTableNames);
		for (int j = 0; j < blobCoulmnList.size(); j++) {
			String coulmnName = (String)blobCoulmnList.get(j);
			for (int z = 0; z < datas.GetTableRows(RootNames); z++) {
				String context = datas.GetTableValue(RootNames, coulmnName, z);
				if(SysUtility.isNotEmpty(context)){
					datas.SetTableValue(RootNames, coulmnName, SysUtility.getBase64decoder().decodeBuffer(context), z);
				}
			}
		}
	}
	

	public static Map GetAccessCustomerMap(String MessageType,String MessageSource) throws LegendException{
		return CacheUtility.GetAccessCustomerMap(MessageType, MessageSource);
	}
	
	public static Map GetAccessCustomerMap(String MessageType,String MessageSource,String SourceId) throws LegendException{
		return CacheUtility.GetAccessCustomerMap(MessageType, MessageSource, SourceId);
	}
	
	public static List GetAccessCustomerList(String MessageType,String MessageSource,String SourceId) throws LegendException{
		return CacheUtility.GetAccessCustomerList(MessageType, MessageSource, SourceId);
	}
	
	public static void ToLocalFile(ServicesBean bean,String xmlData,String fileName){
		String[] SourcePaths = bean.getSourcePath().split(";");//生成本地文件
		for (int j = 0; j < SourcePaths.length; j++) {
			String createSourcePath = SourcePaths[j];
			String MessageDestFolder = "";
			try {
				MessageDestFolder = SysUtility.GetProperty("System.properties", "MessageDestFolder");
			} catch (IOException e) {
				LogUtil.printLog("ToLocalFile Get MessageDestFolder Error", Level.ERROR);
			}
			if(SysUtility.isNotEmpty(bean.getMessageDest()) && "true".equals(MessageDestFolder)){
				createSourcePath = bean.getSourcePath()+File.separator+bean.getMessageDest();
			}
			FileUtility.createFile(createSourcePath, fileName, xmlData, bean.getEncoding());
		}
	}
	
	public static InputStream GetExsXmlIs(ServicesBean bean) throws Exception{
		InputStream is = null;
		if(SysUtility.isNotEmpty(bean.getFile())){
			is = new FileInputStream(bean.getFile());
		}
		if(SysUtility.isEmpty(is)){
			String XmlData = bean.getXmlData();
			is = new ByteArrayInputStream(XmlData.getBytes(bean.getEncoding()));
		}
		return is;
	}
	
	public static HashMap GetMappingMap(List listSql,String RootName){
		HashMap mapping = new HashMap();
		for (int j = 0; j < listSql.size(); j++) {
			HashMap map = (HashMap)listSql.get(j);
			String xmlDocumentName = (String)map.get("XML_DOCUMENT_NAME");
			if(RootName.equals(xmlDocumentName)){
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
				
				if(SysUtility.isEmpty(strMapping)){
					continue;
				}
				String[] Mappings = strMapping.toString().split(",");
				for (int k = 0; k < Mappings.length; k++) {
					String[] strTemp = Mappings[k].split("=");
					mapping.put(strTemp[1], strTemp[0]);
				}
			}
		}
		return mapping;
	}
	
	public static void SetRootField(JSONObject root,HashMap MessageHead){
		SysUtility.removeJsonField(root, SysUtility.KeyFieldDefault);
		SysUtility.removeJsonField(root, "INDX");
		SysUtility.removeJsonField(root, "P_INDX");
		SysUtility.removeJsonField(root, "PART_ID");
		SysUtility.removeJsonField(root, "ORG_ID");
		SysUtility.removeJsonField(root, "CREATOR");
		SysUtility.removeJsonField(root, "CREATE_TIME");
		SysUtility.removeJsonField(root, "MODIFYOR");
		SysUtility.removeJsonField(root, "MODIFY_TIME");
		SysUtility.removeJsonField(root, "MODIFY_DATE");
		SysUtility.removeJsonField(root, "REC_VER");
		SysUtility.putJsonField(root, "PART_ID_SOURCE", SysUtility.getJsonField(root, "PART_ID"));
		SysUtility.putJsonField(root, ExsConstants.PART_ID, (String)MessageHead.get(ExsConstants.MESSAGE_SOURCE));
		SysUtility.putJsonField(root, ExsConstants.PART_ID_SOURCE, (String)MessageHead.get(ExsConstants.PART_ID_SOURCE));
		SysUtility.putJsonField(root, ExsConstants.MESSAGE_SOURCE, (String)MessageHead.get(ExsConstants.MESSAGE_SOURCE));
		SysUtility.putJsonField(root, ExsConstants.MESSAGE_DEST, (String)MessageHead.get(ExsConstants.MESSAGE_DEST));
		SysUtility.putJsonField(root, ExsConstants.MESSAGE_ID, (String)MessageHead.get(ExsConstants.MESSAGE_ID));
		SysUtility.putJsonField(root, ExsConstants.MESSAGE_TIME, (String)MessageHead.get(ExsConstants.MESSAGE_TIME));
		if(SysUtility.isEmpty(SysUtility.getJsonField(root, "MESSAGE_TYPE"))){
			SysUtility.putJsonField(root, ExsConstants.MESSAGE_TYPE, (String)MessageHead.get(ExsConstants.MESSAGE_TYPE));
		}
	}
	
	public static void SetRootField(ServicesBean bean,String RootName,Datas datas) throws LegendException{
//		datas.RemoveTableValue(RootName, SysUtility.KeyFieldDefault);
//		datas.RemoveTableValue(RootName, "INDX");
//		datas.RemoveTableValue(RootName, "P_INDX");
		datas.RemoveTableValue(RootName, "CREATOR");
		datas.RemoveTableValue(RootName, "CREATE_TIME");
		datas.RemoveTableValue(RootName, "MODIFYOR");
		datas.RemoveTableValue(RootName, "MODIFY_TIME");
		datas.RemoveTableValue(RootName, "MODIFY_DATE");
		datas.RemoveTableValue(RootName, "REC_VER");
		
		//发送方与接收方处理
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, ExsConstants.PART_ID))){
			datas.SetTableValue(RootName, ExsConstants.PART_ID, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST));
		}
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, ExsConstants.MESSAGE_SOURCE))){
			datas.SetTableValue(RootName, ExsConstants.MESSAGE_SOURCE, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE));
		}
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, ExsConstants.MESSAGE_DEST))){
			datas.SetTableValue(RootName, ExsConstants.MESSAGE_DEST, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST));
		}
		datas.SetTableValue(RootName, ExsConstants.MESSAGE_ID, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_ID));
		datas.SetTableValue(RootName, ExsConstants.MESSAGE_TIME, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_TIME));
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, "MESSAGE_TYPE"))){
			datas.SetTableValue(RootName, ExsConstants.MESSAGE_TYPE, datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_TYPE));
		}
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, "EXP_MESSAGE_SOURCE"))){
			datas.SetTableValue(RootName, "EXP_MESSAGE_SOURCE", datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE));
		}
		if(SysUtility.isEmpty(datas.GetTableValue(RootName, "EXP_MESSAGE_DEST"))){
			datas.SetTableValue(RootName, "EXP_MESSAGE_DEST", datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST));
		}
	}
	
	public static void SetChildField(JSONObject root,JSONObject child,ServicesBean bean,String ChildXmlDocument) throws LegendException{
		SysUtility.removeJsonField(child, SysUtility.KeyFieldDefault);
		SysUtility.removeJsonField(child, "INDX");
		SysUtility.removeJsonField(child, "PART_ID");
		SysUtility.removeJsonField(child, "ORG_ID");
		SysUtility.removeJsonField(child, "CREATOR");
		SysUtility.removeJsonField(child, "CREATE_TIME");
		SysUtility.removeJsonField(child, "MODIFYOR");
		SysUtility.removeJsonField(child, "MODIFY_TIME");
		SysUtility.removeJsonField(child, "REC_VER");
		SysUtility.putJsonField(child, "PART_ID_SOURCE", SysUtility.getJsonField(child, "PART_ID"));
		SysUtility.putJsonField(child, "P_INDX", SysUtility.getJsonField(root, "INDX"));
		
		HashMap ChildindxNameMap = SysUtility.StrsToHashMap(bean.getRootNames(), bean.getPindxName());
	    String ChildIndxName = (String)ChildindxNameMap.get(ChildXmlDocument);
		//填充主表的外键值到子表中
		if(!ExsConstants.INDX.equalsIgnoreCase(bean.getIndxName())){
			SysUtility.putJsonField(child, bean.getIndxName(), bean.getIndxNameValue());//子表外键：如：ENT_FILING_INFO_ID
		}
		//填充子表的主键值
		if(SysUtility.isEmpty(SysUtility.getJsonField(child, ChildIndxName)) && SysUtility.isNotEmpty(ChildIndxName)){
			SysUtility.putJsonField(child, ChildIndxName, SysUtility.GetOracleSysGuid());//子表主键：如：ENT_ATTACHED_ID
		}
		bean.setCindxName(ChildIndxName);
	}
	
	public static HashMap<String,String> ReturnProcessMsg(String processMsg,String operatorType){
		HashMap<String,String> rtMap = new HashMap<String,String>();
		rtMap.put("processMsg", processMsg);
		rtMap.put("operatorType", operatorType);
		return rtMap;
	}
	
	public static String GetFormatPointCode(String FieldPointCode,HashMap headMap){
		String checkPointCode = "";
		if(SysUtility.isEmpty(FieldPointCode)){
			return checkPointCode;
		}
		String[] points = FieldPointCode.split(",");
		if(points.length < 2){
			LogUtil.printLog("字段校验检查点配置错误："+FieldPointCode+"正确如：COULMN_NAME,COULMN_VALUE=POINT_CODE", Level.ERROR);
			return checkPointCode;
		}
		String PValue = (String)headMap.get(points[0]);
		if(SysUtility.isEmpty(PValue)){
			LogUtil.printLog("字段："+points[0]+"不允许为空", Level.ERROR);
			return checkPointCode;
		}
		for (int i = 1; i < points.length; i++) {
			String tempStr = points[i];//FI=DECL_SFI
			String[] tempStrs = tempStr.split("=");
			if(PValue.equals(tempStrs[0])){
				checkPointCode = tempStrs[1];
				break;
			}
		}
		if(SysUtility.isEmpty(checkPointCode)){
			LogUtil.printLog("字段校验检查点配置错误："+FieldPointCode+"正确如：COULMN_NAME,COULMN_VALUE=POINT_CODE", Level.ERROR);
			return checkPointCode;
		}
		return checkPointCode;
	}
		
	public static String GetMappingPointCode(String MappingPointCode,HashMap headMap){
		String checkPointCode = "";
		if(SysUtility.isEmpty(MappingPointCode)){
			return checkPointCode;
		}
		String[] points = MappingPointCode.split(",");
		if(points.length < 2){
			LogUtil.printLog("规则配置检查点配置错误："+MappingPointCode+"正确如：COULMN_NAME,COULMN_VALUE=POINT_CODE", Level.ERROR);
			return checkPointCode;
		}
		String PValue = (String)headMap.get(points[0]);
		if(SysUtility.isEmpty(PValue)){
			LogUtil.printLog("字段："+points[0]+"不允许为空", Level.ERROR);
			return checkPointCode;
		}
		for (int i = 1; i < points.length; i++) {
			String tempStr = points[i];//FI=DECL_SFI
			String[] tempStrs = tempStr.split("=");
			if(PValue.equals(tempStrs[0])){
				checkPointCode = tempStrs[1];
				break;
			}
		}
		if(SysUtility.isEmpty(checkPointCode)){
			LogUtil.printLog("规则配置检查点配置错误："+MappingPointCode+"正确如：COULMN_NAME,COULMN_VALUE=POINT_CODE", Level.ERROR);
			return checkPointCode;
		}
		return checkPointCode;
	}
	
	public static String parseRequestXml(Map MessageHead, Map rootMap,String rootName,String Encoding){
		return parseRequestXml(MessageHead, rootMap, rootName, Encoding, new HashMap());
	}
	
	public static String parseRequestXml(Map MessageHead, Map rootMap,String rootName,String Encoding,HashMap MappingColumns){
		StringBuffer xmlData = new StringBuffer();
		xmlData.append("<?xml version=\"1.0\" encoding=\""+Encoding+"\"?>\n");
		xmlData.append("<RequestMessage>\n");
		xmlData.append("\t<MessageHead>\n");
		xmlData.append("\t\t<MESSAGE_ID>").append(MessageHead.get(ExsConstants.MESSAGE_ID)).append("</MESSAGE_ID>\n");
		xmlData.append("\t\t<MESSAGE_TYPE>").append(MessageHead.get(ExsConstants.MESSAGE_TYPE)).append("</MESSAGE_TYPE>\n");
		xmlData.append("\t\t<MESSAGE_TIME>").append(MessageHead.get(ExsConstants.MESSAGE_TIME)).append("</MESSAGE_TIME>\n");
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.SendCode))){
			xmlData.append("\t\t<SEND_CODE>").append(MessageHead.get(ExsConstants.SendCode)).append("</SEND_CODE>\n");
		}else{
			xmlData.append("\t\t<MESSAGE_SOURCE>").append(MessageHead.get(ExsConstants.MESSAGE_SOURCE)).append("</MESSAGE_SOURCE>\n");
		}
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.ReciptCode))){
			xmlData.append("\t\t<RECIPT_CODE>").append(MessageHead.get(ExsConstants.ReciptCode)).append("</RECIPT_CODE>\n");
		}else{
			xmlData.append("\t\t<MESSAGE_DEST>").append(MessageHead.get(ExsConstants.MESSAGE_DEST)).append("</MESSAGE_DEST>\n");
		}
		xmlData.append("\t</MessageHead>\n");
		xmlData.append("\t<MessageBody>\n");
		xmlData.append(FileUtility.hashMapToComXml(rootMap, rootName, 3, MappingColumns));
		xmlData.append("\t</MessageBody>\n");
		xmlData.append("</RequestMessage>");
		return xmlData.toString();
	}
	
	public static void groupChild(List childList, Map childMap,String P_INDX) {
		Iterator it = childList.iterator();
		while (it.hasNext()) {
			Map tmpMap = (Map) it.next();
			String pIndx = (String) tmpMap.get(P_INDX);
			if (childMap.containsKey(pIndx)) {
				((List) childMap.get(pIndx)).add(tmpMap);
			} else {
				List tmpLst = new ArrayList();
				tmpLst.add(tmpMap); 
				childMap.put(pIndx, tmpLst);
			}
		}
	}
	
	public static String setRuleMessage(IDataAccess DataAccess,ServicesBean bean,HashMap CheckMap,
			HashMap headMap,String xmlDocumentName,String HIT_PATH,String point)throws Exception{
		/*************命中负面清单或黑名单，回执电子审单命中状态(电子审单未通过)*******************/
		String ruleHitMessage = (String)CheckMap.get("RULE_HIT_MESSAGE");
		CheckMap.remove("RULE_HIT_MESSAGE");
		String RE = (String)CheckMap.get("RE");
		CheckMap.remove("RE");
		
		headMap.put("RULE_REMARK", ruleHitMessage);
		if("Y".equals(RE)){
			InsertReceivedFail(DataAccess, bean,ruleHitMessage);
			return HIT_PATH;//命中负面或黑名单规则
		}
		return "";
	}
	
	
	/**
	 * 回执失败
	 * **/
	public static void InsertReceivedFail(IDataAccess DataAccess ,ServicesBean bean,String MSG_DESC)
			throws Exception{
		InsertReceived(DataAccess, bean, "F", MSG_DESC);
	}
	
	/**
	 * 回执成功
	 * **/
	public static void InsertReceivedSuccess(IDataAccess DataAccess,ServicesBean bean,String MSG_DESC)throws JSONException, LegendException{
		String MSG_RECIPT_CODE = bean.getMessageDest();
		InsertReceived(DataAccess, bean, "S", MSG_DESC);
	}
	
	/**
	 * 插入回执
	 * **/
	public static void InsertReceived(IDataAccess DataAccess,ServicesBean bean,String MSG_CODE,String MSG_DESC)throws JSONException, LegendException{
		Connection conn = SysUtility.getCurrentDynamicConnection(bean.getIndx());

		JSONObject data = new JSONObject();
		data.put("message_source", bean.getMessageSource());
		data.put("message_dest", bean.getMessageDest());
		if(SysUtility.isNotEmpty(MSG_DESC) && MSG_DESC.getBytes().length >= 2000){
			MSG_DESC = MSG_DESC.substring(0, 1500)+"...";
		}
		String uuid = SysUtility.GetUUID();
		data.put("INDX", uuid);
		data.put("MSG_NO", bean.getSerialNo());
		data.put("MSG_CODE", MSG_CODE);
		data.put("MSG_NAME", MSG_CODE.equals("S")?"接收成功":"接收失败");
		data.put("MSG_DESC", MSG_DESC);
		data.put("MSG_TIME", SysUtility.getSysDate());
		data.put("MSG_TYPE", bean.getMessageType());
		data.put("MSG_BIZ_TYPE", bean.getReceiptBizType());
		DataAccess.Insert("exs_handle_received", data, "INDX", conn);

		if(SysUtility.isNotEmpty(bean.getReceiptType())){
			JSONObject obj = new JSONObject();
			data.put("INDX", SysUtility.GetUUID());
			obj.put("msg_type", bean.getReceiptType());
			obj.put("msg_no", uuid);
			obj.put("message_source", bean.getMessageDest());
			obj.put("message_dest", bean.getMessageSource());
			DataAccess.Insert("exs_handle_sender", obj, "INDX", conn);
		}
	}
	
	
	public static HashMap<String,HashMap<String,String>> docNameMap = new HashMap<String,HashMap<String,String>>();
	
	public static String SetNameValue(String tableName, String codeCol,String nameCol,
			JSONObject data, IDataAccess DataAccess)throws Exception{
		return SetNameValue(null, tableName, codeCol, nameCol, data, DataAccess);
	}
	
	public static String SetNameValue(String SQL, String tableName,String codeCol,String nameCol,
			JSONObject data, IDataAccess DataAccess)throws Exception{
		if(!data.has(codeCol) || SysUtility.isEmpty(data.getString(codeCol)) || SysUtility.isEmpty(tableName)){
//			return "参数传递出错：表名"+tableName+" 列名"+codeCol;
			return "";
		}
		String itemCode = "ITEM_CODE";
		String itemName = "ITEM_NAME";
		HashMap<String,String> tableMap = docNameMap.get(tableName);
		if(SysUtility.isEmpty(tableMap)){
			tableMap = new HashMap<String,String>();
			docNameMap.put(tableName, tableMap);
		}
		final String codeValue = data.getString(codeCol);
		String nameValue = tableMap.get(codeValue);
		if(SysUtility.isEmpty(nameValue)){
			final String[] tempCodeValues = codeValue.split(",");
			if(SysUtility.isEmpty(SQL)){
				StringBuffer tempSQL = new StringBuffer();
				tempSQL.append("SELECT "+itemName+" FROM "+tableName+" WHERE "+itemCode+" in (");
				for (int i = 0; i < tempCodeValues.length; i++) {
					if(i == 0){
						tempSQL.append("?");
					}else{
						tempSQL.append(",?");
					}
				}
				tempSQL.append(")");
				SQL = tempSQL.toString();
			}
			try {
			 	List lst = SQLExecUtils.query4List(SQL, new Callback() {
					public void doIn(PreparedStatement ps) throws SQLException {
						for (int i = 0; i < tempCodeValues.length; i++) {
							ps.setString(i+1, tempCodeValues[i]);
						}
						
					}
				});
			 	for (int i = 0; i < lst.size(); i++) {
					HashMap map = (HashMap)lst.get(i);
			 		if(i == 0){
			 			nameValue = (String)map.get(itemName);
			 		}else{
			 			nameValue = nameValue + "," +(String)map.get(itemName);
			 		}
				}
			 	
			} catch (Exception e) {
				LogUtil.printLog(tableName+"读取code失败！", Level.ERROR);
			}
			if(SysUtility.isEmpty(nameValue)){
				return "字段："+codeCol+" 值："+codeValue+" 在表"+tableName+":"+"中不存在！\n";
			}
			tableMap.put(codeValue, nameValue);
		}
		data.put(nameCol, nameValue);
		return "";
	}
	
	public static HashMap<String,String> getFileNameElements(ServicesBean bean,String fileName){
		HashMap<String,String> rtMap = new HashMap<String,String>();
		
		String systemFlag = "";
		String messageType = "";
		String messageDest = "";
		String serialNo = "";
		
		//报文类型含下划线特殊处理。
		List<ServicesBean> lst = bean.getAllBeanList();
		for (int i = 0; i < lst.size(); i++) {
			ServicesBean tempBean = lst.get(i);
			
			String beanMessageType = tempBean.getMessageType();
			if(fileName.indexOf(beanMessageType+"_") >= 0) {//TYPE_001_02.xml、001_TYPE_02.xml
				fileName = fileName.replaceAll(beanMessageType, "#");
				messageType = beanMessageType;
				break;
			}
		}
		
		String[] fns = fileName.split("_");
		if(SysUtility.isNotEmpty(fns) && fns.length == 2){
			systemFlag = "";
			if(SysUtility.isEmpty(messageType)) {
				messageType = fns[0];
			}
			messageDest = "";
			serialNo = fns[1];
		}else if(SysUtility.isNotEmpty(fns) && fns.length == 3){
			systemFlag = "";
			if(SysUtility.isEmpty(messageType)) {
				messageType = fns[0];
			}
			messageDest = fns[1];
			serialNo = fns[2];
		}else if(SysUtility.isNotEmpty(fns) && fns.length == 4){
			systemFlag = fns[0];
			if(SysUtility.isEmpty(messageType)) {
				messageType = fns[1];
			}
			messageDest = fns[2];
			serialNo = fns[3];
		}
		rtMap.put(ExsConstants.SystemFlag, systemFlag);
		rtMap.put(ExsConstants.MessageType, messageType);
		rtMap.put(ExsConstants.MessageDest, messageDest);
		rtMap.put(ExsConstants.SerialNo, serialNo);
		
		return rtMap;
	}
	
	
	static {
		String dbType = SysUtility.GetSetting("System", "DBType");
		if (Constants.Oracle.equalsIgnoreCase(dbType)){
			engine = new OracleCalculateEngine();
		} else if (Constants.Mysql.equalsIgnoreCase(dbType)) {
			engine = new MysqlCalculateEngine();
		} else if (Constants.Sqlserver.equalsIgnoreCase(dbType)) {
			engine = new SqlserverCalculateEngine();
		} 
	}
	
}
