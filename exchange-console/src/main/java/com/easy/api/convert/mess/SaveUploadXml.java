package com.easy.api.convert.mess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.DocumentHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.api.convert.entity.ConfigNameEntity;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;



@WebServlet("/uploadXml")
public class SaveUploadXml extends MainServlet{
	public static int pindx = 0;
	public static int seq = 1;
	
	public void DoCommand() throws LegendException, Exception {
		seq = 1;
		// 使用Apache文件上传组件处理文件上传步骤：
		// 1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
		// 3、判断提交上来的数据是否是上传表单的数据
		if (!ServletFileUpload.isMultipartContent(getRequest())) {
			// 按照传统方式获取数据
			return;
		}
		// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
		List<FileItem> list = upload.parseRequest(getRequest());
		JSONArray UploXmlJsonArr = new JSONArray();
		for (FileItem item : list) {
			
			if (!item.isFormField()) {//只处理文件
				String filename = item.getName();

		        String filestypename = filename.substring(filename.lastIndexOf("\\") + 1);

		        String prefix = filestypename.substring(filestypename.lastIndexOf(".") + 1);
		        if ((!"xml".equalsIgnoreCase(prefix)) && (!"xls".equalsIgnoreCase(prefix))) {
		          ReturnMessage(Boolean.valueOf(false), "文件格式不正确,请重新上传!");
		          return;
		        }
		        String sql = "select count(0) from exs_convert_config_name where p_indx='" + pindx + "'";
		        List query4List = SQLExecUtils.query4List(sql);
		        if (query4List.size() > 0) {
		          sql = "delete exs_convert_config_name where  p_indx='" + pindx + "'";
		          SQLExecUtils.executeUpdate(sql);
		        }
		        InputStream in = item.getInputStream();
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		        byte[] buffer = new byte[1024];  
		        int len;  
		        while ((len = in.read(buffer)) > -1 ) {  
		            baos.write(buffer, 0, len);  
		        }  
		        baos.flush();      
		        InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());  
		        InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());  
		        byte[] bytes = SysUtility.InputStreamToByte(stream1);
		        String RootName = DocumentHelper.parseText(new String(bytes,"UTF-8")).getRootElement().getName();
		        int TargetFileFloor = 0;
		        HashMap xmlParse = FileUtility.xmlParse(stream2, RootName);
		        X2ToJson(xmlParse, UploXmlJsonArr, TargetFileFloor);
		        if(UploXmlJsonArr.length()>0) {
			        getDataAccess().Insert("exs_convert_config_name", UploXmlJsonArr);
			        ReturnMessage(Boolean.valueOf(true), "处理完成");
			        if(in != null) {
			        	in.close();
			        }
			        if(stream1 !=null) {
			        	stream1.close();
			        }
			        if(stream2 !=null) {
			        	stream2.close();
			        }
			        return;
		        }else {
			        ReturnMessage(Boolean.valueOf(false), "处理数据为空");
			        if(in != null) {
			        	in.close();
			        }
			        if(stream1 !=null) {
			        	stream1.close();
			        }
			        if(stream2 !=null) {
			        	stream2.close();
			        }
			        return;
		        }
					
			}else {
		        pindx = Integer.parseInt(item.getFieldName());
		    }
				
		} 
			
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void X2ToJson(Map resultMap, JSONArray UploXmlJsonArr, int TargetFileFloor)
			    throws JSONException {
			    Set keySet = resultMap.keySet();
			    Iterator it = keySet.iterator();
			    List itList = new ArrayList();
			   
			    while (it.hasNext()) {
			      itList.add(it.next());
			    }
			    for (int i = 0; i < itList.size(); ++i) {
			      HashMap ConfigNameMap = new  HashMap();
			      ConfigNameEntity configNameEntity = new ConfigNameEntity();
			      String RootName = (String)itList.get(i);
			      if (SysUtility.isNotEmpty(resultMap)) {
			    	ConfigNameMap.put("P_INDX", pindx);
			    	ConfigNameMap.put("SEQ", seq++);
			    	ConfigNameMap.put("TARGETCOLNAME", RootName);
			    	ConfigNameMap.put("TARGETFILEFLOOR", TargetFileFloor);
//			        configNameEntity.setP_indx(pindx);
//			        configNameEntity.setSeq(seq++);
//			        configNameEntity.setTargetColName(RootName);
//			        configNameEntity.setTargetFileFloor(TargetFileFloor);
			      }
			      Map resulMap = null;
			      if(SysUtility.isEmpty(resultMap.get(RootName).toString())) {
			    	  resulMap = new HashMap();
			      }else {
			    	  Object resuMap = resultMap.get(RootName); 
			    	  if (resuMap instanceof ArrayList) {
			    		  if(((ArrayList) resuMap).size()>1) {
			    			  ConfigNameMap.put("ISSUBLIST", "2");
			    			  ConfigNameMap.put("ISSUBLISTNAME", "重节点");
//			    			  configNameEntity.setIsSubList("2");
//			    			  configNameEntity.setIsSubListName("重节点");
			    		  }
			    		  resulMap = (Map) ((ArrayList) resuMap).get(0);
			    	  }else if (resuMap instanceof String) {
			    		  try {
			    			  if(((String) resuMap).length()>260) {
			    				  ConfigNameMap.put("CN_REMAKE", ((String)resuMap).substring(0, 260));
//			    				  configNameEntity.setCN_REMAKE(((String)resuMap).substring(0, 260));
			    			  }else {
			    				  ConfigNameMap.put("CN_REMAKE", ((String)resuMap));
//			    				  configNameEntity.setCN_REMAKE((String)resuMap); 
			    			  }
//			    			  configNameEntity.toString().replaceAll("\\\\", "");
//				    		  UploXmlJsonArr.put(new JSONObject(configNameEntity.toString()));
				    		  UploXmlJsonArr.put(new JSONObject(ConfigNameMap));
			    		  } catch (Exception e) {
//			    		  	  configNameEntity.setCN_REMAKE(null);
//			    		  	  configNameEntity.toString().replaceAll("\\\\", "");
			    			  UploXmlJsonArr.put(new JSONObject(ConfigNameMap));
			    		  }
			    		 
			    		  continue;
			    	  }else {
			    		  resulMap = (Map) resuMap; 
			    	  }
			      }
			      UploXmlJsonArr.put(new JSONObject(ConfigNameMap));
			      /*142 - 153 修改后逻辑无关 可以删除*/
			      /*
				       if (resulMap.keySet().size() == 1) {
				        it = resulMap.keySet().iterator();
				        if (((String)it.next()).equals(RootName)) {
				          Object object = resulMap.get(RootName);
				          if (object instanceof Map) {
				            resulMap = (Map)object;
				          } else if (object instanceof String) {
				            resulMap = new HashMap();
				            System.out.println("节点结束");
				          }
				        }
				      }
			      */
			      if (SysUtility.isNotEmpty(resulMap)) {
			    	  int CTargetFileFloor =TargetFileFloor+1;
			    	  X2ToJson(resulMap, UploXmlJsonArr, CTargetFileFloor);
			      }
			       
			    }
			  }
	
	
	 
	 
	 
	 public static byte[] InputStreamToByte(InputStream is) {
			ByteArrayOutputStream baos = null;
			try {
				int count = 0;
				baos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				while ((count = is.read(b, 0, 1024)) != -1) {
					baos.write(b, 0, count);
				}
				return baos.toByteArray();
			} catch (IOException e) {
				LogUtil.printLog("InputStreamToByte Error:"+e.getMessage(), LogUtil.ERROR);
			} finally{
				SysUtility.closeOutputStream(baos);
			}
			return new byte[0];
		}

}
