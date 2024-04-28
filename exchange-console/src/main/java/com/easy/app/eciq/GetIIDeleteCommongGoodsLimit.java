package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetIIDeleteCommongGoodsLimit")
public class GetIIDeleteCommongGoodsLimit extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public GetIIDeleteCommongGoodsLimit()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String str = (String) getEnvDatas().get("CommandData");
		JSONObject json = new JSONObject(str);
		JSONArray arr = json.getJSONArray("key");		
		if (arr.length()>0) {			
	         getDataAccess().BeginTrans();
		     int StatusCount = 0;//删除成功的条"
		     int Count = 0;//操作的条"
		        
			for(int i=0;i<arr.length();i++) {
				Count++;
				boolean isFlag =getDataAccess().ExecSQL("delete from  ITF_DCL_IO_DECL_GOODS_LIMIT where LIMIT_ID=" + arr.getJSONObject(i).getString("LIMIT_ID"));
				if(isFlag){
					StatusCount++;
				}
			}

			String ErrMsg = "";
			if(StatusCount>0){
				ErrMsg = "操作"+Count+"条数据！<br>成功删除" + StatusCount + "条！<br>";
				if(Count>StatusCount){
					ErrMsg += "删除失败" + (Count-StatusCount) + "条！";
				}
                ReturnMessage(true, ErrMsg);
                getDataAccess().ComitTrans();
			}
            else
            {
                ReturnMessage(false, "操作失败");
                getDataAccess().RoolbackTrans();
            }
		}
	}
}
