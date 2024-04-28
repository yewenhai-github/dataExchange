package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2XmlSqlByPindx")
public class GetDB2XmlSqlByPindx  extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String Pindx = (String)getEnvDatas().get("Pindx");
        AddToSearchTable("P_INDX", Pindx);
		ReturnWriter(GetReturnDatas("GetDB2XmlSqlByPindx").toString());
	}

}