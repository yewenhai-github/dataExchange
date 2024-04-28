package com.easy.app.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.app.interfaces.IGlobalService;
import com.easy.exception.LegendException;
import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_AppMonitor extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_AppMonitor(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		SQLBuild sqlBuild = SQLBuild.getInstance(); 
		sqlBuild.append("select * from exs_monitor_config where is_enabled = '1' ");
		List lst = sqlBuild.query4List();
		for (int i = 0; i < lst.size(); i++) {
			try {
				HashMap map = (HashMap)lst.get(i);
				String indx = (String)map.get("INDX");
				String url = "http://"+(String)map.get("APP_IP")+":"+(String)map.get("APP_PORT")+"/"+(String)map.get("APP_NAME")+"/AppMonitor";
				
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
				if("success".equals(rt)){
					SQLExecUtils.executeUpdate("update exs_monitor_config set status = '1',modify_time=sysdate where indx = "+indx);
				}else{
					SQLExecUtils.executeUpdate("update exs_monitor_config set status = '0' where indx = "+indx);
				}
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		
		
		SQLBuild sqlBuild2 = SQLBuild.getInstance(); 
		sqlBuild2.append("select * from exs_monitor_config where status = 0 and is_enabled = '1' and modify_time < sysdate - 1/96 ");//15分钟
		List lst2 = sqlBuild2.query4List();
		StringBuffer content = new StringBuffer();
		String mailFrom = "";
		String mailTo = "";
		for (int i = 0; i < lst2.size(); i++) {
			HashMap map = (HashMap)lst2.get(i);
			mailFrom = (String)map.get("MAIL_FROM");
			mailTo = (String)map.get("MAIL_TO");
//			content.append("监控服务器   ");
//			content.append("内网IP："+map.get("APP_IP")+", ");
//			content.append("外网IP："+ipMap.get(map.get("APP_IP"))+", ");
			content.append("上下文名称："+map.get("APP_NAME")+", ");
			content.append("部署路径："+map.get("PUBLISH_PATH")+"\n");
		}
		if(SysUtility.isNotEmpty(content)){
			String ip = (String)ipMap.get(SysUtility.getCurrentHostIPAddress());
			if(SysUtility.isEmpty(ip)){
				ip = SysUtility.getCurrentHostIPAddress();
			}
			String subject = "App异常监控通知("+ip+")";
			content.insert(0, "以下应用无法正常使用，请及时处理！\n");
			if(SysUtility.isNotEmpty(mailFrom) && SysUtility.isNotEmpty(mailTo)){
				SysUtility.SendMail(subject,mailFrom,mailTo, content.toString(), null);
			}else{
				SysUtility.SendMail(subject,content.toString(), null);
			}
		}
		
	}
	
	private static HashMap ipMap = new HashMap();
	static{
		ipMap.put("192.168.1.63", "106.38.55.22");
		ipMap.put("192.168.1.5", "106.74.113.206");
	}
	
	
	
}
