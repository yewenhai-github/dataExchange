package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.session.Operator;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetCurrentUser")
public class GetCurrentUser extends MainServlet {
	private static final long serialVersionUID = 1L;

	public GetCurrentUser(){
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		Operator operator = SysUtility.getCurrentUser();
		if(SysUtility.isEmpty(operator)) {
			ReturnMessage(false, "", "",getFormDatas().toString());
		}else {
			JSONObject obj = new JSONObject();
			obj.put("INDX", operator.getIndx());
			obj.put("USERNAME", operator.getUserName());
			obj.put("PASSWORD", operator.getPassWord());
			obj.put("DEPTID", operator.getDeptId());
			obj.put("ORG_ID", operator.getOrgId());
			obj.put("ISROOT", operator.getIsRoot());
			obj.put("CHECKORGCODE", getSession().getAttribute("CHECKORGCODE"));
			obj.put("CHECKORGNAME", getSession().getAttribute("CHECKORGNAME"));
			JSONArray seuser = new JSONArray();
			seuser.put(obj);
			getFormDatas().put("CurrentUser", seuser);		
			ReturnMessage(true, "", "",getFormDatas().toString());
		}
	}

}