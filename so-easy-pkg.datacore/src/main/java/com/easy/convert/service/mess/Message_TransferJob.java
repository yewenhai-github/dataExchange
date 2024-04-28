package com.easy.convert.service.mess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Level;
import org.jdom.Element;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.convert.service.util.Message_TransferToXmlThread;
import com.easy.convert.service.util.MutiUtil;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.session.SessionManager;
import com.easy.thread.JobDetail;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
/**
 * 私自类  不允许被继承   另一方面为了提升性能
 * @author chenchuang
 *
 */
public  final class Message_TransferJob extends JobDetail{
	HashMap<String, Object> fileMap = new HashMap<String, Object>();
	public Message_TransferJob(HashMap paramData){
		this.fileMap = paramData;
	}
	@SuppressWarnings("rawtypes")
	public void run() throws LegendException{
		List list = (List)fileMap.get("List");
		ServicesBean bean = (ServicesBean)fileMap.get("Bean");
	
		ServicesBean tempBean = (ServicesBean) bean.clone();
		IDataAccess DataAccess = SessionManager.getDataAccess();
//		IDataAccess DataAccess = (IDataAccess)fileMap.get("DataAccess");
		SysUtility.loadOperator(SysUtility.getCurrentConnection());
		JSONObject searchParam = bean.getSearchParam();
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[])list.get(i);
			File file = (File)obj[1];
			try {
				tempBean.setSourcePath((String)obj[0]);
				tempBean.setFileName(file.getName());
				tempBean.setFile(file);
				tempBean.setTempMap(initElentment());
				//主逻辑
				Message_TransferToXmlThread.xmltoxmlLocalThread(tempBean);
			} catch (Exception e) {
				LogUtil.printLog("线程处理文件出错："+e.getMessage(), Level.ERROR);
			} finally{
				try {
					String key  = "";
					if(!searchParam.isNull("CONFIGNAME")) {
						key = searchParam.getString("CONFIGNAME");
					}
					if(!searchParam.isNull("SEQ")) {
						key += searchParam.getString("SEQ");
					}
					MutiUtil.MinusMutiController(key);
				} catch (Exception e2) {
					LogUtil.printLog("线程处理文件出错E2："+e2.getMessage(), Level.ERROR);
				}
				
			}
		}
		list = new ArrayList();
	}
	
	
	
	private  HashMap initElentment() {
		HashMap datas = new HashMap();
		Element element = null;
		datas.put("Element0", element);
		datas.put("Element1", element);
		datas.put("Element2", element);
		datas.put("Element3", element);
		datas.put("Element4", element);
		datas.put("Element5", element);
		datas.put("Element6", element);
		datas.put("Element7", element);
		datas.put("Element8", element);
		datas.put("Element9", element);
		datas.put("Element10", element);
		datas.put("Element11", element);
		datas.put("Element12", element);
		datas.put("Element13", element);
		datas.put("Element14", element);
		datas.put("Element15", element);
		datas.put("Element16", element);
		datas.put("Element17", element);
		datas.put("Element18", element);
		datas.put("Element19", element);
		datas.put("Element20", element);
		return datas;
	}
	
}
