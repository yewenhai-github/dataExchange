package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/GetCCSaveConts")
public class GetCCSaveConts extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetCCSaveConts()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		 String inspRadio=GetDataValue("ContsData", "insp");
		 String custRadio=GetDataValue("ContsData", "cust");
		 /*String allRadio=GetDataValue("ContsData", "all");*/
		 String allRadio="Y";
		 
		 //获取表头主键
		 String custPCon_indx= GetDataValue("custData", "CON_INDX");
		 String inspPindx= GetDataValue("inspData", "DECL_NO");
		 String custPindx=GetDataValue("custData", "INDX");
		 
		 //公共信息
		 String CONTAINNO= GetDataValue("complexContsData", "CONTAINNO");
		  
		//如果是保存报检insp
		 if("Y".equals(inspRadio)){  
			 if(SysUtility.isEmpty(inspPindx)){
				 ReturnMessage(true, "请先保存报检表头信息");
				 return;
			 }	 
			 SaveToTable("inspContsData", "CONT_NO", CONTAINNO);
			 SaveToTable("inspContsData", "DECL_NO", inspPindx); 
			 String Indx = String.valueOf(SaveToDB("inspContsData", "ITF_DCL_IO_DECL_CONT", "CONT_ID"));
			   if (!SysUtility.isEmpty(Indx))
			    {
			       ReturnMessage(true, "保存成功");
			    }
			    else
			    {
			       ReturnMessage(false, "保存失败");	
			    }
		 }
		//如果保存报关cust
		 if("Y".equals(custRadio)){ 
			 if(SysUtility.isEmpty(custPCon_indx)){
				 ReturnMessage(true, "请先保存报关表头信息");
				 return;
			 }
			 SaveToTable("custContsData", "CONTAINNO", CONTAINNO);
			 SaveToTable("custContsData", "CON_INDX", custPCon_indx); 
			 SaveToTable("custContsData", "ENY_INDX", custPindx); 
			 String Indx = String.valueOf(SaveToDB("custContsData", "T_CONTAINERS", "INDX"));
			   if (!SysUtility.isEmpty(Indx))
			    {
			       ReturnMessage(true, "保存成功");
			    }
			    else
			    {
			       ReturnMessage(false, "保存失败");	
			    }
		 }
		 //如果保存关检all
		 if("Y".equals(allRadio)){
			 if(SysUtility.isEmpty(inspPindx)||SysUtility.isEmpty(custPCon_indx)){
				 ReturnMessage(false, "请先保存表头信息");	
				 return;
			 }
			 SaveToTable("inspContsData", "CONT_NO", CONTAINNO);
			 SaveToTable("inspContsData", "DECL_NO", inspPindx); 
			  
			 
			 SaveToTable("custContsData", "CONTAINNO", CONTAINNO);
			 SaveToTable("custContsData", "CON_INDX", custPCon_indx); 
			 
			 SaveToTable("custContsData", "ENY_INDX", custPindx); 
			   
			 
			 String Indx1="";
			 String Indx2="";
			 if(SysUtility.isEmpty(GetDataValue("inspContsData", "CONT_ID"))&&SysUtility.isEmpty(GetDataValue("custContsData", "INDX")) ){
				   Indx1 = String.valueOf(SaveToDB("inspContsData", "ITF_DCL_IO_DECL_CONT", "CONT_ID"));
				   Indx2 = String.valueOf(SaveToDB("custContsData", "T_CONTAINERS", "INDX"));
			 }
			 if(SysUtility.isNotEmpty(GetDataValue("inspContsData", "CONT_ID"))&&SysUtility.isEmpty(GetDataValue("custContsData", "INDX"))){
				 Indx1 = String.valueOf(SaveToDB("inspContsData", "ITF_DCL_IO_DECL_CONT", "CONT_ID"));
			 }
			 if(SysUtility.isNotEmpty(GetDataValue("custContsData", "INDX"))&&SysUtility.isEmpty(GetDataValue("inspContsData", "CONT_ID"))){
				 	Indx2 = String.valueOf(SaveToDB("custContsData", "T_CONTAINERS", "INDX"));
			 }
			 
			 
			 
			   if (!SysUtility.isEmpty(Indx1)||!SysUtility.isEmpty(Indx2))
			    {
			       ReturnMessage(true, "保存成功");
			    }
			    else
			    {
			       ReturnMessage(false, "保存失败");	
			    }
		 }
		 
		
	}
			
}