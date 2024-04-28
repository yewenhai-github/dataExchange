package com.easy.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.easy.exception.LegendException;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public interface ResultSetHandler {
	Object handle(ResultSet rs) throws SQLException, LegendException;
}
