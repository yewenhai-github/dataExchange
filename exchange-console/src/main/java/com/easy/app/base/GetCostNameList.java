package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;

@WebServlet("/base/GetCostNameList")

public class GetCostNameList extends MainServlet{
	public void DoCommand() throws Exception{
		String FRT_CODE=getRequest().getParameter("FRT_CODE");
		String FRT_ABBREV=getRequest().getParameter("FRT_ABBREV");
		String FRT_NAME_EN=getRequest().getParameter("FRT_NAME_EN");
		String FRT_SYSTEM_TYPE=getRequest().getParameter("FRT_SYSTEM_TYPE");
		String sort=getRequest().getParameter("sort");
		 if(sort == null || sort.length() <= 0)
		     sort = "ORDERNUM";
		String order=getRequest().getParameter("order");
		if(order == null || order.length() <= 0)
			order = "asc";
		String sql=SQLMap.getSelect("GetCostNameList"); 
		 if(FRT_CODE != null && FRT_CODE.length() > 0)
			 sql=sql+" AND FRT_CODE like '%"+FRT_CODE+"%' ";
		 if(FRT_ABBREV != null && FRT_ABBREV.length() > 0)
			 sql=sql+" AND FRT_ABBREV like '%"+FRT_ABBREV+"%' ";
		 if(FRT_NAME_EN != null && FRT_NAME_EN.length() > 0)
			 sql=sql+" AND FRT_NAME_EN like '%"+FRT_NAME_EN+"%' ";
		 if(FRT_SYSTEM_TYPE != null && FRT_SYSTEM_TYPE.length() > 0)
			 sql=sql+" AND FRT_SYSTEM_TYPE like '%"+FRT_SYSTEM_TYPE+"%' ";
       sql=sql+" ORDER BY "+sort+" "+order+"";
       int num1=Integer.valueOf(getRequest().getParameter("page")).intValue()-1;
       int num2=Integer.valueOf(getRequest().getParameter("rows")).intValue();
       JSONObject js= getDataAccess().GetTableJSONUI("rows", sql,num1,num2, sort);
		ReturnWriter(js.toString());
	}

}
