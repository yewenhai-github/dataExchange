package com.easy.mail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.easy.exception.ERR;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewh 2015-06-03
 * 
 * @version 7.0.0
 * 
 */
public class MailSender {
	private static String MAILADDRESSDELIMITER = ";";

	/**
	 * 发送电子邮件
	 * 
	 * @param subject
	 *            邮件标题
	 * @param mailFrom
	 *            发件人邮件地址
	 * @param mailTO
	 *            收件人邮件地址列表
	 * @param content
	 *            邮件内容
	 * @param body
	 *            发送邮件的附件列表
	 * @return 邮件发送结果
	 */
	public static void send(String subject, String mailFrom, String mailTO,
			String mailCC, String content, Object body)
			throws MessagingException, Exception {
		if (SysUtility.isEmpty(mailCC)) {
			mailCC = "";
		}
		// 如果收件人为空，则将抄送人直接 付给收件人
		mailTO = getStrSep(mailTO);
		mailCC = getStrSep(mailCC);
		if (SysUtility.isEmpty(mailTO)) {
			if (SysUtility.isNotEmpty(mailCC)) {
				mailTO = mailCC;
			} else {
				throw new Exception("收件人地址和抄送人地址同时为空，无法发送！");
			}
		}
		sendMail(subject, mailFrom, parseMailTo(mailTO), parseMailTo(mailCC),null, content, convertToArray(body));
	}

	/**
	 * 解析收件人地址列表，按照分隔符（用MAILADDRESSDELIMITER定义）将输入的收件人地址列表解析成数组
	 * 
	 * @param mailTO
	 *            收件人邮件地址列表
	 * @return String[] 返回收件人地址列表
	 */
	private static String[] parseMailTo(String mailTO) throws Exception {
		ArrayList lstAddr = new ArrayList();
		if (mailTO == null && "".equals(mailTO)) {
			throw new Exception("未定义接收人地址，无法发送");
		} else {
			StringTokenizer st = new StringTokenizer(mailTO,
					MAILADDRESSDELIMITER);
			while (st.hasMoreTokens()) {
				lstAddr.add(st.nextToken());
			}
		}
		String[] mailAddrs = new String[lstAddr.size()];
		return (String[]) lstAddr.toArray(mailAddrs);
	}

	/**
	 * 将数组对象解析成数组，如果输入不是数组则返回null
	 * 
	 * @param data
	 *            输入的数组对象
	 * @return Object[] 解析后的对象数组
	 */
	private static Object[] convertToArray(Object data) {
		Object[] temp = null;

		if (data != null) {
			if (data.getClass().isArray()) {
				int size = Array.getLength(data);
				temp = new Object[size];
				for (int i = 0; i < size; i++) {
					temp[i] = Array.get(data, i);
				}
			} else {
				temp = new Object[1];
				temp[0] = data;
			}
		}
		return temp;
	}

	/**
	 * 发送电子邮件
	 * 
	 * @param subject
	 *            邮件标题
	 * @param mailFrom
	 *            发件人邮件地址
	 * @param content
	 *            邮件内容
	 * @param mailTO
	 *            收件人邮件地址列表
	 * @param mailCC
	 *            抄送人邮件地址列表
	 * @param mailBCC
	 *            暗送人邮件地址列表
	 * @param body
	 *            发送邮件的附件列表
	 * @return 邮件发送结果
	 */
	private static void sendMail(String subject, String mailFrom,String[] mailTO, String[] mailCC, 
			String[] mailBCC, String content,Object[] body) throws MessagingException, LegendException,Exception {
		Session sendMailSession;
		java.util.Properties smtpProps = SysUtility.GetProperties("config/mail.properties");
		if(SysUtility.isEmpty(smtpProps)){
			smtpProps = SysUtility.GetProperties("mail.properties");
		}
		
		if ("true".equalsIgnoreCase(smtpProps.getProperty("mail.smtp.auth"))) {
			smtpProps.put("mail.smtp.localhost", "localhost");
			// 如果smtp服务器需要验证，则构建AuthertiactorUtil用于mail session的创建
			String smtpuser = smtpProps.getProperty("mail.auth.user");
			String smtppwd = smtpProps.getProperty("mail.auth.password");
			AuthenticatorUtil authutil = new AuthenticatorUtil(smtpuser,smtppwd);
			sendMailSession = Session.getInstance(smtpProps, authutil);
		} else {
			sendMailSession = Session.getInstance(smtpProps, null);
		}

		if (sendMailSession == null) {
			// throw new Exception("获取邮件会话时发生错误!");
			throw LegendException.getLegendException(ERR.GET_MAIL_SESSION_ERR);
		}

		// 创建信息对象
		MimeMessage msg = createMessage(sendMailSession, subject, mailFrom,mailTO, mailCC, mailBCC, content, body);
		if (null == msg) {
			// throw new Exception("无法创建信息对象");
			throw LegendException.getLegendException(ERR.CAN_NOT_CREATE_MSG);
		}
		// 此次会话是发送邮件(smtp)
		Transport transport = sendMailSession.getTransport("smtp");
		transport.send(msg, msg.getAllRecipients()); // 设置多个发送目的地址
		transport.close();
	}

	private static MimeMessage createMessage(Session session, String subject,
			String mailFrom, String[] mailTO, String[] mailCC,
			String[] mailBCC, String content, Object[] body)
			throws MessagingException, Exception {
		// 封装信息对象
		MimeMessage mimeMessage = new MimeMessage(session);
		// 设置发件人邮件地址
		mimeMessage.setFrom(new InternetAddress(mailFrom));
		// 设置收件人邮件地址列表
		mimeMessage.setRecipients(Message.RecipientType.TO,getInternetAddress(mailTO));

		// 设置抄送人邮件地址列表
		if (mailCC != null && mailCC.length > 0) {
			mimeMessage.setRecipients(Message.RecipientType.CC,getInternetAddress(mailCC));
		}
		// 设置暗送人邮件地址列表
		if (mailBCC != null && mailBCC.length > 0) {
			mimeMessage.setRecipients(Message.RecipientType.BCC,getInternetAddress(mailBCC));
		}
		// 设置邮件主题
		mimeMessage.setSubject(getEncodedSubject(subject));
		// 设置发送日期
		mimeMessage.setSentDate(new java.util.Date());
		// 邮件编码
		String charset = SysUtility.GetProperty("config/mail.properties","mail.charset");
		if(SysUtility.isEmpty(charset)){
			charset = SysUtility.GetProperty("mail.properties","mail.charset");
		}
		String style = isHtmlContent(content) ? "text/html" : "text/plain";

		if (body != null && body.length > 0) {
			MimeMultipart mp = new MimeMultipart();
			// 设置邮件内容正文及字符集
			MimeBodyPart contentMbp = new MimeBodyPart();
			contentMbp.setContent((content == null ? "" : content), style + "; charset=" + charset); // 请指定字符集，否则会是乱码
			mp.addBodyPart(contentMbp); // 添加附件
			// 添加附件
			for (int i = 0; i < body.length; i++) {
				MimeBodyPart mbp = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(body[i].toString());
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(MimeUtility.encodeWord(fds.getName(), charset,null));
				mp.addBodyPart(mbp);
			}
			mimeMessage.setContent(mp);
		} else {
			mimeMessage.setContent((content == null ? "" : content), style + ";charset=" + charset); // 设置邮件内容正文
		}
		mimeMessage.saveChanges();
		return mimeMessage;
	}

	// 判断是否发送html格式的邮件
	private static boolean isHtmlContent(String content) {
		return content != null && content.toLowerCase().indexOf("<html>") >= 0;
	}

	private static InternetAddress[] getInternetAddress(String[] mailAddress)
			throws AddressException, LegendException, Exception {
		if (mailAddress != null && mailAddress.length > 0) {
			InternetAddress[] address = new InternetAddress[mailAddress.length];
			for (int i = 0; i < mailAddress.length; i++) {
				address[i] = new InternetAddress(mailAddress[i]);
			}
			return address;
		} else {
			throw LegendException.getLegendException(ERR.MAIL_ADDRESS_NULL);
		}
	}

	/**
	 * 得到编码后的内容
	 */
	private static String getEncodedSubject(String subject) {
		try {
			return MimeUtility.encodeText(subject);
		} catch (java.io.UnsupportedEncodingException uee) {
			return subject;
		}
	}

	public static String getStrSep(String demo) {
		String billNo = demo.trim();
		if (billNo.indexOf(" ") > -1)
			billNo = billNo.replaceAll(" ", ";");
		if (billNo.indexOf("\r\n") > -1)
			billNo = billNo.replaceAll("\r\n", ";");
		if (billNo.indexOf("\r") > -1)
			billNo = billNo.replaceAll("\r", ";");
		if (billNo.indexOf("\n") > -1)
			billNo = billNo.replaceAll("\n", ";");
		if (billNo.indexOf("	") > -1)
			billNo = billNo.replaceAll("	", ";");
		if (billNo.indexOf("，") > -1)
			billNo = billNo.replaceAll("，", ";");
		if (billNo.indexOf("；") > -1)
			billNo = billNo.replaceAll("；", ";");
		if (billNo.indexOf(":") > -1)
			billNo = billNo.replaceAll(":", ";");
		if (billNo.indexOf(",") > -1)
			billNo = billNo.replaceAll(",", ";");

		billNo = evlString(billNo);
		return billNo;
	}

	private static String evlString(String demo) {
		String de = demo;
		int falg = demo.indexOf(";;");
		de = demo.replaceAll(";;", ";");
		if (falg < 0) {
			return de;
		} else {
			de = evlString(de);
		}
		return de;
	}
}
