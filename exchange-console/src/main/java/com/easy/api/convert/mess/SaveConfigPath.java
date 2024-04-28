package com.easy.api.convert.mess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.api.convert.util.JSONEncodeUtil;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveConfigPath")
public class SaveConfigPath  extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		Connection ActiveCN = SysUtility.getCurrentConnection();
		PreparedStatement stmt = null;
		String str = "";
		int len = 0;
		try {
			stmt = ActiveCN.prepareStatement("select * from exs_convert_config_path where 1 <> 1 ");
			stmt.execute();
			ResultSetMetaData rmd = stmt.getMetaData();
			for (int i = 1; i <= rmd.getColumnCount(); i++) {
				str = "";
				len = rmd.getPrecision(i);
				JSONObject jsons = new JSONObject(getEnvDatas());
				JSONArray jsonarr = new JSONArray();;
				if(jsons.get("serviceJson") instanceof JSONArray) {
					jsonarr = jsons.getJSONArray("serviceJson");
				}else if(jsons.get("serviceJson") instanceof JSONObject) {
					jsonarr.put(new JSONObject(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString()))));
				}else if(jsons.get("serviceJson") instanceof Map) {
					jsonarr.put(new JSONObject(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString()))));
				}else if(jsons.get("serviceJson") instanceof String) {
					jsonarr.put(new JSONObject(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString()))));
				}
				
				JSONObject json = jsonarr.getJSONObject(0);//获取json数组中的第一项  
				if(json.has(rmd.getColumnName(i).toUpperCase())){
					str = (String)json.get(rmd.getColumnName(i).toUpperCase());
				}
				if(len>0 && SysUtility.isNotEmpty(str)){
					if(str.length()>len){
						ReturnMessage(false, "输入信息过长，保存失败！");	
					}
				}
			}
		} catch (SQLException e) {
			LogUtil.printLog("getTableColumnNames error!"+SysUtility.getStackTrace(e), Level.ERROR);
			ReturnMessage(false, "保存失败！");	
		} finally {
			SysUtility.closeStatement(stmt);
		}
		
		int Indx = 0;
		Datas formDatas = SessionManager.getFormDatas();
		if(SysUtility.isNotEmpty(formDatas) || formDatas.length() > 0) {
			JSONObject jsons = new JSONObject(getEnvDatas());
			if(jsons.get("serviceJson") instanceof JSONArray) {
				formDatas = new Datas(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.getJSONArray("serviceJson").get(0).toString())));
				SessionManager.setFormDatas(formDatas);
			}else if(jsons.get("serviceJson") instanceof JSONObject) {
				formDatas = new Datas(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString())));
				SessionManager.setFormDatas(formDatas);
			}else if(jsons.get("serviceJson") instanceof Map) {
				formDatas = new Datas(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString())));
				SessionManager.setFormDatas(formDatas);
			}else if(jsons.get("serviceJson") instanceof String) {
				formDatas = new Datas(StringEscapeUtils.unescapeJson(JSONEncodeUtil.getDecodeJSONStr2(jsons.get("serviceJson").toString())));
				SessionManager.setFormDatas(formDatas);
			}
		}
		JSONArray jsonarr = (JSONArray)formDatas.get("MESSDATA");
		JSONObject json = jsonarr.getJSONObject(0);//获取json数组中的第一项  
		if(SysUtility.isNotEmpty(json.getString("CONFIGNAME"))) {
			 
			if(json.getString("CONFIGNAME").indexOf("_")!=-1 || json.getString("CONFIGNAME").indexOf("\\")!=-1  || json.getString("CONFIGNAME").indexOf("&")!=-1) {
				ReturnMessage(false, "禁止使用特殊符号作为配置名！");	
				return;
			}
		}
		if(SysUtility.isEmpty(json.getString("INDX"))) {
			json.put("ORG_ID", SysUtility.getCurrentOrgId());
		}
		int currentUserIndx = Integer.valueOf(SysUtility.getCurrentUserIndx());
		List query4List = SQLExecUtils.query4List("SELECT * FROM s_auth_user WHERE INDX='"+currentUserIndx+"'");
		HashMap query4Datas = (HashMap) query4List.get(0);
		if(!SysUtility.isEmpty(json.get("INDX"))) {
			final String indx = json.getString("INDX");
			List query4List2 = SQLExecUtils.query4List("SELECT CONFIGNAME FROM exs_convert_config_path WHERE INDX=?",new Callback() {
				
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, indx);
				}
			});
			if(query4List2.size()>0) {
				Map pathMap = (HashMap)query4List2.get(0);
				if(!json.get("CONFIGNAME").equals(pathMap.get("CONFIGNAME"))) {
					getDataAccess().ExecSQL("insert into EXS_HANDLE_SENDER (INDX,MSG_NO,MSG_TYPE,ATTRIBUTE1) VALUES(SEQ_EXS_HANDLE_SENDER.NEXTVAL,'"+query4Datas.get("REGISTER_NO")+"','NEWFOLDER','"+json.getString("CONFIGNAME")+",SOURCE|"+json.getString("CONFIGNAME")+",ERROR|"+json.getString("CONFIGNAME")+",BACK|"+json.getString("CONFIGNAME")+",SUCCESS|"+json.getString("CONFIGNAME")+",SUCCESSBACK|"+json.getString("CONFIGNAME")+",SOURCEBACK')");

				}
			}else {
				getDataAccess().ExecSQL("insert into EXS_HANDLE_SENDER (INDX,MSG_NO,MSG_TYPE,ATTRIBUTE1) VALUES(SEQ_EXS_HANDLE_SENDER.NEXTVAL,'"+query4Datas.get("REGISTER_NO")+"','NEWFOLDER','"+json.getString("CONFIGNAME")+",SOURCE|"+json.getString("CONFIGNAME")+",ERROR|"+json.getString("CONFIGNAME")+",BACK|"+json.getString("CONFIGNAME")+",SUCCESS|"+json.getString("CONFIGNAME")+",SUCCESSBACK|"+json.getString("CONFIGNAME")+",SOURCEBACK')");
			}
		}else if(SysUtility.isEmpty(json.get("INDX"))){
			getDataAccess().ExecSQL("insert into EXS_HANDLE_SENDER (INDX,MSG_NO,MSG_TYPE,ATTRIBUTE1) VALUES(SEQ_EXS_HANDLE_SENDER.NEXTVAL,'"+query4Datas.get("REGISTER_NO")+"','NEWFOLDER','"+json.getString("CONFIGNAME")+",SOURCE|"+json.getString("CONFIGNAME")+",ERROR|"+json.getString("CONFIGNAME")+",BACK|"+json.getString("CONFIGNAME")+",SUCCESS|"+json.getString("CONFIGNAME")+",SUCCESSBACK|"+json.getString("CONFIGNAME")+",SOURCEBACK')");
		}
		
		Indx = SaveToDB("MESSDATA", "exs_convert_config_path");
		
		
		if (!SysUtility.isEmpty(Indx))
        {
        	ReturnMessage(true, "保存成功！");
        }
        else
        {
        	ReturnMessage(false, "保存失败！");	
        }
	}
	
}




