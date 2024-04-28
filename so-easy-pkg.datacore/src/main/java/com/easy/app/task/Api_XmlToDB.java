package com.easy.app.task;

import com.easy.access.IDataAccess;
import com.easy.access.MySqlDataAccess;
import com.easy.access.OraDataAccess;
import com.easy.access.SqlDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.constants.Constants;
import com.easy.entity.ServicesBean;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/push")
public class Api_XmlToDB extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;

	public Api_XmlToDB() {
		super();
		SetCheckLogin(false);
	}

	/**
	 * 请求示例地址：http://localhost:8080/push?messageType=value1&messageData=value2
	 * messageType：消息类型，唯一，由平台制定
	 * messageData：消息内容，通常是报文内容，常用为json和xml格式
	 * */
	public void DoCommand() throws Exception {
		if(SysUtility.quartzLimit()){
			return;
		}
		String dbType = GetSetting("system", "DBType");
		if (Constants.Sqlserver.equalsIgnoreCase(dbType)) {
			SessionManager.setDataAccess(new SqlDataAccess());
		} else if (Constants.Oracle.equalsIgnoreCase(dbType)){
			SessionManager.setDataAccess(new OraDataAccess());
		}else if (Constants.Mysql.equalsIgnoreCase(dbType)){
			SessionManager.setDataAccess(new MySqlDataAccess());
		}
		IDataAccess DataAccess = SessionManager.getDataAccess();
		
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.XmlToDB);
		bean.setMessageType((String)GetEnvDatas("messageType"));
		bean.setXmlData((String)GetEnvDatas("messageData"));
		ExsUtility.ApiToC(bean, DataAccess);
		String errorMsg = bean.getResponseMessage();
		if(SysUtility.isEmpty(errorMsg)){
			ReturnMessage(true, "数据解析成功！");
		}else{
			ReturnMessage(false, errorMsg);
		}
	}
	
}
