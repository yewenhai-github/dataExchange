package com.easy.api.convert.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class MyXmlUtil {
	
	/** 
     * 加载xml文件 
     * @return Document 
     */  
    public static Document load(InputStream in){  
          Document document=null;   
//      String url="C:\\Users\\Administrator\\Desktop\\运单回执.xml";   
      try {   
          SAXBuilder reader = new SAXBuilder();    
          document=reader.build(in);   
      } catch (Exception e) {   
          e.printStackTrace();   
      } finally {
		
      }
      return document;  
    }  
	
    
    
    /** 
     * 将xml文件转换为String串   此方法不会为你关闭流
     * @return  
     */  
    public static String XmlToString(InputStream in){  
          Document document=null;   
      document=load(in);   
      Format format =Format.getPrettyFormat();       
      format.setEncoding("UTF-8");//设置编码格式    
      StringWriter out=null; //输出对象   
      String sReturn =""; //输出字符串   
      XMLOutputter outputter =new XMLOutputter();    
      out=new StringWriter();    
      try {   
         outputter.output(document,out);   
      } catch (IOException e) {   
         e.printStackTrace();   
      } finally {
		outputter.clone();
      }   
      sReturn=out.toString();    
      return sReturn;   
  }   

    
}
