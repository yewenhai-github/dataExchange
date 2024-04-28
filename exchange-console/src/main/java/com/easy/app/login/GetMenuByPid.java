package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.web.MainServlet;

@WebServlet("/getMenuByPid")
public class GetMenuByPid extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String pid = (String)getEnvDatas().get("PID");
		JSONArray array = new JSONArray(new GetExsMenu().getConsoleMenuByPid(pid));
		getFormDatas().put("menu", array);
		ReturnMessage(true, "", "",getFormDatas().toString());
	}
}
