package com.easy.app.base;
import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.web.MainServlet;
import com.easy.query.SQLMap;
import com.easy.sequence.SequenceFactory;

@WebServlet("/base/GetFRTDEF")
public class GetFRTDEF extends MainServlet{
	public void DoCommand() throws Exception{
		ReturnWriter(GetComboboxDatas("GetFRTDEF"));
	}
}
