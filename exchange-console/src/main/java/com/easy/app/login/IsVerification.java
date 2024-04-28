package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;

/**
 * Servlet implementation class DoLogin
 */
@WebServlet("/login/IsVerification")

public class IsVerification extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	public IsVerification() {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception 
	{
		  String sql="select SC_VALUE from S_SYS_BIZ_CONFIG t where t.org_id='-1'";
		  
	      JSONObject js= getDataAccess().GetTableJSON("rows", sql);
	      
	      ReturnWriter(js.toString());
	}
}
