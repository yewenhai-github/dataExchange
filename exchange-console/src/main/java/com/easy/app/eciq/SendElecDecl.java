package com.easy.app.eciq;

import java.util.Properties;
import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SendElecDecl")
public class SendElecDecl extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public SendElecDecl()
	{
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		Properties properties = SysUtility.GetProperties("System.properties");
		String paperless_MsgType=properties.getProperty("paperless_MsgType");
		String paperless_SignData=properties.getProperty("paperless_SignData");   
		String paperless_MessageSource=properties.getProperty("paperless_MessageSource");
		String paperless_MessageDest=properties.getProperty("paperless_MessageDest"); 
		boolean isFlag = true;
		
		String DECL_GET_NO = getRequest().getParameter("DECL_GET_NO");;
		if (SysUtility.isNotEmpty(DECL_GET_NO)) {
    		StringBuffer InsertSQL = new StringBuffer();
			InsertSQL.append("INSERT INTO EXS_HANDLE_SENDER(INDX,MSG_TYPE,MSG_NO,SIGN_DATA,MESSAGE_SOURCE,MESSAGE_DEST,TECH_REG_CODE) VALUES(SEQ_EXS_HANDLE_SENDER.nextval,'"+paperless_MsgType+"', ?,'"+paperless_SignData+"','"+paperless_MessageSource+"','"+paperless_MessageDest+"','"+SysUtility.getCurrentOrgId()+"')");
			isFlag= getDataAccess().ExecSQL(InsertSQL.toString(),DECL_GET_NO);
			if(!isFlag){
				ReturnMessage(false, "上传失败！");
				return;
			}
			ReturnMessage(true, "上传成功", "", TableToJSON("ElecDeclData"));
		}
        else
        {
        	ReturnMessage(false, "传入的数据有错误，无法上传！");
        }
	}

}


