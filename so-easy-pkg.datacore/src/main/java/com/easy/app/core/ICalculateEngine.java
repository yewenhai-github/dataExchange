package com.easy.app.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;

public interface ICalculateEngine {

	public void DBToXml(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDB(ServicesBean bean, IDataAccess DataAccess, String messageType) throws Exception;

	public void UpdateToDBForXml(IDataAccess DataAccess, ServicesBean bean) throws Exception;

	public void UpdateToDBForDB(IDataAccess DataAccess, ServicesBean bean) throws Exception;

	public void XmlToDBForAny(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForLocal(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForActiveMQ(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForIBMMQ(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForRabbitMQ(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForHTTP(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToDBForFTP(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void ToXmlFromAny(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void DBToXmlToDB(String ServiceMode, final String MessageType, final String PartId, String XmlData,
			IDataAccess DataAccess) throws Exception;

	public String SaveToDBForAny(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public HashMap<String, String> SaveToDBAnyXmlCore(ServicesBean bean, InputStream is, IDataAccess DataAccess)
			throws Exception;

	public void SetBeanField(ServicesBean bean, List listSql) throws LegendException;

	public boolean ExistsDataDB(String uuid,String[] RootNames, Datas datas, String[] DBTableNames, String[] IndxNames)
			throws LegendException;

	public void DeleteMainDB(String uuid,ServicesBean bean, String MainTableName) throws LegendException;

	public void DeleteChildDB(String uuid,String[] RootNames, List listSql, Datas datas, HashMap hmSourceData,
			String[] DBTableNames, String[] IndxNames) throws LegendException;

	public void UpdateToDBForAny(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void ElecDocsForAny(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void UpdateToDBForDB(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void UpdateReceived(IDataAccess DataAccess, HashMap data) throws LegendException;

	public void UpdateReceived(ServicesBean bean, IDataAccess DataAccess, String messageType, JSONObject data)
			throws LegendException;

	public HashMap getDocumentRequestDataMap(InputStream is) throws LegendException;

	public HashMap getRequestHeadMap(HashMap documentData, String xmlDocumentName, String xmlHeadName)
			throws LegendException;

	public HashMap getDocumentResponseDataMap(InputStream is) throws LegendException;

	public HashMap getRevevieHeadMap(InputStream is, String xmlDocumentName, String xmlHeadName) throws LegendException;

	public HashMap getSaveTableMap(HashMap documentData, String xmlDocumentName, String xmlHeadName)
			throws JSONException;

	public String createRequestXml(HashMap HeadMap, ServicesBean bean, String xmlDocumentName, String xmlHeadName,
			String TARGET_PATH);

	public HashMap getXmlRootMap(HashMap HeadMap, String xmlDocumentName, String xmlHeadName);

	public HashMap getMessageHead(ServicesBean bean, String xmlDocumentName);

	public void GenerateRequestForSocket(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void DBToXmlForLocal(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void ElecDocsForXmlDefault(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void ElecDocsForXml(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public boolean DeleteExistData(IDataAccess DataAccess, ServicesBean bean, JSONObject root) throws Exception;

	public boolean ExecUpdateData(IDataAccess DataAccess, String indx, String UpdateSQL, JSONObject root) throws LegendException;

	public void XmlToFloderForMerge(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToFloderForSplit(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void LocalXmlToSortLocal(ServicesBean bean, IDataAccess DataAccess) throws Exception;

	public void XmlToSortLocal(ServicesBean bean, IDataAccess DataAccess, File file) throws Exception;

	public void SaveToDBForSocket(IDataAccess DataAccess) throws Exception;

	/**
	 * @return "" 文件不是以.temp、.exs结尾 A 文件以.temp、.exs结尾，时间未超过2小时 B
	 *         文件以.temp、.exs结尾，时间超过2小时 C
	 *         文件以不是.temp、.exs结尾，且文件第一行或第二行没有RequestMessage或ResponseMessage的关键字
	 */
	public boolean ExsFileFilter(File file, ServicesBean bean, IDataAccess DataAccess) throws LegendException;

	public Map GetAnyXmlRootMap(ServicesBean bean, List MainDatas, HashMap childsMap)
			throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, UnsupportedEncodingException;

	public String GetAnyJsonData(ServicesBean bean, List MainDatas, HashMap childsMap)
			throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, UnsupportedEncodingException;

	public String GetAnyXmlData(ServicesBean bean, List MainDatas, HashMap childsMap)
			throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, UnsupportedEncodingException;

	public Map GetSenderMap(ServicesBean bean, HashMap MainData) throws LegendException, JSONException;

	public void DeleteMainData(ServicesBean bean, HashMap SQLMap, HashMap MainData)
			throws LegendException, JSONException;

	public void DeleteChildData(ServicesBean bean, String DeleteSql, JSONObject params)
			throws LegendException, JSONException;

	public boolean DeleteData(String DeleteSQL, Object obj) throws LegendException, JSONException;

	public HashMap CacheChildData(ServicesBean bean, IDataAccess DataAccess, List MainDatas) throws LegendException;

	public List GetMainDatas(ServicesBean bean, IDataAccess DataAccess, HashMap childsMap) throws Exception;

	public String XmlToDatas(ServicesBean bean, Datas datas, Map hmSourceData)
			throws LegendException, IOException, SecurityException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException;

	public void setDBToXmlMap(ServicesBean bean, final String Indx, Datas datas) throws LegendException;

	public void setDBToXmlMap(ServicesBean bean, final String Indx, Datas datas, List SQLList) throws LegendException;

	public void InitExsDbtoxmlSql(ServicesBean bean, HashMap temp, StringBuffer strRootName, String TableName,
			String IndxName, String PIndxName, String PIndxNameValue);

	public String getMainChildSql_1(ServicesBean bean, HashMap temp, int j);

	public String getMainChildSql_2(ServicesBean bean, HashMap temp, StringBuffer strRootName);

	public String getChildSql_1(HashMap temp);

	public String getTableMainSql_1(ServicesBean bean, String TableName, String IndxName);

	public String getTableMainSql_2(ServicesBean bean, String TableName, String IndxName);

	public String getTableChildSql_1(String TableName, String PIndxName, String PIndxNameValue);

	public String getOpenApiMainSql(ServicesBean bean, Datas datas, HashMap temp);

	public List getLocalDBToXmlConfig();

	public List getLocalXmlToDBConfig() throws LegendException;

	// exs_cluster_app(indx,ip_address,app_context,app_name,status,create_time,modify_time)
	// 每个app服务轮询当前表，0、app_context获取到的数据，ip_address如果与当前机器不相同，不启动当前应用定时。
	// 1、如果不存在则创建，2、如果存在则更新修改时间，3、如果当前时间超过修改时间10分钟，则自动删除此类数据。

	// exs_cluster_task(indx,exs_type,app_context,source_path,file_name,process_flag,create_time)
	//
	public void XmlToClusterTask(ServicesBean bean, IDataAccess DataAccess) throws LegendException, IOException;
}
