package com.easy.app.eciq;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.query.SQLExecUtils;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveXFInspDecl")
public class SaveXFInspDecl extends MainServlet {
	private static final long serialVersionUID = 4261239235630641154L;

	public SaveXFInspDecl() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		 //声明数据源
		 Connection xfconn = SysUtility.getCurrentConnection("xfsource");
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
		 //0、导入表头
		 tempDeclHead= getDataAccess().GetTableDatas(xfconn,"baseDeclData", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL WHERE DECL_NO=?",oldDeclno);
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
				 	tempDeclHead1=tempDeclHead.getJSONArray("baseDeclData").getJSONObject(0);//取0条， 防止数据库出现多条相同DECL_NO垃圾数据，会导入重复
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
					tempDeclHead1.put("ECIQ_USER_NAME",SysUtility.getCurrentUserDataValue("ECIQ_USER_NAME"));
					tempDeclHead1.put("ECIQ_PASS_WORD",SysUtility.getCurrentUserDataValue("ECIQ_PASS_WORD"));
					tempDeclHead1.put("TECH_REG_CODE", SysUtility.getCurrentOrgId());
					tempDeclHead1.put("DECL_REG_CODE", SysUtility.getCurrentEntRegNo());
					tempDeclHead1.put("Part_ID",SysUtility.getCurrentPartId());
					tempDeclHead1.put("APL_KIND", APL_KIND);
					tempDeclHead1.put("CON_INDX", con_indx);
					tempHeadArrs.put(tempDeclHead1);
					InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL", tempHeadArrs,"DECL_NO");
					if (InsertRt)
			        {
						 //1、导入随附单据
						 tempDecl= getDataAccess().GetTableDatas(xfconn,"baseAtt", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_ATT WHERE DECL_NO=?",oldDeclno);
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
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_ATT", tempArrs,"ATT_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "导入失败!");
										return;
							        }
								}
						 } 
						 //2、导入集装箱
						 JSONObject tempContD=new JSONObject();
						 JSONObject tempContD1=new JSONObject();
						 JSONArray tArray_1=new JSONArray();
						 tempDecl=getDataAccess().GetTableDatas(xfconn,"baseCont", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_CONT WHERE DECL_NO=?",oldDeclno);
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
							        {   //3、导入集装箱详细
										tempContD=new JSONObject();
										
										tempContD=getDataAccess().GetTableDatas(xfconn,"baseContDetail", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_CONT_DETAIL WHERE CONT_ID=?",oldcontid);
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
						 //4、导入商品表
						 tempDecl= getDataAccess().GetTableDatas(xfconn,"baseGoods", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_GOODS WHERE DECL_NO=?",oldDeclno);
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
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", tempArrs,"GOODS_ID");
									if (InsertRt)
							        {   //5、货物箱货对应关系
										 tempContD=new JSONObject();
										 
										 tempContD=getDataAccess().GetTableDatas(xfconn,"baseGoodsCont", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_GOODS_CONT WHERE GOODS_ID=?",oldgoodsid);
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
										 
										 tempContD=getDataAccess().GetTableDatas(xfconn,"baseGoodsLimit", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_GOODS_LIMIT WHERE GOODS_ID=?",oldgoodsid);
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
										 
										 tempContD=getDataAccess().GetTableDatas(xfconn,"baseGoodsPack", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_GOODS_PACK WHERE GOODS_ID=?",oldgoodsid);
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
						 //8、导入企业资质
						 tempDecl= getDataAccess().GetTableDatas(xfconn,"baseDeclLimit", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_LIMIT WHERE DECL_NO=?",oldDeclno);
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
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_LIMIT", tempArrs,"DECL_LIMIT_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "导入失败!");
										return;
							        }
								}
						 } 
						 //9、导入使用人
						 tempDecl= getDataAccess().GetTableDatas(xfconn,"baseDeclUser", "SELECT * FROM ECIQ_GDATA.ITF_DCL_IO_DECL_USER WHERE DECL_NO=?",oldDeclno);
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
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL_USER", tempArrs,"USER_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "导入失败!");
										return;
							        }
								}
						 }
						 //10、导入附件表
						 tempDecl= getDataAccess().GetTableDatas(xfconn,"baseDeclMarkLob", "SELECT * FROM ECIQ_GDATA.ITF_DCL_MARK_LOB WHERE DECL_NO=?",oldDeclno);
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
									tempArrs.put(tempDecl1);
									InsertRt = getDataAccess().Insert("ITF_DCL_MARK_LOB", tempArrs,"MARK_ID");
									if (!InsertRt)
							        {
										ReturnMessage(false, "导入失败!");
										return;
							        }
								}
						 } 
						 ReturnMessage(true, "导入成功!",DECL_NO);
			        }
				}
		 } 
	}
}
