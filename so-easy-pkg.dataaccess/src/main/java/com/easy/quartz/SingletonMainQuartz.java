package com.easy.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * Description: 主调度任务。根据从数据库中取出的每一条定时任务的相关数据，QuartzServlet启动一个MainQuartz实例，
 * MainQuartz再根据要启动的定时任务的具体信息，来启动具体任务，如CronJobTest.doJob(String strParam)
 * 
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 */
public class SingletonMainQuartz implements StatefulJob {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobExecute je = new JobExecute();
		je.execute(context);
	}
}
