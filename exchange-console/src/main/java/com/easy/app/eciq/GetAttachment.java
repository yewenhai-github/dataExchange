package com.easy.app.eciq;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.file.ContentType;
import com.easy.utility.LogUtil;
import com.easy.web.MainServlet;

import sun.misc.BASE64Decoder;

@WebServlet("/forms/GetAttachment")
public class GetAttachment extends MainServlet{
private static final long serialVersionUID = 1564067536519132801L;
	
	public GetAttachment(){
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
		String ifInit= (String)getEnvDatas().get("ifInit");
		String decl_no=(String)getEnvDatas().get("decl_no");
		Datas datas=new Datas();
		if("Y".equals(ifInit)){
			String SelectSQL = "SELECT ATTACHMENT,ATTACH_NAME FROM ITF_DCL_MARK_LOB WHERE ATTACH_TYPE='1' and DECL_NO=?";
	        datas = getDataAccess().GetTableDatas("ITF_DCL_MARK_LOB", SelectSQL, decl_no);
		}else{
			String SelectSQL = "SELECT ATTACHMENT,ATTACH_NAME FROM ITF_DCL_MARK_LOB where ATTACH_TYPE='1' and MARK_ID = ?";
	        datas = getDataAccess().GetTableDatas("ITF_DCL_MARK_LOB", SelectSQL, indx);
		}
        String annexContent = datas.GetTableValue("ITF_DCL_MARK_LOB", "ATTACHMENT");
        String annexName = datas.GetTableValue("ITF_DCL_MARK_LOB", "ATTACH_NAME");
        try {
        	getResponse().reset();// 非常重要
        	if(annexName.indexOf(".")!=-1){
        		String suffix = annexName.substring(annexName.lastIndexOf("."));
        		getResponse().setContentType((String)ContentType.type.get(suffix));
        		  }
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
