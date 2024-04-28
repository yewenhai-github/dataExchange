package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/DeleteRuleCheckCon")
public class DeleteRuleCheckCon extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("Indx");
		if(SysUtility.isEmpty(indx)){
			return;
		}
		String SQL = "delete from RULE_T_CHECK_CON where indx = ? ";
		boolean rt = getDataAccess().ExecSQL(SQL, indx);
		ReturnMessage(rt, "", "", "");
	}
}
