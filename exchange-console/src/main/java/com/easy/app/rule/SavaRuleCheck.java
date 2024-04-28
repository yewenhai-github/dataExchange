package com.easy.app.rule;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.app.utility.ExpUtility;
import com.easy.bizconfig.BizConfigFactory;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.QuzrtzUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SavaRuleCheck")
public class SavaRuleCheck extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		JSONArray rows = getFormDatas().getJSONArray("RULE_CHECK");
		JSONObject Rule = rows.getJSONObject(0);
		String SQL = "select 0 from rule_t_check r where r.indx = ?";
		final String index = Rule.getString("Indx");		
		String rt = SQLExecUtils.query4String(SQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, index);
				
			}
		});
		if(SysUtility.isEmpty(rt)){
			Rule.put("IS_ENABLED", "0");//默认为禁用状态
		}

		String result = validate(rows);//校验数据
		if(SysUtility.isNotEmpty(result)){
			ReturnMessage(false, result, "","");
			return;
		}
		try {
			String REC_VER=BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentOrgId());
			if(SysUtility.isNotEmpty(REC_VER)){
				rows.optJSONObject(0).put("APP_REC_VER",REC_VER );
			}
			int indx=SaveToDB(rows, "RULE_T_CHECK");
			if(SysUtility.isEmpty(indx)){
				ReturnMessage(false, "保存失败！", "","");
			}
			ReturnMessage(true, "保存成功！", "","");
		} catch (Exception e) {
			ReturnMessage(false, "保存失败！", "","");
		}
	}
	
	private String validate(JSONArray rows) throws LegendException{
		if(SysUtility.isEmpty(rows)){
			return "保存数据不能为空！";
		}
		//新增时的校验
		if(SysUtility.isEmpty(SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "INDX", 0))){
			if(SysUtility.isNotEmpty(SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "RULE_HIT_CON"))){
				return "请您先不要保存：表达式";
			}
			String SQL = "select 0 from rule_t_check r where r.point_code = ? and r.rule_no = ?";
			final String pointCode = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "POINT_CODE");
			final String ruleNo = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "RULE_NO");
			String rt = SQLExecUtils.query4String(SQL, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, pointCode);
					ps.setString(2, ruleNo);
				}
			});
			if(SysUtility.isNotEmpty(rt)){
				return "规则编号："+ruleNo + " 已存在，请您更换规则编号后重新保存";
			}
			
		}else{//修改时的校验
			String ExistSQL = "select rule_no from rule_t_check r where r.point_code = ? and r.rule_no = ?";
			for (int i = 0; i < rows.length(); i++) {
				final String indx = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "INDX", i);
				if(SysUtility.isEmpty(indx)){
					return "保存失败，主键不能为空";
				}
				final String pointCode = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "POINT_CODE", i);
				final String ruleNo = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "RULE_NO", i);
				final String forwardSkip = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "FORWARD_SKIP", i);//正向跳转
				final String reverseSkip = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "REVERSE_SKIP", i);//反向跳转
				final String ruleHitCon = SysUtility.GetTableValue(getFormDatas(), "RULE_CHECK", "RULE_HIT_CON", i);//表达式
				//校验正向跳转
				if(SysUtility.isNotEmpty(forwardSkip)){
					String rt = SQLExecUtils.query4String(ExistSQL, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, pointCode);
							ps.setString(2, forwardSkip);
						}
					});
					if(SysUtility.isEmpty(rt)){
						return "保存失败！正向跳转值"+forwardSkip+"(规则编号)在规则中不存在";
					}else if(ruleNo.equals(rt)){
						return "保存失败！正向跳转值"+forwardSkip+" 不能与当前规则编号相同";
					}
				}
				//校验反向跳转
				if(SysUtility.isNotEmpty(reverseSkip)){
					String rt = SQLExecUtils.query4String(ExistSQL, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, pointCode);
							ps.setString(2, reverseSkip);
						}
					});
					if(SysUtility.isEmpty(rt)){
						return "保存失败！反向跳转值"+reverseSkip+"(规则编号)在规则中不存在";
					}else if(ruleNo.equals(rt)){
						return "保存失败！反向跳转值"+forwardSkip+" 不能与当前规则编号相同";
					}
				}
				//校验表达式
				if(SysUtility.isNotEmpty(ruleHitCon)){
					String ConSQL = "select c.* from rule_t_check_con c where c.p_indx = ?";
					List ruleConList = SQLExecUtils.query4List(ConSQL, new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, indx);
						}
					});
					if(SysUtility.isEmpty(ruleConList)){
						return "保存失败！表达式无效，请您先保存条件维护";
					}
					
					
				}
			}
		}
		return "";
	}
	
}
