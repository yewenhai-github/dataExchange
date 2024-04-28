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
public class GetCSS extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/css; charset=UTF8");

		PrintWriter rw = null;
		InputStream is = null;
		BufferedReader br = null;
		String css = "";
		try {
			rw = response.getWriter();
			css = request.getQueryString();
			if(SysUtility.isEmpty(css) || css.indexOf("../") >= 0 || !css.toLowerCase().endsWith("css")){
				return;
			}
			is = this.getClass().getClassLoader().getResourceAsStream(css);//this.getServletContext().getResourceAsStream("/" + js);
			if(SysUtility.isEmpty(is)){
				return;
			}
			br=new BufferedReader(new InputStreamReader(is,"UTF-8"));  
			String s="";  
			while((s=br.readLine())!=null)  
				rw.println(s);
		} catch (Exception e) {
			LogUtil.printLog(request.getRequestURI()+",QueryString:"+css+","+e.getMessage(), Level.ERROR);
		}  finally {
			SysUtility.closeBufferedReader(br);
			SysUtility.closeInputStream(is);
			SysUtility.closePrintWriter(rw);
		}
	}

}
