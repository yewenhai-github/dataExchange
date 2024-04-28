package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/eciq/cust/GetExportDeclareList")
public class GetExportDeclareList  extends MainServlet{  
	
	private static final long serialVersionUID = 8524100130045022405L;
	
	public GetExportDeclareList()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		ReturnWriter(GetReturnDatas("GetImportDeclareList").toString());
		
		
	}
}
