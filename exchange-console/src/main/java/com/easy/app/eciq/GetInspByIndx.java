package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspByIndx")
public class GetInspByIndx extends MainServlet {
	private static final long serialVersionUID = 9133073429676698520L;

	public GetInspByIndx() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		String DECL_NO=(String) this.GetEnvDatas("DECL_NO");
		String allId=(String) this.GetEnvDatas("allId");
		String aplkind=getRequest().getParameter("aplkind");
		//如果是从合并进入详细页
		if(SysUtility.isNotEmpty(allId)){
			String INDX=allId.substring(0,allId.length()-1).split(",")[0];
			this.getEnvDatas().put("INDX", INDX);
			this.getEnvDatas().put("LoginUser", SysUtility.getCurrentUserIndx());
			InitFormData("DeclData", SQLMap.getSelect("GetEciqInspByMergeId"));
			SaveToTable("DeclData","TECH_REG_CODE",SysUtility.getCurrentOrgId());
			//合并时SPNO为空，所以取当前登录用户的报检单位号和名称
			SaveToTable("DeclData","DECL_REG_NO",SysUtility.getCurrentEntRegNo());
			SaveToTable("DeclData","DECL_REG_NAME",SysUtility.getCurrentEntName());
		}else{
			if(SysUtility.isEmpty(DECL_NO)&&SysUtility.isEmpty(allId)){
				this.getEnvDatas().put("LoginUser", SysUtility.getCurrentUserIndx());
				this.getEnvDatas().put("APL_KIND_I", aplkind);
				InitFormData("DeclData",SQLMap.getSelect("EciqInspDefault"));
				if(this.GetTableRows("DeclData") == 0){
					InitFormData("DeclData","Select @LoginUser as CREATOR, Now() as CREATE_TIME,TO_CHAR(Now(),'yyyyMMddhh24miss') || SUBSTR(SEQ_COMMON.NEXTVAL,-4,4) as CON_INDX,Now() as DECL_DATE from dual");
				}
				SaveToTable("DeclData","TECH_REG_CODE",SysUtility.getCurrentOrgId());
				/*注释掉取当前用户的值，从默认值表取
				 *SaveToTable("DeclData","DECL_REG_NO",SysUtility.getCurrentEntRegNo());
				  SaveToTable("DeclData","DECL_REG_NAME",SysUtility.getCurrentEntName());*/
			}
			else{
				if(SysUtility.isNotEmpty(DECL_NO)){
					InitFormData("DeclData", SQLMap.getSelect("GetEciqInspById"));
				}
			}
		}
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
