package com.easy.api.convert.mess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;

import com.easy.access.Datas;
import com.easy.api.convert.util.SerialNumberTool;
import com.easy.api.convert.util.Util;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/SaveDbData")
public class SaveDbData extends MainServlet {

	private static final long serialVersionUID = 7589462269482105274L;
	
	public SaveDbData() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void DoCommand() throws LegendException, JSONException{
		String INDX = getRequest().getParameter("INDX");
		String TABLE = getRequest().getParameter("TABLE");
		String COL   = getRequest().getParameter("COL");
		String VALUE = getRequest().getParameter("SENDVALUE").replace('，', ',');
		if(SysUtility.isEmpty(TABLE)
				|| SysUtility.isEmpty(COL)
				|| SysUtility.isEmpty(VALUE)){
			ReturnMessage(false, "请检查 表名、列名、值 是否为空！");
			return;
		}
		String currentUserIndx = SysUtility.getCurrentUserIndx();
		if(!SysUtility.isEmpty(currentUserIndx)) {
			List query4List = SQLExecUtils.query4List("SELECT REGISTER_NO FROM s_auth_user WHERE INDX = '"+currentUserIndx+"'");
			if(query4List.size()>0) {
				HashMap Q4Data = (HashMap)query4List.get(0);
				String  REGISTER_NO = (String) Q4Data.get("REGISTER_NO");//注册号
				if(!SysUtility.isEmpty(REGISTER_NO)) {
					String ConfigName = "";
					List query4List2 = SQLExecUtils.query4List("SELECT CONFIGNAME FROM exs_convert_config_path WHERE INDX = '"+INDX+"'");
					if(query4List2.size()>0) {
						ConfigName = (String) ((HashMap)query4List2.get(0)).get("CONFIGNAME");
					}
					String[] arr = VALUE.split(",");
					Set<String> set=new HashSet<String>(Arrays.asList(arr));
					for (String str : set) {
						HashMap LogData = new HashMap();
						String Name = REGISTER_NO + "_" + ConfigName + "_" + SaveUploadXZX.getData() + "_" + SerialNumberTool.getInstance().generaterNextNumber(6) + "_" + str + ".xml";
						LogData.put("SERIAL_NO", REGISTER_NO);
						LogData.put("CONFIG_PATH_ID", INDX);
						LogData.put("DATA_SOURCE", "DB");
						LogData.put("SOURCE_FILE_NAME", TABLE.trim() + "." + COL.trim() + "." + str.trim());
						LogData.put("TARGET_FILE_NAME", Name);
						LogData.put("PROCESS_MSG","DB处理中");
						Util.AddMessLog(getDataAccess(), LogData);
						Datas datas= new Datas();
						datas.put("MSG_TYPE", "DBTOXML");
						datas.put("PART_ID", REGISTER_NO);
						datas.put("MSG_NO", str.trim());
						datas.put("TECH_REG_CODE", INDX);
						datas.put("EXTEND_XML", Name);
						datas.put("ATTRIBUTE1", TABLE);
						datas.put("ATTRIBUTE2", COL);
						getDataAccess().Insert("exs_handle_sender", datas);
					}  
					ReturnMessage(true, "处理完成：共处理"+set.size()+"条!");
				}
			}
		}
	}

	
}
