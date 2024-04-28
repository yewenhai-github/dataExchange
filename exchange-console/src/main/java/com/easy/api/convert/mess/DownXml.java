package com.easy.api.convert.mess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;



@WebServlet("/DownXml")
public class DownXml extends MainServlet {
	

	@Override
	public void DoCommand() throws LegendException, Exception {
		final String Indx = (String) getRequest().getParameter("INDX");
		final String TYPE = (String) getRequest().getParameter("TYPE");
		String sql = "SELECT TARGET_FILE_NAME,SOURCE_FILE_NAME, SOURCE_BACK_PATH AS SOURCEPATH ,SUCCESS_BACK_PATH AS SUCCESSPATH FROM exs_convert_log WHERE INDX = ?";
		 
		List query4List = SQLExecUtils.query4List(sql, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, Indx);
				}
			});
		
		if(query4List.size()>0) {
			HashMap successMap = (HashMap) query4List.get(0);
			String successPath = (String) successMap.get("SUCCESSPATH");
			String SOURCEPATH = (String) successMap.get("SOURCEPATH");
			String FILENAME = "";
			 // 读到流中
	        InputStream inStream = null;
	        if(SysUtility.isNotEmpty(TYPE)) {
	        	if(!SysUtility.isEmpty(SOURCEPATH)) {
		        	String filepath = SOURCEPATH.replace("\\", "/");
					int d_ = filepath.length();
					int p_ = filepath.lastIndexOf("/")+1;
					FILENAME = filepath.substring(p_,d_);
	    			inStream = new FileInputStream(SOURCEPATH);// 文件的存放路径
		        }
    		}else {
    			 if(!SysUtility.isEmpty(successPath) ) {
    	    			String[] split = successPath.split(",");
    	    			File tempFile =new File(split[0]);
    					FILENAME =  tempFile.getName(); 
    	    			inStream = new FileInputStream(split[0]);// 文件的存放路径
    		    }
    		}
    			/*for (String string : split) {
    				
    			}*/
				// 设置输出的格式
	        try {
		        getResponse().reset();
		        getResponse().setContentType("bin");
		        getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(FILENAME,"UTF-8") + "\"");
		        // 循环取出流中的数据
		        byte[] b = new byte[100];
		        int len;
		        try {
		            while ((len = inStream.read(b)) > 0) {
		            	getResponse().getOutputStream().write(b, 0, len);
		            }
		            inStream.close();
		            return;
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        ReturnMessage(false, "数据不存在");
				return;
	        } catch (Exception e) {
	        	ReturnMessage(false, e.getMessage());
	        	return;
			}	
			
		}else {
			ReturnMessage(false, "数据不存在");
			return;
		}
	}

	
}
