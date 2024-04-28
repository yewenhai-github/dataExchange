package com.easy.api.audit;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.app.utility.Utility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/AuditAnyXml2DB")
public class AuditAnyXml2DB extends MainServlet {
	private static final long serialVersionUID = 8504238882588900595L;

	public void DoCommand() throws Exception{
		JSONArray params = GetCommandData("key");
		if(SysUtility.isEmpty(params) || params.length() <= 0){
			ReturnMessage(false, "当前操作操作失败！传输数据行数："+params.length());
			return;
		}
		
		HashMap map = Utility.AuditTableAllDB(params, "EXS_CONFIG_XMLTODB", this);
		
		int SuccessCount = (Integer)map.get("SuccessCount");
		int ErrorCount = (Integer)map.get("ErrorCount");
		if(ErrorCount > 0){
			ReturnMessage(true, "审核成功"+SuccessCount+"条数据,审核失败"+ErrorCount+"条数据");
		}else{
			ReturnMessage(true, "审核成功"+SuccessCount+"条数据");
		}
	}
}
