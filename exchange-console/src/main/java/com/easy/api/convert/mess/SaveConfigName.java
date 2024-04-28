package com.easy.api.convert.mess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/SaveConfigName")
public class SaveConfigName extends MainServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void DoCommand() throws LegendException, Exception {
		int Indx = 0;
		Datas formDatas = SessionManager.getFormDatas(); 
		JSONObject daObject = ((JSONArray) formDatas.get("CONFIGNAME")).getJSONObject(0);
		if(SysUtility.isEmpty(daObject.get("INDX"))) {
			int PINDX = daObject.getInt("P_INDX");
			String seq = (String) daObject.get("SEQ");
			String sql = "update exs_convert_config_name SET SEQ=SEQ+1 WHERE P_INDX='"+PINDX+"' AND SEQ>'"+(Integer.parseInt(seq)-1)+"'";
			SQLExecUtils.executeUpdate(sql);
			/*String sql = "SELECT MAX(SEQ) SEQ FROM exs_convert_config_name WHERE P_INDX = '"+PINDX+"'";
			List query4List = SQLExecUtils.query4List(sql);
			HashMap map = (HashMap) query4List.get(0);
			if(SysUtility.isNotEmpty(map.get("SEQ"))) {
				if(Integer.parseInt((String)map.get("SEQ"))+1!=(daObject.getInt("SEQ"))) {
					ReturnMessage(false, "序列填写错误");
					return;
				}
			}else {
				if(daObject.getInt("SEQ")!=1) {
					ReturnMessage(false, "序列填写错误");
					return;
				}
			}*/
		}
		Indx = SaveToDB("CONFIGNAME", "exs_convert_config_name");
		if (!SysUtility.isEmpty(Indx))
        {
        	ReturnMessage(true, "保存成功！");
        }
        else
        {
        	ReturnMessage(false, "保存失败！");	
        }		
	}

}
