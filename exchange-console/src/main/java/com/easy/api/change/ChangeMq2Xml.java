package com.easy.api.change;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/ChangeMq2Xml")
public class ChangeMq2Xml extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String isEnabled = (String)GetCommandData("Mq2XmlData", "IS_ENABLED");
		if("1".equals(isEnabled)){
			SaveToTable("Mq2XmlData", "IS_ENABLED", "0");
			SaveToDB("Mq2XmlData", "exs_config_mqtoxml");
			ReturnMessage(true, "失效成功！");
		}else{
			SaveToTable("Mq2XmlData", "IS_ENABLED", "1");
			SaveToDB("Mq2XmlData", "exs_config_mqtoxml");
			ReturnMessage(true, "恢复成功！");
		}
	}
}
