package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveInspGoodsPackType")
public class SaveInspGoodsPackType extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspGoodsPackType() {
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		String Indx = GetDataValue("PackType", "PACK_ID");
		String IS_MAIN_PACK = GetDataValue("PackType", "IS_MAIN_PACK");

		if (SysUtility.isEmpty(Indx)) {
			InitFormData("CheckExists","Select COUNT(PACK_ID) C FROM ITF_DCL_IO_DECL_GOODS_PACK Where DECL_NO = @PackType.DECL_NO AND GOODS_ID = @PackType.GOODS_ID AND PACK_TYPE_CODE = @PackType.PACK_TYPE_CODE");
			InitFormData("CheckExistsM","Select COUNT(PACK_ID) C FROM ITF_DCL_IO_DECL_GOODS_PACK Where DECL_NO = @PackType.DECL_NO AND GOODS_ID = @PackType.GOODS_ID AND IS_MAIN_PACK = @PackType.IS_MAIN_PACK");
		}
		else{
			InitFormData("CheckExists","Select COUNT(PACK_ID) C FROM ITF_DCL_IO_DECL_GOODS_PACK Where DECL_NO = @PackType.DECL_NO AND GOODS_ID = @PackType.GOODS_ID AND PACK_TYPE_CODE = @PackType.PACK_TYPE_CODE AND PACK_ID <> @PackType.PACK_ID");
			InitFormData("CheckExistsM","Select COUNT(PACK_ID) C FROM ITF_DCL_IO_DECL_GOODS_PACK Where DECL_NO = @PackType.DECL_NO AND GOODS_ID = @PackType.GOODS_ID AND IS_MAIN_PACK = @PackType.IS_MAIN_PACK AND PACK_ID <> @PackType.PACK_ID");
		}
		if(GetDataValue("CheckExists","C").equals("0")){
			if(IS_MAIN_PACK.equals("0")||(IS_MAIN_PACK.equals("1") && GetDataValue("CheckExistsM","C").equals("0"))){
				if (SysUtility.isEmpty(Indx)) {
					Indx = SysUtility.GetUUID();
					SaveToTable("PackType", "PACK_ID", Indx);
					if (!getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_PACK",
							getFormDatas().getJSONArray("PackType"), "PACK_ID")) {
						Indx = "";
					}
				} else {
					if (!getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_PACK",
							getFormDatas().getJSONArray("PackType"), "PACK_ID")) {
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
				ReturnMessage(false,"只能有一个主要包装");
			}
		}
		else{
			ReturnMessage(false,"包装类型不能重复");
		}
		
	}

}