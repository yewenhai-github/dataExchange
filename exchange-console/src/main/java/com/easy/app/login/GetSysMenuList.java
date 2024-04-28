package com.easy.app.login;
import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/auth/getSysMenuList")
public class GetSysMenuList extends MainServlet{
	public void DoCommand() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select M.indx,M.menu_text,M.menu_icon ");
		sql.append(" from S_AUTH_MENU  M ");
		sql.append("WHERE M.IS_ENABLED = '1' ");
		sql.append("  AND M.indx IN (SELECT DISTINCT SM.PERMID FROM s_auth_user S, s_auth_user_role SP, s_auth_perm_role SM WHERE S.INDX = SP.USERID AND SP.ROLEID = SM.ROLEID AND SM.TYPE=1 AND S.IS_ENABLED = '1' AND S.INDX = '"+SysUtility.getCurrentUserIndx()+"')  ");
		sql.append("order by M.order_num ");
		JSONObject data = getDataAccess().GetTableJSON("rows",sql.toString());
	
		//添加固定菜单：首页、个人办公
		JSONArray rows = new JSONArray();
		JSONObject row1 = new JSONObject(); 
		row1.put("INDX", "-2");
		row1.put("MENU_TEXT", "首页");
		row1.put("MENU_ICON", "&#xe696;");
		rows.put(row1);
		JSONObject row2 = new JSONObject(); 
		row2.put("INDX", "-1");
		row2.put("MENU_TEXT", "个人办公");
		row2.put("MENU_ICON", "&#xe696;");
		rows.put(row2);
		JSONArray json = data.getJSONArray("rows");
		for (int i = 0; i < json.length(); i++) {
			rows.put(json.get(i));
		}
		data.put("rows", rows);
		
		ReturnMessage(true, "", "", data.toString());
	}
}
