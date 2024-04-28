package com.easy.exception;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class SQLExceptionProccesor {
	// singleton with a eager initialization.
	private static final SQLExceptionProccesor INSTANCE = new SQLExceptionProccesor();

	public static SQLExceptionProccesor getInstance() {
		return INSTANCE;
	}

	// Exception 2 ErrCode mapping
	private final HashMap errCode;

	private SQLExceptionProccesor() {
		errCode = new HashMap();
		// unique constraint violated
		errCode.put("ORA-00001", new Integer(ERR.DB_SAME_PK));
		errCode.put("ORA-00917", new Integer(ERR.DB_INVALID_QUERY));
		// insert null value to not allowed null field
		errCode.put("ORA-01400", new Integer(ERR.DB_FIELD_NOT_NULL));
		// inserted value too large for column
		errCode.put("ORA-01401", new Integer(ERR.DB_TOO_LARGE_VALUE));
		// inserted value too large to numer column
		errCode.put("ORA-01438", new Integer(ERR.DB_TOO_LARGE_VALUE));
		// sequence does not exist
		errCode.put("ORA-02289", new Integer(ERR.DB_SEQUENCE_ERROR));
		// foreign key violated
		errCode.put("ORA-02291", new Integer(ERR.DB_FOREIGN_KEY_CONSTRAINT));
		// sequence exceeds MAXVALUE and cannot be instantiated
		errCode.put("ORA-08004", new Integer(ERR.DB_SEQUENCE_ERROR));
		errCode.put("ORA-20167", new Integer(ERR.DB_CNT_OPERATE_RECORD_FEE_ERROR));
		errCode.put("ORA-20168", new Integer(ERR.DB_CNT_OPERATE_RECORD_NO_RULE_ERROR));
		errCode.put("ORA-20169", new Integer(ERR.DB_CNT_OPERATE_RECORD_NO_CUST));
		errCode.put("ORA-17002", new Integer(ERR.ORA_17002));
	}

	public int getSinoExceptionErrorCode(SQLException ex) {
		NumberFormat formatter = new DecimalFormat("00000");
		String cde = formatter.format(ex.getErrorCode());
		Integer cdeDefined = (Integer) errCode.get("ORA-" + cde);
		if (cdeDefined != null)
			return cdeDefined.intValue();
		else
			return ERR.DB_ERROR;
	}
}