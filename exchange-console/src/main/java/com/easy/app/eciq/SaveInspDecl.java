package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.query.SQLMap;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveInspDecl")
public class SaveInspDecl extends MainServlet {
	private static final long serialVersionUID = 4261239235630641154L;

	public SaveInspDecl() {
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		ErrMessages = "";
		String allid=GetDataValue("DeclData", "allid");		 //如果不为空则为合并时保存
		String oldDeclno=GetDataValue("DeclData", "DECL_NO");//记录一下复制前的DECL_NO
		String [] mergeIds=null;
		if(SysUtility.isNotEmpty(allid)){
			mergeIds=allid.substring(0, allid.length()-1).split(",");
		}
		if (Checkdata()) {
			boolean saveok = false;
			if (SysUtility.isEmpty(GetDataValue("DeclData", "DECL_NO"))) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SaveToTable("DeclData", "DECL_ID", SysUtility.GetUUID());
				SaveToTable("DeclData", "CREATE_TIME", df.format(new Date()));
				SaveToTable("DeclData", "DECL_STATUS_CODE", "0");
				SaveToTable("DeclData", "DECL_STATUS_NAME", "本地暂存");
				SaveToTable("DeclData", "DATA_SOURCE", "0"); // 数据来源
				SaveToTable("DeclData", "PART_ID",SysUtility.getCurrentPartId());
				String code = GetDataValue("DeclData", "DECL_REG_NO");
				//String kind = GetDataValue("DeclData", "APL_KIND");
				String seq = SequenceFactory.getSequence("DECL_NO");
				this.SaveToTable("DeclData", "DECL_NO", code + seq);
				
				String eciquid = SysUtility.getCurrentUserDataValue("ECIQ_USER_NAME");
				String eciqpwd = SysUtility.getCurrentUserDataValue("ECIQ_PASS_WORD");
				
				SaveToTable("DeclData","ECIQ_USER_NAME",eciquid);
				SaveToTable("DeclData","ECIQ_PASS_WORD",eciqpwd);
				SaveToTable("DeclData","TECH_REG_CODE", SysUtility.getCurrentOrgId());
				
				saveok = getDataAccess().Insert("ITF_DCL_IO_DECL",
						getFormDatas().getJSONArray("DeclData"), "DECL_NO");
			} else {
				saveok = getDataAccess().Update("ITF_DCL_IO_DECL",
						getFormDatas().getJSONArray("DeclData"), "DECL_NO");
			}
			if (saveok) {
				String declno = GetDataValue("DeclData", "DECL_NO");
				String Conindx = GetDataValue("DeclData", "CON_INDX");
				getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_LIMIT set DECL_NO=? where DECL_NO = ?",new String[] { declno, Conindx });
				getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_ATT set DECL_NO=? where DECL_NO = ?",new String[] { declno, Conindx });
				getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_USER set DECL_NO=? where DECL_NO = ?",new String[] { declno, Conindx });
				getDataAccess().ExecSQL("update ITF_DCL_MARK_LOB set DECL_NO=? where DECL_NO = ?",new String[] { declno, Conindx });
				InitFormData("RECP","select COUNT(RSP_CODES) as C from ITF_DCL_RECEIPTS WHERE DECL_NO = @DeclData.DECL_NO AND RSP_CODES = '0'");
				if (GetDataValue("RECP", "C").equals("0")) {
					getDataAccess().ExecSQL("Insert Into ITF_DCL_RECEIPTS (Indx,RSP_CODES,RSP_INFO,RSP_GEN_TIME,DECL_NO,RESPONSE_ID) Values (SEQ_ITF_DCL_RECEIPTS.NEXTVAL,'0','本地暂存',Now(),?,?)",new String[] { declno,SysUtility.GetUUID() });
				}
				if(SysUtility.isNotEmpty(mergeIds)){
					//如果是合并时保存
	        		boolean saveGood=false;
	        		String pindx="";
	    			String [] searchIds=allid.substring(0,allid.length()-1).split(",");
	    			if(SysUtility.isNotEmpty(searchIds)){
	    				for (int i = 0; i < searchIds.length; i++) {
	    					pindx+="'"+searchIds[i]+"',";
						}
	    			}
	    			String sqlUp = SQLMap.getSelect("GetDistriGoodsByPindxs");
	    			sqlUp = sqlUp.replace("#PINDX#", pindx.substring(0, pindx.length()-1));
	    			
	        		 Datas dtConfig=getDataAccess().GetTableDatas("disGoods",sqlUp);
	        		 for (int i = 0; i < dtConfig.GetTableRows("disGoods"); i++) {
	        			 String sql="INSERT INTO ITF_DCL_IO_DECL_GOODS "
		        			 		+ "(GOODS_ID,DECL_NO,GOODS_NO, PROD_HS_CODE,CIQ_CODE,DECL_GOODS_CNAME,DECL_GOODS_ENAME,"
		        			 		+ "QTY,QTY_MEAS_UNIT,QTY_MEAS_UNIT_NAME,WEIGHT,WT_MEAS_UNIT,WT_MEAS_UNIT_NAME,"
		        			 		+ "STD_QTY,STD_QTY_UNIT_CODE,STD_QTY_UNIT_NAME,STD_WEIGHT,STD_WEIGHT_UNIT_CODE,"
		        			 		+ "STD_WEIGHT_UNIT_NAME,PRICE_PER_UNIT,GOODS_TOTAL_VAL,CURRENCY,CURRENCN,GOODS_SPEC,GOODS_BRAND,"
		        			 		+ "ORI_CTRY_CODE,ORI_CTRY_NAME,PURPOSE,PURPOSE_NAME) "
		        			 		+ "VALUES('"+SysUtility.GetUUID()+"','"+GetDataValue("DeclData", "DECL_NO")+"',"+(i+1)+",'"+dtConfig.GetTableValue("disGoods","PROD_HS_CODE",i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "CIQ_CODE", i)+"','"+dtConfig.GetTableValue("disGoods", "DECL_GOODS_CNAME", i)+"','"+dtConfig.GetTableValue("disGoods", "DECL_GOODS_ENAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "QTY", i)+"','"+dtConfig.GetTableValue("disGoods", "QTY_MEAS_UNIT", i)+"','"+dtConfig.GetTableValue("disGoods", "QTY_MEAS_UNIT_NAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "WEIGHT", i)+"','"+dtConfig.GetTableValue("disGoods", "WT_MEAS_UNIT", i)+"','"+dtConfig.GetTableValue("disGoods", "WT_MEAS_UNIT_NAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "STD_QTY", i)+"','"+dtConfig.GetTableValue("disGoods", "STD_QTY_UNIT_CODE", i)+"','"+dtConfig.GetTableValue("disGoods", "STD_QTY_UNIT_NAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "STD_WEIGHT", i)+"','"+dtConfig.GetTableValue("disGoods", "STD_WEIGHT_UNIT_CODE", i)+"','"+dtConfig.GetTableValue("disGoods", "STD_WEIGHT_UNIT_NAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "PRICE_PER_UNIT", i)+"','"+dtConfig.GetTableValue("disGoods", "GOODS_TOTAL_VAL", i)+"','"+dtConfig.GetTableValue("disGoods", "CURRENCY", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "CURRENCN", i)+"','"+dtConfig.GetTableValue("disGoods", "GOODS_SPEC", i)+"','"+dtConfig.GetTableValue("disGoods", "GOODS_BRAND", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "ORI_CTRY_CODE", i)+"','"+dtConfig.GetTableValue("disGoods", "ORI_CTRY_NAME", i)+"',"
		        			 				+ "'"+dtConfig.GetTableValue("disGoods", "PURPOSE", i)+"','"+dtConfig.GetTableValue("disGoods", "PURPOSE_NAME", i)+"')";
	        			 saveGood= getDataAccess().ExecSQL(sql);
	        			 saveGood= getDataAccess().ExecSQL("UPDATE T_DISTRIBUTION SET DECL_NO='"+GetDataValue("DeclData", "DECL_NO")+"',APP_DATE=Now() WHERE INDX IN("+allid.substring(0, allid.length()-1)+")");
	        			 if(saveGood==false){
	        				 getDataAccess().RoolbackTrans();
	        			 } 
	        		 }
	        	}  
				ReturnMessage(true, "保存成功", "", getFormDatas().toString());
			} else {
				ReturnMessage(false, "保存失败，请重新保存");
			}
		}
		else{
			ReturnMessage(false, StringEscapeUtils.escapeEcmaScript(ErrMessages));
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
