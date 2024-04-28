package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetImportDeclareList")
public class GetImportDeclareList  extends MainServlet{  
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4403530510840414254L;
	public GetImportDeclareList()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		ReturnWriter(GetReturnDatas("GetImportDeclareList").toString());
		
		
	}
}
