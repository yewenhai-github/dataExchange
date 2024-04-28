package com.easy.app.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/base/SaveAnnouncement")

public class SaveAnnouncement extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		
		String INDX = getRequest().getParameter("INDX");
		String TITLE=getRequest().getParameter("TITLE");
		String CONTENTES=getRequest().getParameter("CONTENTES");
		String ACM_TYPE=getRequest().getParameter("ACM_TYPE");
		
		JSONObject jsonHXDETAIL = new JSONObject();
		if(INDX == null||"".equals(INDX.trim()))
		{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonHXDETAIL.put("CREATED_TIME", time);
			jsonHXDETAIL.put("CREATED_BY", SysUtility.getCurrentUserIndx());
			
			jsonHXDETAIL.put("TITLE", TITLE);
			jsonHXDETAIL.put("CONTENTES", CONTENTES);
			jsonHXDETAIL.put("ACM_TYPE", ACM_TYPE);
			getDataAccess().Insert("T_ANNOUNCEMENT", jsonHXDETAIL);
		}else{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonHXDETAIL.put("MODIFIED_BY", SysUtility.getCurrentUserIndx());
			jsonHXDETAIL.put("MODIFIED_TIME", time);
			
			jsonHXDETAIL.put("INDX", INDX);
			jsonHXDETAIL.put("TITLE", TITLE);
			jsonHXDETAIL.put("CONTENTES", CONTENTES);
			jsonHXDETAIL.put("ACM_TYPE", ACM_TYPE);
			
			getDataAccess().Update("T_ANNOUNCEMENT", jsonHXDETAIL);
		}
		
		ReturnMessage(true, "保存成功!");
		}

}
