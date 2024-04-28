package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/forms/DelInspContainerByIndx")
public class DelInspContainerByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public DelInspContainerByIndx() {
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		String INDX = this.getRequest().getParameter("INDX"); 
		if(SysUtility.isEmpty(INDX)){
			ReturnMessage(false, "数据错误:没有主键");			
		}
		else{
			this.getDataAccess().ExecSQL("Delete ITF_DCL_IO_DECL_CONT WHERE CONT_ID = ?", new Object[]{INDX});
			ReturnMessage(true, "");
		}

	}

}
