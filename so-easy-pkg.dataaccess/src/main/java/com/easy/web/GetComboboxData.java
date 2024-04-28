package com.easy.web;

import java.sql.Connection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.constants.Constants;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class GetComboboxData extends MainServlet {

	private static final long serialVersionUID = 1L;

	protected void DoCommand() throws Exception{
		IDataAccess DataAccess = getDataAccess();
		HttpServletRequest Request = getRequest();
		StringBuilder sql = new StringBuilder();
		
		Connection conn = null;
		try {
			String DataName = Request.getParameter("DataName");
			String Columns = Request.getParameter("Columns");
			String CheckField = Request.getParameter("CheckField");
			String CheckAccurateField = Request.getParameter("CheckAccurateField");
			String Value = Request.getParameter("Value");
			String[] cols = Columns.split(",");
			String OtherChecks = Request.getParameter("OtherCheckFields");
	        String OtherValues = Request.getParameter("OtherCheckValues");
	        String QueryData=Request.getParameter("QueryData");
	        
	        String OrderField=Request.getParameter("OrderField");
	        
	        String PageSize=Request.getParameter("PageSize");
	        String DBName=Request.getParameter("Conn");
	        if(SysUtility.isNotEmpty(DBName)){
	        	conn = SysUtility.getCurrentConnection(DBName);
			}
	        String[] oc = new String[]{};
			String[] ov = new String[]{};
	        if (SysUtility.isNotEmpty(OtherChecks) && SysUtility.isNotEmpty(OtherValues)){
	            ov = OtherValues.split(",");
	            oc = new String[ov.length];
	            //涓嬫媺妗嗕紶鍏ユ潯浠朵负绌猴紝榛樿涓嶆嫾鎺ユ垚where鏉′欢
	            for (int i = 0; i < ov.length; i++) {
	            	oc[i] = OtherChecks.split(",")[i];
				}
	        }
	        String condition="";
			if(SysUtility.isNotEmpty(QueryData)){
				String query = QueryData;
				if (query != null){
					try {
						JSONObject Datas;
						Datas = new JSONObject(query);
						Iterator<?> keys = Datas.keys();
						if(keys.hasNext()){
							String key = keys.next().toString();
							JSONArray rows = Datas.getJSONArray(key);
							if(rows.length() > 0){
								condition = GetConditionString(rows.getJSONObject(0),false);
							}
						}
					} catch (JSONException e) {
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					}
				}
			}
			String top="";
			String rowNum="";
			if (SysUtility.IsSQLServerDB()){
				top = "top 10";
			}else if (SysUtility.IsOracleDB()){
				if(PageSize.isEmpty())
				   rowNum = "rownum <= 10 and ";
				else
					rowNum = "rownum <= "+PageSize+" and ";
			}	
			//distinct
			String TableSql="";
			if(!OrderField.isEmpty())
				TableSql="(Select * from  " + DataName + " order by "+OrderField+") ";
			else
				TableSql=DataName;
			if(!condition.isEmpty()){
				       sql.append("Select  "+top+" "+Columns+",A.* FROM " + TableSql + " A  Where "+rowNum+" " + condition+ " and (");
			}else{
				  sql.append("Select  "+top+" "+Columns+",A.* FROM " + TableSql + " A  Where "+rowNum+"(");
			}
			//if(!OrderField.isEmpty())
				
			if (SysUtility.isEmpty(CheckField) || SysUtility.isEmpty(Value)) {
				if (SysUtility.isNotEmpty(CheckAccurateField) && SysUtility.isNotEmpty(Value)) {
					String[] tm = CheckAccurateField.split(",");
					String[] ckv = new String[tm.length];
					for (int i = 0; i < tm.length; i++) {
						if(i == 0){
							sql.append(tm[i] + " like ?");
						}else{
							sql.append(" OR "+tm[i] + " like ?");
						}
						ckv[i] = Value+"";
					}
					sql.append(")");
					
					String name="";
					for (int i = 0; i < ckv.length; i++) {
						name+=ckv[i]+",";
					}
					if(!SysUtility.isEmpty(name)){
						name=name.substring(0,name.length()-1);
					}
//					if(!OrderField.isEmpty())
//						sql=
					String rt = DataAccess.GetTable(conn, DataName, sql.toString(), ckv);
					ReturnMessage(true, "", "", rt, null,"","");		
				} else {
					String lk = "";
					String[] vs = new String[oc.length];
					if(oc.length == 0){
						sql.append(" 1 = 1 ");
					}else{
						 vs = new String[oc.length+cols.length];
						for (int i = 0; i < cols.length; i++) {
							if(cols[i].indexOf(" as ") != -1)
								sql.append(lk + cols[i].substring(0,cols[i].indexOf(" as ")) + " like ?");
							else
							    sql.append(lk + cols[i] + " like ?");
							//sql.append(lk + cols[i] + " like ?");
							vs[i] = "%" + Value + "%";
							lk = " OR ";
						}
					}
					sql.append(")");
					lk = " AND ";
		            for (int i = 0; i < oc.length; i++)
		            {
		            	sql.append(lk + oc[i] + " = ?");
		                vs[cols.length + i] = ov[i];
		            }
					String name="";
					for (int i = 0; i < vs.length; i++) {
						name+=vs[i]+",";
					}
					if(!SysUtility.isEmpty(name)){name=name.substring(0,name.length()-1);}
					String rt = DataAccess.GetTable(conn, DataName, sql.toString(), vs);
					ReturnMessage(true, "", "", rt, null,"","");
				}
			} else {
				String[] tm = CheckField.split(",");
				String[] ckv = new String[tm.length];
				for (int i = 0; i < tm.length; i++) {
					if(i == 0){
						sql.append(tm[i] + " like ?");
					}else{
						sql.append(" OR "+tm[i] + " like ?");
					}
					///ckv[i] = Value;
					ckv[i] = "%"+Value+"%";
				}
				sql.append(")");
	
				String name="";
				for (int i = 0; i < ckv.length; i++) {
					name+=ckv[i]+",";
				}
				if(!SysUtility.isEmpty(name)){
					name=name.substring(0,name.length()-1);
				}
				String rt = DataAccess.GetTable(conn, DataName, sql.toString(), ckv);
				ReturnMessage(true, "", "", rt, null,"","");
			}
		} catch (Exception e) {
			LogUtil.printLog(getRequest().getRequestURI()+":"+e.getMessage(), Level.ERROR);
			ReturnMessage(false,"Error:"+e.getMessage(),"","","");
		} finally{
			SysUtility.closeActiveCN(conn);
		}
	}
}
