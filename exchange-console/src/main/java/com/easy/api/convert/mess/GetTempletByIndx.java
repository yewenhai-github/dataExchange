package com.easy.api.convert.mess;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetTempletByIndx")
public class GetTempletByIndx extends MainServlet {
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
		String  sql  = "@SELECT * FROM exs_convert_templet T   WHERE	T.INDX ='" + indx + "'";
		JSONObject jsonObj = GetReturnDatas(sql);
		JSONArray jsonArr = jsonObj.getJSONArray("rows");
		if(jsonArr .length() >0) {
			JSONObject jsonObject = jsonArr.getJSONObject(0);
			String TEMPLET_PATH = jsonObject.getString("TEMPLET_PATH");
			if(!SysUtility.isEmpty(TEMPLET_PATH)) {
				InputStream in = null;
				try {
					try {
						File file = new File(TEMPLET_PATH);
						in = new FileInputStream(file);
						getResponse().reset();
				        getResponse().setContentType("bin");
				        getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(),"UTF-8") + "\"");
				     // 循环取出流中的数据
				        byte[] b = new byte[100];
				        int len;
				        try {
				            while ((len = in.read(b)) > 0) {
				            	getResponse().getOutputStream().write(b, 0, len);
				            }
				            if(in!=null) {
								in.close();
							}
				           
				            ReturnMessage(true, "下载成功");
				            return;
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}catch (Exception e) {
						ReturnMessage(false, "该模板文件不存在!");
					}
					byte[] bytes = SysUtility.InputStreamToByte(in);
					String XmlData = new String(bytes,"UTF-8");
					jsonObject.put("XMLDATA", XmlData);
				} catch (Exception e) {
					ReturnMessage(false, "该模板文件不存在!");
				} finally {
					if(in != null) {
						in.close();
					}
				}
			}
		}
		
		jsonObj.put("TempletDATA", jsonArr);
		ReturnMessage(true, "", "", jsonObj.toString());	
	}

}