package com.easy.app.eciq;



import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SvaeMeasurement")
public class SvaeMeasurement extends MainServlet{
	private static final long serialVersionUID = 1L;

	public SvaeMeasurement(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String TINE_NO=GetDataValue("measData", "TINE_NO");
		 String INDX=GetDataValue("measData", "INDX");
		 String[] TINE_NOS=TINE_NO.split(",");
		 String indx="";
		 if(SysUtility.isEmpty(INDX)&&TINE_NOS.length>=2){
			 for(int i=0;i<TINE_NOS.length;i++){
				 SaveToTable("measData", "TINE_NO", TINE_NOS[i]);
				 SaveToTable("measData","INDX","");
					/*String del="delete from T_measurement where TINE_NO='"+TINE_NOS[i]+"'";
					getDataAccess().ExecSQL(del);*/
				 indx  = String.valueOf(SaveToDB("measData", "T_measurement", "INDX"));
			 }
		 }else{
			 indx=String.valueOf(SaveToDB("measData", "T_measurement", "INDX"));
		 }
		 
		
		
		 if (!SysUtility.isEmpty(indx)) {
			 ReturnMessage(true, "保存成功！", "", "{\"INDX\":\"" + indx + "\"}");
			}else{
				ReturnMessage(false, "保存失败！");	
			}
	}
}
