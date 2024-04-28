package com.easy.api.convert.mess;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;



@WebServlet("/GetConfigPath")
public class GetConfigPath extends MainServlet{
	
	public void DoCommand() throws LegendException, Exception {
		if(!SysUtility.getCurrentUserIsRoot().equals("Y")) {
			AddToSearchTable("ORG_ID",SysUtility.getCurrentOrgId());	
		}
		ReturnWriter(GetDatas("GetConfigPathList").toString());
	}
}
