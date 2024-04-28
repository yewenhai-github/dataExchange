package com.easy.app.eciq;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.MySysUtility;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetHsCodeName")
public class GetHsCodeName   extends MainServlet{  
	 
	private static final long serialVersionUID = -7722225571192321843L;
	public GetHsCodeName()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		InitFormData("HSDATA", SQLMap.getSelect("GetHsCodeName"));
		
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
