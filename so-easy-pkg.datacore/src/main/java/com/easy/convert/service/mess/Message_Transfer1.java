package com.easy.convert.service.mess;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.easy.app.interfaces.IGlobalService;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Message_Transfer1 extends MainServlet implements IGlobalService{
	
	static boolean quartzFlat = true;
	static Integer ThreadCount =Integer.parseInt( SysUtility.GetSetting("System", "MAXTHREADCOUNT"));
	static ExecutorService service = Executors.newFixedThreadPool(ThreadCount);
	private static final long serialVersionUID = 6288128270284406321L;
	
	
	public Message_Transfer1(String param) {
		super();
	}

	public void DoCommand() throws Exception {
		service.execute(new Message_TransferThread());
	}
	
	
	
	
}
