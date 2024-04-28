package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveInspGoodsLimits")
public class SaveInspGoodsLimits extends MainServlet {

	private static final long serialVersionUID = 3594076222956321552L;

	public SaveInspGoodsLimits() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		String Indx = GetDataValue("LimitData", "LIMIT_ID");

		if (SysUtility.isEmpty(Indx)) {
			Indx = SysUtility.GetUUID();
			SaveToTable("LimitData", "LIMIT_ID", Indx);
			if (!getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_LIMIT",
					getFormDatas().getJSONArray("LimitData"), "LIMIT_ID")) {
				Indx = "";
			}
		} else {
			if (!getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_LIMIT",
					getFormDatas().getJSONArray("LimitData"), "LIMIT_ID")) {
				Indx = "";
			}
		}
		if (!SysUtility.isEmpty(Indx)) {
			ReturnMessage(true, "保存成功");
		} else {
			ReturnMessage(false, "保存失败");
		}

		/*
		 * 
		 * String seqSql =
		 * "select SEQ_GOODS_LIMIT_ID.nextval limit_id from dual";
		 * 
		 * 
		 * JSONArray insertData = new JSONArray(); JSONArray updateDate = new
		 * JSONArray(); //动态设置主键 String TableName = "LimitData"; if
		 * (!SysUtility.isEmpty(GetDataValue("LimitData", "DECL_NO"))) {
		 * SaveToTable("LimitData", "GOODS_NO",
		 * GetNoFromGoods(GetDataValue("LimitData", "DECL_NO"))); } JSONArray
		 * rows = getFormDatas().getJSONArray(TableName); for (int i = 0; i <
		 * rows.length(); i++) { JSONObject row = (JSONObject)rows.get(i);
		 * String tempId = SysUtility.getJsonField(row, "LIMIT_ID");
		 * if(SysUtility.isEmpty(tempId)){ Datas data =
		 * getDataAccess().GetTableDatas("data", seqSql);
		 * SysUtility.putJsonField(row, "LIMIT_ID", data.GetTableValue("data",
		 * "LIMIT_ID")); insertData.put(row); }else{ updateDate.put(row); } }
		 * if(insertData.length()>0){ boolean InsertRt =
		 * getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_LIMIT",
		 * insertData,"LIMIT_ID"); if (!SysUtility.isEmpty(InsertRt)) {
		 * ReturnMessage(true, "保存成功"); } else { ReturnMessage(false, "保存失败"); }
		 * }else{ boolean updateRt =
		 * getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_LIMIT",
		 * updateDate,"LIMIT_ID"); if (!SysUtility.isEmpty(updateRt)) {
		 * ReturnMessage(true, "保存成功"); } else { ReturnMessage(false, "保存失败"); }
		 * }
		 */
	}

	private String GetNoFromGoods(String DECL_NO) throws LegendException {
		Datas dt = getDataAccess()
				.GetTableDatas(
						"GoodsList",
						"SELECT max(GOODS_NO) GOODS_NO FROM ITF_DCL_IO_DECL_GOODS_LIMIT WHERE DECL_NO = ?",
						DECL_NO);
		if (dt.GetTableRows("GoodsList") > 0) {
			if (dt.GetTableValue("GoodsList", "GOODS_NO").length() > 0) {
				return String.valueOf(Integer.parseInt(dt.GetTableValue(
						"GoodsList", "GOODS_NO")) + 1);
			} else {
				return "1";
			}
		} else {
			return "1";
		}
	}
}
