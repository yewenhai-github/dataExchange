package com.easy.api.server;

import java.util.Hashtable;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import com.easy.utility.CacheUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetAccessLimit")
public class GetAccessLimit extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	
	public GetAccessLimit() {
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		Hashtable<String, Object> EnvDatas = getEnvDatas();
		HttpServletResponse Response = getResponse();
		String clientIp = (String)EnvDatas.get("CLIENT_IP");
		List list = CacheUtility.getAccessLimitList(clientIp);
		String rt = SysUtility.ListToJSONArray(list).toString();
		Response.setHeader("Content-type", "text/html;charset=UTF-8");
		ReturnWriter(rt);
	}
	
}
