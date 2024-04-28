package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SessionRowRuleList")

public class SessionRowRuleList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String isclear="";
		
		if((String)getEnvDatas().get("isclear")!=null)
			isclear=(String)getEnvDatas().get("isclear");
				
		if(isclear.isEmpty())
		{	
			String SQL = "Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + " order by INDX desc";
			//Datas dtSession=getDataAccess().GetTableDatas("RULE_T_ALIAS_DETAIL_TEMP", "Select * from RULE_T_ALIAS_DETAIL_TEMP order by INDX desc");
		
			ReturnWriter(GetReturnDatas("@"+SQL).toString());
		}
		else
		{			
			getDataAccess().ExecSQL("delete from RULE_T_ALIAS_DETAIL_TEMP ");
			getDataAccess().ComitTrans();
			ReturnMessage(true, "", "", "", "");
		}
	}
}
