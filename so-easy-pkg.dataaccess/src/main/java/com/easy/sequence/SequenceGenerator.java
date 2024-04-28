package com.easy.sequence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
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
public class SequenceGenerator {
	private static final int LOOP_PER_DAY = 1;

	private static final int LOOP_PER_MONTH = 2;

	private static final int LOOP_PER_YEAR = 3;

	public static synchronized String getSequence(String seqName, String partId)throws GenerateSequenceException {
		return getSequence(seqName, partId, null);
	}
	
	public static synchronized String getSequence(String seqName, String[] pattern)throws GenerateSequenceException {
		return getSequence(seqName, null, pattern);
	}
	
	private static synchronized String getSequence(String seqName,String partId, String[] pattern)throws GenerateSequenceException {
		try {
			if(SysUtility.isEmpty(partId)){
				partId = SysUtility.getCurrentPartId();
			}
			SeqGenerator table = SeqGeneratorManager.getSequence(seqName, partId);

			if (SysUtility.isNotEmpty(table)) {
				int minSeqNo = getMinSequenceNo(table.getSequence_start());
				int maxSeqNo = getMaxSequenceNo(table.getSequence_length());
				int sequence = createSequence(seqName, partId, minSeqNo, maxSeqNo, table.getSequence_period());
				return replace(table.getSequence_pre(), table.getSequence_role(), formatSN(sequence, table.getSequence_length()), pattern);
			} else {
				LogUtil.printLog("未定义" + seqName + "编号生成器", LogUtil.ERROR);
				throw new GenerateSequenceException("未定义" + seqName + "编号生成器");
			}
		} catch (Exception e) {
			LogUtil.printLog("生成" + seqName + "编号发生错误", LogUtil.ERROR);
			throw new GenerateSequenceException("生成" + seqName + "编号发生错误:" + e.getMessage());
		}
	}
	
	private static String replace(String SequencePre,String sequenceRole,String sequence,String[] pattern) {
		String txTime = SysUtility.getSysDate();
		String currentUserName=SysUtility.getCurrentUserName();
		if (pattern != null) {
			for (int i = 0; i < pattern.length; i++) {
				if(SysUtility.isEmpty(pattern[i])){
					sequenceRole = sequenceRole.replaceAll("%" + (i + 1), pattern[i]);
				}else{
					//增加对转义字符的支持.如果pattern[i]为"$"的话，原来的语句会报异常
					sequenceRole = sequenceRole.replaceAll("%" + (i + 1), "\\"+pattern[i]);
				}
			}
		}
		// 要放在pattern后面进行替换
		sequenceRole = sequenceRole.replaceAll("%ORGID",SysUtility.getCurrentOrgId());
		sequenceRole = sequenceRole.replaceAll("%YYYY", txTime.substring(0, 4));
		sequenceRole = sequenceRole.replaceAll("%YY", txTime.substring(2, 4));
		sequenceRole = sequenceRole.replaceAll("%MM", txTime.substring(5, 7));
		sequenceRole = sequenceRole.replaceAll("%DD", txTime.substring(8, 10));
		sequenceRole = sequenceRole.replaceAll("%UU", currentUserName);
		sequenceRole = sequenceRole.replaceAll("%SN", sequence);
		if(SysUtility.isNotEmpty(SequencePre)){
			sequenceRole = SequencePre+sequenceRole;
		}
		return sequenceRole;
	}

	private static String formatSN(int sequence, String seqLength) throws Exception {
		int length = Integer.parseInt(seqLength);
		int maxNo = (int) Math.pow((double) 10, length);
		String temp = Integer.toString(maxNo) + Integer.toString(sequence);
		return temp.substring(temp.length() - length);
	}

	private static Date getTxDate(String period) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		switch (Integer.parseInt(period)) {
		case LOOP_PER_DAY:
			break;
		case LOOP_PER_MONTH:
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			break;
		case LOOP_PER_YEAR:
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			break;
		default:
			calendar.set(1900, 0, 1, 0, 0, 0);
		}
//		System.out.println(calendar.getTimeInMillis());
		return new Date(calendar.getTimeInMillis());
	}

	private static int getMinSequenceNo(String sequenceStart) throws Exception {
		return Integer.parseInt(sequenceStart);
	}

	private static int getMaxSequenceNo(String sequenceLength) throws Exception {
		int length = Integer.parseInt(sequenceLength);
		return (int) Math.pow((double) 10, length) - 1;
	}

	//构造Sequence
	private static int createSequence(String sequenceName, String partid,
				   int seqStart, int maxSequence, String period) throws Exception {
		Connection conn = SysUtility.CreateConnection();
		int seqno = 0;
		try {
			SysUtility.BeginTrans(conn);
			Date last_date = getTxDate(period);
			seqno = getCurrentSequence(conn, sequenceName, partid, last_date);
			int nextseqno = seqStart;

			if (seqno < 0) {
				seqno = seqStart;
				nextseqno = seqStart + 1;
				iniSequenceNo(conn, sequenceName, partid, last_date, nextseqno);
			} else if (seqno >= maxSequence) {
				nextseqno = seqStart;
				updateSequenceNo(conn, sequenceName, partid, last_date, nextseqno) ;
			} else {
				nextseqno = seqno + 1;
				updateSequenceNo(conn, sequenceName, partid, last_date, nextseqno);
			}
			SysUtility.ComitTrans(conn);
		} catch (Exception e) {
			LogUtil.printLog("获取sequence连接异常:"+e.getMessage(), Level.ERROR);
		} finally{
			SysUtility.closeActiveCN(conn);
		}
		return seqno;
	}

	private static int getCurrentSequence(Connection conn, final String sequenceName,
			final String partid, final Date lastdate) throws LegendException {
		StringBuffer sql = new StringBuffer(50);
		sql.append("select sequence_no");
		sql.append("  from s_seq_no");
		sql.append(" where sequence_name = ?");
		sql.append("   and sequence_part_id = ?");
		sql.append("   and sequence_last_date = ? for update");
		String rt = SQLExecUtils.query4String(conn, sql.toString(), new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, sequenceName);
				ps.setString(2, SysUtility.isEmpty(partid)?"0":partid);
				ps.setDate(3, lastdate);
			}
		});
		if(SysUtility.isEmpty(rt) || "".equals(rt)){
			return -1;
		}
		return Integer.valueOf(rt).intValue();
	}

	private static void iniSequenceNo(Connection conn, final String sequenceName,final String partid, 
									  final Date last_date, final int sequenceNo)throws LegendException {
		String SQL = "insert into s_seq_no(sequence_name,sequence_no,sequence_part_id, sequence_last_date) values(?, ?, ?, ?)";
		SQLExecUtils.executeUpdate(conn, SQL, new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, sequenceName);
				ps.setInt(2, sequenceNo);
				ps.setString(3, SysUtility.isEmpty(partid)?"0":partid);
				ps.setDate(4, last_date);
			}
		});
	}
	
	private static void updateSequenceNo(Connection conn, final String sequenceName,
			final String partid, final Date lastDate, final int sequenceNo) throws LegendException {
		StringBuffer sql = new StringBuffer(50);
		sql.append("update s_seq_no ");
		sql.append("   set sequence_no = ? ");
		sql.append(" where sequence_last_date = ? ");
		sql.append("   and sequence_name = ?");
		sql.append("   and sequence_part_id = ?");

		SQLExecUtils.executeUpdate(conn, sql.toString(), new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setInt(1, sequenceNo);
				ps.setDate(2, lastDate);
				ps.setString(3, sequenceName);
				ps.setString(4, SysUtility.isEmpty(partid)?"0":partid);
			}
		});
	}
}
