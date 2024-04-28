package com.easy.app.auto;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

@WebServlet(urlPatterns = {"/AutoCreateDB_exp.do"},loadOnStartup = 1)
public class AutoCreate extends AutoProcess {
	private static final long serialVersionUID = 1L;
	private static String[] ExeCoulmnNames = new String[]{};
	private static HashMap<String,String> ExeCoulmnNameMap = new HashMap<String,String>();
	private static HashMap<String,String> ExeCoulmnDescMap = new HashMap<String,String>();
	private static HashMap<String,String> ExeCoulmnDefaultMap = new HashMap<String,String>();
	
	@Override
	public void initTableCloumn() {
		if(SysUtility.getDBPoolClose()){
			return;
		}
		if(!"true".equals(SysUtility.GetSetting("system", "auto.create.table"))){
			return;
		}

		//autoCreateMoulde=exp,quartz,plugin
		try {
			if(SysUtility.IsOracleDB()){
				initCoreTableCloumn("config/oracle", ExeCoulmnNames, ExeCoulmnNameMap, ExeCoulmnDefaultMap, ExeCoulmnDescMap);
			}else if(SysUtility.IsMySqlDB()){
				initCoreTableCloumn("config/mysql", ExeCoulmnNames, ExeCoulmnNameMap, ExeCoulmnDefaultMap, ExeCoulmnDescMap);
			}
		} catch (Exception e) {
			LogUtil.printLog("数据库脚本初始化失败！"+e.getMessage(), Level.ERROR);
			e.printStackTrace();
		}
	}
	
 }
