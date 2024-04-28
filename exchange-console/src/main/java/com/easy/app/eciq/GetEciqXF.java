package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLHolder;
import com.easy.query.SQLMap;
import com.easy.query.SQLParser;
import com.easy.query.SimpleParamSetter;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetEciqXF")
public class GetEciqXF extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetEciqXF()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String APL_KIND=getRequest().getParameter("APL_KIND");
		String DECL_GET_NO=getRequest().getParameter("DECL_GET_NO");
		String DECL_NO=getRequest().getParameter("DECL_NO");
		String declRegNo=getRequest().getParameter("declRegNo");
		JSONObject SearchJson = new JSONObject();
		String DECL_TYPE="";
		if(SysUtility.isNotEmpty(APL_KIND)){
			if("I".equals(APL_KIND)){ 
				DECL_TYPE="1";
			}else if("O".equals(APL_KIND)){
				DECL_TYPE="2";
			}else{
				DECL_TYPE="";
			}
			SearchJson.put("DECL_TYPE", DECL_TYPE);
		}
		if(SysUtility.isNotEmpty(DECL_GET_NO)){
			SearchJson.put("DECL_GET_NO", "%"+DECL_GET_NO+"%");
		}
		SearchJson.put("DECL_NO", DECL_NO);
		SearchJson.put("DECL_REG_NO", declRegNo);
		String SQL = SQLMap.getSelect("GetEciqXF");
		SQLHolder holder = SQLParser.parse(SQL, SearchJson);
		Datas datas = SQLExecUtils.query4Datas(SysUtility.getCurrentConnection("xfsource"), "rows", 
				holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		ReturnWriter(datas.getDataJSON().toString()); 
	}
}