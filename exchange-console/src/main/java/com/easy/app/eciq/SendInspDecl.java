package com.easy.app.eciq;

import java.util.List;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SendInspDecl")
public class SendInspDecl extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public SendInspDecl()
	{
		this.SetCheckLogin(true);
	}
	
	private void AddCheck(JSONArray ckrows,String ckt , String ckf , String ck , String msg){
		try {
			JSONObject r = new JSONObject();
			r.put("CheckTable", ckt);
			r.put("CheckField", ckf);
			r.put("CheckValue", ck);
			r.put("CheckMessage", msg);
			ckrows.put(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
	private boolean Checks(String APL_KIND){
		boolean rt = true;
		JSONArray ckrows = new JSONArray();
		AddCheck(ckrows,"CheckDECL","DECL_DATE","[\\S\\s]+","请填写报检日期");
		AddCheck(ckrows,"CheckDECL","DECL_CODE","[\\S\\s]+","请填写报检类别");
		AddCheck(ckrows,"CheckDECL","ORG_CODE","[\\S\\s]+","请填写报检地");
		AddCheck(ckrows,"CheckDECL","VSA_ORG_CODE","[\\S\\s]+","请填写领证地");
		AddCheck(ckrows,"CheckDECL","DECL_PERSN_CERT_NO","[\\S\\s]+","请填写报检员证号");
		AddCheck(ckrows,"CheckDECL","DECL_PERSON_NAME","[\\S\\s]+","请填写报检员名称");
		AddCheck(ckrows,"CheckDECL","CONTACTPERSON","[\\S\\s]+","请填写联系人");
		AddCheck(ckrows,"CheckDECL","CONT_TEL","[\\S\\s]+","请填写电话");
		AddCheck(ckrows,"CheckDECL","TRANS_MODE_CODE","[\\S\\s]+","请填写运输方式");
		AddCheck(ckrows,"CheckDECL","TRADE_MODE_CODE","[\\S\\s]+","请填写贸易方式");
		AddCheck(ckrows,"CheckDECL","CONTRACT_NO","[\\S\\s]+","请填写合同号");
		AddCheck(ckrows,"CheckDECL","GOODS_PLACE","[\\S\\s]+","请填写货物存放地点");
		AddCheck(ckrows,"CheckDECL","MARK_NO","[\\S\\s]+","请填写标记及号码");
		AddCheck(ckrows,"CheckDECL","ATTA_COLLECT_NAME","[\\S\\s]+","请填写随附单据");
		
		AddCheck(ckrows,"CheckGoods","PROD_HS_CODE","[\\S\\s]+","请填写商品HS编码");
		AddCheck(ckrows,"CheckGoods","CIQ_CODE","[\\S\\s]+","请填写商品CIQ编码");
		/*AddCheck(ckrows,"CheckGoods","PRICE_PER_UNIT","[\\S\\s]+","请填写商品货物单价");*/
		AddCheck(ckrows,"CheckGoods","STD_DISPLAY","[\\S\\s]+","请填写商品标准量");
		AddCheck(ckrows,"CheckGoods","PACK_CATG","[\\S\\s]+","请填写商品主包装");
		AddCheck(ckrows,"CheckGoods","GOODS_TOTAL_VAL","[\\S\\s]+","请填写商品货物总值");
		AddCheck(ckrows,"CheckGoods","CURRENCY","[\\S\\s]+","请填写商品货币单位");
		AddCheck(ckrows,"CheckGoods","PURPOSE","[\\S\\s]+","请填写商品用途");
		
		if(APL_KIND.equals("O")){
			AddCheck(ckrows,"CheckDECL","PURP_ORG_CODE","[\\S\\s]+","请填写口岸机构");
			AddCheck(ckrows,"CheckDECL","INSP_ORG_CODE","[\\S\\s]+","请填写施检地");
			AddCheck(ckrows,"CheckDECL","DECL_REG_NO","[\\S\\s]+","请填写报检登记号");
			AddCheck(ckrows,"CheckDECL","DECL_REG_NAME","[\\S\\s]+","请填写报检单位名称");
			AddCheck(ckrows,"CheckDECL","CONSIGNOR_CODE","[\\S\\s]+","请填写 发货人编码");
			AddCheck(ckrows,"CheckDECL","CONSIGNOR_CNAME","[\\S\\s]+","请填写 发货人中文名称");
			AddCheck(ckrows,"CheckDECL","TRADE_COUNTRY_CODE","[\\S\\s]+","请填写输往国家(地区)");
			AddCheck(ckrows,"CheckDECL","ARRIV_PORT_CODE","[\\S\\s]+","请填写到达口岸");

			AddCheck(ckrows,"CheckGoods","ORIG_PLACE_CODE","[\\S\\s]+","请填写商品产地");
			AddCheck(ckrows,"CheckGoods","MNUFCTR_REG_NO","[\\S\\s]+","请填写商品生产单位注册号");
			AddCheck(ckrows,"CheckGoods","MNUFCTR_REG_NAME","[\\S\\s]+","请填写商品生产单位名称");
			AddCheck(ckrows,"CheckGoods","PRODUCE_DATE","[\\S\\s]+","请填写商品生产日期");
			
		}else{ 
			AddCheck(ckrows,"CheckDECL","INSP_ORG_CODE","[\\S\\s]+","请填写口岸机构");
			AddCheck(ckrows,"CheckDECL","DECL_REG_NO","[\\S\\s]+","请填写报检登记号");
			AddCheck(ckrows,"CheckDECL","CONSIGNEE_CODE","[\\S\\s]+","请填写收货人编码");
			AddCheck(ckrows,"CheckDECL","CONSIGNEE_CNAME","[\\S\\s]+","请填写收货人中文名称");
			AddCheck(ckrows,"CheckDECL","CONSIGNEE_ENAME","[\\S\\s]+","请填写收货人英文名称");
			AddCheck(ckrows,"CheckDECL","BILL_LAD_NO","[\\S\\s]+","请填写提/运单号");
			AddCheck(ckrows,"CheckDECL","DELIVERY_ORDER","[\\S\\s]+","请填写提货单号");
			AddCheck(ckrows,"CheckDECL","DESP_CTRY_CODE","[\\S\\s]+","请填写启运国家/地区");
			AddCheck(ckrows,"CheckDECL","DESP_PORT_CODE","[\\S\\s]+","请填写启运口岸");
			AddCheck(ckrows,"CheckDECL","DESP_DATE","[\\S\\s]+","请填写启运日期");
			AddCheck(ckrows,"CheckDECL","TRADE_COUNTRY_CODE","[\\S\\s]+","请填写贸易国别/地区");
			AddCheck(ckrows,"CheckDECL","DEST_CODE","[\\S\\s]+","请填写目的地");
			AddCheck(ckrows,"CheckDECL","ENTY_PORT_CODE","[\\S\\s]+","请填写入境口岸");
			AddCheck(ckrows,"CheckDECL","GDS_ARVL_DATE","[\\S\\s]+","请填写到货日期");
			AddCheck(ckrows,"CheckGoods","ORI_CTRY_CODE","[\\S\\s]+","请填写商品原产国/地区");
		}
		
		rt = CheckReg(getFormDatas(), ckrows);

		return rt;
	}
	
	public void DoCommand() throws Exception{
		Properties properties = SysUtility.GetProperties("System.properties");
		String eciq_MsgType=properties.getProperty("eciq_MsgType");//消息类型
		String eciq_SignData=properties.getProperty("eciq_SignData");//密钥
		String eciq_MessageSource=properties.getProperty("eciq_MessageSource");//接入方代码
		String eciq_MessageDest=properties.getProperty("eciq_MessageDest"); //接收方代码
		String ExportInspIsLaw=properties.getProperty("ExportInspIsLaw");//区外进区是否报法检通道（盐田需求）
		String DECL_NO = GetDataValue("DeclData", "DECL_NO");
		String IN_DECL_NO = GetDataValue("DeclData", "IN_DECL_NO");
		String APL_KIND=GetDataValue("DeclData", "APL_KIND");
		String OUT_IN_CODE=GetDataValue("DeclData", "OUT_IN_CODE");
		//判断参数为Y，并且是区外进区
		if("Y".equals(ExportInspIsLaw)&&"1".equals(OUT_IN_CODE)&&"O".equals(APL_KIND)){
			eciq_MsgType="104";
		}
		if (!SysUtility.isEmpty(DECL_NO)) {
			//验证数量或重量是否超过库存
			boolean bool=IsResidueQty(DECL_NO,IN_DECL_NO);
			
			InitFormData("CheckDECL","select * from ITF_DCL_IO_DECL where DECL_NO = @DeclData.DECL_NO");
			InitFormData("CheckGoods",SQLMap.getSelect("InspCheckGoods"));
			if(Checks(GetDataValue("DeclData", "APL_KIND"))&&bool){
	            getDataAccess().BeginTrans();
	            
	            //表体为空不让申报
	            List lstgoods = SQLExecUtils.query4List("select * FROM ITF_DCL_IO_DECL_GOODS where DECL_NO='"+DECL_NO+"'"); 
	    		int sizegoods = lstgoods.size();
	    		if(lstgoods.size()==0){
	    			ReturnMessage(false, "申报单商品为空，不允许申报！");
	                return;
	    		}
	    		
	    		//判断HS编号和ciq编号是否相符
	    		Datas dtGoods = getDataAccess().GetTableDatas("GoodsList", "select * FROM ITF_DCL_IO_DECL_GOODS where DECL_NO=?",DECL_NO);
				JSONArray arrsGoods = dtGoods.getDataJSON().optJSONArray("GoodsList");
				if (dtGoods.GetTableRows("GoodsList") > 0) {
					for(int i=0;i<arrsGoods.length();i++){
						String HsCode=arrsGoods.optJSONObject(i).optString("PROD_HS_CODE");
						String CiqCode=arrsGoods.optJSONObject(i).optString("CIQ_CODE");
						String goodCname=arrsGoods.optJSONObject(i).optString("DECL_GOODS_CNAME");
						String goodNo=arrsGoods.optJSONObject(i).optString("GOODS_NO");
						String CiqNo=CiqCode.substring(0,10);
						if(!CiqNo.equals(HsCode)){
							ReturnMessage(false, "HS编号:"+HsCode+"和ciq编号:"+CiqCode+"不符，不允许申报！");
			                return;
						}
						if(SysUtility.isNotEmpty(goodCname)){
							int goodCnameSpace=0;
							for (int j = 0; j < goodCname.length(); j++) {
								  char c=goodCname.charAt(j);
								  if(Character.isSpace(c)){
									  goodCnameSpace++;
								  }
							}
							if(goodCnameSpace>0){
								ReturnMessage(false, "货物序号为:【"+goodNo+"】的商品货物中文名称中不能含有空格！");
								return;
							}
						}
					}
				}
	    		
				
				
	    		List lstconts;
	    		int sizeconts;
	    		/*
	    		List lstconts = SQLExecUtils.query4List("select * FROM ITF_DCL_IO_DECL_CONT where DECL_NO='"+DECL_NO+"'"); 
	     		int sizeconts = lstconts.size();
	     		if(lstconts.size()==0){
	     			ReturnMessage(false, "申报单集装箱为空，不允许申报");
	                 return;
	     		}
	     		*/
	            
				String test = "AND DECL_STATUS_CODE in ('0') AND DECL_NO='" + DECL_NO+"'";
				StringBuffer SelectSQL = new StringBuffer();
				SelectSQL.append("SELECT * FROM ITF_DCL_IO_DECL WHERE 1=1 " + test);
				List lstCanSend = SQLExecUtils.query4List(SelectSQL.toString());
				int sizeCanSend = lstCanSend.size();
				if (sizeCanSend !=0) {
					StringBuffer UpdateSQL = new StringBuffer();
					UpdateSQL.append("update ITF_DCL_IO_DECL set DECL_STATUS_CODE = '1',DECL_STATUS_NAME='已上传',OPER_CODE = '"+SysUtility.getCurrentName()+"', OPER_TIME=Now() where DECL_NO= '" + DECL_NO+"'");
					boolean isFlag= getDataAccess().ExecSQL(UpdateSQL.toString());
					if(isFlag){ 
						SaveToTable("DeclData","DECL_STATUS_CODE","1");
						SaveToTable("DeclData","DECL_STATUS_NAME","已上传");
						String SqlTemp=String.format("select * from ITF_DCL_RECEIPTS WHERE DECL_NO = '%s' AND RSP_CODES = '1'",DECL_NO);
						lstconts = SQLExecUtils.query4List(SqlTemp);
						sizeCanSend = lstconts.size();
			    		if(sizeCanSend==0){
			    			JSONObject tempDecl = new JSONObject();
			    			JSONArray tempArrs = new JSONArray(); 
							tempDecl.put("DECL_NO",DECL_NO); 
							/*if(SysUtility.isNotEmpty(DECL_NO))	{
								tempDecl.put("RSP_NO",GetNoFromGoods(DECL_NO));	
							}*/
							tempDecl.put("RSP_CODES","1");								
							tempDecl.put("RSP_INFO","已上传"); 
							tempDecl.put("RSP_GEN_TIME",SysUtility.getSysDate());	
							tempDecl.put("RESPONSE_ID", SysUtility.GetUUID());
							tempArrs.put(tempDecl);
							
							int ResIndx =SaveToDB(tempArrs, "ITF_DCL_RECEIPTS");
							if (SysUtility.isEmpty(ResIndx))
					        {
								ReturnMessage(false, "上传失败！");
								return;
					        }
			    		}
			    		
			    		StringBuffer InsertSQL = new StringBuffer();
//						InsertSQL.append("INSERT INTO EXS_HANDLE_SENDER(INDX,MSG_TYPE,MSG_NO) VALUES(SEQ_EXS_HANDLE_SENDER.nextval,'103', ?)");
						InsertSQL.append("INSERT INTO EXS_HANDLE_SENDER(INDX,MSG_TYPE,MSG_NO,SIGN_DATA,MESSAGE_SOURCE,MESSAGE_DEST,TECH_REG_CODE) VALUES(SEQ_EXS_HANDLE_SENDER.nextval,'"+eciq_MsgType+"', ?,'"+eciq_SignData+"','"+eciq_MessageSource+"','"+eciq_MessageDest+"','"+SysUtility.getCurrentOrgId()+"')");
						isFlag= getDataAccess().ExecSQL(InsertSQL.toString(),DECL_NO);
						if(!isFlag){
							ReturnMessage(false, "上传失败！");
							return;
						}
						ReturnMessage(true, "上传成功", "", TableToJSON("DeclData"));
						 
					}else {
						ReturnMessage(true, "上传失败!");
					}
				} else {
					ReturnMessage(true, "没有可上传的数据");
				}				
			}
			else{
	        	ReturnMessage(false,  StringEscapeUtils.escapeEcmaScript(ErrMessages));			
			}
			
		}
        else
        {
        	ReturnMessage(false, "传入的数据有错误，无法上传！");
        }
	}

	//验证数量或重量是否超过库存
	private boolean IsResidueQty(String DECL_NO,String IN_DECL_NO) throws LegendException
	{
		//判断是否是一般贸易
		Datas dtTrade = getDataAccess().GetTableDatas("GetTrade","SELECT TRADE_MODE_CODE,OUT_IN_CODE FROM itf_dcl_io_decl WHERE decl_no=?",DECL_NO);
		if(dtTrade.GetTableRows("GetTrade")>0){
			String TRADE_MODE_CODE=dtTrade.GetTableValue("GetTrade", "TRADE_MODE_CODE");
			String OUT_IN_CODE=dtTrade.GetTableValue("GetTrade", "OUT_IN_CODE");
			String [] INDECL_NO=IN_DECL_NO.split(",");
			String DeclNo="";
			String InDeclNo="";
			if(TRADE_MODE_CODE.equals("11")&&OUT_IN_CODE.equals("2")&&SysUtility.isNotEmpty(IN_DECL_NO)){

				for (int i = 0; i < INDECL_NO.length; i++) {
					DeclNo+="'"+INDECL_NO[i]+"',";
					if(i==0){
						InDeclNo="IN_DECL_NO LIKE'%"+INDECL_NO[i]+"%'";
					}else{
						InDeclNo+=" OR IN_DECL_NO LIKE'%"+INDECL_NO[i]+"%'";
					}
				}
				DeclNo=DeclNo.substring(0, DeclNo.length()-1);
				
				//判断上传申报单是否是集报分送合并过来
				Datas dtDis=getDataAccess().GetTableDatas("GetDis", "SELECT * FROM t_distribution WHERE DECL_NO=?",DECL_NO);
				
				String sql="";
				String []parms=new String[]{};
				if(dtDis.GetTableRows("GetDis")>0){
					sql=SQLMap.getSelect("GetEciqDeclResidueQtySend");
					sql = sql.replace("#DECL_NO#", DeclNo);
					sql = sql.replace("#IN_DECL_NO#", InDeclNo);
					sql = sql.replace("##", "?");
//					parms=new String[]{IN_DECL_NO,IN_DECL_NO,IN_DECL_NO};
				}else{
					sql=SQLMap.getSelect("GetEciqSendQtyDeclNo");
					sql = sql.replace("#DECL_NO#", DeclNo);
					sql = sql.replace("#IN_DECL_NO#", InDeclNo);
					sql = sql.replace("##", "?");
					parms=new String[]{DECL_NO};
				}
				
				Datas dtDecl = getDataAccess().GetTableDatas("GetDeclList", sql,parms);
				JSONArray arrsGoods = dtDecl.getDataJSON().optJSONArray("GetDeclList");
				if (dtDecl.GetTableRows("GetDeclList") > 0) {
					for(int i=0;i<arrsGoods.length();i++){
						double RESIDUEQTY=Double.parseDouble(arrsGoods.optJSONObject(i).optString("RESIDUEQTY"));
						String PROD_HS_CODE=arrsGoods.optJSONObject(i).optString("PROD_HS_CODE");
						if(RESIDUEQTY<0){
							ReturnMessage(false, "商品:"+PROD_HS_CODE+",数量或重量超过库存，不允许上传！");
							return false;
						}
					}
				}else{
					ReturnMessage(false, "所选商品不存在当前入区申报单中，不允许上传！");
					return false;
				}
			}
		}
		return true;
	}
	
	//设置商品序号
	private String GetNoFromGoods(String DECL_NO) throws LegendException
	{
		Datas dt = getDataAccess().GetTableDatas("GoodsList", "SELECT max(RSP_NO) RSP_NO FROM ITF_DCL_RECEIPTS WHERE DECL_NO = ?", DECL_NO);
	    if (dt.GetTableRows("GoodsList") > 0)
	    {
	        if (dt.GetTableValue("GoodsList", "RSP_NO").length()>0)
	        {
	            return String.valueOf(Integer.parseInt(dt.GetTableValue("GoodsList", "RSP_NO")) + 1);
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


