package com.easy.api.convert.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Level;

import com.easy.utility.LogUtil;


/**
 * FTP工具类
 * @author wangk
 *
 */

public class FtpUtil {
	
	private static String msg = "";
	private static boolean state = false;
	private static String[] FILETYPE= new String[]{"xml","zip"};
	
	public FtpUtil() {
		msg = "";
		state = false;
	}

	/**  
	 * Description: 向FTP服务器上传文件  
	 * @param host FTP服务器hostname  
	 * @param port FTP服务器端口  
	 * @param username FTP登录账号  
	 * @param password FTP登录密码  
	 * @param basePath FTP服务器基础目录 
	 * @param filePath FTP服务器文件存放路径。例如分日期存放：2017/01/01。文件的路径为basePath+filePath 
	 * @param filename 上传到FTP服务器上的文件名  
	 * @param input 输入流  
	 * @return 成功返回true，否则返回false  
	 * @throws IOException 
	 */ 
	public static boolean uploadFile(String host, int port, String username, String password, String filename, InputStream input) throws IOException {  
        boolean result = false;  
        FTPClient ftp = new FTPClient();  
        try {  
            int reply;  
            ftp.connect(host, port);// 连接FTP服务器  
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器  
            ftp.login(username, password);// 登录  
            reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                msg += "FTP用户名密码验证失败";
                LogUtil.printLog("Send FTP Error:"+msg, Level.ERROR);
                return result;  
            }  
            ftp.setControlEncoding("UTF-8");
            //切换到上传目录  
          /*  if (!ftp.changeWorkingDirectory(basePath+filePath)) {  
                //如果目录不存在创建目录  
                String[] dirs = filePath.split("\\\\");  
                String tempPath = basePath;  
                for (String dir : dirs) {  
                    if (null == dir || "".equals(dir)) continue;  
                    tempPath += "\\"+ dir;  
                    if (!ftp.changeWorkingDirectory(tempPath)) {  
                        if (!ftp.makeDirectory(tempPath)) {  
                        	 msg += "FTP目录错误";
                        	 LogUtil.printLog("Send FTP Error:"+msg, Level.ERROR);
                            return result;  
                        } else {  
                            ftp.changeWorkingDirectory(tempPath);  
                        }  
                    }  
                }  
            }  */
            //设置上传文件的类型为二进制类型  
            ftp.setFileType(FTP.BINARY_FILE_TYPE);  
            //上传文件  
            if (!ftp.storeFile(new String(filename.getBytes("GBK"),"iso-8859-1"),input)) {  
            	msg += "上传文件失败";
            	LogUtil.printLog("Send FTP Error:"+msg, Level.ERROR);
                return result;  
            }  
            input.close();  
            ftp.logout();  
            msg+="上传成功";
            state = true;
            result = true;  
        } catch (IOException e) {  
        	
        	LogUtil.printLog("Send FTP Error:"+e.getMessage(), Level.ERROR);
        	msg+=e.getMessage();
            state = false;
        } finally { 
        	if(input!=null) {
        		input.close();
        	}
            if (ftp.isConnected()) {  
                try {  
                    ftp.disconnect();  
                } catch (IOException ioe) {  
                	msg+=ioe.getMessage();
                	LogUtil.printLog("Send FTP Error:"+ioe.getMessage(), Level.ERROR);
                }  
            }  
        }  
        return result;  
    }  
	
	
	 /**  
     * Description: 从FTP服务器下载文件  
     * @param host FTP服务器hostname  
     * @param port FTP服务器端口  
     * @param username FTP登录账号  
     * @param password FTP登录密码  
     * @param remotePath FTP服务器上的相对路径  
     * @param fileName 要下载的文件名  
     * @param localPath 下载后保存到本地的路径  
     * @return  
     */    
    public static boolean downloadFile(String host, int port, String username, String password, String remotePath,  
            String fileName, String localPath) {  
        boolean result = false;  
        FTPClient ftp = new FTPClient();  
        try {  
            int reply;  
            ftp.connect(host, port);  
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器  
            ftp.login(username, password);// 登录  
            reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                return result;  
            }  
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录  
            FTPFile[] fs = ftp.listFiles();  
            for (FTPFile ff : fs) {  
                if (ff.getName().equals(fileName)) {  
                    File localFile = new File(localPath + "/" + ff.getName());  
  
                    OutputStream is = new FileOutputStream(localFile);  
                    ftp.retrieveFile(ff.getName(), is);  
                    is.close();  
                }  
            }  
  
            ftp.logout();  
            result = true;  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (ftp.isConnected()) {  
                try {  
                    ftp.disconnect();  
                } catch (IOException ioe) {  
                }  
            }  
        }  
        return result;  
    }

    /**  
     * Description: 从FTP服务器下载文件夹所有  
     * @param host FTP服务器hostname  
     * @param port FTP服务器端口  
     * @param username FTP登录账号  
     * @param password FTP登录密码  
     * @param remotePath FTP服务器上的相对路径  
     * @param fileName 要下载的文件名  
     * @param localPath 下载后保存到本地的路径  
     * @return  
     */    
    public static boolean downloadFileS(String host, int port, String username, String password, String remotePath,  
             String localPath) {  
        boolean result = false;  
        FTPClient ftp = new FTPClient();  
        try {  
            int reply;  
            ftp.connect(host, port);  
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器  
            ftp.login(username, password);// 登录  
            reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                msg += "FTP用户名密码验证失败";
                return result;  
            }  
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录  
            FTPFile[] fs = ftp.listFiles();  
            for (FTPFile ff : fs) {  
            	String[] ValueSplit = ff.getName().split("\\.");
            	if(ValueSplit.length<1){
            		continue;
            	}
            	String value = ValueSplit[ValueSplit.length-1];
            	if(Arrays.asList(FILETYPE).contains(value)){
            		File file = new File(localPath);
            		 if (!file.exists()) { 
            			 file.mkdirs();
                     }
                    File localFile = new File(localPath + "/" + ff.getName());  
                    OutputStream is = new FileOutputStream(localFile);  
                    ftp.retrieveFile(ff.getName(), is);  
                    is.close();  
                    msg+=ff.getName()+"文件下载成功!\n";
//                    boolean deleteFile = ftp.deleteFile(ff.getName());
//                    if(deleteFile){
//                    	msg+=ff.getName()+"FTP删除成功!\n";
//                    }
            	}
            }  
  
            ftp.logout();  
            result = true;  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (ftp.isConnected()) {  
                try {  
                    ftp.disconnect();  
                } catch (IOException ioe) {  
                }  
            }  
        }  
        return result;  
    }
    
	public static String getMsg() {
		return msg;
	}

	public static boolean isState() {
		return state;
	}

	public static String[] getFILETYPE() {
		return FILETYPE;
	}

	public static void setFILETYPE(String[] fILETYPE) {
		FILETYPE = fILETYPE;
	}
	
	

    
}
