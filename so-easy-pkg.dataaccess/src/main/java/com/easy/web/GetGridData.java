package com.easy.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.constants.Constants;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLHolder;
import com.easy.query.SQLMap;
import com.easy.query.SQLParser;
import com.easy.query.SimpleParamSetter;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class GetGridData extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	protected void DoCommand() throws Exception{
		IDataAccess DataAccess = getDataAccess();
		HttpServletRequest Request = getRequest();
		
		try {
			String CommandData = Request.getParameter("CommandData");
			JSONObject Setting = new JSONObject(CommandData);
			String DataName = Setting.getString("DataName");
			int pagesize = Integer.parseInt(Setting.getString("PageSize"));
			int currentpage = Integer.parseInt(Setting.getString("Page")) - 1;
			String orderData="";
			if(Setting.has("orderData")){
				orderData=Setting.getString("orderData");
			}
			JSONObject params = null;
			String searchTable = "";
			if(Setting.has("QueryData")){
				String query = Setting.getString("QueryData");
				if (SysUtility.isNotEmpty(query)){
					try {
						params = new JSONObject(query);
						Iterator it = params.keys();
			  			if(it.hasNext()){
			  				searchTable = it.next().toString();
			  			}
					} catch (JSONException e) {
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					}
				}
			}
			String rtdata = "";
			
			String SQL = SQLMap.getSelect(DataName);
			if(SysUtility.isNotEmpty(SQL)){
				if(params == null){
					params = new JSONObject();
					searchTable = "searchTable";
					params.put(searchTable, new JSONArray().put(new JSONObject()));
				}
				//自动填充缓存的用户信息
				String orgNo = SysUtility.getCurrentPartId();
				String deptId = SysUtility.getCurrentDeptId();
				String userIndx = SysUtility.getCurrentUserIndx();
				
				JSONObject temp = (JSONObject)((JSONArray)params.get(searchTable)).get(0);
				if(SysUtility.isNotEmpty(orgNo)){
					temp.put("ORG_NO", orgNo);
				}
				if(SysUtility.isNotEmpty(deptId)){
					temp.put("DEPT_ID", deptId);
				}
				if(SysUtility.isNotEmpty(userIndx)){
					temp.put("CREATOR", userIndx);
				}
				SQLHolder holder = SQLParser.parse(SQL, temp);
				rtdata = SQLExecUtils.query4JSONObject(DataAccess.GetActiveCN(), DataName, 
						holder.getSql(), new SimpleParamSetter(holder.getParamList()),currentpage, pagesize).toString();
			}else{
				String condition = "";
				if(SysUtility.isNotEmpty(params)){
					Iterator<?> keys = params.keys();
					if(keys.hasNext()){
						String key = keys.next().toString();
						JSONArray rows = params.getJSONArray(key);
						if(rows.length() > 0){
							condition = GetConditionString(rows.getJSONObject(0),false);
						}
					}
				}
				
				String top="";
				if (SysUtility.IsSQLServerDB()){
					top = "top 10000000";
				}
				SQL = "Select "+top+" * from " + DataName + (condition.length() > 0 ? " Where " + condition : "");
				if(orderData.length()>0){
					SQL += " order by "+orderData+"";
				}
				if(pagesize > 0){
					rtdata = DataAccess.GetTable(DataName, SQL, currentpage, pagesize);
				}else{
					rtdata = DataAccess.GetTable(DataName, SQL);
				}
			}
			ReturnMessage(true, "", "", rtdata, "","","");
		} catch (Exception e) {
			LogUtil.printLog(getRequest().getRequestURI()+":"+e.getMessage(), Level.ERROR);
			ReturnMessage(false,"Error:"+e.getMessage(),"","","");
		}
	}
}
