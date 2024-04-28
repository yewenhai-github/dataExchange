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

import com.easy.query.SQLExecUtils;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveCopyInspDecl")
public class SaveCopyInspDecl extends MainServlet {
	private static final long serialVersionUID = 4261239235630641154L;

	public SaveCopyInspDecl() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {

		 boolean InsertRt=false;
		 List lst = SQLExecUtils.query4List("Select TO_CHAR(Now(),'yyyyMMddhh24miss') || SUBSTR(SEQ_COMMON.NEXTVAL,-4,4) as CONINDX from dual");
		 Map map =(Map)lst.get(0);
		 String con_indx=(String) map.get("CONINDX");//获取综合流水号
		 String oldDeclno=getRequest().getParameter("declNo");
		 String APL_KIND=getRequest().getParameter("aplkind");
	 	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		 JSONObject tempDecl = new JSONObject();
		 JSONObject tempDecl1 = new JSONObject();
		 JSONArray tempArrs = new JSONArray(); 
		 
		 JSONObject tempDeclHead = new JSONObject();
		 JSONObject tempDeclHead1 = new JSONObject();
		 JSONArray  tempHeadArrs = new JSONArray(); 
		 //0、复制表头
		 tempDeclHead= getDataAccess().GetTableJSON("baseDeclData", "SELECT * FROM ITF_DCL_IO_DECL WHERE DECL_NO=?",oldDeclno);
		 if(SysUtility.isNotEmpty(tempDeclHead.getJSONArray("baseDeclData")) && tempDeclHead.getJSONArray("baseDeclData").length()>0){
			 for (int k = 0; k < tempDeclHead.getJSONArray("baseDeclData").length(); k++) {
				    if(k>0){
				    	break;
				    }
				 	String code = SysUtility.getCurrentEntRegNo();//DECL_REG_NO
					String seq = SequenceFactory.getSequence("DECL_NO");
					String DECL_NO=code + seq;
					tempDeclHead1=new JSONObject();
				 	tempArrs=new JSONArray();
				 	tempDeclHead1=tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0);//取0条， 防止数据库出现多条相同DECL_NO垃圾数据，会复制重复
				 	tempDeclHead1.put("DECL_ID", SysUtility.GetUUID());
				 	tempDeclHead1.put("CREATE_TIME", df.format(new Date()));
				 	tempDeclHead1.put("DECL_DATE", df.format(new Date()));
				 	tempDeclHead1.put("DECL_STATUS_CODE", "0");
				 	tempDeclHead1.put("DECL_STATUS_NAME", "本地暂存");
				 	tempDeclHead1.put("DATA_SOURCE", "0"); // 数据来源
				 	tempDeclHead1.put("INPUT_SERIAL", "");//清空录入序号
				 	tempDeclHead1.put("DECL_GET_NO", "");//清空报检号
				 	tempDeclHead1.put("PASS_CODE", "");//清空通关单号
				 	tempDeclHead1.put("DECL_NO", DECL_NO); 
					String eciquid = SysUtility.getCurrentUserDataValue("ECIQ_USER_NAME");
					String eciqpwd = SysUtility.getCurrentUserDataValue("ECIQ_PASS_WORD"); 
					tempDeclHead1.put("ECIQ_USER_NAME",eciquid);
					tempDeclHead1.put("ECIQ_PASS_WORD",eciqpwd);
					tempDeclHead1.put("TECH_REG_CODE", SysUtility.getCurrentOrgId());
					tempDeclHead1.put("DECL_REG_CODE", SysUtility.getCurrentEntRegNo());
					//tempDeclHead1.put("DECL_REG_NAME", SysUtility.getCurrentEntName());
					tempDeclHead1.put("APL_KIND", APL_KIND);
					tempDeclHead1.put("CON_INDX", con_indx);
					tempDeclHead1.put("APPROVAL_TIME", ""); //清空审批时间
					
					String GDS_ARVL_DATE=(String)tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0).get("GDS_ARVL_DATE");
					String DESP_DATE=(String)tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0).get("DESP_DATE");
					String CMPL_DSCHRG_DT=(String)tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0).get("CMPL_DSCHRG_DT");
					String COUNTER_CLAIM=(String)tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0).get("COUNTER_CLAIM");
					if(SysUtility.isNotEmpty(GDS_ARVL_DATE)){
						tempDeclHead1.put("GDS_ARVL_DATE", df.format(df.parse(GDS_ARVL_DATE))); //到货时间
					}else{
						tempDeclHead1.put("GDS_ARVL_DATE","");
					}
					if(SysUtility.isNotEmpty(DESP_DATE)){
						tempDeclHead1.put("DESP_DATE", df.format(df.parse(DESP_DATE))); //启运日期
					}else{
						tempDeclHead1.put("DESP_DATE","");
					}
					if(SysUtility.isNotEmpty(CMPL_DSCHRG_DT)){
						tempDeclHead1.put("CMPL_DSCHRG_DT", df.format(df.parse(CMPL_DSCHRG_DT))); //卸毕日期
					}else{
						tempDeclHead1.put("CMPL_DSCHRG_DT","");
					}
					if(SysUtility.isNotEmpty(COUNTER_CLAIM)){
						tempDeclHead1.put("COUNTER_CLAIM", df.format(df.parse(COUNTER_CLAIM))); //索赔截止日期
					}else{
						tempDeclHead1.put("COUNTER_CLAIM","");
					}
					tempDeclHead1.put("OPER_TIME", df.format(new Date())); //操作时间
					tempHeadArrs.put(tempDeclHead1);
					InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL", tempHeadArrs,"DECL_NO");
					if (InsertRt)
			        {
						 //1、复制随附单据
						 tempDecl= getDataAccess().GetTableJSON("baseAtt", "SELECT * FROM ITF_DCL_IO_DECL_ATT WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseAtt")) && tempDecl.getJSONArray("baseAtt").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseAtt").length(); i++) {
								 	tempDecl1=new JSONObject();
								 	tempArrs=new JSONArray();
									tempDecl1=tempDecl.getJSONArray("baseAtt").getJSONObject(i);
									tempDecl1.put("ATT_ID",SysUtility.GetUUID());		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME", df.format(new Date())); //操作时间
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_ATT", tempArrs,"ATT_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "复制失败!");
										return;
							        }
								}
						 } 
						 //2、复制集装箱
						 JSONObject tempContD=new JSONObject();
						 JSONObject tempContD1=new JSONObject();
						 JSONArray tArray_1=new JSONArray();
						 tempDecl=getDataAccess().GetTableJSON("baseCont", "SELECT * FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseCont")) && tempDecl.getJSONArray("baseCont").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseCont").length(); i++) {
									tempDecl1=new JSONObject();
									tempArrs=new JSONArray();
								 	tempDecl1=tempDecl.getJSONArray("baseCont").getJSONObject(i);
								 	String oldcontid=(String) tempDecl1.get("CONT_ID");
								 	String newcontid=SysUtility.GetUUID();
									tempDecl1.put("CONT_ID",newcontid);		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME",df.format(new Date()));
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_CONT", tempArrs,"CONT_ID");
									if (InsertRt)
							        {   //3、复制集装箱详细
										tempContD=new JSONObject();
										
										tempContD=getDataAccess().GetTableJSON("baseContDetail", "SELECT * FROM ITF_DCL_IO_DECL_CONT_DETAIL WHERE CONT_ID=?",oldcontid);
										if(SysUtility.isNotEmpty(tempContD.getJSONArray("baseContDetail")) && tempContD.getJSONArray("baseContDetail").length()>0){
											for (int j = 0; j < tempContD.getJSONArray("baseContDetail").length(); j++) { 
												tempContD1=new JSONObject();
												tArray_1=new JSONArray(); 
												tempContD1=tempContD.getJSONArray("baseContDetail").getJSONObject(j);
												tempContD1.put("CONT_DT_ID",SysUtility.GetUUID());
												tempContD1.put("CONT_ID", newcontid);
												tempContD1.put("DECL_NO",DECL_NO);
												tempContD1.put("CREATE_TIME",df.format(new Date()));
												tempContD1.put("OPER_TIME",df.format(new Date()));
												tempContD1.put("PART_ID",SysUtility.getCurrentPartId());
												tempContD1.put("CREATOR",SysUtility.getCurrentUserIndx());
												tArray_1.put(tempContD1);
												InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_CONT_DETAIL", tArray_1,"CONT_DT_ID");
											}
										}
										
							        }
								}
						 }
						 //4、复制商品表
						 tempDecl= getDataAccess().GetTableJSON("baseGoods", "SELECT * FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseGoods")) && tempDecl.getJSONArray("baseGoods").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseGoods").length(); i++) {
								 	tempDecl1=new JSONObject();
								 	tempArrs=new JSONArray();
								 	tempDecl1=tempDecl.getJSONArray("baseGoods").getJSONObject(i);
								 	String oldgoodsid=(String) tempDecl1.get("GOODS_ID");
								 	String newgoodsid=SysUtility.GetUUID(); 
									tempDecl1.put("GOODS_ID",newgoodsid);		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME",df.format(new Date()));
									String PROD_VALID_DT=(String)tempDecl.getJSONArray("baseGoods").getJSONObject(i).get("PROD_VALID_DT");
									if(SysUtility.isNotEmpty(PROD_VALID_DT)){
										tempDecl1.put("PROD_VALID_DT", df.format(df.parse((String)tempDecl.getJSONArray("baseGoods").getJSONObject(i).get("PROD_VALID_DT")))); //产品有效期
									}else{
										tempDecl1.put("PROD_VALID_DT","");
									}
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", tempArrs,"GOODS_ID");
									if (InsertRt)
							        {   //5、货物箱货对应关系
										 tempContD=new JSONObject();
										 
										 tempContD=getDataAccess().GetTableJSON("baseGoodsCont", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_CONT WHERE GOODS_ID=?",oldgoodsid);
										 if(SysUtility.isNotEmpty(tempContD.getJSONArray("baseGoodsCont")) && tempContD.getJSONArray("baseGoodsCont").length()>0){
											for (int j = 0; j < tempContD.getJSONArray("baseGoodsCont").length(); j++) {
												tempContD1=new JSONObject();
												tArray_1=new JSONArray(); 
												tempContD1=tempContD.getJSONArray("baseGoodsCont").getJSONObject(j);
												tempContD1.put("GOODS_CONT_ID",SysUtility.GetUUID());
												tempContD1.put("GOODS_ID", newgoodsid);
												tempContD1.put("DECL_NO",DECL_NO);
												tempContD1.put("CREATE_TIME",df.format(new Date()));
												tempContD1.put("OPER_TIME",df.format(new Date()));
												tempContD1.put("PART_ID",SysUtility.getCurrentPartId());
												tempContD1.put("CREATOR",SysUtility.getCurrentUserIndx());
												tArray_1.put(tempContD1);
												InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_CONT", tArray_1,"GOODS_CONT_ID");
											}
										}
										 //6、货物许可证
										 tempContD=new JSONObject();
										 
										 tempContD=getDataAccess().GetTableJSON("baseGoodsLimit", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_LIMIT WHERE GOODS_ID=?",oldgoodsid);
										 if(SysUtility.isNotEmpty(tempContD.getJSONArray("baseGoodsLimit")) && tempContD.getJSONArray("baseGoodsLimit").length()>0){
											for (int j = 0; j < tempContD.getJSONArray("baseGoodsLimit").length(); j++) {
												tempContD1=new JSONObject();
												tArray_1=new JSONArray();
												tempContD1=tempContD.getJSONArray("baseGoodsLimit").getJSONObject(j);
												tempContD1.put("LIMIT_ID",SysUtility.GetUUID());
												tempContD1.put("GOODS_ID", newgoodsid);
												tempContD1.put("DECL_NO",DECL_NO);
												tempContD1.put("CREATE_TIME",df.format(new Date()));
												tempContD1.put("OPER_TIME",df.format(new Date()));
												tempContD1.put("PART_ID",SysUtility.getCurrentPartId());
												tempContD1.put("CREATOR",SysUtility.getCurrentUserIndx());
												tArray_1.put(tempContD1);
												InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_LIMIT", tArray_1,"LIMIT_ID");
											}
										}
										 //7、货物包装
										 tempContD=new JSONObject();
										 
										 tempContD=getDataAccess().GetTableJSON("baseGoodsPack", "SELECT * FROM ITF_DCL_IO_DECL_GOODS_PACK WHERE GOODS_ID=?",oldgoodsid);
										 if(SysUtility.isNotEmpty(tempContD.getJSONArray("baseGoodsPack")) && tempContD.getJSONArray("baseGoodsPack").length()>0){
											for (int j = 0; j < tempContD.getJSONArray("baseGoodsPack").length(); j++) {
												tempContD1=new JSONObject();
												tArray_1=new JSONArray();
												tempContD1=tempContD.getJSONArray("baseGoodsPack").getJSONObject(j);
												tempContD1.put("PACK_ID",SysUtility.GetUUID());
												tempContD1.put("GOODS_ID", newgoodsid);
												tempContD1.put("DECL_NO",DECL_NO);
												tempContD1.put("CREATE_TIME",df.format(new Date()));
												tempContD1.put("OPER_TIME",df.format(new Date()));
												tempContD1.put("PART_ID",SysUtility.getCurrentPartId());
												tempContD1.put("CREATOR",SysUtility.getCurrentUserIndx());
												tArray_1.put(tempContD1);
												InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS_PACK", tArray_1,"PACK_ID");
											}
										}
										
							        }
								}
						 }
						 //8、复制企业资质
						 tempDecl= getDataAccess().GetTableJSON("baseDeclLimit", "SELECT * FROM ITF_DCL_IO_DECL_LIMIT WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseDeclLimit")) && tempDecl.getJSONArray("baseDeclLimit").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseDeclLimit").length(); i++) {
								 	tempDecl1=new JSONObject();
								 	tempArrs=new JSONArray();
									tempDecl1=tempDecl.getJSONArray("baseDeclLimit").getJSONObject(i);
									tempDecl1.put("DECL_LIMIT_ID",SysUtility.GetUUID());		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME",df.format(new Date()));
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_LIMIT", tempArrs,"DECL_LIMIT_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "复制失败!");
										return;
							        }
								}
						 } 
						 //9、复制使用人
						 tempDecl= getDataAccess().GetTableJSON("baseDeclUser", "SELECT * FROM ITF_DCL_IO_DECL_USER WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseDeclUser")) && tempDecl.getJSONArray("baseDeclUser").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseDeclUser").length(); i++) {
								 	tempDecl1=new JSONObject();
								 	tempArrs=new JSONArray();
									tempDecl1=tempDecl.getJSONArray("baseDeclUser").getJSONObject(i);
									tempDecl1.put("USER_ID",SysUtility.GetUUID());		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME",df.format(new Date()));
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_USER", tempArrs,"USER_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "复制失败!");
										return;
							        }
								}
						 }
						 //10、复制附件表
						 tempDecl= getDataAccess().GetTableJSON("baseDeclMarkLob", "SELECT * FROM ITF_DCL_MARK_LOB WHERE DECL_NO=?",oldDeclno);
						 if(SysUtility.isNotEmpty(tempDecl.getJSONArray("baseDeclMarkLob")) && tempDecl.getJSONArray("baseDeclMarkLob").length()>0){
							 for (int i = 0; i < tempDecl.getJSONArray("baseDeclMarkLob").length(); i++) {
								 	tempDecl1=new JSONObject();
								 	tempArrs=new JSONArray();
									tempDecl1=tempDecl.getJSONArray("baseDeclMarkLob").getJSONObject(i);
									tempDecl1.put("MARK_ID",SysUtility.GetUUID());		
									tempDecl1.put("DECL_NO",DECL_NO);
									tempDecl1.put("PART_ID",SysUtility.getCurrentPartId());
									tempDecl1.put("CREATOR",SysUtility.getCurrentUserIndx());
									tempDecl1.put("CREATE_TIME",df.format(new Date()));
									tempDecl1.put("OPER_TIME",df.format(new Date()));
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_MARK_LOB", tempArrs,"MARK_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "复制失败!");
										return;
							        }
								}
						 } 
						 ReturnMessage(true, "复制成功!",DECL_NO);
			        }
				}
		 } 
	}
	boolean Checkdata() {
		boolean rt = true;

		JSONArray ckrows = new JSONArray();
		JSONObject r = new JSONObject();
		/*
		r.put("CheckTable", "DeclData");
		r.put("CheckField", "ORG_CODE");
		r.put("CheckValue", "[\\d]+");
		r.put("CheckMessage", "请选择报检地");
		ckrows.put(r);
		 */
		rt = CheckReg(getFormDatas(), ckrows);

		
		return rt;
	}

}
