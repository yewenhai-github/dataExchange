package com.easy.app.login;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;
import com.easy.utility.LogUtil;

@WebServlet("/auth/GetSysAllMenuSelect")
public class GetSysAllMenuSelect extends MainServlet{
	public void DoCommand() throws Exception{
		String rtdata = "";  	
    	try {
    		String sql=SQLMap.getSelect("GetSysAllMenuListSelect"); 
	    	JSONObject treedata = getDataAccess().GetTableJSON("Tree", sql);
	    	JSONArray tree = getDataAccess().ToTree(treedata.getJSONArray("Tree"),"GROUP_INDX","GROUP_TOP_MENU_ID","GROUP_PARENTID");
	    	rtdata = tree.toString();
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		ReturnWriter(rtdata);
	}
}
