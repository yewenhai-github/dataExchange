package com.easy.api.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

@WebServlet("/GetFileData")
public class GetFileData extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rt = "";
		resp.setHeader("Content-type", "text/html;charset=UTF-8");
		try {
			String filePath = req.getParameter("FILE_PATH");
			String fileName = req.getParameter("FILE_NAME");
			rt = FileUtility.readFile(filePath+File.separator+fileName, false);
		} catch (Exception e) {
			rt = "GetFileData error!"+e.getMessage();
			LogUtil.printLog("GetFileData error!"+SysUtility.getStackTrace(e), Level.ERROR);
		}
		PrintWriter ResponseWriter = resp.getWriter();
		ResponseWriter.print(rt);
		ResponseWriter.close();
	}
	
}
