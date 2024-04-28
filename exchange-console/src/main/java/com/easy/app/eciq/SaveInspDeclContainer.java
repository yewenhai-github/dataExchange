package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

import java.net.*;

@WebServlet("/forms/SaveInspDeclContainer")
public class SaveInspDeclContainer extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public SaveInspDeclContainer() {
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception {

		String Indx = GetDataValue("ContainerData", "CONT_ID");
		String conts = "," + GetDataValue("ContainerData", "CONT_NO") + ",";
		String[] contArray = GetDataValue("ContainerData", "CONT_NO")
				.split(",");
		boolean cntrep = false;
		for (int i = 0; i < contArray.length; i++) {
			String cks = conts.replaceFirst("," + contArray[i] + ",", ",");
			if (cks.equals(contArray[i])) {
				cntrep = true;
				break;
			}
		}
		if (!cntrep) {
			SaveToTable("ContainerData", "CONTAINER_QTY",
					String.valueOf(contArray.length));
			if (SysUtility.isEmpty(Indx)) {
				InitFormData(
						"ContainerListCount",
						"SELECT Count(SEQ_NO) + 1 as SEQ_NO FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO = @ContainerData.DECL_NO");

				Indx = SysUtility.GetUUID();
				SaveToTable("ContainerData", "CONT_ID", Indx);
				SaveToTable("ContainerData", "SEQ_NO",
						GetDataValue("ContainerListCount", "SEQ_NO"));
				if (!getDataAccess().Insert("ITF_DCL_IO_DECL_CONT",
						getFormDatas().getJSONArray("ContainerData"), "CONT_ID")) {
					Indx = "";
				}
			} else {
				if (!getDataAccess().Update("ITF_DCL_IO_DECL_CONT",
						getFormDatas().getJSONArray("ContainerData"), "CONT_ID")) {
					Indx = "";
				}
			}

			if (!SysUtility.isEmpty(Indx)) {
				String alljson = GetDataValue("ContainerData", "INFOJSON");
				alljson = alljson.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
				String urlStr = URLDecoder.decode(alljson, "UTF-8");
				/*if (SysUtility.isEmpty(urlStr)) {
					String lcl = GetDataValue("ContainerData", "LCL_FLAG");
					StringBuilder sb = new StringBuilder();
					String lk = "";
					for (int i = 0; i < contArray.length; i++) {
						sb.append(lk
								+ "{\"SEQ_NO\":\""
								+ (i + 1)
								+ "\",\"CONT_NO\":\""
								+ contArray[i]
								+ "\",\"LCL_FLAG\":\""
								+ lcl
								+ "\",\"CONT_ID\":\"\",\"DECL_NO\":\"\",\"CNTNR_MODE_CODE\":\"\"}");
						lk = ",";
					}
					urlStr = "{\"INFOJSON\":[" + sb.toString() + "]}";
				}*/

				String declno = GetDataValue("ContainerData", "DECL_NO");
				if (SysUtility.isNotEmpty(urlStr)) {
					getFormDatas().put("CONTDETALL",
							new JSONObject(urlStr).getJSONArray("INFOJSON"));

					for (int i = 0; i < GetTableRows("CONTDETALL"); i++) {
						SaveToTable("CONTDETALL", "CONT_DT_ID",
								SysUtility.GetUUID(), i);
						SaveToTable("CONTDETALL", "CONT_ID", Indx, i);
						SaveToTable("CONTDETALL", "DECL_NO", declno, i);
						SaveToTable(
								"CONTDETALL",
								"CNTNR_MODE_CODE",
								GetDataValue("ContainerData", "CNTNR_MODE_CODE"),
								i);
					}
					getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_CONT_DETAIL Where CONT_ID= ? and DECL_NO= ?",
									new String[] { Indx, declno });
					getDataAccess().Insert("ITF_DCL_IO_DECL_CONT_DETAIL",
							getFormDatas().getJSONArray("CONTDETALL"), "CONT_DT_ID");
				}else{
					getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_CONT_DETAIL Where CONT_ID= ? and DECL_NO= ?",
									new String[] { Indx, declno });
				}

				ReturnMessage(true, "保存成功");
			} else {
				ReturnMessage(false, "保存失败");
			}
		}
		else{
			ReturnMessage(false,"包含重复的集装箱号");
		}

	}

}