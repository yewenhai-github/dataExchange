package com.easy.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;

/**
 * so-easy private
 * 
 * @author yewh 2016-12-14
 * 
 * @version 7.0.0
 * 
 */
public final class QuzrtzUtility {
	final static String ipAddress = SysUtility.getCurrentHostIPAddress();
	final static String ContextPath = AppContext.getContextPath();
	final static String AbsolutePath = AppContext.getAbsolutePath();
	
	public static synchronized HashMap GetGuardJobTask(){
		HashMap map = new HashMap();
		map.put("NAME", "cc");
		map.put("METHOD","cc");
		map.put("CLASS", "com.easy.quartz.JobGuard");
		map.put("PARAM", "void");
		map.put("QUARTZ_CRON", "2");
		map.put("QUARTZ_TYPE", "0");
		map.put("SINGLETON_FLAG", "1");
		return map;
	}
	
	public static synchronized Map GetQuartzConfigByName(final String name) throws LegendException{
		StringBuffer exsSQL = new StringBuffer();
		exsSQL.append("select * from exs_quartz_config where name = ?");
		return SQLExecUtils.query4Map(exsSQL.toString(), new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, name);
			}
		});
	}
	
	public static synchronized void ModifyQuartzClusterApp() throws SQLException{
		try {
			if(!SysUtility.TableNameExists("exs_quartz_config_cluster")){
				return;
			}
			
			
			
			List lst = GetQuartzClusterApp();
			if(SysUtility.isEmpty(lst)){
				AddQuartzClusterApp();
			}else{
				UpdateQuartzClusterApp();
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	private static synchronized List GetQuartzClusterApp() throws LegendException{
		if(SysUtility.isEmpty(ipAddress) || SysUtility.isEmpty(ContextPath)){
			return new ArrayList();
		}
		
		String SelectSQL = "select * from exs_quartz_config_cluster where client_ip_address = ? and app_context = ? and rownum = 1";
		List lst = SQLExecUtils.query4List(SelectSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, ipAddress);
				ps.setString(2, ContextPath);
			}
		});
		return lst;
	}
	
	private static synchronized void AddQuartzClusterApp() throws LegendException{
		if(SysUtility.isEmpty(ipAddress) || SysUtility.isEmpty(ContextPath)){
			return;
		}
		
		String InsertSQL = "insert into exs_quartz_config_cluster(indx,client_ip_address,app_context,absolute_path) values(seq_exs_quartz_config_cluster.nextval,?,?,?)";
		SQLExecUtils.executeUpdate(InsertSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, ipAddress);
				ps.setString(2, ContextPath);
				ps.setString(3, AbsolutePath);
			}
		});
	}
	
	private static synchronized void UpdateQuartzClusterApp() throws LegendException{
		if(SysUtility.isEmpty(ipAddress) || SysUtility.isEmpty(ContextPath)){
			return;
		}
		
		String UpdateSQL = "update exs_quartz_config_cluster set modify_time = sysdate where client_ip_address = ? and app_context = ?";
		SQLExecUtils.executeUpdate(UpdateSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, ipAddress);
				ps.setString(2, ContextPath);
			}
		});
	}
	
	
	
}