package com.easy.app.base;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/base/DelAnnouncement")
public class DelAnnouncement extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		
			String INDX = (String)GetEnvDatas("id");
			StringBuffer deleteSQLs = new StringBuffer();
			deleteSQLs.append("update T_ANNOUNCEMENT  set IS_ENABLED = '0'");
			deleteSQLs.append(" WHERE INDX in ( "+INDX+" )");
			getDataAccess().ExecSQL(deleteSQLs.toString());
			ReturnMessage(true, "删除成功!");
		
	}

}
