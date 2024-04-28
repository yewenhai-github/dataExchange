package com.easy.app.auth;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/app/user/getorglist",
						   "/app/user/saveorg",
						   "/app/user/delorg"})
public class OrgServlet extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		String servletPath = getRequest().getServletPath();
		if("/app/user/getorglist".equals(servletPath)) {
			getorglist();
		}else if("/app/user/saveorg".equals(servletPath)) {
			saveorg();
		}else if("/app/user/delorg".equals(servletPath)) {
			delorg();
		}
		
	}
	
	
	public void getorglist() throws Exception{
		Datas datas = GetDatas("getorglist", "rows", true);
		for (int i = 0; i < datas.GetTableRows("rows"); i++) {
			String parentId = datas.GetTableValue("rows", "_parentId", i);
			if(SysUtility.isEmpty(parentId)) {
				datas.SetTableValue("rows", "_parentId", null, i);
			}
		}
		ReturnWriter(datas.toString());
	}
	
	public void saveorg() throws Exception {
    	Datas entity = SessionManager.getFormDatas();
		//新增信息
		String indx = (String)GetEnvDatas("indx");
		if(SysUtility.isEmpty(indx)) {
			String str = SQLExecUtils.query4String("select * from s_auth_organization x where x.org_name=? and x.is_enabled = '1'", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "org_name"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名机构，请修改后再保存!");
                return;
            }
			getDataAccess().Insert("s_auth_organization", entity);
		}else {
			String str = SQLExecUtils.query4String("select * from s_auth_organization x where x.org_name=? and x.is_enabled = '1' and indx <> ?",new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "org_name"));
					ps.setString(2, SysUtility.getJsonField(entity, "indx"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名机构，请修改后再保存!");
                return;
            }
			entity.put(SysUtility.KeyFieldDefault, indx);
			getDataAccess().Update("s_auth_organization", entity);
		}
		Datas datas = new Datas();
		datas.put("indx",SysUtility.getJsonField(entity, SysUtility.KeyFieldDefault));
		ReturnMessage(true, "保存成功", "", datas.toString());
    }
	
	public void delorg() throws Exception{
		String indx = (String)GetEnvDatas("indx");
		
		String str = SQLExecUtils.query4String("select 0 from s_auth_organization where parentid="+indx+" and is_enabled = '1'");
		if(SysUtility.isNotEmpty(str)) {
			ReturnMessage(false, "删除失败，请优先删除子节点!");
		}else {
			getDataAccess().ExecSQL("delete from s_auth_organization where indx = "+ indx);
			ReturnMessage(true, "删除成功!");
		}
	}
	
	
}
