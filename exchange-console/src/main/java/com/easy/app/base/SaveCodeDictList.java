package com.easy.app.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLExecUtils;
import com.easy.web.MainServlet;


@WebServlet("/base/SaveCodeDictList")

public class SaveCodeDictList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = getRequest().getParameter("INDX");
		String CODE_VALUE=getRequest().getParameter("CODE_VALUE");
		String CODE_DISPLAY_VALUE_EN=getRequest().getParameter("CODE_DISPLAY_VALUE_EN");
		String CODE_DISPLAY_VALUE_CN=getRequest().getParameter("CODE_DISPLAY_VALUE_CN");
		String CODE_TYPE=getRequest().getParameter("CODE_TYPE");
		String CODE_ORDER=getRequest().getParameter("CODE_ORDER");
		JSONObject jsonHXDETAIL = new JSONObject();
		if(INDX == null||"".equals(INDX.trim()))
		{
			String SQL = "select * from S_BASE_CODE_TYPE where is_enabled=1 and CODE_TYPE='"+CODE_TYPE+"' and CODE_VALUE='"+CODE_VALUE+"'";
			List lst = SQLExecUtils.query4List(SQL);
			if(lst.size()>0)
			{
				ReturnMessage(false, "存在同ID字典，请确认！");
			    return;
			}
		
		else{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonHXDETAIL.put("CREATE_TIME", time);
			jsonHXDETAIL.put("CODE_VALUE", CODE_VALUE);
			jsonHXDETAIL.put("CODE_DISPLAY_VALUE_EN", CODE_DISPLAY_VALUE_EN);
			jsonHXDETAIL.put("CODE_DISPLAY_VALUE_CN", CODE_DISPLAY_VALUE_CN);
			jsonHXDETAIL.put("CODE_TYPE", CODE_TYPE);
			jsonHXDETAIL.put("CODE_ORDER", CODE_ORDER);
			//jsonHXDETAIL.put("CREATOR", SysUtility.getCurrentUserIndx());
			getDataAccess().Insert("S_BASE_CODE_TYPE", jsonHXDETAIL);
			
		}
		}
		else{
//			Date date=new Date();
//			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String time=format.format(date);
//			jsonHXDETAIL.put("MODIFYOR", SysUtility.getCurrentUserIndx());
//			jsonHXDETAIL.put("MODIFY_DATE", time);
			jsonHXDETAIL.put("INDX", INDX);
			jsonHXDETAIL.put("CODE_VALUE", CODE_VALUE);
			jsonHXDETAIL.put("CODE_DISPLAY_VALUE_EN", CODE_DISPLAY_VALUE_EN);
			jsonHXDETAIL.put("CODE_DISPLAY_VALUE_CN", CODE_DISPLAY_VALUE_CN);
			jsonHXDETAIL.put("CODE_TYPE", CODE_TYPE);
			jsonHXDETAIL.put("CODE_ORDER", CODE_ORDER);
			
			getDataAccess().Update("S_BASE_CODE_TYPE", jsonHXDETAIL);
		}
		
		ReturnMessage(true, "保存成功!");
		}

}
