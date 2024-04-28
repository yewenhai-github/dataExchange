package com.easy.convert.service.mess;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;

import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.mq.ToActiveMQ;
import com.easy.convert.service.util.FtpUtil;
import com.easy.convert.service.util.Util;
import com.easy.exception.LegendException;
import com.easy.file.FileFilterHandle;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


public class Message_TransferUpMq extends MainServlet implements IGlobalService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8076497871200812682L;
	private static final Integer TotalFile = 1000;
	
	public Message_TransferUpMq(String param) {
	}

	@SuppressWarnings("rawtypes")
	public void DoCommand() throws LegendException, Exception {
		try {
		List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE nvl(ISENABLED,'1') = '1'");
		if(query4List.size()>0) {
			for(int i=0;i<query4List.size();i++) {
				HashMap logMap = new HashMap();
				HashMap  Sudata = (HashMap)query4List.get(i);
				String successpath = (String) Sudata.get("TARGETFILEPATH");
				String RECEIVETYPE = (String) Sudata.get("RECEIVETYPE");
				String RECEIVEADRESS = (String) Sudata.get("RECEIVEADRESS");
				String RECEIVEUSERNAME = (String) Sudata.get("RECEIVEUSERNAME");
				String RECEIVEUSERPWD = (String) Sudata.get("RECEIVEUSERPWD");
				String RECEIVEPATH = (String) Sudata.get("RECEIVEPATH");
				String FUNCTIONTYPE = (String) Sudata.get("FUNCTIONTYPE");
				String SOURCEFILETYPE = (String) Sudata.get("SOURCEFILETYPE");
				String TRANSACTIONTYPE = (String) Sudata.get("TRANSACTIONTYPE");
				if(!SysUtility.isEmpty(successpath)) {
					File files[] = new File(successpath).listFiles(new FileFilterHandle());
					if(!SysUtility.isEmpty(files)) {
						for (int f = 0; f < files.length; f++) {
							if(f==TotalFile) {
								return;
							}else if(SysUtility.isEmpty(files[f].getName())){
								continue;
							}
							if(SysUtility.isNotEmpty(FUNCTIONTYPE)){
								
								if(FUNCTIONTYPE.equals("1")) {//只转换不发送
									String[] fileSplit = files[f].getName().split("_");
									if(fileSplit.length>=4) {
										
										final String regNo = fileSplit[0];
										final String fileType = fileSplit[1];
										String fileDate = fileSplit[2];
										String no =fileSplit[3];
										String sql = "select indx from exs_convert_log where serial_no='"+regNo+"' and TARGET_FILE_NAME='"+files[f].getName()+"' AND SEND_CODE=0";
										List query4List2 = SQLExecUtils.query4List(sql);
										if(query4List2.size()<=0) {
											continue;
										}else {
											if(!SOURCEFILETYPE.equals("2") && !SOURCEFILETYPE.equals("4")) {
												logMap.put("DATA_SOURCE", "ActiveMq");
												logMap.put("SERIAL_NO", regNo);
												logMap.put("TARGET_FILE_NAME", files[f].getName());
												logMap.put("FILE_PATH", files[f].getPath());
												logMap.put("SEND_CODE", "1");
												logMap.put("SEND_NAME", "无需");
												Util.AddMessLog(getDataAccess(), logMap);
											}
										}
										
									}
									continue;
								}
								
							}
							if(!files[f].getName().substring(files[f].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){
								continue;
							}
							long startTime=System.currentTimeMillis();   //获取开始时间
							if(SysUtility.isNotEmpty(RECEIVETYPE)){
								if(RECEIVETYPE.equals("1")) {//FTP
									String [] ADRESSSplit = RECEIVEADRESS.split(":");
									int port =21; 
									if(ADRESSSplit.length>1) {
										port = Integer.parseInt(ADRESSSplit[ADRESSSplit.length-1]);
									}
									FtpUtil.uploadFile(getDataAccess(),ADRESSSplit[0], port, RECEIVEUSERNAME, RECEIVEUSERPWD,  files[f].getName(),files[f].getPath(), files[f]);
								}else if(RECEIVETYPE.equals("2")) {//WEBService
									
								}else if(RECEIVETYPE.equals("3")) {//MS MQ

								}else if(RECEIVETYPE.equals("4")) {//ACTIVEMQ
									ToActiveMQ.LocalXmlToActiveMQ(getDataAccess(), files[f],RECEIVEADRESS,RECEIVEUSERNAME,RECEIVEUSERPWD);
								}else if(RECEIVETYPE.equals("5")) {//IBM MQ

								}else if(RECEIVETYPE.equals("6")) {//其他
									
								}
							}
						}	
					}
				}
				
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}

}
