package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;
//


@WebServlet("/base/GetDingTalkByIndx")
public class GetDingTalkByIndx extends MainServlet{
	public GetDingTalkByIndx(){
		CheckLogin = false;
	}
	public void DoCommand() throws Exception{

		String INDX=getRequest().getParameter("INDX");
		String sql=SQLMap.getSelect("GetDinkTalkList"); 
		
		if(INDX != null && INDX.length() > 0)
			 sql=sql+" AND INDX = " + INDX;
	  
     JSONObject js= getDataAccess().GetTableJSON("B_MESSAGE_TEMPLATE", sql);
		ReturnWriter(js.toString());
	}
}