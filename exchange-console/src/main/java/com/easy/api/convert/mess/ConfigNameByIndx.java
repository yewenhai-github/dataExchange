package com.easy.api.convert.mess;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/ConfigNameByIndx")
public class ConfigNameByIndx extends MainServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void DoCommand() throws LegendException, Exception {
		String INDX = getRequest().getParameter("INDX");
		AddToSearchTable("INDX", INDX);
		//企业机构代码 
		 if(!SysUtility.getCurrentUserIsRoot().equals("Y")) {
			AddToSearchTable("ORG_ID",SysUtility.getCurrentOrgId());	
		} 
		ReturnWriter(GetDatas("GetConfigNameList").toString());
	}

}
