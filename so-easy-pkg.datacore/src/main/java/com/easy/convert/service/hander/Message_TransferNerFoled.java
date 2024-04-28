package com.easy.convert.service.hander;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;


public class Message_TransferNerFoled {
	
	
	public static void Message_NewNerFoled(HashMap map) throws LegendException {
		String MainFolder_Source = SysUtility.GetSetting("System", "MainFolder_Source");
		if(SysUtility.isEmpty(MainFolder_Source)) {
			MainFolder_Source = "D://GlobalServiceMESS";
		}
		final String MSG_NO = (String) map.get("MSG_NO");
		String MESSAGE_DEST =(String) map.get("ATTRIBUTE1");
		String[] MESSAGE_DESTSplit = MESSAGE_DEST.split("\\|");
		for (String dest : MESSAGE_DESTSplit) {
			String configt = "";
			String FonerNAME = "";
			if(SysUtility.isNotEmpty(dest)){
				String[] split = dest.split(",");
				configt = split[0];
				for (String string : split) {
					FonerNAME+=string+File.separator;
				}
				dest = FonerNAME;
			}
			if(SysUtility.isEmpty(dest)) {
				dest="";
			}
			
			String FolderName = MSG_NO+File.separator+dest;
			final String config =configt;
			String createFolder = FileUtility.createFolder(MainFolder_Source, FolderName);
			List queryList = SQLExecUtils.query4List("SELECT INDX FROM exs_convert_config_path WHERE ORG_ID=(SELECT ORG_ID FROM s_auth_user WHERE REGISTER_NO=?) AND CONFIGNAME=?",new Callback() {
				
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, MSG_NO);
					ps.setString(2, config);
				}
			});
			if(queryList.size()>0) {
				if(FolderName.contains("SUCCESSBACK")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET SUCCESS_BACK_PATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}else if(FolderName.contains("SOURCEBACK")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET SOURCE_BACK_PATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}else if(FolderName.contains("ERROR")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET ERRORPATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}else if(FolderName.contains("BACK")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET BACKPATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}else if(FolderName.contains("SUCCESS")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET TARGETFILEPATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}else if(FolderName.contains("SOURCE")) {
					SQLExecUtils.executeUpdate("UPDATE exs_convert_config_path SET SOURCEFILEPATH='"+createFolder+"' WHERE INDX='"+((HashMap)queryList.get(0)).get("INDX")+"'");
				}
			}
		}
		
	}
}
