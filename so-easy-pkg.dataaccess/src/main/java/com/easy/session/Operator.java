package com.easy.session;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;


/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class Operator{
	private static final long serialVersionUID = 4137108050486770371L;
	
	private int Indx;
	private String UserName;
	private String IcNo;
	private String SenderId;
	private String ReceiverId;
	private String PassWord;
	private String Name;
	private String Theme;
	private String StartPage;
	private String IsEnabled;
	private String OrgId;
	private String OrgName;
	private String orgCode;
	private String EmpId;
	private String DeptId;
	private String DeptName;
	private String PartId;
	private String EntCode;
	private String EntName;
	private String EntRegNo;
	private String IsRoot;
	private String RoleId;
	private String RoleName;
	private String RoleType;
	private String RoleLevel;
	public String getRolePLevel() {
		return RolePLevel;
	}
	public void setRolePLevel(String rolePLevel) {
		RolePLevel = rolePLevel;
	}
	private String RolePLevel;
	private String AppRecVer;
	private JSONObject UserData;
	private String Sign;
	private String Locale;
	private String UserType;
	private String UserMobile;
	private String systemName;
	
	private List<UserRight> UserRights = new ArrayList<UserRight>();
	
	public int getIndx() {
		return Indx;
	}
	public void setIndx(int indx) {
		Indx = indx;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getIcNo() {
		return IcNo;
	}

	public void setIcNo(String icNo) {
		IcNo = icNo;
	}

	public String getSenderId() {
		return SenderId;
	}

	public void setSenderId(String senderId) {
		SenderId = senderId;
	}

	public String getReceiverId() {
		return ReceiverId;
	}

	public void setReceiverId(String receiverId) {
		ReceiverId = receiverId;
	}

	public String getPassWord() {
		return PassWord;
	}
	public void setPassWord(String passWord) {
		PassWord = passWord;
	}
	public String getTheme() {
		return Theme;
	}
	public void setTheme(String theme) {
		Theme = theme;
	}
	public String getStartPage() {
		return StartPage;
	}
	public void setStartPage(String startPage) {
		StartPage = startPage;
	}
	public String getIsEnabled() {
		return IsEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		IsEnabled = isEnabled;
	}
	
	public String getEmpId() {
		return EmpId;
	}
	public void setEmpId(String empId) {
		EmpId = empId;
	}
	public String getDeptId() {
		return DeptId;
	}
	public void setIsRoot(String isRoot) {
		IsRoot = isRoot;
	}
	public String getIsRoot() {
		return IsRoot;
	}
	public String getRoleId() {
		return RoleId;
	}
	public void setRoleId(String roleId) {
		RoleId = roleId;
	}
	public String getRoleName() {
		return RoleName;
	}
	public void setRoleName(String roleName) {
		RoleName = roleName;
	}
	public String getRoleType() {
		return RoleType;
	}
	public void setRoleType(String roleType) {
		RoleType = roleType;
	}
	public String getRoleLevel() {
		return RoleLevel;
	}
	public void setRoleLevel(String roleLevel) {
		RoleLevel = roleLevel;
	}
	public String getAppRecVer() {
		return AppRecVer;
	}
	public void setAppRecVer(String appRecVer) {
		AppRecVer = appRecVer;
	}
	public JSONObject getUserData() {
		return UserData;
	}
	public void setUserData(JSONObject userData) {
		UserData = userData;
	}
	public String getSign() {
		return Sign;
	}
	public void setSign(String sign) {
		Sign = sign;
	}
	public void setDeptId(String deptId) {
		DeptId = deptId;
	}
	public String getDeptName() {
		return DeptName;
	}

	public void setDeptName(String deptName) {
		DeptName = deptName;
	}
	public String getOrgId() {
		return OrgId;
	}
	public void setOrgId(String orgId) {
		OrgId = orgId;
	}
	
	public String getOrgName() {
		return OrgName;
	}
	public void setOrgName(String orgName) {
		OrgName = orgName;
	}
	public String getPartId() {
		return PartId;
	}
	public void setPartId(String partId) {
		PartId = partId;
	}
	public String getEntCode() {
		return EntCode;
	}
	public void setEntCode(String entCode) {
		EntCode = entCode;
	}
	public String getEntName() {
		return EntName;
	}
	public void setEntName(String entName) {
		EntName = entName;
	}
	public String getEntRegNo() {
		return EntRegNo;
	}
	public void setEntRegNo(String entRegNo) {
		EntRegNo = entRegNo;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<UserRight> getUserRights() {
		return UserRights;
	}
	public void setUserRights(List<UserRight> userRights) {
		UserRights = userRights;
	}
	public String getLocale() {
		return Locale;
	}
	public void setLocale(String locale) {
		Locale = locale;
	}
	public String getUserType() {
		return UserType;
	}
	public void setUserType(String userType) {
		UserType = userType;
	}
	public String getUserMobile() {
		return UserMobile;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public void setUserMobile(String userMobile) {
		UserMobile = userMobile;
	}

}