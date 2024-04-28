package com.easy.web;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
public class GetIMG extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String img = request.getQueryString();
		if(SysUtility.isEmpty(img) || img.indexOf("../") >= 0){
			return;
		}
		MimetypesFileTypeMap mft = new MimetypesFileTypeMap();
		String ctype = mft.getContentType(img);
		response.setContentType(ctype);	
		ServletOutputStream Outer = null;
		InputStream is = null;
		try {
			Outer = response.getOutputStream();
			is = this.getClass().getClassLoader().getResourceAsStream(img);//this.getServletContext().getResourceAsStream("/" + js);
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
