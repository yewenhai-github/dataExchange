package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/getGoodsLimitsNextByIndx")
public class getGoodsLimitsNextByIndx   extends MainServlet{
	private static final long serialVersionUID = -7722225571192321843L;
	public getGoodsLimitsNextByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String action = getRequest().getParameter("action");
		String limitId = getRequest().getParameter("limitId");
		String GOODS_ID = getRequest().getParameter("GoodsId");
		String declNo = getRequest().getParameter("DECL_NO");
		String limitRowNumber = getRequest().getParameter("limitRowNumber");
		int row = 0;
		if(!SysUtility.isEmpty(limitRowNumber)){
			row = Integer.parseInt(limitRowNumber);
		}
		String msg = "";
		if(action.equals("next")){
			row ++;
			msg = "没有下一条数据";
		}
		else{
			row --;
			msg = "没有上一条数据";
		}
		this.getEnvDatas().put("ROWNUM", String.valueOf(row));
		String SQL = "Select * FROM (Select LICENCE_NO as UNLIMIT_CODE ,  LIC_NAME as ENTDECLNAME , LIMIT_ID as LIMITID , rownum as ROW_NUM FROM ITF_DCL_IO_DECL_GOODS_LIMIT Where decl_no = @DECL_NO) T Where ROW_NUM = @ROWNUM";
		InitFormData("GoodsData",SQL);
		if(this.GetTableRows("GoodsData") > 0){
			ReturnMessage(true,"","",getFormDatas().toString());
		}
		else{
			ReturnMessage(false,msg);
		
		}
		/*String sqlWhere=" where 1=1 "; 
		if(action.equals("next"))
		{
			if(!limitId.isEmpty())
				sqlWhere+=" and row_num>"+limitRowNumber+" ";
			 String SQL="select t.*  from ( Select  a.*,rownum row_num from ITF_DCL_IO_DECL_GOODS_LIMIT a Where a.decl_no='"+declNo+"' and a.GOODS_ID='"+GOODS_ID+"') t "+sqlWhere+" and rownum=1";
			 InitFormData("GoodsData",SQL);
			if(this.GetTableRows("GoodsData") > 0){
					ReturnMessage(true,"","",getFormDatas().toString());
			}
			// ReturnWriter(GetReturnDatas("@"+SQL,"LimitDATA").toString());
		}
		else
		{
			if(!limitId.isEmpty())
				sqlWhere+=" and row_num<"+limitRowNumber+" ";
			 String SQL="select t.*  from ( Select  a.*,rownum row_num from ITF_DCL_IO_DECL_GOODS_LIMIT a Where a.decl_no='"+declNo+"' and a.GOODS_ID='"+GOODS_ID+"'  order by rownum desc ) t "+sqlWhere+" and rownum=1";
			 ReturnWriter(GetReturnDatas("@"+SQL,"LimitDATA").toString());
		}*/
	}
}
