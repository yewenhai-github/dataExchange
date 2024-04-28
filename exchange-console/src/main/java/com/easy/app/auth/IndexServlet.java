package com.easy.app.auth;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/auth/GetUserInfo",
		   				   "/auth/GetAllMenuList"})
public class IndexServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	public IndexServlet() {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		String servletPath = getRequest().getServletPath();
		if("/auth/GetUserInfo".equals(servletPath)) {
			getuserinfo();
		}else if("/auth/GetAllMenuList".equals(servletPath)) {
			getallmenulist();
		}
		
		
	}  
	
	public void getuserinfo() throws Exception{
//		getResponse().setHeader("X-Frame-Options", "ALLOW-FROM http://localhost:63342/");
//		getResponse().setHeader("Access-Control-Allow-Credentials", "true");
//		getResponse().setHeader("Access-Control-Allow-Origin", "*");
//      getResponse().setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        
		JSONObject data = new JSONObject();
		data.put("indx", SysUtility.getCurrentUserIndx());
		data.put("rolename", SysUtility.getCurrentRoleName());
		data.put("username", SysUtility.getCurrentUserName());
		data.put("user_real_name", SysUtility.getCurrentName());
		data.put("user_mobile", SysUtility.getCurrentUserMobile());
		data.put("indx", SysUtility.getCurrentUserIndx());
		data.put("usertype", SysUtility.getCurrentUserType());
		data.put("roletype", SysUtility.getCurrentRoleType());
		data.put("orgid", SysUtility.getCurrentOrgId());
		data.put("isRoot", SysUtility.getCurrentUserIsRoot());
		ReturnWriter(data.toString());
	}
	public void getallmenulist() throws Exception{
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
			sql.append("select s.indx,s.parentid,s.menu_text,s.icon,s.url,s.tlevel,s.order_num");
			sql.append(" from s_auth_menu s");
			sql.append(" where s.is_enabled = '1'");
			if(oneLevel) {
				sql.append(" and ifnull(parentid,'0') = 0");
			}else {
				sql.append(" and ifnull(parentid,'0') <> 0");
			}
			if(!"Y".equals(SysUtility.getCurrentUserIsRoot())) {
				sql.append(" and s.indx in(select permid from s_auth_perm_role where roleid in(select role_id from s_auth_user where indx = ?))");
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
