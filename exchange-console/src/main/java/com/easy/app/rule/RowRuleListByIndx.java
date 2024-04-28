package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/RowRuleListByIndx")

public class RowRuleListByIndx extends MainServlet {

	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
	
		String indx = (String)getEnvDatas().get("indx");
		if(!indx.isEmpty())
		{
			if(Integer.parseInt(indx)>0)
			{
				getDataAccess().BeginTrans();
				try{
					getDataAccess().ExecSQL("delete from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=?",SysUtility.getCurrentUserIndx());
					String SQL = "Select COULMN_VALUE as BIZ_CODE,COULMN_DESC as BIZ_NAME,BIZ_TYPE,CREATOR from RULE_T_ALIAS_DETAIL Where p_indx=" + indx;				
					String sql="INSERT INTO RULE_T_ALIAS_DETAIL_TEMP(indx,Biz_Code,Biz_Name,Biz_Type,Creator) SELECT seq_RULE_T_ALIAS_DETAIL_TEMP.Nextval, COULMN_VALUE as BIZ_CODE,COULMN_DESC as BIZ_NAME,BIZ_TYPE,CREATOR from RULE_T_ALIAS_DETAIL Where p_indx="+ indx;
					getDataAccess().ExecSQL(sql);
					getDataAccess().ComitTrans();
					ReturnWriter(GetReturnDatas("@"+SQL).toString());
				}catch(Exception e){
					getDataAccess().RoolbackTrans();
				}
			}
			else
			{
				try{
				getDataAccess().BeginTrans();
				getDataAccess().ExecSQL("delete from RULE_T_ALIAS_DETAIL_TEMP Where CREATOR=?",SysUtility.getCurrentUserIndx());
				getDataAccess().ComitTrans();
				}catch(Exception e){
					getDataAccess().RoolbackTrans();
				}
			}
		}
		
	}
}
