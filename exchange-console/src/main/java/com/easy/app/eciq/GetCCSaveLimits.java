package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetCCSaveLimits")
public class GetCCSaveLimits extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSaveLimits()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String pIndx=GetDataValue("LimitData", "DECL_NO");
		 if(SysUtility.isEmpty(pIndx)){
			 ReturnMessage(true, "请先保存表头信息");
			 return;
		 }
		
		 if(SysUtility.isEmpty(GetDataValue("LimitData", "INDX"))){
			 if (!SysUtility.isEmpty(GetDataValue("LimitData", "DECL_NO"))) {
				 //SaveToTable("corpData", "DEC_TYPE", "0");
			 }
		 }
		 String Indx = String.valueOf(SaveToDB("LimitData", "T_DECL_LIMIT", "INDX"));
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