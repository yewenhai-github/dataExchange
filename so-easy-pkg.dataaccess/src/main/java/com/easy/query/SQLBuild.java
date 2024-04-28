package com.easy.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class SQLBuild {
	private StringBuffer sql = new StringBuffer();
	private List params = new ArrayList();
	
	public static SQLBuild getInstance(){
		return new SQLBuild();
	}
	
	public void append(String sqlpart) {
		this.sql.append(sqlpart);
	}
	
	public void append(String sqlpart,Object param) {
		this.sql.append(" "+sqlpart);
		if(param instanceof String[]){
			String[] strs = (String[])param;
			for (int i = 0; i < strs.length; i++) {
				params.add(strs[i]);
			}
		}else if(param instanceof String){
			params.add((String)param);
		}else if(param instanceof List){
			List lst = (List)param;
			for (int i = 0; i < lst.size(); i++) {
				params.add((String)lst.get(i));
			}
		}
	}
	
	public void append(String coulmnName,String operation,Object param) {
		this.sql.append(" and");
		if(param instanceof String[]){
			String[] strs = (String[])param;
			this.sql.append(" "+coulmnName+" "+operation+" (");
			for (int i = 0; i < strs.length; i++) {
				if(i == 0){
					this.sql.append("?");
				}else{
					this.sql.append(",?");
				}
				params.add(strs[i]);
			}
			this.sql.append(") ");
		}else if(param instanceof String){
			this.sql.append(" "+coulmnName+" "+operation+" ? ");
			params.add((String)param);
		}else if(param instanceof List){
			List lst = (List)param;
			this.sql.append(" "+coulmnName+" "+operation+" (");
			for (int i = 0; i < lst.size(); i++) {
				if(i == 0){
					this.sql.append("?");
				}else{
					this.sql.append(",?");
				}
				params.add((String)lst.get(i));
			}
			this.sql.append(") ");
		}
	}
	
	public Datas query4Datas(){
		return query4Datas(SysUtility.getCurrentConnection());
	}
	
	public List query4List(){
		return query4List(SysUtility.getCurrentConnection());
	}
	
	public Map query4Map(){
		return query4Map(SysUtility.getCurrentConnection());
	}
	
	public String query4String(){
		return query4String(SysUtility.getCurrentConnection());
	}
	
	public Datas query4Datas(Connection conn){
		return query4Datas(conn, "rows");
	}
	
	public boolean query4Exists(){
		return query4Exists(SysUtility.getCurrentConnection());
	}
	
	public Datas query4Datas(Connection conn,String tableName){
		try {
			return SQLExecUtils.query4Datas(conn,tableName,sql.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					Object[] parms = new Object[params.size()];
					for (int i = 0; i < params.size(); i++) {
						parms[i] = params.get(i);
					}
					SysUtility.bindParams(ps, parms);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return new Datas();
	}
	
	public List query4List(Connection conn){
		try {
			return SQLExecUtils.query4List(conn,sql.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					Object[] parms = new Object[params.size()];
					for (int i = 0; i < params.size(); i++) {
						parms[i] = params.get(i);
					}
					SysUtility.bindParams(ps, parms);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return new ArrayList();
	}
	
	public Map query4Map(Connection conn){
		try {
			return SQLExecUtils.query4Map(conn,sql.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					Object[] parms = new Object[params.size()];
					for (int i = 0; i < params.size(); i++) {
						parms[i] = params.get(i);
					}
					SysUtility.bindParams(ps, parms);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return new HashMap();
	}
	
	public String query4String(Connection conn){
		try {
			return SQLExecUtils.query4String(conn,sql.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					Object[] parms = new Object[params.size()];
					for (int i = 0; i < params.size(); i++) {
						parms[i] = params.get(i);
					}
					SysUtility.bindParams(ps, parms);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	public boolean query4Exists(Connection conn){
		if(SysUtility.isNotEmpty(query4String(conn))){
			return true;
		}
		return false;
	}
	
	
	public boolean execute4Insert(){
		return execute4Insert(SysUtility.getCurrentConnection());
	}
	
	public boolean execute4Update(){
		return execute4Update(SysUtility.getCurrentConnection());
	}

	public boolean execute4Delete(){
		return execute4Delete(SysUtility.getCurrentConnection());
	}
	
	public boolean execute4Insert(Connection conn){
		if(SysUtility.isEmpty(sql) || sql.toString().toUpperCase().indexOf("INSERT") < 0){
			LogUtil.printLog("Insert Sql语法错误："+sql, Level.ERROR);
			return false;
		}
		return executeSql(conn);
	}
	
	public boolean execute4Update(Connection conn){
		if(SysUtility.isEmpty(sql) || sql.toString().toUpperCase().indexOf("UPDATE") < 0){
			LogUtil.printLog("Update Sql语法错误："+sql, Level.ERROR);
			return false;
		}
		return executeSql(conn);
	}

	public boolean execute4Delete(Connection conn){
		if(SysUtility.isEmpty(sql) || sql.toString().toUpperCase().indexOf("DELETE") < 0){
			LogUtil.printLog("Delete Sql语法错误："+sql, Level.ERROR);
			return false;
		}
		return executeSql(conn);
	}
	
	private boolean executeSql(Connection conn){
		try {
			return SQLExecUtils.executeUpdate(sql.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					Object[] parms = new Object[params.size()];
					for (int i = 0; i < params.size(); i++) {
						parms[i] = params.get(i);
					}
					SysUtility.bindParams(ps, parms);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return false;
	}
}
