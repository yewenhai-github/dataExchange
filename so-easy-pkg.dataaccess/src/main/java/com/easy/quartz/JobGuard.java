package com.easy.quartz;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class JobGuard {
	
	public void cc(String param){
		final String ipAddress = SysUtility.getCurrentHostIPAddress();
		final String contextPath = AppContext.getContextPath();
		final String expireTime = "sysdate - 1/48";
		try {
			/********************1. 载入集群分配到的定时任务*****************************/
			String QuartzIpAddress = SysUtility.GetProperty("system.properties","QuartzIpAddress");
			QuartzIpAddress = SysUtility.isEmpty(QuartzIpAddress)?SysUtility.getCurrentHostIPAddress():QuartzIpAddress;
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select * from exs_quartz_config where rec_ver = -1");
			sqlBuild.append("ip_address", "in", QuartzIpAddress.split(","));
			sqlBuild.append("app_context", "=",contextPath);
			List JobsList = sqlBuild.query4List();
			for (int i = 0; i < JobsList.size(); i++) {
				HashMap map = (HashMap)JobsList.get(i);
				final String JobName = (String)map.get("NAME");
				QuartzContext.AddCurrentSchedule(JobName);
				SQLExecUtils.executeUpdate("update exs_quartz_config set modify_time = sysdate,rec_ver = rec_ver + 1 where name = ?", new Callback() {
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, JobName);
					}
				});
				SysUtility.ComitTrans();
			}
			
			/********************2. 集群发现逻辑，半小时内有活动才标识为活跃子集群*****************************/
			String ClusterSQL = "select * from exs_quartz_config_cluster where modify_time > "+expireTime+" and client_ip_address = ?";
			List ClusterList = SQLExecUtils.query4List(ClusterSQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, ipAddress);
				}
			});
			if(SysUtility.isEmpty(ClusterList) || ClusterList.size() <= 0){
				return;
			}
			
			/********************3. 无效定时任务发现*****************************/
			SQLBuild sqlBuild2 = SQLBuild.getInstance();
			sqlBuild2.append("select * from exs_quartz_config s where 1 = 1");
			sqlBuild2.append(" and ((rec_ver <> -1 and s.modify_time < "+expireTime+")");
			sqlBuild2.append(" or rec_ver = -1 and exists(select 0 from exs_quartz_config_cluster s where s.app_context = app_context and s.modify_time < "+expireTime+"))");
			sqlBuild2.append("ip_address", "in", QuartzIpAddress.split(","));
			List stopJobsList = sqlBuild2.query4List();
			if(SysUtility.isEmpty(stopJobsList) || stopJobsList.size() <= 0){
				return;
			}
			/********************4. 移除当前容器无效定时任务，并重新分配,分配后的任务标识为exs_quartz_config.rec_ver = -1*****************************/
			String updateSQL = "update exs_quartz_config set rec_ver = -1,app_context = ? where name = ? and (rec_ver > -1 or exists(select 0 from exs_quartz_config_cluster s where s.app_context = app_context and s.modify_time < "+expireTime+"))";
			for (int i = 0; i < stopJobsList.size(); i++) {
				try {
					SysUtility.BeginTrans();
					HashMap map = (HashMap)stopJobsList.get(i);
					final String name = (String)map.get("NAME");
					//移除定时任务
					QuartzContext.DelCurrentSchedule(name);
					//重新分配定时任务的分配
					java.util.Random random=new java.util.Random();// 定义随机类
					int result = random.nextInt(ClusterList.size());
					HashMap hitClusterMap = (HashMap)ClusterList.get(result);
					final String app_context = (String)hitClusterMap.get("APP_CONTEXT");
					SQLExecUtils.executeUpdate(updateSQL, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, app_context);
							ps.setString(2, name);
						}
					});
					SysUtility.ComitTrans();
				} catch (Exception e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
				}
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public void monitor(String param){
		SQLBuild sqlBuild = SQLBuild.getInstance(); 
		sqlBuild.append("select * from exs_monitor_server where is_enabled = '1' ");
		List lst = sqlBuild.query4List();
		for (int i = 0; i < lst.size(); i++) {
			HashMap map = (HashMap)lst.get(0);
			String indx = (String)map.get("INDX");
			String url = (String)map.get("URL");
			
			Map<String, String> postParam = new HashMap<String, String>();
			Request request = new Request(postParam, url);
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			String rt = "";
			if (response.isSuccess()) {
				rt = response.getStringResult("UTF-8");
			} else {
				rt = response.getFailureMessage();
			}
			try {
				if("success".equals(rt)){
					SQLExecUtils.executeUpdate("update exs_monitor_server set status = '1',modify_time=sysdate where indx = "+indx);
				}else{
					SQLExecUtils.executeUpdate("update exs_monitor_server set status = '0',modify_time=sysdate where indx = "+indx);
				}
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		
		
		SQLBuild sqlBuild2 = SQLBuild.getInstance(); 
		sqlBuild2.append("select * from exs_monitor_server where is_enabled = '1' and modify_time > sysdate - 1/48 ");
		List lst2 = sqlBuild2.query4List();
		String subject = "";
		StringBuffer content = new StringBuffer();
		String mailFrom = "";
		String mailTo = "";
		for (int i = 0; i < lst2.size(); i++) {
			HashMap map = (HashMap)lst2.get(0);
			String appName = (String)map.get("APP_NAME");
			String url = (String)map.get("URL");
			mailFrom = (String)map.get("MAIL_FROM");
			mailTo = (String)map.get("MAIL_TO");
			content.append("上下文名称："+appName+"，路径："+url);
		}
		if(SysUtility.isNotEmpty(content)){
			SysUtility.SendMail(subject,mailFrom,mailTo, content.toString(), null);
		}
		
	}
	
}
