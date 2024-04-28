package com.easy.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.Set;

import org.apache.log4j.Level;

import com.easy.context.AppContext;
import com.easy.exception.LegendException;
import com.easy.file.FileFilterHandle;
import com.easy.file.FileNameFilter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
public final class FileUtility {
	public static final String XML_UTF8_ENCODING = "UTF-8";
	public static BASE64Encoder base64encoder = new BASE64Encoder();
	public static BASE64Decoder base64decoder = new BASE64Decoder();
	
	/**
	 * 创建本地文件
	 * */
	public static boolean createFile(String lockPath,String fileName,String xmlData) {
		return createFile(lockPath, fileName, xmlData, XML_UTF8_ENCODING);
	}
	
	public static boolean createFile(String lockPath,String fileName,String xmlData,String Encoding) {
		if(SysUtility.isEmpty(lockPath) || SysUtility.isEmpty(xmlData)){
			return false;
		}
		File file = new File(lockPath);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+lockPath, Level.INFO);
		   }
		}
		String path = lockPath + File.separator + fileName;
		path = renameFileNameXmlToExs(path);
		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		byte tempByte[] = null;
		try {
			fo = new FileOutputStream(path, false);
			bo = new BufferedOutputStream(fo);
			tempByte = xmlData.getBytes(Encoding);
			bo.write(tempByte, 0, tempByte.length);
			LogUtil.printLog("文件已生成："+path, Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			try {
				bo.flush();
				bo.close();
				fo.close();
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		try {
			renameFileExsToXml(path);
			LogUtil.printLog("文件已重命名："+ path.substring(0, path.length() - 4)+".xml", Level.INFO);
			return true;
		} catch (Exception e) {
			LogUtil.printLog("文件重名失败："+path, Level.ERROR);
		}
		return false;
	}
	
	/**
	 * 创建本地文件
	 * */
	public static boolean createFile(String lockPath,String fileName,InputStream is) {
		if(SysUtility.isEmpty(lockPath)){
			return false;
		}
		File file = new File(lockPath);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+lockPath, Level.INFO);
		   }
		}
		String path = lockPath + File.separator + fileName;
		path = renameFileNameXmlToExs(path);
		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		byte tempByte[] = null;
		try {
			fo = new FileOutputStream(path, false);
			bo = new BufferedOutputStream(fo);
			int count = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((count = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, count);
			}
			tempByte = baos.toByteArray();
			bo.write(tempByte, 0, tempByte.length);
			LogUtil.printLog("文件已生成："+path, Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			try {
				bo.flush();
				bo.close();
				fo.close();
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		try {
			renameFileExsToXml(path);
			LogUtil.printLog("文件已重命名："+ path.substring(0, path.length() - 4)+".xml", Level.INFO);
			return true;
		} catch (Exception e) {
			LogUtil.printLog("文件重名失败："+path, Level.ERROR);
		}
		return false;
	}
		
	/**
	 * 创建本地文件
	 * */
	public static boolean createFile(String path,InputStream is) {
		if(SysUtility.isEmpty(path)){
			return false;
		}
		path = renameFileNameXmlToExs(path);
		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		byte tempByte[] = null;
		try {
			fo = new FileOutputStream(path, false);
			bo = new BufferedOutputStream(fo);
			int count = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((count = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, count);
			}
			tempByte = baos.toByteArray();
			bo.write(tempByte, 0, tempByte.length);
			LogUtil.printLog("文件已生成："+path, Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			try {
				bo.flush();
				bo.close();
				fo.close();
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
		try {
			renameFileExsToXml(path);
			LogUtil.printLog("文件已重命名："+ path.substring(0, path.length() - 4)+".xml", Level.INFO);
			return true;
		} catch (Exception e) {
			LogUtil.printLog("文件重名失败："+path, Level.ERROR);
		}
		return false;
	}
	
	/**
	 * 删除文件
	 * **/
	public static void deleteFile(File file){
		try {
			file.delete();
			LogUtil.printLog("文件删除成功："+file.getPath(), Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog("文件删除失败："+e.getMessage(), Level.INFO);
		}
	}
	
	/**
	 * 删除文件
	 * **/
	public static void deleteFile(String path){
		try {
			File file = new File(path);
			file.delete();
			LogUtil.printLog("文件删除成功："+path, Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog("文件删除失败："+e.getMessage(), Level.INFO);
		}
	}
	
	/**
	 * 删除文件
	 * **/
	public static boolean deleteFile(String path,String fileName){
		if(SysUtility.isEmpty(path) || SysUtility.isEmpty(fileName)){
			return false;
		}
		try {
			path = path + File.separator + fileName;
			File file = new File(path);
			if(file.exists()){
				return file.delete();
			}
			LogUtil.printLog("文件删除成功："+path, Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog("文件删除失败："+e.getMessage(), Level.INFO);
		}
		return true;
	}
	
	/**
	 * 读取文件
	 * ***/
	public static String readFile(String path){
		return readFile(path, true);
	}
	
	/**
	 * 读取文件
	 * **/
	public static String readFile(String path,boolean Base64Encoder){
		File file = new File(path);
		return readFile(file,Base64Encoder,XML_UTF8_ENCODING);
	}
	
	public static String readFile(String path,boolean Base64Encoder,String Encoding){
		return readFile(new File(path),Base64Encoder,Encoding);
	}
	
	public static String readFile(File file,boolean Base64Encoder,String Encoding){
		String content = "";
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			if (!file.exists()) {
				return "";
			}
			is = new FileInputStream(file);
			int count = 0;
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((count = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, count);
			}
			byte tempByte[] = baos.toByteArray();
			if(Base64Encoder){
			    content = base64encoder.encode(tempByte);
			}else{
				content = new String(tempByte,Encoding);
			}
		} catch (FileNotFoundException e1) {
			LogUtil.printLog("文件不存在，无法读取:"+e1.getMessage(), LogUtil.ERROR);
		} catch (IOException e1) {
			LogUtil.printLog("文件无法读取:"+e1.getMessage(), LogUtil.ERROR);
		} finally{
			SysUtility.closeInputStream(is);
			SysUtility.closeOutputStream(baos);
		}
		return content;
	}
	
	public static byte[] readFileByte(String path){
		if(SysUtility.isEmpty(path)) {
			return null;
		}
		
		File file = new File(path);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(file);
			int count = 0;
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((count = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, count);
			}
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			LogUtil.printLog("Error:"+e.getMessage(), LogUtil.ERROR);
		} catch (IOException e) {
			LogUtil.printLog("Error:"+e.getMessage(), LogUtil.ERROR);
		}finally{
			SysUtility.closeInputStream(is);
			SysUtility.closeOutputStream(baos);
		}
		return null;
	}
	
	/**
	 * 拷贝文件
	 * @param source 源文件地址
	 * @param dest 目标文件地址
	 * @return
	 */
	public static boolean copyFile(String sFileName, String dFileName) {
		if(SysUtility.isEmpty(sFileName) || SysUtility.isEmpty(dFileName)){
			return false;
		}
		
		boolean booRec = false;
		try {
			File file_in = new File(sFileName);
			
			dFileName = renameFileNameXmlToExs(dFileName);
			File file_out = new File(dFileName);
			FileInputStream fis = new FileInputStream(file_in);
			FileOutputStream fos = new FileOutputStream(file_out);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = fis.read(bytes)) != -1) {
				fos.write(bytes, 0, c);
			}
			fis.close();
			fos.close();
			LogUtil.printLog("文件已生成："+dFileName, Level.INFO);
			booRec = renameFileExsToXml(dFileName);
			if(booRec) {
				LogUtil.printLog("文件已重命名："+dFileName, Level.INFO);
			}else {
				LogUtil.printLog("文件重名失败："+dFileName, Level.INFO);
			}
			LogUtil.printLog("拷贝文件成功："+dFileName, Level.INFO);
		} catch (Exception ex) {
			LogUtil.printLog("拷贝文件失败:"+ex.getMessage(), LogUtil.ERROR);
		}
		return booRec;
	}
	
	/**
	 * 拷贝文件
	 * @param SourcePath 源文件地址
	 * @param TargetPath 目标文件地址
	 * @param FileName 文件名
	 * @return
	 */
	public static boolean copyFile(String SourcePath, String TargetPath, String FileName) {
		if(SysUtility.isEmpty(SourcePath) || SysUtility.isEmpty(TargetPath) || SysUtility.isEmpty(FileName)){
			return false;
		}
		
		String sFileName = SourcePath + File.separator + FileName;
		String dFileName = TargetPath + File.separator + FileName;
		return copyFile(sFileName, dFileName);
	}
	
	/**
	 * 移动文件
	 * **/ 
	public static boolean removeFile(String souceDir, String sourceFileName,String targetDir, String targetFileName) {
		boolean booRec = false;
		try {
			File file = new File(targetDir);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹创建成功："+targetDir, Level.INFO);
			   }
			}
			new File(souceDir, sourceFileName).renameTo(new File(targetDir,targetFileName));
			booRec = true;
		} catch (Exception e) {
			LogUtil.printLog("移动文件失败："+e.getMessage(), LogUtil.ERROR);
		}
		return booRec;
	}
	
	//将报文转换成HashMap
	public static HashMap xmlParse(InputStream inputStream, String xmlRootName) throws LegendException {
		return xmlParse(inputStream,xmlRootName, true);
	}

	public static HashMap xmlParse(InputStream inputStream, String xmlRootName,String Encoding) throws LegendException {
		return xmlParse(inputStream,xmlRootName, true, Encoding);
	}
	
	public static HashMap xmlParse(InputStream inputStream, String xmlRootName, boolean filterEnable)throws LegendException {
		return new XML2HashMapHandler().getParseResult(inputStream, filterEnable,xmlRootName);
	}
	
	public static HashMap xmlParse(InputStream inputStream, String xmlRootName, boolean filterEnable,String Encoding)throws LegendException {
		return new XML2HashMapHandler().getParseResult(inputStream, filterEnable,xmlRootName,Encoding);
	}
	
	public static HashMap parseHashMap(HashMap hmSourceData, String xmlRootName, boolean isRootNode)throws LegendException {
		Object obj = hmSourceData.get(xmlRootName);
		HashMap hmReturn = new HashMap();
		if (obj != null) {
			if (isRootNode && obj.equals("")) {
				throw LegendException.getLegendException("XML解析出错！");
			} else if (obj instanceof List) {
				List list = (List) obj;
				if (list.size() > 0) {
					return (HashMap) list.get(0);
				}
			}
			return hmReturn;
		} else {
			throw LegendException.getLegendException("XML解析出错！");
		}
	}
	
	public static String parseXml(Map rootMap,String rootName){
		StringBuffer xmlData = new StringBuffer();
		xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlData.append(hashMapToXml(rootMap, rootName,2));
		return xmlData.toString();
	}
	
	/**
	 * TODO parseXMLCData
	 * **/
	public static String processXmlValue(String str){
		if(SysUtility.isEmpty(str)){
			return "";
		}
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&apos;").replaceAll("\"\"", "&quot;");
	}
	
	public static String hashMapToXml(Map rootMap,String rootName,int countT){
		StringBuffer xmlData = new StringBuffer();
		Set mapSet = rootMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
	        Object key = entry.getKey();
	        Object value = entry.getValue();
			if (key.toString().equalsIgnoreCase(rootName)) {
				Object tmpObj = (Object)rootMap.get(key);
				if (tmpObj instanceof List) {
					List list = (List)tmpObj;
					for (int j = 0; j < list.size(); j++) {
						xmlData.append("\t\t<"+rootName+">");
						xmlData.append("\n");
						HashMap temMap = (HashMap)list.get(j);//只处理一个报文一条主记录的场景。
						Set mapSet2 = temMap.entrySet();
						for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
							Entry entry2 = (Entry)it2.next();
					        Object key2 = entry2.getKey();
					        Object value2 = entry2.getValue();
					        if(value2 instanceof HashMap) {
					        	HashMap childMap = (HashMap)value2;
					        	Set mapSet3 = childMap.entrySet();
					        	for (Iterator it3 = mapSet3.iterator(); it3.hasNext();) {
					        		Entry entry3 = (Entry)it3.next();
							        Object key3 = entry3.getKey();
							        Object value3 = entry3.getValue();
							        if(value3 instanceof List) {
							        	List temList = (List)value3;
							        	xmlData.append("\t\t\t<"+key2+">\n");
										for (int i = 0; i < temList.size(); i++) {
											HashMap childHashMap = (HashMap)temList.get(i);
											StringBuffer clildXmlData = new StringBuffer();
											clildXmlData.append("\t\t\t\t");
											clildXmlData.append("<"+key3+">");
											clildXmlData.append("\n");
											clildXmlData.append(hashMapToXml(childHashMap, key3.toString(), 5));
											clildXmlData.append("\t\t\t\t");
											clildXmlData.append("</"+key3+">");
											clildXmlData.append("\n");
											xmlData.append(clildXmlData);
										}
										xmlData.append("\t\t\t</"+key2+">\n");
							        }
					        	}
					        }else if(value2 instanceof List) {
								List temList = (List)value2;
								for (int i = 0; i < temList.size(); i++) {
									HashMap childHashMap = (HashMap)temList.get(i);
									StringBuffer clildXmlData = new StringBuffer();
									clildXmlData.append("\t\t\t<"+key2+">\n");
									clildXmlData.append(hashMapToXml(childHashMap, key2.toString(), 4));
									clildXmlData.append("\t\t\t</"+key2+">\n");
									xmlData.append(clildXmlData);
								}
							}else{
								if(SysUtility.isNotEmpty(value2) 
										&& !"ORG_ID".equalsIgnoreCase(key2.toString())
										&& !"DEPT_ID".equalsIgnoreCase(key2.toString())
										&& !"CREATOR".equalsIgnoreCase(key2.toString())
										&& !"CREATE_TIME".equalsIgnoreCase(key2.toString())
										&& !"REC_VER".equalsIgnoreCase(key2.toString())){
					            	xmlData.append("\t\t<").append(key2).append(">");
					            	xmlData.append(processXmlValue(value2.toString()));
					            	xmlData.append("</").append(key2).append(">\n");
								}
							}
						}
						xmlData.append("\t\t</"+rootName+">\n");
					}
				}
			}else if(key.toString().equalsIgnoreCase(rootName+"Information")
					|| (rootName.indexOf("List") > 0 && key.toString().equalsIgnoreCase(rootName.substring(0, rootName.indexOf("List"))+"Information"))
					) {
//				xmlData.append("\t\t<"+rootName+">");
//				xmlData.append("\n");
				Object tmpObx = rootMap.get(key);				
				if (tmpObx instanceof List) {
					List temList = (List)tmpObx;
					for (int i = 0; i < temList.size(); i++) {
						Object obj = temList.get(i);
						if(SysUtility.isEmpty(obj)){
		        			continue;
		        		}
						HashMap childHashMap = (HashMap)temList.get(i);
						StringBuffer clildXmlData = new StringBuffer();
						clildXmlData.append("\t\t\t\t");
						clildXmlData.append("<"+key+">");
						clildXmlData.append("\n");
						clildXmlData.append(hashMapToXml(childHashMap, key+"", 5));
						clildXmlData.append("\t\t\t\t");
						clildXmlData.append("</"+key+">");
						clildXmlData.append("\n");
						xmlData.append(clildXmlData);
					}
				}
//				xmlData.append("\t\t</"+rootName+">\n");
			}else{
				if(SysUtility.isNotEmpty(value)
						&& !"ORG_ID".equalsIgnoreCase(key.toString())
						&& !"DEPT_ID".equalsIgnoreCase(key.toString())
						&& !"CREATOR".equalsIgnoreCase(key.toString())
						&& !"CREATE_TIME".equalsIgnoreCase(key.toString())
						&& !"REC_VER".equalsIgnoreCase(key.toString())){
	            	xmlData.append(appenT(countT)).append("<"+key+">").append("");
	            	xmlData.append(processXmlValue(value.toString()));
	            	xmlData.append("</").append(key).append(">");
	            	xmlData.append("\n");
				}
			}
		}
		return xmlData.toString();
	}
	
	private static List FilterList = new ArrayList();
	static{
		FilterList.add("ORG_ID");
		FilterList.add("DEPT_ID");
		FilterList.add("PART_ID_SOURCE");
		FilterList.add("CREATOR");
		FilterList.add("CREATE_TIME");
		FilterList.add("MODIFYOR");
		FilterList.add("MODIFY_TIME");
		FilterList.add("REC_VER");
	}
	
	public static String hashMapToComXml(Map rootMap,String rootName,int countT){
		return hashMapToComXml(rootMap, rootName, countT, new HashMap());
	}
	
	public static String hashMapToComXml(Map rootMap,String rootName,int countT,HashMap MappingColumns){
		StringBuffer xmlData = new StringBuffer();
		Set mapSet = rootMap.entrySet();
		
		//1.处理当前表的所有字段
		Object obj = MappingColumns.get(rootName);
		if(SysUtility.isNotEmpty(obj)){//有映射的排序列，数据库级
			String[] columns = (String[])obj;
			HashMap tempValue = new HashMap();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
			    Object key = entry.getKey();
			    Object value = entry.getValue();
			    if(SysUtility.isNotEmpty(value)){
			    	if(value instanceof String){
			    		tempValue.put(key, value);
			    	}else if(value instanceof byte[]){
			    		tempValue.put(key, value);
			    	}
				}
			}
			for (int i = 0; i < columns.length; i++) {
				Object value = tempValue.get(columns[i]);
				if(SysUtility.isNotEmpty(value)){
					if(value instanceof String){
		        		xmlData.append(appenT(countT)+"<"+columns[i]+">"+processXmlValue(value.toString())+"</"+columns[i]+">"+"\n");
					}else if(value instanceof byte[]){
						xmlData.append(appenT(countT)+"<"+columns[i]+">"+new ByteArrayInputStream((byte[])value)+"</"+columns[i]+">"+"\n");
					}
				}
			}
		}else{//原逻辑，HashMap无序的拼接
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
		        Object key = entry.getKey();
		        Object value = entry.getValue();
		        if(SysUtility.isNotEmpty(value) && !FilterList.contains(key.toString())){
		        	if(value instanceof String){
		        		xmlData.append(appenT(countT)+"<"+key+">"+processXmlValue(value.toString())+"</"+key+">"+"\n");
					}else if(value instanceof byte[]){
						xmlData.append(appenT(countT)+"<"+key+">"+new ByteArrayInputStream((byte[])value)+"</"+key+">"+"\n");
					}
				}
			}
		}
		
		
		//2.处理子Map下的表，子表重复1的动作。
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
	        Object key = entry.getKey();
	        Object value = entry.getValue();
	        if (value instanceof List) {
				List list = (List)value;
				for (int j = 0; j < list.size(); j++) {
					xmlData.append(appenT(countT-1)+"<"+rootName+">"+"\n");
					HashMap temMap = (HashMap)list.get(j);//只处理一个报文一条主记录的场景。
					Set mapSet2 = temMap.entrySet();
					//2.1 Head
					for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
						Entry entry2 = (Entry)it2.next();
				        Object key2 = entry2.getKey();
				        Object value2 = entry2.getValue();
				        if(value2 instanceof List) {
							List temList = (List)value2;
							for (int i = 0; i < temList.size(); i++) {
								HashMap childHashMap = (HashMap)temList.get(i);
								StringBuffer clildXmlData = new StringBuffer();
								clildXmlData.append(appenT(countT)+"<"+key2+">\n");
								clildXmlData.append(hashMapToComXml(childHashMap, key2.toString(), (countT+1), MappingColumns));
								clildXmlData.append(appenT(countT)+"</"+key2+">\n");
								xmlData.append(clildXmlData);
							}
						}
					}
					//2.2 Information
					for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
						Entry entry2 = (Entry)it2.next();
				        Object key2 = entry2.getKey();
				        Object value2 = entry2.getValue();
				        if(value2 instanceof HashMap) {
				        	HashMap childMap = (HashMap)value2;
				        	Set mapSet3 = childMap.entrySet();
				        	for (Iterator it3 = mapSet3.iterator(); it3.hasNext();) {
				        		Entry entry3 = (Entry)it3.next();
						        Object key3 = entry3.getKey();
						        Object value3 = entry3.getValue();
						        if(value3 instanceof List) {
						        	List temList = (List)value3;
						        	xmlData.append(appenT(countT)+"<"+key2+">\n");
									for (int i = 0; i < temList.size(); i++) {
										HashMap childHashMap = (HashMap)temList.get(i);
										StringBuffer clildXmlData = new StringBuffer();
										clildXmlData.append(appenT(countT+1)+"<"+key3+">"+"\n"+hashMapToComXml(childHashMap, key3.toString(), (countT+2), MappingColumns)+appenT(countT+1)+"</"+key3+">"+"\n");
										xmlData.append(clildXmlData);
									}
									xmlData.append(appenT(countT)+"</"+key2+">\n");
						        }
				        	}
				        }
					}
					xmlData.append("\t\t</"+rootName+">\n");
				}
			}
		}
		return xmlData.toString();
	}
	
	public static String hashMapToAnyXml(Map rootMap,int countT){
		return hashMapToAnyXml(null, rootMap, countT);
	}
	
	public static String hashMapToAnyXml(String rootName,Map rootMap,int countT){
		return hashMapToAnyXml(rootName, rootMap, countT, new HashMap());
	}
	
	public static String hashMapToAnyXml(String rootName,Map rootMap,int countT,HashMap MappingColumns){
		StringBuffer xmlData = new StringBuffer();
		Set mapSet = rootMap.entrySet();
		//1. 处理当前表的所有字段
		Object obj = MappingColumns.get(rootName);
		if(SysUtility.isNotEmpty(obj) && ((String[])obj).length != 0){//有映射的排序列，数据库级
			String[] columns = (String[])obj;
			HashMap tempValue = new HashMap();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
			    Object key = entry.getKey();
			    Object value = entry.getValue();
			    if(SysUtility.isNotEmpty(value)){
			    	if(value instanceof String){
			    		tempValue.put(key, value);
			    	}else if(value instanceof byte[]){
			    		tempValue.put(key, value);
			    	}
				}
			}
			for (int i = 0; i < columns.length; i++) {
				Object value = tempValue.get(columns[i]);
				if(SysUtility.isNotEmpty(value)){
					if(value instanceof String){
						xmlData.append(appenT(countT)).append("<"+columns[i]+">").append(processXmlValue((String)value)).append("</").append(columns[i]).append(">\n");	
					}else if(value instanceof byte[]){
						xmlData.append(appenT(countT)).append("<"+columns[i]+">").append(new ByteArrayInputStream((byte[])value)).append("</").append(columns[i]).append(">\n");
					}
				}
			}
		}else{//原逻辑，HashMap无序的拼接
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
			    Object key = entry.getKey();
			    Object value = entry.getValue();
			    if(SysUtility.isNotEmpty(value)){
					if(value instanceof String){
						xmlData.append(appenT(countT)).append("<"+key+">").append(processXmlValue((String)value)).append("</").append(key).append(">\n");
					}else if(value instanceof byte[]){
						xmlData.append(appenT(countT)).append("<"+key+">").append(new ByteArrayInputStream((byte[])value)).append("</").append(key).append(">\n");
					}
				}
			}
		}
		
		//2. 处理子Map下的表，子表重复1的动作。
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
		    Object key = entry.getKey();
		    Object value = entry.getValue();
			if (value instanceof Map) {
				xmlData.append(appenT(countT)).append("<"+key+">\n");
				HashMap temMap = (HashMap)value;
				Set mapSet2 = temMap.entrySet();
				StringBuffer clildXmlData = new StringBuffer();
				for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
					Entry entry2 = (Entry)it2.next();
			        Object key2 = entry2.getKey();
			        Object value2 = entry2.getValue();
			        if(value2 instanceof String) {
			        	xmlData.append(appenT(countT+1)).append("<"+key2+">").append(processXmlValue((String)value2)).append("</"+key2+">\n");
					}else if(value2 instanceof Map) {
						xmlData.append(hashMapToAnyXml((String)key2,(HashMap)value2, countT+1, MappingColumns));
					}else if(value2 instanceof List) {
						List lst2 = (List)value2;
						for (int i = 0; i < lst2.size(); i++) {
							xmlData.append(appenT(countT+1)).append("<"+key2+">\n");
							xmlData.append(hashMapToAnyXml((String)key2,(HashMap)lst2.get(i), countT+2, MappingColumns));
							xmlData.append(appenT(countT+1)).append("</"+key2+">\n");
						}
					}
				}
				xmlData.append(appenT(countT)).append("</"+key+">\n");
			}else if (value instanceof List) {
				List lst2 = (List)value;
				
				if(SysUtility.isNotEmpty(rootName) && rootName.equals(key)){
					xmlData.append(appenT(countT)).append("<"+key+">\n");
					for (int i = 0; i < lst2.size(); i++) {
						xmlData.append(hashMapToAnyXml((String)key,(HashMap)lst2.get(i), countT+1, MappingColumns));
					}
					xmlData.append(appenT(countT)).append("</"+key+">\n");
				}else{
					for (int i = 0; i < lst2.size(); i++) {
						if(SysUtility.isNotEmptyMapList(lst2)){
							xmlData.append(appenT(countT)).append("<"+key+">\n");
							xmlData.append(hashMapToAnyXml((String)key,(HashMap)lst2.get(i), countT+1, MappingColumns));
							xmlData.append(appenT(countT)).append("</"+key+">\n");
						}
					}
				}
			}
		}
		return xmlData.toString();
	}
	
	public static String appenT(int countT){
		StringBuffer t = new StringBuffer();
		for (int i = 0; i < countT; i++) {
			t.append("\t");
		}
		return t.toString();
	}
	
	/**
	 * 获取文件夹路径
	 * @throws IOException 
	 * **/
	public static String getNewFilePath(String Folder,String suffix) throws IOException{
		String FileServersRoot = SysUtility.GetProperty("system.properties", "FileServersRoot");
		return getNewFilePath(FileServersRoot, Folder ,suffix);
	}
	
	/**
	 * 获取文件夹路径
	 * @throws IOException 
	 * **/
	public static String getNewFilePath(String FileServersRoot,String Folder,String suffix) throws IOException{
		if(SysUtility.isEmpty(FileServersRoot)){
			return "";
		}
		String newFilePath = "";
		try {
			String currTime = SysUtility.getSysDate();
			SimpleDateFormat dFmt = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime(dFmt.parse(currTime));
			} catch (Exception e) {
				LogUtil.printLog("日期转换错误："+e.getMessage(),Level.ERROR);
			}
			String YEAR = String.valueOf(cal.get(Calendar.YEAR));
			String MONTH = String.valueOf(cal.get(Calendar.MONTH) + 1);
			String DATE = String.valueOf(cal.get(Calendar.DATE));
			
			String oppositePath = Folder + File.separator + YEAR + File.separator + MONTH + File.separator + DATE;
			//模块下时间文件夹如果不存在，则创建 
			String absolutePath = FileServersRoot + File.separator + oppositePath;
			File file = new File(absolutePath);
			if (!file.exists()) {
			   if(file.mkdirs()){
				   LogUtil.printLog("文件夹："+absolutePath+" 创建成功！",LogUtil.INFO);
			   }else{
				   LogUtil.printLog("文件夹："+absolutePath+" 创建失败！",LogUtil.ERROR);
			   }
			}
			newFilePath = absolutePath + File.separator + SysUtility.getMilliSeconds() + suffix;
		} catch (Exception e) {
			LogUtil.printLog("文件构造出错:"+e.getMessage(),Level.ERROR);
		}
		return newFilePath;
	}
	
	public static BASE64Encoder getBase64encoder() throws IOException{
		return base64encoder;
	}
	
	public static BASE64Decoder getBase64decoder() throws IOException{
		return base64decoder;
	}
	
	public static String createFolder(String Path, String folderName){
		if(SysUtility.isEmpty(Path)){
			return "";
		}
		Path = Path + File.separator + folderName;
		File file = new File(Path);
		if (!file.exists()) {
		   if(file.mkdirs()){
			   LogUtil.printLog("文件夹创建成功："+Path, Level.INFO);
		   }
		}
		return Path;
	}
	
	public static boolean isExsFileName(String fileName){
		if(SysUtility.isNotEmpty(fileName) && fileName.length() > 3 &&fileName.endsWith(".exs")){
			return true;
		}
		return false;
	}
	
	public static String renameFileNameXmlToExs(String FileName){
		if(isFileNameXml(FileName)) {
			return FileName.substring(0, FileName.length() - 4)+".exs";
		}
		return FileName;
	}
	
	public static boolean renameFileExsToXml(String Path,String FileName){
		return renameFileExsToXml(Path + File.separator + FileName);
	}
	
	public static boolean renameFileExsToXml(String FilePath){
		boolean rt = false;
		if(isFileNameExs(FilePath)){
			File file = new File(FilePath);  
			if(file.exists()){
				FilePath = FilePath.substring(0, FilePath.length() - 4)+".xml";
				if(new File(FilePath).exists()){
					deleteFile(new File(FilePath));
				}
				rt = file.renameTo(new File(FilePath));
			}
		}
		return rt;
	}
	
	/**
	 * @return
	 * "" 文件不是以.temp、.exs结尾
	 * A 文件以.temp、.exs结尾，时间未超过2小时
	 * B 文件以.temp、.exs结尾，时间超过2小时
	 * C 文件以不是.temp、.exs结尾，且文件第一行或第二行没有RequestMessage或ResponseMessage的关键字
	 * */
	public static String FilterFileName(File file){
		String fileName = file.getName();
		if(SysUtility.isNotEmpty(fileName) && fileName.length() > 3){
			if(fileName.endsWith(".temp")||fileName.endsWith(".exs")){
				long currTime = (new Date()).getTime();
				long lastModifiedTime = file.lastModified();
				if(currTime - lastModifiedTime > 1000*7200){
					return "B";
				}
				return "A";
			}else if(!isExsFile(file)){
				return "C";
			}
		}
		return "";
	}
	
	//判断是否是框架能解析的xml格式
	public static boolean isExsFile(File file){
		String XmlRootName = GetExsFileRootName(file);
		if("RequestMessage".equals(XmlRootName) || "ResponseMessage".equals(XmlRootName)){
			return true;
		}
		return false;
	}
	
	/**
	 * @return true：交换框架文件， false不是交换框架文件。
	 * A 文件以.temp、.exs结尾，时间未超过2小时
	 * B 文件以.temp、.exs结尾，时间超过2小时
	 * */
	public static boolean ExsFileProcess(File file,String sourcePath,String errorPath){
		String FilterRT = FilterFileName(file);
		if("A".equals(FilterRT)){
			return true;
		}else if("B".equals(FilterRT)){
			FileUtility.copyFile(sourcePath + File.separator + file.getName(), errorPath + File.separator + file.getName());
			FileUtility.deleteFile(sourcePath, file.getName());
			return true;
		}else{
			return false;
		}
	}
	
	public static HashMap GetExsFileData(String FilePath,String FileName,String Encoding){
		HashMap RequestMessage = new HashMap();
		try {
			String tempXmlData = FileUtility.readFile(FilePath + File.separator+ FileName, false);
			InputStream is = new ByteArrayInputStream(tempXmlData.getBytes(Encoding));
			String XmlRootName = GetExsFileRootName(new File(FilePath + File.separator+ FileName));
			RequestMessage = FileUtility.xmlParse(is,XmlRootName,Encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("GetExsFileData Error:"+e.getMessage(), Level.ERROR);
		} catch (LegendException e) {
			LogUtil.printLog("GetExsFileData Error:"+e.getMessage(), Level.ERROR);
		}
		return RequestMessage;
	}
	
	/**
	 * 获取Xml文件的RootName
	 * **/
	public static String GetExsFileRootName(File file){
		if(SysUtility.isEmpty(file)){
			return "";
		}
		
		String XmlRootName = "";
		try {
			XmlRootName = GetExsFileRootName(new FileInputStream(file), 0);
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileRootName(new FileInputStream(file), 1);
			}
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileRootName(new FileInputStream(file), 2);
			}
		} catch (FileNotFoundException e) {
			LogUtil.printLog("GetExsFileRootName Error:"+e.getMessage(), Level.ERROR);
		}
		return XmlRootName;
	}
	
	/**
	 * 获取Xml文件的RootName
	 * **/
	public static String GetExsFileRootName(InputStream is,int line){
		String lineStr = SysUtility.GetFileLine(is, line);
		if(SysUtility.isNotEmpty(lineStr) && lineStr.indexOf("<?") >= 0 && lineStr.indexOf("?>") >= 0){
			lineStr = lineStr.substring(lineStr.substring(lineStr.indexOf("<?"), lineStr.indexOf("?>")+2).length());
		}
		
		if(SysUtility.isNotEmpty(lineStr) && lineStr.indexOf("<") >= 0 && lineStr.indexOf(">") >= 0){
			return lineStr.substring(lineStr.indexOf("<")+1, lineStr.indexOf(">"));
		}
		
		if(SysUtility.isNotEmpty(lineStr) && lineStr.startsWith("UNH+")){
			String[] strs = lineStr.split("\\+");
			return strs.length >1? strs[1]:"";
		}
		return "";
	}

	/**
	 * 获取Xml文件的RootName
	 * **/
	public static String GetExsFileRootName(String data){
		return GetExsFileRootName(data, "UTF-8");
	}
	
	/**
	 * 获取Xml文件的RootName
	 * **/
	public static String GetExsFileRootName(String data,String endcoding){
		if(SysUtility.isEmpty(data)){
			return "";
		}
		
		String XmlRootName = "";
		try {
			XmlRootName = GetExsFileRootName(new ByteArrayInputStream(data.getBytes(endcoding)), 0);
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileRootName(new ByteArrayInputStream(data.getBytes(endcoding)), 1);
			}
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileRootName(new ByteArrayInputStream(data.getBytes(endcoding)), 2);
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("GetExsFileRootName Error:"+e.getMessage(), Level.ERROR);
		}
		return XmlRootName;
	}
	
	public static List<String> GetFileNameList(int GetCount,String sourcePath){
		List<String> list = new ArrayList<String>();
		if(SysUtility.isEmpty(GetCount) || SysUtility.isEmpty(sourcePath)){
			return list;
		}
		String[] names = new File(sourcePath).list(new FileNameFilter());
		if(SysUtility.isNotEmpty(names)){
			for (int i = 0; i < names.length; i++) {
				list.add(names[i]);
			}
		}
		return list;
	}
	
	public static List<Object[]> GetSourceFileList(){
		List<Object[]> fileList = new ArrayList<Object[]>();
		String sourcePath = AppContext.getAbsolutePath()+File.separator+"WEB-INF"+File.separator+"classes";
		fileList = FileUtility.GetSourceFileList(1000, sourcePath);
		return fileList;
	}
	
	public static List<Object[]> GetSourceFileList(int GetCount,String sourcePath){
		List<Object[]> list = new ArrayList<Object[]>();
		if(SysUtility.isEmpty(GetCount) || SysUtility.isEmpty(sourcePath)){
			return list;
		}
		
		File files[] = new File(sourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()){
				list.add(new Object[]{sourcePath,files[i]});
				if(list.size() > GetCount){
					break;
				}
			}else{
				String folderName = files[i].getName();
				if("Temps".equals(folderName) ||"Backup".equals(folderName) || "Error".equals(folderName)){
					continue;
				}
				String tempSourcePath = FileUtility.createFolder(sourcePath, folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					if (!files2[k].isDirectory()){
						list.add(new Object[]{tempSourcePath,files2[k]});
						if(list.size() > GetCount){
							break;
						}
					}
				}
				if(list.size() > GetCount){
					break;
				}
			}
		}
		return list;
	}
	
	public static List<Object[]> GetSourceFileList(int GetCount,String sourcePath,String errorPath){
		List<Object[]> list = new ArrayList<Object[]>();
		if(SysUtility.isEmpty(GetCount) || SysUtility.isEmpty(sourcePath)){
			return list;
		}
		
		File files[] = new File(sourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()){
				boolean rt = FileUtility.ExsFileProcess(files[i], sourcePath, errorPath);
				if(rt){
					continue;
				}
				list.add(new Object[]{sourcePath,files[i]});
				if(list.size() > GetCount){
					break;
				}
			}else{
				String folderName = files[i].getName();
				if("Temps".equals(folderName) ||"Backup".equals(folderName) || "Error".equals(folderName)){
					continue;
				}
				String tempSourcePath = FileUtility.createFolder(sourcePath, folderName);
				File files2[] = new File(tempSourcePath).listFiles(new FileFilterHandle());
				for (int k = 0; k < files2.length; k++) {
					if (!files2[k].isDirectory()){
						boolean rt = FileUtility.ExsFileProcess(files2[k], sourcePath, errorPath);
						if(rt){
							continue;
						}
						
						list.add(new Object[]{tempSourcePath,files2[k]});
						if(list.size() > GetCount){
							break;
						}
					}
				}
				if(list.size() > GetCount){
					break;
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 获取字段值
	 * param: CheckMap:校验的数据
	 * param: strKey：取值的key   
	 * return String
	 * */
	public static void getNameValue(HashMap CheckMap, String strKey,String tableName,Object result)throws LegendException {
		if(strKey == null) return;
		
		Set mapSet = CheckMap.entrySet();
        for (Iterator it = mapSet.iterator(); it.hasNext();) {
            Entry entry =  (Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof List){
            	List list = (List) value;
    			for(int i = 0 ; i < list.size() ; i++){
    				HashMap temp = (HashMap) list.get(i);
    				if(key.equals(tableName)){
    					getCoulmnValue(temp, strKey, tableName, result);
    				}else{
    					Set set2 = temp.entrySet();
    					for (Iterator it2 = set2.iterator(); it2.hasNext();) {
    			            Entry entry2 =  (Entry)it2.next();
    			            Object key2 = entry2.getKey();
    			            Object value2 = entry2.getValue();
    			            if(key2.equals(tableName)){
    			            	if(value2 instanceof List){
    			            		List list2 = (List) value2;
    			            		for(int j = 0 ; j < list2.size() ; j++){
    			            			HashMap temp2 = (HashMap) list2.get(i);
    			            			/*if(key2.toString().endsWith("Head") || key2.toString().endsWith("Information")){
    			            				getCoulmnValue(temp2, strKey, tableName, result);
    			            			}else{
    			            				getNameValue(temp2, strKey,tableName,result);
    			            			}*/
    			            			getNameValue(temp2, strKey,tableName,result);
    			            		}
    			            	}
    			            }
    					}
    				}
    			}
            }
        }
	}
	
	
	protected static void getCoulmnValue(HashMap CheckMap, String strKey,String tableName,Object result)
			throws LegendException {
		if(strKey == null) return;
		
		String value = "";
		Set mapSet = CheckMap.entrySet();
        for (Iterator it = mapSet.iterator(); it.hasNext();) {
            Entry entry =  (Entry)it.next();
            Object key = entry.getKey();
            if(strKey.equals(key)){
            	value = entry.getValue().toString();
            }
        }
        
        if(result instanceof List) {
    		if("".equals(value)){
    			value = null;
    		}
    		((List)result).add(value);
    	}
	}
	
	public static String getRootName(HashMap hmSourceData){
		StringBuffer RootName = new StringBuffer();
		Set mapSet = hmSourceData.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
		    Object key = entry.getKey();
		    Object value = entry.getValue();
			if (value instanceof Map) {
				HashMap temMap = (HashMap)value;
				Set mapSet2 = temMap.entrySet();
				for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
					Entry entry2 = (Entry)it2.next();
			        Object key2 = entry2.getKey();
			        Object value2 = entry2.getValue();
			        if(value2 instanceof String) {
			        	
					}else if(value2 instanceof Map) {
						
					}else if(value2 instanceof List) {
						
					}
				}
			}else if (value instanceof List) {
				if(!"RequestMessage".equals(key) && !"MessageHead".equals(key) && !"MessageBody".equals(key)){
					RootName.append(key+",");
				}
				List lst2 = (List)value;
				for (int i = 0; i < lst2.size(); i++) {
					if(SysUtility.isNotEmptyMapList(lst2)){
						RootName.append(getRootName((HashMap)lst2.get(i)));
					}
				}
			}
		}
		return RootName.toString();
	}
	
	public static HashMap getParentMap(String RootName,HashMap hmSourceData){
		HashMap ParentMap = new HashMap();
		Set mapSet = hmSourceData.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
		    Object key = entry.getKey();
		    Object value = entry.getValue();
			if (value instanceof Map) {
				HashMap temMap = (HashMap)value;
				Set mapSet2 = temMap.entrySet();
				for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
					Entry entry2 = (Entry)it2.next();
			        Object key2 = entry2.getKey();
			        Object value2 = entry2.getValue();
			        if(value2 instanceof String) {
			        	
					}else if(value2 instanceof Map) {
						
					}else if(value2 instanceof List) {
						
					}
				}
			}else if (value instanceof List) {
				if(!"RequestMessage".equals(key) && !"MessageHead".equals(key) && !"MessageBody".equals(key)){
					if("RequestMessage".equals(RootName) || "MessageHead".equals(RootName) || "MessageBody".equals(RootName)){
						ParentMap.put(key, "");
					}else{
						ParentMap.put(key, RootName);
					}
				}
				List lst2 = (List)value;
				for (int i = 0; i < lst2.size(); i++) {
					if(SysUtility.isNotEmptyMapList(lst2)){
						ParentMap.putAll(getParentMap((String)key, (HashMap)lst2.get(i)));
					}
				}
			}
		}
		return ParentMap;
	}
	
	private static final byte[] BUFFER = new byte[4096 * 1024];
	public static void CreateZipFile(String FilePath) {
		File zipfile = new File(FilePath+".zip");
		if (zipfile.exists()) {
			return;
		}
		try {
			OutputStream out = new FileOutputStream(zipfile);
			ZipOutputStream zipOut = new ZipOutputStream(out);
			File srcfile[] = new File(FilePath).listFiles(new FileFilterHandle());
			for (int i = 0; i < srcfile.length; i++) {
				FileInputStream in = new FileInputStream(srcfile[i]);
				zipOut.putNextEntry(new ZipEntry(srcfile[i].getName()));
				int len;
				while ((len = in.read(BUFFER)) > 0) {
					zipOut.write(BUFFER, 0, len);
				}
				zipOut.closeEntry();
				in.close();
				FileUtility.deleteFile(srcfile[i]);
			}
			zipOut.close();
			out.close();
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public static void copyZipFile(String SourceZip,String TargetZip) {
		try {
			ZipFile war = new ZipFile(SourceZip);
			ZipOutputStream append = new ZipOutputStream(new FileOutputStream(TargetZip));

			Enumeration<? extends ZipEntry> entries = war.entries();
			while (entries.hasMoreElements()) {
			    ZipEntry e = entries.nextElement();
			    System.out.println("copy: " + e.getName());
			    append.putNextEntry(e);
			    if (!e.isDirectory()) {
			        InputStream input = war.getInputStream(e);
			        OutputStream output = append;
			        
			        int bytesRead;
			        while ((bytesRead = input.read(BUFFER))!= -1) {
			            output.write(BUFFER, 0, bytesRead);
			        }
			    }
			    append.closeEntry();
			}
			war.close();
			append.close();
		} catch (FileNotFoundException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	/*public static String GetExsFileEndcoding(String data,String endcoding){
		String XmlRootName = "";
		try {
			XmlRootName = GetExsFileEndcoding(new ByteArrayInputStream(data.getBytes(endcoding)), 0);
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileEndcoding(new ByteArrayInputStream(data.getBytes(endcoding)), 1);
			}
			if(SysUtility.isEmpty(XmlRootName)){
				XmlRootName = GetExsFileEndcoding(new ByteArrayInputStream(data.getBytes(endcoding)), 2);
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("GetExsFileEndcoding Error:"+e.getMessage(), Level.ERROR);
		}
		return XmlRootName;
	}
	public static String GetExsFileEndcoding(InputStream is,int line){
		String lineStr = SysUtility.GetFileLine(is, line);
		if(SysUtility.isNotEmpty(lineStr) && lineStr.indexOf("<?") >= 0 && lineStr.indexOf("?>") >= 0){
			lineStr = lineStr.substring(lineStr.substring(lineStr.indexOf("<?"), lineStr.indexOf("?>")+2).length());
		}
		if(SysUtility.isNotEmpty(lineStr) && lineStr.indexOf("<") >= 0 && lineStr.indexOf(">") >= 0){
			return lineStr.substring(lineStr.indexOf("<")+1, lineStr.indexOf(">"));
		}
		return "";
	}*/
	
	public static boolean isFileNameXml(String FileName){
		if(FileName.length() > 4 && "xml".equalsIgnoreCase(FileName.substring(FileName.length() - 3, FileName.length()))){
			return true;
		}
		return false;
	}
	
	public static boolean isFileNameExs(String FileName){
		if(FileName.length() > 4 && "exs".equalsIgnoreCase(FileName.substring(FileName.length() - 3, FileName.length()))){
			return true;
		}
		return false;
	}
}