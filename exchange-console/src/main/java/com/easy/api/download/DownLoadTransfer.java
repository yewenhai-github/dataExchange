package com.easy.api.download;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;

import com.easy.context.AppContext;
import com.easy.file.ContentType;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.web.MainServlet;

import sun.misc.BASE64Decoder;

@WebServlet("/DownLoadTransfer")
public class DownLoadTransfer extends MainServlet{
private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String fileName = "X-LinkTransfer.doc";
		String path = AppContext.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "classes"+ File.separator + fileName;
        String annexContent = FileUtility.readFile(path);
        try {
			String suffix = fileName.substring(fileName.lastIndexOf("."));
			getResponse().reset();// 非常重要
			getResponse().setContentType((String)ContentType.type.get(suffix));
			getResponse().setHeader("Content-Disposition", "attachment; filename=" + fileName);
			
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] contentByte = decoder.decodeBuffer(annexContent);
			InputStream is = new ByteArrayInputStream(contentByte);
			if(is != null){
				getResponse().setContentLength(is.available());
			}
			
			ServletOutputStream out = getResponse().getOutputStream();
			int c;
			while((c=is.read())!=-1){
				out.write(c);
			}
			out.flush();
			is.close();
			out.close();
		} catch (Exception e) {
			LogUtil.printLog("处理请求时发生错误:"+e.getMessage(), LogUtil.ERROR);
		}
	}
}
