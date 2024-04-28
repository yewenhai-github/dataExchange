package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SavaRuleCheckCon")
public class SavaRuleCheckCon extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		try {
			if (SysUtility.isNotEmpty(GetDataValue("ConDate", "P_INDX"))) {
				int indx=SaveToDB(getFormDatas().getJSONArray("ConDate"), "RULE_T_CHECK_CON");
				if(SysUtility.isEmpty(indx)){
					ReturnMessage(false, "保存失败！", "","");
				}
				ReturnMessage(true, "保存成功！", "","");
			}else{
				ReturnMessage(false, "未选择规则信息不能保存！", "","");
			}
		} catch (Exception e) {
			ReturnMessage(false, "保存失败！", "","");
		}
		
	}
}
