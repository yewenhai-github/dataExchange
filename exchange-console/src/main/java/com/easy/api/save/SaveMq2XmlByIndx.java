package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveMq2XmlByIndx")
public class SaveMq2XmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = (String) getEnvDatas().get("INDX");
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("Mq2XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("Mq2XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("Mq2XmlData", "exs_config_mqtoxml");
		ReturnMessage(true, "保存成功！");
		

	}
}