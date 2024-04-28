package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetIISaveCommonGoodsLimit")
public class GetIISaveCommonGoodsLimit extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetIISaveCommonGoodsLimit()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		
		if(SysUtility.isEmpty(GetDataValue("LimitData", "GOODS_ID"))){
			ReturnMessage(true, "请先选择商品信息");
			return;
		}
		
		if (SysUtility.isEmpty(GetDataValue("LimitData", "LIMIT_ID"))) {
			 
        }
		
		JSONArray insertData = new JSONArray();
		JSONArray updateDate = new JSONArray();
		//动态设置主键
		String TableName = "LimitData";
		String seqSql = "select seq_itf_limit_id.nextval limit_id from dual";
		
		JSONArray rows = getFormDatas().getJSONArray(TableName);
		for (int i = 0; i < rows.length(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			String tempId = SysUtility.getJsonField(row, "LIMIT_ID");
			if(SysUtility.isEmpty(tempId)){
				Datas data = getDataAccess().GetTableDatas("data", seqSql);
				SysUtility.putJsonField(row, "LIMIT_ID", data.GetTableValue("data", "LIMIT_ID"));
				insertData.put(row);
			}else{
				updateDate.put(row);
			}
		}
		if(insertData.length()>0){
			boolean InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_LIMIT", insertData,"LIMIT_ID");
			  if (!SysUtility.isEmpty(InsertRt))
			     {
			        	ReturnMessage(true, "保存成功");
			     }
			     else
			     {
			        	ReturnMessage(false, "保存失败");	
			     }
		}else{
			boolean updateRt = getDataAccess().Update("ITF_DCL_IO_DECL_GOODS_LIMIT", updateDate,"LIMIT_ID");
			 if (!SysUtility.isEmpty(updateRt))
		     {
		        	ReturnMessage(true, "保存成功");
		     }
		     else
		     {
		        	ReturnMessage(false, "保存失败");	
		     }
		}
		
		
		
		
	   
	}
			
}