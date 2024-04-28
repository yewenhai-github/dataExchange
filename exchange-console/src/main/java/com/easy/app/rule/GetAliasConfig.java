package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetAliasConfig")

public class GetAliasConfig extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		
		String indx = (String)getEnvDatas().get("indx");
		String tableName="";
		String strWhere=" 1=1";
		if(SysUtility.isEmpty(indx)){
			return;
		}
		if(Integer.parseInt(indx)>0)
		{
			Datas dtBaseTable = getDataAccess().GetTableDatas("RULE_T_ALIAS", "Select * from RULE_T_ALIAS Where indx=?", indx);
			tableName=dtBaseTable.GetTableValue("RULE_T_ALIAS", "TABLE_NAME");
			if(!tableName.isEmpty())
				strWhere+=" and TABLE_NAME='"+tableName+"'";
		}
		Datas rt=getDataAccess().GetTableDatas("rows", "Select INDX as id,0 as pid,TABLE_DESC as name from RULE_B_ALIAS_CONFIG Where "+strWhere);
		ReturnWriter(rt.getJSONArray("rows").toString());
	}
}
