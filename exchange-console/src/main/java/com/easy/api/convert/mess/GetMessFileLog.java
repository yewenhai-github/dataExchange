package com.easy.api.convert.mess;

import java.util.HashMap;
import java.util.List;
import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/GetMessFileLog")
public class GetMessFileLog extends MainServlet{
	
	public void DoCommand() throws LegendException, Exception {
		String currentUserIndx = SysUtility.getCurrentUserIndx();
		String UserSQL = "SELECT REGISTER_NO FROM s_auth_user WHERE INDX='"+currentUserIndx+"'";
		List query4List = SQLExecUtils.query4List(UserSQL);
		String SERIAL_NO = "";
		for(int i=0;i<query4List.size();i++) {
			HashMap object = (HashMap) query4List.get(i);
			SERIAL_NO = (String) object.get("REGISTER_NO");
		}
		if(!SysUtility.getCurrentUserIsRoot().equals("Y")) {
			if(SysUtility.isNotEmpty(SERIAL_NO)) {
				AddToSearchTable("SERIAL_NO", SERIAL_NO);
			}			
		}			
		ReturnWriter(GetReturnDatas("GetMessFileLogList").toString());	
	}
	
}
