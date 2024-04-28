package com.easy.api.convert.mess;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.annotation.WebServlet;
import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/TestDBLink")
public class TestDBLink extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		try{			
			/*JSONObject jsons = new JSONObject((String)getEnvDatas().get("CommandData"));
			JSONArray jsonarr = (JSONArray)jsons.get("MESSDATA");
			JSONObject json = jsonarr.getJSONObject(0);*/
			//String strStype = GetDataValue("MESSDATA", "SOURCEFILETYPE");//获取源文件类型：1.xml、2.excel、3.db数据库
			String strSendType = GetDataValue("MESSDATA", "SENDTYPE");//发送方类型
			String strSendDress = GetDataValue("MESSDATA", "SENDADRESS");//发送方地址  
			//sqlserver连接地址方式：jdbc:sqlserver://localhost:1433;DatabaseName=People
			//oracle连接地址方式：jdbc:oracle:thin:@localhost:1521:orcl;
			//mysql连接地址方式：localhost:3306/mysql?user=root&password=wb
			String strSendName = GetDataValue("MESSDATA", "SENDUSERNAME");//发送方用户名
			String strSendPwd = GetDataValue("MESSDATA", "SENDUSERPWD");//发送方密码	
			if(SysUtility.isEmpty(strSendType) || SysUtility.isEmpty(strSendDress) || SysUtility.isEmpty(strSendName) || SysUtility.isEmpty(strSendPwd)){
				ReturnMessage(false,"发送方类型、发送方地址、用户名、密码不允许为空");
				return;
			}
			String url;//数据库连接地址
			String ip = "";//IP地址
			String port = "";//端口
			String table = "";//表名|实例名
			if(strSendDress.split(":").length!=3) {
				ReturnMessage(false,"请按照格式填写发送方地址  !");
				return;
			}else {
				ip = strSendDress.split(":")[0];
				port = strSendDress.split(":")[1];
				table = strSendDress.split(":")[2];
			}
			if(!"6".equals(strSendType) 
					&& !"7".equals(strSendType) 
					&& !"8".equals(strSendType)){	
				ReturnMessage(false,"请确认发送方类型是否为：sqlserver、oracle、mysql");
				return;
			}
			if("6".equals(strSendType)){//IP：端口：数据库表名					
				 Connection conn=null;
				 Boolean isSuccess  = false;
				
				 try
				    {
					// 加载驱动
				    	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
				    }
					catch(ClassNotFoundException ex)
					{
						ReturnMessage(false,"加载sqlserver驱动错误");
						return;
					}	
				
				 try {
					 url = ip+":"+port+";DatabaseName="+table;
					 conn = DriverManager.getConnection("jdbc:sqlserver://"+url, strSendName, strSendPwd);
					 if(conn!=null)
					 {
						 isSuccess = true;
					 }
					 conn.close();
			        } catch (Exception e) {
			        	ReturnMessage(isSuccess,e.getMessage());
			            e.printStackTrace();  
			            return;
			        } finally {  
			  
			            try {  
			                if (conn != null)  
			                    conn.close();  
			            } catch (SQLException e) {  
			                e.printStackTrace();  
			            }  
			        }  				 				
				 if(isSuccess){ReturnMessage(isSuccess,"连接成功") ;}else{ReturnMessage(isSuccess,"连接失败") ;}
			}
			if("7".equals(strSendType)){
				//数据源为oracle	 //iP：端口：实例
			    Connection conn = null;  
			    Boolean isSuccess  = false;
			   
			    try
			    { 
			    	// 加 载驱动  
			    	 Class.forName("oracle.jdbc.driver.OracleDriver");  
			    }
				catch(ClassNotFoundException ex)
				{
					ReturnMessage(false,"加载oracle驱动错误");
				}	   
	           
		        try {  
		        	 url = ip+":"+port +":"+table;
		            // 通过驱动管理类获取数据库连接  
		            conn = DriverManager.getConnection("jdbc:oracle:thin:@"+url, strSendName, strSendPwd);		            
		            if(conn!=null){isSuccess = true;}
		             
		        } 
		        catch (Exception e) {
		        	
		        }  
		        finally
		        {		        	
		        	try {  
		        		// 关闭连接 
		                if (conn != null)  
		                    conn.close();  
		                
		            } catch (SQLException e) {  
		                e.printStackTrace();  
		            }  
		        }
		        if(isSuccess){ReturnMessage(isSuccess,"连接成功") ;}else{ReturnMessage(isSuccess,"连接失败") ;}
			}
			if("8".equals(strSendType)){
				//数据源为mysql	//ip ：端口：数据库名
				  Connection conn = null;  
				  Boolean isSuccess  = false;
				    
				try
				{
				Class.forName("com.mysql.jdbc.Driver"); 
				}
				catch(ClassNotFoundException ex)
				{
					ReturnMessage(false,"加载mysql驱动错误");
				}
				/* String url = "localhost:3306/mysql?user=root&password=wb"; url地址:端口/表名 
	                conn = DriverManager.getConnection(url);  
	                */
	                try {  
	 		           url = ip+":"+port+"/"+table;
			           // 通过驱动管理类获取数据库连接  
			           conn = DriverManager.getConnection("jdbc:mysql://"+url,strSendName,strSendPwd);		            
			           if(conn!=null) 
			           {isSuccess = true;}
			           conn.close();
			        } 
			         catch (Exception e) {  
			        	ReturnMessage(false, e.getMessage());
			        	e.printStackTrace();
			        	return;
			        }  
			        finally
			        {		        	
			        	try {  
			                if (conn != null)  
			                    conn.close();  
			            } catch (SQLException e) {  
			                e.printStackTrace();  
			            }  
			        }
			        if(isSuccess){ReturnMessage(isSuccess,"连接成功") ;}else{ReturnMessage(isSuccess,"连接失败") ;}
			}			
		}
		catch(Exception e){
			LogUtil.printLog("TestDBLink error!"+SysUtility.getStackTrace(e), Level.ERROR);
			ReturnMessage(false, e.getMessage());	
			
		}		
	}
	
	//测试驱动连接
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver"); 
		// 通过驱动管理类获取数据库连接  
		Connection conn = null; 
        conn = DriverManager.getConnection("jdbc:mysql://47.94.217.98:3306/r1","root","926453");
        conn.close();
	}
}
