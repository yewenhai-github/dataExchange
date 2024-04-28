package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SessionRuleAliasRowTable")


public class SessionRuleAliasRowTable extends MainServlet {
private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String indx="";
		String allId="";
		String typeName="";
		if((String)getEnvDatas().get("txtId")!=null)
			indx=(String)getEnvDatas().get("txtId");
		if((String)getEnvDatas().get("allid")!=null)
			allId=(String)getEnvDatas().get("allid");
//		if(SysUtility.isEmpty(indx)){
//			return;
//		}
		if((String)getEnvDatas().get("typename")!=null){
			typeName=(String)getEnvDatas().get("typename");
		}
		Datas dtSession=getDataAccess().GetTableDatas("RULE_T_ALIAS_DETAIL_TEMP", "Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=? order by INDX desc",SysUtility.getCurrentUserIndx());
		if(!allId.isEmpty())
		{
		  if(dtSession.GetTableRows("RULE_T_ALIAS_DETAIL_TEMP")>0)
		  {
				getDataAccess().ExecSQL("delete from RULE_T_ALIAS_DETAIL_TEMP Where BIZ_CODE in ("+allId.substring(0,allId.length()-1)+") and CREATOR=" + SysUtility.getCurrentUserIndx() + " ");
				getDataAccess().ComitTrans();
		  }
			String SQL = "Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + " order by INDX desc";
			ReturnWriter(GetReturnDatas("@"+SQL).toString());
		}else if(!typeName.isEmpty()&&"afterImport".equals(typeName)){
			if(indx.isEmpty()){
				ReturnMessage(false, "加载错误!");
				return;
			}
			String sqlRow="Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + " order by INDX desc";
			ReturnWriter(GetReturnDatas("@"+sqlRow).toString());
			//无业务要进行处理
		}
		else
		{
			String other=(String)getEnvDatas().get("other");
			JSONObject dtRuleAll = new JSONObject(other);
			JSONArray rows = dtRuleAll.getJSONArray("addQuery");
			JSONObject dtRule = rows.getJSONObject(0);
			String MenuId=dtRule.getString("txtMenuId").toString();
			Datas dtConfig=getDataAccess().GetTableDatas("RULE_B_ALIAS_CONFIG", "Select * from RULE_B_ALIAS_CONFIG Where INDX=" + MenuId + "");
			JSONObject dtNewTable = null;
			
			if(dtConfig.GetTableRows("RULE_B_ALIAS_CONFIG")>0)
			{
				
				String roleSqlRow = "Select " + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + " as BIZ_CODE," + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_NAME") + " as BIZ_NAME,1 as BIZ_TYPE," + SysUtility.getCurrentUserIndx() + " as CREATOR from " + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","TABLE_NAME") + " Where 1=1";
				Datas dtRow=getDataAccess().GetTableDatas(dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","TABLE_NAME"),roleSqlRow);
				if(dtRule.getString("txtRuleId").toString().equals("txtLike"))
				{
					  roleSqlRow += "  and (" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + " like '" + dtRule.getString("txtKeyWord").toString() + "%' or " +dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_NAME")+ " like '" + dtRule.getString("txtKeyWord").toString() + "%')";
					  InitFormData("TEMP",roleSqlRow);
					  JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
					  if(TEMP.length()!=0)
					  SaveToDB(TEMP,"RULE_T_ALIAS_DETAIL_TEMP");
				}
				if(dtRule.getString("txtRuleId").toString().equals("txtSel"))
				{
					JSONArray rowsSearch = (JSONArray)getFormDatas().getJSONArray("searchTable");
					if(rowsSearch.length()!=0)
					{
					for(int i = 0 ; i < rowsSearch.length();i++){
						SaveToTable("searchTable", "CREATOR", SysUtility.getCurrentUserIndx(),i);
					}
                    int allRow=SaveToDB(getFormDatas().getJSONArray("searchTable"),"RULE_T_ALIAS_DETAIL_TEMP");
					}
				}
				if(dtRule.getString("txtRuleId").toString().equals("txtFaiWei"))
				{
					if(!dtRule.getString("txtStartNum").toString().isEmpty())
						  roleSqlRow += " and to_number(REGEXP_REPLACE(ltrim(rtrim(" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + ")),'[a-zA-Z/:.]',''))>=" +Long.parseLong(dtRule.getString("txtStartNum").toString()) + ""; 
					  if(!dtRule.getString("txtEndNum").toString().isEmpty())
						  roleSqlRow += " and to_number(REGEXP_REPLACE(ltrim(rtrim(" + dtConfig.GetTableValue("RULE_B_ALIAS_CONFIG","COULMN_CODE") + ")),'[a-zA-Z/:.]',''))<" +Long.parseLong(dtRule.getString("txtEndNum").toString()) + ""; 
					  InitFormData("TEMP",roleSqlRow);
					  JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
					  if(TEMP.length()!=0)
					  SaveToDB(TEMP,"RULE_T_ALIAS_DETAIL_TEMP");
				}
				if(dtRule.getString("txtRuleId").toString().equals("txtAll"))
				{
					InitFormData("TEMP",roleSqlRow);
					JSONArray  TEMP =(JSONArray)getFormDatas().get("TEMP");
					if(TEMP.length()!=0)
					SaveToDB(TEMP,"RULE_T_ALIAS_DETAIL_TEMP");
				}
				String sqlRow="Select * from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=" + SysUtility.getCurrentUserIndx() + " order by INDX desc";
				ReturnWriter(GetReturnDatas("@"+sqlRow).toString());
			}
		}
	}
}
