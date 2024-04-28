package com.easy.sequence;

public class SequenceFactory {
	/**
	 * @param sequence名称
	 * */
	public static String getSequence(String sequenceName)throws GenerateSequenceException {
		return SequenceGenerator.getSequence(sequenceName, new String[]{});
	}

	/**
	 * @param sequence名称
	 * */
	public static String getSequence(String sequenceName,String part_id)throws GenerateSequenceException {
		return SequenceGenerator.getSequence(sequenceName, part_id);
	}
	
	/**
	 * @param sequence名称
	 * @param:pattern 表达式，如果想传固定的指，直接放到数组中即可，会自动优先拼接。
	 * */
	public static String getSequence(String sequenceName, String[] pattern)throws GenerateSequenceException {
		return SequenceGenerator.getSequence(sequenceName, pattern);
	}
	
}
