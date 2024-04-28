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


@WebServlet("/base/SaveCostNameList")

public class SaveCostNameList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = getRequest().getParameter("INDX");
		String FRT_CODE=getRequest().getParameter("FRT_CODE");
		String FRT_ABBREV=getRequest().getParameter("FRT_ABBREV");
		String FRT_NAME_EN=getRequest().getParameter("FRT_NAME_EN");
		String FRT_SYSTEM_TYPE=getRequest().getParameter("FRT_SYSTEM_TYPE");
		String REMARK=getRequest().getParameter("REMARK");
		String ORDERNUM=getRequest().getParameter("ORDERNUM");
		JSONObject jsonHXDETAIL = new JSONObject();
		
		
		if(INDX == null ||"".equals(INDX.trim()))
		{
			String SQL = "select * from B_FRTDEF where is_enabled=1 and FRT_CODE='"+FRT_CODE+"' or FRT_ABBREV='"+FRT_ABBREV+"'";
		List lst = SQLExecUtils.query4List(SQL);
		if(lst.size()>0)
		{
			ReturnMessage(false, "存在同名模块，请确认！");
		    return;
		}
		
		else{
			jsonHXDETAIL.put("FRT_CODE", FRT_CODE);
			jsonHXDETAIL.put("FRT_ABBREV", FRT_ABBREV);
			jsonHXDETAIL.put("FRT_NAME_EN", FRT_NAME_EN);
			jsonHXDETAIL.put("FRT_SYSTEM_TYPE", FRT_SYSTEM_TYPE);
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("ORDERNUM", ORDERNUM);
			jsonHXDETAIL.put("CREATOR", SysUtility.getCurrentUserIndx());
			getDataAccess().Insert("B_FRTDEF", jsonHXDETAIL);
		}
		}
		else{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonHXDETAIL.put("MODIFYOR", SysUtility.getCurrentUserIndx());
			jsonHXDETAIL.put("MODIFY_DATE", time);
			jsonHXDETAIL.put("INDX", INDX);
			jsonHXDETAIL.put("FRT_CODE", FRT_CODE);
			jsonHXDETAIL.put("FRT_ABBREV", FRT_ABBREV);
			jsonHXDETAIL.put("FRT_NAME_EN", FRT_NAME_EN);
			jsonHXDETAIL.put("FRT_SYSTEM_TYPE", "SAS");
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("ORDERNUM", ORDERNUM);
			getDataAccess().Update("B_FRTDEF", jsonHXDETAIL);
			
		}
		ReturnMessage(true, "保存成功!");
		}

}
