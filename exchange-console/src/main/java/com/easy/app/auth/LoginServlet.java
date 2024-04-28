package com.easy.app.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;

import com.easy.access.Datas;
import com.easy.bizconfig.BizConfigFactory;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.security.MD5Utility;
import com.easy.session.Operator;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet(urlPatterns = {"/login/DoLogin",
		   				   "/login/LoginOut"})
public class LoginServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		String servletPath = getRequest().getServletPath();
		if("/login/DoLogin".equals(servletPath)) {
			dologin();
		}else if("/login/LoginOut".equals(servletPath)) {
			loginout();
		}
		
		
	}  
	
	public void dologin() throws Exception{
		try {
			String userName=getRequest().getParameter("USERNAME");
			String pwd=getRequest().getParameter("PASSWORD");
			StringBuffer SQL = new StringBuffer();
			SQL.append("select u.*,");
			SQL.append("      (select role_name from s_auth_role r where r.indx=u.role_id limit 1) role_name,");
			SQL.append("      (select org_name from s_auth_organization where org_id = u.user_dept_id limit 1) user_dept_name,");
			SQL.append("      (select org_name from s_auth_organization where org_id = u.org_id limit 1) org_name");
			SQL.append(" from s_auth_user u");
			SQL.append(" where is_enabled = ?");
			SQL.append("   and username = ?");
			SQL.append("   and password = ?");
			Datas datas = getDataAccess().GetTableDatas("rows", SQL.toString(), new String[]{"1",userName,MD5Utility.encrypt(pwd)});
			
			
			String indx = datas.GetTableValue("rows", "Indx");
			//填充【商户】备案号
			Map map = SQLExecUtils.query4Map("select t.market_purchase_no,t.company_name from t_reg_supplier_business t where filing_status_code = '3' and t.creator = "+indx);
			datas.SetTableValue("rows", "reg_no", (String)map.get("market_purchase_no"));
			datas.SetTableValue("rows", "reg_name", (String)map.get("company_name"));
			//填充【组货人】备案号
			if(SysUtility.isEmpty(datas.GetTableValue("rows", "reg_no"))) {
				map = SQLExecUtils.query4Map("select t.group_reg_no,t.company_name from t_reg_group_person t where review_status_code = '3' and t.creator = "+indx);
				datas.SetTableValue("rows", "reg_no", (String)map.get("group_reg_no"));
				datas.SetTableValue("rows", "reg_name", (String)map.get("company_name"));
			}
			//填充【代理商】备案号
			if(SysUtility.isEmpty(datas.GetTableValue("rows", "reg_no"))) {
				map = SQLExecUtils.query4Map("select t.filling_reg_no,t.company_name from t_reg_foreign_trade_company t where filing_status_code = '3' and t.creator = "+indx);
				datas.SetTableValue("rows", "reg_no", (String)map.get("filling_reg_no"));
				datas.SetTableValue("rows", "reg_name", (String)map.get("company_name"));
			}
			//填充【收汇人-个体工商户】备案号
			if(SysUtility.isEmpty(datas.GetTableValue("rows", "reg_no"))) {
				map = SQLExecUtils.query4Map("select soe_self_reg_no,soe_person_name from t_reg_soe_self where review_status_code = '3' and creator = "+indx);
				datas.SetTableValue("rows", "reg_no", (String)map.get("soe_self_reg_no"));
				datas.SetTableValue("rows", "reg_name", (String)map.get("soe_person_name"));
			}
			//填充【收汇人-境外采购商】备案号
			if(SysUtility.isEmpty(datas.GetTableValue("rows", "reg_no"))) {
				map = SQLExecUtils.query4Map("select soe_abroad_reg_no,soe_person_name from t_reg_soe_abroad where review_status_code = '3' and creator = "+indx);
				datas.SetTableValue("rows", "reg_no", (String)map.get("soe_abroad_reg_no"));
				datas.SetTableValue("rows", "reg_name", (String)map.get("soe_person_name"));
			}
			
			//管理员角色
			if("Y".equals(datas.GetTableValue("rows", "IS_ROOT"))) {
				datas.SetTableValue("rows", "reg_no", "-1");
				datas.SetTableValue("rows", "reg_name", "-1");
			}
			if(datas.GetTableRows("rows") > 0){
				SetOperator(datas);
				ReturnMessage(true, "", StartPage, datas.toString(), "");
			}else{
				ReturnMessage(false, "用户名密码错误", "", "", "");
			}
		} catch (Exception e) {
			LogUtil.printLog("登录出错:" + e.getMessage(), Level.ERROR);
			ReturnMessage(false, "登录出错", "", "", "");
		}
	}
	public void loginout() throws Exception{
		if(getSession() != null) {
			getSession().invalidate();			
		}
		String loginPage = SysUtility.GetSetting("system", "LoginPage");
		ReturnMessage(false, "", loginPage);
	}
		
	public void SetOperator(Datas datas) throws LegendException{
		String INDX = datas.GetTableValue("rows", "INDX");
		if(SysUtility.isEmpty(INDX)){
			INDX = "-1";
		}
		String USER_REAL_NAME = datas.GetTableValue("rows", "USER_REAL_NAME");
		String USERNAME = datas.GetTableValue("rows", "USERNAME");
		String PASSWORD = datas.GetTableValue("rows", "PASSWORD");
		String ORG_ID = datas.GetTableValue("rows", "ORG_ID");
		String deptId = datas.GetTableValue("rows", "USER_DEPT_ID");
		String USER_DEPT_NAME = datas.GetTableValue("rows", "USER_DEPT_NAME");
		String roleId = datas.GetTableValue("rows", "ROLE_ID");
		String roleName = datas.GetTableValue("rows", "ROLE_NAME");
		String roleType = datas.GetTableValue("rows", "ROLE_TYPE");
		String isRoot = datas.GetTableValue("rows", "IS_ROOT");
		
		Operator operator = new Operator();
		operator.setIndx(Integer.parseInt(INDX));
		operator.setName(USER_REAL_NAME);
		operator.setUserName(USERNAME);
		operator.setUserMobile(datas.GetTableValue("rows", "USER_MOBILE"));
		operator.setPassWord(PASSWORD);
		operator.setOrgId(ORG_ID);	
		operator.setOrgName(datas.GetTableValue("rows", "ORG_NAME"));
		operator.setPartId(ORG_ID);
		operator.setDeptId(deptId);
		operator.setDeptName(USER_DEPT_NAME);
		operator.setRoleId(roleId);
		operator.setRoleName(roleName);
		operator.setRoleType(roleType);
		operator.setIsRoot(isRoot);
		operator.setUserType(datas.GetTableValue("rows", "USER_TYPE"));
		operator.setAppRecVer(BizConfigFactory.getCfgValue("APP_REC_VER", ORG_ID));
		operator.setEntRegNo(datas.GetTableValue("rows", "reg_no"));
		operator.setEntName(datas.GetTableValue("rows", "reg_name"));
		SessionManager.setAttribute(SysUtility.LoginUser, operator);
	}

 }
