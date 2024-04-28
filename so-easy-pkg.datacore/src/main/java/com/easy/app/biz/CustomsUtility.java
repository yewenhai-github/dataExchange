package com.easy.app.biz;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class CustomsUtility {

	/**
	 * 根据datas生成报关单业务报文
	 * */
	public static String buildDecData(ServicesBean bean,Datas datas) throws LegendException{
		String[] RootNames = bean.getRootNames().split(",");
		StringBuffer dclData = new StringBuffer();
		try {
			if("DECMSG".equals(bean.getMessageType())){
				
				dclData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
				dclData.append("<DecMessage xmlns=\"http://www.chinaport.gov.cn/dec\">").append("\n");
				//DecList=DecLists,Container=DecContainers,LicenseDocu=DecLicenseDocus,TrnContainer=TrnContainers,TrnContaGoods=TrnContaGoodsList
				for (int i = 0; i < RootNames.length; i++) {
					if(RootNames[i].equals("DecHead")||RootNames[i].equals("DecFreeTxt")||RootNames[i].equals("DecSign")||RootNames[i].equals("TrnHead")||RootNames[i].equals("TrnList")||RootNames[i].equals("EdocRealation")){
						dclData.append(CustomsUtility.appendDclXmlData(RootNames[i], datas.GetTableMap(RootNames[i]),CustMapping.map));
					}else if(RootNames[i].equals("DecList")){
						dclData.append(appendDclsXmlData("DecLists", RootNames[i], datas.GetTableList(RootNames[i]),CustMapping.map));
					}else if(RootNames[i].equals("Container")){
						dclData.append(appendDclsXmlData("DecContainers", RootNames[i], datas.GetTableList(RootNames[i]),CustMapping.map));
					}else if(RootNames[i].equals("LicenseDocu")){
						dclData.append(appendDclsXmlData("DecLicenseDocus", RootNames[i], datas.GetTableList(RootNames[i]),CustMapping.map));
					}else if(RootNames[i].equals("TrnContainer")){
						dclData.append(appendDclsXmlData("TrnContainers", RootNames[i], datas.GetTableList(RootNames[i]),CustMapping.map));
					}else if(RootNames[i].equals("TrnContaGoods")){
						dclData.append(appendDclsXmlData("TrnContaGoodsList", RootNames[i], datas.GetTableList(RootNames[i]),CustMapping.map));
					}
				}
				dclData.append("</DecMessage>");
			}else if("".equals(bean.getMessageType())){
				
			}
		} catch (Exception e) {
			LogUtil.printLog("DclXml生成失败！"+e.getMessage(), Level.ERROR);
			dclData.delete(0, dclData.length());
		}
		return dclData.toString();
	}
	public static String buildDecDxpDataNoSign(String CardID,String CertNo,String dclData,String SenderId,String FileName)throws Exception {
		return buildDecDxpDataSign(CardID,CertNo, dclData, "", "", SenderId, FileName);
	}
	/**
	 * 根据报关单业务报文和加签内容，生成加签后的dxp报文
	 * */
	public static String buildDecDxpDataSign(String CardID,String CertNo,String dclData,String DigestValue,String SignatureValue,String SenderId,String FileName)throws Exception {
		String ReceiverId = "DXPEDCDEC0000001";//数据分中心的总署端Id
		
		StringBuffer dxpdata=new StringBuffer();
		
		dxpdata.append("<?xml version=\'1.0\' encoding=\'utf-8\'?><dxp:DxpMsg xmlns:dxp=\'http://www.chinaport.gov.cn/dxp\' xmlns:ds=\'http://www.w3.org/2000/09/xmldsig#\' xmlns:xsi=\'http://www.w3.org/2001/XMLSchema-instance\' ver=\'1.0\'>");
        dxpdata.append(" <dxp:TransInfo> ");
        dxpdata.append("<dxp:CopMsgId>"+SysUtility.GetUUID()+"</dxp:CopMsgId>");
        dxpdata.append("<dxp:SenderId>"+SenderId+"</dxp:SenderId>");
        dxpdata.append("<dxp:ReceiverIds><dxp:ReceiverId>"+ReceiverId+"</dxp:ReceiverId></dxp:ReceiverIds>");
        dxpdata.append("<dxp:CreatTime>"+SysUtility.getSysDate().replaceAll(" ", "T")+"Z"+"</dxp:CreatTime>");
        dxpdata.append("<dxp:MsgType>DECMSG</dxp:MsgType>");
        dxpdata.append("</dxp:TransInfo>");
        dxpdata.append("<dxp:Data>"+SysUtility.base64encoder.encode(dclData.toString().getBytes("UTF-8"))+"</dxp:Data>");
        dxpdata.append("<dxp:AddInfo><dxp:FileName>"+FileName+"</dxp:FileName>");
        dxpdata.append("<dxp:IcCard>"+CardID+"</dxp:IcCard></dxp:AddInfo>");
		dxpdata.append("<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">");
		if(SysUtility.isNotEmpty(DigestValue) && SysUtility.isNotEmpty(SignatureValue)){//加签内容
			dxpdata.append("<ds:SignedInfo>");
			dxpdata.append("<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>");
		    dxpdata.append("<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>");
	        dxpdata.append("<ds:Reference URI=\"\">");
	        dxpdata.append("<ds:Transforms>");
	        dxpdata.append("<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>");
	        dxpdata.append("</ds:Transforms>");
	        dxpdata.append("<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>");
	        dxpdata.append("<ds:DigestValue>"+DigestValue+"</ds:DigestValue>");
	        dxpdata.append("</ds:Reference>");
	        dxpdata.append("</ds:SignedInfo>");
	        dxpdata.append("<ds:SignatureValue>"+SignatureValue+"</ds:SignatureValue>");
		}
        dxpdata.append("<ds:KeyInfo>");
        dxpdata.append("<ds:KeyName>"+CertNo+"</ds:KeyName>");
        dxpdata.append("</ds:KeyInfo>");
        dxpdata.append("</ds:Signature>");
        dxpdata.append("</dxp:DxpMsg>");
		return dxpdata.toString();
	}
	public static boolean SaveDclDxpData(String dxpData,String GUID,String FileName,String MessageType,String dataSource) throws Exception{
		IDataAccess DataAccess = SessionManager.getDataAccess();
		
		JSONObject json = new JSONObject();
		json.put("UUID", SysUtility.GetUUID());
		json.put("GUID", GUID);
		json.put("MSG_TYPE", MessageType);
		json.put("MSG_NAME", FileName);
		json.put("MSG_DATA", dxpData);
		json.put("DATA_SOURCE", dataSource);
		json.put("MSG_FLAG", "0");
		json.put("SIGN_FLAG", "1");
		json.put("ENCODING", "UTF-8");
		boolean bool= DataAccess.Insert("T_CUS_DXP_DATA", json,"UUID");
		return bool;
	}
	
	/**
	 * 根据datas生成跨境电商业务报文
	 * */
	public static String buildCebData(ServicesBean bean,Datas datas) throws LegendException{
		String[] RootNames = bean.getRootNames().split(",");
		String[] IndxNames = bean.getIndxName().split(",");
		String MessageType = bean.getMessageType();
		String dxpId = bean.getSourceId();
		String XmlDocument = bean.getXmlDocument();
		
		StringBuffer cebData = new StringBuffer();
		try {
			HashMap<String, String> transferMap = new HashMap<String, String>();
			transferMap.put("copCode", datas.GetTableValue(RootNames[0], "agentCode"));
			transferMap.put("copName", datas.GetTableValue(RootNames[0], "agentName"));
			transferMap.put("dxpMode", "DXP");
			transferMap.put("dxpId", dxpId);//MQ的路由配置
			transferMap.put("note", "");

			cebData.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>").append("\n");
			cebData.append("<ceb:"+MessageType+" guid=\""+datas.GetTableValue(RootNames[0], IndxNames[0])+"\" version=\"1.0\" xmlns:ceb=\"http://www.chinaport.gov.cn/ceb\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">").append("\n");
			if(SysUtility.isNotEmpty(XmlDocument)){
				cebData.append("<ceb:"+XmlDocument+">").append("\n");
			}
			for (int j = 0; j < datas.GetTableRows(RootNames[0]); j++) {
				cebData.append(CustomsUtility.appendXmlData(RootNames[0], datas.GetTableMap(RootNames[0],j),CustMapping.map));//主表遍历
				for (int i = 1; i < RootNames.length; i++) {
					String Filter = IndxNames[0]+"="+datas.GetTableMap(RootNames[0],j).get(IndxNames[0]);
					cebData.append(CustomsUtility.appendXmlData(RootNames[i], datas.GetTableList(RootNames[i],Filter),CustMapping.map));//子表遍历
				}
			}
			if(SysUtility.isNotEmpty(XmlDocument)){
				cebData.append("</ceb:"+XmlDocument+">").append("\n");
			}
			cebData.append(CustomsUtility.appendXmlData("BaseTransfer", transferMap,CustMapping.map));//传输
			cebData.append("</ceb:"+MessageType+">");
		} catch (Exception e) {
			LogUtil.printLog("CebXml生成失败！"+e.getMessage(), Level.ERROR);
			cebData.delete(0, cebData.length());
		}
		return cebData.toString();
	}
	/**
	 * 根据业务报文，生成未加签的dxp报文
	 * */
	public static String buildCebDxpDataNoSign(String cebData,String SenderId,String MessageType)throws Exception {
		String ReceiverId = "DXPEDCDEC0000001";//数据分中心的总署端Id

		StringBuffer dxpSignData=new StringBuffer();
		dxpSignData.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		dxpSignData.append("<dxp:DxpMsg ver=\"1.0\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dxp=\"http://www.chinaport.gov.cn/dxp\">");
		dxpSignData.append(" <dxp:TransInfo>");
		dxpSignData.append("  <dxp:CopMsgId>"+SysUtility.GetUUID()+"</dxp:CopMsgId>");
		dxpSignData.append("  <dxp:SenderId>"+SenderId+"</dxp:SenderId>");
		dxpSignData.append("  <dxp:ReceiverIds>");
		dxpSignData.append("   <dxp:ReceiverId>"+ReceiverId+"</dxp:ReceiverId>");
		dxpSignData.append("  </dxp:ReceiverIds>");
		dxpSignData.append("  <dxp:CreatTime>"+SysUtility.getSysDate()+"</dxp:CreatTime>");
		dxpSignData.append("  <dxp:MsgType>"+MessageType+"</dxp:MsgType> ");
		dxpSignData.append(" </dxp:TransInfo>");
		dxpSignData.append(" <dxp:Data>"+SysUtility.base64encoder.encode(cebData.getBytes())+"</dxp:Data>");
		dxpSignData.append(" <dxp:AddInfo>");
		dxpSignData.append("<FileName>"+SysUtility.GetUUID()+"xml"+"</FileName>");
		dxpSignData.append("</dxp:AddInfo>");                    
		dxpSignData.append("</dxp:DxpMsg>");
		return dxpSignData.toString();
	}
	public static String buildCebDxpDataSign(ServicesBean bean,String cebData)throws Exception {

		
		return "";
	}
	
	
	public static boolean SaveCebDxpData(String dxpData,String GUID,String FileName,String MessageType,String dataSource) throws Exception{
		IDataAccess DataAccess = SessionManager.getDataAccess();
		
		JSONObject json = new JSONObject();
		json.put("UUID", SysUtility.GetUUID());
		json.put("GUID", GUID);
		json.put("MSG_TYPE", MessageType);
		json.put("MSG_NAME", FileName);
		json.put("MSG_DATA", dxpData);
		json.put("DATA_SOURCE", dataSource);
		json.put("MSG_FLAG", "0");
		json.put("SIGN_FLAG", "1");
		json.put("ENCODING", "UTF-8");
		boolean bool= DataAccess.Insert("T_CUST_DXP_DATA", json,"UUID");
		return bool;
	}

	public static String appendDclsXmlData(String xmlParentDocument,String xmlDocumentName,Object sourceData,HashMap<String,String[]> map){
		StringBuffer tempStr = new StringBuffer();
		tempStr.append("<"+xmlParentDocument+">");
		tempStr.append(appendDclXmlData(xmlDocumentName, sourceData, map));
		tempStr.append("</"+xmlParentDocument+">").append("\n");
		return tempStr.toString();
	}
	
	public static String appendDclXmlData(String xmlDocumentName,Object sourceData,HashMap<String,String[]> map){
		String[] mappings = map.get(xmlDocumentName);
		if(SysUtility.isEmpty(mappings)){
			return "";
		}
		StringBuffer tempXmlData = new StringBuffer();
		if(sourceData instanceof Map) {
			tempXmlData.append("<"+xmlDocumentName+">").append("\n");
			HashMap mapData = (HashMap)sourceData;
			for (int i = 0; i < mappings.length; i++) {
				if(SysUtility.isEmpty(mapData.get(mappings[i]))){
					tempXmlData.append("\t<"+mappings[i]+"></"+mappings[i]+">").append("\n");
				}else{
					tempXmlData.append("\t<"+mappings[i]+">"+mapData.get(mappings[i])+"</"+mappings[i]+">").append("\n");
				}
			}
			tempXmlData.append("</"+xmlDocumentName+">").append("\n");
		}else if(sourceData instanceof List){
			List rows = (List)sourceData;
			for (int i = 0; i < rows.size(); i++) {
				HashMap row = (HashMap)rows.get(i);
				tempXmlData.append("<"+xmlDocumentName+">").append("\n");
				for (int j = 0; j < mappings.length; j++) {
					if(SysUtility.isEmpty(row.get(mappings[j]))){
						tempXmlData.append("\t<"+mappings[j]+"></"+mappings[j]+">").append("\n");
					}else{
						tempXmlData.append("\t<"+mappings[j]+">"+row.get(mappings[j])+"</"+mappings[j]+">").append("\n");
					}
				}
				tempXmlData.append("</"+xmlDocumentName+">").append("\n");
			}
		}
		
		return tempXmlData.toString();
	}
	
	//字段按报文排序
	public static String appendXmlData(String xmlDocumentName,Object sourceData,HashMap<String,String[]> map){
		String[] mappings = map.get(xmlDocumentName);
		if(SysUtility.isEmpty(mappings)){
			return "";
		}
		StringBuffer tempXmlData = new StringBuffer();
		tempXmlData.append("<ceb:"+xmlDocumentName+">").append("\n");
		if(sourceData instanceof Map) {
			HashMap mapData = (HashMap)sourceData;
			for (int i = 0; i < mappings.length; i++) {
				tempXmlData.append("<ceb:"+mappings[i]+">"+mapData.get(mappings[i])+"</ceb:"+mappings[i]+">").append("\n");
			}
		}else if(sourceData instanceof List){
			List rows = (List)sourceData;
			for (int i = 0; i < rows.size(); i++) {
				HashMap row = (HashMap)rows.get(i);
				tempXmlData.append("<ceb:"+mappings[i]+">"+row.get(mappings[i])+"</ceb:"+mappings[i]+">").append("\n");
			}
		}
		tempXmlData.append("</ceb:"+xmlDocumentName+">").append("\n");
		return tempXmlData.toString();
	}
		
	public static Map GetAccessCustomerMap(final String MessageType,final String MessageSource,String SourceId) throws LegendException{
		List lst = GetAccessCustomerList(MessageType, MessageSource, "");
		for (int i = 0; i < lst.size(); i++) {
			Map map = (HashMap)lst.get(i);
			String tempSourceId = (String)map.get("SOURCE_ID");
			if(SysUtility.isEmpty(tempSourceId) || tempSourceId.equals(SourceId)){
				return map;
			}
		}
		return new HashMap();
	}
	public static List GetAccessCustomerList(final String MessageType,final String MessageSource,String SourceId) throws LegendException{
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_access_customer where nvl(IS_ENABLED,'1') = '1'");
		sqlBuild.append(" and message_type like ?","%"+MessageType+"%");
		if(SysUtility.isNotEmpty(MessageSource)){
			sqlBuild.append(" and message_source = ?",MessageSource);
		}
		if(SysUtility.isNotEmpty(SourceId)){
			sqlBuild.append(" and source_id = ?",SourceId);
		}
		return sqlBuild.query4List();
	}

	public static String Sha1UpdateAndEncoder(String decript) {
		 try {
			 MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			 digest.update(decript.getBytes());
			 return SysUtility.base64encoder.encode(digest.digest().toString().getBytes());
		 } catch (Exception e) {
			 LogUtil.printLog(e.getMessage(), Level.ERROR);
		 }
		 return "";
	 }
}
