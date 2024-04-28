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
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class GetJS extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/javascript; charset=UTF8");

		String js = "";
		PrintWriter rw = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			rw = response.getWriter();
			js = request.getQueryString();
			if(SysUtility.isEmpty(js) || js.indexOf("../") >= 0 || !js.toLowerCase().endsWith("js")){
				return;
			}
			is = this.getClass().getClassLoader().getResourceAsStream(js);//this.getServletContext().getResourceAsStream("/" + js);
			if(SysUtility.isEmpty(is)){
				return;
			}
			br=new BufferedReader(new InputStreamReader(is,"UTF-8"));  
			String s="";  
			while((s=br.readLine())!=null)  
				rw.println(s);
		} catch (Exception e) {
			LogUtil.printLog(request.getRequestURI()+":"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeBufferedReader(br);
			SysUtility.closeInputStream(is);
			SysUtility.closePrintWriter(rw);
		}
	}
}
