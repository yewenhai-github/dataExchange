package com.easy.app.rule;

import java.sql.Connection;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SaveRowRuleAias")

public class SaveRowRuleAias extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		try {
			Connection conn = SysUtility.getCurrentConnection();
		      boolean flag=getDataAccess().ExecSQL("insert into RULE_T_ALIAS_DETAIL_TEMP(INDX,BIZ_CODE,BIZ_NAME,BIZ_TYPE,CREATOR) values(SEQ_RULE_T_ALIAS_DETAIL_TEMP.nextval,'" + (String)getEnvDatas().get("txtValue") + "','" + (String)getEnvDatas().get("txtText") + "',2," + SysUtility.getCurrentUserIndx() + ")");
		      //Datas dtSession=getDataAccess().GetTableDatas("RULE_T_ALIAS_DETAIL_TEMP", "Select * from RULE_T_ALIAS_DETAIL_TEMP order by INDX desc");
		     // ReturnMessage(true, "", "","保存成功!", "");
		      SysUtility.ComitTrans();
		      
		     Datas dtSession=getDataAccess().GetTableDatas("RULE_T_ALIAS_DETAIL_TEMP", "Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + " order by INDX desc");
		      ReturnMessage(true, "", "","保存成功!", "");
		      
		} catch (Exception e) {
			ReturnMessage(false, "保存失败！", "","");
		}
		ReturnMessage(true, "保存成功！", "","");
	} 
}
