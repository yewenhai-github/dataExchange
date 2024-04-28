package com.easy.app.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easy.utility.SysUtility;

/**
 * 智卓字符串工具类
 */
public class ZzStringUtil {

    public final static String TYPE_MARI = "Mari";//海事
    public final static String TYPE_PORT = "Port";//港口
    public final static String TYPE_CIQ = "";//海关
    public final static String SQL_PART_TYPE = "type";
    public final static String SQL_PART_TABLE = "table";
    public final static String SQL_PART_VALUE = "value";
    public final static String SQL_PART_WHERE = "where";

    /**
     * 获得一个文件的后缀名
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        String suffix = "";
        if (SysUtility.isNotEmpty(fileName) && fileName.contains(".")) {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return suffix;
    }

    /**
     * 获得一个文件的前缀
     *
     * @param fileName
     * @return
     */
    public static String getFilePrefix(String fileName) {
        String suffix = fileName;
        if (SysUtility.isNotEmpty(fileName) && fileName.contains(".")) {
            suffix = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return suffix;
    }

    /**
     * 转换结束时间
     *
     * @param endTime
     * @return
     */
    public static String translateEndTime(String endTime) {
        if (SysUtility.isNotEmpty(endTime) && !endTime.contains(" ")) {
            endTime += " 23:59:59";
        }
        return endTime;
    }

    /**
     * 转换开始时间
     *
     * @param startTime
     * @return
     */
    public static String translateStartTime(String startTime) {
        if (SysUtility.isNotEmpty(startTime) && !startTime.contains(" ")) {
            startTime += " 00:00:00";
        }
        return startTime;
    }

    /**
     * 驼峰命名改为下划线命名
     *
     * @param name
     * @return
     */
    public static String transLateUpper2UnderLine(final String name) {
        String rtn = name;
        if (SysUtility.isNotEmpty(name)) {
            rtn = name.replaceAll("([A-Z])", "_$1").toUpperCase();
        }
        return rtn;
    }

    /**
     * 下划线转驼峰
     *
     * @param name
     * @return
     */
    public static String transLateUnderLine2Upper(final String name) {
        String rtn = "";
        if (SysUtility.isNotEmpty(name)) {
            String[] splits = name.toLowerCase().split("_");
            for (String str : splits) {
                rtn += (str.substring(0, 1).toUpperCase() + str.substring(1));
            }
            rtn = rtn.substring(0, 1).toLowerCase() + rtn.substring(1);
        }
        return rtn;
    }

    /**
     * 返回指定格式的时间
     *
     * @param format
     * @return
     */
    public static String getNowTime(String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(cal.getTime());
    }

    /**
     * 首字符大写
     *
     * @param str
     * @return
     */
    public static String capFirst(final String str) {
        String rtnStr = str;
        if (SysUtility.isNotEmpty(str)) {
            rtnStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return rtnStr;
    }

    /**
     * 生成随机字符串包括字母数字下划线
     *
     * @param minLen
     * @param maxLen
     * @return
     */
    public static String generateRandomStr(int minLen, int maxLen) {
        int len = minLen + (int) Math.round(Math.random() * (maxLen - minLen));
        String generateSource = "0123456789";
        String rtnStr = "";
        for (int i = 0; i < len; i++) {
            rtnStr += generateSource.charAt((int) Math.floor(Math.random() * generateSource.length()));
        }
        return rtnStr;
    }

    /**
     * 给一个对象随机赋值
     *
     * @param o
     */
    public static void autoSetRandom(Object o) throws IllegalAccessException {
        Field fields[] = o.getClass().getDeclaredFields();
        StringBuffer fieilds = new StringBuffer("(");
        StringBuffer values = new StringBuffer("VALUES(");
        for (Field field : fields) {
            field.setAccessible(true);
            if (String.class == field.getType()) {
                field.set(o, generateRandomStr(3, 5));
            }
        }
    }

    /**
     * 生成报文的sendTime
     *
     * @return
     */
    public static String getSendTime() {
        return getNowTime("yyyyMMddHHmmss+08");
    }

    /**
     * 获得毫秒时间
     *
     * @return
     */
    public static String getMsSendTime() {
        return getNowTime("yyyyMMddHHmmssSSS");
    }

    /**
     * 获取默认时间格式
     *
     * @return
     */
    public static String getDefaultDateTime() {
        return getNowTime("yyyyMMddHHmmss");
    }


    /*
        '1': "未申报", '2': "已申报", '3': "退回", '4': "已受理", '5': "已通过", 6: "未受理", 7: "已办理"
        */
    public static Map<String, Boolean> mapEditAble = new HashMap<String, Boolean>() {
        {
            put("1", true);
            put("2", false);
            put("3", true);
            put("4", false);
            put("5", false);
            put("6", false);
            put("7", false);
            put("", false);
        }
    };

    /**
     * 获取类的模板class名称
     *
     * @param clazz
     * @param type
     * @return
     */
    public static String getTemplateClassName(Class clazz, String type) {
        String className = clazz.getSimpleName();
        if (SysUtility.isNotEmpty(type)) {
            className += ("#" + type);
        }
        return className;
    }

    /**
     * 生成指定大小的文件
     *
     * @param size
     * @param fileName
     */
    public static void generateSizeFile(int size, String fileName) {
        byte[] bytes = new byte[size];
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在指定字符串中查找正则表达式中的group
     *
     * @param ciqMsg
     * @param regFind
     * @return
     */
    public static String getFindMsg(String ciqMsg, String regFind) {
        String findMsg = "";
        Pattern pattern;
        pattern = Pattern.compile(regFind);
        Matcher matcher = pattern.matcher(ciqMsg);
        if (matcher.find() && matcher.groupCount() > 0) {
            findMsg = matcher.group(1);
        }
        return findMsg.trim();
    }

    /**
     * 解析修改内容的sql（insert,delete,update)
     *
     * @param sql
     * @return
     */
    public static Map<String, String> parseUpdateSql(String sql) {
        String[] sqlRegexes = {
                "\\W*(delete)\\W+from\\W+([\\w|\\.]+)\\W+where\\W(.+)",
                "\\W*(insert)\\W+into\\W+([\\w|\\.]+)(.+)",
                "\\W*(update)\\W+([\\w|\\.]+)\\W+set\\W+(.+)\\Wwhere\\W+(.+)"
        };
        Map<String, String> mapTypeParam = new HashMap() {{
            put("insert1", SQL_PART_TYPE);
            put("insert2", SQL_PART_TABLE);
            put("insert3", SQL_PART_VALUE);
            put("update1", SQL_PART_TYPE);
            put("update2", SQL_PART_TABLE);
            put("update3", SQL_PART_VALUE);
            put("update4", SQL_PART_WHERE);
            put("delete1", SQL_PART_TYPE);
            put("delete2", SQL_PART_TABLE);
            put("delete3", SQL_PART_WHERE);
        }};
        Map<String, String> tmpParseMap = new HashMap<String, String>();
        Map<String, String> parseMap = new HashMap<String, String>();
        for (String regex : sqlRegexes) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sql);
            if (matcher.find() && matcher.groupCount() > 0) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    tmpParseMap.put("" + i, matcher.group(i).trim());
                }
                break;
            }
        }
        if (!tmpParseMap.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = tmpParseMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (mapTypeParam.containsKey(tmpParseMap.get("1").toLowerCase() + entry.getKey())) {
                    parseMap.put(mapTypeParam.get(tmpParseMap.get("1").toLowerCase() + entry.getKey()), entry.getValue());
                }
            }
        }
        return parseMap;
    }

    /**
     * map2String
     *
     * @param map
     * @return
     */
    public static <K, V> String map2String(Map<K, V> map) {
        if (null == map) {
            return "null";
        }
        Iterator<Map.Entry<K, V>> i = map.entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            Map.Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key);
            sb.append('=');
            if(null!=value){
                sb.append(value.getClass().isArray() ? Arrays.toString((Object[]) value) : value);
            }
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

    public static void main(String[] args) {
        System.out.println(parseUpdateSql("INsert into tableA   (a,B)values(1,2)"));
    }


}
