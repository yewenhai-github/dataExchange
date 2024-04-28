package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/SaveInspDeclUser")
public class SaveInspDeclUser extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspDeclUser()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		
/*		if(SysUtility.isEmpty(GetDataValue("DeclUserData", "DECL_NO"))){
			ReturnMessage(true, "请先保存表头信息");
			return;
		}
		
		if (SysUtility.isEmpty(GetDataValue("DeclUserData", "USER_ID"))) {
			 
        }
		 String Indx = String.valueOf(SaveToDB("DeclUserData", "ITF_DCL_IO_DECL_USER", "USER_ID"));
	     if (!SysUtility.isEmpty(Indx))
	     {
	        	ReturnMessage(true, "保存成功");
	     }
	     else
	     {
	        	ReturnMessage(false, "保存失败");	
	     }*/
	     String Indx = GetDataValue("DeclUserData", "USER_ID");
	     if (SysUtility.isEmpty(Indx)) {
				Indx = SysUtility.GetUUID();
				SaveToTable("DeclUserData", "USER_ID", Indx);
				if (!getDataAccess().Insert("ITF_DCL_IO_DECL_USER",
						getFormDatas().getJSONArray("DeclUserData"), "USER_ID")) {
					Indx = "";
				}
			} else {
				if (!getDataAccess().Update("ITF_DCL_IO_DECL_USER",
						getFormDatas().getJSONArray("DeclUserData"), "USER_ID")) {
					Indx = "";
				}
			}
			if (!SysUtility.isEmpty(Indx)) {
				ReturnMessage(true, "保存成功");
			} else {
				ReturnMessage(false, "保存失败");
			}			
	}
			
}