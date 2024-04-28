package com.easy.api.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;

public class DefaultJob {

	public void Exs_ApiPollingToInvoke(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPollingToInvoke(param));
		gs.DoCommand();
	}
	
	public void Exs_ApiPollingToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPollingToDB(param));
		gs.DoCommand();
	}
	
	public void Exs_ApiPollingToPush(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPollingToPush(param));
		gs.DoCommand();
	}
}
