package com.easy.api.monitor;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.springframework.core.task.AsyncTaskExecutor;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetPointMonitorList")
public class GetPointMonitorList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	@Resource(name = "taskExecutor")
	private AsyncTaskExecutor taskExecutor;

	public void DoCommand() throws Exception, LegendException {
		Datas datas = (Datas) GetReturnDatasAllDB("GetPointMonitorList");
		datas.put("rows", SysUtility.JSONArrayToLowerCase((JSONArray)datas.get("rows")));
		ReturnWriter(datas.toString());
	}

}