package com.easy.api.change;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.web.MainServlet;

@WebServlet("/ChangeDB2XmlList")
public class ChangeDB2XmlList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String Indxs = getRequest().getParameter("Indxs");
		String [] strIndxs= Indxs.split(","); 
		String action = getRequest().getParameter("action");
		if ("btnDle".equals(action)) {
			changeByIndx(strIndxs, "exs_config_dbtoxml","1", "0", "失效");
		} else if ("btnRecovery".equals(action)) {
			changeByIndx(strIndxs, "exs_config_dbtoxml","0", "1", "恢复");
		}else if ("btnDleSql".equals(action)) {
			changeByIndx(strIndxs,"exs_config_dbtoxml_SQL","1", "0", "失效");
		}else if ("btnRecoverySql".equals(action)) {
			changeByIndx(strIndxs,"exs_config_dbtoxml_SQL", "0", "1", "恢复");
		}else{
			ReturnMessage(true, "没有可操作的数据");
		}
	}
/**
 * @param strIndxs 操作数据得INDX
 * @param table 操作表名
 * @param fromisEnabled 当前的isEnabled状态
 * @param toisEnabled 修改的isEnabled状态
 * @param test 提示句
 * @throws LegendException
 */
	private void changeByIndx(String[] strIndxs,String table,String fromisEnabled,String toisEnabled,String test) throws LegendException {
		StringBuffer conditions = new StringBuffer();
		conditions.append("  and  (");
		for (int i = 0; i < strIndxs.length; i++) {
			if(i == 0){
				conditions.append("indx in("+strIndxs[i]+")");
			}else{
				conditions.append(" or indx in("+strIndxs[i]+")");
			}
		}
		conditions.append(")");
		StringBuffer SelectSQL = new StringBuffer();
		SelectSQL.append("SELECT * FROM "+table+" WHERE 1=1 "+conditions+" AND IS_ENABLED='"+fromisEnabled+"'");
		List lst = SQLExecUtils.query4List(SelectSQL.toString());
		int size = lst.size();
		Object[] UpdateParams = null;
		if(size!=0){
			StringBuffer UpdateSQL = new StringBuffer();
			UpdateSQL.append("UPDATE  "+table+" SET IS_ENABLED = ? WHERE 1=1 "+conditions);
			UpdateParams = new Object[]{toisEnabled};
			getDataAccess().ExecSQL(UpdateSQL.toString(), UpdateParams);
			ReturnMessage(true, test+""+size+"条数据成功！");
		}else{
			ReturnMessage(true, "没有"+test+"的数据");
		}
	}
}