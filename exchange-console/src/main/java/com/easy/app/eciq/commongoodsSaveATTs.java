package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/commongoodsSaveATTs")
public class commongoodsSaveATTs  extends MainServlet {

	private static final long serialVersionUID = 3594076222956321552L; 
	public commongoodsSaveATTs ()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		if (SysUtility.isEmpty(GetDataValue("LimitData", "DECL_NO"))) {
	
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SaveToTable("LimitData", "OPER_TIME", df.format(new Date()));
		} 
		String indx = String.valueOf(SaveToDB("LimitData", "ITF_DCL_IO_DECL_ATT", "ATT_ID"));
		if (!SysUtility.isEmpty(indx)) {
			ReturnMessage(true, "保存成功", "", TableToJSON("LimitData"));
		}else{
			ReturnMessage(false, "保存失败");	
		}
     }
}
