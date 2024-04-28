package com.easy.api.convert.util;

public class JSONEncodeUtil {
	
	
	/**
     * 将奇数个转义字符变为偶数个
     * @param s
     * @return
     */
    public static String getDecodeJSONStr(String s){
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
            case '\\':
                sb.append("\\\\");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    
    
    /**
     * 将奇数个转义字符4个 转义后为2个
     * @param s
     * @return
     */
    public static String getDecodeJSONStr2(String s){
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
            case '\\':
                sb.append("\\\\\\\\");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
