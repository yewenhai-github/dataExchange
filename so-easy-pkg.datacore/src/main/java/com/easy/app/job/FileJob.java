package com.easy.app.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.app.core.MysqlCalculateEngine;
import com.easy.app.core.OracleCalculateEngine;
import com.easy.app.core.SqlserverCalculateEngine;
import com.easy.app.utility.MutiUtility;
import com.easy.constants.Constants;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.session.SessionManager;
import com.easy.thread.JobDetail;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class FileJob extends JobDetail{
	HashMap<String, Object> fileMap = new HashMap<String, Object>();
	public FileJob(HashMap paramData){
		this.fileMap = paramData;
	}
	
	public void run() throws LegendException{
		List list = (List)fileMap.get("List");
		ServicesBean bean = (ServicesBean)fileMap.get("Bean");
		
		ServicesBean tempBean = (ServicesBean) bean.clone();
		IDataAccess DataAccess = SessionManager.getDataAccess();
//		IDataAccess DataAccess = (IDataAccess)fileMap.get("DataAccess");
		SysUtility.loadOperator(SysUtility.getCurrentConnection());
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[])list.get(i);
			File file = (File)obj[1];
			try {
				tempBean.setSourcePath((String)obj[0]);
				tempBean.setFileName(file.getName());
				tempBean.setFile(file);
				
				String processMsg = "";
				String dbType = SysUtility.GetSetting("System", "DBType");
				if (Constants.Oracle.equalsIgnoreCase(dbType)){
					processMsg = new OracleCalculateEngine().SaveToDBForAny(tempBean, DataAccess);
				} else if (Constants.Mysql.equalsIgnoreCase(dbType)) {
					processMsg = new MysqlCalculateEngine().SaveToDBForAny(tempBean, DataAccess);
				} else if (Constants.Sqlserver.equalsIgnoreCase(dbType)) {
					processMsg = new SqlserverCalculateEngine().SaveToDBForAny(tempBean, DataAccess);
				}
				
				if(SysUtility.isEmpty(processMsg)){
					FileUtility.copyFile(bean.getSourcePath(), bean.getTargetPath(), bean.getFileName());
				}else{
					FileUtility.copyFile(bean.getSourcePath(), bean.getErrorPath(), bean.getFileName());
				}
			} catch (Exception e) {
				LogUtil.printLog("线程处理文件出错："+e.getMessage(), Level.ERROR);
			} finally{
				FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
				DataAccess.ComitTrans();
				MutiUtility.MinusMutiController(tempBean.getXmlDocument()+tempBean.getMessageType());
			}
		}
		list = new ArrayList();
	}
}
