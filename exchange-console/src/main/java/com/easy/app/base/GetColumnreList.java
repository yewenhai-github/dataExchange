package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;

@WebServlet("/base/GetColumnreList")

public class GetColumnreList extends MainServlet{
	
	public void DoCommand() throws Exception{
		
		String FORMID=getRequest().getParameter("FORMID");
		String sort=getRequest().getParameter("sort");
		 if(sort == null || sort.length() <= 0)
		     sort = "CREATE_TIME";
		String order=getRequest().getParameter("order");
		if(order == null || order.length() <= 0)
			order = "desc";
		String sql=SQLMap.getSelect("GetColumnreList"); 
		 if(FORMID != null && FORMID.length() > 0)
			 sql=sql+" AND FORMID='"+FORMID+"' ";
		
       sql=sql+" ORDER BY "+sort+" "+order+"";
       int num1=Integer.valueOf(getRequest().getParameter("page")).intValue()-1;
       int num2=Integer.valueOf(getRequest().getParameter("rows")).intValue();
       JSONObject js= getDataAccess().GetTableJSONUI("rows", sql,num1,num2, sort);
		ReturnWriter(js.toString());
	}

}
