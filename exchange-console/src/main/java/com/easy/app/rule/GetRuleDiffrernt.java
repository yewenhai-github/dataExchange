package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetRuleDiffrernt")
public class GetRuleDiffrernt extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		JSONArray params1 = GetCommandData("searchTable");
		JSONArray params2 = GetCommandData("addQuery");
		JSONObject dtRule = params2.getJSONObject(0);
		String MenuId=dtRule.getString("txtMenuId").toString();
		Datas dtConfig=getDataAccess().GetTableDatas("RULE_B_ALIAS_CONFIG", "Select * from RULE_B_ALIAS_CONFIG Where INDX=" + MenuId + "");
		if(dtConfig.GetTableRows("RULE_B_ALIAS_CONFIG")>0)
		{
			
			String roleSqlRow = "Select " + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + " as BIZ_CODE," + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_NAME") + " as BIZ_NAME,1 as BIZ_TYPE," + SysUtility.getCurrentUserIndx() + " as CREATOR from " + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","TABLE_NAME") + " Where 1=1";
		if(dtRule.getString("txtRuleId").toString().equals("txtLike"))
		{
			  roleSqlRow += "  and (" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + " like '" + dtRule.getString("txtKeyWord").toString() + "%' or " +dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_NAME")+ " like '" + dtRule.getString("txtKeyWord").toString() + "%')";
			  InitFormData("TEMP",roleSqlRow);
			  JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
			  for (int k = 0; k < TEMP.length(); k++) {
	 			   JSONObject childs=TEMP.getJSONObject(k);
	 			   String BIZ_CODE=(String)childs.getString("BIZ_CODE");
	 			   String SelectSQL="SELECT count(*) FROM RULE_T_ALIAS_DETAIL_TEMP WHERE BIZ_CODE='"+BIZ_CODE+"'";
				   Datas dataHX = getDataAccess().GetTableDatas("num", SelectSQL);
	 		 }
		}
		if(dtRule.getString("txtRuleId").toString().equals("txtSel"))
		{
			JSONArray rowsSearch = (JSONArray)getFormDatas().getJSONArray("searchTable");
			for(int i = 0 ; i < rowsSearch.length();i++){
				SaveToTable("searchTable", "CREATOR", SysUtility.getCurrentUserIndx(),i);
			}
            int allRow=SaveToDB(getFormDatas().getJSONArray("searchTable"),"RULE_T_ALIAS_DETAIL_TEMP");
		}
		if(dtRule.getString("txtRuleId").toString().equals("txtFaiWei"))
		{
			if(!dtRule.getString("txtStartNum").toString().isEmpty())
				  roleSqlRow += " and to_number(REGEXP_REPLACE(ltrim(rtrim(" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + ")),'[a-zA-Z/:.]',''))>=" +Long.parseLong(dtRule.getString("txtStartNum").toString()) + ""; 
			  if(!dtRule.getString("txtEndNum").toString().isEmpty())
				  roleSqlRow += " and to_number(REGEXP_REPLACE(ltrim(rtrim(" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + ")),'[a-zA-Z/:.]',''))<" +Long.parseLong(dtRule.getString("txtEndNum").toString()) + ""; 
			  InitFormData("TEMP",roleSqlRow);
			  JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
			  SaveToDB(TEMP,"RULE_T_ALIAS_DETAIL_TEMP");
		}
		if(dtRule.getString("txtRuleId").toString().equals("txtAll"))
		{
			InitFormData("TEMP",roleSqlRow);
			JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
			SaveToDB(TEMP,"RULE_T_ALIAS_DETAIL_TEMP");
		}
		}
	}
}
