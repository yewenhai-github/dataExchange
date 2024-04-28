package com.easy.web;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
 * @author yewh 2016-05-16
 * 
 * @version 7.0.0
 * 
 */
public class GetCSSTemplet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/css; charset=UTF8");

		PrintWriter rw = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			rw = response.getWriter();
			String css = request.getQueryString();
			if(SysUtility.isEmpty(css) || css.indexOf("../") >= 0){
				return;
			}
			String cssTemplet = SysUtility.getCurrentCssTemplet();
			if(SysUtility.isNotEmpty(cssTemplet)){
				css = cssTemplet + "/" + css;
			}
			String str = this.getServletContext().getRealPath(css);//this.getServletContext().getResourceAsStream("/" + js);
			is = new FileInputStream(str);
			if(SysUtility.isEmpty(is)){
				return;
			}
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));  
			String s="";  
			while((s=br.readLine())!=null)  
				rw.println(s);
		} catch (Exception e) {
			LogUtil.printLog(request.getRequestURI()+":"+e.getMessage(), Level.ERROR);
		}  finally {
		  	SysUtility.closeBufferedReader(br);
		  	SysUtility.closeInputStream(is);
		  	SysUtility.closePrintWriter(rw);
		}
	}
}
