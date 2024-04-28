package com.easy.app.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.entity.ServicesBean;
import com.easy.exception.ErrorCode;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.rule.FieldFormatUtil;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
import com.sun.mail.util.BASE64EncoderStream;

public class AppUtility {
	
	public static String EciqSaveToDB(MainServlet dataPage,HashMap SourceData)throws LegendException{
		return EciqSaveToDB(dataPage.getDataAccess(), SourceData);
	}
	
	public static String EciqSaveToDB(MainServlet dataPage,HashMap SourceData,boolean UpCodeName)throws LegendException{
		return EciqSaveToDB(dataPage.getDataAccess(), SourceData, true);
	}
	
	
	public static String EciqSaveToDB(IDataAccess DataAccess,HashMap SourceData)throws LegendException{
		return EciqSaveToDB(DataAccess, SourceData, true);
	}
	
	public static String EciqSaveToDB(IDataAccess DataAccess,HashMap SourceData,boolean UpCodeName){
		String rtMsg = "";
		try {
			Datas datas = new Datas();
			datas.remove("ITF_DCL_IO_DECL"); 
			datas.remove("ITF_DCL_IO_DECL_ATT"); 
			datas.remove("ITF_DCL_IO_DECL_GOODS"); 
			datas.remove("ITF_DCL_IO_DECL_GOODS_CONT"); 
			datas.remove("ITF_DCL_IO_DECL_GOODS_LIMIT"); 
			datas.remove("ITF_DCL_IO_DECL_GOODS_LIMIT_VN"); 
			datas.remove("ITF_DCL_IO_DECL_GOODS_PACK"); 
			datas.remove("ITF_DCL_IO_DECL_LIMIT"); 
			datas.remove("ITF_DCL_IO_DECL_USER"); 
			datas.remove("ITF_DCL_MARK_LOB"); 
			datas.remove("ITF_DCL_IO_DECL_CONT"); 
			datas.remove("ITF_DCL_IO_DECL_CONT_DETAIL"); 
			datas.remove("ITF_CIQ_PACK_APL"); 
			datas.remove("ITF_CIQ_PACK_DISPART"); 
			datas.remove("ITF_CIQ_PACK_MODEL"); 
			datas.remove("ITF_DCL_DEAD_DECL"); 
			datas.remove("ITF_DCL_DEAD_DECL_GOODS"); 
			datas.remove("ITF_DCL_CONT"); 
			datas.remove("ITF_DCL_CONT_DETAIL"); 
			datas.remove("ITF_DCL_CONT_INSP_LOB"); 
			datas.remove("ITF_DCL_CONT_LIST_LOB"); 
			datas.remove("ITF_DCL_CONT_OTHER_LOB"); 
			datas.remove("DETAIL"); 
			datas.MapToDatas("ITF_DCL_IO_DECL",SourceData,XmlDeclMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_ATT",SourceData,XmlDeclAttMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_GOODS",SourceData,XmlDeclGoodsMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_GOODS_CONT",SourceData,XmlDeclGoodsContMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_GOODS_LIMIT",SourceData,XmlDeclGoodsLimitMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_GOODS_LIMIT_VN",SourceData,XmlDeclGoodsLimitVnMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_GOODS_PACK",SourceData,XmlDeclGoodsPackMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_LIMIT",SourceData,XmlDeclLimitMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_USER",SourceData,XmlDeclUserMap);
			datas.MapToDatas("ITF_DCL_MARK_LOB",SourceData,XmlDeclLobMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_CONT",SourceData,XmlDeclContMap);
			datas.MapToDatas("ITF_DCL_IO_DECL_CONT_DETAIL",SourceData,XmlDeclContDetailMap); 
			datas.MapToDatas("ITF_CIQ_PACK_APL",SourceData,XmlPackAplMap);
			datas.MapToDatas("ITF_CIQ_PACK_DISPART",SourceData,XmlPackDispartMap);
			datas.MapToDatas("ITF_CIQ_PACK_MODEL",SourceData,XmlPackModelMap); 
			datas.MapToDatas("ITF_DCL_DEAD_DECL",SourceData,XmlDeadDeclMap);
			datas.MapToDatas("ITF_DCL_DEAD_DECL_GOODS",SourceData,XmlDeadDeclGoodsMap); 
			datas.MapToDatas("ITF_DCL_CONT",SourceData,XmlContMap);
			datas.MapToDatas("ITF_DCL_CONT_DETAIL",SourceData,XmlContDetailMap);
			datas.MapToDatas("ITF_DCL_CONT_INSP_LOB",SourceData,XmlContInspLobMap);
			datas.MapToDatas("ITF_DCL_CONT_LIST_LOB",SourceData,XmlContListLobMap);
			datas.MapToDatas("ITF_DCL_CONT_OTHER_LOB",SourceData,XmlContOtherLobMap); 
			datas.MapToDatas("DETAIL",SourceData,XmlStationMap); 
			
			/*************删除已存在的数据！**************/
			final String declNo = datas.GetTableValue("ITF_DCL_IO_DECL", "DECL_NO");
			String rt = SQLExecUtils.query4String("select 0 from ITF_DCL_IO_DECL where DECL_NO = ?",new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, declNo);
				}
			});
			if(SysUtility.isNotEmpty(rt)){
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL where decl_no ='"+declNo+"'");//业务主键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_GOODS where decl_no='"+declNo+"'");//外键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_GOODS_CONT where GOODS_ID in(select GOODS_ID from ITF_DCL_IO_DECL_GOODS where decl_no='"+declNo+"')");//外键GOODS_ID
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_GOODS_LIMIT where GOODS_ID in(select GOODS_ID from ITF_DCL_IO_DECL_GOODS where decl_no='"+declNo+"')");//外键GOODS_ID
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_GOODS_LIMIT_VN where LIMIT_ID in(select LIMIT_ID from ITF_DCL_IO_DECL_GOODS_LIMIT where decl_no='"+declNo+"')");//外键LIMIT_ID
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_GOODS_PACK where decl_no='"+declNo+"'");//外键GOODS_ID
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_CONT where decl_no = '"+declNo+"'");//外键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_CONT_DETAIL where decl_no='"+declNo+"'");//外键CONT_ID
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_ATT where decl_no='"+declNo+"'");//外键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_LIMIT where decl_no='"+declNo+"'");//外键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_IO_DECL_USER where decl_no='"+declNo+"'");//外键DECL_NO
				SQLExecUtils.executeUpdate("delete ITF_DCL_MARK_LOB where decl_no='"+declNo+"'");//外键DECL_NO
			}
			
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL", "ITF_DCL_IO_DECL","DECL_NO");
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_GOODS", "ITF_DCL_IO_DECL_GOODS","GOODS_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_GOODS_CONT", "ITF_DCL_IO_DECL_GOODS_CONT","GOODS_CONT_ID");//外键GOODS_ID
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_GOODS_LIMIT", "ITF_DCL_IO_DECL_GOODS_LIMIT","LIMIT_ID");//外键GOODS_ID
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_GOODS_LIMIT_VN", "ITF_DCL_IO_DECL_GOODS_LIMIT_VN","LIMIT_VIN_ID");//外键LIMIT_ID
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_GOODS_PACK", "ITF_DCL_IO_DECL_GOODS_PACK","PACK_ID");//外键GOODS_ID
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_CONT", "ITF_DCL_IO_DECL_CONT","CONT_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_CONT_DETAIL", "ITF_DCL_IO_DECL_CONT_DETAIL","CONT_DT_ID");//外键CONT_ID
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_ATT", "ITF_DCL_IO_DECL_ATT","ATT_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_LIMIT", "ITF_DCL_IO_DECL_LIMIT","DECL_LIMIT_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_IO_DECL_USER", "ITF_DCL_IO_DECL_USER","USER_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_MARK_LOB", "ITF_DCL_MARK_LOB","MARK_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_CIQ_PACK_APL","ITF_CIQ_PACK_APL","DECL_NO");
			datas.InsertDB(DataAccess,"ITF_CIQ_PACK_DISPART","ITF_CIQ_PACK_DISPART","DISPART_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_CIQ_PACK_MODEL","ITF_CIQ_PACK_MODEL","MODEL_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_DEAD_DECL","ITF_DCL_DEAD_DECL","DECL_NO");
			datas.InsertDB(DataAccess,"ITF_DCL_DEAD_DECL_GOODS","ITF_DCL_DEAD_DECL_GOODS","DEAD_GOODS_ID");//外键DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_CONT","ITF_DCL_CONT","ENT_DECL_NO");
			datas.InsertDB(DataAccess,"ITF_DCL_CONT_DETAIL","ITF_DCL_CONT_DETAIL","CONT_DETAIL_ID");//外键CONT_DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_CONT_INSP_LOB","ITF_DCL_CONT_INSP_LOB","INSP_ID");//外键CONT_DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_CONT_LIST_LOB","ITF_DCL_CONT_LIST_LOB","LIST_ID");//外键CONT_DECL_NO
			datas.InsertDB(DataAccess,"ITF_DCL_CONT_OTHER_LOB","ITF_DCL_CONT_OTHER_LOB","OTHER_ID");//外键CONT_DECL_NO
			datas.InsertDB(DataAccess,"DETAIL","ITF_DCL_STATION","CONT_DETAIL_ID");
			
			if(UpCodeName){
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_GOODS");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_GOODS_CONT");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_GOODS_LIMIT");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_GOODS_LIMIT_VN");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_GOODS_PACK");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_CONT");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_CONT_DETAIL");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_ATT");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_LIMIT");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_IO_DECL_USER");
				UpdateCodeName(DataAccess,datas,"ITF_DCL_MARK_LOB");
			}
		} catch (LegendException e) {
			rtMsg = e.getMessage();
		}
		return rtMsg;
	}
	
	
	public static void UpdateCodeName(IDataAccess DataAccess,Datas datas,String TableName)throws LegendException{
		String aplKind =  datas.GetTableValue("ITF_DCL_IO_DECL", "APL_KIND");
		
		try {
			StringBuffer UpdateSQL = new StringBuffer();
			if("ITF_DCL_IO_DECL".equals(TableName)){
				UpdateSQL.append(" UPDATE ITF_DCL_IO_DECL SET");
				UpdateSQL.append(" INSP_ORG_NAME=(select ITEM_NAME  FROM S_ECIQ_ORGANIZE WHERE item_code=INSP_ORG_CODE AND rownum=1),");
				UpdateSQL.append(" ORG_NAME=(select ITEM_NAME  FROM S_ECIQ_ORGANIZE WHERE item_code=ORG_CODE AND rownum=1),");
				UpdateSQL.append(" VSA_ORG_NAME=(select ITEM_NAME  FROM S_ECIQ_ORGANIZE WHERE item_code=VSA_ORG_CODE AND rownum=1),");
				UpdateSQL.append(" PURP_ORG_NAME=(select ITEM_NAME  FROM S_ECIQ_ORGANIZE WHERE item_code=PURP_ORG_CODE AND rownum=1),");
				UpdateSQL.append(" TRANS_MODE_NAME=(select ITEM_NAME  FROM S_ECIQ_TRANS_MODE WHERE item_code=TRANS_MODE_CODE AND rownum=1),");
				UpdateSQL.append(" TRADE_MODE_NAME=(select ITEM_NAME  FROM S_ECIQ_TRADE_MODE WHERE item_code=TRADE_MODE_CODE AND rownum=1),");
				UpdateSQL.append(" DESP_CTRY_NAME=(select ITEM_NAME  FROM S_ECIQ_COUNTRYCODE WHERE item_code=DESP_CTRY_CODE AND rownum=1),");
				UpdateSQL.append(" DESP_PORT_NAME=(select ITEM_NAME  FROM  S_ECIQ_PORTCODE WHERE item_code=DESP_PORT_CODE AND rownum=1),");
				UpdateSQL.append(" TRADE_COUNTRY_NAME=(select ITEM_NAME  FROM  S_ECIQ_COUNTRYCODE WHERE item_code=TRADE_COUNTRY_CODE AND rownum=1),");
				UpdateSQL.append(" PORT_STOP_NAME=(select ITEM_NAME  FROM  S_ECIQ_PORTCODE WHERE item_code=PORT_STOP_CODE AND rownum=1), ");
				UpdateSQL.append(" ENTY_PORT_NAME=(select ITEM_NAME  FROM  S_ECIQ_PORTCODE WHERE item_code=ENTY_PORT_CODE AND rownum=1),");
				UpdateSQL.append(" DECL_CUSTM_NAME=(select ITEM_NAME  FROM  S_ECIQ_DECLCUSTM WHERE item_code=DECL_CUSTM AND rownum=1),");
				UpdateSQL.append(" DEST_NAME=(select ITEM_NAME  FROM  S_ECIQ_CHINAADMINAREAS WHERE item_code=DEST_CODE AND rownum=1),");
				if("O".equals(datas.GetTableValue("ITF_DCL_IO_DECL", "APL_KIND"))){
					UpdateSQL.append(" ARRIV_PORT_NAME=(select ITEM_NAME  FROM  S_ECIQ_WORLDPORT WHERE item_code=ARRIV_PORT_CODE AND rownum=1)");
				}else{
					UpdateSQL.append(" ARRIV_PORT_NAME=(select ITEM_NAME  FROM  S_ECIQ_PORTCODE WHERE item_code=ARRIV_PORT_CODE AND rownum=1)");
				}	
				UpdateSQL.append(" where DECL_NO = ?");
				DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL", "DECL_NO")}); 
			}else if("ITF_DCL_IO_DECL_GOODS".equals(TableName)){
				 for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_GOODS"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_GOODS set ");
					 UpdateSQL.append(" ORI_CTRY_NAME=(select ITEM_NAME  FROM  S_ECIQ_COUNTRYCODE WHERE item_code=ORI_CTRY_CODE AND rownum=1),");
					 UpdateSQL.append(" GOODS_ATTR_NAME=(select ITEM_NAME  FROM  S_ECIQ_GOODS_ATTR WHERE item_code=GOODS_ATTR AND rownum=1),");
					 UpdateSQL.append(" QTY_MEAS_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_QTY_UNIT WHERE item_code=QTY_MEAS_UNIT AND rownum=1),");
					 UpdateSQL.append(" WT_MEAS_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_WEIGHT_UNIT WHERE item_code=WT_MEAS_UNIT AND rownum=1),");
					 UpdateSQL.append(" STD_QTY_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_QTY_UNIT WHERE item_code=STD_QTY_UNIT_CODE AND rownum=1),");
					 UpdateSQL.append(" STD_WEIGHT_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_WEIGHT_UNIT WHERE item_code=STD_WEIGHT_UNIT_CODE AND rownum=1),");
					 UpdateSQL.append(" CURRENCY_NAME=(select ITEM_NAME  FROM  S_ECIQ_CURRENCY WHERE item_code=CURRENCY AND rownum=1),");
					 UpdateSQL.append(" PURPOSE_NAME=(select ITEM_NAME  FROM  S_ECIQ_COMMODITYUSAGE WHERE item_code=PURPOSE AND rownum=1),");
					 if("O".equals(aplKind))
					 {
						 UpdateSQL.append(" ARRIV_PORT_NAME=(select ITEM_NAME  FROM  S_ECIQ_WORLDPORT WHERE item_code=ARRIV_PORT_CODE AND rownum=1)"); 
					 }else
					 {
						 UpdateSQL.append(" ORIG_PLACE_NAME=(select ITEM_NAME  FROM S_ECIQ_ORG_AREA WHERE item_code=ORIG_PLACE_CODE AND rownum=1)");
					 }	 
					 UpdateSQL.append(" where GOODS_ID = ?");
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_GOODS", "GOODS_ID",i)});
				} 
			}else if("ITF_DCL_IO_DECL_GOODS_CONT".equals(TableName)){
				 for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_GOODS_CONT"); i++) {
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_GOODS_CONT set ");   
					 UpdateSQL.append(" CNTNR_MODE_NAME=(select ITEM_NAME  FROM  S_ECIQ_CONTAINERSPECCODE WHERE item_code=CNTNR_MODE_CODE AND rownum=1),");
					 UpdateSQL.append(" QTY_MEAS_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_QTY_UNIT WHERE item_code=QTY_MEAS_UNIT AND rownum=1),");
					 UpdateSQL.append(" WT_UNIT_CODE_NAME=(select ITEM_NAME  FROM  S_ECIQ_WEIGHT_UNIT WHERE item_code=WT_UNIT_CODE AND rownum=1),");
					 UpdateSQL.append(" STD_MEAS_UNIT_NAME=(select ITEM_NAME  FROM  S_ECIQ_QTY_UNIT WHERE item_code=STD_MEAS_UNIT AND rownum=1),");
					 UpdateSQL.append(" TRANS_MEANS_TYPE_NAME=(select ITEM_NAME  FROM  S_ECIQ_TRANS_TYPE WHERE item_code=TRANS_MEANS_TYPE AND rownum=1)");
					 UpdateSQL.append(" where GOODS_CONT_ID= ?");   
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_GOODS_CONT", "GOODS_CONT_ID",i)});
				} 
			}else if("ITF_DCL_IO_DECL_GOODS_LIMIT".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_GOODS_LIMIT"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_GOODS_LIMIT set ");  
					 if("O".equals(aplKind))
					 {
						 UpdateSQL.append(" LIC_NAME=(select ITEM_NAME  FROM  S_ECIQ_OPERMIT WHERE item_code=LIC_TYPE_CODE AND rownum=1)"); 
					 }else
					 {
						 UpdateSQL.append(" LIC_NAME=(select ITEM_NAME  FROM  S_ECIQ_PERMIT WHERE item_code=LIC_TYPE_CODE AND rownum=1)"); 
					 }		 
					 UpdateSQL.append(" where LIMIT_ID= ?");   
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_GOODS_LIMIT", "LIMIT_ID",i)});
				}  
			}else if("ITF_DCL_IO_DECL_GOODS_LIMIT_VN".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_GOODS_LIMIT_VN"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_GOODS_LIMIT_VN set ");  
					 if("O".equals(aplKind))
					 {
						 UpdateSQL.append(" LIC_NAME=(select ITEM_NAME  FROM  S_ECIQ_OPERMIT WHERE item_code=LIC_TYPE_CODE AND rownum=1)"); 
					 }else
					 {
						 UpdateSQL.append(" LIC_NAME=(select ITEM_NAME  FROM  S_ECIQ_PERMIT WHERE item_code=LIC_TYPE_CODE AND rownum=1)"); 
					 }		
					 UpdateSQL.append(" where LIMIT_VIN_ID= ?");   
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_GOODS_LIMIT_VN", "LIMIT_VIN_ID",i)});
				} 
			}else if("ITF_DCL_IO_DECL_GOODS_PACK".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_GOODS_PACK"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_GOODS_PACK set ");  
					 UpdateSQL.append(" PACK_CATG_NAME=(select ITEM_NAME  FROM  S_ECIQ_PACKAGETYPE WHERE item_code=PACK_TYPE_CODE AND rownum=1)"); 
					 UpdateSQL.append(" where PACK_ID= ?");  
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_GOODS_PACK", "PACK_ID",i)});
				} 
			}else if("ITF_DCL_IO_DECL_ATT".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_ATT"); i++) {
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("UPDATE ITF_DCL_IO_DECL_ATT set ");
					 if("O".equals(aplKind)){
						 UpdateSQL.append(" ATT_DOC_NAME=(select ITEM_NAME  FROM  S_ECIQ_OSYSSHEET WHERE item_code=ATT_DOC_TYPE_CODE AND rownum=1)");
					 }else{
						 UpdateSQL.append(" ATT_DOC_NAME=(select ITEM_NAME  FROM  S_ECIQ_SYSSHEET WHERE item_code=ATT_DOC_TYPE_CODE AND rownum=1)");
					 }		
					 UpdateSQL.append(" where ATT_ID = ?");
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_ATT", "ATT_ID",i)});
				} 
			}else if("ITF_DCL_IO_DECL_CONT".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_CONT"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_CONT set ");   
					 UpdateSQL.append(" CNTNR_MODE_NAME=(select ITEM_NAME  FROM  S_ECIQ_CONTAINERSPECCODE WHERE item_code=CNTNR_MODE_CODE AND rownum=1)");
					 UpdateSQL.append(" where CONT_ID= ?"); 
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_CONT", "CONT_ID",i)});
				}
			}else if("ITF_DCL_IO_DECL_CONT_DETAIL".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_CONT_DETAIL"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_CONT_DETAIL set ");  
					 UpdateSQL.append(" CNTNR_MODE_NAME=(select ITEM_NAME  FROM  S_ECIQ_CONTAINERSPECCODE WHERE item_code=CNTNR_MODE_CODE AND rownum=1)"); 
					 UpdateSQL.append(" where CONT_DT_ID=?");
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_CONT_DETAIL", "CONT_DT_ID",i)});
				}
			}else if("ITF_DCL_IO_DECL_LIMIT".equals(TableName)){
				for (int i = 0; i < datas.GetTableRows("ITF_DCL_IO_DECL_LIMIT"); i++) { 
					 UpdateSQL.setLength(0);
					 UpdateSQL.append("update ITF_DCL_IO_DECL_LIMIT set ");  
					 if("O".equals(aplKind))
					 {
						 UpdateSQL.append(" ENT_QUALIF_NAME=(select ITEM_NAME  FROM  S_ECIQ_OQUALIFICATION WHERE item_code=ENT_QUALIF_TYPE_CODE AND rownum=1)"); 
					 }else{
						 UpdateSQL.append(" ENT_QUALIF_NAME=(select ITEM_NAME  FROM  S_ECIQ_QUALIFICATION WHERE item_code=ENT_QUALIF_TYPE_CODE AND rownum=1)"); 
					 }
					 UpdateSQL.append(" where DECL_LIMIT_ID=?");
					 DataAccess.ExecSQL(UpdateSQL.toString(),new Object[]{datas.GetTableValue("ITF_DCL_IO_DECL_LIMIT", "DECL_LIMIT_ID",i)});
				}
			}else if("ITF_DCL_IO_DECL_USER".equals(TableName)){
				
			}else if("ITF_DCL_MARK_LOB".equals(TableName)){
				
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	
	public static HashMap<String,String> XmlDeclMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclGoodsMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclGoodsContMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclGoodsLimitMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclGoodsLimitVnMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclGoodsPackMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclAttMap=new HashMap<String,String>();

	public static HashMap<String,String> XmlDeclLimitMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclUserMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclLobMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclContMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeclContDetailMap=new HashMap<String,String>();

	public static HashMap<String,String> XmlPackAplMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlPackDispartMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlPackModelMap=new HashMap<String,String>();
	
	public static HashMap<String,String> XmlDeadDeclMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlDeadDeclGoodsMap=new HashMap<String,String>();
	
	public static HashMap<String,String> XmlContMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlContDetailMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlContInspLobMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlContListLobMap=new HashMap<String,String>();
	public static HashMap<String,String> XmlContOtherLobMap=new HashMap<String,String>();
	
	public static HashMap<String,String> XmlStationMap=new HashMap<String,String>();
	
	public static HashMap<String,String> XmlDeclReceiptsMap=new HashMap<String,String>();
	
	public static HashMap<String,String> DBTableName = new HashMap<String,String>();
	
	static{
		XmlDeclMap.put("techRegCode","TECH_REG_CODE");
		XmlDeclMap.put("EntUuid","DECL_ID");
		XmlDeclMap.put("EntDeclNo","DECL_NO");
		XmlDeclMap.put("TradeModeCode","TRADE_MODE_CODE");
		XmlDeclMap.put("ContractNo","CONTRACT_NO");
		XmlDeclMap.put("MarkNo","MARK_NO");
		XmlDeclMap.put("TradeCountryCode","TRADE_COUNTRY_CODE");
		XmlDeclMap.put("DespCtryCode","DESP_CTRY_CODE");
		XmlDeclMap.put("TransModeCode","TRANS_MODE_CODE");
		XmlDeclMap.put("ConvynceName","CONVYNCE_NAME");
		XmlDeclMap.put("TransMeanNo","TRANS_MEAN_NO");
		XmlDeclMap.put("DespPortCode","DESP_PORT_CODE");
		XmlDeclMap.put("PortStopCode","PORT_STOP_CODE");
		XmlDeclMap.put("EntyPortCode","ENTY_PORT_CODE");
		XmlDeclMap.put("GdsArvlDate","GDS_ARVL_DATE");
		XmlDeclMap.put("CmplDschrgDt","CMPL_DSCHRG_DT");
		XmlDeclMap.put("GoodsPlace","GOODS_PLACE");
		XmlDeclMap.put("DestCode","DEST_CODE");
		XmlDeclMap.put("CounterClaim","COUNTER_CLAIM");
		XmlDeclMap.put("BillLadNo","BILL_LAD_NO");
		XmlDeclMap.put("DeliveryOrder","DELIVERY_ORDER");
		XmlDeclMap.put("InspOrgCode","INSP_ORG_CODE");
		XmlDeclMap.put("ExcInspDeptCode","EXC_INSP_DEPT_CODE");
		XmlDeclMap.put("DeclCustm","DECL_CUSTM");
		XmlDeclMap.put("SpecDeclFlag","SPEC_DECL_FLAG");
		XmlDeclMap.put("PurpOrgCode","PURP_ORG_CODE");
		XmlDeclMap.put("CorrelationDeclNo","CORRELATION_DECL_NO");
		XmlDeclMap.put("CorrelationReasonFlag","CORRELATION_REASON_FLAG");
		XmlDeclMap.put("SpeclInspQuraRe","SPECL_INSP_QURA_RE");
		XmlDeclMap.put("AppCertCode","APP_CERT_CODE");
		XmlDeclMap.put("ApplOri","APPL_ORI");
		XmlDeclMap.put("ApplCopyQuan","APPL_COPY_QUAN");
		XmlDeclMap.put("CustmRegNo","CUSTM_REG_NO");
		XmlDeclMap.put("DeclPersnCertNo","DECL_PERSN_CERT_NO");
		XmlDeclMap.put("DeclPersonName","DECL_PERSON_NAME");
		XmlDeclMap.put("DeclRegName","DECL_REG_NAME");
		XmlDeclMap.put("Contactperson","CONTACTPERSON");
		XmlDeclMap.put("ContTel","CONT_TEL");
		XmlDeclMap.put("ConsigneeCode","CONSIGNEE_CODE");
		XmlDeclMap.put("ConsigneeCname","CONSIGNEE_CNAME");
		XmlDeclMap.put("ConsigneeEname","CONSIGNEE_ENAME");
		XmlDeclMap.put("ConsignorCname","CONSIGNOR_CNAME");
		XmlDeclMap.put("ConsignorAddr","CONSIGNOR_ADDR");
		XmlDeclMap.put("DeclCode","DECL_CODE");
		XmlDeclMap.put("DeclDate","DECL_DATE");
		XmlDeclMap.put("DeclGetNo","DECL_GET_NO");
		XmlDeclMap.put("SpecPassFlag","SPEC_PASS_FLAG");
		XmlDeclMap.put("DespDate","DESP_DATE");
		XmlDeclMap.put("ArrivPortCode","ARRIV_PORT_CODE");
		XmlDeclMap.put("ConsigneeAddr","CONSIGNEE_ADDR");
		XmlDeclMap.put("ConsignorCode","CONSIGNOR_CODE");
		XmlDeclMap.put("AttaCollectName","ATTA_COLLECT_NAME");
		XmlDeclMap.put("IsListGood","IS_LIST_GOOD");
		XmlDeclMap.put("IsCont","IS_CONT");
		XmlDeclMap.put("FfjFlag","FFJ_FLAG");
		XmlDeclMap.put("FfjStatus","FFJ_STATUS");
		XmlDeclMap.put("ResendNum","RESEND_NUM");
		XmlDeclMap.put("IsDraw","IS_DRAW");
		XmlDeclMap.put("TotalValUs","TOTAL_VAL_US");
		XmlDeclMap.put("TotalValCn","TOTAL_VAL_CN");
		XmlDeclMap.put("ContCancelFlag","CONT_CANCEL_FLAG");
		XmlDeclMap.put("FeeHandleState","FEE_HANDLE_STATE");
		XmlDeclMap.put("RelsState","RELS_STATE");
		XmlDeclMap.put("FlgPortInland","FLG_PORT_INLAND");
		XmlDeclMap.put("EnableTransFlag","ENABLE_TRANS_FLAG");
		XmlDeclMap.put("FalgArchive","FALG_ARCHIVE");
		XmlDeclMap.put("SituationCode","SITUATION_CODE");
		XmlDeclMap.put("SituationLevel","SITUATION_LEVEL");
		XmlDeclMap.put("ProcessStatus","PROCESS_STATUS");
		XmlDeclMap.put("OperCode","OPER_CODE");
		XmlDeclMap.put("OperTime","OPER_TIME");
		XmlDeclMap.put("AppCertName","APP_CERT_NAME");
		XmlDeclMap.put("DeclRegNo","DECL_REG_NO");
		XmlDeclMap.put("ProcessLink","PROCESS_LINK");
		XmlDeclMap.put("OrgCode","ORG_CODE");
		XmlDeclMap.put("CertCancelFlag","CERT_CANCEL_FLAG");
		XmlDeclMap.put("ConsignorEname","CONSIGNOR_ENAME");
		XmlDeclMap.put("ArchiveTime","ARCHIVE_TIME");
		XmlDeclMap.put("TransFlag","TRANS_FLAG");
		XmlDeclMap.put("TransBatch","TRANS_BATCH");
		XmlDeclMap.put("IsMove","IS_MOVE");
		XmlDeclMap.put("SplitBillLadNo","SPLIT_BILL_LAD_NO");
		XmlDeclMap.put("DeclWorkNo","DECL_WORK_NO");
		XmlDeclMap.put("AplKind","APL_KIND");
		XmlDeclMap.put("SoftType","SOFT_TYPE");
		XmlDeclMap.put("spare1","SPARE1");
		XmlDeclMap.put("spare2","SPARE2");
		XmlDeclMap.put("spare3","SPARE3");
		XmlDeclMap.put("spare4","SPARE4");  
				
		XmlDeclGoodsMap.put("GoodsId","GOODS_ID");  
		XmlDeclGoodsMap.put("EntDeclNo","DECL_NO");  
		XmlDeclGoodsMap.put("GoodsNo","GOODS_NO");  
		XmlDeclGoodsMap.put("ProdHsCode","PROD_HS_CODE");  
		XmlDeclGoodsMap.put("HsCodeDesc","HS_CODE_DESC");  
		XmlDeclGoodsMap.put("InspType","INSP_TYPE");  
		XmlDeclGoodsMap.put("CiqCode","CIQ_CODE");  
		XmlDeclGoodsMap.put("CiqName","CIQ_NAME");  
		XmlDeclGoodsMap.put("DeclGoodsCname","DECL_GOODS_CNAME");  
		XmlDeclGoodsMap.put("DeclGoodsEname","DECL_GOODS_ENAME");  
		XmlDeclGoodsMap.put("Qty","QTY");  
		XmlDeclGoodsMap.put("QtyMeasUnit","QTY_MEAS_UNIT");  
		XmlDeclGoodsMap.put("Weight","WEIGHT");  
		XmlDeclGoodsMap.put("WtMeasUnit","WT_MEAS_UNIT");  
		XmlDeclGoodsMap.put("StdQty","STD_QTY");  
		XmlDeclGoodsMap.put("GoodsTotalVal","GOODS_TOTAL_VAL");  
		XmlDeclGoodsMap.put("Currency","CURRENCY");  
		XmlDeclGoodsMap.put("PricePerUnit","PRICE_PER_UNIT");  
		XmlDeclGoodsMap.put("GoodsSpec","GOODS_SPEC");  
		XmlDeclGoodsMap.put("GoodsModel","GOODS_MODEL");  
		XmlDeclGoodsMap.put("GoodsBrand","GOODS_BRAND");  
		XmlDeclGoodsMap.put("OriCtryCode","ORI_CTRY_CODE");  
		XmlDeclGoodsMap.put("OrigPlaceCode","ORIG_PLACE_CODE");  
		XmlDeclGoodsMap.put("Purpose","PURPOSE");  
		XmlDeclGoodsMap.put("ProduceDate","PRODUCE_DATE");  
		XmlDeclGoodsMap.put("ProdBatchNo","PROD_BATCH_NO");  
		XmlDeclGoodsMap.put("ProdValidDt","PROD_VALID_DT");  
		XmlDeclGoodsMap.put("ProdQgp","PROD_QGP");  
		XmlDeclGoodsMap.put("GoodsAttr","GOODS_ATTR");  
		XmlDeclGoodsMap.put("Stuff","STUFF");  
		XmlDeclGoodsMap.put("UnCode","UN_CODE");  
		XmlDeclGoodsMap.put("DangName","DANG_NAME");  
		XmlDeclGoodsMap.put("PackType","PACK_TYPE");  
		XmlDeclGoodsMap.put("PackSpec","PACK_SPEC");  
		XmlDeclGoodsMap.put("MnufctrRegNo","MNUFCTR_REG_NO");  
		XmlDeclGoodsMap.put("By1","BY1");  
		XmlDeclGoodsMap.put("By2","BY2");  
		XmlDeclGoodsMap.put("EngManEntCnm","ENG_MAN_ENT_CNM");  
		XmlDeclGoodsMap.put("StdQtyUnitCode","STD_QTY_UNIT_CODE");  
		XmlDeclGoodsMap.put("StdWeight","STD_WEIGHT");  
		XmlDeclGoodsMap.put("StdWeightUnitCode","STD_WEIGHT_UNIT_CODE");  
		XmlDeclGoodsMap.put("MnufctrRegName","ENG_MAN_ENT_CNM");  
		XmlDeclGoodsMap.put("NoDangFlag","NO_DANG_FLAG");  		
		
		XmlDeclGoodsContMap.put("GoodsContId","GOODS_CONT_ID");  
		XmlDeclGoodsContMap.put("GoodsId","GOODS_ID");  
		XmlDeclGoodsContMap.put("EntDeclNo","DECL_NO");  
		XmlDeclGoodsContMap.put("GoodsNo","GOODS_NO");  
		XmlDeclGoodsContMap.put("ContCode","CONT_CODE");  
		XmlDeclGoodsContMap.put("ProdHsCode","PROD_HS_CODE");  
		XmlDeclGoodsContMap.put("TransMeansType","TRANS_MEANS_TYPE");  
		XmlDeclGoodsContMap.put("CntnrModeCode","CNTNR_MODE_CODE");  
		XmlDeclGoodsContMap.put("Qty","QTY");  
		XmlDeclGoodsContMap.put("QtyMeasUnit","QTY_MEAS_UNIT");  
		XmlDeclGoodsContMap.put("StdMeasUnitQyt","STD_MEAS_UNIT_QYT");  
		XmlDeclGoodsContMap.put("StdMeasUnit","STD_MEAS_UNIT");  
		XmlDeclGoodsContMap.put("Weight","WEIGHT");  
		XmlDeclGoodsContMap.put("WtUnitCode","WT_UNIT_CODE");  
		
		XmlDeclGoodsLimitMap.put("LimitId","LIMIT_ID");  
		XmlDeclGoodsLimitMap.put("GoodsId","GOODS_ID");  
		XmlDeclGoodsLimitMap.put("EntDeclNo","DECL_NO");  
		XmlDeclGoodsLimitMap.put("GoodsNo","GOODS_NO");  
		XmlDeclGoodsLimitMap.put("LicTypeCode","LIC_TYPE_CODE");  
		XmlDeclGoodsLimitMap.put("LicenceNo","LICENCE_NO");  
		XmlDeclGoodsLimitMap.put("LicName","LIC_NAME");  
		XmlDeclGoodsLimitMap.put("LicWrtofDetailNo","LIC_WRTOF_DETAIL_NO");  
		XmlDeclGoodsLimitMap.put("LicWrtofQty","LIC_WRTOF_QTY");  
		XmlDeclGoodsLimitMap.put("LicDetailLeft","LIC_DETAIL_LEFT");  
		XmlDeclGoodsLimitMap.put("LicWrtofLeft","LIC_WRTOF_LEFT"); 
		
		XmlDeclGoodsPackMap.put("PackId","PACK_ID");  
		XmlDeclGoodsPackMap.put("GoodsId","GOODS_ID");  
		XmlDeclGoodsPackMap.put("EntDeclNo","DECL_NO");  
		XmlDeclGoodsPackMap.put("GoodsNo","GOODS_NO");  
		XmlDeclGoodsPackMap.put("PackLenth","PACK_LENTH");  
		XmlDeclGoodsPackMap.put("PackHigh","PACK_HIGH");  
		XmlDeclGoodsPackMap.put("PackWide","PACK_WIDE");  
		XmlDeclGoodsPackMap.put("PackTypeCode","PACK_TYPE_CODE");  
		XmlDeclGoodsPackMap.put("PackCatgName","PACK_CATG_NAME");  
		XmlDeclGoodsPackMap.put("PackTypeShort","PACK_TYPE_SHORT");  
		XmlDeclGoodsPackMap.put("PackQty","PACK_QTY");  
		XmlDeclGoodsPackMap.put("IsMainPack","IS_MAIN_PACK");  
		XmlDeclGoodsPackMap.put("MatType","MAT_TYPE");  
		XmlDeclGoodsPackMap.put("ProcFactory","PROC_FACTORY");  

		
		XmlDeclAttMap.put("AttId","ATT_ID");  
		XmlDeclAttMap.put("EntDeclNo","DECL_NO");  
		XmlDeclAttMap.put("AttDocTypeCode","ATT_DOC_TYPE_CODE");  
		XmlDeclAttMap.put("AttDocNo","ATT_DOC_NO");  
		XmlDeclAttMap.put("AttDocName","ATT_DOC_NAME");  
		XmlDeclAttMap.put("AttDocWrtofDetailNo","ATT_DOC_WRTOF_DETAIL_NO");  
		XmlDeclAttMap.put("AttDocWrtofQty","ATT_DOC_WRTOF_QTY");  
		XmlDeclAttMap.put("AttDocDetailLeft","ATT_DOC_DETAIL_LEFT");  
		XmlDeclAttMap.put("AttDocWrtofLeft","ATT_DOC_WRTOF_LEFT");  		
		
		XmlDeclLimitMap.put("DeclLimitId","DECL_LIMIT_ID"); 
		XmlDeclLimitMap.put("EntDeclNo","DECL_NO"); 
		XmlDeclLimitMap.put("EntQualifNo","ENT_QUALIF_NO"); 
		XmlDeclLimitMap.put("EntQualifTypeCode","ENT_QUALIF_TYPE_CODE"); 
		XmlDeclLimitMap.put("EntQualifName","ENT_QUALIF_NAME"); 
		XmlDeclLimitMap.put("EntOrgCode","ENT_ORG_CODE"); 
		XmlDeclLimitMap.put("OperTime","OPER_TIME"); 
		XmlDeclLimitMap.put("FalgArchive","FALG_ARCHIVE"); 
		XmlDeclLimitMap.put("EntName","ENT_NAME"); 
		 
		XmlDeclUserMap.put("UserId","USER_ID"); 
		XmlDeclUserMap.put("EntDeclNo","DECL_NO"); 
		XmlDeclUserMap.put("ConsumerUsrCode","CONSUMER_USR_CODE"); 
		XmlDeclUserMap.put("UserName","USER_NAME"); 
		XmlDeclUserMap.put("UseOrgPersonCode","USE_ORG_PERSON_CODE"); 
		XmlDeclUserMap.put("UseOrgPersonTel","USE_ORG_PERSON_TEL"); 
 
		XmlDeclLobMap.put("MarkId","MARK_ID"); 
		XmlDeclLobMap.put("EntDeclNo","DECL_NO"); 
		XmlDeclLobMap.put("AttachName","ATTACH_NAME"); 
		XmlDeclLobMap.put("AttachType","ATTACH_TYPE"); 
		XmlDeclLobMap.put("Attachment","ATTACHMENT"); 

		XmlDeclContMap.put("ContId","CONT_ID"); 
		XmlDeclContMap.put("EntDeclNo","DECL_NO"); 
		XmlDeclContMap.put("CntnrModeCode","CNTNR_MODE_CODE"); 
		XmlDeclContMap.put("ContainerQty","CONTAINER_QTY"); 
		XmlDeclContMap.put("LclFlag","LCL_FLAG"); 
		XmlDeclContMap.put("ContNo","CONT_NO"); 
		XmlDeclContMap.put("SeqNo","SEQ_NO"); 
		
		XmlDeclContDetailMap.put("ContDtId","CONT_DT_ID"); 
		XmlDeclContDetailMap.put("ContId","CONT_ID"); 
		XmlDeclContDetailMap.put("EntDeclNo","DECL_NO"); 
		XmlDeclContDetailMap.put("CntnrModeCode","CNTNR_MODE_CODE"); 
		XmlDeclContDetailMap.put("ContNo","CONT_NO"); 
		XmlDeclContDetailMap.put("LclFlag","LCL_FLAG"); 
		XmlDeclContDetailMap.put("SeqNo","SEQ_NO");  
		
		DBTableName.put("DeclAttHead", "ITF_DCL_IO_DECL_ATT");//2
		DBTableName.put("DeclGoodsHead", "ITF_DCL_IO_DECL_GOODS");//2
		DBTableName.put("DeclGoodsContHead", "ITF_DCL_IO_DECL_GOODS_CONT");//3
		DBTableName.put("DeclGoodsLimitHead", "ITF_DCL_IO_DECL_GOODS_LIMIT");//3
		DBTableName.put("DeclGoodsLimitVnHead", "ITF_DCL_IO_DECL_GOODS_LIMIT_VN");//4
		DBTableName.put("DeclGoodsPackHead", "ITF_DCL_IO_DECL_GOODS_PACK");//3
		DBTableName.put("DeclLimitHead", "ITF_DCL_IO_DECL_LIMIT");//2
		DBTableName.put("DeclUserHead", "ITF_DCL_IO_DECL_USER");//2
		DBTableName.put("DeclLobHead", "ITF_DCL_MARK_LOB");//2
		DBTableName.put("DeclContHead", "ITF_DCL_IO_DECL_CONT");//2
		DBTableName.put("DeclContDetailHead", "ITF_DCL_IO_DECL_CONT_DETAIL");//3 
				
		XmlDeclReceiptsMap.put("RspNo","RSP_NO");
		XmlDeclReceiptsMap.put("RspCodes","RSP_CODES");
		XmlDeclReceiptsMap.put("RspState","RSP_STATE");
		XmlDeclReceiptsMap.put("DeclNoType","DECL_NO_TYPE");
		XmlDeclReceiptsMap.put("DeclNo","DECL_NO");
		XmlDeclReceiptsMap.put("AddInfo","ADD_INFO");
		XmlDeclReceiptsMap.put("DeclRegNo","DECL_REG_NO");
		XmlDeclReceiptsMap.put("DeclGetNo","DECL_GET_NO");
		XmlDeclReceiptsMap.put("RspGenTime","RSP_GEN_TIME");
		XmlDeclReceiptsMap.put("RspSendTime","RSP_SEND_TIME");
		XmlDeclReceiptsMap.put("OperTime","OPER_TIME");
		XmlDeclReceiptsMap.put("RspKind","RSP_KIND");
		XmlDeclReceiptsMap.put("RspInfo","RSP_INFO");
		XmlDeclReceiptsMap.put("ResponseId","RESPONSE_ID");
		XmlDeclReceiptsMap.put("SenderAddr","SENDER_ADDR");
		XmlDeclReceiptsMap.put("EntUuid","ENT_UUID"); 

		XmlDeclGoodsLimitVnMap.put("LimitVinId","LIMIT_VIN_ID");
		XmlDeclGoodsLimitVnMap.put("LimitId","LIMIT_ID");
		XmlDeclGoodsLimitVnMap.put("EntDeclNo","ENT_DECL_NO");
		XmlDeclGoodsLimitVnMap.put("GoodsId","GOODS_ID");
		XmlDeclGoodsLimitVnMap.put("GoodsNo","GOODS_NO");
		XmlDeclGoodsLimitVnMap.put("VinNo","VIN_NO");
		XmlDeclGoodsLimitVnMap.put("LicTypeCode","LIC_TYPE_CODE");
		XmlDeclGoodsLimitVnMap.put("LicenceNo","LICENCE_NO");
		XmlDeclGoodsLimitVnMap.put("BillLadDate","BILLLAD_DATE");
		XmlDeclGoodsLimitVnMap.put("QualityQgp","QUALITY_QGP");
		XmlDeclGoodsLimitVnMap.put("MotorNo","MOTOR_NO");
		XmlDeclGoodsLimitVnMap.put("VinCode","VIN_CODE");
		XmlDeclGoodsLimitVnMap.put("ChassisNo","CHASSIS_NO");
		XmlDeclGoodsLimitVnMap.put("InvoiceNum","INVOICE_NUM");
		XmlDeclGoodsLimitVnMap.put("ProdCnnm","PROD_CNNM");
		XmlDeclGoodsLimitVnMap.put("ProdEnnm","PROD_ENNM");
		XmlDeclGoodsLimitVnMap.put("ModelEn","MODEL_EN");
		XmlDeclGoodsLimitVnMap.put("PricePerUnit","PRICE_PER_UNIT");
		
		XmlPackAplMap.put("EntDeclNo","DECL_NO");
		XmlPackAplMap.put("EntUuid","ENT_UUID");
		XmlPackAplMap.put("DeclRegName","ENT_NAME");
		XmlPackAplMap.put("AppliInspName","DECL_PERSON_NAME");
		XmlPackAplMap.put("DeclDate","DECL_DATE");
		XmlPackAplMap.put("PackUseUnitCode","PACK_USE_UNIT_CODE");
		XmlPackAplMap.put("PackUseUnitName","PACK_USE_UNIT_NAME");
		XmlPackAplMap.put("PackFactoryCode","PACK_FACTORY_CODE");
		XmlPackAplMap.put("PkgMnufctrName","PACK_FACTORY_NAME");
		XmlPackAplMap.put("PackContainerCode","PACK_CONTAINER_CODE");
		XmlPackAplMap.put("PkgCntnrName","PACK_CONTAINER_NAME");
		XmlPackAplMap.put("PkgCntnrSpec","INNER_PACK_MODEL");
		XmlPackAplMap.put("RawMatName","MATERIAL_NAME");
		XmlPackAplMap.put("MatOrigCode","MATERIAL_ORIGIN_PLACE");
		XmlPackAplMap.put("PackLicenseCode","PACK_LICENSE_CODE");
		XmlPackAplMap.put("DeclCode","DECL_CODE");
		XmlPackAplMap.put("ProduceDate","PROD_DATE");
		XmlPackAplMap.put("PackDgSpecCode","PACK_DG_SPEC_CODE");
		XmlPackAplMap.put("PackPlaceCode","PACK_PLACE_CODE");
		XmlPackAplMap.put("PkgStorePlace","PACK_PLACE");
		XmlPackAplMap.put("CheckupResuCodes","CHECKUP_RESU_CODES");
		XmlPackAplMap.put("TransType","TRANS_MODE_CODE");
		XmlPackAplMap.put("LoadGdsName","GOODS_NAME");
		XmlPackAplMap.put("GoodsShapeCode","GOODS_SHAPE_CODE");
		XmlPackAplMap.put("PreldGdsDensity","GOODS_DENSITY");
		XmlPackAplMap.put("GoodsWeight","GOODS_WEIGHT");
		XmlPackAplMap.put("GoodsValuesRmb","GOODS_NET");
		XmlPackAplMap.put("InerPkgCtnrName","INNER_PACK_NAME");
		XmlPackAplMap.put("InerPackContSpf","INNER_PACK_MODEL");
		XmlPackAplMap.put("InterMatrialMeth","INNER_MATERIAL");
		XmlPackAplMap.put("ShippingPortCode","SHIPPING_PORT_CODE");
		XmlPackAplMap.put("ShippingDate","SHIPPING_DATE");
		XmlPackAplMap.put("CodeCountry","COUNTRY_CODE");
		XmlPackAplMap.put("ContainerLastName","CONTAINER_LAST_NAME");
		XmlPackAplMap.put("SheetTypeCodes","SHEET_TYPE_CODES");
		XmlPackAplMap.put("InspOrgCode","ORG_CODE");
		XmlPackAplMap.put("PackContainerNo","PACK_CONTAINER_CODE");
		XmlPackAplMap.put("PackMarkNo","PACK_MARK_NO");
		XmlPackAplMap.put("DisptCertFlag","DISPT_CERT_FLAG");
		XmlPackAplMap.put("DeclUnitContact","DECL_UNIT_CONTACT");
		XmlPackAplMap.put("DeclUnitTel","DECL_UNIT_TEL");
		XmlPackAplMap.put("SpecialRequire","SPECIAL_REQUIRE");
		XmlPackAplMap.put("DeclRegNo","DECL_REG_NO");
		XmlPackAplMap.put("DeclareCode","DECL_PERSN_CERT_NO");
		XmlPackAplMap.put("WtUnitCode","WEIGHT_UNIT_CODE");
		XmlPackAplMap.put("ProdTech","PROD_TECH");
		XmlPackAplMap.put("TestRptNo","TEST_RPT_NO");
		XmlPackAplMap.put("SoftType","SOFT_TYPE");
		XmlPackAplMap.put("techRegCode","TECH_REG_CODE");
		XmlPackAplMap.put("PackId","PACK_ID");
		XmlPackAplMap.put("spare1","SPARE1");
		XmlPackAplMap.put("spare2","SPARE2");
		XmlPackAplMap.put("spare3","SPARE3");
		XmlPackAplMap.put("spare4","SPARE4");
		
		XmlPackDispartMap.put("DispartNo","DISPART_NO");
		XmlPackDispartMap.put("EntDeclNo","DECL_NO");
		XmlPackDispartMap.put("CertDivideCode","DISPART_UNIT_CODE");
		XmlPackDispartMap.put("CertDvidEntName","DISPART_UNIT_NAME");
		XmlPackDispartMap.put("DisptCertQty","DISPART_QTY");
		XmlPackDispartMap.put("DispartId","DISPART_ID");

		XmlPackModelMap.put("PackNo","PACK_NO");
		XmlPackModelMap.put("EntDeclNo","DECL_NO");
		XmlPackModelMap.put("ModelId","MODEL_ID");
		XmlPackModelMap.put("PkgCntnrSpec","PACK_CONTAINER_MODEL");
		XmlPackModelMap.put("ModelSize","MODEL_SIZE");
		XmlPackModelMap.put("QTY","QTY");
				
		XmlDeadDeclMap.put("EntDeclNo","DECL_NO");
		XmlDeadDeclMap.put("DespPortCode","DESP_PORT_CODE");
		XmlDeadDeclMap.put("PortStopCode","PORT_STOP_CODE");
		XmlDeadDeclMap.put("EntyPortCode","ENTY_PORT_CODE");
		XmlDeadDeclMap.put("DestCode","DEST_CODE");
		XmlDeadDeclMap.put("ExeInspOrgCode","EXE_INSP_ORG_CODE");
		XmlDeadDeclMap.put("PurpOrgCode","PURP_ORG_CODE");
		XmlDeadDeclMap.put("RemarkColumnFlag","REMARK_COLUMN_FLAG");
		XmlDeadDeclMap.put("DeclDate","DECL_DATE");
		XmlDeadDeclMap.put("ExpImpFlag","EXP_IMP_FLAG");
		XmlDeadDeclMap.put("DeclCode","DECL_CODE");
		XmlDeadDeclMap.put("EntUuid","ENT_UUID");
		XmlDeadDeclMap.put("DeadType","DEAD_TYPE");
		XmlDeadDeclMap.put("CorrelationDeaclNo","CORRELATION_DEACL_NO");
		XmlDeadDeclMap.put("TransModeCode","TRANS_MODE_CODE");
		XmlDeadDeclMap.put("ConvynceName","CONVYNCE_NAME");
		XmlDeadDeclMap.put("TransMeanNo","TRANS_MEAN_NO");
		XmlDeadDeclMap.put("BillLadNo","BILL_LAD_NO");
		XmlDeadDeclMap.put("DespCtryCode","DESP_CTRY_CODE");
		XmlDeadDeclMap.put("ExpPort","EXP_PORT_CODE");
		XmlDeadDeclMap.put("ExpImpDate","EXP_IMP_DATE");
		XmlDeadDeclMap.put("techRegCode","TECH_REG_CODE");
		XmlDeadDeclMap.put("SoftType","SOFT_TYPE");
		XmlDeadDeclMap.put("spare1","SPARE1");
		XmlDeadDeclMap.put("spare2","SPARE2");
		XmlDeadDeclMap.put("spare3","SPARE3");
		XmlDeadDeclMap.put("spare4","SPARE4");
		
		XmlDeadDeclGoodsMap.put("DeadGoodsId","DEAD_GOODS_ID");
		XmlDeadDeclGoodsMap.put("EntDeclNo","DECL_NO");
		XmlDeadDeclGoodsMap.put("GoodsNo","GOODS_NO");
		XmlDeadDeclGoodsMap.put("DeadName","DEAD_NAME");
		XmlDeadDeclGoodsMap.put("Nationality","NATIONALITY");
		XmlDeadDeclGoodsMap.put("Gender","GENDER");
		XmlDeadDeclGoodsMap.put("Work","WORK");
		XmlDeadDeclGoodsMap.put("Birth","BIRTH");
		XmlDeadDeclGoodsMap.put("DeadDate","DEAD_DATE");
		XmlDeadDeclGoodsMap.put("DeadPlace","DEAD_PLACE");
		XmlDeadDeclGoodsMap.put("DeadReason","DEAD_REASON");
		XmlDeadDeclGoodsMap.put("BirthAddr","BIRTH_ADDR");
		XmlDeadDeclGoodsMap.put("BuriedAddr","BURIED_ADDR");
		XmlDeadDeclGoodsMap.put("ProcWay","PROC_WAY");
		XmlDeadDeclGoodsMap.put("ConsignorCode","CONSIGNOR_CODE");
		XmlDeadDeclGoodsMap.put("ConsignorName","CONSIGNOR_NAME");
		XmlDeadDeclGoodsMap.put("ConsignorAddr","CONSIGNOR_ADDR");
		XmlDeadDeclGoodsMap.put("Agent","AGENT");
		XmlDeadDeclGoodsMap.put("Telephone","TELEPHONE");
		XmlDeadDeclGoodsMap.put("AttachDocCode","ATTACH_DOC_CODE");
		XmlDeadDeclGoodsMap.put("AttDocName","ATT_DOC_NAME");
		XmlDeadDeclGoodsMap.put("ProdHsCode","PROD_HS_CODE");
		XmlDeadDeclGoodsMap.put("HsCodeDesc","HS_CODE_DESC");
		XmlDeadDeclGoodsMap.put("CiqCode","CIQ_CODE");
		XmlDeadDeclGoodsMap.put("CiqName","CIQ_NAME");
		XmlDeadDeclGoodsMap.put("DeadType","DEAD_TYPE");
				
		XmlContMap.put("EntDeclNo","ENT_DECL_NO");
		XmlContMap.put("StationRegiNo","STATION_REGI_NO");
		XmlContMap.put("StationName","STATION_NAME");
		XmlContMap.put("StationAddr","STATION_ADDR");
		XmlContMap.put("DeclUnitCode","DECL_UNIT_CODE");
		XmlContMap.put("DeclRegName","DECL_REG_NAME");
		XmlContMap.put("DeclUnitContact","DECL_UNIT_CONTACT");
		XmlContMap.put("DeclUnitTel","DECL_UNIT_TEL");
		XmlContMap.put("DeclTime","DECL_TIME");
		XmlContMap.put("PreInspDate","PRE_INSP_DATE");
		XmlContMap.put("ContainerQty","CONTAINER_QTY");
		XmlContMap.put("ContType","CONT_TYPE");
		XmlContMap.put("SettingTemper","SETTING_TEMPER");
		XmlContMap.put("LoadGoodsName","LOAD_GOODS_NAME");
		XmlContMap.put("InspOrgCode","INSP_ORG_CODE");
		XmlContMap.put("EntUuid","ENT_UUID");
		XmlContMap.put("ContId","CONT_ID");
		XmlContMap.put("techRegCode","TECH_REG_CODE");
		XmlContMap.put("SoftType","SOFT_TYPE");
		XmlContMap.put("spare1","SPARE1");
		XmlContMap.put("spare2","SPARE2");
		XmlContMap.put("spare3","SPARE3");
		XmlContMap.put("spare4","SPARE4");
						
		XmlContDetailMap.put("ContDetailId","CONT_DETAIL_ID");
		XmlContDetailMap.put("EntDeclNo","DECL_NO");
		XmlContDetailMap.put("ContNo","CONT_NO");
		XmlContDetailMap.put("Containtype","CONTAINTYPE");
		XmlContDetailMap.put("DeclNo","DECL_GET_NO");
		XmlContDetailMap.put("LclFlag","LCL_FLAG");
						
		XmlContInspLobMap.put("InspId","INSP_ID");
		XmlContInspLobMap.put("EntDeclNo","CONT_DECL_NO");
		XmlContInspLobMap.put("AttachName","ATTACH_NAME");
		XmlContInspLobMap.put("AttachType","ATTACH_TYPE");
		XmlContInspLobMap.put("Attachment","ATTACHMENT");
						
		XmlContListLobMap.put("ListId","LIST_ID");
		XmlContListLobMap.put("EntDeclNo","CONT_DECL_NO");
		XmlContListLobMap.put("AttachName","ATTACH_NAME");
		XmlContListLobMap.put("AttachType","ATTACH_TYPE");
		XmlContListLobMap.put("Attachment","ATTACHMENT");
						
		XmlContOtherLobMap.put("OtherId","OTHER_ID");
		XmlContOtherLobMap.put("EntDeclNo","CONT_DECL_NO");
		XmlContOtherLobMap.put("AttachName","ATTACH_NAME");
		XmlContOtherLobMap.put("AttachType","ATTACH_TYPE");
		XmlContOtherLobMap.put("Attachment","ATTACHMENT");
						
		XmlStationMap.put("ContDetailId","CONT_DETAIL_ID");
		XmlStationMap.put("EntUuid","ENT_UUID");
		XmlStationMap.put("ContNo","CONT_NO");
		XmlStationMap.put("Containtype","CONTAINTYPE");
		XmlStationMap.put("DeclNo","DECL_GET_NO");
		XmlStationMap.put("ContDeclNo","CONT_DECL_NO");
		XmlStationMap.put("LclFlag","LCL_FLAG");
		XmlStationMap.put("SoftType","SOFT_TYPE");
		XmlStationMap.put("techRegCode","TECH_REG_CODE");
		XmlStationMap.put("spare1","SPARE1");
		XmlStationMap.put("spare2","SPARE2");
		XmlStationMap.put("spare3","SPARE3");
		XmlStationMap.put("spare4","SPARE4");		 
	}  
	
	
	
	
	
	
	
	
	
	
	
	
	public static String PutMessage(ServicesBean bean,IDataAccess DataAccess) throws LegendException{
		String ErrorMsg = PutMessageAnalyze(bean, DataAccess);
		if(SysUtility.isEmpty(ErrorMsg)){
			ExsUtility.AddLogSuccess(DataAccess, bean, "PutMessage Success!");
		}else{
			ExsUtility.AddLogFailDesc(DataAccess, bean, "PutMessage Fail!");
		}
		DataAccess.ComitTrans();
		FileUtility.copyFile(bean.getSourcePath()+File.separator+bean.getFileName(), bean.getTargetPath()+File.separator+bean.getFileName());
		FileUtility.deleteFile(bean.getSourcePath(),bean.getFileName());//删除本地文件
		return ErrorMsg;
	}
	
	public static String PutMessageAnalyze(ServicesBean bean,IDataAccess DataAccess) throws LegendException{
		StringBuffer ErrorMsg = new StringBuffer();
		HashMap RootMessage = new HashMap();
		HashMap MessageHead = new HashMap();
		HashMap MessageBody = new HashMap();
		HashMap ItfDclIoDecl = new HashMap();
		Map CustomerMap = bean.getCustomerMap();
		
		String RootName = "";
		String Endcoding = "";
		String SourceId = "";
		DataAccess.BeginTrans();
		try {
			/************************1.解析传入的文件**********************************/
			try {
				String tempXmlData = FileUtility.readFile(bean.getSourcePath() + File.separator+ bean.getFileName(), false);
				Endcoding = tempXmlData.substring(tempXmlData.indexOf("encoding=\"")+10, tempXmlData.indexOf("\"?>"));
				tempXmlData = tempXmlData.substring(tempXmlData.indexOf("\"?>")+3, tempXmlData.indexOf("\"?>")+100);
				RootName = tempXmlData.substring(tempXmlData.indexOf("<")+1, tempXmlData.indexOf(">"));
				bean.setEncoding(Endcoding);
				bean.setRootNames(RootName);
				
				String XmlData = FileUtility.readFile(bean.getSourcePath() + File.separator+ bean.getFileName(), false, Endcoding);
				InputStream is = new ByteArrayInputStream(XmlData.getBytes(Endcoding));
				RootMessage = (HashMap)((List)FileUtility.xmlParse(is,RootName,Endcoding).get(RootName)).get(0);
			} catch (Exception e) {
				ErrorMsg.append(ErrorCode.ErrorCode62+e.getMessage());
				return ErrorMsg.toString();
			}
			/************************2.读取配置**********************************/
			if("OBORMessage".equalsIgnoreCase(RootName)){
				MessageHead = (HashMap)((List)RootMessage.get(ExsConstants.MessageHead)).get(0);
				MessageBody = (HashMap)((List)RootMessage.get(ExsConstants.MessageBody)).get(0);
				ItfDclIoDecl = (HashMap)((List)MessageBody.get("ITF_DCL_IO_DECL")).get(0);
			}else if("ROOT".equalsIgnoreCase(RootName)){
				ItfDclIoDecl = (HashMap)((List)RootMessage.get("ITF_DCL_IO_DECL")).get(0);
				SourceId = (String)ItfDclIoDecl.get("techRegCode");
				CustomerMap = getItfItownetCustomer(SourceId, bean.getDataType());
				bean.setCustomerMap(CustomerMap);
			}else{
				ErrorMsg.append(ErrorCode.ErrorCode62+"文件格式不匹配");
				return ErrorMsg.toString();
			}
			/************************2.填充逻辑参数、填充e-CIQ的帐号信息**********************************/
			bean.setMessageId((String)ItfDclIoDecl.get("EntUuid"));
			bean.setSerialNo((String)ItfDclIoDecl.get("EntUuid"));
			bean.setLogPath(bean.getSourcePath()+File.separator+bean.getFileName());
			bean.setLogPath(bean.getLogPath()+"\n"+bean.getTargetPath()+File.separator+bean.getFileName());
//		if(SysUtility.isEmpty(bean.getFileName())){
//			bean.setFileName(SysUtility.isEmpty(ItfDclIoDecl.get("DeclId"))?SysUtility.GetUUID()+".xml":ItfDclIoDecl.get("techRegCode")+"_"+ItfDclIoDecl.get("DeclId")+".xml");
//		}
			ItfDclIoDecl.put("MESSAGE_SOURCE", (String)MessageHead.get("MESSAGE_SOURCE"));
			ItfDclIoDecl.put("PART_ID", (String)MessageHead.get("MESSAGE_SOURCE"));
			ItfDclIoDecl.put("ECIQ_USER_NAME", (String)CustomerMap.get("ECIQ_USER_NAME"));
			ItfDclIoDecl.put("ECIQ_PASS_WORD", (String)CustomerMap.get("ECIQ_PASS_WORD"));
			ItfDclIoDecl.put("SoftType", SysUtility.isEmpty((String)CustomerMap.get("SEND_ORG_CODE"))?(String)CustomerMap.get("ECIQ_SOURCE_ID"): (String)CustomerMap.get("SEND_ORG_CODE"));
			ItfDclIoDecl.put("spare1", (String)CustomerMap.get("ECIQ_USER_NAME"));
			ItfDclIoDecl.put("spare2", (String)CustomerMap.get("ECIQ_PASS_WORD"));
			ItfDclIoDecl.put("spare3", "1");
			ItfDclIoDecl.put("spare4", "");
			/************************3.创建e-CIQ标准文件**********************************/
			try {
				if("OBORMessage".equalsIgnoreCase(RootName)){
					HashMap RootMap = new HashMap();
					List RootList = new ArrayList();
					RootList.add(MessageBody);
					RootMap.put("ROOT", RootList);
					String tempXmlData = "<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(RootMap, 0);
					FileUtility.createFile(bean.getTargetPath(),bean.getFileName(),tempXmlData,bean.getEncoding());
				}else if("ROOT".equalsIgnoreCase(RootName)){
					HashMap RootMap = new HashMap();
					List RootList = new ArrayList();
					RootList.add(RootMessage);
					RootMap.put("ROOT", RootList);
					bean.setEncoding("GBK");
					String tempXmlData = "<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(RootMap, 0);
					FileUtility.createFile(bean.getTempPath(),bean.getFileName(),tempXmlData,bean.getEncoding());
				}else{
					//TODO
				}
			} catch (Exception e) {
				ErrorMsg.append(ErrorCode.ErrorCode62+e.getMessage());
				return ErrorMsg.toString();
			}
			/************************4.验证业务字段长度及必填项**********************************/
			Datas TempDatas = new Datas();
			if("OBORMessage".equalsIgnoreCase(RootName)){
				String ImpExpFlag = (String)ItfDclIoDecl.get("AplKind");//出入境类别【I：入境；O：出境】
				ValidateEciq(ErrorMsg, ImpExpFlag, MessageBody, TempDatas);
				if(SysUtility.isNotEmpty(ErrorMsg)){
					ErrorMsg.insert(0, ErrorCode.ErrorCode64);
					return ErrorMsg.toString();
				}
			}
			
			/************************5.验证业务逻辑**********************************/
			if("OBORMessage".equalsIgnoreCase(RootName)){
				if(!TempDatas.has("ITF_DCL_IO_DECL_GOODS")){
					ErrorMsg.append(ErrorCode.ErrorCode63);
					return ErrorMsg.toString();
				}
			}
			/************************6.验证信城通CRM系统帐号信息**********************************/
			String ItowNetCrmHeader = getItowNetCRMHeader(bean.getMessageId(),CustomerMap);
			if(SysUtility.isEmpty(ItowNetCrmHeader)){
				ErrorMsg.append(ErrorCode.ErrorCode59);
				return ErrorMsg.toString();
			}
			/************************7.保存业务数据**********************************/
			String rt = AppUtility.EciqSaveToDB(DataAccess, RootMessage, false);
			if(SysUtility.isNotEmpty(rt)){
				ErrorMsg.append(ErrorCode.ErrorCode60).append(rt);
				return ErrorMsg.toString();
			}
			/************************8.调用EdiWebservices发送本地文件**********************************/
			rt = MethodInvoke(bean.getClassInvoke(), ItowNetCrmHeader, bean);
			if(SysUtility.isNotEmpty(rt)){
				ErrorMsg.append(ErrorCode.ErrorCode61).append(rt);
				return ErrorMsg.toString();
			}
		} catch (Exception e) {
			ErrorMsg.append(ErrorCode.ErrorCode99+e.getMessage());
			return ErrorMsg.toString();
		} finally{
			if(SysUtility.isNotEmpty(ErrorMsg)){//插入错误回执
				DataAccess.RoolbackTrans();
				InsertItfDclReceipts(DataAccess, MessageHead, ItfDclIoDecl, CustomerMap, ErrorMsg);
				DataAccess.ComitTrans();
			}
		}
		return ErrorCode.ErrorCode00;
	}
	
	private static void InsertItfDclReceipts(IDataAccess DataAccess,HashMap MessageHead,HashMap ItfDclIoDecl,Map CustomerMap,StringBuffer ErrorMsg){
		try {
			JSONObject row = new JSONObject();
			row.put("DECL_NO", (String)ItfDclIoDecl.get("EntDeclNo"));
			row.put("RSP_CODES", "0000");
			row.put("RSP_INFO", "报检失败！企业报检号【"+(String)ItfDclIoDecl.get("EntDeclNo")+"】，"+ErrorMsg);
			row.put("RSP_GEN_TIME", SysUtility.getSysDate());
			row.put("RSP_SEND_TIME", SysUtility.getSysDate());
			row.put("RESPONSE_ID", SysUtility.GetUUID());
			row.put("DECL_NO_TYPE", "1");
			row.put("RSP_NO", "1");
			row.put("DECL_REG_NO", "");
			row.put("SENDER_ADDR", (String)ItfDclIoDecl.get("techRegCode"));
			row.put("RSP_KIND", "1");
			row.put("DECL_GET_NO", "");
			row.put("RSP_STATE", "");
			row.put("ADD_INFO", "");
			row.put("OPER_TIME", SysUtility.getSysDate());
			row.put("ENT_UUID", (String)ItfDclIoDecl.get("EntUuid"));
			row.put("TECH_REG_CODE", (String)ItfDclIoDecl.get("techRegCode"));
			row.put("SEND_ORG_CODE", CustomerMap.get("SEND_ORG_CODE"));
			row.put("ECIQ_USER_NAME", CustomerMap.get("ECIQ_USER_NAME"));
			row.put("MESSAGE_SOURCE", SysUtility.isNotEmpty(CustomerMap.get("MESSAGE_SOURCE"))?CustomerMap.get("MESSAGE_SOURCE"):MessageHead.get("MESSAGE_SOURCE"));
			DataAccess.Insert("ITF_DCL_RECEIPTS", row);
			DataAccess.ComitTrans();
		} catch (JSONException e) {
			LogUtil.printLog("ITF_DCL_RECEIPTS保存出错:"+e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog("ITF_DCL_RECEIPTS保存出错:"+e.getMessage(), Level.ERROR);
		}
	}
	public static String MethodInvoke(String MethodInvoke,String str,ServicesBean bean){
		if(SysUtility.isEmpty(MethodInvoke)){
			return "";
		}
		try {
			Class[] classes = new Class[] {String.class, ServicesBean.class};
			Object[] objs = new Object[] {str, bean};
			return MethodInvokeCore(MethodInvoke, classes, objs);
		} catch (SecurityException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (ClassNotFoundException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (NoSuchMethodException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (InstantiationException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (IllegalAccessException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	public static String MethodInvokeCore(String MethodInvoke,Class[] objClass,Object[] objs)throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		String rt = "";
		if(SysUtility.isEmpty(MethodInvoke) || MethodInvoke.indexOf(".") < 0){
			return rt;
		}
		String className = MethodInvoke.substring(0, MethodInvoke.lastIndexOf("."));
		String methodName = MethodInvoke.substring(MethodInvoke.lastIndexOf(".")+1);
		Class cController = Class.forName(className);
		Object oController = cController.newInstance();
		
		Method method = cController.getMethod(methodName, objClass);
		try {
			Object obj = method.invoke(oController, objs);
			rt = (String)obj;
		} catch (Exception e) {
			LogUtil.printLog("MethodInvoke Error："+oController+"."+method.getName()+" "+e.getMessage(), Level.ERROR);
		}
		return rt;
	}
	public static String getItowNetCRMHeader(String MessageId,Map CustomerMap){
		StringBuffer header = new StringBuffer();
		try {
			//system
			String sourceId = (String)CustomerMap.get("SOURCE_ID");
			if(SysUtility.isNotEmpty(SysUtility.GetSetting("System", "pltSourceId"))){
				sourceId = SysUtility.GetSetting("System", "pltSourceId");//企业组织机构代码，测试环境固定值
			}
			String sourceApp = SysUtility.isEmpty(CustomerMap.get("SOURCE_APP"))?"SWEP":(String)CustomerMap.get("SOURCE_APP");
			String destinationId = SysUtility.isEmpty(CustomerMap.get("DESTINATION_ID"))?"790000":(String)CustomerMap.get("DESTINATION_ID");
			String sourceDomainId = SysUtility.isEmpty(CustomerMap.get("SOURCE_DOMAIN_ID"))?"ITOWN":(String)CustomerMap.get("SOURCE_DOMAIN_ID");
			String destinationDomainId = SysUtility.isEmpty(CustomerMap.get("DESTINATION_DOMAIN_ID"))?"ITOWN":(String)CustomerMap.get("DESTINATION_DOMAIN_ID");
			String destinationApp = SysUtility.isEmpty(CustomerMap.get("DESTINATION_APP"))?"SWEP":(String)CustomerMap.get("DESTINATION_APP");
			String messageType = "11";
			String messageFormat = SysUtility.isEmpty(CustomerMap.get("MESSAGE_FORMAT"))?"uncompress":(String)CustomerMap.get("MESSAGE_FORMAT");
			String messageId = MessageId;
			String messageVersion = (String)CustomerMap.get("MESSAGE_VERSION");
			String sendTime = SysUtility.getSysDate();
			String odexSignature = (String)CustomerMap.get("ODEX_SIGNATURE");
			//app
			String reportType = (String)CustomerMap.get("MESSAGE_TYPE");
			String actionType = SysUtility.isEmpty(CustomerMap.get("ACTION_TYPE"))?"IU":(String)CustomerMap.get("ACTION_TYPE");
			String bizField1 = (String)CustomerMap.get("BIZ_FIELD1");
			String bizField2 = (String)CustomerMap.get("BIZ_FIELD2");
			String bizField3 = (String)CustomerMap.get("BIZ_FIELD3");
			String enterpriseType = SysUtility.isEmpty(CustomerMap.get("ENTERPRISE_TYPE"))?"1":(String)CustomerMap.get("ENTERPRISE_TYPE");
			String softEncryptCode = (String)CustomerMap.get("SOFT_ENCRYPT_CODE");
			if(SysUtility.isNotEmpty(softEncryptCode)){
				softEncryptCode = GetSoftEncryptCode(softEncryptCode+sourceId);
			}
			String softType = SysUtility.isEmpty(CustomerMap.get("SOFT_TYPE"))?"8":(String)CustomerMap.get("SOFT_TYPE");
			//auth
			String authType = SysUtility.isEmpty(CustomerMap.get("AUTH_TYPE"))?"1":(String)CustomerMap.get("AUTH_TYPE");
			String authUserName = (String)CustomerMap.get("AUTH_USER_NAME");
			String authPassword = (String)CustomerMap.get("AUTH_PASSWORD");
			if(SysUtility.isNotEmpty(authPassword)){
				authPassword = GetAuthPassword(authPassword);
			}
			String authEntityId = (String)CustomerMap.get("AUTH_ENTITY_ID");
			String authTokenId = (String)CustomerMap.get("AUTH_TOKEN_ID");
			String authSignInfo = (String)CustomerMap.get("AUTH_SIGN_INFO");
			
			header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			header.append("<root>        ");
			header.append("  <system>");
			header.append("    <argument name=\"source-id\" value=\""+sourceId+"\" />");//企业组织机构代码
			header.append("    <argument name=\"source-domain-id\" value=\""+sourceDomainId+"\" />");//源域标识
			header.append("    <argument name=\"source-app\" value=\""+sourceApp+"\" />");//源应用标识
			header.append("    <argument name=\"destination-id\" value=\""+destinationId+"\" />");//目标ID 780000
			header.append("    <argument name=\"destination-domain-id\" value=\""+destinationDomainId+"\" />");//目标域标识
			header.append("    <argument name=\"destination-app\" value=\""+destinationApp+"\" />");//目标应用标识
			header.append("    <argument name=\"message-type\" value=\""+messageType+"\" />");//消息类别
			header.append("    <argument name=\"message-format\" value=\""+messageFormat+"\" />");//消息格式 compress-压缩 uncompress-不压缩
			header.append("    <argument name=\"message-id\" value=\""+messageId+"\" />");//消息ID 生成报文时替换
			header.append("    <argument name=\"message-version\" value=\""+messageVersion+"\" />");//消息版本
			header.append("    <argument name=\"send-time\" value=\""+sendTime+"\" />");//发送时间 生成报文时替换
			header.append("    <argument name=\"odex-signature\" value=\""+odexSignature+"\" />");//ODEX签名
			header.append("  </system>");
			header.append("  <app>");
			header.append("    <argument name=\"report-type\" value=\""+reportType+"\" />");//报文类型 生成报文时替换
			header.append("    <argument name=\"action-type\" value=\""+actionType+"\" />");//动作 IU
			header.append("    <argument name=\"enterprise-type\" value=\""+enterpriseType+"\" />");//企业类型 1
			header.append("    <argument name=\"biz-field1\" value=\""+bizField1+"\" />");//业务关键字1
			header.append("    <argument name=\"biz-field2\" value=\""+bizField2+"\" />");//业务关键字2
			header.append("    <argument name=\"biz-field3\" value=\""+bizField3+"\" />");//业务关键字3
			header.append("    <argument name=\"soft_encrypt_code\" value=\""+softEncryptCode+"\" />");//企业端加密串
			header.append("    <argument name=\"soft_type\" value=\""+softType+"\" />");//企业端类型  11
			header.append("  </app>");
			if (SysUtility.isNotEmpty(authType)){
				header.append("  <auth>");
				header.append("    <argument name=\"auth-type\" value=\""+authType+"\" />");//认证类型 1
				header.append("    <argument name=\"auth-user-name\" value=\""+authUserName+"\" />");//帐号信息
				header.append("    <argument name=\"auth-password\" value=\""+authPassword+"\" />");//帐号密码
				header.append("    <argument name=\"auth-entity-id\" value=\""+authEntityId+"\" />");//Key信息
				header.append("    <argument name=\"auth-token-id\" value=\""+authTokenId+"\" />");//用于验证身份
				header.append("    <argument name=\"auth-sign-info\" value=\""+authSignInfo+"\" />");//签名信息
				header.append("  </auth>");
			}
			header.append("</root>");
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return "";
		}
		return header.toString().replaceAll("null", "");
	}
	
	private static String GetSoftEncryptCode(String src) throws LegendException{
		return EcodingPasswd(src, "md5");
	}
	
	private static String GetAuthPassword(String str) throws LegendException{
		try {
			byte[] b2 = str.getBytes("GBK");
			byte[] result2 = encrypt(b2);
			return new String(result2,"GBK");
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("密码加密出错："+e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	private static String EcodingPasswd(String src, String method) {
		try {
			// 信息摘要算法
			MessageDigest md5 = MessageDigest.getInstance(method);
			md5.update(src.getBytes());
			byte[] encoding = md5.digest();
			// 使用64进行编码，一避免出现丢数据情景
			return new String(BASE64EncoderStream.encode(encoding));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e + "加密失败！！");
		}
	}
	
	private static byte[] encrypt(byte[] param) {
		if (param == null || param.length == 0) {
			return null;
		}
		byte[] result = new byte[param.length * 2];
		for (int i = 0; i < param.length; ++i) {
			result[(2 * i)] = (byte) (param[i] >> 4 & 0x0F | 0x60); 
			result[(2 * i + 1)] = (byte) (param[i] & 0x0F | 0x40);
		}
		//颠倒
		byte b ;
		for (int i = 0; i < result.length / 2; i++) {
			b = result[result.length - 1 - i];
			result[result.length - 1 - i] = result[i];
			result[i] = b;
		}
		return result;
	}
	
	private static void ValidateEciq(StringBuffer ErrorMsg,String ImpExpFlag,HashMap MessageBody,Datas TempDatas){
		try {
			if("I".equals(ImpExpFlag)){
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL","ITF_DCL_IO_DECL,ITF_DCL_IO_DECL_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_ATT","ITF_DCL_IO_DECL_ATT,ITF_DCL_IO_DECL_ATT_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS","ITF_DCL_IO_DECL_GOODS,ITF_DCL_IO_DECL_GOODS_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_CONT","ITF_DCL_IO_DECL_GOODS_CONT,ITF_DCL_IO_DECL_GOODS_CONT_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_LIMIT","ITF_DCL_IO_DECL_GOODS_LIMIT,ITF_DCL_IO_DECL_GOODS_LIMIT_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_LIMIT_VN","ITF_DCL_IO_DECL_GOODS_LIMIT_VN,ITF_DCL_IO_DECL_GOODS_LIMIT_VN_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_PACK","ITF_DCL_IO_DECL_GOODS_PACK,ITF_DCL_IO_DECL_GOODS_PACK_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_LIMIT","ITF_DCL_IO_DECL_LIMIT,ITF_DCL_IO_DECL_LIMIT_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_USER","ITF_DCL_IO_DECL_USER,ITF_DCL_IO_DECL_USER_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_MARK_LOB","ITF_DCL_MARK_LOB,ITF_DCL_MARK_LOB_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_CONT","ITF_DCL_IO_DECL_CONT,ITF_DCL_IO_DECL_CONT_I", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_CONT_DETAIL","ITF_DCL_IO_DECL_CONT_DETAIL,ITF_DCL_IO_DECL_CONT_DETAIL_I", MessageBody));
			}else{
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL","ITF_DCL_IO_DECL,ITF_DCL_IO_DECL_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_ATT","ITF_DCL_IO_DECL_ATT,ITF_DCL_IO_DECL_ATT_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS","ITF_DCL_IO_DECL_GOODS,ITF_DCL_IO_DECL_GOODS_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_CONT","ITF_DCL_IO_DECL_GOODS_CONT,ITF_DCL_IO_DECL_GOODS_CONT_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_LIMIT","ITF_DCL_IO_DECL_GOODS_LIMIT,ITF_DCL_IO_DECL_GOODS_LIMIT_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_LIMIT_VN","ITF_DCL_IO_DECL_GOODS_LIMIT_VN,ITF_DCL_IO_DECL_GOODS_LIMIT_VN_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_GOODS_PACK","ITF_DCL_IO_DECL_GOODS_PACK,ITF_DCL_IO_DECL_GOODS_PACK_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_LIMIT","ITF_DCL_IO_DECL_LIMIT,ITF_DCL_IO_DECL_LIMIT_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_USER","ITF_DCL_IO_DECL_USER,ITF_DCL_IO_DECL_USER_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_MARK_LOB","ITF_DCL_MARK_LOB,ITF_DCL_MARK_LOB_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_CONT","ITF_DCL_IO_DECL_CONT,ITF_DCL_IO_DECL_CONT_E", MessageBody));
				ErrorMsg.append(ValidateEciqTab(TempDatas,"ITF_DCL_IO_DECL_CONT_DETAIL","ITF_DCL_IO_DECL_CONT_DETAIL,ITF_DCL_IO_DECL_CONT_DETAIL_E", MessageBody));
			}
		} catch (Exception e) {
			ErrorMsg.append(ErrorCode.ErrorCode64+e.getMessage());
		}
	}
	
	private static String ValidateEciqTab(Datas TempDatas,String TableName,String PointCode,HashMap MessageBody) throws Exception{
		TempDatas.MapToDatas(TableName,MessageBody,new HashMap());
		HashMap map = new HashMap();
		if(!TempDatas.has(TableName)){
			return "";
		}
		List lst = SysUtility.JSONArrayToList((JSONArray)TempDatas.get(TableName));
		if(SysUtility.isEmpty(lst)){
			return "";
		}
		map.put(TableName, lst);
		String msg = FieldFormatUtil.checkField(map, PointCode);
		return msg;
	}
	
	private static Map getItfItownetCustomer(final String SourceId,final String ConfigType) throws LegendException{
		String SQL = "select * from itf_itownet_customer where is_enabled = '1' AND SOURCE_ID = ? and config_type like ? and rownum = 1";
		Map CustomerMap = SQLExecUtils.query4Map(SQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, SourceId);
				ps.setString(2, "%"+ConfigType+"%");
			}
		});
		return CustomerMap;
	}
	
	
	
	
	
	
}
