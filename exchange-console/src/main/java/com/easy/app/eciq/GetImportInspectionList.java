package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetImportInspectionList")
public class GetImportInspectionList  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetImportInspectionList()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String portalType = getRequest().getParameter("portalType");
		if(!SysUtility.isEmpty(portalType))
		{
			AddToSearchTable("DECL_STATUS_CODE",portalType);
		}
		this.AddToSearchTable("part_id", SysUtility.getCurrentPartId());
		ReturnWriter(GetReturnDatas("GetInspectionList").toString());
	}
}
