package com.easy.api.convert.mess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
/**
 * 获取数据表信息
 * @author chenchuang
 *
 */
@WebServlet("/GetTableList")
public class GetTableList extends MainServlet{

	@SuppressWarnings("unchecked")
	@Override
	public void DoCommand() throws LegendException, Exception {
		final String INDX = getRequest().getParameter("INDX");
		final String TABLE = getRequest().getParameter("TABLE");
		if(SysUtility.isEmpty(INDX)) {
			ReturnMessage(false, "数据为空");
			return;
		}else if(TABLE != null && TABLE .equals("")) {
			ReturnMessage(true, "","","");
			return;
		}
		List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE INDX=?",new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, INDX);
			}
		});
		
		if(query4List.size() <1) {
			ReturnMessage(false, "数据为空");
			return;
		}
		HashMap Data = (HashMap)query4List.get(0);
		String strSendType = (String) Data.get("SENDTYPE");//发送方类型
		String strSendDress = (String) Data.get("SENDADRESS");//发送方地址
		String strSendName = (String) Data.get("SENDUSERNAME");//发送方用户名
		String strSendPwd = (String) Data.get("SENDUSERPWD");//发送方密码
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
		if("6".equals(strSendType)){//IP：端口：数据库表名  sqlservice
			Connection conn = null;  
			 Statement stmt = null;
			 try{
				 Class.forName("oracle.jdbc.driver.OracleDriver");  
			 }catch(ClassNotFoundException ex){
					ReturnMessage(false,"加载mysql驱动错误");
			 }
			 try {  
	        	 url = ip+":"+port +":"+table;
	            // 通过驱动管理类获取数据库连接  
	            conn = DriverManager.getConnection("jdbc:oracle:thin:@"+url, strSendName, strSendPwd);		            
	            if(conn!=null){
	            	   stmt = conn.createStatement();
		        	   String sql = "";
		        	   JSONArray array = null;
		        	   if(SysUtility.isEmpty(TABLE)) {
		        		   sql = "SELECT SOURCENOTENAME FROM exs_convert_config_name WHERE P_INDX =? AND (ISSUBLIST='1' OR ISSUBLIST='2' OR ISSUBLIST='3')";
		        		   List ConfigNameList = SQLExecUtils.query4List(sql,new Callback() {
							@Override
							public void doIn(PreparedStatement ps) throws SQLException {
								ps.setString(1, INDX);
							}
						});
		        		   if(ConfigNameList.size() > 0) {
		        			   array = new JSONArray();
		        			   for(int i =0; i<ConfigNameList.size(); i++) {
		        				   HashMap ConfigNameMap = (HashMap)ConfigNameList.get(i);
		        				   HashMap map = new HashMap();
		        				   String SOURCENOTENAME = (String) ConfigNameMap.get("SOURCENOTENAME");
		        				   if(!SysUtility.isEmpty(SOURCENOTENAME)) {
		        					   map.put("TABLENAME", SOURCENOTENAME.
		        							   split("\\&")[0]);  
		        					   array.put(map);
		        				   }
		        			   }
		        			   
		        		   }
//		        		   sql = "SELECT NAME TABLE_NAME  TABLE_NAME FROM SysObjects Where XType='U' ORDER BY Name";
		        	   }else {
		        		   sql = "SELECT NAME column_name FROM SYSCOLUMNS WHERE ID=OBJECT_ID('"+TABLE+"')";
		        		   ResultSet rs = stmt.executeQuery(sql);
		        		   array = new JSONArray();
			        	   while (rs.next()) {
			        		  HashMap map = new HashMap();
			        		  String column_name = rs.getString("column_name");
			        		  map.put("COLUMNNAME", column_name);
			        		  array.put(map);
			        	   }
		        	   }
		        	  
		        	   ReturnMessage(true, "","",array.toString());
		        	   return;
	            }
			 } catch (Exception e) {
		        	ReturnMessage(false, "连接失败!");
		     } finally
		        {		        	
		        	try {  
		        		// 关闭连接 
		                if (conn != null)  
		                    conn.close();  
		                
		            } catch (SQLException e) {  
		                e.printStackTrace();  
		            }  
		        } 
		}else if("7".equals(strSendType)) {//oracle
			 Connection conn = null;  
			 Statement stmt = null;
			 try{
				 Class.forName("oracle.jdbc.driver.OracleDriver");  
			 }catch(ClassNotFoundException ex){
					ReturnMessage(false,"加载mysql驱动错误");
			 }
			 try {  
	        	 url = ip+":"+port +":"+table;
	            // 通过驱动管理类获取数据库连接  
	            conn = DriverManager.getConnection("jdbc:oracle:thin:@"+url, strSendName, strSendPwd);		            
	            if(conn!=null){
	            	   stmt = conn.createStatement();
		        	   String sql = "";
		        	   JSONArray array = null;
		        	   if(SysUtility.isEmpty(TABLE)) {
		        		   sql = "SELECT SOURCENOTENAME FROM exs_convert_config_name WHERE P_INDX =? AND (ISSUBLIST='1' OR ISSUBLIST='2' OR ISSUBLIST='3')";
		        		   List ConfigNameList = SQLExecUtils.query4List(sql,new Callback() {
							@Override
							public void doIn(PreparedStatement ps) throws SQLException {
								ps.setString(1, INDX);
							}
						});
		        		   if(ConfigNameList.size() > 0) {
		        			   array = new JSONArray();
		        			   for(int i =0; i<ConfigNameList.size(); i++) {
		        				   HashMap ConfigNameMap = (HashMap)ConfigNameList.get(i);
		        				   HashMap map = new HashMap();
		        				   String SOURCENOTENAME = (String) ConfigNameMap.get("SOURCENOTENAME");
		        				   if(!SysUtility.isEmpty(SOURCENOTENAME)) {
		        					   map.put("TABLENAME", SOURCENOTENAME.
		        							   split("\\&")[0]);  
		        					   array.put(map);
		        				   }
		        			   }
		        			   
		        		   }
//		        		   sql = "select t.TABLE_NAME TABLE_NAME  from user_tables t";
		        	   }else {
		        		   sql ="select t.column_name column_name from user_col_comments t where t.table_name = '"+TABLE+"'";
		        		   ResultSet rs = stmt.executeQuery(sql);
		        		  /* int row = rs.getRow();
		        		   if(row == 0) {
		        			   sql ="select t.column_name column_name from user_col_comments t where t.table_name = '"+TABLE.toUpperCase()+"'"; 
		        			   rs = stmt.executeQuery(sql);
		        			   row = rs.getRow();
		        			   if(row == 0) {
			        			   sql ="select t.column_name column_name from user_col_comments t where t.table_name = '"+TABLE.toLowerCase()+"'"; 
			        			   rs = stmt.executeQuery(sql);
			        		   }
		        		   }*/
		        		   array = new JSONArray();
			        	   while (rs.next()) {
			        		  HashMap map = new HashMap();
		        			  String column_name = rs.getString("column_name");
		        			  map.put("COLUMNNAME", column_name);
			        		  array.put(map);
			        	   }
			        	   
			        	   if(array .length() <= 0) {
			        		   sql ="select t.column_name column_name from user_col_comments t where t.table_name = '"+TABLE.toUpperCase()+"'"; 
		        			   rs = stmt.executeQuery(sql);
		        			   while (rs.next()) {
					        		  HashMap map = new HashMap();
				        			  String column_name = rs.getString("column_name");
				        			  map.put("COLUMNNAME", column_name);
					        		  array.put(map);
					           }
		        			   if(array .length() <= 0) {
		        				   sql ="select t.column_name column_name from user_col_comments t where t.table_name = '"+TABLE.toLowerCase()+"'"; 
			        			   rs = stmt.executeQuery(sql);
			        			   while (rs.next()) {
						        		  HashMap map = new HashMap();
					        			  String column_name = rs.getString("column_name");
					        			  map.put("COLUMNNAME", column_name);
						        		  array.put(map);
						           }
		        			   }
			        	   }
		        	   }
		        	  
		        	   ReturnMessage(true, "","",array.toString());
		        	   return;
	            }
	             
	        } 
	        catch (Exception e) {
	        	ReturnMessage(false, "连接失败!");
	        } finally
	        {		        	
	        	try {  
	        		if (stmt != null)
	        			stmt.close();
	        		// 关闭连接 
	                if (conn != null)  
	                    conn.close();  
	                
	            } catch (SQLException e) {  
	                e.printStackTrace();  
	            }  
	        }  
		}else if("8".equals(strSendType)) {//mysql
			 Connection conn = null;  
			 Statement stmt = null;
			 try{
				Class.forName("com.mysql.jdbc.Driver"); 
			 }catch(ClassNotFoundException ex){
					ReturnMessage(false,"加载mysql驱动错误");
			 }
			 try {  
		           url = ip+":"+port+"/"+table;
		           // 通过驱动管理类获取数据库连接  
		           conn = DriverManager.getConnection("jdbc:mysql://"+url,strSendName,strSendPwd);		            
		           if(conn!=null){
		        	   stmt = conn.createStatement();
		        	   String sql = "";	
		        	   JSONArray array = null;
		        	   if(SysUtility.isEmpty(TABLE)) {
		        		   sql = "SELECT SOURCENOTENAME FROM exs_convert_config_name WHERE P_INDX =? AND (ISSUBLIST='1' OR ISSUBLIST='2' OR ISSUBLIST='3')";
		        		   List ConfigNameList = SQLExecUtils.query4List(sql,new Callback() {
							@Override
							public void doIn(PreparedStatement ps) throws SQLException {
								ps.setString(1, INDX);
							}
						});
		        		   if(ConfigNameList.size() > 0) {
		        			   array = new JSONArray();
		        			   for(int i =0; i<ConfigNameList.size(); i++) {
		        				   HashMap ConfigNameMap = (HashMap)ConfigNameList.get(i);
		        				   HashMap map = new HashMap();
		        				   String SOURCENOTENAME = (String) ConfigNameMap.get("SOURCENOTENAME");
		        				   if(!SysUtility.isEmpty(SOURCENOTENAME)) {
		        					   map.put("TABLENAME", SOURCENOTENAME.
		        							   split("\\&")[0]);  
		        					   array.put(map);
		        				   }
		        			   }
		        			   
		        		   }
//		        		   sql = "select TABLE_NAME from information_schema.TABLES WHERE table_schema = '"+table+"'";
		        	   }else {
		        		   sql = "select column_name from information_schema.columns where table_name ='"+TABLE+"' and table_schema='"+table+"'";
		        		   ResultSet rs = stmt.executeQuery(sql);
		        		   array = new JSONArray();
			        	   while (rs.next()) {
			        		  HashMap map = new HashMap();
		        			  String column_name = rs.getString("column_name");
		        			  map.put("COLUMNNAME", column_name);
			        		  array.put(map);
			        	   }
		        	   }
		        	  
		        	  
		        	   ReturnMessage(true, "","",array.toString());
		        	   return;
		           }
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
			 
		}
	}
	
	

}
