package com.easy.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Level;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
public class ZipUtil {
	private static final int COMPRESSBUFFERSIZE = 1024;

	/**
	 * Compression level for best compression.
	 */
	private static final int BEST_COMPRESSION = Deflater.BEST_COMPRESSION;

	private static void closeOutputStream(OutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 解压输入流
	 * @param inputData
	 * @return
	 * @throws IOException
	 */
	public static byte[] dataDecompress(InputStream inputData) throws IOException {
		int count = 0;
		byte[] b = new byte[2048];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			while ((count = inputData.read(b, 0, 2048)) != -1) {
				baos.write(b, 0, count);
			}

			return dataDecompress(baos.toByteArray());
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			closeOutputStream(baos);
			// closeInputStream(inputData);
		}
	}

	/**
	 * 解压输入字节
	 * @param inputData
	 * @return
	 * @throws IOException
	 */
	public static byte[] dataDecompress(byte[] inputData) throws IOException {
		ByteArrayOutputStream bouts = new ByteArrayOutputStream();
		byte[] bout = new byte[COMPRESSBUFFERSIZE];
		Inflater decompresser = new Inflater();
		decompresser.setInput(inputData, 0, inputData.length);
		int length = 0;

		try {
			while ((length = decompresser.inflate(bout)) > 0) {
				bouts.write(bout, 0, length);
			}

			decompresser.end();

			return bouts.toByteArray();
		} catch (java.util.zip.DataFormatException dfe) {
			throw new IOException(dfe.getMessage());
		} finally {
			closeOutputStream(bouts);
		}
	}

	/**
	 * 压缩输出字节
	 * @param outputData
	 * @return
	 * @throws IOException
	 */
	public static byte[] dataCompress(byte[] outputData) throws IOException {
		byte[] temp = dataCompress(outputData, BEST_COMPRESSION); 
		return temp;
	}

	private static byte[] dataCompress(byte[] outputData, int level) throws IOException {
		ByteArrayOutputStream bouts = new ByteArrayOutputStream();
		byte[] bout = new byte[COMPRESSBUFFERSIZE];
		int length = 0;
		Deflater compresser = new Deflater(level);
		compresser.setInput(outputData);
		compresser.finish();

		try {
			while ((length = compresser.deflate(bout)) > 0) {
				bouts.write(bout, 0, length);
			}

			return bouts.toByteArray();
		} finally {
			closeOutputStream(bouts);
		}
	}
	
	/**
	 * 压缩多份文件到 zip文件中
	 * @param zipFile zip文件路径
	 * @param errFile 文件
	 * @throws IOException
	 */
	public static void compressToZip(String zipFile, File errFile)
			throws IOException {
		File[] errFiles = errFile.listFiles();
		ZipOutputStream zipOut = new ZipOutputStream(
				new FileOutputStream(zipFile));
		try {
			for (int i = 0; i < errFiles.length; i++) {
				if (errFiles[i].isFile()) {
					zipOut.putNextEntry(new ZipEntry(
							errFiles[i].getName()));
					FileInputStream fis = new FileInputStream(errFiles[i]);
					byte[] content = new byte[fis.available()];
					fis.read(content);
					zipOut.write(content);
					fis.close();
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			zipOut.close();
		}
	}
	
	
	public static String unZip(String str) {
		return unZip(str, "UTF-8");
	}
	
	public static String zip(String str) {
		return zip(str,"UTF-8");
	}
	
	/**
	 * 解压byte数组类型的Zip
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String unZip(String str,String endcoding) {
		if(SysUtility.isEmpty(str)){
			return "";
		}
		
		
		String rt = "";
		
		byte[] b = null;
		ByteArrayInputStream bis = null;
		ZipInputStream zip = null;
		try {
			byte[] data = new sun.misc.BASE64Decoder().decodeBuffer(str);
			bis = new ByteArrayInputStream(data);
			zip = new ZipInputStream(bis);
			while (zip.getNextEntry() != null) {
				byte[] buf = new byte[8096];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = zip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
			}
			
			if(SysUtility.isNotEmpty(b)){
				rt = new String(b,endcoding);
			}else{
				rt = str;
			}
		} catch (Exception e) {
			rt = str;
			LogUtil.printLog("unZip Error:"+e.getMessage(), Level.ERROR);
		} finally {
			try {
				if (zip != null) {
					zip.close();
				}
			} catch (IOException e) {
				LogUtil.printLog("unZip Error:"+e.getMessage(), Level.ERROR);
			}
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				LogUtil.printLog("unZip Error:"+e.getMessage(), Level.ERROR);
			}
		}
		return rt;
	}
	
	/**
	 * 压缩byte数组类型的Zip
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String zip(String str,String endcoding) {
		if (SysUtility.isEmpty(str)){
			return "";
		}
			
		byte[] compressed = null;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		String zipStr = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes(endcoding));
			zout.closeEntry();
			compressed = out.toByteArray();
			zipStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);
		} catch (IOException e) {
			compressed = null;
			LogUtil.printLog("zip Error:"+e.getMessage(), Level.ERROR);
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					LogUtil.printLog("zip Error:"+e.getMessage(), Level.ERROR);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogUtil.printLog("zip Error:"+e.getMessage(), Level.ERROR);
				}
			}
		}
		return zipStr;
	}

}
