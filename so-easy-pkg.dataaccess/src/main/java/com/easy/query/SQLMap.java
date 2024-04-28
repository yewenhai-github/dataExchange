package com.easy.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class SQLMap {
	private static final Pattern P = Pattern.compile("\\s*\\?\\s*");
	private static final SAXReader SAX_READER = new SAXReader();
	private static Map select = new HashMap();
	private static Map insert = new HashMap();
	private static Map update = new HashMap();
	private static Map delete = new HashMap();
	private static Map callable = new HashMap();
	private static Map unkown = new HashMap();
	
	public static void add(InputStream is) throws DocumentException, LegendException {
		Document doc = null;
		try {
			doc = SAX_READER.read(is);
			Element root = (Element) doc.selectSingleNode("/SqlMap");
			for (Iterator it = root.elementIterator(); it.hasNext();) {
				Element e = (Element) it.next();
				String action = e.getName();
				String id = e.valueOf("@id");
				if ("select".equals(action)) {
					select.put(id, filter(id, e.getText()));
				} else if ("insert".equals(action)) {
					insert.put(id, filter(id, e.getText()));
				} else if ("update".equals(action)) {
					update.put(id, filter(id, e.getText()));
				} else if ("delete".equals(action)) {
					delete.put(id, filter(id, e.getText()));
				} else if ("callable".equals(action)) {
					callable.put(id, filter(id, e.getText()));
				} else {
					unkown.put(id, filter(id, e.getText()));
				}
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.printLog("关闭文件失败!", LogUtil.WARN, e);
				}
			}
			if (doc != null) {
				doc.clearContent();
			}
		}
	}
	
	private static String filter(String id, String text) throws LegendException{
		String sql = text.replaceAll("\\s+", " ");
		if (check(sql))
			throw LegendException.getLegendException("xml获取SQL出错！");
		return sql;
	}
	
	private static boolean check(String sql) {
		sql = sql.replaceAll("('.*?')|(\".*?\")", "");
		return P.matcher(sql).find();
	}
	
	public static String getSelect(String id) throws LegendException {
		String sql = (String) select.get(id);
		if (SysUtility.isEmpty(sql)) {
			sql = (String) unkown.get(id);
		}
		if (SysUtility.isEmpty(sql)) {
			LogUtil.printLog("SQLMap.getSelect("+id+") 找不到定义的SQL", Level.DEBUG);
		}else{
			LogUtil.printLog("SQLMap.getSelect("+id+"):"+sql, Level.INFO);
		}
		return sql;
	}
	
	public String getDelete(String id) {
		String sql = (String) delete.get(id);
		if (SysUtility.isEmpty(sql)) {
			return (String) unkown.get(id);
		}
		return sql;
	}

	public String getInsert(String id) {
		String sql = (String) insert.get(id);
		if (SysUtility.isEmpty(sql)) {
			return (String) unkown.get(id);
		}
		return sql;
	}

	public String getUpdate(String id) {
		String sql = (String) update.get(id);
		if (SysUtility.isEmpty(sql)) {
			return (String) unkown.get(id);
		}
		return sql;
	}

	public String getCallable(String id) {
		String sql = (String) callable.get(id);
		if (SysUtility.isEmpty(sql)) {
			return (String) unkown.get(id);
		}
		return sql;
	}
	
}
