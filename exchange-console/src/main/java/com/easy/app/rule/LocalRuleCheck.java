package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/LocalRuleCheck")
public class LocalRuleCheck extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("Indx");
		String is_enabled = (String)getEnvDatas().get("is_enabled");
		if(SysUtility.isEmpty(indx)){
			return;
		}
		if("1".equals(is_enabled)){
		String SQL = "update  rule_t_check set is_enabled='0' where indx = ? ";
		boolean rt = getDataAccess().ExecSQL(SQL, indx);
		
		if(rt){
			SQL = "update  RULE_T_CHECK_CON  set is_enabled='0' where P_INDX = ? ";
			getDataAccess().ExecSQL(SQL, indx);
			
			SQL = "update  RULE_T_CHECK_RESULT set is_enabled='0' where P_INDX = ? ";
			getDataAccess().ExecSQL(SQL, indx);
		}
		ReturnMessage(rt, "", "", "");
		}
		else{

			String SQL = "update  rule_t_check set is_enabled='1' where indx = ? ";
			boolean rt = getDataAccess().ExecSQL(SQL, indx);
			
			if(rt){
				SQL = "update  RULE_T_CHECK_CON  set is_enabled='1' where P_INDX = ? ";
				getDataAccess().ExecSQL(SQL, indx);
				
				SQL = "update  RULE_T_CHECK_RESULT set is_enabled='1' where P_INDX = ? ";
				getDataAccess().ExecSQL(SQL, indx);
			}
			ReturnMessage(rt, "", "", "");
			
		}
		
	}
}
