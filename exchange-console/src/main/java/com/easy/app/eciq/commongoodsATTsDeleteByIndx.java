package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/commongoodsATTsDeleteByIndx")
public class commongoodsATTsDeleteByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public commongoodsATTsDeleteByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String str = (String) getEnvDatas().get("CommandData");
		JSONObject json = new JSONObject(str);
		JSONArray arr = json.getJSONArray("LimitData");
		String infoMsg="";
		if(SysUtility.isNotEmpty(arr.getJSONObject(0).getString("ATT_ID"))){
			boolean isFlag =getDataAccess().ExecSQL("delete from  ITF_DCL_IO_DECL_ATT where ATT_ID=" + arr.getJSONObject(0).getString("ATT_ID"));
			if(isFlag)
				infoMsg="删除成功！";
				else
				  infoMsg="删除失败！";
			 ReturnMessage(true, infoMsg);
             getDataAccess().ComitTrans();
		}else{
			ReturnMessage(false,"请选择要删除的数据");
		}   
	}
}