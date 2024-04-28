package com.easy.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>Description: 主调度任务。根据从数据库中取出的每一条定时任务的相关数据，QuartzServlet启动一个MainQuartz实例，
 * MainQuartz再根据要启动的定时任务的具体信息，来启动具体任务，如CronJobTest.doJob(String strParam)</p>
 * 
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 */
public class MainQuartz implements Job{
  /**
	 * QuartzServlet对应每一条定时任务，启动一个MainQuartz，调用execute方法。
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobExecute je = new JobExecute();
		je.execute(context);
	}
}
