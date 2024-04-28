package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetRuleCheck")
public class GetRuleCheck extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String point_Code = (String)getEnvDatas().get("point_Code");
		String name = (String)getEnvDatas().get("name");
		String SQL="";
		if(SysUtility.isEmpty(point_Code)){
			return;
		}
		String REC_VER=BizConfigFactory.getCfgValue(SysUtility.isNotEmpty(BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()))?BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()):BizConfigFactory.getCfgValue("APP_REC_VER"));
		AddToSearchTable("point_Code", point_Code);
		AddToSearchTable("APP_REC_VER", REC_VER);
		AddToSearchTable("RULE_NAME", name);
		
		if(!SysUtility.isEmpty(name)){
			SQL = "@select  RULE_T_CHECK.* from RULE_T_CHECK where POINT_CODE = #point_Code# and RULE_NAME=#RULE_NAME# order by chk_level,indx";
		}else if(SysUtility.isEmpty(REC_VER)){
			SQL = "@select  RULE_T_CHECK.* from RULE_T_CHECK where POINT_CODE = #point_Code# order by chk_level,indx";
		}else{
		 SQL = "@select  RULE_T_CHECK.* from RULE_T_CHECK where POINT_CODE = #point_Code# and APP_REC_VER=#APP_REC_VER# order by chk_level,indx";
		}
		//ReturnMessage(true, "", "", GetReturnDatas(SQL).toString());
		ReturnWriter(GetReturnDatas(SQL).toString());
	}
}
