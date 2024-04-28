package com.easy.web;

import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.session.Operator;
import com.easy.session.UserRight;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class GetUserRights extends MainServlet{
	private static final long serialVersionUID = 1L;

	public GetUserRights(){
        SetCheckLogin(false);
    }
	 
	protected void DoCommand() throws Exception{
		Hashtable<String, Object> EnvDatas = getEnvDatas();
		
		Operator operator = SysUtility.getCurrentUser();
		if(SysUtility.isNotEmpty(operator)){
			String command = EnvDatas.get("command").toString();
			JSONArray rtRows = new JSONArray();
			
			List<UserRight> UserRights = operator.getUserRights();
			if(SysUtility.isNotEmpty(UserRights)){
				ReturnMessage(false, "权限不足！", LoginPage, "", "","","");
			}
			for (int i = 0; i < UserRights.size(); i++) {
				UserRight userRight = UserRights.get(i);
				String CommandName = userRight.getCommandName();
				String IsEnabled = userRight.getIsEnabled();
				if(CommandName.indexOf(command) >= 0 && IsEnabled.equals("0")){
					String[] cmds = CommandName.split(".");
					JSONObject temp = new JSONObject();
					temp.put("Disabled", cmds[1]);
					rtRows.put(temp);
				}
			}
			JSONObject rt = new JSONObject();
			rt.put("Rights", rtRows);
			
		    ReturnMessage(true, "", "",TableToJSON("Rights",rt));
		}else{
			ReturnMessage(false, "请您重新登录！", LoginPage, "", "","","");
		}
	}
}
