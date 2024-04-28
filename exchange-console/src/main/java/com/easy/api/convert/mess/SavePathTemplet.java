package com.easy.api.convert.mess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.dom4j.DocumentHelper;
import org.json.JSONArray;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
import com.sun.org.apache.bcel.internal.generic.Select;


@SuppressWarnings("serial")
@WebServlet("/SavePathTemplet")
public class SavePathTemplet extends MainServlet{

	@SuppressWarnings("unused")
	@Override
	public void DoCommand() throws LegendException, Exception {
		
		final String INDX =  getRequest().getParameter("INDX");
		String PATH_INDX = getRequest().getParameter("PATH_INDX");
		String sql = "select count(0) from exs_convert_config_name where p_indx='" + PATH_INDX + "'";
        List query4List = SQLExecUtils.query4List(sql);
        if (query4List.size() > 0) {
          sql = "delete exs_convert_config_name where  p_indx='" + PATH_INDX + "'";
          SQLExecUtils.executeUpdate(sql);
        }
        List query4List2 = SQLExecUtils.query4List("select * from exs_convert_templet where indx = ?",new Callback() {
			
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, INDX);
			}
		});
        if(query4List2.size()>0) {
        	JSONArray UploXmlJsonArr = new JSONArray();
        	HashMap TempLet = (HashMap)query4List2.get(0);
        	String TEMPLET_PATH = (String) TempLet.get("TEMPLET_PATH");
        	if(!SysUtility.isEmpty(TEMPLET_PATH)) {
        		InputStream in = null;
        		InputStream stream1 = null;
        		InputStream stream2 = null;
        		try {
					in = new FileInputStream(TEMPLET_PATH);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			        byte[] buffer = new byte[1024];  
			        int len;  
			        while ((len = in.read(buffer)) > -1 ) {  
			            baos.write(buffer, 0, len);  
			        }  
			        baos.flush();      
			        stream1 = new ByteArrayInputStream(baos.toByteArray());  
			        stream2 = new ByteArrayInputStream(baos.toByteArray()); 
			        byte[] bytes = SysUtility.InputStreamToByte(stream1);
			        String RootName = DocumentHelper.parseText(new String(bytes,"UTF-8")).getRootElement().getName();
			        int TargetFileFloor = 0;
			        HashMap xmlParse = FileUtility.xmlParse(stream2, RootName);
			        SaveUploadXml SaveUploadXml = new SaveUploadXml();
			        SaveUploadXml.pindx =Integer.parseInt(PATH_INDX);
			        SaveUploadXml.X2ToJson(xmlParse, UploXmlJsonArr, TargetFileFloor);
			        if(UploXmlJsonArr.length()>0) {
				        getDataAccess().Insert("exs_convert_config_name", UploXmlJsonArr);
				        ReturnMessage(Boolean.valueOf(true), "处理完成");
				        return;
			        }
				} catch (Exception e) {
					
				} finally {
					if(in != null) {
			        	in.close();
			        }
			        if(stream1 !=null) {
			        	stream1.close();
			        }
			        if(stream2 !=null) {
			        	stream2.close();
			        }
				}
        		
        		
        	}
        }
	}

}
