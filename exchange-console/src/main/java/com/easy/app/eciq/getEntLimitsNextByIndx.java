package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/getEntLimitsNextByIndx")
public class getEntLimitsNextByIndx   extends MainServlet{
	private static final long serialVersionUID = -7722225571192321843L;
	public getEntLimitsNextByIndx()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{ 
		String action = this.GetEnvDatas("action").toString();
		String limitRowNumber = this.GetEnvDatas("limitRowNumber").toString();
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
		String SQL = "Select * FROM (Select ENT_QUALIF_TYPE_CODE as ENTDECLCODE ,  ENT_QUALIF_NAME as ENTDECLNAME , DECL_LIMIT_ID as LIMITID , rownum as ROW_NUM FROM ITF_DCL_IO_DECL_LIMIT Where decl_no = @DECL_NO) T Where ROW_NUM = @ROWNUM";
		InitFormData("DeclData",SQL);
		if(this.GetTableRows("DeclData") > 0){
			ReturnMessage(true,"","",getFormDatas().toString());
		}
		else{
			ReturnMessage(false,msg);
		
		}
	}
}
