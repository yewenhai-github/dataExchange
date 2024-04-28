package com.easy.app.eciq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.sequence.SequenceFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetExportInspsaveDecl")
public class GetExportInspsaveDecl  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetExportInspsaveDecl()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		JSONArray rows;
	 	JSONArray insertData = new JSONArray();
		JSONArray updateDate = new JSONArray();
		boolean InsertRt =false;
		boolean updateRt =false;
		if (SysUtility.isEmpty(GetDataValue("DeclData", "DECL_NO"))) {
			/* String II_SERIAL_NO = SequenceFactory.getSequence("II_SERIAL_NO",Utility.DefaultOrgId);	*/
 			//String DECL_NO = SequenceFactory.getSequence("DECL_NO",Utility.DefaultOrgId);
 			/*SaveToTable("DeclData", "OrgId_ID", SysUtility.getCurrentOrgId());
 			SaveToTable("DeclData", "Decl_date",SysUtility.getSysDate());
 			SaveToTable("DeclData", "ENT_CODE", SysUtility.getCurrentEntCode());
	        SaveToTable("DeclData", "ENT_NAME", SysUtility.getCurrentEntName());*/
           /* SaveToTable("DeclData", "BILL_TYPE_CODE", "01");  //单据类型 0暂存 
            SaveToTable("DeclData", "BILL_TYPE_NAME", "暂存");//单据类型  暂存
            SaveToTable("DeclData", "QF_TYPE_CODE", "0");     //""状" 0 未检"
            SaveToTable("DeclData", "QF_TYPE_NAME", "未检");//""状" 0 未检"
            SaveToTable("DeclData", "CF_TYPE_CODE", "0");     //查验状" 0 未查"
            SaveToTable("DeclData", "CF_TYPE_NAME", "未查");//查验状" 0 未查"  
  			SaveToTable("DeclData", "II_SERIAL_NO", II_SERIAL_NO);
	        //申报后由""端生"
            // SaveToTable("DeclData", "DECL_NO", DECL_NO);
	        SaveToTable("DeclData", "STOCK_FLAG", "C");//集货/备货标识 C备货 B集货  
            SaveToTable("DeclData", "STOCK_FLAG_NAME", "备货");//集货/备货标识名称*/
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dfDate = new SimpleDateFormat("yy");
			SaveToTable("DeclData", "DECL_NO", "Z"+getSession().getAttribute("CHECKORGCODE").toString()+"O"+dfDate.format(new Date())+SequenceFactory.getSequence("DECL_NO"));
			
			SaveToTable("DeclData", "APL_KIND", "O");
			SaveToTable("DeclData", "DECL_ID",SysUtility.GetUUID());
			SaveToTable("DeclData", "CREATE_TIME", df.format(new Date()));
			SaveToTable("DeclData", "DECL_STATUS_CODE", "0");
			SaveToTable("DeclData", "DECL_STATUS_NAME", "本地暂存");
			SaveToTable("DeclData", "DATA_SOURCE", "0");  //数据来源
			SaveToTable("DeclData", "TECH_REG_CODE",getSession().getAttribute("TECH_REG_CODE").toString());
			//动态设置主键 
			SaveToTable("DeclData", "DECL_NO", SequenceFactory.getSequence("DECL_NO"));
			rows = getFormDatas().getJSONArray("DeclData");
			JSONObject row = (JSONObject)rows.get(0);
			insertData.put(row);
		} else{
			rows = getFormDatas().getJSONArray("DeclData");
			JSONObject row = (JSONObject)rows.get(0);
			updateDate.put(row);
		}

		if(insertData.length()>0){
			 InsertRt = getDataAccess().Insert("ITF_DCL_IO_DECL", insertData,"DECL_NO");
			   
		}else{
			 updateRt = getDataAccess().Update("ITF_DCL_IO_DECL", updateDate,"DECL_NO");
		}
		
		//String indx = String.valueOf(SaveToDB("DeclData", "ITF_DCL_IO_DECL", "DECL_NO"));
		if (InsertRt||updateRt) { 
            String DECL_NO=GetDataValue("DeclData", "DECL_NO");
            SaveToTable("DeclData", "DECL_NO", DECL_NO);		
            
            //添加的时候，先保存综合流水号，表头保存完成之后，再换成报检流水号
            getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_LIMIT set DECL_NO=? where DECL_NO = ?",new String[] { DECL_NO,GetDataValue("DeclData", "CON_INDX")});
            getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_ATT set DECL_NO=? where DECL_NO = ?",new String[] { DECL_NO,GetDataValue("DeclData", "CON_INDX")});
            getDataAccess().ExecSQL("update ITF_DCL_IO_DECL_USER set DECL_NO=? where DECL_NO = ?",new String[] { DECL_NO,GetDataValue("DeclData", "CON_INDX")});
            getDataAccess().ExecSQL("update ITF_DCL_MARK_LOB set DECL_NO=? where DECL_NO = ?",new String[] { DECL_NO,GetDataValue("DeclData", "CON_INDX")});
			String SqlTemp=String.format("select * from ITF_DCL_RECEIPTS WHERE DECL_NO = '%s' AND RSP_CODES = '0'",DECL_NO);
			List lst = SQLExecUtils.query4List(SqlTemp);
    		int size = lst.size();
    		if(size==0){
    			JSONObject tempDecl = new JSONObject();
    			JSONArray tempArrs = new JSONArray();
	
				tempDecl.put("DECL_NO",DECL_NO);
				
				/*if(SysUtility.isNotEmpty(DECL_NO))	{
					tempDecl.put("RSP_NO",GetNoFromGoods(DECL_NO));	
				}*/
				tempDecl.put("RSP_CODES","0");								
				tempDecl.put("RSP_INFO","本地暂存"); 
				tempDecl.put("RSP_GEN_TIME",SysUtility.getSysDate());								
				tempArrs.put(tempDecl); 
				int ResIndx =SaveToDB(tempArrs, "ITF_DCL_RECEIPTS");
				if (SysUtility.isEmpty(ResIndx))
		        {
					ReturnMessage(false, "保存失败！");
					return;
		        }
    		}
            
            
            
			ReturnMessage(true, "保存成功", "", TableToJSON("DeclData"));
		}else{
			ReturnMessage(false, "保存失败");	
		}
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
