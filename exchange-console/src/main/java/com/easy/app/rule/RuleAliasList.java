package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/RuleAliasList")

public class RuleAliasList extends MainServlet {

	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String PROCESS_NAME = (String)GetCommandData("SearchTable", "PROCESS_NAME");
		String REMARK = (String)GetCommandData("SearchTable", "REMARK");
		String REC_VER=SysUtility.isNotEmpty(BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()))?BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()):BizConfigFactory.getCfgValue("APP_REC_VER");
		String strWhere="";
		 if(PROCESS_NAME != null && PROCESS_NAME.length() != 0)
		    strWhere+=" AND PROCESS_NAME like '"+PROCESS_NAME+"' ";
		 if(REMARK != null && REMARK.length() != 0)
			strWhere+=" AND REMARK like '"+REMARK+"' "; 
		 if(SysUtility.isNotEmpty(REC_VER)){
			 strWhere+=" AND APP_REC_VER='"+REC_VER+"'";
		 }else{
			 strWhere+=" AND APP_REC_VER='B'";
		 }
		 String sql="WITH p AS  (SELECT  ORG_ID p_org_id FROM  s_auth_organization  WHERE org_id=(SELECT ORG_ID FROM EXs_auth_user WHERE INDX ='"+SysUtility.getCurrentUserIndx()+"' )"+")SELECT *  FROM RULE_T_ALIAS t,p  WHERE IS_ENABLED = 1   AND t.creator IN (SELECT indx FROM EXs_auth_user y WHERE y.org_id IN  (SELECT ORG_ID FROM S_AUTH_ORGANIZATION START WITH ORG_ID=p.p_org_id CONNECT BY PRIOR ORG_ID = P_ORG_ID))" + strWhere;
		 String rt = getDataAccess().GetTable(sql);
		ReturnWriter(rt);
	}
}
