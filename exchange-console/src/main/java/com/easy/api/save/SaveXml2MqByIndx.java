package com.easy.api.save;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SaveXml2MqByIndx")
public class SaveXml2MqByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = (String) getEnvDatas().get("INDX");
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("Xml2MqData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("Xml2MqData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("Xml2MqData", "exs_config_xmltomq");
		ReturnMessage(true, "保存成功！");
		

	}
}
