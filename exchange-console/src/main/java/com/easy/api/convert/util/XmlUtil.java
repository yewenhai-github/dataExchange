package com.easy.api.convert.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Level;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

public class XmlUtil {



	/**
     * 生成文件
     * @param path
     * @param sXml
     * @return  -1:生成xml出错    0：创建成功   1：所要上报的文件已生成
	 * @throws DocumentException 
	 * @throws IOException 
     */
    public  void WriteStringToFile(String path,String sXml) throws DocumentException, IOException{
    	PrintStream ps = null;
    	   try {
               File file = new File(path);
               if(!file.exists()) {
            	   file.createNewFile();
               }
               ps = new PrintStream(new FileOutputStream(file));
               ps.println(sXml);// 往文件里写入字符串
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (Exception e) {
        	   e.printStackTrace();
           }finally {
        	   if(ps != null) {
        		   ps.close();
        	   }
           }
    }
    
    


}
