package com.easy.app.auth;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/app/user/getcomboboxmenulist",
						   "/app/user/getcomboboxorglist",
						   "/app/user/getcomboboxorglist2",
						   "/app/user/getcomboboxorg",
						   "/app/user/getcomboboxdept",
						   "/app/user/getcomboboxrole"})
public class ComboboxServlet extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		String servletPath = getRequest().getServletPath();
		if("/app/user/getcomboboxmenulist".equals(servletPath)) {
			getcomboboxmenulist();
		}else if("/app/user/getcomboboxorglist".equals(servletPath)) {
			getcomboboxorglist();
		}else if("/app/user/getcomboboxorglist2".equals(servletPath)) {
			getcomboboxorglist2();
		}else if("/app/user/getcomboboxorg".equals(servletPath)) {
			getcomboboxorg();
		}else if("/app/user/getcomboboxdept".equals(servletPath)) {
			getcomboboxdept();
		}else if("/app/user/getcomboboxrole".equals(servletPath)) {
			getcomboboxrole();
		}
		
	}
	
	public void getcomboboxmenulist() throws Exception{
		List<HashMap> list = SQLExecUtils.query4List(SQLMap.getSelect("getcomboboxmenulist"));
		List rtList = SysUtility.ToTree(list, "group_indx","group_parentid");
		ReturnWriter(SysUtility.ListToJSONArray(rtList).toString());
	}
	
	public void getcomboboxorglist() throws Exception{
		List<HashMap> list = SQLExecUtils.query4List(SQLMap.getSelect("getcomboboxorglist"));
		List rtList = SysUtility.ToTree(list, "group_indx","group_parentid");
		ReturnWriter(SysUtility.ListToJSONArray(rtList).toString());
	}
	
	public void getcomboboxorglist2() throws Exception{
		List<HashMap> list = SQLExecUtils.query4List(SQLMap.getSelect("getcomboboxorglist2"));
		List rtList = SysUtility.ToTree(list, "group_indx","group_parentid");
		ReturnWriter(SysUtility.ListToJSONArray(rtList).toString());
	}

	public void getcomboboxorg() throws Exception{
		List<HashMap> list = SQLExecUtils.query4List(SQLMap.getSelect("getcomboboxorg"));
		List rtList = SysUtility.ToTree(list, "group_indx","group_parentid");
		ReturnWriter(SysUtility.ListToJSONArray(rtList).toString());
	}
	public void getcomboboxdept() throws Exception{
		AddToSearchTable("orgId", GetEnvDatas("org_id"));
		Datas datas = GetDatas("getcomboboxdept", "rows", true);
		ReturnWriter(datas.get("rows").toString());
	}
	public void getcomboboxrole() throws Exception{
		Datas datas = GetDatas("getrolelist", "rows", true);
		ReturnWriter(datas.get("rows").toString());
	}
}
