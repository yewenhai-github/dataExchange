package com.easy.app.eciq;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

import com.easy.sequence.SequenceFactory;
import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/UpLoadcheckFiles")
public class UpLoadcheckFiles extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		 doGet(getRequest(), getResponse());
	}
	public void doGet(HttpServletRequest Request, HttpServletResponse Response)
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
          if(!ServletFileUpload.isMultipartContent(Request)){
              //按照传统方式获取数据
              return;
          }
          //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
          List<FileItem> list = upload.parseRequest(Request);
          for(FileItem item : list){
              //如果fileitem中封装的是普通输入项的数据
              if(item.isFormField()){
                  String name = item.getFieldName();
                  //解决普通输入项的数据的中文乱码问题
                  String value = item.getString("UTF-8");
                  //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                  System.out.println(name + "=" + value);
              }else{//如果fileitem中封装的是上传文件
                  //得到上传的文件名称，
                  String filename = item.getName();
                  System.out.println(filename);
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
                // json.put("INDX", "1"); 
                 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                 String PIndx = Request.getParameter("Pindx");
                 String ATTACH_DESCRIBE=Request.getParameter("ATTACH_DESCRIBE");
                 ATTACH_DESCRIBE=URLDecoder.decode(ATTACH_DESCRIBE,"UTF-8");
                 String ATTACH_ACTION_NO = Request.getParameter("type");
                // String ANNEXSERIALNO = SequenceFactory.getSequence("ANNEX_SERIAL_NO",Utility.DefaultPartId);
                           
                 json.put("ATTACHMENT", res1);
                 json.put("ATTACH_DESCRIBE", ATTACH_DESCRIBE);
                 
                 json.put("ATTACH_ACTION_NO", ATTACH_ACTION_NO);
                 json.put("DECL_NO",PIndx);
                 
                 json.put("ATTACH_NAME",filename);
                 json.put("ATTACH_TYPE", filestypename.substring(filestypename.lastIndexOf(".")+1));
                 json.put("CREATOR",SysUtility.getCurrentUserIndx());
                 json.put("CREATE_TIME",df.format(new Date()));
                 json.put("MARK_ID", SysUtility.GetUUID());
                 
                 boolean Flag=getDataAccess().Insert("ITF_DCL_MARK_LOB", json,"MARK_ID");
                 if (Flag)
                 {
              	   	ReturnMessage(true, "上传成功!");
                 }else{
               	   	ReturnMessage(false, "上传失败!");
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
              }
          }
      }catch (Exception e) {
          message= "文件上传失败！";
          e.printStackTrace();
          
      }
}
}
