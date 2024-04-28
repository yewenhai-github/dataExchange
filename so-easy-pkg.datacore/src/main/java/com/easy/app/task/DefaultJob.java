package com.easy.app.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;

public class DefaultJob {

	/**
	 * 从数据库生成报文
	 * */
	public void Exs_DBToXml(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_DBToXml(param));
		gs.DoCommand();
	}

	/**
	 * 报文入库
	 * */
	public void Exs_XmlToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToDB(param));
		gs.DoCommand();
	}
	
	/**
	 * 更新数据库
	 * */
	public void Exs_UpdateToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_UpdateToDB(param));
		gs.DoCommand();
	}
	
	/**
	 * 目录过电子审单-未启用
	 * */
	public void Exs_XmlToRule(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToRule(param));
		gs.DoCommand();
	}
	
	/**
	 * 服务器监听-未启用
	 * */
	public void Exs_ListenServer(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_ListenServer(param));
		gs.DoCommand();
	}
	
	/**
	 * 报文目录分拣合并
	 * */
	public void Exs_XmlToFloderMerge(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToFloderMerge(param));
		gs.DoCommand();
	}
	
	/**
	 * 报文目录分拣拆分
	 * */
	public void Exs_XmlToFloderSplit(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToFloderSplit(param));
		gs.DoCommand();
	}
	
	/**
	 * 集群任务采集-未启用
	 * */
	public void Exs_XmlToClusterTask(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToClusterTask(param));
		gs.DoCommand();
	}
	
	/**
	 * 监控各个App的运行状况
	 * */
	public void Exs_AppMonitor(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_AppMonitor(param));
		gs.DoCommand();
	}
	
	/**
	 * 自动发送邮件模块
	 * */
	public void Exs_MailSend(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MailSend(param));
		gs.DoCommand();
	}
	/**
	 * 读取T_CUST_DXP_DATA,将报文落地
	 * */
	public void ExsCustXml(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new ExsCustXml(param));
		gs.DoCommand();
	}



	public void Exs_DBToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_DBToMQ(param));
		gs.DoCommand();
	}
	public void Exs_MQToDB(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToDB(param));
		gs.DoCommand();
	}
}
