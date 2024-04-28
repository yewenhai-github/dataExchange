package com.easy.app.login;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;



@WebServlet("/auth/GetAllMenuList")
public class GetAllMenuList extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("rows", GetAllMenuList(true));
		map.put("children", GetAllMenuList(false));
		map.put("IsOk", "1");
		ReturnWriter(SysUtility.MapToJSONObject(map).toString());
	}
	
	public List<Map<String,String>> GetAllMenuList(boolean oneLevel){
		if(SysUtility.isEmpty(SysUtility.getCurrentUserIndx())) {
			return new ArrayList<>();
		}
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select s.indx,s.menu_code,s.parentid,s.menu_text,s.icon,s.url,s.tlevel,s.top_menu_id,s.order_num");
			sql.append(" from s_auth_menu s");
			sql.append(" where s.is_enabled = '1'");
			if(oneLevel) {
				sql.append(" and ifnull(parentid,0) = 0");
			}else {
				sql.append(" and ifnull(parentid,0) <> 0");
			}
			if(!"Y".equals(SysUtility.getCurrentUserIsRoot())) {
				sql.append(" and s.indx in(select permid from s_auth_perm_role where roleid in(select roleid from s_auth_user_role where userid = ?))");
			}
			sql.append(" order by s.parentid,s.order_num");
			return SQLExecUtils.query4List(sql.toString(), new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					if(!"Y".equals(SysUtility.getCurrentUserIsRoot())) {
						ps.setString(1, SysUtility.getCurrentUserIndx());
					}
				}
			});
		} catch (LegendException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}
