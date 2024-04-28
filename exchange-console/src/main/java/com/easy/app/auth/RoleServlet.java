package com.easy.app.auth;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/app/user/getrolelist",
						   "/app/user/saverole",
						   "/app/user/delrole"})
public class RoleServlet extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		String servletPath = getRequest().getServletPath();
		if("/app/user/getrolelist".equals(servletPath)) {
			getrolelist();
		}else if("/app/user/saverole".equals(servletPath)) {
			saverole();
		}else if("/app/user/delrole".equals(servletPath)) {
			delrole();
		}
	}
	
	
	public void getrolelist() throws Exception{
		Datas datas = GetDatas("getrolelist", "rows", true);
		ReturnWriter(datas.toString());
	}
	
	public void saverole() throws Exception {
    	Datas entity = SessionManager.getFormDatas();
		//新增信息
		String indx = (String)GetEnvDatas("indx");
		if(SysUtility.isEmpty(indx)) {
			String str = SQLExecUtils.query4String("select * from s_auth_role x where x.role_name=? and x.is_enabled = '1'", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "role_name"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名角色，请修改后再保存!");
                return;
            }
			getDataAccess().Insert("s_auth_role", entity);
		}else {
			String str = SQLExecUtils.query4String("select * from s_auth_role x where x.role_name=? and x.is_enabled = '1' and indx <> ?",new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "role_name"));
					ps.setString(2, SysUtility.getJsonField(entity, "indx"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名角色，请修改后再保存!");
                return;
            }
			entity.put(SysUtility.KeyFieldDefault, indx);
			getDataAccess().Update("s_auth_role", entity);
		}
		Datas datas = new Datas();
		datas.put("indx",SysUtility.getJsonField(entity, SysUtility.KeyFieldDefault));
		ReturnMessage(true, "保存成功", "", datas.toString());
    }
	
	public void delrole() throws Exception{
		String indx = (String)GetEnvDatas("indx");
		
		String str = SQLExecUtils.query4String("select 0 from s_auth_perm_role where roleid="+indx+" and is_enabled = '1'");
		if(SysUtility.isNotEmpty(str)) {
			ReturnMessage(false, "删除失败，请优先取消或删除对应授权的用户!");
		}else {
			getDataAccess().ExecSQL("delete from s_auth_role where indx = "+ indx);
			ReturnMessage(true, "删除成功!");
		}
	}
	
}
