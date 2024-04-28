package com.easy.transfer.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;

public class DefaultJob {
	/**
	 * 报文从mq生成与发送任务
	 */
	public void MQProcess(String param)throws Exception{
		Exs_MQToXml(param);
		Exs_XmlToMQ(param);
	}
	/**
	 * 报文发送到MQ中
	 * */
	public void Exs_XmlToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_XmlToMQ(param));
		gs.DoCommand();
	}

	/**
	 * 从MQ中生成报文
	 * */
	public void Exs_MQToXml(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToXml(param));
		gs.DoCommand();
	}

	public void Exs_MQToMQ(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Exs_MQToMQ(param));
		gs.DoCommand();
	}
}
