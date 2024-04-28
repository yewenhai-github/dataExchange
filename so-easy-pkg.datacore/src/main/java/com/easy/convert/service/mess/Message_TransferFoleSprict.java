package com.easy.convert.service.mess;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.exception.LegendException;
import com.easy.file.FileFilterHandle;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;



/**
 * 处理文件目录
 * @author wangk
 * 2018年4月8日10:48:19
 */
public class Message_TransferFoleSprict extends MainServlet implements IGlobalService{

	
	public Message_TransferFoleSprict(String Param) {
	}

	private static final long serialVersionUID = 5974132682841977217L;
	private static final Integer TotalFile = 1000;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public void DoCommand() throws LegendException, Exception {
		try {
			List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE nvl(ISENABLED,'1') = '1' AND FUNCTIONTYPE=2 ORDER BY ORG_ID");
			if(query4List.size()>0) {
				for(int i=0;i<query4List.size();i++) {
					HashMap  dataMap = (HashMap) query4List.get(i);
					String SOURCEFILEPATH = (String) dataMap.get("SOURCEFILEPATH");
					File files[] = new File(SOURCEFILEPATH).listFiles(new FileFilterHandle());
					if(SysUtility.isEmpty(files)) {
						continue;
					}
					try {
						JSONArray logArr = new JSONArray();
						
						IDataAccess dataAccess = getDataAccess();
						dataAccess.BeginTrans();
						for (int j = 0; j < files.length; j++) {
							JSONObject logobj = new JSONObject();
							if(j==TotalFile) {
								break;
							}
							String fileName = files[j].getName();
							if(SysUtility.isEmpty(fileName)) {
								LogUtil.printLog("文件："+fileName+"/n 处理文件名称为空", Level.WARN);
								FileUtility.copyFile(files[j].getPath(), (String)dataMap.get("ERRORPATH"));
								files[j].delete();
								continue;
							}
							String[] FileNameSplit = fileName.split("_");//默认取第一位为注册号
							String SourceFilename  = "";
							for(int k =4;k<FileNameSplit.length;k++) {
								SourceFilename += FileNameSplit[k];
							}
							if(FileNameSplit.length < 4) {
								logobj.put("DATA_SOURCE", "服务器");
								logobj.put("SERIAL_NO", FileNameSplit[0]);
								logobj.put("TARGET_FILE_NAME", fileName);
								logobj.put("TRANSFORMATION_CODE", "3");
								logobj.put("TRANSFORMATION_NAME", "失败");
								logobj.put("PROCESS_MSG", "文件："+fileName+"/n 处理文件名称为空");
								logArr.put(logobj);
								LogUtil.printLog("文件："+fileName+"/n 处理文件名称为空", Level.WARN);
								continue;
							}
							String userSql = "SELECT COUNT(0) FROM s_auth_user WHERE REGISTER_NO=? AND nvl(ISENABLED,'1') = '1' AND nvl(ISSTATE,'1') = '1'";
							int RowCount = dataAccess.GetRowCount(userSql, new String[]{FileNameSplit[0]});
							if(RowCount < 1) {
								logobj.put("DATA_SOURCE", "服务器");
								logobj.put("SERIAL_NO", FileNameSplit[0]);
								logobj.put("TARGET_FILE_NAME", fileName);
								logobj.put("TRANSFORMATION_CODE", "1");
								logobj.put("TRANSFORMATION_NAME", "无需");
								logobj.put("PROCESS_MSG", "账号：该无账号或已被禁用");
								logArr.put(logobj);
//									Util.AddMessLog(getDataAccess(), logMap);
							}else {
								FileUtility.copyFile(files[j].getPath(), ((String)dataMap.get("TARGETFILEPATH"))+File.separator+fileName);
								FileUtility.copyFile(files[j].getPath(), ((String)dataMap.get("SOURCE_BACK_PATH"))+File.separator+fileName);
								FileUtility.deleteFile(files[j]);
								logobj.put("DATA_SOURCE", "服务器");
								logobj.put("SERIAL_NO", FileNameSplit[0]);
								logobj.put("TARGET_FILE_NAME", fileName);
								logobj.put("TRANSFORMATION_CODE", "1");
								logobj.put("TRANSFORMATION_NAME", "无需");
								logobj.put("SOURCE_BACK_PATH", ((String)dataMap.get("SOURCE_BACK_PATH"))+File.separator+fileName);
								logArr.put(logobj);
//									Util.AddMessLog(getDataAccess(), logMap);
							}
						}
						if(logArr.length()>0) {
							boolean update = dataAccess.Update("exs_convert_log", logArr, "TARGET_FILE_NAME");
							dataAccess.ComitTrans();
						}
						
						
					} catch (Exception e) {
						LogUtil.printLog("文件处理："+e.getMessage(), Level.ERROR);
					}
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("数据："+e.getMessage(), Level.ERROR);
		}
	}

	
	
}
