package com.easy.app.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/base/SaveDingTalkEdit")
public class SaveDingTalkEdit extends MainServlet {
	public void DoCommand() throws Exception {
		
		String INDX = getRequest().getParameter("INDX");
		String NAME = getRequest().getParameter("NAME");

		JSONObject jsonString = new JSONObject();
		jsonString = GetObj();
		if(INDX==null){
			String sql1 = "select * from B_MESSAGE_TEMPLATE where name = '" + NAME + "' AND IS_ENABLED = 1";
			List lst = SQLExecUtils.query4List(sql1);
			if(lst.size()>0)
			{
				ReturnMessage(false, "此名称已存在!不可重复添加");
			    return;
			}
		}
		
		String NEW_INDX="";
		
		if(INDX.length()<=0||INDX==null){
			jsonString.put("CREATED_BY_NAME", SysUtility.getCurrentName());
			jsonString.put("CREATOR", SysUtility.getCurrentUserIndx());
//			jsonString.put("ORG_ID", SysUtility.getCurrentOrgId());
//			jsonString.put("DEPT_ID", SysUtility.getCurrentDeptId());
			getDataAccess().Insert("B_MESSAGE_TEMPLATE", jsonString);
			NEW_INDX=jsonString.getString("Indx");
			ReturnMessage(true, "保存成功","Insert",NEW_INDX);
		}
		else{
			jsonString.put("INDX", INDX);
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonString.put("MODIFYOR", SysUtility.getCurrentUserIndx());
			jsonString.put("MODIFY_DATE", time);
			getDataAccess().Update("B_MESSAGE_TEMPLATE", jsonString);
			NEW_INDX=jsonString.getString("INDX");
			ReturnMessage(true, "保存成功","Update",NEW_INDX);
		}

	}

	public JSONObject GetObj() throws Exception {

		String NAME = getRequest().getParameter("NAME");
		String TEMPLATE = getRequest().getParameter("TEMPLATE");
		String TOROLES = getRequest().getParameter("TOROLES");
		String TOUSERS = getRequest().getParameter("TOUSERS");
		String REMARK = getRequest().getParameter("REMARK");



		JSONObject jsonString = new JSONObject();

		jsonString.put("NAME", NAME);
		jsonString.put("TEMPLATE", TEMPLATE);
		jsonString.put("TOROLES", TOROLES);
		jsonString.put("TOUSERS", TOUSERS);
		jsonString.put("REMARK", REMARK);
//		jsonString.put("ORG_CODE", SysUtility.getCurrentOrgId());


		return jsonString;
	}
}
