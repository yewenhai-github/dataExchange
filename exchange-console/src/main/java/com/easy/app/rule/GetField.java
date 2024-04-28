package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetField")
public class GetField extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String point_Code = (String)getEnvDatas().get("point_Code");
		if(SysUtility.isEmpty(point_Code)){
			return;
		}
		AddToSearchTable("point_Code", point_Code);
		String SQL = "@select INDX,Table_name,rule_no,Field_code,Field_Name,Field_Data_type,is_mustinput,IS_CONTROL_TOTAL,char_total_start,char_total,IS_CONTROL_ROW,row_total,col_total,row_total||'*'||col_total AS rowcol,IS_CHINESE,OPERATOR,FIELD_VALUE,FIELD_VALUE_CN,CREATOR,CREATE_TIME,POINT_CODE,IS_CHINESE_NAME from RULE_T_FIELD where POINT_CODE = #point_Code# ";
		String REC_VER=BizConfigFactory.getCfgValue(SysUtility.isNotEmpty(BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()))?BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()):BizConfigFactory.getCfgValue("APP_REC_VER"));
		if(SysUtility.isNotEmpty(REC_VER)){
			SQL=SQL+" and  APP_REC_VER='"+REC_VER+"'";
		}
		SQL=SQL+" order by INDX";
		ReturnWriter(GetReturnDatas(SQL).toString());
	}
}
