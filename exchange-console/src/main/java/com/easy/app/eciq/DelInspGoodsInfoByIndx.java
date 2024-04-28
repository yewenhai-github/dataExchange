package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/DelInspGoodsInfoByIndx")
public class DelInspGoodsInfoByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public DelInspGoodsInfoByIndx() {
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		String INDX = this.getRequest().getParameter("INDX"); 
		if(SysUtility.isEmpty(INDX)){
			ReturnMessage(false, "数据错误:没有主键");			
		}
		else{
			this.InitFormData("GNO", "Select ifnull(GOODS_NO,0) as GOODS_NO FROM ITF_DCL_IO_DECL_GOODS Where GOODS_ID = @INDX");
			this.getDataAccess().ExecSQL("Delete ITF_DCL_IO_DECL_GOODS WHERE GOODS_ID = ?", new Object[]{INDX});
			String gno = this.GetDataValue("GNO", "GOODS_NO");
			if(!SysUtility.isEmpty(gno)){
				getDataAccess().ExecSQL("Update ITF_DCL_IO_DECL_GOODS Set GOODS_NO = GOODS_NO - 1 Where GOODS_NO > " + gno);
			}
			ReturnMessage(true, "");
		}

	}

}
