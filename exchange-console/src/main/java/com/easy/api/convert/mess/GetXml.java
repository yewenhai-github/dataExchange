package com.easy.api.convert.mess;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetXml")
public class GetXml extends MainServlet{

	@Override
	public void DoCommand() throws LegendException, Exception {
		final String INDX = getRequest().getParameter("INDX");
		String sql = "SELECT SOURCE_BACK_PATH , SUCCESS_BACK_PATH FROM exs_convert_log WHERE INDX=?";
		List query4List = SQLExecUtils.query4List(sql, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, INDX);
			}
		});
		if(query4List.size()>0) {
			HashMap xmlData = (HashMap)query4List.get(0);
			String SOURCE_BACK_PATH = (String) xmlData.get("SOURCE_BACK_PATH");
			String SUCCESS_BACK_PATH = (String) xmlData.get("SUCCESS_BACK_PATH");
			InputStream in = null;
			InputStream ins = null;
			String SOURCExmlData = "";
			String SUCCESSxmlData = "";
			if(!SysUtility.isEmpty(SUCCESS_BACK_PATH)) {
				SUCCESS_BACK_PATH = SUCCESS_BACK_PATH.split(",")[0];
				ins = new FileInputStream(SUCCESS_BACK_PATH);
				byte[] bytess = SysUtility.InputStreamToByte(ins);
				SUCCESSxmlData = new String(bytess,"UTF-8");
			}
			if(!SysUtility.isEmpty(SOURCE_BACK_PATH)) {
				String prefix=SOURCE_BACK_PATH.substring(SOURCE_BACK_PATH.lastIndexOf(".")+1);
				if(!prefix.contentEquals("xls")) {
					in = new FileInputStream(SOURCE_BACK_PATH);
					byte[] bytes = SysUtility.InputStreamToByte(in);
					SOURCExmlData = new String(bytes,"UTF-8");
				}
			}
			
			if(!SysUtility.isEmpty(in)) {
				in.close();
			}
			if(!SysUtility.isEmpty(ins)) {
				ins.close();
			}
			
			JSONObject datas = new  JSONObject();
			try {
				datas.put("SOURCEXMLDATA", format(SOURCExmlData));
			} catch (Exception e) {
				datas.put("SOURCEXMLDATA", SOURCExmlData);
			}
			try {
				datas.put("SUCCESSXMLDATA", format(SUCCESSxmlData));
			} catch (Exception e) {
				datas.put("SUCCESSXMLDATA", SUCCESSxmlData);
			}
			ReturnMessage(true, "获取成功","",datas.toString());
		}
		
		
	}
	
	
	
	public static String format(String str) throws Exception {
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // 注释：创建输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        //formater=OutputFormat.createCompactFormat();
        // 注释：设置xml的输出编码
        formater.setEncoding("utf-8");
        // 注释：创建输出(目标)
        StringWriter out = new StringWriter();
        // 注释：创建输出流
        XMLWriter writer = new XMLWriter(out, formater);
        // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
        writer.write(doc);
        writer.close();
        // 注释：返回我们格式化后的结果
        return out.toString();
    }

}
