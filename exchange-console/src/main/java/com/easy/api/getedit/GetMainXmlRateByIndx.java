package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMainXmlRateByIndx")
public class GetMainXmlRateByIndx extends MainServlet {
	private static final long serialVersionUID = 4808343104729560748L;

	public GetMainXmlRateByIndx() {
		super();
	}
	
	public void DoCommand() throws Exception{
		String COUNT_TIME= getRequest().getParameter("COUNT_TIME");
		if(SysUtility.isEmpty(COUNT_TIME)){
			COUNT_TIME="1";
	    }
		AddToSearchTable("COUNT_TIME", COUNT_TIME);
		ReturnWriter(GetReturnDatas("GetMainXmlRateByIndx").toString());
	}

}