package com.easy.app.eciq;

import java.io.InputStream;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/UploadInspAtt")
public class UploadInspAtt extends MainServlet {
	private static final long serialVersionUID = 4403530510840414254L;
	public UploadInspAtt()
	{
		SetCheckLogin(false);
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {
		boolean rtok = false;
		String msg = "没有上传文件";
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List<FileItem> list = upload.parseRequest(this.getRequest());
		String uuid="";
		for (FileItem item : list) {
			if (!item.isFormField()) {
				// 得到上传的文件名称，
				 String filename = item.getName();
				 
				// 判断附件大小
				 if(getFileSizeMB(item.getSize())>Double.parseDouble("0.5")){
						ReturnMessage(false, "您上传的附件应不超过0.5M,请重新上传!");
						return;
					}
				// 获取上传的文件大小
				String fileSize = getFileSize(item.getSize());

				 if(filename==null || filename.trim().equals("")){
                     continue;
                 }
				 String filestypename=filename.substring(filename.lastIndexOf("\\")+1);
				 
				//获取文件后缀名str1.equalsIgnoreCase(str2); 
				 String prefix=filestypename.substring(filestypename.lastIndexOf(".")+1);
				 if(!("jpg".equalsIgnoreCase(prefix)||"png".equalsIgnoreCase(prefix)||"jpeg".equalsIgnoreCase(prefix)||"pdf".equalsIgnoreCase(prefix))){
					 ReturnMessage(false, "您上传的附件格式不对,请重新上传格式为jpg,png,jpeg,pdf!");
						return;
				 }
				 
				// this.SaveToTable("markData", "ATTACH_NAME", filename);
				// this.SaveToTable("markData", "ATTACH_TYPE", filestypename);
				 /*long size = item.getSize();
				 this.SaveToTable("EdocData", "EDOC_SIZE", String.valueOf(size));
				 this.SaveToTable("EdocData", "EDOC_FOMAT_TYPE", "US");*/
				 InputStream in = item.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream();
                 //创建一个缓冲区
                 byte buffer[] = new byte[1024];
                 //判断输入流中的数据是否已经读完的标识
                 int len = 0;
                 //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                 while((len=in.read(buffer))>0){
                     //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                     out.write(buffer, 0, len);
                 }
                 byte [] result = out.toByteArray();
                 String res = FileUtility.base64encoder.encode(result);
                 String indx = "";
				 JSONArray insertData = new JSONArray();
				 JSONArray updateDate = new JSONArray();
					boolean InsertRt =false;
					boolean updateRt =false;
					JSONArray rows = getFormDatas().getJSONArray("markData");
					for (int i = 0; i < rows.length(); i++) {
						JSONObject row = (JSONObject)rows.get(i);
						String tempId = SysUtility.getJsonField(row, "MARK_ID");		
						SysUtility.putJsonField(row, "ATTACHMENT",res);
						SysUtility.putJsonField(row, "ATTACH_TYPE","1");
						boolean flag=getDataAccess().ExecSQL("delete from ITF_DCL_MARK_LOB Where ATTACH_TYPE='1' and decl_no='"+row.getString("DECL_NO")+"'");
						Datas dtLob=getDataAccess().GetTableDatas("ITF_DCL_MARK_LOB", "Select * from ITF_DCL_MARK_LOB Where ATTACH_TYPE='1' and DECL_NO='" + row.getString("DECL_NO") + "'");
						//if(dtLob.GetTableRows("ITF_DCL_MARK_LOB")<=0)
						if(SysUtility.isEmpty(tempId))
						{	 
							uuid=SysUtility.GetUUID();
							//动态设置主键 
							SysUtility.putJsonField(row, "MARK_ID", uuid);
							SysUtility.putJsonField(row, "ATTACH_NAME",filename);
							//uuid=SysUtility.GetUUID();
							insertData.put(row);
						}else{
							uuid=dtLob.GetTableValue("ITF_DCL_MARK_LOB", "MARK_ID");
							updateDate.put(row);
						}
					}
					if(insertData.length()>0){
						 InsertRt = getDataAccess().Insert("ITF_DCL_MARK_LOB", insertData,"MARK_ID");
						   
					}else{
						 updateRt = getDataAccess().Update("ITF_DCL_MARK_LOB", updateDate,"MARK_ID");
					} 
				// String indx = String.valueOf(this.SaveToDB("markData", "ITF_DCL_MARK_LOB","MARK_ID"));
					
					 if(InsertRt||updateRt){
						 msg = "图片已上传";
						 rtok = true;
					 }
					 else{
						 msg = "上传图片时错误";
						 rtok = false;
					 }
					 
//				 if(!InsertRt||!updateRt){
//					 msg = "上传图片时错误";
//				 }
//				 else{
//					 msg = "图片已上传";
//					 rtok = true;
//				 }
			}
		}
		ReturnMessage(rtok,msg+"|"+uuid);
	}


	// 转换文件大小为B、KB、MB、GB
	public static String getFileSize(long size) {
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
		} else {
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
		}
	}
	
	public static double getFileSizeMB(double size) {
	
		return size/1000/1000;
	}
}
