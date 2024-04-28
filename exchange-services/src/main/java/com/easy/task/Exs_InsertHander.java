package com.easy.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;

import com.easy.app.interfaces.IGlobalService;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class Exs_InsertHander extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;

	public Exs_InsertHander(String param) {
		super();
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		Connection conn = null;
		try {
			conn = SysUtility.CreateProxoolConnection("jdbc-1");
			if(SysUtility.isEmpty(conn)) {
				LogUtil.printLog("源数据库连接失败，数据库无法连接", Level.ERROR);
				return;
			}
			for (int i = 0; i < tables.length; i++) {
				Thread.sleep(2000);

				String tableName = tables[i].split("\\.")[0];
				String indxName = tables[i].split("\\.")[1];
				insertHandleSender(conn, tableName, indxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SysUtility.closeActiveCN(conn);
		}

	}

	private synchronized void insertHandleSender(Connection conn, String tableName, String indxName) throws Exception {
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select "+indxName+" from "+tableName+"  ");//where create_time > sysdate - 7   //正式环境运行一次后，再拼接上
		List list = sqlBuild.query4List(conn);
		for (int j = 0; j < list.size(); j++) {
			HashMap map = (HashMap)list.get(j);
			String indxValue = (String)map.get(indxName.toUpperCase());

			String str = SQLExecUtils.query4String("select 0 from exs_handle_sender where msg_type = '"+tableName.toUpperCase()+"' and msg_no = '"+indxValue+"'");
			if(SysUtility.isEmpty(str)){
				SQLExecUtils.executeUpdate("insert into exs_handle_sender(indx,msg_type,msg_no,msg_flag) values(?,?,?,?)",new Callback() {
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, SysUtility.GetUUID());
						ps.setString(2, tableName.toUpperCase());
						ps.setString(3, indxValue);
						ps.setString(4, "0");
					}
				});
				SysUtility.ComitTrans();
			}
		}
	}

	private static String[] tables = new String[] {
			"sgs_affiche_spotcheck.ccjcid",
			"sgs_e_sp_pledge.imporgid"};
}
