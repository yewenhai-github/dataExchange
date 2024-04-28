package com.easy.quartz;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easy.utility.*;
import org.apache.log4j.Level;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.easy.access.Datas;
import com.easy.bizconfig.BizConfigFactory;
import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class QuartzContext {
	static SchedulerFactory schedFact = new StdSchedulerFactory();
	static Scheduler sched = null;
	static int quartzMode = 0;
	
	public static synchronized void init() {
		try {
			sched = schedFact.getScheduler("data_service");
			addSchedule();
			sched.start();
		} catch (SchedulerException e) {
			LogUtil.printLog("QuartzContext.init error:"+e.getMessage(), Level.ERROR);
		}
	}

	/**
	 * 任务数据放在一个List，每一条任务数据是一个HashMap，取出任务数据，调用addSchedule方法来制定schedule
	 * @paramname 定时任务的名字，也就是ServiceName
	 * @paramclass 定时任务的类名
	 * @parammethod 定时任务的方法名
	 * @paramPARAM 定时任务的一个String的输入参数
	 * @paramquartz_type 定时类型
	 * @paramquartz_cron 定时模式
	 * @paramsingleton_flag 任务是否单态(Singleton)
	 * @throws IOException 
	 */
	private static synchronized void addSchedule(){
		List quartzConfigList = new ArrayList();
		/*********1.添加守护进程********/
		if(!SysUtility.getDBPoolClose()){
//			quartzConfigList.add(QuzrtzUtility.GetGuardJobTask());
		}
		/*********2.先查找本地话的配置*********/
		SearchLocalSchedule(quartzConfigList);
		
		if(!SysUtility.getDBPoolClose()){
			Connection conn = null;
			try {
				conn = SysUtility.CreateConnection();
				/*********3.更新集群监视表********/
				//UpdateQuartzAppContext(conn);
				
				/*********4.查到定时任务配置表********/
				SearchDBSchedule(conn,quartzConfigList);
				
				/*********5.加载定时任务帐号信息********/
				SysUtility.loadOperator(conn);
				//LogUtil.printLog("定时任务关键信息：上下文="+AppContext.getContextPath()+",用户名="+SysUtility.getCurrentUserName()+",所属公司="+SysUtility.getCurrentPartId()+",APP_REC_VER="+BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentPartId()), Level.WARN);
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.WARN);
			} finally{
				try {
					SysUtility.ComitTrans(conn);
				} catch (LegendException e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
				}
				SysUtility.closeActiveCN(conn);
			}
		}
		/*********6.加载定时任务********/
		for(int i = 0; i < quartzConfigList.size(); i++){
			addSchedule((HashMap)quartzConfigList.get(i));
		}
	}

	/**
	 * 启动本地配置文件quartz.properties的定时任务
	 * **/
	public static synchronized void SearchLocalSchedule(List quartzConfigList){
		try {
			if(!SysUtility.getDBPoolClose()){
				return;
			}
			
			Datas datas = new Datas();
			String TableName = "exs_quartz_config";
			SysUtility.setQuartzConfigDatas(datas, FileUtility.GetSourceFileList());
			for (int i = 0; i < datas.GetTableRows(TableName); i++) {
				HashMap map = new HashMap();
				map.put("NAME", datas.GetTableValue(TableName, "NAME", i));
				map.put("METHOD",datas.GetTableValue(TableName, "METHOD", i));
				map.put("CLASS", datas.GetTableValue(TableName, "CLASS", i));
				map.put("PARAM", datas.GetTableValue(TableName, "PARAM", i));
				map.put("QUARTZ_CRON", datas.GetTableValue(TableName, "QUARTZ_CRON", i));
				map.put("QUARTZ_TYPE", datas.GetTableValue(TableName, "QUARTZ_TYPE", i));
				map.put("SINGLETON_FLAG", datas.GetTableValue(TableName, "SINGLETON_FLAG", i));
				map.put("IP_ADDRESS", datas.GetTableValue(TableName, "IP_ADDRESS", i));
				quartzConfigList.add(map);
			}
		} catch (Exception e) {
			LogUtil.printLog("文件初始化失败：exs_quartz_config.xml"+e.getMessage(), Level.ERROR);
		}
	}
	
	/**
	 * 启动定时框架表exs_quartz_config作为配置文件
	 * **/
	public static synchronized void SearchDBSchedule(Connection conn,List quartzConfigList) {
		try {
			final String QuartzName = SysUtility.GetProperty("system.properties","QuartzName");
			String QuartzIpAddress = SysUtility.GetProperty("system.properties","QuartzIpAddress");
			QuartzIpAddress = SysUtility.isEmpty(QuartzIpAddress)?AddressUtils.getInnetIp():QuartzIpAddress;
			
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select * from exs_quartz_config where 1=1");
			if(SysUtility.isNotEmpty(QuartzName)){
				sqlBuild.append("name", "in", QuartzName.split(","));
			}else {
				sqlBuild.append("ip_address", "in", QuartzIpAddress.split(","));
//				sqlBuild.append("app_context", "=", AppContext.getContextPath());
			}
			LogUtil.printLog("定时任务启动参数：ip_address="+ QuartzIpAddress+"|"+"app_context="+AppContext.getContextPath()+"|"+"quartz_name="+QuartzName, Level.WARN);
			List lst = sqlBuild.query4List(conn);
			quartzConfigList.addAll(lst);
		} catch (Exception e) {
			LogUtil.printLog("启动交换任务表exs_quartz_config出错："+e.getMessage(), Level.ERROR);
		}
	}

	/**
	 * @param jobDetailMap
	 * 时间模式： strQuartzType = 0 从当前时间开始，每隔strQuartzCron秒执行一次 strQuartzType = 1
	 * 每天strQuartzCron时刻执行一次，例如：1:15:30 strQuartzType = 7 unix crontab 类似的语法
	 */
	public static synchronized void addSchedule(HashMap jobDetailMap) {
		String strJobName = SysUtility.isNotEmpty(jobDetailMap.get("NAME"))?(String) jobDetailMap.get("NAME"):(String) jobDetailMap.get("name");
		String strClassName = SysUtility.isNotEmpty(jobDetailMap.get("CLASS"))?(String) jobDetailMap.get("CLASS"):(String) jobDetailMap.get("class");
		String strMethodName = SysUtility.isNotEmpty(jobDetailMap.get("METHOD"))?(String) jobDetailMap.get("METHOD"):(String) jobDetailMap.get("method");
		String strParam = SysUtility.isNotEmpty(jobDetailMap.get("PARAM"))?(String) jobDetailMap.get("PARAM"):(String) jobDetailMap.get("param");
		String strQuartzType = SysUtility.isNotEmpty(jobDetailMap.get("QUARTZ_TYPE"))?(String) jobDetailMap.get("QUARTZ_TYPE"):(String) jobDetailMap.get("quartz_type");
		String strQuartzCron = SysUtility.isNotEmpty(jobDetailMap.get("QUARTZ_CRON"))?(String) jobDetailMap.get("QUARTZ_CRON"):(String) jobDetailMap.get("quartz_cron");
		String strSingletonFlag = SysUtility.isNotEmpty(jobDetailMap.get("SINGLETON_FLAG"))?(String) jobDetailMap.get("SINGLETON_FLAG"):(String) jobDetailMap.get("singleton_flag");
		String ipAddress = SysUtility.isNotEmpty(jobDetailMap.get("IP_ADDRESS"))?(String) jobDetailMap.get("IP_ADDRESS"):(String) jobDetailMap.get("ip_address");
		String strTriggerName = strJobName;
		//强制设置为单例模式
		JobDetail jobDetail = new JobDetail(strJobName,Scheduler.DEFAULT_GROUP,com.easy.quartz.SingletonMainQuartz.class);
		/*if ("1".equals(strSingletonFlag)) {
			jobDetail = new JobDetail(strJobName,Scheduler.DEFAULT_GROUP,createlegend.quartz.SingletonMainQuartz.class);
		} else {
			jobDetail = new JobDetail(strJobName,Scheduler.DEFAULT_GROUP,createlegend.quartz.MainQuartz.class);
		}*/
		// 把具体的任务数据传给jobDetail
		jobDetail.getJobDataMap().put("CronJobName", strJobName);
		jobDetail.getJobDataMap().put("CronClassName", strClassName);
		jobDetail.getJobDataMap().put("CronMethodName", strMethodName);
		jobDetail.getJobDataMap().put("CronParamName", strParam);
		jobDetail.getJobDataMap().put("CronIpAddress", ipAddress);
		
		Trigger trigger = null;
		int intQuartzType = Integer.parseInt(strQuartzType);

		switch (intQuartzType) {
		case 0:
			try {
				int intQuartzCron = Integer.parseInt(strQuartzCron);
				trigger = new SimpleTrigger(strTriggerName, Scheduler.DEFAULT_GROUP, new Date(),
						null, -1, 1000L * intQuartzCron);
				break;
			} catch (Exception e) {
				LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
				return;
			}
		case 1:
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			Date datQuartzCron = null;
			try {
				datQuartzCron = sdf.parse(strQuartzCron);
			} catch (ParseException e) {
				LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
				return;
			}
			strQuartzCron = "" + datQuartzCron.getSeconds() + " " + datQuartzCron.getMinutes() + " " + datQuartzCron.getHours() + " * * ? *";
			try {
				trigger = new CronTrigger(strTriggerName, Scheduler.DEFAULT_GROUP, strQuartzCron);
			} catch (ParseException e) {
				LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
				return;
			}
			break;
		case 2:
			try {
				strQuartzCron = "0 0/" + strQuartzCron + " * * * ? *";  //TD33939
				trigger = new CronTrigger(strTriggerName, Scheduler.DEFAULT_GROUP, strQuartzCron);
				break;
			} catch (ParseException e) {
				LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
				return;
			}				
		case 7:
			try {
				trigger = new CronTrigger(strTriggerName, Scheduler.DEFAULT_GROUP, strQuartzCron);
				break;
			} catch (ParseException e) {
				LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
				return;
			}
		default:
			return;
		}
		try {
			sched.scheduleJob(jobDetail, trigger);
			if(strClassName.endsWith("JobGuard")){
				LogUtil.printLog("守护进程已启动："+strJobName+":"+strClassName+"."+strMethodName+"("+strParam+")", Level.WARN);
			}else{
				LogUtil.printLog("定时任务已启动："+strJobName+":"+strClassName+"."+strMethodName+"("+strParam+")", Level.WARN);
			}
		} catch (SchedulerException e) {
			LogUtil.printLog("addSchedule error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static synchronized void AddCurrentSchedule(final String JobName) throws LegendException {
		if(SysUtility.isEmpty(JobName)){
			return;
		}
		List lst = SQLExecUtils.query4List("select * from exs_quartz_config where name = ?",new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, JobName);
			}
		});
		if(SysUtility.isEmpty(lst) || lst.size() < 1){
			return;
		}
		HashMap jobDetailMap = (HashMap)lst.get(0);
		try {
			if(-1 == sched.getTriggerState(JobName,Scheduler.DEFAULT_GROUP)){
				addSchedule(jobDetailMap);
				//LogUtil.printLog("定时任务："+JobName+" 装载成功", Level.WARN);
			}
		} catch (SchedulerException e) {
			LogUtil.printLog("AddCurrentSchedule error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static synchronized void DelCurrentSchedule(String JobName) {
		String triggerName = JobName;
		try {
			if(-1 != sched.getTriggerState(JobName,Scheduler.DEFAULT_GROUP)){
				sched.pauseTrigger(triggerName, Scheduler.DEFAULT_GROUP);
				//sched.interrupt(JobName, Scheduler.DEFAULT_GROUP);
				sched.unscheduleJob(triggerName, Scheduler.DEFAULT_GROUP);
				sched.deleteJob(JobName, Scheduler.DEFAULT_GROUP);
				Map qmap = QuzrtzUtility.GetQuartzConfigByName(JobName);
				String strJobName = (String)qmap.get("NAME");
				String strClassName = (String)qmap.get("CLASS");
				String strMethodName = (String)qmap.get("METHOD");
				String strParam = (String)qmap.get("PARAM");
				LogUtil.printLog("定时任务已移除："+strJobName+":"+strClassName+"."+strMethodName+"("+strParam+")", Level.WARN);
			}
		} catch (SchedulerException e) {
			LogUtil.printLog("removeSchedule error:"+e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog("removeSchedule error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public static synchronized int GetScheduleState(String JobName) {
		//state的值代表该任务触发器的状态：STATE_BLOCKED=4 STATE_COMPLETE=2 STATE_ERROR=3 STATE_NONE=-1 STATE_NORMAL=0 STATE_PAUSED=1
		try {
			return sched.getTriggerState(JobName,Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			LogUtil.printLog("GetTriggerState error:"+e.getMessage(), Level.ERROR);
		}
		return -1;
	}
	
	public static synchronized void UpdateQuartzAppContext(Connection conn) throws Exception{
		final String contextPath = AppContext.getContextPath();
		final String clientIpAddress = SysUtility.getCurrentHostIPAddress();
		final String quartzIpAddress = SysUtility.GetProperty("system.properties","QuartzIpAddress");
		try {
			String SQL = "select count(0) from exs_quartz_config_cluster where modify_time > sysdate - 1/48";
			String rt = SQLExecUtils.query4String(conn,SQL);
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("update exs_quartz_config ");
			sqlBuild.append(" set app_context = ?,",contextPath);
			sqlBuild.append(" Client_Ip_Address = ?",clientIpAddress);
			sqlBuild.append(" where 1 = 1");
			if(SysUtility.isNotEmpty(quartzIpAddress)){
				sqlBuild.append("ip_address","in",quartzIpAddress.split(","));
			}
			if(Integer.parseInt(rt) > 1){
				sqlBuild.append(" and app_context is null");
			}
			sqlBuild.execute4Update();
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		finally{
			try {
					SysUtility.ComitTrans();
			} catch (LegendException e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	}
	
	public static synchronized void destroy() {
		
	}
}
