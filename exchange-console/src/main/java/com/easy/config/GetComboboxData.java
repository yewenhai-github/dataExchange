package com.easy.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/GetComboboxData","/pages/GetComboboxData","/pages/auth/GetComboboxData"},name="GetComboboxData")
public class GetComboboxData extends com.easy.web.GetComboboxData {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

}
