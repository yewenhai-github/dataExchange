package com.easy.app.biz;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.query.SQLBuild;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class InspectionUtility {
	
	/***企业端报检调用ECIQ最新回执数据接入方法
	 * @param DataAccess 数据库连接对象
	 * @param techRegCode 组织机构代码
	 * @param declNo 报检单号
	 * @param url 调用地址，如：http://localhost:8080/OBORExpDispatcher-DEV
	 * @param signData 密钥
	 * @param messageSource 接入方代码
	 * */
	public static boolean SaveEciqReceived(IDataAccess DataAccess,String techRegCode,String declNo,String url,String signData,String messageSource){
		if(SysUtility.isEmpty(DataAccess) || SysUtility.isEmpty(techRegCode) || SysUtility.isEmpty(declNo) || SysUtility.isEmpty(url) || SysUtility.isEmpty(signData) || SysUtility.isEmpty(messageSource)){
			return false;
		}
		try {
			String XmlData = "MESSAGE_TYPE=EEntDeclIo|MESSAGE_SOURCE="+messageSource+"|MESSAGE_DEST=OBORExpInsp|SIGN_DATA="+signData+"|SERIAL_NAME=RESPONSE_ID|SERVER_NAME=GetEciqMsg|RECEIVED_MODE=1";
			String searchTable = "TECH_REG_CODE="+techRegCode+"|DECL_NO="+declNo+"";
			
			ApiClientUtility.invokeMessageByClient(url+"/InvokeMessage", XmlData, searchTable);
			String StringResult = ApiClientUtility.getMessageByClient(url+"/GetMessage",XmlData,searchTable);//1、获取字符串
			
			if("代码:01;没有符合条件的数据！".equals(StringResult)){
				return true;//
			}
			JSONArray Stringrows = new JSONArray(StringResult);
			JSONArray rows = (JSONArray)((JSONObject)((JSONObject)Stringrows.get(0)).get("MessageBody")).get("DCL_RESPONSE");
			for (int i = 0; i < rows.length(); i++) {
				try {
					JSONObject row = (JSONObject)rows.get(i);

					declNo = SysUtility.getJsonField(row, "DeclNo");
					String ResponseId = SysUtility.getJsonField(row, "RESPONSE_ID");
					SQLBuild sqlBuild = SQLBuild.getInstance();
					sqlBuild.append("select 0 from itf_dcl_receipts where response_id = ?",ResponseId);
					List lst = sqlBuild.query4List();
					if(lst.size() > 0){
						continue;
					}
					sqlBuild = SQLBuild.getInstance();
					sqlBuild.append("select tech_reg_code from itf_dcl_io_decl where decl_no = ?",declNo);
					Map declMap = sqlBuild.query4Map();
					
					Datas data = new Datas();
					data.put("DECL_NO", declNo);
					data.put("RSP_CODES", SysUtility.getJsonField(row, "RspCodes"));
					data.put("RSP_INFO", SysUtility.getJsonField(row, "RspInfo"));
					data.put("RSP_GEN_TIME", SysUtility.getJsonField(row, "RspGenTime"));
					data.put("RSP_SEND_TIME", SysUtility.getJsonField(row, "RspSendTime"));
					data.put("RESPONSE_ID", SysUtility.getJsonField(row, "RESPONSE_ID"));
					data.put("DECL_NO_TYPE", SysUtility.getJsonField(row, "DeclNoType"));
					data.put("RSP_NO", SysUtility.getJsonField(row, "RspNo"));
					data.put("DECL_REG_NO", SysUtility.getJsonField(row, "DeclRegNo"));
					data.put("SENDER_ADDR", SysUtility.getJsonField(row, "SenderAddr"));
					data.put("RSP_KIND", SysUtility.getJsonField(row, "RspKind"));
					data.put("DECL_GET_NO", SysUtility.getJsonField(row, "DeclGetNo"));
					data.put("RSP_STATE", SysUtility.getJsonField(row, "RspState"));
					data.put("ADD_INFO", SysUtility.getJsonField(row, "AddInfo"));
					data.put("OPER_TIME", SysUtility.getJsonField(row, "OperTime"));
					data.put("ENT_UUID", SysUtility.getJsonField(row, "EntUuid"));
					data.put("SEND_ORG_CODE", "");
					data.put("TECH_REG_CODE", declMap.get("TECH_REG_CODE"));
					data.put("ECIQ_USER_NAME", declMap.get("ECIQ_USER_NAME"));
					data.put("MESSAGE_SOURCE", declMap.get("MESSAGE_SOURCE"));
					DataAccess.Insert("ITF_DCL_RECEIPTS", data, "RESPONSE_ID");
					
					final String RspCodes = SysUtility.getJsonField(row, "RspCodes");
					final String RspInfo = SysUtility.getJsonField(row, "RspInfo");
					String preDeclNo = ZzStringUtil.getFindMsg(RspInfo, ".*企业申请流水号.*[:：]\\s*([0-9]+).*");//预录入号
					String declGetNo = ZzStringUtil.getFindMsg(RspInfo, ".*报检号.*[:：]\\s*([0-9]+).*");//正式报检号
					String passCode = ZzStringUtil.getFindMsg(RspInfo, ".*电子通关单号：\\s*([0-9]+).*");//通关单号
					String prepassCode="";
					if(RspInfo.indexOf("将由")>0){
						prepassCode = RspInfo.substring(RspInfo.indexOf("将由")+2,RspInfo.indexOf("将由")+17)+"000";//预通关单号 
					}
					String DeclGetNo = SysUtility.getJsonField(row, "DeclGetNo");
					String RspName=RspInfo.substring(0,4);
					if(SysUtility.isEmpty(DeclGetNo)){
						DeclGetNo="";
					}
					//调取修改报检单状态方法
					boolean isFlag = UpdateEciqReceived(DataAccess,declNo,RspCodes,RspName,DeclGetNo,prepassCode,passCode);
			        if(!isFlag){
			        	return false;
			        }
				} catch (JSONException e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
					return false;
				}
			}
			//3、删除数据
			ApiClientUtility.deleteMessageByClient(url+"/DeleteMessage", XmlData, StringResult);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return false;
		}
		return true;
	}
	
	
	/***企业端报检调用ECIQ最新回执数据接入方法
	 * @param DataAccess 数据库连接对象
	 * @param declNo 报检单号
	 * @param RspCodes 报文类型
	 * @param RspName 是否的回执
	 * @param DeclGetNo 报检单号
	 * @param prepassCode 预通关单号
	 * @param passCode 通关单号
	 * */
	public static boolean UpdateEciqReceived(IDataAccess DataAccess,String declNo,String RspCodes,String RspName,String DeclGetNo,String prepassCode,String passCode){
		if(SysUtility.isEmpty(declNo)){
			return false;
		}
		try {

			StringBuffer UpdateSql = new StringBuffer();
			UpdateSql.append("update itf_dcl_io_decl set ");
			StringBuffer UpdateDI = new StringBuffer();
			UpdateDI.append("update itf_dcl_io_decl set ");
			StringBuffer condition = new StringBuffer();
			String decl_status_code="0";
			if(RspCodes.equals("1001")||RspCodes.equals("1002")||RspCodes.equals("1003")||RspCodes.equals("1004")){
				UpdateDI.append(" decl_status_code = '2',");
				UpdateDI.append("decl_status_name='上传失败'");
				UpdateSql.append(" decl_status_code = '2'");
				decl_status_code="2";
			}else if(RspCodes.equals("1000")||(RspCodes.equals("0000")&&RspName.equals("报检成功"))){
				UpdateDI.append(" decl_status_code = '3',");
				UpdateDI.append("decl_status_name='Eciq预录入'");
				if(SysUtility.isNotEmpty(DeclGetNo)){
					UpdateDI.append(",INPUT_SERIAL='"+DeclGetNo+"'"); 
					UpdateSql.append("INPUT_SERIAL='"+DeclGetNo+"'"); 
				}else{
					UpdateSql.append("INPUT_SERIAL=''"); 
				}
				decl_status_code="3";
			}else if(RspCodes.equals("1401")){
				UpdateDI.append(" decl_get_no='"+DeclGetNo+"'"); 
				UpdateSql.append(" decl_get_no='"+DeclGetNo+"'"); 
				decl_status_code="4";
			}else if(RspCodes.equals("1100")||RspCodes.equals("1200")){
				UpdateDI.append(" decl_status_code = '4',");
				UpdateDI.append("decl_status_name='审单通过',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				if(prepassCode.length()==18){
					UpdateDI.append(",PRE_CUSTOMS_NO='"+prepassCode+"'");
					UpdateSql.append(",PRE_CUSTOMS_NO='"+prepassCode+"'");
				}
				decl_status_code="4";
			}else if(RspCodes.equals("1300")||RspCodes.equals("2300")||RspCodes.equals("2500")){
				UpdateDI.append(" decl_status_code = '5',");
				UpdateDI.append("decl_status_name='受理通过',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				if(prepassCode.length()==18){
					UpdateDI.append(",PRE_CUSTOMS_NO='"+prepassCode+"'");
					UpdateSql.append(",PRE_CUSTOMS_NO='"+prepassCode+"'");
				}
				decl_status_code="5";
			}else if(RspCodes.equals("1400")){
				UpdateDI.append(" decl_status_code = '6',");
				UpdateDI.append("decl_status_name='施检分单',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				decl_status_code="6";
			}else if(RspCodes.equals("1500")){
				UpdateDI.append(" decl_status_code = '7',");
				UpdateDI.append("decl_status_name='查验指令',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				decl_status_code="7";
			}else if(RspCodes.equals("2000")){
				UpdateDI.append(" decl_status_code = '8',");
				UpdateDI.append("decl_status_name='放行成功',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				decl_status_code="8";
			}else if(RspCodes.equals("1101")||RspCodes.equals("1201")||RspCodes.equals("1301")||RspCodes.equals("1901")){
				UpdateDI.append(" decl_status_code = '9',");
				UpdateDI.append("decl_status_name='报检失败',");
				UpdateDI.append("decl_get_no='"+DeclGetNo+"'");
				UpdateSql.append("decl_get_no='"+DeclGetNo+"'");
				decl_status_code="9";
			}else if(RspCodes.equals("3000")){ 
				UpdateDI.append(" decl_status_code = '10',");
				UpdateDI.append("decl_status_name='已通关',");
				UpdateDI.append("pass_code='"+passCode+"'"); 
				UpdateSql.append("pass_code='"+passCode+"'"); 
				decl_status_code="10";
			}else{ 
				if((RspCodes.equals("0000")&&RspName.equals("报检失败"))||!RspCodes.substring(RspCodes.length()-3).equals("000"))
				{
					UpdateDI.append(" decl_status_code = '9',");
					UpdateDI.append("decl_status_name='报检失败'");
					UpdateSql.append(" decl_status_code = '9'");
					decl_status_code="11";
				} 
			} 
			UpdateDI.append(" where decl_no = ? and decl_status_code < "+ decl_status_code);
			UpdateSql.append(" where decl_no = ? and decl_status_code > "+ decl_status_code);
			DataAccess.ExecSQL(UpdateDI.toString(), declNo);
			DataAccess.ExecSQL(UpdateSql.toString(), declNo);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return false;
		}
		return true;
	}
}
