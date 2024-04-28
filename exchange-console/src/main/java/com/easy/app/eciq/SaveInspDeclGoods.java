package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/SaveInspDeclGoods")
public class SaveInspDeclGoods extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public SaveInspDeclGoods()
	{
		this.SetCheckLogin(true);
	}
	String ErroeMsg="";
	private boolean Checks() throws LegendException{
		boolean rt = true;
		int goodCnameSpace=0;
		/*int goodEnameSpace=0;*/
		String goodCname=GetDataValue("GoodsData","DECL_GOODS_CNAME");
		for (int i = 0; i < goodCname.length(); i++) {
			  char c=goodCname.charAt(i);
			  if(Character.isSpace(c)){
				  goodCnameSpace++;
			  }
		}
	/*	String goodEname=GetDataValue("GoodsData","DECL_GOODS_ENAME");
		for (int i = 0; i < goodEname.length(); i++) {
			  char c=goodEname.charAt(i);
			  if(Character.isSpace(c)){
				  goodEnameSpace++;
			  }
		}*/
		if(goodCnameSpace>0){
			ErroeMsg="货物中文名称中不能含有空格";
			rt=false;
		}/*
		if(goodEnameSpace>0){
			ErroeMsg="货物英文名称中不能含有空格";
			rt=false;
		}*/
		return rt;
	}
	
	
	public void DoCommand() throws Exception{
		String allid=GetDataValue("GoodsData","allid");
		String INDX=GetDataValue("GoodsData", "GOODS_ID");
		String pIndx=GetDataValue("GoodsData", "DECL_NO");
		String HS_CODE=GetDataValue("GoodsData", "PROD_HS_CODE");
		String GOODS_CNAME=GetDataValue("GoodsData", "DECL_GOODS_CNAME");
		String STD_WEIGHT_UNIT_CODE=GetDataValue("GoodsData", "STD_WEIGHT_UNIT_CODE");
		String QTY=GetDataValue("GoodsData", "QTY");
		String WEIGHT=GetDataValue("GoodsData", "WEIGHT");
		String GIN_DECL_NO=GetDataValue("GoodsData", "IN_DECL_NO");
		boolean bool=IsResidueQty(INDX,pIndx,HS_CODE,GOODS_CNAME,STD_WEIGHT_UNIT_CODE,QTY,WEIGHT,GIN_DECL_NO);		
	    
		if(Checks()&&bool){
			if(SysUtility.isEmpty(allid)){
				String STD = GetDataValue("GoodsData","STD_TYPE");
				if(STD.equals("2")){
					SaveToTable("GoodsData","STD_WEIGHT_UNIT_CODE",GetDataValue("GoodsData","STD_MEASURE_CODE"));
					SaveToTable("GoodsData","STD_WEIGHT_UNIT_NAME",GetDataValue("GoodsData","STD_MEASURE_CODE_NAME"));
					SaveToTable("GoodsData","STD_WEIGHT",GetDataValue("GoodsData","STD_DISPLAY"));		
				}else{
					SaveToTable("GoodsData","STD_QTY_UNIT_CODE",GetDataValue("GoodsData","STD_MEASURE_CODE"));
					SaveToTable("GoodsData","STD_QTY_UNIT_NAME",GetDataValue("GoodsData","STD_MEASURE_CODE_NAME"));
					SaveToTable("GoodsData","STD_QTY",GetDataValue("GoodsData","STD_DISPLAY"));
				}
				String Indx = GetDataValue("GoodsData", "GOODS_ID");
				String CN = GetDataValue("GoodsData","CIQ_NAME");
				if(CN.length() > 25){
					SaveToTable("GoodsData","CIQ_NAME",CN.substring(0, 25));
				}
				if (SysUtility.isEmpty(Indx)) {
					InitFormData("GoodsListCount","SELECT ifnull(MAX(GOODS_NO),0) + 1 as GOODS_NO FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO = @GoodsData.DECL_NO");
					Indx = SysUtility.GetUUID();
					SaveToTable("GoodsData", "MNUFCTR_REG_NAME",GetDataValue("GoodsData", "MNUFCTR_REG_NAME") );
					SaveToTable("GoodsData","GOODS_ID",Indx);
					SaveToTable("GoodsData", "GOODS_NO", GetDataValue("GoodsListCount", "GOODS_NO"));
					if(!getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", getFormDatas().getJSONArray("GoodsData"),"GOODS_ID")){
						Indx = "";
					}
					else{
						String SQL = "Update ITF_DCL_IO_DECL_GOODS_PACK Set GOODS_ID = ? , GOODS_NO = ? Where DECL_NO = ? AND GOODS_ID = '0'";
						String[] ps = new String[]{ Indx , GetDataValue("GoodsListCount", "GOODS_NO") , GetDataValue("GoodsData", "DECL_NO") };
						getDataAccess().ExecSQL(SQL, ps);
					}
				}
				else{
					if(!getDataAccess().Update("ITF_DCL_IO_DECL_GOODS", getFormDatas().getJSONArray("GoodsData"),"GOODS_ID")){
						Indx = "";
					}
				}
				
				if (!SysUtility.isEmpty(Indx)) {
			        String PackIndx=GetDataValue("GoodsData", "ALLPACKINDX");
			        PackIndx = "'" + PackIndx.replace(",", "','") + "'";
			        if(!PackIndx.isEmpty())
			            getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_GOODS_PACK set GOODS_ID = ? Where Pack_ID IN ("+PackIndx+")",new String[]{Indx});
			        String ContIndx=GetDataValue("GoodsData", "ALLCONTINDX");
			        ContIndx = "'" + ContIndx.replace(",", "','") + "'";
			        if(!ContIndx.isEmpty())
			            getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_GOODS_CONT set GOODS_ID= ? Where GOODS_CONT_ID IN ("+ContIndx+")",new String[]{Indx});			
				}
		        
		        if (!SysUtility.isEmpty(Indx))
		        {
		        	ReturnMessage(true, "保存成功");
		        }
		        else
		        {
		        	ReturnMessage(false, "保存失败");	
		        }
		}else{ 
				boolean issave=false;
				String Indx = GetDataValue("GoodsData", "GOODS_ID");
				SaveToTable("GoodsData","INDX",Indx);
				SaveToTable("GoodsData","P_INDX",allid.substring(0, allid.length()-1).split(",")[0]);
				SaveToTable("GoodsData","HS_CODE",GetDataValue("GoodsData", "PROD_HS_CODE"));
				SaveToTable("GoodsData","CIQ_CODE",GetDataValue("GoodsData", "CIQ_CODE"));
				SaveToTable("GoodsData","GOODS_CNAME",GetDataValue("GoodsData", "DECL_GOODS_CNAME"));
				SaveToTable("GoodsData","GOODS_ENAME",GetDataValue("GoodsData", "DECL_GOODS_ENAME"));
				SaveToTable("GoodsData","ORIGIN_PLACE_CODE",GetDataValue("GoodsData", "ORI_CTRY_CODE"));
				SaveToTable("GoodsData","ORIGIN_PLACE_NAME",GetDataValue("GoodsData", "ORI_CTRY_NAME")); 
		        SaveToTable("GoodsData","ORIG_PLACE_CODE",GetDataValue("GoodsData", "ORIG_PLACE_CODE"));
				SaveToTable("GoodsData","ORIG_PLACE_NAME",GetDataValue("GoodsData", "ORIG_PLACE_NAME"));
				SaveToTable("GoodsData","QTY",GetDataValue("GoodsData", "QTY"));
				SaveToTable("GoodsData","PURPOSE_CODE",GetDataValue("GoodsData", "PURPOSE"));
				SaveToTable("GoodsData","PURPOSE_NAME",GetDataValue("GoodsData", "PURPOSE_NAME"));
				SaveToTable("GoodsData","GOODS_MODEL",GetDataValue("GoodsData", "GOODS_MODEL"));
				SaveToTable("GoodsData","QTY_UNIT_CODE",GetDataValue("GoodsData", "QTY_MEAS_UNIT"));
				SaveToTable("GoodsData","QTY_UNIT_NAME",GetDataValue("GoodsData", "QTY_MEAS_UNIT_NAME"));
				SaveToTable("GoodsData","WEIGHT",GetDataValue("GoodsData", "WEIGHT"));
				SaveToTable("GoodsData","WEIGHT_UNIT_CODE",GetDataValue("GoodsData", "WT_MEAS_UNIT"));
				SaveToTable("GoodsData","WEIGHT_UNIT_NAME",GetDataValue("GoodsData", "WT_MEAS_UNIT_NAME"));
				SaveToTable("GoodsData","GOODS_UNIT_PRICE",GetDataValue("GoodsData", "PRICE_PER_UNIT"));
				SaveToTable("GoodsData","GOODS_TOTAL_VALUES",GetDataValue("GoodsData", "GOODS_TOTAL_VAL"));
				SaveToTable("GoodsData","CURR_UNIT",GetDataValue("GoodsData", "CURRENCY"));
				SaveToTable("GoodsData","CURR_UNIT_NAME",GetDataValue("GoodsData", "CURRENCN"));
				SaveToTable("GoodsData","GOODS_ATTR",GetDataValue("GoodsData", "GOODS_ATTR"));
				SaveToTable("GoodsData","GOODS_ATTR_NAME",GetDataValue("GoodsData", "GOODS_ATTR_NAME"));
				
				
				if (SysUtility.isEmpty(Indx)) { 
					 issave= getDataAccess().Insert("T_DISTRIBUTION_GOODS", getFormDatas().getJSONArray("GoodsData"));
				}
				else{
					issave=getDataAccess().Update("T_DISTRIBUTION_GOODS", getFormDatas().getJSONArray("GoodsData"));
				}
		        if (issave)
		        { 
		        	SaveToTable("GoodsData", "allid", allid);
		        	ReturnMessage(true, "保存成功");
		        }
		        else
		        {
		         	SaveToTable("GoodsData", "allid", allid);
		        	ReturnMessage(false, "保存失败");	
		        }
		}
		}else{
			ReturnMessage(false, ErroeMsg);			
		}		
	}

	//验证数量或重量是否超过库存
	private boolean IsResidueQty(String Gindx,String indx,String HS_CODE,String GOODS_CNAME,String STD_WEIGHT_UNIT_CODE,String QTY,String WEIGHT,String GIN_DECL_NO) throws LegendException
	{
		//判断是否是一般贸易
		Datas dtTrade = getDataAccess().GetTableDatas("GetTrade","SELECT IN_DECL_NO,TRADE_MODE_CODE,OUT_IN_CODE FROM itf_dcl_io_decl WHERE decl_no=?",indx);
		if(dtTrade.GetTableRows("GetTrade")>0){
			String TRADE_MODE_CODE=dtTrade.GetTableValue("GetTrade", "TRADE_MODE_CODE");
			String OUT_IN_CODE=dtTrade.GetTableValue("GetTrade", "OUT_IN_CODE");
			String [] IN_DECL_NO=dtTrade.GetTableValue("GetTrade", "IN_DECL_NO").split(",");
			String DeclNo="";
			String InDeclNo="";
			if(TRADE_MODE_CODE.equals("11")&&OUT_IN_CODE.equals("2")&&SysUtility.isNotEmpty(GIN_DECL_NO)){
		
				for (int i = 0; i < IN_DECL_NO.length; i++) {
					DeclNo+="'"+IN_DECL_NO[i]+"',";
					if(i==0){
						InDeclNo="IN_DECL_NO LIKE'%"+IN_DECL_NO[i]+"%'";
					}else{
						InDeclNo+=" OR IN_DECL_NO LIKE'%"+IN_DECL_NO[i]+"%'";
					}
				}
				DeclNo=DeclNo.substring(0, DeclNo.length()-1);
				
				String sql="";
				String []parms=new String[]{};
				if(SysUtility.isEmpty(Gindx)){
					sql=SQLMap.getSelect("GetEciqDeclResidueQty");
					sql = sql.replace("#DECL_NO#", DeclNo);
					sql = sql.replace("#IN_DECL_NO#", InDeclNo);
					sql = sql.replace("##", "?");
					parms=new String[]{GIN_DECL_NO,HS_CODE,GOODS_CNAME};
				}else{
					sql=SQLMap.getSelect("GetEciqDeclResidueQtyIndx");
					sql = sql.replace("#DECL_NO#", DeclNo);
					sql = sql.replace("#IN_DECL_NO#", InDeclNo);
					sql = sql.replace("##", "?");
					parms=new String[]{Gindx,GIN_DECL_NO,HS_CODE,GOODS_CNAME};
				}
				Datas dtDecl = getDataAccess().GetTableDatas("GetDeclList", sql,parms);
				if (dtDecl.GetTableRows("GetDeclList") > 0) {
					double RESIDUEQTY=Double.parseDouble(dtDecl.GetTableValue("GetDeclList", "RESIDUEQTY"));
					double QtyCount=0;
					if(SysUtility.isEmpty(STD_WEIGHT_UNIT_CODE)){
						if(SysUtility.isNotEmpty(QTY))
						QtyCount=Double.parseDouble(QTY);
					}else{
						if(SysUtility.isNotEmpty(WEIGHT))
						QtyCount=Double.parseDouble(WEIGHT);
					}
					
					if(RESIDUEQTY<QtyCount){
						ReturnMessage(false, "商品数量或重量超过库存，请重新填写！");
						return false;
					}
				}else{
					ReturnMessage(false, "所选商品不存在当前入区申报单中，请重新选择！");
					return false;
				}
			}
		}
		return true;
	}

}
