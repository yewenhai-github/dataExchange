package com.easy.access;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.ERR;
import com.easy.exception.LegendException;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewh 2017-11-06
 * 
 * @version 7.0.0
 * 
 */
public class MySqlDataAccess extends AbstractDataAccess {
	
	public MySqlDataAccess() {
		CurrentKey = "";
	}
	
	public MySqlDataAccess(String DBName) {
		CurrentKey = "";
		this.DBName = DBName;
	}
	
	@Override
	public Connection GetActiveCN() throws LegendException{
		Connection ActiveCN = super.GetActiveCN();
		if(SysUtility.isEmpty(ActiveCN)){
			ActiveCN = SysUtility.getCurrentConnection();
			SessionManager.setAttribute(SysUtility.ActiveCN, ActiveCN);
		}
		return ActiveCN;
	}
	
	@Override
	public boolean BeginTrans() throws LegendException{
		return super.BeginTrans();
	}

	@Override
	public boolean ComitTrans() throws LegendException{
		return super.ComitTrans();
	}

	@Override
	public boolean RoolbackTrans() throws LegendException{
		return super.RoolbackTrans();
	}
	
	@Override
	public String GetTable(String SQL) throws LegendException{
		return super.GetTable(SQL);
	}
	
	@Override
	public String GetTable(String TableName, String SQL) throws LegendException{
		return super.GetTable(TableName, SQL);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTable(TableName, SQL, parms);
	}
	
	@Override
	public String GetTable(String SQL, int page, int pagesize) throws LegendException{
		return super.GetTable(SQL, page, pagesize);
	}

	@Override
	public String GetTable(String TableName, String SQL, int page, int pagesize) throws LegendException{
		return super.GetTable(TableName, SQL, page, pagesize);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize) throws LegendException{
		return super.GetTable(TableName, SQL, parms, page, pagesize);
	}
	
	@Override
	public String GetTable(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTable(TableName, SQL, page, pagesize, KeyField);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		return super.GetTable(TableName, SQL, parms, page, pagesize, KeyField);
	}
		
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL) throws LegendException{
		return super.GetTableJSON(TableName, SQL);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTableJSON(TableName, SQL, parms);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize) throws LegendException{
		return super.GetTableJSON(TableName, SQL, page, pagesize);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize) throws LegendException{
		return super.GetTableJSON(TableName, SQL, parms, page, pagesize);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTableJSON(TableName, SQL, page, pagesize, KeyField);
	}
	
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize, String KeyField) throws LegendException{
		return super.GetTableJSON(TableName, SQL, parms, page, pagesize, KeyField);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL) throws LegendException{
		return super.GetTableDatas(TableName, SQL);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTableDatas(TableName, SQL, parms);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,int page,int pagesize) throws LegendException{
		return super.GetTableDatas(TableName, SQL, page, pagesize);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,Object parms,int page,int pagesize) throws LegendException{
		return super.GetTableDatas(TableName, SQL, parms, page, pagesize);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTableDatas(TableName, SQL, page, pagesize, KeyField);
	}
	
	@Override //默认为Oracle实现
	public Datas GetTableDatas(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		if(page < 1){
			page = 1;
		}
//		String nSQL = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + SQL + ") A WHERE ROWNUM <= " + ( page * pagesize ) + ") WHERE RN >= " +  ( (page-1) * pagesize + 1) ;
		String nSQL = SQL + " limit  " + ( page * pagesize) +", " + pagesize ;
//		String nSQL = "SELECT * FROM (SELECT A.*, (@rowNum:=@rowNum+1)  as  RN FROM (" + SQL + ") A ,(Select (@rowNum :=0)) b) res  WHERE RN >= " +  ( (page-1) * pagesize + 1) +" and RN <= "+ (page * pagesize );
		
		Datas data = GetTableDatas(TableName,nSQL,parms);
		JSONObject rt = data.getDataJSON();
		int rc = GetRowCount(SQL,parms);
		try {
//			rt.put("ROWCOUNT", String.valueOf(rc));
			rt.put("total", String.valueOf(rc%pagesize == 0 ? rc/pagesize : rc/pagesize + 1));
			rt.put("page", String.valueOf(page));
			rt.put("records", String.valueOf(rc));
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	public Datas GetTableDatasUI(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		return GetTableDatasUI(GetActiveCN(), TableName, SQL, parms, page, pagesize, KeyField);
	}
	
	@Override //默认为Oracle实现
	public Datas GetTableDatasUI(Connection conn,String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
//		String nSQL = "SELECT * FROM (SELECT @rowNum:=@rowNum + 1 AS RN ,A.* FROM  (" + SQL + ") A,(SELECT @rowNum:=0) B ) as A limit  " + ( page * pagesize) +", " + pagesize ;
//		String nSQL = "SELECT * FROM (SELECT A.*, (@rowNum:=@rowNum+1)  as  RN FROM (" + SQL + ") A ,(Select (@rowNum :=0)) b) res  WHERE RN >= " +  ( (page-1) * pagesize + 1) +" and RN <= "+ (page * pagesize );
		String nSQL = SQL + " limit  " + ( page * pagesize) +", " + pagesize ;
		
		Datas data = GetTableDatas(conn,TableName,nSQL,parms);
		JSONObject rt = data.getDataJSON();
		int rc = GetRowCount(conn,SQL,parms);
		try {
			rt.put("total", String.valueOf(rc));
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	@Override
	public Datas GetTableDatasUI(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField,JSONArray footer) throws LegendException{
		Datas data = GetTableDatasUI(GetActiveCN(), TableName, SQL, parms, page, pagesize, KeyField);
		try {
			data.put("footer", footer);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	
	
	@Override
	public int GetRowCount(String SQL) throws LegendException{
		return super.GetRowCount(SQL);
	}

	@Override
	public int GetRowCount(String SQL, Object parms) throws LegendException{
		return super.GetRowCount(SQL, parms);
	}

	@Override
	public boolean Exists(String SQL)throws LegendException{
		return super.Exists(SQL);
	}
	
	@Override
	public boolean Exists(String SQL, Object parms)throws LegendException{
		return super.Exists(SQL, parms);
	}
	
	@Override
	public boolean Insert(String TableName, Object objs) throws LegendException {
		return super.Insert(TableName, objs);
	}

	@Override
	public boolean Insert(String TableName, Object objs, String KeyField) throws LegendException {
		return super.Insert(TableName, objs, KeyField);
	}

	@Override
	public boolean Insert(String TableName, Object objs, String KeyField, Connection conn) throws LegendException {
		return super.Insert(TableName, objs, KeyField, conn);
	}
	
	@Override
	public boolean Update(String TableName, Object objs) throws LegendException{
		return super.Update(TableName, objs);
	}
	
	@Override
	public boolean Update(String TableName, Object objs,String KeyField) throws LegendException{
		return super.Update(TableName, objs, KeyField);
	}
	
	@Override //默认为Oracle实现
	public boolean Update(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		boolean rt = false;
		List<String> ColumnNames = SysUtility.getTableColumnNames(conn, TableName);
		HashMap<String,Integer> ColumnTypes = SysUtility.getTableColumnTypes(conn, TableName);
		
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		
		for(int i = 0 ; i<Datas.length();i++){
			try {
				JSONObject row = Datas.getJSONObject(i);
				
				
				Iterator<?> keys2 = row.keys();
				while(keys2.hasNext()){
					String key = keys2.next().toString();
					if(KeyField.equalsIgnoreCase(key)){
						Object value = row.get(key);
						if(SysUtility.isEmpty(value)) {
							SetInsertDefault(row);
						}else {
							SetUpdateDefault(row);
						}
						break;
					}
				}
				
				StringBuilder updatesql = new StringBuilder();
				StringBuilder insertsql = new StringBuilder();
				StringBuilder fields = new StringBuilder();
				StringBuilder wstr = new StringBuilder();
				ArrayList<Object> uparms = new ArrayList<Object>();
				ArrayList<Object> iparms = new ArrayList<Object>();
				
				ArrayList<String> wparms = new ArrayList<String>();
				updatesql.append("Update ");
				updatesql.append(TableName);
				updatesql.append(" Set ");
				
				insertsql.append("Insert Into ");
				insertsql.append(TableName);
				insertsql.append("(");

				
				Iterator<?> keys = row.keys();
				String usp = "";
				String isp = "";
				String wlk = "";
				boolean isinsert = true;
				String rec_ver = "";
				
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(!ColumnNames.contains(key.toUpperCase())){
						continue;//传入列在数据库表中不存在，自动忽略
					}
					if("REC_VER".equals(key.toUpperCase())){
						rec_ver = row.get(key).toString();
						continue;//版本号控制，特殊处理。
					}
					int columnType = ColumnTypes.get(key.toUpperCase());
					Object value = row.get(key);
					if(KeyField.equalsIgnoreCase(key)){
						if(SysUtility.isEmpty(value)){
							value = SysUtility.getJsonField(row, KeyField);
						}
						KeyField = key;
					}
					
					if(!KeyField.equals(key)){
						if(value.equals("")){
							updatesql.append(usp + key + "=null");							
						}else{
							if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
								updatesql.append(usp + key + "=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
							}else{
								updatesql.append(usp + key + "=?");
							}
							if(columnType == Types.BLOB){
								if(value instanceof byte[]){
									uparms.add((byte[])value);
								}else{//base64字符串
									uparms.add(value.toString().getBytes());
								}
							}else{
								uparms.add(value.toString());
							}
							//uparms.add(value.toString());
						}
						usp = ",";
					}else{
						if(value.equals("")){
							wstr.append(wlk + key + " is null");
						}else{
							if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
								wstr.append(wlk + key + "=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
							}else{
								wstr.append(wlk + key + "=?");
							}
							wparms.add(value.toString());
						}
						wlk = " AND ";
						if(value!=null && !value.equals("")){
							isinsert = false;
						}
					}
					if(!row.get(key).equals("")){
						insertsql.append(isp + key);
						if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
							fields.append(isp + "str_to_date(?,'%Y-%m-%d %H:%i:%s')");
						}else{
							fields.append(isp + "?");	
						}
						if(columnType == Types.BLOB){
							iparms.add(value.toString().getBytes());
						}else{
							iparms.add(value.toString());
						}
//						iparms.add(value.toString());
						isp = ",";
					}
				}
				StringBuffer KeyValue = new StringBuffer();
				if(isinsert){
					if(!row.has(KeyField) || SysUtility.isEmpty(row.get(KeyField))){
						int columnType = ColumnTypes.get(KeyField.toUpperCase());
						if(columnType == Types.VARCHAR){
							insertsql.insert(("Insert Into "+TableName+"(").length(),KeyField+",");
							KeyValue = new StringBuffer(SysUtility.GetUUID());
							fields.insert(0, "'"+KeyValue.toString()+"',");
						}
					}
					
					insertsql.append(") Values (");
					insertsql.append(fields.toString());
					insertsql.append(")");
					rt = ExecSQL(insertsql.toString(),(Object[]) iparms.toArray(new Object[iparms.size()]),KeyField,KeyValue,conn);
					row.put(KeyField, KeyValue.toString());
					
					LogTableHistory(SysUtility.INSERT, rt, TableName, KeyField, null, row);
				}else{
					uparms.addAll(wparms);
					if(SysUtility.isNotEmpty(rec_ver)){
						updatesql.append(",REC_VER = REC_VER + 1");
						updatesql.append(" Where ");
						updatesql.append(wstr);
						updatesql.append(" and REC_VER = "+rec_ver);
					}else{
						updatesql.append(" Where ");
						updatesql.append(wstr);
					}
					
					Datas datas = LogTableOldValue(TableName, KeyField, row);
					rt = ExecSQL(updatesql.toString(),(Object[]) uparms.toArray(new Object[uparms.size()]),null,new StringBuffer(),conn);
					LogTableHistory(SysUtility.UPDATE, rt, TableName, KeyField, datas, row);
					if(!rt){
						throw LegendException.getLegendException(ERR.DB_DATA_CONFLICT);
					}
				}
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			} catch(Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());				
			}
		}
		return rt;		
	}
	
	
	@Override
	public boolean Delete(String TableName, Object objs) throws LegendException{
		return Delete(TableName, objs, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public boolean Delete(String TableName, Object objs, String KeyField) throws LegendException{
		return Delete(TableName, objs, KeyField, GetActiveCN());
	}

	@Override
	public boolean Delete(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		return super.Delete(TableName, objs, KeyField, conn);
	}
	
	@Override
	public boolean ExecSQL(String SQL) throws LegendException{
		return super.ExecSQL(SQL);
	}
	
	@Override
	public boolean ExecSQL(String SQL,Object parms) throws LegendException{
		return super.ExecSQL(SQL, parms);
	}

	@Override
	public Datas Exec(String SQL,Object[] params) throws LegendException{
		return super.Exec(SQL, params);
	}

	@Override
	public void Despose() throws LegendException{
		super.Despose();
	}

	@Override
	public void setCurrentUser(JSONObject currentUser){
		super.setCurrentUser(currentUser);
	}
	
	@Override
	public void LogCopHistory(boolean Flag) {
		super.LogCopHistory(Flag);
	}
	
	@Override
	public String GetCurrentKey() {
		return super.GetCurrentKey();
	}
	
	@Override
	public boolean ExecSQL(String SQL,Object parms,String KeyField) throws LegendException{
		return super.ExecSQL(SQL, parms, KeyField);
	}
	
}
