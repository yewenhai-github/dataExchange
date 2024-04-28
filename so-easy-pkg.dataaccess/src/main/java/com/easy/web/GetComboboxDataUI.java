package com.easy.web;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;

import com.easy.query.SQLMap;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

@WebServlet("/GetComboboxDataUI")
public class GetComboboxDataUI extends MainServlet {
	private static final long serialVersionUID = 1L;

	protected void DoCommand() throws Exception{
		String ID = (String)getEnvDatas().get("SelectID");
		if(SysUtility.isNotEmpty(ID) && SysUtility.isNotEmpty(SQLMap.getSelect(ID))){
			ReturnWriter(GetComboboxDatas(ID));
		}else{
			LogUtil.printLog("无法发现ComboboxData数据源，SelectID="+ID, Level.ERROR);
			ReturnWriter("");
		}
	}
}
