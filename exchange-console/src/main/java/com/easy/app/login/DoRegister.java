package com.easy.app.login;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.security.MD5Utility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DoRegister")
public class DoRegister extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	String ErrMessages;

	public DoRegister() {
		CheckLogin = false;
	}

	public void DoCommand() throws Exception {
		IDataAccess DataAccess = getDataAccess();
		if (!Validate()) {
			ReturnMessage(false, "注册时错误：<br>" + ErrMessages);
			return;
		}
		String nameCode = GetDataValue("REGISTER", "USERNAME") + "@" + GetDataValue("REGISTER", "ORG_CODE");
		// 验证用户名是否已存在
		if (!IsExists(nameCode, GetDataValue("REGISTER", "ORG_CODE"))) {
			ReturnMessage(false, "注册时错误：<br>" + ErrMessages);
			return;
		}
		
		JSONObject tempDecl = new JSONObject();
		tempDecl.put("INDX", SysUtility.GetUUID());
		tempDecl.put("USERNAME", nameCode);
		tempDecl.put("PASSWORD", MD5Utility.encrypt(GetDataValue("REGISTER", "PASSWORD")));
		tempDecl.put("PASSWORD_CLEAR", GetDataValue("REGISTER", "PASSWORD"));
		tempDecl.put("ORG_CODE", GetDataValue("REGISTER", "ORG_CODE"));
		tempDecl.put("ORG_NAME", GetDataValue("REGISTER", "COMPANYNAME"));
		tempDecl.put("USER_REAL_NAME", GetDataValue("REGISTER", "LINKMAN"));
		tempDecl.put("USER_EMAIL", GetDataValue("REGISTER", "COMPANYEMAIL"));
		tempDecl.put("USER_MOBILE", GetDataValue("REGISTER", "LINKMANTEL"));
		tempDecl.put("IS_ENABLED", "1");
		boolean rt = DataAccess.Insert("EXs_auth_user", tempDecl, "INDX");
		if (rt) {
			ReturnMessage(true, "注册成功！登录名为 " + nameCode, "");
		} else {
			ReturnMessage(false, "注册失败!");
			DataAccess.RoolbackTrans();
			return;
		}
	}

	/// <summary>
	/// 必填项验证
	/// </summary>
	/// <returns></returns>
	private boolean Validate() {
		boolean rt = true;
		ErrMessages = "";
		try {
			if (SysUtility.isEmpty(GetDataValue("REGISTER", "ORG_CODE"))) {
				ErrMessages += "接入方代码不能为空！<br>";
				rt = false;
			}
			if (!GetDataValue("REGISTER", "PASSWORD").equals(GetDataValue("REGISTER", "PWD"))) {
				ErrMessages += "密码和确认密码不一致！<br>";
				rt = false;
			}
		} catch (LegendException e) {
			rt = false;
			e.printStackTrace();
		}
		return rt;
	}

	// 验证企业名称是否已存在
	private boolean IsExists(String cname, String orgcode) {
		boolean rt = true;
		try {
			ErrMessages = "";
			String sqlUser = String.format("Select COUNT(*) as RCount FROM EXs_auth_user Where USERNAME ='%s'", cname);
			Datas dt = getDataAccess().GetTableDatas("UserList", sqlUser);
			int counts = Integer.parseInt(dt.GetTableValue("UserList", "RCount"));
			if (counts != 0) {
				ErrMessages = "该用户名已存在，请重新填写！";
				rt = false;
			}
		} catch (LegendException e) {
			rt = false;
			e.printStackTrace();
		}
		return rt;
	}

}