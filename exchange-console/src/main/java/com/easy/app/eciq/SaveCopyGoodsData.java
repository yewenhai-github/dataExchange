package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveCopyGoodsData")
public class SaveCopyGoodsData extends MainServlet {
	private static final long serialVersionUID = 4261239235630641154L;

	public SaveCopyGoodsData() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		 String GOODS_ID = getRequest().getParameter("GOODS_ID");
	 	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		 boolean saveFlag=false;
		 JSONObject tempDeclHead = new JSONObject();
		 JSONObject tempDeclHead1 = new JSONObject();
		 JSONArray  tempHeadArrs = new JSONArray(); 
		 //0.复制货物表头
		 tempDeclHead=getDataAccess().GetTableJSON("dataGoods", "SELECT * FROM ITF_DCL_IO_DECL_GOODS WHERE GOODS_ID='"+GOODS_ID+"'");
		 if(SysUtility.isNotEmpty(tempDeclHead.getJSONArray("dataGoods")) && tempDeclHead.getJSONArray("dataGoods").length()>0){
			 String newGOODS_ID=SysUtility.GetUUID();
			 tempDeclHead1= tempDeclHead.getJSONArray("dataGoods").getJSONObject(0);
			 String newGOODS_NO=getGoodsNo((String)tempDeclHead1.getString("DECL_NO"));
			 tempDeclHead1.put("GOODS_NO", newGOODS_NO);
			 tempDeclHead1.put("GOODS_ID", newGOODS_ID);
			 tempDeclHead1.put("PART_ID",SysUtility.getCurrentPartId());
			 tempDeclHead1.put("CREATOR",SysUtility.getCurrentUserIndx());
			 tempDeclHead1.put("CREATE_TIME",df.format(new Date()));
			 tempDeclHead1.put("OPER_TIME",df.format(new Date()));

			 String PROD_VALID_DT=(String)tempDeclHead.getJSONArray("dataGoods").getJSONObject(0).get("PROD_VALID_DT");
			 if(SysUtility.isNotEmpty(PROD_VALID_DT)){
				 tempDeclHead1.put("PROD_VALID_DT", df.format(df.parse(PROD_VALID_DT))); //到货时间
			 }else{
				 tempDeclHead1.put("PROD_VALID_DT","");
			 }
			 tempHeadArrs.put(tempDeclHead1);
			 saveFlag= getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", tempHeadArrs,"GOODS_ID");
			 if(saveFlag){
				 //1.复制货物包装
				 tempDeclHead=new JSONObject();
				 tempDeclHead=getDataAccess().GetTableJSON("dataGoodsPack", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_PACK WHERE GOODS_ID='"+GOODS_ID+"'");
				 if(SysUtility.isNotEmpty(tempDeclHead.getJSONArray("dataGoodsPack")) && tempDeclHead.getJSONArray("dataGoodsPack").length()>0){
					 for (int i = 0; i < tempDeclHead.getJSONArray("dataGoodsPack").length(); i++) {
							tempDeclHead1=new JSONObject();
							tempHeadArrs=new JSONArray();
							tempDeclHead1= tempDeclHead.getJSONArray("dataGoodsPack").getJSONObject(i);
							tempDeclHead1.put("GOODS_ID",newGOODS_ID);
							tempDeclHead1.put("PACK_ID",SysUtility.GetUUID());
							tempDeclHead1.put("GOODS_NO",newGOODS_NO);
							tempDeclHead1.put("CREATE_TIME",df.format(new Date()));
							tempDeclHead1.put("OPER_TIME",df.format(new Date()));
							tempDeclHead1.put("PART_ID",SysUtility.getCurrentPartId());
							tempDeclHead1.put("CREATOR",SysUtility.getCurrentUserIndx());
							tempHeadArrs.put(tempDeclHead1);
							saveFlag= getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_PACK", tempHeadArrs,"PACK_ID");
							if (!saveFlag)
					        {
								ReturnMessage(false, "复制失败!");
								return;
					        }
					 }
				}
				//2.复制箱货对应
				tempDeclHead=new JSONObject();
				tempDeclHead=getDataAccess().GetTableJSON("dataGoodsCont", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_CONT WHERE GOODS_ID='"+GOODS_ID+"'");
				if(SysUtility.isNotEmpty(tempDeclHead.getJSONArray("dataGoodsCont")) && tempDeclHead.getJSONArray("dataGoodsCont").length()>0){
					 for (int i = 0; i < tempDeclHead.getJSONArray("dataGoodsCont").length(); i++) {
						 	tempDeclHead1=new JSONObject();
							tempHeadArrs=new JSONArray();
							tempDeclHead1= tempDeclHead.getJSONArray("dataGoodsCont").getJSONObject(i);
							tempDeclHead1.put("GOODS_ID",newGOODS_ID);
							tempDeclHead1.put("GOODS_CONT_ID",SysUtility.GetUUID());
							tempDeclHead1.put("GOODS_NO",newGOODS_NO);
							tempDeclHead1.put("CREATE_TIME",df.format(new Date()));
							tempDeclHead1.put("OPER_TIME",df.format(new Date()));
							tempDeclHead1.put("PART_ID",SysUtility.getCurrentPartId());
							tempDeclHead1.put("CREATOR",SysUtility.getCurrentUserIndx());
							tempHeadArrs.put(tempDeclHead1);
							saveFlag= getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_CONT", tempHeadArrs,"GOODS_CONT_ID");
							if (!saveFlag)
					        {
								ReturnMessage(false, "复制失败!");
								return;
					        }
					 }
			    }
				//3.复制产品资质
				tempDeclHead=new JSONObject();
				tempDeclHead=getDataAccess().GetTableJSON("dataGoodsLimit", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_LIMIT WHERE GOODS_ID='"+GOODS_ID+"'");
				if(SysUtility.isNotEmpty(tempDeclHead.getJSONArray("dataGoodsLimit")) && tempDeclHead.getJSONArray("dataGoodsLimit").length()>0){
					 for (int i = 0; i < tempDeclHead.getJSONArray("dataGoodsLimit").length(); i++) {
						 	tempDeclHead1=new JSONObject();
							tempHeadArrs=new JSONArray();
							tempDeclHead1= tempDeclHead.getJSONArray("dataGoodsLimit").getJSONObject(i);
							tempDeclHead1.put("GOODS_ID",newGOODS_ID);
							tempDeclHead1.put("LIMIT_ID",SysUtility.GetUUID());
							tempDeclHead1.put("GOODS_NO",newGOODS_NO);
							tempDeclHead1.put("CREATE_TIME",df.format(new Date()));
							tempDeclHead1.put("OPER_TIME",df.format(new Date()));
							tempDeclHead1.put("PART_ID",SysUtility.getCurrentPartId());
							tempDeclHead1.put("CREATOR",SysUtility.getCurrentUserIndx());
							tempHeadArrs.put(tempDeclHead1);
							saveFlag= getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_LIMIT", tempHeadArrs,"LIMIT_ID");
							if (!saveFlag)
					        {
								ReturnMessage(false, "复制失败!");
								return;
					        }
					 }
			    }
				 
			 }
		 }
		 if(saveFlag){
			 ReturnMessage(true, "复制成功!","");
		 }else{
			 ReturnMessage(false, "复制失败!","");
		 }
		
	}
	public String getGoodsNo(String declNo) throws Exception{
		Datas dt = getDataAccess().GetTableDatas("GoodsList", "SELECT max(GOODS_NO) GOODS_NO FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO = ?", declNo);
	    if (dt.GetTableRows("GoodsList") > 0)
	    {
	        if (dt.GetTableValue("GoodsList", "GOODS_NO").length()>0)
	        {
	            return String.valueOf(Integer.parseInt(dt.GetTableValue("GoodsList", "GOODS_NO")) + 1);
	        }
	        else
	        {
	            return "1";
	        }
	    }
	    else
	    {
	        return "1";
	    }
	}
}