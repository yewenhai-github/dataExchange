package com.easy.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewh 2016-03-14
 * 
 * @version 7.0.0
 * 
 */
public class GetHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF8");

		String html = "";
		PrintWriter rw = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			rw = response.getWriter();
			html = request.getQueryString();
			if(SysUtility.isEmpty(html) || html.indexOf("../") >= 0){
				return;
			}
			if(html.indexOf("?") > 0){
				html = html.substring(0, html.indexOf("?"));
			}else if(html.indexOf("&") > 0){
				html = html.substring(0, html.indexOf("&"));
			}
			is = this.getClass().getClassLoader().getResourceAsStream(html);//this.getServletContext().getResourceAsStream("/" + js);
			if(SysUtility.isEmpty(is)){
				return;
			}
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));  
			String s="";  
			while((s = br.readLine())!=null){
				rw.println(s);
			}
		} catch (Exception e) {
			LogUtil.printLog(request.getRequestURI()+":"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeBufferedReader(br);
			SysUtility.closeInputStream(is);
			SysUtility.closePrintWriter(rw);
		}
	}
}
