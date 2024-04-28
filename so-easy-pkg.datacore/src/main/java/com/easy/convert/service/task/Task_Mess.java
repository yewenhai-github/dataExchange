package com.easy.convert.service.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.app.proxy.ServiceProxy;
import com.easy.convert.service.mess.Message_TransferDb;
import com.easy.convert.service.mess.Message_TransferForLocal;
import com.easy.convert.service.mess.Message_TransferHander;
import com.easy.convert.service.mess.Message_TransferMq;
import com.easy.convert.service.mess.Message_TransferOrcale;
import com.easy.convert.service.mess.Message_TransferUpMq;
import com.easy.convert.service.mess.Message_TransferFoleSprict;

public class Task_Mess {
	
   //本地excel配置模式（电子口岸正在使用  持续优化中）
	public void MessageHead_Transfer(String param)throws Exception{
			IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferForLocal(param));
			gs.DoCommand();	
	}
	//数据库配置模式（excel转换成xml，xml转换成xml）
	public void MessageHead_TransferOracle(String param)throws Exception{
			IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferOrcale(param));
			gs.DoCommand();	
	}
	
	//数据库配置模式（db数据库表配置转化成xml方法）
	public void MessageHead_TransferDb(String param)throws Exception{
			IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferDb(param));
			gs.DoCommand();	
	}
		
	//指定服务器发送消息队列(未使用)
	public void MessageHead_TransferMq(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferMq(param));
		gs.DoCommand();
	}
	
	
	//读取hander任务表（读取任务表exs_handle_sender）
	public void MessageHead_Hander(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferHander(param));
		gs.DoCommand();
	}
	
	//上传MQ (发送mq：active、ms mq、ibmmq、FTP)
	public void MessageHead_UpMq(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferUpMq(param));
		gs.DoCommand();
	}
	
	//分拣只上传文件
	public void Message_TransferFoleSprict(String param)throws Exception{
		IGlobalService gs = (IGlobalService)ServiceProxy.getInstance().newProxyInstance(new Message_TransferFoleSprict(param));
		gs.DoCommand();
	}

}
