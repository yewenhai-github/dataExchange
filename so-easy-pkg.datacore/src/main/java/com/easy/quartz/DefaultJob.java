package com.easy.quartz;

import com.easy.api.task.Exs_ApiPolling;
import com.easy.api.task.Exs_ApiPollingToInvoke;
import com.easy.api.task.Exs_ApiPush;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;
import com.easy.app.task.ExsCustXml;
import com.easy.app.task.Exs_AppMonitor;
import com.easy.app.task.Exs_DBToLocal;
import com.easy.app.task.Exs_DBToMQ;
import com.easy.app.task.Exs_EdiTab;
import com.easy.app.task.Exs_ListenServer;
import com.easy.app.task.Exs_LocalFloderToMerge;
import com.easy.app.task.Exs_LocalFloderToSplit;
import com.easy.app.task.Exs_LocalToDB;
import com.easy.app.task.Exs_MQToDB;
import com.easy.app.task.Exs_MailSend;
import com.easy.app.task.Exs_UpdateToDB;
import com.easy.app.task.Exs_XmlToClusterTask;
import com.easy.app.task.Exs_XmlToRule;
import com.easy.transfer.task.*;

public class DefaultJob {
	//文件交换适配
	public void Exs_DBToLocal(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_DBToLocal(param));
		gs.DoCommand();
	}
	public void Exs_LocalToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_LocalToMQ(param));
		gs.DoCommand();
	}
	public void Exs_MQToLocal(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToLocal(param));
		gs.DoCommand();
	}
	public void Exs_LocalToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_LocalToDB(param));
		gs.DoCommand();
	}
	public void Exs_LocalFloderToMerge(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_LocalFloderToMerge(param));
		gs.DoCommand();
	}
	public void Exs_LocalFloderToSplit(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_LocalFloderToSplit(param));
		gs.DoCommand();
	}
	//队列交换适配
	public void Exs_DBToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_DBToMQ(param));
		gs.DoCommand();
	}
	public void Exs_MQToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToDB(param));
		gs.DoCommand();
	}
	//传输交换适配
	public void Exs_MQToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToMQ(param));
		gs.DoCommand();
	}
	public void Exs_LocalToC(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_LocalToC(param));
		gs.DoCommand();
	}
	public void Exs_CToLocal(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_CToLocal(param));
		gs.DoCommand();
	}
	//api接口交换适配
	public void Exs_ApiPolling(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPolling(param));
		gs.DoCommand();
	}
	public void Exs_ApiPush(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPush(param));
		gs.DoCommand();
	}
	//转换交换适配
	
	
	//数据库直连适配
	public void Exs_EdiTab(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_EdiTab(param));
		gs.DoCommand();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** 废弃的功能 * */
	public void Exs_ApiPollingToInvoke(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ApiPollingToInvoke(param));
		gs.DoCommand();
	}
	public void Exs_UpdateToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_UpdateToDB(param));
		gs.DoCommand();
	}
	public void Exs_XmlToRule(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToRule(param));
		gs.DoCommand();
	}
	public void Exs_ListenServer(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ListenServer(param));
		gs.DoCommand();
	}
	public void Exs_XmlToClusterTask(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToClusterTask(param));
		gs.DoCommand();
	}
	public void Exs_AppMonitor(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_AppMonitor(param));
		gs.DoCommand();
	}
	public void Exs_MailSend(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MailSend(param));
		gs.DoCommand();
	}
	public void ExsCustXml(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new ExsCustXml(param));
		gs.DoCommand();
	}
}
