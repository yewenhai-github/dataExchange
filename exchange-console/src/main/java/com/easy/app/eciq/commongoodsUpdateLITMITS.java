package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.web.MainServlet;

@WebServlet("/forms/commongoodsUpdateLITMITS")
public class commongoodsUpdateLITMITS  extends MainServlet {

	private static final long serialVersionUID = 3594076222956321552L; 
	public commongoodsUpdateLITMITS ()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String CommandData = (String) getEnvDatas().get("CommandData");
		JSONObject json =new JSONObject(CommandData);
		JSONArray arr = json.getJSONArray("LimitData");
		//动态设置主键
		

      boolean bool = getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_LIMIT", arr, "LIMIT_ID");
       if(bool){
    	   ReturnMessage(true, "修改成功");
       }else{
    	   ReturnMessage(false, "修改失败");
       }
	}
}
