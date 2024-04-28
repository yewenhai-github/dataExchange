package com.easy.api.getlist;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetSchemaList")
public class GetSchemaList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		Datas datas = new Datas();
		String TableName = "rows";
		String schema = GetToSearchTable("SCHEMA");
		if(SysUtility.ProxoolPool){
    		List rtLst = SysUtility.GetProxoolCount(this, "proxool.properties");
    		HashMap SchemaMap = SysUtility.GetProxoolSchema(this, "proxool.properties");
    		HashMap SchemaNameMap = SysUtility.GetProxoolValue(this, "proxool.properties","aliasName");
    		
    		if(SysUtility.isNotEmpty(schema)){
    			for (int i = 0; i < rtLst.size(); i++) {
        			String dbName = (String)rtLst.get(i);
        			if(schema.equals(SchemaMap.get(dbName))){
        				datas.SetTableValue(TableName, "SCHEMA", SchemaMap.get(dbName), 0);
        				datas.SetTableValue(TableName, "SCHEMA_NAME", SchemaNameMap.get(dbName), 0);
        				break;
        			}
    			}
    		}else{
    			for (int i = 0; i < rtLst.size(); i++) {
        			String dbName = (String)rtLst.get(i);
        			datas.SetTableValue(TableName, "SCHEMA", SchemaMap.get(dbName), i);
        			datas.SetTableValue(TableName, "SCHEMA_NAME", SchemaNameMap.get(dbName), i);
    			}
    		}
		}
		
		ReturnWriter(datas.getDataJSON().toString());
	}

}