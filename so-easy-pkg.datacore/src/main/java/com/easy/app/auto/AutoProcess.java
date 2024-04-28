package com.easy.app.auto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class AutoProcess extends GenericServlet{
	private static final long serialVersionUID = -8505611749348389713L;

	public void init() throws ServletException {
		super.init();
		initTableCloumn();
	}
	
	public void initTableCloumn(){
		
	}

	public void initCoreTableCloumn(String moulde,String[] ExeCoulmnNames,HashMap<String,String> ExeCoulmnNameMap,
			HashMap<String,String> ExeCoulmnDefaultMap,HashMap<String,String> ExeCoulmnDescMap) throws SQLException{
		AutoCreateTable(FltToDBMap("CreateTable", this, moulde+"/addtable.flt"));
		AutoCreateSequence(FltToDBMap("CreateSequence", this, moulde+"/addsequence.flt"));
		
		ExeCoulmnNameMap = FltToDBMap("AlterTableAdd",this, moulde+"/addcoulmn.flt");
		ExeCoulmnDescMap = FltToDBMap("CommentOnColumn", this, moulde+"/addcoulmn.flt");
		ExeCoulmnDefaultMap = FltToDBMap("AlterTableModify", this, moulde+"/addcoulmn.flt");
		ExeCoulmnNames = FltGetKey(this, moulde+"/addcoulmn.flt");
		AutoAddTableColumn(ExeCoulmnNames, ExeCoulmnNameMap, ExeCoulmnDescMap, ExeCoulmnDefaultMap);
	}
	
	public void AutoCreateSequence(HashMap<String,String> SequenceMap){
		Set mapSet = SequenceMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			try {
				if(!SysUtility.SequenceExists(key)){
					if(value.startsWith("#")){
						continue;
					}
					if(value.endsWith(";")){
						value = value.substring(0, value.length() - 1);
					}
					SQLExecUtils.executeUpdate(value);
					LogUtil.printLog("序列创建成功："+key+"", Level.WARN);
				}
			} catch (LegendException e) {
				LogUtil.printLog("序列创建失败："+key+"", Level.ERROR);
			}
		}
	}
	
	public void AutoCreateTable(HashMap<String,String> TableMap) throws SQLException{
		Set mapSet = TableMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			try {
				if(!SysUtility.TableNameExists(key)){
					if(value.startsWith("#")){
						continue;
					}
					if(value.endsWith(";")){
						value = value.substring(0, value.length() - 1);
					}
					SQLExecUtils.executeUpdate(value);
					LogUtil.printLog("表创建成功："+key+"", Level.WARN);
				}
			} catch (LegendException e) {
				LogUtil.printLog("表创建失败："+key+"", Level.ERROR);
			}
		}
	}
	
	public void AutoAddTableColumn(String[] FieldNames,HashMap<String,String> FieldName,HashMap<String,String> FieldDesc,HashMap<String,String> FieldDefault) throws SQLException{
		if(SysUtility.isEmpty(FieldName)){
			return;
		}
		
		try {
			final List TabList = new ArrayList();
			Set TabSet = FieldName.entrySet();
			for (Iterator it = TabSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				String key = (String)entry.getKey();
				if(SysUtility.isNotEmpty(key) && key.split("\\.").length == 2 && !TabList.contains(key.split("\\.")[0])){
					TabList.add(key.split("\\.")[0]);
				}
			}
			if(SysUtility.isEmpty(TabList)){
				return;
			}
			SQLBuild sqlBuild = SQLBuild.getInstance();
			if(SysUtility.IsOracleDB()) {
				sqlBuild.append("select table_name||'.'||column_name hascolumn from user_tab_columns u where 1 = 1");
			}else if(SysUtility.IsMySqlDB()) {
				sqlBuild.append("select concat(table_name,'.',column_name) hascolumn from information_schema.columns u where table_schema='"+SysUtility.getMySqlSchema()+"'");
			}
			
			sqlBuild.append("u.table_name", "in", TabList);
			List rt = sqlBuild.query4List();
			
			List currentList = new ArrayList();
			for (int i = 0; i < rt.size(); i++) {
				HashMap map = (HashMap)rt.get(i);
				String hascolumn = (String)map.get("hascolumn");
				if(SysUtility.isEmpty(hascolumn)) {
					hascolumn = (String)map.get("HASCOLUMN");
				}
				currentList.add(hascolumn);
			}
			
			for (int i = 0; i < FieldNames.length; i++) {
				String key = (String)FieldNames[i];
				if(SysUtility.isEmpty(key)){
					break;
				}
				if(!currentList.contains(key)){
		        	String TableName = key.split("\\.")[0];
		        	if(SysUtility.TableNameExists(TableName)){
		        		try {
							if(SysUtility.isNotEmpty(FieldName.get(key))){
								String exscuteSQL = FieldName.get(key);
								if(exscuteSQL.startsWith("#")){
									continue;
								}
								if(exscuteSQL.endsWith(";")){
									exscuteSQL = exscuteSQL.substring(0, exscuteSQL.length() - 1);
								}
								SQLExecUtils.executeUpdate(exscuteSQL);
								LogUtil.printLog("字段添加成功："+FieldName.get(key)+"", Level.WARN);
							}
							if(SysUtility.isNotEmpty(FieldDesc.get(key))){
								String exscuteSQL = FieldDesc.get(key);
								if(exscuteSQL.startsWith("#")){
									continue;
								}
								if(exscuteSQL.endsWith(";")){
									exscuteSQL = exscuteSQL.substring(0, exscuteSQL.length() - 1);
								}
								SQLExecUtils.executeUpdate(exscuteSQL);
								LogUtil.printLog("备注添加成功："+FieldDesc.get(key)+"", Level.WARN);
							}
							if(SysUtility.isNotEmpty(FieldDefault.get(key))){
								String exscuteSQL = FieldDefault.get(key);
								if(exscuteSQL.startsWith("#")){
									continue;
								}
								if(exscuteSQL.endsWith(";")){
									exscuteSQL = exscuteSQL.substring(0, exscuteSQL.length() - 1);
								}
								SQLExecUtils.executeUpdate(exscuteSQL);
								LogUtil.printLog("默认值添加成功："+FieldDefault.get(key)+"", Level.WARN);
							}
						} catch (Exception e) {
							LogUtil.printLog("字段添加失败："+FieldName.get(key)+"", Level.ERROR);
						}
		        	}
		        }
			}
		} catch (LegendException e) {
			LogUtil.printLog("字段自动新增出错，请手工检查数据库表字段一致性："+e.getMessage(), Level.ERROR);
		}
	}
	
	public void destroy() {
		super.destroy();
	}

	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}
	
	
	public void GenerateFieldName(final String[] params){
		StringBuffer selSQL = new StringBuffer();
		selSQL.append("SELECT 'FieldName.put(\"'||TABLE_NAME||'.'||COLUMN_NAME||'\", \"'||'ALTER TABLE '||TABLE_NAME||' ADD '||COLUMN_NAME||' '||DATA_TYPE||decode(DATA_TYPE,'DATE','','('||DATA_LENGTH||')')||'\");' FieldName");
		selSQL.append(" FROM USER_TAB_COLUMNS u ");
		selSQL.append(" WHERE TABLE_NAME in(");
		for (int i = 0; i < params.length; i++) {
			if(i == 0){
				selSQL.append("?");
			}else{
				selSQL.append(",?");
			}
		}
		selSQL.append(")");
		
		try {
			List rt = SQLExecUtils.query4List(selSQL.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < params.length; i++) {
						ps.setString(i+1, params[i]);
					}
				}
			});
			for (int i = 0; i < rt.size(); i++) {
				HashMap map = (HashMap)rt.get(i);
				String FieldName = (String)map.get("FIELDNAME");
				System.out.println(FieldName);
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public void GenerateFieldDesc(final String[] params){
		StringBuffer selSQL = new StringBuffer();
		selSQL.append("SELECT 'FieldDesc.put(\"'||TABLE_NAME||'.'||COLUMN_NAME||'\", \"'||'COMMENT ON COLUMN '||TABLE_NAME||'.'||COLUMN_NAME||' IS '''||(SELECT COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME = U.TABLE_NAME AND COLUMN_NAME = U.COLUMN_NAME)||'''\");' FieldDesc");
		selSQL.append(" FROM USER_TAB_COLUMNS u ");
		selSQL.append(" WHERE TABLE_NAME in(");
		for (int i = 0; i < params.length; i++) {
			if(i == 0){
				selSQL.append("?");
			}else{
				selSQL.append(",?");
			}
		}
		selSQL.append(")");
		
		try {
			List rt = SQLExecUtils.query4List(selSQL.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < params.length; i++) {
						ps.setString(i+1, params[i]);
					}
				}
			});
			for (int i = 0; i < rt.size(); i++) {
				HashMap map = (HashMap)rt.get(i);
				String FieldDesc = (String)map.get("FIELDDESC");
				System.out.println(FieldDesc);
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public void GenerateFieldDefault(final String[] params){
		StringBuffer selSQL = new StringBuffer();
		selSQL.append("SELECT DATA_DEFAULT,TABLE_NAME,COLUMN_NAME FROM USER_TAB_COLUMNS WHERE DATA_DEFAULT IS NOT NULL AND TABLE_NAME in(");
		for (int i = 0; i < params.length; i++) {
			if(i == 0){
				selSQL.append("?");
			}else{
				selSQL.append(",?");
			}
		}
		selSQL.append(")");
		
		try {
			List rt = SQLExecUtils.query4List(selSQL.toString(),new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < params.length; i++) {
						ps.setString(i+1, params[i]);
					}
				}
			});
			for (int i = 0; i < rt.size(); i++) {
				HashMap map = (HashMap)rt.get(i);
				String TableName = (String)map.get("TABLE_NAME");
				String ColumnName = (String)map.get("COLUMN_NAME");
				
				Set mapSet = map.entrySet();
				for (Iterator it = mapSet.iterator(); it.hasNext();) {
					Entry entry = (Entry)it.next();
					String key = TableName+"."+ColumnName;
			        String value = "ALTER TABLE "+TableName+" MODIFY "+ColumnName+" DEFAULT "+entry.getValue().toString().replaceAll("\n", "");
			        System.out.println(value+";");
			        break;
				}
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public static HashMap<String,String> FltToDBMap(String type,Object obj,String filepath){
		HashMap<String,String> map = new HashMap<String,String>();
		InputStream is = obj.getClass().getClassLoader().getResourceAsStream(filepath);
		if(SysUtility.isEmpty(is)){
			return new HashMap<String,String>();
		}
		InputStreamReader isRd = null;
		LineNumberReader reader = null;
		try {
			isRd = new InputStreamReader(is);
			reader = new LineNumberReader(isRd);
			int blankCount = 0;
			while(true){
				String str = "";
				try {
					str = reader.readLine();
					if(blankCount >= 5){
						break;
					}else if(SysUtility.isEmpty(str) || str.startsWith("#")){//遇到空行，累加一次 ;遇到注释符，自动跳过
						blankCount++;
						continue;
					}else if(SysUtility.isNotEmpty(str)){
						blankCount = 0;
						String[] strs = str.split(" ");
						if("AlterTableAdd".equals(type) && "ALTER".equalsIgnoreCase(strs[0])&& "TABLE".equalsIgnoreCase(strs[1])&& "ADD".equalsIgnoreCase(strs[3])){
							map.put(strs[2]+"."+strs[4], str);
						}else if("CommentOnColumn".equals(type) && "Comment".equalsIgnoreCase(strs[0])&& "On".equalsIgnoreCase(strs[1])&& "Column".equalsIgnoreCase(strs[2])){
							map.put(strs[3], str);
						}else if("AlterTableModify".equals(type) && "Alter".equalsIgnoreCase(strs[0])&& "Table".equalsIgnoreCase(strs[1])&& "Modify".equalsIgnoreCase(strs[3])){
							map.put(strs[2]+"."+strs[4], str);
						}else if("CreateTable".equals(type)&& "Create".equalsIgnoreCase(strs[0])&& ("Table".equalsIgnoreCase(strs[1]) || "GLOBAL".equalsIgnoreCase(strs[1]))){
							if("TEMPORARY".equalsIgnoreCase(strs[2])) {
								map.put(strs[4], str);
							}else {
								map.put(strs[2], str);
							}
						}else if("CreateSequence".equals(type)&& "Create".equalsIgnoreCase(strs[0])&& "Sequence".equalsIgnoreCase(strs[1])){
							map.put(strs[2].substring(0, strs[2].length()-1), str);
						}
					}
				} catch (Exception e) {
					LogUtil.printLog("脚本配置错误："+str+" ，"+e.getMessage(), Level.ERROR);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			SysUtility.closeInputStreamReader(reader);
			SysUtility.closeLineNumberReader(isRd);
			SysUtility.closeInputStream(is);
		}
		return map;
	}
	
	public static String[] FltGetKey(Object obj,String filepath){
		String[] strs = new String[100000];
		InputStream is = obj.getClass().getClassLoader().getResourceAsStream(filepath);
		if(SysUtility.isEmpty(is)){
			return new String[]{};
		}
		InputStreamReader isRd = null;
		LineNumberReader reader = null;
		try {
			isRd = new InputStreamReader(is);
			reader = new LineNumberReader(isRd);
			int blankCount = 0;
			int count = 0;
			while(true){
				String str = reader.readLine();
				if(blankCount >= 5){
					break;
				}else if(SysUtility.isEmpty(str) || str.startsWith("#")){
					blankCount++;
					continue;
				}else if(SysUtility.isNotEmpty(str)){
					blankCount = 0;
					String[] tstrs = str.split(" ");
					if("ALTER".equalsIgnoreCase(tstrs[0])&& "TABLE".equalsIgnoreCase(tstrs[1])&& "ADD".equalsIgnoreCase(tstrs[3])){
						strs[count++] = tstrs[2]+"."+tstrs[4];
					}
				}
			}
		} catch (IOException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally{
			SysUtility.closeInputStreamReader(reader);
			SysUtility.closeLineNumberReader(isRd);
			SysUtility.closeInputStream(is);
		}
		return strs;
	}
	
	
}
