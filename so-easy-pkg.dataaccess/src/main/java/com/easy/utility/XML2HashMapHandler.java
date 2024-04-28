package com.easy.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.easy.exception.LegendException;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class XML2HashMapHandler extends DefaultHandler {
	private static final String DBNULL = "%HPFRAMEWORK_DBNULL%";
	
	public static final String XML_READ_ENCODING = "UTF-8";
	
	private final int intInitialFlag = 0;

	private final int intStartFlag = 1;

	private final int intCharactersFlag = 2;

	private final int intEndFlag = 3;

	private int intCurrentFlag = intInitialFlag; // 1 startElement, 2 characters, 3 endElement

	private HashMap hmData = new HashMap();

	private Stack stkElement = new Stack();

	private Stack stkElementFlag = new Stack();

	private Stack stkData = new Stack();

	private boolean rootFilterEnable = false;

	private String rootFilterName = null;

	private StringBuffer currentElementText = new StringBuffer();

	/**
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		hmData.clear();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		intCurrentFlag = intInitialFlag;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		// 开始处理xml时，如果使用请求文件头过滤器，则判断根节点名称是否符合约定值
		if (intCurrentFlag == intInitialFlag) {
			if (rootFilterEnable && !qName.equals(rootFilterName)) {
				throw new SAXException("不符合协议的xml文件");
			}
		}

		// 非叶子节点的特殊处理
		if (intCurrentFlag != intEndFlag && intCurrentFlag != intInitialFlag) {
			stkElementFlag.setElementAt("0", stkElementFlag.size() - 1);

			if (!stkData.isEmpty()) {
				try {
					Object data = stkData.peek();
					Object parentKey = stkElement.get(stkElement.size() - 2);
					HashMap hmParentData = (HashMap) data;
					List parentList = (List) hmParentData.get(parentKey);

					if (parentList.isEmpty()) {
						HashMap hmChildData = new HashMap();
						hmChildData.put(stkElement.peek(), new ArrayList());
						parentList.add(hmChildData);
						stkData.push(hmChildData);
					} else {
						HashMap hmChildData = (HashMap) parentList.get(parentList.size() - 1);
						stkData.push(hmChildData);
						Object value = hmChildData.get(stkElement.peek());

						if(value instanceof List){
							((List) value).add(new HashMap());
						}else if (value == null || value.equals("")) {
							hmChildData.put(stkElement.peek(), new ArrayList());
						}else{
							hmChildData.put(stkElement.peek(), new ArrayList());//TODO
						}
					}
				} catch (Exception e) {
					throw new SAXException("在转换xml时发生错误：" + e.getMessage());
				}
			} else {
				hmData.put(stkElement.peek(), new ArrayList());
				stkData.push(hmData);
			}
		}

		// 记录当前处理标志
		intCurrentFlag = intStartFlag;

		// 将当前节点默认为是叶子节点，并将其标志压栈
		stkElementFlag.push("1");

		// 当前节点压栈
		stkElement.push(qName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char ch[], int start, int length) throws SAXException {
		// 得到当前节点值，如果连续调用多次characters方法并且当前处理节点为叶子节点时，
		// 将这几次调用的的currentElementText值合并处理
		String tmpStr = new String(ch, start, length);
		if (intCurrentFlag == intCharactersFlag && stkElementFlag.peek().equals("1")) {
			currentElementText.append(tmpStr);
		} else {
			currentElementText = new StringBuffer().append(tmpStr);
		}

		// 记录当前处理标志
		if (intCurrentFlag != intEndFlag) {
			intCurrentFlag = intCharactersFlag;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (intCurrentFlag == intStartFlag) {
			currentElementText.delete(0, currentElementText.length());
		}

		// 记录当前处理标志
		intCurrentFlag = intEndFlag;

		// 得到当前处理节点的节点标志值
		Object flag = stkElementFlag.peek();

		// 处理叶子节点
		if ("1".equals(flag)) {
			// 将当前处理节点的数据出栈
			Object obj = stkData.peek();

			if (obj != null && obj instanceof HashMap) {
				HashMap hmSubData = (HashMap) obj;
				Object parentKey = stkElement.get(stkElement.size() - 2);
				
				if (currentElementText.toString().equalsIgnoreCase(DBNULL)) {
					currentElementText.delete(0, currentElementText.length());
				}
				
				Object obj2 = hmSubData.get(parentKey);
				if(obj2 instanceof List){
					List list = (List) obj2;
					if (list.isEmpty()) {
						HashMap map = new HashMap();
						map.put(qName, currentElementText.toString());
						list.add(map);
					} else {
						HashMap map = (HashMap) list.get(list.size() - 1);
						putValue(map, qName, currentElementText.toString());
					}
				}else{
					//TODO
				}
			}
		} else {
			stkData.pop();
		}

		stkElementFlag.pop();
		stkElement.pop();
	}

	public static void putValue(Map hashmap, String key, Object value) {
		Object existvalue = hashmap.get(key);
		if (existvalue == null) {
			hashmap.put(key, value);
		} else {
			if (existvalue instanceof List) {
				((ArrayList) existvalue).add(value);
			} else {
				ArrayList list = new ArrayList();
				list.add(existvalue);
				list.add(value);
				hashmap.put(key, list);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		LogUtil.printLog("行：" + e.getLineNumber() + "列:" + e.getColumnNumber() + "解析发生错误！",
				LogUtil.DEBUG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}

	/*
	 * 显示xml处理过程中的错误信息，然后继续解析
	 * 
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException e) throws SAXException {
		LogUtil.printLog("行：" + e.getLineNumber() + "列:" + e.getColumnNumber() + "解析发生错误！",
				LogUtil.DEBUG);
	}

	/**
	 * 是否使用xml根节点过滤器
	 * 
	 * @param rootFilterEnabled
	 *            如果为true表示设置，否则不设置
	 */
	private void setRootFilterEnable(boolean rootFilterEnabled) {
		this.rootFilterEnable = rootFilterEnabled;
	}

	/**
	 * 设置xml根节点过滤时的根节点名称
	 * 
	 * @param rootName
	 *            根节点名称
	 */
	private void setFilterRootName(String rootName) {
		this.rootFilterName = rootName;
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param file
	 *            xml文件
	 * @return HashMap 解析后的结果。默认将不使用xml根节点过滤
	 */
	public HashMap getParseResult(java.io.File file) {
		return getParseResult(file, false, null);
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param file
	 *            xml文件
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。默认使用根节点过滤，如果xml根节点名称与输入 的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(java.io.File file, String rootName) {
		return getParseResult(file, true, rootName);
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param file
	 *            xml文件
	 * @param filterEnable
	 *            根节点过滤开关
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。如果filterEnable为true，并且xml根节点名称
	 *         与输入的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(java.io.File file, boolean filterEnable, String rootName) {
		java.io.FileInputStream input = null;

		try {
			input = new java.io.FileInputStream(file);
			getParseResult(input, filterEnable, rootName);
		} catch (Exception e) {
			hmData = new HashMap();
			LogUtil.printLog("进行xml解析时发生错误"+e.getMessage(), LogUtil.ERROR);
		} finally {
			closeInputStream(input);
		}

		return hmData;
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseString
	 *            xml文件内容
	 * @return HashMap 解析后的结果。默认将不使用xml根节点过滤
	 */
	public HashMap getParseResult(String parseString) {
		return getParseResult(parseString, false, null);
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseString
	 *            xml文件内容
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。默认使用根节点过滤，如果xml根节点名称与输入 的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(String parseString, String rootName) {
		return getParseResult(parseString, true, rootName);
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseString
	 *            xml文件内容
	 * @param filterEnable
	 *            根节点过滤开关
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。如果filterEnable为true，并且xml根节点名称
	 *         与输入的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(String parseString, boolean filterEnable, String rootName) {
		ByteArrayInputStream input = null;

		try {
			byte[] bParse = parseString.getBytes(XML_READ_ENCODING);
			input = new ByteArrayInputStream(bParse);
			getParseResult(input, filterEnable, rootName);
		} catch (Exception e) {
			hmData = new HashMap();
			LogUtil.printLog("进行xml解析时发生错误"+e.getMessage(), LogUtil.ERROR);
		} finally {
			closeInputStream(input);
		}

		return hmData;
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseStream
	 *            xml文件流
	 * @return HashMap 解析后的结果。默认将不使用xml根节点过滤
	 */
	public HashMap getParseResult(InputStream parseStream) throws LegendException {
		return getParseResult(parseStream, false, null);
	}

	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseStream
	 *            xml文件流
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。默认使用根节点过滤，如果xml根节点名称与输入 的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(InputStream parseStream, String rootName) throws LegendException {
		return getParseResult(parseStream, true, rootName);
	}

	public HashMap getParseResult(InputStream parseStream, boolean filterEnable, String rootName)
			throws LegendException {
		return getParseResult(parseStream, filterEnable, rootName, XML_READ_ENCODING);
	}
	
	/**
	 * 将输入的xml文件解析为HashMap
	 * 
	 * @param parseStream
	 *            xml文件流
	 * @param filterEnable
	 *            根节点过滤开关
	 * @param rootName
	 *            使用xml根节点过滤时根节点名称
	 * @return HashMap 解析后的结果。如果filterEnable为true，并且xml根节点名称
	 *         与输入的节点名称不同，则返回空的HashMap
	 */
	public HashMap getParseResult(InputStream parseStream, boolean filterEnable, String rootName,String Encoding)
			throws LegendException {
		try {
			setRootFilterEnable(filterEnable);
			setFilterRootName(rootName);
			SAXParserFactory sf = SAXParserFactory.newInstance();
			SAXParser sp = sf.newSAXParser();
			InputSource is = new InputSource(parseStream);
			is.setEncoding(Encoding);
			sp.parse(is, this);
		} catch (FactoryConfigurationError e) {
			LogUtil.printLog("进行xml解析时发生错误:FactoryConfigurationError"+e.getMessage(), LogUtil.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (ParserConfigurationException e) {
			LogUtil.printLog("进行xml解析时发生错误：ParserConfigurationException"+e.getMessage(), LogUtil.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (SAXException e) {
			hmData = new HashMap();
			LogUtil.printLog("进行xml解析时发生错误：SAXException"+e.getMessage(), LogUtil.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (IOException e) {
			hmData = new HashMap();
			LogUtil.printLog("进行xml解析时发生错误：IOException"+e.getMessage(), LogUtil.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (Exception e) {
			hmData = new HashMap();
			LogUtil.printLog("进行xml解析时发生错误:Exception"+e.getMessage(), LogUtil.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} finally {
			closeInputStream(parseStream);
		}

		return hmData;
	}

	private void closeInputStream(InputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (Exception e) {
			}
		}
	}
}
