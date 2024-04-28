package com.easy.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.constants.Constants;
import com.easy.exception.LegendException;
import com.easy.mail.MailSender;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.query.SqlParamList;
import com.easy.query.SqlParameter;
import com.easy.security.MD5Utility;
import com.easy.session.Operator;
import com.easy.session.SessionKeyType;
import com.easy.session.SessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
@Component
public class SysUtility {
	public final static String EMPTY = "";
	public static int Milli = 0;
	public static int Milli16 = 0;
	public final static String KeyFieldDefault = "Indx";
	public final static String LoginUser = "LoginUser";
	public final static String UPDATE = "Update";
	public final static String DELETE = "Delete";
	public final static String INSERT = "Insert";
	public final static String ActiveCN = "ActiveCN";
	public static HashMap<String,List<String>> allTableCoulmnNames = new HashMap<String,List<String>>();
	public static HashMap<String,HashMap<String,Integer>> allTableCoulmnTypes = new HashMap<String,HashMap<String,Integer>>();
	private static final LinkedHashMap dateFormat = new LinkedHashMap();
	public static HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>();
	public static final String ROWNUM = "ROWNUM";
	public static BASE64Encoder base64encoder = new BASE64Encoder();
	public static BASE64Decoder base64decoder = new BASE64Decoder();
	private static String CssTemplet;
	public static final String exs_temp_id = "INSERT INTO exs_temp_id T(ID) VALUES(?)";
	public static boolean TomcatPool = false;
	public static boolean ProxoolPool = true;
	public static boolean HibernatePool = false;
	public static final String XML_UTF8_ENCODING = "UTF-8";

	static {
		initPattern();
		if("open".equals(GetSetting("system", "TomcatPool"))){
			TomcatPool = true;
		}else if("open".equals(GetSetting("system", "ProxoolPool"))){
			ProxoolPool = true;
		}else if(getDBPoolClose()){
			//TODO
		}
	}
	
	public static boolean getDBPoolClose() {
//		String DBName = GetSetting("system", "DBName");
//		if(SysUtility.isEmpty(DBName) || "true".equals(GetSetting("system", "DBPoolClose"))){
//			return true;
//		}
		return false;
	}
	
	public static String getCssTemplet() {
		return CssTemplet;
	}
	public static void setCssTemplet(String cssTemplet) {
		CssTemplet = cssTemplet;
	}
	
	public static boolean isEmpty(String str) {
        return ("null".equalsIgnoreCase(str) || str == null || str.trim().equals(""));
    }

	public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
	
	public static boolean isEmpty(Object obj) {
		try {
			if(obj!=null){
				if (obj instanceof String) 
					return ((String)obj).trim().equals("");
				if (obj instanceof Map) 
					return ((Map)obj).isEmpty()||((Map)obj).size()==0;
				if (obj instanceof List) 
					return ((List)obj).isEmpty()||((List)obj).size()==0;
				if (obj instanceof Long) 
					return ((Long)obj)<=0;
				if (obj instanceof StringBuffer) 
					return ((StringBuffer)obj).toString().trim().equals("");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean isNotEmpty(Object obj){
		return !isEmpty(obj);
	}
	
	public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }
	
	public static boolean isNull(Object obj) {
        return obj == null;
    }
	
	public static String closeResultSet(ResultSet rs) {
		try {
			if(rs != null){
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			return e.getMessage();
		}
		return "";
	}
	
	public static String closeStatement(Statement stmt){
		try{
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
		}catch(SQLException e){
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return e.getMessage();
		}
		return "";
	}
	
	public static String closeCallableStatement(CallableStatement cs) {
		try {
			if (cs != null){
				cs.close();
			}
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return e.getMessage();
		}
		return "";
	}

	public static void closeActiveCN() {
		closeActiveCN(getCurrentConnection());
	}

	public static void closeActiveCN(Connection ActiveCN) {
		if (ActiveCN == null) {
			return;
		}
		try {
			// 把connection的autoCommit改回默认值后,返回给连接池
			if (!ActiveCN.getAutoCommit()) {
				ActiveCN.rollback();
				ActiveCN.setAutoCommit(false);
			}
		} catch (SQLException e) {
			LogUtil.printLog("回滚事务发生错误", LogUtil.ERROR);
		} finally {
			try {
				ActiveCN.close();
			} catch (SQLException e) {
				LogUtil.printLog("释放数据库连接发生错误", LogUtil.ERROR);
			}
		}
	}
	
	public static String getSysDate() {
		return getDateFormat().format(new Date());
	}
	
	public static String getSysDateWithoutTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}
	
	public static String getSysDateMonth() {
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
	    return format.format(new Date());
	}
	
	public static String getSysDateWithMilliseconds() {
		Date myDate = new Date();
		String str= getDateFormat().format(myDate);
		String millsTime = ""+myDate.getTime();
		return str+" "+millsTime.substring(millsTime.length()-3, millsTime.length());
	}
	
	public static String GetYesterday(){
		String dateStr = "";
		try {
			Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(SysUtility.getSysDateWithoutTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			dateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());
		} catch (ParseException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return dateStr;
	}
	
	public static String getSysYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int rt = c.get(Calendar.YEAR);
		String result = String.valueOf(rt);
		if(result.length() == 1){
			result = "0"+result;
		}
		return result;
	}
	public static String getSysMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int rt = c.get(Calendar.MONTH) + 1;
		String result = String.valueOf(rt);
		if(result.length() == 1){
			result = "0"+result;
		}
		return result;
	}
	public static String getSysDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int rt = c.get(Calendar.DAY_OF_MONTH);
		String result = String.valueOf(rt);
		if(result.length() == 1){
			result = "0"+result;
		}
		return result;
	}
	public static String getSysDayWeek() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int rt = c.get(Calendar.DAY_OF_WEEK);
		String result = String.valueOf(rt);
		if(result.length() == 1){
			result = "0"+result;
		}
		return result;
	}
	
	public static String getHourOfDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int hours = c.get(Calendar.HOUR_OF_DAY);
		String result = String.valueOf(hours);
		if(result.length() == 1){
			result = "0"+result;
		}
		return result;
	}
	
	public static String getSysDate(String currentTime,int second){
		String dateStr = "";
		try {
			Date currentDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(currentTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + second);
			dateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());
		} catch (ParseException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return dateStr;
	}
	
	
	public static synchronized String getMilliSeconds() {
		Milli++;
		if(Milli >= 9999){
			Milli = 1;
		}
		String only = String.valueOf((new Date()).getTime());
		if(Milli < 10){
			only = only+"000"+Milli;
		}else if(Milli >= 10 && Milli < 100){
			only = only+"00"+Milli;
		}else if(Milli >= 100 && Milli < 1000){
			only = only+"0"+Milli;
		}else if(Milli >= 1000 && Milli < 10000){
			only = only+Milli;
		}
		return only;
	}
	
	public static synchronized String getMilli16Seconds() {
		Milli16++;
		if(Milli16 >= 999){
			Milli16 = 1;
		}
		String only = String.valueOf((new Date()).getTime());
		if(Milli16 < 10){
			only = only+"00"+Milli16;
		}else if(Milli16 >= 10 && Milli16 < 100){
			only = only+"0"+Milli16;
		}else if(Milli16 >= 100 && Milli16 < 1000){
			only = only+Milli16;
		}
		return only;
	}
	
	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 将字符串转为日期
	 * */
	public static String DataFormatStr(Date date){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	/**
	 * 将字符串转为日期
	 * */
	public static Date strFormatDate(String str){
		if(isEmpty(str)) return null;
		String rule = null; 
		if(str.indexOf("-") > 0) 
			rule = "yyyy-MM-dd HH:mm:ss";
		if(str.indexOf("/") > 0)
			rule = "yyyy/MM/dd HH:mm:ss";
		if(null == rule) return null;	
		SimpleDateFormat sdf = new SimpleDateFormat(rule);
		try {
			return sdf.parse(str);
		} catch (Exception e) {
			 LogUtil.printLog(e.getMessage(),LogUtil.ERROR,e);;
		}
		return null;
	}
	
	/**
	 * 处理数据库的时间戳字段，转为标准的日期变量
	 * 时间戳格式：yyyy-MM-dd HH:mm:ss.S
	 * 标准格式：yyyy-MM-dd HH:mm:ss
	 * */
	public static Date perfectTimestamp(Date date){
		String strDate = DataFormatStr(date);
		return strFormatDate(strDate);
	}
	
	/**
	 * 将字符串yyyy-MM-dd 转为 yyyy-MM-dd HH:mm:ss
	 **/
	public static String perfectDateStr(String str){
		Date strDate = strFormatDate(str);
		return DataFormatStr(strDate);
	}
	
	public static void bindParams(PreparedStatement stmt,Object parms)throws SQLException{
		if(stmt == null || parms == null || "".equals(parms))
			return ;
		
		if(parms instanceof String){
			stmt.setString(1, parms.toString());
			LogUtil.printLog("parms<String>:"+parms.toString(), Level.INFO);
		}else if(parms instanceof Integer){
			stmt.setInt(1, ((Integer)parms).intValue());
			LogUtil.printLog("parms<Int>:"+((Integer)parms).intValue(), Level.INFO);
		}else if(parms instanceof Long){
			stmt.setLong(1, ((Long)parms).longValue());
			LogUtil.printLog("parms<Long>:"+((Long)parms).longValue(), Level.INFO);
		}else if(parms instanceof String[]){
			String[] p = (String[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotEmpty(p[i-1])){
					stmt.setString(i, p[i-1]);
					LogUtil.printLog("parms" + i + "<String>:"+p[i-1], Level.INFO);
				}else{
					stmt.setNull(i, Types.VARCHAR);
					LogUtil.printLog("parms" + i + "<String>:"+Types.VARCHAR, Level.INFO);
				}
			}
		}else if(parms instanceof Integer[]){
			Integer[] p = (Integer[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotNull(p[i-1])){
					stmt.setInt(i, p[i-1].intValue());
					LogUtil.printLog("parms" + i + "<Int>:"+p[i-1].intValue(), Level.INFO);
				}else{
					stmt.setNull(i, Types.INTEGER);
					LogUtil.printLog("parms" + i + "<Int>:"+Types.INTEGER, Level.INFO);
				}
			}
		}else if(parms instanceof Long[]){
			Long[] p = (Long[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotNull(p[i-1])){
					stmt.setLong(i, p[i-1].longValue());
					LogUtil.printLog("parms" + i + "<Long>:"+p[i-1].longValue(), Level.INFO);
				}else{
					stmt.setNull(i, Types.NUMERIC);
					LogUtil.printLog("parms" + i + "<Long>:"+Types.NUMERIC, Level.INFO);
				}
			}
		}else if(parms instanceof Object[]){
			Object[] p = (Object[])parms;
			for(int i = 1; i <= p.length; i++){
				Object o = p[i-1];
				if(o instanceof String){
					if(isNotNull(o) && isNotEmpty(o.toString())){
						stmt.setString(i, o.toString());
						LogUtil.printLog("parms" + i + "<String>:"+o.toString(), Level.INFO);
					}else{
						stmt.setNull(i, Types.VARCHAR);
						LogUtil.printLog("parms" + i + "<String>:"+Types.VARCHAR, Level.INFO);
					}
				}else if(o instanceof Integer){
					if(isNotNull(o)){
						stmt.setInt(i, ((Integer)o).intValue());
						LogUtil.printLog("parms" + i + "<Int>:"+((Integer)o).intValue(), Level.INFO);
					}else{
						stmt.setNull(i, Types.INTEGER);
						LogUtil.printLog("parms" + i + "<Int>:"+Types.INTEGER, Level.INFO);
					}
				}else if(o instanceof Long){
					if(isNotNull(o)){
						stmt.setLong(i, ((Long)o).longValue());
						LogUtil.printLog("parms" + i + "<Long>:"+((Long)o).longValue(), Level.INFO);
					}else{
						stmt.setNull(i, Types.NUMERIC);
						LogUtil.printLog("parms" + i + "<Long>:"+Types.NUMERIC, Level.INFO);
					}
				}else if(o instanceof Date){
					if(isNotNull(o)){
						Timestamp date = new Timestamp(((Date)o).getTime());
						stmt.setTimestamp(i, date);
						LogUtil.printLog("parms" + i + "<Date>:"+((Long)o).longValue(), Level.INFO);
					}else{
						stmt.setNull(i, Types.DATE);
						LogUtil.printLog("parms" + i + "<Date>:"+Types.DATE, Level.INFO);
					}
				}else if(o instanceof byte[]){
					if(isNotNull(o)){
						byte[] bytes = (byte[])o;
						stmt.setBinaryStream(i, new ByteArrayInputStream(bytes),bytes.length);
						LogUtil.printLog("parms" + i + "<Bolb>:"+bytes.toString(), Level.INFO);
					}else{
						stmt.setNull(i, Types.BINARY);
						LogUtil.printLog("parms" + i + "<Bolb>:"+Types.BINARY, Level.INFO);
					}
				}else{
					stmt.setString(i, "");
				}
			}
		}
	}
	
	public static String getBindParams(Object parms){
		StringBuffer paramStr = new StringBuffer();
		
		if(parms == null || "".equals(parms))
			return paramStr.toString();
		
		if(parms instanceof String){
			paramStr.append("parms<String>:"+parms.toString()).append("\n");
		}else if(parms instanceof Integer){
			paramStr.append("parms<Int>:"+((Integer)parms).intValue()).append("\n");
		}else if(parms instanceof Long){
			paramStr.append("parms<Long>:"+((Long)parms).longValue()).append("\n");
		}else if(parms instanceof String[]){
			String[] p = (String[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotEmpty(p[i-1])){
					paramStr.append("parms" + i + "<String>:"+p[i-1]).append("\n");
				}else{
					paramStr.append("parms" + i + "<String>:"+Types.VARCHAR).append("\n");
				}
			}
		}else if(parms instanceof Integer[]){
			Integer[] p = (Integer[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotNull(p[i-1])){
					paramStr.append("parms" + i + "<Int>:"+p[i-1]).append("\n");
				}else{
					paramStr.append("parms" + i + "<Int>:"+Types.INTEGER).append("\n");
				}
			}
		}else if(parms instanceof Long[]){
			Long[] p = (Long[])parms;
			for(int i = 1; i <= p.length; i++){
				if(isNotNull(p[i-1])){
					paramStr.append("parms" + i + "<Long>:"+p[i-1].longValue()).append("\n");
				}else{
					paramStr.append("parms" + i + "<Long>:"+Types.NUMERIC).append("\n");
				}
			}
		}else if(parms instanceof Object[]){
			Object[] p = (Object[])parms;
			for(int i = 1; i <= p.length; i++){
				Object o = p[i-1];
				if(o instanceof String){
					if(isNotNull(o) && isNotEmpty(o.toString())){
						paramStr.append("parms" + i + "<String>:"+o.toString()).append("\n");
					}else{
						paramStr.append("parms" + i + "<String>:"+Types.VARCHAR).append("\n");
					}
				}else if(o instanceof Integer){
					if(isNotNull(o)){
						paramStr.append("parms" + i + "<Int>:"+((Integer)o).intValue()).append("\n");
					}else{
						paramStr.append("parms" + i + "<Int>:"+Types.INTEGER).append("\n");
					}
				}else if(o instanceof Long){
					if(isNotNull(o)){
						paramStr.append("parms" + i + "<Long>:"+((Long)o).longValue()).append("\n");
					}else{
						paramStr.append("parms" + i + "<Long>:"+Types.NUMERIC).append("\n");
					}
				}else if(o instanceof Date){
					if(isNotNull(o)){
						Timestamp date = new Timestamp(((Date)o).getTime());
						paramStr.append("parms" + i + "<Date>:"+((Long)o).longValue()).append("\n");
					}else{
						paramStr.append("parms" + i + "<Date>:"+Types.DATE).append("\n");
					}
				}else if(o instanceof byte[]){
					if(isNotNull(o)){
						byte[] bytes = (byte[])o;
						paramStr.append("parms" + i + "<Bolb>:"+bytes.toString()).append("\n");
					}else{
						paramStr.append("parms" + i + "<Bolb>:"+Types.BINARY).append("\n");
					}
				}
			}
		}
		return paramStr.toString();
	}
	
	public static Object paramDouble(Object parms){
		if(parms instanceof String){
			String t = (String)parms;
			return new String[]{t,t};
		}else if(parms instanceof Integer){
			Integer t = (Integer)parms;
			return new Integer[]{t,t};
		}else if(parms instanceof Long){
			Long t = (Long)parms;
			return new Long[]{t,t};
		}else if(parms instanceof String[]){
			String[] t = (String[])parms;
			String[] nparms = new String[t.length * 2];
			System.arraycopy(parms, 0, nparms, 0, t.length);
			System.arraycopy(parms, 0, nparms, t.length, t.length);
			return nparms;
		}else if(parms instanceof Integer[]){
			Integer[] t = (Integer[])parms;
			Integer[] nparms = new Integer[t.length * 2];
			System.arraycopy(parms, 0, nparms, 0, t.length);
			System.arraycopy(parms, 0, nparms, t.length, t.length);
			return nparms;
		}else if(parms instanceof Long[]){
			Long[] t = (Long[])parms;
			Long[] nparms = new Long[t.length * 2];
			System.arraycopy(parms, 0, nparms, 0, t.length);
			System.arraycopy(parms, 0, nparms, t.length, t.length);
			return nparms;
		}else if(parms instanceof Object[]){
			Object[] t = (Object[])parms;
			Object[] nparms = new Object[t.length * 2];
			System.arraycopy(parms, 0, nparms, 0, t.length);
			System.arraycopy(parms, 0, nparms, t.length, t.length);
			return nparms;
		}
		return null;
	}

	/**
	 * 获取配置文件值
	 * @param FileName 配置文件名称，不包含后缀
	 * @param Name 配置文件中的key
	 * @return String 配置文件中的value
	 * **/
	public static String GetSettingProperties(String FileName, String Name) {
		if(SysUtility.isEmpty(FileName) || SysUtility.isEmpty(Name)){
			return "";
		}
		FileName = FileName.toLowerCase();//转为小写配置文件名称

		Properties pro = new Properties();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Object obj = cl.getResource(FileName);
		if(SysUtility.isEmpty(obj)){
			obj = cl.getResource(FileName);
		}
		if(SysUtility.isEmpty(obj)){
			return "";
		}
		InputStream stream = cl.getResourceAsStream(FileName);
		if(isEmpty(stream)){
			stream = cl.getResourceAsStream(FileName);
		}
		if(isEmpty(stream)){
			return "";
		}
		String rt = "";
		try {
			pro.load(stream);
			rt = pro.getProperty(Name);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(stream);
		}
		return rt;
	}

	/**
	 * 获取配置文件值
	 * @param Section 配置文件名称，不包含后缀
	 * @param Name 配置文件中的key
	 * @return String 配置文件中的value
	 * **/
	public static String GetSetting(String Section, String Name) {
		if(SysUtility.isEmpty(Section) || SysUtility.isEmpty(Name)){
			return "";
		}
		Section = Section.toLowerCase();//转为小写配置文件名称
		
		Properties pro = new Properties();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Object obj = cl.getResource(Section + ".properties");
		if(SysUtility.isEmpty(obj)){
			obj = cl.getResource(Section.toLowerCase() + ".properties");
		}
		if(SysUtility.isEmpty(obj)){
			return "";
		}
		InputStream stream = cl.getResourceAsStream(Section + ".properties");
		if(isEmpty(stream)){
			stream = cl.getResourceAsStream(Section.toLowerCase() + ".properties");
		}
		if(isEmpty(stream)){
			return "";
		}
		String rt = "";
		try {
			pro.load(stream);
			rt = pro.getProperty(Name);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(stream);
		}
		return rt;
	}
	
	public static Properties GetProperties(String fileName) {
		Properties prop = propertiesMap.get(fileName);
		if(prop == null){
			prop = new Properties();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream stream = cl.getResourceAsStream(fileName);
			if(SysUtility.isNotEmpty(stream)){
				try {
					prop.load(stream);
				} catch (IOException e) {
					LogUtil.printLog("配置文件"+fileName+"加载出错:"+e.getMessage(), Level.ERROR);
					return new Properties();
				}finally{
					closeInputStream(stream);
				}
				propertiesMap.put(fileName, prop);
			}
		}
		return prop;
	}
	
	public static String GetProperty(String fileName, String key)throws IOException{
		Properties prop = GetProperties(fileName);
		String value = "";
		if(isNotEmpty(prop)){
			value = prop.getProperty(key);
		}
		return value;
	}
	
	//错误日志
	public static String getStackTrace(Exception e){
		if(isEmpty(e)){
			return "";
		}
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer,true));	  
		return writer.toString()+"\n";
	}
	
	/*public static String getCausedby(Exception e){
		String rt = "";
		String str = getStackTrace(e);
		int begin = str.indexOf("Caused by");
		if(begin >= 0){
			rt = str.substring(begin, begin+600);
		}
		return rt;
	}*/
	
	/**
     * 如果datas中包含表名，则返回datas的表名
     * */
    public static String ParseTableName(JSONObject datas,String TableName){
    	Iterator<?> tableKeys = datas.keys();
		while(tableKeys.hasNext()){
			String key = tableKeys.next().toString();
			if(key.equalsIgnoreCase(TableName)){
				TableName = key;
			}
		}
    	return TableName;
    }
    
    public static String[] getTableColumns(String SQL) throws LegendException {
    	return getTableColumns(SysUtility.getCurrentConnection(), SQL, new String[]{});
    }
    
    public static String[] getTableColumns(String SQL,Object parms) throws LegendException {
    	return getTableColumns(SysUtility.getCurrentConnection(), SQL, parms);
    }
    
    public static String[] getTableColumns(Connection ActiveCN,String SQL) throws LegendException {
    	return getTableColumns(SysUtility.getCurrentConnection(), SQL, new String[]{});
    }
    
    public static String[] getTableColumns(Connection ActiveCN,String SQL,Object parms) throws LegendException {
    	if(SysUtility.isEmpty(SQL)){
    		return new String[]{};
    	}
    	String[] columnNames = new String[]{};
    	PreparedStatement stmt = null;
		try {
			stmt = ActiveCN.prepareStatement(SQL);
			
			if(parms instanceof SqlParamList){
	    		SqlParamList sqlParamList = (SqlParamList)parms;
	    		Iterator it = sqlParamList.listIterator();
	    		List lst = new ArrayList();
	    		while(it.hasNext()){
	    			SqlParameter pater = (SqlParameter)it.next();
	    			lst.add(pater.getParamValue());
	    		}
	    		String[] tempParms = new String[lst.size()];
	    		for (int i = 0; i < lst.size(); i++) {
	    			tempParms[i] = (String)lst.get(i);
				}
	    		SysUtility.bindParams(stmt, tempParms);//绑定参数
	    	}else{
	    		SysUtility.bindParams(stmt, parms);//绑定参数
	    	}
			
			stmt.execute();
			ResultSetMetaData rmd = stmt.getMetaData();
			columnNames = new String[rmd.getColumnCount()];
			for (int j = 1; j <= rmd.getColumnCount(); j++) {
				columnNames[j-1] = rmd.getColumnName(j);
			}
		} catch (SQLException e) {
			LogUtil.printLog("getTableColumns error!"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeStatement(stmt);
		}
		return columnNames;
    }
    
    public static List<String> getTableColumnBlobs(Connection ActiveCN,String TableName) throws LegendException {
    	List<String> lst = new ArrayList<String>();
    	PreparedStatement stmt = null;
		try {
			stmt = ActiveCN.prepareStatement("select * from " + TableName + " where 1 <> 1 ");
			stmt.execute();
			ResultSetMetaData rmd = stmt.getMetaData();
			for (int i = 1; i <= rmd.getColumnCount(); i++) {
				if(Types.BLOB == rmd.getColumnType(i)){
					lst.add(rmd.getColumnName(i));
				}
			}
		} catch (SQLException e) {
			LogUtil.printLog("getTableColumnBlobs error!"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeStatement(stmt);
		}
    	return lst;
    }
    
    public static List<String> getTableColumnNames(Connection ActiveCN,String TableName) {
		List<String> ColumnNames = null;
		try {
			ColumnNames = (List<String>)allTableCoulmnNames.get(ActiveCN.getMetaData().getURL()+TableName);
			if(ColumnNames == null){
				boolean rt = initTableColumns(ActiveCN, TableName);
				if(rt){
					return getTableColumnNames(ActiveCN, TableName);
				}else{
					return null;
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return ColumnNames;
	}
    
    public static HashMap<String,Integer> getTableColumnTypes(Connection ActiveCN,String TableName) {
		HashMap<String,Integer> ColumnTypes = null;
		try {
			ColumnTypes = allTableCoulmnTypes.get(ActiveCN.getMetaData().getURL()+TableName);
			if(ColumnTypes == null){
				boolean rt = initTableColumns(ActiveCN, TableName);
				if(rt){
					return getTableColumnTypes(ActiveCN, TableName);
				}else{
					return null;
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return ColumnTypes;
    }
    
    public static String getTableColumnName(String TableName,String ColumnName) {
    	return getTableColumnName(SysUtility.getCurrentConnection(), TableName, ColumnName);
    }
    
    public static String getTableColumnName(Connection ActiveCN,String TableName,String ColumnName) {
    	List<String> ColumnNames = getTableColumnNames(ActiveCN, TableName);
    	if(SysUtility.isNotEmpty(ColumnNames)){
    		for (int i = 0; i < ColumnNames.size(); i++) {
				String tempStr = ColumnNames.get(i);
    			if(ColumnName.equals(tempStr)){
    				return tempStr;
    			}
			}
    	}
    	return "";
    }
    
    public static boolean initTableColumns(Connection ActiveCN,String TableName) {
    	boolean rt = true;
    	List<String> ColumnNames = new ArrayList<String>();
		HashMap<String,Integer> ColumnTypes = new HashMap<String,Integer>();
    	PreparedStatement stmt = null;
		try {
			stmt = ActiveCN.prepareStatement("select * from " + TableName + " where 1 <> 1 ");
			stmt.execute();
			ResultSetMetaData rmd = stmt.getMetaData();
			for (int i = 1; i <= rmd.getColumnCount(); i++) {
				ColumnNames.add(rmd.getColumnName(i).toUpperCase());
				ColumnTypes.put(rmd.getColumnName(i).toUpperCase(), rmd.getColumnType(i));
			}
			allTableCoulmnNames.put(ActiveCN.getMetaData().getURL()+TableName, ColumnNames);
			allTableCoulmnTypes.put(ActiveCN.getMetaData().getURL()+TableName, ColumnTypes);
		} catch (SQLException e) {
			rt = false;
			LogUtil.printLog("getTableColumnNames error!"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeStatement(stmt);
		}
		return rt;
    }
    
    public static String perfectDateFormat(Date date) {
		if (date == null)
			return null;
		return getDateFormat().format(date);
	}
	
	public static String perfectTimestampFormat(Date date) {
		if (date == null)
			return null;
		return getTimeStampFormat().format(date);
	}
	
	public static DateFormat getTimeStampFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 初始化日期格式
	 * 
	 * @return
	 */
	private static void initPattern() {
		// 1600~9999年
		String year = "((1[6-9]|[2-9]\\d)\\d{2})";
		String month = "((0\\d|\\d)|1[0-2])";
		String day = "([0-3]\\d|\\d)";
		String hour = "(([0-1]\\d)|(2[0-3]|\\d))";
		String minute = "[0-5]\\d";
		String second = "[0-5]\\d";
		String split = "-";
		String splitTime = ":";
		String betweenDayTime = " ";
		// 拼凑表达式
		String[] ymd = { year, month, day };
		String[] mdy = { month, day, year };
		String[] hm = { hour, minute };
		String[] hms = { hour, minute, second };
		// h:m
		String hmExpress = injoin(hm, splitTime);
		// h:m:s
		String hmsExpress = injoin(hms, splitTime);
		// y-m-d
		String ymdExpress = injoin(ymd, split);
		// y-m-d h:m
		String[] ymdhm = { ymdExpress, hmExpress };
		String ymdhmExpress = injoin(ymdhm, betweenDayTime);
		// y-m-d h:m:s
		String[] ymdhms = { ymdExpress, hmsExpress };
		String ymdhmsExpress = injoin(ymdhms, betweenDayTime);
		// m-d-y
		String mdyExpress = injoin(mdy, split);
		// m-d-y h:m
		String[] mdyhm = { mdyExpress, hmExpress };
		String mdyhmExpress = injoin(mdyhm, betweenDayTime);
		// m-d-y h:m:s
		String[] mdyhms = { mdyExpress, hmsExpress };
		String mdyhmsExpress = injoin(mdyhms, betweenDayTime);
		// 需要转换的格式
		// Map dateFormat = new HashMap();
		// 这里定义地局部变量与全局变量重名，注释掉
		dateFormat.put("y-M-d H:m:s", Pattern.compile(ymdhmsExpress));
		dateFormat.put("y-M-d", Pattern.compile(ymdExpress));
		dateFormat.put("y-M-d H:m", Pattern.compile(ymdhmExpress));
		dateFormat.put("M-d-y", Pattern.compile(mdyExpress));
		dateFormat.put("M-d-y H:m", Pattern.compile(mdyhmExpress));
		dateFormat.put("M-d-y H:m:s", Pattern.compile(mdyhmsExpress));
	}
	/**
	 * 把两段表达式拼在一起
	 * 
	 * @param elements
	 * @param split
	 * @return
	 */
	private static String injoin(String[] elements, String split) {
		StringBuffer elementsString = new StringBuffer();
		elementsString.append("(");
		for (int i = 0; i < elements.length; i++) {
			elementsString.append(elements[i]);
			elementsString.append(split);
		}
		String result = elementsString.toString();
		result = result.substring(0, result.length() - 1).intern();
		result = result + ")";
		return result;
	}
	
	public static Date str2Date(String str) {
		if (isEmpty(str)) {
			return null;
		}
		Date result = null;
		for (Iterator it = dateFormat.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String formatName = (String) entry.getKey();
			Pattern pattern = (Pattern) entry.getValue();
			if (pattern.matcher(str).matches()) {
				DateFormat desFormatter = new SimpleDateFormat(formatName);
				try {
					result = desFormatter.parse(str);
					break;
				} catch (ParseException e) {
					
				}
			}
		}
		return result;
	}
	
	public static void closeByteArrayOutputStream(ByteArrayOutputStream bos) {
		if (bos == null) {
			return;
		}
		try {
			bos.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象ByteArrayOutputStream出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	
	public static void closeBufferedReader(BufferedReader br) {
		if (br == null) {
			return;
		}
		try {
			br.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象BufferedReader出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closeLineNumberReader(InputStreamReader isRd) {
		if (isRd == null) {
			return;
		}
		try {
			isRd.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象InputStreamReader出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closeInputStreamReader(LineNumberReader reader) {
		if (reader == null) {
			return;
		}
		try {
			reader.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象LineNumberReader出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closeInputStream(InputStream is) {
		if (is == null) {
			return;
		}
		try {
			is.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象InputStream出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closeOutputStream(OutputStream out) {
		if (out == null) {
			return;
		}
		try {
			out.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象OutputStream出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closePrintWriter(PrintWriter rw) {
		if (rw == null) {
			return;
		}
		try {
			rw.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象PrintWriter出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void closeServletOutputStream(ServletOutputStream sos) {
		if (sos == null) {
			return;
		}
		try {
			sos.close();
		} catch (Exception e) {
			LogUtil.printLog("关闭对象ServletOutputStream出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static Connection check(Connection conn) {
		if (conn != null) {
			return conn;
		} else {
			return getCurrentConnection();
		}
	}
	
	public static void setCurrentUser(JSONObject CurrentUser) {
		//
	}
	
	public static String getCurrentHostIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "";
		}
	}
	
	public static String getCurrentHostIP(){
		try{
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()){
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()){
					InetAddress ip = (InetAddress) addresses.nextElement();
					if (ip != null 
							&& ip instanceof Inet4Address
                    		&& !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                    		&& ip.getHostAddress().indexOf(":")==-1){
						return ip.getHostAddress();
					} 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getCurrentHostAppName() {
		return (String)SessionManager.getAttribute(SessionKeyType.SESSION_APP_NAME);
	}

	public static void setCurrentConnection(Connection conn) {
		SessionManager.setAttribute(SysUtility.ActiveCN, conn);
	}

	public static Connection getCurrentConnection() {
		Connection ActiveCN = (Connection)SessionManager.getAttribute(SysUtility.ActiveCN);
		if(null != ActiveCN){
			return ActiveCN;
		}
		
		if(TomcatPool){
			ActiveCN = CreateTomcatConnection();
		}else if(HibernatePool){
			ActiveCN = CreateHibernateConnection();
		}else if(ProxoolPool){
			ActiveCN = CreateProxoolConnection();
		}
		if(SysUtility.isNotEmpty(ActiveCN)){
			try {
				SysUtility.BeginTrans(ActiveCN);
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
			SessionManager.setAttribute(SysUtility.ActiveCN, ActiveCN);
		}
		return ActiveCN;
	}
	
	public static Connection CreateConnection() {
		Connection conn = null;
		if(TomcatPool){
			conn = CreateTomcatConnection();
		}else if(HibernatePool){
			conn = CreateHibernateConnection();
		}else if(ProxoolPool){
			conn = CreateProxoolConnection();
		}
		return conn;
	}
	
	public static Connection getCurrentConnection(String DBName) {
		Connection ActiveCN = (Connection)SessionManager.getAttribute(DBName);
		if(null != ActiveCN){
			return ActiveCN;
		}
		if(TomcatPool){
			ActiveCN = CreateTomcatConnection(DBName);
		}else if(HibernatePool){
			ActiveCN = CreateHibernateConnection(DBName);
		}else if(ProxoolPool){
			ActiveCN = CreateProxoolConnection(DBName);
		}
		if(SysUtility.isNotEmpty(ActiveCN)){
			try {
				SysUtility.BeginTrans(ActiveCN);
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
			SessionManager.setAttribute(DBName, ActiveCN);
		}
		return ActiveCN;
	}

	public final static String username = "root";
	public final static String password = "16tO0fB20sTZlZev";
	public static Connection getCurrentDruidConnection() throws SQLException {
		return connUtility.getDuridConnection("masterDataSource").getConnection("", "");
	}
	public static Connection getCurrentDruidConnection(String DBName) throws SQLException {
		return getCurrentDruidConnection(DBName, username, password);
	}
	public static Connection getCurrentDruidConnection(String DBName, String username, String password) throws SQLException {
		return connUtility.getDuridConnection(DBName).getConnection(username, password);
	}

	/** 获取Tomcat的数据源
	 * @return Connection
	 * */
	public static Connection CreateTomcatConnection() {
		return CreateTomcatConnection(GetSetting("system", "DBName"));
	}
	/** 获取Tomcat的数据源
	 * @param DBName
	 * @return Connection
	 * */
	public synchronized static Connection CreateTomcatConnection(String DBName) {
		Connection ActiveCN = null;
		try {
			DataSource dataSource = (DataSource)new InitialContext().lookup("java:comp/env/" + DBName);
			ActiveCN = dataSource.getConnection();
		} catch (NamingException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return ActiveCN;
	}
	
	/**获取Proxool代理的数据源
	 * @return Connection
	 * */
	public static Connection CreateProxoolConnection() {
		return CreateProxoolConnection("jdbc-0");
	}
	
	/**获取Proxool代理的数据源
	 * @param DBName
	 * @return Connection
	 * */
	public static Connection CreateProxoolConnection(String DBName) {
		return CreateProxoolConnection(DBName, 0);
	}

	/**获取Proxool代理的数据源
	 * @param DBName
	 * @param tryCount
	 * @return Connection
	 * */
	private static int firstLoad = 0;
	public synchronized static Connection CreateProxoolConnection(String DBName,int tryCount) {
		String fileName = "proxool.properties";
		String profiles = GetSettingProperties("application.yml", "spring.profiles.active");
		if(SysUtility.isNotEmpty(profiles)){
			fileName = "proxool-"+profiles+".properties";
		}
		Connection ActiveCN = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		//嵌入加载Spring durid的数据源获取方式
		if(SysUtility.isEmpty(cl.getResource(fileName))){
			try {
				if("jdbc-0".equals(DBName)){
					return connUtility.getConnection();
				}else{
					return getCurrentDruidConnection(DBName);
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				LogUtil.printLog("durid数据源初始化失败", LogUtil.ERROR);
				return null;
			}
		}

		String proxoolPath = "";
		if(SysUtility.isEmpty(cl.getResource("/"))) {
			try {
				proxoolPath = java.net.URLDecoder.decode(cl.getResource(fileName).getPath(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				LogUtil.printLog("load "+fileName+" error!", Level.ERROR);
			}
		}else {
			proxoolPath = cl.getResource("/").getPath()+ fileName;
		}


//		if(!new File(proxoolPath).exists()){
//			LogUtil.printLog("数据库文件读取失败："+proxoolPath, LogUtil.ERROR);
//			return null;
//		}

		try {
			if(0 == firstLoad){
				firstLoad ++;
				Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
				org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure(proxoolPath);
			}
			InputStream is = cl.getResourceAsStream(fileName);
			Properties keypath = new Properties();
			try {
				keypath.load(is);
			} catch (Exception e) {
				LogUtil.printLog("load "+fileName+" error:"+e.getMessage(), Level.ERROR);
			} finally{
				closeInputStream(is);
			}
			String alias = keypath.getProperty(DBName+".proxool.alias");
			String driver = keypath.getProperty(DBName+".proxool.driver-class");
			String url = keypath.getProperty(DBName+".proxool.driver-url");
			String user = keypath.getProperty(DBName+".user");
			String password = keypath.getProperty(DBName+".password");

			ActiveCN = DriverManager.getConnection("proxool."+alias+":"+driver+":"+url,user,password);
			ActiveCN.setAutoCommit(false);
			tryCount = 0;
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
//			if(tryCount > 1 && tryCount%1 ==0){
//				LogUtil.printLog("Try "+tryCount+" Time Error:"+getStackTrace(e), Level.INFO);
//				return ActiveCN;
//			}
//			tryCount++;
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e1) {
//				LogUtil.printLog(e1.getMessage(), Level.ERROR);
//			}
//			return CreateProxoolConnection(DBName,tryCount);
		}
		return ActiveCN;
	}
	
	static HashMap<String,String> dynamic = new HashMap<String,String>();
	
	public static Connection getCurrentDynamicConnection(String uuid) {
		return (Connection)SessionManager.getAttribute(uuid);
	}
	
	public static Connection getCurrentDynamicConnection(String uuid, String dbtype, String dbdriverurl, String dbuser, String dbpassword) {
		Connection ActiveCN = (Connection)SessionManager.getAttribute(uuid);
		if(isNotEmpty(ActiveCN)){
			return ActiveCN;
		}
		
		ActiveCN = CreateDynamicProxoolConnection(uuid, dbtype, dbdriverurl, dbuser, dbpassword);
		SessionManager.setAttribute(uuid, ActiveCN);
		return ActiveCN;
	}
	
	public synchronized static Connection CreateDynamicProxoolConnection(String uuid, String dbtype, String dbdriverurl, String dbuser, String dbpassword) {
		String filePath = System.getProperty("java.io.tmpdir")+"proxool";
		String fileName = uuid+".properties";

		StringBuffer txt = new StringBuffer();
		if(Constants.Oracle.equalsIgnoreCase(dbtype)) {
			txt.append(uuid+".proxool.alias="+uuid+"\n");
			txt.append(uuid+".proxool.driver-class=oracle.jdbc.driver.OracleDriver"+"\n");
			txt.append(uuid+".proxool.house-keeping-test-sql=select sysdate from dual"+"\n");
			txt.append(uuid+".proxool.minimum-connection-count=2"+"\n");
			txt.append(uuid+".proxool.maximum-connection-count=10"+"\n");
			txt.append(uuid+".proxool.simultaneous-build-throttle=10"+"\n");
			txt.append(uuid+".proxool.maximum-active-time=1800000"+"\n");
			txt.append(uuid+".proxool.driver-url="+dbdriverurl+"\n");
			txt.append(uuid+".user="+dbuser+"\n");
			txt.append(uuid+".password="+dbpassword+"\n");
		}else if(Constants.Mysql.equalsIgnoreCase(dbtype)) {
			txt.append(uuid+".proxool.alias="+uuid+"\n");
			txt.append(uuid+".proxool.driver-class=com.mysql.jdbc.Driver"+"\n");
			txt.append(uuid+".proxool.prototype-count=4"+"\n");
			txt.append(uuid+".proxool.verbose=true"+"\n");
			txt.append(uuid+".proxool.statistics=10s,1m,1d"+"\n");
			txt.append(uuid+".proxool.statistics-log-level=error"+"\n");
			txt.append(uuid+".proxool.minimum-connection-count=2"+"\n");
			txt.append(uuid+".proxool.maximum-connection-count=10"+"\n");
			txt.append(uuid+".proxool.maximum-active-time=1800000"+"\n");
			txt.append(uuid+".proxool.driver-url="+dbdriverurl+"\n");
			txt.append(uuid+".user="+dbuser+"\n");
			txt.append(uuid+".password="+dbpassword+"\n");
		}else if(Constants.Sqlserver.equalsIgnoreCase(dbtype)) {
			
		}
		FileUtility.createFile(filePath, fileName, txt.toString());
		
		String url = "";
		String user = "";
		String password = "";
		Connection ActiveCN = null;
		try {
		    String firstLoad = dynamic.get(uuid);
			if(!"Y".equals(firstLoad)){
				Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
				org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure(java.net.URLDecoder.decode(filePath + File.separator + fileName,"UTF-8"));
				dynamic.put(uuid, "Y");
			}
			File file = new File(filePath + File.separator + fileName);
			InputStream is = new FileInputStream(file);
			Properties keypath = new Properties();
			try {
				keypath.load(is);
			} catch (Exception e) {
				LogUtil.printLog("load proxool.properties error:"+e.getMessage(), Level.ERROR);
			} finally{
				closeInputStream(is);
			}
			String alias = keypath.getProperty(uuid+".proxool.alias");
			String driver = keypath.getProperty(uuid+".proxool.driver-class");
			url = keypath.getProperty(uuid+".proxool.driver-url");
			user = keypath.getProperty(uuid+".user");
			password = keypath.getProperty(uuid+".password");
			ActiveCN = DriverManager.getConnection("proxool."+alias+":"+driver+":"+url,user,password);
			ActiveCN.setAutoCommit(false);
		} catch (Exception e) {
			dynamic.put(uuid, "N");
			LogUtil.printLog("数据库连接初始化失败："+uuid+"|"+dbtype+"|"+dbdriverurl+"|"+dbuser+"|"+dbpassword, Level.ERROR);
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		if(SysUtility.isEmpty(ActiveCN)) {
			LogUtil.printLog("数据库连接初始化失败："+uuid+"|"+dbtype+"|"+dbdriverurl+"|"+dbuser+"|"+dbpassword, Level.ERROR);
		}
		
	    return ActiveCN;  
	}
	
	/**
	 * 获取Spring代理的数据源
	 * @return Connection
	 * */
	public static Connection CreateHibernateConnection() {
		return CreateHibernateConnection("");
	}
	
	/**
	 * 获取Spring代理的数据源
	 * @param DBName
	 * @return Connection
	 * */
	public synchronized static Connection CreateHibernateConnection(String DBName) {
		Connection ActiveCN = null;
		try {
			Class cController = Class.forName("com.sun.light.util.ConnectionUtils");
			Object oController = cController.newInstance();
			Method method = cController.getMethod("CreateConnection", new Class[] {String.class});
			ActiveCN = (Connection)method.invoke(oController, new Object[] {DBName});
		} catch (SecurityException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (IllegalArgumentException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (ClassNotFoundException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (InstantiationException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (IllegalAccessException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (NoSuchMethodException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		} catch (InvocationTargetException e) {
			LogUtil.printLog("CreateHibernateConnection error:"+e.getMessage(), Level.ERROR);
		}
		return ActiveCN;
	}
	
	public static boolean BeginTrans() throws LegendException{
		return BeginTrans(getCurrentConnection());
	}
	
	public static boolean BeginTrans(Connection conn) throws LegendException{
		if(conn != null){
			try {
				conn.setAutoCommit(false);
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}
	
	public static boolean BeginTrans(IDataAccess DataAccess) throws LegendException{
		if(SysUtility.isNotEmpty(DataAccess)){
			DataAccess.BeginTrans();
			return true;
		}
		return false;
	}
	
	public static void SetAutoCommit(boolean autoCommit) throws LegendException{
		SetAutoCommit(getCurrentConnection(), autoCommit);
	}
	
	public static void SetAutoCommit(Connection conn,boolean autoCommit) throws LegendException{
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
	}
	
	public static boolean ComitTrans() throws LegendException{
		return ComitTrans(getCurrentConnection());
	}

	public static boolean ComitTrans(Connection conn) throws LegendException{
		if(conn != null){
			try {
				conn.setAutoCommit(false);
				conn.commit();
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}
	
	public static boolean ComitTrans(IDataAccess DataAccess) throws LegendException{
		if(SysUtility.isNotEmpty(DataAccess)){
			DataAccess.ComitTrans();
			return true;
		}
		return false;
	}
	
	public static boolean RoolbackTrans() throws LegendException{
		Connection conn = getCurrentConnection();
		if(conn != null){
			try {
				conn.rollback();
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}
	
	public static boolean RoolbackTrans(Connection conn) throws LegendException{
		if(conn != null){
			try {
				conn.rollback();
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}
	
	public static boolean RoolbackTrans(IDataAccess DataAccess) throws LegendException{
		if(SysUtility.isNotEmpty(DataAccess)){
			DataAccess.RoolbackTrans();
			return true;
		}
		return false;
	}
	//参数过滤
	public static void paramConvert(HashMap param,JSONObject body){
		try {
			if(SysUtility.isNotEmpty(body)){
	  			Iterator it = body.keys();
	  			while(it.hasNext()){
	  				Object key2 = it.next();
	  				if(key2 instanceof String){
	  					if(SysUtility.isNotEmpty(key2) && SysUtility.isNotEmpty(body.get(key2.toString()))){
	  						param.put(key2, body.get(key2.toString()));
	  					}
	  				}
	  			}
			}
		} catch (JSONException e) {
  			LogUtil.printLog("参数转换错误"+e.getMessage()+e.getMessage(), Level.ERROR);
  		}
  	}
	
	public static void setCurrentOperator(Operator operator) {
		SessionManager.setAttribute(SysUtility.LoginUser, operator);
	}
	
	public static Operator getCurrentOperator() {
		Operator operator = (Operator)SessionManager.getAttribute(SysUtility.LoginUser);
		if(isEmpty(operator)){
			operator = (Operator)SessionManager.getAttribute(SessionKeyType.SESSION_OPERATOR);
		}
		if(isEmpty(operator)){
			operator = new Operator();
		}
		return operator;
	}
	
	public static Operator getCurrentUser() {
		return (Operator)SessionManager.getAttribute(LoginUser);
	}
	
	public static String getCurrentName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		String str = ((Operator)SessionManager.getAttribute(LoginUser)).getName();
		if(isEmpty(str)){
			str = ((Operator)SessionManager.getAttribute(LoginUser)).getUserName();
		}
		return str;
	}
	
	public static String getCurrentUserMobile() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getUserMobile();
	}
	
	public static String getCurrentUserIsRoot() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getIsRoot();
	}
	
	public static String getCurrentPartId() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getPartId();
	}
	
	public static String getCurrentOrgId() {
		return getCurrentPartId();
	}
	public static String getCurrentOrgName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getOrgName();
	}
	
	public static String getCurrentUserType() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getUserType();
	}
	
	public static String getCurrentDeptId() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getDeptId();
	}
	
	public static String getCurrentDeptName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getDeptName();
	}
	
	public static String getCurrentRoleId() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getRoleId();
	}
	
	public static String getCurrentRoleName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getRoleName();
	}
	
	public static String getCurrentRoleType() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getRoleType();
	}
	
	public static String getCurrentRoleLevel() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getRoleLevel();
	}
	
	public static String getCurrentRolePLevel() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getRolePLevel();
	}
	
	public static String getCurrentUserIndx() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return String.valueOf(((Operator)SessionManager.getAttribute(LoginUser)).getIndx());
	}
	
	public static String getCurrentUserName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getUserName();
	}

	public static String getCurrentUserIcNo() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getIcNo();
	}

	public static String getCurrentUserSenderId() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getSenderId();
	}

	public static String getCurrentUserReceiverId() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getReceiverId();
	}

	public static String getCurrentSign() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getSign();
	}
	
	public static String getCurrentEntRegNo() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getEntRegNo();
	}
	
	public static String getCurrentEntCode() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getEntCode();
	}
	
	public static String getCurrentEntName() {
		if(SysUtility.isEmpty(getCurrentUser()))
			return "";
		return ((Operator)SessionManager.getAttribute(LoginUser)).getEntName();
	}
	
	public static void setCurrentUserDataValue(String FieldName,String FieldValue) {
		Operator operator = (Operator)SessionManager.getAttribute(LoginUser);
		if(SysUtility.isEmpty(operator)){
			operator = new Operator();
		}
		JSONObject userData = operator.getUserData();
		if(SysUtility.isEmpty(userData)){
			userData = new JSONObject();
			operator.setUserData(userData);
		}
		putJsonField(userData, FieldName, FieldValue);
	}
	
	public static String getCurrentUserDataValue(String FieldName) {
		String rt = "";
		if(SysUtility.isEmpty(FieldName)){
			return rt;
		}
		Operator operator = (Operator)SessionManager.getAttribute(LoginUser);
		if(SysUtility.isEmpty(operator)){
			return rt;
		}
		JSONObject data = operator.getUserData();
		rt = getJsonField(data, FieldName);
		return rt;
	}
	
	public static String getCurrentCssTemplet() {
		String columnName = getTableColumnName(SysUtility.getCurrentConnection(), "s_auth_user","TEMPLET_CSS");
		if(SysUtility.isEmpty(getCssTemplet()) && SysUtility.isNotEmpty(columnName)){
			setCssTemplet("templetCss/TempletA");//登录页只能使用默认样式。
		}
		return getCssTemplet();
	}
	
	public static String getCurrentCronJobName() {
		return (String)SessionManager.getAttribute(SessionKeyType.SessionCronJobName);
	}
	
	public static String getCurrentCronClassName() {
		return (String)SessionManager.getAttribute(SessionKeyType.SessionCronClassName);
	}
	
	public static String getCurrentCronMethodName() {
		return (String)SessionManager.getAttribute(SessionKeyType.SessionCronMethodName);
	}
	
	public static String getCurrentCronIpAddress() {
		return (String)SessionManager.getAttribute(SessionKeyType.SessionCronIpAddress);
	}
	
	public static JSONObject GetTable(JSONObject dataJSON,String RootName) throws LegendException{
		return GetTable(dataJSON,RootName,0);
	}
	
	public static JSONObject GetTable(JSONObject dataJSON,String RootName, int RowIndex) throws LegendException{
		JSONObject root = new JSONObject();
		try {
			if(dataJSON.has(RootName)){
				root = (JSONObject)((JSONArray)dataJSON.get(RootName)).get(RowIndex);//(JSONObject)datas.get(RootName);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTable Error:"+e.getMessage(), Level.ERROR);
		}
		return root;
	}
	
	public static JSONArray GetTables(JSONObject dataJSON,String RootName) throws LegendException{
		JSONArray rows = new JSONArray();
		try {
			if(dataJSON.has(RootName)){
				rows = (JSONArray)dataJSON.get(RootName);//(JSONObject)datas.get(RootName);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTables Error:"+e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	
	/**返回内存表的行数
	 * @param TableName 查找的内存表名
	 * @return 数据行数
	 * */
	public static int GetTableRows(JSONObject dataJSON,String TableName) throws LegendException{
		int rt = 0;
		if(dataJSON == null || SysUtility.isEmpty(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(dataJSON, TableName);
			if(dataJSON.has(TableName) && dataJSON.get(TableName)!=null){
				JSONArray rows = dataJSON.getJSONArray(TableName);
				if(rows != null){
					rt = rows.length();
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}		
		return rt;
	}
	
	/**获取dataJSON的字段值
	 * @param TableField 表.字段代替分开的表字段参数
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public static String GetTableValue(JSONObject dataJSON,String TableField) throws LegendException{
    	String[] TF = TableField.split("\\.");
		if (TF.length > 1){
			return GetTableValue(dataJSON,TF[0], TF[1], 0);
		}
        return "";
    }
	
    /**获取dataJSON的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public static String GetTableValue(JSONObject dataJSON,String TableName, String FieldName) throws LegendException{
        return GetTableValue(dataJSON,TableName, FieldName, 0);
    }
    
    /**获取dataJSON的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param RowIndex 数据所在行
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public static String GetTableValue(JSONObject dataJSON,String TableName, String FieldName,int RowIndex) throws LegendException{
		String rt = "";
        try {
        	TableName = SysUtility.ParseTableName(dataJSON, TableName);
        	if(dataJSON.get(TableName)==null || dataJSON.get(TableName) == null){
    			return "";
    		}
        	JSONArray rows = (JSONArray)dataJSON.get(TableName);
        	if(rows.length() == 0){
        		return "";
        	}
    		JSONObject row = rows.getJSONObject(RowIndex);
    		Iterator<?> keys = row.keys();
    		while(keys.hasNext()){
				String key = keys.next().toString();
				if(key.equalsIgnoreCase(FieldName)){
					FieldName = key;
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		rt = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(obj);
	            	}else{
	            		rt = obj.toString();
	            	}
	            	break;
				}
    		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	/**获取dataJSON的字段值，返回符合条件的第一条数据
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param Filter 过滤条件，取得在条件内的数据，配置(列名=值)
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public static String GetTableValue(JSONObject dataJSON,String TableName, String FieldName, String Filter) throws LegendException{
		String rt = "";
        try {
        	TableName = SysUtility.ParseTableName(dataJSON, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(dataJSON.get(TableName)==null || dataJSON.get(TableName) == null || ft.length < 2){
    			return "";
    		}
        	JSONArray rows = (JSONArray)dataJSON.get(TableName);
        	for(int i = 0 ; i < rows.length();i++){
				JSONObject row = rows.getJSONObject(i);
				Iterator<?> keys = row.keys();
				String value = "";
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(filterKey)){
						filterKey = key;
						Object objValue = row.get(filterKey);
						if(objValue instanceof Date){
							value = SysUtility.DataFormatStr((Date)objValue);
						}else{
							value = objValue.toString();
						} 
					}
					if(key.equalsIgnoreCase(FieldName)){
						FieldName = key;
					}
	    		}
	    		if(value.equals(filterValue)){
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(obj);
	            	}
	            	return obj.toString();
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	/**保存数据到数据池中数据表的第0行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @return void
	 * */
	public static void SetTableValue(JSONObject dataJSON,String TableName,String FieldName , String setValue) throws LegendException{
		SetTableValue(dataJSON,TableName, FieldName, setValue, 0);
	}
	
	/**保存数据到数据池中数据表的指定行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @param RowIndex 指定行
	 * @return void
	 * */
    public static void SetTableValue(JSONObject dataJSON,String TableName, String FieldName, Object setValue, int RowIndex) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(dataJSON, TableName);
			if(dataJSON.get(TableName)!=null){
				JSONArray rows = dataJSON.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject row = rows.getJSONObject(RowIndex);
						row.put(FieldName, setValue);
						Iterator<?> keys = row.keys();
			    		while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName) && !key.equals(FieldName)){
								row.remove(key);
								return;
							}
			    		}
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
    /**保存数据到数据池中数据表按条件过滤后的所有行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @param Filter 过滤条件
	 * @return void
	 * */
    public static void SetTableValue(JSONObject dataJSON,String TableName, String FieldName, String setValue, String Filter) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(dataJSON, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(dataJSON.get(TableName)==null || dataJSON.get(TableName) == null || ft.length < 2){
    			return;
    		}
        	JSONArray rows = (JSONArray)dataJSON.get(TableName);
        	for(int i = 0 ; i < rows.length();i++){
				JSONObject row = rows.getJSONObject(i);
				Iterator<?> keys = row.keys();
				String value = "";
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(filterKey)){
						filterKey = key;
						Object objValue = row.get(filterKey);
						if(objValue instanceof Date){
							value = SysUtility.DataFormatStr((Date)objValue);
						}else{
							value = objValue.toString();
						} 
					}
					if(key.equalsIgnoreCase(FieldName)){
						FieldName = key;
					}
	    		}
	    		if(value.equals(filterValue)){
					row.put(FieldName, setValue);
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
	
    /**移除数据池中数据表的指定行
	 * @param TableName 移除据的数据表
	 * @param FieldName 移除数据表的字段
	 * @return void
	 * */
    public static void RemoveTableValue(JSONObject dataJSON,String TableName, String FieldName) throws LegendException{
    	RemoveTableValue(dataJSON,TableName, FieldName, 0);
    }
    
    /**移除数据池中数据表的指定行
	 * @param TableName 移除据的数据表
	 * @param FieldName 移除数据表的字段
	 * @param RowIndex 指定行
	 * @return void
	 * */
    public static void RemoveTableValue(JSONObject dataJSON,String TableName, String FieldName, int RowIndex) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(dataJSON, TableName);
			if(dataJSON.get(TableName)!=null){
				JSONArray rows = dataJSON.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject row = rows.getJSONObject(RowIndex);
						Iterator<?> keys = row.keys();
			    		while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName)){
								FieldName = key;
								row.remove(FieldName);
								return;//移除成功后直接返回，跳出循环
							}
			    		}
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
	public static Object[] ListToArray(List list) {
		if(isEmpty(list)){
			return new Object[]{};
		}
		Object[] params = new Object[list.size()];
    	for (int j = 0; j < params.length; j++) {
    		Object obj = list.get(j);
    		if(obj == null){
    			params[j] = "";
    		}else{
    			params[j] = obj.toString();
    		}
    		
		}
    	return params;
	}
	
	public static String[] ListToStrs(List list,String coulmnName) {
		if(SysUtility.isEmpty(list)){
			return new String[]{};
		}
		String[] rts = new String[list.size()];
		
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if(obj == null){
				
			}else if(obj instanceof Map){
				Map map = (Map)obj;
				rts[i] = ""+(isEmpty(map.get(coulmnName.toUpperCase()))?"":map.get(coulmnName.toUpperCase()));
			}else if(obj instanceof String){
				rts[i] = ""+(isEmpty(obj)?"":obj);
			}
		}
		return rts;
	}
	
	public static String convertObjectToString(Object object) {
		if (object instanceof Calendar) { // Calendar
			return SysUtility.dateFormat(object, null);
		} else if (object instanceof Date) { // date
			return SysUtility.dateFormat(object, null);
		} else { // other type
			return object.toString();
		}
	}
	
	public static String dateFormat(Object dateObj, String pattern) {
		DateFormat formatter = new SimpleDateFormat((isEmpty(pattern)) ? "yyyy-MM-dd HH:mm:ss" : pattern,Locale.CHINA);
		if (dateObj instanceof Calendar) { // Calendar
			Date date = ((Calendar) dateObj).getTime();
			return formatter.format(date);
		} else if (dateObj instanceof Date) { // date
			return formatter.format((Date) dateObj);
		} else {
			return null;
		}
	}
	
	public static boolean isEmptyCollection(List list)throws LegendException{
		if(null == list || list.size() <= 0) return true;
		return false;
	}
	
	public static boolean isNotEmptyCollection(List list)throws LegendException{
		if(null == list || list.size() <= 0) return false;
		return true;
	}
	public static boolean isNumber(String str){       
	    Pattern pattern = Pattern.compile("[0-9]*");       
	    return pattern.matcher(str).matches();          
	}  
	public static boolean isNumeric(String str){       
	    Pattern pattern = Pattern.compile("[0-9.]*");       
	    return pattern.matcher(str).matches();          
	}   
	
	public static boolean isStreric(String str){       
	    Pattern pattern = Pattern.compile("[a-zA-Z]*");       
	    return pattern.matcher(str).matches();          
	}
	
	public static boolean isStrNumericNp(String str){       
	    Pattern pattern = Pattern.compile("[a-zA-Z0-9-_:]*");       
	    return pattern.matcher(str).matches();          
	}
	
	public static boolean isNumericNp(String str){       
	    Pattern pattern = Pattern.compile("[0-9]*");       
	    return pattern.matcher(str).matches();          
	 }   
	
	public static String fillZero(String str,int length){
		if(isEmpty(str)) return "";
		if(!isNumeric(str)) return str;
		
		String endPointStr = "";
		if(str.indexOf(".") >= 0)
		   endPointStr = str.substring(str.indexOf(".") + 1, str.length());
		else
			str = str + ".";
		
		for(int i = 0 ; i < length - endPointStr.length() ; i++){
			str = str + "0";
		}
		
		return str;
	}
	
	/**
	 * 默认 ' , '(逗号)拆分,默认每１０００个拆分一次
	 * 
	 * @param inSql
	 *            进行拆分的字符串 如："aa,bb,cc,dd"
	 * @return String[]
	 * @throws LegendException
	 */
	public static String[] strSplitToStrArray(String inSql) {
		int itemNum = 1000;
		return strSplitToStrArray(inSql, itemNum);
	}
	
	/**
	 * 默认按 , (逗号)拆分
	 * 
	 * @param inSql
	 *            进行拆分的字符串 如："1,2,3……,1002"
	 * @param itemNum
	 *            每itemNum个元素拆分一次 如：1000
	 * @return String[] 返回值：{ "1,2,3……,1000" , "1001,1002" }
	 * @throws LegendException
	 */
	public static String[] strSplitToStrArray(String inSql, int itemNum) {
		if (SysUtility.isEmpty(inSql))
			return new String[] {};
		if (inSql.startsWith(",")) {
			inSql = inSql.substring(1);
		}
		if (inSql.endsWith(",")) {
			inSql = inSql.substring(0, inSql.length() - 1);
		}
		String[] bl_ids = null;
		String[] exp_bl_ids = inSql.split(",");
		int size = 0;
		if (exp_bl_ids.length > itemNum) {
			size = exp_bl_ids.length / itemNum;
			if (exp_bl_ids.length % itemNum > 0)
				size += 1;
			bl_ids = new String[size];
			for (int i = 0; i < bl_ids.length; i++) {
				bl_ids[i] = new String();
			}
			for (int i = 0; i < exp_bl_ids.length; i++) {
				bl_ids[i / itemNum] += (exp_bl_ids[i] + ",");
			}

			for (int i = 0; i < bl_ids.length; i++) {
				bl_ids[i] = bl_ids[i].substring(0, bl_ids[i].length() - 1);
			}
			return bl_ids;
		} else {
			if (inSql.startsWith(",")) {
				return new String[] { inSql.substring(1) };
			}
			if (inSql.endsWith(",")) {
				return new String[] { inSql.substring(0, inSql.length() - 1) };
			}
			return new String[] { inSql };
		}
	}
	
	public static String[] JSONArraySplitToStrArray(JSONArray rows, String fieldName) throws JSONException {
		if(SysUtility.isEmpty(rows)){
			return new String[]{};
		}
		
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < rows.length(); i++) {
			JSONObject jobj = (JSONObject)rows.get(i);
			String INDX = SysUtility.getJsonField(jobj, fieldName);
			if(SysUtility.isNotEmpty(INDX)){
				ids.append(",").append(INDX);
			}
		}
		if(SysUtility.isNotEmpty(ids)){
			return ids.substring(1).split(",");
		}else{
			return new String[]{};
		}
	}
	
	public static void removeMapField(HashMap obj, String FieldName) {
		if(isEmpty(obj)){
			return;
		}
		if(obj.containsKey(FieldName)){
			obj.remove(FieldName);
		}
	}
	
	//移除JSONObject中key为FieldName的值
	public static void removeJsonField(JSONObject obj, String FieldName) {
		if(isEmpty(obj)){
			return;
		}
		if(obj.has(FieldName)){
			obj.remove(FieldName);
		}
	}
	
	public static void putJsonField(JSONObject obj, String FieldName, Object FieldValue) {
		if(isEmpty(obj)){
			return;
		}
		try {
			obj.put(FieldName,FieldValue);
		} catch (JSONException e) {
			LogUtil.printLog("putJsonField出现出错:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static String getMapField(Map map, String FieldName) {
		return SysUtility.isNotEmpty(map.get(FieldName))?(String)map.get(FieldName.toUpperCase()):(String)map.get(FieldName.toLowerCase());
	}
	
	public static String getJsonField(JSONObject obj, String FieldName) {
		if(isEmpty(obj)){
			return "";
		}
		try {
			if(obj.has(FieldName) && isNotEmpty(obj.get(FieldName)) && !"null".equals(obj.get(FieldName).toString())){
				return ""+obj.get(FieldName);
			}
		} catch (JSONException e) {
			LogUtil.printLog("getJsonField出现出错:"+e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	public static List getJsonFieldList(JSONObject obj, String FieldName) {
		if(isEmpty(obj)){
			return new ArrayList();
		}
		if(obj.has(FieldName)){
			try {
				Object o = obj.get(FieldName);
				if(o instanceof List){
					return (List)o;
				}else{
					return new ArrayList();
				}
			} catch (JSONException e) {
				LogUtil.printLog("getJsonField出现出错:"+e.getMessage(), Level.ERROR);
			}
		}
		return new ArrayList();
	}
	
	public static List getStringAsList(String str) {
		List lst = new ArrayList();
		if(SysUtility.isNotEmpty(str) && str.indexOf("[{") >= 0 && str.indexOf("}]") >= 0){
			String[] strs = (str.substring(str.indexOf("[{")+2, str.indexOf("}]"))).split("\\}\\, \\{");
			for (int i = 0; i < strs.length; i++) {
				String[] row = strs[i].split(",");
				HashMap tempMap = new HashMap();
				for (int j = 0; j < row.length; j++) {
					if(SysUtility.isNotEmpty(row[j].split("=")) 
							&& row[j].split("=").length == 2
							&& SysUtility.isNotEmpty(row[j].split("=")[0]) 
							&& SysUtility.isNotEmpty(row[j].split("=")[1])){
						tempMap.put(row[j].split("=")[0].trim(), row[j].split("=")[1].trim());
					}
				}
				lst.add(tempMap);
			}
		}
		return lst;
	}
	
	public static String processNullString(String str) {
		if(SysUtility.isEmpty(str)){
			return "";
		}
		return str;
	}
	
	public static List<Map<String,Object>> JSONToList(String TableName,JSONObject datas) throws LegendException{
		if(SysUtility.isEmpty(TableName) || SysUtility.isEmpty(datas)){
    		return new ArrayList<Map<String,Object>>();
    	}
		try {
			if(datas.has(TableName)){
				JSONArray rows = datas.getJSONArray(TableName);
				return JSONArrayToList(rows);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}	
		return new ArrayList<Map<String,Object>>();
	}
	
	public static HashMap JSONObjectToHashMap(JSONObject obj,String rootName) throws LegendException, JSONException{
		HashMap map = new HashMap();
		Object tempObj = obj.get(rootName);
		if(tempObj instanceof JSONObject){
			map.put(rootName, JSONObjectToHashMap((JSONObject)tempObj));
		}else if(tempObj instanceof JSONArray){
			map.put(rootName, JSONArrayToList((JSONArray)tempObj));
		}
		return map;
	}
	
	public static HashMap JSONObjectToHashMap(JSONObject obj) throws LegendException, JSONException{
		HashMap row = new HashMap();
		if(SysUtility.isEmpty(obj)){
			return row;
		}
		
		for (Iterator<?> keys = obj.keys(); keys.hasNext();) {
			String key = keys.next().toString();
			Object value = (Object)obj.get(key);
			if(value instanceof String){
				row.put(key, value);
			}else if(value instanceof JSONObject){
				row.put(key, JSONObjectToHashMap((JSONObject)value));
			}else if(value instanceof JSONArray){
				row.put(key, JSONArrayToList((JSONArray)value));
			}
		}
		return row;
	}
	
	public static List<Map<String,Object>> JSONArrayToList(JSONArray array) throws LegendException{
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		if(SysUtility.isEmpty(array)){
    		return rows;
    	}
		try {
			for(int i = 0 ; i < array.length();i++){
				JSONObject row = array.getJSONObject(i);
				Iterator<?> keys = row.keys();
				Map<String,Object> map = new HashMap<String,Object>();
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					Object value = row.get(key);
					if(value instanceof String){
						map.put(key, value);
					}else if(value instanceof JSONObject){
						map.put(key, JSONObjectToHashMap((JSONObject)value));
					}else if(value instanceof JSONArray){
						map.put(key, JSONArrayToList((JSONArray)value));
					}
	    		}
	    		rows.add(map);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rows;
	}
	
	public static JSONArray ListToJSONArray(List<Map<String,Object>> list) throws LegendException{
		JSONArray rows = new JSONArray();
		if(SysUtility.isEmpty(list)){
    		return rows;
    	}
		try {
			for(int i = 0 ; i < list.size();i++){
				JSONObject row = new JSONObject();
				Map<String,Object> map = list.get(i);
				Set mapSet = map.entrySet();
				for (Iterator it = mapSet.iterator(); it.hasNext();) {
				    Entry entry = (Entry)it.next();
				    String key = (String)entry.getKey();
				    Object value = entry.getValue();
				    if(value instanceof String){
				    	row.put(key, value);
				    }else if(value instanceof Map){
					    row.put(key, MapToJSONObject((Map)entry.getValue()));
				    }else if(value instanceof List){
				    	row.put(key, ListToJSONArray((List)value));
				    }
				}
	    		rows.put(row);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rows;
	}
	
	public static JSONObject MapToJSONObject(Map map) throws LegendException, JSONException{
		JSONObject row = new JSONObject();
		if(SysUtility.isEmpty(map)){
			return row;
		}
		
		Set mapSet = map.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
		   Entry entry = (Entry)it.next();
		   String key = (String)entry.getKey();
		   Object value = entry.getValue();
		   
		   if(value instanceof String){
			   row.put(key, value);
		   }else if(value instanceof Map){
			   row.put(key, MapToJSONObject((Map)value));
		   }else if(value instanceof List){
			   row.put(key, ListToJSONArray((List)value));
		   }
		}
		return row;
	}
	
	public static String processStr(String str){
		if(SysUtility.isEmpty(str)){
			return "";
		}
//		return parseXMLCData(str);
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&apos;").replaceAll("\"\"", "&quot;");
	}
	
	public static String parseXMLCData(String strValue) {
		if (strValue.indexOf('<') > -1 || strValue.indexOf('>') > -1 || strValue.indexOf('\n') > -1
				|| strValue.indexOf('&') > -1 || strValue.indexOf('\'') > -1
				|| strValue.indexOf('"') > -1) {
			StringBuffer sb = new StringBuffer();

			if (strValue.indexOf("]]>") > -1) {
				sb.append(parseEndXMLCData(strValue));
			} else {
				sb.append("<![CDATA[");
				sb.append(strValue);
				sb.append("]]>");
			}

			return sb.toString();
		} else {
			return strValue;
		}
	}

	private static String parseEndXMLCData(String strValue) {
		int pos = strValue.indexOf("]]>");
		StringBuffer sb = new StringBuffer();

		if (pos > -1) {
			sb.append("<![CDATA[");
			sb.append(strValue.substring(0, pos + 1));
			sb.append("]]>");
			sb.append(parseEndXMLCData(strValue.substring(pos + 1)));
		} else {
			sb.append("<![CDATA[");
			sb.append(strValue);
			sb.append("]]>");
		}

		return sb.toString();
	}
	
	//字符串"sha1"编码
	public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
        	LogUtil.printLog(e.getMessage(), Level.ERROR);
        }
        return "";
    }
	
	public static Set splitToSet(String inputStr) {
		return splitToSet(inputStr, ",");
	}
	
	/**
	 * 按分隔符解析字符串 自动去掉重复的值及空字符串
	 * 
	 * @param inputStr
	 * @param regex
	 * @return
	 */
	public static Set splitToSet(String inputStr, String regex) {
		if (isEmpty(inputStr)) {
			return new HashSet();
		} else {
			String[] partners = inputStr.split(regex);
			Set set = new HashSet(partners.length);
			set.addAll(Arrays.asList(partners));
			set.remove(EMPTY);
			return set;
		}
	}
	
	public static String GetFileLine(InputStream is){
		StringBuffer rtMsg = new StringBuffer();
		try {
			InputStreamReader isRd = new InputStreamReader(is);
			LineNumberReader reader = new LineNumberReader(isRd);
			int totalLine = 0;
			int nullCount = 0;//结束读写内容
			while(true){
				String str = reader.readLine();
				if(SysUtility.isEmpty(str)){
					nullCount++;
					if(nullCount > 3)//遇到4个空格，表示结束
			    		break;
					continue;
				}else{
					rtMsg.append(str);
				}
				nullCount = 0;
				totalLine ++;
			}
			
			if(reader != null){
				reader.close();
			}
			if(isRd != null){
				isRd.close();
			}
		} catch (IOException e) {
			LogUtil.printLog("GetFileLine(InputStream is) Error:"+e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(is);
		}
		return rtMsg.toString();
	}
	
	/**
	 * @param breakCount 所得到的行数内容
	 * @param 
	 * 
	 * **/
	public static String GetFileLine(InputStream is,int breakCount){
		String result = "";
		try {
			InputStreamReader isRd = new InputStreamReader(is);
			LineNumberReader reader = new LineNumberReader(isRd);
			int totalLine = 0;
			int nullCount = 0;//结束读写内容
			while(true){
				String str = reader.readLine();
				if(totalLine == breakCount){
					result = str;
					break;
				}
				if(SysUtility.isEmpty(str)){
					nullCount++;
					if(nullCount > 3)//遇到4个空格，表示结束
			    		break;
					continue;
				}
				nullCount = 0;
				totalLine ++;
			}
			
			if(reader != null){
				reader.close();
			}
			if(isRd != null){
				isRd.close();
			}
		} catch (IOException e) {
			LogUtil.printLog("GetFileLine Error:"+e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(is);
		}
		return result;
	}
	
	
	public static HashMap StrsToHashMap(String keys,String values){
		HashMap map = new HashMap();
		if(SysUtility.isEmpty(keys) || SysUtility.isEmpty(values)){
			return map;
		}
		String[] key = keys.split(",");
		String[] value = values.split(",");
		for (int i = 0; i < key.length; i++) {
			if(i == value.length){
				break;
			}
			map.put(key[i], value[i]);
		}
		return map;
	}
	
	public static Map createRow(ResultSet rs, ResultSetMetaData md,int columnCount,int blobProcess) 
			throws SQLException,LegendException {
		Map row = new HashMap();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = md.getColumnName(i);
			if ("ROW_NUM__".equals(columnName)) {
				continue;
			}
			int sqlType = md.getColumnType(i);
			row.put(columnName, SysUtility.getString(rs, i, sqlType,blobProcess));
		}
		return row;
	}
	
	/**结果集转成JSONObject对象
	 * @param TableName 对象表名
	 * @return JSONObject对象
	 * */
	public static JSONObject ResToJSON(String TableName,ResultSet rs) throws LegendException{
		return ResToJSON(TableName, rs, 0);
	}
	
	public static JSONObject ResToJSON(String TableName,ResultSet rs,int blobProcess) throws LegendException{
		JSONObject rtobj = new JSONObject();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			JSONArray rows = new JSONArray();
			try {
				while(rs.next()) {
					JSONObject row = new JSONObject();
					for(int index = 1 ; index <= rsmd.getColumnCount() ; index++){
						String key = rsmd.getColumnLabel(index);
						
						try {
							Object value = rs.getObject(index);
							if(value == null){
								row.put(key, "");
							}else{
								row.put(key, SysUtility.getString(rs, index, rsmd.getColumnType(index),blobProcess));
							}
						} catch (Exception e) {
							e.printStackTrace();
							row.put(key, "");
						}
					}
					rows.put(row);
				}
				rtobj.put(TableName, rows);
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rtobj;
	}
	
	/**结果集转成JSON格式的StringBuilder
	 * @param TableName 对象表名
	 * @return JSON格式的StringBuilder
	 * */
	public static String ResToString(String TableName, ResultSet rs) throws LegendException{
		if(SysUtility.isEmpty(TableName)){
			return ResToString(rs);
		}
		StringBuilder rt = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			rt.append("{\"" + TableName + "\":[");
			String rlk = "";
			while(rs.next()) {
				String flk = "";
				rt.append(rlk + "{");
				for(int c = 1 ; c <= rsmd.getColumnCount() ; c++)
				{
					rt.append(flk + "\"");
					rt.append(rsmd.getColumnLabel(c));
					rt.append("\":\"");
					String v = rs.getString(c);
					if(v==null)
					{
						v = "";
					}
					rt.append(v.replace("\"", "\\\""));
					rt.append("\"");
					flk = ",";
				}
				rt.append("}");
				rlk = ",";
			}
			rt.append("]}");
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rt.toString();
	}
	
	public static String ResToString(ResultSet rs) throws LegendException{
		StringBuilder rt = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			rt.append("[");
			String rlk = "";
			while (rs.next()) {
				String flk = "";
				rt.append(rlk + "{");
				for (int c = 1; c <= rsmd.getColumnCount(); c++) {
					rt.append(flk + "\"");
					rt.append(rsmd.getColumnLabel(c));
					rt.append("\":\"");
					String v = rs.getString(c);
					if (v == null) {
						v = "";
					}
					rt.append(v.replace("\"", "\\\""));
					rt.append("\"");
					flk = ",";
				}
				rt.append("}");
				rlk = ",";
			}
			  rt.append("]");
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rt.toString();
	}
	
	/**结果集转成ArrayList
	 * @return ArrayList
	 * */
	public static List ResToList(ResultSet rs) throws LegendException{
		List rtlist = new ArrayList();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
				Map row = new HashMap();
				for(int c = 1 ; c <= rsmd.getColumnCount() ; c++){
					row.put(rsmd.getColumnLabel(c), rs.getString(c));
				}
				rtlist.add(row);
			}
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rtlist;
	}
	
	/**
	 * 根据sqlType 把数据库数据转换成字符串
	 * 
	 * @param rs
	 * @param index
	 * @param sqlType
	 * @return
	 * @throws SQLException
	 * @throws LegendException
	 */
	public static Object getString(ResultSet rs, int index, int sqlType,int blobProcess) throws SQLException, LegendException {
		switch (sqlType) {
		// 浮点型
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
			/*int scale = rs.getMetaData().getScale(index);//官方说明，int getScale(int column)  获取指定列的小数点右边的位数。
			if (scale > 0) {
				String value = String.valueOf(rs.getBigDecimal(index));
				return "null".equals(value) ? null : value;
			} else {
				return rs.getString(index);
			}*/
			String value = String.valueOf(rs.getBigDecimal(index));
			return "null".equals(value) ? null : value;
			// 时间型
		case Types.DATE:
			return perfectDateFormat(rs.getTimestamp(index));

			// 时间戳型
		case Types.TIMESTAMP:
			return perfectTimestampFormat(rs.getTimestamp(index));

			// BLOB型
		case Types.BLOB:
			return readBlob(rs.getBlob(index),blobProcess);
			// 其他类型 包括Clob
		default:
			return rs.getString(index);
		}
	}
	
	public static Object readBlob(Blob blob,int blobProcess) throws LegendException {
		if (blob == null) {
			return "";
		}
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] bytes = bos.toByteArray();
			if(1 == blobProcess){
				return FileUtility.getBase64encoder().encode(bytes);
			}else if(2 == blobProcess){
				String str = new String(bytes,"utf-8");
				return str;
//				return FileUtility.getBase64decoder().decodeBuffer(str);
			}else if(3 == blobProcess){
				String str = new String(bytes,"gbk");
				return str;
			}
			return new String(bytes,"utf-8");
		} catch (Exception e) {
			throw LegendException.getLegendException("读取Blob出错");
		} finally {
			SysUtility.closeInputStream(is);
		}
	}
	
	public static String GetStringByIs(InputStream is,boolean isBase64Encode) throws LegendException, IOException {
		if (is == null) {
			return "";
		}
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] bytes = bos.toByteArray();
			if(isBase64Encode){
				return FileUtility.getBase64encoder().encode(bytes);
			}
			return new String(bytes,"utf-8");
		} catch (Exception e) {
			throw LegendException.getLegendException("SysUtility.GetStrByIs()出错!");
		} finally {
			closeByteArrayOutputStream(bos);
			SysUtility.closeInputStream(is);
		}
	}
	
	public static InputStream String2InputStream(String str,String Endcoding) throws LegendException, IOException {
		InputStream is = new ByteArrayInputStream(str.getBytes());
		return is;
	}
	
	public static String InputStream2String(InputStream is,String Endcoding) throws LegendException, IOException {
		if (is == null) {
			return "";
		}
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] bytes = bos.toByteArray();
			return new String(bytes,Endcoding);
		} catch (Exception e) {
			throw LegendException.getLegendException("InputStream2String出错!");
		} finally {
			closeByteArrayOutputStream(bos);
			SysUtility.closeInputStream(is);
		}
	}
	
	public static String GetOracleSysGuid() throws LegendException {
		String SQL = "select sys_guid() from dual";
		return SQLExecUtils.query4String(SQL);
	}
	
	public static String GetOracleSequence(String SequenceName) throws LegendException {
		String SQL = "select "+SequenceName+".nextval from dual";
		return SQLExecUtils.query4String(SQL);
	}
	
	public static String GetUUID() {
		String str = UUID.randomUUID().toString();
		return str.replaceAll("-", "");
	}
	
	public static void AddOracleBlob(String Content,String ForUpdateSQL,String ColumnName) throws LegendException, IOException, SQLException {
		if(SysUtility.isEmpty(Content)){
			return;
		}
		
		byte[] by = FileUtility.getBase64decoder().decodeBuffer(Content);
		Connection conn = SysUtility.getCurrentConnection();
		
		InputStream is = new ByteArrayInputStream(by);
		Statement stmt = null;
		ResultSet rs = null;
		OutputStream os = null;  
		try {
			stmt = conn.createStatement(); 
			rs = stmt.executeQuery(ForUpdateSQL); 
			if (rs.next()) {
			    oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(ColumnName);// 得到java.sql.Blob对象后强制转换为oracle.sql.BLOB    // 通过getBinaryOutputStream()方法获得向数据库中插入图片的"管道"  
			    os = blob.getBinaryOutputStream();  // 依次读取流字节,并输出到已定义好的数据库字段中.  
			    int i = 0;  
			    while ((i = is.read()) != -1) {  
			        os.write(i);  
			    }  
			}
		} catch (Exception e) {
			LogUtil.printLog("SysUtility.AddOracleBlob出错："+e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(is);
			closeStatement(stmt);
			closeResultSet(rs);
			closeOutputStream(os);
		}
	}
	
	public static void AddOracleBlob(InputStream is,String ForUpdateSQL,String ColumnName) throws LegendException, IOException, SQLException {
		if(SysUtility.isEmpty(is)){
			return;
		}
		Connection conn = SysUtility.getCurrentConnection();
		
		Statement stmt = null;
		ResultSet rs = null;
		OutputStream os = null;  
		try {
			stmt = conn.createStatement(); 
			rs = stmt.executeQuery(ForUpdateSQL); 
			if (rs.next()) {
			    oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(ColumnName);// 得到java.sql.Blob对象后强制转换为oracle.sql.BLOB    // 通过getBinaryOutputStream()方法获得向数据库中插入图片的"管道"  
			    os = blob.getBinaryOutputStream();  // 依次读取流字节,并输出到已定义好的数据库字段中.  
			    int i = 0;  
			    while ((i = is.read()) != -1) {  
			        os.write(i);  
			    }  
			}
		} catch (Exception e) {
			LogUtil.printLog("SysUtility.AddOracleBlob出错："+e.getMessage(), Level.ERROR);
		} finally{
			closeInputStream(is);
			closeStatement(stmt);
			closeResultSet(rs);
			closeOutputStream(os);
		}
	}
	
	public static boolean SequenceExists(final String SequenceName) throws LegendException{
		if(SysUtility.isEmpty(SequenceName)){
			return false;
		}
		final String TName = SequenceName.toUpperCase();
		String SQL = "SELECT 0 FROM USER_SEQUENCES U WHERE U.SEQUENCE_NAME = ? and rownum = 1";
		String rt = SQLExecUtils.query4String(SQL,new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, TName);
			}
		});
		if(SysUtility.isNotEmpty(rt)){
			return true;
		}
		return false;
	}
	
	public static String mysqlschema = "";
	
	public static String getMySqlSchema() throws LegendException, SQLException{
		if(SysUtility.isEmpty(mysqlschema)) {
			Connection conn = SysUtility.getCurrentConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			String url = metaData.getURL();
			mysqlschema = url.substring(0, url.indexOf("?"));
			mysqlschema = mysqlschema.substring(mysqlschema.lastIndexOf("/")+1, mysqlschema.length());
		}
		return mysqlschema;
	}
	
	public static boolean TableNameExists(final String TableName) throws LegendException, SQLException{
		if(SysUtility.isEmpty(TableName)){
			return false;
		}
		
		if(SysUtility.IsOracleDB()) {
			String rt = SQLExecUtils.query4String("SELECT 0 FROM USER_ALL_TABLES U WHERE U.TABLE_NAME = '"+TableName.toUpperCase()+"' and rownum = 1");
			if(SysUtility.isNotEmpty(rt)){
				return true;
			}
		}else if(SysUtility.IsMySqlDB()) {
			String rt = SQLExecUtils.query4String("select 0 from information_schema.tables where table_schema='"+getMySqlSchema()+"' and table_name = '"+TableName.toLowerCase()+"'");
			if(SysUtility.isNotEmpty(rt)){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean ColumnNameExists(String TableName,String ColumnName) throws LegendException{
		if(SysUtility.isEmpty(TableName)){
			return false;
		}
		final String T_TableName = TableName.toUpperCase();
		final String T_ColumnName = ColumnName.toUpperCase();
		
		String SQL = "SELECT 0 FROM USER_TAB_COLUMNS U WHERE U.TABLE_NAME = ? AND COLUMN_NAME = ? AND ROWNUM = 1";
		String rt = SQLExecUtils.query4String(SQL,new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, T_TableName);
				ps.setString(2, T_ColumnName);
			}
		});
		if(SysUtility.isNotEmpty(rt)){
			return true;
		}
		return false;
	}
	
	//判断是否是框架能解析的xml格式
	public static boolean isExsFile(File file){
		try {
			String lineStr = SysUtility.GetFileLine(new FileInputStream(file), 0);
			if(SysUtility.isNotEmpty(lineStr)){
				if(lineStr.indexOf("RequestMessage") >= 0 || lineStr.indexOf("ResponseMessage") >= 0){
					return true;
				}
			}
			lineStr = SysUtility.GetFileLine(new FileInputStream(file), 1);
			if(SysUtility.isNotEmpty(lineStr)){
				if(lineStr.indexOf("RequestMessage") >= 0 || lineStr.indexOf("ResponseMessage") >= 0){
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			return false;
		}
		return false;
	}
	
	public static byte[] unzip(byte[] data) throws IOException {
		if ((data == null) || (data.length == 0)) {
			return null;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ZipInputStream zipin = new ZipInputStream(in);
		zipin.getNextEntry();

		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i;
		do {
			i = zipin.read(buffer, 0, 1024);
			if ((i != -1) && (i < 1024)) {
				byte[] bytesOnce = new byte[i];
				for (int j = 0; j < i; j++)
					bytesOnce[j] = buffer[j];
				out.write(bytesOnce);
			} else if (i == 1024) {
				out.write(buffer);
			}
		} while (i > 0);
		out.flush();

		return out.toByteArray();
	}
	
	public static BASE64Encoder getBase64encoder() throws IOException{
		return base64encoder;
	}
	
	public static BASE64Decoder getBase64decoder() throws IOException{
		return base64decoder;
	}
	
	public static void SendMail(String subject,String content){
		SendMail(subject, null, null, content, null);
	}
	
	public static void SendMail(String subject,String content, Object body){
		SendMail(subject, null, null, content, body);
	}
	
	public static void SendMail(String subject,String mailFrom, String mailTO,String content){
		SendMail(subject, mailFrom, mailTO, content, null);
	}
	
	public static void SendMail(String subject,String mailFrom, String mailTO,String content, Object body){
		try {
			String system = "";
			if("true".equals(SysUtility.GetProperty("thr/mail.properties","mail.open"))){
				system = SysUtility.GetProperty("thr/mail.properties","mail.system");
				if(SysUtility.isEmpty(system)){
					system = "";
				}
				if(SysUtility.isEmpty(mailFrom)){
					mailFrom = SysUtility.GetProperty("thr/mail.properties","mail.mailFrom");
				}
				if(SysUtility.isEmpty(mailTO)){
					mailTO = SysUtility.GetProperty("thr/mail.properties","mail.mailTO");
				}
				MailSender.send(system+subject, mailFrom,mailTO, null, content, body);
			}else if("true".equals(SysUtility.GetProperty("mail.properties","mail.open"))){
				system = SysUtility.GetProperty("mail.properties","mail.system");
				if(SysUtility.isEmpty(system)){
					system = "";
				}
				if(SysUtility.isEmpty(mailFrom)){
					mailFrom = SysUtility.GetProperty("mail.properties","mail.mailFrom");
				}
				if(SysUtility.isEmpty(mailTO)){
					mailTO = SysUtility.GetProperty("mail.properties","mail.mailTO");
				}
			}
			//发送邮件
			MailSender.send(system+subject, mailFrom,mailTO, null, content, body);
		} catch (MessagingException e) {
			LogUtil.printLog("MessagingException"+e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog("LegendException"+e.getMessage(), Level.ERROR);
		} catch (Exception e) {
			LogUtil.printLog("Exception"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void loadOperator(Connection conn) throws LegendException{
		try {
			String userName = SysUtility.GetProperty("system.properties","username");
			String pwd = SysUtility.GetProperty("system.properties","password");
			SysUtility.loadOperator(SysUtility.getCurrentConnection(), userName, pwd);
		} catch (IOException e) {
			LogUtil.printLog("自动加载用户信息出错："+e.getMessage(), Level.ERROR);
		} catch (SQLException e) {
			LogUtil.printLog("自动加载用户信息出错："+e.getMessage(), Level.ERROR);
		}
	}
	
	public static void loadOperator(Connection conn,String uName,String pwd) throws LegendException, SQLException{
		if(SysUtility.isEmpty(conn)){
			return;
		}
		
		Map map = new HashMap();
		if(SysUtility.IsOracleDB() && SysUtility.TableNameExists("s_auth_user") && SysUtility.TableNameExists("S_AUTH_DEPARTMENT")){
			StringBuffer SQL = new StringBuffer();
			SQL.append("select u.* from s_auth_user u where 1 = 1 ");
			SQL.append(" and u.is_enabled ='1'");
			SQL.append(" and username = ? and password = ?");
			try {
				final String userName = SysUtility.isEmpty(uName)?"daemon":uName;
				final String password = MD5Utility.encrypt(SysUtility.isEmpty(pwd)?"111111":pwd);
				map = SQLExecUtils.query4Map(conn, SQL.toString(), new Callback() {
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, userName);
						ps.setString(2, password);
					}
				});
			} catch (LegendException e) {
				LogUtil.printLog("自动加载用户信息出错："+e.getMessage(), Level.ERROR);
			}
		}
		Operator operator = new Operator();
		if(SysUtility.isNotEmpty(map) && SysUtility.IsOracleDB()){
			operator.setIndx(Integer.parseInt((String)map.get("INDX")));
			operator.setName((String)map.get("USER_REAL_NAME"));
			operator.setUserName((String)map.get("USERNAME"));
			operator.setPassWord((String)map.get("PASSWORD"));
			operator.setOrgId((String)map.get("ORG_ID"));	
			operator.setPartId(operator.getOrgId());
			operator.setDeptId((String)map.get("USER_DEPT_ID"));
			operator.setDeptName((String)map.get("USER_DEPT_NAME"));
		}else if(SysUtility.isNotEmpty(map) && SysUtility.IsMySqlDB()){
			operator.setIndx(Integer.parseInt((String)map.get("indx")));
			operator.setName((String)map.get("user_real_name"));
			operator.setUserName((String)map.get("username"));
			operator.setPassWord((String)map.get("password"));
			operator.setOrgId((String)map.get("org_id"));	
			operator.setPartId(operator.getOrgId());
			operator.setDeptId((String)map.get("user_dept_id"));
			operator.setDeptName((String)map.get("user_dept_name"));
		}
		SessionManager.setAttribute(LoginUser, operator);
	}
	
	public static void GroupChildByPindx(List childList, Map childMap) {
		Iterator it = childList.iterator();
		while (it.hasNext()) {
			Map tmpMap = (Map) it.next();
			String pIndx = (String) tmpMap.get("P_INDX");
			if (childMap.containsKey(pIndx)) {
				((List) childMap.get(pIndx)).add(tmpMap);
			} else {
				List tmpLst = new ArrayList();
				tmpLst.add(tmpMap); 
				childMap.put(pIndx, tmpLst);
			}
		}
	}
	
	public static byte[] InputStreamToByte(InputStream is) {
		ByteArrayOutputStream baos = null;
		try {
			int count = 0;
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((count = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, count);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			LogUtil.printLog("InputStreamToByte Error:"+e.getMessage(), LogUtil.ERROR);
		} finally{
			SysUtility.closeInputStream(is);
			SysUtility.closeOutputStream(baos);
		}
		return new byte[0];
	}
	
	public final static String Windows = "Windows";
	public final static String Linux = "Linux";
	
	public static String GetOsName(){
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if(SysUtility.isEmpty(os)){
			return Windows;
		}
		if(os.startsWith("Win") ||os.startsWith("win")){
			return Windows;
		}else if(os.startsWith(Linux)){
			return Linux;
		}
		return Windows;
	}
	
	public static String CreateCBMainFolder(String tag){
		String MainFolder = "";
		
		if(SysUtility.Windows.equals(SysUtility.GetOsName())){
			MainFolder = "D://GlobalService"+tag;
			boolean rt = CreateFolder(MainFolder);
			if(!rt){
				MainFolder = "E://GlobalService"+tag;
				CreateFolder(MainFolder);
			}
		}else if(SysUtility.Linux.equals(SysUtility.GetOsName())){
			MainFolder = "/usr/java/GlobalService"+tag;
			CreateFolder(MainFolder);
		}
		return MainFolder;
	}
	
	public static boolean CreateFolder(String path){
		boolean rt = false;
		
		File file = new File(path);
		if(!file.exists()) {
		   if(file.mkdirs()){
			   rt = true;
			   LogUtil.printLog("文件夹创建成功："+path, Level.INFO);
		   }
		}else{
			rt = true;
		}
		return rt;
	}
	
	public static InetAddress getLocalInetAddress(){
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LogUtil.printLog("SysUtility.getLocalInetAddress()出错："+e.getMessage(), Level.ERROR);
		}
		return null;
	}
	
	public static InetAddress getInetAddressByName(String PcName){
		try {
			return InetAddress.getByName(PcName);
		} catch (UnknownHostException e) {
			LogUtil.printLog("SysUtility.getInetAddressByName("+PcName+")出错："+e.getMessage(), Level.ERROR);
		}
		return null;
	}
	
	public static byte[] ObjectToByte(java.lang.Object obj) {
	    byte[] bytes = null;  
	    ByteArrayOutputStream bo = null;  
	    ObjectOutputStream oo = null;  
	    try {  
	        bo = new ByteArrayOutputStream();  
	        oo = new ObjectOutputStream(bo);  
	        oo.writeObject(obj);  
	        bytes = bo.toByteArray();  
	    } catch (Exception e) {  
	    	LogUtil.printLog(""+e.getMessage(), Level.ERROR);
	    } finally{
	    	if(isNotEmpty(bo)){
	    		try {
					bo.close();
				} catch (IOException e) {
					LogUtil.printLog(""+e.getMessage(), Level.ERROR);
				}
	    	}
	    	if(isNotEmpty(oo)){
	    		try {
					oo.close();
				} catch (IOException e) {
					LogUtil.printLog(""+e.getMessage(), Level.ERROR);
				}
	    	}
	    }
	    return bytes;  
	}  
	
	public static Object ByteToObject(byte[] bytes) {  
		Object obj = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {  
		    bi = new ByteArrayInputStream(bytes);  
		    oi = new ObjectInputStream(bi);  
		    obj = oi.readObject();  
		} catch (Exception e) {  
			LogUtil.printLog(""+e.getMessage(), Level.ERROR);
		} finally{
			if(isNotEmpty(bi)){
	    		try {
	    			bi.close();
				} catch (IOException e) {
					LogUtil.printLog(""+e.getMessage(), Level.ERROR);
				}
	    	}
			if(isNotEmpty(oi)){
	    		try {
	    			oi.close();
				} catch (IOException e) {
					LogUtil.printLog(""+e.getMessage(), Level.ERROR);
				}
	    	}
		}
		return obj;  
	}  
	
	public static boolean quartzLimit() throws ParseException {
		String EffectTime = SysUtility.GetSetting("system", "EffectTime");
		if(SysUtility.isNotEmpty(EffectTime) && EffectTime.length() == 5){
			String[] EffectTimes = EffectTime.split(",");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date currentTime = dateFormat.parse(SysUtility.getSysDate());
			int currentHours = currentTime.getHours();//小时
			if(currentHours > Integer.parseInt(EffectTimes[0]) && currentHours < Integer.parseInt(EffectTimes[1])){
				return true;
			}
		}
		return false;
	}
	
	public static void CreateFolders(List<String> paths){
		for (int i = 0; i < paths.size(); i++) {
			if(SysUtility.isEmpty(paths.get(i)) || paths.get(i).indexOf(File.separator+"null") > 0){
				continue;
			}
			String path = paths.get(i).toString();
			
			File file = new File(path);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+path, Level.INFO);
			   }
			}
		}
	}
	
	public static boolean isNotEmptyMapList(Object obj){
		if(obj instanceof List){
			List lst = (List)obj;
			for (int i = 0; i < lst.size(); i++) {
				Map tempMap = (Map)lst.get(i);
				Set tempSet = tempMap.entrySet();
				for (Iterator it = tempSet.iterator(); it.hasNext();) {
					Entry entry = (Entry)it.next();
			        Object key = entry.getKey();
			        Object value = entry.getValue();
			        if (value instanceof List) {
			        	if(isNotEmptyMapList((List)value))
			        		return true;
			        }else if (value instanceof Map) {
			        	Map map = (Map)value;
			        	Set tempSet2 = map.entrySet();
			        	for (Iterator it2 = tempSet2.iterator(); it2.hasNext();) {
			        		Entry entry2 = (Entry)it2.next();
					        Object key2 = entry2.getKey();
					        Object value2 = entry2.getValue();
					        if(value2 instanceof List) {
					        	if(isNotEmptyMapList((List)value2))
					        		return true;
					        }else if (value2 instanceof Map){
					        	if(isNotEmptyMapList((Map)value2))
					        		return true;
					        }else if (value2 instanceof String && SysUtility.isNotEmpty(value2)){
					        	return true;
					        }
			        	}
			        }else if (value instanceof String && SysUtility.isNotEmpty(value)){
			        	return true;
			        }
				}
			}
		}else if(obj instanceof Map){
			Map map = (Map)obj;
        	Set tempSet = map.entrySet();
        	for (Iterator it = tempSet.iterator(); it.hasNext();) {
        		Entry entry = (Entry)it.next();
		        Object key = entry.getKey();
		        Object value = entry.getValue();
		        if(value instanceof List) {
		        	if(isNotEmptyMapList((List)value))
		        		return true;
		        }else if (value instanceof Map){
		        	if(isNotEmptyMapList((List)value))
		        		return true;
		        }else if (value instanceof String && SysUtility.isNotEmpty(value)){
		        	return true;
		        }
        	}
		}
		return false;
	}
	
	public static HashMap getMapByProperties(Object obj,String filepath){
		HashMap map = new HashMap();
		
		InputStream is = obj.getClass().getClassLoader().getResourceAsStream(filepath);
		Properties pro = new Properties();
		try {
			pro.load(is);
			Iterator<Entry<Object, Object>> it = pro.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Entry<Object, Object> entry = it.next();  
	            Object key = entry.getKey();
//	            pro.getProperty(key.toString())
//	            new String(value.toString().getBytes(), "UTF-")
	            Object value = entry.getValue();
	            if(isNotEmpty(key) && isNotEmpty(value)){
	            	map.put(key, value);
	            }
	        }  
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			SysUtility.closeInputStream(is);
		}
		return map;
	}
	
	public static List GetProxoolCount(Object obj,String filepath){
		List rtLst = new ArrayList();
		
		InputStream is = obj.getClass().getClassLoader().getResourceAsStream(filepath);
		if(SysUtility.isEmpty(is)){
			return new ArrayList();
		}
		InputStreamReader isRd = null;
		LineNumberReader reader = null;
		try {
			isRd = new InputStreamReader(is);
			reader = new LineNumberReader(isRd);
			int blankCount = 0;
			while(true){
				String str = reader.readLine();
				if(blankCount >= 5){
					break;
				}else if(SysUtility.isEmpty(str) || str.startsWith("#")){
					blankCount++;
					continue;
				}else if(SysUtility.isNotEmpty(str) && str.indexOf(".") > 0){
					String tempStr = str.substring(0, str.indexOf(".")).trim();
					if(!rtLst.contains(tempStr)){
						rtLst.add(tempStr);
					}
					blankCount = 0;
				}
			}
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			closeInputStreamReader(reader);
			closeLineNumberReader(isRd);
			closeInputStream(is);
		}
		return rtLst;
	}
	
	public static HashMap GetProxoolSchema(Object obj,String filepath){
		return GetProxoolValue(obj, filepath, "proxool.alias");
	}
	
	public static HashMap GetProxoolValue(Object obj,String filepath,String key){
		HashMap map = new HashMap();
		
		InputStream is = obj.getClass().getClassLoader().getResourceAsStream(filepath);
		if(SysUtility.isEmpty(is)){
			return new HashMap();
		}
		InputStreamReader isRd = null;
		LineNumberReader reader = null;
		try {
			isRd = new InputStreamReader(is);
			reader = new LineNumberReader(isRd);
			int blankCount = 0;
			while(true){
				String str = reader.readLine();
				if(blankCount >= 5){
					break;
				}else if(SysUtility.isEmpty(str) || str.startsWith("#")){
					blankCount++;
					continue;
				}else if(SysUtility.isNotEmpty(str) && str.indexOf(".") > 0){
					String tempStr = str.substring(0, str.indexOf(".")).trim();
					if(SysUtility.isEmpty(map.get(tempStr)) && str.indexOf(key) >= 0){
						map.put(tempStr, str.substring(str.indexOf("=")+1).trim());
					}
					blankCount = 0;
				}
			}
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			closeInputStreamReader(reader);
			closeLineNumberReader(isRd);
			closeInputStream(is);
		}
		return map;
	}
	
	public static String replacePath(String path) throws IOException{
		if(SysUtility.isEmpty(path)){
			return "";
		}
		StringBuffer rtPath = new StringBuffer();
		String sign = ";";
		String[] paths = path.split(sign); 
		for (int i = 0; i < paths.length; i++) {
			String tempPath = paths[i];
			
			int beginIndex = tempPath.indexOf("{");
			int endIndex = tempPath.indexOf("}");
			if(beginIndex > -1 && endIndex > -1 && endIndex > beginIndex){
				String key = tempPath.substring(beginIndex+1, endIndex);
				/*******1.读取当前变量的值，如果Windows和Linux的粒度下有配置，则取更小粒度的配置值**********/
				String value = SysUtility.GetProperty("system.properties", key);
				if(SysUtility.Windows.equals(SysUtility.GetOsName()) && SysUtility.isNotEmpty(SysUtility.GetProperty("system.properties", "Windows" +key))){
					value = SysUtility.GetProperty("system.properties", "Windows" +key);
				}else if(SysUtility.Linux.equals(SysUtility.GetOsName()) && SysUtility.isNotEmpty(SysUtility.GetProperty("system.properties", "Linux" +key))){
					value = SysUtility.GetProperty("system.properties", "Linux" +key);
				}
				/*******2.多个路径时，需要拼接拆分号**********/
				if(i > 0){
					rtPath.append(sign);
				}
				if(SysUtility.isEmpty(value)){
					/*******3.1 没有读取到配置文件时，默认一个根目录：/Default**********/
					rtPath.append(File.separator+"Default"+tempPath.substring(endIndex+1,tempPath.length()));
				}else{
					/*******3.2 正常解析后得到的值**********/
					rtPath.append(value + tempPath.substring(endIndex+1,tempPath.length()));
				}
			}else{
				if(i > 0){
					rtPath.append(sign);
				}
				rtPath.append(tempPath);
			}
		}
		return rtPath.toString();
	}

	public static String replaceIP(String path) throws IOException{
		if(SysUtility.isEmpty(path)){
			return "";
		}
		String[] paths = path.split(";");
		for (int i = 0; i < paths.length; i++) {
			String tempPath = paths[i];

			int beginIndex = tempPath.indexOf("{");
			int endIndex = tempPath.indexOf("}");
			if(beginIndex > -1 && endIndex > -1 && endIndex > beginIndex){
				String key = tempPath.substring(beginIndex+1, endIndex);
				/*******1.读取当前变量的值**********/
				String value = SysUtility.GetProperty("system.properties", key);

				path = path.replaceAll("\\{"+key+"\\}",value);
			}
		}
		return path;
	}

	public static void main(String[] args) throws IOException {
		String str = replaceIP("tcp://{activemq_ip}:61616");
		System.out.println(str);
	}

	public static String EncryptKeys(String messageSource,String messageTime,String keys) throws LegendException{
		return MD5Utility.encrypt(messageSource+messageTime+keys);
	}
	
	public static boolean IsOracleDB(){
		if (Constants.DriverClass_Oracle.equalsIgnoreCase(getDriverClass())){
			return true;
		}
		return false;
	}
	public static boolean IsSQLServerDB(){
		if (Constants.DriverClass_Sqlserver.equalsIgnoreCase(getDriverClass()) || Constants.DriverClass_Sqlserver2.equalsIgnoreCase(getDriverClass())) {
			return true;
		}
		return false;
	}
	public static boolean IsMySqlDB(){
		if (Constants.DriverClass_Mysql.equalsIgnoreCase(getDriverClass())){
			return true;
		}
		return false;
	}

	private static String getDriverClass(){
		if(isNotEmpty(driverclassname)){
			return driverclassname;
		}

		String fileName = "proxool";
		String profiles = GetSettingProperties("application.yml", "spring.profiles.active");
		if(SysUtility.isNotEmpty(profiles)){
			fileName = "proxool-"+profiles;
		}
		String driverclass = SysUtility.GetSetting(fileName, Constants.DriverClass_Key);
		return driverclass;
	}


	public static boolean IsOracleDB(Connection conn){
		return IsJudgeDB(conn, "Oracle");
	}
	public static boolean IsSQLServerDB(Connection conn){
		return IsJudgeDB(conn, "SQLServer");
	}
	public static boolean IsMySqlDB(Connection conn){
		return IsJudgeDB(conn, "MySQL");
	}
	public static boolean IsJudgeDB(Connection conn, String str){
		try {
			String driverName = conn.getMetaData().getDriverName();
			if(SysUtility.isNotEmpty(driverName) && driverName.indexOf(str) >= 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static long getStrLength(String str){
		long count = 0;
		if(SysUtility.isEmpty(str)){
			return count;
		}
		
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			char tempC = c[i];
			String temmStr = tempC+"";
			if(isStrNumericNp(temmStr) || " ".equals(temmStr)){
				count ++;
			}else{
				count ++;
				count ++;
			}
		}
		
		return count;
	}
	
	public static String getStrByLength(String str,long length){
		if(SysUtility.isEmpty(str) || length <= 0){
			return "";
		}
		
		char[] c = str.toCharArray();
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			if(length <= 0){
				break;
			}
			String temmStr = c[i]+"";
			if(isStrNumericNp(temmStr) || " ".equals(temmStr)){
				length --;
			}else{
				length --;
				length --;
			}
			if(length >= 0){
				strBuf.append(temmStr);
			}else{
				break;
			}
		}
		return strBuf.toString();
	}
	
	public static void setQuartzConfigDatas(Datas datas,List<Object[]> fileList){
		setConfigDatas(datas, fileList, "exs_quartz_config");
	}
	
	public static void setExsConfigDatas(Datas datas,List<Object[]> fileList){
		setConfigDatas(datas, fileList, new String[]{"exs_config_xmltomq","exs_config_mqtoxml"});
	}
	
	public static void setConfigDatas(Datas datas,List<Object[]> fileList,Object XmlDocuments){
		for (int i = 0; i < fileList.size(); i++) {
			try {
				Object[] obj = (Object[])fileList.get(i);
				File file = (File)obj[1];
				String tempFileName = file.getName();
				if(SysUtility.isEmpty(tempFileName) || tempFileName.length() <=4 
						|| !"exs".equalsIgnoreCase(tempFileName.substring(0, 3))
						|| !".xml".equalsIgnoreCase(tempFileName.substring(tempFileName.length() - 4, tempFileName.length()))){
					continue;
				}
				InputStream is = new FileInputStream(file);
				String XmlRootName = FileUtility.GetExsFileRootName(file);
				HashMap hmSourceData = FileUtility.xmlParse(is,XmlRootName);
				if(XmlDocuments instanceof String[]){
					String[] tempXmlDocuments = (String[])XmlDocuments;
					for (int j = 0; j < tempXmlDocuments.length; j++) {
						datas.MapToDatas(tempXmlDocuments[j], hmSourceData);
					}
				}else if(XmlDocuments instanceof String){
					datas.MapToDatas((String)XmlDocuments, hmSourceData);
				}else{
					continue;
				}
			} catch (FileNotFoundException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	}
	
	public static String getNameValue(Map CheckMap, String strKey)throws LegendException {
		List result = new ArrayList();
		SysUtility.getNameValue(CheckMap, strKey, result);
		if(SysUtility.isNotEmpty(result) && result.size() > 0){
			return (String)result.get(0);
		}
		return "";
	}
	
	public static void getNameValue(Map CheckMap, String strKey,List result)throws LegendException {
		if(SysUtility.isEmpty(CheckMap) || SysUtility.isEmpty(strKey)){
			return;
		}
		
		Set mapSet = CheckMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
		    Entry entry =  (Entry)it.next();
		    Object key = entry.getKey();
		    Object value = entry.getValue();
		    
		    if(value instanceof String && strKey.equals(key)){
		    	result.add(value);
		    }else if(value instanceof Map){
		    	HashMap map2 = (HashMap)value;
				Set mapSet2 = map2.entrySet();
				for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
		            Entry entry2 =  (Entry)it2.next();
		            Object key2 = entry2.getKey();
		            Object value2 = entry2.getValue();
		            if(value2 instanceof String && strKey.equals(key2)){
		            	result.add(value2);
		            }else if(value2 instanceof Map){
		            	getNameValue((Map)value2, strKey, result);
		            }else if(value2 instanceof List){
		            	if(SysUtility.isNotEmpty(result)){
				    		return;//最外一层取一次
				    	}
		            	List list2 = (List)value2;
		            	for (int i = 0; i < list2.size(); i++) {
		            		getNameValue((Map)list2.get(i), strKey, result);
						}
		            }
		        }
		    }else if(value instanceof List){
		    	if(SysUtility.isNotEmpty(result)){
		    		return;//最外一层取一次
		    	}
		    	List list = (List) value;
				for(int i = 0 ; i < list.size() ; i++){
					getNameValue((Map)list.get(i), strKey, result);
				}
		    }
		}
	}
	
	public static int MathRandom(int count){
		if(SysUtility.isEmpty(count) || count <= 0){
			return 0;
		}
		
		return (int)(Math.ceil(Math.random() * count));
	}
	
	private static long loadCacheCount = 300000;//5分钟
	private static long lasttime = System.currentTimeMillis();
	public static boolean OutDate5Minute(){
		if(System.currentTimeMillis() - lasttime > loadCacheCount){
			lasttime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	private static long loadCacheCount2 = 5*1000;//5秒
	private static long lasttime2 = System.currentTimeMillis();
	public static boolean OutDate5Second(){
		if(System.currentTimeMillis() - lasttime2 > loadCacheCount2){
			lasttime2 = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public static void OutDate5MinuteReset(Object obj){
		if(OutDate5Minute()){
			if(obj instanceof Map){
				Map map = (Map)obj;
				map.clear();
			}else if(obj instanceof List){
				List list = (List)obj;
				list = new ArrayList();
			}else if(obj instanceof String){
				String str = (String)obj;
				str = "";
			}else{
				obj = null;
			}
		}
	}
	
	public static String getIdsByDatas(Datas datas,String tableName,String ColumnName) throws LegendException{
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < datas.GetTableRows(tableName); i++) {
			String id = datas.GetTableValue(tableName, ColumnName, i);
			ids.append(id).append(",");
		}
		return ids.toString();
	}
	
	public static String getIdsByLists(List list,String ColumnName){
		if(SysUtility.isEmpty(list)){
			return "";
		}
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			ids.append(map.get(ColumnName)).append(",");
		}
		return ids.toString();
	}
	
	public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
        	LogUtil.printLog(e.getMessage(), Level.ERROR);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LogUtil.printLog(e.getMessage(), Level.ERROR);
            }
        }
        return true;
    }
	
	public static boolean isHostReachable(String host, int timeOut) {
        try {
        	InetAddress inet = InetAddress.getByName(host);
        	boolean rt = inet.isReachable(timeOut);
            return rt;
        } catch (UnknownHostException e) {
        	LogUtil.printLog(e.getMessage(), Level.ERROR);
        } catch (IOException e) {
        	LogUtil.printLog(e.getMessage(), Level.ERROR);
        }
        return false;
    }
	
	public static String MethodInvokeCore(String MethodInvoke,Class[] objClass,Object[] objs)throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		String rt = "";
		if(SysUtility.isEmpty(MethodInvoke) || MethodInvoke.indexOf(".") < 0){
			return rt;
		}
		String className = MethodInvoke.substring(0, MethodInvoke.lastIndexOf("."));
		String methodName = MethodInvoke.substring(MethodInvoke.lastIndexOf(".")+1);
		Class cController = Class.forName(className);
		
		Method[] methods = cController.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				try {
					Method method = cController.getMethod(methodName, objClass);
					Object oController = cController.newInstance();
					Object obj = method.invoke(oController, objs);
					rt = (String)obj;
				} catch (Exception e) {
					LogUtil.printLog("MethodInvoke Error："+MethodInvoke+" "+e.getMessage(), Level.ERROR);
				}
			}
		}
		return rt;
	}
	
	public static Map<String, Object> hashtable2hashMap(Hashtable<String, Object> EnvDatas) {
		Map<String, Object> postParam = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = EnvDatas.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if(entry.getValue() instanceof String){
				postParam.put(entry.getKey(), (String)entry.getValue());
			}
		}
		return postParam;
	}
	
	public static List ToTree(List data, String idfield,String pidfield) {
		List rt = new ArrayList();
		HashMap rows = new HashMap();
		for (int i = 0; i < data.size(); i++) {
			HashMap row = (HashMap)data.get(i);
			HashMap item = new HashMap();
			Set mapSet = row.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
			    Entry entry = (Entry)it.next();
			    String key = entry.getKey()+"";
			    Object value = entry.getValue();
			    item.put(key, value);
			}
			if (row.containsKey(pidfield)) {
				String pid = row.get(pidfield)+"";
				if (rows.containsKey(pid)) {
					HashMap p = (HashMap)rows.get(pid);
					List children = null;
					if (p != null) {
						if (!p.containsKey("children")) {
							children = new ArrayList();
							p.put("children", children);
						} else {
							children = (List)p.get("children");
						}
					}
					if (children != null) {
						children.add(item);
					}
				}
				else{
					rt.add(item);
				}
			} else {
				rt.add(item);
			}
			rows.put(row.get(idfield)+"", item);
		}
		rows = null;

		return rt;
	}
	public static List ToTree(List data, String idfield,String pidfield1,String pidfield2) {
		List rt = new ArrayList();
		HashMap rows = new HashMap();
		for (int i = 0; i < data.size(); i++) {
			HashMap row = (HashMap)data.get(i);
			HashMap item = new HashMap();
			Set mapSet = row.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
			    Entry entry = (Entry)it.next();
			    String key = (String)entry.getKey();
			    Object value = entry.getValue();
			    item.put(key, value);
			}
			if (row.containsKey(pidfield1)) {
				String pid = (String)row.get(pidfield1);
				if (rows.containsKey(pid)) {
					HashMap p = (HashMap)rows.get(pid);
					List children = null;
					if (p != null) {
						if (!p.containsKey("children")) {
							if (row.containsKey(pidfield2)) {
								String pid1 = (String)row.get(pidfield2);
								if (rows.containsKey(pid1)) {
									HashMap p1 = (HashMap)rows.get(pid1);
									List childrens = null;
									if (p1 != null) {
										if (!p1.containsKey("children")) {
											childrens = new ArrayList();
											p1.put("children", childrens);
										} else {
											childrens = (List)p1.get("children");
										}
									}
									if (childrens != null) {
										childrens.add(item);
									}
								}
							} 
							children = new ArrayList();
							p.put("children", children);
						} else {
							children = (List)p.get("children");
						}
					}
					if (children != null) {
						children.add(item);
					}
				}
				else{
					rt.add(item);
				}
			} else {
				rt.add(item);
			}
			rows.put(row.get(idfield), item);
		}
		rows = null;
		return rt;
	}
	
	public static String getWhereIns(String indxs, String FieldName) {
		String[] ids = indxs.split(",");
		
		StringBuffer condition = new StringBuffer();
		condition.append(" "+FieldName+" in (");
		for (int i = 0; i < ids.length; i++) {
			if(i == 0) {
				condition.append("'"+ids[i]+"'");
			}else {
				condition.append(",'"+ids[i]+"'");
			}
		}
		condition.append(")");
		return condition.toString();
	}
	public static String getWhereIns(Datas datas, String FieldName) throws LegendException {
		StringBuffer condition = new StringBuffer();
		condition.append(" "+FieldName+" in (");
		for (int i = 0; i < datas.GetTableRows("rows"); i++) {
			String indx = datas.GetTableValue("rows", FieldName, i);
			if(i == 0) {
				condition.append("'"+indx+"'");
			}else {
				condition.append(",'"+indx+"'");
			}
		}
		condition.append(")");
		return condition.toString();
	}
	public static String getJoinString(Datas datas, String FieldName) throws LegendException {
		StringBuffer condition = new StringBuffer();
		
		for (int i = 0; i < datas.GetTableRows("rows"); i++) {
			String indx = datas.GetTableValue("rows", FieldName, i);
			if(i == 0) {
				condition.append(indx);
			}else {
				condition.append(","+indx);
			}
		}
		return condition.toString();
	}
    
    
	
	public static JSONObject JSONObjectToLowerCase(JSONObject obj){
    	JSONObject row = new JSONObject();
		if(SysUtility.isEmpty(obj)){
			return row;
		}
		
		try {
			for (Iterator<?> keys = obj.keys(); keys.hasNext();) {
				String key = keys.next().toString();
				Object value = (Object)obj.get(key);
				if(value instanceof String){
					row.put(key.toLowerCase(), value);
				}else if(value instanceof JSONObject){
					row.put(key.toLowerCase(), JSONObjectToLowerCase((JSONObject)value));
				}else if(value instanceof JSONArray){
					row.put(key.toLowerCase(), JSONArrayToLowerCase((JSONArray)value));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (LegendException e) {
			e.printStackTrace();
		}
		return row;
	}
	
	public static JSONArray JSONArrayToLowerCase(JSONArray array) throws LegendException{
		JSONArray rows = new JSONArray();
		if(SysUtility.isEmpty(array)){
    		return rows;
    	}
		try {
			for(int i = 0 ; i < array.length();i++){
				JSONObject row = array.getJSONObject(i);
				Iterator<?> keys = row.keys();
				JSONObject temp = new JSONObject();
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					Object value = row.get(key);
					if(value instanceof String){
						temp.put(key.toLowerCase(), value);
					}else if(value instanceof JSONObject){
						temp.put(key.toLowerCase(), JSONObjectToUpperCase((JSONObject)value));
					}else if(value instanceof JSONArray){
						temp.put(key.toLowerCase(), JSONArrayToUpperCase((JSONArray)value));
					}
	    		}
	    		rows.put(temp);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rows;
	}
	
	
    public static JSONObject JSONObjectToUpperCase(JSONObject obj){
    	JSONObject row = new JSONObject();
		if(SysUtility.isEmpty(obj)){
			return row;
		}
		
		try {
			for (Iterator<?> keys = obj.keys(); keys.hasNext();) {
				String key = keys.next().toString();
				Object value = (Object)obj.get(key);
				if(value instanceof String){
					row.put(key.toUpperCase(), value);
				}else if(value instanceof JSONObject){
					row.put(key.toUpperCase(), JSONObjectToUpperCase((JSONObject)value));
				}else if(value instanceof JSONArray){
					row.put(key.toUpperCase(), JSONArrayToUpperCase((JSONArray)value));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (LegendException e) {
			e.printStackTrace();
		}
		return row;
	}
	
	public static JSONArray JSONArrayToUpperCase(JSONArray array) throws LegendException{
		JSONArray rows = new JSONArray();
		if(SysUtility.isEmpty(array)){
    		return rows;
    	}
		try {
			for(int i = 0 ; i < array.length();i++){
				JSONObject row = array.getJSONObject(i);
				Iterator<?> keys = row.keys();
				JSONObject temp = new JSONObject();
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					Object value = row.get(key);
					if(value instanceof String){
						temp.put(key.toUpperCase(), value);
					}else if(value instanceof JSONObject){
						temp.put(key.toUpperCase(), JSONObjectToUpperCase((JSONObject)value));
					}else if(value instanceof JSONArray){
						temp.put(key.toUpperCase(), JSONArrayToUpperCase((JSONArray)value));
					}
	    		}
	    		rows.put(temp);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rows;
	}

	@Autowired
	private ConnectionUtility tempConnUtility;
	private static ConnectionUtility connUtility;

	@Value("${spring.datasource.driver-class-name}")
	private String tempdriverclassname;
	private static String driverclassname;

	@PostConstruct
	public void init() {
		connUtility = tempConnUtility;
		driverclassname = tempdriverclassname;
	}



	public static String getHostIp(){
		try{
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()){
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()){
					InetAddress ip = (InetAddress) addresses.nextElement();
					if (ip != null
							&& ip instanceof Inet4Address
							&& !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
							&& ip.getHostAddress().indexOf(":")==-1){
						return ip.getHostAddress();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
