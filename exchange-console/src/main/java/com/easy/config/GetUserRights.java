package com.easy.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/GetUserRights","/pages/GetUserRights","/pages/auth/GetUserRights"},name="GetUserRights")
public class GetUserRights extends com.easy.web.GetUserRights {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

}
