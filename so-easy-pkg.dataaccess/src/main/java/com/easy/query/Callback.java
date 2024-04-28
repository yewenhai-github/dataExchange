package com.easy.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public interface Callback {
	public void doIn(PreparedStatement ps) throws SQLException;
}
