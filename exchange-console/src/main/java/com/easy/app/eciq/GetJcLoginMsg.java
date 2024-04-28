package com.easy.app.eciq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.web.MainServlet;
/**
 * 获取九城登录信息
 */
@WebServlet("/forms/GetJcLoginMsg")
public class GetJcLoginMsg  extends MainServlet{

	private static final long serialVersionUID = -223265283712360300L;

	@Override
	protected void DoCommand() throws LegendException, Exception {
		//doGet(getRequest(), getResponse());
			String result = "";
	        BufferedReader in = null;
	        String sessionid  = "";
	        try {
	        	String userName = getRequest().getParameter("username");
	        	String passWord = getRequest().getParameter("password");
	            String urlNameString = "http://61.178.78.93:8000/loginController.do?checkentreg&declPersonCode=%s&loginPassP=%s";
	            urlNameString = String.format(urlNameString, userName,passWord);
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	           // connection.setRequestProperty("Cookie", "JSESSIONID="+getRequest().getSession().getId());
	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
//	            // 遍历所有的响应头字段
//	            for (String key : map.keySet()) {
//	                System.out.println(key + "--->" + map.get(key));
//	            }
	            List<String> JsesessionId = map.get("Set-Cookie"); 
	            sessionid = JsesessionId.get(0).split(";")[0].split("=")[1];
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream(),"utf-8"));
	            String line;
	            while ((line = in.readLine()) != null) {     
	                result += line;
	            }
	            JSONObject json = new JSONObject(result);
	            if(!json.getBoolean("success")){
	            	ReturnMessage(false,json.getString("msg"));
	            }
	        } catch (Exception e) {
	        	ReturnMessage(false,"连接超时,请稍后再试");;
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                	//doGet(getRequest(), getResponse(), sessionid);
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        ReturnMessage(true,"","",result.toString());;
	}
	
	
	
	
	 public void doGet(HttpServletRequest request, HttpServletResponse response,String sessionid) throws IOException {
		 String loginAction = "http://61.178.78.93:8000/tdeclInfoController.do?datagridSel&declTypeCode=2&field=declNo,declGetNo,declDate,declPersonName,consignorCname,orgName,inspOrgName,destOrgName,certOrgName,";  
		 URL realUrl = new URL(loginAction);
         // 打开和URL之间的连接
         URLConnection connection = realUrl.openConnection();
         // 设置通用的请求属性
         connection.setRequestProperty("accept", "*/*");
         connection.setRequestProperty("connection", "Keep-Alive");
         connection.setRequestProperty("user-agent",
                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
         connection.setRequestProperty("Cookie", "JSESSIONID="+sessionid);
         // 建立实际的连接
         connection.connect();
         // 获取所有响应头字段
         Map<String, List<String>> map = connection.getHeaderFields();
         BufferedReader in = null;
         in = new BufferedReader(new InputStreamReader(
                 connection.getInputStream(),"utf-8"));
         String line;
         String result="";
         while ((line = in.readLine()) != null) {
             result += line;
         }
         System.out.println(result.toString());
	 }




	public static String getCookie() throws Exception{  
		 //登录  
	        URL url = new URL("http://61.178.78.93:8000/loginController.do");
	        String param = "checkentreg&password=declPersonCode=6200700327&loginPassP=123456";  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoInput(true);  
	        conn.setDoOutput(true);  
	        conn.setRequestMethod("POST");  
	        OutputStream out = conn.getOutputStream();  
	        out.write(param.getBytes());  
	        out.flush();  
	        out.close(); 
	        String sessionId = "";  
	        String cookieVal = "";  
	        String key = null;  
	        //取cookie  
	        for(int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++){  
	            if(key.equalsIgnoreCase("set-cookie")){  
	                cookieVal = conn.getHeaderField(i);  
	                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));  
	                sessionId = sessionId + cookieVal + ";";  
	            }  
	        }  
	        return sessionId; 
	 }
	
	
	

}
