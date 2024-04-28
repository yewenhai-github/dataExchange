package com.easy.app.eciq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/ImportExcel")
public class ImportExcel extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		doGet1(getRequest(), getResponse());
	}

	public void doGet1(HttpServletRequest Request, HttpServletResponse Response) throws ServletException, IOException {
		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
		String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
		File file = new File(savePath);
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			file.mkdir();
		}
		// 消息提示
		String message = "";
		try {
			// 使用Apache文件上传组件处理文件上传步骤：
			// 1、创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2、创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			// 3、判断提交上来的数据是否是上传表单的数据
			if (!ServletFileUpload.isMultipartContent(getRequest())) {
				// 按照传统方式获取数据
				return;
			}
			// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			List<FileItem> list = upload.parseRequest(getRequest());
			for (FileItem item : list) {
				// 如果fileitem中封装的是普通输入项的数据

				String filename = item.getName();
				// 获取item中的上传文件的输入流
				InputStream in = item.getInputStream();
				// 创建一个文件输出流
				FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
				// ByteArrayOutputStream out = new ByteArrayOutputStream();
				// 创建一个缓冲区
				byte buffer[] = new byte[1024];
				// 判断输入流中的数据是否已经读完的标识
				int len = 0;
				// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
				while ((len = in.read(buffer)) > 0) {
					// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" +
					// filename)当中
					out.write(buffer, 0, len);
				}
				in.close();
				// 关闭输出流
				out.close();

				getDataAccess().BeginTrans();
				String err = getAllByExcel(savePath + "\\" + filename);
				item.delete();
				if (!SysUtility.isEmpty(err)) {
					getDataAccess().RoolbackTrans();
					ReturnMessage(false, "导入失败!" + err);
				} else {
					getDataAccess().ComitTrans();
					ReturnMessage(true, "导入成功!");
				}
			}
		} catch (Exception e) {
			message = "文件上传失败！";
			e.printStackTrace();

		}
	}

	/**
	 * 查询指定目录中电子表格中所有的数据
	 * 
	 * @param file
	 *            文件完整路径
	 * @return
	 */
	public String getAllByExcel(String file) {
		String err = "";
		try {
			Workbook rwb = Workbook.getWorkbook(new File(file));
			Sheet rs = rwb.getSheet(0);
			int clos = rs.getColumns();// 得到所有的列
			int rows = rs.getRows();// 得到所有的行
			JSONObject json = new JSONObject();
			JSONArray arrs = new JSONArray();
			int excelColIndx = -1;
			for (int i = 1; i < rows; i++) {
				json = new JSONObject();
				arrs = new JSONArray();
				for (int j = 0; j < 1; j++) {
					// 判断表中是否存在空行
					int rowdataisnullcount = 0;
					for (int r = 0; r < 2; r++) {
						if (SysUtility.isEmpty(rs.getCell(r++, i).getContents())) {
							rowdataisnullcount++;
						}
					}
					if (rowdataisnullcount < 2) {
						if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("规格", rs), i).getContents())) {
							err += "规格不能为空！";
							return err;
						}
						if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("型号", rs), i).getContents())) {
							err += "型号不能为空！";
							return err;
						}
						if (SysUtility.isEmpty(rs.getCell(isFlagExcelCol("数量", rs), i).getContents())) {
							err += "数量不能为空！";
							return err;
						}
						DecimalFormat df = new DecimalFormat("0.00");
						String GOODS_STANDARD = rs.getCell(isFlagExcelCol("规格", rs), i).getContents();
						String GOODS_MODLE = rs.getCell(isFlagExcelCol("型号", rs), i).getContents();
						Datas dt = getDataAccess().GetTableDatas("MaterialList",
								"Select * FROM S_ECIQ_MATERIALCODE where GOODS_STANDARD='" + GOODS_STANDARD
										+ "'and GOODS_MODLE='" + GOODS_MODLE + "'");
						Datas defaultvalGoods = getDataAccess().GetTableDatas("DefaultvalGoods",
								"Select * FROM T_DEFAULTVAL_GOODS");
						String PROD_HS_CODE=defaultvalGoods.GetTableValue("DefaultvalGoods", "PROD_HS_CODE");
						Datas dtHsCode = getDataAccess().GetTableDatas("SBBDHSCODE",
								"SELECT INSP_MONITOR_COND,ITEM_NAME from S_BBD_HSCODE where ITEM_CODE='" + PROD_HS_CODE+"'");
						String hsCode=dtHsCode.GetTableValue("SBBDHSCODE", "INSP_MONITOR_COND");
						String ITEM_NAME=dtHsCode.GetTableValue("SBBDHSCODE", "ITEM_NAME");
						String DECL_NO = getRequest().getParameter("DECL_NO");
						int number = Integer.parseInt(rs.getCell(isFlagExcelCol("数量", rs), i).getContents());
						double price = 0;
						if (!"".equals(dt.GetTableValue("MaterialList", "GOODS_UNIT_PRICE"))) {
							 price = Double.parseDouble(dt.GetTableValue("MaterialList", "GOODS_UNIT_PRICE"));
							 double goods_total_val = number * price;
						     json.put("GOODS_TOTAL_VAL", df.format(goods_total_val));
						}
						double weight = 0;
						if (!"".equals(dt.GetTableValue("MaterialList", "GOODS_PIECE_WEIGHT"))) {
							weight = Double.parseDouble(dt.GetTableValue("MaterialList", "GOODS_PIECE_WEIGHT"));
							double totalweight =  weight * number;
							json.put("WEIGHT", df.format(totalweight));
						}
						Datas dtCount = getDataAccess().GetTableDatas("GOODSCOUNT",
								"SELECT Count(GOODS_NO) + 1 as GOODS_NO FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO = '"
										+ DECL_NO + "'");
						
						int goodsCount = Integer.parseInt(dtCount.GetTableValue("GOODSCOUNT", "GOODS_NO"));
						json.put("PRICE_PER_UNIT", dt.GetTableValue("MaterialList", "GOODS_UNIT_PRICE"));
						json.put("DECL_GOODS_CNAME", dt.GetTableValue("MaterialList", "GOODS_CNAME"));
						json.put("GOODS_BRAND", dt.GetTableValue("MaterialList", "GOODS_BRAND"));
						json.put("GOODS_MODEL", dt.GetTableValue("MaterialList", "GOODS_MODLE"));
						json.put("GOODS_SPEC", dt.GetTableValue("MaterialList", "GOODS_STANDARD"));
						json.put("PROD_HS_CODE", defaultvalGoods.GetTableValue("DefaultvalGoods", "PROD_HS_CODE"));
						json.put("CIQ_CODE", defaultvalGoods.GetTableValue("DefaultvalGoods", "CIQ_CODE"));
						json.put("ORI_CTRY_CODE", defaultvalGoods.GetTableValue("DefaultvalGoods", "ORI_CTRY_CODE"));
						json.put("ORI_CTRY_NAME", defaultvalGoods.GetTableValue("DefaultvalGoods", "ORI_CTRY_NAME"));
						json.put("QTY_MEAS_UNIT_NAME",
								defaultvalGoods.GetTableValue("DefaultvalGoods", "QTY_MEAS_UNIT_NAME"));
						json.put("QTY_MEAS_UNIT", defaultvalGoods.GetTableValue("DefaultvalGoods", "QTY_MEAS_UNIT"));
						json.put("CURRENCY", defaultvalGoods.GetTableValue("DefaultvalGoods", "CURRENCY"));
						json.put("CURRENCN", defaultvalGoods.GetTableValue("DefaultvalGoods", "CURRENCN"));
						json.put("PURPOSE_NAME", defaultvalGoods.GetTableValue("DefaultvalGoods", "PURPOSE_NAME"));
						json.put("PURPOSE", defaultvalGoods.GetTableValue("DefaultvalGoods", "PURPOSE"));
						json.put("DECL_NO", DECL_NO);
						json.put("GOODS_NO", goodsCount);
						json.put("QTY",number);
						json.put("MONITOR_CONDITION",hsCode);
						json.put("STD_QTY_UNIT_CODE",defaultvalGoods.GetTableValue("DefaultvalGoods", "QTY_MEAS_UNIT"));
						json.put("STD_QTY_UNIT_NAME",defaultvalGoods.GetTableValue("DefaultvalGoods", "QTY_MEAS_UNIT_NAME"));
						json.put("HS_CODE_DESC",ITEM_NAME);
						arrs.put(json);
						boolean ISinsert = true;
						if (ISinsert) {
							ISinsert = getDataAccess().Insert("ITF_DCL_IO_DECL_GOODS", arrs, "GOODS_ID");
							if (!ISinsert) {
								err = "ITF_DCL_IO_DECL_GOODS插入失败！";
								return err;
							} else {
								err = "";
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "导入文件异常,";
		}
		return err;
	}

	public static int isFlagExcelCol(String colName, Sheet excelTable) {
		int colIndx = -1;
		int clos = excelTable.getColumns();// 得到所有的列
		for (int i = 0; i < clos; i++) {
			if (excelTable.getCell(i, 0).getContents().equals(colName))
				colIndx = i;
		}
		return colIndx;
	}

}
