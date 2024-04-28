package com.easy.api.get;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.Utility;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetLogListResendCopy")
public class GetLogListResendCopy extends MainServlet {
	private static final long serialVersionUID = 6629063879844450960L;

	public void DoCommand() throws Exception{
		try {
			JSONArray params = null;
			String indx = (String)GetEnvDatas("INDX");//详细页触发
			if(SysUtility.isNotEmpty(indx)){
				params = new JSONArray();
				
				JSONObject obj = new JSONObject();
				obj.put("INDX", indx);
				params.put(obj);
			}else{
				params = GetCommandData("key");
			}
			if(SysUtility.isEmpty(params) || params.length() <= 0){
				ReturnMessage(false, "当前操作操作失败！传输数据行数："+params.length());
				return;
			}
			String conditions = Utility.GetIndxConditions(params); 

			String sqlent = "select * from exs_handle_log where 1=1 "+conditions;
			List listent = SQLExecUtils.query4List(sqlent); 
			for (int i = 0; i < listent.size(); i++) {
				HashMap map = (HashMap)listent.get(i);
			    String SOURCE_PATH = (String)map.get("SOURCE_PATH"); 
			    String ERROR_PATH = (String)map.get("ERROR_PATH"); 
			    String MSG_NAME = (String)map.get("MSG_NAME");
				String filePath = ERROR_PATH + File.separator + MSG_NAME;
				File file = new File(filePath);
			    if(!file.exists()){
					ReturnMessage(false, "文件不存在或已归档！");
				}
			    FileUtility.copyFile(ERROR_PATH + File.separator + MSG_NAME, SOURCE_PATH + File.separator +MSG_NAME);
				FileUtility.deleteFile(ERROR_PATH, MSG_NAME);//删除本地文件
			}
			ReturnMessage(true, "消息恢复成功！");
		} catch (Exception e) {	
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} 
	}
}