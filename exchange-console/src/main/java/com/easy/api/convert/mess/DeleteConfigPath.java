package com.easy.api.convert.mess;


import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DeleteConfigPath")
public class DeleteConfigPath   extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String ids = (String) getEnvDatas().get("ids");
		//删除
		if(SysUtility.isNotEmpty(ids)) {
			String strMessLogId = "select * from exs_convert_log where CONFIG_PATH_ID in("+ids+")";
			List query4List2 = SQLExecUtils.query4List(strMessLogId);
			boolean executeUpdate;
			if(query4List2.size() > 0)
			{
				executeUpdate =SQLExecUtils.executeUpdate("update exs_convert_config_path set VALID_STATE = '0' where indx in("+ids+")");				
			}else{
				SQLExecUtils.executeUpdate("delete from exs_convert_config_name where p_indx in("+ids+")");
				String sql = "delete from exs_convert_config_path where indx in("+ids+")";
				executeUpdate = SQLExecUtils.executeUpdate(sql);							
			}
			if(executeUpdate) {
				ReturnMessage(true, "删除成功！");
			}else {
				ReturnMessage(false, "删除失败！");
			}	
			return;			
		}
		/*if(SysUtility.isNotEmpty(GetDataValue("MESSDATA", "INDX")))
		{
			SQLExecUtils.executeUpdate("delete from exs_convert_config_name where p_indx in("+GetDataValue("MESSDATA", "INDX")+")");
			DeleteDB("MESSDATA", "exs_convert_config_path");
			ReturnMessage(true, "删除成功！");
		}else{
			ReturnMessage(false, "信息不存在！", "","");
		}*/
	}
}