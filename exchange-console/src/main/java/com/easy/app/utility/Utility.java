package com.easy.app.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.access.MySqlDataAccess;
import com.easy.access.OraDataAccess;
import com.easy.access.SqlDataAccess;
import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class Utility {
	public static final String INSERT_ID = "insert into t_temp_id t(id) values(?)";
	public static final String INSERT_ID1 = "insert into t_temp_id t(id1) values(?)";
	public static final String INSERT_ID2 = "insert into t_temp_id t(id1,id2) values(?,?)";
	public static final String INSERT_ID3 = "insert into t_temp_id t(id1,id2,id3) values(?,?,?)";
	public static final String INSERT_ID4 = "insert into t_temp_id t(id1,id2,id3,id4) values(?,?,?,?)";
	public static final String INSERT_ID5 = "insert into t_temp_id t(id1,id2,id3,id4,id5) values(?,?,?,?,?)";
	public static final String DELETE_ID = "delete from t_temp_id";
	public static final String UPDATE_DECL_HANDLE = "UPDATE T_DECL_HANDLE SET PROCESS_FLAG = '1' WHERE TABLE_NAME = ? AND INDX_VALUE = ?";
	public static HashMap dbTableName = new HashMap();
	public static final String DefaultOrgId = "000000";
	
	
	static {
		dbTableName.put("V_DECL_II", "T_DECL_II");
		dbTableName.put("V_DECL_II_GOODS", "T_DECL_II_GOODS");
		dbTableName.put("V_CONTAINER", "T_CONTAINER");
	}
	
	public static String[] strSplitToStrArray(StringBuffer inSql) {
		return strSplitToStrArray(inSql.toString());
	}

	/**
	 * 默认 ' , '(逗号)拆分,默认每１０００个拆分一次
	 * 
	 * @param inSql
	 *            进行拆分的字符串 如："aa,bb,cc,dd"
	 * @return String[]
	 * @throws SinoException
	 */
	public static String[] strSplitToStrArray(String inSql) {
		int itemNum = 1000;
		return strSplitToStrArray(inSql, itemNum);
	}

	/**
	 * 默认按 , (逗号)拆分
	 * 
	 * @param inSql
	 *            进行拆分的字符串 如："1,2,3……,1002"
	 * @param itemNum
	 *            每itemNum个元素拆分一次 如：1000
	 * @return String[] 返回值：{ "1,2,3……,1000" , "1001,1002" }
	 * @throws SinoException
	 */
	public static String[] strSplitToStrArray(String inSql, int itemNum) {
		if (SysUtility.isEmpty(inSql))
			return new String[] {};
		if (inSql.startsWith(",")) {
			inSql = inSql.substring(1);
		}
		if (inSql.endsWith(",")) {
			inSql = inSql.substring(0, inSql.length() - 1);
		}
		String[] bl_ids = null;
		String[] exp_bl_ids = inSql.split(",");
		int size = 0;
		if (exp_bl_ids.length > itemNum) {
			size = exp_bl_ids.length / itemNum;
			if (exp_bl_ids.length % itemNum > 0)
				size += 1;
			bl_ids = new String[size];
			for (int i = 0; i < bl_ids.length; i++) {
				bl_ids[i] = new String();
			}
			for (int i = 0; i < exp_bl_ids.length; i++) {
				bl_ids[i / itemNum] += (exp_bl_ids[i] + ",");
			}

			for (int i = 0; i < bl_ids.length; i++) {
				bl_ids[i] = bl_ids[i].substring(0, bl_ids[i].length() - 1);
			}
			return bl_ids;
		} else {
			if (inSql.startsWith(",")) {
				return new String[] { inSql.substring(1) };
			}
			if (inSql.endsWith(",")) {
				return new String[] { inSql.substring(0, inSql.length() - 1) };
			}
			return new String[] { inSql };
		}
	}
	
	public static String getCondition(String conditionValue) {
		return getCondition("INDX", conditionValue);
	}
	
	public static String getCondition(String coulmnName,String conditionValue) {
		if(conditionValue.indexOf("undefined") >= 0){
			return "INDX = -1";
		}
		
		StringBuffer condition = new StringBuffer();
        String[] ids = strSplitToStrArray(conditionValue);
		 for(int i = 0 ; i < ids.length ; i++){
			if(i == 0)
				condition.append(" "+coulmnName+" in (").append(ids[i]).append(")");
			else
				condition.append(" or "+coulmnName+" in (").append(ids[i]).append(")");
		 }
		 return condition.toString();
	}
	
	public static JSONObject setHistory(String tableName,String coulmnName,String coulmnValue,String opTypeName,String opContent){
		JSONObject temp = new JSONObject();
		try {
			temp.put("TABLE_NAME", tableName);
			temp.put("COULMN_NAME", coulmnName);
			temp.put("COULMN_VALUE", coulmnValue);
			temp.put("OP_TYPE_NAME", opTypeName);
			temp.put("OP_CONTENT", opContent);
			temp.put("OP_NAME", SysUtility.getCurrentName());
			temp.put("ORG_NO", SysUtility.getCurrentPartId());
			temp.put("DEPT_CODE", SysUtility.getCurrentDeptId());
			temp.put("DEPT_NAME", SysUtility.getCurrentDeptName());
			temp.put("CREATOR", SysUtility.getCurrentUserIndx());
			temp.put("CREATE_TIME", SysUtility.getSysDate());
		} catch (JSONException e) {
			LogUtil.printLog("addHistory业务历史表初始化出错："+e.getMessage(), Level.ERROR);
		}
		return temp;
	}
	
	public static JSONObject setReceipt(String tableName,String coulmnName,String receiptNo,String msgCode,String msgDesc){
		JSONObject receipt = new JSONObject();
		try {
			receipt.put("TABLE_NAME", tableName);
			receipt.put("COULMN_NAME", coulmnName);
			receipt.put("RECEIPT_NO", receiptNo);
			receipt.put("MSG_CODE", msgCode);
			receipt.put("MSG_DESC", msgDesc);
			receipt.put("ORG_NO", SysUtility.getCurrentPartId());
			receipt.put("DEPT_CODE", SysUtility.getCurrentDeptId());
			receipt.put("CREATOR", SysUtility.getCurrentUserIndx());
			receipt.put("CREATE_TIME", SysUtility.getSysDate());
		} catch (JSONException e) {
			LogUtil.printLog("addReceipt业务回执表初始化出错："+e.getMessage(), Level.ERROR);
		}
		return receipt;
	}
	
	/**
	 * 定时任务表
	 * *****/
	public static void InsertDeclHandle(IDataAccess DataAccess, String handleType,String handleNo)throws JSONException, LegendException{
		StringBuffer AddSQL = new StringBuffer();
		AddSQL.append("insert into t_decl_handle(indx,handle_type,handle_no) values(?,?,?) ");
		DataAccess.ExecSQL(AddSQL.toString(),new String[]{handleType,handleNo});
	}
	
	/**
	 * 插入状态历史
	 * *****/
	public static void InsertHistoryStatus(IDataAccess DataAccess, String TABLE_NAME,String COULMN_NAME,
				String COULMN_VALUE,String OP_TYPE_NAME,String OP_CONTENT)throws JSONException, LegendException{
		JSONObject data = new JSONObject();
		data.put("TABLE_NAME", TABLE_NAME);
		data.put("COULMN_NAME", COULMN_NAME);
		data.put("COULMN_VALUE", COULMN_VALUE);
		data.put("OP_TYPE_NAME", OP_TYPE_NAME);
		data.put("OP_CONTENT", OP_CONTENT);
		data.put("OP_NAME", SysUtility.getCurrentName());
		data.put("DEPT_CODE", SysUtility.getCurrentDeptId());
		data.put("DEPT_NAME", SysUtility.getCurrentDeptName());
		data.put("ORG_ID", SysUtility.getCurrentPartId());
		data.put("CREATOR", SysUtility.getCurrentUserIndx());
		DataAccess.Insert("T_HISTORY_STATUS", data);
	}
	
	/**
	 * 插入回执
	 * *****/
	public static void InsertEntReceipt(IDataAccess DataAccess, String receiptType,String receiptNo,
			String msgCode,String msgName,String msgDesc,String reciptCode,String entRegNo)throws JSONException, LegendException{
		JSONObject data = new JSONObject();
		data.put("RECEIPT_TYPE", receiptType);
		data.put("RECEIPT_NO", receiptNo);
		data.put("MSG_CODE", msgCode);
		data.put("MSG_NAME", msgName);
		if(msgDesc.getBytes().length >= 2000){
			msgDesc = msgDesc.substring(0, 1500)+"...";
		}
		if(SysUtility.isNotEmpty(entRegNo)){
			data.put("ATTRIBUTE6", entRegNo);
		}
		data.put("MSG_DESC", msgDesc);
		data.put("RECIPT_CODE", reciptCode);
		DataAccess.Insert("R_RECEIPT", data);
	}
	
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值
	public static void DeleteTempId(IDataAccess DataAccess) throws LegendException{
		DataAccess.ExecSQL(DELETE_ID);
	}
		
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值
	public static void InsertTempId(IDataAccess DataAccess,String Id1,String Id2,String Id3,String Id4) throws LegendException{
		DataAccess.ExecSQL(INSERT_ID4, new String[]{Id1,Id2,Id3,Id4});
	}
	
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值 ID5:子表流水号
	public static void InsertTempId(IDataAccess DataAccess,String Id1,String Id2,String Id3,String Id4,String Id5) throws LegendException{
		DataAccess.ExecSQL(INSERT_ID5, new String[]{Id1,Id2,Id3,Id4,Id5});
	}
		
	public static void InsertHistoryStatusByTemp(IDataAccess DataAccess, String TableName,String CoulmnName,String opTypeName,String opContent) throws LegendException{
		if(opContent.getBytes().length >= 2000){
			opContent = opContent.substring(0, 1500)+"...";
		}
		
		StringBuffer AddSQL = new StringBuffer();
		AddSQL.append("INSERT INTO T_HISTORY_STATUS(INDX,TABLE_NAME,COULMN_NAME,COULMN_VALUE,OP_TYPE_NAME,OP_CONTENT,OP_NAME,DEPT_NAME,ORG_ID,CREATOR)");
		AddSQL.append("SELECT SEQ_T_HISTORY_STATUS.NEXTVAL,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ID1,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?");
		AddSQL.append("  FROM T_TEMP_ID");
		Object Params = new Object[]{TableName,CoulmnName,opTypeName,opContent,SysUtility.getCurrentName(),SysUtility.getCurrentDeptName(),SysUtility.getCurrentPartId(),SysUtility.getCurrentUserIndx()};
		DataAccess.ExecSQL(AddSQL.toString(),Params);
	}
	
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值
	/*public static void InsertTempReceiptByTemp(IDataAccess DataAccess, String receiptType,String msgCode,String msgName,
			String msgDesc)throws JSONException, LegendException{
		StringBuffer AddSQL = new StringBuffer();
		AddSQL.append("INSERT INTO TEMP_RECEIPTS(INDX,MSG_ID,MSG_REF,MSG_TYPE,MSG_CODE,MSG_DESC,MSG_GEN_DATE,CREATOR,PART_ID)");
		AddSQL.append("SELECT SEQ_TEMP_RECEIPT.NEXTVAL,");
		AddSQL.append("       ID2,");
		AddSQL.append("       ID1,");
		AddSQL.append("       '"+receiptType+"',");
		AddSQL.append("       '"+msgCode+"',");
		AddSQL.append("       '"+msgDesc+"',");
		AddSQL.append("       Now(),");
		AddSQL.append("       '"+SysUtility.getCurrentUserIndx()+"',");
		AddSQL.append("       '"+SysUtility.getCurrentPartId()+"'");
		AddSQL.append("  FROM T_TEMP_ID");
		DataAccess.ExecSQL(AddSQL.toString());
	}*/
		
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值
	public static void InsertMfReceiptByTemp(IDataAccess DataAccess, String receiptType,String msgCode,String msgName,
			String msgDesc)throws JSONException, LegendException{
		StringBuffer AddSQL = new StringBuffer();
		AddSQL.append("INSERT INTO MF_RECEIPTS(INDX,MSG_ID,MSG_REF,MSG_TYPE,MSG_CODE,MSG_DESC,MSG_GEN_DATE,CREATOR,PART_ID)");
		AddSQL.append("SELECT SEQ_MF_RECEIPT.NEXTVAL,");
		AddSQL.append("       ID2,");
		AddSQL.append("       ID1,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       ?,");
		AddSQL.append("       Now(),");
		AddSQL.append("       ?,");
		AddSQL.append("       ?");
		AddSQL.append("  FROM T_TEMP_ID");
		Object Params = new Object[]{receiptType,msgCode,msgDesc,SysUtility.getCurrentUserIndx(),SysUtility.getCurrentPartId()};
		DataAccess.ExecSQL(AddSQL.toString(),Params);
	}
	
	//ID1:流水号  ID2:备案号 ID3:回执方 ID4: 业务主键值
	public static void InsertDeclHandleByTemp(IDataAccess DataAccess, String handleType)throws JSONException, LegendException{
		StringBuffer AddSQL = new StringBuffer();
		AddSQL.append("insert into exs_handle_sender(indx,msg_type,msg_no) ");
		AddSQL.append("select seq_exs_handle_sender.nextval,");
		AddSQL.append("       ?,");
		AddSQL.append("       ID2");
		AddSQL.append("  FROM T_TEMP_ID");
		DataAccess.ExecSQL(AddSQL.toString(),handleType);
	}
	
	public static String GetIndxConditions(JSONArray params) throws JSONException{
		StringBuffer indxs = new StringBuffer();
		for (int i = 0; i < params.length(); i++) {
			JSONObject jobj = (JSONObject)params.get(i);
			indxs.append(","+SysUtility.getJsonField(jobj, "INDX"));
		}
		if(SysUtility.isEmpty(indxs)){
			return "";
		}
		String[] strIndxs = Utility.strSplitToStrArray(indxs.substring(1));
		for (int i = 0; i < strIndxs.length; i++) {
			String[] strs = strIndxs[i].split(",");
			for (int j = 0; j < strs.length; j++) {
				strs[j] = "'"+strs[j]+"'";
			}
			strIndxs[i] = Arrays.toString(strs);
		}
		StringBuffer conditions = new StringBuffer();
		conditions.append(" and(");
		for (int i = 0; i < strIndxs.length; i++) {
			String str = strIndxs[i];
			if(str.indexOf("[") >= 0) {
				str = str.substring(str.indexOf("[")+1, str.length()-1);
			}
			if(i == 0){
				conditions.append("indx in("+str+")");
			}else{
				conditions.append(" or indx in("+str+")");
			}
		}
		conditions.append(")");
		if(SysUtility.isEmpty(conditions) || SysUtility.isEmpty(strIndxs) || strIndxs.length == 0){
			return "";
		}
		return conditions.toString();
	}
	public static String GetIdConditionsToPid(JSONArray params) throws JSONException{
		StringBuffer indxs = new StringBuffer();
		for (int i = 0; i < params.length(); i++) {
			JSONObject jobj = (JSONObject)params.get(i);
			indxs.append(","+SysUtility.getJsonField(jobj, "INDX"));
		}
		if(SysUtility.isEmpty(indxs)){
			return "";
		}
		String[] strIndxs = Utility.strSplitToStrArray(indxs.substring(1));
		StringBuffer conditions = new StringBuffer();
		conditions.append(" and(");
		for (int i = 0; i < strIndxs.length; i++) {
			if(i == 0){
				conditions.append("p_indx in("+strIndxs[i]+")");
			}else{
				conditions.append(" or p_indx in("+strIndxs[i]+")");
			}
		}
		conditions.append(")");
		if(SysUtility.isEmpty(conditions) || SysUtility.isEmpty(strIndxs) || strIndxs.length == 0){
			return "";
		}
		return conditions.toString();
	}
	
	public static IDataAccess getDataAccessByDBName(String dbName){
		IDataAccess dataAccess = null;
		if (SysUtility.IsSQLServerDB()){
			dataAccess = new SqlDataAccess(dbName);
		} else if (SysUtility.IsOracleDB()){
			dataAccess = new OraDataAccess(dbName);
		}else if (SysUtility.IsMySqlDB()){
			dataAccess = new MySqlDataAccess(dbName);
		}
		return dataAccess;
	}
	
	
	public static HashMap AuditTableAllDB(JSONArray params,String DBTableName,Object obj) throws LegendException, JSONException{
		int SuccessCount = 0;//成功总数。
		int ErrorCount = 0;//失败总数。
		HashMap SchemaMap = SysUtility.GetProxoolSchema(obj, "proxool.properties");
		
		HashMap map = new HashMap();
		Iterator<Map.Entry<Integer, String>> it = SchemaMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> entry = it.next();
			map.put(entry.getValue(), entry.getKey());
		}
		
		for (int i = 0; i < params.length(); i++) {
			JSONObject row = (JSONObject)params.get(i);
			IDataAccess dataAccess = null;
			try {
				String Schema = SysUtility.getJsonField(row, "Schema");
				String INDX = SysUtility.getJsonField(row, "INDX");
				if(SysUtility.isEmpty(Schema) || SysUtility.isEmpty(INDX)) {
					ErrorCount++;
					continue;
				}
				String dbName = (String)map.get(Schema);
				dataAccess = Utility.getDataAccessByDBName(dbName);
				boolean falg = dataAccess.ExecSQL("update "+DBTableName+" set AUDIT_FLAG = '1' where INDX='"+INDX+"'");
				if(falg){					
					SuccessCount++;
				}else{
					ErrorCount++;
				}
			} catch (Exception e) {
				dataAccess.RoolbackTrans();
			} finally{			
				dataAccess.ComitTrans();
			}
		}
		
		HashMap rtmap = new HashMap();
		rtmap.put("SuccessCount", SuccessCount);
		rtmap.put("ErrorCount", ErrorCount);
		return rtmap;
	}
	
	
}
