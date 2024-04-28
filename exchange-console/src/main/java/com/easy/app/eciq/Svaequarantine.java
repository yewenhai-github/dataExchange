package com.easy.app.eciq;




import javax.servlet.annotation.WebServlet;



import org.json.JSONArray;

import com.easy.app.utility.Utility;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/Svaequarantine")
public class Svaequarantine extends MainServlet{
	private static final long serialVersionUID = 1L;

	public Svaequarantine(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String CONT_ID=GetDataValue("TineData", "CONT_ID");
		String[] strArray=CONT_ID.split(",");
		String INDX=GetDataValue("TineData", "INDX");
		
		String[] INDXJSON=INDX.trim().split(",");
		String TINE_NO=GetDataValue("TineData", "TINE_NO");
		String[] TINE_NOs=TINE_NO.split(",");
		String DECL_NOJson=GetDataValue("TineData", "DECL_NO");
		String[] DECL_NO=DECL_NOJson.split(",");
		String MODE_NAME=GetDataValue("TineData", "CNTNR_MODE_NAME");
		String[] CNTNR_MODE_NAME=MODE_NAME.split(",");
		String MODE_CODE=GetDataValue("TineData", "CNTNR_MODE_CODE");
		String[] CNTNR_MODE_CODE=MODE_CODE.split(",");
		String aa="";
		int indxC=0;
		/*String DECL_NO=GetDataValue("TineData", "DECL_NO");*/

		String Y=getRequest().getParameter("Y");
		String SQL = null;
		boolean bool=true;
		
		String orgname=SysUtility.getCurrentUserName();
		if("save".equals(Y)){
			for(int i=0;i<strArray.length;i++){
				SaveToTable("TineData", "CREATE_USER", orgname);
				
				SaveToTable("TineData","CONT_ID",strArray[i]);
				SaveToTable("TineData","CNTNR_MODE_NAME",CNTNR_MODE_NAME[i]);
				SaveToTable("TineData","DECL_NO",DECL_NO[i]);
				SaveToTable("TineData","CNTNR_MODE_CODE",CNTNR_MODE_CODE[i]);
				SaveToTable("TineData","INDX","");
				if(!SysUtility.isEmpty(INDX)){
					SaveToTable("TineData","INDX",INDXJSON[i]);
					SaveToTable("TineData","TINE_NO",TINE_NOs[i]);
				}
				
				if(SysUtility.isEmpty(INDX)){
					String Quarantine=SequenceFactory.getSequence("Quarantine",Utility.DefaultOrgId);
					SaveToTable("TineData", "TINE_NO", Quarantine);
					if(strArray.length-1==i){
						TINE_NO+=Quarantine;
					}else{
						TINE_NO+=Quarantine+",";
					}
				}
			    String	INDXS  = String.valueOf(SaveToDB("TineData", "T_quarantine", "INDX"));
			    getDataAccess().ComitTrans();
				aa+=INDXS+",";
			}
			if (!SysUtility.isEmpty(aa)) {
				ReturnMessage(true, "保存成功！", "", "{\"MODE_NAME\":\"" + MODE_NAME + "\",\"TINE_NO\":\"" + TINE_NO + "\",\"MODE_CODE\":\"" + MODE_CODE + "\",\"CONT_ID\":\"" + CONT_ID + "\",\"CREATE_USER\":\"" + orgname + "\",\"INDXS\":\"" + aa + "\"}");
				
			}else{
				ReturnMessage(false, "保存失败！");	
			}

		}
		if(SysUtility.isEmpty(Y)||"qualified".equals(Y)){
			for(int i=0;i<strArray.length;i++){
				if("qualified".equals(Y)){
					SQL="insert into T_CONTCARSTATE(INDX,CONT_ID,DECL_NO,STATE_CODE,STATE_NAME,CREATE_USER)VALUES (SEQ_T_CONTCARSTATE.NEXTVAL,'"+strArray[i]+"','"+DECL_NO[i]+"','08','检疫合格','"+orgname+"')";
				}else if(SysUtility.isEmpty(Y)){
					SQL="insert into T_CONTCARSTATE(INDX,CONT_ID,DECL_NO,STATE_CODE,STATE_NAME,CREATE_USER)VALUES (SEQ_T_CONTCARSTATE.NEXTVAL,'"+strArray[i]+"','"+DECL_NO[i]+"','09','检疫不合格','"+orgname+"')";
				}
				bool =getDataAccess().ExecSQL(SQL);

			}
			if (bool) {
				ReturnMessage(true, "审核成功！");
			}else{
				ReturnMessage(false, "审核失败！");	
			}  
		}
	}
}
