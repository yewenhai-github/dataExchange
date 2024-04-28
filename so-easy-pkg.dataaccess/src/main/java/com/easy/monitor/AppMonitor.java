package com.easy.monitor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

@WebServlet("/AppMonitor")
public class AppMonitor extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rt = "success";
		
		resp.setHeader("Content-type", "text/html;charset=UTF-8");
		try {
			PrintWriter ResponseWriter = resp.getWriter();
			ResponseWriter.print(rt);
			ResponseWriter.close();
		} catch (Exception e) {
			LogUtil.printLog("AppMonitor error!"+SysUtility.getStackTrace(e), Level.ERROR);
		}
	}
	
}
