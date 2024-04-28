package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetCCSaveGood")
public class GetCCSaveGood extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSaveGood() {
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		// 判断选择保存类型
		String inspRadio = GetDataValue("GoodsData", "insp");
		String custRadio = GetDataValue("GoodsData", "cust");
		String allRadio = GetDataValue("GoodsData", "all");

		// 获取表头主键
		String custPindx = GetDataValue("custData", "INDX");
		String inspPindx = GetDataValue("inspData", "DECL_NO");

		String CODE_T = GetDataValue("complexGoodsData", "CODE_T");// HS_CODE
		String CODE_S = GetDataValue("complexGoodsData", "CODE_S");// HS_CODE
		String CARGOCN = GetDataValue("complexGoodsData", "CARGOCN"); // 商品名称
		String MODEL = GetDataValue("complexGoodsData", "MODEL");// 规格
		String BARGAINTO = GetDataValue("complexGoodsData", "BARGAINTO");// 货物总值
		String BARGAINSM = GetDataValue("complexGoodsData", "BARGAINSM");// 成交/保健数量
		String LEGALSM = GetDataValue("complexGoodsData", "LEGALSM");// 法定/HS标准量
		String BARGAINPRICE = GetDataValue("complexGoodsData", "BARGAINPRICE");// 成交单价
		String STD_TYPE=GetDataValue("complexGoodsData", "STD_TYPE");//判断重量还是数量
		String C_SEQNO = "";
		
		/*boolean saveinsp = "Y".equals(inspRadio) || "Y".equals(allRadio);
		boolean saveentry = "Y".equals(custRadio) || "Y".equals(allRadio);*/
		boolean saveinsp=true;
		boolean saveentry=true;
		boolean saveinspok = true;
		boolean saveentryok = true;
		if (saveinsp) {
			if (SysUtility.isEmpty(inspPindx)) {
				ReturnMessage(false, "请先保存报检表头信息");
				return;
			}
			if (SysUtility.isEmpty(GetDataValue("inspGoodsData", "GOODS_ID"))) {
				SaveToTable("inspGoodsData", "DECL_NO", inspPindx);
				C_SEQNO = getDataAccess().GetTableDatas("SEQNO","Select seq_con_item_indx.nextval as seqno from dual").GetTableValue("SEQNO","seqno");
				SaveToTable("inspGoodsData","CON_ITEM_INDX",C_SEQNO);
				SaveToTable("inspGoodsData", "GOODS_NO",
						GetNoFromGoods(inspPindx));
			}
			else{
				if(SysUtility.isEmpty(GetDataValue("custGoodsData","CON_ITEM_INDX"))){
					SaveToTable("inspGoodsData","CON_ITEM_INDX",GetDataValue("custGoodsData","CON_ITEM_INDX"));
				}
			}

			SaveToTable("inspGoodsData", "PROD_HS_CODE", CODE_T + CODE_S);
			SaveToTable("inspGoodsData", "DECL_GOODS_CNAME", CARGOCN);
			SaveToTable("inspGoodsData", "GOODS_SPEC", MODEL);
			SaveToTable("inspGoodsData", "TOTAL_VAL_CN", BARGAINTO);
			SaveToTable("inspGoodsData", "QTY", BARGAINSM);
			if(STD_TYPE.equals("2")){
				SaveToTable("inspGoodsData", "STD_WEIGHT", LEGALSM);
			}else{
				SaveToTable("inspGoodsData", "STD_QTY", LEGALSM);
			}
			
			SaveToTable("inspGoodsData", "PRICE_PER_UNIT", BARGAINPRICE);

			String Indx = String.valueOf(SaveToDB("inspGoodsData",
					"ITF_DCL_IO_DECL_GOODS", "GOODS_ID"));
			if (SysUtility.isEmpty(Indx)) {
				saveinspok = false;
			}
		}
		if (saveentry) {
			if (SysUtility.isEmpty(custPindx)) {
				ReturnMessage(false, "请先保存报关表头信息");
				return;
			}
			if (SysUtility.isEmpty(GetDataValue("custGoodsData", "INDX"))) {
				if(SysUtility.isEmpty(C_SEQNO)){
					C_SEQNO = getDataAccess().GetTableDatas("SEQNO","Select seq_con_item_indx.nextval as seqno from dual").GetTableValue("SEQNO","seqno");
				}
				SaveToTable("custGoodsData","CON_ITEM_INDX",C_SEQNO);
				SaveToTable("custGoodsData", "ENY_INDX", custPindx);
				SaveToTable("custGoodsData", "SERIALNO",
						GetNoFromGoodsCust(custPindx));
			}
			else{
				if(!SysUtility.isEmpty(GetDataValue("inspGoodsData","CON_ITEM_INDX"))){
					SaveToTable("custGoodsData","CON_ITEM_INDX",GetDataValue("inspGoodsData","CON_ITEM_INDX"));
				}
			}
			SaveToTable("custGoodsData", "CARGOTAXCO", CODE_T);
			SaveToTable("custGoodsData", "CODE_T", CODE_T);
			SaveToTable("custGoodsData", "CODE_S", CODE_S);
			SaveToTable("custGoodsData", "CARGOCN", CARGOCN);
			SaveToTable("custGoodsData", "MODEL", MODEL);
			SaveToTable("custGoodsData", "BARGAINTO", BARGAINTO);
			SaveToTable("custGoodsData", "BARGAINSM", BARGAINSM);
			SaveToTable("custGoodsData", "LEGALSM", LEGALSM);
			SaveToTable("custGoodsData", "BARGAINPRICE", BARGAINPRICE);

			String Indx = String.valueOf(SaveToDB("custGoodsData", "T_CARGO",
					"INDX"));
			if (SysUtility.isEmpty(Indx)) {
				saveentryok = false;
			}
		}
		if (saveentryok && saveinspok) {
			ReturnMessage(true, "保存成功");
		} else {
			ReturnMessage(true, "保存失败");
		}
		// 如果是保存报检insp
		// if ("Y".equals(inspRadio)) {
		//
		// }
		// 如果保存报关cust
		// if ("Y".equals(custRadio)) {
		//
		// }
		// 如果保存关检all
		// if ("Y".equals(allRadio)) {
		// if (SysUtility.isEmpty(inspPindx) || SysUtility.isEmpty(custPindx)) {
		// ReturnMessage(false, "请先保存表头信息");
		// return;
		// }
		// if (SysUtility.isEmpty(GetDataValue("custGoodsData", "INDX"))) {
		// SaveToTable("custGoodsData", "SERIALNO",
		// GetNoFromGoodsCust(custPindx));
		// }
		// if (SysUtility
		// .isNotEmpty(GetDataValue("inspGoodsData", "GOODS_ID"))) {
		// SaveToTable("inspGoodsData", "GOODS_NO",
		// GetNoFromGoodsCust(custPindx));
		// }
		//
		// SaveToTable("inspGoodsData", "DECL_NO", inspPindx);
		// SaveToTable("inspGoodsData", "PROD_HS_CODE", CARGOTAXCO);
		// SaveToTable("inspGoodsData", "DECL_GOODS_CNAME", CARGOCN);
		// SaveToTable("inspGoodsData", "GOODS_SPEC", MODEL);
		// SaveToTable("inspGoodsData", "TOTAL_VAL_CN", BARGAINTO);
		// SaveToTable("inspGoodsData", "QTY", BARGAINSM);
		// SaveToTable("inspGoodsData", "STD_QTY", LEGALSM);
		// SaveToTable("inspGoodsData", "PRICE_PER_UNIT", BARGAINPRICE);
		//
		// SaveToTable("custGoodsData", "ENY_INDX", custPindx);
		// SaveToTable("custGoodsData", "CARGOTAXCO", CARGOTAXCO);
		// SaveToTable("custGoodsData", "CARGOCN", CARGOCN);
		// SaveToTable("custGoodsData", "MODEL", MODEL);
		// SaveToTable("custGoodsData", "BARGAINTO", BARGAINTO);
		// SaveToTable("custGoodsData", "BARGAINSM", BARGAINSM);
		// SaveToTable("custGoodsData", "LEGALSM", LEGALSM);
		// SaveToTable("custGoodsData", "BARGAINPRICE", BARGAINPRICE);
		//
		// String saveCustIndx = "";
		// String saveInspIndx = "";
		// if (SysUtility.isEmpty(GetDataValue("custGoodsData", "INDX"))
		// && SysUtility.isEmpty(GetDataValue("inspGoodsData",
		// "GOODS_ID"))) {
		// saveCustIndx = String.valueOf(SaveToDB("custGoodsData",
		// "T_CARGO", "INDX"));
		// saveInspIndx = String.valueOf(SaveToDB("inspGoodsData",
		// "ITF_DCL_IO_DECL_GOODS", "GOODS_ID"));
		// } else if (SysUtility.isNotEmpty(GetDataValue("custGoodsData",
		// "INDX"))
		// && SysUtility.isEmpty(GetDataValue("inspGoodsData",
		// "GOODS_ID"))) {
		// saveCustIndx = String.valueOf(SaveToDB("custGoodsData",
		// "T_CARGO", "INDX"));
		// } else if (SysUtility.isNotEmpty(GetDataValue("inspGoodsData",
		// "GOODS_ID"))
		// && SysUtility
		// .isEmpty(GetDataValue("custGoodsData", "INDX"))) {
		// saveInspIndx = String.valueOf(SaveToDB("inspGoodsData",
		// "ITF_DCL_IO_DECL_GOODS", "GOODS_ID"));
		// }
		//
		// if (!SysUtility.isEmpty(saveInspIndx)
		// || !SysUtility.isEmpty(saveCustIndx)) {
		// ReturnMessage(true, "保存成功");
		// } else {
		// ReturnMessage(false, "保存失败");
		// }
		// }

	}

	// 设置商品序号
	private String GetNoFromGoods(String INDX) throws LegendException {
		Datas dt = getDataAccess()
				.GetTableDatas(
						"GoodsList",
						"SELECT count(GOODS_NO) + 1 as GOODS_NO FROM Itf_Dcl_Io_Decl_Goods WHERE DECL_NO = ?",
						INDX);
		return dt.GetTableValue("GoodsList", "GOODS_NO"); 
	}

	// 设置商品序号
	private String GetNoFromGoodsCust(String INDX) throws LegendException {
		Datas dt = getDataAccess()
				.GetTableDatas(
						"GoodsList",
						"SELECT count(SERIALNO) + 1 SERIALNO FROM T_CARGO WHERE ENY_INDX = ?",
						INDX);
		return dt.GetTableValue("GoodsList", "SERIALNO");
	}

}