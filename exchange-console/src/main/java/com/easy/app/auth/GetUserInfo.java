package com.easy.app.auth;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/base/GetUserInfo")
public class GetUserInfo extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		Datas datas = new Datas();
		datas.put("userIndx",SysUtility.getCurrentUserIndx());
		datas.put("entRegNo",SysUtility.getCurrentEntRegNo());
		datas.put("entName",SysUtility.getCurrentEntName());
		
		ReturnMessage(true, "保存成功", "", datas.toString());
	}
}