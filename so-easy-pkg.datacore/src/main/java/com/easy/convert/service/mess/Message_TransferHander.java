package com.easy.convert.service.mess;

import java.util.HashMap;
import java.util.List;

import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.constants.ExsConstants;
import com.easy.convert.service.hander.Message_TransferNerFoled;
import com.easy.query.SQLExecUtils;
import com.easy.web.MainServlet;


public class Message_TransferHander extends MainServlet implements IGlobalService{
	
	public Message_TransferHander(String param) {
		super();
	}
	public void DoCommand() throws Exception {
		String sql = "SELECT * FROM EXS_HANDLE_SENDER WHERE MSG_FLAG='0' AND IS_ENABLED='1'";
		List query4List = SQLExecUtils.query4List(sql);
		for(int i =0; i<query4List.size(); i++) {
			HashMap Map = (HashMap) query4List.get(i);
			String MSGTYPE = (String) Map.get("MSG_TYPE");
			
			if(MSGTYPE.equals(ExsConstants.NEWFOLDER)) {//文件创建目录
				Message_TransferNerFoled.Message_NewNerFoled(Map);
				boolean execSQL = getDataAccess().ExecSQL("UPDATE EXS_HANDLE_SENDER SET MSG_FLAG ='1' WHERE INDX =? ",new String[] {(String)Map.get("INDX")});
			}
		}
	}
	
}
