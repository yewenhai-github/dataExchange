package com.easy.api.convert.mess;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.web.MainServlet;

@WebServlet("/DeletesConfigName")
public class DeletesConfigName extends MainServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void DoCommand() throws LegendException, Exception {
		String INDXS = getRequest().getParameter("ids");
		String[] split = INDXS.split(",");
		for (String item : split) {
			String sql = "SELECT SEQ,P_INDX PINDX FROM exs_convert_config_name WHERE INDX = '"+item+"'";
			HashMap map = (HashMap) SQLExecUtils.query4List(sql).get(0);
			sql = "SELECT INDX FROM exs_convert_config_name WHERE P_INDX='"+map.get("PINDX")+"' AND SEQ >"+map.get("SEQ");
			List list = SQLExecUtils.query4List(sql);
			if(list.size()>0) {
				String ids = "";
				for(int i =0;i<list.size();i++) {
					map = (HashMap) list.get(i);
					ids +=map.get("INDX")+",";
				}
				ids = ids.substring(0, ids.length()-1);
				sql = "update exs_convert_config_name SET SEQ = SEQ-1 WHERE INDX IN ("+ids+")";
				getDataAccess().ExecSQL(sql);
			}
			boolean flag = this.getDataAccess().ExecSQL("delete exs_convert_config_name where exs_convert_config_name.INDX=" + item);
			if (flag)
				ReturnMessage(true, "");
			else
				ReturnMessage(false,"");

		}

	}

}
