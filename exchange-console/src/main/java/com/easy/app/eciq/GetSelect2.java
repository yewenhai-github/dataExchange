package com.easy.app.eciq;

import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Iterator;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetSelect2")
public class GetSelect2 extends MainServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception{ 
		Connection conn = null;
		String sql="";
		try {
			String DBName=getRequest().getParameter("connName");
			if(SysUtility.isNotEmpty(DBName)){
				conn = SysUtility.getCurrentConnection(DBName);
			}
			String NAME=URLDecoder.decode(getRequest().getParameter("q"),"UTF-8");//下拉框搜索值
			String page=getRequest().getParameter("page");			//页数
			String dataSource=getRequest().getParameter("dataSource");//数据源
			String colums=getRequest().getParameter("colums");		//显示的列
			String condition=getRequest().getParameter("condition"); //条件
			String sort=getRequest().getParameter("sort");
			String showfields=getRequest().getParameter("showfields");
			if(sort == null || sort.length() <= 0)
			    sort = "INDX";
			String order=getRequest().getParameter("order");
			if(order == null || order.length() <= 0)
				order = "DESC";
			
			String [] showcolums=showfields.split(",");
			String [] searchcolums=colums.split(",");
			String zd="";
			if(showcolums.length>0){
				if(showcolums.length>2){
					for (int i = 0; i < showcolums.length; i++) {
						zd+=showcolums[i]+",";
					}
				}else{
					zd+=showcolums[0]+" AS id,";
					zd+=showcolums[1]+" AS text,";
				}
				zd=zd.substring(0, zd.length()-1);
			}
			//主sql
			if(SysUtility.isNotEmpty(condition)){
				 sql="SELECT "+zd+" FROM "+dataSource+" WHERE 1=1 AND "+condition+"";
			}else{
				 sql="SELECT "+zd+" FROM "+dataSource+" WHERE 1=1 ";
			}
			
			if(NAME != null && NAME.length() > 0){
				String nameSearch=" AND (";
				if(searchcolums.length>2){
					for (int i = 0; i < searchcolums.length; i++) {
						if(i<searchcolums.length-1){
							nameSearch+=searchcolums[i]+" like '%"+NAME+"%' OR ";
						}else{
							nameSearch+=searchcolums[i]+" like '%"+NAME+"%' ";
						}
					}
					nameSearch+=") ";
				}
				else{
					nameSearch+=searchcolums[0]+" like '%"+NAME+"%' or "+searchcolums[1]+" like '%"+NAME+"%'";
					nameSearch+=") ";
				}
				sql+=nameSearch;
			}
			sql=sql+" ORDER BY "+sort+" "+order+"";
			if(page == null || page.length() <= 0)
				page = "1";
			int num1=Integer.valueOf(page).intValue()-1;
			int num2=30;
			JSONObject js= getDataAccess().GetTableDatasUI(conn, "rows", sql, "", num1, num2, sort);
			ReturnMessage(true, "", "", js.toString(), null,"","");
			//ReturnWriter(js.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ReturnMessage(false,"下拉菜单sql语句："+sql.toString()+" /n错误信息："+e.getMessage(),"","","");
		}
		finally{
			SysUtility.closeActiveCN(conn);
		}
		
		
	}
}
