package com.easy.entity;

import java.io.Serializable;

public class ClusterBean implements Serializable{
	private static final long serialVersionUID = 1564017533219132801L;
	
	String QuartzName;//定时任务名称
	String MainPcName;//集群监听主机名
	String MainPcPort;//集群监听主机端口
	
//	String ClusterType;//集群类型:DBToXml、XmlToMQ、MQToXml、XmlToDB等
	String ClusterIP;//集群IP
	String ClusterPort;//集群端口
	String ClusterAppName;//集群实例名称
	
	public String getQuartzName() {
		return QuartzName;
	}
	public void setQuartzName(String quartzName) {
		QuartzName = quartzName;
	}
	public String getMainPcName() {
		return MainPcName;
	}
	public void setMainPcName(String mainPcName) {
		MainPcName = mainPcName;
	}
	public String getMainPcPort() {
		return MainPcPort;
	}
	public void setMainPcPort(String mainPcPort) {
		MainPcPort = mainPcPort;
	}
	public String getClusterIP() {
		return ClusterIP;
	}
	public void setClusterIP(String clusterIP) {
		ClusterIP = clusterIP;
	}
	public String getClusterPort() {
		return ClusterPort;
	}
	public void setClusterPort(String clusterPort) {
		ClusterPort = clusterPort;
	}
	public String getClusterAppName() {
		return ClusterAppName;
	}
	public void setClusterAppName(String clusterAppName) {
		ClusterAppName = clusterAppName;
	}
}
