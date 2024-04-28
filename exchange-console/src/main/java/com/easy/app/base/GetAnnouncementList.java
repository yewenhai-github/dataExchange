package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;

@WebServlet("/flow/GetAnnouncementList")
public class GetAnnouncementList extends MainServlet{

	public GetAnnouncementList(){
		CheckLogin = false;
	}
	public void DoCommand() throws Exception{	
		String sort=getRequest().getParameter("sort");
		 if(sort == null || sort.length() <= 0)
		     sort = "indx";
		String order=getRequest().getParameter("order");
		if(order == null || order.length() <= 0)
			order = "desc";
		
		String TITLE=getRequest().getParameter("TITLE");
		String CREATED_BY=getRequest().getParameter("CREATED_BY");


		String sql=SQLMap.getSelect("GetAnnouncementList"); 
		
		if(TITLE != null && TITLE.length() > 0)
			 sql=sql+" AND TITLE like '%"+TITLE+"%' ";
		if(CREATED_BY != null && CREATED_BY.length() > 0)
			 sql=sql+" AND CREATED_BY like '%"+CREATED_BY+"%' ";
		
	  
		sql=sql+" ORDER BY " + sort +" "+order+"";
	    int num1=Integer.valueOf(getRequest().getParameter("page")).intValue()-1;
	    int num2=Integer.valueOf(getRequest().getParameter("rows")).intValue();
	    JSONObject js= getDataAccess().GetTableJSONUI("rows", sql,num1,num2, sort);
			ReturnWriter(js.toString());
	}
}
