package com.easy.app.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.bizconfig.BizConfigFactory;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveAliaNews")
public class SaveAliaNews extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String nowTime = dateFormat.format(new java.util.Date());

		JSONArray jsonData = getFormDatas().getJSONArray("Role");
		JSONObject rtObj = jsonData.getJSONObject(0);

		Datas dtConfig = getDataAccess().GetTableDatas("RULE_B_ALIAS_CONFIG",
				"Select * from RULE_B_ALIAS_CONFIG Where INDX="+ rtObj.getString("tableId").toString() + "");
		if (dtConfig.GetTableRows("RULE_B_ALIAS_CONFIG") > 0) {
			SaveToTable("Role", "TABLE_NAME",dtConfig.GetTableValue("TABLE_NAME"));
		}
		SaveToTable("Role", "CREATE_TIME", nowTime);
		SaveToTable("Role", "CREATOR", SysUtility.getCurrentUserIndx());
		SaveToTable("Role","APP_REC_VER",BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId()));
		int Indx = SaveToDB(getFormDatas().getJSONArray("Role"), "RULE_T_ALIAS");
		getDataAccess().ExecSQL("delete from RULE_T_ALIAS_DETAIL Where p_indx="+ Indx);
		getDataAccess().ComitTrans();
		String sql = " insert INTO  RULE_T_ALIAS_DETAIL (indx,P_INDX,SEQ,BIZ_TYPE,COULMN_VALUE,COULMN_DESC,CREATOR)VALUES(seq_RULE_T_ALIAS_DETAIL.Nextval,?,?,?,?,?,?)";
		Datas aliaTable = getDataAccess().GetTableDatas("RULE_T_ALIAS_DETAIL_TEMP","Select '' as p_indx,'' as SEQ,'' as CREATE_TIME,BIZ_CODE as COULMN_VALUE,BIZ_NAME as COULMN_DESC,CREATOR from RULE_T_ALIAS_DETAIL_TEMP  WHERE creator="
								+ SysUtility.getCurrentUserIndx()
								+ " order by INDX desc");
		Connection con = getDataAccess().GetActiveCN();
		PreparedStatement ps = getDataAccess().GetActiveCN().prepareStatement(sql);
		con.setAutoCommit(false);
		if (aliaTable.GetTableRows("RULE_T_ALIAS_DETAIL_TEMP") > 0) {
			getDataAccess().BeginTrans();
			try {
				for (int i = 0; i < aliaTable.GetTableRows("RULE_T_ALIAS_DETAIL_TEMP"); i++) {
					ps.setInt(1, Indx);
					ps.setInt(2, i);
					ps.setString(3, "2");
					ps.setString(4, aliaTable.GetTableValue("RULE_T_ALIAS_DETAIL_TEMP", "COULMN_VALUE", i));
					ps.setString(5, aliaTable.GetTableValue("RULE_T_ALIAS_DETAIL_TEMP", "COULMN_DESC", i));
					ps.setInt(6,Integer.parseInt(SysUtility.getCurrentUserIndx()));
					ps.addBatch();
					if (i % 5000 == 0) {
						ps.executeBatch();
					}
				}
				ps.executeBatch();
			} catch (Exception e) {
				getDataAccess().RoolbackTrans();
			}
			// int CurrentId = SaveToDB((JSONArray)
			// aliaTable.getDataJSON().get("RULE_T_ALIAS_DETAIL_TEMP"),
			// "RULE_T_ALIAS_DETAIL");
		}
		String SQL = "Select *  from RULE_T_ALIAS Where Indx =" + Indx;
		ReturnMessage(true, "操作成功", "", GetReturnDatas("@" + SQL, "Role")
				.toString());
	}
}
