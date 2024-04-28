package com.easy.app.eciq;
 
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

import java.net.*;
@WebServlet("/forms/GetInspContainerByIndx")
public class GetInspContainerByIndx extends MainServlet {

	private static final long serialVersionUID = 2021168339536873343L;
	public GetInspContainerByIndx()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception {
		InitFormData("ContainerData", SQLMap.getSelect("GetInspContainerById"));
		String contId=getFormDatas().GetTableValue("ContainerData", "CONT_ID");
		String declNo=getFormDatas().GetTableValue("ContainerData", "DECL_NO");
		JSONObject contdetailList= getDataAccess().GetTableJSON("contDetail", "SELECT * FROM ITF_DCL_IO_DECL_CONT_DETAIL where CONT_ID=?",new Object[]{contId});
		String INFOJSON="{\"INFOJSON\":[";
		if(SysUtility.isNotEmpty(contdetailList.getJSONArray("contDetail")) && contdetailList.getJSONArray("contDetail").length()>0){
			for (int i = 0; i < contdetailList.getJSONArray("contDetail").length(); i++) {
				if(i<contdetailList.getJSONArray("contDetail").length()-1){
					INFOJSON+="{\"SEQ_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("SEQ_NO")+"\""
							+ ",\"CONT_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CONT_NO")+"\""
							+ ",\"LCL_FLAG\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("LCL_FLAG")+"\""
							+ ",\"CONT_ID\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CONT_ID")+"\""
							+ ",\"DECL_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("DECL_NO")+"\""
							+ ",\"CNTNR_MODE_CODE\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CNTNR_MODE_CODE")+"\"}";
					INFOJSON+=",";
				}else{
					INFOJSON+="{\"SEQ_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("SEQ_NO")+"\""
							+ ",\"CONT_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CONT_NO")+"\""
							+ ",\"LCL_FLAG\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("LCL_FLAG")+"\""
							+ ",\"CONT_ID\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CONT_ID")+"\""
							+ ",\"DECL_NO\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("DECL_NO")+"\""
							+ ",\"CNTNR_MODE_CODE\":\""+contdetailList.getJSONArray("contDetail").getJSONObject(i).get("CNTNR_MODE_CODE")+"\"}";
				}
			}
			INFOJSON+="]}";
		}
		INFOJSON=URLEncoder.encode(INFOJSON, "UTF-8");
	    SaveToTable("ContainerData","INFOJSON",INFOJSON);
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
