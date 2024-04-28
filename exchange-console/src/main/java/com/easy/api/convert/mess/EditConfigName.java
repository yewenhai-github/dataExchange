package com.easy.api.convert.mess;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
import com.sun.org.apache.bcel.internal.generic.NEW;

@WebServlet("/EditConfigName")
public class EditConfigName extends MainServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	protected void DoCommand() throws LegendException, Exception {
		String indx = (String)getEnvDatas().get("INDX");
		String SOURCENOTENAME = (String)getEnvDatas().get("SOURCENOTENAME");
		if(SysUtility.isNotEmpty(SOURCENOTENAME)) {
			boolean execSQL = getDataAccess().ExecSQL("UPDATE exs_convert_config_name SET SOURCENOTENAME =? WHERE INDX=?", new String[]{SOURCENOTENAME,indx});
			ReturnMessage(execSQL, "处理完成");
			return;
		}else {
			String  sql  = "@SELECT * FROM exs_convert_config_name  T   WHERE	T.INDX ='" + indx + "'";
			JSONObject jsonObj = GetReturnDatas(sql);
			JSONArray jsonArr = jsonObj.getJSONArray("rows");
			jsonObj.put("CONFIGNAME", jsonArr);
			ReturnMessage(true, "", "", jsonObj.toString());
			return;
		}
			
	}
	
	
	public static void main(String[] args) throws JSONException {
		String string = "{'a':'/a'}";
		string2Json("{'a':'/a'}");
		JSONObject jsonObject = new JSONObject(string);
		System.out.println(jsonObject.toString());
	}
	
	public  static String string2Json(String s) {        
        StringBuffer sb = new StringBuffer();        
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);    
             switch (c){  
             case '\"':        
                 sb.append("\\\"");        
                 break;        
             case '\\':        
                 sb.append("\\\\");        
                 break;        
             case '/':        
                 sb.append("\\/");        
                 break;        
             case '\b':        
                 sb.append("\\b");        
                 break;        
             case '\f':        
                 sb.append("\\f");        
                 break;        
             case '\n':        
                 sb.append("\\n");        
                 break;        
             case '\r':        
                 sb.append("\\r");        
                 break;        
             case '\t':        
                 sb.append("\\t");        
                 break;        
             default:        
                 sb.append(c);     
             }  
         }      
        return sb.toString();     
        }  
}
