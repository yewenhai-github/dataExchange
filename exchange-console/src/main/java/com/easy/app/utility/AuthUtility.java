package com.easy.app.utility;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class AuthUtility {
	
	public static String isUserRoot(){
		try {
			String GetRootSql="select is_root from exs_auth_user t WHERE t.org_id = ? and t.username = ?";
			final String orgId = SysUtility.getCurrentOrgId();
			final String userName = SysUtility.getCurrentUserName();
			return SQLExecUtils.query4String(GetRootSql, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, orgId);
					ps.setString(2, userName);
				}
			});
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "N";
	}
	
}
