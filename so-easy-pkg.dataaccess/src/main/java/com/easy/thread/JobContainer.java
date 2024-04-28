package com.easy.thread;

import org.apache.log4j.Level;

import com.easy.access.MySqlDataAccess;
import com.easy.access.OraDataAccess;
import com.easy.access.SqlDataAccess;
import com.easy.constants.Constants;
import com.easy.exception.LegendException;
import com.easy.session.SessionKeyType;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private 异步任务 容器
 * 
 * @author yewh 2015-09-26
 * 
 * @version 7.0.0
 * 
 */
public class JobContainer {

	private static final JobContainer INSTANCE = new JobContainer();
	
	private JobContainer() {
	}

	public static JobContainer getInstance() {
		return INSTANCE;
	}

	/**
	 * 添加到工作队列 目前是简单的实现，以后考虑增强
	 * 
	 * @param detail
	 */
	public synchronized void addToList(JobDetail detail) {
		new Job(detail).start();
	}

	private static class Job extends Thread {

		private JobDetail detail;

		private Job(JobDetail detail) {
			this.detail = detail;
		}

		public synchronized void run() {
			SessionManager.setAttribute(SessionKeyType.SESSION_OPERATOR, detail.getOperator());
			SessionManager.setAttribute(SessionKeyType.SESSION_TXTIME, SysUtility.getSysDate());
			if (SysUtility.IsSQLServerDB()){
				SessionManager.setDataAccess(new SqlDataAccess());
			}else if (SysUtility.IsOracleDB()){
				SessionManager.setDataAccess(new OraDataAccess());
			}else {
				SessionManager.setDataAccess(new MySqlDataAccess());
			}
			try {
				detail.run();
			} catch (Exception e) {
				LogUtil.printLog("线程执行出错:"+e.getMessage(), Level.ERROR);
				try {
					SysUtility.RoolbackTrans(SysUtility.getCurrentConnection());
				} catch (LegendException e1) {
					
				}
			}finally {
				SysUtility.closeActiveCN(SysUtility.getCurrentConnection());
				SessionManager.destorySession();
			}
		}
	}
}
