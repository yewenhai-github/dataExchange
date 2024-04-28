package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/DeleteRuleCheck")
public class DeleteRuleCheck extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("Indx");
		if(SysUtility.isEmpty(indx)){
			return;
		}
		String SQL = "delete from rule_t_check where indx = ? ";
		boolean rt = getDataAccess().ExecSQL(SQL, indx);
		
		if(rt){
			SQL = "delete from RULE_T_CHECK_CON where P_INDX = ? ";
			getDataAccess().ExecSQL(SQL, indx);
			
			SQL = "delete from RULE_T_CHECK_RESULT where P_INDX = ? ";
			getDataAccess().ExecSQL(SQL, indx);
		}
		ReturnMessage(rt, "", "", "");
	}
}
