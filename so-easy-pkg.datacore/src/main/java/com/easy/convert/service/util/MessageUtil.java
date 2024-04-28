package com.easy.convert.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;

import com.easy.app.constants.ExsConstants;
import com.easy.convert.mq.ToActiveMQ;
import com.easy.file.FileFilterHandle;
import com.easy.utility.SysUtility;


public  class MessageUtil {
	
	private static String PathFileName = "";
	private static String msg = "";
	private static String SourceFileName = "";
	private static HashMap<String, String> sheetmap = null;
	private static boolean success = false;
	
	
	public static void  ReadExcelSheet(String excelPath) throws Exception {
		List<String[]> readExcelSheet = readExcelSheet("ConfigPath", excelPath);
		for (String[] ExcelSheet : readExcelSheet) {
			//判断excel文件然后进行赋值 格式不正确自动调过
			if(ExcelSheet.length<4){
				 continue;
			}
			//循环sheet表
			HashMap<String, String> sheetMap = new HashMap<String, String>();
			//唯一序列
			sheetMap.put("Seq", ExcelSheet[0]);
			 //源路径 
			sheetMap.put("SourceFilePath", ExcelSheet[1]);
			//配置名称
			sheetMap.put("ConfigName", ExcelSheet[2]);
			//目标路径
			sheetMap.put("TargetFilePath", ExcelSheet[3]);
			//备份目录
			sheetMap.put("BackPath", ExcelSheet[4]);
			//错误目录
			sheetMap.put("ErrorPath", ExcelSheet[5]);
			//需要发送的文件存放路径
			sheetMap.put("SendPath", ExcelSheet[6]);
			//发送类型 1.FTP;2.Webservice;3:MS MQ;4.Active MQ;5.IBM MQ;6.其他
			sheetMap.put("SendTyPe", ExcelSheet[7]);
			//发送地址，以转换平台为对象
			sheetMap.put("SendAdress", ExcelSheet[8]);
			//发送地址所需要的用户名
			sheetMap.put("SendUserName", ExcelSheet[9]);
			//发送地址所需要的密码
			sheetMap.put("SendUserPwd", ExcelSheet[10]);
			//需要接收文件的地址
			sheetMap.put("ReceivePath", ExcelSheet[11]);
			//接受类型1.FTP;2.Webservice;3:MS MQ;4.Active MQ;5.IBM MQ;6.其他
			sheetMap.put("ReceiveTyPe", ExcelSheet[12]);
			//接收地址
			sheetMap.put("ReceiveAdress", ExcelSheet[13]);
			//接收地址所用的用户名
			sheetMap.put("ReceiveUserName", ExcelSheet[14]);
			//接收地址所用的密码
			sheetMap.put("ReceiveUserPwd", ExcelSheet[15]);
			//平台使用者的邮箱（转换结果、发送结果信息回执）
			sheetMap.put("UserEmail", ExcelSheet[16]);
			//备注
			sheetMap.put("Remark", ExcelSheet[17]);
			sheetmap = sheetMap;
			//进行下一步 根据excel内容进行动态修改 然后使用jdom去写XML文件
			File files[] = new File(sheetMap.get("SourceFilePath")).listFiles(new FileFilterHandle());
			for (int i = 0; i < files.length; i++) {
				long startTime=System.currentTimeMillis();   //获取开始时间
				initRoot();
				sheetMap.put("SourceFilePath", files[i].getPath());
				ReadxcelXML(sheetMap,excelPath);
				if(SysUtility.isEmpty(PathFileName)){
					continue;
				}
				//上传传输
				if("1".equals(sheetMap.get("SendType"))){//FTP
					InputStream input = new FileInputStream(getPathFileName());
//					if(FtpUtil.uploadFile(sheetMap.get("SendAdress"), 21, sheetMap.get("SendUserName"), sheetMap.get("SendUserPwd"), "\\", "\\", getSourceFileName(), input)){
//						EmailSend.send(getSourceFileName()+" 转换成功温馨提示", "<html>信息提示:你的报文     <font  color=\"red\">"+getSourceFileName()+"</font>   已转换完成,已成功发送到FTP服务器</html>", sheetMap.get("UserEmail"));
//					}
				}else if("2".equals(sheetMap.get("SendType"))){//WEBSERVICE
					
				}else if("3".equals(sheetMap.get("SendType"))){//MS MQ
					
				}else if("4".equals(sheetMap.get("SendType"))){//ACTIVEMQ
					ToActiveMQ.LocalXmlToActiveMQ(null,new File(PathFileName),"","","");
					EmailSend.send(getSourceFileName()+" 转换成功温馨提示", "<html>信息提示:你的报文     <font  color=\"red\">"+getSourceFileName()+"</font>   已转换完成,已成功发送到ActiveMq队列</html>", sheetMap.get("UserEmail"));
				}else if("5".equals(sheetMap.get("SendType"))){//IBM MQ
					LocalXmlIbmMq(new File(PathFileName));
				}
				long endTime=System.currentTimeMillis(); //获取结束时间
				System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
				msg += "已成功转换! ";
				success = true;
			}
		}
	}
	
	public static void ReadxcelXML(HashMap<String, String> sheetMap,String excelPath) throws FileNotFoundException, IOException{
		//读取
		List<String[]> ExcelSheets = readExcelSheet(sheetMap.get("ConfigName"), excelPath);
		if(ExcelSheets.size()<1){
			return;
		}
		//读取每个sheet
		boolean flat =true;
		//读取XML
		String xmlToString = XmlToString(sheetMap.get("SourceFilePath"));//参数为XML地址  如果地址错误 错误信息会以!开头提示信息
		if(xmlToString.equals("!文件路径不正确或XML格式有误")){
			msg += xmlToString;
			flat=false;
			return;
		}
		//存放出现次数
		Map<String, Integer> AppearMap = new HashMap<String, Integer>();
		for (int i=0;i<ExcelSheets.size();i++) {
			 HashMap<String, String> XMLMap = readXMLMap(i, ExcelSheets);
			 Integer cout = AppearMap.get(XMLMap.get("SourceNoteName"));
			 if(isEmpty(cout)){
				 AppearMap.put(XMLMap.get("SourceNoteName"), 1);
			 }else{
				 AppearMap.put(XMLMap.get("SourceNoteName"), cout++);
			 }
			 if(isEmpty(XMLMap.get("Seq")) || "".equals(XMLMap.get("Seq")) || !((i+1)+"").equals(XMLMap.get("Seq"))){
				 msg += "Seq序号顺序不统一";
				 flat=false;
				 return;
			 }
			 //重表逻辑
			 if("Y".equals(XMLMap.get("IsSubList"))){
				 String Seq = XMLMap.get("Seq");
				 String SourceNoteName = XMLMap.get("SourceNoteName");
				 String TargetFileFloor = XMLMap.get("TargetFileFloor");
				 int DefNumber = FindDef(xmlToString, SourceNoteName);
				 for(int j=0;j<DefNumber;j++){
					//截取重表XML内容
					 String DefXML = subStringNum(xmlToString,SourceNoteName,j+1);
					 String SourceName = "";
					 while(SourceName!="!"){
						//创建XML
						 AddXmlElement(XMLMap, ExcelSheets, i, AppearMap, DefXML);
						 i++;
						 for(;i<ExcelSheets.size();i++){
							 XMLMap = readXMLMap(i, ExcelSheets);
							 cout = AppearMap.get(XMLMap.get("SourceNoteName"));
							 if(isEmpty(cout)){
								 AppearMap.put(XMLMap.get("SourceNoteName"), 1);
							 }else{
								 AppearMap.put(XMLMap.get("SourceNoteName"), cout++);
							 }
							 //重重表逻辑
							 if("Y1".equals(XMLMap.get("IsSubList"))){
								 String YSeq = XMLMap.get("Seq");
								 String YSourceNoteName = XMLMap.get("SourceNoteName");
								 String YTargetFileFloor = XMLMap.get("TargetFileFloor");
								 int YDefNumber = FindDef(DefXML, YSourceNoteName);
								 for(int k=0;k<YDefNumber;k++){
									//截取重表XML内容
									 String YDefXML = subStringNum(DefXML,YSourceNoteName,k+1);
									 String YSourceName = "";
									 while(YSourceName!="!"){
										//创建XML
										 AddXmlElement(XMLMap, ExcelSheets, Integer.parseInt(YSeq)-1, AppearMap, YDefXML);
										 i++;
										 for(;i<ExcelSheets.size();i++){
											 XMLMap = readXMLMap(Integer.parseInt(YSeq), ExcelSheets);
											 XMLMap = readXMLMap(i, ExcelSheets);
											 if(Integer.parseInt(XMLMap.get("TargetFileFloor"))==Integer.parseInt(YTargetFileFloor)||Integer.parseInt(XMLMap.get("TargetFileFloor"))<Integer.parseInt(YTargetFileFloor)){
												 SourceName="!";
												 if(k+1==YDefNumber){
													 AddXmlElement(XMLMap, ExcelSheets, i+1, AppearMap, DefXML); 
												 }
												 break;
											 }
											 if(isEmpty(XMLMap.get("Seq")) || "".equals(XMLMap.get("Seq")) || !((i+1)+"").equals(XMLMap.get("Seq"))){
												 msg += "Seq序号顺序不统一";
												 return;
											 }
											 cout = AppearMap.get(XMLMap.get("SourceNoteName"));
											 if(isEmpty(cout)){
												 AppearMap.put(XMLMap.get("SourceNoteName"), 1);
											 }else{
												 AppearMap.put(XMLMap.get("SourceNoteName"), cout++);
											 }
											//创建XML
											 AddXmlElement(XMLMap, ExcelSheets, i+1, AppearMap, YDefXML);
										 }
										 YSourceName="!";
										 if(YDefNumber-1>k){
											 i=Integer.parseInt(YSeq)-1;
											 XMLMap = readXMLMap(i, ExcelSheets); 
										 }
									 }
								 }
							 }
							 if(Integer.parseInt(XMLMap.get("TargetFileFloor"))==Integer.parseInt(TargetFileFloor)||Integer.parseInt(XMLMap.get("TargetFileFloor"))<Integer.parseInt(TargetFileFloor)){
								 SourceName="!";
								 if(j+1==DefNumber){
									 AddXmlElement(XMLMap, ExcelSheets, i+1, AppearMap, DefXML); 
								 }
								 break;
							 }
							 if(isEmpty(XMLMap.get("Seq")) || "".equals(XMLMap.get("Seq")) || !((i+1)+"").equals(XMLMap.get("Seq"))){
								 msg += "Seq序号顺序不统一";
								 return;
							 }
							//创建XML
							 AddXmlElement(XMLMap, ExcelSheets, i+1, AppearMap, DefXML);
						 }
						 SourceName="!";
						 if(DefNumber-1>j){
							 i=Integer.parseInt(Seq)-1;
							 XMLMap = readXMLMap(i, ExcelSheets); 
						 }
						 
					 }
				 }
				 
			 }else{
				//创建XML
				 AddXmlElement(XMLMap, ExcelSheets, i, AppearMap, xmlToString);
			 }
			
			 
			
			 
		}
		
		 Document Doc  = new Document(root);
		 XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
		 newfile(sheetMap, XMLOut, Doc);
		 
	}
	
	
	public static void AddXmlElement(HashMap<String, String> XMLMap,List<String[]> ExcelSheets,int i,Map<String, Integer> AppearMap,String xmlToString){
		 //创建XML
		 if("0".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&& isEmpty(root)){
			 root = new Element(XMLMap.get("TargetColName")); 
		 }else if("1".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("SourceFileFloor"), NextTargetFileFloor, value);
			 root.addContent(element1 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("2".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element1)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element1.addContent(element2 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("3".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element2)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element2.addContent(element3 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("4".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element3)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element3.addContent(element4 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("5".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element4)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element4.addContent(element5 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("6".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element5)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element5.addContent(element6 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("7".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element6)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element6.addContent(element7 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("8".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element7)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element7.addContent(element8 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("9".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element8)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element8.addContent(element9 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("10".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element9)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element9.addContent(element0 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("11".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element0)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element0.addContent(element11 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("12".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element11)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element11.addContent(element12 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("13".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element12)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element12.addContent(element13 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("14".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element13)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element13.addContent(element14 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("15".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element14)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element14.addContent(element15 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("16".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element15)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element15.addContent(element16 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("17".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element16)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element16.addContent(element17 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("18".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element17)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element17.addContent(element18 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("19".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element18)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element18.addContent(element19 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("20".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element19)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element19.addContent(element20 = new Element(XMLMap.get("TargetColName")).setText(value));
		 }else if("21".equals(XMLMap.get("TargetFileFloor"))&&!isEmpty(XMLMap.get("TargetColName"))&&!isEmpty(element20)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(XMLMap.get("SourceNoteName")), XMLMap.get("SourceNoteName"),ExcelSheets.get(i));
			 String NextTargetFileFloor =null;
			 if((ExcelSheets.size()>i+1)){
				 NextTargetFileFloor = ExcelSheets.get(i+1)[3];
			 }
			 value = init(XMLMap.get("TargetColName"), NextTargetFileFloor, value);
			 element20.addContent( new Element(XMLMap.get("TargetColName")).setText(value));
		 }
	}
	
	
	
	public static List<String[]> readExcelSheet(String sheetName,String excelPath){
		FileInputStream readFile = null;
		try {
			readFile= new FileInputStream(excelPath);
		} catch (Exception e) {
			e.printStackTrace();
			msg += "文件不存在";
			return null;//文件不存在
		}
		List<String[]> list = new ArrayList<String[]>();
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(readFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		HSSFSheet sheet = wb.getSheet(sheetName);
		if(sheet!=null){
             //获得当前sheet的开始行  
             int firstRowNum  = sheet.getFirstRowNum();  
             //获得当前sheet的结束行  
             int lastRowNum = sheet.getLastRowNum(); 
             //循环除了第一行的所有行  
             for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){ 
            	 Row row = sheet.getRow(rowNum);  
                 if(row == null){  //防止有空时针出现 简单做一下处理 
                     continue;  
                 }  
                 //获得当前行的开始列  
                 int firstCellNum = row.getFirstCellNum();  
                 if(firstCellNum == -1){
            		 continue;
            	 }
                 //获得当前行的列数  
                 int lastCellNum = row.getPhysicalNumberOfCells();  
                 String[] cells = new String[sheet.getRow(0).getPhysicalNumberOfCells()];  
                 //循环当前行  
                 for(int cellNum = firstCellNum; cellNum < sheet.getRow(0).getPhysicalNumberOfCells();cellNum++){  
                	
                	 //当前列
                     Cell cell = row.getCell(cellNum);  
                     if(cells.length<=1){
                     	cells[0]="";
                     }
                     cells[cellNum] = getCellValue(cell);  
                 }  
                 list.add(cells);
             }
		}
		return list;
	}
	
	public static String getCellValue(Cell cell){  
        String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        } 
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;  
    }
	
	  public static String XmlToString(String xmlpath){ 
	      Document document=null;   
	      document=load(xmlpath);   
	      if(document==null){
	    	  return "!文件路径不正确或XML格式有误";
	      }
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
	      }    
	      sReturn=out.toString();
	      return sReturn;
	  }
	  
	  public static Document load(String xmlpath){  
	         Document document=null;   
	     String url=xmlpath;   
	    try {   
	    	 File file = new File(url);
	    	 SourceFileName  = file.getName();
	         SAXBuilder reader = new SAXBuilder();    
	         document=reader.build(file);   
	    } catch (Exception e){
	    	msg+= e.getMessage();
	    	return null;
	    }
	     return document;  
	  }  
	  
	  public static Format FormatXML(){  
	        //格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...  
	        Format format = Format.getCompactFormat();  
	        format.setEncoding("utf-8");  
	        format.setIndent(" ");  
	        return format;  
	  }  
	  
	  public static boolean regEx(String str,String reg){
	    	// 编译正则表达式
	        Pattern pattern = Pattern.compile(reg);
	        Matcher matcher = pattern.matcher(str);
	        // 字符串是否与正则表达式相匹配
	        boolean rs = matcher.matches();
	    	return rs;
	    }
	  
	  public static boolean isEmpty(Object obj){
			if (obj == null)  
	        {  
	            return true;  
	        }  
	        if ((obj instanceof List))  
	        {  
	            return ((List) obj).size() == 0;  
	        }  
	        if ((obj instanceof String))  
	        {  
	            return ((String) obj).trim().equals("");  
	        }  
	        return false;  
		}
	  
	  public static String FindTheLocation(String str,int number,String theStr,String[] thisbody){
	    	if(!isEmpty(thisbody[5])&&!"".equals(thisbody[5])){
	    		return thisbody[5];
	    	}
	    	int indexOf = 0;
	    	theStr = "<"+theStr+">";
	    	for (int i = 0; i < number; i++) {
	    		indexOf= str.indexOf(theStr,indexOf+1);
			}
	    	int indexOf2 = str.indexOf(">",indexOf+1);
			int indexOf3 = str.indexOf("<",indexOf2);
			String returnStr = str.substring(indexOf2+1,indexOf3);
	    	return returnStr;
	  }
	  
	  public static String init(String TargetFileFloor,String NextTargetFileFloor,String value){
		  try {
			  if(Integer.parseInt(NextTargetFileFloor)>Integer.parseInt(TargetFileFloor)){
				  return null;
			  }else{
				  return value;
			  }
		  } catch (Exception e) {
			  return value;
		}
	  }
	  
	  /**
		 * @param xml
		 * @param theStr
		 * @return 用来查询从表的次数
		 */
		 public static int FindDef(String xml,String theStr){
				String findstr = "<"+theStr+">";
				int indexOf = 0;
				int number = 0;
				while (indexOf!=-1) {
					indexOf = xml.indexOf(findstr,indexOf+1);
					if(indexOf!=-1){
						number++;
					}
				}
				return number;
		 }
		 /**
		  * 
		  * @return 得到重表内容规定次数的内容
		  */
		 public static String subStringNum(String xml,String SourceNoteName,int number){
			 int indexOf = 0;
			 int indexOutOf = 0;
			 String xmlBody="";
			 for(int i=0;i<number;i++){
				 indexOf = xml.indexOf("<"+SourceNoteName+">",indexOf+1);
				 indexOutOf = xml.indexOf("</"+SourceNoteName+">",indexOf);
				 xmlBody = xml.substring(indexOf,indexOutOf+(3+SourceNoteName.length()));
			 }
			 return xmlBody;
		 }
		 
		 public static HashMap<String, String> readXMLMap(int i,List<String[]> ExcelSheets){
			 HashMap<String, String> XMLMap = new HashMap<String, String>();
			 XMLMap.put("Seq", ExcelSheets.get(i)[0]);
			 XMLMap.put("SourceFileFloor", ExcelSheets.get(i)[1]);
			 XMLMap.put("SourceNoteName", ExcelSheets.get(i)[2]);
			 XMLMap.put("TargetFileFloor", ExcelSheets.get(i)[3]);
			 XMLMap.put("TargetColName", ExcelSheets.get(i)[4]);
			 XMLMap.put("DefValue", ExcelSheets.get(i)[5]);
			 XMLMap.put("IsSubList", ExcelSheets.get(i)[6]);
			 XMLMap.put("TargetNoteAttribute", ExcelSheets.get(i)[7]);
			 XMLMap.put("TargetNoteIsInput", ExcelSheets.get(i)[8]);
			 XMLMap.put("Remark", ExcelSheets.get(i)[9]);
			 return XMLMap;
		 }
		 
		
		 public static void newfile(HashMap<String, String> sheetMap,XMLOutputter XMLOut,Document doc) throws FileNotFoundException, IOException{
			//如果文件夹不存在则创建    
			String reg = "^[a-zA-Z]:(\\\\[^\\\\\\:\"<>/|]+)+$";
			if(regEx(sheetMap.get("TargetFilePath"), reg)){
				File file =new File( sheetMap.get("TargetFilePath")+"\\Source");    
				// 创建目录  
		        if (!file.exists()) {  
		        	file.mkdirs();// 目录不存在的情况下，创建目录。  
		            XMLOut.output(doc, new FileOutputStream(sheetMap.get("TargetFilePath")+"\\"+System.currentTimeMillis()+".xml"));  
		            //拷贝
					 if(sheetMap.get("BackPath")==null||sheetMap.get("BackPath").equals("")){
						 sheetMap.put("BackPath", sheetMap.get("SourceFilePath").substring(0, 1)+":\\copy");
					 }else if(!regEx(sheetMap.get("TargetFilePath"), reg)){
						 sheetMap.put("BackPath", sheetMap.get("SourceFilePath").substring(0, 1)+":\\copy");
					 }
					 File copyFile =new File(sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd"));
					 	//文件存在
						if (!copyFile.exists()) { 
							copyFile.mkdirs();// 目录不存在的情况下，创建目录。  
							copyFile(sheetMap.get("SourceFilePath"), sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd")+"\\"+getCurrentTime("yyyyMMddHHmmss")+".xml");
							deleteFile(sheetMap.get("SourceFilePath"));
						}else{
							copyFile(sheetMap.get("SourceFilePath"), sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd")+"\\"+getCurrentTime("yyyyMMddHHmmss")+".xml");
							deleteFile(sheetMap.get("SourceFilePath"));
						}
		        }else{
		        	 XMLOut.output(doc, new FileOutputStream(PathFileName = sheetMap.get("TargetFilePath")+"\\"+System.currentTimeMillis()+".xml"));  
		        	 if(sheetMap.get("BackPath")==null||sheetMap.get("BackPath").equals("")){
						 sheetMap.put("BackPath", sheetMap.get("SourceFilePath").substring(0, 1)+":\\copy");
					 }else if(!regEx(sheetMap.get("TargetFilePath"), reg)){
						 sheetMap.put("BackPath", sheetMap.get("SourceFilePath").substring(0, 1)+":\\copy");
					 }
		        	 File copyFile =new File(sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd"));    
		        	//文件存在
					if (!copyFile.exists()) { 
						copyFile.mkdirs();// 目录不存在的情况下，创建目录。  
						copyFile(sheetMap.get("SourceFilePath"), sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd")+"\\"+getCurrentTime("yyyyMMddHHmmss")+".xml");
						deleteFile(sheetMap.get("SourceFilePath"));
					}else{
						copyFile(sheetMap.get("SourceFilePath"), sheetMap.get("BackPath")+"\\"+getCurrentTime("yyyyMMdd")+"\\"+getCurrentTime("yyyyMMddHHmmss")+".xml");
						deleteFile(sheetMap.get("SourceFilePath"));
					}
		        }
		        
			}
		 }
	  
	    public static String getCurrentTime(String sldf) {
		          SimpleDateFormat sdf = new SimpleDateFormat(sldf);
		          Date date = new Date();
		          return sdf.format(date);
		}
	    
	    public static void copyFile(String oldPath, String newPath) throws IOException { 
	           int bytesum = 0; 
	           int byteread = 0; 
	           File oldfile = new File(oldPath); 
	           if (oldfile.exists()) {                  //文件存在时 
	               InputStream inStream = new FileInputStream(oldPath);      //读入原文件 
	               FileOutputStream fs = new FileOutputStream(newPath); 
	               byte[] buffer = new byte[1444]; 
	               int length; 
	               while ( (byteread = inStream.read(buffer)) != -1) { 
	                   bytesum += byteread;            //字节数 文件大小 
//	                   System.out.println(bytesum); 
	                   fs.write(buffer, 0, byteread); 
	               } 
	               inStream.close(); 
	           } 
	      
	   } 
	    
	    public static boolean deleteFile(String sPath) {
	        boolean flag = false;
	        File file = new File(sPath);
	        // 路径为文件且不为空则进行删除
	        if (file.isFile() && file.exists()) {
	            file.delete();
	            flag = true;
	        }
	        return flag;
	    }
	    
	    public static void initRoot(){
	    	 root = null;
			 element1 = null;
			 element2 = null;
			 element3 = null;
			 element4 = null;
			 element5 = null;
			 element6 = null;
			 element7 = null;
			 element8 = null;
			 element9 = null;
			 element0 = null;
			 element11 = null;
			 element12 = null;
			 element13 = null;
			 element14 = null;
			 element15 = null;
			 element16 = null;
			 element17 = null;
			 element18 = null;
			 element19 = null;
			 element20 = null;
	    }
	    
	    
	    private static Element root = null;
		private static Element element1 = null;
		private static Element element2 = null;
		private static Element element3 = null;
		private static Element element4 = null;
		private static Element element5 = null;
		private static Element element6 = null;
		private static Element element7 = null;
		private static Element element8 = null;
		private static Element element9 = null;
		private static Element element0 = null;
		private static Element element11 = null;
		private static Element element12 = null;
		private static Element element13 = null;
		private static Element element14 = null;
		private static Element element15 = null;
		private static Element element16 = null;
		private static Element element17 = null;
		private static Element element18 = null;
		private static Element element19 = null;
		private static Element element20 = null;
		
		public static String getPathFileName() {
			return PathFileName;
		}

		public static String getMsg() {
			return msg;
		}

		public static String getSourceFileName() {
			return SourceFileName;
		}

		public static HashMap<String, String> getSheetmap() {
			return sheetmap;
		}

		public static boolean isSuccess() {
			return success;
		}
		
	/*
	 * public static void main(String[] args) { LocalXmlIbmMq(new
	 * File("E:\\B\\1510025439093.xml")); }
	 */
		
		public static void LocalXmlIbmMq(File file){

		}
		
		
		 /**
	     * 根据List获取到对应的JSONArray
	     * @param list
	     * @return
	     */
	    public static JSONArray getJSONArrayByList(List<?> list){
	        JSONArray jsonArray = new JSONArray();
	        if (list==null ||list.isEmpty()) {
	            return jsonArray;//nerver return null
	        }

	        for (Object object : list) {
	            jsonArray.put(object);
	        }
	        return jsonArray;
	    }
	    
}
