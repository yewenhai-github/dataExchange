package com.easy.app.task;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class ExsCustXml extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public ExsCustXml(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		SQLBuild SqlBuild = SQLBuild.getInstance();
		SqlBuild.append("SELECT UUID,MSG_TYPE,MSG_NAME,MSG_DATA,DATA_SOURCE ,MSG_PATH FROM T_CUST_DXP_DATA WHERE MSG_FLAG = '0' AND DATA_SOURCE='3' ");
		List lst = SqlBuild.query4List();
		for (int i = 0; i < lst.size(); i++) {
			    HashMap map = (HashMap)lst.get(i);
			    String MSG_DATA = (String)map.get("MSG_DATA");
			    String MSG_NAME = (String)map.get("MSG_NAME");
			    String MSG_PATH = (String)map.get("MSG_PATH")+"\\"+MSG_NAME;
			    String UUID = (String)map.get("UUID");
			    OutputStreamWriter write = null;
			try {
				write = new OutputStreamWriter(new FileOutputStream(MSG_PATH),"UTF-8");
				write.write(MSG_DATA);
				String sql="update T_CUST_DXP_DATA set MSG_FLAG='1' where UUID= ? ";
				getDataAccess().ExecSQL(sql, new String[]{UUID});
			} catch (Exception e) {
				String sql="update T_CUST_DXP_DATA set MSG_FLAG='2' where UUID= ? ";
				getDataAccess().ExecSQL(sql, new String[]{UUID});
				e.printStackTrace();
			}finally{
				write.close();
			}
		}
	}
	
	
}
