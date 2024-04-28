package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetHsCodeSelect")
public class GetHsCodeSelect    extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
	String SQL="SELECT * FROM (Select H.*,H.ITEM_NAME as HSNAME,H.ITEM_ENAME as HSENAME, STD_MEASURE_CODE || '/' || ifnull(Q.ITEM_NAME,W.ITEM_NAME) as STD_MEASURE_NAME ,ifnull(Q.ITEM_NAME,W.ITEM_NAME) as STD_MEASURE_CODE_NAME from S_ECIQ_HSCODE H LEFT JOIN S_ECIQ_QTY_UNIT Q ON H.STD_MEASURE_CODE = Q.ITEM_CODE AND H.MEASURE_TYPE_CODE IN(1,3,4) LEFT JOIN S_ECIQ_WEIGHT_UNIT W ON H.STD_MEASURE_CODE = W.ITEM_CODE AND H.MEASURE_TYPE_CODE = 2 ORDER BY H.ITEM_CODE asc) WHERE ROWNUM<=20";	
		String strWhere="";
		if(getRequest().getParameter("Value")!=null)
		{
		   if(!getRequest().getParameter("Value").isEmpty())
		   {
			   StringBuffer strSQL = new StringBuffer();
			   strSQL.append(" and (ITEM_CODE like '%");
			   strSQL.append(getRequest().getParameter("Value"));
			   strSQL.append("%' or  ITEM_NAME like '%");
			   strSQL.append(getRequest().getParameter("Value"));
			   strSQL.append("%')");
			strWhere+=strSQL;
			SQL+=strWhere;
		   }
		}
		String rt = getDataAccess().GetTable("S_ECIQ_HSCODE", SQL, null);
		ReturnMessage(true, "", "", rt, null,"","");
	}
}
