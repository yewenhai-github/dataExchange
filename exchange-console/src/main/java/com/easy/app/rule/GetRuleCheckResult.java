package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetRuleCheckResult")
public class GetRuleCheckResult extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String pindx = getRequest().getParameter("pindx");
		if(SysUtility.isEmpty(pindx)){
			return;
		}
		AddToSearchTable("P_INDX", pindx);
		String SQL = "@select  RULE_T_CHECK_RESULT.* from RULE_T_CHECK_RESULT where P_INDX = #P_INDX# order by indx";
		ReturnWriter(GetReturnDatas(SQL).toString());
	}
}
