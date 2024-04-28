package com.easy.access;

import java.sql.Connection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class OraDataAccess extends AbstractDataAccess {
	
	public OraDataAccess() {
		CurrentKey = "";
	}
	
	public OraDataAccess(String DBName) {
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
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		return super.GetTableDatas(TableName, SQL, parms, page, pagesize, KeyField);
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

	@Override
	public boolean Update(String TableName,Object objs,String KeyField, Connection conn) throws LegendException{
		return super.Update(TableName, objs, KeyField, conn);
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
