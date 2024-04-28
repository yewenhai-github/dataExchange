package com.easy.app.task;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_MailSend extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_MailSend(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		SQLBuild SqlBuild = SQLBuild.getInstance();
		SqlBuild.append("SELECT MSG_TYPE,MAIL_TYPE,MAIL_CRON,CLASS_NAME from s_mail_event_config where is_enabled = '1'");
		List lst = SqlBuild.query4List();
		for (int i = 0; i < lst.size(); i++) {
			HashMap map = (HashMap)lst.get(i);
			String msgType = (String)map.get("MSG_TYPE");
			String mailType = (String)map.get("MAIL_TYPE");
			String mailCron = (String)map.get("MAIL_CRON");
			String className = (String)map.get("CLASS_NAME");
			
			if(SysUtility.isNotEmpty(className)){//
				ExsUtility.MethodInvoke(className, map);
			}else if(SysUtility.isNotEmpty(mailType)){
				if("1".equals(mailType)){
					//不校验
				}else if("2".equals(mailType)){
					if(!"06".equals(SysUtility.getSysDayWeek())){
						return;//每周五才执行
					}
				}else{
					return;//不支持
				}
				String currentDay = SysUtility.getSysDateWithoutTime();
				String[] mailCrons = mailCron.split("\\|");
				for (int j = 0; j < mailCrons.length; j++) {
					String[] tempMailCrons = mailCrons[j].split("\\,");
					String beginDay = tempMailCrons[0].substring(1);
					String endDay = tempMailCrons[1].substring(0,tempMailCrons[1].length() - 1);
					insertMailSenderJob(msgType,currentDay+" "+beginDay, currentDay+" "+endDay);
					List jobLst = getMailSenderJob(msgType);
					if(SysUtility.isNotEmpty(lst)){
						SendMailSenderJob(jobLst, msgType, getDataAccess());
					}
				}
			}
		}
		
		
	}
	
	private void insertMailSenderJob(String msgType,String beginTime,String endTime) throws LegendException, JSONException{
		String currentTime = SysUtility.getSysDate();
		if(currentTime.compareTo(beginTime) > 0 && currentTime.compareTo(endTime) < 0){
			String TableName = "rows";
			String SQL = "select * from s_mail_handle_sender where msg_type = ? and create_time > to_date(?,'yyyy-mm-dd hh24:mi:ss') and create_time < to_date(?,'yyyy-mm-dd hh24:mi:ss')";
			Datas datas = getDataAccess().GetTableDatas(TableName,SQL, new String[]{msgType,beginTime,endTime});
			if(datas.GetTableRows(TableName) <= 0){
				JSONObject row = new JSONObject();
				row.put("MSG_NO", SysUtility.GetUUID());
				row.put("MSG_TYPE", msgType);
				getDataAccess().Insert("s_mail_handle_sender", row);
				getDataAccess().ComitTrans();
//				LogUtil.printLog("邮件发送任务创建成功："+currentTime, Level.WARN);
			}
		}
	}
	
	private List getMailSenderJob(String msgType){
		SQLBuild SqlBuild = SQLBuild.getInstance();
		SqlBuild.append("select e.indx EINDX,t.* ");
		SqlBuild.append("  from s_mail_handle_sender e,s_mail_event_config t ");
		SqlBuild.append(" where e.msg_type = t.msg_type");
		SqlBuild.append("   and e.msg_flag = '0'");
		SqlBuild.append("   and t.msg_type = ?",msgType);
		return SqlBuild.query4List();
	}
	
	
	private void SendMailSenderJob(List lst,String msgType,IDataAccess DataAccess) throws LegendException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException{
		if(SysUtility.isEmpty(lst)){
			return;
		}
		
		for (int i = 0; i < lst.size(); i++) {
			HashMap map = (HashMap)lst.get(i);
			String indx = (String)map.get("EINDX");
			try {
				String subject = (String)map.get("MAILSUBJECT");
				String mailFrom = (String)map.get("MAILFROM");
				String mailTO = (String)map.get("MAILTO");
				//邮件内容
				String MailContent = SendMailContent(msgType, map);
				//发送邮件
				if(SysUtility.isNotEmpty(MailContent)){
					SysUtility.SendMail(subject+"("+msgType+")"+"(监控服务器:"+SysUtility.getCurrentHostIPAddress()+")", mailFrom, mailTO, MailContent.toString());
				}
				//更新发送状态
				String UpdateSQL = "update s_mail_handle_sender set msg_flag = '1' where indx = ?";
				DataAccess.ExecSQL(UpdateSQL, new String[]{indx});
			} catch (LegendException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				String UpdateSQL = "update s_mail_handle_sender set msg_flag = '2' where indx = ?";
				DataAccess.ExecSQL(UpdateSQL, new String[]{indx});
			}finally{
				DataAccess.ComitTrans();
			}
		}
	 }
	
	private String SendMailContent(String msgType,HashMap map) throws LegendException{
		StringBuffer MailContent = new StringBuffer();
		
		String mailSqlCount = (String)map.get("MAIL_SQL_COUNT");
		String mailSqlDetail = (String)map.get("MAIL_SQL_DETAIL");
		if(SysUtility.isEmpty(mailSqlCount) && SysUtility.isEmpty(mailSqlDetail)){
			return MailContent.toString();
		}
		
		if(SysUtility.isNotEmpty(mailSqlCount) && mailSqlCount.toUpperCase().indexOf("SELECT") < 0){
			MailContent.append(mailSqlCount);
		}
		if(SysUtility.isNotEmpty(mailSqlDetail) && mailSqlDetail.toUpperCase().indexOf("SELECT") < 0){
			MailContent.append(mailSqlDetail);
		}
		if(SysUtility.isNotEmpty(MailContent)){
			return MailContent.toString();
		}
		
		
		//拼接文件内容
		MailContent.append("<!DOCTYPE html><html><head></head><body>");
		List mailList = SQLExecUtils.query4List(mailSqlCount);
		if(SysUtility.isNotEmpty(mailList)){
			
		}
		
		MailContent.append("\n");
		
		List detailList = SQLExecUtils.query4List(mailSqlDetail);
		if(SysUtility.isNotEmpty(detailList)){
			MailContent.append("<table border=\"1\">");
			String[] columnNames = SysUtility.getTableColumns(SysUtility.getCurrentConnection(), mailSqlDetail);
			MailContent.append("<tr>");
			for (int j = 0; j < columnNames.length; j++) {
				MailContent.append("<td style=\"width:120px;\">");
				MailContent.append(columnNames[j]);
				MailContent.append("</td>");
			}
			MailContent.append("</tr>");
			for (int j = 0; j < detailList.size(); j++) {
				HashMap detailMap = (HashMap)detailList.get(j);
				MailContent.append("<tr>");
				for (int k = 0; k < columnNames.length; k++) {
					MailContent.append("<td style=\"width:120px;\">");
					MailContent.append(detailMap.get(columnNames[k]));
					MailContent.append("</td>");
				}
				MailContent.append("</tr>");
			}
			MailContent.append("</table>");
		}
		MailContent.append("</body></html>");
		if(detailList.size() <= 0){
			MailContent.delete(0, MailContent.length());
		}
		
		return MailContent.toString();
	}
	
}
