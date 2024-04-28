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


@WebServlet("/base/SaveFormarkList")

public class SaveFormarkList extends MainServlet{
	
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = getRequest().getParameter("INDX");
		String REMARK=getRequest().getParameter("REMARK");
		String NAME=getRequest().getParameter("NAME");
		JSONObject jsonHXDETAIL = new JSONObject();
		
		
		if(INDX == null ||"".equals(INDX.trim()))
		{
			String SQL = "select * from S_FORMREMARK where IS_ENABLED = 1 AND REMARK='"+REMARK+"'";
		List lst = SQLExecUtils.query4List(SQL);
		if(lst.size()>0)
		{
			ReturnMessage(false, "存在同名模块，请确认！");
		    return;
		}
		
		else{
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("NAME", NAME);
			jsonHXDETAIL.put("CREATOR", SysUtility.getCurrentUserIndx());
			getDataAccess().Insert("S_FORMREMARK", jsonHXDETAIL);
		}
		}
		else{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=format.format(date);
			jsonHXDETAIL.put("MODIFYOR", SysUtility.getCurrentUserIndx());
			jsonHXDETAIL.put("MODIFY_DATE", time);
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("NAME", NAME);//CREATE_TIME
			jsonHXDETAIL.put("INDX", INDX);
			getDataAccess().Update("S_FORMREMARK", jsonHXDETAIL);
			
		}
		ReturnMessage(true, "保存成功!");
		}
	}
