package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.access.Datas;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetAnyXml2DBByIndx")
public class GetAnyXml2DBByIndx extends MainServlet{
	private static final long serialVersionUID = -8810135130442923052L;

	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
        if(SysUtility.isEmpty(indx)){
        	ReturnMessage(false, "参数错误,Indx为空！");
        	return;
        }
		InitFormData("XmlData", SQLMap.getSelect("GetAnyXml2DBByIndx"));
//		Datas data = getFormDatas();
//		if(SysUtility.IsMySqlDB() && SysUtility.isNotEmpty(data) && data.has("rows")) {
//    		data.put("rows", SysUtility.JSONArrayToUpperCase((JSONArray)data.get("rows")));
//    	}
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
