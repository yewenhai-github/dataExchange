package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetILawDeclGoodsSelect")
public class GetILawDeclGoodsSelect    extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String DECL_NO = (String) getEnvDatas().get("DECL_NO");
		String SQL=SQLMap.getSelect("GetILawDeclGoodsSelect");	
		String strWhere="";
		if(getRequest().getParameter("Value")!=null)
		{
		   if(!getRequest().getParameter("Value").isEmpty())
		   {
			   StringBuffer strSQL = new StringBuffer();
			   strSQL.append(" and (PROD_HS_CODE like '%");
			   strSQL.append(getRequest().getParameter("Value"));
			   strSQL.append("%' or  DECL_GOODS_CNAME like '%");
			   strSQL.append(getRequest().getParameter("Value"));
			   strSQL.append("%')");
			strWhere+=strSQL;
			SQL+=strWhere;
		   }
		}
		if(SysUtility.isNotEmpty(DECL_NO)){
			String [] declNos=DECL_NO.split(",");
			String declNo="";
			for (int i = 0; i < declNos.length; i++) {
				declNo+="'"+declNos[i]+"',";
			}
			SQL+=" AND DECL_NO IN ("+declNo.substring(0,declNo.length()-1)+")";
		}else{
			SQL+=" AND DECL_NO IN ('0')";
		}
		String rt = getDataAccess().GetTable("S_ECIQ_HSCODE", SQL, null);
		ReturnMessage(true, "", "", rt, null,"","");
	}
}
