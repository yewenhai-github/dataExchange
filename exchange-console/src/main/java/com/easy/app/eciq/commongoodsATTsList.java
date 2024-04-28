package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/commongoodsATTsList")
public class commongoodsATTsList  extends MainServlet{  
	
	private static final long serialVersionUID = 8524100130045022405L;
	
	public commongoodsATTsList()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		AddToSearchTable("DECL_NO", getRequest().getParameter("DECL_NO"));
		ReturnWriter(GetReturnDatas("commongoodsATTsList").toString());	
	}
}