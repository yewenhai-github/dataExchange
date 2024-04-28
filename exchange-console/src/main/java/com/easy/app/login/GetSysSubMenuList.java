package com.easy.app.login;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/auth/GetSysSubMenuList")
public class GetSysSubMenuList extends MainServlet{
	public void DoCommand() throws Exception{
		String rtdata = "";  	
    	//String CurrentUserId = "";
    	//String CurrentCompanyID = "";
		String ParentMenuId = "";
    	try {
    	//当ParentMenuId=-1的时候，是个人办公，需要查出自己权限所有S_SYSTEM_SUBMENU
    		ParentMenuId = getRequest().getParameter("ParentMenuId");
	    	if("-1".equals(ParentMenuId)){
	    		StringBuffer sql1 = new StringBuffer();
	    		sql1.append("select INDX ");
	    		sql1.append(" from s_auth_menu  M ");
	    		sql1.append("WHERE M.IS_ENABLED = '1' ");
	    		sql1.append("  AND M.indx IN (SELECT DISTINCT SM.PERMID FROM s_auth_user S, s_auth_user_role SP, s_auth_perm_role SM WHERE S.INDX = SP.USERID AND SP.ROLEID = SM.ROLEID AND SM.TYPE=1 AND S.IS_ENABLED = '1' AND S.INDX = '"+SysUtility.getCurrentUserIndx()+"')  ");
	    		sql1.append("order by M.order_num ");
	    		Datas data = getDataAccess().GetTableDatas("rows", sql1.toString());
	    		String a="";
	    		for(int i=0;i<data.GetTableRows("rows");i++){
	    			if(i==data.GetTableRows("rows")-1){
	    				a+=data.GetTableValue("rows", "INDX", i);
	    			}else{
	    				a+=data.GetTableValue("rows", "INDX", i)+",";
	    			}
	    		};
	    		StringBuffer sqlS=new StringBuffer();
	    		sqlS.append("select M.INDX,M.PARENTID,M.MENU_TEXT,M.ICON,M.URL");
	    		sqlS.append(" from s_auth_submenu  M ");
	    		sqlS.append("WHERE M.IS_ENABLED = '1' ");
	    		sqlS.append("  and TOP_MENU_ID in (" + a + ")");
	    		sqlS.append("  AND M.indx IN (SELECT DISTINCT SM.PERMID FROM s_auth_user S, s_auth_user_role SP, s_auth_perm_role SM WHERE S.INDX = SP.USERID AND SP.ROLEID = SM.ROLEID AND SM.TYPE=2 AND S.IS_ENABLED = '1' AND S.INDX = '"+SysUtility.getCurrentUserIndx()+"')  ");
	    		sqlS.append("order by M.TLEVEL,M.PARENTID,M.TOP_MENU_ID,M.ORDER_NUM");
		    	JSONObject treedata1 = getDataAccess().GetTableJSON("Tree", sqlS.toString());
		    	JSONArray tree1 = getDataAccess().ToTree(treedata1.getJSONArray("Tree"),"INDX","PARENTID");
		    	rtdata = tree1.toString();
	    	}else{
	    		StringBuffer sql = new StringBuffer();
	    		sql.append("select M.INDX,M.PARENTID,M.MENU_TEXT,M.ICON,M.URL");
	    		sql.append(" from S_AUTH_SUBMENU  M ");
	    		sql.append("WHERE M.IS_ENABLED = '1' ");
	    		sql.append("  and TOP_MENU_ID =" + ParentMenuId + "");
	    		sql.append("  AND M.indx IN (SELECT DISTINCT SM.PERMID FROM s_auth_user S, s_auth_user_ROLE SP, s_auth_perm_role SM WHERE S.INDX = SP.USERID AND SP.ROLEID = SM.ROLEID AND SM.TYPE=2 AND S.IS_ENABLED = '1' AND S.INDX = '"+SysUtility.getCurrentUserIndx()+"')  ");
	    		sql.append("order by M.TLEVEL,M.PARENTID,M.ORDER_NUM");
		    	JSONObject treedata = getDataAccess().GetTableJSON("Tree", sql.toString());
		    	JSONArray tree = getDataAccess().ToTree(treedata.getJSONArray("Tree"),"INDX","PARENTID");
		    	rtdata = tree.toString();
	    	}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
    	ReturnMessage(true, "", "", rtdata);
	}
}
