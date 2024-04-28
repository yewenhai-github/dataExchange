package com.easy.app.auth;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.security.MD5Utility;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/app/user/getuserlist",
						   "/app/user/saveuser",
						   "/app/user/deluser",
						   "/app/user/updatepassword",
						   "/app/user/resetpassword"})
public class UserServlet extends MainServlet{
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{
		String servletPath = getRequest().getServletPath();
		if("/app/user/getuserlist".equals(servletPath)) {
			getuserlist();
		}else if("/app/user/saveuser".equals(servletPath)) {
			saveuser();
		}else if("/app/user/deluser".equals(servletPath)) {
			deluser();
		}else if("/app/user/updatepassword".equals(servletPath)) {
			updatepassword();
		}else if("/app/user/resetpassword".equals(servletPath)) {
			resetpassword();
		}
	}
	
	
	public void getuserlist() throws Exception{
		Datas datas = GetDatas("getuserlist", "rows", true);
		ReturnWriter(datas.toString());
	}
	
	public void saveuser() throws Exception {
    	Datas entity = SessionManager.getFormDatas();
		//新增信息
		String indx = (String)GetEnvDatas("indx");
		if(SysUtility.isEmpty(indx)) {
			String str = SQLExecUtils.query4String("select * from s_auth_user x where x.username=? and x.is_enabled = '1'", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "username"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名用户，请修改后再保存!");
                return;
            }
            entity.put("password_clear", "111111");
            entity.put("password", MD5Utility.encrypt("111111"));
			getDataAccess().Insert("s_auth_user", entity);
		}else {
			String str = SQLExecUtils.query4String("select * from s_auth_user x where x.username=? and x.is_enabled = '1' and indx <> ?",new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, SysUtility.getJsonField(entity, "username"));
					ps.setString(2, SysUtility.getJsonField(entity, "indx"));
				}
			});
            if (SysUtility.isNotEmpty(str)) {
                ReturnMessage(false, "添加失败，存在同名用户，请修改后再保存!");
                return;
            }
			entity.put(SysUtility.KeyFieldDefault, indx);
			getDataAccess().Update("s_auth_user", entity);
		}
		Datas datas = new Datas();
		datas.put("indx",SysUtility.getJsonField(entity, SysUtility.KeyFieldDefault));
		ReturnMessage(true, "保存成功", "", datas.toString());
    }
	
	public void deluser() throws Exception{
		String indxs = (String)GetEnvDatas("indxs");
		StringBuffer deleteSQLs = new StringBuffer();
		deleteSQLs.append("delete from s_auth_user ");
		deleteSQLs.append(" where indx in ( "+indxs+" )");
		getDataAccess().ExecSQL(deleteSQLs.toString());
		ReturnMessage(true, "删除成功!");
	}
	
	public void updatepassword() throws Exception{
		String password = getRequest().getParameter("PASSWORD");
		String new_password = getRequest().getParameter("NEW_PASSWORD");
		String confirm_password = getRequest().getParameter("CONFIRM_PASSWORD");
        String msg = "";
        if(SysUtility.isEmpty(password)){
            msg += "旧密码、";
        }
        if(SysUtility.isEmpty(new_password)){
            msg += "新密码、";
        }
        if(SysUtility.isEmpty(confirm_password)){
            msg += "确认密码、";
        }
        if(!SysUtility.isEmpty(msg)){
            ReturnMessage(false, msg.substring(0, msg.length() - 1) + " 不能为空！");
            return;
        }
        Datas datas = getDataAccess().GetTableDatas("data", "select indx,password from s_auth_user where indx = '"+SysUtility.getCurrentUserIndx()+"'");
		String passwordget = datas.GetTableValue("data", "password");
		if(!SysUtility.isEmpty(passwordget)){
            if (!passwordget.equals(MD5Utility.encrypt(password))){
                ReturnMessage(false, "旧密码有误！");
                return;
            }
        }else{
            ReturnMessage(false, "旧密码有误！");
            return;
        }
		if(!new_password.equals(confirm_password)){
            ReturnMessage(false, "两次输入密码不一致！");
            return;
        }else{
            String pwd = MD5Utility.encrypt(new_password);
            StringBuffer UpdateSQL = new StringBuffer();
            UpdateSQL.append("update s_auth_user set password_clear = ?,password = ? where indx = ?");
			Object[] UpdateParams = new Object[]{new_password,pwd,SysUtility.getCurrentUserIndx()};
			Boolean result= getDataAccess().ExecSQL(UpdateSQL.toString(), UpdateParams);
			getSession().invalidate();	
            if (result.equals(false)){
                ReturnMessage(false, "修改密码失败！");
            }else{
                ReturnMessage(true, "保存成功！");
            }
        }
	}
	
	public void resetpassword() throws Exception{
		String indxs = (String)GetEnvDatas("indxs");
		StringBuffer deleteSQLs = new StringBuffer();
		deleteSQLs.append("update s_auth_user set password_clear='111111',password = '" + MD5Utility.encrypt("111111") + "' ");
		deleteSQLs.append(" where indx in ( "+indxs+" )");
		getDataAccess().ExecSQL(deleteSQLs.toString());
		ReturnMessage(true, "重置成功!");
	}
}
