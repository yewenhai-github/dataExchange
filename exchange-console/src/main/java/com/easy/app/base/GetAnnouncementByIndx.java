package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;


@WebServlet("/base/GetAnnouncementByIndx")
public class GetAnnouncementByIndx extends MainServlet{
	public GetAnnouncementByIndx(){
		CheckLogin = false;
	}
	public void DoCommand() throws Exception{

		String INDX=getRequest().getParameter("INDX");
		String sql=SQLMap.getSelect("GetAnnouncementList"); 
		
		if(INDX != null && INDX.length() > 0)
			 sql=sql+" AND INDX = " + INDX;
	  
     JSONObject js= getDataAccess().GetTableJSON("T_ACCOUNCEMENT", sql);
		ReturnWriter(js.toString());
	}
}