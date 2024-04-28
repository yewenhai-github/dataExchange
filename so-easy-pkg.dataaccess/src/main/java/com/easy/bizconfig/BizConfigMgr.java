package com.easy.bizconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.ResultSetWrap;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * 业务配置信息管理,cache配置信息,并对外提供方法获得配置信息; 设计为singleton;
 */
final class BizConfigMgr {
	/**
	 * 公司配置信息类型
	 * 1 := 前台使用 2 := 前台and后台 3 := 后台
	 */
	private static final String SEPERATOR = "|";

	private BizConfigMgr() {
	}

	/**
	 * 静态实例对象，以保证单一实例
	 */
	private static BizConfigMgr INSTANCE ;

	private  Map CONFIG = new TreeMap();

	/**
	 * 取得实例的工厂方法，确保单例模式
	 * 
	 * @return BizConfigMgr
	 */
	public static synchronized  BizConfigMgr getInstance() {
		if(INSTANCE == null){
			 INSTANCE = new BizConfigMgr();
		}
		return INSTANCE;
	}

	String getConfigValue(String code, String orgId, int bizLevel) {
		String key = buildKey(code, orgId, bizLevel);
		String value = (String) CONFIG.get(key);
		if (value == null && !CONFIG.containsKey(key)) {
			value = (String) CONFIG.get(buildKey(code, "0", bizLevel));
		}
		return (value == null) ? SysUtility.EMPTY : value;
	}

	private ResultSetWrap loadData(Connection conn) throws LegendException {
		return SQLExecUtils.query4Wrap(conn, getSql(), SQLExecUtils.EMPTY_CALLBACK, 2000);
	}

	private void buildMap(ResultSetWrap wrap) {
		CONFIG.clear();
		while (wrap.next()) {
			CONFIG.put(wrap.get("K"), wrap.get("V"));
		}
	}

	public static String buildKey(String code, String orgId, int bizLevel) {
		return orgId + SEPERATOR + bizLevel + SEPERATOR + code;
	}

	void init() {
		Connection conn = null;
		try {
			conn = SysUtility.getCurrentConnection();
			buildMap(loadData(conn));
			LogUtil.printLog("分参配置S_SYS_BIZ_CONFIG初始化成功！", Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog("分参配置表S_SYS_BIZ_CONFIG读取出错！"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeActiveCN(conn);
			SessionManager.destorySession();
		}
	}

	private static String getSql() {
		StringBuffer sql = new StringBuffer(150);
		sql.append("select b.org_id || '|' || b.biz_level || '|' || b.sc_code k,b.sc_value v");
		sql.append("  from s_sys_biz_config b");
		sql.append(" where b.use_type in (1,2,3)");
		return sql.toString();
	}
	
	void reloadSingleCfg(String key,String cbic_sc_value) {
		CONFIG.put(key, cbic_sc_value);
	}
	
	protected static String reloadCfgBySql(final String Cbic_org_id,final String Cbic_biz_level,final String Cbic_sc_code) throws LegendException{
		StringBuffer sql = new StringBuffer(150);
		sql.append(" select SC_VALUE V ");
		sql.append("  from s_sys_biz_config ");
		sql.append(" where use_type in (1,2,3)");
		sql.append("   and org_id = ? ");
		sql.append("   and biz_level = ? ");
		sql.append("   and sc_code = ? ");
		Connection conn = null;
		String result = "";
		try {
			conn = SysUtility.getCurrentConnection();
			result = SQLExecUtils.query4String(conn,sql.toString(), new Callback() {
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, Cbic_org_id); 
					ps.setString(2, Cbic_biz_level);
					ps.setString(3, Cbic_sc_code);
				}
			});
		} catch (Exception e1) {
			LogUtil.printLog("分参s_sys_biz_config数据库查询错误!", LogUtil.ERROR);	
		}finally {
			SysUtility.closeActiveCN(SysUtility.getCurrentConnection());
		}
		return result;
	}
	
	
}
