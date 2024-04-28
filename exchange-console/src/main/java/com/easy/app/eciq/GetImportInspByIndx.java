package com.easy.app.eciq;
 

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.MySysUtility;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetImportInspByIndx")
public class GetImportInspByIndx  extends MainServlet{  
	 
	private static final long serialVersionUID = -7722225571192321843L;
	public GetImportInspByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String action = getRequest().getParameter("action");
		String Pindx = getRequest().getParameter("DECL_NO");
		String GoodsId=getRequest().getParameter("GoodsId");
		String CONT_ID=getRequest().getParameter("CONT_ID");
		if("getjson".equals(action)){ 
			InitFormData("DeclData", SQLMap.getSelect("GetEciqImportInspById"));
			SaveToTable("DeclData", "CFDISDO",(("0".equals(GetDataValue("DeclData", "CF_TYPE_CODE"))) && ("0".equals(GetDataValue("DeclData", "DECL_STATUS_CODE")) || "1".equals(GetDataValue("DeclData", "DECL_STATUS_CODE"))) ? "1" : "0"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		else if("getdefault".equals(action)){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			InitFormData("DeclData","SELECT TO_CHAR(Now(),'yyyyMMddhh24miss')|| SUBSTR(SEQ_COMMON.NEXTVAL,-4,4) AS CON_INDX FROM DUAL" );
			SaveToTable("DeclData", "DECL_DATE",df.format(new Date()));
			SaveToTable("DeclData", "maker_z", MySysUtility.userName);
			SaveToTable("DeclData", "makedate_z", df.format(new Date())); 
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		else if("list".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetImportInspGoodsByPindx").toString());
		}else if("getgoodjson".equals(action)){ 
            InitFormData("GoodsData", SQLMap.getSelect("GetInspGoodsById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		else if("getContainer".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetImportInspContainerByPIndx").toString());
		}else if("getContainerInfo".equals(action)){
			getEnvDatas().put("CONT_ID", CONT_ID);
			InitFormData("ContainerData", SQLMap.getSelect("GetImportInspContainerInfoById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		//企业资质
		else if("getDeclLimitList".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetcommonEntLimitListByPIndx").toString());
		}
		else if("getDeclLimitJson".equals(action)){
			InitFormData("DeclLimitData", SQLMap.getSelect("GetcommonEntLimitInfoByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		}
		//使用人
		else if("getcommonDeclUserList".equals(action)){
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetcommonDeclUserListByPIndx").toString());
		}
		else if("getCommonDeclUserJson".equals(action)){
			InitFormData("DeclUserData", SQLMap.getSelect("GetcommonDeclUserInfoByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		}
		//产品资质
		else if("getGoodsLimitJson".equals(action)){
			InitFormData("LimitData", SQLMap.getSelect("GetGoodsLimitJsonByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		}else if("getContainerList".equals(action)){
			
			ReturnWriter(GetReturnDatas("GetImportInspContainerInfo").toString());
		}
		
		//包装数量
		if("getPackTypeList".equals(action)){
			if(GoodsId.isEmpty())
			   AddToSearchTable("GOODS_ID", "0");
			else
				AddToSearchTable("GOODS_ID", GoodsId);
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetcommonPackTypeListByPIndx").toString());
		}
		if("getPackTypeJson".equals(action)){
			InitFormData("PackType", SQLMap.getSelect("GetcommonPackTypeInfoByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		} 
		//货物集装箱关联信息
		if("getgoodsContList".equals(action)){
			if(GoodsId.isEmpty())
				   AddToSearchTable("GOODS_ID", "0");
				else
					AddToSearchTable("GOODS_ID", GoodsId);
			AddToSearchTable("DECL_NO", Pindx);
			ReturnWriter(GetReturnDatas("GetcommonGoodsContListByPIndx").toString());
		}	
		if("getgoodsContJson".equals(action)){
			InitFormData("goodsContData", SQLMap.getSelect("GetcommonGoodsContInfoByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		}
		//许可证
		if("getGoodsLimitList".equals(action)){
			AddToSearchTable("GOODS_ID", GoodsId);
			ReturnWriter(GetReturnDatas("GetcommonGoodsLimitListByPIndx").toString());
		}	
		if("getGoodsLimitJson".equals(action)){
			InitFormData("LimitData", SQLMap.getSelect("GetcommonGoodsLimitInfoByIndx"));
			ReturnMessage(true, "", "", getFormDatas().toString()); 
		}
	}
}
