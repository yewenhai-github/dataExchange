package com.easy.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/GetCSSTemplet","/pages/GetCSSTemplet","/pages/auth/GetCSSTemplet"},name="GetCSSTemplet")
public class GetCSSTemplet extends com.easy.web.GetCSSTemplet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

}
