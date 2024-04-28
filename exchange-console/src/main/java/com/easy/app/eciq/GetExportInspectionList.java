package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetExportInspectionList")
public class GetExportInspectionList  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetExportInspectionList()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String portalType = getRequest().getParameter("portalType");
		if(!SysUtility.isEmpty(portalType))
		{
			AddToSearchTable("DECL_STATUS_CODE",portalType);
		}
		this.AddToSearchTable("part_id", SysUtility.getCurrentPartId());
		ReturnWriter(GetReturnDatas("GetExportInspectionList").toString());
	}
}
