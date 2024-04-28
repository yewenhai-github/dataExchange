package com.easy.api.getlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.utility.EntityUtility;
import com.easy.utility.SimpleExsUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2AnyXmlTest")
public class GetDB2AnyXmlTest extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String INDX = (String)getEnvDatas().get("INDX");
		
		Datas datas = new Datas();
		try {
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("SELECT * FROM exs_config_dbtoxml WHERE ifnull(IS_ENABLED,'1') = '1' and rownum = 1 ");
			sqlBuild.append(" AND INDX = ?",INDX);
			Map db2xmlMap = sqlBuild.query4Map();
			
			ServicesBean tempBean = new ServicesBean();
			EntityUtility.hashMapToEntity(tempBean, db2xmlMap);
			tempBean.setServiceType("1");
			SimpleExsUtility.setExsConfigDbtoxmlSql(tempBean,(String)db2xmlMap.get("INDX"),datas);
			SimpleExsUtility.setExsConfigDbtoxmlTest(tempBean, datas);
			List MainDatas = SimpleExsUtility.GetCacheData(tempBean, getDataAccess(), new HashMap());
			
			if(SysUtility.isNotEmpty(MainDatas)){
				HashMap MainData = (HashMap)MainDatas.get(0);
				tempBean.setSerialNo(SysUtility.isNotEmpty(MainData.get(tempBean.getSerialName()))?(String)MainData.get(tempBean.getSerialName()):(String)MainData.get(tempBean.getIndxName()));
			}
			String xmlData = SimpleExsUtility.GetAnyXmlData(tempBean, MainDatas, new HashMap());
			datas.SetTableValue("FileData", "SOURCEDATA",xmlData);
		} catch (LegendException e) {
			datas.SetTableValue("FileData", "SOURCEDATA",e.getMessage());
		} catch (Exception e) {
			datas.SetTableValue("FileData", "SOURCEDATA",e.getMessage());
		}
		ReturnMessage(true,"", "", datas.getDataJSON().getJSONArray("FileData").toString());
	}
}