package com.easy.app.rule;


import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/RuleAliasRowList")

public class RuleAliasRowList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String tableId = (String)getEnvDatas().get("id");
		String tableName = (String)getEnvDatas().get("tableName");
		String strWhere = "1=1";
		
        Datas dtConfig =null;
        if(Integer.parseInt(tableId)>0)
        {
           dtConfig= getDataAccess().GetTableDatas("RULE_B_ALIAS_CONFIG","Select * from RULE_B_ALIAS_CONFIG Where INDX=?",tableId);
        strWhere+=" and "+dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG", "COULMN_CODE")+" not in (Select BIZ_CODE from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + ")";
        if(dtConfig.GetTableRows("RULE_B_ALIAS_CONFIG")>0)
        {
        	if(!dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG", "FILTER_CONDITION").isEmpty())
        		strWhere+=" and "+dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG", "FILTER_CONDITION");
        	ReturnWriter(GetReturnDatas("@Select * from "+dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG", "TABLE_NAME")+" Where "+strWhere).toString());
        }
        }
        else
            ReturnMessage(true, "", "", "", "");
	}
}
