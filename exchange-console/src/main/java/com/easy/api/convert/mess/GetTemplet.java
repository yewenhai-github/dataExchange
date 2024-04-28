package com.easy.api.convert.mess;

import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.web.MainServlet;


@WebServlet("/GetTemplet")
public class GetTemplet extends MainServlet{
	
	public void DoCommand() throws LegendException, Exception {
		ReturnWriter(GetDatas("GetTemplet").toString());	
	}
	
}
