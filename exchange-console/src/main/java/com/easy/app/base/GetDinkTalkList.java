package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;

@WebServlet("/base/GetDinkTalkList")

public class GetDinkTalkList extends MainServlet{
	public void DoCommand() throws Exception{
		String sort=getRequest().getParameter("sort");
		 if(sort == null || sort.length() <= 0)
		     sort = "CREATE_TIME";
		String order=getRequest().getParameter("order");
		if(order == null || order.length() <= 0)
			order = "asc";
		
		String NAME=getRequest().getParameter("NAME");
		String TEMPLATE=getRequest().getParameter("TEMPLATE");
		
		String sql=SQLMap.getSelect("GetDinkTalkList"); 
		
		 if(NAME != null && NAME.length() > 0)
			 sql=sql+" AND NAME like '%"+NAME+"%' ";
		 if(TEMPLATE != null && TEMPLATE.length() > 0)
			 sql=sql+" AND TEMPLATE like '%"+TEMPLATE+"%' ";
       sql=sql+" ORDER BY "+sort+" "+order+"";
       int num1=Integer.valueOf(getRequest().getParameter("page")).intValue()-1;
       int num2=Integer.valueOf(getRequest().getParameter("rows")).intValue();
       JSONObject js= getDataAccess().GetTableJSONUI("rows", sql,num1,num2, sort);
		ReturnWriter(js.toString());
	}
}
