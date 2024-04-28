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


@WebServlet("/forms/GetGoodAttrs")
public class GetGoodAttrs extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetGoodAttrs()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String SQL = SQLMap.getSelect("GetGoodAttrs");
		JSONObject SearchJson = new JSONObject();
		SQLHolder holder = SQLParser.parse(SQL, SearchJson);
		Datas datas = SQLExecUtils.query4Datas(SysUtility.getCurrentConnection("busi"), "rows", 
				holder.getSql(), new SimpleParamSetter(holder.getParamList()));
		ReturnWriter(datas.getDataJSON().toString()); 
	}
			
}