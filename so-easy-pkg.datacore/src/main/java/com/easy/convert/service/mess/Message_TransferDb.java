package com.easy.convert.service.mess;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.constants.ExsConstants;
import com.easy.convert.service.util.MessUtility;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.web.MainServlet;


public class Message_TransferDb extends MainServlet implements IGlobalService{

	/**
	 * 串行版本 用来实现序列号的唯一辨识
	 */
	private static final long serialVersionUID = 1458846663078180820L;
	
	public Message_TransferDb(String Param) {
		super();
	}
	@Override
	public void DoCommand() throws LegendException, Exception {
		IDataAccess DataAccess = getDataAccess();
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.DB);
		MessUtility.MessToxml(bean, DataAccess);
	}

	
}
