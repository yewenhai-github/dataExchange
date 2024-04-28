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


@WebServlet("/forms/GetCommonDeclNo")
public class GetCommonDeclNo extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCommonDeclNo()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
//		String SQL = SQLMap.getSelect("GetCommonDeclNo");
//		JSONObject SearchJson = new JSONObject();
//		SearchJson.append("part_id", SysUtility.getCurrentPartId());
////		this.AddToSearchTable("part_id", SysUtility.getCurrentPartId());
//		SQLHolder holder = SQLParser.parse(SQL, SearchJson);
//		Datas datas = SQLExecUtils.query4Datas(SysUtility.getCurrentConnection("busi"), "rows", 
//				holder.getSql(), new SimpleParamSetter(holder.getParamList()));
//		ReturnWriter(datas.getDataJSON().toString()); 
		this.AddToSearchTable("part_id", SysUtility.getCurrentPartId());
		ReturnWriter(GetReturnDatas("GetCommonDeclNo").toString());
	}
			
}