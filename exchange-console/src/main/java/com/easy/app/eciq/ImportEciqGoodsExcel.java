package com.easy.app.eciq;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;



@WebServlet("/forms/ImportEciqGoodsExcel")
public class ImportEciqGoodsExcel  extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

    String DECL_NO;
    String IN_DECL_NO;
    String OUT;
    public static String errLength="";
	public void DoCommand() throws Exception{
        DECL_NO = (String) getEnvDatas().get("DECL_NO");
        IN_DECL_NO = (String) getEnvDatas().get("IN_DECL_NO");
        OUT = (String) getEnvDatas().get("OUT");
		doGet1(getRequest(), getResponse());
	}
	public void doGet1(HttpServletRequest Request, HttpServletResponse Response)
            throws ServletException, IOException {
                //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
                String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
                File file = new File(savePath);
                //判断上传文件的保存目录是否存在
                if (!file.exists() && !file.isDirectory()) {
                    System.out.println(savePath+"目录不存在，需要创建");
                    //创建目录
                    file.mkdir();
                }
                //消息提示
                String message = "";
                try{
                    //使用Apache文件上传组件处理文件上传步骤：
                    //1、创建一个DiskFileItemFactory工厂
                    DiskFileItemFactory factory = new DiskFileItemFactory();
                    //2、创建一个文件上传解析器
                    ServletFileUpload upload = new ServletFileUpload(factory);
                     //解决上传文件名的中文乱码
                    upload.setHeaderEncoding("UTF-8"); 
                    //3、判断提交上来的数据是否是上传表单的数据
                    if(!ServletFileUpload.isMultipartContent(getRequest())){
                        //按照传统方式获取数据
                        return;
                    }
                    //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
                    List<FileItem> list = upload.parseRequest(getRequest());
                    for(FileItem item : list){
                        //如果fileitem中封装的是普通输入项的数据

                            String filename = item.getName();
                            //获取item中的上传文件的输入流
                            InputStream in = item.getInputStream();
                            //创建一个文件输出流
                            FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
                           // ByteArrayOutputStream out = new ByteArrayOutputStream();
                            //创建一个缓冲区
                            byte buffer[] = new byte[1024];
                            //判断输入流中的数据是否已经读完的标识
                            int len = 0;
                            //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                            while((len=in.read(buffer))>0){
                                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                                out.write(buffer, 0, len);
                            }
                            in.close();
                            //关闭输出流
                            out.close();
                            
                            getDataAccess().BeginTrans();
                            String err=getAllByExcel(savePath + "\\" + filename);
                            item.delete();
                            if(!SysUtility.isEmpty(err)){
                            	getDataAccess().RoolbackTrans();
                                ReturnMessage(false, "导入失败!" + err);
                            }else{
                            	getDataAccess().ComitTrans();
                                ReturnMessage(true, "导入成功!");
                            }
                    }
                }catch (Exception e) {
                    message= "文件上传失败！";
                    e.printStackTrace();
                    
                }
    }
    /**
     * 查询指定目录中电子表格中所有的数据
     * @param file 文件完整路径
     * @return
     */
	   public String getAllByExcel(String file){
       		String err="";
	        try {
	            Workbook rwb=Workbook.getWorkbook(new File(file));
	            Sheet rs=rwb.getSheet(0);
	            int clos=rs.getColumns();//得到所有的列
	            int rows=rs.getRows();//得到所有的行
	            JSONObject json = new JSONObject();
	            JSONArray arrs = new JSONArray();
	            int excelColIndx=-1;
	            
	            //判断是否是空模板
	            if(rows==1){
	            	err="空模板不能导入，请填写内容后重新导入！";
	            }
	            
	            JSONArray goodsTableArray=new JSONArray();
	            Datas goodsTabledt = getDataAccess().GetTableDatas("GOODSTABLE", "SELECT b.column_name column_name  ,b.data_type data_type ,b.data_length FROM all_tab_columns b  WHERE  b.table_name  = 'ITF_DCL_IO_DECL_GOODS' and b.OWNER='CEDS'");
            	if (goodsTabledt.GetTableRows("GOODSTABLE") > 0)
        	    {
            		goodsTableArray= new JSONArray(goodsTabledt.GetTables("GOODSTABLE").toString());
        	    }
	            
	            
	            for (int i = 1; i <rows; i++) {
	            	json = new JSONObject();
	            	arrs = new JSONArray();
	                for (int j = 0; j < 1; j++) {

	                	 //判断表中是否存在空行
	                    int rowdataisnullcount = 0;
	                    for (int r = 0; r < 31; r++)
	                    {
	                        if (SysUtility.isEmpty(rs.getCell(r++, i).getContents()))
	                        {
	                            rowdataisnullcount++;
	                        }
	                    }

	                    if (rowdataisnullcount < 31)
	                    {
	                        String HsCode="";
	                        String Hstype="";
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("HS编码",rs), i).getContents()))
	                        {
	                        	HsCode=rs.getCell(isFlagExcelCol("HS编码",rs), i).getContents();
	                        	if(!checkLength(HsCode,"PROD_HS_CODE",goodsTableArray)){
	                        		err += "商品HS编码超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PROD_HS_CODE",rs.getCell(isFlagExcelCol("HS编码",rs), i).getContents());
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT MEASURE_TYPE_CODE FROM S_ECIQ_HSCODE WHERE ITEM_CODE=?", HsCode);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		Hstype=dt.GetTableValue("ITEMTABLE", "MEASURE_TYPE_CODE");
	                    	    }
	                        }
	                        else
	                        {
	                            err += "商品HS编码不能为空！";
	                            return err;
	                        }
	                        err = "HS编码：" + HsCode;
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("监管条件",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("监管条件",rs), i).getContents(),"MONITOR_CONDITION",goodsTableArray)){
	                        		err += "监管条件超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("MONITOR_CONDITION",rs.getCell(isFlagExcelCol("监管条件",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物中文名称",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物中文名称",rs), i).getContents(),"DECL_GOODS_CNAME",goodsTableArray)){
	                        		err += "货物中文名称超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("DECL_GOODS_CNAME",rs.getCell(isFlagExcelCol("货物中文名称",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("英文名称",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("英文名称",rs), i).getContents(),"DECL_GOODS_ENAME",goodsTableArray)){
	                        		err += "英文名称超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("DECL_GOODS_ENAME",rs.getCell(isFlagExcelCol("英文名称",rs), i).getContents());
	                        }
	                        String CiqNo="";
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("CIQ编码",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("CIQ编码",rs), i).getContents(),"CIQ_CODE",goodsTableArray)){
	                        		err += "CIQ编码超过长度"+errLength;
		                            return err;
	                        	}
	                        	CiqNo=rs.getCell(isFlagExcelCol("CIQ编码",rs), i).getContents();
	                        	json.put("CIQ_CODE",rs.getCell(isFlagExcelCol("CIQ编码",rs), i).getContents());
	                        }
	                        else
	                        {
	                            err += "CIQ编码不能为空！";
	                            return err;
	                        }
	                        //判断ciq编码和hs编码是否相符
	                        if(!CiqNo.substring(0,10).equals(HsCode)){
	                            err += "和ciq编号:"+CiqNo+"不符，不允许申报！";
	                            return err;
							}
	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("原产国/地区",rs), i).getContents()))
	                        {
	                        	String ORI_CTRY_CODE=rs.getCell(isFlagExcelCol("原产国/地区",rs), i).getContents();
	                        	if(!checkLength(ORI_CTRY_CODE,"ORI_CTRY_NAME",goodsTableArray)){
	                        		err += "原产国/地区超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_COUNTRYCODE WHERE ITEM_CODE=?", ORI_CTRY_CODE);
	                        	json.put("ORI_CTRY_CODE",ORI_CTRY_CODE);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		json.put("ORI_CTRY_NAME",dt.GetTableValue("ITEMTABLE", "ITEM_NAME"));
	                    	    }
	                            else
	                            {
	                                err += "原产国/地区不正确！";
		                            return err;
	                            }
	                        }else
	                        {
	                            err += "原产国/地区不能为空！";
	                            return err;
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("原产地区",rs), i).getContents()))
	                        {
	                        	String ORIG_PLACE_CODE=rs.getCell(isFlagExcelCol("原产地区",rs), i).getContents();
	                        	if(!checkLength(ORIG_PLACE_CODE,"ORIG_PLACE_NAME",goodsTableArray)){
	                        		err += "原产地区超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_ORG_AREA WHERE ITEM_CODE=?", ORIG_PLACE_CODE);
	                        	json.put("ORIG_PLACE_CODE",ORIG_PLACE_CODE);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		json.put("ORIG_PLACE_NAME",dt.GetTableValue("ITEMTABLE", "ITEM_NAME"));
	                    	    }
	                            else
	                            {
	                                err += "原产地区不正确！";
		                            return err;
	                            }
	                        }

	                        if(Hstype.equals("2")){
	                        	if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("重量",rs), i).getContents()))
		                        {
	                        		err += "重量不能为空！";
		                            return err;
		                        }
	                        	if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("重量单位",rs), i).getContents()))
		                        {
	                        		err += "重量单位不能为空！";
		                            return err;
		                        }
	                        }else{
	                        	if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("数量",rs), i).getContents()))
		                        {
	                        		err += "数量不能为空！";
		                            return err;
		                        }
	                        	if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("数量单位",rs), i).getContents()))
		                        {
	                        		err += "数量单位不能为空！";
		                            return err;
		                        }
	                        }
	                        
	                        String QTY_MEAS_UNIT_NAME="";
	                        String WT_MEAS_UNIT_NAME="";
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("数量",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("数量",rs), i).getContents(),"QTY",goodsTableArray)){
	                        		err += "数量超过长度"+errLength;
		                            return err;
	                        	}	
	                        	json.put("QTY",rs.getCell(isFlagExcelCol("数量",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("数量单位",rs), i).getContents()))
	                        {
	                        	String QTY_MEAS_UNIT=rs.getCell(isFlagExcelCol("数量单位",rs), i).getContents();
	                        	if(!checkLength(QTY_MEAS_UNIT,"QTY_MEAS_UNIT",goodsTableArray)){
	                        		err += "数量单位超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_QTY_UNIT WHERE ITEM_CODE=?", QTY_MEAS_UNIT);
	                        	json.put("QTY_MEAS_UNIT",QTY_MEAS_UNIT);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		QTY_MEAS_UNIT_NAME=dt.GetTableValue("ITEMTABLE", "ITEM_NAME");
	                        		json.put("QTY_MEAS_UNIT_NAME",QTY_MEAS_UNIT_NAME);
	                    	    }
	                            else
	                            {
	                                err += "数量单位不正确！";
		                            return err;
	                            }
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("重量",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("重量",rs), i).getContents(),"WEIGHT",goodsTableArray)){
	                        		err += "重量超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("WEIGHT",rs.getCell(isFlagExcelCol("重量",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("重量单位",rs), i).getContents()))
	                        {
	                        	String WT_MEAS_UNIT=rs.getCell(isFlagExcelCol("重量单位",rs), i).getContents();
	                        	if(!checkLength(WT_MEAS_UNIT,"WT_MEAS_UNIT",goodsTableArray)){
	                        		err += "重量单位超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_WEIGHT_UNIT WHERE ITEM_CODE=?", WT_MEAS_UNIT);
	                        	json.put("WT_MEAS_UNIT",WT_MEAS_UNIT);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		WT_MEAS_UNIT_NAME=dt.GetTableValue("ITEMTABLE", "ITEM_NAME");
	                        		json.put("WT_MEAS_UNIT_NAME",WT_MEAS_UNIT_NAME);
	                    	    }
	                            else
	                            {
	                                err += "重量单位不正确！";
		                            return err;
	                            }
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物单价",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物单价",rs), i).getContents(),"PRICE_PER_UNIT",goodsTableArray)){
	                        		err += "货物单价超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PRICE_PER_UNIT",rs.getCell(isFlagExcelCol("货物单价",rs), i).getContents());
	                        }
	                        /*else
	                        {
	                            err += "货物单价不能为空！";
	                            return err;
	                        }*/
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物总值",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物总值",rs), i).getContents(),"GOODS_TOTAL_VAL",goodsTableArray)){
	                        		err += "货物总值超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("GOODS_TOTAL_VAL",rs.getCell(isFlagExcelCol("货物总值",rs), i).getContents());
	                        }
	                        else
	                        {
	                            err += "货物总值不能为空！";
	                            return err;
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货币单位",rs), i).getContents()))
	                        {
	                        	String CURRENCN=rs.getCell(isFlagExcelCol("货币单位",rs), i).getContents();
	                        	if(!checkLength(CURRENCN,"CURRENCN",goodsTableArray)){
	                        		err += "货币单位超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_CURRENCY WHERE ITEM_CODE=?", CURRENCN);
	                        	json.put("CURRENCN",CURRENCN);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		json.put("CURRENCY",dt.GetTableValue("ITEMTABLE", "ITEM_NAME"));
	                    	    }
	                            else
	                            {
	                                err += "货币单位不正确！";
		                            return err;
	                            }
	                        }
	                        else
	                        {
	                            err += "货币单位不能为空！";
	                            return err;
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("用途",rs), i).getContents()))
	                        {
	                        	String PURPOSE=rs.getCell(isFlagExcelCol("用途",rs), i).getContents();
	                        	if(!checkLength(PURPOSE,"PURPOSE",goodsTableArray)){
	                        		err += "用途超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT ITEM_NAME FROM S_ECIQ_COMMODITYUSAGE WHERE ITEM_CODE=?", PURPOSE);
	                        	json.put("PURPOSE",PURPOSE);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		json.put("PURPOSE_NAME",dt.GetTableValue("ITEMTABLE", "ITEM_NAME"));
	                    	    }
	                            else
	                            {
	                                err += "用途不正确！";
		                            return err;
	                            }
	                        }
	                        else
	                        {
	                            err += "用途不能为空！";
	                            return err;
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("境外生产企业名称",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("境外生产企业名称",rs), i).getContents(),"ENG_MAN_ENT_CNM",goodsTableArray)){
	                        		err += "境外生产企业名称超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("ENG_MAN_ENT_CNM",rs.getCell(isFlagExcelCol("境外生产企业名称",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物规格",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物规格",rs), i).getContents(),"GOODS_SPEC",goodsTableArray)){
	                        		err += "货物规格超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("GOODS_SPEC",rs.getCell(isFlagExcelCol("货物规格",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物型号",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物型号",rs), i).getContents(),"GOODS_MODEL",goodsTableArray)){
	                        		err += "货物型号超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("GOODS_MODEL",rs.getCell(isFlagExcelCol("货物型号",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("货物品牌",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("货物品牌",rs), i).getContents(),"GOODS_BRAND",goodsTableArray)){
	                        		err += "货物品牌超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("GOODS_BRAND",rs.getCell(isFlagExcelCol("货物品牌",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("产品有效期",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("产品有效期",rs), i).getContents(),"PROD_VALID_DT",goodsTableArray)){
	                        		err += "产品有效期超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PROD_VALID_DT",rs.getCell(isFlagExcelCol("产品有效期",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("产品保质期",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("产品保质期",rs), i).getContents(),"PROD_QGP",goodsTableArray)){
	                        		err += "产品保质期超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PROD_QGP",rs.getCell(isFlagExcelCol("产品保质期",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("成份/原料",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("成份/原料",rs), i).getContents(),"STUFF",goodsTableArray)){
	                        		err += "成份/原料超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("STUFF",rs.getCell(isFlagExcelCol("成份/原料",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("生产日期",rs), i).getContents()))
	                        {
	                        	json.put("PRODUCE_DATE",rs.getCell(isFlagExcelCol("生产日期",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("生产批号",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("生产批号",rs), i).getContents(),"PROD_BATCH_NO",goodsTableArray)){
	                        		err += "生产批号超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PROD_BATCH_NO",rs.getCell(isFlagExcelCol("生产批号",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("非危险化学物品",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("非危险化学物品",rs), i).getContents(),"NO_DANG_FLAG",goodsTableArray)){
	                        		err += "非危险化学物品超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("NO_DANG_FLAG",rs.getCell(isFlagExcelCol("非危险化学物品",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("UN编码",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("UN编码",rs), i).getContents(),"UN_CODE",goodsTableArray)){
	                        		err += "UN编码超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("UN_CODE",rs.getCell(isFlagExcelCol("UN编码",rs), i).getContents());
	                        }	                        
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("危险货物名称",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("危险货物名称",rs), i).getContents(),"DANG_NAME",goodsTableArray)){
	                        		err += "危险货物名称超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("DANG_NAME",rs.getCell(isFlagExcelCol("危险货物名称",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("危包类别",rs), i).getContents()))
	                        {
	                        	String PACK_TYPE=rs.getCell(isFlagExcelCol("危包类别",rs), i).getContents();
	                        	if(!checkLength(PACK_TYPE,"PACK_TYPE",goodsTableArray)){
	                        		err += "危包类别超过长度"+errLength;
		                            return err;
	                        	}
	                    		Datas dt = getDataAccess().GetTableDatas("ITEMTABLE", "SELECT NAME FROM S_ECIQ_CLASSFY WHERE CODE=?", PACK_TYPE);
	                        	json.put("PACK_TYPE",PACK_TYPE);
	                        	if (dt.GetTableRows("ITEMTABLE") > 0)
	                    	    {
	                        		json.put("DANG_PACK_TYPE_NAME",dt.GetTableValue("ITEMTABLE", "NAME"));
	                    	    }
	                            else
	                            {
	                                err += "危包类别不正确！";
		                            return err;
	                            }
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("危包规格",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("危包规格",rs), i).getContents(),"PACK_SPEC",goodsTableArray)){
	                        		err += "危包规格超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("PACK_SPEC",rs.getCell(isFlagExcelCol("危包规格",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("备用一",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("备用一",rs), i).getContents(),"BY1",goodsTableArray)){
	                        		err += "备用一超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("BY1",rs.getCell(isFlagExcelCol("备用一",rs), i).getContents());
	                        }
	                        if (!SysUtility.isEmpty(rs.getCell(isFlagExcelCol("备用二",rs), i).getContents()))
	                        {
	                        	if(!checkLength(rs.getCell(isFlagExcelCol("备用二",rs), i).getContents(),"BY2",goodsTableArray)){
	                        		err += "备用二超过长度"+errLength;
		                            return err;
	                        	}
	                        	json.put("BY2",rs.getCell(isFlagExcelCol("备用二",rs), i).getContents());
	                        }

	                        json.put("DECL_NO",DECL_NO);
	                        json.put("GOODS_ID",SysUtility.GetUUID());
	                        json.put("GOODS_NO",GetNoFromGoods(DECL_NO));
	                        
	                        if(Hstype.equals("2")){
		                        json.put("STD_DISPLAY",rs.getCell(isFlagExcelCol("重量",rs), i).getContents());
		                        json.put("STD_MEASURE_CODE",rs.getCell(isFlagExcelCol("重量单位",rs), i).getContents());
		                        json.put("STD_MEASURE_CODE_NAME",WT_MEAS_UNIT_NAME);
		                        json.put("STD_WEIGHT",rs.getCell(isFlagExcelCol("重量",rs), i).getContents());
		                        json.put("STD_WEIGHT_UNIT_CODE",rs.getCell(isFlagExcelCol("重量单位",rs), i).getContents());
		                        json.put("STD_WEIGHT_UNIT_NAME",WT_MEAS_UNIT_NAME);
	                        }else{
		                        json.put("STD_DISPLAY",rs.getCell(isFlagExcelCol("数量",rs), i).getContents());
		                        json.put("STD_MEASURE_CODE",rs.getCell(isFlagExcelCol("数量单位",rs), i).getContents());
		                        json.put("STD_MEASURE_CODE_NAME",QTY_MEAS_UNIT_NAME);
		                        json.put("STD_QTY",rs.getCell(isFlagExcelCol("数量",rs), i).getContents());
		                        json.put("STD_QTY_UNIT_CODE",rs.getCell(isFlagExcelCol("数量单位",rs), i).getContents());
		                        json.put("STD_QTY_UNIT_NAME",QTY_MEAS_UNIT_NAME);
	                        }
		                    arrs.put(json);		                    

	                        //判断是否是出区导入，如果是验证导入商品是否存在入区申报单中
	                        boolean ISinsert = true;
	                        if(!SysUtility.isEmpty(OUT)){
		                        if(!SysUtility.isEmpty(IN_DECL_NO)){
		                        	Object UpdateParams = new Object[]{IN_DECL_NO,HsCode,rs.getCell(isFlagExcelCol("货物中文名称",rs), i).getContents()};
			                        Datas dt = getDataAccess().GetTableDatas("GoodsList", "SELECT COUNT(*) COUNT FROM ITF_DCL_IO_DECL_GOODS WHERE decl_no=? AND prod_hs_code=? AND decl_goods_cname=?",UpdateParams);			
			            	        int counts = Integer.parseInt(dt.GetTableValue("GoodsList", "COUNT"));

			                        if (counts == 0)
			                        {
			                        	ISinsert = false;
		                                err = "入区申报单:" + IN_DECL_NO + "下不存在商品："+HsCode+","+rs.getCell(isFlagExcelCol("货物中文名称",rs), i).getContents()+"！";
		                                return err;
			                        }
		                        }else{
	                                ISinsert = false;
		                        	err += "请先保存入区申报单号！";
		                            return err;
		                        }
	                        }

	                        if (ISinsert)
	                        {
	                        	ISinsert=getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", arrs,"GOODS_ID");
	                            if (!ISinsert)
	                            {
	                                err = "ITF_DCL_IO_DECL_GOODS插入失败！";
	                                return err;
	                            }else{
	                            	err="";
	                            }
	                        }
	                    }  
	                }
	            }
	        } catch (Exception e) {
	        	err="模板不正确！";
	            e.printStackTrace();
	        } 
            return err;
	    }

		//设置商品序号
		private String GetNoFromGoods(String indx) throws LegendException
		{
			Datas dt = getDataAccess().GetTableDatas("GoodsList", "SELECT max(GOODS_NO) GOODS_NO FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO = ?", indx);
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
	    public static int isFlagExcelCol(String colName,Sheet excelTable)
	    {
	    	int colIndx=-1;
	    	 int clos=excelTable.getColumns();//得到所有的列
	    	 for(int i=0;i<clos;i++)
	    	 {
	    		 if(excelTable.getCell(i, 0).getContents().equals(colName))
	    			 colIndx=i; 
	    	 }
	    	return colIndx;
	    }
	    /***
	     * 将Execl中数值与数据库中相应字段进行比对，判读器长度，中文占2字节
	     * @param 值
	     * @param 列明
	     * @param 数据库列数组
	     * @return
	     */
	    public static boolean checkLength(String value,String colName,JSONArray array)
	    {
	    	if(array!=null){
	    		for(int i = 0;i<array.length();i++){
		    		try {
						if(array.getJSONObject(i).getString("COLUMN_NAME").equals(colName)){
							//只对VARCHAR2类型数据进行比对
							if(!array.getJSONObject(i).getString("DATA_TYPE").equals("VARCHAR2"))
								return true;
							int dataLength = array.getJSONObject(i).getInt("DATA_LENGTH");
							String regEx = "[\\u4e00-\\u9fa5]";
					        String term = value.replaceAll(regEx, "00");
							if(term.length()>dataLength){
								errLength=dataLength+"";
								return false;
							}else{
								return true;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}	
		    	}
	    	}
	    	
	    	return true;
	    }
	    


}
