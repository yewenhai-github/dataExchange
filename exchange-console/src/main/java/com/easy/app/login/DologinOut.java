package com.easy.app.login;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

/**
 * Servlet implementation class DoLogin
 */
@WebServlet("/DologinOut")
public class DologinOut extends MainServlet {
	private static final long serialVersionUID = 1L;

	public DologinOut() {
		super();
	}
	
	public void DoCommand() throws Exception {
		if(getSession() != null) {
			getSession().invalidate();			
		}
		//和访问页面保持相对路径
		//		Response.sendRedirect("login.html");
		ReturnMessage(false, "", LoginPage);
	}

 }
