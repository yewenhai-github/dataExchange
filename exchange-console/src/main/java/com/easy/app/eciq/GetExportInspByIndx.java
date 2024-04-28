package com.easy.app.eciq;
 

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.easy.app.utility.MySysUtility;
import com.easy.query.SQLMap;
import com.easy.web.MainServlet; 

@WebServlet("/forms/GetExportInspByIndx")
public class GetExportInspByIndx  extends MainServlet{  
	 
	private static final long serialVersionUID = -7722225571192321843L;
	public GetExportInspByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String action = getRequest().getParameter("action");
		String Pindx = getRequest().getParameter("DECL_NO");
		
		if("getjson".equals(action)){
			InitFormData("DeclData", SQLMap.getSelect("GetExportInspById")); 
			String status = GetDataValue("DeclData", "PROCESS_STATUS"); 
			/*SaveToTable("DeclData", "BTNSEND", ("暂存".equals(status) ? "1" : "0"));
			SaveToTable("DeclData", "CANSAVE", ("已申".equals(status) ? "0" : "1")); */
			ReturnMessage(true, "", "", getFormDatas().toString());
		}else if("getdefault".equals(action)){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			InitFormData("DeclData","SELECT TO_CHAR(Now(),'yyyyMMddhh24miss')|| SUBSTR(SEQ_COMMON.NEXTVAL,-4,4) AS CON_INDX FROM DUAL" );
			SaveToTable("DeclData", "DECL_DATE",df.format(new Date()));
			SaveToTable("DeclData", "maker_z", MySysUtility.userName);
			SaveToTable("DeclData", "makedate_z", df.format(new Date())); 
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		
		else if("list".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetExportInspGoodsByPindx").toString());
		}else if("getgoodjson".equals(action)){ 
            InitFormData("GoodsData", SQLMap.getSelect("GetInspGoodsById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		else if("getContainer".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetExportInspContainerByPIndx").toString());
		}else if("getContainerInfo".equals(action)){
			InitFormData("ContainerData", SQLMap.getSelect("GetExportInspContainerInfoById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
	}
}
