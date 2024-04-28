package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveInspDeclLimit")
public class SaveInspDeclLimit extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspDeclLimit() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		String Indx = GetDataValue("DeclLimitData", "DECL_LIMIT_ID");
		if (SysUtility.isEmpty(Indx)) {
			InitFormData("CheckExists","Select COUNT(DECL_LIMIT_ID) C FROM ITF_DCL_IO_DECL_LIMIT Where DECL_NO = @DeclLimitData.DECL_NO AND ENT_QUALIF_TYPE_CODE = @DeclLimitData.ENT_QUALIF_TYPE_CODE");
		}
		else{
			InitFormData("CheckExists","Select COUNT(DECL_LIMIT_ID) C FROM ITF_DCL_IO_DECL_LIMIT Where DECL_NO = @DeclLimitData.DECL_NO AND ENT_QUALIF_TYPE_CODE = @DeclLimitData.ENT_QUALIF_TYPE_CODE AND DECL_LIMIT_ID <> @DeclLimitData.DECL_LIMIT_ID");
		}
		
		
		if(GetDataValue("CheckExists","C").equals("0")){
			if (SysUtility.isEmpty(Indx)) {
				Indx = SysUtility.GetUUID();
				SaveToTable("DeclLimitData", "DECL_LIMIT_ID", Indx);
				//企业组织机构代码，改为从前台页面获取
				//SaveToTable("DeclLimitData", "ENT_ORG_CODE", SysUtility.getCurrentOrgId());
				if (!getDataAccess().Insert("ITF_DCL_IO_DECL_LIMIT",
						getFormDatas().getJSONArray("DeclLimitData"), "DECL_LIMIT_ID")) {
					Indx = "";
				}
			} else {
				//企业组织机构代码，改为从前台页面获取
				//SaveToTable("DeclLimitData", "ENT_ORG_CODE", SysUtility.getCurrentOrgId());
				if (!getDataAccess().Update("ITF_DCL_IO_DECL_LIMIT",
						getFormDatas().getJSONArray("DeclLimitData"), "DECL_LIMIT_ID")) {
					Indx = "";
				}
			}
			if (!SysUtility.isEmpty(Indx)) {
				ReturnMessage(true, "保存成功");
			} else {
				ReturnMessage(false, "保存失败");
			}			
		}
		else{
			ReturnMessage(false, "存在相同的资质名称，不可重复添加");
		}
	}
}