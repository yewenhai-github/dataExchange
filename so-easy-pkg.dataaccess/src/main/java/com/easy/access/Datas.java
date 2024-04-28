package com.easy.access;

import java.io.Writer;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class Datas extends JSONObject {
	
	public void MapToDatas(String XmlName,Map rootMap){
		MapToDatas(XmlName, rootMap, new HashMap());
	}
	
	public void MapToDatas(String XmlName,List rootList,Map mapping){
		HashMap rootMap = new HashMap();
		rootMap.put(XmlName, rootList);
		MapToDatas(XmlName, rootMap, mapping);
	}
	
	public void MapToDatas(String XmlName,Map rootMap,Map mapping){
		try {
			Set mapSet = rootMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
			    Object key = entry.getKey();
			    Object value = entry.getValue();
				if(XmlName.equals(key)){
					if (value instanceof Map) {
						HashMap map = (HashMap)value;
						JSONObject row = new JSONObject();
						Set mapSet2 = map.entrySet();
						for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
							Entry entry2 = (Entry)it2.next();
					        Object key2 = entry2.getKey();
					        Object value2 = entry2.getValue();
					        if(value2 instanceof String) {
					        	if(mapping.containsKey(key2.toString())){
					        		row.put((String)mapping.get(key2.toString()), value2);
					        	}else{
					        		row.put(key2.toString(), value2);
					        	}
					        }
						}
						if(this.has(key.toString())){
							JSONArray rows = (JSONArray)this.get(key.toString());
							rows.put(row);
						}else{
							JSONArray rows = new JSONArray();
							rows.put(row);
							this.put(key.toString(), rows);
						}
					}else if (value instanceof List) {
						List list = (List)value;
						for (int i = 0; i < list.size(); i++) {
							HashMap map = (HashMap)list.get(i);
							JSONObject row = new JSONObject();
							Set mapSet2 = map.entrySet();
							for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
								Entry entry2 = (Entry)it2.next();
						        Object key2 = entry2.getKey();
						        Object value2 = entry2.getValue();
						        if(value2 instanceof String) {
						        	if(mapping.containsKey(key2.toString())){
						        		row.put((String)mapping.get(key2.toString()), value2);
						        	}else{
						        		row.put(key2.toString(), value2);
						        	}
						        }
							}
							if(this.has(key.toString())){
								JSONArray rows = (JSONArray)this.get(key.toString());
								rows.put(row);
							}else{
								JSONArray rows = new JSONArray();
								rows.put(row);
								this.put(key.toString(), rows);
							}
						}
					}
				}else if(value instanceof Map){
					MapToDatas(XmlName, (Map)value, mapping);
				}else if(value instanceof List){
					List list = (List)value;
					for (int i = 0; i < list.size(); i++) {
						Object obj = (Object)list.get(i);
						if(obj instanceof Map){
							HashMap map = (HashMap)obj;
							MapToDatas(XmlName, map, mapping);
						}else{
							LogUtil.printLog("不支持的类型：XmlName="+XmlName+"key="+key+",value="+obj, Level.INFO);
						}
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public void AddAllDatas(Datas datas){
		JSONObject thisRows = this.getDataJSON();
		
		JSONObject rows = datas.getDataJSON();
		Iterator<?> keys = rows.keys();
		while(keys.hasNext()){
			try {
				String key = keys.next().toString();
				if(rows.get(key) instanceof JSONArray){
					JSONArray tableRows = rows.getJSONArray(key);//
					
					if(thisRows.has(key) && SysUtility.isNotEmpty(thisRows.getJSONArray(key))){
						JSONArray thisTableRows = thisRows.getJSONArray(key);
						for (int i = 0; i < tableRows.length(); i++) {
							thisTableRows.put(tableRows.getJSONObject(i));
						}
					}else{
						if(!thisRows.has(key)){
							thisRows.put(key, new JSONArray());
						}
						JSONArray thisTableRows = thisRows.getJSONArray(key);
						if(SysUtility.isEmpty(thisTableRows)){
							thisTableRows = tableRows;
						}
					}
				}
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}
	}
	
	/*public Datas(String TableName, ResultSet rs) throws LegendException{
		if(SysUtility.isEmpty(TableName)){
			try {
				this.setData(ResToString(TableName, rs));
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		}else{
			this.setDataJSON(ResToJSON(TableName, rs));
		}
	}
	
	public void setData(String data) throws JSONException {
		this = new Datas(data);
	}
	*
	*/

	
	
	/**返回内存表的行数
	 * @param TableName 查找的内存表名
	 * @return 数据行数
	 * */
	public int GetTableRows(String TableName) throws LegendException{
		int rt = 0;
		if(this == null || SysUtility.isEmpty(TableName) || !this.has(TableName)){
    		return rt;
    	}
		try {
			TableName = SysUtility.ParseTableName(this, TableName);
			if(this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				if(rows != null){
					rt = rows.length();
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}		
		return rt;
	}
	
	/**获取dataJSON的字段值
	 * @param TableField 表.字段代替分开的表字段参数
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public String GetTableValue(String TableField) throws LegendException{
    	String[] TF = TableField.split("\\.");
		if (TF.length > 1){
			return GetTableValue(TF[0], TF[1], 0);
		}
        return "";
    }
	
    /**获取dataJSON的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
    public String GetTableValue(String TableName, String FieldName) throws LegendException{
        return GetTableValue(TableName, FieldName, 0);
    }
    
    /**获取dataJSON的字段值
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param RowIndex 数据所在行
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public String GetTableValue(String TableName, String FieldName,int RowIndex) throws LegendException{
		String rt = "";
        try {
        	TableName = SysUtility.ParseTableName(this, TableName);
        	if(!this.has(TableName) || this.get(TableName) == null){
    			return "";
    		}
        	JSONArray rows = (JSONArray)this.get(TableName);
        	if(rows.length() == 0){
        		return "";
        	}
    		JSONObject row = rows.getJSONObject(RowIndex);
    		Iterator<?> keys = row.keys();
    		while(keys.hasNext()){
				String key = keys.next().toString();
				if(key.equalsIgnoreCase(FieldName)){
					FieldName = key;
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		rt = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(obj);
	            	}else{
	            		rt = obj.toString();
	            	}
	            	break;
				}
    		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	/**获取dataJSON的字段值，返回符合条件的第一条数据
	 * @param TableName 表名
	 * @param FieldName 字段名
	 * @param Filter 过滤条件，取得在条件内的数据，配置(列名=值)
	 * @return 数据字符串（日期强制转换为yyyy-MM-dd HH:mm:ss形式）
	 * */
	public String GetTableValue(String TableName, String FieldName, String Filter) throws LegendException{
		String rt = "";
        try {
        	TableName = SysUtility.ParseTableName(this, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(!this.has(TableName) || this.get(TableName) == null || ft.length < 2){
    			return "";
    		}
        	JSONArray rows = (JSONArray)this.get(TableName);
        	for(int i = 0 ; i < rows.length();i++){
				JSONObject row = rows.getJSONObject(i);
				Iterator<?> keys = row.keys();
				String value = "";
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(filterKey)){
						filterKey = key;
						Object objValue = row.get(filterKey);
						if(objValue instanceof Date){
							value = SysUtility.DataFormatStr((Date)objValue);
						}else{
							value = objValue.toString();
						} 
					}
					if(key.equalsIgnoreCase(FieldName)){
						FieldName = key;
					}
	    		}
	    		if(value.equals(filterValue)){
					Object obj = row.get(FieldName);
	            	if(obj instanceof Date){
	            		return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(obj);
	            	}
	            	return obj.toString();
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
        return rt;
	}
	
	public void SetTable(String TableName, JSONObject row) throws LegendException{
		try {
			if(this.has(TableName)){
				JSONArray rows = (JSONArray)this.get(TableName);
				rows.put(row);
			}else{
				this.put(TableName, row);
			}
		} catch (JSONException e) {
			LogUtil.printLog("SetTable Error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public void SetTable(String TableName, JSONArray rows) throws LegendException{
		try {
			if(this.has(TableName)){
				JSONArray tempRows = (JSONArray)this.get(TableName);
				for (int i = 0; i < rows.length(); i++) {
					tempRows.put((JSONObject)rows.get(i));
				}
			}else{
				this.put(TableName, rows);
			}
		} catch (JSONException e) {
			LogUtil.printLog("SetTable Error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	public JSONObject GetTable(String TableName) throws LegendException{
		return GetTable(TableName,0);
	}
	
	public JSONObject GetTable(String TableName, int RowIndex) throws LegendException{
		JSONObject root = new JSONObject();
		try {
			if(this.has(TableName)){
				root = (JSONObject)((JSONArray)this.get(TableName)).get(RowIndex);//(JSONObject)datas.get(RootName);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTable Error:"+e.getMessage(), Level.ERROR);
		}
		return root;
	}
	
	public JSONArray GetTables(String TableName) throws LegendException{
		JSONArray rows = new JSONArray();
		try {
			if(this.has(TableName)){
				rows = (JSONArray)this.get(TableName);//(JSONObject)datas.get(RootName);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTables Error:"+e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public HashMap GetTableMap(String TableName) throws LegendException{
		return GetTableMap(TableName, 0);
	}
	public HashMap GetTableMap(String TableName, int RowIndex) throws LegendException{
		HashMap rtMap = new HashMap();
		try {
			if(this.has(TableName)){
				JSONObject row = (JSONObject)((JSONArray)this.get(TableName)).get(RowIndex);
				rtMap = SysUtility.JSONObjectToHashMap(row);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTableMap Error:"+e.getMessage(), Level.ERROR);
		}
		return rtMap;
	}
	
	public List GetTableList(String TableName) throws LegendException{
		return GetTableList(TableName, null);
	}
	
	public List GetTableList(String TableName, String Filter) throws LegendException{
		List rtList = new ArrayList();
		try {
			if(this.has(TableName)){
				JSONArray rows = (JSONArray)this.get(TableName);
				
				if(SysUtility.isNotEmpty(Filter)){
					String[] ft = Filter.split("\\=");
			    	String filterKey = ft[0];
			    	String filterValue = ft[1];
			    	
			    	JSONArray tempRows = new JSONArray();
			    	for (int i = 0; i < rows.length(); i++) {
			    		JSONObject row = (JSONObject)rows.get(i);
			    		if(SysUtility.isNotEmpty(row) && filterValue.equals(SysUtility.getJsonField(row, filterKey))){
			    			tempRows.put(row);
			    		}
					}
			    	rows = tempRows;
				}
				rtList = SysUtility.JSONArrayToList(rows);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTableList Error:"+e.getMessage(), Level.ERROR);
		}
		return rtList;
	}
	
	public JSONObject RemoveTable(String TableName, int RowIndex) throws LegendException{
		JSONObject root = new JSONObject();
		try {
			if(this.has(TableName)){
				JSONArray rows = (JSONArray)this.get(TableName);
				rows.remove(RowIndex);
			}
		} catch (JSONException e) {
			LogUtil.printLog("GetTable Error:"+e.getMessage(), Level.ERROR);
		}
		return root;
	}
	
	/**保存数据到数据池中数据表的第0行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @return void
	 * */
	public void SetTableValue(String TableName,String FieldName , Object setValue) throws LegendException{
		SetTableValue(TableName, FieldName, setValue, 0);
	}
	
	/**保存数据到数据池中数据表的指定行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @param RowIndex 指定行
	 * @return void
	 * */
    public void SetTableValue(String TableName, String FieldName, Object setValue, int RowIndex) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(this, TableName);
			if(this.has(TableName) && this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				if(rows != null){
					if(rows.length() > RowIndex){
						JSONObject row = rows.getJSONObject(RowIndex);
						row.put(FieldName, setValue);
						Iterator<?> keys = row.keys();
			    		while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName) && !key.equals(FieldName)){
								row.remove(key);
								return;
							}
			    		}
					}else{
						JSONObject row = new JSONObject();
						row.put(FieldName, setValue);
						rows.put(row);
					}
				}
			}else{
				JSONArray rows = new JSONArray();
				JSONObject row = new JSONObject();
				row.put(FieldName, setValue);
				rows.put(row);
				this.put(TableName, rows);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
    /**保存数据到数据池中数据表按条件过滤后的所有行
	 * @param TableName 保存数据的数据表
	 * @param FieldName 保存数据表的字段
	 * @param setValue 保存数据的值
	 * @param Filter 过滤条件
	 * @return void
	 * */
    public void SetTableValue(String TableName, String FieldName, String setValue, String Filter) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(this, TableName);
        	String[] ft = Filter.split("\\=");
        	String filterKey = ft[0];
        	String filterValue = ft[1];
        	if(!this.has(TableName) || this.get(TableName) == null || ft.length < 2){
    			return;
    		}
        	JSONArray rows = (JSONArray)this.get(TableName);
        	for(int i = 0 ; i < rows.length();i++){
				JSONObject row = rows.getJSONObject(i);
				Iterator<?> keys = row.keys();
				String value = "";
	    		while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(filterKey)){
						filterKey = key;
						Object objValue = row.get(filterKey);
						if(objValue instanceof Date){
							value = SysUtility.DataFormatStr((Date)objValue);
						}else{
							value = objValue.toString();
						} 
					}
					if(key.equalsIgnoreCase(FieldName)){
						FieldName = key;
					}
	    		}
	    		if(value.equals(filterValue)){
					row.put(FieldName, setValue);
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
	
    /**移除数据池中数据表的指定行
	 * @param TableName 移除据的数据表
	 * @param FieldName 移除数据表的字段
	 * @return void
	 * */
    public void RemoveTableValue(String TableName, String FieldName) throws LegendException{
    	RemoveTableValue(TableName, FieldName, 0);
    }
    
    /**移除数据池中数据表的指定行
	 * @param TableName 移除据的数据表
	 * @param FieldName 移除数据表的字段
	 * @param RowIndex 指定行
	 * @return void
	 * */
    public void RemoveTableValue(String TableName, String FieldName, int RowIndex) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(this, TableName);
			if(this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				if(rows != null){
					if(rows.length()>0){
						JSONObject row = rows.getJSONObject(RowIndex);
						Iterator<?> keys = row.keys();
			    		while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName)){
								FieldName = key;
								row.remove(FieldName);
								return;//移除成功后直接返回，跳出循环
							}
			    		}
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
    }
    
	public String getData() {
		return this.toString();
	}

	public JSONObject getDataJSON() {
		return this;
	}

	public void setDataJSON(JSONObject dataJSON) {
		new JSONObject(dataJSON);
	}

	/*********************************构造函数重写*****************************************/
	public Datas() {
		super();
	}

	public Datas(JSONObject arg0, String[] arg1) throws JSONException {
		super(arg0, arg1);
	}

	public Datas(JSONTokener x) throws JSONException {
		super(x);
	}

//	public Datas(Map arg0, boolean arg1) {
//		super(arg0, arg1);
//	}

	public Datas(Map map) {
		super(map);
	}

//	public Datas(Object bean, boolean includeSuperClass) {
//		super(bean, includeSuperClass);
//	}

	public Datas(Object arg0, String[] arg1) {
		super(arg0, arg1);
	}

	public Datas(Object bean) {
		super(bean);
	}

	public Datas(String source) throws JSONException {
		super(source);
	}
	/*********************************构造函数重写*****************************************/
	
	/*********************************普通方法重写*****************************************/
	@Override
	public JSONObject append(String key, Object value) throws JSONException {
		return super.append(key, value);
	}

	@Override
	public Object get(String key) throws JSONException {
		return super.get(key);
	}

	@Override
	public boolean getBoolean(String key) throws JSONException {
		return super.getBoolean(key);
	}

	@Override
	public double getDouble(String arg0) throws JSONException {
		return super.getDouble(arg0);
	}

	@Override
	public int getInt(String key) throws JSONException {
		return super.getInt(key);
	}

	@Override
	public JSONArray getJSONArray(String key) throws JSONException {
		return super.getJSONArray(key);
	}

	@Override
	public JSONObject getJSONObject(String key) throws JSONException {
		return super.getJSONObject(key);
	}

	@Override
	public long getLong(String key) throws JSONException {
		return super.getLong(key);
	}

	@Override
	public String getString(String key) throws JSONException {
		return super.getString(key);
	}

	@Override
	public boolean has(String key) {
		return super.has(key);
	}

	@Override
	public boolean isNull(String key) {
		return super.isNull(key);
	}

	@Override
	public Iterator keys() {
		return super.keys();
	}

	@Override
	public int length() {
		return super.length();
	}

	@Override
	public JSONArray names() {
		return super.names();
	}

	@Override
	public Object opt(String key) {
		return super.opt(key);
	}

	@Override
	public boolean optBoolean(String arg0, boolean arg1) {
		return super.optBoolean(arg0, arg1);
	}

	@Override
	public boolean optBoolean(String key) {
		return super.optBoolean(key);
	}

	@Override
	public double optDouble(String arg0, double arg1) {
		return super.optDouble(arg0, arg1);
	}

	@Override
	public double optDouble(String key) {
		return super.optDouble(key);
	}

	@Override
	public int optInt(String arg0, int arg1) {
		return super.optInt(arg0, arg1);
	}

	@Override
	public int optInt(String key) {
		return super.optInt(key);
	}

	@Override
	public JSONArray optJSONArray(String key) {
		return super.optJSONArray(key);
	}

	@Override
	public JSONObject optJSONObject(String key) {
		return super.optJSONObject(key);
	}

	@Override
	public long optLong(String arg0, long arg1) {
		return super.optLong(arg0, arg1);
	}

	@Override
	public long optLong(String key) {
		return super.optLong(key);
	}

	@Override
	public String optString(String key, String defaultValue) {
		return super.optString(key, defaultValue);
	}

	@Override
	public String optString(String key) {
		return super.optString(key);
	}

	@Override
	public JSONObject put(String key, boolean value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, Collection value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, double value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, int value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, long value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, Map value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject put(String key, Object value) throws JSONException {
		return super.put(key, value);
	}

	@Override
	public JSONObject putOnce(String key, Object value) throws JSONException {
		return super.putOnce(key, value);
	}

	@Override
	public JSONObject putOpt(String key, Object value) throws JSONException {
		return super.putOpt(key, value);
	}

	@Override
	public Object remove(String key) {
		return super.remove(key);
	}

	@Override
	public JSONArray toJSONArray(JSONArray arg0) throws JSONException {
		return super.toJSONArray(arg0);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String toString(int indentFactor) throws JSONException {
		return super.toString(indentFactor);
	}

	@Override
	public Writer write(Writer arg0) throws JSONException {
		return super.write(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	
	public String InsertDB(IDataAccess DataAccess,String TableName,String DBTableName,String KeyField) throws LegendException{
		return InsertDB(DataAccess, SysUtility.getCurrentConnection(), TableName, DBTableName, KeyField);
	}
	/**将数据池中的数据保存到数据库中，有主键的做更新，无主键的做插入
	 * @param TableName 保存数据的数据表
	 * @param DBTableName 数据库表名
	 * @param KeyField 主键
	 * @return 第一条记录的主键
	 * */
	public String InsertDB(IDataAccess DataAccess,Connection conn,String TableName,String DBTableName,String KeyField) throws LegendException{
    	String rt = "-1";
    	try { 
	    	TableName = SysUtility.ParseTableName(this, TableName);
	    	if(null == this || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)||!this.has(TableName)){
	    		return String.valueOf(rt).toString();
	    	} 
			if(this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				if(rows != null && rows.length() > 0){
					DataAccess.Insert(DBTableName, rows,KeyField,conn);
					JSONObject rtObj = rows.getJSONObject(0);
					if(rtObj.has(KeyField)){
						rt = rtObj.getString(KeyField);
					}else if(rtObj.has(KeyField)){
						rt = rtObj.getString(KeyField);
					}
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		if(SysUtility.isEmpty(rt)){
			rt = "-1";
		}
		return String.valueOf(rt).toString();
	}
	
	public boolean DeleteDB(IDataAccess DataAccess,String TableName,String DBTableName,String KeyField) throws LegendException{
		return DeleteDB(DataAccess, SysUtility.getCurrentConnection(), TableName, DBTableName, KeyField);
	}
	
	public boolean DeleteDB(IDataAccess DataAccess,Connection conn,String TableName,String DBTableName,String KeyField) throws LegendException{
    	boolean rt = false;
    	try {
	    	TableName = SysUtility.ParseTableName(this, TableName);
	    	if(null == this || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)||!this.has(TableName)){
	    		return false;
	    	} 
			if(this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				if(rows != null && rows.length() > 0){
					rt = DataAccess.Delete(DBTableName, rows, KeyField, conn);
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rt;
	}
	
	public boolean DeleteDB(IDataAccess DataAccess,String TableName,String DBTableName,String KeyField,int RowIndex) throws LegendException{
		return DeleteDB(DataAccess, SysUtility.getCurrentConnection(), TableName, DBTableName, KeyField, RowIndex);
	}
	
	public boolean DeleteDB(IDataAccess DataAccess,Connection conn,String TableName,String DBTableName,String KeyField,int RowIndex) throws LegendException{
    	boolean rt = false;
    	try {
	    	TableName = SysUtility.ParseTableName(this, TableName);
	    	if(null == this || SysUtility.isEmpty(DBTableName) || SysUtility.isEmpty(DBTableName)||!this.has(TableName)){
	    		return false;
	    	} 
			if(this.get(TableName)!=null){
				JSONArray rows = this.getJSONArray(TableName);
				JSONObject row = rows.getJSONObject(RowIndex);
				if(row != null ){
					rt = DataAccess.Delete(DBTableName, row,KeyField);
				}
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return rt;
	}
}
