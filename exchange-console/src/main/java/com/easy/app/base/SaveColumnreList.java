package com.easy.app.base;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/base/SaveColumnreList")

public class SaveColumnreList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = getRequest().getParameter("INDX");
		String CONTRALNAME=getRequest().getParameter("CONTRALNAME");
		String REMARK=getRequest().getParameter("REMARK");
		String FORMID=getRequest().getParameter("FORMID");
		String LABLEREMARK=getRequest().getParameter("LABLEREMARK");
		JSONObject jsonHXDETAIL = new JSONObject();
		if(INDX == null||"".equals(INDX.trim()))
		{
			String SQL = "select * from S_COLUMNREMARK where is_enabled=1 and FORMID='"+FORMID+"' and CONTRALNAME='"+CONTRALNAME+"'";
			List lst = SQLExecUtils.query4List(SQL);
			if(lst.size()>0)
			{
				ReturnMessage(false, "存在同ID控件，请确认！");
			    return;
			}
		
		else{
			jsonHXDETAIL.put("CONTRALNAME", CONTRALNAME);
			jsonHXDETAIL.put("LABLEREMARK", LABLEREMARK);
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("FORMID", FORMID);
			jsonHXDETAIL.put("CREATED_BY", SysUtility.getCurrentUserIndx());
			jsonHXDETAIL.put("CREATED_BY_NAME", SysUtility.getCurrentName());
//			jsonHXDETAIL.put("DEPT_ID", SysUtility.getCurrentDeptId());
//			jsonHXDETAIL.put("ORG_ID", SysUtility.getCurrentOrgId());
			getDataAccess().Insert("S_COLUMNREMARK", jsonHXDETAIL);
			
		}
		}
		else{
			//Date date=new Date();
			//DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//String time=format.format(date);
			//jsonHXDETAIL.put("MODIFYOR", SysUtility.getCurrentUserIndx());
			//jsonHXDETAIL.put("MODIFY_DATE", time);
			jsonHXDETAIL.put("INDX", INDX);
			jsonHXDETAIL.put("CONTRALNAME", CONTRALNAME);
			jsonHXDETAIL.put("LABLEREMARK", LABLEREMARK);
			jsonHXDETAIL.put("REMARK", REMARK);
			jsonHXDETAIL.put("FORMID", FORMID);
			getDataAccess().Update("S_COLUMNREMARK", jsonHXDETAIL);
		}
		
		ReturnMessage(true, "保存成功!");
		}
	}

