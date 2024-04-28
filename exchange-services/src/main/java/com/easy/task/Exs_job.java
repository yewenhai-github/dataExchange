package com.easy.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;

public class Exs_job {
	public void Exs_InsertHander(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_InsertHander(param));
		gs.DoCommand();
	}
}
