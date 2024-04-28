package com.easy.app.eciq;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONObject;

import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/SaveElecDecl")
public class SaveElecDecl extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		 doGet(getRequest(), getResponse());
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
      //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
      String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
      File file = new File(savePath);
      //判断上传文件的保存目录是否存在
      if (!file.exists() && !file.isDirectory()) {
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
          if(!ServletFileUpload.isMultipartContent(request)){
              //按照传统方式获取数据
              return;
          }
          //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
          List<FileItem> list = upload.parseRequest(request);
          for(FileItem item : list){
              //如果fileitem中封装的是普通输入项的数据
              if(item.isFormField()){
                  String name = item.getFieldName();
                  //解决普通输入项的数据的中文乱码问题
                  String value = item.getString("UTF-8");
                  //value = new String(value.getBytes("iso8859-1"),"UTF-8");
              }else{//如果fileitem中封装的是上传文件
                  //得到上传的文件名称，
                  String filename = item.getName();
                  if(filename==null || filename.trim().equals("")){
                      continue;
                  }
                  //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                  //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                  String filestypename=filename.substring(filename.lastIndexOf("\\")+1);
                  //获取item中的上传文件的输入流
                  InputStream in = item.getInputStream();
                  //创建一个文件输出流
                  //FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
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
                 String res1 = FileUtility.base64encoder.encode(result);
                 JSONObject json = new JSONObject();
                 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 String ENT_UUID = SysUtility.GetUUID();
                 String SRC_UUID = SysUtility.GetUUID();
                 String INPUT_TIME = df.format(new Date());
      			 String INPUT_USER = SysUtility.getCurrentUserName();
      			 String DECL_GET_NO = getRequest().getParameter("DECL_GET_NO");
      			 String ORG_NAME = getRequest().getParameter("ORG_NAME");
      			 String ORG_CODE = getRequest().getParameter("ORG_CODE");
      			 String DECL_PERSN_CERT_NO = getRequest().getParameter("DECL_PERSN_CERT_NO");
      			 String CONT_TEL = getRequest().getParameter("CONT_TEL");
      			 String PAPER_STYLE = getRequest().getParameter("PAPER_STYLE");
      			 String INPUT_ENT = getRequest().getParameter("INPUT_ENT");
      			 String REMARK = URLDecoder.decode((new String(getRequest().getParameter("REMARK").getBytes("ISO8859-1"), "UTF-8")), "UTF-8");
      			 //String REMARK = getRequest().getParameter("REMARK");
      			 
      			 String ATTACH_CODE = getRequest().getParameter("ATTACH_CODE");
      			 String CERT_NO = getRequest().getParameter("CERT_NO");
      			 String CERT_NAME = getRequest().getParameter("CERT_NAME");
      			 ORG_NAME = new String(ORG_NAME.getBytes("iso-8859-1"),"UTF-8");
      			 CERT_NAME = new String(CERT_NAME.getBytes("iso-8859-1"),"UTF-8");
                 json.put("ENT_UUID", ENT_UUID);
                 json.put("SRC_UUID", SRC_UUID);
                 json.put("INPUT_TIME", INPUT_TIME);
                 json.put("INPUT_USER", INPUT_USER);
                 json.put("INPUT_ENT", INPUT_ENT);
                 json.put("DECL_GET_NO", DECL_GET_NO);
                 json.put("ORG_NAME", ORG_NAME);
                 json.put("ORG_CODE", ORG_CODE);
                 json.put("DECL_PERSN_CERT_NO", DECL_PERSN_CERT_NO);
                 json.put("CONT_TEL", CONT_TEL);
                 json.put("PAPER_STYLE", PAPER_STYLE);
                 json.put("REMARK", REMARK);
                 json.put("ATTACH_CODE", ATTACH_CODE);
                 json.put("CERT_NO", CERT_NO);
                 json.put("CERT_NAME", CERT_NAME);
                 json.put("SRC_CONTENT", res1);
                 
                 boolean Flag=getDataAccess().Insert("ITF_SRC_DECL", json);
                 
         		 if(Flag){
         			ReturnMessage(true, "保存成功!");
         		 }else{
         			ReturnMessage(false, "保存失败，请重新保存!");
         		 }
                 
                 getDataAccess().BeginTrans();
                
                 // byte [] result = out.t
                 //res = FileUtility.base64encoder.encode(result);
                  //关闭输入流
                  in.close();
                  //关闭输出流
                  out.close();
                  //删除处理文件上传时生成的临时文件
                  item.delete();
                  message = "文件上传成功！";
                  
                  getDataAccess().ComitTrans();
              }
          }
      }catch (Exception e) {
          message= "文件上传失败！";
          e.printStackTrace();
      }
}
}
