package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2AnyXmlMapSqlByIndx")
public class GetDB2AnyXmlMapSqlByIndx extends MainServlet{
	private static final long serialVersionUID = -8810135130442923052L;

	public void DoCommand() throws Exception{
		String INDX=(String)getEnvDatas().get("INDX");
		if(SysUtility.isNotEmpty(INDX)){
			AddToSearchTable("INDX", INDX);
			InitFormData("DB2AnyData", SQLMap.getSelect("GetDB2AnyXmlMapSqlByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		
	}
}
