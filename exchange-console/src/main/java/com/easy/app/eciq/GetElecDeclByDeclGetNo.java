package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetElecDeclByDeclGetNo")
public class GetElecDeclByDeclGetNo extends MainServlet {
	private static final long serialVersionUID = 9133073429676698520L;

	public GetElecDeclByDeclGetNo() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		//String srcIndx=(String) this.GetEnvDatas("INDX");
		String DECL_GET_NO=(String) this.GetEnvDatas("DECL_GET_NO");
		String FLAG=(String) this.GetEnvDatas("FLAG");
		String CERT_NO=(String) this.GetEnvDatas("CERT_NO");
		String sql = "";
		if(FLAG.equals("NEW") && (SysUtility.isEmpty(CERT_NO))){
		    sql=String.format("Select Decl_No,DECL_GET_NO,decl_status_name,Decl_Date,DECL_PERSN_CERT_NO,CONT_TEL from ITF_DCL_IO_DECL where DECL_GET_NO = '%s'",DECL_GET_NO);
		    InitFormData("ElecDeclData", sql.toString());
		}
		else{
			if(SysUtility.isNotEmpty(DECL_GET_NO)){
				InitFormData("ElecDeclData", (SQLMap.getSelect("GetElecDeclByDeclGetNo") + " and src.DECL_GET_NO='"+DECL_GET_NO+"'"));
			}
		}
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
