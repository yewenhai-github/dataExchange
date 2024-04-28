package com.easy.app.eciq;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.app.utility.Utility;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

/***
 * 功能按钮：查验分发指令
 * 
 * **/
@WebServlet("/forms/UpdateCfBatchDistributeDo")
public class UpdateCfBatchDistributeDo extends MainServlet {
private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		JSONArray params = null;
		String deptId=null;
		String declNo =GetDataValue("SearchTable", "declNo");//详细页触发
		deptId = GetDataValue("SearchTable", "ORG_ID");
		if(SysUtility.isEmpty(declNo)){
			ReturnMessage(false, "当前操作操作失败！传输数据行数0");
			return;
		}
		int SuccessCount = 0;//符合更新条件的数据总数。
		int ErrorCount = 0;//不符合更新条件的数据总数。
		String SQL = "SELECT CF_TYPE_CODE,DECL_STATUS_CODE,DECL_NO FROM ITF_DCL_IO_DECL WHERE 1 = 1 AND DECL_NO='"+declNo+"'";
		List lst = SQLExecUtils.query4List(SQL);
		String compareStatus = "0,1";
		for (int i = 0; i < lst.size(); i++) {
			HashMap map = (HashMap)lst.get(i);
			String cfTypeCode = (String)map.get("CF_TYPE_CODE");
			String DECL_STATUS_CODE = (String)map.get("DECL_STATUS_CODE");
			String DECL_NO = (String)map.get("DECL_NO");
			//String messageDest = (String)map.get("MESSAGE_DEST");
			String SysGuid = SysUtility.GetOracleSysGuid();
			if("0,1".indexOf(DECL_STATUS_CODE)>= 0 &&compareStatus.indexOf(cfTypeCode) >= 0){
				SuccessCount++;
				Utility.InsertTempId(getDataAccess(), DECL_NO,SysGuid,"","");
			}else{
				ErrorCount++;
			}
		}
		if(SuccessCount == 0){
			ReturnMessage(true, "没有符合条件的数据，更新失败"+ErrorCount+"条数据");
			return;
		}
		
		//更新数据库
		String msgCode = "1";
		String msgName = "待查验";
		String msgDesc = "待查验指令发送成功";
		StringBuffer UpdateSQL = new StringBuffer();
		UpdateSQL.append("UPDATE ITF_DCL_IO_DECL ");
		UpdateSQL.append("   SET CF_TYPE_CODE = ?,");
		UpdateSQL.append("       CF_TYPE_NAME = ?,");
		UpdateSQL.append("       CF_PERSON = ?,");
		UpdateSQL.append("       part_id= ?,");
		UpdateSQL.append("       CF_TIME = Now()");
		UpdateSQL.append(" WHERE CF_TYPE_CODE = ? ");
		UpdateSQL.append("   AND DECL_NO IN (SELECT ID1 FROM T_TEMP_ID)");
		Object UpdateParams = new Object[]{"1","待查验",SysUtility.getCurrentName(),deptId,"0"};
		getDataAccess().ExecSQL(UpdateSQL.toString(), UpdateParams);
		//根据临时表批量插入状态历史
		Utility.InsertHistoryStatusByTemp(getDataAccess(), "ITF_DCL_IO_DECL", "DECL_NO", msgName, msgDesc);
		//根据临时表批量插入回执表
		Utility.InsertMfReceiptByTemp(getDataAccess(), "200", msgCode, msgName, msgDesc);
		Utility.InsertDeclHandleByTemp(getDataAccess(), "200");
		//返回
		if(ErrorCount > 0){
			ReturnMessage(true, "更新成功"+SuccessCount+"条数据,更新失败"+ErrorCount+"条数据");
		}else{
			ReturnMessage(true, "更新成功"+SuccessCount+"条数据");
		}
	}
}
