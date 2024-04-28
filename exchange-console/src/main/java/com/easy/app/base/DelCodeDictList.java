package com.easy.app.base;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.web.MainServlet;



@WebServlet("/base/DelCodeDictList")
public class DelCodeDictList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		
			String INDX = (String)GetEnvDatas("id");
			StringBuffer deleteSQLs = new StringBuffer();
			deleteSQLs.append("update S_BASE_CODE_TYPE set IS_ENABLED = '0'");
			deleteSQLs.append(" WHERE INDX in ( "+INDX+" )");
			getDataAccess().ExecSQL(deleteSQLs.toString());
			ReturnMessage(true, "删除成功!");
		
	}

}
