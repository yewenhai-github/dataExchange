package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.easy.app.utility.Utility;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SvaeDeclCheck")
public class SvaeDeclCheck extends MainServlet{
	private static final long serialVersionUID = 1L;

	public SvaeDeclCheck(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String Y=getRequest().getParameter("Y");
		String CONT_ID=GetDataValue("CheckDATA", "CONT_ID");
		String[] strArray=CONT_ID.split(",");
		String DECL_NOJson=GetDataValue("CheckDATA", "DECL_NO");
		String[] DECL_NO=DECL_NOJson.split(",");
		String MODE_NAME=GetDataValue("CheckDATA", "CNTNR_MODE_NAME");
		String[] CNTNR_MODE_NAME=MODE_NAME.split(",");
		String MODE_CODE=GetDataValue("CheckDATA", "CNTNR_MODE_CODE");
		String[] CNTNR_MODE_CODE=MODE_CODE.split(",");
		
		int indxC=0;
		
		String SQL;
		String orgname=SysUtility.getCurrentUserName();
		
			for(int i=0;i<strArray.length;i++){
				SaveToTable("CheckDATA","CONT_ID",strArray[i]);
				SaveToTable("CheckDATA","CNTNR_MODE_NAME",CNTNR_MODE_NAME[i]);
				SaveToTable("CheckDATA","DECL_NO",DECL_NO[i]);
				SaveToTable("CheckDATA","CNTNR_MODE_CODE",CNTNR_MODE_CODE[i]);
				SaveToTable("CheckDATA","INDX","");
				SaveToTable("CheckDATA", "create_user", orgname);
				String indx = String.valueOf(SaveToDB("CheckDATA", "T_DECLCHECK", "INDX"));
				if(!SysUtility.isEmpty(Y)){
					 SQL="insert into T_CONTCARSTATE(INDX,CONT_ID,DECL_NO,STATE_CODE,STATE_NAME,CREATE_USER)VALUES (SEQ_T_CONTCARSTATE.NEXTVAL,'"+strArray[i]+"','"+DECL_NO[i]+"','10','查验合格','"+orgname+"')";
				}else{
					 SQL="insert into T_CONTCARSTATE(INDX,CONT_ID,DECL_NO,STATE_CODE,STATE_NAME,CREATE_USER)VALUES (SEQ_T_CONTCARSTATE.NEXTVAL,'"+strArray[i]+"','"+DECL_NO[i]+"','11','查验不合格','"+orgname+"')";
				}
				boolean bool =getDataAccess().ExecSQL(SQL);
				indxC+=Integer.valueOf(indx);
			
			}
			if (!SysUtility.isEmpty(indxC)) {
				ReturnMessage(true, "审核成功！", "", "{\"INDX\":\"" + indxC + "\"}");
			}else{
				ReturnMessage(true, "审核失败！", "", "{\"INDX\":\"" + indxC + "\"}");
		}
		
	}
}
