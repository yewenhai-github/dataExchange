package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/DeleteInspDeclContainer")
public class DeleteInspDeclContainer extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public DeleteInspDeclContainer()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		JSONArray arr = getFormDatas().getJSONArray("key");		
		if (arr.length()>0) {			
	         getDataAccess().BeginTrans();
		     int StatusCount = 0;//删除成功的条"
		     int Count = 0;//操作的条"
		        
			for(int i=0;i<arr.length();i++) {
				Count++;
				boolean isFlag =getDataAccess().ExecSQL("delete from  ITF_DCL_IO_DECL_CONT where CONT_ID= ? " , new String[]{ arr.getJSONObject(i).getString("CONT_ID")});
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
                ReturnMessage(true, ErrMsg,"",getFormDatas().toString());
                getDataAccess().ComitTrans();
			}
            else
            {
                ReturnMessage(false, "删除失败!");
                getDataAccess().RoolbackTrans();
            }
		}
	}
}
