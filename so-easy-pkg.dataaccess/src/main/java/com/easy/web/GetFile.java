package com.easy.web;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.easy.file.ContentType;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author 
 * 
 * @version 7.0.0
 * 
 */
public class GetFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getQueryString();
		if(SysUtility.isEmpty(path) || path.indexOf("../") >= 0){
			return;
		}
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String contentType = "";
		if(SysUtility.isNotEmpty(path)){
			contentType = (String)ContentType.type.get(path.substring(path.lastIndexOf("."), path.length()));
		}
		if(SysUtility.isEmpty(contentType)){
			contentType = (String)ContentType.type.get(".*");
		}
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=" + path.substring(path.lastIndexOf("/")+1, path.length()));
		
		ServletOutputStream Outer = null;
		InputStream is = null;
		try {
			Outer = response.getOutputStream();
			String str = this.getServletContext().getRealPath(path);//this.getServletContext().getResourceAsStream("/" + js);
			is = new FileInputStream(str);
			if(SysUtility.isEmpty(is)){
				return;
			}
			byte[] buff = new byte[1024*1024];
			int l = 0;
			if(is != null){
				while( (l=is.read(buff, 0, 1024*1024)) > 0){
					Outer.write(buff, 0, l);
				}
				is.close();
			}
		} catch (Exception e) {
			LogUtil.printLog(request.getRequestURI()+":"+e.getMessage(), Level.ERROR);
		} finally {
		  	SysUtility.closeInputStream(is);
		  	SysUtility.closeServletOutputStream(Outer); 
		}
	}
}
