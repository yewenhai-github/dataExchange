package com.easy.entity;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class ItfDclIoDeclBean extends BaseBean{
	public JSONObject MessageHead = new JSONObject();
	public JSONObject MessageBody = new JSONObject();
	/**
	 * 出入境表级别：
	 */
	public JSONArray itfDclIoDecls = new JSONArray();//1.：出入境报检基本信息表
	public JSONArray itfDclIoDeclAtts = new JSONArray();//2.：出入境随附单据信息表
	public JSONArray itfDclIoDeclGoodss = new JSONArray();//2.：出入境货物产品信息表
	public JSONArray itfDclIoDeclGoods_conts = new JSONArray();//3.：出入境货物与集装箱关联信息表
	public JSONArray itfDclIoDeclGoods_limits = new JSONArray();//3.：出入境许可证信息表
	public JSONArray itfDclIoDeclGoods_packs = new JSONArray();//3.：出入境包装信息表
	public JSONArray itfDclIoDeclLimits = new JSONArray();//2.：企业资质信息表
	public JSONArray itfDclIoDeclUsers = new JSONArray();//2.：出入境使用人信息表
	public JSONArray itf_dcl_markLobs = new JSONArray();//2.：出入境报检标记号码附件表
	public JSONArray itfDclIoDeclConts = new JSONArray();//2.：出入境集装箱信息表
	public JSONArray itfDclIoDeclContDetails = new JSONArray();//3.：出入境集装箱号明细表
	
	
	public static ItfDclIoDeclBean getInstance(){
		return new ItfDclIoDeclBean();
	}
	
	public JSONObject getMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			JSONObject itfDclIoDecl = (JSONObject)itfDclIoDecls.get(0);
			if(SysUtility.isNotEmpty(itfDclIoDeclAtts) && itfDclIoDeclAtts.length() > 0){
				itfDclIoDecl.put("ITF_DCL_IO_DECL_ATT", itfDclIoDeclAtts);
			}
			if(SysUtility.isNotEmpty(itfDclIoDeclGoodss) && itfDclIoDeclGoodss.length() > 0){
				itfDclIoDecl.put("ITF_DCL_IO_DECL_GOODS", itfDclIoDeclGoodss);
				for (int i = 0; i < itfDclIoDeclGoodss.length(); i++) {
					JSONObject row = (JSONObject)itfDclIoDeclGoodss.get(i);
					
					if(SysUtility.isNotEmpty(itfDclIoDeclGoodss) && itfDclIoDeclGoods_conts.length() > 0){
						row.put("ITF_DCL_IO_DECL_GOODS_CONT", getChildData(row, itfDclIoDeclGoods_conts, "goods_id"));
					}
					if(SysUtility.isNotEmpty(itfDclIoDeclGoodss) && itfDclIoDeclGoods_limits.length() > 0){
						row.put("ITF_DCL_IO_DECL_GOODS_LIMIT", getChildData(row, itfDclIoDeclGoods_limits, "goods_id"));
					}
					if(SysUtility.isNotEmpty(itfDclIoDeclGoodss) && itfDclIoDeclGoods_packs.length() > 0){
						row.put("ITF_DCL_IO_DECL_GOODS_PACK", getChildData(row, itfDclIoDeclGoods_packs, "goods_id"));
					}
				}
			}
			if(SysUtility.isNotEmpty(itfDclIoDeclLimits) && itfDclIoDeclLimits.length() > 0){
				itfDclIoDecl.put("ITF_DCL_IO_DECL_LIMIT", itfDclIoDeclLimits);
			}
			if(SysUtility.isNotEmpty(itfDclIoDeclUsers) && itfDclIoDeclUsers.length() > 0){
				itfDclIoDecl.put("ITF_DCL_IO_DECL_USER", itfDclIoDeclUsers);	
			}
			if(SysUtility.isNotEmpty(itf_dcl_markLobs) && itf_dcl_markLobs.length() > 0){
				itfDclIoDecl.put("ITF_DCL_MARK_LOB", itf_dcl_markLobs);
			}
			if(SysUtility.isNotEmpty(itfDclIoDeclConts) && itfDclIoDeclConts.length() > 0){
				itfDclIoDecl.put("ITF_DCL_IO_DECL_CONT", itfDclIoDeclConts);
				for (int i = 0; i < itfDclIoDeclConts.length(); i++) {
					JSONObject row = (JSONObject)itfDclIoDeclConts.get(i);
					if(SysUtility.isNotEmpty(itfDclIoDeclContDetails) && itfDclIoDeclContDetails.length() > 0){
						row.put("ITF_DCL_IO_DECL_CONT_DETAIL", getChildData(row, itfDclIoDeclContDetails, "cont_id"));
					}
				}
			}
			MessageBody.put("ITF_DCL_IO_DECL", itfDclIoDecls);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	}
	
	
}
