package com.easy.session;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class SessionManager {
	private static final Map<String, Operator> UserMap = new HashMap<String, Operator>();
	private final static ThreadLocal<HttpSession> httpSession = new ThreadLocal<HttpSession>();
	private final static ThreadLocal<Map<String, Object>> userSession = new ThreadLocal<Map<String, Object>>() {
		protected synchronized Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};
	private final static ThreadLocal<IDataAccess> DataAccess = new ThreadLocal<IDataAccess>();
	private final static ThreadLocal<IDataAccess> DataAccess2 = new ThreadLocal<IDataAccess>();
	private final static ThreadLocal<HttpServletRequest> Request = new ThreadLocal<HttpServletRequest>();
	private final static ThreadLocal<HttpServletResponse> Response = new ThreadLocal<HttpServletResponse>();
	private final static ThreadLocal<Cookie[]> Cookies = new ThreadLocal<Cookie[]>();
	private final static ThreadLocal<Hashtable<String, Object>> EnvDatas = new ThreadLocal<Hashtable<String, Object>>();
	private final static ThreadLocal<Datas> FormDatas = new ThreadLocal<Datas>();
	private final static ThreadLocal<String> LoginPage = new ThreadLocal<String>();
	private final static ThreadLocal<String> StartPage = new ThreadLocal<String>();
	private final static ThreadLocal<Boolean> CheckLogin = new ThreadLocal<Boolean>();
	private final static ThreadLocal<Boolean> IsPost = new ThreadLocal<Boolean>();
	
	public static void setSession(HttpSession session) {
		if (session != null) {
			httpSession.set(session);
		}
	}

	public static HttpSession getSession() {
		return (HttpSession) httpSession.get();
	}

	public static void setAttribute(String var, Object value) {
		if (httpSession.get() != null && (SysUtility.LoginUser.equals(var))) {
			HttpSession session = (HttpSession) httpSession.get();
			Operator obj = (Operator) value;
			String userId = String.valueOf(obj.getIndx());
			session.setAttribute(var, userId);
			UserMap.put(userId, (Operator) value);
		} else {
			userSession.get().put(var, value);
		}
	}

	public static Object getAttribute(String key) {
		Object value = null;
		if (httpSession.get() != null && (SysUtility.LoginUser.equals(key))) {
			HttpSession session = (HttpSession) httpSession.get();
			String userId = (String) session.getAttribute(key);
			if (userId != null && !userId.trim().equals("")) {
				value = UserMap.get(userId);
			}
		}else {
			value = userSession.get().get(key);
		}
		return value;
	}

	public static Operator getUser() {
		return (Operator) getAttribute(SysUtility.LoginUser);
	}

	public static void clearUser() {
		if (httpSession.get() == null) {
			throw new RuntimeException("session not exist");
		}
		HttpSession session = (HttpSession) httpSession.get();
		Operator user = (Operator) session.getAttribute(SysUtility.LoginUser);
		if (user != null) {
			session.removeAttribute(SysUtility.LoginUser);
		}
	}

	public static void destorySession() {
		httpSession.set(null);
		DataAccess.set(null);
		DataAccess2.set(null);
		Request.set(null);
		Response.set(null);
		Cookies.set(null);
		EnvDatas.set(null);
		FormDatas.set(null);
		LoginPage.set(null);
		StartPage.set(null);
		CheckLogin.set(null);
		IsPost.set(null);
		userSession.get().clear();
	}
	
	public static void logoutSession() {
		// 得到HttpSession，清除HttpSession中用户定义对象并使其失效
		HttpSession hs = httpSession.get();
		if (hs != null) {
			hs.removeAttribute(SysUtility.LoginUser);
			hs.invalidate();
		}
		userSession.get().clear();
		DataAccess.set(null);
		DataAccess2.set(null);
		Request.set(null);
		Response.set(null);
		Cookies.set(null);
		EnvDatas.set(null);
		FormDatas.set(null);
		LoginPage.set(null);
		StartPage.set(null);
		CheckLogin.set(null);
		IsPost.set(null);
	}

	public static void setDataAccess(IDataAccess IDataAccess) {
		if (IDataAccess != null) {
			DataAccess.set(IDataAccess);
		}
	}
	
	public static void setDataAccessByName(IDataAccess IDataAccess,String DBName) {
		if (SysUtility.isNotEmpty(IDataAccess) && SysUtility.isNotEmpty(DBName)) {
			DataAccess2.set(IDataAccess);
		}
	}

	public static void setRequest(HttpServletRequest httpRequest) {
		if (httpRequest != null) {
			Request.set(httpRequest);
		}
	}

	public static void setResponse(HttpServletResponse httpResponse) {
		if (httpResponse != null) {
			Response.set(httpResponse);
		}
	}

	public static void setCookies(Cookie[] cookies) {
		if (cookies != null) {
			Cookies.set(cookies);
		}
	}

	public static void setEnvDatas(Hashtable<String, Object> envdatas) {
		if (envdatas != null) {
			EnvDatas.set(envdatas);
		}
	}

	public static void setFormDatas(Datas datas) {
		if (datas != null) {
			FormDatas.set(datas);
		}
	}

	public static void setLoginPage(String loginpage) {
		if (loginpage != null) {
			LoginPage.set(loginpage);
		}
	}

	public static void setStartPage(String startpage) {
		if (startpage != null) {
			StartPage.set(startpage);
		}
	}

	public static void setCheckLogin(boolean checklogin) {
		CheckLogin.set(checklogin);
	}

	public static void setIsPost(boolean ispost) {
		IsPost.set(ispost);
	}

	public static IDataAccess getDataAccess() {
		return (IDataAccess)DataAccess.get();
	}

	public static IDataAccess getDataAccessByName() {
		return (IDataAccess)DataAccess2.get();
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest)Request.get();
	}

	public static HttpServletResponse getResponse() {
		return (HttpServletResponse)Response.get();
	}

	public static Cookie[] getCookies() {
		return (Cookie[])Cookies.get();
	}

	public static Hashtable<String, Object> getEnvDatas() {
		return (Hashtable<String, Object>)EnvDatas.get();
	}

	public static Datas getFormDatas() {
		return FormDatas.get();
	}

	public static String getLoginPage() {
		return LoginPage.get();
	}

	public static String getStartPage() {
		return StartPage.get();
	}

	public static boolean getCheckLogin() {
		return CheckLogin.get();
	}

	public static boolean getIsPost() {
		return IsPost.get();
	}

}
