package com.easy.api.convert.mess;


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
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.easy.api.convert.entity.ZtreeNode;
import com.easy.api.convert.util.MyXmlUtil;
import com.easy.api.convert.util.ParseXmlUtil;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetConfigYs")
public class GetConfigYs extends MainServlet{
	
	
	@Override
	protected void DoCommand() throws LegendException, Exception {
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
		for (FileItem item : list) {
			
			if (!item.isFormField()) {//只处理文件
				String filename = item.getName();
				// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
				String filestypename = filename.substring(filename.lastIndexOf("\\") + 1);
				//获取文件后缀名str1.equalsIgnoreCase(str2); 
				String prefix=filestypename.substring(filestypename.lastIndexOf(".")+1);
				if(!("xml".equalsIgnoreCase(prefix)||"xls".equalsIgnoreCase(prefix))){
				   ReturnMessage(false, "文件格式不正确,请重新上传!");
				   return;
				}
				// 获取item中的上传文件的输入流
				InputStream in = item.getInputStream();
				List<ZtreeNode> getZtreeList = GetZtreeList(in);
				
				ReturnMessage(true, "","",getZtreeList.toString());
			}
			
		}
	}
	
	
	
	
	
	public static List<ZtreeNode> GetZtreeList(InputStream in) throws DocumentException{
		 Map IdJl = new HashMap();
		 Map sonMap = new HashMap();
		 List<ZtreeNode> nodesList = new ArrayList<ZtreeNode>();
		 ZtreeNode ztreeNode = new ZtreeNode();
         // 获取一个xml文件   
         String textFromFile = MyXmlUtil.XmlToString(in);  
         String RootName = DocumentHelper.parseText(textFromFile).getRootElement().getName();
         ztreeNode.setId(RootName);
         ztreeNode.setName(RootName);
         nodesList.add(ztreeNode);
         //将xml解析为Map  
         Map resultMap = ParseXmlUtil.xml2map(textFromFile);  
         Set entrySet = resultMap.keySet();
         List DomList = new ArrayList();
         Iterator<String> it = entrySet.iterator();
         while (it.hasNext()) {
           String str = it.next();
           DomList.add(str);
         }
         for(int i =0;i < DomList.size(); i++) {
        	 String a= "";
        	 ZtreeNode Node = new ZtreeNode();
        	 String DomName = (String) DomList.get(i);
        	 if(SysUtility.isNotEmpty(sonMap.get(DomName))) {
        		 if(SysUtility.isNotEmptyMapList(sonMap.get(String.valueOf(i)))) {
        			 while(a=="") {
        				 java.util.Random r=new java.util.Random(10);
        				 int nextInt = r.nextInt();
        				 if(SysUtility.isNotEmptyMapList(sonMap.get(nextInt+"@"+i))) {
        					 sonMap.put(DomName+"1", nextInt+"@"+i); 
        				 }
            		 }
    			 }else {
    				 sonMap.put(DomName+"1", i);
    			 }
        		 
        		 
        	 }else {
        		 if(SysUtility.isNotEmptyMapList(sonMap.get(String.valueOf(i)))) {
        			 while(a=="") {
        				 java.util.Random r=new java.util.Random(10);
        				 int nextInt = r.nextInt();
        				 if(SysUtility.isNotEmptyMapList(sonMap.get(nextInt+"@"+i))) {
        					 sonMap.put(DomName+"1", nextInt+"@"+i); 
        				 }
            		 }
    			 }else {
    				 sonMap.put(DomName, i);
    			 }
        	 }
        	 Node.setId(DomName);
        	 Node.setPid(RootName);
        	 Node.setName(DomName);
        	 nodesList.add(Node);
         }
		return nodesList;
	}
	
	
	
    

}
