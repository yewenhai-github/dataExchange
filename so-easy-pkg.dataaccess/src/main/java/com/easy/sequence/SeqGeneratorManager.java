package com.easy.sequence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
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
public class SeqGeneratorManager {
	private static final Map SEQ_GENERATOR = new HashMap();

	private static final String COMM_PART_ID = "0";

	/**
	 * @param  sequenceName Sequence名称
	 * @param  partid 机构编码 0为公共配置
	 * @return SeqGenerator
	 * @throws SQLException 数据库错误
	 */
	public static SeqGenerator getSequence(String seqName, String partid) throws LegendException {
		List eventsList = (List) SEQ_GENERATOR.get(seqName);
		if (SysUtility.isEmpty(eventsList)) {
			reload(seqName);
			eventsList = (List) SEQ_GENERATOR.get(seqName);
		}
		
		SeqGenerator temp = null;
		for (int i = 0; i < eventsList.size(); i++) {
			SeqGenerator table = (SeqGenerator) eventsList.get(i);
			if (COMM_PART_ID.equals(table.getSequence_part_id())) {
				temp = table;
			}
			if (table.getSequence_part_id().equals(partid)) {
				temp = table;
				break;
			}
		}

		return temp;
	}

	/**
	 * 从数据库中重新装入所有的Sequence定义
	 * @throws LegendException 
	 * 
	 */
	private synchronized static void reload(String seqName) throws LegendException {
		if (SysUtility.isEmpty(SEQ_GENERATOR.get(seqName))) {
			loadSeqByName(seqName);
		}
	}

	/**
	 * 从数据库中重新装入所有的Sequence定义
	 * @throws LegendException 
	 */
	public synchronized static void loadSeqByName(String seqName) throws LegendException {
		try {
			Connection conn = SysUtility.getCurrentConnection();
			String SQL = "select * from s_seq_generator where sequence_name = ?";
			List list = SQLExecUtils.query4List(conn, SQL, SQLExecUtils.parmsToSetter(seqName));
			List<SeqGenerator> seqList = new ArrayList<SeqGenerator>();
			for (int i = 0; i < list.size(); i++) {
				HashMap temp = (HashMap)list.get(i);
				SeqGenerator seq = new SeqGenerator();
				seq.setSequence_pre((String)temp.get("sequence_pre"));
				seq.setSequence_name((String)temp.get("sequence_name"));
				seq.setSequence_role((String)temp.get("sequence_role"));
				seq.setSequence_start((String)temp.get("sequence_start"));
				seq.setSequence_length((String)temp.get("sequence_length"));
				seq.setSequence_period((String)temp.get("sequence_period"));
				seq.setSequence_part_id((String)temp.get("sequence_part_id"));
				seq.setSequence_org_id((String)temp.get("sequence_org_id"));
				seqList.add(seq);
			}
			SEQ_GENERATOR.put(seqName, seqList);
		} catch (Exception e) {
			LogUtil.printLog("初始化编码生成器定义时发生错误", Level.ERROR);
		}
	}
}
