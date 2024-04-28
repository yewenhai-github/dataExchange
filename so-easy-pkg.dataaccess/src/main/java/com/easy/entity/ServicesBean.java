package com.easy.entity;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;

public class ServicesBean implements Serializable,Cloneable{
	private static final long serialVersionUID = 1564067533219132801L;
	
	/*********公共属性**************/
	String indx;
	String serviceMode;//交换框架支持的功能分类
	String aesKey;//报文加密密钥
	String threadCount;//线程数
	String searchId;
	String classInvoke;//直接调用反射类
	String preMethodInvoke;//报文操作前反射方法类地址
	String rootMethodInvoke;//主表新增前反射方法类地址
	String childMethodInvoke;//子表新增前反射方法类地址
	String rootNames;//节点，第一个为主节点，后续为子节点
	String dbTableNames;//对应数据库表，第一个为主表，后续的为子表
	String mainTableName;//数据库主表表名
	String systemFlag;//系统标识
	String encoding;//字符集编码 GBK、UTF-8
	String indxName;//主表主键
	String pindxName;//子表外键，第1个逗号后为子表的主键
	String cindxName;//子表主键
	String serialName;//流水号字段名称
	String serialNo;//流水号
	String indxNameValue;
	String updateSql;
	String messageHeadSql;//报文生成时，MessageHead中列值取值不规则，则可从此配置的SQL中取得，如：select exs_message_source,exs_message_dest from TableName where serialName = #serialNo#（serialNo为主表中的字段名称）
	String deleteChildFlag;
	boolean existsReturn;//Insert时配置1，表示不删除重新增，Update时配置1，表示不删除子表
	String sourcePath;
	String targetSourcePath;
	String targetPath;
	boolean targetPathHourFlag = false;
	String errorPath;
	String tempPath;
	String receivedPath;
	String hitPath;
	String passPath;
	String logPath;//日志表记录使用的变量
	String childFolder;
	String partPath;
	String dataType;//数据类型，ComXml、AnyXml
	String serviceType;//服务类型：空或1=EDI模式，2=接口表模式，默认EDI模式（使用索引表exs_handle_sender）
	int blobProcess;//数据库Blob字段处理，1：base64加码，2base64解码
	String partId;//分区Id，配置分离ID
	boolean testFlag;//测试标识
	boolean zipFlag;//数据压缩标识
//	Hashtable<String, Object> envDatas;//前端传入的请求参数集合
	String dataFilterReg;//数据过滤条件
	String accessType;//接入方式，OpenApi
	int subfolderNo;//子文件夹数量
	int mqSubNo;//子MQ数量
	/*********Xml报文属性**************/
	String rootDefault;//默认的报文头构造
	String messageId;//报文ID
	String messageType;//报文类型
	String messageSource;//来源代码
	String messageDest;//目标代码
	String messageSourceName;//来源名称
	String messageDestName;//目标名称
	String receiptType;//文件回执类型
	String receiptBizType;//文件业务回执类型
	String xmlRoot;
	String xmlDocument;
	String xmlHead;
	String xmlBody;
	String fileName;
	String fileNameReg;
	String xmlData;
	String jsonData;
	String resquestType;//请求类别：Resquest、Response
	
	/*********业务属性**************/
	String techRegCode;//企业组织机构代码
	String signData;//密钥
	String bizNo;
	String ieFlag;
	String bizType;

	/*********信城通研发接口对接*************/
	String itowNetReportType;//EDI报文类别
	String sourceId;
	String sourceAppId;
	String destAppId;
	String sourceDomainId;
	String destinationDomainId;
	
	/**************自动化引擎检查点*****************/
	String pointCode;
	String fieldPointCode;
	String mappingPointCode;
	String rulePointCode;
	
	/**************交换传输属性*****************/
	String mqType;//MQ类型，ActiveMQ、IBMMQ
	String sourceMq;
	String targetMq;
	String activeMqMode;
	String consumerUser;
    String consumerPwd;
	String consumerUrl;
	String producerUser;
    String producerPwd;
	String producerUrl;
	String url;
	String clientIP;
	String responseMessage;
	String methodName;
	String serverName;
	long limitMillis; 
	String quartzFlag;//1 启动定时模式
	boolean logFlag = false;
	String senderIndx;//发送任务表的indx
	
	/**************数据库*****************/
	String dbType;
	String dbDriverUrl;
	String dbUser;
	String dbPassword;
	
	/**************集合*****************/
	List<ClusterBean> clusterList;
	List<ServicesBean> allBeanList;
	List<HashMap> listSql;
	HashMap<String,String> mapSql;
	HashMap<String,String[]> mappingColumns;
	JSONObject searchParam;
	Map customerMap;
	List customerList;
	File file;
	Datas datas;//自定义的json对象
	HashMap tempMap;//自定义的临时map对象
	public String getSenderIndx() {
		return senderIndx;
	}
	public void setSenderIndx(String senderIndx) {
		this.senderIndx = senderIndx;
	}
	public String getQuartzFlag() {
		return quartzFlag;
	}
	public void setQuartzFlag(String quartzFlag) {
		this.quartzFlag = quartzFlag;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAesKey() {
		return aesKey;
	}
	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}
	public String getIndx() {
		return indx;
	}
	public void setIndx(String indx) {
		this.indx = indx;
	}
	public String getServiceMode() {
		return serviceMode;
	}
	public void setServiceMode(String serviceMode) {
		this.serviceMode = serviceMode;
	}
	public String getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}
	public List<ServicesBean> getAllBeanList() {
		return allBeanList;
	}
	public void setAllBeanList(List<ServicesBean> allBeanList) {
		this.allBeanList = allBeanList;
	}
	public List<ClusterBean> getClusterList() {
		return clusterList;
	}
	public void setClusterList(List<ClusterBean> clusterList) {
		this.clusterList = clusterList;
	}
	public String getPartPath() {
		return partPath;
	}
	public void setPartPath(String partPath) {
		this.partPath = partPath;
	}
	public String getChildFolder() {
		return childFolder;
	}
	public void setChildFolder(String childFolder) {
		this.childFolder = childFolder;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getTargetSourcePath() {
		return targetSourcePath;
	}
	public void setTargetSourcePath(String targetSourcePath) {
		this.targetSourcePath = targetSourcePath;
	}
	public String getTargetPath() {
		return targetPath;
	}
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	public String getReceivedPath() {
		return receivedPath;
	}
	public void setReceivedPath(String receivedPath) {
		this.receivedPath = receivedPath;
	}
	public String getTempPath() {
		return tempPath;
	}
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	public String getErrorPath() {
		return errorPath;
	}
	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}
	public String getHitPath() {
		return hitPath;
	}
	public void setHitPath(String hitPath) {
		this.hitPath = hitPath;
	}
	public String getPassPath() {
		return passPath;
	}
	public void setPassPath(String passPath) {
		this.passPath = passPath;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getSourceMq() {
		return sourceMq;
	}
	public void setSourceMq(String sourceMQ) {
		this.sourceMq = sourceMQ;
	}
	public String getTargetMq() {
		return targetMq;
	}
	public void setTargetMq(String targetMq) {
		this.targetMq = targetMq;
	}
	public String getConsumerUser() {
		return consumerUser;
	}
	public void setConsumerUser(String consumerUser) {
		this.consumerUser = consumerUser;
	}
	public String getConsumerPwd() {
		return consumerPwd;
	}
	public void setConsumerPwd(String consumerPwd) {
		this.consumerPwd = consumerPwd;
	}
	public String getConsumerUrl() {
		return consumerUrl;
	}
	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}
	public String getProducerUser() {
		return producerUser;
	}
	public void setProducerUser(String producerUser) {
		this.producerUser = producerUser;
	}
	public String getProducerPwd() {
		return this.producerPwd;
	}
	public void setProducerPwd(String producerPwd) {
		this.producerPwd = producerPwd;
	}
	public String getProducerUrl() {
		return this.producerUrl;
	}
	public void setProducerUrl(String producerUrl) {
		this.producerUrl = producerUrl;
	}
	public String getActiveMqMode() {
		return activeMqMode;
	}
	public void setActiveMqMode(String activeMQMode) {
		this.activeMqMode = activeMQMode;
	}
	public String getFieldPointCode() {
		return fieldPointCode;
	}
	public void setFieldPointCode(String fieldPointCode) {
		this.fieldPointCode = fieldPointCode;
	}
	public String getMappingPointCode() {
		return mappingPointCode;
	}
	public void setMappingPointCode(String mappingPointCode) {
		this.mappingPointCode = mappingPointCode;
	}
	public String getRulePointCode() {
		return rulePointCode;
	}
	public void setRulePointCode(String rulePointCode) {
		this.rulePointCode = rulePointCode;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	public JSONObject getSearchParam() {
		return searchParam;
	}
	public void setSearchParam(JSONObject searchParam) {
		this.searchParam = searchParam;
	}
	public String getRootNames() {
		return rootNames;
	}
	public void setRootNames(String rootNames) {
		this.rootNames = rootNames;
	}
	public String getRootDefault() {
		return rootDefault;
	}
	public void setRootDefault(String rootDefault) {
		this.rootDefault = rootDefault;
	}
	public String getDbTableNames() {
		return dbTableNames;
	}
	public void setDbTableNames(String dbTableNames) {
		this.dbTableNames = dbTableNames;
	}
	public String getMainTableName() {
		return mainTableName;
	}
	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}
	public String getXmlDocument() {
		return xmlDocument;
	}
	public void setXmlDocument(String xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	public String getXmlHead() {
		return xmlHead;
	}
	public void setXmlHead(String xmlHead) {
		this.xmlHead = xmlHead;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileNameReg() {
		return fileNameReg;
	}
	public void setFileNameReg(String fileNameReg) {
		this.fileNameReg = fileNameReg;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getEncoding() {
		if(SysUtility.isEmpty(encoding)){
			return "UTF-8";
		}
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getXmlData() {
		return xmlData;
	}
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
	public String getJsonData() {
		return jsonData;
	}
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	public String getIndxName() {
		if(SysUtility.isEmpty(indxName)){
			return "Indx";
		}
		return indxName;
	}
	public void setIndxName(String indxName) {
		this.indxName = indxName;
	}
	public String getCindxName() {
		if(SysUtility.isEmpty(cindxName)){
			return getIndxName();
		}
		return cindxName;
	}
	public void setCindxName(String cindxName) {
		this.cindxName = cindxName;
	}
	public String getPindxName() {
		if(SysUtility.isEmpty(pindxName)){
			return "P_Indx";
		}
		return this.pindxName;
	}
	public void setPindxName(String pIndxName) {
		pindxName = pIndxName;
	}
	public String getSerialName() {
		return serialName;
	}
	public void setSerialName(String serialName) {
		this.serialName = serialName;
	}
	public String getPointCode() {
		return pointCode;
	}
	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}
	public String getUpdateSql() {
		return updateSql;
	}
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}
	public String getMessageHeadSql() {
		return messageHeadSql;
	}
	public void setMessageHeadSql(String messageHeadSql) {
		this.messageHeadSql = messageHeadSql;
	}
	public boolean isExistsReturn() {
		return existsReturn;
	}
	public void setExistsReturn(boolean existsReturn) {
		this.existsReturn = existsReturn;
	}
	public String getIndxNameValue() {
		return indxNameValue;
	}
	public void setIndxNameValue(String indxNameValue) {
		this.indxNameValue = indxNameValue;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getSystemFlag() {
		return systemFlag;
	}
	public void setSystemFlag(String systemFlag) {
		this.systemFlag = systemFlag;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(String messageSource) {
		this.messageSource = messageSource;
	}
	public String getMessageDest() {
		return messageDest;
	}
	public void setMessageDest(String messageDest) {
		this.messageDest = messageDest;
	}
	public String getMessageSourceName() {
		return messageSourceName;
	}
	public void setMessageSourceName(String messageSourceName) {
		this.messageSourceName = messageSourceName;
	}
	public String getMessageDestName() {
		return messageDestName;
	}
	public void setMessageDestName(String messageDestName) {
		this.messageDestName = messageDestName;
	}
	public String getTechRegCode() {
		return techRegCode;
	}
	public void setTechRegCode(String techRegCode) {
		this.techRegCode = techRegCode;
	}
	public String getSignData() {
		return signData;
	}
	public void setSignData(String signData) {
		this.signData = signData;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceAppId() {
		return sourceAppId;
	}
	public void setSourceAppId(String sourceAppId) {
		this.sourceAppId = sourceAppId;
	}
	public String getDestAppId() {
		return destAppId;
	}
	public void setDestAppId(String destAppId) {
		this.destAppId = destAppId;
	}
	public String getSourceDomainId() {
		return sourceDomainId;
	}
	public void setSourceDomainId(String sourceDomainId) {
		this.sourceDomainId = sourceDomainId;
	}
	public String getDestinationDomainId() {
		return destinationDomainId;
	}
	public void setDestinationDomainId(String destinationDomainId) {
		this.destinationDomainId = destinationDomainId;
	}
	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}
	public String getReceiptBizType() {
		return receiptBizType;
	}
	public void setReceiptBizType(String receiptBizType) {
		this.receiptBizType = receiptBizType;
	}
	public List<HashMap> getListSql() {
		return listSql;
	}
	public void setListSql(List<HashMap> listSql) {
		this.listSql = listSql;
	}
	public HashMap<String,String> getMapSql() {
		return mapSql;
	}
	public void setMapSql(HashMap<String,String> mapSql) {
		this.mapSql = mapSql;
	}
	public HashMap<String, String[]> getMappingColumns() {
		if(SysUtility.isEmpty(mappingColumns)){
			mappingColumns = new HashMap<String, String[]>();
		}
		return mappingColumns;
	}
	public void setMappingColumns(HashMap<String, String[]> mappingColumns) {
		this.mappingColumns = mappingColumns;
	}
	public boolean getTargetPathHourFlag() {
		return targetPathHourFlag;
	}
	public void setTargetPathHourFlag(boolean targetPathHourFlag) {
		this.targetPathHourFlag = targetPathHourFlag;
	}
	public String getClassInvoke() {
		return classInvoke;
	}
	public void setClassInvoke(String classInvoke) {
		this.classInvoke = classInvoke;
	}
	public String getPreMethodInvoke() {
		return preMethodInvoke;
	}
	public void setPreMethodInvoke(String preMethodInvoke) {
		this.preMethodInvoke = preMethodInvoke;
	}
	public String getRootMethodInvoke() {
		return rootMethodInvoke;
	}
	public void setRootMethodInvoke(String rootMethodInvoke) {
		this.rootMethodInvoke = rootMethodInvoke;
	}
	public String getChildMethodInvoke() {
		return childMethodInvoke;
	}
	public void setChildMethodInvoke(String childMethodInvoke) {
		this.childMethodInvoke = childMethodInvoke;
	}
	public String getXmlRoot() {
		return xmlRoot;
	}
	public void setXmlRoot(String xmlRoot) {
		this.xmlRoot = xmlRoot;
	}
	public String getXmlBody() {
		return xmlBody;
	}
	public void setXmlBody(String xmlBody) {
		this.xmlBody = xmlBody;
	}
	public void SetItowNetReportType(String itowNetReportType){
		this.itowNetReportType = itowNetReportType;
	}
	public String GetItowNetReportType(){
		return itowNetReportType ;
	}
	public boolean getLogFlag() {
		return logFlag;
	}
	public void setLogFlag(boolean logFlag) {
		this.logFlag = logFlag;
	}
	public String getBizNo() {
		return bizNo;
	}
	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}
	public String getIeFlag() {
		return ieFlag;
	}
	public void setIeFlag(String ieFlag) {
		this.ieFlag = ieFlag;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getMqType() {
		return mqType;
	}
	public void setMqType(String mqType) {
		this.mqType = mqType;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId; 
	}
	public String getMessageId() {
		return messageId;
	}
	public int getBlobProcess() {
		return blobProcess;
	}
	public void setBlobProcess(int blobProcess) {
		this.blobProcess = blobProcess;
	}
	public String getPartId() {
		return partId;
	}
	public void setPartId(String partId) {
		this.partId = partId;
	}
	public boolean isTestFlag() {
		return testFlag;
	}
	public void setTestFlag(boolean testFlag) {
		this.testFlag = testFlag;
	}
	public boolean isZipFlag() {
		return zipFlag;
	}
	public void setZipFlag(boolean zipFlag) {
		this.zipFlag = zipFlag;
	}
	/**
	 * @return the dataFilterReg
	 */
	public String getDataFilterReg() {
		return dataFilterReg;
	}
	/**
	 * @param dataFilterReg the dataFilterReg to set
	 */
	public void setDataFilterReg(String dataFilterReg) {
		this.dataFilterReg = dataFilterReg;
	}
	/**
	 * @return the accessType
	 */
	public String getAccessType() {
		return accessType;
	}
	/**
	 * @param accessType the accessType to set
	 */
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	/**
	 * @return the subfolderNo
	 */
	public int getSubfolderNo() {
		return subfolderNo;
	}
	/**
	 * @param subfolderNo the subfolderNo to set
	 */
	public void setSubfolderNo(int subfolderNo) {
		this.subfolderNo = subfolderNo;
	}
	public int getMqSubNo() {
		return mqSubNo;
	}
	public void setMqSubNo(int mqSubNo) {
		this.mqSubNo = mqSubNo;
	}
	public String getResquestType() {
		return resquestType;
	}
	public void setResquestType(String resquestType) {
		this.resquestType = resquestType;
	}
	public void setDeleteChildFlag(String deleteChildFlag) {
		this.deleteChildFlag=deleteChildFlag;
	}
	public String getDeleteChildFlag() {
		return deleteChildFlag;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public long getLimitMillis() {
		return limitMillis;
	}
	public void setLimitMillis(long LimitMillis) {
		this.limitMillis = LimitMillis;
	}
	public Map getCustomerMap() {
		return customerMap;
	}
	public void setCustomerMap(Map customerMap) {
		this.customerMap = customerMap;
	}
	public List getCustomerList() {
		return customerList;
	}
	public void setCustomerList(List customerList) {
		this.customerList = customerList;
	}
	public Datas getDatas() {
		return datas;
	}
	public void setDatas(Datas datas) {
		this.datas = datas;
	}
	public HashMap getTempMap() {
		return tempMap;
	}
	public void setTempMap(HashMap tempMap) {
		this.tempMap = tempMap;
	}
	public String getItowNetReportType() {
		return itowNetReportType;
	}
	public void setItowNetReportType(String itowNetReportType) {
		this.itowNetReportType = itowNetReportType;
	}
	
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getDbDriverUrl() {
		return dbDriverUrl;
	}
	public void setDbDriverUrl(String dbDriverUrl) {
		this.dbDriverUrl = dbDriverUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
