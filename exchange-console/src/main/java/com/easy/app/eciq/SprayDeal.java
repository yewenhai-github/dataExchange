package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SprayDeal")
public class SprayDeal extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String CONT_ID = (String)getEnvDatas().get("INDX");
		String DECL_NO = (String)getEnvDatas().get("DECL_NO");
        if(SysUtility.isEmpty(CONT_ID)){
        	ReturnMessage(false, "参数错误,CONT_ID为空！");
        	return;
        }
        if(SysUtility.isEmpty(DECL_NO)){
        	ReturnMessage(false, "参数错误,DECL_NO为空！");
        	return;
        }
        
        JSONArray params1 = GetCommandData("key");
        String SprayPeople="";
        for (int k = 0; k < params1.length(); k++) {
	    	JSONObject childs=params1.getJSONObject(k);
	    	SprayPeople =(String)childs.getString("SprayPeople");
	    }
        
        String SelectSQL = "SELECT SPRAY_STATE_CODE FROM ITF_DCL_IO_DECL_CONT WHERE cont_id=? and decl_no=?";
        Datas datas = getDataAccess().GetTableDatas("ContList", SelectSQL,new String[]{CONT_ID,DECL_NO});
        String StatusCode = datas.GetTableValue("ContList", "SPRAY_STATE_CODE");
        if("1".equals(StatusCode)){
        	ReturnMessage(false, "集装箱已喷淋！");
        	return;
        }
        //业务逻辑
        String UpdateSQL = "update ITF_DCL_IO_DECL_CONT set SPRAY_STATE_CODE='1',SPRAY_STATE_NAME='已喷淋',SPRAY_DEAL_PEOPLE=?,SPRAY_DEAL_TIME=Now() where cont_id=? and decl_no=?";
        Object params = new Object[]{SprayPeople,CONT_ID,DECL_NO};
        boolean flag=getDataAccess().ExecSQL(UpdateSQL, params);

        if(flag){
			ReturnMessage(true, "操作成功！");
		}else{
			ReturnMessage(false, "操作失败！");
		}
	}
}
