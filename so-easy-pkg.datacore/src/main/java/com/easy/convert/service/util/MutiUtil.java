package com.easy.convert.service.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.convert.service.mess.Message_TransferJob;
import com.easy.entity.ServicesBean;
import com.easy.file.FileFilterHandle;
import com.easy.thread.JobContainer;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


/**
 * 线程工具类
 * @author chenchuang
 *
 */
public class MutiUtil {
	public static final String NewFolderOpen = SysUtility.isEmpty(SysUtility.GetSetting("System", "NewFolder.open"))?"":SysUtility.GetSetting("System", "NewFolder.open");
	public static final HashMap<String,Integer> MutiController = new HashMap<String,Integer>();
	public static final HashMap<String,List<Integer>> MonitorController = new HashMap<String,List<Integer>>();
	public static final int mutiProcessCount = 100000;//多线程模式：单任务的总报文处理量。

	
	public static boolean IsMutiProcess(ServicesBean bean){
		String ThreadCount = bean.getThreadCount();
		if(SysUtility.isNotEmpty(ThreadCount) && Integer.parseInt(ThreadCount) > 1){
			return true;
		}
		if(SysUtility.isNotEmpty(bean.getClusterList())){
			return true;
		}
		return false;
	}
	
	//处理报文转换  目前支持本地文件转换lOCAl 
	public static void MutiProcessToXmlLocal(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		JSONObject searchParam = bean.getSearchParam();
		if(SysUtility.isEmpty(searchParam)) {
			LogUtil.printLog("Bean.searchParam数据为空  多线程处理失败！", Level.ERROR);
			return;
		}
		String key  = "";
		if(!searchParam.isNull("CONFIGNAME")) {
			key = searchParam.getString("CONFIGNAME");
		}
		if(!searchParam.isNull("SEQ")) {
			key += searchParam.getString("SEQ");
		}
		if(SysUtility.isNotEmpty(key)) {
			if(SysUtility.isEmpty(MutiController.get(key))){
				MutiController.put(key,0);
			}
			
			if(MutiController.get(key) == 0){
				MutisToXmlLocal(bean, DataAccess);
			}else{
				LogUtil.printLog(key+":文件正在处理中...线程中剩余文件数:"+MutiController.get(key), Level.INFO);
				ProcessMutiController(key);
			}
		}else {
			LogUtil.printLog("Bean.searchParam.ConfigName数据为空  多线程处理失败！", Level.ERROR);
			return;
		}
		
	}
	
	
	public static void MutisToXmlLocal(ServicesBean bean,IDataAccess DataAccess)throws Exception{
		JSONObject searchParam = bean.getSearchParam();
		String key  = "";
		if(!searchParam.isNull("CONFIGNAME")) {
			key = searchParam.getString("CONFIGNAME");
		}
		if(!searchParam.isNull("SEQ")) {
			key += searchParam.getString("SEQ");
		}
		if(SysUtility.isNotEmpty(key)) {
			if(MutiController.get(key) != 0){
				LogUtil.printLog(key+":文件正在处理中...", Level.INFO);
				return;
			}
			
			String SOURCE_PATH = "" ;
			String ERROR_PATH = "";
			if(!searchParam.isNull("SOURCEFILEPATH")) {
				SOURCE_PATH = searchParam.getString("SOURCEFILEPATH");
			}
			if(!searchParam.isNull("ERRORPATH")) {
				ERROR_PATH = searchParam.getString("ERRORPATH");
			}
			File files[] = new File(SOURCE_PATH).listFiles(new FileFilterHandle());
			List list = new ArrayList();
			if(SysUtility.isEmpty(files)){
				if(!SysUtility.isEmpty(SOURCE_PATH)) {
					if(!SysUtility.isEmpty(NewFolderOpen) && "true".equals(NewFolderOpen)) {
						FileUtility.createFolder(SOURCE_PATH, "");
					}
				}else {
					LogUtil.printLog("Bean.searchParam.SOURCEFILEPATH数据为空  多线程处理失败！", Level.ERROR);
				}
				
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()){
					boolean rt = FileUtility.ExsFileProcess(files[i], SOURCE_PATH, ERROR_PATH);
					if(rt){
						continue;
					}
					list.add(new Object[]{SOURCE_PATH,files[i]});
					if(list.size() > mutiProcessCount){
						break;
					}
				}else{
					String folderName = files[i].getName();
					if("Backup".equals(folderName)){
						continue;
					}
					bean.setChildFolder(folderName);
					String TEMP_SOURCE_PATH = FileUtility.createFolder(SOURCE_PATH, folderName);
					File files2[] = new File(TEMP_SOURCE_PATH).listFiles(new FileFilterHandle());
					for (int k = 0; k < files2.length; k++) {
						boolean rt = FileUtility.ExsFileProcess(files2[k], SOURCE_PATH, ERROR_PATH);
						if(rt){
							continue;
						}
						list.add(new Object[]{TEMP_SOURCE_PATH,files2[k]});
						if(list.size() > mutiProcessCount){
							break;
						}
					}
					if(list.size() > mutiProcessCount){
						break;
					}
				}
			}
			if(SysUtility.isEmpty(list)){
				return;
			}
			
			/******************************************************************/
			if(SysUtility.isNotEmpty(bean.getThreadCount())){//ThreadProcess
				int ThreadCount = Integer.parseInt(bean.getThreadCount());
				int count = 0;
				for (int i = 1; i <= ThreadCount; i++) {
					List tempList = new ArrayList();
					int listSize = (list.size()/ThreadCount+1)*i;
					for (int j = count; j < listSize; j++) {
						if(count == list.size()){
							break;
						}
						tempList.add(list.get(count));
						count++;
					}
					if(SysUtility.isEmpty(tempList)){
						continue;
					}
					
					HashMap<String, Object> fileMap = new HashMap<String, Object>();
					fileMap.put("List", tempList);
					fileMap.put("Bean", bean);
					fileMap.put("DataAccess", DataAccess);
					AddMutiController(key, tempList.size());
					JobContainer.getInstance().addToList(new Message_TransferJob(fileMap));
				}
			}
			
			while(MutiController.get(key) != 0){
				LogUtil.printLog(key+":文件正在处理中...线程中剩余文件数:"+MutiController.get(key), Level.INFO);//测试暂时提高warn级别，正常后降低至Info级
				Thread.sleep(1000);
			}
		}
		
		
		
		
	}
	
	
	
	public static synchronized void AddMutiController(String MutiControllerName,int size){
		int processCount = MutiController.get(MutiControllerName).intValue();
		processCount += size;
		MutiController.put(MutiControllerName,processCount);
	}
	
	public static synchronized void MinusMutiController(String MutiControllerName){
		int processCount = MutiController.get(MutiControllerName).intValue();
		processCount--;
		MutiController.put(MutiControllerName,processCount);
	}
	
	
	public static synchronized void ProcessMutiController(String key){
		if(SysUtility.isEmpty(MonitorController.get(key))){
			MonitorController.put(key,new ArrayList<Integer>());
		}
		
		//变量如果在一段时间内变量未改变，则直接重置。
		List<Integer> lst = MonitorController.get(key);
		Integer IMuti = MutiController.get(key);
		if(IMuti == 0){
			return;
		}
		
		if(lst.size() == 0){
			lst.add(IMuti);//将当前线程统计变量加入监控集合中
		}else{
			//上次(第一次)线程的值与当前线程的值比对，如果一致，则累加用于判断的次数，否则清空。
			Integer FirstCount = lst.get(lst.size()-1);
			if(SysUtility.isNotEmpty(FirstCount) && SysUtility.isNotEmpty(IMuti) 
					&& FirstCount.intValue() != 0 && IMuti.intValue() != 0 
				    && FirstCount.intValue() == IMuti.intValue()){
				lst.add(IMuti);//将当前线程统计变量加入监控集合中
//				LogUtil.printLog(key+":多线程变量添加成功...lst.size()="+lst.size()+",IMuti="+IMuti, Level.WARN);
			}else{
				MonitorController.put(key,new ArrayList<Integer>());//清空
//				LogUtil.printLog(key+":多线程变量重置成功...lst.size()="+0+",IMuti="+IMuti, Level.WARN);
			}
		}
		if(lst.size() > 5){//多线程中，10次监控到的参数一致，则说明监控参数异常，需重置。
			LogUtil.printLog("线程出现异常："+key+":多线程自动处理成功...偏移量："+lst.size()+",IMuti="+IMuti, Level.WARN);
			MutiController.put(key,0);
		}
	}
	
}
