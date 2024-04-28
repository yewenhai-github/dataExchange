package com.easy.api.convert.mess;


import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/gen/SaveMessFileLog")
public class SaveMessFileLog  extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		int Indx = 0;
		Datas formDatas = SessionManager.getFormDatas();
		JSONArray jsonarr = (JSONArray)formDatas.get("MessFileLogDATA");
		JSONObject json = jsonarr.getJSONObject(0);//获取json数组中的第一项  
		Indx = SaveToDB("MessFileLogDATA", "exs_convert_log");
		if (!SysUtility.isEmpty(Indx))
        {
        	ReturnMessage(true, "保存成功！");
        }
        else
        {
        	ReturnMessage(false, "保存失败！");	
        }
	}
}
