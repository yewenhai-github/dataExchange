package com.easy.app.base;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.web.MainServlet;


@WebServlet("/base/DelDingTalkList")

public class DelDingTalkList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String INDX = (String)GetEnvDatas("id");
		StringBuffer deleteSQLs = new StringBuffer();
		deleteSQLs.append("update B_MESSAGE_TEMPLATE set IS_ENABLED = '0'");
		deleteSQLs.append(" WHERE INDX in ( "+INDX+" )");
		getDataAccess().ExecSQL(deleteSQLs.toString());
		ReturnMessage(true, "删除成功!");
	}

}
