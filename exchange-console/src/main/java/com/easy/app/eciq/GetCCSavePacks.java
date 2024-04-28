package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetCCSavePacks")
public class GetCCSavePacks extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSavePacks()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String pIndx=GetDataValue("PackData", "DECL_NO");
		 if(SysUtility.isEmpty(pIndx)){
			 ReturnMessage(true, "保存成功");
			 return;
		 }
		 if(SysUtility.isEmpty(GetDataValue("PackData", "INDX"))){
			 if (!SysUtility.isEmpty(GetDataValue("PackData", "DECL_NO"))) {
				 //SaveToTable("corpData", "DEC_TYPE", "0");
			 }
		 }
		 String Indx = String.valueOf(SaveToDB("PackData", "T_DECL_PACK", "INDX"));
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