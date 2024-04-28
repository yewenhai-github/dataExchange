package com.easy.exception;

import java.sql.SQLException;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class LegendException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String CONFIRM_ERR_CODE = "CONFIRM_ERR_CODE";
	
	private int errCode;

	private String errMsg;

	private int level;

	private static final LegendException INSTANCE = new LegendException();

	public static LegendException getInstance() {
		return INSTANCE;
	}
	
	private LegendException(){}
	
	public int getLevel() {
		return level;
	}

	public static LegendException getLegendException(SQLException e) {
		LogUtil.printLog(e.getMessage(), LogUtil.WARN);
		int errCde = SQLExceptionProccesor.getInstance().getSinoExceptionErrorCode(e);
		if (errCde == ERR.DB_ERROR) {
			return getLegendException(ERR.GENERIC_ERR, new String[] {e.getMessage().replaceAll("\n", "")});
		} else {
			return getLegendException(errCde, new String[] {e.getMessage() });
		}
	}
	
	public static LegendException getLegendException(String message) {
		return new LegendException(ERR.UNKOWN_ERROR, message, ErrorLevel.NONE, null);
	}
	
	public static LegendException getLegendException(int errCode) {
		return getLegendException(errCode, null);
	}
	
	public static LegendException getLegendException(int errCode, String[] lstPattern) {
		return getLegendException(errCode, lstPattern, null);
	}

	public static LegendException getLegendException(int errCode, String[] lstPattern, Throwable cause) {
		String strErrorMessage = ErrorMessage.getInstance().getErrorMessageWithoutCheckLevel(errCode, lstPattern);
		if (strErrorMessage == null) {
			return new LegendException(ERR.FW_INVALID_ERRCODE,
					"ErrorCode.properties load error!", ErrorLevel.ERROR, null);
		}

		int errLevel = ErrorLevel.NONE;
		if (ErrorMessage.getInstance().checkLevel(strErrorMessage)) {
			errLevel = Integer.parseInt(strErrorMessage.substring(0, 1));
			strErrorMessage = strErrorMessage.substring(2);
		}
		if(lstPattern.length >= 1){
			strErrorMessage += ":"+lstPattern[0];
		}
		
		return new LegendException(errCode, strErrorMessage, errLevel, cause);
	}
	
	protected LegendException(int errCode, String errMessage, int errLevel, Throwable cause) {
		super(errMessage, cause);
		this.errCode = errCode;
		this.level = errLevel;
		this.errMsg = errMessage;
		log(cause);
	}

	public int getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	private void log(Throwable cause) {
		LogUtil.printLog(errMsg, ErrorLevel.parseLevle(level), cause);
	}

}