package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelDB2ComXmlByPIndx")
public class DelDB2ComXmlByPIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
	
		JSONArray params = GetCommandData("key");
		int Success=0;
		int Err=0;
		JSONObject jobj=new JSONObject();
	    if(SysUtility.isNotEmpty(params)){
	    	
	    	for(int i=0;i<params.length();i++){
	    		 jobj = (JSONObject)params.get(i);
				String upsql="UPDATE exs_config_dbtoxml_SQL SET IS_ENABLED='0' WHERE INDX=?";
				Datas datas = new Datas();
				try {
					getDataAccess().ExecSQL(upsql,SysUtility.getJsonField(jobj, "INDX"));
					Success++;
					getDataAccess().ComitTrans();
				} catch (Exception e) {
					Err++;
					getDataAccess().RoolbackTrans();
				}
			}	
	    }
	   
	    ReturnMessage(true, "失效成功'"+Success+"'条数据，失败'"+Err+"'条数据");
	}
}