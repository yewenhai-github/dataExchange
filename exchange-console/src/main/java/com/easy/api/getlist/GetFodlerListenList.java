package com.easy.api.getlist;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;

import com.easy.access.Datas;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetFodlerListenList")
public class GetFodlerListenList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String querystring = getRequest().getQueryString();
		String msg_type = "";
		String msg_type2 = "";
		if(querystring.indexOf("msg_type=") > 0) {
			msg_type = querystring.substring(querystring.indexOf("msg_type=")+9, querystring.indexOf("msg_type=")+10);
		}
		if(querystring.indexOf("msg_type2=") > 0) {
			msg_type2 = querystring.substring(querystring.indexOf("msg_type2=")+10, querystring.indexOf("msg_type2=")+11);
		}
		String type = GetToSearchTable("TYPE");
		String createTimeFrom = GetToSearchTable("CREATE_TIME_FROM");
		String createTimeTo = GetToSearchTable("CREATE_TIME_TO");
		
		String TableName = "rows";
		StringBuffer sql = new StringBuffer();
		sql.append("@SELECT '' EXS_TYPE,'' SOURCE_PATH,'' TARGET_PATH,'' ERROR_PATH,'' TARGET_SOURCE_PATH,str_to_date('','%Y-%m-%d%k%i') CREATE_TIME,'' MSG_TYPE,'' MSG_NAME,'' MSG_NAME2,'' CREATOR FROM DUAL");
		/*if("DBToXml".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'DBToXml' EXS_TYPE,SOURCE_PATH,'' TARGET_PATH,'' ERROR_PATH,'' TARGET_SOURCE_PATH,CREATE_TIME,'' MSG_TYPE FROM exs_config_dbtoxml WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
		}
		if("XmlToDB".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'XmlToDB' EXS_TYPE,SOURCE_PATH,TARGET_PATH,ERROR_PATH,'' TARGET_SOURCE_PATH,CREATE_TIME,'' MSG_TYPE FROM EXS_CONFIG_XMLTODB WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
		}
		if("XmlToFloderSplit".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'XmlToFloderSplit' EXS_TYPE,SOURCE_PATH,TARGET_PATH,ERROR_PATH,TARGET_SOURCE_PATH,CREATE_TIME,'' MSG_TYPE FROM exs_config_xmltosplit WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
		}*/
		if("XmlToFloderMerge".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'XmlToFloderMerge' EXS_TYPE,SOURCE_PATH,TARGET_PATH,ERROR_PATH,TARGET_SOURCE_PATH,CREATE_TIME,MSG_TYPE,MSG_NAME,MSG_NAME2,CREATOR FROM exs_config_xmltomerge WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
			if(SysUtility.isNotEmpty(msg_type)){
				sql.append(" AND MSG_TYPE = '"+msg_type+"'");
			}
			if(SysUtility.isNotEmpty(msg_type2)){
				sql.append(" AND MSG_TYPE2 = '"+msg_type2+"'");
			}
		}
		/*if("XmlApiToClient".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'XmlApiToClient' EXS_TYPE,SOURCE_PATH,TARGET_PATH,ERROR_PATH,'' TARGET_SOURCE_PATH,CREATE_TIME FROM exs_config_api_push WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
		}
		if("XmlApiToServer".equals(type) || SysUtility.isEmpty(type)){
			sql.append(" union all ");
			sql.append("SELECT 'XmlApiToServer' EXS_TYPE,SOURCE_PATH,TARGET_PATH,ERROR_PATH,'' TARGET_SOURCE_PATH,CREATE_TIME FROM exs_config_api_polling WHERE ifnull(IS_ENABLED,'1') = '1'");
			if(SysUtility.isNotEmpty(createTimeFrom)){
				sql.append(" AND CREATE_TIME >=str_to_date('"+createTimeFrom+"','%Y-%m-%d%k%i')");
			}
			if(SysUtility.isNotEmpty(createTimeTo)){
				sql.append(" AND CREATE_TIME < str_to_date('"+createTimeTo+"','%Y-%m-%d%k%i')+1");
			}
		}*/
		Datas datas = GetDatasAllDB(sql.toString(),GetToSearchTable("SCHEMA"));
		datas.RemoveTable(TableName, 0);
		
		for (int i = 0; i < datas.GetTableRows(TableName); i++) {
			String sourcePath = SysUtility.replacePath(datas.GetTableValue(TableName, "SOURCE_PATH", i));
			String targetPath = SysUtility.replacePath(datas.GetTableValue(TableName, "TARGET_PATH", i));
			String errorPath = SysUtility.replacePath(datas.GetTableValue(TableName, "ERROR_PATH", i));
			String targetSourcePath = SysUtility.replacePath(datas.GetTableValue(TableName, "TARGET_SOURCE_PATH", i));
			datas.SetTableValue(TableName, "CLIENT_IP", SysUtility.getCurrentHostIPAddress(), i);
			datas.SetTableValue(TableName, "SOURCE_PATH", sourcePath, i);
			datas.SetTableValue(TableName, "TARGET_PATH", targetPath, i);
			datas.SetTableValue(TableName, "ERROR_PATH", errorPath, i);
			datas.SetTableValue(TableName, "TARGET_SOURCE_PATH", targetSourcePath, i);
			datas.SetTableValue(TableName, "EXS_TYPE", datas.GetTableValue(TableName, "EXS_TYPE", i), i);
			datas.SetTableValue(TableName, "CREATE_TIME", datas.GetTableValue(TableName, "CREATE_TIME", i), i);
			datas.SetTableValue(TableName, "SCHEMA", datas.GetTableValue(TableName, "SCHEMA", i), i);
			try {
				datas.SetTableValue(TableName, "SOURCE_COUNT", 0 , i);
				datas.SetTableValue(TableName, "TARGET_COUNT", 0 , i);
				datas.SetTableValue(TableName, "ERROR_COUNT", 0 , i);
				datas.SetTableValue(TableName, "TARGET_SOURCE_COUNT", 0 , i);
				
				if(SysUtility.isNotEmpty(sourcePath)){
					List fileNamelist = FileUtility.GetFileNameList(100000000, sourcePath);
					datas.SetTableValue(TableName, "SOURCE_COUNT", fileNamelist.size(), i);
				}
				if(SysUtility.isNotEmpty(targetPath)){
					List fileNamelist = FileUtility.GetFileNameList(100000000, targetPath);
					datas.SetTableValue(TableName, "TARGET_COUNT", fileNamelist.size(), i);
				}
				if(SysUtility.isNotEmpty(errorPath)){
					List fileNamelist = FileUtility.GetFileNameList(100000000, errorPath);
					datas.SetTableValue(TableName, "ERROR_COUNT", fileNamelist.size(), i);
				}
				if(SysUtility.isNotEmpty(targetSourcePath)){
					List fileNamelist = FileUtility.GetFileNameList(100000000, targetSourcePath);
					datas.SetTableValue(TableName, "TARGET_SOURCE_COUNT", fileNamelist.size(), i);
				}
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		ReturnWriter(datas.getDataJSON().toString());
	}

}