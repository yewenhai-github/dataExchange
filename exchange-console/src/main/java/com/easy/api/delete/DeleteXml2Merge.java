package com.easy.api.delete;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.Utility;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DeleteXml2Merge")
public class DeleteXml2Merge extends MainServlet {
	private static final long serialVersionUID = 1947026515096977015L;

	public void DoCommand() throws Exception{
		int SuccessCount = 0;//成功总数。
		int ErrorCount = 0;//失败总数。
		try {
			JSONArray params = null;
			String indx = (String)GetEnvDatas("INDX");//详细页触发
			if(SysUtility.isNotEmpty(indx)){
				params = new JSONArray();
				
				JSONObject obj = new JSONObject();
				obj.put("INDX", indx);
				params.put(obj);
			}else{
				params = GetCommandData("key");
			}
			if(SysUtility.isEmpty(params) || params.length() <= 0){
				ReturnMessage(false, "当前操作操作失败！传输数据行数："+params.length());
				return;
			}
			String conditions = Utility.GetIndxConditions(params); 

			String sqlent = "select * from exs_config_xmltomerge where 1=1 "+conditions;
			List listent = SQLExecUtils.query4List(sqlent); 
			for (int i = 0; i < listent.size(); i++) {
				HashMap map = (HashMap)listent.get(i);
			    String INDX = (String)map.get("indx"); 
			    if("1".equals((String)map.get("audit_flag"))) {
			    	ErrorCount++;
			    	continue;
			    }
			    boolean falg=getDataAccess().ExecSQL("update exs_config_xmltomerge set IS_ENABLED='0' where INDX='"+INDX+"'");	
				if(falg){					
					SuccessCount++;
				}else{
					ErrorCount++;
				}				
			}
		} catch (Exception e) {	
			getDataAccess().RoolbackTrans();
		} finally{			
			getDataAccess().ComitTrans();
		}
		if(ErrorCount > 0){
			ReturnMessage(true, "删除成功"+SuccessCount+"条数据,<br/>删除失败"+ErrorCount+"条数据(数据已审核)");
		}else{
			ReturnMessage(true, "删除成功"+SuccessCount+"条数据");
		}
	}
}
