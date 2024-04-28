package com.easy.api.convert.mess;


import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/DeleteMessFileLog")
public class DeleteMessFileLog   extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String ids = (String) getEnvDatas().get("ids");
		//批量删除
		if(SysUtility.isNotEmpty(ids)) {
			String sql = "delete from exs_convert_log where INDX in("+ids+")";
			boolean executeUpdate = SQLExecUtils.executeUpdate(sql);
			if(executeUpdate) {
				ReturnMessage(true, "删除成功！");
			}else {
				ReturnMessage(false, "删除失败！");
			}
			return;
			
		}
		if(SysUtility.isNotEmpty(GetDataValue("MessFileLogDATA", "INDX")))
		{
			DeleteDB("MessFileLogDATA", "exs_convert_log");
			ReturnMessage(true, "删除成功！");
		}else{
			ReturnMessage(false, "信息不存在！", "","");
		}
	}
}