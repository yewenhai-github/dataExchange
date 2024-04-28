package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/auth/GetUserInfo")
public class GetUserInfo extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
//		getResponse().setHeader("X-Frame-Options", "ALLOW-FROM http://localhost:63342/");
//		getResponse().setHeader("Access-Control-Allow-Credentials", "true");
//		getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        getResponse().setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        
		JSONObject data = new JSONObject();
		data.put("rolename", SysUtility.getCurrentRoleName());
		data.put("username", SysUtility.getCurrentName());
		data.put("indx", SysUtility.getCurrentUserIndx());
		data.put("usertype", SysUtility.getCurrentUserType());
		data.put("roletype", SysUtility.getCurrentRoleType());
		data.put("orgid", SysUtility.getCurrentOrgId());
		data.put("isRoot", SysUtility.getCurrentUserIsRoot());
		ReturnWriter(data.toString());
	}
	
}
