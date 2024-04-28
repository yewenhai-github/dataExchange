package com.easy.api.monitor;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.springframework.core.task.AsyncTaskExecutor;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.monitor.MonitorUtility;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMqMonitorList")
public class GetMqMonitorList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	@Resource(name = "taskExecutor")
	private AsyncTaskExecutor taskExecutor;

	public void DoCommand() throws Exception, LegendException {
		Datas datas = (Datas) GetReturnDatasAllDB("GetMqMonitorList");
		datas.put("rows", SysUtility.JSONArrayToLowerCase((JSONArray)datas.get("rows")));
		
		taskExecutor.execute(() -> {
			try {
				for (int i = 0; i < datas.GetTableRows("rows"); i++) {
					org.json.JSONObject obj = datas.GetTable("rows", i);
					try {
						String indx = SysUtility.getJsonField(obj, "indx");
						String app_monitor_url = SysUtility.getJsonField(obj, "app_monitor_url");
						int state = MonitorUtility.geturlstate(app_monitor_url);
						if (state == 200 || state == 401) {
							SQLExecUtils.executeUpdate("update exs_monitor_config set status = 1 where indx = " + indx);
							SysUtility.ComitTrans();
							
							/*String app_url = SysUtility.getJsonField(obj, "app_url");
							String app_user = SysUtility.getJsonField(obj, "app_user");
							String app_pwd = SysUtility.getJsonField(obj, "app_pwd");
							Connection ProducerConn = null;
							try {
								ConnectionFactory ProducerConnFactory = new ActiveMQConnectionFactory(app_user, app_pwd,app_url);
								ProducerConn = ProducerConnFactory.createConnection();
								
							} catch (Exception e) {
								LogUtil.printLog(e.getMessage(), Level.ERROR);
							} finally {
								AuxiliUtility.closeJmsConnection(ProducerConn);
							}*/
						}else {
							SQLExecUtils.executeUpdate("update exs_monitor_config set status = 2 where indx = " + indx);
							SysUtility.ComitTrans();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (LegendException e) {
				e.printStackTrace();
			}
		});
		
		ReturnWriter(datas.toString());
	}

}