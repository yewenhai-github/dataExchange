package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/SaveInspGoodsCont")
public class SaveInspGoodsCont extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspGoodsCont()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String Indx = GetDataValue("goodsContData", "GOODS_CONT_ID");
		
		if (SysUtility.isEmpty(Indx)) {
			Indx = SysUtility.GetUUID();
			SaveToTable("goodsContData", "GOODS_CONT_ID", Indx);
			if (!getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_CONT",
					getFormDatas().getJSONArray("goodsContData"), "GOODS_CONT_ID")) {
				Indx = "";
			}
		} else {
			if (!getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_CONT",
					getFormDatas().getJSONArray("goodsContData"), "GOODS_CONT_ID")) {
				Indx = "";
			}
		}
	     if (!SysUtility.isEmpty(Indx))
	     {
	        	ReturnMessage(true, "保存成功");
	     }
	     else
	     {
	        	ReturnMessage(false, "保存失败");	
	     }
	}
			
}