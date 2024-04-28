package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveInspDeclAtt")
public class SaveInspDeclAtt extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspDeclAtt()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String Indx = GetDataValue("AttData", "ATT_ID");
		
		if (SysUtility.isEmpty(Indx)) {
			InitFormData("CheckExists","Select COUNT(ATT_ID) C FROM ITF_DCL_IO_DECL_ATT Where ATT_DOC_NO = @AttData.ATT_DOC_NO AND DECL_NO = @AttData.DECL_NO ");
		}
		else{
			InitFormData("CheckExists","Select COUNT(ATT_ID) C FROM ITF_DCL_IO_DECL_ATT Where ATT_DOC_NO = @AttData.ATT_DOC_NO AND ATT_ID <> @AttData.ATT_ID AND DECL_NO = @AttData.DECL_NO ");
		}

		/*if(GetDataValue("CheckExists","C").equals("0")){*/
	     if (SysUtility.isEmpty(Indx)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SaveToTable("AttData", "OPER_TIME", df.format(new Date()));
				Indx = SysUtility.GetUUID();
				SaveToTable("AttData", "ATT_ID", Indx);
				if (!getDataAccess().Insert("ITF_DCL_IO_DECL_ATT",
						getFormDatas().getJSONArray("AttData"), "ATT_ID")) {
					Indx = "";
				}
			} else {
				if (!getDataAccess().Update("ITF_DCL_IO_DECL_ATT",
						getFormDatas().getJSONArray("AttData"), "ATT_ID")) {
					Indx = "";
				}
			}
			if (!SysUtility.isEmpty(Indx)) {
				ReturnMessage(true, "保存成功");
			} else {
				ReturnMessage(false, "保存失败");
			}	
		/*}
		else{
			ReturnMessage(false, "存在相同的单据号码，不可重复添加");
		}*/
	}
}
