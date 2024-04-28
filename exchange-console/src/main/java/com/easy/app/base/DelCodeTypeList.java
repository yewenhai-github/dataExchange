package com.easy.app.base;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.web.MainServlet;



@WebServlet("/base/DelCodeTypeList")
public class DelCodeTypeList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		
		String INDX = (String)GetEnvDatas("id");
		
		String SQL = "select * from S_BASE_CODE_TYPE where code_type in (select code_type from S_BASE_CODE_TYPE where indx="+INDX+") and is_enabled=1";
		List lst = SQLExecUtils.query4List(SQL);
		if(lst.size()>0)
		{
			ReturnMessage(false, "所删除的类型下存在关联数据，请先删除关联数据后，再删除该类型！");
		    return;
		}
		
		else{

			StringBuffer deleteSQLs = new StringBuffer();
			deleteSQLs.append("update S_BASE_CODE_TYPE set IS_ENABLED = '0'");
			deleteSQLs.append(" WHERE INDX in ( "+INDX+" )");
			getDataAccess().ExecSQL(deleteSQLs.toString());
			ReturnMessage(true, "删除成功!");
			
		}
	}


}
