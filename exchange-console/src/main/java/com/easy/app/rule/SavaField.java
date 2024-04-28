package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SavaField")
public class SavaField extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		try {
			String REC_VER=BizConfigFactory.getCfgValue(SysUtility.isNotEmpty(BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()))?BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()):BizConfigFactory.getCfgValue("APP_REC_VER"));
			if(SysUtility.isNotEmpty(REC_VER)){
				getFormDatas().getJSONArray("RULE_CHECK").getJSONObject(0).put("APP_REC_VER", REC_VER);
			}
			int indx=SaveToDB(getFormDatas().getJSONArray("RULE_CHECK"), "RULE_T_FIELD");
			if(SysUtility.isEmpty(indx)){
				ReturnMessage(false, "保存失败！", "","");
			}
			ReturnMessage(true, "保存成功！", "","");
		} catch (Exception e) {
			ReturnMessage(false, "保存失败！", "","");
		}
		
	}
}
