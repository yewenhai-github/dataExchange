package com.easy.api.server;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import com.easy.utility.CacheUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetApiToServer")
public class GetApiToServer extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	
	public GetApiToServer() {
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		Hashtable<String, Object> EnvDatas = getEnvDatas();
		HttpServletResponse Response = getResponse();
		String messageType = (String)EnvDatas.get("MESSAGE_TYPE");
		
		Map map = CacheUtility.getApiToServerMap(messageType);
		String rt = SysUtility.MapToJSONObject(map).toString();
		Response.setHeader("Content-type", "text/html;charset=UTF-8");
		ReturnWriter(rt);
	}
	
}
