package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.web.MainServlet;

@WebServlet("/forms/SavePack")
public class SavePack extends MainServlet {
	private static final long serialVersionUID = 4261239235630641154L;

	public SavePack() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		getFormDatas().getJSONArray("GoodsPack");
		String goodsId = GetDataValue("GoodsPack", "GOODS_ID");
		String[] gId = goodsId.split(",");
		String packTypeCode = GetDataValue("GoodsPack", "PACK_TYPE_CODE");
		String packCatgName = GetDataValue("GoodsPack", "PACK_CATG_NAME");
		String packTypeShort = GetDataValue("GoodsPack", "PACK_CATG_NAME");
		String packQty = GetDataValue("GoodsPack", "QTY");

		for (int i = 0; i < gId.length; i++) {
			String SQL = "DELETE FROM ITF_DCL_IO_DECL_GOODS_PACK WHERE GOODS_ID = ?";
			String[] ps = new String[] { gId[i] };
			getDataAccess().ExecSQL(SQL, ps);
			String GoodsSQL = "";
			String[] Goodsps = null;
			SQL = "select sys_guid() as GUID from dual";
			Datas dataGuId = getDataAccess().GetTableDatas("GUID", SQL);
			String GUID = dataGuId.GetTableValue("GUID", "GUID");
			SQL = "select DECL_NO, GOODS_NO from ITF_DCL_IO_DECL_GOODS WHERE GOODS_ID = ?";
			Datas dataGoods = getDataAccess().GetTableDatas("ITF_DCL_IO_DECL_GOODS", SQL, ps);
			String declNo = dataGoods.GetTableValue("ITF_DCL_IO_DECL_GOODS", "DECL_NO");
			String goodsNo = dataGoods.GetTableValue("ITF_DCL_IO_DECL_GOODS", "GOODS_NO");

			if (i == 0) {
				GoodsSQL = "INSERT INTO ITF_DCL_IO_DECL_GOODS_PACK(PACK_ID, GOODS_ID, DECL_NO, GOODS_NO, PACK_TYPE_CODE, PACK_CATG_NAME, PACK_TYPE_SHORT, PACK_QTY, IS_MAIN_PACK) "
						+ "VALUES(?,?,?,?,?,?,?,?,?)";
				Goodsps = new String[] { GUID, gId[i], declNo, goodsNo, packTypeCode, packCatgName, packTypeShort,
						packQty, "1" };

			} else {
				GoodsSQL = "INSERT INTO ITF_DCL_IO_DECL_GOODS_PACK(PACK_ID, GOODS_ID, DECL_NO, GOODS_NO, PACK_TYPE_CODE, PACK_CATG_NAME, PACK_TYPE_SHORT, IS_MAIN_PACK) "
						+ "VALUES(?,?,?,?,?,?,?,?)";
				Goodsps = new String[] { GUID, gId[i], declNo, goodsNo, packTypeCode, packCatgName, packTypeShort,
						"1" };
			}
			getDataAccess().ExecSQL(GoodsSQL, Goodsps);
		}
	}
}
