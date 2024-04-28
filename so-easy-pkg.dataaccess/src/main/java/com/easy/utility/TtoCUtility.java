package com.easy.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;




/**
 * @author zpp
 * @TIME 2017-12-07
 *
 */
public class TtoCUtility {
	private static HashMap XmlEntMappingMap = new HashMap();
	private static HashMap XmlAttchMappingMap =new HashMap();
	private static HashMap XmlGoodsMappingMap = new HashMap();
	private static HashMap XmlEntMagMappingMap=new HashMap();
	private static HashMap XmlGoodAttchsMappingMap=new HashMap();
	private static HashMap XmlIIMappingMap=new HashMap();
	private static HashMap XmlIItemMappingMap=new HashMap();
	private static HashMap XmlContMappingMap=new HashMap();
	private static HashMap XmlCFMappingMap=new HashMap();
	private static HashMap XmlOIMappingMap=new HashMap();
	private static HashMap XmlOItemMappingMap=new HashMap();
	private static HashMap XmlIOMappingMap=new HashMap();
	private static HashMap XmlIOItemMapping=new HashMap();
	private static HashMap XmlOrderMapping=new HashMap();

	private static HashMap XmlOrderItemMapping=new HashMap();
	private static HashMap XmlPayMapping=new HashMap();
	private static HashMap XmlBillMapping=new HashMap();

	private static HashMap XmlDeclareMapping=new HashMap();
	private static HashMap XmlOOMappingMap=new HashMap();
	private static HashMap XmlOOItemMapping=new HashMap();

	private static HashMap XmlFAMappingMap=new HashMap();
	private static HashMap XmlFAItemMapping=new HashMap();
	static{
		XmlEntMappingMap.put("ENT_GUID_NO", "ENT_FILING_INFO_ID");
		XmlEntMappingMap.put("ENT_CNAME", "ENT_CNAME");
		XmlEntMappingMap.put("DECL_NO", "CHECKLIST_NO");
		XmlEntMappingMap.put("APPROVAL_TIME", "AUDIT_DATE");
		/*XmlEntMappingMap.put("ENT_REG_NO", "ENT_SELF_NO");*/
		XmlEntMappingMap.put("ENT_CBEC_NO", "ENT_CBEC_CODE");
		XmlEntMappingMap.put("REG_ADDR", "ENT_REG_ADDR");
		XmlEntMappingMap.put("ENT_STATUS_CODE","ENT_STATUS");
		XmlEntMappingMap.put("CONT_PERSON", "ENT_CONTACTOR");
		XmlEntMappingMap.put( "CONT_MOBILE","ENT_CONTACTOR_TEL");
		XmlEntMappingMap.put("LEGL_PERSN_NAME", "ENT_LEGAL_PERSON");
		XmlEntMappingMap.put("INSP_ORG_CODE", "MONITOR_ORG_CODE");
		XmlEntMappingMap.put("INSP_ORG_NAME", "MONITOR_ORG_NAME");
		XmlEntMappingMap.put("DECL_DATE", "OPER_DATE");
		XmlEntMappingMap.put("EMAIL", "E_MAIL");
		XmlEntMappingMap.put("FAX", "FAX_NO");
		XmlEntMappingMap.put("POST_CODE", "ZIP_CODE");
		XmlEntMappingMap.put("ENT_WEBSITE_NAME","PLAT_NAME");
		XmlEntMappingMap.put("ENT_WEBSITE","PLAT_WEBSITE");
		XmlEntMappingMap.put("ORG_NO", "TECH_REG_CODE");
		XmlEntMappingMap.put("BIZ_SCOP", "ENT_BUZI_SCOPE");//转换经营范围字段
		
		//附件表
		/*XmlAttchMappingMap.put("rownum", "ATTACHED_SEQ_NO");*/
		XmlAttchMappingMap.put("ATTACH_TYPE", "CERT_TYPE_CODE");
		XmlAttchMappingMap.put("ATTACH_NAME", "FILE_NAME");
		XmlAttchMappingMap.put("ATTACH_CONTENT", "FILE_CONTENT");
		XmlAttchMappingMap.put("CREATE_TIME", "STORE_DATE");

		//企业备案历史表
		XmlEntMagMappingMap.put("ENT_GUID_NO", "ENT_FILING_INFO_ID");
		XmlEntMagMappingMap.put("INSP_ORG_CODE", "OPER_ORG_CODE");
		XmlEntMagMappingMap.put("APPROVALER", "OPER_PERSON_NAME");
		XmlEntMagMappingMap.put("APPROVAL_TIME", "OPER_DATE");
		//商品
		XmlGoodsMappingMap.put("GOODS_GUID_NO","PRODUCT_FILING_INFO_ID");
		XmlGoodsMappingMap.put("ENT_CBEC_NO","ENT_CBEC_CODE");
		XmlGoodsMappingMap.put("GOODS_CBEC_NO","PROD_CBEC_CODE");
		XmlGoodsMappingMap.put("HSCODE", "HS_CODE");
		XmlGoodsMappingMap.put("PROD_NAME_CN","PRODUCT_NAME");
		XmlGoodsMappingMap.put("COMM_BARCODE","BAR_CODE");
		XmlGoodsMappingMap.put("PROD_BRD_CN","BRAND");
		XmlGoodsMappingMap.put("PROD_SPECS","MODEL");
		XmlGoodsMappingMap.put("MAIN_COMP","BASES");
		XmlGoodsMappingMap.put("PURPOSE_NAME","PURPOSE");
		XmlGoodsMappingMap.put("INSP_FLAG","IS_LAW_REVIEW");
		XmlGoodsMappingMap.put("SKU_NO","SKU");
		XmlGoodsMappingMap.put("MNUFCTR_NAME","PROD_ENT");
		XmlGoodsMappingMap.put("ORI_CTRY_CODE","PROD_COUNTRY_CODE");
		XmlGoodsMappingMap.put("ORI_CTRY_NAME","PROD_COUNTRY_NAME");
		XmlGoodsMappingMap.put("DECL_DATE","APPLY_DATE");
		XmlGoodsMappingMap.put("INSP_ORG_CODE","MONITOR_ORG_CODE");
		XmlGoodsMappingMap.put("INSP_ORG_NAME","MONITOR_ORG_NAME");
		//XmlGoodsMappingMap.put("AUDIT_DATE","CREATE_TIME");
		XmlGoodsMappingMap.put("GOODS_STATUS_CODE", "PRODUCT_STATUS");
		XmlGoodsMappingMap.put("GOODS_STATUS_NAME", "MESSAGE");
		XmlGoodsMappingMap.put("APPROVAL_TIME", "AUDIT_DATE");
		XmlGoodsMappingMap.put("MAIL_TAX_NO", "GOODS_NO");
		//商品附件
		//XmlGoodAttchsMappingMap.put("ATTACH_TYPE", "CERT_TYPE_CODE");
		//XmlGoodAttchsMappingMap.put("ATTACHED_SEQ_NO", "ATTACHED_SEQ_NO");
		XmlGoodAttchsMappingMap.put("ATTACH_NAME", "FILE_NAME");
		XmlGoodAttchsMappingMap.put("ATTACH_CONTENT", "FILE_CONTENT");
		XmlGoodAttchsMappingMap.put("CREATE_TIME","STORE_DATE");
		//入区清单II
		XmlIIMappingMap.put("II_GUID_NO", "DECL_DECLAR_CHECK_ID");
		XmlIIMappingMap.put("DECL_NO", "DECL_NO");
		//STATE
		XmlIIMappingMap.put("DECL_DATE", "DECL_DATE");
		XmlIIMappingMap.put("ENT_CODE", "DECL_REG_NO");
		XmlIIMappingMap.put("ENT_NAME", "DECL_REG_NAME");
		XmlIIMappingMap.put("ENT_PERSON", "CONTACTOR");
		XmlIIMappingMap.put("ENT_TEL", "TELEPHONE");
		XmlIIMappingMap.put("CONSIGNEE_CNAME", "CONSIGNEE_CNAME");
		XmlIIMappingMap.put("CONSIGNEE_ENAME", "CONSIGNEE_ENAME");
		XmlIIMappingMap.put("ENT_NATURE_CODE", "ENT_PROPERTY");
		XmlIIMappingMap.put("CONSIGNOR_CNAME", "CONSIGNOR_CNAME");
		XmlIIMappingMap.put("CONSIGNOR_ENAME", "CONSIGNOR_ENAME");
		XmlIIMappingMap.put("TRANS_TYPE_CODE", "TRANS_TYPE_CODE");
		XmlIIMappingMap.put("TRANS_TYPE_NO", "TRANS_MEANS_CODE");
		XmlIIMappingMap.put("CONTRACT_NO", "CONTRACT_NO");
		XmlIIMappingMap.put("TRADE_MODE_CODE", "TRADE_MODE_CODE");
		XmlIIMappingMap.put("TRADE_CTRY_CODE", "TRADE_COUNTRY_CODE");
		XmlIIMappingMap.put("DESP_CTRY_CODE", "DESP_COUNTRY_CODE");
		XmlIIMappingMap.put("CARRIER_NOTE_NO", "CARRIER_NOTE_NO");
		XmlIIMappingMap.put("ARRI_DATE", "ARRI_DATE");
		XmlIIMappingMap.put("UNLOAD_DATE", "UNLOAD_DATE");
		XmlIIMappingMap.put("DESP_PORT_CODE", "DESP_PORT_CODE");
		XmlIIMappingMap.put("ENTRY_PORT_CODE", "ENTRY_PORT_CODE");
		XmlIIMappingMap.put("GOODS_PLACE", "GOODS_PLACE");
		XmlIIMappingMap.put("INSP_ORG_CODE", "INSP_ORG_CODE");
		XmlIIMappingMap.put("SHEET_TYPE_CODES", "SHEET_TYPE_CODES");
		XmlIIMappingMap.put("CERT_TYPE_CODES", "CERT_TYPE_CODES");
		XmlIIMappingMap.put("CERT_ORIGINALS", "CERT_ORIGINALS");
		XmlIIMappingMap.put("CERT_COPIES", "CERT_COPIES");
		XmlIIMappingMap.put("ORG_NO", "DECL_REG_CODE");
		XmlIIMappingMap.put("CBE_CODE", "CBE_CODE");
		XmlIIMappingMap.put("CBE_NAME", "CBE_NAME");
		XmlIIMappingMap.put("CONSIGNEE_TEL", "CONSIGNEE_TEL");
		XmlIIMappingMap.put("CONSIGNOR_TEL", "CONSIGNOR_TEL");
		XmlIIMappingMap.put("WASTE_FLAG", "WASTE_FLAG");
		//入区II商品
		XmlIItemMappingMap.put("II_GOODS_GUID_NO", "UUID");
		XmlIItemMappingMap.put("II_GUID_NO", "DECL_DECLAR_CHECK_ID");
		XmlIItemMappingMap.put("HSCODE", "HS_CODE");
		XmlIItemMappingMap.put("PROD_NAME_CN", "GOODS_CNAME");
		XmlIItemMappingMap.put("PROD_NAME_EN", "GOODS_ENAME");
		XmlIItemMappingMap.put("WEIGHT", "WEIGHT");
		XmlIItemMappingMap.put("WEIGHT_UNIT_CODE", "WEIGHT_UNIT_CODE");
		XmlIItemMappingMap.put("QTY", "QTY");
		XmlIItemMappingMap.put("QTY_UNIT_CODE", "QTY_UNIT_CODE");
		XmlIItemMappingMap.put("PRICE_TOTAL_VAL", "VALUES_RMB");
		XmlIItemMappingMap.put("CURR_UNIT", "CCY");
		XmlIItemMappingMap.put("PRICE_PER_UNIT", "PRICE");
		XmlIItemMappingMap.put("PROD_SPECS", "GOODS_MODEL");
		XmlIItemMappingMap.put("PACK_TYPE_CODE", "PACK_TYPE_CODE");
		XmlIItemMappingMap.put("PACK_NUMBER", "PACK_NUMBER");
		XmlIItemMappingMap.put("ORI_CTRY_CODE", "ORIGIN_COUNTRY_CODE");
		XmlIItemMappingMap.put("PURPOSE_CODE", "PURPOSE_CODE");
		XmlIItemMappingMap.put("WASTE_FLAG", "WASTE_FLAG");
		/*XmlIItemMappingMap.put("GOODS_REG_NO", "PRODUCT_RECORD_NO");*/
		XmlIItemMappingMap.put("PROD_BRD_CN","PROD_BRD_CN");
		XmlIItemMappingMap.put("COMM_BARCODE","COMM_BARCODE");
		XmlIItemMappingMap.put("REMARK","REMARK");
		XmlIItemMappingMap.put("STD_QUANTITY","STD_QUANTITY");
		XmlIItemMappingMap.put("STD_UNIT_CODE","STD_UNIT_CODE");
		XmlIItemMappingMap.put("SKU_NO","SKU_NO");
		//II集装箱
		XmlContMappingMap.put("CON_MODEL", "CONTAINER_MODEL_CODE");
		XmlContMappingMap.put("CON_NUM", "CONTAINER_QTY");
		XmlContMappingMap.put("CON_NO", "CONTAINER_CODE");
		//II查验
		XmlCFMappingMap.put("CF_SERIAL_NO", "CHECKLIST_RESULT_ID");
		XmlCFMappingMap.put("DECL_NO", "CHECKLIST_NO");
		XmlCFMappingMap.put("CBECODE", "E_BUSINESS_COMPANY_CODE");
		XmlCFMappingMap.put("CBENAME", "E_BUSINESS_COMPANY_NAME");
		XmlCFMappingMap.put("INSP_ORG_CODE", "ORG_CODE");
		XmlCFMappingMap.put("CHECK_NAME", "PERSON_CODE");
		XmlCFMappingMap.put("CREATE_TIME", "CREATE_TIME");
		XmlCFMappingMap.put("IS_GOODSCHECK_INFO", "SPOT_DESC");
		XmlCFMappingMap.put("IS_GOODSCHECK", "SPOT_RESULT");
		XmlCFMappingMap.put("CHECK_REMARK", "REMARK");
		XmlCFMappingMap.put("CHECK_DATE", "RECORD_TIME");
		XmlCFMappingMap.put("CHECK_ADDR", "RECORD_PLACE");

		//OI申报单
		XmlOIMappingMap.put("OI_GUID_NO", "DECL_DECLAR_CHECK_ID");
		XmlOIMappingMap.put("DECL_NO", "DECL_NO");
		XmlOIMappingMap.put("DECL_DATE", "DECL_DATE");
		XmlOIMappingMap.put("ENT_CODE", "DECL_REG_NO");
		XmlOIMappingMap.put("ENT_NAME", "DECL_REG_NAME");
		XmlOIMappingMap.put("DECL_PERSON", "CONTACTOR");
		XmlOIMappingMap.put("CONSIGNEE_NO", "CONSIGNEE_CODE");
		XmlOIMappingMap.put("CONSIGNEE_CNAME", "CONSIGNEE_CNAME");
		XmlOIMappingMap.put("CONSIGNEE_ENAME", "CONSIGNEE_ENAME");
		XmlOIMappingMap.put("TRADE_MODE_CODE", "TRADE_MODE_CODE");
		XmlOIMappingMap.put("TRADE_COUNTRY_CODE", "TRADE_COUNTRY_CODE");
		XmlOIMappingMap.put("INSP_ORG_CODE", "INSP_ORG_CODE");
		/*XmlOIMappingMap.put("CBE_CODE", "E_BUSINESS_COMPANY_CODE");
		XmlOIMappingMap.put("CBE_NAME", "E_BUSINESS_COMPANY_NAME");*/
		XmlOIMappingMap.put("CONSIGNEE_TEL", "CONSIGNEE_TEL");
		XmlOIMappingMap.put("CONSIGNOR_TEL", "CONSIGNOR_TEL");
		XmlOIMappingMap.put("WASTE_FLAG", "WASTE_FLAG");
		XmlOIMappingMap.put("REMARK", "REMARK");
		XmlOIMappingMap.put("ENT_TEL", "TELEPHONE");
		XmlOIMappingMap.put("ENT_NATURE_CODE", "ENT_PROPERTY");
		XmlOIMappingMap.put("CONSIGNOR_NO", "CONSIGNOR_CODE");
		XmlOIMappingMap.put("TRANS_TYPE_NO", "TRANS_MEANS_CODE");
		XmlOIMappingMap.put("ECP_CODE", "ELECTRIC_BUSINESS_NO");
		XmlOIMappingMap.put("ORG_NO", "DECL_REG_CODE");
		//OI商品
		XmlOItemMappingMap.put("OI_GOODS_GUID_NO", "UUID");
		XmlOItemMappingMap.put("HSCODE", "HS_CODE");
		XmlOItemMappingMap.put("PROD_NAME_CN", "GOODS_CNAME");
		XmlOItemMappingMap.put("QTY", "QTY");
		XmlOItemMappingMap.put("QTY_UNIT_CODE", "QTY_UNIT_CODE");		
		XmlOItemMappingMap.put("PRICE_PER_UNIT", "PRICE");		
		XmlOItemMappingMap.put("PACK_TYPE_CODE", "PACK_TYPE_CODE");
		XmlOItemMappingMap.put("PACK_NUMBER", "PACK_NUMBER");
		XmlOItemMappingMap.put("ORI_CTRY_CODE", "ORIGIN_COUNTRY_CODE");
		/*XmlOItemMappingMap.put("GOODS_REG_NO", "PRODUCT_RECORD_NO");*/
		XmlOItemMappingMap.put("PROD_BRD_CN", "PROD_BRD_CN");
		XmlOItemMappingMap.put("REMARK", "REMARK");
		XmlOItemMappingMap.put("SEQ_NO", "SEQ_NO");
		XmlOItemMappingMap.put("SKU_NO", "SKU_NO");
		XmlOItemMappingMap.put("PROD_NAME_EN", "GOODS_ENAME");
		
		
		//IO*****************
		XmlIOMappingMap.put("IO_GUID_NO", "GOODS_DECLAR_CHECK_ID");
		XmlIOMappingMap.put("DECL_NO", "CHECKLIST_NO");
		XmlIOMappingMap.put("PORT_CODE", "IN_OUT_PORT_NUMBER");
		XmlIOMappingMap.put("DECL_DATE", "DECLARE_DATE");
		XmlIOMappingMap.put("CBE_CODE", "E_BUSINESS_COMPANY_CODE");
		XmlIOMappingMap.put("CBE_NAME", "E_BUSINESS_COMPANY_NAME");
		/*XmlIOMappingMap.put("ECP_CODE", "E_BUSINESS_PLATFORM_CODE");*/
		XmlIOMappingMap.put("ECP_NAME", "E_BUSINESS_PLATFORM_NAME");
		//XmlIOMappingMap.put("DECL_NO", "DECL_NO");
		XmlIOMappingMap.put("ORDER_NO", "ORDER_NUMBER");
		XmlIOMappingMap.put("TRADE_COUNTRY_CODE", "FROM_COUNTRY_CODE11");
		XmlIOMappingMap.put("TRADE_COUNTRY_NAME", "FROM_COUNTRY_NAME");
		XmlIOMappingMap.put("GROSS_WEIGHT", "ROUGH_WEIGHT");
		XmlIOMappingMap.put("NET_WEIGHT", "NET_WEIGHT");
		XmlIOMappingMap.put("CONSIGNOR_CNAME", "SENDER");
		XmlIOMappingMap.put("CONSIGNEE_CNAME", "RECEIVER");
		XmlIOMappingMap.put("TOTAL_VALUES", "WORTH");
		XmlIOMappingMap.put("CURR_UNIT", "CURRENCY_CODE");
		XmlIOMappingMap.put("CURR_UNIT_NAME", "CURRENCY_NAME");
		XmlIOMappingMap.put("CREATE_TIME", "CREATE_TIME");
		XmlIOMappingMap.put("SYSDATE","IMP_DATE");
		//ORDER_ID
		XmlIOMappingMap.put("LOGISTICS_NO", "LOGISTICS_NO");
		XmlIOMappingMap.put("PAYMENT_NO", "PAYMENT_NO");
		XmlIOMappingMap.put("LOGISTICS_CODE", "LOGISTICS_CODE");
		XmlIOMappingMap.put("LOGISTICS_NAME", "LOGISTICS_NAME");
		XmlIOMappingMap.put("INSP_ORG_CODE", "INSP_ORG_CODE");
		XmlIOMappingMap.put("ID_TYPE", "ID_TYPE");
		XmlIOMappingMap.put("ID_CARD", "ID_CARD");
		XmlIOMappingMap.put("ID_TYPENAME", "ID_TYPENAME");
		XmlIOMappingMap.put("ID_TEL", "ID_TEL");
		XmlIOMappingMap.put("CONSIGNEE_ADDR", "CONSIGNEE_ADDR");
		XmlIOMappingMap.put("CONSIGNEE_TEL", "CONSIGNEE_TEL");
		XmlIOMappingMap.put("CONSIGNOR_ADDRESS", "CONSIGNOR_ADDR");
		XmlIOMappingMap.put("CONSIGNOR_TEL", "CONSIGNOR_TEL");
		XmlIOMappingMap.put("TRADE_COUNTRY_CODE", "PURPOS_CTRY_CODE");
		XmlIOMappingMap.put("DESP_COUNTRY_CODE", "DESP_CTRY_CODE");
		XmlIOMappingMap.put("AREA_CODE", "AREA_CODE");
		XmlIOMappingMap.put("AREA_NAME", "AREA_NAME");
		XmlIOMappingMap.put("TRAF_MODE", "TRAF_MODE");
		XmlIOMappingMap.put("TRANS_TYPE_CODE", "TRANS_TYPE_CODE");
		XmlIOMappingMap.put("TRANS_TYPE_NO", "TRANS_TYPE_NO");
		XmlIOMappingMap.put("LICENSE_NO", "LICENSE_NO");
		XmlIOMappingMap.put("PACK_NO", "PACK_NO");
		XmlIOMappingMap.put("REMARK", "REMARK");
		//IO商品
		XmlIOItemMapping.put("IO_GOODS_GUID_NO", "DETAIL_ID");
		XmlIOItemMapping.put("PROD_NAME_CN", "GOODS_NAME");
		XmlIOItemMapping.put("PROD_SPECS", "GOODS_SPECIFICATION");
		XmlIOItemMapping.put("ORI_CTRY_CODE", "PRO_MARKETING_COUNTRY_CODE");
		XmlIOItemMapping.put("ORI_CTRY_NAME", "PRO_MARKETING_COUNTRY_NAME");
		XmlIOItemMapping.put("PRICE_PER_UNIT", "DECLARE_PRICE");
		XmlIOItemMapping.put("QTY", "DECLARE_COUNT");
		XmlIOItemMapping.put("QTY_UNIT_CODE", "DECLARE_MEASURE_UNIT_CODE");
		XmlIOItemMapping.put("QTY_UNIT_NAME", "DECLARE_MEASURE_UNIT_NAME");
		XmlIOItemMapping.put("CREATE_TIME", "CREATE_TIME");
		/*XmlIOItemMapping.put("GOODS_REG_NO", "PRODUCT_RECORD_NO");*/
		XmlIOItemMapping.put("HSCODE", "HS_CODE");
		XmlIOItemMapping.put("SEQ_NO", "SEQ_NO");
		XmlIOItemMapping.put("SKU_NO", "SKU_NO");
		XmlIOItemMapping.put("PROD_BRD_CN", "PROD_BRD_CN");
		XmlIOItemMapping.put("PRICE_TOTAL_VAL", "PRICE_TOTAL_VAL");
		XmlIOItemMapping.put("CURR_UNIT", "CURR_UNIT");
		XmlIOItemMapping.put("COMM_BARCODE", "COMM_BARCODE");
		XmlIOItemMapping.put("REMARK", "REMARK");
		XmlIOItemMapping.put("GROSS_WEIGHT", "GOODS_ROUGH_WEIGHT");
		XmlIOItemMapping.put("IN_DECL_NO", "IN_CHECKLIST_NO");
		//包裹单
		XmlDeclareMapping.put("BILL_GUID_NO", "GOODS_DECLAR_CHECK_ID");
		XmlDeclareMapping.put("IE_FLAG","IN_OUT_FLAG");
		XmlDeclareMapping.put("ACCEPT_TIME","DECLARE_DATE");
		XmlDeclareMapping.put("CBE_CODE","E_BUSINESS_COMPANY_CODE");
		XmlDeclareMapping.put("CBE_NAME","E_BUSINESS_COMPANY_NAME");
		XmlDeclareMapping.put("ORDER_NO","ORDER_NUMBER");
		XmlDeclareMapping.put("LOGISTICS_NO","SUB_CARRIAGE_NO");
		XmlDeclareMapping.put("TRADE_CTRY_CODE","FROM_COUNTRY");
		XmlDeclareMapping.put("GROSS_WEIGHT","ROUGH_WEIGHT");
		XmlDeclareMapping.put("NET_WEIGHT","NET_WEIGHT");
		XmlDeclareMapping.put("CONSIGNOR_CNAME","SENDER");
		XmlDeclareMapping.put("CONSIGNEE_CNAME","RECEIVER");
		XmlDeclareMapping.put("CONSIGNOR_CTRY_CODE","SENDER_COUNTRY");
		XmlDeclareMapping.put("PRICE_TOTAL_VAL","WORTH");
		XmlDeclareMapping.put("CURR_UNIT","CURRENCY");
		XmlDeclareMapping.put("PROD_NAME_CN","MAJOR_GOODS_NAME");
		XmlDeclareMapping.put("CREATE_TIME","CREATE_TIME");
		XmlDeclareMapping.put("INSP_ORG_CODE", "SEND_SOURCE_NODE");
		XmlDeclareMapping.put("ORDER_GUID_NO", "ORDER_ID");
		XmlDeclareMapping.put("INSP_ORG_CODE", "ORG_CODE");
		XmlDeclareMapping.put("REMARK", "REMARK");
		XmlDeclareMapping.put("CONSIGNOR_CITY_CODE", "SENDER_CITY");
		//订单
		XmlOrderMapping.put("ORDER_GUID_NO","ORDER_ID");
		XmlOrderMapping.put("IE_FLAG","I_E_FLAG");
		XmlOrderMapping.put("ORDER_TOTAL_AMOUNT","ORDER_TOTAL_AMOUNT");
		XmlOrderMapping.put("ORDER_NO","ORDER_NO");
		XmlOrderMapping.put("ECP_NAME","E_BUSINESS_PLATFORM_NAME");
		XmlOrderMapping.put("ECP_CODE","E_BUSINESS_PLATFORM_CODE");
		XmlOrderMapping.put("ENT_CODE","DECL_REG_NO");
		XmlOrderMapping.put("ENT_NAME","DECL_REG_NAME");
		XmlOrderMapping.put("INSP_ORG_CODE","ORG_CODE");
		XmlOrderMapping.put("CBE_CODE","E_BUSINESS_COMPANY_CODE");
		XmlOrderMapping.put("CBE_NAME","E_BUSINESS_COMPANY_NAME");
		XmlOrderMapping.put("PAYMENT_NO","PAY_TRANSACTION_NO");
		XmlOrderMapping.put("PAY_NAME","PAY_ENTERPRISE_NAME");
		XmlOrderMapping.put("PAY_CODE","PAY_ENTERPRISE_CODE");
		XmlOrderMapping.put("TRADE_CTRY_CODE","FROM_COUNTRY");
		XmlOrderMapping.put("TRADE_TIME","TRADE_TIME");
		XmlOrderMapping.put("CURR_UNIT","CURR_CODE");
		XmlOrderMapping.put("CONSIGNEE_EMAIL","CONSIGNEE_EMAIL");
		XmlOrderMapping.put("CONSIGNEE_TEL","CONSIGNEE_TEL");
		XmlOrderMapping.put("CONSIGNEE_CNAME","CONSIGNEE");
		XmlOrderMapping.put("CONSIGNEE_ADDR","CONSIGNEE_ADDRESS");
		XmlOrderMapping.put("PACK_NO","TOTAL_COUNT");
		XmlOrderMapping.put("POST_MODE","POST_MODE");
		XmlOrderMapping.put("CONSIGNOR_CTRY_CODE","SALER_COUNTRY");
		XmlOrderMapping.put("CONSIGNOR_CNAME","ADDRESSOR_NAME");
		XmlOrderMapping.put("CREATE_TIME","CREATE_TIME");
		XmlOrderMapping.put("LOGISTICS_NAME","LOGIS_COMPANY_NAME");
		XmlOrderMapping.put("LOGISTICS_CODE","LOGIS_COMPANY_CODE");
		//XmlOrderMapping.put("GOODS_DECLAR_CHECK_ID"); 可能去掉
		XmlOrderMapping.put("DISCOUNT","DISCOUNT");
		XmlOrderMapping.put("TAX_TOTAL","TAX_TOTAL");
		XmlOrderMapping.put("ACTURAL_PAID","ACTURAL_PAID");
		XmlOrderMapping.put("ID_REGNO","BUYER_REG_NO");
		XmlOrderMapping.put("ID_TYPENAME","BUYER_NAME");
		XmlOrderMapping.put("ID_TYPE","BUYER_ID_TYPE");
		XmlOrderMapping.put("ID_CARD","BUYER_ID_NUMBER");
		XmlOrderMapping.put("BATCH_NO","BATCH_NUMBER");
		XmlOrderMapping.put("CONSIGNEE_DITRIC","CONSIGNEE_DITRIC");
		XmlOrderMapping.put("SENDER_CITY","SENDER_CITY");
		XmlOrderMapping.put("ID_TEL","BUYER_ID_TEL");
		XmlOrderMapping.put("PRICE_TOTAL_VAL","PRICE_TOTAL_VAL");
		XmlOrderMapping.put("FREIGHT","FREIGHT");
		XmlOrderMapping.put("LOGISTICS_NO","LOGISTICS_NO");
		XmlOrderMapping.put("MAIN_WB_NO","MAIN_WB_NO");
		XmlOrderMapping.put("INSURED_FEE","INSURED_FEE");
		/*XmlOrderMapping.put("","MONITOR_DECL_FLAG");   默认值
		XmlOrderMapping.put("","FROM_WHERE");		      企业接入时可填空
		XmlOrderMapping.put("","SEND_SOURCE_NODE");	      企业接入时可填空*/
		XmlOrderMapping.put("BIZ_TYPE","IMPORT_TYPE");

		//订单商品
		/*XmlOrderItemMapping.put("", "DETAIL_ID"); 生成UUID主键
		XmlOrderItemMapping.put("", "ORDER_ID");    取表头ORDER_ID*/
		XmlOrderItemMapping.put("PROD_NAME_CN", "GOODS_NAME");
		XmlOrderItemMapping.put("PROD_SPECS", "GOODS_SPECIFICATION");
		XmlOrderItemMapping.put("ORI_CTRY_CODE", "PRODUCTION_MARKETING_COUNTRY");
		XmlOrderItemMapping.put("PRICE_PER_UNIT", "DECLARE_PRICE");
		XmlOrderItemMapping.put("QTY", "DECLARE_COUNT");
		XmlOrderItemMapping.put("QTY_UNIT_CODE", "DECLARE_MEASURE_UNIT");
		XmlOrderItemMapping.put("GROSS_WEIGHT", "GOODS_ROUGH_WEIGHT");
		XmlOrderItemMapping.put("CREATE_TIME", "CREATE_TIME");
		XmlOrderItemMapping.put("PROD_CBEC_CODE", "PRODUCT_RECORD_NO");
		XmlOrderItemMapping.put("WEBSITE_HREF", "WEBSITE_HREF");
		XmlOrderItemMapping.put("MAIL_TAX_NO", "MAIL_TAX_NO");
		XmlOrderItemMapping.put("HS_CODE", "HS_CODE");
		XmlOrderItemMapping.put("SEQ_NO", "SEQ_NO");
		XmlOrderItemMapping.put("SKU_NO", "SKU");
		XmlOrderItemMapping.put("ITEM_DESCRIBE", "ITEM_DESCRIBE");
		XmlOrderItemMapping.put("PROD_BRD_CN", "PROD_BRD_CN");
		XmlOrderItemMapping.put("PRICE_TOTAL_VAL", "PRICE_TOTAL_VAL");
		XmlOrderItemMapping.put("COMM_BARCODE", "COMM_BARCODE");
		XmlOrderItemMapping.put("REMARK", "REMARK");
		XmlOrderItemMapping.put("PACK_NUMBER", "PACK_NUMBER");
		XmlOrderItemMapping.put("PACK_TYPE_CODE", "PACK_TYPE_CODE");
		XmlOrderItemMapping.put("CURR_UNIT", "CURRENCY");

		
		
		//运单
		XmlBillMapping.put("BILL_GUID_NO","GOODS_DECLAR_CHECK_ID");
		XmlBillMapping.put("ACCOUNT_BOOK_NO","ACCOUNT_BOOK_NO");
		XmlBillMapping.put("IE_FLAG","IN_OUT_FLAG");
		XmlBillMapping.put("BIZ_TYPE","IMPORT_TYPE");
		XmlBillMapping.put("IN_OUT_DATE","IN_OUT_DATE");
		XmlBillMapping.put("PORT_CODE","IN_OUT_PORT_NUMBER");
		XmlBillMapping.put("DECL_DATE","DECLARE_DATE");
		XmlBillMapping.put("ARRIVED_PORT_CODE","ARRIVED_PORT");
		XmlBillMapping.put("LOGISTICS_CODE","LOGIS_COMPANY_CODE");
		XmlBillMapping.put("LOGISTICS_NAME","LOGIS_COMPANY_NAME");
		XmlBillMapping.put("CBE_CODE","E_BUSINESS_COMPANY_CODE");
		XmlBillMapping.put("CBE_NAME","E_BUSINESS_COMPANY_NAME");
		XmlBillMapping.put("ECP_NAME","E_BUSINESS_PLATFORM_NAME");
		XmlBillMapping.put("ECP_CODE","E_BUSINESS_PLATFORM_CODE");
		XmlBillMapping.put("ENT_CODE","DECL_REG_NO");
		XmlBillMapping.put("ENT_NAME","DECL_REG_NAME");
		XmlBillMapping.put("FREIGHT","FREIGHT");
		XmlBillMapping.put("INSURED_FEE","INSURED_FEE");
		XmlBillMapping.put("MAIN_WB_NO","MAIN_WB_NO");
		XmlBillMapping.put("PACK_NO","PACK_NO");
		XmlBillMapping.put("ORDER_NO","ORDER_NO");
		XmlBillMapping.put("SUB_CARRIAGE_NO","SUB_CARRIAGE_NO"); 
		XmlBillMapping.put("TRADE_CTRY_CODE","FROM_COUNTRY");
		XmlBillMapping.put("GROSS_WEIGHT","ROUGH_WEIGHT");
		XmlBillMapping.put("NET_WEIGHT","NET_WEIGHT");
		XmlBillMapping.put("PACK_TYPE","PACK_TYPE");
		XmlBillMapping.put("DECLARE_PORT_CODE","DECLARE_PORT_CODE");
		XmlBillMapping.put("GOODS_YARD_CODE","GOODS_YARD_CODE");
		XmlBillMapping.put("CONSIGNOR_CNAME","SENDER");
		XmlBillMapping.put("CONSIGNEE_NAME","RECEIVER");
		XmlBillMapping.put("CONSIGNOR_CTRY_CODE","SENDER_COUNTRY");
		XmlBillMapping.put("SENDER_CITY","SENDER_CITY");
		XmlBillMapping.put("CONSIGNEE_TEL","CONSIGNEE_TEL");
		XmlBillMapping.put("CONSIGNEE_ADDR","CONSIGNEE_ADDRESS");
		XmlBillMapping.put("LOGISTICS_NO","LOGISTICS_NO");
		XmlBillMapping.put("PRICE_TOTAL_VAL","WORTH");
		XmlBillMapping.put("CURR_UNIT","CURRENCY");
		XmlBillMapping.put("PROD_NAME_CN","MAJOR_GOODS_NAME");
		XmlBillMapping.put("CREATE_TIME","CREATE_TIME");
		/*XmlBillMapping.put("","FROM_WHERE"); // 企业接入时可填空
		XmlBillMapping.put("","DEST_NODE");	   // 企业接入时可填空
		XmlBillMapping.put("","MONITOR_DECL_FLAG");默认值N
		XmlBillMapping.put("","SEND_SOURCE_NODE");// 企业接入时可填空
		XmlBillMapping.put("","ORDER_ID");赋值*/
		XmlBillMapping.put("INSP_ORG_CODE","ORG_CODE");
		XmlBillMapping.put("REMARK","REMARK");

		
		
		
		
		//支付单
		XmlPayMapping.put("PAY_GUID_NO", "PAY_ID");
		XmlPayMapping.put("ORDER_NO", "ORDER_NO");
		XmlPayMapping.put("PAYMENT_NO", "PAY_TRANSACTION_NO");
		XmlPayMapping.put("PAY_NAME", "PAY_ENTERPRISE_NAME");
		XmlPayMapping.put("PAY_CODE", "PAY_ENTERPRISE_CODE");
		XmlPayMapping.put("ECP_NAME", "E_BUSINESS_PLATFORM_NAME");
		XmlPayMapping.put("ECP_CODE", "E_BUSINESS_PLATFORM_CODE");
		XmlPayMapping.put("CBE_CODE", "E_BUSINESS_COMPANY_CODE");
		XmlPayMapping.put("CBE_NAME", "E_BUSINESS_COMPANY_NAME");
		XmlPayMapping.put("ENT_CODE", "DECL_REG_NO");
		XmlPayMapping.put("ENT_NAME", "DECL_REG_NAME");
		XmlPayMapping.put("INSP_ORG_CODE", "ORG_CODE");
		XmlPayMapping.put("PAYER_ID_TYPE", "PAYER_DOCUMENT_TYPE");
		XmlPayMapping.put("PAYER_ID_CARD", "PAYER_DOCUMENT_NUMBER");
		XmlPayMapping.put("CREATE_TIME", "CREATE_TIME");
		/*XmlPayMapping.put("", "ORDER_ID"); 赋默认值*/
		XmlPayMapping.put("PAYER_ID_NAME", "PAYER_NAME");
		XmlPayMapping.put("PAYER_ID_TEL", "TELEPHONE");
		XmlPayMapping.put("AMOUNT", "AMOUNT_PAID");
		XmlPayMapping.put("CURR_UNIT", "CURRENCY");
		XmlPayMapping.put("PAYMENT_TIME", "PAY_TIME");
		XmlPayMapping.put("REMARK", "REMARK");
		/*XmlPayMapping.put("", "FROM_WHERE"); 企业接入时可填空
		XmlPayMapping.put("", "MONITOR_DECL_FLAG");赋默认值N
		XmlPayMapping.put("", "SEND_SOURCE_NODE");企业接入时可填空*/

		
		
		//OO**********
		XmlOOMappingMap.put("OO_GUID_NO", "GOODS_DECLAR_CHECK_ID");
		XmlOOMappingMap.put("DECL_NO", "CHECKLIST_NO");
		XmlOOMappingMap.put("PORT_CODE", "IN_OUT_PORT_NUMBER");
		XmlOOMappingMap.put("DECL_DATE", "DECLARE_DATE");
		XmlOOMappingMap.put("CBE_CODE", "E_BUSINESS_COMPANY_CODE");
		XmlOOMappingMap.put("CBE_NAME", "E_BUSINESS_COMPANY_NAME");
		XmlOOMappingMap.put("ORDER_NO", "ORDER_NUMBER");
		XmlOOMappingMap.put("PURPOS_CTRY_CODE", "FROM_COUNTRY_CODE");
		XmlOOMappingMap.put("PURPOS_CTRY_NAME", "FROM_COUNTRY_NAME");
		XmlOOMappingMap.put("CONSIGNOR_CNAME", "SENDER");
		XmlOOMappingMap.put("CONSIGNEE_CNAME", "RECEIVER");
		XmlOOMappingMap.put("PRICE_TOTAL_VAL", "WORTH");
		XmlOOMappingMap.put("CREATE_TIME", "CREATE_TIME");
		XmlOOMappingMap.put("SYSDATE", "IMP_DATE");
		//ORDER_ID
		XmlOOMappingMap.put("LOGISTICS_NO", "LOGISTICS_NO");
		XmlOOMappingMap.put("PAYMENT_NO", "PAYMENT_NO");
		XmlOOMappingMap.put("LOGISTICS_CODE", "LOGISTICS_CODE");
		XmlOOMappingMap.put("LOGISTICS_NAME", "LOGISTICS_NAME");
		//XmlOOMappingMap.put("MESSAGE_DEST", "INSP_ORG_CODE");
		XmlOOMappingMap.put("ID_TYPE", "ID_TYPE");
		XmlOOMappingMap.put("ID_CARD", "ID_CARD");
		XmlOOMappingMap.put("ID_TYPENAME", "ID_TYPENAME");
		XmlOOMappingMap.put("CONSIGNEE_ADDR", "CONSIGNEE_ADDR");
		XmlOOMappingMap.put("CONSIGNEE_TEL", "CONSIGNEE_TEL");
		XmlOOMappingMap.put("CONSIGNOR_ADDR", "CONSIGNOR_ADDR");
		XmlOOMappingMap.put("CONSIGNOR_TEL", "CONSIGNOR_TEL");
		XmlOOMappingMap.put("PURPOS_CTRY_CODE", "PURPOS_CTRY_CODE");
		XmlOOMappingMap.put("REMARK", "REMARK");
		//OO商品
		XmlOOItemMapping.put("OO_GOODS_GUID_NO", "DETAIL_ID");
		XmlOOItemMapping.put("PROD_NAME_CN", "GOODS_NAME");
		XmlOOItemMapping.put("ORI_CTRY_CODE", "PRO_MARKETING_COUNTRY_CODE");
		XmlOOItemMapping.put("ORI_CTRY_NAME", "PRO_MARKETING_COUNTRY_NAME");
		XmlOOItemMapping.put("PRICE_PER_UNIT", "DECLARE_PRICE");
		XmlOOItemMapping.put("QTY", "DECLARE_COUNT");
		XmlOOItemMapping.put("QTY_UNIT_CODE", "DECLARE_MEASURE_UNIT_CODE");
		XmlOOItemMapping.put("QTY_UNIT_NAME", "DECLARE_MEASURE_UNIT_NAME");
		XmlOOItemMapping.put("CREATE_TIME", "CREATE_TIME");
		/*XmlOOItemMapping.put("GOODS_REG_NO", "PRODUCT_RECORD_NO");*/
		XmlOOItemMapping.put("HSCODE", "HS_CODE");
		XmlOOItemMapping.put("SEQ_NO", "SEQ_NO");
		XmlOOItemMapping.put("SKU_NO", "SKU_NO");
		XmlOOItemMapping.put("PROD_BRD_CN", "PROD_BRD_CN");
		XmlOOItemMapping.put("PRICE_TOTAL_VAL", "PRICE_TOTAL_VAL");
		XmlOOItemMapping.put("CURR_UNIT", "CURR_UNIT");
		XmlOOItemMapping.put("REMARK", "REMARK");
		XmlOOItemMapping.put("PROD_SPECS", "GOODS_SPECIFICATION");
		XmlOOItemMapping.put("GROSS_WEIGHT", "GOODS_ROUGH_WEIGHT");
		XmlOOItemMapping.put("IN_DECL_NO", "IN_CHECKLIST_NO");
		//FA
		XmlFAMappingMap.put("FA_GUID_NO", "GOODS_DECLAR_CHECK_ID");
		XmlFAMappingMap.put("DECL_NO", "CHECKLIST_NO");
		XmlFAMappingMap.put("IE_FLAG", "IN_OUT_FLAG");
		XmlFAMappingMap.put("PORT_CODE", "IN_OUT_PORT_NUMBER");
		XmlFAMappingMap.put("DECL_DATE", "DECLARE_DATE");
		XmlFAMappingMap.put("ARRIVED_PORT_CODE", "ARRIVED_PORT_CODE");
		XmlFAMappingMap.put("ARRIVED_PORT_NAME", "ARRIVED_PORT_NAME");
		XmlFAMappingMap.put("CBE_CODE", "E_BUSINESS_COMPANY_CODE");
		XmlFAMappingMap.put("CBE_NAME", "E_BUSINESS_COMPANY_NAME");
		XmlFAMappingMap.put("ECP_CODE", "E_BUSINESS_PLATFORM_CODE");
		XmlFAMappingMap.put("ECP_NAME", "E_BUSINESS_PLATFORM_NAME");
		XmlFAMappingMap.put("ORDER_NO", "ORDER_NUMBER");
		XmlFAMappingMap.put("ORDER_ID", "ORDER_GUID_NO");
		XmlFAMappingMap.put("GROSS_WEIGHT", "ROUGH_WEIGHT");
		XmlFAMappingMap.put("NET_WEIGHT", "NET_WEIGHT");
		XmlFAMappingMap.put("PKGS_CODE", "PACK_TYPE_CODE");
		XmlFAMappingMap.put("PKGS_NAME", "PACK_TYPE_NAME");
		XmlFAMappingMap.put("SENDER_NAME", "SENDER");
		XmlFAMappingMap.put("CONSIGNEE_CNAME", "RECEIVER");
		XmlFAMappingMap.put("CURR_UNIT", "CURRENCY_CODE");
		XmlFAMappingMap.put("CURR_UNIT_NAME", "CURRENCY_NAME");
		XmlFAMappingMap.put("PROD_NAME_CN", "MAJOR_GOODS_NAME");
		XmlFAMappingMap.put("CREATE_TIME", "CREATE_TIME");
		XmlFAMappingMap.put("LOGISTICS_NO", "LOGISTICS_NO");
		XmlFAMappingMap.put("MAIN_WB_NO", "MAIN_WB_NO");
		XmlFAMappingMap.put("LOGISTICS_CODE", "LOGISTICS_CODE");
		XmlFAMappingMap.put("LOGISTICS_NAME", "LOGISTICS_NAME");
		XmlFAMappingMap.put("INSP_ORG_CODE", "INSP_ORG_CODE");
		XmlFAMappingMap.put("LOCT_NO", "LOCT_NO");
		XmlFAMappingMap.put("ID_TYPE", "ID_TYPE");
		XmlFAMappingMap.put("ID_CARD", "ID_CARD");
		XmlFAMappingMap.put("ID_TYPENAME", "ID_TYPENAME");
		XmlFAMappingMap.put("ID_TEL", "ID_TEL");
		XmlFAMappingMap.put("CONSIGNEE_ADDR", "CONSIGNEE_ADDR");
		XmlFAMappingMap.put("CONSIGNEE_TEL", "CONSIGNEE_TEL");
		XmlFAMappingMap.put("SENDER_ADDRESS", "CONSIGNOR_ADDR");
		XmlFAMappingMap.put("DESP_CTRY_CODE", "DESP_CTRY_CODE");
		XmlFAMappingMap.put("AREA_CODE", "AREA_CODE");
		XmlFAMappingMap.put("AREA_NAME", "AREA_NAME");
		XmlFAMappingMap.put("TRAF_MODE", "TRAF_MODE");
		XmlFAMappingMap.put("TRANS_TYPE_CODE", "TRANS_TYPE_CODE");
		XmlFAMappingMap.put("TRANS_TYPE_NO", "TRANS_TYPE_NO");
		XmlFAMappingMap.put("LICENSE_NO", "LICENSE_NO");
		XmlFAMappingMap.put("PACK_NO", "PACK_NO");
		XmlFAMappingMap.put("REMARK", "REMARK");
		XmlFAMappingMap.put("MESSAGE_SOURCE", "MESSAGE_SOURCE");
		XmlFAMappingMap.put("PRICE_TOTAL_VAL", "WORTH");
		//FA商品
		XmlFAItemMapping.put("FA_GOODS_GUID_NO", "DETAIL_ID");
		XmlFAItemMapping.put("PROD_NAME_CN", "GOODS_NAME");
		XmlFAItemMapping.put("PROD_SPECS", "GOODS_SPECIFICATION");
		XmlFAItemMapping.put("ORI_CTRY_CODE", "PRO_MARKETING_COUNTRY_CODE");
		XmlFAItemMapping.put("ORI_CTRY_NAME", "PRO_MARKETING_COUNTRY_NAME");
		XmlFAItemMapping.put("PRICE_PER_UNIT", "DECLARE_PRICE");
		XmlFAItemMapping.put("QTY", "DECLARE_COUNT");
		XmlFAItemMapping.put("QTY_UNIT_CODE", "DECLARE_MEASURE_UNIT_CODE");
		XmlFAItemMapping.put("QTY_UNIT_NAME", "DECLARE_MEASURE_UNIT_NAME");
		XmlFAItemMapping.put("CREATE_TIME", "CREATE_TIME");
		/*XmlFAItemMapping.put("GOODS_REG_NO", "PRODUCT_RECORD_NO");*/
		XmlFAItemMapping.put("HSCODE", "HS_CODE");
		XmlFAItemMapping.put("SEQ_NO", "SEQ_NO");
		XmlFAItemMapping.put("SKU_NO", "SKU_NO");
		XmlFAItemMapping.put("PROD_BRD_CN", "PROD_BRD_CN");
		XmlFAItemMapping.put("PRICE_TOTAL_VAL", "PRICE_TOTAL_VAL");
		XmlFAItemMapping.put("CURR_UNIT", "CURR_UNIT");
		XmlFAItemMapping.put("COMM_BARCODE", "COMM_BARCODE");
		XmlFAItemMapping.put("REMARK", "REMARK");
		XmlFAItemMapping.put("GROSS_WEIGHT", "GOODS_ROUGH_WEIGHT");
	}
	 
	//
	/**
	 * @param doMethod　操作逻辑来源
	 * @param indx　　　 
	 * @param DataAccess　 
	 * @throws Exception   
	 * 企业备案上报总局逻辑：
	 * 1、DATA_SOURCE为1或者空时(企业申报)，只通过服务轮询来上报总局，只有审核通过才上报
	 * 2、DATA_SOURCE为2时(服务平台申报)，通过应用来上报总局，根据doMethod，来判断不同状态，通过与不通过都要上报
	 */
	public static void SaveEnt(String doMethod,String indx,IDataAccess DataAccess)throws Exception{
			try {
				//根据不同操作决定上报总局状态
				String STATUS=""; 
				String MESSAGE="";
				if(SysUtility.isNotEmpty(doMethod)){
					if("EntToC_Ent".equals(doMethod)||"UpdateEntRegPassed".equals(doMethod)||"UpdateEntRegChangePassed".equals(doMethod)||"ENTRegToC".equals(doMethod)){
						STATUS="3";
						MESSAGE="审核通过";
					}else if("UpdateEntRegOneLevelUnPassed".equals(doMethod)||"UpdateEntRegUnPassed".equals(doMethod)||"UpdateEntRegChangeUnPassed".equals(doMethod)){
						STATUS="4";
						MESSAGE="审核不通过";
					}
				}else{
					return;
				}
				HashMap SourceData = new HashMap();
				//表头
				String Entsql="select * from T_ENT_REG where INDX=?";
				JSONObject jsonEnt=DataAccess.GetTableJSON("T_ENT_REG", Entsql, indx);
				List listEnt=SysUtility.JSONToList("T_ENT_REG", jsonEnt);
				//附件数据
				String	AttchSql="select ATTACH_TYPE,ATTACH_NAME,CREATE_TIME,FILE_TYPE,ATTACH_CONTENT FROM T_ATTACH  WHERE BIZ_TYPE='1' and  p_indx=?";
				JSONObject jsonAttch =   DataAccess.GetTableJSON("T_ATTACH", AttchSql, indx);
				List listAttch=SysUtility.JSONToList("T_ATTACH", jsonAttch);
				SourceData.put("C_ENT_FILING_INFO", listEnt);
				SourceData.put("C_ENT_ATTACHED", listAttch);
				Datas datas = new Datas();
				datas.remove("C_ENT_FILING_INFO");
				datas.remove("C_ENT_ATTACHED");
				datas.remove("C_ENT_FILING_MAG");
				
				datas.MapToDatas("C_ENT_FILING_INFO",SourceData,XmlEntMappingMap);
				datas.MapToDatas("C_ENT_ATTACHED",SourceData,XmlAttchMappingMap);
				datas.MapToDatas("C_ENT_FILING_MAG",SourceData,XmlEntMappingMap);
				
				//状态转换
				String ENT_STATUS=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_STATUS");
				if("0".equals(ENT_STATUS)){
					ENT_STATUS="1";
				}else if("2".equals(ENT_STATUS)){
					ENT_STATUS="3";
				}else if("1".equals(ENT_STATUS)||"4".equals(ENT_STATUS)){
					ENT_STATUS="4";
				}
				//企业类别转换
				String ENT_TYPE="";
				String ENT_TYPE_PL=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_PL");
				String ENT_TYPE_CBE=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_CBE");
				String ENT_TYPE_LST=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_LST");
				String ENT_TYPE_OT=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_OT");
				String ENT_TYPE_AGE=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_AGE");
				String ENT_TYPE_STO=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_STO");
				String ENT_TYPE_RGL=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_RGL");
				String ENT_TYPE_PAY=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_TYPE_PAY");
				datas.SetTableValue("C_ENT_FILING_INFO", "PLAT_NAME",jsonEnt.getJSONArray("T_ENT_REG").getJSONObject(0).getString("ENT_WEBSITE_NAME"));
				if("Y".equals(ENT_TYPE_PL)){
					ENT_TYPE+="1";
				}
				if("Y".equals(ENT_TYPE_CBE)){
					ENT_TYPE+="2";
				}
				if("Y".equals(ENT_TYPE_LST)){
					ENT_TYPE+="3";
				}
				if("Y".equals(ENT_TYPE_OT)||"Y".equals(ENT_TYPE_AGE)||"Y".equals(ENT_TYPE_STO)||"Y".equals(ENT_TYPE_RGL)||"Y".equals(ENT_TYPE_PAY)){
					ENT_TYPE+="4";
				}
				//赋个性化值
				String	MESSAGE_SOURCE=datas.GetTableValue("C_ENT_FILING_INFO", "MONITOR_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_ENT_FILING_INFO", "MESSAGE_DEST");
				}
				String  MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE,2)+"0000";
				String ORG_NO=datas.GetTableValue("C_ENT_FILING_INFO", "TECH_REG_CODE");
				String TEL=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_CONTACTOR_TEL");
				String PERSON=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_LEGAL_PERSON");
				String EMAIL=datas.GetTableValue("C_ENT_FILING_INFO", "E_MAIL");
				String FAX_NO=datas.GetTableValue("C_ENT_FILING_INFO", "FAX_NO");
				String ZIP_CODE=datas.GetTableValue("C_ENT_FILING_INFO", "ZIP_CODE");
				
				datas.SetTableValue("C_ENT_FILING_INFO", "TECH_REG_CODE",SysUtility.getStrByLength(ORG_NO,20));
				datas.SetTableValue("C_ENT_FILING_INFO", "ENT_CONTACTOR_TEL",SysUtility.getStrByLength(TEL,20));
				datas.SetTableValue("C_ENT_FILING_INFO", "ENT_LEGAL_PERSON",SysUtility.getStrByLength(PERSON,40));
				datas.SetTableValue("C_ENT_FILING_INFO", "MONITOR_ORG_CODE",SysUtility.getStrByLength(MESSAGE_SOURCE, 40));
				datas.SetTableValue("C_ENT_FILING_INFO", "E_MAIL",SysUtility.getStrByLength(EMAIL, 50));
				datas.SetTableValue("C_ENT_FILING_INFO", "FAX_NO",SysUtility.getStrByLength(FAX_NO, 40));
				datas.SetTableValue("C_ENT_FILING_INFO", "ZIP_CODE",SysUtility.getStrByLength(ZIP_CODE, 16));
				datas.SetTableValue("C_ENT_FILING_INFO", "EXP_MESSAGE_DEST",MESSAGE_DEST);
				datas.SetTableValue("C_ENT_FILING_INFO", "EXP_MESSAGE_SOURCE",MESSAGE_SOURCE);
				datas.SetTableValue("C_ENT_FILING_INFO", "ENT_TYPE",ENT_TYPE);
				datas.SetTableValue("C_ENT_FILING_INFO", "ENT_STATUS",ENT_STATUS);
				datas.SetTableValue("C_ENT_FILING_INFO", "BUSINESS_TYPE",datas.GetTableValue("C_ENT_FILING_INFO", "BUSINESS_TYPE"));
				datas.SetTableValue("C_ENT_FILING_INFO", "COMPANY_SUBJECT","0");
				datas.SetTableValue("C_ENT_FILING_INFO", "REGIST_OVERSEA","0");
				datas.SetTableValue("C_ENT_FILING_INFO", "TAX_PAYER_NATURE","1");
				//datas.SetTableValue("C_ENT_FILING_INFO", "BUSINESS_MODE","1111");
				datas.SetTableValue("C_ENT_FILING_INFO", "MESSAGE_SOURCE", "");
				datas.SetTableValue("C_ENT_FILING_INFO", "MESSAGE_DEST", "");
				datas.SetTableValue("C_ENT_FILING_INFO", "CREATE_TIME", "");
				
				//附件转换
				String ENT_FILING_INFO_ID=datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID");
				for (int i = 0; i < datas.GetTableRows("C_ENT_ATTACHED"); i++) {
					datas.SetTableValue("C_ENT_ATTACHED", "ENT_FILING_INFO_ID",ENT_FILING_INFO_ID,i);
					datas.SetTableValue("C_ENT_ATTACHED", "ENT_ATTACHED_ID", SysUtility.GetUUID(),i);//设置UUID***************
					datas.SetTableValue("C_ENT_ATTACHED","BIZ_TYPE_CODE","1",i);
					String CERT_TYPE_CODE=datas.GetTableValue("C_ENT_ATTACHED", "CERT_TYPE_CODE",i);
					datas.SetTableValue("C_ENT_ATTACHED","BIZ_TYPE_NAME","所需文件",i);
					String CERT_TYPE_NAME="";
					if("1".equals(CERT_TYPE_CODE)){
						CERT_TYPE_NAME="备案申请";
					}else if("2".equals(CERT_TYPE_CODE)){
						CERT_TYPE_NAME="工商营业执照";
					}else if("3".equals(CERT_TYPE_CODE)){
						CERT_TYPE_NAME="组织机构代码";
					}else if("4".equals(CERT_TYPE_CODE)){
						CERT_TYPE_NAME="法人身份证";
					}else {
						CERT_TYPE_CODE="5";
						CERT_TYPE_NAME="其它";
					}
					datas.SetTableValue("C_ENT_ATTACHED","ATTACHED_SEQ_NO",i+1,i);
					datas.SetTableValue("C_ENT_ATTACHED","CERT_TYPE_CODE",SysUtility.getStrByLength(CERT_TYPE_CODE, 5),i);
					datas.SetTableValue("C_ENT_ATTACHED", "FILE_VERSION", "1.0",i);
					datas.SetTableValue("C_ENT_ATTACHED", "PARSE_TYPE", "1",i);
					//获取文件后缀
					datas.SetTableValue("C_ENT_ATTACHED","CERT_TYPE_NAME",CERT_TYPE_NAME,i);
					String ATTACH_NAME=datas.GetTableValue("C_ENT_ATTACHED", "FILE_NAME",i);
					String FileType=datas.GetTableValue("C_ENT_ATTACHED", "FILE_TYPE",i);
					String FILE_TYPE="";
					if(SysUtility.isEmpty(FileType)){
						 FILE_TYPE=ATTACH_NAME.substring(ATTACH_NAME.lastIndexOf(".")+1);
					}
					else{
						 FILE_TYPE=FileType.substring(FileType.lastIndexOf(".")+1);
					}
									
					//String FILE_TYPE=ATTACH_NAME.substring(ATTACH_NAME.lastIndexOf(".")+1);
					datas.SetTableValue("C_ENT_ATTACHED","FILE_TYPE",FILE_TYPE,i);
				}
				//历史表
				datas.SetTableValue("C_ENT_FILING_MAG", "ENT_FILING_INFO_ID", datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
				datas.SetTableValue("C_ENT_FILING_MAG", "ENT_FILING_MAG_ID", SysUtility.GetUUID());//************UUid
				datas.SetTableValue("C_ENT_FILING_MAG", "OPER_ORG_CODE",SysUtility.getStrByLength(MESSAGE_SOURCE, 20));
				datas.SetTableValue("C_ENT_FILING_MAG", "OPER_PERSON_NAME", SysUtility.getStrByLength(datas.GetTableValue("C_ENT_FILING_INFO", "APPROVALER"), 40));
				datas.SetTableValue("C_ENT_FILING_MAG", "STATUS", STATUS);
				datas.SetTableValue("C_ENT_FILING_MAG", "MESSAGE", MESSAGE);
				datas.SetTableValue("C_ENT_FILING_MAG", "OPER_PERSON_CODE", "auto");
				datas.SetTableValue("C_ENT_FILING_MAG", "CREATE_TIME",  SysUtility.getDateFormat().format(new Date()));
				datas.SetTableValue("C_ENT_FILING_MAG", "OPER_DATE", SysUtility.getDateFormat().format(new Date()));
				
				//1.删除已存在数据
				String delEntSql="delete from C_ENT_FILING_INFO where ENT_FILING_INFO_ID=?";
				DataAccess.ExecSQL(delEntSql, datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
				String delEntMagSql="delete from C_ENT_FILING_MAG where ENT_FILING_INFO_ID=?";
				DataAccess.ExecSQL(delEntMagSql, datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
				String delAttSql="delete from C_ENT_ATTACHED where ENT_FILING_INFO_ID=?";
				DataAccess.ExecSQL(delAttSql, datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
				
				
				//DATA_SOURCE为1，服务轮询插入C表，为2,审核通过插入C表
				String DATA_SOURCE=	datas.GetTableValue("C_ENT_FILING_INFO", "DATA_SOURCE");
	            if("1".equals(DATA_SOURCE)||SysUtility.isEmpty(DATA_SOURCE)){
	            	//如果是来自企业的数据，那么只有3审核通过才上报总局,并且只有服务轮询才要报   
	            	//变更申报通过也走下面的方法
	            	if(("ENTRegToC".equals(doMethod)||"UpdateEntRegChangePassed".equals(doMethod))&&"3".equals(STATUS)){
	            		//赋默认值
		    			datas.SetTableValue("C_ENT_FILING_INFO", "PROCESS_STATUS","1");
		    			datas.SetTableValue("C_ENT_FILING_INFO", "MSG_FLAG","1");
		    			//2.插入C表
		    			datas.InsertDB(DataAccess,"C_ENT_FILING_INFO", "C_ENT_FILING_INFO","ENT_FILING_INFO_ID");
		    			datas.InsertDB(DataAccess,"C_ENT_ATTACHED", "C_ENT_ATTACHED","ENT_FILING_INFO_ID");
		    			datas.InsertDB(DataAccess,"C_ENT_FILING_MAG", "C_ENT_FILING_MAG","ENT_FILING_MAG_ID");
		            	//3.插入任务表
		    			SaveCbecSendTable(DataAccess, "SEND_I_ENT_FILING_INFO", datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
		    			//4.更新主表MSG_FLAG为1
		    			String upENT="UPDATE T_ENT_REG SET MSG_FLAG='1' WHERE ENT_GUID_NO=?";
		    			DataAccess.ExecSQL(upENT, datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
	            	} 
	            }else if("2".equals(DATA_SOURCE)){
	            	//来自服务平台上报结果
	            	datas.SetTableValue("C_ENT_FILING_INFO", "PROCESS_RESULT_STATUS","1");
	            	//2.插入C表
	            	datas.InsertDB(DataAccess,"C_ENT_FILING_INFO", "C_ENT_FILING_INFO","ENT_FILING_INFO_ID");
	    			datas.InsertDB(DataAccess,"C_ENT_FILING_MAG", "C_ENT_FILING_MAG","ENT_FILING_MAG_ID");
	            	//3.插入任务
	    			SaveCbecSendTable(DataAccess, "UP_ENT_FILING_INFO", datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
	    			//4.更新主表MSG_FLAG为1
	    			String upENT="UPDATE T_ENT_REG SET MSG_FLAG='1' WHERE ENT_GUID_NO=?";
	    			DataAccess.ExecSQL(upENT, datas.GetTableValue("C_ENT_FILING_INFO", "ENT_FILING_INFO_ID"));
	            }
	            DataAccess.ComitTrans();
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_ENT_REG SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, indx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				e.printStackTrace();
				DataAccess.ComitTrans();
			}

		}
	/**
	 * @param doMethod　操作逻辑来源
	 * @param indx　　　 
	 * @param DataAccess　 
	 * @throws Exception   
	 * 商品备案上报总局逻辑：
	 * 1、DATA_SOURCE为1或者空时(企业申报)，只通过服务轮询来上报总局，只有审核通过才上报
	 * 2、DATA_SOURCE为2时(服务平台申报)，通过应用来上报总局，根据doMethod，来判断不同状态，通过与不通过都要上报
	 */
    public static void SaveGoods(String doMethod,String GoodsIndx,IDataAccess DataAccess)throws Exception{
		try {
			/**根据不同操作 决定上报总局状态*/
			String STATUS="";
			String MESSAGE="";
			if(SysUtility.isNotEmpty(doMethod)){
				if("GoodToC_Pro".equals(doMethod)||"GOODSRegToC".equals(doMethod)||"CMtoReg_Goods".equals(doMethod)||"GoodsBatchToDoChange".equals(doMethod)||"GoodsBatchToDoChangeList".equals(doMethod)||"btnTwoLevelPassed".equals(doMethod)||"CM_GoodsImport".equals(doMethod)){
					STATUS="3";
					MESSAGE="审核通过";
				}else if("GoodsBatchToDoChangeUnPass".equals(doMethod)||"btnOneLevelUnPassed".equals(doMethod)||"GoodsBatchToDoChangeUnPassList".equals(doMethod)){
					STATUS="4";
					MESSAGE="审核不通过";
				}
			}else{
				return;
			}
			
			HashMap SourceData =new HashMap();
			//表头
			String	GoodssSql="select * from t_goods_reg where indx=?";
			JSONObject jsonGoods=DataAccess.GetTableJSON("T_GOODS_REG", GoodssSql, GoodsIndx);
			List listGoods=SysUtility.JSONToList("T_GOODS_REG", jsonGoods);
			//附件
			String AttchSql="select ATTACH_TYPE,ATTACH_NAME,CREATE_TIME,FILE_TYPE,ATTACH_CONTENT FROM T_ATTACH  WHERE   BIZ_TYPE='2' and  p_indx=?";
			JSONObject jsonAttch=DataAccess.GetTableJSON("T_ATTACH", AttchSql, GoodsIndx);
			List Attch=SysUtility.JSONToList("T_ATTACH", jsonAttch);
			
			SourceData.put("C_PRODUCT_FILING_INFO", listGoods);
			SourceData.put("C_PRODUCT_ATTACHED", Attch);

			Datas datas = new Datas();
			datas.remove("C_PRODUCT_FILING_INFO");
			datas.remove("C_PRODUCT_ATTACHED");
			datas.remove("C_PRODUCT_FILING_MAG");
			
			datas.MapToDatas("C_PRODUCT_FILING_INFO",SourceData,XmlGoodsMappingMap);
			datas.MapToDatas("C_PRODUCT_ATTACHED",SourceData,XmlGoodAttchsMappingMap);
			
			String MESSAGE_SOURCE=datas.GetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_CODE");
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("C_PRODUCT_FILING_INFO", "MESSAGE_DEST");
				datas.SetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_CODE", SysUtility.getStrByLength(MESSAGE_SOURCE,20));
			}
			//更新T_GOODS_REG表 ENT_CBEC_NO字段
			String ENT_REG_NO=datas.GetTableValue("C_PRODUCT_FILING_INFO", "ENT_REG_NO");
			String sql ="SELECT * FROM T_ENT_REG WHERE ENT_REG_NO=?";
			Datas datass = DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
			String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			String sqlupgoods ="update T_GOODS_REG set ENT_CBEC_NO='"+ENT_CBEC_NO+"' where indx=?";
			DataAccess.ExecSQL(sqlupgoods,GoodsIndx);
			String ENT_CNAME=datass.GetTableValue("T_ENT_REG", "ENT_CNAME");
			String PRODUCT_NAME=datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_NAME");
			String BRAND=datas.GetTableValue("C_PRODUCT_FILING_INFO", "BRAND");
			String MODEL=datas.GetTableValue("C_PRODUCT_FILING_INFO", "MODEL");
			String BASES=datas.GetTableValue("C_PRODUCT_FILING_INFO", "BASES");
			String  SKU=datas.GetTableValue("C_PRODUCT_FILING_INFO", "SKU");
			String PROD_ENT=datas.GetTableValue("C_PRODUCT_FILING_INFO", "PROD_ENT");
			String MONITOR_ORG_NAME=datas.GetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_NAME");
			String REMARK=datas.GetTableValue("C_PRODUCT_FILING_INFO", "REMARK");
			
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_CODE",SysUtility.getStrByLength(MESSAGE_SOURCE,20));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "MESSAGE_SOURCE", "");
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "EXP_MESSAGE_SOURCE",MESSAGE_SOURCE);
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "EXP_MESSAGE_DEST",MESSAGE_DEST);
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "ENT_CNAME", ENT_CNAME);
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_STATUS", STATUS);
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "MESSAGE_DEST", "");
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "ENTER_OUT_FLAG", "1");
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "CREATE_TIME", "");
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_NAME", SysUtility.getStrByLength(PRODUCT_NAME,400));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "BRAND", SysUtility.getStrByLength(BRAND,255));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "MODEL", SysUtility.getStrByLength(MODEL,300));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "BASES", SysUtility.getStrByLength(BASES,400));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "SKU", SysUtility.getStrByLength(SKU,30));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "PROD_ENT", SysUtility.getStrByLength(PROD_ENT,30));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_NAME", SysUtility.getStrByLength(MONITOR_ORG_NAME,100));
			datas.SetTableValue("C_PRODUCT_FILING_INFO", "REMARK", SysUtility.getStrByLength(REMARK,1000));
			if("Y".equals(datas.GetTableValue("C_PRODUCT_FILING_INFO", "BIZ_TYPE_I"))||"Y".equals(datas.GetTableValue("C_PRODUCT_FILING_INFO", "BIZ_TYPE_O"))){
				datas.SetTableValue("C_PRODUCT_FILING_INFO", "TRADE_MODE", "2");
			}
			if("Y".equals(datas.GetTableValue("C_PRODUCT_FILING_INFO", "BIZ_TYPE_C"))){
				datas.SetTableValue("C_PRODUCT_FILING_INFO", "TRADE_MODE", "1");
			}
			//历史表
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "PRODUCT_FILING_INFO_ID", datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"));
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "PRODUCT_FILING_MAG_ID", SysUtility.GetUUID());
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "OPER_ORG_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_PRODUCT_FILING_INFO", "OPER_ORG_CODE"),20));
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "CREATE_TIME", "");
			if(SysUtility.isEmpty(datas.GetTableValue("C_PRODUCT_FILING_INFO", "OPER_ORG_CODE"))){
				datas.SetTableValue("C_PRODUCT_FILING_MAG", "OPER_ORG_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_PRODUCT_FILING_INFO", "MONITOR_ORG_CODE"),20));
			}
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "OPER_DATE", datas.GetTableValue("C_PRODUCT_FILING_INFO", "AUDIT_DATE"));
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "OPER_PERSON_NAME", SysUtility.getStrByLength(datas.GetTableValue("C_PRODUCT_FILING_INFO", "APPROVALER"),40));
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "STATUS", STATUS);
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "MESSAGE",MESSAGE);
			datas.SetTableValue("C_PRODUCT_FILING_MAG", "OPER_PERSON_CODE", "auto");
			for (int i = 0; i < datas.GetTableRows("C_PRODUCT_ATTACHED"); i++) {
				String ATTACH_TYPE=datas.GetTableValue("C_PRODUCT_ATTACHED", "ATTACH_TYPE",i);
				String CERT_TYPE_NAME="";
				if(!("1".equals(ATTACH_TYPE)||"2".equals(ATTACH_TYPE)||"3".equals(ATTACH_TYPE)||"4".equals(ATTACH_TYPE))){				
					ATTACH_TYPE="5";
					CERT_TYPE_NAME="其他可提供的证明材料";
				}
				else{
					String sqltype="select ITEMNAME from B_BBD_GOODSFILETYPE where ITEMCODE = ?";
					Datas datastype = DataAccess.GetTableDatas("T_ATTACH", sqltype,ATTACH_TYPE);
					CERT_TYPE_NAME=datastype.GetTableValue("T_ATTACH", "ITEMNAME");
				}
				datas.SetTableValue("C_PRODUCT_ATTACHED", "CERT_TYPE_CODE", ATTACH_TYPE,i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "ATTACHED_SEQ_NO", i+1,i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "PRODUCT_FILING_INFO_ID", datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"),i);
				
				String FILE_NAME=datas.GetTableValue("C_PRODUCT_ATTACHED", "FILE_NAME",i);
				String FileType=datas.GetTableValue("C_PRODUCT_ATTACHED", "FILE_TYPE",i);
				String FILE_TYPE="";
				if(SysUtility.isEmpty(FileType)){
					 FILE_TYPE=FILE_NAME.substring(FILE_NAME.lastIndexOf(".")+1);
				}
				else{
					 FILE_TYPE=FileType.substring(FileType.lastIndexOf(".")+1);
				}
				
				datas.SetTableValue("C_PRODUCT_ATTACHED", "BIZ_TYPE_CODE","1",i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "BIZ_TYPE_NAME","所需文件",i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "CERT_TYPE_NAME",CERT_TYPE_NAME,i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "FILE_TYPE",FILE_TYPE,i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "FILE_VERSION", "1.0",i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "PARSE_TYPE", "1",i);
				datas.SetTableValue("C_PRODUCT_ATTACHED", "PRODUCT_ATTACHED_ID",SysUtility.GetUUID(),i);
			}
			//1.删除已存在数据
			String delGoodsSql="delete from C_PRODUCT_FILING_INFO WHERE PRODUCT_FILING_INFO_ID=?";
			DataAccess.ExecSQL(delGoodsSql, datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"));
			String delGOODSMagSql="delete from C_PRODUCT_FILING_MAG WHERE PRODUCT_FILING_INFO_ID=?";
			DataAccess.ExecSQL(delGOODSMagSql, datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"));
			String delAttSql="delete from C_PRODUCT_ATTACHED WHERE PRODUCT_FILING_INFO_ID=?";
			DataAccess.ExecSQL(delAttSql, datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"));
			
			String DATA_SOURCE=datas.GetTableValue("C_PRODUCT_FILING_INFO", "DATA_SOURCE"); 
			if("1".equals(DATA_SOURCE)||SysUtility.isEmpty(DATA_SOURCE)){
				//如果是来自企业的数据，那么只有3审核通过才上报总局,并且只有服务轮询才要报
				//变更审核通过的也走下面这个方法上报
				if(("GOODSRegToC".equals(doMethod)||"GoodsBatchToDoChange".equals(doMethod)||"GoodsBatchToDoChangeList".equals(doMethod))&&"3".equals(STATUS)){
					datas.SetTableValue("C_PRODUCT_FILING_INFO", "PROCESS_STATUS","1");
					datas.SetTableValue("C_PRODUCT_FILING_INFO", "MSG_FLAG","1");
					//2.插入C表
					datas.InsertDB(DataAccess, "C_PRODUCT_FILING_INFO", "C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID");
					datas.InsertDB(DataAccess,"C_PRODUCT_FILING_MAG", "C_PRODUCT_FILING_MAG","PRODUCT_FILING_INFO_ID");
				    datas.InsertDB(DataAccess, "C_PRODUCT_ATTACHED", "C_PRODUCT_ATTACHED", "PRODUCT_ATTACHED_ID");
				    //3.插入任务表
				    SaveCbecSendTable(DataAccess, "SEND_I_PRODUCT_FILING_INFO",  datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"),  MESSAGE_SOURCE, MESSAGE_DEST);
				    //4.更新主表
				    String sqlgoods="UPDATE T_GOODS_REG SET MSG_FLAG='1' WHERE INDX=?";
					DataAccess.ExecSQL(sqlgoods, GoodsIndx);   
				}
			}else if("2".equals(DATA_SOURCE)){
				datas.SetTableValue("C_PRODUCT_FILING_INFO", "PROCESS_RESULT_STATUS","1");
				//2.插入C表
				datas.InsertDB(DataAccess, "C_PRODUCT_FILING_INFO", "C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID");
				datas.InsertDB(DataAccess,"C_PRODUCT_FILING_MAG", "C_PRODUCT_FILING_MAG","PRODUCT_FILING_INFO_ID");
			    //3.插入任务
				SaveCbecSendTable(DataAccess, "UP_PRODUCT_FILING_INFO",  datas.GetTableValue("C_PRODUCT_FILING_INFO", "PRODUCT_FILING_INFO_ID"),  MESSAGE_SOURCE, MESSAGE_DEST);
				//4.更新主表
    			String sqlgoods="UPDATE T_GOODS_REG SET MSG_FLAG='1' WHERE INDX=?";
				DataAccess.ExecSQL(sqlgoods, GoodsIndx);
			}
			DataAccess.ComitTrans();
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			String sql="UPDATE T_GOODS_REG SET MSG_FLAG='2' WHERE INDX=?";
			DataAccess.ExecSQL(sql, GoodsIndx);
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			DataAccess.ComitTrans();
		}
	}
    
   
    /**入区清单上报
     * @param doMethod
     * @param Iindx
     * @param DataAccess
     * @throws Exception
     */
    public static void SaveI(String doMethod,String Iindx,IDataAccess DataAccess)throws Exception{
    	if("UpdateIICfBatchDo".equals(doMethod))
    		SaveIICF(Iindx, DataAccess);
    	if("UpdateIICfBatchToDo".equals(doMethod))
    		SaveIIBatch(Iindx, DataAccess);
    	if("UpdateIIPassBatchDo".equals(doMethod))//通过服务轮询上报，SaveII不用
    		SaveII(Iindx, DataAccess);
    	if("UpdateIOCfBatchDo".equals(doMethod))
    		SvaeIOCF(Iindx, DataAccess);
    	if("UpdateIOCfBatchToDo".equals(doMethod))
    		SaveIOBatch(Iindx, DataAccess);
    	if("UpdateIOPassBatchDo".equals(doMethod))//通过服务轮询上报，SaveIO不用
    		SaveIO(Iindx, DataAccess);
    	if("OtoC_DECL_II".equals(doMethod))
    		SaveIIByFw(Iindx, DataAccess);
    	if("OtoC_DECL_IO".equals(doMethod))
    		SaveIOByFw(Iindx, DataAccess);
    }
    /**出区清单上报
     * @param doMethod
     * @param Iindx
     * @param DataAccess
     * @throws Exception
     */
    public static void SaveO(String doMethod,String Oindx,IDataAccess DataAccess)throws Exception{
    	if("UpdateOICfBatchDo".equals(doMethod))
    		SaveOICF(Oindx, DataAccess);
    	if("UpdateOICfBatchToDo".equals(doMethod))
    		SaveOIBatch(Oindx, DataAccess);
    	if("UpdateOIPassBatchDo".equals(doMethod))//通过服务轮询上报，SaveOI不用
    		SaveOI(Oindx, DataAccess);
    	if("UpdateOOCfBatchDo".equals(doMethod))
    		SvaeOOCF(Oindx, DataAccess);
    	if("UpdateOOCfBatchToDo".equals(doMethod))
    		SaveOOBatch(Oindx, DataAccess);
    	if("UpdateOOPassBatchDo".equals(doMethod))//通过服务轮询上报，SaveOO不用
    		SaveOO(Oindx, DataAccess);
    	if("OtoC_DECL_OI".equals(doMethod))
    		SaveOIByFw(Oindx, DataAccess);
    	if("OtoC_DECL_OO".equals(doMethod))
    		SaveOOByFw(Oindx, DataAccess);
    }
    public static void SaveFa(String doMethod,String faIndx,IDataAccess DataAccess)throws Exception{
    	if("UpdateFAReBatch".equals(doMethod)||"UpdateReturnedBack".equals(doMethod))
    		SavereFaBack(faIndx, DataAccess);
    	if("UpdateFAReCheckBack".equals(doMethod))
    		SaveFaCheckBack(faIndx, DataAccess);
    	if("UpdateFAReCheckBatchDetain".equals(doMethod))
    		SaveFaBeta(faIndx, DataAccess);
    	if("UpdateFAReCheckPass".equals(doMethod)||"UpdateQuaRecord".equals(doMethod))//通过服务轮询上报，SaveFA不用
    		SaveFaPass(faIndx, DataAccess);
    	if("UpdateFARevApproved_check".equals(doMethod)||"UpdateFARevExpressCheck".equals(doMethod))
    		SaveFaCheck(faIndx, DataAccess);
    	if("UpdateFARevApproved_pass".equals(doMethod)||"UpdateFARevExpressPass".equals(doMethod))
    		SavereFaPass(faIndx, DataAccess);
    	if("OtoC_DECL_FA".equals(doMethod))
    		SaveFaPassByFw(faIndx, DataAccess);
    	
    }
    
	//II入区清单放行
	public static void SaveII(String Iindx,IDataAccess DataAccess){

		try {
			HashMap SourceData=new HashMap();
			//表头
			String sqlII="select * from T_DECL_II where indx=?";
			JSONObject jsonII=DataAccess.GetTableJSON("T_DECL_II", sqlII, Iindx);
			List listII=SysUtility.JSONToList("T_DECL_II", jsonII);
			//货物表体
			String sqlIIGoods="select * from T_DECL_II_GOODS WHERE P_INDX=?";
			JSONObject jsonIG=DataAccess.GetTableJSON("T_DECL_II_GOODS", sqlIIGoods,Iindx);
			List listIIG=SysUtility.JSONToList("T_DECL_II_GOODS", jsonIG);
			//集装箱表体
			String sqlConts="SELECT * FROM T_CONTAINER WHERE BIZ_TYPE='1' AND P_INDX=?";
			JSONObject JsonCont=DataAccess.GetTableJSON("T_CONTAINER", sqlConts,Iindx);
			List listCont=SysUtility.JSONToList("T_CONTAINER", JsonCont);
			SourceData.put("C_ENTER_DECLARE", listII);
			SourceData.put("C_ENTER_DECLARE_ITEMS", listIIG);
			SourceData.put("C_ENTER_DECLARE_CONT",listCont );

			
			Datas datas = new Datas();
			datas.remove("C_ENTER_DECLARE");
			datas.remove("C_ENTER_DECLARE_ITEMS");
			datas.remove("C_ENTER_DECLARE_CONT");
			datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlIIMappingMap);
			datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlIItemMappingMap);
			datas.MapToDatas("C_ENTER_DECLARE_CONT", SourceData,XmlContMappingMap);
           
			
			String	MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
			String sql="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
			Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
			String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			
			
			String DATA_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				//1插入主表数据C_DECLARE_RELEASE_RECORD
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 40));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  MESSAGE_SOURCE);
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "APPROVALER"), 20));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength( ENT_CBEC_NO, 20));
				datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
				//2、插入任务表
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
					SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),  MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					EXS="EXS";
				}
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);

			}
				datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
				datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "1");
				datas.SetTableValue("C_ENTER_DECLARE", "IE_FLAG", "I");
				datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
				datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
				datas.SetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST", "");
				datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);
				
				String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
				
				String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
				
				String TRANS_TYPE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 4));
				
				String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE", SysUtility.getStrByLength(TRADE_COUNTRY_CODE, 4));
				
				String GOODS_PLACE=datas.GetTableValue("C_ENTER_DECLARE", "GOODS_PLACE");
				datas.SetTableValue("C_ENTER_DECLARE", "GOODS_PLACE", SysUtility.getStrByLength(GOODS_PLACE, 50));
				
				String DECL_REG_CODE=datas.GetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE", SysUtility.getStrByLength(DECL_REG_CODE, 10));
				
				String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME", SysUtility.getStrByLength(CBE_NAME, 100));
				
				String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL", SysUtility.getStrByLength(CONSIGNEE_TEL, 20));
				
				String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
				
				datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);
				
				String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
				datas.SetTableValue("C_ENTER_DECLARE", "REMARK",SysUtility.getStrByLength(REMARK, 1000));
				datas.GetTableValue("DECL_DECLAR_CHECK_ID", "DECL_DECLAR_CHECK_ID");
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
				//2、插入商品
				for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);		
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);	
					
					
					String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
					
					String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
					
			        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
			        
			        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK", SysUtility.getStrByLength(REMARK1, 1000),i);
			        
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i),i);
				    
			        String GOODS_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO",i);
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", SysUtility.getStrByLength(GOODS_NO, 3),i);
			        
			        
			        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
				//插入集装箱
				for (int i=0;i<datas.GetTableRows("C_ENTER_DECLARE_CONT");i++){
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "UUID", SysUtility.GetUUID(),i);
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_DECLAR_CHECK_ID", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_NO", SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 50),i);
				}
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE_CONT", "C_ENTER_DECLARE_CONT", "UUID");
				if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
					//3、插入任务表
					SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
				}
		
		} catch (Exception e) {
  			LogUtil.printLog(e.getMessage(), Level.ERROR);
  		}
	}
	//II入区清单放行
		public static void SaveIIByFw(String Iindx,IDataAccess DataAccess)throws Exception{

			try {
				HashMap SourceData=new HashMap();
				String sqlII="select * from T_DECL_II where indx=?";
				JSONObject jsonII=DataAccess.GetTableJSON("T_DECL_II", sqlII, Iindx);
				List listII=SysUtility.JSONToList("T_DECL_II", jsonII);

				String sqlIIGoods="select * from T_DECL_II_GOODS WHERE P_INDX=?";
				JSONObject jsonIG=DataAccess.GetTableJSON("T_DECL_II_GOODS", sqlIIGoods,Iindx);
				List listIIG=SysUtility.JSONToList("T_DECL_II_GOODS", jsonIG);

				String sqlConts="SELECT * FROM T_CONTAINER WHERE BIZ_TYPE='1' AND P_INDX=?";
				JSONObject JsonCont=DataAccess.GetTableJSON("T_CONTAINER", sqlConts,Iindx);
				List listCont=SysUtility.JSONToList("T_CONTAINER", JsonCont);
	 

				SourceData.put("C_ENTER_DECLARE", listII);
				SourceData.put("C_ENTER_DECLARE_ITEMS", listIIG);
				SourceData.put("C_ENTER_DECLARE_CONT",listCont );

				Datas datas = new Datas();
				datas.remove("C_ENTER_DECLARE");
				datas.remove("C_ENTER_DECLARE_ITEMS");
				datas.remove("C_ENTER_DECLARE_CONT");
				datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlIIMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlIItemMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_CONT", SourceData,XmlContMappingMap);
	           
				String DATA_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
				String	MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
	       
				String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
				String sql="select ENT_CBEC_NO from t_ent_reg where (ENT_TYPE_CBE='Y' OR ENT_TYPE_PL='Y') AND  ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
				String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				String DECL_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "DECL_REG_NO");
				String sqlENT_CODE="select ENT_CBEC_NO from t_ent_reg where    ENT_REG_NO=?";
				Datas datasENTCODE=DataAccess.GetTableDatas("T_ENT_REG", sqlENT_CODE,DECL_REG_NO);
				String ENT_CBEC_NO_ENT_CODE=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//1插入主表数据C_DECLARE_RELEASE_RECORD
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "APPROVALER"), 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength( ENT_CBEC_NO, 20));
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
					//2、插入任务表
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),  MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
						EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);

				}else{
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "1");
					datas.SetTableValue("C_ENTER_DECLARE", "IE_FLAG", "I");
					//datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST", "");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_ENTER_DECLARE", "CREATE_TIME", "");
					datas.SetTableValue("C_ENTER_DECLARE", "SEND_SOURCE_NODE", MESSAGE_DEST);
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_REG_NO", DECL_REG_NO);
					String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
					
					String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
					
					String TRANS_TYPE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 4));
					
					String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE", SysUtility.getStrByLength(TRADE_COUNTRY_CODE, 4));
					
					String GOODS_PLACE=datas.GetTableValue("C_ENTER_DECLARE", "GOODS_PLACE");
					datas.SetTableValue("C_ENTER_DECLARE", "GOODS_PLACE", SysUtility.getStrByLength(GOODS_PLACE, 50));
					 
					String DECL_REG_CODE=datas.GetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE", SysUtility.getStrByLength(DECL_REG_CODE, 10));
					
					String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME", SysUtility.getStrByLength(CBE_NAME, 100));
					
					String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL", SysUtility.getStrByLength(CONSIGNEE_TEL, 20));
					
					String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
					
					datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);
					
					String REMARK=datas.GetTableValue("C_ENTER_DECsLARE", "REMARK");
					datas.SetTableValue("C_ENTER_DECLARE", "REMARK",SysUtility.getStrByLength(REMARK, 1000));
					datas.GetTableValue("DECL_DECLAR_CHECK_ID", "DECL_DECLAR_CHECK_ID");
					//2、插入商品
					for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);		
					
						String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
						
						String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
						
				        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
				        
				        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK", SysUtility.getStrByLength(REMARK1, 1000),i);
				        
				        String GOODS_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", SysUtility.getStrByLength(GOODS_NO, 3),i);
				        
				        String GOODS_MODEL= SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_MODEL"), 40);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_MODEL",GOODS_MODEL,i);
				  
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "CREATE_TIME", "",i);
				        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
				        if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							if(SysUtility.isEmpty(GOODS_CBEC_NO)){
								return;
							}
							datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					
					//插入集装箱
					for (int i=0;i<datas.GetTableRows("C_ENTER_DECLARE_CONT");i++){
						datas.SetTableValue("C_ENTER_DECLARE_CONT", "UUID", SysUtility.GetUUID(),i);
						datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_DECLAR_CHECK_ID", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_NO", SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 50),i);
					}
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						//3、插入任务表
						datas.InsertDB(DataAccess, "C_ENTER_DECLARE_CONT", "C_ENTER_DECLARE_CONT", "UUID");
						datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
						datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
						SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
						String sqliis="update T_DECL_II set MSG_FLAG='1' where indx=?";
						DataAccess.ExecSQL(sqliis, Iindx);
					}
				}
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_DECL_II SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, Iindx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				e.printStackTrace();
	  		}
		}
	//II布控
	public static void SaveIIBatch(String  Iindx,IDataAccess DataAccess)throws Exception{

		try {
			HashMap SourceData=new HashMap();
			//表头
			String sqlII="select * from T_DECL_II where indx=?";
			JSONObject jsonII=DataAccess.GetTableJSON("T_DECL_II", sqlII, Iindx);
			List listII=SysUtility.JSONToList("T_DECL_II", jsonII);
			SourceData.put("T_DECL_II", listII);
			
			Datas datas = new Datas();
			datas.remove("T_DECL_II");
			datas.MapToDatas("T_DECL_II", SourceData,XmlIIMappingMap);
			String DATA_SOURCE=	datas.GetTableValue("T_DECL_II", "DATA_SOURCE");
			String	MESSAGE_SOURCE=datas.GetTableValue("T_DECL_II", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("T_DECL_II", "MESSAGE_DEST");
			}
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			
			String CBE_CODE =datas.GetTableValue("T_DECL_II", "E_BUSINESS_COMPANY_CODE");
			 String ENT_CBEC_NO="";
			if(SysUtility.isNotEmpty(CBE_CODE)){
				String sql="SELECT * FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,CBE_CODE);
				ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			}
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", datas.GetTableValue("T_DECL_II", "DECL_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_II", "DECL_NO"), 40));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("T_DECL_II", "DECL_NO"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE",SysUtility.getStrByLength(ENT_CBEC_NO, 30));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("T_DECL_II", "CBE_NAME"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("T_DECL_II", "INSP_ORG_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_II", "CF_PERSON"), 50));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("T_DECL_II", "CF_TYPE_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "1");
				datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");
				//3、插入任务表
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
					SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD",datas.GetTableValue("T_DECL_II", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("T_DECL_II", "DECL_DECLAR_CHECK_ID"));
				    EXS="EXS";
				}
				
				String CF_TYPE_CODE= datas.GetTableValue("T_DECL_II", "CF_TYPE_CODE");
				String MESSAGE_TYPE="";
				String MESSAGE_DESC="";
				if("1".equals(CF_TYPE_CODE)){
					MESSAGE_TYPE="4";
					MESSAGE_DESC="待查验";
				}else if("2".equals(CF_TYPE_CODE)||"3".equals(CF_TYPE_CODE)){
					MESSAGE_TYPE="5";
					MESSAGE_DESC="查验完成";
				}
				SaveRSP(DataAccess, datas.GetTableValue("T_DECL_II", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	//II查验结果登记确认放行
	public static void SaveIICF(String Iindx,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			String sqlII="select * from T_DECL_II where indx=?";
			JSONObject jsonII=DataAccess.GetTableJSON("T_DECL_II", sqlII, Iindx);
			List listII=SysUtility.JSONToList("T_DECL_II", jsonII);

			String sqlIIGoods="select * from T_DECL_II_GOODS WHERE P_INDX=?";
			JSONObject jsonIG=DataAccess.GetTableJSON("T_DECL_II_GOODS", sqlIIGoods,Iindx);
			List listIIG=SysUtility.JSONToList("T_DECL_II_GOODS", jsonIG);

			String sqlConts="SELECT * FROM T_CONTAINER WHERE BIZ_TYPE='1' AND P_INDX=?";
			JSONObject JsonCont=DataAccess.GetTableJSON("T_CONTAINER", sqlConts,Iindx);
			List listCont=SysUtility.JSONToList("T_CONTAINER", JsonCont);

			String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='1' AND P_INDX=?";
			JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, Iindx);
			List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);

			SourceData.put("C_ENTER_DECLARE", listII);
			SourceData.put("C_ENTER_DECLARE_ITEMS", listIIG);
			SourceData.put("C_ENTER_DECLARE_CONT",listCont );
			SourceData.put("C_DECLARE_CHECK_RESULT",listCF );

			Datas datas = new Datas();
			datas.remove("C_ENTER_DECLARE");
			datas.remove("C_ENTER_DECLARE_ITEMS");
			datas.remove("C_ENTER_DECLARE_CONT");
			datas.remove("C_DECLARE_CHECK_RESULT");
			datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlIIMappingMap);
			datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlIItemMappingMap);
			datas.MapToDatas("C_ENTER_DECLARE_CONT", SourceData,XmlContMappingMap);
			datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
			String  MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			String DATA_SOURCE=	datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
			 String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
			 String sql="select ENT_CBEC_NO,ENT_CNAME from t_ent_reg where ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
			 Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
			 String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			 String ENT_CNAME=datass.GetTableValue("T_ENT_REG", "ENT_CNAME");
			 
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				//1、插入查验结果
			String 	CHECKLIST_RESULT_ID=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID",SysUtility.getStrByLength(CHECKLIST_RESULT_ID, 32));
			
			String CHECKLIST_NO=datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO",SysUtility.getStrByLength(CHECKLIST_NO, 40));
			
			String PERSON_CODE=datas.GetTableValue("C_ENTER_DECLARE", "PERSON_CODE");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "PERSON_CODE",SysUtility.getStrByLength(PERSON_CODE, 20));

			String SPOT_DESC=datas.GetTableValue("C_ENTER_DECLARE", "SPOT_DESC");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC",SysUtility.getStrByLength(SPOT_DESC, 500));
			
			String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "REMARK",SysUtility.getStrByLength(REMARK, 500));
			datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);

			String RECORD_PLACE=datas.GetTableValue("C_ENTER_DECLARE", "RECORD_PLACE");
			datas.SetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE",SysUtility.getStrByLength(RECORD_PLACE, 50));
			
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "1");
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO);
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_NAME", ENT_CNAME);
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_PLATFORM_CODE", datas.GetTableValue("C_ENTER_DECLARE", "E_BUSINESS_PLATFORM_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_ENTER_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				
				datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
                String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
                	SaveCbecSendTable(DataAccess,  "UP_CHECKLIST_RESULT", datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
    				String upStatus="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
    				DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
    				EXS="EXS";
				}
				//2、插入任务表
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);

			}
				//1、插入数据
				datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
				datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "1");
				datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "I");
				datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
				datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
				datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);

				String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
				
				String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
				
				String TRANS_TYPE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 4));
				
				String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE", SysUtility.getStrByLength(TRADE_COUNTRY_CODE, 4));
				
				String GOODS_PLACE=datas.GetTableValue("C_ENTER_DECLARE", "GOODS_PLACE");
				datas.SetTableValue("C_ENTER_DECLARE", "GOODS_PLACE", SysUtility.getStrByLength(GOODS_PLACE, 50));
				
				String DECL_REG_CODE=datas.GetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE");
				datas.SetTableValue("C_ENTER_DECLARE", "DECL_REG_CODE",SysUtility.getStrByLength(DECL_REG_CODE, 10));
				
				String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
				datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME", SysUtility.getStrByLength(CBE_NAME, 100));
				
				String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL", SysUtility.getStrByLength(CONSIGNEE_TEL, 20));
				
				String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
				datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
				
				String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
				datas.SetTableValue("C_ENTER_DECLARE", "REMARK",SysUtility.getStrByLength(REMARK, 1000));
				
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
				//2、插入商品
				for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);		
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
					
					String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
					
					String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
					
			        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
			        
			        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK", SysUtility.getStrByLength(REMARK1, 1000),i);
			        
			        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i),i);
				
			        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
				//插入集装箱
				for (int i=0;i<datas.GetTableRows("C_ENTER_DECLARE_CONT");i++){
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "UUID", SysUtility.GetUUID(),i);
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_DECLAR_CHECK_ID", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_ENTER_DECLARE_CONT", "DECL_NO", SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 50),i);
				}
				datas.InsertDB(DataAccess, "C_ENTER_DECLARE_CONT", "C_ENTER_DECLARE_CONT", "UUID");
				if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
					//插入任务
					SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
				}
				//修改DATA_SOURCE=1
				String upDATA="update T_DECL_II SET DATA_SOURCE ='1' WHERE INDX=?";
				DataAccess.ExecSQL(upDATA,Iindx);
			
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}  
	}
	//OI入区清单放行
		public static void SaveOI(String OIindx,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				String sqlOI="select * from T_DECL_OI where indx=?";
				JSONObject jsonOI=DataAccess.GetTableJSON("T_DECL_OI", sqlOI, OIindx);
				List listOI=SysUtility.JSONToList("T_DECL_OI", jsonOI);

				String sqlOIGoods="select * from T_DECL_OI_GOODS WHERE P_INDX=?";
				JSONObject jsonOIG=DataAccess.GetTableJSON("T_DECL_OI_GOODS", sqlOIGoods,OIindx);
				List listOIG=SysUtility.JSONToList("T_DECL_OI_GOODS", jsonOIG);

				SourceData.put("C_ENTER_DECLARE", listOI);
				SourceData.put("C_ENTER_DECLARE_ITEMS", listOIG);

				Datas datas = new Datas();
				datas.remove("C_ENTER_DECLARE");
				datas.remove("C_ENTER_DECLARE_ITEMS");
				datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlOIMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlOItemMappingMap);
				if(SysUtility.isEmpty(datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "CCY"))){
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "CCY",datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "CURR_UNIT"));
				}
				if(SysUtility.isEmpty(datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB"))){
					datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRICE_TOTAL_VAL"));
				}
				String DATA_SOURCE=	datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE",MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
				String sql="select ENT_CBEC_NO from t_ent_reg where ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
				String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//1、主表数据
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",   SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",   SysUtility.getStrByLength(ENT_CBEC_NO,30));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  MESSAGE_SOURCE);
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "APPROVALER"),20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "1");
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
	                String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
	                	//2、插入任务表
	                	SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
	    				String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
	    				DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
	                    EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);
				}
					//1、插入数据
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "2");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "O");
					datas.SetTableValue("C_ENTER_DECLARE", "IE_FLAG", "O");
					datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST", "");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);
					
					String CONSIGNEE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
				   
					String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
					
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
					
					String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
					
					String TRADE_MODE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE", SysUtility.getStrByLength(TRADE_MODE_CODE, 4));
					
					String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE",SysUtility.getStrByLength( TRADE_COUNTRY_CODE, 4));
					
					String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME", SysUtility.getStrByLength(CBE_NAME, 100));
					
					String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL", SysUtility.getStrByLength(CONSIGNEE_TEL, 20));
					
					String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
					
					String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
					datas.SetTableValue("C_ENTER_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);

					
					
					datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
					//2、插入商品
					for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);	
						
						String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
						
						String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
						
				        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i),i);
				        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",SysUtility.getStrByLength(REMARK1, 1000),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
				        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						//3、插入任务表
						SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					}
					//修改DATA_SOURCE=1
					String upDATA="update T_DECL_OI SET DATA_SOURCE ='1' WHERE INDX=?";
					DataAccess.ExecSQL(upDATA,OIindx);
				
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		
		
		//OI入区清单放行
		public static void SaveOIByFw(String OIindx,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				String sqlOI="select * from T_DECL_OI where indx=?";
				JSONObject jsonOI=DataAccess.GetTableJSON("T_DECL_OI", sqlOI, OIindx);
				List listOI=SysUtility.JSONToList("T_DECL_OI", jsonOI);

				String sqlOIGoods="select * from T_DECL_OI_GOODS WHERE P_INDX=?";
				JSONObject jsonOIG=DataAccess.GetTableJSON("T_DECL_OI_GOODS", sqlOIGoods,OIindx);
				List listOIG=SysUtility.JSONToList("T_DECL_OI_GOODS", jsonOIG);

				SourceData.put("C_ENTER_DECLARE", listOI);
				SourceData.put("C_ENTER_DECLARE_ITEMS", listOIG);

				Datas datas = new Datas();
				datas.remove("C_ENTER_DECLARE");
				datas.remove("C_ENTER_DECLARE_ITEMS");
				datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlOIMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlOItemMappingMap);
				String DATA_SOURCE=	datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE",MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
				String sql="select ENT_CBEC_NO from t_ent_reg where ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
				String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				String DECL_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "DECL_REG_NO");
				String sqlENT_CODE="select ENT_CBEC_NO from t_ent_reg where    ENT_REG_NO=?";
				Datas datasENTCODE=DataAccess.GetTableDatas("T_ENT_REG", sqlENT_CODE,DECL_REG_NO);
				String ENT_CBEC_NO_ENT_CODE=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//1、主表数据
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",   SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",   SysUtility.getStrByLength(ENT_CBEC_NO,30));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "APPROVALER"),20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_ENTER_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "1");
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
	                String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
	                	//2、插入任务表
	                	SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
	    				String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
	    				DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
	                    EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);
				}else{
					//1、插入数据
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_REG_NO", DECL_REG_NO);
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "2");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "O");
					//datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST", "");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_ENTER_DECLARE", "CREATE_TIME", "");
					datas.SetTableValue("C_ENTER_DECLARE", "SEND_SOURCE_NODE", MESSAGE_DEST);
					String CONSIGNEE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
				   
					String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
					
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
					
					String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
					
					String TRADE_MODE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE", SysUtility.getStrByLength(TRADE_MODE_CODE, 4));
					
					String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE",SysUtility.getStrByLength( TRADE_COUNTRY_CODE, 4));
					
					String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME", SysUtility.getStrByLength(CBE_NAME, 100));
					
					String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL", SysUtility.getStrByLength(CONSIGNEE_TEL, 20));
					
					String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
					
					String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
					datas.SetTableValue("C_ENTER_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);

					
					
					//2、插入商品
					for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);	
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "CREATE_TIME", "",i);
						String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
						
						String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
						
				        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i));

				        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",SysUtility.getStrByLength(REMARK1, 1000),i);
					
				        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
				        if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							if(SysUtility.isEmpty(GOODS_CBEC_NO)){
								return;
							}
							datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
						datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
						//3、插入任务表
						SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
						String sqlois="update T_DECL_OI set MSG_FLAG='1' where indx=?";
						DataAccess.ExecSQL(sqlois, OIindx);
						String upDATA="update T_DECL_OI SET DATA_SOURCE ='1' WHERE INDX=?";
						DataAccess.ExecSQL(upDATA,OIindx);
					}
				}
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_DECL_OI SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, OIindx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	//OI布控
		public static void SaveOIBatch(String OIindx,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				String sqlOI="select * from T_DECL_OI where indx=?";
				JSONObject jsonOI=DataAccess.GetTableJSON("T_DECL_OI", sqlOI, OIindx);
				List listOI=SysUtility.JSONToList("T_DECL_OI", jsonOI);
				SourceData.put("C_ENTER_DECLARE", listOI);
				Datas datas = new Datas();
				datas.remove("C_ENTER_DECLARE");
				datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlOIMappingMap);
				String DATA_SOURCE=	datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";

				String CBE_CODE =datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
				String sql="SELECT * FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,CBE_CODE);
				 String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_ENTER_DECLARE", "CF_PERSON"), 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("C_ENTER_DECLARE", "CF_TYPE_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "1");
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");
	                String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
	                	//3、插入任务表
	                	SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
	    				String upStatus="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
	    				DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
	                    EXS="EXS";
					}
					String CF_TYPE_CODE= datas.GetTableValue("C_ENTER_DECLARE", "CF_TYPE_CODE");
					String MESSAGE_TYPE=MESSAGE_TYPE="4";
					String MESSAGE_DESC=MESSAGE_DESC="待查验";
					SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);
				}
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	//OI查验结果登记确认放行
		public static void SaveOICF(String OIindx,IDataAccess DataAccess)throws Exception{

			try {
				HashMap SourceData=new HashMap();
				String sqlOI="select * from T_DECL_OI where indx=?";
				JSONObject jsonOI=DataAccess.GetTableJSON("T_DECL_OI", sqlOI, OIindx);
				List listOI=SysUtility.JSONToList("T_DECL_OI", jsonOI);

				String sqlOIGoods="select * from T_DECL_OI_GOODS WHERE P_INDX=?";
				JSONObject jsonOIG=DataAccess.GetTableJSON("T_DECL_OI_GOODS", sqlOIGoods,OIindx);
				List listOIG=SysUtility.JSONToList("T_DECL_OI_GOODS", jsonOIG); 
	 
				String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='3' AND P_INDX=?";
				JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, OIindx);
				List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);

				SourceData.put("C_ENTER_DECLARE", listOI);
				SourceData.put("C_ENTER_DECLARE_ITEMS", listOIG);
				SourceData.put("C_DECLARE_CHECK_RESULT",listCF );

				Datas datas = new Datas();
				datas.remove("C_ENTER_DECLARE");
				datas.remove("C_ENTER_DECLARE_ITEMS");
				datas.remove("C_DECLARE_CHECK_RESULT");
				datas.MapToDatas("C_ENTER_DECLARE", SourceData,XmlOIMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_ITEMS", SourceData,XmlOItemMappingMap);
				datas.MapToDatas("C_ENTER_DECLARE_CONT", SourceData,XmlContMappingMap);
				datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
				String  MESSAGE_SOURCE= datas.GetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_ENTER_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);				
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				String DATA_SOURCE=datas.GetTableValue("C_ENTER_DECLARE", "DATA_SOURCE");
				String ENT_REG_NO=datas.GetTableValue("C_ENTER_DECLARE", "CBE_CODE");
				String sql="select ENT_CBEC_NO,ENT_CNAME from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
				Datas datass=DataAccess.GetTableDatas("T_ENT_REG", sql,ENT_REG_NO);
				String ENT_CBEC_NO=datass.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				 String ENT_CNAME=datass.GetTableValue("T_ENT_REG", "ENT_CNAME");
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//1、插入查验结果
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "1");
					String CHECKLIST_NO=datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_NAME", ENT_CNAME);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_PLATFORM_CODE", datas.GetTableValue("C_ENTER_DECLARE", "E_BUSINESS_PLATFORM_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_ENTER_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO",SysUtility.getStrByLength(CHECKLIST_NO, 40));
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						//2、插入任务表
						SaveCbecSendTable(DataAccess, "UP_CHECKLIST_RESULT", datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
					    EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					SaveRSP(DataAccess, datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),MESSAGE_TYPE,MESSAGE_DESC, "", "1", "",MESSAGE_SOURCE,MESSAGE_DEST,EXS);
				}
					//1、插入数据
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_SOURCE",MESSAGE_SOURCE);
					datas.SetTableValue("C_ENTER_DECLARE", "EXP_MESSAGE_DEST",MESSAGE_DEST);
					datas.SetTableValue("C_ENTER_DECLARE", "STATE", "6");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "1");
					datas.SetTableValue("C_ENTER_DECLARE", "DECL_TYPE", "I");
					datas.SetTableValue("C_ENTER_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_ENTER_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_ENTER_DECLARE", "MESSAGE_DEST", "");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_CODE", ENT_CBEC_NO);
					datas.SetTableValue("C_ENTER_DECLARE", "ENT_BUSINESS_NO", ENT_CBEC_NO);
					String CONSIGNEE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
				   
					String CONSIGNEE_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_CNAME", SysUtility.getStrByLength(CONSIGNEE_CNAME, 50));
					
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CODE", SysUtility.getStrByLength(CONSIGNEE_CODE, 10));
					
					String CONSIGNOR_CNAME=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CNAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_CODE", SysUtility.getStrByLength(CONSIGNOR_CNAME, 50));
					
					String TRADE_MODE_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_MODE_CODE", SysUtility.getStrByLength(TRADE_MODE_CODE, 4));
					
					String TRADE_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE");
					datas.SetTableValue("C_ENTER_DECLARE", "TRADE_COUNTRY_CODE", SysUtility.getStrByLength(TRADE_COUNTRY_CODE, 4));
					
					String CBE_NAME=datas.GetTableValue("C_ENTER_DECLARE", "CBE_NAME");
					datas.SetTableValue("C_ENTER_DECLARE", "CBE_NAME",SysUtility.getStrByLength(CBE_NAME, 100));
					
					String CONSIGNEE_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNEE_TEL",SysUtility.getStrByLength( CONSIGNEE_TEL, 20));
					
					String CONSIGNOR_TEL=datas.GetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL");
					datas.SetTableValue("C_ENTER_DECLARE", "CONSIGNOR_TEL", SysUtility.getStrByLength(CONSIGNOR_TEL, 20));
					
					String REMARK=datas.GetTableValue("C_ENTER_DECLARE", "REMARK");
					datas.SetTableValue("C_ENTER_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					
					
					datas.InsertDB(DataAccess, "C_ENTER_DECLARE", "C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID");
					//2、插入商品
					for(int i=0;i< datas.GetTableRows("C_ENTER_DECLARE_ITEMS"); i++){
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_DECLAR_CHECK_ID",datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "DECL_NO",datas.GetTableValue("C_ENTER_DECLARE", "DECL_NO"),i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_NO", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "SEQ_NO",i),i);	
						
						String HSCODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "HS_CODE", SysUtility.getStrByLength(HSCODE, 12),i);
						
						String ORIGIN_COUNTRY_CODE=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE",i);
						datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "ORIGIN_COUNTRY_CODE", SysUtility.getStrByLength(ORIGIN_COUNTRY_CODE, 4),i);
						
				        String PRODUCT_RECORD_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", SysUtility.getStrByLength(PRODUCT_RECORD_NO, 50),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_VALUES", datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "VALUES_RMB",i),i);
				        String REMARK1=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "REMARK",SysUtility.getStrByLength(REMARK1, 1000),i);
				        datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
				        String GOODS_REG_NO=datas.GetTableValue("C_ENTER_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								datas.SetTableValue("C_ENTER_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					datas.InsertDB(DataAccess, "C_ENTER_DECLARE_ITEMS", "C_ENTER_DECLARE_ITEMS", "UUID");
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						//3、插入任务表
						SaveCbecSendTable(DataAccess, "MM_ENTER_DECL_DECLAR_CHECK", datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_ENTER_DECLARE set PROCESS_STATUS='1' where DECL_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_ENTER_DECLARE", "DECL_DECLAR_CHECK_ID"));
					}
				
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}  
		}
	//IO出区清单放行
	public static void SaveIO(String IOindx,IDataAccess DataAccess)throws Exception{
		try {
			//申报单信息
			HashMap SourceData=new HashMap();
			String sqlIO="SELECT O.ORDER_GUID_NO,T.*,T.TRADE_COUNTRY_CODE AS FROM_COUNTRY_CODE11 FROM T_DECL_IO T, T_ORDER O WHERE T.ORDER_NO=O.ORDER_NO AND T.CBE_CODE=O.CBE_CODE AND T.INDX=?";
			JSONObject jsonIO=DataAccess.GetTableJSON("T_DECL_IO", sqlIO, IOindx);
			List listIO=SysUtility.JSONToList("T_DECL_IO", jsonIO);
			//申报单商品
			String sqlIOG="SELECT * FROM T_DECL_IO_GOODS WHERE P_INDX=?";
			JSONObject jsonIOG=DataAccess.GetTableJSON("T_DECL_IO_GOODS", sqlIOG, IOindx);
			List listIOG=SysUtility.JSONToList("T_DECL_IO_GOODS", jsonIOG);

			SourceData.put("C_EXIT_DECLARE", listIO);//申报单
			SourceData.put("C_EXIT_DECLARE_ITEMS", listIOG);//申报单商品
			
			
			

			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.remove("C_EXIT_DECLARE_ITEMS");
		   
			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlIOMappingMap);
			datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlIOItemMapping);
			
			String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "I");
			datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "1");
			datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
			datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
			datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "2");
			datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
			String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
			/*String FROM_COUNTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "TRADE_COUNTRY_CODE");
			datas.SetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE", FROM_COUNTRY_CODE);
			String CURRENCY_NAME=datas.GetTableValue("C_EXIT_DECLARE", "CURR_UNIT_NAME");
			datas.SetTableValue("C_EXIT_DECLARE", "CURRENCY_NAME", CURRENCY_NAME);*/
			String sql="SELECT DECL_DATE,ENT_CBEC_NO from T_ENT_REG WHERE ENT_REG_NO=?";
			if(SysUtility.isNotEmpty(ECP_CODE)){
				Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
				String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
				String ENT_CBEC_NO_ECP=datass1.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
			}
			String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			
			String  MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000"; 
           //CBE
			String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
			Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
			String ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			//LO
			String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
			String FROM_COUNTRY_CODE11=datas.GetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE11");
			datas.SetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE", FROM_COUNTRY_CODE11);
			if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
				String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
				Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
				String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				//datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", ENT_CBEC_NO_LO);
			}
			//ECP
			String sqleECP="select ENT_CBEC_NO from T_ENT_REG WHERE  ENT_REG_NO=?";
			if(SysUtility.isNotEmpty(ECP_CODE)){
				Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqleECP,ECP_CODE);
				String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
			}
			
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 40));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
				
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",MESSAGE_SOURCE);
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20) );
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"), 20));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT", "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS", "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO", datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NO"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG", "2");
				
				datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
                String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//任务
					SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					EXS="EXS";
				}
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
				SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
			}
				String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
				String sqlAREA="select * from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? or ENT_CBEC_NO=?";
				if(SysUtility.isNotEmpty(AREA_CODE)){
					
					Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
					String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
				}
				
				//插入主表数据
				
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				
				
				String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
				
				String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
				
				String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
				
				String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

				String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
				datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
				
				String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
				datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
				
				String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
				datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));
				
				String PURPOS_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE", SysUtility.getStrByLength(PURPOS_CTRY_CODE, 3));

				String DESP_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE", SysUtility.getStrByLength(DESP_CTRY_CODE, 3));

				String TRANS_TYPE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 2));
				
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
				datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
				
				String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
				datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
                if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
                	datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
                }
			
				//插入商品
                String GOODS_DECLAR_CHECK_ID=datas.GetTableValue("C_EXIT_DECLARE","GOODS_DECLAR_CHECK_ID");
				for (int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
					                     
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", GOODS_DECLAR_CHECK_ID,i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
					String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);
					
					String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
				
					String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
				  
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//出区任务
					SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					
				}
			 
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	//IO出区清单放行
		public static void SaveIOByFw(String IOindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				
				HashMap SourceData=new HashMap();
				
				String sqlIO=" SELECT T.*,T.TRADE_COUNTRY_CODE AS FROM_COUNTRY_CODE11, (select O.ORDER_GUID_NO from  T_ORDER O where T.ORDER_NO=O.ORDER_NO AND T.CBE_CODE=O.CBE_CODE) ORDER_GUID_NO FROM T_DECL_IO T WHERE T.INDX=?";
				JSONObject jsonIO=DataAccess.GetTableJSON("T_DECL_IO", sqlIO, IOindx);
				List listIO=SysUtility.JSONToList("T_DECL_IO", jsonIO);
				//申报单商品
				String sqlIOG="SELECT * FROM T_DECL_IO_GOODS WHERE P_INDX=?";
				JSONObject jsonIOG=DataAccess.GetTableJSON("T_DECL_IO_GOODS", sqlIOG, IOindx);
				List listIOG=SysUtility.JSONToList("T_DECL_IO_GOODS", jsonIOG);

				
				SourceData.put("C_EXIT_DECLARE", listIO);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listIOG);//申报单商品
				
				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");
			   
				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlIOMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlIOItemMapping);

				String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "I");
				datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "1");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "1");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
				datas.SetTableValue("C_EXIT_DECLARE", "CREATE_TIME", "");
				String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
				
				String sql="SELECT DECL_DATE,ENT_CBEC_NO from T_ENT_REG WHERE ENT_REG_NO=?";
				if(SysUtility.isNotEmpty(ECP_CODE)){
					Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
					String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					String ENT_CBEC_NO_ECP=datass1.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				}
				String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				
				String  MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000"; 
	           //CBE
				String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE  ENT_REG_NO=?";
				Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
				String ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				//LO
				String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
				String FROM_COUNTRY_CODE11=datas.GetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE11");
				datas.SetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE", FROM_COUNTRY_CODE11);
				if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
					String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
					Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
					String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
					//datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", ENT_CBEC_NO_LO);
				}
				//ECP
				String sqleECP="select ENT_CBEC_NO from T_ENT_REG WHERE  ENT_REG_NO=?";
				if(SysUtility.isNotEmpty(ECP_CODE)){
					Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqleECP,ECP_CODE);
					String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				}
				
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
					
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE", datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20) );
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"), 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT", "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS", "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO", datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG", "2");
					
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
	                String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//任务
						SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}else{
					String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
					String sqlAREA="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? OR ENT_CBEC_NO=? ";
					if(SysUtility.isNotEmpty(AREA_CODE)){
						Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
						String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
						datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
					}
					//插入主表数据
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					
					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
					
					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
					
					String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
					
					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
					
					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
					
					String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
					datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));
					
					String PURPOS_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE", SysUtility.getStrByLength(PURPOS_CTRY_CODE, 3));

					String DESP_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE", SysUtility.getStrByLength(DESP_CTRY_CODE, 3));

					String TRANS_TYPE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 2));
					
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
					//datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
					
					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					datas.SetTableValue("C_EXIT_DECLARE", "SEND_SOURCE_NODE", MESSAGE_DEST);
				
					//插入商品
	                String GOODS_DECLAR_CHECK_ID=datas.GetTableValue("C_EXIT_DECLARE","GOODS_DECLAR_CHECK_ID");
					for (int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						                     
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", GOODS_DECLAR_CHECK_ID,i);
						
						String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);
						
						String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
					
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								if(SysUtility.isEmpty(GOODS_CBEC_NO)){
									String sqlu="UPDATE T_DECL_IO SET MSG_FLAG='3' WHERE INDX=?";
									DataAccess.ExecSQL(sqlu, IOindx);
									return;
								}
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
				
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
							datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
						}
						datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
						//出区任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						String sqlIOS="UPDATE T_DECL_IO SET MSG_FLAG='1' WHERE INDX=?";
						DataAccess.ExecSQL(sqlIOS, IOindx);
					}
				 }
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_DECL_IO SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, IOindx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	//IO查验结果登记-确认放行时：
		public static void SvaeIOCF(String IOindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				HashMap SourceData=new HashMap();
				String sqlIO="SELECT O.ORDER_GUID_NO,T.* FROM T_DECL_IO T, T_ORDER O WHERE T.ORDER_NO=O.ORDER_NO AND T.CBE_CODE=O.CBE_CODE AND T.INDX=?";
				JSONObject jsonIO=DataAccess.GetTableJSON("T_DECL_IO", sqlIO, IOindx);
				List listIO=SysUtility.JSONToList("T_DECL_IO", jsonIO);
				//申报单商品
				String sqlIOG="SELECT * FROM T_DECL_IO_GOODS WHERE P_INDX=?";
				JSONObject jsonIOG=DataAccess.GetTableJSON("T_DECL_IO_GOODS", sqlIOG, IOindx);
				List listIOG=SysUtility.JSONToList("T_DECL_IO_GOODS", jsonIOG);

			
				SourceData.put("C_EXIT_DECLARE", listIO);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listIOG);//申报单商品
				//SourceData.put("C_ORDER_DECLARE", listOrder);//订单

				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");
			
				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlIOMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlIOItemMapping);
			
				String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "I");
				datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "1");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "2");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
				
				String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
				if(SysUtility.isNotEmpty(ECP_CODE)){
					String sql="SELECT DECL_DATE,ENT_CBEC_NO  from t_ent_reg where ENT_REG_NO=?";
					Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
					String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					String ENT_CBEC_NO_ECP=datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				}
				
				String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
	           //CBE
				String ENT_CBEC_NO_CBE="";
				String CBE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				if(SysUtility.isNotEmpty(CBE_CODE)){
					String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
					Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,CBE_CODE);
					ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				}
				
				//LO
				String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
					String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
					Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
					String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				}
				
				
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//查验表
					String ORG_CODE=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='2' AND P_INDX=?";
					JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, IOindx);
					List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);
					SourceData.put("C_DECLARE_CHECK_RESULT",listCF );
					datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "ORG_CODE", ORG_CODE);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "2");
					
					String CHECKLIST_RESULT_ID=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID", SysUtility.getStrByLength(CHECKLIST_RESULT_ID, 32));
					
					String CHECKLIST_NO=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));

					String PERSON_CODE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "PERSON_CODE");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "PERSON_CODE", SysUtility.getStrByLength(PERSON_CODE, 20));

					String SPOT_DESC=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC", SysUtility.getStrByLength(SPOT_DESC, 500));

					String REMARK=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "REMARK");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					
					String RECORD_PLACE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE", SysUtility.getStrByLength(RECORD_PLACE,50));


					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//任务
						SaveCbecSendTable(DataAccess, "UP_CHECKLIST_RESULT",  datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
						EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}
					String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
					if(SysUtility.isNotEmpty(AREA_CODE)){
						String sqlAREA="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? OR ENT_CBEC_NO=?";
						Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
						String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
						datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
					}
					
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					
					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
						
					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
						
					String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
						
					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
						
					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
						
					String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
					datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));
						
					String PURPOS_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "PURPOS_CTRY_CODE", SysUtility.getStrByLength(PURPOS_CTRY_CODE, 3));

					String DESP_CTRY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "DESP_CTRY_CODE", SysUtility.getStrByLength(DESP_CTRY_CODE, 3));

					String TRANS_TYPE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 2));
					
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
					
					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
						datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
					}
					//插入商品
					for (int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
					
					//是否插入任务
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//插入出区上报任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					}
					//查验表
					String ORG_CODE1=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='2' AND P_INDX=?";
					JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, IOindx);
					List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);
					SourceData.put("C_DECLARE_CHECK_RESULT",listCF );
					datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
					
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "ORG_CODE", ORG_CODE1);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "2");
					
					String CHECKLIST_RESULT_ID=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID",SysUtility.getStrByLength(CHECKLIST_RESULT_ID, 32));
					
					String CHECKLIST_NO1=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO",SysUtility.getStrByLength(CHECKLIST_NO1, 40));

					String PERSON_CODE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "PERSON_CODE",SysUtility.getStrByLength(PERSON_CODE, 20));

					String SPOT_DESC=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC",SysUtility.getStrByLength(SPOT_DESC, 500));

					String REMARKC=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "REMARK");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "REMARK",SysUtility.getStrByLength(REMARKC, 1000));

					String RECORD_PLACE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE",SysUtility.getStrByLength(RECORD_PLACE, 50));

					
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					
					//任务
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						SaveCbecSendTable(DataAccess, "UP_CHECKLIST_RESULT", datas.GetTableValue("C_DECLARE_CHECK_RESULT", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatusCF="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
						DataAccess.ExecSQL(upStatusCF, datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
					}
				
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		//IO布控
		public static void SaveIOBatch(String IOindx,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				String sqlIO="SELECT * from T_DECL_IO where indx=?";
				JSONObject jsonIO=DataAccess.GetTableJSON("T_DECL_IO", sqlIO, IOindx);
				List listIO=SysUtility.JSONToList("T_DECL_IO", jsonIO);
				SourceData.put("T_DECL_IO", listIO);
				Datas datas = new Datas();
				datas.remove("T_DECL_IO");
				datas.MapToDatas("T_DECL_IO", SourceData,XmlIOMappingMap);
				String DATA_SOURCE=datas.GetTableValue("T_DECL_IO", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("T_DECL_IO", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE=datas.GetTableValue("T_DECL_IO", "MESSAGE_DEST");
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				String CBE_CODE =datas.GetTableValue("T_DECL_IO", "E_BUSINESS_COMPANY_CODE");
				
				Datas datasO = DataAccess.GetTableDatas("T_DECL_IO", sqlIO,IOindx);
				String ORDER_NO=datasO.GetTableValue("T_DECL_IO", "ORDER_NO");
				
				//订单信息
					String sqlOrder="SELECT * FROM T_ORDER WHERE ORDER_NO='"+ORDER_NO+"' AND CBE_CODE=?";
					JSONObject jsonORDER=DataAccess.GetTableJSON("T_ORDER", sqlOrder, CBE_CODE);
					List listOrder=SysUtility.JSONToList("T_ORDER", jsonORDER);
					Datas datasOG = DataAccess.GetTableDatas("T_ORDER", sqlOrder,CBE_CODE);
					String OGindx=datasOG.GetTableValue("T_ORDER", "INDX");
					String ECP_CODE=datasOG.GetTableValue("T_ORDER", "ECP_CODE");//电商平台备案编码
					
					String sqlecp="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE   ENT_REG_NO=?";
					Datas datasEcp=DataAccess.GetTableDatas("T_ENT_REG", sqlecp,ECP_CODE);
					 String ENT_CBEC_NO_CBE=datasEcp.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
	             
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_IO", "GOODS_DECLAR_CHECK_ID"), 50));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", datas.GetTableValue("T_DECL_IO", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("T_DECL_IO", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("T_DECL_IO", "E_BUSINESS_COMPANY_NAME"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("T_DECL_IO", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("T_DECL_IO", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_IO", "CF_PERSON"), 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("T_DECL_IO", "CF_TYPE_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "2");
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", datas.GetTableValue("T_DECL_IO", "ORDER_NUMBER"));
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//3、插入任务表
						SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD", datas.GetTableValue("T_DECL_IO", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatusCF="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatusCF,datas.GetTableValue("T_DECL_IO", "GOODS_DECLAR_CHECK_ID"));
						EXS="EXS";
					}
					String CF_TYPE_CODE= datas.GetTableValue("T_DECL_IO", "CF_TYPE_CODE");
					String MESSAGE_TYPE="4";
					String MESSAGE_DESC="待查验";
					
					String ORDER_GUID_NO=datasO.GetTableValue("T_ORDER", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO,"2",ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		//OO出区清单放行
		public static void SaveOO(String OOindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				HashMap SourceData=new HashMap();
				String sqlOO="SELECT O.ORDER_GUID_NO,O.ECP_CODE,T.*,T.PURPOS_CTRY_CODE AS FROM_COUNTRY_CODE11 FROM T_DECL_OO T, T_ORDER O WHERE T.ORDER_NO=O.ORDER_NO AND T.CBE_CODE=O.CBE_CODE AND T.INDX=?";
				JSONObject jsonOO=DataAccess.GetTableJSON("T_DECL_OO", sqlOO, OOindx);
				List listOO=SysUtility.JSONToList("T_DECL_OO", jsonOO);
				//申报单商品
				String sqlOOG="SELECT * FROM T_DECL_OO_GOODS WHERE P_INDX=?";
				JSONObject jsonOOG=DataAccess.GetTableJSON("T_DECL_OO_GOODS", sqlOOG, OOindx);
				List listOOG=SysUtility.JSONToList("T_DECL_OO_GOODS", jsonOOG);

				SourceData.put("C_EXIT_DECLARE", listOO);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listOOG);//申报单商品
				
				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");
			

				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlOOMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlOOItemMapping);
				String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST"));
				String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				//CBE
				String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				String ENT_CBEC_NO_CBE="";
				if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
					String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
					Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
					 ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				}
				
				//LO
				
				String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
					String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
					Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
					String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				//	datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", ENT_CBEC_NO_LO);
				}
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){                 
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
					/*datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);*/
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE", MESSAGE_SOURCE);
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"), 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT", "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS", "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO", datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG", "2");
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//任务
						SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));  
						EXS="EXS";
					}
					
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					String sql="SELECT DECL_DATE from T_ENT_REG WHERE ENT_REG_NO=?";
					String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
					Datas datass1=new Datas();
					if(SysUtility.isNotEmpty(ECP_CODE)){
					datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
					}
					
					String	E_BUSINESS_COMPANY_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", E_BUSINESS_COMPANY_DATE);
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "O");
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
					datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
					datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "1");
					datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_EXIT_DECLARE", "IMP_DATE", SysUtility.getSysDate());
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					/*datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);*/
					
					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));

					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));

					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));

					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));

					String INSP_ORG_CODE=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", SysUtility.getStrByLength(INSP_ORG_CODE, 20));

					String ID_TYPE=datas.GetTableValue("C_EXIT_DECLARE", "ID_TYPE");
					datas.SetTableValue("C_EXIT_DECLARE", "ID_TYPE", SysUtility.getStrByLength(ID_TYPE, 1));

					String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
					datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));
					
					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));

					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
					String FROM_COUNTRY_CODE11=datas.GetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE11");
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_COUNTRY_CODE", FROM_COUNTRY_CODE11);
					//插入主表数据
					if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
						datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
					}
				
					for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
				
					datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
					
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//插入主表任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						  //三单任务
						/*SaveCbecSendTable(DataAccess, "I_MM_ORDER_IMPORT_CHECK", datas.GetTableValue("C_ORDER_DECLARE", "ORDER_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					    String upStatusITEM="update C_ITEM_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
					    DataAccess.ExecSQL(upStatusITEM,datas.GetTableValue("C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID"));*/
					}
				
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		
		//OO出区清单放行
		public static void SaveOOByFw(String OOindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				HashMap SourceData=new HashMap();
				String sqlOO="SELECT O.ORDER_GUID_NO,O.ECP_CODE,T.* FROM T_DECL_OO T, T_ORDER O WHERE T.ORDER_NO=O.ORDER_NO AND T.CBE_CODE=O.CBE_CODE AND T.INDX=?";
				JSONObject jsonOO=DataAccess.GetTableJSON("T_DECL_OO", sqlOO, OOindx);
				List listOO=SysUtility.JSONToList("T_DECL_OO", jsonOO);
				//申报单商品
				String sqlOOG="SELECT * FROM T_DECL_OO_GOODS WHERE P_INDX=?";
				JSONObject jsonOOG=DataAccess.GetTableJSON("T_DECL_OO_GOODS", sqlOOG, OOindx);
				List listOOG=SysUtility.JSONToList("T_DECL_OO_GOODS", jsonOOG);
				SourceData.put("C_EXIT_DECLARE", listOO);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listOOG);//申报单商品
				
				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");

				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlOOMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlOOItemMapping);
			
				String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				//CBE
				String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				String ENT_CBEC_NO_CBE="";
				if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
					String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
					Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
					 ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				}
				
				//LO
				String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
					String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
					Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
					String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				//	datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", ENT_CBEC_NO_LO);
				}
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){                 
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
					/*datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);*/
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"), 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT", "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME", datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS", "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO", datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG", "2");
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//任务
						SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus, datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));  
						EXS="EXS";
					}
					
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}else{
					String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
					String sql="SELECT DECL_DATE from T_ENT_REG WHERE ENT_REG_NO=?";
					String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
					Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
					String	E_BUSINESS_COMPANY_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", E_BUSINESS_COMPANY_DATE);
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "O");
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
					datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
					datas.SetTableValue("C_EXIT_DECLARE", "CREATE_TIME", "");
					datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "1");
					datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					/*datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);*/
					
					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));

					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));

					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));

					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));

					String INSP_ORG_CODE=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", SysUtility.getStrByLength(INSP_ORG_CODE, 20));

					String ID_TYPE=datas.GetTableValue("C_EXIT_DECLARE", "ID_TYPE");
					datas.SetTableValue("C_EXIT_DECLARE", "ID_TYPE", SysUtility.getStrByLength(ID_TYPE, 1));

					String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
					datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));
					
					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));

					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
					//datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_EXIT_DECLARE", "SEND_SOURCE_NODE", MESSAGE_DEST);
				
					for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								if(SysUtility.isEmpty(GOODS_CBEC_NO)){
									String sqlu="UPDATE T_DECL_OO SET MSG_FLAG='3' WHERE INDX=?";
									DataAccess.ExecSQL(sqlu, OOindx);
									return;
								}
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//插入主表数据
						if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
							datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
						}
						datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
						//插入主表任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						String	sqlOOS = "update T_DECL_OO set MSG_FLAG='1' where indx=?";
						DataAccess.ExecSQL(sqlOOS, OOindx);
					}
				}
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_DECL_OO SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, OOindx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	//OO查验结果登记确认放行
		public static void SvaeOOCF(String OOindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				HashMap SourceData=new HashMap();
				String sqlOO="select * from T_DECL_OO where indx=?";
				JSONObject jsonOO=DataAccess.GetTableJSON("T_DECL_OO", sqlOO, OOindx);
				List listOO=SysUtility.JSONToList("T_DECL_OO", jsonOO);
				//申报单商品
				String sqlOOG="SELECT * FROM T_DECL_OO_GOODS WHERE P_INDX=?";
				JSONObject jsonOOG=DataAccess.GetTableJSON("T_DECL_OO_GOODS", sqlOOG, OOindx);
				List listOOG=SysUtility.JSONToList("T_DECL_OO_GOODS", jsonOOG);

		

				SourceData.put("C_EXIT_DECLARE", listOO);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listOOG);//申报单商品
				//SourceData.put("C_ORDER_DECLARE", listOrder);//订单
				
				
			

				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");
				
				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlOOMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlOOItemMapping);
				

	           String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "ECP_CODE");
	           String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
				String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "O");
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "2");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
	           if(SysUtility.isNotEmpty(ECP_CODE)){
	        	   String sql="select * from t_ent_reg where ENT_REG_NO=?";
	   			Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
	   			String	decl_date = datass1.GetTableValue("T_ENT_REG", "decl_date");
	   			datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", decl_date);
	           }
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
				String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"); 
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				
				//CBE
				
				String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				String ENT_CBEC_NO_CBE="";
				if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
					String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
					Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
				    ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				}
				
				//LO
				String ENT_REG_NO_LO=datas.GetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_LO)){
					String sqlLO="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST='Y' AND ENT_REG_NO=?";
					Datas datasLO=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,ENT_REG_NO_LO);
					String ENT_CBEC_NO_LO=datasLO.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
					datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", ENT_CBEC_NO_LO);
				}
				
				//ECP
				String ENT_REG_NO_ECP=datas.GetTableValue("C_ORDER_DECLARE", "COMPANY_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_ECP)){
					String sqlE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_PL ='Y' AND ENT_REG_NO=?";
					Datas datassE=DataAccess.GetTableDatas("T_ENT_REG", sqlE,ENT_REG_NO_ECP);
					String ENT_CBEC_NO_ECP =datassE.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", ENT_CBEC_NO_ECP);
				}
				
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					//查验表
					String ORG_CODE=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='4' AND P_INDX=?";
					JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, OOindx);
					List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);
					SourceData.put("C_DECLARE_CHECK_RESULT",listCF );
					datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "ORG_CODE", ORG_CODE);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "2");
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//任务
						SaveCbecSendTable(DataAccess, "UP_CHECKLIST_RESULT", datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatusCF="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
						DataAccess.ExecSQL(upStatusCF,datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
						EXS="EXS";
					}
					String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}
					//插入主表数据
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
					

					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));

					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));

					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));

					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));

					String INSP_ORG_CODE=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", SysUtility.getStrByLength(INSP_ORG_CODE, 20));

					String ID_TYPE=datas.GetTableValue("C_EXIT_DECLARE", "ID_TYPE");
					datas.SetTableValue("C_EXIT_DECLARE", "ID_TYPE", SysUtility.getStrByLength(ID_TYPE, 1));

					String CONSIGNEE_ADDR=datas.GetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR");
					datas.SetTableValue("C_EXIT_DECLARE", "CONSIGNEE_ADDR", SysUtility.getStrByLength(CONSIGNEE_ADDR, 400));

					String TRANS_TYPE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "TRANS_TYPE_CODE", SysUtility.getStrByLength(TRANS_TYPE_CODE, 2));

					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
	                if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
	                	datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
	                }
					
	                datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "2");
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
	                
					//插入商品
					for (int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
						String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);

						String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
					    
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					
				    	datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
				    
					
					
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						//插入出区任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					}
					//查验表
					String ORG_CODE1=datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
					String sqlCf="SELECT * FROM T_CF_REG WHERE BIZ_TYPE='2' AND P_INDX=?";
					JSONObject jsonCF=DataAccess.GetTableJSON("T_CF_REG", sqlCf, OOindx);
					List listCF=SysUtility.JSONToList("T_CF_REG", jsonCF);
					SourceData.put("C_DECLARE_CHECK_RESULT",listCF );
					datas.MapToDatas("C_DECLARE_CHECK_RESULT", SourceData,XmlCFMappingMap);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "ORG_CODE", ORG_CODE1);
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_AUDIT_STATUS", "1");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "INVENTORY_FLAG", "2");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);

					String CHECKLIST_RESULT_ID=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID",SysUtility.getStrByLength(CHECKLIST_RESULT_ID, 32));

					String CHECKLIST_NO1=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO",SysUtility.getStrByLength(CHECKLIST_NO1, 40));

					String PERSON_CODE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_NO");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "PERSON_CODE",SysUtility.getStrByLength(PERSON_CODE, 20));

					String SPOT_DESC=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "SPOT_DESC",SysUtility.getStrByLength(SPOT_DESC, 500));

					String REMARKC=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "REMARK");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "REMARK",SysUtility.getStrByLength(REMARKC, 1000));

					String RECORD_PLACE=datas.GetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE");
					datas.SetTableValue("C_DECLARE_CHECK_RESULT", "RECORD_PLACE",SysUtility.getStrByLength(RECORD_PLACE, 50));
					
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RESULT", "C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID");
					//任务
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						SaveCbecSendTable(DataAccess, "UP_CHECKLIST_RESULT", datas.GetTableValue("C_DECLARE_CHECK_RESULT", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatusCF="update C_DECLARE_CHECK_RESULT set PROCESS_STATUS='1' where CHECKLIST_RESULT_ID=?";
						DataAccess.ExecSQL(upStatusCF,datas.GetTableValue("C_DECLARE_CHECK_RESULT", "CHECKLIST_RESULT_ID"));
					}
					
				
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		//OO布控  
		public static void SaveOOBatch(String OOindx,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				String sqlOO="select * from T_DECL_OO where indx=?";
				JSONObject jsonOO=DataAccess.GetTableJSON("T_DECL_OO", sqlOO, OOindx);
				List listOO=SysUtility.JSONToList("T_DECL_OO", jsonOO);
				SourceData.put("T_DECL_OO", listOO);

				Datas datas = new Datas();
				datas.remove("T_DECL_OO");
				datas.MapToDatas("T_DECL_OO", SourceData,XmlOOMappingMap);
				String DATA_SOURCE=	datas.GetTableValue("T_DECL_OO", "DATA_SOURCE");
				String  MESSAGE_SOURCE= datas.GetTableValue("T_DECL_OO", "INSP_ORG_CODE"); 
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("T_DECL_OO", "MESSAGE_DEST"); 
				}
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000"; 


				String CBE_CODE1 =datas.GetTableValue("T_DECL_OO", "E_BUSINESS_COMPANY_CODE");
				String sql="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
				Datas datassa=DataAccess.GetTableDatas("T_ENT_REG", sql,CBE_CODE1);
				 String ENT_CBEC_NO=datassa.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", datas.GetTableValue("T_DECL_OO", "GOODS_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_OO", "DECL_NO"), 40));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("T_DECL_OO", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(ENT_CBEC_NO, 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("T_DECL_OO", "E_BUSINESS_COMPANY_NAME"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("T_DECL_OO", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("T_DECL_OO", "CF_PERSON"), 20));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("T_DECL_OO", "CF_TYPE_CODE"));
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "2");
					datas.SetTableValue("C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", datas.GetTableValue("T_DECL_OO", "ORDER_NUMBER"));
					datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");
	                 String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO)){
						//3、插入任务表
						SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD", datas.GetTableValue("T_DECL_OO", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("T_DECL_OO", "GOODS_DECLAR_CHECK_ID"));
						EXS="EXS";
					}
					String CF_TYPE_CODE= datas.GetTableValue("T_DECL_OO", "CF_TYPE_CODE");
					String MESSAGE_TYPE="4";
					String MESSAGE_DESC="待查验";
					String ORDER_NO=datas.GetTableValue("T_DECL_OO", "ORDER_NUMBER");
					String CBE_CODE=datas.GetTableValue("T_DECL_OO", "E_BUSINESS_COMPANY_CODE");
					String sqlO="select ORDER_GUID_NO from T_ORDER where ORDER_NO='"+ORDER_NO+"' AND CBE_CODE=?";
					Datas datass = DataAccess.GetTableDatas("T_ORDER", sqlO,CBE_CODE);
					String ORDER_GUID_NO=datass.GetTableValue("T_ORDER", "ORDER_GUID_NO");
					SaveRSP(DataAccess,  datass.GetTableValue("T_ORDER", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO,"2",ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);			
				}
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	
	//FA调回	
	public static void SaveFaCheckBack(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			String sqlFA="select * from T_DECL_FA where indx=?";
			JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
			List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
			SourceData.put("C_EXIT_DECLARE", listFA);//申报单
			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
			String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
			String CBE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			
			String sql ="SELECT * FROM T_ORDER WHERE ORDER_NO='"+ORDER_NO+"' and CBE_CODE=?";
			Datas datasor = DataAccess.GetTableDatas("T_ORDER", sql,CBE_CODE);
			String ORDER_GUID_NO=datasor.GetTableValue("T_ORDER", "ORDER_GUID_NO");
			String ECP_CODE=datasor.GetTableValue("T_ORDER", "ECP_CODE");
			
			String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			
			String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
			Datas datassa=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,CBE_CODE);
			String ENT_CBEC_NO_CBE=datassa.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			 
	        String sqlECP="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_PL='Y' AND  ENT_REG_NO=?";
	        Datas dataECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
	        String ENT_CBEC_NO_ECP=dataECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), 40));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE",SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CF_PERSON"), 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("C_EXIT_DECLARE", "CF_TYPE_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "ORDER_NO",ORDER_NO);
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "2");
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE",ENT_CBEC_NO_CBE);
				/*datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");*/
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//2、插入任务表
					SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					EXS="EXS";
				}
				String MESSAGE_TYPE="11";
				String MESSAGE_DESC="已调回";
				SaveRSP(DataAccess, datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	//FA人工初审/复审/下线批次管理点击退单
	public static void SavereFaBack(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			//申报单信息
			HashMap SourceData=new HashMap();
			String sqlFA="select * from T_DECL_FA where indx=?";
			JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
			List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
			//申报单商品
			String sqlFAG="SELECT * FROM T_DECL_FA_GOODS WHERE P_INDX=?";
			JSONObject jsonFAG=DataAccess.GetTableJSON("T_DECL_FA_GOODS", sqlFAG, Faindx);
			List listFAG=SysUtility.JSONToList("T_DECL_FA_GOODS", jsonFAG);

		
			SourceData.put("C_EXIT_DECLARE", listFA);//申报单
			SourceData.put("C_EXIT_DECLARE_ITEMS", listFAG);//申报单商品
		
			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.remove("C_EXIT_DECLARE_ITEMS");
		

			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
			datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlFAItemMapping);
		
			//String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
			String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
			String CBE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			String sqll ="SELECT * FROM T_ORDER WHERE ORDER_NO='"+ORDER_NO+"' and CBE_CODE=?";
			Datas datasor = DataAccess.GetTableDatas("T_ORDER", sqll,CBE_CODE);
			String ORDER_GUID_NO=datasor.GetTableValue("T_ORDER", "ORDER_GUID_NO");
			
			
			String DATA_SOURCE=	datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			String PAYMENT_NO=datas.GetTableValue("C_EXIT_DECLARE", "PAYMENT_NO");
			String  MESSAGE_SOURCE= SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"), 2)+"0000";
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			datas.SetTableValue("C_EXIT_DECLARE","MESSAGE_SOURCE","");
			//CBE
			String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			String ENT_CBEC_NO_CBE="";
			if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
				String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
				Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
			    ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			}
			
			
			 //LO
			String ENT_REG_NO_LOI=datas.GetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE");
			if(SysUtility.isNotEmpty(ENT_REG_NO_LOI)){
				String sqlLOI="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=?";
				Datas datassLOI=DataAccess.GetTableDatas("T_ENT_REG", sqlLOI,ENT_REG_NO_LOI);
				String ENT_CBEC_NO_LO=datassLOI.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				
			}
			String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
			if(SysUtility.isNotEmpty(ECP_CODE)){
				String sqlECP="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_PL ='Y' AND ENT_REG_NO=?";
				Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
				String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", ENT_CBEC_NO_ECP);
			}
			
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), 50));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  MESSAGE_SOURCE);
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO",  ORDER_NO);
			/*	datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");*/
				//2、插入任务表
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					/*SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));*/
					EXS="EXS";
				}
				String MESSAGE_TYPE="10";
				String MESSAGE_DESC="已退回";
				
				
				SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
			}else{
				String sql="select DECL_DATE from t_ent_reg where ENT_REG_NO=?";
				Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
				String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
				String IE_FLAG=datas.GetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG");
				if("I".equals(IE_FLAG)){
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "0");
				}else{
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "");
				}
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE",SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "IMP_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "PAYMENT_NO", PAYMENT_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "1");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
			
				String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
				if(SysUtility.isNotEmpty(AREA_CODE)){
					String sqlAREA="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? OR ENT_CBEC_NO=?";
					Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
					String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
				}
				
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
			
				
				String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
				
				String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
				
				String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
				
				String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

				String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
				datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
				
				String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
				datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
				String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
				datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
				
				datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
				if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
					datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
				    
					
				}
				
				
				for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID",datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
					String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);

					String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
				    
					String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID");
			
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//插入快件任务
					SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}

	}
	
	//FA查验管理点击放行//FA检疫结果处理登记放行
	public static void SaveFaPass(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			//申报单信息
			HashMap SourceData=new HashMap();
			String sqlFA="select * from T_DECL_FA where indx=?";
			JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
			List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
			//申报单商品
			String sqlFAG="SELECT * FROM T_DECL_FA_GOODS WHERE P_INDX=?";
			JSONObject jsonFAG=DataAccess.GetTableJSON("T_DECL_FA_GOODS", sqlFAG, Faindx);
			List listFAG=SysUtility.JSONToList("T_DECL_FA_GOODS", jsonFAG);

		
			SourceData.put("C_EXIT_DECLARE", listFA);//申报单
			SourceData.put("C_EXIT_DECLARE_ITEMS", listFAG);//申报单商品
		
			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.remove("C_EXIT_DECLARE_ITEMS");
		

			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
			datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlFAItemMapping);
		
			String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
			String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
			String DATA_SOURCE=	datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			String PAYMENT_NO=datas.GetTableValue("C_EXIT_DECLARE", "PAYMENT_NO");
			if(SysUtility.isEmpty(PAYMENT_NO)){
				String sqlPAY="select PAYMENT_NO from t_payment where order_no=?";
				JSONObject jsonPAY=DataAccess.GetTableJSON("t_payment", sqlPAY,ORDER_NO );
			    JSONArray arrPAY=jsonPAY.getJSONArray("t_payment");
			    if(arrPAY.length()>0){
			    	PAYMENT_NO=arrPAY.getJSONObject(0).getString("PAYMENT_NO");
			   }
			   }
			String  MESSAGE_SOURCE= SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"), 2)+"0000";
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			datas.SetTableValue("C_EXIT_DECLARE","MESSAGE_SOURCE","");
			//CBE
			String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			String ENT_CBEC_NO_CBE="";
			if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
				String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
				Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
			    ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			}
			
			
			 //LO
			String ENT_REG_NO_LOI=datas.GetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE");
			if(SysUtility.isNotEmpty(ENT_REG_NO_LOI)){
				String sqlLOI="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=?";
				Datas datassLOI=DataAccess.GetTableDatas("T_ENT_REG", sqlLOI,ENT_REG_NO_LOI);
				String ENT_CBEC_NO_LO=datassLOI.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				
			}
			String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
			if(SysUtility.isNotEmpty(ECP_CODE)){
				String sqlECP="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_PL ='Y' AND ENT_REG_NO=?";
				Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
				String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", ENT_CBEC_NO_ECP);
			}
			
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 50));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  MESSAGE_SOURCE);
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO",  ORDER_NO);
				/*datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");*/
				//2、插入任务表
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					/*SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));*/
					EXS="EXS";
				}
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				
				
				SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
			}
				String sql="select DECL_DATE from t_ent_reg where ENT_REG_NO=?";
				Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
				String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
				String IE_FLAG=datas.GetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG");
				if("I".equals(IE_FLAG)){
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "0");
				}else{
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "");
				}
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE",SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "IMP_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "PAYMENT_NO", PAYMENT_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "1");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
			
				String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
				if(SysUtility.isNotEmpty(AREA_CODE)){
					String sqlAREA="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? OR ENT_CBEC_NO=?";
					Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
					String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
				}
				
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
			
				
				String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
				
				String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
				
				String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
				
				String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

				String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
				datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
				
				String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
				datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
				String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
				datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
				
				datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
				if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
					datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
				    
					
				}
				
				
				for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID",datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
					String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);

					String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
				    
					String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID");
			
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//插入快件任务
					SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				}
			
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}

	}
	
	//FA查验管理点击放行//FA检疫结果处理登记放行
		public static void SaveFaPassByFw(String Faindx,IDataAccess DataAccess)throws Exception{
			try {
				//申报单信息
				HashMap SourceData=new HashMap();
				String sqlFA="select * from T_DECL_FA where indx=?";
				JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
				List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
				//申报单商品
				String sqlFAG="SELECT * FROM T_DECL_FA_GOODS WHERE P_INDX=?";
				JSONObject jsonFAG=DataAccess.GetTableJSON("T_DECL_FA_GOODS", sqlFAG, Faindx);
				List listFAG=SysUtility.JSONToList("T_DECL_FA_GOODS", jsonFAG);

			
				SourceData.put("C_EXIT_DECLARE", listFA);//申报单
				SourceData.put("C_EXIT_DECLARE_ITEMS", listFAG);//申报单商品
			//	SourceData.put("C_ORDER_DECLARE", listOrder);//订单
			
			

				Datas datas =new Datas();
				datas.remove("C_EXIT_DECLARE");
				datas.remove("C_EXIT_DECLARE_ITEMS");
			
				datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
				datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlFAItemMapping);
			
				String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
				String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				String DATA_SOURCE=	datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
				String PAYMENT_NO=datas.GetTableValue("C_EXIT_DECLARE", "PAYMENT_NO");
				String  MESSAGE_SOURCE= SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"), 2)+"0000";
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
					datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
				}
				String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				datas.SetTableValue("C_EXIT_DECLARE","MESSAGE_SOURCE","");
				//CBE
				String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				String ENT_CBEC_NO_CBE="";
				if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
					String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE  ENT_REG_NO=?";
					Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
				    ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				}
				
				
				 //LO
				String ENT_REG_NO_LOI=datas.GetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE");
				if(SysUtility.isNotEmpty(ENT_REG_NO_LOI)){
					String sqlLOI="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=?";
					Datas datassLOI=DataAccess.GetTableDatas("T_ENT_REG", sqlLOI,ENT_REG_NO_LOI);
					String ENT_CBEC_NO_LO=datassLOI.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				}
				String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
				if(SysUtility.isNotEmpty(ECP_CODE)){
					String sqlECP="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_PL ='Y' AND ENT_REG_NO=?";
					Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
					String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
					datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", ENT_CBEC_NO_ECP);
				}
				
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), 50));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "2");
					datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO",  ORDER_NO);
					datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
					//2、插入任务表
					String EXS="";
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						EXS="EXS";
					}
					String MESSAGE_TYPE="6";
					String MESSAGE_DESC="放行";
					
					
					SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "DECL_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
				}else{
					String sql="select DECL_DATE from t_ent_reg where ENT_REG_NO=?";
					Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
					String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
					String IE_FLAG=datas.GetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG");
					if("I".equals(IE_FLAG)){
						datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "0");
					}else{
						datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "");
					}
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
					datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE",SysUtility.getSysDate());
					datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
					datas.SetTableValue("C_EXIT_DECLARE", "IMP_DATE", SysUtility.getSysDate());
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
					datas.SetTableValue("C_EXIT_DECLARE", "PAYMENT_NO", PAYMENT_NO);
					datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "2");
					datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
					//datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
					datas.SetTableValue("C_EXIT_DECLARE", "CREATE_TIME", "");
					String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
					if(SysUtility.isNotEmpty(AREA_CODE)){
						String sqlAREA="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? OR ENT_CBEC_NO=?";
						Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
						String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
						datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
					}
					
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
				
					
					String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
					
					String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
					
					String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
					datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
					
					String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
					datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

					String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
					datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
					
					String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
					datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
					datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
					String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
					datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
					datas.SetTableValue("C_EXIT_DECLARE", "SEND_SOURCE_NODE", MESSAGE_DEST);
					for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID",datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);

						String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);

						String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
						datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
					    
						String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
					    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
								String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
								Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
								String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
								if(SysUtility.isEmpty(GOODS_CBEC_NO)){
									String sqlu="UPDATE T_DECL_FA SET MSG_FLAG='3' WHERE INDX=?";
									DataAccess.ExecSQL(sqlu, Faindx);
									return;
								}
								datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
						}
					}
					
					if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
						if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
							datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
						}
						datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID");
						//插入快件任务
						SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
						String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
						DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
						String sqlfas="update T_DECL_FA set MSG_FLAG='1' where indx=?";
						DataAccess.ExecSQL(sqlfas, Faindx);
					}
				}
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_DECL_FA SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, Faindx);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}

		}
	public static void SavereFaPass(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			//申报单信息
			HashMap SourceData=new HashMap();
			String sqlFA="select * from T_DECL_FA where indx=?";
			JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
			List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
			//申报单商品
			String sqlFAG="SELECT * FROM T_DECL_FA_GOODS WHERE P_INDX=?";
			JSONObject jsonFAG=DataAccess.GetTableJSON("T_DECL_FA_GOODS", sqlFAG, Faindx);
			List listFAG=SysUtility.JSONToList("T_DECL_FA_GOODS", jsonFAG);

		
			SourceData.put("C_EXIT_DECLARE", listFA);//申报单
			SourceData.put("C_EXIT_DECLARE_ITEMS", listFAG);//申报单商品
		
			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.remove("C_EXIT_DECLARE_ITEMS");
		

			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
			datas.MapToDatas("C_EXIT_DECLARE_ITEMS", SourceData, XmlFAItemMapping);
		
			String ORDER_GUID_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_GUID_NO");
			String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
			String DATA_SOURCE=	datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			String PAYMENT_NO=datas.GetTableValue("C_EXIT_DECLARE", "PAYMENT_NO");
			if(SysUtility.isEmpty(PAYMENT_NO)){
			String sqlPAY="select PAYMENT_NO from t_payment where order_no=?";
			JSONObject jsonPAY=DataAccess.GetTableJSON("t_payment", sqlPAY,ORDER_NO );
		    JSONArray arrPAY=jsonPAY.getJSONArray("t_payment");
		    if(arrPAY.length()>0){
		    	PAYMENT_NO=arrPAY.getJSONObject(0).getString("PAYMENT_NO");
		   }
		   }
			String  MESSAGE_SOURCE= SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"), 2)+"0000";
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
				datas.SetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE", MESSAGE_SOURCE);
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			datas.SetTableValue("C_EXIT_DECLARE","MESSAGE_SOURCE","");
			//CBE
			String ENT_REG_NO_CBE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			String ENT_CBEC_NO_CBE="";
			if(SysUtility.isNotEmpty(ENT_REG_NO_CBE)){
				String sqlCBE="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_CBE ='Y' AND ENT_REG_NO=?";
				Datas datasENT=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,ENT_REG_NO_CBE);
			    ENT_CBEC_NO_CBE=datasENT.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			}
			
			
			 //LO
			String ENT_REG_NO_LOI=datas.GetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE");
			if(SysUtility.isNotEmpty(ENT_REG_NO_LOI)){
				String sqlLOI="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=?";
				Datas datassLOI=DataAccess.GetTableDatas("T_ENT_REG", sqlLOI,ENT_REG_NO_LOI);
				String ENT_CBEC_NO_LO=datassLOI.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "LOGISTICS_CODE", ENT_CBEC_NO_LO);
				
			}
			String ECP_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
			if(SysUtility.isNotEmpty(ECP_CODE)){
				String sqlECP="select ENT_CBEC_NO from T_ENT_REG WHERE ENT_TYPE_PL ='Y' AND ENT_REG_NO=?";
				Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
				String ENT_CBEC_NO_ECP=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", ENT_CBEC_NO_ECP);
			}
			
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "CHECKLIST_NO",  SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), 50));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "DECL_NO",  datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_PLATFORM_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_ORG_CODE",  MESSAGE_SOURCE);
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "MONITOR_PERSON_CODE",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVALER"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_RESULT",  "1");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_CODE",  SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "E_BUSINESS_COMPANY_NAME",  datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "SYS_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "FACT_RELEASE_TIME",  datas.GetTableValue("C_EXIT_DECLARE", "APPROVAL_TIME"));
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "RELEASE_STATUS",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "INVENTORY_FLAG",  "2");
				datas.SetTableValue("C_DECLARE_RELEASE_RECORD", "ORDER_NO",  ORDER_NO);
				datas.InsertDB(DataAccess, "C_DECLARE_RELEASE_RECORD", "C_DECLARE_RELEASE_RECORD", "RELEASE_ORDER_RECORD_ID");
				//2、插入任务表
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					SaveCbecSendTable(DataAccess, "SEND_RELEASE_ORDER_RECORD", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_RELEASE_RECORD set PROCESS_STATUS='1' where RELEASE_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					EXS="EXS";
				}
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				
				
				/*SaveRSP(DataAccess,  datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);*/
			}
				String sql="select DECL_DATE from t_ent_reg where ENT_REG_NO=?";
				Datas datass1 = DataAccess.GetTableDatas("T_ENT_REG", sql,ECP_CODE);
				String	DECL_DATE = datass1.GetTableValue("T_ENT_REG", "DECL_DATE");
				String IE_FLAG=datas.GetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG");
				if("I".equals(IE_FLAG)){
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "0");
				}else{
					datas.SetTableValue("C_EXIT_DECLARE", "IMPORT_TYPE", "");
					datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_FLAG", "O");
				}
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_SOURCE", MESSAGE_SOURCE);
				datas.SetTableValue("C_EXIT_DECLARE", "EXP_MESSAGE_DEST", MESSAGE_DEST);
				datas.SetTableValue("C_EXIT_DECLARE", "IN_OUT_DATE",SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "STATUS", "6");
				datas.SetTableValue("C_EXIT_DECLARE", "IMP_DATE", SysUtility.getSysDate());
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_DATE", DECL_DATE);
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_ID", ORDER_GUID_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "PAYMENT_NO", PAYMENT_NO);
				datas.SetTableValue("C_EXIT_DECLARE", "TRADE_MODE", "2");
				datas.SetTableValue("C_EXIT_DECLARE", "MONITOR_FLAG", "N");
			
				String AREA_CODE=datas.GetTableValue("C_EXIT_DECLARE", "AREA_CODE");
				if(SysUtility.isNotEmpty(AREA_CODE)){
					String sqlAREA="select * from T_ENT_REG WHERE ENT_TYPE_LST ='Y' AND ENT_REG_NO=? or ENT_CBEC_NO=?";
					Datas datasAREA=DataAccess.GetTableDatas("T_ENT_REG", sqlAREA,new Object [] {AREA_CODE,AREA_CODE});
					String AREA_CODE1=datasAREA.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
					datas.SetTableValue("C_EXIT_DECLARE", "AREA_CODE", AREA_CODE1);
				}
				
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", ENT_CBEC_NO_CBE);
			
				
				String CHECKLIST_NO=datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO", SysUtility.getStrByLength(CHECKLIST_NO, 40));
				
				String E_BUSINESS_COMPANY_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
				
				String E_BUSINESS_PLATFORM_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE");
				datas.SetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(E_BUSINESS_PLATFORM_CODE, 20));
				
				String ORDER_NUMBER=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
				datas.SetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER, 50));

				String SENDER=datas.GetTableValue("C_EXIT_DECLARE", "SENDER");
				datas.SetTableValue("C_EXIT_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER, 100));
				
				String RECEIVER=datas.GetTableValue("C_EXIT_DECLARE", "RECEIVER");
				datas.SetTableValue("C_EXIT_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER, 100));
				String REMARK =datas.GetTableValue("C_EXIT_DECLARE", "REMARK");
				datas.SetTableValue("C_EXIT_DECLARE", "CHECKLIST_TYPE", "3");
				//数据来源3为直属局 ,4为总局
				if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "4");
				}else{
					datas.SetTableValue("C_EXIT_DECLARE", "FROM_WHERE", "3");
				}
				
				datas.SetTableValue("C_EXIT_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK, 1000));
				//当主表包装类型为空时，试着去取子表数据
				if(SysUtility.isEmpty(datas.GetTableValue("C_EXIT_DECLARE", "PACK_TYPE_CODE"))){
					datas.SetTableValue("C_EXIT_DECLARE", "PACK_TYPE_CODE", datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "PACK_TYPE_CODE"));
					datas.SetTableValue("C_EXIT_DECLARE", "PACK_TYPE_NAME", datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "PACK_TYPE_NAME"));
				}
				
				if(SysUtility.isNotEmpty(datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"))){
					datas.InsertDB(DataAccess, "C_EXIT_DECLARE", "C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID");
				}
				
				
				
				for(int i=0;i<datas.GetTableRows("C_EXIT_DECLARE_ITEMS");i++){
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_DECLAR_CHECK_ID",datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "SEND_SOURCE_NODE", MESSAGE_DEST,i);
					String CURR_UNIT=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "CURR_UNIT", SysUtility.getStrByLength(CURR_UNIT, 3),i);

					String REMARK1=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK",i);
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "REMARK1", SysUtility.getStrByLength(REMARK1, 1000),i);
				    
					datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "MAIN_WB_NO", datas.GetTableValue("C_EXIT_DECLARE", "MAIN_WB_NO"));		
						
					String GOODS_REG_NO=datas.GetTableValue("C_EXIT_DECLARE_ITEMS", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				datas.InsertDB(DataAccess, "C_EXIT_DECLARE_ITEMS", "C_EXIT_DECLARE_ITEMS", "DETAIL_ID");
			
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//插入快件任务
					SaveCbecSendTable(DataAccess, "UP_GOODS_DECLAR_CHECK", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_EXIT_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				}
			
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}

	}
	
	//FA人工复审查验	
	public static void SaveFaCheck(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			String sqlFA="select * from T_DECL_FA where indx=?";
			JSONObject jsonFA=DataAccess.GetTableJSON("T_DECL_FA", sqlFA, Faindx);
			List listFA=SysUtility.JSONToList("T_DECL_FA", jsonFA);
			SourceData.put("C_EXIT_DECLARE", listFA);//申报单
			Datas datas =new Datas();
			datas.remove("C_EXIT_DECLARE");
			datas.MapToDatas("C_EXIT_DECLARE",SourceData,XmlFAMappingMap);
			String DATA_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "DATA_SOURCE");
			String ORDER_NO=datas.GetTableValue("C_EXIT_DECLARE", "ORDER_NUMBER");
			String CBE_CODE=datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_CODE");
			
			String sql ="SELECT * FROM T_ORDER WHERE ORDER_NO='"+ORDER_NO+"' and CBE_CODE=?";
			Datas datasor = DataAccess.GetTableDatas("T_ORDER", sql,CBE_CODE);
			String ORDER_GUID_NO=datasor.GetTableValue("T_ORDER", "ORDER_GUID_NO");
			String ECP_CODE=datasor.GetTableValue("T_ORDER", "ECP_CODE");
			
			String  MESSAGE_SOURCE= datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datas.GetTableValue("C_EXIT_DECLARE", "MESSAGE_DEST");
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			
			String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_CBE='Y' AND  ENT_REG_NO=?";
			Datas datassa=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,CBE_CODE);
			String ENT_CBEC_NO_CBE=datassa.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			 
	        String sqlECP="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_TYPE_PL='Y' AND  ENT_REG_NO=?";
	        Datas dataECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
	        String ENT_CBEC_NO_ECP=dataECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
			
			if(SysUtility.isNotEmpty(DATA_SOURCE)&&!"1".equals(DATA_SOURCE)){
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID", datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "CHECKLIST_NO", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), 40));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "DECL_NO", datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(ENT_CBEC_NO_ECP, 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_PLATFORM_NAME"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE",SysUtility.getStrByLength(ENT_CBEC_NO_CBE, 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_NAME", datas.GetTableValue("C_EXIT_DECLARE", "E_BUSINESS_COMPANY_NAME"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_ORG_CODE", datas.GetTableValue("C_EXIT_DECLARE", "INSP_ORG_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "AUDIT_PERSON_CODE", SysUtility.getStrByLength(datas.GetTableValue("C_EXIT_DECLARE", "CF_PERSON"), 20));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "SPOT_STATUS", datas.GetTableValue("C_EXIT_DECLARE", "CF_TYPE_CODE"));
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "ORDER_NO",ORDER_NO);
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "INVENTORY_FLAG", "2");
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_PLATFORM_CODE", ENT_CBEC_NO_ECP);
				datas.SetTableValue("C_DECLARE_CHECK_RECORD", "E_BUSINESS_COMPANY_CODE",ENT_CBEC_NO_CBE);
				datas.InsertDB(DataAccess, "C_DECLARE_CHECK_RECORD", "C_DECLARE_CHECK_RECORD", "CHECK_ORDER_RECORD_ID");
				String EXS="";
				if(SysUtility.isNotEmpty(ENT_CBEC_NO_CBE)){
					//2、插入任务表
					SaveCbecSendTable(DataAccess, "SEND_CHECK_ORDER_RECORD",  datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
					String upStatus="update C_DECLARE_CHECK_RECORD set PROCESS_STATUS='1' where CHECK_ORDER_RECORD_ID=?";
					DataAccess.ExecSQL(upStatus,datas.GetTableValue("C_EXIT_DECLARE", "GOODS_DECLAR_CHECK_ID"));
					EXS="EXS";
				}
				String MESSAGE_TYPE="6";
				String MESSAGE_DESC="放行";
				/*SaveRSP(DataAccess, datas.GetTableValue("C_EXIT_DECLARE", "CHECKLIST_NO"), MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);*/
				
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}	
	//FA查验管理暂扣
	public static void SaveFaBeta(String Faindx,IDataAccess DataAccess)throws Exception{
		try {
			String sqlFA="select * from T_DECL_FA where indx=?";
			Datas datasor = DataAccess.GetTableDatas("T_DECL_FA", sqlFA,Faindx);
			String DECL_NO=datasor.GetTableValue("T_DECL_FA", "DECL_NO");
			String ORDER_NO=datasor.GetTableValue("T_DECL_FA", "ORDER_NO");
			String CBE_CODE=datasor.GetTableValue("T_DECL_FA", "CBE_CODE");
			String sqlO="select * from T_ORDER where ORDER_NO='"+ORDER_NO+"' AND CBE_CODE=?";
			Datas dataso = DataAccess.GetTableDatas("T_ORDER", sqlO,CBE_CODE);
			String ORDER_GUID_NO=dataso.GetTableValue("T_ORDER", "ORDER_GUID_NO");
			String MESSAGE_SOURCE=datasor.GetTableValue("T_DECL_FA", "INSP_ORG_CODE");
			if(SysUtility.isEmpty(MESSAGE_SOURCE)){
				MESSAGE_SOURCE=datasor.GetTableValue("T_DECL_FA", "MESSAGE_DEST");
			}
			String	MESSAGE_DEST= SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			String MESSAGE_TYPE="5";
			String MESSAGE_DESC="查验完成";
			String EXS="EXS";
			SaveRSP(DataAccess, DECL_NO, MESSAGE_TYPE, MESSAGE_DESC, ORDER_NO, "2", ORDER_GUID_NO, MESSAGE_SOURCE, MESSAGE_DEST,EXS);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	//三单上报
		public static void SaveOBP(String OINDX,String BINDX,IDataAccess DataAccess)throws Exception{
			try {
				HashMap SourceData=new HashMap();
				//订单
				String sqlOrder="SELECT * FROM T_ORDER WHERE INDX=?";
				JSONObject jsonORDER=DataAccess.GetTableJSON("T_ORDER", sqlOrder, OINDX);
				List listOrder=SysUtility.JSONToList("T_ORDER", jsonORDER);
				SourceData.put("C_ORDER_DECLARE", listOrder);//订单
				
				//订单商品
				String sqlOrdeG="SELECT * FROM T_ORDER_GOODS WHERE p_indx=?";
				JSONObject jsonORDERG=DataAccess.GetTableJSON("T_ORDER_GOODS", sqlOrdeG,OINDX);
				List listOrderGoods=SysUtility.JSONToList("T_ORDER_GOODS", jsonORDERG);
				SourceData.put("C_ORDER_DECLARE_DETAIL", listOrderGoods);//订单商品
				String sqlBill="SELECT * FROM T_BILL WHERE INDX=?";
				JSONObject jsonBill=DataAccess.GetTableJSON("T_BILL", sqlBill, BINDX);
				List listBill=SysUtility.JSONToList("T_BILL", jsonBill);
				SourceData.put("T_BILL", listBill);//订单
				
				Datas datas =new Datas();
				
				datas.remove("C_ORDER_DECLARE");
				datas.remove("C_ORDER_DECLARE_DETAIL");
				datas.remove("C_PAYMENT_DECLARE");
				datas.remove("C_ITEM_DECLARE");
				
				datas.MapToDatas("C_ORDER_DECLARE", SourceData,XmlOrderMapping);
				datas.MapToDatas("T_BILL", SourceData,XmlBillMapping);
				
				String ORDER_NO=datas.GetTableValue("C_ORDER_DECLARE", "ORDER_NO");
				String CBE_CODE=datas.GetTableValue("C_ORDER_DECLARE", "CBE_CODE");		
				//ECP_CODE
				String ECP_CODE=datas.GetTableValue("C_ORDER_DECLARE", "COMPANY_CODE");
				String sqlECP="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_REG_NO=?";
				Datas datasECP=DataAccess.GetTableDatas("T_ENT_REG", sqlECP,ECP_CODE);
				String COMPANY_CODE=datasECP.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_ORDER_DECLARE", "COMPANY_CODE", SysUtility.getStrByLength(COMPANY_CODE, 20));	
				//运单
				//LOGIS_COMPANY_NAME
				String sqlB="SELECT LOGISTICS_NAME,BILL_GUID_NO,LOGISTICS_CODE,LOGISTICS_NO,MAIN_WB_NO,INSURED_FEE FROM T_BILL WHERE INDX=?";
				Datas datasBILL=DataAccess.GetTableDatas("BILL", sqlB,BINDX);
				String LOGIS_COMPANY_NAME=datasBILL.GetTableValue("BILL", "LOGISTICS_NAME");
				datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_NAME", SysUtility.getStrByLength(LOGIS_COMPANY_NAME, 200));
				//BILL_GUID_NO
				String GOODS_DECLAR_CHECK_ID=datasBILL.GetTableValue("BILL", "BILL_GUID_NO");
				datas.SetTableValue("C_ORDER_DECLARE", "GOODS_DECLAR_CHECK_ID", GOODS_DECLAR_CHECK_ID);
				
				//LOGISTICS_CODE
				String LOGISTICS_CODE=datasBILL.GetTableValue("BILL", "LOGISTICS_CODE");
				String sqlLO="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_REG_NO=?";
				Datas datasEL=DataAccess.GetTableDatas("T_ENT_REG", sqlLO,LOGISTICS_CODE);
				String LOGIS_COMPANY_CODE=datasBILL.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				datas.SetTableValue("C_ORDER_DECLARE", "LOGIS_COMPANY_CODE", LOGIS_COMPANY_CODE);
				
				String LOGISTICS_NO=datasBILL.GetTableValue("BILL", "LOGISTICS_NO");
				datas.SetTableValue("C_ORDER_DECLARE", "LOGISTICS_NO", LOGISTICS_NO);
				
				String MAIN_WB_NO=datasBILL.GetTableValue("BILL", "MAIN_WB_NO");
				datas.SetTableValue("C_ORDER_DECLARE", "MAIN_WB_NO", SysUtility.getStrByLength(MAIN_WB_NO, 37));
				
				String INSURED_FEE=datasBILL.GetTableValue("BILL", "INSURED_FEE");
				datas.SetTableValue("C_ORDER_DECLARE", "INSURED_FEE", INSURED_FEE);
				
				String CURR_CODE=datas.GetTableValue("C_ORDER_DECLARE", "CURR_CODE");
				datas.SetTableValue("C_ORDER_DECLARE", "INSURED_FEE", SysUtility.getStrByLength(CURR_CODE, 3));
				
				String CONSIGNEE=datas.GetTableValue("C_ORDER_DECLARE", "CONSIGNEE");
				datas.SetTableValue("C_ORDER_DECLARE", "CONSIGNEE", SysUtility.getStrByLength(CONSIGNEE, 120));
				
				String CONSIGNEE_ADDRESS=datas.GetTableValue("C_ORDER_DECLARE", "CONSIGNEE_ADDRESS");
				datas.SetTableValue("C_ORDER_DECLARE", "CONSIGNEE_ADDRESS", SysUtility.getStrByLength(CONSIGNEE_ADDRESS, 255));
				
				String ADDRESSOR_NAME=datas.GetTableValue("C_ORDER_DECLARE", "ADDRESSOR_NAME");
				datas.SetTableValue("C_ORDER_DECLARE", "ADDRESSOR_NAME", SysUtility.getStrByLength(ADDRESSOR_NAME, 50));

				String BUYER_ID_TYPE=datas.GetTableValue("C_ORDER_DECLARE", "BUYER_ID_TYPE");
				datas.SetTableValue("C_ORDER_DECLARE", "BUYER_ID_TYPE", SysUtility.getStrByLength(BUYER_ID_TYPE, 1));

				//包裹单
				/*StringBuffer OBSQL=new StringBuffer();
				OBSQL.append(" select  B.BILL_GUID_NO,   B.IE_FLAG,  B.ACCEPT_TIME,B.CONSIGNOR_CITY_CODE,B.DEST_NODE,B.MONITOR_DECL_FLAG,B.SEND_SOURCE_NODE, ");
				OBSQL.append(" B.LOGISTICS_NO, B.GROSS_WEIGHT,  B.NET_WEIGHT, ");
				OBSQL.append(" B.CONSIGNOR_CTRY_CODE,  B.PROD_NAME_CN,  B.CREATE_TIME,  B.REMARK, ");
				OBSQL.append(" O.CBE_CODE, O.CBE_NAME,  O.ORDER_NO, O.TRADE_CTRY_CODE, O.CONSIGNOR_CNAME,O.CONSIGNEE_CNAME, ");
				OBSQL.append(" O.PRICE_TOTAL_VAL, O.CURR_UNIT,  O.INSP_ORG_CODE,  O.ORDER_GUID_NO, O.INSP_ORG_NAME, ");
				OBSQL.append(" O.BIZ_TYPE from T_ORDER O, T_BILL B ");
				OBSQL.append(" WHERE  O.INDX =? AND B.INDX =? ");*/
				
				StringBuffer OBSQL=new StringBuffer();
				OBSQL.append(" select  B.BILL_GUID_NO,   B.IE_FLAG,  B.ACCEPT_TIME,B.CONSIGNOR_CITY_CODE,B.MONITOR_DECL_FLAG, ");
				OBSQL.append(" B.LOGISTICS_NO, B.GROSS_WEIGHT,  B.NET_WEIGHT, ");
				OBSQL.append(" B.CONSIGNOR_CTRY_CODE,  B.PROD_NAME_CN,  B.CREATE_TIME,  B.REMARK, ");
				OBSQL.append(" O.CBE_CODE, O.CBE_NAME,  O.ORDER_NO, O.TRADE_CTRY_CODE, O.CONSIGNOR_CNAME,O.CONSIGNEE_CNAME, ");
				OBSQL.append(" O.PRICE_TOTAL_VAL, O.CURR_UNIT,  O.INSP_ORG_CODE,  O.ORDER_GUID_NO, O.INSP_ORG_NAME, ");
				OBSQL.append(" O.BIZ_TYPE from T_ORDER O, T_BILL B ");
				OBSQL.append(" WHERE  O.INDX =? AND B.INDX =? ");
				
				
				JSONObject jsonDECLARE=DataAccess.GetTableJSON("C_ITEM_DECLARE", OBSQL.toString(),new String[]{OINDX,BINDX});
				List listDECLARE=SysUtility.JSONToList("C_ITEM_DECLARE", jsonDECLARE);
				
				//插入包裹单数据C_ITEM_DECLARE
				SourceData.put("C_ITEM_DECLARE", listDECLARE);
				datas.MapToDatas("C_ITEM_DECLARE", SourceData,XmlDeclareMapping);
				
				String IE_FLAG1=datas.GetTableValue("C_ITEM_DECLARE", "IN_OUT_FLAG");
				String BIZ_TYPE=datas.GetTableValue("C_ITEM_DECLARE", "BIZ_TYPE");
				if("I".equals(IE_FLAG1)&&"1".equals(BIZ_TYPE)){
					datas.SetTableValue("C_ITEM_DECLARE", "IMPORT_TYPE", "1");
				}else if("I".equals(IE_FLAG1)&&"2".equals(BIZ_TYPE)){
					datas.SetTableValue("C_ITEM_DECLARE", "IMPORT_TYPE", "0");
				}
				else if("E".equals(IE_FLAG1)){
					datas.SetTableValue("C_ITEM_DECLARE", "IN_OUT_FLAG", "O");
				}
				datas.SetTableValue("C_ITEM_DECLARE", "MONITOR_DECL_FLAG", "N");
				
				String sqlCBE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_REG_NO=?";
				Datas datasCBE=DataAccess.GetTableDatas("T_ENT_REG", sqlCBE,CBE_CODE);
				String E_BUSINESS_COMPANY_CODE=datasCBE.GetTableValue("T_ENT_REG", "ENT_CBEC_NO");
				
				datas.SetTableValue("C_ITEM_DECLARE", "E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(E_BUSINESS_COMPANY_CODE, 20));
				
				String ORDER_NUMBER1=datas.GetTableValue("C_ITEM_DECLARE", "ORDER_NUMBER");
				datas.SetTableValue("C_ITEM_DECLARE", "ORDER_NUMBER", SysUtility.getStrByLength(ORDER_NUMBER1, 50));

				String SUB_CARRIAGE_NO=datas.GetTableValue("C_ITEM_DECLARE", "SUB_CARRIAGE_NO");
				datas.SetTableValue("C_ITEM_DECLARE", "SUB_CARRIAGE_NO", SysUtility.getStrByLength(SUB_CARRIAGE_NO, 50));

				String SENDER1=datas.GetTableValue("C_ITEM_DECLARE", "SENDER");
				datas.SetTableValue("C_ITEM_DECLARE", "SENDER", SysUtility.getStrByLength(SENDER1, 100));

				String RECEIVER1=datas.GetTableValue("C_ITEM_DECLARE", "RECEIVER");
				datas.SetTableValue("C_ITEM_DECLARE", "RECEIVER", SysUtility.getStrByLength(RECEIVER1, 100));

				String SEND_SOURCE_NODE=datas.GetTableValue("C_ITEM_DECLARE", "SEND_SOURCE_NODE");
				datas.SetTableValue("C_ITEM_DECLARE", "SEND_SOURCE_NODE", SysUtility.getStrByLength(SEND_SOURCE_NODE, 10));

				String ORG_CODE=datas.GetTableValue("C_ITEM_DECLARE", "ORG_CODE");
				datas.SetTableValue("C_ITEM_DECLARE", "ORG_CODE", SysUtility.getStrByLength(ORG_CODE, 10));
				if(SysUtility.isEmpty(datas.GetTableValue("C_ITEM_DECLARE", "ORG_CODE"))){
					datas.SetTableValue("C_ITEM_DECLARE", "ORG_CODE", datas.GetTableValue("C_ORDER_DECLARE", "MESSAGE_DEST"));
				}
				
				String REMARK1=datas.GetTableValue("C_ITEM_DECLARE", "REMARK");
				datas.SetTableValue("C_ITEM_DECLARE", "REMARK", SysUtility.getStrByLength(REMARK1, 1000));
				datas.SetTableValue("C_ITEM_DECLARE", "FROM_WHERE", "3");
				
				
				//包裹单商品
				String sqlOG="SELECT * FROM T_ORDER_GOODS WHERE P_iNDX=?";
				JSONObject jsonOG=DataAccess.GetTableJSON("C_ORDER_DECLARE_DETAIL", sqlOG,OINDX);
				List listOG=SysUtility.JSONToList("C_ORDER_DECLARE_DETAIL", jsonOG);
				
				SourceData.put("C_ORDER_DECLARE_DETAIL", listOG);
				datas.MapToDatas("C_ORDER_DECLARE_DETAIL", SourceData,XmlOrderItemMapping);
				
				for(int i=0;i<datas.GetTableRows("C_ORDER_DECLARE_DETAIL");i++){
					
					//PROD_SPECS	
				String PROD_SPECS=	datas.GetTableValue("C_ORDER_DECLARE_DETAIL", "GOODS_SPECIFICATION",i);
				if(SysUtility.isEmpty(PROD_SPECS)){
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "GOODS_SPECIFICATION",datas.GetTableValue("C_ORDER_DECLARE_DETAIL", "PROD_SPECS",i), i);
				}
					
					
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "DETAIL_ID", SysUtility.GetUUID(),i);
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "GOODS_DECLAR_CHECK_ID", datas.GetTableValue("C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID"),i);

					String PROD_BRD_CN=datas.GetTableValue("C_ORDER_DECLARE_DETAIL", "PROD_BRD_CN",i);
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "PROD_BRD_CN", SysUtility.getStrByLength(PROD_BRD_CN, 100),i);
				    
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "ORDER_ID", datas.GetTableValue("C_ITEM_DECLARE", "ORDER_ID"),i);
					
					String REMARKG=datas.GetTableValue("C_ORDER_DECLARE_DETAIL", "REMARK",i);
					datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "REMARK", SysUtility.getStrByLength(REMARKG, 1000),i);
				
					String GOODS_REG_NO=datas.GetTableValue("C_ORDER_DECLARE_DETAIL", "GOODS_REG_NO",i);
				    if(SysUtility.isNotEmpty(GOODS_REG_NO)){
							String sqlG ="SELECT GOODS_CBEC_NO from T_GOODS_REG WHERE GOODS_REG_NO=?";
							Datas data=DataAccess.GetTableDatas("T_GOODS_REG", sqlG,GOODS_REG_NO);
							String GOODS_CBEC_NO=data.GetTableValue("T_GOODS_REG", "GOODS_CBEC_NO");
							if(SysUtility.isEmpty(GOODS_CBEC_NO)){
								String sqlu="UPDATE T_ORDER_GOODS SET MSG_FLAG='3' WHERE GOODS_REG_NO=?";
								DataAccess.ExecSQL(sqlu, GOODS_REG_NO);
								return;
							}
							datas.SetTableValue("C_EXIT_DECLARE_ITEMS", "PRODUCT_RECORD_NO", GOODS_CBEC_NO,i);
					}
				}
				
				//支付单
				String sqlPAY="SELECT * FROM T_PAYMENT WHERE ORDER_NO=? AND CBE_CODE=? AND MSG_FLAG='0' ";
				JSONObject jsonPAY=DataAccess.GetTableJSON("C_PAYMENT_DECLARE", sqlPAY,new String[]{ORDER_NO,CBE_CODE});
				List listPAY=SysUtility.JSONToList("C_PAYMENT_DECLARE", jsonPAY);
				if(listPAY.size()>0){
					Datas datasP =new Datas();
					HashMap SourceDataP=new HashMap();
					datasP.remove("C_PAYMENT_DECLARE");
					SourceDataP.put("C_PAYMENT_DECLARE", listPAY);
					datasP.MapToDatas("C_PAYMENT_DECLARE", SourceDataP,XmlPayMapping);

					String PAY_CODE= datasP.GetTableValue("C_PAYMENT_DECLARE", "PAY_CODE");
					String sqlPE="SELECT ENT_CBEC_NO FROM T_ENT_REG WHERE ENT_REG_NO=?";
					Datas datasPAY=DataAccess.GetTableDatas("PAY", sqlPE,PAY_CODE);
					String PAY_ENTERPRISE_CODE=datasPAY.GetTableValue("PAY", "ENT_CBEC_NO");

					datasP.SetTableValue("C_PAYMENT_DECLARE", "PAY_ENTERPRISE_CODE", PAY_ENTERPRISE_CODE);

					String ORDER_ID=datas.GetTableValue("C_ORDER_DECLARE", "ORDER_ID");
					datasP.SetTableValue("C_PAYMENT_DECLARE", "ORDER_ID", ORDER_ID);
					
					String PAY_ENTERPRISE_NAME=datas.GetTableValue("C_PAYMENT_DECLARE", "PAY_ENTERPRISE_NAME");
					datasP.SetTableValue("C_PAYMENT_DECLARE", "PAY_ENTERPRISE_NAME",SysUtility.getStrByLength(PAY_ENTERPRISE_NAME, 200));
					
					String PAYER_DOCUMENT_NUMBER=datas.GetTableValue("C_PAYMENT_DECLARE", "PAYER_DOCUMENT_NUMBER");
					datasP.SetTableValue("C_PAYMENT_DECLARE", "PAYER_DOCUMENT_NUMBER",SysUtility.getStrByLength(PAYER_DOCUMENT_NUMBER, 30));

					String CURRENCY=datas.GetTableValue("C_PAYMENT_DECLARE", "CURRENCY");
					datasP.SetTableValue("C_PAYMENT_DECLARE", "CURRENCY",SysUtility.getStrByLength(CURRENCY, 3));
					
					String REMARK=datas.GetTableValue("C_PAYMENT_DECLARE", "REMARK");
					datasP.SetTableValue("C_PAYMENT_DECLARE", "REMARK",SysUtility.getStrByLength(REMARK, 1000));
					
					datasP.InsertDB(DataAccess, "C_PAYMENT_DECLARE", "C_PAYMENT_DECLARE", "PAY_ID");
					String upsql="UPDATE T_PAYMENT SET MSG_FLAG='1' WHERE CBE_CODE='"+CBE_CODE+"' AND ORDER_NO=?";
					DataAccess.ExecSQL(upsql,CBE_CODE);
				}
				String MESSAGE_SOURCE=datas.GetTableValue("C_ITEM_DECLARE", "ORG_CODE");
				if(SysUtility.isEmpty(MESSAGE_SOURCE)){
					 MESSAGE_SOURCE=datas.GetTableValue("C_ITEM_DECLARE", "MESSAGE_SOURCE");
				}
				
				datas.InsertDB(DataAccess, "C_ORDER_DECLARE", "C_ORDER_DECLARE", "ORDER_ID");
				datas.InsertDB(DataAccess, "C_ITEM_DECLARE", "C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID");
				datas.InsertDB(DataAccess, "C_ORDER_DECLARE_DETAIL", "C_ORDER_DECLARE_DETAIL", "DETAIL_ID");
				String	sqlOrders = "update T_ORDER set MSG_FLAG='1' where INDX=?";
				DataAccess.ExecSQL(sqlOrders, OINDX);
				String	sqlBills = "update T_BILL set MSG_FLAG='1' where INDX=?";
				DataAccess.ExecSQL(sqlBills, BINDX);
				String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
				SaveCbecSendTable(DataAccess, "I_MM_ORDER_IMPORT_CHECK", datas.GetTableValue("C_ORDER_DECLARE", "ORDER_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
				String upStatusITEM="update C_ITEM_DECLARE set PROCESS_STATUS='1' where GOODS_DECLAR_CHECK_ID=?";
				DataAccess.ExecSQL(upStatusITEM, datas.GetTableValue("C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID"));
			    DataAccess.ComitTrans();
			} catch (Exception e) {
				DataAccess.RoolbackTrans();
				String sql="UPDATE T_ORDER SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sql, OINDX);
				String sqlB="UPDATE T_BILL SET MSG_FLAG='2' WHERE INDX=?";
				DataAccess.ExecSQL(sqlB, BINDX);
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	//订单上报
	public static void SaveOrder(String INDX,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			//1、查询主表信息，转换主表字段
			String sqlOrder="SELECT ORDER_GUID_NO,IE_FLAG,ORDER_TOTAL_AMOUNT,ORDER_NO,ECP_NAME,ECP_CODE,ENT_CODE,ENT_NAME,INSP_ORG_CODE,CBE_CODE,CBE_NAME,PAYMENT_NO,PAY_NAME,PAY_CODE,TRADE_CTRY_CODE,TRADE_TIME,"
					+ "CURR_UNIT,CONSIGNEE_EMAIL,CONSIGNEE_TEL,CONSIGNEE_CNAME,CONSIGNEE_ADDR,PACK_NO,POST_MODE,CONSIGNOR_CTRY_CODE,CONSIGNOR_CNAME,CREATE_TIME,LOGISTICS_NAME,LOGISTICS_CODE,DISCOUNT,TAX_TOTAL,"
					+ "ACTURAL_PAID,ID_REGNO,ID_TYPENAME,ID_TYPE,ID_CARD,BATCH_NO,CONSIGNEE_DITRIC,SENDER_CITY,ID_TEL,PRICE_TOTAL_VAL,FREIGHT,LOGISTICS_NO,MAIN_WB_NO,INSURED_FEE,BIZ_TYPE"
					+ " FROM T_ORDER WHERE INDX=?";
			JSONObject jsonORDER=DataAccess.GetTableJSON("T_ORDER", sqlOrder, INDX);
			List listOrder=SysUtility.JSONToList("T_ORDER", jsonORDER);
			SourceData.put("C_ORDER_DECLARE", listOrder);
			String sqlOG="SELECT * FROM T_ORDER_GOODS WHERE P_iNDX=?";
			JSONObject jsonOG=DataAccess.GetTableJSON("C_ORDER_DECLARE_DETAIL", sqlOG,INDX);
			List listOG=SysUtility.JSONToList("C_ORDER_DECLARE_DETAIL", jsonOG);
			SourceData.put("C_ORDER_DECLARE_DETAIL", listOG);
			Datas datas =new Datas();
			datas.remove("C_ORDER_DECLARE");
			datas.remove("C_ORDER_DECLARE_DETAIL");
			datas.MapToDatas("C_ORDER_DECLARE", SourceData,XmlOrderMapping);
			datas.MapToDatas("C_ORDER_DECLARE_DETAIL", SourceData,XmlOrderItemMapping);
			//赋默认值
			datas.SetTableValue("C_ORDER_DECLARE", "MONITOR_DECL_FLAG", "N");
			//转换ECP_CODE
			String ECP_CODE=datas.GetTableValue("C_ORDER_DECLARE", "E_BUSINESS_PLATFORM_CODE");
			datas.SetTableValue("C_ORDER_DECLARE", "E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(turnCbeToEnt(ECP_CODE), 20));	
			//转换ENT_CODE
			String ENT_CODE=datas.GetTableValue("C_ORDER_DECLARE","DECL_REG_NO");
			datas.SetTableValue("C_ORDER_DECLARE","DECL_REG_NO", SysUtility.getStrByLength(turnCbeToEnt(ENT_CODE), 20));	
			//转换CBE_CODE
			String CBE_CODE=datas.GetTableValue("C_ORDER_DECLARE","E_BUSINESS_COMPANY_CODE");
			datas.SetTableValue("C_ORDER_DECLARE","E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(turnCbeToEnt(CBE_CODE), 20));	
			//转换PAY_CODE
			String PAY_CODE=datas.GetTableValue("C_ORDER_DECLARE","PAY_ENTERPRISE_CODE");
			datas.SetTableValue("C_ORDER_DECLARE","PAY_ENTERPRISE_CODE", SysUtility.getStrByLength(turnCbeToEnt(PAY_CODE), 20));	
			//转换LOGISTICS_CODE
			String LOGISTICS_CODE=datas.GetTableValue("C_ORDER_DECLARE","LOGIS_COMPANY_CODE");
			datas.SetTableValue("C_ORDER_DECLARE","LOGIS_COMPANY_CODE", SysUtility.getStrByLength(turnCbeToEnt(LOGISTICS_CODE), 20));	
			String ORDER_ID=datas.GetTableValue("C_ORDER_DECLARE", "ORDER_ID");
			for(int i=0;i<datas.GetTableRows("C_ORDER_DECLARE_DETAIL");i++){
				datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "DETAIL_ID", SysUtility.GetUUID(),i);
				datas.SetTableValue("C_ORDER_DECLARE_DETAIL", "ORDER_ID",ORDER_ID,i);
			}
			//2、插入C表，更改主表状态
			datas.InsertDB(DataAccess, "C_ORDER_DECLARE", "C_ORDER_DECLARE", "ORDER_ID");
			datas.InsertDB(DataAccess, "C_ORDER_DECLARE_DETAIL", "C_ORDER_DECLARE_DETAIL", "DETAIL_ID");
			String MESSAGE_SOURCE=datas.GetTableValue("C_ORDER_DECLARE", "ORG_CODE");
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			//3、插入任务表
			SaveCbecSendTable(DataAccess, "RE_ORDER_CHECK_BUREAU", datas.GetTableValue("C_ORDER_DECLARE", "ORDER_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
			String	sqlOrders = "UPDATE T_ORDER SET MSG_FLAG='1' WHERE INDX=?";
			DataAccess.ExecSQL(sqlOrders, INDX);
			DataAccess.ComitTrans();
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			String sql="UPDATE T_ORDER SET MSG_FLAG='2' WHERE INDX=?";
			DataAccess.ExecSQL(sql, INDX);
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	//运单上报
	public static void SaveBill(String INDX,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			Datas datasBILL=new Datas();
			//1、查询主表信息，转换主表字段
			String sqlBill="SELECT * FROM T_BILL WHERE INDX=?";
			datasBILL=DataAccess.GetTableDatas("BILL", sqlBill,INDX);
			JSONObject jsonBill=DataAccess.GetTableJSON("T_BILL", sqlBill, INDX);
			List listBill=SysUtility.JSONToList("T_BILL", jsonBill);
			SourceData.put("C_ITEM_DECLARE", listBill);
			Datas datas =new Datas();
			datas.remove("C_ITEM_DECLARE");
			datas.MapToDatas("C_ITEM_DECLARE", SourceData,XmlBillMapping);
			//转换LOGISTICS_CODE
			String LOGISTICS_CODE=datas.GetTableValue("C_ITEM_DECLARE","LOGIS_COMPANY_CODE");
			datas.SetTableValue("C_ITEM_DECLARE","LOGIS_COMPANY_CODE", SysUtility.getStrByLength(turnCbeToEnt(LOGISTICS_CODE), 20));	
			//转换CBE_CODE
			String CBE_CODE=datas.GetTableValue("C_ITEM_DECLARE","E_BUSINESS_COMPANY_CODE");
			datas.SetTableValue("C_ITEM_DECLARE","E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(turnCbeToEnt(CBE_CODE), 20));	
			//转换ECP_CODE
			String ECP_CODE=datas.GetTableValue("C_ITEM_DECLARE","E_BUSINESS_PLATFORM_CODE");
			datas.SetTableValue("C_ITEM_DECLARE","E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(turnCbeToEnt(ECP_CODE), 20));	
			//转换ENT_CODE
			String ENT_CODE=datas.GetTableValue("C_ITEM_DECLARE","DECL_REG_NO");
			datas.SetTableValue("C_ITEM_DECLARE","DECL_REG_NO", SysUtility.getStrByLength(turnCbeToEnt(ENT_CODE), 20));	
			//转换BIZ_TYPE
			String BIZ_TYPE=datas.GetTableValue("C_ITEM_DECLARE","IMPORT_TYPE");
			datas.SetTableValue("C_ITEM_DECLARE","IMPORT_TYPE","3".equals(BIZ_TYPE) ? 0 : BIZ_TYPE);	
			//赋默认值
			datas.SetTableValue("C_ITEM_DECLARE", "MONITOR_DECL_FLAG", "N");
			//2、插入C表，更改主表状态
			datas.InsertDB(DataAccess, "C_ITEM_DECLARE", "C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID");
			String MESSAGE_SOURCE=datas.GetTableValue("C_ITEM_DECLARE", "ORG_CODE");
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			//3、插入任务表
			SaveCbecSendTable(DataAccess, "RE_GOODS_CHECK_BUREAU", datas.GetTableValue("C_ITEM_DECLARE", "GOODS_DECLAR_CHECK_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
			String	sqlOrders = "UPDATE T_BILL SET MSG_FLAG='1' WHERE INDX=?";
			DataAccess.ExecSQL(sqlOrders, INDX);
			DataAccess.ComitTrans();
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			String sql="UPDATE T_BILL SET MSG_FLAG='2' WHERE INDX=?";
			DataAccess.ExecSQL(sql, INDX);
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	//支付单上报
	public static void SavePayment(String INDX,IDataAccess DataAccess)throws Exception{
		try {
			HashMap SourceData=new HashMap();
			Datas datasPay=new Datas();
			//1、查询主表信息，转换主表字段
			String sqlPay="SELECT * FROM T_PAYMENT WHERE INDX=?";
			datasPay=DataAccess.GetTableDatas("PAYMENT", sqlPay,INDX);
			JSONObject jsonPay=DataAccess.GetTableJSON("T_PAYMENT", sqlPay, INDX);
			List listPay=SysUtility.JSONToList("T_PAYMENT", jsonPay);
			SourceData.put("C_PAYMENT_DECLARE", listPay);
			Datas datas =new Datas();
			datas.remove("C_PAYMENT_DECLARE");
			datas.MapToDatas("C_PAYMENT_DECLARE", SourceData,XmlPayMapping);
			//转换CBE_CODE
			String CBE_CODE=datas.GetTableValue("C_PAYMENT_DECLARE","E_BUSINESS_COMPANY_CODE");
			datas.SetTableValue("C_PAYMENT_DECLARE","E_BUSINESS_COMPANY_CODE", SysUtility.getStrByLength(turnCbeToEnt(CBE_CODE), 20));	
			//转换ECP_CODE
			String ECP_CODE=datas.GetTableValue("C_PAYMENT_DECLARE","E_BUSINESS_PLATFORM_CODE");
			datas.SetTableValue("C_PAYMENT_DECLARE","E_BUSINESS_PLATFORM_CODE", SysUtility.getStrByLength(turnCbeToEnt(ECP_CODE), 20));	
			//转换ENT_CODE
			String ENT_CODE=datas.GetTableValue("C_PAYMENT_DECLARE","DECL_REG_NO");
			datas.SetTableValue("C_PAYMENT_DECLARE","DECL_REG_NO", SysUtility.getStrByLength(turnCbeToEnt(ENT_CODE), 20));	
			//赋默认值
			datas.SetTableValue("C_PAYMENT_DECLARE", "MONITOR_DECL_FLAG", "N");
			//2、插入C表，更改主表状态
			datas.InsertDB(DataAccess, "C_PAYMENT_DECLARE", "C_PAYMENT_DECLARE", "PAY_ID");
			String MESSAGE_SOURCE=datas.GetTableValue("C_PAYMENT_DECLARE", "ORG_CODE");
			String MESSAGE_DEST=SysUtility.getStrByLength(MESSAGE_SOURCE, 2)+"0000";
			//3、插入任务表
			SaveCbecSendTable(DataAccess, "RE_PAY_CHECK_BUREAU", datas.GetTableValue("C_PAYMENT_DECLARE", "PAY_ID"), MESSAGE_SOURCE, MESSAGE_DEST);
			String	sqlOrders = "UPDATE T_PAYMENT SET MSG_FLAG='1' WHERE INDX=?";
			DataAccess.ExecSQL(sqlOrders, INDX);
			DataAccess.ComitTrans();
		} catch (Exception e) {
			DataAccess.RoolbackTrans();
			String sql="UPDATE T_PAYMENT SET MSG_FLAG='2' WHERE INDX=?";
			DataAccess.ExecSQL(sql, INDX);
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	
	//现场备案号转总局备案号
	public static String turnCbeToEnt(String DECL_REG_NO) throws Exception{
		 String entCode=DECL_REG_NO;
		 if(SysUtility.isNotEmpty(entCode)){
				SQLBuild sqlmain= SQLBuild.getInstance();
				sqlmain.append("SELECT ENT_CBEC_NO FROM  T_ENT_REG  WHERE ENT_REG_NO='"+entCode+"' OR ENT_CBEC_NO='"+entCode+"' and rownum=1");
				Map mp=sqlmain.query4Map();
				entCode= (String)mp.get("ENT_CBEC_NO");
		 }
		 return SysUtility.processNullString(entCode);
	}
	//任务申报
	public static void SaveCbecSendTable(IDataAccess DataAccess,String MSG_TYPE,String MSG_NO,String MESSAGE_SOURCE,String MESSAGE_DEST) throws JSONException, LegendException{
		JSONObject json = new JSONObject();
		json.put("MSG_TYPE", MSG_TYPE);
		json.put("MSG_NO", MSG_NO);
		json.put("PART_ID", SysUtility.getCurrentOrgId());
		json.put("MESSAGE_SOURCE", MESSAGE_SOURCE);
		json.put("MESSAGE_DEST", MESSAGE_DEST);
		DataAccess.Insert("exs_handle_sender", json,"INDX");		 
	}
	//DATA_SOURCE为2时回执数据
	public static void SaveRSP(IDataAccess DataAccess,String CHECKLIST_NO,String MESSAGE_TYPE,String MESSAGE_DESC,String ORDER_NO,String INVENTORY_FLAG,String ORDER_ID,String MESSAGE_SOURCE,String MESSAGE_DEST,String EXS) throws Exception{
		JSONObject json = new JSONObject();
		String RSP_ID=SysUtility.GetUUID();
		json.put("RSP_ID", RSP_ID);
		json.put("MESSAGE_TIME", SysUtility.getSysDate());
		json.put("CHECKLIST_NO", CHECKLIST_NO);
		json.put("MESSAGE_TYPE", MESSAGE_TYPE);
		json.put("MESSAGE_DESC", MESSAGE_DESC);
		json.put("ORDER_NO", ORDER_NO);
		json.put("INVENTORY_FLAG",INVENTORY_FLAG);
		json.put("ORDER_ID", ORDER_ID);
		DataAccess.Insert("C_DECLARE_RECEIVE_RSP", json,"RSP_ID");
		//任务
		if("EXS".equals(EXS)){
			JSONObject ejson = new JSONObject();
			ejson.put("MSG_TYPE", "SEND_CHECKLIST_RSP");
			ejson.put("MSG_NO", RSP_ID);
			ejson.put("PART_ID", SysUtility.getCurrentOrgId());
			ejson.put("MESSAGE_SOURCE", MESSAGE_SOURCE);
			ejson.put("MESSAGE_DEST", MESSAGE_DEST);
			DataAccess.Insert("exs_handle_sender", ejson,"INDX");
			String upStatus="update C_DECLARE_RECEIVE_RSP set PROCESS_STATUS='1' where RSP_ID=?";
			DataAccess.ExecSQL(upStatus,RSP_ID);
		}
	
	}
} 


