package com.easy.app.rule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/UpdateRuleCheck")
public class UpdateRuleCheck extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	private static final String CancelRuleCheck = "0";
	private static final String RecoverRuleCheck = "1";
	private static final String PublishRuleCheck = "3";
	private static final String UpAcTime = "4";
	
	public void DoCommand() throws Exception{
		String doMethod = (String)getEnvDatas().get("doMethod");
		String indx = (String)getEnvDatas().get("Indx");
		String beginTime = (String)getEnvDatas().get("beginTime");
		String endTime = (String)getEnvDatas().get("endTime");
		List timeList = new ArrayList();
		if(SysUtility.isEmpty(doMethod) || SysUtility.isEmpty(indx)){
			return;
		}
		StringBuffer SQL = new StringBuffer();
		if(CancelRuleCheck.equals(doMethod)){
			SQL.append("update RULE_T_CHECK set is_validate = 1,ins_syn = 0 ");
		}else if(RecoverRuleCheck.equals(doMethod)){
			SQL.append("update RULE_T_CHECK set is_validate = 0,ins_syn = 0 ");
		}else if(PublishRuleCheck.equals(doMethod)){
			SQL.append("update RULE_T_CHECK set ins_syn = 1 ");
		}else if(UpAcTime.equals(doMethod)){
			SQL.append("update RULE_T_CHECK set indx = indx");
			if(SysUtility.isNotEmpty(beginTime)){
				SQL.append(",ACTIVE_TIME_BEGIN = to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
				timeList.add(beginTime);
			}
			if(SysUtility.isNotEmpty(endTime)){
				SQL.append(",ACTIVE_TIME_END = to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
				timeList.add(endTime);
			}
		}
		int listSize = timeList.size();
		StringBuffer suff = new StringBuffer();
		String[] indxs = indx.split(",");
		Object[] params = new Object[indxs.length+listSize];
		for (int i = 0; i < listSize; i++) {
			params[i] = timeList.get(i);
		}
		for (int i = 0; i < indxs.length; i++) {
			params[i+listSize] = Integer.parseInt(indxs[i]);
			if(i == 0){
				suff.append("?");
			}else{
				suff.append(",?");
			}
		}
		SQL.append(" where indx in(").append(suff).append(")");
		boolean rt = getDataAccess().ExecSQL(SQL.toString(), params);
		ReturnMessage(rt, "", "", "");
	}
}
