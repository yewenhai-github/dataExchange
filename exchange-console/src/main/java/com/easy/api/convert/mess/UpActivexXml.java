package com.easy.api.convert.mess;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;

import com.easy.api.convert.util.XmlUtil;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


/**
 * 目前Controller 只支持Xml文件传输 ActiveX控件 只支持IE内核
 * @author chenchuang
 *
 */
@WebServlet("/upactivexxml")
public class UpActivexXml extends MainServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1705515826787125608L;

	@Override
	public void DoCommand() throws  DocumentException, LegendException, IOException {
		HttpServletRequest request = getRequest();
		if(SysUtility.isEmpty(request)) {
			return;//无需处理即可
		}
		String oindx = request.getParameter("INDX");
		if(!SysUtility.isEmpty(oindx)) {
			if (SysUtility.isNumber(oindx)) {//NumberUtil.isNumber(oindx)
				final int indx = Integer.parseInt(oindx.toString());//ConfigPath属性 拿到Path属性即可  为了安全 不要从前台传输进来
				String  sql = "select * from exs_convert_config_path where indx = ?";
				List PathData = SQLExecUtils.query4List(sql, new Callback() {
					
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setInt(1, indx);
					}
				});
				for (int i = 0; i < PathData.size(); i++) {
					Object oPathData  = PathData.get(i);
					if(oPathData instanceof Map) {
						HashMap mPathData = (HashMap) oPathData;
						String filename =  request.getParameter("filename");
						String content = request.getParameter("content");
						Object SOURCEFILEPATH = mPathData.get("SOURCEFILEPATH");
						if(filename.isEmpty() || content.isEmpty() || SysUtility.isEmpty(SOURCEFILEPATH)) {
							continue;
						}
						XmlUtil xmlUtil = new XmlUtil();
//						SOURCEFILEPATH + File.separator+filename;
						xmlUtil.WriteStringToFile(SOURCEFILEPATH +filename,content);
						ReturnMessage(true, "处理完毕！");
					}
					
				}
			}
		} 
	}
	
	
	

}
