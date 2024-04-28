package com.easy.convert.service.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;


public class Util {
	
	
	@SuppressWarnings("rawtypes")
	public static boolean AddMessLog(IDataAccess access,Map dataMap) throws JSONException, LegendException {
		try {
			access.BeginTrans();
			java.util.Date a = new java.util.Date();
			JSONObject jsonObject =new JSONObject();
			String SERIAL_NO = "";
			String FILE_NAME = "";
			if(SysUtility.isNotEmpty(dataMap.get("DATA_SOURCE"))) {
				jsonObject.put("DATA_SOURCE", dataMap.get("DATA_SOURCE"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SERIAL_NO"))) {
				jsonObject.put("SERIAL_NO", dataMap.get("SERIAL_NO"));
				SERIAL_NO = (String) dataMap.get("SERIAL_NO");
			}
			if(SysUtility.isNotEmpty(dataMap.get("TARGET_FILE_NAME"))) {
				jsonObject.put("TARGET_FILE_NAME", dataMap.get("TARGET_FILE_NAME"));
				FILE_NAME = (String) dataMap.get("TARGET_FILE_NAME");
			}
			if(SysUtility.isNotEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
				jsonObject.put("SOURCE_FILE_NAME", dataMap.get("SOURCE_FILE_NAME"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("FILE_PATH"))) {
				jsonObject.put("FILE_PATH", dataMap.get("FILE_PATH"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("PROCESS_MSG"))) {
				jsonObject.put("PROCESS_MSG", dataMap.get("PROCESS_MSG"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_CODE"))) {
				jsonObject.put("TRANSFORMATION_CODE", dataMap.get("TRANSFORMATION_CODE"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_NAME"))) {
				jsonObject.put("TRANSFORMATION_NAME", dataMap.get("TRANSFORMATION_NAME"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SEND_CODE"))) {
				jsonObject.put("SEND_CODE", dataMap.get("SEND_CODE"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SEND_NAME"))) {
				jsonObject.put("SEND_NAME", dataMap.get("SEND_NAME"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SEND_TIME"))) {
				jsonObject.put("SEND_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			if(SysUtility.isNotEmpty(dataMap.get("RECEIPT_RESULT"))) {
				jsonObject.put("RECEIPT_RESULT", dataMap.get("RECEIPT_RESULT"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SOURCE_BACK_PATH"))) {
				jsonObject.put("SOURCE_BACK_PATH", dataMap.get("SOURCE_BACK_PATH"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("SUCCESS_BACK_PATH"))) {
				jsonObject.put("SUCCESS_BACK_PATH", dataMap.get("SUCCESS_BACK_PATH"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("REMARKS"))) {
				jsonObject.put("REMARKS", dataMap.get("REMARKS"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("CONFIG_PATH_ID"))) {
				jsonObject.put("CONFIG_PATH_ID", dataMap.get("CONFIG_PATH_ID"));
			}
			if(SysUtility.isNotEmpty(dataMap.get("RECEIPT_TIME"))) {
				jsonObject.put("RECEIPT_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			
			if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_TIME"))) {
				jsonObject.put("TRANSFORMATION_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			boolean flah = false;
			String indx = "";
			String filename = "";
			String SOURCE_FILE_NAME = "";
			//查询是否存在 
			String sql = "SELECT INDX,SUCCESS_BACK_PATH,SOURCE_FILE_NAME FROM exs_convert_log WHERE SERIAL_NO='"+SERIAL_NO+"' AND TARGET_FILE_NAME='"+FILE_NAME+"'";
			List query4List = SQLExecUtils.query4List(sql);
			if(query4List.size()>0) {
				indx = (String) ((HashMap)query4List.get(0)).get("INDX");
				filename = (String) ((HashMap)query4List.get(0)).get("SUCCESS_BACK_PATH");
				SOURCE_FILE_NAME = (String) ((HashMap)query4List.get(0)).get("SOURCE_FILE_NAME");
				/*if(SysUtility.isNotEmpty(filename)) {
					jsonObject.put("SUCCESS_BACK_PATH", filename+","+dataMap.get("SUCCESS_BACK_PATH"));
				}	*/

				if (!SOURCE_FILE_NAME.substring(SOURCE_FILE_NAME.lastIndexOf(".")+1).toUpperCase().equals("XLS")) {
					jsonObject.put("INDX", indx);
				}
				
			}else{
				sql = "SELECT INDX,SUCCESS_BACK_PATH,SOURCE_FILE_NAME FROM exs_convert_log WHERE SERIAL_NO='"+SERIAL_NO+"' AND SUCCESS_BACK_PATH  LIKE '%"+FILE_NAME+"'";
				query4List = SQLExecUtils.query4List(sql);
				if(query4List.size()>0) {
					indx = (String) ((HashMap)query4List.get(0)).get("INDX");
					if (!SOURCE_FILE_NAME.substring(SOURCE_FILE_NAME.lastIndexOf(".")+1).toUpperCase().equals("XLS")) {
						jsonObject.put("INDX", indx);
						flah = true;
					}
				}else {
					String[] fileNameSplit = FILE_NAME.split("_");
					String name = "";
					for(int b=0;b<fileNameSplit.length-1;b++) {
						name += fileNameSplit[b]+"_";
					}
					if(SysUtility.isNotEmpty(name)) {
						name = name.substring(0, name.length()-1);
					}
					String seq = fileNameSplit[fileNameSplit.length-1];
					seq = seq.split("\\.")[0];
					sql = "SELECT INDX,SUCCESS_BACK_PATH,SOURCE_FILE_NAME,TARGET_FILE_NAME FROM exs_convert_log WHERE SERIAL_NO='"+SERIAL_NO+"' AND TARGET_FILE_NAME like '%"+name+"%' ORDER BY TARGET_FILE_NAME DESC";
					query4List = SQLExecUtils.query4List(sql);
					if(query4List.size()>0) {
						HashMap Xmap2 = (HashMap) query4List.get(0);
						String TARGET_FILE_NAME = (String) Xmap2.get("TARGET_FILE_NAME");
						String targetName = TARGET_FILE_NAME.split("\\.")[0];
						String name1 = targetName+"_"+seq+".xml";
						if(name1.equals(FILE_NAME)) {
							jsonObject.remove("TARGET_FILE_NAME");
							indx = (String) Xmap2.get("INDX");
							jsonObject.put("INDX", indx);
							flah =true;
						}
					}else {
						
						
					}
				}
			}
			if(SysUtility.isEmpty(indx)) {
				if(SysUtility.isEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
					if(SysUtility.isNotEmpty(SOURCE_FILE_NAME)) {
						jsonObject.put("SOURCE_FILE_NAME", SOURCE_FILE_NAME);
					}else {
						return false;
					}
				}
				boolean insert = access.Insert("exs_convert_log", jsonObject);
				if ( insert)access.ComitTrans();
				return  insert;
			}else {
				if(!flah) {
					if(SysUtility.isEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
						if(SysUtility.isNotEmpty(SOURCE_FILE_NAME)) {
							jsonObject.put("SOURCE_FILE_NAME", SOURCE_FILE_NAME);
						}else {
							return false;
						}
					}
				}
				boolean update = access.Update("exs_convert_log", jsonObject, "INDX");
				if( update) access.ComitTrans();
				
				return  update;
			}
		
	}catch (Exception e) {
		access.RoolbackTrans();
	} 
		return false;
	}
	
	/**
	 * 获取连接
	 * @param SendType 发送类型
	 * @param SendDress 发送地址
	 * @param UserName 用户名
	 * @param PassWord 密码
	 * @return conn连接
	 */
	public static Connection GetConn(String SendType,String SendDress,String UserName,String PassWord) {
		Connection conn=null;
		String url;//数据库连接地址
		String ip = "";//IP地址
		String port = "";//端口
		String table = "";//表名|实例名
		if(SendDress.split(":").length!=3) {
			throw new RuntimeException("请按照格式填写发送方地址  !");
		}else {
			ip = SendDress.split(":")[0];
			port = SendDress.split(":")[1];
			table = SendDress.split(":")[2];
		}
		if("6".equals(SendType)){
			try
		    {
			// 加载驱动
		    	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
		    }
			catch(ClassNotFoundException ex)
			{
				throw new RuntimeException("加载sqlserver驱动失败");
			}	
			
			
			try {
				url = ip+":"+port+";DatabaseName="+table;
				conn = DriverManager.getConnection("jdbc:sqlserver://"+url, UserName, PassWord);
				
			} catch (Exception e) {
				throw new RuntimeException("连接失败");
			}
		}else if("7".equals(SendType)){
			try
		    {
			// 加载驱动
				Class.forName("oracle.jdbc.driver.OracleDriver");  
		    }
			catch(ClassNotFoundException ex)
			{
				throw new RuntimeException("加载oracle驱动失败");
			}	
			try {  
	        	 url = ip+":"+port +":"+table;
	            // 通过驱动管理类获取数据库连接  
	            conn = DriverManager.getConnection("jdbc:oracle:thin:@"+url, UserName, PassWord);		            
	        } 
	        catch (Exception e) {
	        	throw new RuntimeException("连接失败");
	        }
		}else if("8".equals(SendType)){
			try
			{
			Class.forName("com.mysql.jdbc.Driver"); 
			}
			catch(ClassNotFoundException ex)
			{
				throw new RuntimeException("加载mysql驱动错误");
			}
			
			try {  
		           url = ip+":"+port+"/"+table;
		           // 通过驱动管理类获取数据库连接  
		           conn = DriverManager.getConnection("jdbc:mysql://"+url,UserName,PassWord);		            
		        } 
		         catch (Exception e) {  
		        	 throw new RuntimeException("连接失败");
		        }  
		} 
		return conn;
	}
	
	
	
	/**
     * 格式化
     * 
     * @param jsonStr
     * @return
     * @author lizhgb
     * @Date 2015-10-14 下午1:17:35
     * @Modified 2017-04-28 下午8:55:35
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
            case '"':
                                if (last != '\\'){
                    isInQuotationMarks = !isInQuotationMarks;
                                }
                sb.append(current);
                break;
            case '{':
            case '[':
                sb.append(current);
                if (!isInQuotationMarks) {
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                }
                break;
            case '}':
            case ']':
                if (!isInQuotationMarks) {
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                }
                sb.append(current);
                break;
            case ',':
                sb.append(current);
                if (last != '\\' && !isInQuotationMarks) {
                    sb.append('\n');
                    addIndentBlank(sb, indent);
                }
                break;
            default:
                sb.append(current);
            }
        }

        return sb.toString();
    }
    
    
    /**
     * 添加space
     * 
     * @param sb
     * @param indent
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }
   
    
    public static Map toUpperCaseMap(Map map) {
    	Map<String,Object> obdmap = new HashMap<String, Object>();    
    	Set<String> keySet = map.keySet();
    	for(String set: keySet) {
    		 obdmap.put(set.toUpperCase(), map.get(set));        
    	}
    	return obdmap;
    }
    
    
    //修改任务状态
    public static boolean UpHanderFlatOk(String indx,IDataAccess dataAccess) throws LegendException, JSONException {
		if(!SysUtility.isEmpty(dataAccess)) {
			dataAccess.BeginTrans();
			Datas datas = new Datas();
			datas.put("INDX", indx);
			datas.put("MSG_FLAG", "1");
			datas.put("MODIFY_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			boolean flat = dataAccess.Update("exs_handle_sender", datas);
			dataAccess.ComitTrans();
			return flat;
		}else {
			return false;
		}
		
	}
    
    //修改任务状态
    public static boolean UpHanderFlatErr(String indx,IDataAccess dataAccess) throws LegendException, JSONException {
		if(!SysUtility.isEmpty(dataAccess)) {
			dataAccess.BeginTrans();
			Datas datas = new Datas();
			datas.put("INDX", indx);
			datas.put("MSG_FLAG", "2");
			datas.put("MODIFY_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			boolean flat = dataAccess.Update("exs_handle_sender", datas);
			dataAccess.ComitTrans();
			return flat;
		}else {
			return false;
		}
		
	}

}
