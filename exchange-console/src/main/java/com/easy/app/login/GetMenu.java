package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/getMenu")
public class GetMenu extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		if(SysUtility.isEmpty(SysUtility.getCurrentUserIndx())){
			ReturnMessage(false, "用户失效，请您重新登录！", LoginPage);
		}else{
			JSONArray array = new JSONArray(new GetExsMenu().getConsoleMenu());//菜单信息
			getFormDatas().put("menu", array);
			ReturnMessage(true, "", "",getFormDatas().toString());
		}
	}
}
