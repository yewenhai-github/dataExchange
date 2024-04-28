package com.easy.app.eciq;



import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/getSaveAppcert")
public class getSaveAppcert  extends MainServlet {

	private static final long serialVersionUID = 3594076222956321552L; 
	public getSaveAppcert ()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{  
		SaveToTable("inspData", "DECL_NO",GetDataValue("inspData", "DECL_NO"));
 
		String indx = String.valueOf(SaveToDB("inspData", "ITF_DCL_IO_DECL", "DECL_NO"));
		if (!SysUtility.isEmpty(indx)) {
			ReturnMessage(true, "保存成功", "", getFormDatas().toString());
		}else{
			ReturnMessage(false, "保存失败");	
		}
     }
}
