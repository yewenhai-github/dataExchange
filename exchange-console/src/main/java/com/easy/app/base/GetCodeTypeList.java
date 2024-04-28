package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;

@WebServlet("/base/GetCodeTypeList")
public class GetCodeTypeList extends MainServlet{
	public void DoCommand() throws Exception{
		String CREATE_TIME=getRequest().getParameter("CREATE_TIME");
		String CODE_GRADE=getRequest().getParameter("CODE_GRADE");
		String CODE_TYPENAME=getRequest().getParameter("CODE_TYPENAME");
		String CODE_TYPE=getRequest().getParameter("CODE_TYPE");
		String sort=getRequest().getParameter("sort");
		 if(sort == null || sort.length() <= 0)
		     sort = "CODE_GRADE";
		String order=getRequest().getParameter("order");
		if(order == null || order.length() <= 0)
			order = "desc";
		String sql=SQLMap.getSelect("GetCodeTypeList");
		 if(CODE_GRADE != null && CODE_GRADE.length() > 0)
			 sql=sql+" AND CODE_GRADE like '%"+CODE_GRADE+"%' ";
		 if(CREATE_TIME != null && CREATE_TIME.length() > 0)
			 sql=sql+" AND CREATE_TIME like '%"+CREATE_TIME+"%' ";
		 if(CODE_TYPE != null && CODE_TYPE.length() > 0)
			 sql=sql+" AND CODE_TYPE like '%"+CODE_TYPE+"%' ";
		 if(CODE_TYPENAME != null && CODE_TYPENAME.length() > 0)
			 sql=sql+" AND CODE_TYPENAME like '%"+CODE_TYPENAME+"%' ";
       sql=sql+" ORDER BY "+sort+" ,CREATE_TIME "+order+"";
       int num1=Integer.valueOf(getRequest().getParameter("page")).intValue()-1;
       int num2=Integer.valueOf(getRequest().getParameter("rows")).intValue();
       JSONObject js= getDataAccess().GetTableJSONUI("rows", sql,num1,num2, sort);
		ReturnWriter(js.toString());
	}

}
