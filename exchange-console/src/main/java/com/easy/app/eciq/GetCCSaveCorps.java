package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetCCSaveCorps")
public class GetCCSaveCorps extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSaveCorps()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String pIndx=GetDataValue("DeclDocData", "CON_INDX");
		 if(SysUtility.isEmpty(pIndx)){
			 ReturnMessage(true, "请先保存表头信息");
			 return;
		 }
		
		 if(SysUtility.isEmpty(GetDataValue("DeclDocData", "INDX"))){
			 if (!SysUtility.isEmpty(GetDataValue("DeclDocData", "CON_INDX"))) {
				 //SaveToTable("DeclDocData", "DEC_TYPE", "0");
			 }
		 }
		 SaveToTable("DeclDocData", "ENY_INDX", GetDataValue("custData", "INDX"));
		 SaveToTable("DeclDocData", "DOCUMENTSCO_NAME", GetDataValue("DeclDocData", "DOCUMENTSCN"));
		 
		 String Indx = String.valueOf(SaveToDB("DeclDocData", "T_DOCUMENTS", "INDX"));
		   if (!SysUtility.isEmpty(Indx))
		    {
		       ReturnMessage(true, "保存成功");
		    }
		    else
		    {
		       ReturnMessage(false, "保存失败");	
		    }
	}
			
}