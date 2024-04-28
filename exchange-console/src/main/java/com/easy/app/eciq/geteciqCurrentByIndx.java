package com.easy.app.eciq;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;
@WebServlet("/forms/geteciqCurrentByIndx")
public class geteciqCurrentByIndx extends MainServlet {

	private static final long serialVersionUID = -7722225571192321843L;
	public geteciqCurrentByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmmsssssss");
		String Json="{\"DeclData\":[{\"CONTACTPERSON\":\""+getSession().getAttribute("LINKMAN")+"\",\"CONT_TEL\":\""+getSession().getAttribute("LINKMANTEL")+"\",\"DECL_REG_NO\":\""+getSession().getAttribute("INSPREGNUMBER")+"\",\"DECL_REG_NAME\":\""+getSession().getAttribute("ENT_NAME")+"\",\"INSP_ORG_NAME\":\""+getSession().getAttribute("CHECKORGNAME")+"\",\"INSP_ORG_CODE\":\""+getSession().getAttribute("CHECKORGCODE")+"\",\"ORG_NAME\":\""+getSession().getAttribute("CHECKORGNAME")+"\",\"ORG_CODE\":\""+getSession().getAttribute("CHECKORGCODE")+"\",\"VSA_ORG_NAME\":\""+getSession().getAttribute("CHECKORGNAME")+"\",\"VSA_ORG_CODE\":\""+getSession().getAttribute("CHECKORGCODE")+"\",\"PURP_ORG_NAME\":\""+getSession().getAttribute("CHECKORGNAME")+"\",\"PURP_ORG_CODE\":\""+getSession().getAttribute("CHECKORGCODE")+"\",\"DECL_DATE\":\""+df.format(new Date())+"\",\"CON_INDX\":\""+dfdate.format(new Date())+"\"}]}";
		ReturnMessage(true, "", "", Json.toString());
	}
}
