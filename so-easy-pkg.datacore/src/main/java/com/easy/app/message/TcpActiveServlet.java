package com.easy.app.message;

import com.easy.exception.LegendException;
import com.easy.web.MainServlet;

public class TcpActiveServlet extends MainServlet{
	private static final long serialVersionUID = -8505611749348389713L;

	public TcpActiveServlet(String param) {
		super();
		SetCheckLogin(false);
//		try {
//			while (true) {
//				ExsUtility.SaveToDBForSocket(DataAccess);
//				Thread.sleep(2000);
//			}
//		} catch (Exception e) {
//			LogUtil.printLog("TcpActiveServlet监听处理出错:"+e.getMessage(), Level.ERROR);
//		}
	}

	@Override
	protected void DoCommand() throws LegendException, Exception {
		super.DoCommand();
	}
	
}
