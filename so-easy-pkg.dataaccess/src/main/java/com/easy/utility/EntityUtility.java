package com.easy.utility;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * 
 * @author yewh 2017-04-11
 * 
 * @version 1.0
 * 
 */
@Slf4j
public class EntityUtility {
	
	public static HashMap entityToHashMap(Object entityObject) {
		return entityToHashMap(entityObject, true);
	}
	
	/**
	 * 将Entity中的数据转换成HashMap
	 * 
	 * @param entityObject
	 *            Entity对象
	 * @param isParseByClassName Entity没有定义getEntityName方法时，是使用方法名称还是类名称生成Key。true表示使用类名称生成key， 否则用方法名称get后的字符串生成key
	 * @return HashMap 返回的HashMap数据
	 */
	private static HashMap entityToHashMap(Object entityObject, boolean isParseByClassName) {
		if (entityObject == null) {
			return null;
		}

		Method[] methods = entityObject.getClass().getMethods();
		HashMap hmReturn = new HashMap();

		/** 循环处理每一个方法 */
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();

			// 过滤非get方法、有参get方法以及指定的get方法
			if (EntityUtility.filterGetMethod(method)) {
				continue;
			}

			try {
				Object objResult = method.invoke(entityObject, null); // 执行方法

				if (objResult != null) {
					Class resultClass = objResult.getClass(); // 执行后返回值的类型对象

					if (resultClass.isArray()) { // 数组的处理
						int size = Array.getLength(objResult);

						if (size > 0) {
							int k = 0;
							while (Array.get(objResult, k) == null) {
								k++;
							}
							Object obj = Array.get(objResult, k);
							if (obj.getClass().getName().startsWith("com.sinotrans.marine")) { // Entity
								// Array
								// if (obj instanceof IXmlEntity) { //Entity
								// Array
								List list = new java.util.ArrayList();
								hmReturn.put(EntityUtility.getKey(obj, methodName, isParseByClassName), list);

								for (int j = k; j < size; j++) {
									Object obj1 = Array.get(objResult, j);

									if (obj1 != null) {
										list.add(entityToHashMap(obj1, isParseByClassName));
									}
								}
							} else { // Other Type Array
								List list = new ArrayList();
								hmReturn.put(EntityUtility.getFieldNameTag(methodName), list);

								for (int j = k; j < size; j++) {
									Object obj1 = Array.get(objResult, j);

									if (obj1 != null) {
										list.add(convertObjectToString(obj1));
									}
								}
							}
						}
					} else if (objResult instanceof java.util.Collection) { // Collection的处理
						Object[] objs = ((Collection) objResult).toArray();
						List list = new ArrayList();
						hmReturn.put(EntityUtility.getFieldNameTag(methodName), list);

						for (int j = 0; j < objs.length; j++) {
							if (objs[j].getClass().getName().startsWith("com.sinotrans.marine")) {
								// if (objs[j] instanceof IXmlEntity) {
								list.add(entityToHashMap(objs[j], isParseByClassName));
							} else {
								list.add(convertObjectToString(objs[j]));
							}
						}
					} else if (objResult.getClass().getName().startsWith("com.sinotrans.marine")) {
						// else if (objResult instanceof IXmlEntity) {//Entity
						hmReturn.put(EntityUtility
								.getKey(objResult, methodName, isParseByClassName),
								entityToHashMap(objResult, isParseByClassName));
					} else { // 其他类型
						hmReturn.put(EntityUtility.getFieldNameTag(methodName), convertObjectToString(objResult));
					}
				}
			} catch (Exception e) {
				log.error("在将Entity解析成HashMap时发生错误 at EntityUtility.entityToHashMap:"+e.getMessage());
			}
		}
		methods = null;
		return hmReturn;
	}
	
	/**
	 * 将HashMap解析成Entity
	 * 
	 * @param entityObject
	 *            Entity实体对象
	 * @param hmData
	 *            HashMap数据
	 * @return Object 解析后的Entity
	 */
	public static Object hashMapToEntity(Object entityObject, Map hmData) {
		if (entityObject == null || hmData == null || hmData.isEmpty()) {
			return entityObject;
		}
		Method[] methods = entityObject.getClass().getMethods();

		for (int i = 0; i < methods.length; i++) {
			// 过滤掉不符合约定的方法
			if (EntityUtility.filterSetMethod(methods[i])) {
				continue;
			}

			Class[] paramTypes = methods[i].getParameterTypes();
			String paramClassName = paramTypes[0].getName();
			String key = EntityUtility.getFieldNameTag(methods[i].getName());

			try {
				if (isInBaseType(paramClassName)) { // 基本类型的处理
					//对于有标签，但是内容为空的字段仍旧需要传入到对象，对于hashmap中没有的标签则不传入对象。
					if (hmData.containsKey(key)){
						Object subValue = hmData.get(key);
						Object obj = convertToTypedObject(paramClassName, subValue);
						if("int".equalsIgnoreCase(paramClassName) || "long".equalsIgnoreCase(paramClassName)|| "double".equalsIgnoreCase(paramClassName)|| "float".equalsIgnoreCase(paramClassName)|| "short".equalsIgnoreCase(paramClassName)){
							methods[i].invoke(entityObject, SysUtility.isEmpty(obj)?0:obj);
						}else if("boolean".equalsIgnoreCase(paramClassName)){
							methods[i].invoke(entityObject, SysUtility.isEmpty(obj)?false:obj);
						}else{
							methods[i].invoke(entityObject, new Object[] { obj });
						}
					}
				} else if (paramTypes[0].isArray()) { // 数组的处理
					String methodName = methods[i].getName();
					Class fieldClassType = getFieldType(entityObject, methodName.substring(3));

					if (fieldClassType == null) {
						log.info("无法得到[" + methodName + "]方法对应属性的具体类型，无法解析该部分的数据");
						continue;
					}

					String fieldType = fieldClassType.getComponentType().getName();

					if (fieldType.startsWith("com.sinotrans.marine")) {
						Object subObject = getInstance(fieldType);
						String tableName = EntityUtility.getTableName(subObject);
						String subKey = (tableName == null || tableName.equals("")) ? key
								: tableName.toUpperCase();
						Object subValue = hmData.get(subKey);

						if (subValue == null) {
							continue;
						}

						Object[] objEntitys = null;

						if (subValue instanceof List) {
							List list = (List) subValue;
							objEntitys = new Object[list.size()];
							for (int j = 0; j < list.size(); j++) {
								objEntitys[j] = hashMapToEntity(getInstance(fieldType),
										(HashMap) list.get(j));
							}
						} else {
							objEntitys = new Object[1];
							objEntitys[0] = hashMapToEntity(getInstance(fieldType),
									(HashMap) subValue);
						}
						Object arr = Array.newInstance(fieldClassType.getComponentType(),
								objEntitys.length);
						System.arraycopy(objEntitys, 0, arr, 0, objEntitys.length);
						methods[i].invoke(entityObject, new Object[] { arr });
					} else {
						Object objValues = hmData.get(key);

						if (objValues == null) {
							continue;
						}

						Object[] value = null;

						if (objValues instanceof List) {
							value = ((List) objValues).toArray();
						} else {
							value = new Object[1];
							value[0] = objValues;
						}

						methods[i].invoke(entityObject, new Object[] { value });
					}
				} else if (paramClassName.equals("java.util.List")) { // List的处理
					String methodName = methods[i].getName();
					Class fieldType = getFieldType(entityObject, methodName.substring(3));

					if (fieldType == null) {
						log.info("无法得到[" + methodName + "]方法对应属性的具体类型，无法解析该部分的数据");
						continue;
					}

					if (fieldType.isArray()) { // Field是数组，set方法的参数是List
						String fieldTypeName = fieldType.getComponentType().getName();

						if (fieldTypeName.startsWith("com.sinotrans.marine")) { // Entity
							// Array
							Object subObject = getInstance(fieldTypeName);
							String tableName = EntityUtility.getTableName(subObject);
							String subKey = (tableName == null || tableName.equals("")) ? key
									: tableName.toUpperCase();
							// String subKey = getTableNameTag(subObject,
							// isClassNameKey);
							Object subValue = hmData.get(subKey);

							if (subValue == null) {
								continue;
							}

							List listParams = new ArrayList();

							if (subValue instanceof List) {
								List subList = (List) subValue;
								for (int j = 0; j < subList.size(); j++) {
									listParams.add(j, hashMapToEntity(getInstance(fieldTypeName), (HashMap) subList.get(j)));
								}
							} else {
								listParams.add(hashMapToEntity(getInstance(fieldTypeName), (HashMap) subValue));
							}

							methods[i].invoke(entityObject, new Object[] { listParams });
						} else if (isInBaseType(fieldTypeName)) { // base
							// type
							// array
							Object objValues = hmData.get(key);
							List listParams = new ArrayList();
							List subList = new ArrayList();

							if (objValues == null) {
								continue;
							}

							if (objValues instanceof List) {
								subList = (List) objValues;
							} else {
								subList.add(objValues);
							}

							// 方案1:set方法的参数是Object[]
							for (int j = 0; j < subList.size(); j++) {
								listParams.set(j, convertToTypedObject(fieldTypeName, subList
										.get(j)));
							}

							methods[i].invoke(entityObject, new Object[] { listParams });
						} else {
							log.info("不支持的实体[" + entityObject.getClass().getName() + "]方法["
									+ methods[i].getName() + "]定义，系统不能处理");
						}
					} else if (fieldType.getName().equals("java.util.List")) { // Field是List，set方法的参数是List
						// 由于无法得到值的类型，所以直接将HashMap中的值设置到Entity中
						Object value = hmData.get(key);

						if (value != null && value instanceof List) {
							methods[i].invoke(entityObject, new Object[] { (List) value });
						}
					} else { // Field是除以上两种类型以外的其他类型，set方法的参数是List，不支持该定义
						log.info("不支持的实体[" + entityObject.getClass().getName() + "]方法["
								+ methods[i].getName() + "]定义，系统不能处理");
					}
				} else if (paramClassName.equals("java.util.Vector")) { // Vector的处理
					String methodName = methods[i].getName();
					Class fieldType = getFieldType(entityObject, methodName.substring(3));

					if (fieldType == null) {
						log.info("无法得到[" + methodName + "]方法对应属性的具体类型，无法解析该部分的数据");
						continue;
					}

					if (fieldType.isArray()) { // Field是数组，set方法的参数是Vector
						String fieldTypeName = fieldType.getComponentType().getName();

						if (fieldTypeName.startsWith("com.sinotrans.marine")) { // Entity
							// Array
							Object subObject = getInstance(fieldTypeName);
							String tableName = EntityUtility.getTableName(subObject);
							String subKey = (tableName == null || tableName.equals("")) ? key
									: tableName.toUpperCase();
							// String subKey = getTableNameTag(subObject,
							// isClassNameKey);
							Object subValue = hmData.get(subKey);

							if (subValue == null) {
								continue;
							}

							List listParams = new ArrayList();
							java.util.Vector vecParams = new java.util.Vector();

							if (subValue instanceof List) {
								listParams = (List) subValue;
								for (int j = 0; j < listParams.size(); j++) {
									vecParams.add(hashMapToEntity(
											getInstance(fieldTypeName), (HashMap) listParams
											.get(j)));
								}
							} else {
								vecParams.add(hashMapToEntity(
										getInstance(fieldTypeName), (HashMap) subValue));
							}

							methods[i].invoke(entityObject, new Object[] { vecParams });
						} else if (isInBaseType(fieldTypeName)) { // base
							// type
							// array
							Object objValues = hmData.get(key);

							if (objValues == null) {
								continue;
							}

							List listParams = new ArrayList();
							java.util.Vector vecParams = new java.util.Vector();

							if (objValues instanceof List) {
								listParams = (List) objValues;
							} else {
								listParams.add(objValues);
							}

							// 方案1:set方法的参数是Object[]
							for (int j = 0; j < listParams.size(); j++) {
								vecParams
										.add(convertToTypedObject(fieldTypeName, listParams.get(j)));
							}

							methods[i].invoke(entityObject, new Object[] { vecParams });
						} else {
							log.info("不支持的实体[" + entityObject.getClass().getName() + "]方法["
									+ methods[i].getName() + "]定义，系统不能处理");
						}
					} else if (fieldType.getName().equals("java.util.Vector")) { // Field是Vector，set方法的参数是Vector
						Object value = hmData.get(key);

						if (value != null && value instanceof List) {
							List listParams = (List) value;
							java.util.Vector vecParams = new java.util.Vector();

							for (int j = 0; j < listParams.size(); j++) {
								vecParams.add(listParams.get(j));
							}

							methods[i].invoke(entityObject, new Object[] { vecParams });
						}
					} else { // Field是除以上两种类型以外的其他类型，set方法的参数是Vector，不支持该定义
						log.info("不支持的实体[" + entityObject.getClass().getName() + "]方法["
								+ methods[i].getName() + "]定义，系统不能处理");
					}
				} else if (paramClassName.startsWith("com.sinotrans.marine")) { // Entity的处理
					Object subObject = getInstance(paramClassName);
					String tableName = EntityUtility.getTableName(subObject);
					String subKey = (tableName == null || tableName.equals("")) ? key : tableName
							.toUpperCase();
					// String subKey = getTableNameTag(subObject,
					// isClassNameKey);
					Object subValue = hmData.get(subKey);

					if (subValue != null) {
						if (subValue instanceof List) {
							List list = (List) subValue;
							if (list.size() > 0) {
								methods[i].invoke(entityObject, new Object[] { hashMapToEntity(
										subObject, (HashMap) list.get(0)) });
							}
						} else {
							methods[i].invoke(entityObject, new Object[] { hashMapToEntity(
									subObject, (HashMap) subValue) });
						}
					}
				} else { // 其他类型暂时不考虑
					log.info("不支持的实体[" + entityObject.getClass().getName() + "]方法["
							+ methods[i].getName() + "]定义，系统不能处理");
				}
			} catch (Exception e) {
				log.error(" 解析实体错误 at EntityUtility.hashMapToEnntity():"+key+","+paramClassName);
//				LogUtil.printLog(" 解析实体错误 at EntityUtility.hashMapToEnntity():", LogUtil.ERROR);
//				throw Exception.getLegendException(ERR.FW_HASHMAP_TO_ENTITY_ERR, null, e);
			}
		}

		return entityObject;
	}
	
	/**
	 * set方法过滤，如果是不符合处理要求的方法就返回true
	 * 
	 * @param method
	 *            要判断的方法
	 * @return boolean 判断的结果
	 */
	private static boolean filterSetMethod(Method method) {
		String methodName = method.getName();
		return (!methodName.startsWith("set") || methodName.endsWith("Condition")
				|| methodName.equals("setPageInfo") || methodName.endsWith("setMaxRecordCount")
				|| methodName.endsWith("setSuffix") || methodName.endsWith("setFieldSelect")
				|| methodName.endsWith("setConnection") || method.getParameterTypes().length != 1);
	}
	
	/** 通过方法名称得到字段名称 */
	private static String getFieldNameTag(String methodName) {
		if(SysUtility.isEmpty(methodName)){
			return "";
		}
		
		
		String strFieldName = null;

		if (methodName.length() > 6) {
			strFieldName = (methodName.startsWith("getStr") || methodName.startsWith("setStr")) ? methodName
					.substring(6)
					: methodName.substring(3);
		} else {
			strFieldName = methodName.substring(3);
		}
		char[] c = strFieldName.toCharArray();
		StringBuffer rtMsg = new StringBuffer();
		rtMsg.append(c[0]);
		for (int i = 1; i < c.length; i++) {
			if(Character.isUpperCase(c[i])){
				rtMsg.append("_").append(c[i]);
			}else{
				rtMsg.append(c[i]);
			}
		}
		return rtMsg.toString().toUpperCase();
	}
	
	private static Object convertToTypedObject(String className, Object objValue) {
		Object objResult = null;
		if (objValue == null || SysUtility.isEmpty(objValue.toString()))
			return null;

		try {
			if (className.equals("boolean") || className.equals("java.lang.Boolean")) {
				objResult = Boolean.valueOf(objValue.equals("true"));
			} else if (className.equals("byte") || className.equals("java.lang.Byte")) {
				objResult = Byte.valueOf(objValue.toString());
			} else if (className.equals("double") || className.equals("java.lang.Double")) {
				objResult = Double.valueOf(objValue.toString());
			} else if (className.equals("int") || className.equals("java.lang.Integer")) {
				objResult = Integer.valueOf(objValue.toString());
			} else if (className.equals("float") || className.equals("java.lang.Float")) {
				objResult = Float.valueOf(objValue.toString());
			} else if (className.equals("long") || className.equals("java.lang.Long")) {
				objResult = Long.valueOf(objValue.toString());
			} else if (className.equals("short") || className.equals("java.lang.Short")) {
				objResult = Short.valueOf(objValue.toString());
			} else if (className.equals("java.util.Date")) {
				objResult = toDate(objValue, null);
			} else if (className.equals("java.sql.Date")) {
				java.util.Date date = toDate(objValue, null);
				objResult = new java.sql.Date(date.getTime());
			} else if (className.equals("java.util.Calendar")) {
				java.util.Date date = toDate(objValue, null);
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(date);
				objResult = cal;
			} else {
				objResult = new String(objValue.toString());
			}
		} catch (Exception e) {
			log.warn("无法将 " + objValue + " 转换成 " + className + " 类型的数据");
		} finally {
			objValue = null;
		}

		return objResult;
	}
	
	
	private static Date toDate(Object objDate, String pattern) throws Exception {
		DateFormat formatter = new SimpleDateFormat(
				(pattern == null) ? "yyyy-MM-dd HH:mm:ss" : pattern,
				Locale.CHINA);
		return formatter.parse(objDate.toString());
	}
	
	private static Class getFieldType(Object objEntity, String methodName) {
		try {
			Method method = objEntity.getClass().getMethod("get" + methodName, null);
			return method.getReturnType();
		} catch (Exception e) {
			return null;
		}
	}
	
	// 初始化指定类
	private static Object getInstance(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class claz = Class.forName(className);
		return claz.newInstance();
	}
		
	private static boolean isInBaseType(String typeName) {
		return baseTypes.contains(typeName);
	}
	
	private static List baseTypes = new ArrayList(20);
	static {
		baseTypes.add("boolean");
		baseTypes.add("java.lang.Boolean");
		baseTypes.add("byte");
		baseTypes.add("java.lang.Byte");
		baseTypes.add("double");
		baseTypes.add("java.lang.Double");
		baseTypes.add("float");
		baseTypes.add("java.lang.Float");
		baseTypes.add("int");
		baseTypes.add("java.lang.Integer");
		baseTypes.add("long");
		baseTypes.add("java.lang.Long");
		baseTypes.add("short");
		baseTypes.add("java.lang.Short");
		baseTypes.add("java.lang.String");
		baseTypes.add("java.util.Date");
		baseTypes.add("java.sql.Date");
		baseTypes.add("java.util.Calendar");
		baseTypes.add("java.lang.Object");
		baseTypes.add("java.math.BigDecimal");
	}
	
	/**
	 * 通过执行getEntityName方法得到定义的表名称
	 * 
	 * @param object
	 *            Entity对象
	 * @return String 定义的表名称
	 */
	private static String getTableName(Object object) {
		String tableName = null;
		String methodName = "getEntityName";

		try {
			Method[] methods = object.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(methodName)) {
					tableName = (String) methods[i].invoke(object, null);
					break;
				}
			}

		} catch (Exception ee) { // NoSuchMethodException
			log.warn(" MethodException:[" + methodName + "] " + ee.toString() + " at " + getClassName(object));
		}

		return tableName;
	}
	
	/** 从类对象中得到类名称 */
	private static String getClassName(Object object) {
		String className = object.getClass().getName();
		return className.substring(className.lastIndexOf(".") + 1);
	}
	
	/**
	 * get方法过滤，如果是不符合处理要求的方法就返回true
	 * 
	 * @param method
	 *            要判断的方法
	 * @return boolean 判断的结果
	 */
	public static boolean filterGetMethod(Method method) {
		String methodName = method.getName();
		return (!methodName.startsWith("get") || methodName.equals("getEntityName")
				|| methodName.endsWith("Condition") || methodName.equals("getRowstate")
				|| methodName.equals("getSuffix") || methodName.equals("getClass")
				|| method.getParameterTypes().length != 0 || methodName.equals("getIDValue"));
	}
	
	public static String getKey(Object object, String methodName, boolean isParseByClassName) {
		String tableName = getTableName(object);
		return (tableName == null || tableName == "") ? (isParseByClassName ? getClassName(object)
				.toUpperCase() : getFieldNameTag(methodName)) : tableName;
	}
	
	/**
	 * 将对象转换成字符串表示
	 * 
	 * @param object
	 *            对象
	 * @return String 转换后的字符串
	 */
	public static String convertObjectToString(Object object) {
		if (object instanceof Calendar) { // Calendar
			return dateFormat(object, null);
		} else if (object instanceof Date) { // date
			return dateFormat(object, null);
		} else { // other type
			return object.toString();
		}
	}
	
	/**
	 * 将日期对象格式化
	 * 
	 * @param dateObj
	 *            日期对象
	 * @param pattern
	 *            日期和时间的表示格式
	 * @return String 格式化的日期表示
	 */
	public static String dateFormat(Object dateObj, String pattern) {
		DateFormat formatter = new SimpleDateFormat(
				(SysUtility.isEmpty(pattern)) ? "yyyy-MM-dd HH:mm:ss" : pattern,
				Locale.CHINA);

		if (dateObj instanceof Calendar) { // Calendar
			Date date = ((Calendar) dateObj).getTime();
			return formatter.format(date);
		} else if (dateObj instanceof Date) { // date
			return formatter.format((Date) dateObj);
		} else {
			return null;
		}
	}
	
}
