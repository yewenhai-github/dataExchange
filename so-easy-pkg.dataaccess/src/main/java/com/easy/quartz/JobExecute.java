package com.easy.quartz;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easy.access.MySqlDataAccess;
import com.easy.access.OraDataAccess;
import com.easy.access.SqlDataAccess;
import com.easy.constants.Constants;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionKeyType;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.QuzrtzUtility;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class JobExecute {
	/**
	 * QuartzServlet对应每一条定时任务，启动一个MainQuartz，调用execute方法。
	 * 
	 * @throws SQLException
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String instName = context.getJobDetail().getName();
		// get jobDataMap from JobDetail
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		// get param from JobDataMap
		
		String cronJobName = dataMap.getString("CronJobName");
		String cronClassName = dataMap.getString("CronClassName");
		String cronMethodName = dataMap.getString("CronMethodName");
		String cronParamName = dataMap.getString("CronParamName");
		String cronIpAddress = dataMap.getString("CronIpAddress");
		LogUtil.printLog("定时程序:"+cronClassName+"."+cronMethodName+"("+cronParamName+")"+"执行开始", Level.INFO);
		SessionManager.setAttribute(SessionKeyType.SessionCronJobName, cronJobName);
		SessionManager.setAttribute(SessionKeyType.SessionCronClassName, cronClassName);
		SessionManager.setAttribute(SessionKeyType.SessionCronMethodName, cronMethodName);
		SessionManager.setAttribute(SessionKeyType.SessionCronIpAddress, cronIpAddress);
		
		if (SysUtility.IsSQLServerDB()){
			SessionManager.setDataAccess(new SqlDataAccess());
		} else if (SysUtility.IsOracleDB()){
			SessionManager.setDataAccess(new OraDataAccess());
		}else {
			SessionManager.setDataAccess(new MySqlDataAccess());
		}
		
		Connection conn = SysUtility.getCurrentConnection();
		/*if(SysUtility.isEmpty(conn)){
			LogUtil.printLog("JobExecute.execute() error"+"数据库连接为空", Level.ERROR);
			return;
		}*/
		try {
			//缓存帐号信息
			SysUtility.loadOperator(conn);
			// 实例化Daemon类和方法
			Class cController = Class.forName(cronClassName);
			Object oController = cController.newInstance();
			Method method = cController.getMethod(cronMethodName, new Class[] { String.class });
			try {
				method.invoke(oController, new Object[] { cronParamName });
			} catch (Exception e) {
				e.printStackTrace();
				
				LogUtil.printLog("定时程序执行错误："+oController+"."+method.getName()+" "+SysUtility.getStackTrace(e), Level.ERROR);
			}
			if(SysUtility.getDBPoolClose()){
				LogUtil.printLog("定时程序："+cronClassName+"."+cronMethodName+"("+cronParamName+")"+"执行结束", Level.INFO);
				return;
			}
			StringBuffer SQL = new StringBuffer();
			SQL.append("update exs_quartz_config set rec_ver = rec_ver + 1");
			if (SysUtility.IsSQLServerDB()){
				SQL.append(",modify_time = getdate()");
			}else if (SysUtility.IsOracleDB()){
				SQL.append(",modify_time = sysdate");
			}else{
				SQL.append(",modify_time = now()");
			}
			SQL.append(" where name = ? ");
			SQLExecUtils.executeUpdate(conn, SQL.toString(), SQLExecUtils.parmsToSetter(cronJobName));
			/*********1.集群配置处理********/
//			QuzrtzUtility.ModifyQuartzClusterApp();
			LogUtil.printLog("定时程序："+cronClassName+"."+cronMethodName+"("+cronParamName+")"+"执行结束", Level.INFO);
		} catch (ClassNotFoundException e) {
			LogUtil.printLog("ClassNotFoundException:"+e.getMessage(), Level.ERROR);
		} catch (NoSuchMethodException e) {
			LogUtil.printLog("NoSuchMethodException:"+e.getMessage(), Level.ERROR);
		} catch (IllegalArgumentException e) {
			LogUtil.printLog("IllegalArgumentException:"+e.getMessage(), Level.ERROR);
		} catch (InstantiationException e) {
			LogUtil.printLog("InstantiationException:"+e.getMessage(), Level.ERROR);
		} catch (IllegalAccessException e) {
			LogUtil.printLog("IllegalAccessException:"+e.getMessage(), Level.ERROR);
		} catch (Exception e) {
			LogUtil.printLog("Exception:"+e.getMessage(), Level.ERROR);
		} finally{
			try {
				SysUtility.ComitTrans(conn);
			} catch (LegendException e) {
				LogUtil.printLog("JobExecute ComitTrans Error:"+e.getMessage(), Level.ERROR);
			}
			SysUtility.closeActiveCN(conn);
			SessionManager.destorySession();
		}
	}

}
