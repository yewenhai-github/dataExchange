package com.easy.app.job;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.app.utility.ExsUtility;
import com.easy.app.utility.MutiUtility;
import com.easy.entity.ClusterBean;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.thread.JobDetail;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class SockitSendJob extends JobDetail{
	HashMap<String, Object> fileMap = new HashMap<String, Object>();
	public SockitSendJob(HashMap paramData){
		this.fileMap = paramData;
	}
	
	public void run() throws LegendException{
		List list = (List)fileMap.get("List");
		ServicesBean bean = (ServicesBean)fileMap.get("ServicesBean");
		ClusterBean cbean = (ClusterBean)fileMap.get("ClusterBean");
		IDataAccess DataAccess = (IDataAccess)fileMap.get("DataAccess");
		
		DatagramSocket sendSocket = null;
		InetAddress socketAddress = null;
		try {
			socketAddress = InetAddress.getByName(cbean.getMainPcName());
		} catch (UnknownHostException e) {
			LogUtil.printLog("InetAddress.getByName("+cbean.getMainPcName()+")出错："+e.getMessage(), Level.ERROR);
			return;
		}
		if(SysUtility.isEmpty(socketAddress)){
			LogUtil.printLog("无法识别集群机器："+cbean.getMainPcName(), Level.ERROR);
			return;
		}
		
		int socketPort = Integer.parseInt(cbean.getMainPcPort());
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[])list.get(i);
			File file = (File)obj[1];
			try {
				bean.setSourcePath((String)obj[0]);
				bean.setFileName(file.getName());
				bean.setFile(file);
				if(SysUtility.isEmpty(sendSocket)){
					sendSocket = new DatagramSocket();//创建发送方的套接字，IP默认为本地，端口号随机   
				}
				byte[] buf = SysUtility.ObjectToByte(bean);//由于数据报的数据是以字符数组传的形式存储的，所以传转数据   
				DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, socketAddress,socketPort);//创建发送类型的数据报：
		        sendPacket.setAddress(socketAddress);
		        sendPacket.setPort(socketPort);
		        sendSocket.send(sendPacket);//通过套接字发送数据： 
				
		        /************创建发送方收到的参数对象*******************/
	            byte[] getBuf = new byte[1024];//确定接受反馈数据的缓冲存储器，即存储数据的字节数组   
	            DatagramPacket getPacket = new DatagramPacket(getBuf, getBuf.length);//创建接受类型的数据报   
	            sendSocket.receive(getPacket);//通过套接字接受数据  
	            String rt = new String(getBuf, 0, getPacket.getLength());//解析反馈的消息，并打印
	            if(SysUtility.isEmpty(rt) || rt.indexOf("0") >= 0){//处理失败
	            	ExsUtility.AddLogFailDesc(DataAccess, bean, rt);
	            	FileUtility.copyFile(bean.getSourcePath()+File.separator+bean.getFileName(), bean.getErrorPath()+File.separator+bean.getFileName());
	            	FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
	            }else if("1".equals(rt)){//处理成功
	            	ExsUtility.AddLogSuccess(DataAccess, bean, "");
	            	FileUtility.copyFile(bean.getSourcePath()+File.separator+bean.getFileName(), bean.getTargetPath()+File.separator+bean.getFileName());
	            	FileUtility.deleteFile(bean.getSourcePath(), bean.getFileName());
	            }
			} catch (Exception e) {
				LogUtil.printLog("线程处理文件出错："+e.getMessage(), Level.ERROR);
			} finally{
				DataAccess.ComitTrans();
				MutiUtility.MinusMutiController(bean.getXmlDocument()+bean.getMessageType());
			}
		}
		list = new ArrayList();
	}
}
