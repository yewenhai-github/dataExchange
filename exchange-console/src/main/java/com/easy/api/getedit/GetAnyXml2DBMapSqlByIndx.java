package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetAnyXml2DBMapSqlByIndx")
public class GetAnyXml2DBMapSqlByIndx extends MainServlet{
	private static final long serialVersionUID = -8810135130442923052L;

	public void DoCommand() throws Exception{
		String INDX=(String)getEnvDatas().get("INDX");
		if(SysUtility.isNotEmpty(INDX)){
			AddToSearchTable("INDX", INDX);
			InitFormData("AnyXmlData", SQLMap.getSelect("GetAnyXml2DBMapSqlByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		
	}
}
