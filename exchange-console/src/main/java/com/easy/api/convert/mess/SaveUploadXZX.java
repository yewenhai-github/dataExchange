package com.easy.api.convert.mess;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easy.api.convert.util.SerialNumberTool;
import com.easy.api.convert.util.Util;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/uploadXZX")
public class SaveUploadXZX extends MainServlet{
	private static final long serialVersionUID = 1L;

	/*
    创建FileItem
     */
    private FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }
	
	@SuppressWarnings({ "unchecked", "unused" })
	public void DoCommand() throws LegendException, Exception {
		final String INDX = getRequest().getParameter("INDX");
		if(INDX.equals("undefined")) {
			ReturnMessage(false, "请选择一条数据");
			return;
		}
		if(SysUtility.isEmpty(INDX)) {
			ReturnMessage(false, "数据为空");
			return;
		}
		String Configsql = "SELECT CONFIGNAME,SOURCEFILETYPE,FUNCTIONTYPE FROM exs_convert_config_path WHERE INDX =?";
		List query4List = SQLExecUtils.query4List(Configsql, new Callback() {
			
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, INDX);
			}
		});
		String fileName = "";
		HashMap dataMap = (HashMap)query4List.get(0);
		final String configName =(String) dataMap.get("CONFIGNAME");
		final String SOURCEFILETYPE =(String) dataMap.get("SOURCEFILETYPE");
		final String FUNCTIONTYPE =(String) dataMap.get("FUNCTIONTYPE");
		if(SysUtility.isEmpty(configName) || SysUtility.isEmpty(SOURCEFILETYPE)) {
			ReturnMessage(false, "文件类型或配置名错误,请修改相应配置!");
			return;
		}
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
		if("4".equals(FUNCTIONTYPE)) {
			SAXReader saxReader = new SAXReader();  
			File fileTemp=new File("C://GlobalServiceMESS//compoundTemp.xml");
	        Document  documentTemp = null; 
			for (FileItem item : list) {
				if(documentTemp==null) {
					item.write(fileTemp);
					documentTemp=(Document) saxReader.read(fileTemp); 
				}else {
					File fileTemp2 = new File("C://GlobalServiceMESS//temp.xml");
					item.write(fileTemp2);
					Document documentTemp2=saxReader.read(fileTemp2); 
			        List<Element> elements = documentTemp2.getDocument().getRootElement().elements();
			        Element parent = (Element) documentTemp.getRootElement();//获得第一个xml的根节点  
			        for (Element element : elements) {  
			            parent.add(element.detach());//将b下的节点添加到a的根节点下  
			        }  
			        System.out.println(documentTemp.asXML());  
                    FileUtility.createFile("C://GlobalServiceMESS//", "compoundTemp.xml", documentTemp.asXML());
				}
								
			}
			list.clear();
			FileItem fileItem = createFileItem(fileTemp ,"itemFileName");//FileItem不用指定路径，内存形势存在。
			fileItem.setFormField(false);
			list.add(fileItem);
		}
		
		for (FileItem item : list) {
			
			if (!item.isFormField()) {//只处理文件
				String filename = item.getName();
				// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
				String filestypename = filename.substring(filename.lastIndexOf("\\") + 1);
				//获取文件后缀名str1.equalsIgnoreCase(str2); 
				String prefix=filestypename.substring(filestypename.lastIndexOf(".")+1);
				if(!("zip".equalsIgnoreCase(prefix)) && !("xml".equalsIgnoreCase(prefix)) && !("xls".equalsIgnoreCase(prefix))){
				   ReturnMessage(false, filename+"文件格式不正确,请重新上传!");
				   continue;
				}/*else {
					if(SOURCEFILETYPE.equals("1")) {
						if(!"XML".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xml格式数据!");
							 continue;
						}
					}else if(SOURCEFILETYPE.equals("2")) {
						if(!"XLS".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xls格式数据!");
							 continue;
						}
						if(FUNCTIONTYPE.equals("2")) {
							 ReturnMessage(false,"XLS文件不支持 单发送");
							 continue;
						}
					}
				}*/
				
				/*String SEND = SysUtility.GetSetting("System","SEND");
				String[] SendSplit = SEND.split(",");
				if(SendSplit.length < 4) {
//					Util.AddMessLog(getDataAccess(),"发送MQ 系统配置有误  请联系管理员进行维护!",filename);
					ReturnMessage(false, "发送MQ 系统配置有误  请联系管理员维护!");
					LogUtil.printLog("发送MQ 系统配置有误  请维护!", Level.ERROR);
					return;
				}*/
				HashMap LogMap = null;
				// 获取item中的上传文件的输入流
				InputStream in = item.getInputStream();
				if("zip".equalsIgnoreCase(prefix)) {
					String folder=System.getProperty("java.io.tmpdir");
					String FileName = SysUtility.GetUUID()+".zip";
					FileUtility.createFile(folder+File.separator+FileName, in);
					File file = new File(folder+File.separator+FileName);
					ZipFile zip = new ZipFile(file,Charset.forName("GBK"));//解决中文文件夹乱码 
					String name = zip.getName().substring(zip.getName().lastIndexOf('\\')+1, zip.getName().lastIndexOf('.')); 
					for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {  
						ZipEntry entry = (ZipEntry) entries.nextElement(); 
						String zipEntryName = entry.getName();  
						//判断字符
						if(SysUtility.isNotEmpty(zipEntryName)) {
							String UsersQL = "SELECT REGISTER_NO FROM s_auth_user WHERE INDX='"+SysUtility.getCurrentUserIndx()+"'";
							List query4List2 = SQLExecUtils.query4List(UsersQL);
							HashMap userMap = (HashMap)query4List2.get(0);
							final String regNo = (String) userMap.get("REGISTER_NO");
							zipEntryName = fileName = regNo+"_"+configName+"_"+getData()+"_"+SerialNumberTool.getInstance().generaterNextNumber(6)+"_"+zipEntryName;
							String Zipprefix=zipEntryName.substring(zipEntryName.lastIndexOf(".")+1);
							String sql ="SELECT * FROM exs_convert_config_path WHERE ORG_ID =(SELECT ORG_ID FROM s_auth_user WHERE REGISTER_NO=?)  AND CONFIGNAME=?";
							List mqList = SQLExecUtils.query4List(sql, new Callback() {
								@Override
								public void doIn(PreparedStatement ps) throws SQLException {
									ps.setString(1,regNo);
									ps.setString(2, configName);
								}
							});
							if(mqList.size()>0) {
								HashMap mqMap = (HashMap) mqList.get(0);
								String TARGETFILETYPE = (String)mqMap.get("TARGETFILETYPE");
								if(FUNCTIONTYPE.equals("1") || FUNCTIONTYPE.equals("3")) {
									if(SysUtility.isEmpty(TARGETFILETYPE)) {
										ReturnMessage(false, "目标文件类型不可为空！");
										return;
									}
								}
								if(SOURCEFILETYPE.equals("1")) {
									if(!"XML".equalsIgnoreCase(Zipprefix.toUpperCase())){
//										 ReturnMessage(false,"请上传 xml格式数据!");
										 continue;
									}
									LogMap = new HashMap();
									LogMap.put("SERIAL_NO", regNo);
									LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
									LogMap.put("DATA_SOURCE", "服务器");
									LogMap.put("SOURCE_FILE_NAME", filename);
									LogMap.put("TARGET_FILE_NAME", fileName);
									LogMap.put("PROCESS_MSG","ZIP文件传输");
									Util.AddMessLog(getDataAccess(), LogMap);
									InputStream ins = zip.getInputStream(entry);
									boolean createFile = FileUtility.createFile(mqMap.get("SOURCEFILEPATH")+File.separator+zipEntryName, ins);
									String BACK_PATH =  mqMap.get("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
									fileHandle(BACK_PATH);
									FileUtility.copyFile(mqMap.get("SOURCEFILEPATH")+File.separator+zipEntryName, BACK_PATH+File.separator+zipEntryName);
									if(createFile) {
										LogMap = new HashMap();
										LogMap.put("SERIAL_NO", regNo);
										LogMap.put("DATA_SOURCE", "服务器");
										LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
										LogMap.put("SOURCE_FILE_NAME", filename);
										LogMap.put("TARGET_FILE_NAME", zipEntryName);
										LogMap.put("PROCESS_MSG","ZIP文件传输");
										LogMap.put("SOURCE_BACK_PATH",BACK_PATH+File.separator+zipEntryName);
										Util.AddMessLog(getDataAccess(), LogMap);
									}else {
										LogMap = new HashMap();
										LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
										LogMap.put("TARGET_FILE_NAME", zipEntryName);
										LogMap.put("PROCESS_MSG","ZIP文件传输失败");
										Util.AddMessLog(getDataAccess(), LogMap);
									}
								}else if(SOURCEFILETYPE.equals("2")) {
									if(!"XLS".equalsIgnoreCase(Zipprefix.toUpperCase())){
//										 ReturnMessage(false,"请上传 xls格式数据!");
										 continue;
									}
									if(FUNCTIONTYPE.equals("2")) {
										 ReturnMessage(false,"XLS文件不支持 单发送");
										 return;
									}
									LogMap = new HashMap();
									LogMap.put("SERIAL_NO", regNo);
									LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
									LogMap.put("DATA_SOURCE", "服务器");
									LogMap.put("SOURCE_FILE_NAME", filename);
									LogMap.put("TARGET_FILE_NAME", fileName);
									LogMap.put("PROCESS_MSG","ZIP文件传输");
									Util.AddMessLog(getDataAccess(), LogMap);
									InputStream ins = zip.getInputStream(entry);
									String BACK_PATH =  mqMap.get("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
									fileHandle(BACK_PATH);
									boolean createFile = createFile(mqMap.get("SOURCEFILEPATH")+File.separator+zipEntryName, ins,BACK_PATH+File.separator+zipEntryName);
//									copyFile(mqMap.get("SOURCEFILEPATH")+File.separator+zipEntryName, mqMap.get("SOURCE_BACK_PATH")+File.separator+zipEntryName);
									if(createFile) {
										LogMap = new HashMap();
										LogMap.put("SERIAL_NO", regNo);
										LogMap.put("DATA_SOURCE", "服务器");
										LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
										LogMap.put("SOURCE_FILE_NAME", filename);
										LogMap.put("TARGET_FILE_NAME", zipEntryName);
										LogMap.put("PROCESS_MSG","ZIP文件传输");
										LogMap.put("SOURCE_BACK_PATH",BACK_PATH+File.separator+zipEntryName);
										Util.AddMessLog(getDataAccess(), LogMap);
									}else {
										LogMap = new HashMap();
										LogMap.put("TARGET_FILE_NAME", zipEntryName);
										LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
										LogMap.put("PROCESS_MSG","ZIP文件传输失败");
										Util.AddMessLog(getDataAccess(), LogMap);
									}
								}
								/*
								InputStream ins = zip.getInputStream(entry);
								byte[] bytes = SysUtility.InputStreamToByte(ins);
								String xmlData = new String(bytes,"UTF-8");
								String SENDTYPE = SendSplit[0];//发送类型
								String SENDADRESS = SendSplit[1];//发送地址
								String SENDUSERNAME = SendSplit[2];//发送用户名
								String SENDUSERPWD = SendSplit[3];//发送密码
								String QUEUENAME = SendSplit[4];//队列
								String[] ADRESSSplit = SENDADRESS.split(":");
								int port =21; 
								
								if("1".equals(SENDTYPE)) {//FTP
									boolean uploadFile = FtpUtil.uploadFile(ADRESSSplit[0], port, SENDUSERNAME, SENDUSERPWD, zipEntryName, ins);
									String msg = FtpUtil.getMsg();
									boolean state = FtpUtil.isState(); 
								}else if("2".equals(SENDTYPE)) {
									
								}else if("3".equals(SENDTYPE)) {//MS MQ
									LogMap.put("SERIAL_NO", regNo);
									LogMap.put("DATA_SOURCE", "Msmq");
									LogMap.put("SOURCE_FILE_NAME", filename);
									LogMap.put("TARGET_FILE_NAME", zipEntryName);
									LogMap.put("FILE_PATH",file.getPath());
									LogMap.put("PROCESS_MSG","ZIP文件传输");
									Util.AddMessLog(getDataAccess(), LogMap);
									ToMSMQ.run(ADRESSSplit[0], ADRESSSplit[1], zipEntryName, xmlData);
								}else if("4".equals(SENDTYPE)) {
									LogMap.put("SERIAL_NO", regNo);
									LogMap.put("DATA_SOURCE", "ActiveMq");
									LogMap.put("SOURCE_FILE_NAME", filename);
									LogMap.put("TARGET_FILE_NAME", zipEntryName);
									LogMap.put("FILE_PATH",file.getPath());
									LogMap.put("PROCESS_MSG","ZIP文件传输");
									Util.AddMessLog(getDataAccess(), LogMap);
									ToActiveMQ.LocalXmlToActiveMQ(SENDADRESS, SENDUSERNAME, QUEUENAME, SENDUSERPWD, zipEntryName, xmlData);
								}else if("5".equals(SENDTYPE)) {
									
								}*/
								//准备发送MQ
							}else {
								  ReturnMessage(false, "注册号或类型不存在!");
							}
						}
						
					}
					FileUtility.deleteFile(file);
				}else if("xml".equalsIgnoreCase(prefix)) {
					if(SOURCEFILETYPE.equals("1")) {
						if(!"XML".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xml格式数据!");
							 return;
						}
					}else if(SOURCEFILETYPE.equals("2")) {
						if(!"XLS".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xls格式数据!");
							 return;
						}
						if(FUNCTIONTYPE.equals("2")) {
							 ReturnMessage(false,"XLS文件不支持 单发送");
							 return;
						}
					}
					
					String UsersQL = "SELECT REGISTER_NO FROM s_auth_user WHERE INDX='"+SysUtility.getCurrentUserIndx()+"'";
					List query4List2 = SQLExecUtils.query4List(UsersQL);
					HashMap userMap = (HashMap)query4List2.get(0);
					final String regNo = (String) userMap.get("REGISTER_NO");
					filestypename = fileName = regNo+"_"+configName+"_"+getData()+"_"+SerialNumberTool.getInstance().generaterNextNumber(6)+"_"+filestypename;
					
					String sql ="select * from exs_convert_config_path where org_id =(select org_id from s_auth_user where register_no=?)  and CONFIGNAME=?";
					List mqList = SQLExecUtils.query4List(sql, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1,regNo);
							ps.setString(2, configName);
						}
					});
					if(mqList.size()>0) {
						HashMap mqMap = (HashMap) mqList.get(0);
						String functionType = (String)mqMap.get("FUNCTIONTYPE");
						String TARGETFILETYPE = (String)mqMap.get("TARGETFILETYPE");
						String BACK_PATH =  mqMap.get("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
						fileHandle(BACK_PATH);
						if(functionType.equals("1") || functionType.equals("3")) {
							if(SysUtility.isEmpty(TARGETFILETYPE)) {
								ReturnMessage(false, "目标文件类型不可为空！");
								return;
							}
						}
						//准备发送
						LogMap = new HashMap();
						LogMap.put("SERIAL_NO", regNo);
						LogMap.put("DATA_SOURCE", "服务器");
						LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
						LogMap.put("SOURCE_FILE_NAME", filename);
						LogMap.put("TARGET_FILE_NAME", fileName);
						LogMap.put("PROCESS_MSG","XML文件传输");
						Util.AddMessLog(getDataAccess(), LogMap);
						LogMap = new HashMap();
						LogMap.put("SERIAL_NO", regNo);
						LogMap.put("DATA_SOURCE", "服务器");
						LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
						LogMap.put("SOURCE_FILE_NAME", filename);
						LogMap.put("TARGET_FILE_NAME", fileName);
						LogMap.put("PROCESS_MSG","XML文件传输");
						LogMap.put("SOURCE_BACK_PATH",BACK_PATH+File.separator+fileName);
						Util.AddMessLog(getDataAccess(), LogMap);
						boolean createFile = FileUtility.createFile(mqMap.get("SOURCEFILEPATH")+File.separator+fileName, in);
						
						FileUtility.copyFile(mqMap.get("SOURCEFILEPATH")+File.separator+fileName, BACK_PATH+File.separator+fileName);
						/*
						//准备发送MQ
						byte[] bytes = SysUtility.InputStreamToByte(in);
						String xmlData = new String(bytes,"UTF-8");
						String SENDTYPE = SendSplit[0];//发送类型
						String SENDADRESS = SendSplit[1];//发送地址
						String SENDUSERNAME = SendSplit[2];//发送用户名
						String SENDUSERPWD = SendSplit[3];//发送密码
						String QUEUENAME = SendSplit[4];//队列
						String[] ADRESSSplit = SENDADRESS.split(":");
						int port =21; 
						
						if("1".equals(SENDTYPE)) {//FTP
							boolean uploadFile = FtpUtil.uploadFile(ADRESSSplit[0], port, SENDUSERNAME, SENDUSERPWD, filename, in);
							String msg = FtpUtil.getMsg();
							boolean state = FtpUtil.isState(); 
						}else if("2".equals(SENDTYPE)) {
							
						}else if("3".equals(SENDTYPE)) {//MS MQ
							LogMap.put("SERIAL_NO", regNo);
							LogMap.put("DATA_SOURCE", "IBMMQ");
							LogMap.put("SOURCE_FILE_NAME", filename);
							LogMap.put("TARGET_FILE_NAME", filestypename);
							LogMap.put("PROCESS_MSG","XML文件传输");
							Util.AddMessLog(getDataAccess(), LogMap);
							ToMSMQ.run(ADRESSSplit[0], ADRESSSplit[1], filename, xmlData);
							LogMap.put("PROCESS_MSG","XML文件传输已发送至服务器");
							Util.AddMessLog(getDataAccess(), LogMap);
						}else if("4".equals(SENDTYPE)) {
							LogMap.put("SERIAL_NO", regNo);
							LogMap.put("DATA_SOURCE", "ActiveMQ");
							LogMap.put("SOURCE_FILE_NAME", filename);
							LogMap.put("TARGET_FILE_NAME", filestypename);
							LogMap.put("PROCESS_MSG","XML文件传输");
							Util.AddMessLog(getDataAccess(), LogMap);
							boolean localXmlToActiveMQ = ToActiveMQ.LocalXmlToActiveMQ(SENDADRESS, SENDUSERNAME, QUEUENAME, SENDUSERPWD, filestypename, xmlData);
							if(localXmlToActiveMQ) {
								LogMap.put("PROCESS_MSG","XML文件传输成功");
								Util.AddMessLog(getDataAccess(), LogMap);
							}else {
								LogMap.put("PROCESS_MSG","XML文件传输失败");
								Util.AddMessLog(getDataAccess(),LogMap);
							}
						}else if("5".equals(SENDTYPE)) {
							
						}
						*/
						
					}else {
						  ReturnMessage(false, "注册号或类型不存在!");
						  return;
					}
				}else if("xls".equalsIgnoreCase(prefix)) {
					if(SOURCEFILETYPE.equals("1")) {
						if(!"XML".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xml格式数据!");
							 return;
						}
					}else if(SOURCEFILETYPE.equals("2")) {
						if(!"XLS".equalsIgnoreCase(prefix.toUpperCase())){
							 ReturnMessage(false,"请上传 xls格式数据!");
							 return;
						}
						if(FUNCTIONTYPE.equals("2")) {
							 ReturnMessage(false,"XLS文件不支持 单发送");
							 return;
						}
					}
					//尝试以byte发送
					String UsersQL = "SELECT REGISTER_NO FROM s_auth_user WHERE INDX='"+SysUtility.getCurrentUserIndx()+"'";
					List query4List2 = SQLExecUtils.query4List(UsersQL);
					HashMap userMap = (HashMap)query4List2.get(0);
					final String regNo = (String) userMap.get("REGISTER_NO");
					filestypename = fileName = regNo+"_"+configName+"_"+getData()+"_"+SerialNumberTool.getInstance().generaterNextNumber(6)+"_"+filestypename;
					String sql ="select * from exs_convert_config_path where org_id =(select org_id from s_auth_user where register_no=?)  and CONFIGNAME=?";
					List mqList = SQLExecUtils.query4List(sql, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1,regNo);
							ps.setString(2, configName);
						}
					});
					if(mqList.size()>0) {
						HashMap mqMap = (HashMap) mqList.get(0);
						String functionType = (String)mqMap.get("FUNCTIONTYPE");
						String TARGETFILETYPE = (String)mqMap.get("TARGETFILETYPE");
						String BACK_PATH =  mqMap.get("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
						fileHandle(BACK_PATH);
						if(functionType.equals("1") || functionType.equals("3")) {
							if(SysUtility.isEmpty(TARGETFILETYPE)) {
								ReturnMessage(false, "目标文件类型不可为空！");
								return;
							}
						}
						//准备发送
						LogMap = new HashMap();
						LogMap.put("SERIAL_NO", regNo);
						LogMap.put("DATA_SOURCE", "服务器");
						LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
						LogMap.put("SOURCE_FILE_NAME", filename);
						LogMap.put("TARGET_FILE_NAME", fileName);
						LogMap.put("PROCESS_MSG","XLS文件传输");
						Util.AddMessLog(getDataAccess(), LogMap);
						boolean createFile = createFile(mqMap.get("SOURCEFILEPATH")+File.separator+fileName, in,BACK_PATH+File.separator+fileName);
//						copyFile(mqMap.get("SOURCEFILEPATH")+File.separator+fileName, mqMap.get("SOURCE_BACK_PATH")+File.separator+fileName);
						if(createFile) {
							LogMap = new HashMap();
							LogMap.put("SERIAL_NO", regNo);
							LogMap.put("DATA_SOURCE", "服务器");
							LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
							LogMap.put("SOURCE_FILE_NAME", filename);
							LogMap.put("TARGET_FILE_NAME", fileName);
							LogMap.put("PROCESS_MSG","XLS文件传输");
							LogMap.put("SOURCE_BACK_PATH",BACK_PATH+File.separator+fileName);
							Util.AddMessLog(getDataAccess(), LogMap);
						}else {
							LogMap = new HashMap();
							LogMap.put("TARGET_FILE_NAME", fileName);
							LogMap.put("CONFIG_PATH_ID", mqMap.get("INDX"));
							LogMap.put("PROCESS_MSG","XLS文件传输失败");
							Util.AddMessLog(getDataAccess(), LogMap);
						}
						
					}else {
						  ReturnMessage(false, "注册号或类型不存在!");
						  continue;
					}
					ReturnMessage(true, "处理完成  处理文件名为:"+filename +" 请到处理结果查看!");
				}
				
			}else {
				
			}
			
		}
		if(SysUtility.isNotEmpty(fileName)) {
			ReturnMessage(true, "处理完成  处理文件名为:"+fileName +" 请到处理结果查看!");
		}
		return;
		
	}
	
	
	
	public static String getData() {
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyMMdd");
		return format.format(date);	
	}
	
	
	public static boolean createFile(String path,InputStream is,String dFileName) {
		if(SysUtility.isEmpty(path)){
			return false;
		}
		if(path.endsWith("xls")){
			path = renameFileNameXmlToExs(path);
		}
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
			LogUtil.printLog("文件生成成功："+path, Level.INFO);
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
			copyFile(path, dFileName);
			renameFileExsToXml(path);
			LogUtil.printLog("文件重名成功："+path, Level.INFO);
			return true;
		} catch (Exception e) {
			LogUtil.printLog("文件重名失败："+path, Level.ERROR);
		}
		return false;
	}
	
	
	public static String renameFileNameXmlToExs(String fileName){
		
		return fileName.substring(0, fileName.length() - 4)+".exs";
	}
	
	public static boolean renameFileExsToXml(String FilePath){
		if(SysUtility.isEmpty(FilePath) || FilePath.length() < 4 || !FilePath.endsWith(".exs")){
			return false;
		}
		boolean rt = false;
		File file = new File(FilePath);  
		if(file.exists()){
			FilePath = FilePath.substring(0, FilePath.length() - 4)+".xls";
			if(new File(FilePath).exists()){
				FileUtility.deleteFile(new File(FilePath));
			}
			rt = file.renameTo(new File(FilePath));
		}
		return rt;
	}
	
	
	public static boolean copyFile(String sFileName, String dFileName) {
		boolean booRec = false;
		try {
			File file_in = new File(sFileName);
			if(dFileName.endsWith("xls")){
				dFileName = renameFileNameXmlToExs(dFileName);
			}
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
			LogUtil.printLog("文件生成成功："+dFileName, Level.INFO);
			renameFileExsToXml(dFileName);
			LogUtil.printLog("文件重名成功："+dFileName, Level.INFO);
			booRec = true;
			LogUtil.printLog("拷贝文件成功："+dFileName, Level.INFO);
		} catch (Exception ex) {
			LogUtil.printLog("拷贝文件失败:"+ex.getMessage(), LogUtil.ERROR);
		}
		return booRec;
	}
	
	//文件处理
			public static void fileHandle(String path) {
				try {
					File file = new File(path);
					if (!file.exists()) {
						   if(file.mkdirs()){
							   LogUtil.printLog("文件夹创建成功："+file.getPath(), Level.INFO);
					   }
					}
				} catch (Exception e) {
				}
			}

}
