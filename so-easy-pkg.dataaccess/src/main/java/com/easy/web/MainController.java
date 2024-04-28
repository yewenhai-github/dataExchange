package com.easy.web;

import com.easy.access.*;
import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.query.*;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * so-easy private
 * 
 * @author yewenhai 2021-04-13
 * 
 * @version 7.0.0
 * 
 */
public class MainController {
	private static final long serialVersionUID = 1L;
	public boolean CheckLogin;
	public String LoginPage;
	public String StartPage;

	public void DoCommand(HttpServletRequest request, HttpServletResponse response){
		DoCommand(request, response, true);
	}

	public void DoCommand(HttpServletRequest request, HttpServletResponse response, boolean checkLogin){
		SetCheckLogin(checkLogin);
		try {
			Check(request, response);
		} catch (Exception e) {
			LogUtil.printLog("请求错误！"+SysUtility.getStackTrace(e), Level.ERROR);
		} finally {
			SysUtility.closeActiveCN(SysUtility.getCurrentConnection());
			SessionManager.destorySession();
		}
	}

	/**
	 * 检查登录信息并执行DoCommand()调用业务类处理业务逻辑
	 * @throws Exception
	 * */
	private void Check(HttpServletRequest request, HttpServletResponse response)throws Exception {
		SysUtility.BeginTrans(SessionManager.getDataAccess());
		Init(request, response);
		boolean cando = true;
		if (SessionManager.getCheckLogin()) {
			if (SysUtility.getCurrentUser() == null) {
				ReturnMessage(false, "请您重新登录！", SessionManager.getLoginPage(), "", "","","");
				cando = false;
			}
		}
		if (cando) {
			try {
				if(SysUtility.isNotEmpty(SessionManager.getDataAccess()) || SysUtility.getDBPoolClose()){
					LogUtil.printLog("当前线程类名："+request.getServletPath(), Level.INFO);
					DoCommand();//Business Implementation
				}else{
					ReturnMessage(false, "DataBase Access error！", "", "", "");
				}
			} catch (Exception e) {
				SysUtility.RoolbackTrans(SessionManager.getDataAccess());
				ReturnMessage(false, e.getMessage(), "", "", "");
				throw e;
			}
		}
		SysUtility.ComitTrans(SessionManager.getDataAccess());
	}

	/**
	 * 每次请求时初始化交互所需的内存对象
	 * @param request
	 * @param response
	 * @throws JSONException
	 * */
	private void Init(HttpServletRequest request, HttpServletResponse response)throws IOException ,LegendException, JSONException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Datas FormDatas = new Datas();
		if (request.getParameter("CommandData") != null && request.getParameter("CommandData") != ""){
			String commanddata = request.getParameter("CommandData");
			try {
				if(SysUtility.isNotEmpty(commanddata) && commanddata.equals(new String(commanddata.getBytes("ISO8859-1"), "ISO8859-1"))) {
					commanddata = new String(commanddata.getBytes("ISO-8859-1"),"UTF-8");
				}

				commanddata=commanddata.replaceAll("[\\t\\n\\r]", "");//将commanddata中的回车换行去除
				if(SysUtility.isNotEmpty(commanddata) && commanddata.trim().startsWith("{") && commanddata.trim().endsWith("}")){
					FormDatas = new Datas(commanddata);
				}
			} catch (JSONException e) {
				ReturnMessage(false, "Init Error,CommandData="+commanddata+";"+e.getMessage(), "", "", "");
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		//1. 解析request.getParameterNames()
		Hashtable<String, Object> EnvDatas = new Hashtable<String, Object>();
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()){
			String k = keys.nextElement();
			Object obj = request.getParameter(k);
			if(obj instanceof String){
				String str = (String)obj;
				if(SysUtility.isNotEmpty(str) && str.equals(new String(str.getBytes("ISO8859-1"), "ISO8859-1"))){
					str = new String(str.getBytes("ISO-8859-1"),"UTF-8");
				}
				EnvDatas.put(k, str);
			}else{
				EnvDatas.put(k, obj);
			}
		}
		//2. 解析request.getReader()    http发送大量数据时，如大图片，EnvDatas无法初始化参数处理
		if(SysUtility.isEmpty(EnvDatas)){
			BufferedReader reader = null;
			try {
				boolean isMultipart = ServletFileUpload.isMultipartContent(request);
				if (isMultipart == true) {
					//带文件上传的表单
				}else{
					//普通表单
					reader = request.getReader();//request.getInputStream();
					StringBuffer tempStr = new StringBuffer();
					String line = "";
					while ((line = reader.readLine()) != null){
						tempStr.append(line);
					}
					if(SysUtility.isNotEmpty(tempStr)){
						String[] tempStrs = tempStr.toString().split("\\&");
						for (int i = 0; i < tempStrs.length; i++) {
							String[] strs = tempStrs[i].split("\\=");
							if(2 == strs.length & SysUtility.isNotEmpty(strs[1])){
								strs[1] = java.net.URLDecoder.decode(strs[1]);
								EnvDatas.put(strs[0], strs[1]);
							}
						}
					}
				}
			} catch (Exception e) {
				LogUtil.printLog("request.getReader() Error:"+SysUtility.getStackTrace(e), Level.ERROR);
			} finally {
				SysUtility.closeBufferedReader(reader);
			}
		}
		//3. request.getInputStream()  TODO
		if((SysUtility.isEmpty(FormDatas)||FormDatas.length()==0) && SysUtility.isNotEmpty(EnvDatas)) {
			for(Iterator<String> iterator = EnvDatas.keySet().iterator(); iterator.hasNext();){
				String key = iterator.next();
				FormDatas.put(key, EnvDatas.get(key));
			}
		}

		if (SysUtility.IsSQLServerDB()){
			SessionManager.setDataAccess(new SqlDataAccess());
		}else if (SysUtility.IsOracleDB()){
			SessionManager.setDataAccess(new OraDataAccess());
		}else {
			SessionManager.setDataAccess(new MySqlDataAccess());
		}
		AppContext.setContextPort(request.getLocalPort()+"");
		AppContext.setServletPath(request.getServletPath());
		SessionManager.setRequest(request);
		SessionManager.setResponse(response);
		SessionManager.setCookies(request.getCookies());
		SessionManager.setSession(request.getSession());
		SessionManager.getSession().setMaxInactiveInterval(3600);
		SessionManager.setFormDatas(FormDatas);
		SessionManager.setEnvDatas(EnvDatas);
		SessionManager.setLoginPage(SysUtility.isEmpty(LoginPage) ? "~/Login.html": GetSetting("system", "LoginPage"));
		SessionManager.setStartPage(SysUtility.isEmpty(StartPage)? "~/Index.html" :GetSetting("system", "StartPage"));
		SessionManager.setAttribute(SysUtility.ActiveCN, SysUtility.getCurrentConnection());
		if (SysUtility.IsSQLServerDB()){
			SessionManager.setDataAccess(new SqlDataAccess());
		}else if (SysUtility.IsOracleDB()){
			SessionManager.setDataAccess(new OraDataAccess());
		}else {
			SessionManager.setDataAccess(new MySqlDataAccess());
		}
	}

	/**
	 * 业务类重载接口方法，所有业务逻辑在此处实现
	 * @throws LegendException,Exception
	 * */
	protected void DoCommand() throws LegendException, Exception {

	}
	
	/**
	 * 校验登录。
	 * */
	public void SetCheckLogin(boolean ck){
		CheckLogin = ck;
		SessionManager.setCheckLogin(ck);
	}
	
	public String ResolveClientUrl(String path){
		String rt = path;
		if(path.startsWith("~/")){
			rt = SessionManager.getRequest().getContextPath() + path.substring(1);
		}
		return rt;
	}

	public void ReturnMessage(Boolean isok, String msg) {
		ReturnMessage(isok, msg, "","", "","","");
	}

	public void ReturnMessage(Boolean isok, String msg, String address) {
		ReturnMessage(isok, msg, address,"", "","","");
	}
	
	public void ReturnMessage(Boolean isok, String msg, String address, String data) {
		ReturnMessage(isok, msg, address,data, "","","");
	}

	public void ReturnMessage(Boolean isok, String msg, String address, String data, String otherjson) {
		ReturnMessage(isok, msg, address,data, otherjson,"","");
	}
	
	public void ReturnMessage(Boolean isok, String msg, String address,
			String data, String otherjson,String reloadgrid) {
		ReturnMessage(isok, msg, address,data, otherjson,reloadgrid,"");
	}
	
	public void ReturnMessage(Boolean isok, String msg, String address,
			String data, String otherjson,String reloadgrid,String cleardata) {
		ReturnMessage(isok, msg, address, data, otherjson, reloadgrid, cleardata, false);
	}
	
	public void ReturnMessage(Boolean isok, String msg, String address,
			String data, String otherjson,String reloadgrid,String cleardata,boolean RetuenOri) {
		if (SysUtility.isEmpty(SessionManager.getResponse())) {
			return;
		}
		StringBuilder rtmsg = new StringBuilder();
		if (RetuenOri) {
			rtmsg.append(data);
		}else{
			rtmsg.append("{\"IsOk\":\"" + (isok ? "1" : "0") + "\"");
			if (msg != null && msg != "") {
				rtmsg.append(",\"ErrMessage\":\"");
				rtmsg.append(msg.replace("\"",	"\"\""));
				rtmsg.append("\"");
			}
			if (address != null && !address.equals("")) {
				rtmsg.append(",\"ReturnAddress\":\"");
				address = ResolveClientUrl(address);
				rtmsg.append(address.replace("\"", "\"\""));
				rtmsg.append("\"");

			}
			if (data != null && !data.equals("")) {
				rtmsg.append(",\"ReturnData\":\"");
				rtmsg.append(data.replace("\\", "\\\\").replace("\"", "\\\""));
				rtmsg.append("\"");

			}
			if (otherjson != null && otherjson != "") {
				rtmsg.append("," + otherjson);
			}
			if(reloadgrid != null && !reloadgrid.equals("")){
				rtmsg.append(",\"Grid\":\"" +reloadgrid + "\"");
			}
			if(cleardata != null && !cleardata.equals("")){
				rtmsg.append(",\"Clear\":\"" + cleardata + "\"");
			}
			rtmsg.append("}");
		}
		SessionManager.setAttribute("rtMsg", rtmsg);
		try {
			PrintWriter ResponseWriter = SessionManager.getResponse().getWriter();
			ResponseWriter.print(rtmsg);
			try {
				ResponseWriter.close();
			} catch (Exception e) {
				//
			} 
		} catch (Exception e) {
			LogUtil.printLog("PrintWriter error!"+SysUtility.getStackTrace(e), Level.ERROR);
		}
	}

	public void ReturnWriter(String data){
		ReturnMessage(null, null, null, data, null, null, null, true);
	}
	
	public void ReturnWriter(Boolean isok, String msg){
		ReturnMessage(isok, msg, "","", "","","");
	}
	
	public void ReturnWriter(Boolean isok, String msg,Datas datas){
		try {
			datas.put("IsOk", (isok ? "1" : "0"));
			datas.put("ErrMessage", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ReturnMessage(null, null, null, datas.toString(), null, null, null, true);
	}
	
	/**
	 * 获取配置文件值
	 * @param Section 配置文件名称，不包含后缀
	 * @param Name 配置文件中的key
	 * @return String 配置文件中的value
	 * **/
	public String GetSetting(String Section, String Name) {
		return SysUtility.GetSetting(Section, Name);
	}

	/**将JSONObject中的元素组装成SQL的条件语句，key-value：("id", "=A")、("id2", "=A2,=A3")、("id3", "!A3")
	 *@param data 待组装成SQL条件语句的元数据
	 *@param withfromto 是否包含以_from、_to为结尾的组装参数
	 *@return SQL的条件语句，如：(id='A') AND (id2='A2' OR id2='A3') AND  (id3 like 'A3')
	 * */
	public String GetConditionString(JSONObject data, boolean withfromto) throws LegendException{
		StringBuilder rt = new StringBuilder();
		StringBuilder ort = new StringBuilder();
		String lk = "";
		String olk = "";
		for (Iterator<?> keys = data.keys(); keys.hasNext();) {
			String key = keys.next().toString();
			String chk = "";
			String colname = key;
			if (withfromto) {
				if (key.toLowerCase().endsWith("_from")) {
					chk = "From";
					colname = key.substring(0, key.length() - 5);
				}
				if (key.toLowerCase().endsWith("_to")) {
					chk = "To";
					colname = key.substring(0, key.length() - 3);
				}
			}
			String val = "";
			try {
				val = data.getString(key);
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
			if (val != null && !val.equals("")) {
				val = val.replace("'", "''");
				if (chk == "From") {
					rt.append(lk);
					rt.append(colname);
					rt.append(" >= to_date('");
					rt.append(val);
					rt.append("','yyyy-MM-dd HH:mm:ss')");
					lk = " AND ";
				}
				if (chk == "To") {
					rt.append(lk);
					rt.append(colname);
					rt.append(" <= to_date('");
					rt.append(val);
					rt.append("','yyyy-MM-dd HH:mm:ss')");
					lk = " AND ";
				}
				if (chk.equals("")) {
					if (val.startsWith("!")) {
						ort.append(olk);
						ort.append(colname);
						ort.append(" like '");
						ort.append(val.substring(1));
						ort.append("'");
						olk = " OR ";
					} else {
						rt.append(lk);
						String[] vs = val.trim().replace("；", ";")
								.replace("，", ",").replace("‘", "'")
								.replace("'", "''").replace("\r\n", ",")
								.split(",");
						if (vs.length > 0) {
							//rt.append(lk);
							rt.append("(");
							lk = "";
							for (String v1 : vs) {
								String[] v1s = v1.split(";");
								for (String v : v1s) {
									rt.append(lk);

									if (v.startsWith("=")) {
										rt.append(colname);
										rt.append("=");
										rt.append("'" + v.substring(1) + "'");
									} else if (v.startsWith("#")) {
										rt.append(colname);
										rt.append("<>");
										rt.append("'" + v.substring(1) + "'");
									} else if (v.startsWith(">=")) {
										rt.append(colname);
										rt.append(">=");
										rt.append("'" + v.substring(2) + "'");
									} else if (v.startsWith(">")) {
										rt.append(colname);
										rt.append(">");
										rt.append("'" + v.substring(1) + "'");
									} else if (v.indexOf("<") > -1) {
										String[] vsp = v.split("<");
										rt.append("(");
										if (vsp[0] != "") {
											rt.append(colname);
											rt.append(">=");
											rt.append("'" + vsp[0] + "'");
										}
										if (vsp[0] != "" && vsp[1] != "") {
											rt.append(" AND ");
										}
										if (vsp[1] != "") {
											rt.append(colname);
											rt.append("<=");
											rt.append("'" + vsp[1] + "'");
										}
										rt.append(")");
									} else if (v == "''''") {
										rt.append(colname);
										rt.append(" IS NULL");
									} else if (v == "%") {
										rt.append(" NOT ");
										rt.append(colname);
										rt.append(" IS NULL");
									} else {
										rt.append(colname);
										rt.append(" LIKE ");
										rt.append("'" + v + "'");
									}
									lk = " AND ";
								}
								lk = " OR ";
							}
							rt.append(")");
							lk = " AND ";
						}

					}
				}
			}
		}
		if (ort.length() > 0) {
			if (rt.length() > 0) {
				rt.append(" AND (");
				rt.append(ort);
				rt.append(")");
			} else {
				rt.append(ort);
			}
		}
		return rt.toString();
	}
	
	public String GetConditionString(String value, JSONObject definedata) {
		StringBuilder rt = new StringBuilder();
		String lk = "";
		String colname = "";
		for (Iterator<?> keys = definedata.keys(); keys.hasNext();) {
			colname = keys.next().toString();
			String[] vs = value.trim().replace("；", ";").replace("，", ",")
					.replace("‘", "'").replace("'", "''").replace("\r\n", ",")
					.split(",");
			if (vs.length > 0) {
				rt.append(lk);
				rt.append("(");
				lk = "";
				for (String v1 : vs) {
					String[] v1s = v1.split(";");
					for (String v : v1s) {
						rt.append(lk);
						
						if (v.startsWith("=")) {
							rt.append(colname);
							rt.append("=");
							rt.append("'" + v.substring(1) + "'");
						} else if (v.startsWith("#")) {
							rt.append(colname);
							rt.append("<>");
							rt.append("'" + v.substring(1) + "'");
						} else if (v.startsWith(">=")) {
							rt.append(colname);
							rt.append(">=");
							rt.append("'" + v.substring(2) + "'");
						} else if (v.startsWith(">")) {
							rt.append(colname);
							rt.append(">");
							rt.append("'" + v.substring(1) + "'");
						} else if (v.indexOf("<") > -1) {
							String[] vsp = v.split("<");
							rt.append("(");
							if (vsp[0] != "") {
								rt.append(colname);
								rt.append(">=");
								rt.append("'" + vsp[0] + "'");
							}
							if (vsp[0] != "" && vsp[1] != "") {
								rt.append(" AND ");
							}
							if (vsp[1] != "") {
								rt.append(colname);
								rt.append("<=");
								rt.append("'" + vsp[1] + "'");
							}
							rt.append(")");
						} else if (v == "''''") {
							rt.append(colname);
							rt.append(" IS NULL");
						} else if (v == "%") {
							rt.append(" NOT ");
							rt.append(colname);
							rt.append(" IS NULL");
						} else {
							rt.append(colname);
							rt.append(" LIKE ");
							rt.append("'" + v + "'");
						}
						lk = " AND ";
					}
					lk = " OR ";
				}
				rt.append(")");
				lk = " OR ";
			}
		}
		return rt.toString();
	}

	/**按照指定SQL加载数据到数据池
	 * @param TableName 要加载的缓存表名
	 * @return void
	 * */
	public void InitFormData(String TableName, String SQL) throws LegendException{
		if(SysUtility.isEmpty(TableName) || SysUtility.isEmpty(SQL)){
    		return;
    	}
		ArrayList<String> parms = new ArrayList<String>();
		SQLProcer sp = new SQLProcer(SQL, parms, SessionManager.getEnvDatas(), SessionManager.getFormDatas());
		String[] p = new String[] {};
		p = parms.toArray(p);
		JSONObject T = SessionManager.getDataAccess().GetTableJSON(TableName, sp.SQL , p);
		try {
			SessionManager.getFormDatas().put(TableName, T.getJSONArray(TableName));
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
	}
	
	/**添加环境变量
	 * @param key 环境变量名
	 * @param Data 环境变量数据
	 * @return void
	 * */
	public void AddToEnvDatas(String key, Object Data) throws LegendException{
		if(SessionManager.getEnvDatas() == null || SysUtility.isEmpty(key)){
    		return;
    	}
    	SessionManager.getEnvDatas().put(key, Data);
    }
	
	public String GetToSearchTable(String key){
		try {
			String commandData = (String)SessionManager.getEnvDatas().get("CommandData");
			if(SysUtility.isEmpty(commandData)){
				return "";
			}
			JSONObject ParamDatas = new JSONObject(commandData);
			if(SysUtility.isEmpty(ParamDatas)){
				return "";
			}
			JSONArray SearchArray = (JSONArray)ParamDatas.get("SearchTable");
			if(SysUtility.isEmpty(SearchArray)){
				return "";
			}
			JSONObject SearchJson = (JSONObject)SearchArray.get(0);
			if(SysUtility.isEmpty(SearchJson)){
				return "";
			}
			return SysUtility.getJsonField(SearchJson, key);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	public void AddToSearchTable(String key, Object Data) throws LegendException, JSONException{
		String commandData = (String)SessionManager.getEnvDatas().get("CommandData");
		JSONObject ParamDatas = null;
		JSONObject SearchJson = null;
		try {
			if(SysUtility.isNotEmpty(commandData)){
				ParamDatas = new JSONObject(commandData);
				JSONArray SearchArray = new JSONArray();
				if(ParamDatas.has("SearchTable")){
					SearchArray = (JSONArray)ParamDatas.get("SearchTable");
				}else if(ParamDatas.has("SEARCHTABLE")){
					SearchArray = (JSONArray)ParamDatas.get("SEARCHTABLE");
					//将SEARCHTABLE 替换为：SearchTable
					ParamDatas.remove("SEARCHTABLE");
					ParamDatas.put("SearchTable", SearchArray);
				}else{
					SearchJson = new JSONObject();
					SearchArray = new JSONArray();
					SearchArray.put(SearchJson);
					
					ParamDatas.put("SearchTable", SearchArray);
				}
				if(SearchArray.length() > 0){
					SearchJson = (JSONObject)SearchArray.get(0);
				}
			}else{
				ParamDatas = new JSONObject();
				SearchJson = new JSONObject();
				JSONArray SearchArray = new JSONArray();
				SearchArray.put(SearchJson);
				
				ParamDatas.put("SearchTable", SearchArray);
			}
		} catch (JSONException e) {
			LogUtil.printLog("AddToSearchTable查询条件失败："+e.getMessage(), Level.ERROR);
		}
		if(SysUtility.isNotEmpty(SearchJson)){
			SearchJson.put(key, Data);
			SessionManager.getEnvDatas().put("CommandData", ParamDatas.toString());
		}
	}
	
	/**
	 * @param key
	 * @return Object
	 * */
	public Object GetEnvDatas(String key) throws LegendException{
		if(SysUtility.isEmpty(key) || SysUtility.isEmpty(SessionManager.getEnvDatas())){
    		return "";
    	}
    	return SessionManager.getEnvDatas().get(key);
    }
	
	/**
	 * @param TableName
	 * @param key
	 * @return Object
	 * */
	public Object GetCommandData(String TableName,String key) throws LegendException, JSONException{
		String rt = "";
		if(SysUtility.isEmpty(TableName) || SysUtility.isEmpty(key)){
    		return rt;
    	}
		JSONObject table;
		try {
			String commandData = (String)SessionManager.getEnvDatas().get("CommandData");
			JSONObject ParamDatas = new JSONObject(commandData);
			JSONArray jsonarray = (JSONArray)ParamDatas.get(TableName);
			table = (JSONObject)jsonarray.get(0);
			rt = (String)table.get(key);
		} catch (Exception e) {
			rt = "";
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
    	return rt;
    }
	
	/**
	 * @param TableName
	 * @return Object
	 * */
	public JSONArray GetCommandData(String TableName) throws LegendException, JSONException{
		JSONArray jsonarray = new JSONArray();
		if(SysUtility.isEmpty(TableName)){
    		return jsonarray;
    	}
		try {
			String commandData = (String)SessionManager.getEnvDatas().get("CommandData");
			JSONObject ParamDatas = new JSONObject(commandData);
			if(ParamDatas.has(TableName)){
				jsonarray = (JSONArray)ParamDatas.get(TableName);
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
    	return jsonarray;
    }
	
	/**将数据表添加到数据池中
	 * @param Data 添加的数据
	 * @return void
	 * */
    public void AddToDatas(String TableName,JSONArray Data) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(FormDatas == null || SysUtility.isEmpty(TableName)){
    		return;
    	}
    	TableName = SysUtility.ParseTableName(FormDatas, TableName);
    	if(FormDatas.has(TableName)){
			FormDatas.remove(TableName);
		}
    	try {
			FormDatas.put(TableName, Data);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
    /**从数据池中移除指定表
	 * @return void
	 * */
    public void RemoveDatas(String TableName) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(FormDatas == null || SysUtility.isEmpty(TableName)){
    		return;
    	}
    	TableName = SysUtility.ParseTableName(FormDatas, TableName);
		if(FormDatas.has(TableName)){
			FormDatas.remove(TableName);
		}
    }
	
    /**获取FormDatas的字段值
	 * @param TableField 表.字段代替分开的表字段参数
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public String GetDataValue(String TableField) throws LegendException{
    	if(SysUtility.isEmpty(TableField)){
    		return "";
    	}
    	String[] TF = TableField.split("\\.");
		if (TF.length > 1){
			return GetDataValue(TF[0], TF[1], 0);
		}
        return "";
    }
	
    /**获取FormDatas的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public String GetDataValue(String TableName, String FieldName) throws LegendException{
        return GetDataValue(TableName, FieldName, 0);
    }
    
    /**获取内存中FormDatas的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param RowIndex 数据所在行
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public String GetDataValue(String TableName, String FieldName,int RowIndex) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		String rt = "";
		if(FormDatas == null || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(FieldName) || RowIndex < 0
				 || !FormDatas.has(TableName)){
    		return rt;
    	}
        try {
        	TableName = SysUtility.ParseTableName(FormDatas, TableName);
        	if(!FormDatas.has(TableName) || FormDatas.get(TableName)==null || FormDatas.get(TableName) == null){
    			return "";
    		}
        	JSONArray rows = (JSONArray)FormDatas.get(TableName);
    		JSONObject row = rows.getJSONObject(RowIndex);
    		Iterator<?> keys = row.keys();
    		while(keys.hasNext()){
				String key = keys.next().toString();
				if(key.equalsIgnoreCase(FieldName)){
					FieldName = key;
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		rt = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(obj);
	            	}else{
	            		rt = obj.toString();
	            	}
				}
    		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	/**获取FormDatas的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param Filter 过滤条件，取得在条件内的数据，配置(列名=值)
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public String GetDataValue(String TableName, String FieldName, String Filter) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		String rt = "";
		if(FormDatas == null || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(FieldName) || SysUtility.isEmpty(Filter)
				 || !FormDatas.has(TableName)){
    		return rt;
    	}
        try {
        	TableName = SysUtility.ParseTableName(FormDatas, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(FormDatas.get(TableName)==null || FormDatas.get(TableName) == null || ft.length < 2){
    			return "";
    		}
        	JSONArray rows = (JSONArray)FormDatas.get(TableName);
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
	
	/**根据过滤条件查找数据行
	 * @param TableName 查找的内存表名
	 * @param Filter 查找条件，配置(列名=值)
	 * @return 找到的数据行
	 * */
    public JSONArray GetRowList(String TableName, String Filter) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	JSONArray rt = new JSONArray();
    	if(FormDatas == null || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(Filter) || !FormDatas.has(TableName)){
    		return rt;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(FormDatas, TableName);
			String[] ft = Filter.split("\\=");
			String filterKey = ft[0];
			String filterValue = ft[1];
			if(FormDatas.get(TableName)==null || FormDatas.get(TableName) == null || !FormDatas.has(TableName) || ft.length < 2){
				return rt;
			}
			JSONArray rows = (JSONArray)FormDatas.get(TableName);
			for(int i = 0 ; i < rows.length();i++){
				JSONObject row = rows.getJSONObject(i);
				Iterator<?> keys = row.keys();
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(filterKey)){
						filterKey = key;
						if(row.get(filterKey).equals(filterValue)){
							rt.put(row);
						}
					}
	    		}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
    }
    
    /**返回内存表的行数
	 * @param TableName 查找的内存表名
	 * @return 数据行数
	 * */
	public int GetTableRows(String TableName) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		int rt = 0;
		if(FormDatas == null || SysUtility.isEmpty(TableName) || !FormDatas.has(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray rows = FormDatas.getJSONArray(TableName);
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
	
	/**返回内存表的行数
	 * @param TableName 查找的内存表名
	 * @return 数据行数
	 * */
	public int GetTableRows(JSONObject obj,String TableName) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		int rt = 0;
		if(obj == null || SysUtility.isEmpty(TableName) || !FormDatas.has(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(obj, TableName);
			if(obj.get(TableName)!=null){
				JSONArray rows = obj.getJSONArray(TableName);
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
	
	/**JSONObject对象转List<Map>
	 * @param TableName 查找的表名
	 * @return List<Map>
	 * */
	public List<Map<String,Object>> JSONToList(String TableName,JSONObject datas) throws LegendException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(SysUtility.isEmpty(TableName) || null == datas){
    		return list;
    	}
		try {
			TableName = SysUtility.ParseTableName(datas, TableName);
			if(datas.get(TableName)!=null){
				JSONArray rows = datas.getJSONArray(TableName);
				if(rows != null){
					for (int i = 0; i < rows.length(); i++) {
						JSONObject row = rows.getJSONObject(i);
						Map<String,Object> rowMap = new HashMap<String,Object>();
				        Iterator<?> keys =row.keys();
				        while(keys.hasNext()){
				              String key = keys.next().toString();
				              Object value = row.get(key);
				              rowMap.put(key, value);
				        }
				        list.add(rowMap);
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}	
		return list;
	}
	
	/**将FormDatas中的数据转换为JSON字符串
	 * @return JSON字符串
	 * */
	public String TableToJSON(String tablename) throws LegendException{
		return TableToJSON(tablename, String.valueOf(GetTableRows(tablename)));		
	}
	
	/**将FormDatas中的数据转换为JSON字符串
	 * @param TableName 查找的内存表名
	 * @param rowcount 内存表总行数
	 * @return JSON字符串
	 * */
	public String TableToJSON(String TableName,String rowcount) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		String rt = "";
		if(null == FormDatas || SysUtility.isEmpty(TableName) || !FormDatas.has(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONObject t = new JSONObject();
				JSONArray rows = FormDatas.getJSONArray(TableName);
				if(rows != null){
					t.put(TableName, rows);
					rt = t.toString();
					rt = rt.substring(0, rt.length() - 1) + ",\"RowCount\":\"" + rowcount + "\"}";
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}		
		return rt;		
	}
	public  String MoreTableToJSON(String ALLTableName,int IsShowCount) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		String [] tableName= ALLTableName.split(","); 
		String rt="";
		JSONObject t = new JSONObject();
		try {
		for(int i=0;i<tableName.length;i++)
		{
			JSONArray rows = FormDatas.getJSONArray(tableName[i]);
			if(rows != null){
				t.put(tableName[i], rows);
				rt = t.toString();
				if(IsShowCount==0)
					rt= rt.substring(1, rt.length() - 1) + ",";
				//else
					//rt = rt.substring(0, rt.length() - 1) + ",\"RowCount\":\"" + String.valueOf(GetTableRows(tableName[i])) + "\"";
			}
		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}		
		return "{"+rt.substring(0,rt.length()-1)+"}";
	}
	
	/**保存数据到数据池中数据表的第0行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param value 保存数据的值
	 * @return void
	 * */
	public void SaveToTable(String TableName,String FieldName , String value)  throws LegendException{
		SaveToTable(TableName, FieldName, value, 0);
	}
	
	/**保存数据到数据池中数据表的指定行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param value 保存数据的值
	 * @param RowIndex 指定行
	 * @return void
	 * */
    public void SaveToTable(String TableName, String FieldName, String value, int RowIndex) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(null == FormDatas || SysUtility.isEmpty(TableName)|| SysUtility.isEmpty(FieldName)|| RowIndex < 0){
    		return;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray rows = FormDatas.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject r = rows.getJSONObject(RowIndex);
						r.put(FieldName, value);
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
    public void RemoveToTable(String TableName, String FieldName,int RowIndex) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(null == FormDatas || SysUtility.isEmpty(TableName)|| SysUtility.isEmpty(FieldName)|| RowIndex < 0){
    		return;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray rows = FormDatas.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject r = rows.getJSONObject(RowIndex);
						r.remove(FieldName);
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
	 * @param Filter 过滤条件
	 * @return void
	 * */
    public void SaveToTable(String TableName, String FieldName, String setValue, String Filter) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(null == FormDatas || SysUtility.isEmpty(TableName)|| SysUtility.isEmpty(FieldName)|| SysUtility.isEmpty(Filter)){
    		return;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(FormDatas, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(FormDatas.get(TableName)==null || FormDatas.get(TableName) == null || ft.length < 2){
    			return;
    		}
        	JSONArray rows = (JSONArray)FormDatas.get(TableName);
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
    
    /**将传入Datas中的数据转换为JSON字符串
	 * @param TableName 查找的表名
	 * @return JSON字符串
	 * */
	public String TableToJSON(String TableName,JSONObject datas) throws LegendException{
		Datas FormDatas = SessionManager.getFormDatas();
		String rt = "";
		if(null == datas || SysUtility.isEmpty(TableName) || !FormDatas.has(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(datas, TableName);
			if(datas.get(TableName)!=null){
				JSONObject t = new JSONObject();
				JSONArray rows = datas.getJSONArray(TableName);
				if(rows != null){
					t.put(TableName, rows);
					rt = t.toString();
					rt = rt.substring(0, rt.length() - 1) + ",\"RowCount\":\"" + rows.length() + "\"}";
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}		
		return rt;	
	}
	
    /**获取传入datas的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param RowIndex 数据所在行
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public String GetDataValue(JSONObject datas,String TableName,String FieldName,int RowIndex) throws LegendException{
		String rt = "";
		if(null == datas || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(FieldName) || RowIndex < 0){
    		return rt;
    	}
        try {
        	TableName = SysUtility.ParseTableName(datas, TableName);
        	if(datas.get(TableName)==null || datas.get(TableName) == null){
    			return "";
    		}
        	JSONArray rows = (JSONArray)datas.get(TableName);
    		JSONObject row = rows.getJSONObject(RowIndex);
    		Iterator<?> keys = row.keys();
    		while(keys.hasNext()){
				String key = keys.next().toString();
				if(key.equalsIgnoreCase(FieldName)){
					FieldName = key;
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		rt = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(obj);
	            	}else{
	            		rt = obj.toString();
	            	}
				}
    		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	/**保存数据到传入数据表datas的指定行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param value 保存数据的值
	 * @param RowIndex 指定行
	 * @return void
	 * */
	public void SaveToTable(JSONObject datas,String TableName, String FieldName, String value, int RowIndex) throws LegendException{
		if(null == datas || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(FieldName) || RowIndex < 0){
    		return;
    	}
		try {
    		TableName = SysUtility.ParseTableName(datas, TableName);
			if(datas.get(TableName)!=null){
				JSONArray rows = datas.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject row = rows.getJSONObject(RowIndex);
						Iterator<?> keys = row.keys();
						while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName)){
								FieldName = key;
								row.put(FieldName, value);
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
    
	/***
	 * 从datas中移除第RowIndex行名称为FieldName的列
	 * */
	public void RemoveToTable(JSONObject datas,String TableName, String FieldName, int RowIndex) throws LegendException{
		if(datas == null || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(FieldName) || RowIndex < 0){
    		return;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(datas, TableName);
			if(datas.get(TableName)!=null){
				JSONArray rows = datas.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject row = rows.getJSONObject(RowIndex);
						Iterator<?> keys = row.keys();
						while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName)){
								row.remove(FieldName);
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
	
    /**将数据池中的数据保存到数据库中，有主键的做更新，无主键的做插入
	 * @param TableName 保存数据的数据表
	 * @param DBTableName 数据库表名
	 * @return 第一条记录的主键
	 * */
    public int SaveToDB(String TableName,String DBTableName) throws LegendException{
		String rt = SaveToDB(TableName, DBTableName, SysUtility.KeyFieldDefault);
		if(SysUtility.isNumber(rt)) {
			Integer.parseInt(rt);
		}else if(SysUtility.isNotEmpty(rt)) {
			return 1;
		}
		return 0;
	}
	
    public String SaveToDB(String TableName,String DBTableName,String KeyField) throws LegendException{
    	return SaveToDB(TableName, DBTableName, KeyField, SysUtility.getCurrentConnection());
    }
    
    public String SaveToDB(String TableName,String DBTableName,String KeyField, Connection conn) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	String rt = "-1";
    	if(null == FormDatas || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)){
    		return String.valueOf(rt).toString();
    	}
		try {
			TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray rows = FormDatas.getJSONArray(TableName);
				if(rows != null && rows.length() > 0){
					DataAccess.Update(DBTableName, rows, KeyField, conn);
					JSONObject rtObj = rows.getJSONObject(0);
					if(rtObj.has(KeyField)){
						rt = rtObj.getString(KeyField);
					}else if(rtObj.has(KeyField.toUpperCase())){
						rt = rtObj.getString(KeyField.toUpperCase());
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		if(SysUtility.isEmpty(rt)){
			rt = "-1";
		}
		return String.valueOf(rt).toString();
	}
    
    /**将传入的数据保存到数据库中，有主键的做更新，无主键的做插入
	 * @param DBTableName 数据库表名
	 * @return 第一条记录的主键
	 * */
    public int SaveToDB(Object objs, String DBTableName) throws LegendException{
    	return SaveToDB(objs, DBTableName, SysUtility.KeyFieldDefault);
    }
 
    public int SaveToDB(Object objs, String DBTableName,String KeyField) throws LegendException{
    	return SaveToDB(objs, DBTableName, KeyField, SysUtility.getCurrentConnection());
    }
    
    public int SaveToDB(Object objs, String DBTableName,String KeyField, Connection conn) throws LegendException{
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	String rt = "-1";
    	if(objs == null || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)){
    		return Integer.valueOf(rt).intValue();
    	}
    	JSONArray datas = new JSONArray();
		if(objs instanceof JSONArray){
			datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			datas.put((JSONObject)objs);
		}
		try {
			DataAccess.Update(DBTableName, datas, KeyField, conn);
			JSONObject rtObj = datas.getJSONObject(0);
			if(rtObj.has(KeyField)){
				rt = rtObj.getString(KeyField);
			}else if(rtObj.has(KeyField.toUpperCase())){
				rt = rtObj.getString(KeyField.toUpperCase());
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		if(SysUtility.isEmpty(rt)){
			rt = "-1";
		}
		return Integer.valueOf(rt).intValue();
	}
    
    /**将数据池中的数据保存到数据库中，有主键的做更新，无主键的做插入
	 * @param TableName 保存数据的数据表
	 * @param DBTableName 数据库表名
	 * @param KeyField 主键
	 * @return 第一条记录的主键
	 * */
    public String InsertDB(String TableName,String DBTableName,String KeyField) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	return FormDatas.InsertDB(DataAccess, TableName, DBTableName, KeyField);
	}
    
    public String UpdateDB(String TableName,String DBTableName,String KeyField) throws LegendException{
    	return UpdateDB(TableName, DBTableName, KeyField, SysUtility.getCurrentConnection());
    }
    
    /**将数据池中的数据保存到数据库中，有主键的做更新，无主键的做插入
	 * @param TableName 保存数据的数据表
	 * @param DBTableName 数据库表名
	 * @param KeyField 主键
	 * @return 第一条记录的主键
	 * */
    public String UpdateDB(String TableName,String DBTableName,String KeyField, Connection conn) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	String rt = "-1";
    	if(null == FormDatas || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)){
    		return String.valueOf(rt).toString();
    	}
		try {
			TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray rows = FormDatas.getJSONArray(TableName);
				if(rows != null && rows.length() > 0){
					DataAccess.Update(DBTableName, rows, KeyField, conn);
					JSONObject rtObj = rows.getJSONObject(0);
					if(rtObj.has(KeyField)){
						rt = rtObj.getString(KeyField);
					}else if(rtObj.has(KeyField)){
						rt = rtObj.getString(KeyField);
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		if(SysUtility.isEmpty(rt)){
			rt = "-1";
		}
		return String.valueOf(rt).toString();
	}
    
    /**使用内存表删除数据
	 * @param TableName 缓存表名
	 * @param DBTableName 数据库表名
	 * @return void
	 * */
    public void DeleteDB(String TableName, String DBTableName) throws LegendException{
    	Datas FormDatas = SessionManager.getFormDatas();
    	if(FormDatas == null || SysUtility.isEmpty(TableName) || SysUtility.isEmpty(DBTableName)){
    		return;
    	}
    	try {
    		TableName = SysUtility.ParseTableName(FormDatas, TableName);
			if(FormDatas.get(TableName)!=null){
				JSONArray Datas = FormDatas.getJSONArray(TableName);
				if(Datas != null && Datas.length() > 0){
					DeleteDB(Datas, DBTableName);
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }

    /**将传入的数据做删除
	 * @param DBTableName 数据库表名
	 * @return void
	 * */
    public void DeleteDB(JSONArray datas, String DBTableName) throws LegendException{
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	if(SysUtility.isEmpty(DBTableName) || datas == null){
    		return;
    	}
    	DataAccess.Delete(DBTableName, datas);
    }
    
    public boolean ExecSQL(String SQL, Object parms) throws LegendException {
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	return DataAccess.ExecSQL(SQL, parms);
    }
    
    public boolean BeginTrans() throws LegendException{
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	return DataAccess.BeginTrans();
    }
    
    public boolean ComitTrans() throws LegendException{
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	return DataAccess.ComitTrans();
    }
    
    public boolean RoolbackTrans() throws LegendException{
    	IDataAccess DataAccess = SessionManager.getDataAccess();
    	return DataAccess.RoolbackTrans();
    }
    /*******************************后台分页查询入口*************************************************/
    public String TreeDefine(String Indx, String Caption, String Icon, String Command, String Parent){
    	String fmt="\"treedefine\":{\"Indx\":\"%s\",\"Caption\":\"%s\",\"Icon\":\"%s\",\"Url\":\"%s\",\"Parent\":\"%s\"}";
        return String.format(fmt, Indx, Caption, Icon, Command, Parent);
    }
    
  //EasyUi grid 重载
    public JSONObject GetReturnDatas(String ID,boolean IsEasyUi) throws LegendException{
    	return GetReturnDatas(ID, "rows",true);
    }
    public JSONObject GetReturnDatas(String ID) throws LegendException{
    	return GetReturnDatas(ID, "rows");
    }
  
    public JSONObject GetReturnDatas(String ID,String TableName) throws LegendException{
    	Datas datas = GetDatas(ID, TableName);
        return datas.getDataJSON();
    }
   //EasyUi grid 重载
    public JSONObject GetReturnDatas(String ID,String TableName,boolean IsEasyUi) throws LegendException{
    	Datas datas = GetDatas(ID, TableName,true);
        return datas.getDataJSON();
    }
    public JSONObject GetReturnDatas(String ID,String TableName,boolean IsEasyUic,Connection conn) throws LegendException{
    	Datas datas = GetDatas(ID, TableName,true,conn);
        return datas.getDataJSON();
    }
    
    public String GetComboboxDatas(String ID) throws LegendException{
    	return GetComboboxDatas(ID, "rows");
    } 
    
    public String GetComboboxDatas(String ID,String TableName) throws LegendException{
        return GetComboboxDatas(ID, TableName, SysUtility.getCurrentConnection());
    }
    public String GetComboboxDatas(String ID,String TableName,Connection conn) throws LegendException{
    	Datas datas = GetDatas(ID, TableName,true,conn);
    	JSONObject js = datas.getDataJSON();
    	String datavalue = js.toString();
		String ComboboxValue = datavalue.substring(8, datavalue.length()-1);
        return ComboboxValue;
    }
    
    public Datas GetDatas(String ID) throws LegendException{
    	return GetDatas(ID, "rows");
    }
  //EasyUi grid 重载
    public Datas GetDatas(String ID,String TableName) throws LegendException{
    	return GetDatas(ID, TableName,false);
    }

    public Datas GetDatas(String ID,String TableName,boolean IsEasyUi) throws LegendException{
    	Connection conn = SysUtility.getCurrentConnection();
    	return GetDatas(ID, TableName, IsEasyUi, conn);
    }
    
    public Datas GetDatas(String ID,String TableName,boolean IsEasyUi,Connection conn) throws LegendException{
    	return GetDatas(ID, TableName, IsEasyUi, conn, 0);
    }
    
    /***
     * @param BlobProcess: 1=返回的流经过base64加码、2=返回的流转utf-8字符串、3=返回的流转gbk字符串、其他值=返回的流转utf-8字符串
     * **/
    public Datas GetDatas(String ID,String TableName,boolean IsEasyUi,Connection conn,int BlobProcess) throws LegendException{
    	Hashtable<String, Object> EnvDatas = SessionManager.getEnvDatas();
    	HttpServletRequest Request = SessionManager.getRequest();
    	if(IsEasyUi){
    		AddEasyUIEnvDatasToSearchTable();
    	}
    	//添加session登录参数
		try {
			AddToSearchTable("ISROOT", SysUtility.getCurrentUserIsRoot());
			AddToSearchTable("CREATOR", SysUtility.getCurrentUserIndx());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    	String commandData = (String)EnvDatas.get("CommandData");
        JSONObject SearchJson = new JSONObject();
		try {
			if(SysUtility.isNotEmpty(commandData)){
				JSONObject ParamDatas = new JSONObject(commandData);
				JSONArray SearchArray = new JSONArray();
				if(ParamDatas.has("SearchTable")){
					SearchArray = (JSONArray)ParamDatas.get("SearchTable");
				}else if(ParamDatas.has("SEARCHTABLE")){
					SearchArray = (JSONArray)ParamDatas.get("SEARCHTABLE");
					//将SEARCHTABLE 替换为：SearchTable
					ParamDatas.remove("SEARCHTABLE");
					ParamDatas.put("SearchTable", SearchArray);
				}
				if(SearchArray.length() > 0){
					SearchJson = (JSONObject)SearchArray.get(0);
				}
				LogUtil.printLog("查询参数："+SearchJson, Level.INFO);
			}
		} catch (JSONException e) {
			LogUtil.printLog("获取SearchTable查询条件失败："+e.getMessage(), Level.ERROR);
		}
		
		String tpage = (String)EnvDatas.get("page");
		String trows = (String)EnvDatas.get("rows");
		String SQL = "";
		if("@".equals(ID.substring(0, 1))){
			SQL = ID.substring(1);
		}else{
			SQL = SQLMap.getSelect(ID);
		}
		if (Request.getParameter("OrderField") != null && !Request.getParameter("OrderField").isEmpty()){
			SQL="Select SQL_.* from ("+SQL+") SQL_ order by "+Request.getParameter("OrderField")+" "+Request.getParameter("Order");
		}else if (IsEasyUi && Request.getParameter("sort") != null && !Request.getParameter("sort").isEmpty() && Request.getParameter("order") != null && !Request.getParameter("order").isEmpty()){//easyui 版排序
			SQL="Select SQL_.* from ("+SQL+") SQL_ order by "+Request.getParameter("sort")+" "+Request.getParameter("order");
		}
		SQLHolder holder = SQLParser.parse(SQL, SearchJson);
		Datas datas = new Datas();
		if (SysUtility.isNotEmpty(Request.getParameter("IsAll"))){
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		}else if(SysUtility.isNotEmpty(tpage) && SysUtility.isNotEmpty(trows)){
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()),Integer.parseInt(tpage), Integer.parseInt(trows),IsEasyUi,BlobProcess);
		}else{
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		}
		return datas;
    } 
    
    public Datas GetDatasByMapParams(Connection conn,String ID,HashMap params,String TableName,boolean IsEasyUi) throws LegendException{
    	Hashtable<String, Object> EnvDatas = SessionManager.getEnvDatas();
    	HttpServletRequest Request = SessionManager.getRequest();
    	
    	String tpage = (String)EnvDatas.get("page");
		String trows = (String)EnvDatas.get("rows");
		String SQL = "";
		if("@".equals(ID.substring(0, 1))){
			SQL = ID.substring(1);
		}else{
			SQL = SQLMap.getSelect(ID);
		}
		if (Request.getParameter("OrderField") != null && !Request.getParameter("OrderField").isEmpty()){
			SQL="Select SQL_.* from ("+SQL+") SQL_ order by "+Request.getParameter("OrderField")+" "+Request.getParameter("Order");
		}else if (IsEasyUi && Request.getParameter("sort") != null && !Request.getParameter("sort").isEmpty() && Request.getParameter("order") != null && !Request.getParameter("order").isEmpty()){//easyui 版排序
			SQL="Select SQL_.* from ("+SQL+") SQL_ order by "+Request.getParameter("sort")+" "+Request.getParameter("order");
		}
		SQLHolder holder = SQLParser.parse(SQL, params);
		Datas datas = new Datas();
		if (SysUtility.isNotEmpty(Request.getParameter("IsAll"))){
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		}else if(SysUtility.isNotEmpty(tpage) && SysUtility.isNotEmpty(trows)){
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()),Integer.parseInt(tpage), Integer.parseInt(trows),IsEasyUi,0);
		}else{
			datas = SQLExecUtils.query4Datas(conn, TableName,holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		}
		return datas;
    }
    
    public void AddEasyUIEnvDatasToSearchTable() throws LegendException{
    	Hashtable<String, Object> EnvDatas = SessionManager.getEnvDatas();
    	try {
			Hashtable<String, Object> tempEnvDatas = (Hashtable<String, Object>) EnvDatas.clone();
			if(SysUtility.isNotEmpty(tempEnvDatas)){
				Set mapSet = tempEnvDatas.entrySet();
    			for (Iterator it = mapSet.iterator(); it.hasNext();) {
    				Entry entry = (Entry)it.next();
    		        String key = (String)entry.getKey();
    		        Object value = entry.getValue();
    		        if("sort".equals(key) || "order".equals(key) || "rows".equals(key) || "page".equals(key) || SysUtility.isEmpty(value)){
						continue;
					}
					AddToSearchTable(key, value);
    			}
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetDatas EasyUI 自动填充查询条件出错。", Level.ERROR);
		}
    }
    
    public JSONObject GetReturnDatasAllDB(String ID) throws LegendException, JSONException{
    	Datas datas = GetDatasAllDB(ID,GetToSearchTable("SCHEMA"));
    	JSONObject data = datas.getDataJSON();
    	if("true".equalsIgnoreCase(GetSetting("system", "MySqlColumnUpper")) && SysUtility.IsMySqlDB() && SysUtility.isNotEmpty(data) && data.has("rows")) {
    		data.put("rows", SysUtility.JSONArrayToUpperCase((JSONArray)data.get("rows")));
    	}
    	
    	return data;
    }
    
    public Datas GetDatasAllDB(String ID,String Schema) throws LegendException{
    	String TableName = "rows";
    	
    	List rtLst = SysUtility.GetProxoolCount(this, "proxool.properties");
		HashMap SchemaMap = SysUtility.GetProxoolSchema(this, "proxool.properties");
		
		/*************场景1.查询指定数据库*********************/
		for (int i = 0; i < rtLst.size(); i++) {
			String dbName = (String)rtLst.get(i);
			if(SysUtility.isNotEmpty(Schema) && Schema.equals(SchemaMap.get(dbName))){
				Connection conn = null;
    			String tempSchema = "";
				try {
					conn = SysUtility.getCurrentConnection(dbName);
					tempSchema = (String)SchemaMap.get(dbName);
					Datas tempDatas = GetDatas(ID, TableName, true, conn);
					for (int j = 0; j < tempDatas.GetTableRows(TableName); j++) {
						tempDatas.SetTableValue(TableName, "SCHEMA", tempSchema, j);
    				}
					return tempDatas;
				} catch (Exception e) {
					LogUtil.printLog("Schema="+tempSchema+" "+e.getMessage(), Level.ERROR);
				}finally {
					SysUtility.closeActiveCN(conn);
				}
			}
		}
		/*************场景2.SCHEMA不为空，但没有匹配的数据库*********************/
		if(SysUtility.isNotEmpty(Schema)){
			return new Datas();
		}
		
		/*************场景3.查询所有数据库*********************/
		Datas datas = null;
		try {
			datas = GetDatas(ID, TableName, true, SysUtility.getCurrentConnection());
		} catch (LegendException e) {
			LogUtil.printLog("GetDatas Error:"+e.getMessage(), Level.ERROR);
			datas = new Datas();
		}
		long total = 0L;
//		long page = 0L;
//		long records = 0L;
		for (int i = 0; i < rtLst.size(); i++) {
			String dbName = (String)rtLst.get(i);

//			if(SysUtility.isNotEmpty(SysUtility.getJsonField(datas, "page"))){
//				page += Long.valueOf(SysUtility.getJsonField(datas, "page"));
//			}
//			if(SysUtility.isNotEmpty(SysUtility.getJsonField(datas, "records"))){
//				records += Long.valueOf(SysUtility.getJsonField(datas, "records"));
//			}
			if("jdbc-0".equals(dbName)){
				for (int j = 0; j < datas.GetTableRows(TableName); j++) {
		    		datas.SetTableValue(TableName, "SCHEMA", SchemaMap.get(dbName), j);
				}
				if(SysUtility.isNotEmpty(SysUtility.getJsonField(datas, "total"))){
					total += Long.valueOf(SysUtility.getJsonField(datas, "total"));
				}
				continue;
			}
			Connection conn = null;
			String tempSchema = "";
			try {
				conn = SysUtility.getCurrentConnection(dbName);
				tempSchema = (String)SchemaMap.get(dbName);
				
				Datas tempDatas = GetDatas(ID, TableName, true, conn);
				for (int j = 0; j < tempDatas.GetTableRows(TableName); j++) {
					tempDatas.SetTableValue(TableName, "SCHEMA", tempSchema, j);
				}
				if(SysUtility.isNotEmpty(SysUtility.getJsonField(tempDatas, "total"))){
					total += Long.valueOf(SysUtility.getJsonField(tempDatas, "total"));
				}
				datas.AddAllDatas(tempDatas);
			} catch (Exception e) {
				LogUtil.printLog("Schema="+tempSchema+" "+e.getMessage(), Level.ERROR);
			}finally {
				SysUtility.closeActiveCN(conn);
			}
		}
		SysUtility.putJsonField(datas, "total", total);
//		SysUtility.putJsonField(datas, "page", page);
//		SysUtility.putJsonField(datas, "records", records);
		return datas;
    }
    
    public String ErrMessages;

	public boolean CheckReg(JSONObject FormDatas, JSONArray ckrows) {
		boolean rt = true;
		ErrMessages = "";
		try {
			for (int i = 0; i < ckrows.length(); i++) {
				JSONObject ckr = ckrows.getJSONObject(i);
				String tb = ckr.getString("CheckTable");
				JSONArray cktable = FormDatas.getJSONArray(tb);
				if (cktable.length() > 0) {
					String ckf = ckr.getString("CheckField");
					String ckregex = ckr.getString("CheckValue");
					String ckmsg = ckr.getString("CheckMessage");
					if (cktable.length() == 1) {
						String v = cktable.getJSONObject(0).getString(ckf);
						if (!CheckReg(v, ckregex)) {
							ErrMessages += ckmsg + "\r\n";
							rt = false;
						}
					} else {
						for (int c = 0; c < cktable.length(); c++) {
							String v = cktable.getJSONObject(c).getString(ckf);
							if (!CheckReg(v, ckregex)) {
								ErrMessages += "[" + tb + "]第["
										+ String.valueOf(c + 1) + "]行数据中"
										+ ckmsg + "\r\n";
								rt = false;
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			ErrMessages = "系统错误";
			e.printStackTrace();
		}
		return rt;
	}

	private boolean CheckReg(String v, String reg) {
		boolean rt = false;
		rt = Pattern.compile(reg).matcher(v).find();
		return rt;
	}
	
	
	public IDataAccess getDataAccess() {
		return SessionManager.getDataAccess();
	}

	public HttpServletRequest getRequest() {
		return SessionManager.getRequest();
	}

	public HttpServletResponse getResponse() {
		return SessionManager.getResponse();
	}

	public Cookie[] getCookies() {
		return SessionManager.getCookies();
	}

	public Hashtable<String, Object> getEnvDatas() {
		return SessionManager.getEnvDatas();
	}

	public Datas getFormDatas() {
		return SessionManager.getFormDatas();
	}

	public String getLoginPage() {
		return SessionManager.getLoginPage();
	}

	public String getStartPage() {
		return SessionManager.getStartPage();
	}

	public boolean getCheckLogin() {
		return SessionManager.getCheckLogin();
	}

	public boolean getIsPost() {
		return SessionManager.getIsPost();
	}
	
	public HttpSession getSession() {
		return SessionManager.getSession();
	}
	
}
