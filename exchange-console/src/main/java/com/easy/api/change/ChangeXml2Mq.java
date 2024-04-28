package com.easy.api.change;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/ChangeXml2Mq")
public class ChangeXml2Mq extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
//		DeleteDB("Xml2MqData", "exs_config_xmltomq");
		String isEnabled = (String)GetCommandData("Xml2MqData", "IS_ENABLED");
		if("1".equals(isEnabled)){
			SaveToTable("Xml2MqData", "IS_ENABLED", "0");
			SaveToDB("Xml2MqData", "exs_config_xmltomq");
			ReturnMessage(true, "废弃成功！");
		}else{
			SaveToTable("Xml2MqData", "IS_ENABLED", "1");
			SaveToDB("Xml2MqData", "exs_config_xmltomq");
			ReturnMessage(true, "恢复成功！");
		}
	}
}
