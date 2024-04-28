package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2AnyXmlByIndx")
public class GetDB2AnyXmlByIndx extends MainServlet{
	private static final long serialVersionUID = 7709746085960620374L;

	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
        if(SysUtility.isEmpty(indx)){
        	ReturnMessage(false, "参数错误,Indx为空！");
        	return;
        }
		InitFormData("XmlData", SQLMap.getSelect("GetDB2AnyXmlByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
