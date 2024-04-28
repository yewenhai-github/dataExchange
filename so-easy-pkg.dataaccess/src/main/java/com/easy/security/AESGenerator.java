package com.easy.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
/**
 * AES对称加密算法
 */
public class AESGenerator {
	private static byte[] aesencrypt(String content, String password) {
		try {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());//添加安全算法
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(128, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			LogUtil.printLog("AES加密出错,NoSuchAlgorithm："+e.getMessage(), Level.ERROR);
		} catch (NoSuchPaddingException e) {
			LogUtil.printLog("AES加密出错,NoSuchPadding："+e.getMessage(), Level.ERROR);
		} catch (InvalidKeyException e) {
			LogUtil.printLog("AES加密出错,InvalidKey："+e.getMessage(), Level.ERROR);
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("AES加密出错,UnsupportedEncoding："+e.getMessage(), Level.ERROR);
		} catch (IllegalBlockSizeException e) {
			LogUtil.printLog("AES加密出错,IllegalBlockSize："+e.getMessage(), Level.ERROR);
		} catch (BadPaddingException e) {
			LogUtil.printLog("AES加密出错,BadPadding："+e.getMessage(), Level.ERROR);
		} catch (Exception e) {
			LogUtil.printLog("AESGenerator.aesencrypt遇到未知错误："+e.getMessage(), Level.ERROR);
		}
		return new byte[0];
	}
	
	private static byte[] aesdecrypt(byte[] content, String password) {
		try {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());//添加安全算法
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(128, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			LogUtil.printLog("AES解密出错,NoSuchAlgorithm："+e.getMessage(), Level.ERROR);
		} catch (NoSuchPaddingException e) {
			LogUtil.printLog("AES解密出错,NoSuchPadding："+e.getMessage(), Level.ERROR);
		} catch (InvalidKeyException e) {
			LogUtil.printLog("AES解密出错,InvalidKey："+e.getMessage(), Level.ERROR);
		} catch (IllegalBlockSizeException e) {
			LogUtil.printLog("AES解密出错,IllegalBlockSize："+e.getMessage(), Level.ERROR);
		} catch (BadPaddingException e) {
			LogUtil.printLog("AES解密出错,BadPadding："+e.getMessage(), Level.ERROR);
		} 
		return new byte[0];
	}

	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				sb.append(('0' + hex).toUpperCase());
			}else{
				sb.append(hex.toUpperCase());
			}
		}
		return sb.toString();
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 加密算法
	 * @param plaintext 明文
	 * @param key 密钥
	 * @return
	 */
	public static String encrypt(String plaintext, String key) {
		byte[] encryptResult = aesencrypt(plaintext, key);
		String encryptResultStr = parseByte2HexStr(encryptResult);
		return encryptResultStr;
	}

	/**
	 * 解密算法
	 * @param ciphertext 密文
	 * @param key 密钥
	 * @return
	 */
	public static String decrypt(String ciphertext, String key) {
		byte[] decryptFrom = parseHexStr2Byte(ciphertext);
		byte[] decryptResult = aesdecrypt(decryptFrom, key);
		try {
			return new String(decryptResult,"utf-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("AESGenerator.decrypt Error！"+e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String str = "V6jKJ2wz3nFjEo0kyrkhJg==";
		System.out.println("前台AES加密：" + str);
		System.out.println("：" + SysUtility.base64encoder.encode(encrypt("sskj999         ", "bi_ecology_qwert").getBytes()));
	}
	
	
	
}
