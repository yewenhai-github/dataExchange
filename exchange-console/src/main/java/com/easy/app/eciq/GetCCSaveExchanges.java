package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetCCSaveExchanges")
public class GetCCSaveExchanges extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSaveExchanges()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String pIndx=	GetDataValue("ExchangeData", "INDX");
		 if(SysUtility.isEmpty(pIndx)){
			 ReturnMessage(true, "请先保存表头信息");
			 return;
		 }
		
		 if(SysUtility.isEmpty(GetDataValue("ExchangeData", "INDX"))){
			 if (!SysUtility.isEmpty(GetDataValue("ExchangeData", "DECL_NO"))) {
				 //SaveToTable("corpData", "DEC_TYPE", "0");
			 }
		 }
		 String Indx = String.valueOf(SaveToDB("ExchangeData", "T_DECL_EXCHANGE", "INDX"));
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