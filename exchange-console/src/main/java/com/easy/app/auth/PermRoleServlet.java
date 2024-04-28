package com.easy.app.auth;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/app/user/getsysrole",
		   				   "/app/user/getsysrolecheck",
						   "/app/user/getsysallmenuselect",
						   "/app/user/savepermrole"})
public class PermRoleServlet extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		String servletPath = getRequest().getServletPath();
		if("/app/user/getsysrole".equals(servletPath)) {
			getsysrole();
		}else if("/app/user/getsysrolecheck".equals(servletPath)) {
			getsysrolecheck();
		}else if("/app/user/getsysallmenuselect".equals(servletPath)) {
			getsysallmenuselect();
		}else if("/app/user/savepermrole".equals(servletPath)) {
			savepermrole();
		}
	}
	
	public void getsysrole() throws Exception{
		Datas datas = GetDatas("getsysrole", "rows", true);
		ReturnWriter(datas.get("rows").toString());
	}
	
	public void getsysrolecheck() throws Exception{
		AddToSearchTable("roleid", GetEnvDatas("node"));
		Datas datas = GetDatas("getsysrolecheck", "rows", true);
		ReturnWriter(datas.get("rows").toString());
	}
	
	public void getsysallmenuselect() throws Exception{
		Datas datas = GetDatas("getcomboboxmenulist");
		JSONArray rows = (JSONArray)datas.get("rows");
		List list = SysUtility.JSONArrayToList(rows);
		List rtList = SysUtility.ToTree(list, "group_indx","group_parentid");
		ReturnWriter(SysUtility.ListToJSONArray(rtList).toString());
	}
	
	public void savepermrole() throws Exception{
		String indx = getRequest().getParameter("roleId");
		String permid = getRequest().getParameter("permids");
		permid = permid.substring(0, permid.length() - 1);
		String permids[] = permid.split(",");
		
		getDataAccess().ExecSQL("delete from s_auth_perm_role where roleid="+indx);
		for (int i = 0; i < permids.length; i++) {
			int pid = Integer.parseInt(permids[i]);
			JSONObject entity = new JSONObject();
			entity.put("permid", pid);
			entity.put("roleid", indx);
			getDataAccess().Insert("s_auth_perm_role", entity);
		}
		ReturnMessage(true, "保存成功!");
	}
	
}
