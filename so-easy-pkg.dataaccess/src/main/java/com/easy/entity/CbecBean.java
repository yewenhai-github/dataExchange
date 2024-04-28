package com.easy.entity;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class CbecBean extends BaseBean {
	public JSONObject MessageHead = new JSONObject();
	public JSONObject MessageBody = new JSONObject();

	public JSONArray C_ENT_FILING_INFO = new JSONArray();
	public JSONArray C_ENT_ATTACHEDs = new JSONArray();
	public JSONArray C_ENT_FILING_MAGs = new JSONArray();
	
	public JSONArray C_PRODUCT_FILING_INFO = new JSONArray();
	public JSONArray C_PRODUCT_ATTACHEDs = new JSONArray();
	public JSONArray C_PRODUCT_FILING_MAGs = new JSONArray();

	public JSONArray C_ENTER_DECLARE = new JSONArray();
	public JSONArray C_ENTER_DECLARE_ITEMSs = new JSONArray();
	public JSONArray C_ENTER_DECLARE_CONTs = new JSONArray();

	public JSONArray C_EXIT_DECLARE = new JSONArray();
	public JSONArray C_EXIT_DECLARE_ITEMSs = new JSONArray();
	
	public JSONArray C_ORDER_DECLARE = new JSONArray();
	public JSONArray C_ITEM_DECLARE_DETAILs = new JSONArray();

	public JSONArray C_ITEM_DECLARE = new JSONArray();

	public JSONArray C_PAYMENT_DECLARE = new JSONArray();
	
	public JSONArray C_ENTER_BILL = new JSONArray();
	
	public JSONArray SEARCH_TABLE = new JSONArray();
	

	public JSONArray CM_ENT_FILING_INFO = new JSONArray();
	public JSONArray CM_ENT_ATTACHEDs = new JSONArray();
	public JSONArray CM_ENT_FILING_MAGs = new JSONArray();
	
	public JSONArray CM_PRODUCT_FILING_INFO = new JSONArray();
	public JSONArray CM_PRODUCT_ATTACHEDs = new JSONArray();
	public JSONArray CM_PRODUCT_FILING_MAGs = new JSONArray();

	public JSONArray CM_ITEM_DECLARE_RSP = new JSONArray();

	public JSONArray CM_DECLARE_CHECK_RECORD = new JSONArray();
	
	public JSONArray CM_DECLARE_RELEASE_RECORD = new JSONArray();
	
	public JSONArray CM_DECLARE_RECEIVE_RSP = new JSONArray();
	
	public JSONArray CM_DECLARE_NO_RSP = new JSONArray();
	
	public static CbecBean getInstance(){
		return new CbecBean();
	}
	
/*	public JSONObject getEntMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < C_ENT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)C_ENT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(C_ENT_ATTACHEDs) && C_ENT_ATTACHEDs.length() > 0){
					row.put("C_ENT_ATTACHED", getChildData(row, C_ENT_ATTACHEDs, "ENT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(C_ENT_FILING_MAGs) && C_ENT_FILING_MAGs.length() > 0){
					row.put("C_ENT_FILING_MAG", getChildData(row, C_ENT_FILING_MAGs, "ENT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("C_ENT_FILING_INFO", C_ENT_FILING_INFO);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getProductMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < C_PRODUCT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)C_PRODUCT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(C_PRODUCT_ATTACHEDs) && C_PRODUCT_ATTACHEDs.length() > 0){
					row.put("C_PRODUCT_ATTACHED", getChildData(row, C_PRODUCT_ATTACHEDs, "PRODUCT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(C_PRODUCT_FILING_MAGs) && C_PRODUCT_FILING_MAGs.length() > 0){
					row.put("C_PRODUCT_FILING_MAG", getChildData(row, C_PRODUCT_FILING_MAGs, "PRODUCT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("C_PRODUCT_FILING_INFO", C_PRODUCT_FILING_INFO);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getEnterMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < C_ENTER_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_ENTER_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_ENTER_DECLARE_ITEMSs) && C_ENTER_DECLARE_ITEMSs.length() > 0){
					row.put("C_ENTER_DECLARE_ITEMS", getChildData(row, C_ENTER_DECLARE_ITEMSs, "DECL_DECLAR_CHECK_ID"));
				}
				if(SysUtility.isNotEmpty(C_ENTER_DECLARE_CONTs) && C_ENTER_DECLARE_CONTs.length() > 0){
					row.put("C_ENTER_DECLARE_CONT", getChildData(row, C_ENTER_DECLARE_CONTs, "DECL_DECLAR_CHECK_ID"));
				}
			}
			MessageBody.put("C_ENTER_DECLARE", C_ENTER_DECLARE);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 

	public JSONObject getExitMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < C_EXIT_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_EXIT_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_EXIT_DECLARE_ITEMSs) && C_EXIT_DECLARE_ITEMSs.length() > 0){
					row.put("C_ENTER_DECLARE_ITEMS", getChildData(row, C_EXIT_DECLARE_ITEMSs, "GOODS_DECLAR_CHECK_ID"));
				}
			}
			MessageBody.put("C_EXIT_DECLARE", C_EXIT_DECLARE);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getOrderMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < C_ORDER_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_ORDER_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_ORDER_DECLARE_DETAILs) && C_ORDER_DECLARE_DETAILs.length() > 0){
					row.put("C_ORDER_DECLARE_DETAIL", getChildData(row, C_ORDER_DECLARE_DETAILs, "ORDER_ID"));
				}
			}
			MessageBody.put("C_ORDER_DECLARE", C_ORDER_DECLARE);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 

	public JSONObject getItemMessage(){
		JSONObject OBORMessage = new JSONObject();
		try { 
			MessageBody.put("C_ITEM_DECLARE", C_ITEM_DECLARE);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getPaymentMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			MessageBody.put("C_PAYMENT_DECLARE", C_PAYMENT_DECLARE);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getEnterBillMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			MessageBody.put("C_ENTER_BILL", C_ENTER_BILL);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 

	public JSONObject getReceivedMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONObject getDeleteReceivedMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {			
			MessageBody.put("SEARCH_TABLE", SEARCH_TABLE);
			JSONArray Root = new JSONArray(); 
			
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
*/
 	
	public JSONArray getEntMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < C_ENT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)C_ENT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(C_ENT_ATTACHEDs) && C_ENT_ATTACHEDs.length() > 0){
					row.put("C_ENT_ATTACHED", getChildData(row, C_ENT_ATTACHEDs, "ENT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(C_ENT_FILING_MAGs) && C_ENT_FILING_MAGs.length() > 0){
					row.put("C_ENT_FILING_MAG", getChildData(row, C_ENT_FILING_MAGs, "ENT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("C_ENT_FILING_INFO", C_ENT_FILING_INFO);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getProductMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < C_PRODUCT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)C_PRODUCT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(C_PRODUCT_ATTACHEDs) && C_PRODUCT_ATTACHEDs.length() > 0){
					row.put("C_PRODUCT_ATTACHED", getChildData(row, C_PRODUCT_ATTACHEDs, "PRODUCT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(C_PRODUCT_FILING_MAGs) && C_PRODUCT_FILING_MAGs.length() > 0){
					row.put("C_PRODUCT_FILING_MAG", getChildData(row, C_PRODUCT_FILING_MAGs, "PRODUCT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("C_PRODUCT_FILING_INFO", C_PRODUCT_FILING_INFO);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 

	public JSONArray getEnterMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < C_ENTER_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_ENTER_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_ENTER_DECLARE_ITEMSs) && C_ENTER_DECLARE_ITEMSs.length() > 0){
					row.put("C_ENTER_DECLARE_ITEMS", getChildData(row, C_ENTER_DECLARE_ITEMSs, "DECL_DECLAR_CHECK_ID"));
				}
				if(SysUtility.isNotEmpty(C_ENTER_DECLARE_CONTs) && C_ENTER_DECLARE_CONTs.length() > 0){
					row.put("C_ENTER_DECLARE_CONT", getChildData(row, C_ENTER_DECLARE_CONTs, "DECL_DECLAR_CHECK_ID"));
				}
			}
			MessageBody.put("C_ENTER_DECLARE", C_ENTER_DECLARE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
 
	public JSONArray getExitMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < C_EXIT_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_EXIT_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_EXIT_DECLARE_ITEMSs) && C_EXIT_DECLARE_ITEMSs.length() > 0){
					row.put("C_EXIT_DECLARE_ITEMS", getChildData(row, C_EXIT_DECLARE_ITEMSs, "GOODS_DECLAR_CHECK_ID"));
				}
			}
			MessageBody.put("C_EXIT_DECLARE", C_EXIT_DECLARE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
 
	public JSONArray getOrderMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < C_ORDER_DECLARE.length(); i++) {
				JSONObject row = (JSONObject)C_ORDER_DECLARE.get(i);
				if(SysUtility.isNotEmpty(C_ITEM_DECLARE_DETAILs) && C_ITEM_DECLARE_DETAILs.length() > 0){
					row.put("C_ITEM_DECLARE_DETAIL", getChildData(row, C_ITEM_DECLARE_DETAILs, "ORDER_ID"));
				}
			}
			MessageBody.put("C_ORDER_DECLARE", C_ORDER_DECLARE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
 
	public JSONArray getItemMessage(){
		JSONArray rows = new JSONArray();
		try {					
			MessageBody.put("C_ITEM_DECLARE", C_ITEM_DECLARE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getPaymentMessage(){
		JSONArray rows = new JSONArray();
		try {					
			MessageBody.put("C_PAYMENT_DECLARE", C_PAYMENT_DECLARE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getEnterBillMessage(){
		JSONArray rows = new JSONArray();
		try {					
			MessageBody.put("C_ENTER_BILL", C_ENTER_BILL);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 	
	
	public JSONArray getMessageHead(){
		JSONArray rows = new JSONArray();
		try {			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getDeleteReceived(){
		JSONArray rows = new JSONArray();
		try {					
			MessageBody.put("SEARCH_TABLE", SEARCH_TABLE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}  
	
	public JSONArray getBase(){
		JSONArray rows = new JSONArray();
		try {					
			MessageBody.put("SEARCH_TABLE", SEARCH_TABLE);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}  

	public JSONArray getMEntMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < CM_ENT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)CM_ENT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(CM_ENT_ATTACHEDs) && CM_ENT_ATTACHEDs.length() > 0){
					row.put("CM_ENT_ATTACHED", getChildData(row, CM_ENT_ATTACHEDs, "ENT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(CM_ENT_FILING_MAGs) && CM_ENT_FILING_MAGs.length() > 0){
					row.put("CM_ENT_FILING_MAG", getChildData(row, CM_ENT_FILING_MAGs, "ENT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("CM_ENT_FILING_INFO", CM_ENT_FILING_INFO);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getMProductMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < CM_PRODUCT_FILING_INFO.length(); i++) {
				JSONObject row = (JSONObject)CM_PRODUCT_FILING_INFO.get(i);
				if(SysUtility.isNotEmpty(CM_PRODUCT_ATTACHEDs) && CM_PRODUCT_ATTACHEDs.length() > 0){
					row.put("CM_PRODUCT_ATTACHED", getChildData(row, CM_PRODUCT_ATTACHEDs, "PRODUCT_FILING_INFO_ID"));
				}
				if(SysUtility.isNotEmpty(CM_PRODUCT_FILING_MAGs) && CM_PRODUCT_FILING_MAGs.length() > 0){
					row.put("CM_PRODUCT_FILING_MAG", getChildData(row, CM_PRODUCT_FILING_MAGs, "PRODUCT_FILING_INFO_ID"));
				}
			}
			MessageBody.put("CM_PRODUCT_FILING_INFO", CM_PRODUCT_FILING_INFO);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 

	public JSONArray getMItemMessage(){
		JSONArray rows = new JSONArray();
		try { 
			 
			MessageBody.put("CM_ITEM_DECLARE_RSP", CM_ITEM_DECLARE_RSP);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 

	public JSONArray getMCheckMessage(){
		JSONArray rows = new JSONArray();
		try { 
			 
			MessageBody.put("CM_DECLARE_CHECK_RECORD", CM_DECLARE_CHECK_RECORD);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 

	public JSONArray getMReleaseMessage(){
		JSONArray rows = new JSONArray();
		try { 
			 
			MessageBody.put("CM_DECLARE_RELEASE_RECORD", CM_DECLARE_RELEASE_RECORD);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getMRspMessage(){
		JSONArray rows = new JSONArray();
		try { 
			 
			MessageBody.put("CM_DECLARE_RECEIVE_RSP", CM_DECLARE_RECEIVE_RSP);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getMDeclNoMessage(){
		JSONArray rows = new JSONArray();
		try { 
			 
			MessageBody.put("CM_DECLARE_NO_RSP", CM_DECLARE_NO_RSP);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
}
