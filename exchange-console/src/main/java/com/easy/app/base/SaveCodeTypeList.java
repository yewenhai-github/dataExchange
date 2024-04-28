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


@WebServlet("/base/SaveCodeTypeList")

public class SaveCodeTypeList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = getRequest().getParameter("INDX");
		String CODE_TYPE=getRequest().getParameter("CODE_TYPE");
		String CODE_TYPENAME=getRequest().getParameter("CODE_TYPENAME");
		String CODE_REMARK=getRequest().getParameter("CODE_REMARK");
		String CODE_GRADE=getRequest().getParameter("CODE_GRADE");
		String CODE_TYPE_OLD=getRequest().getParameter("CODE_TYPE_OLD");
		JSONObject jsonHXDETAIL = new JSONObject();
		
		if(INDX == null ||"".equals(INDX.trim()))
		{
			String SQL = "select * from S_BASE_CODE_TYPE where is_enabled=1 and CODE_TYPE='"+CODE_TYPE+"' and CODE_TYPENAME='"+CODE_TYPENAME+"'";
			List lst = SQLExecUtils.query4List(SQL);
			if(lst.size()>0)
			{
				ReturnMessage(false, "存在同名类型，请确认！");
			    return;
			}
			else{
				jsonHXDETAIL.put("CODE_TYPE", CODE_TYPE);
				jsonHXDETAIL.put("CODE_TYPENAME", CODE_TYPENAME);
				jsonHXDETAIL.put("CODE_GRADE", CODE_GRADE);
				jsonHXDETAIL.put("CODE_REMARK", CODE_REMARK);
				jsonHXDETAIL.put("CREATOR", SysUtility.getCurrentUserIndx());
				getDataAccess().Insert("S_BASE_CODE_TYPE", jsonHXDETAIL);
			}
		}
		else
		{
			String SQL = "select * from S_BASE_CODE_TYPE where CODE_TYPE='"+CODE_TYPE+"' and CODE_TYPENAME='"+CODE_TYPENAME+"' and CODE_GRADE='"+CODE_GRADE+"'";
			List lst = SQLExecUtils.query4List(SQL);
			if(lst.size()>0)
			{
				ReturnMessage(false, "存在同名类型，请确认！");
			    return;
			}
			else{
				Date date=new Date();
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time=format.format(date);
				jsonHXDETAIL.put("MODIFYOR", SysUtility.getCurrentUserIndx());
				jsonHXDETAIL.put("MODIFY_DATE", time);
				jsonHXDETAIL.put("INDX", INDX);
				jsonHXDETAIL.put("CODE_TYPE", CODE_TYPE);
				jsonHXDETAIL.put("CODE_TYPENAME", CODE_TYPENAME);
				jsonHXDETAIL.put("CODE_GRADE", CODE_GRADE);
				jsonHXDETAIL.put("CODE_REMARK", CODE_REMARK);
				getDataAccess().Update("S_BASE_CODE_TYPE", jsonHXDETAIL);
				
				String sql_update="update S_BASE_CODE_TYPE set CODE_TYPE='"+CODE_TYPE+"' where IS_ENABLED=1 AND CODE_TYPE='"+CODE_TYPE_OLD+"'";
				getDataAccess().ExecSQL(sql_update);
				
			}
			
		}
		
		ReturnMessage(true, "保存成功!");
		}

}
