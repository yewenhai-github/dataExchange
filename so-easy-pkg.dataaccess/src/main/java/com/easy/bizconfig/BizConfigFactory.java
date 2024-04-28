package com.easy.bizconfig;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;

public final class BizConfigFactory {

	// 缺省的二级业务类型，即通用业务类型，此常量主要用于公司配置信息中的业务类型匹配
	private static final int GENERAL_BIZ_ID = 0;

	private BizConfigFactory() {
	}

	public static String getCfgValue(String cfg_code) {
		return getCfgValue(cfg_code, SysUtility.isNotEmpty(SysUtility.getCurrentOrgId())?SysUtility.getCurrentOrgId().substring(0, 2)+"0000":"" , GENERAL_BIZ_ID);
	}

	public static String getCfgValue(String cfg_code, int bizLevel) {
		return getCfgValue(cfg_code, SysUtility.getCurrentOrgId(), bizLevel);
	}

	public static String getCfgValue(String cfg_code, String org_id) {
		return getCfgValue(cfg_code, org_id, GENERAL_BIZ_ID);
	}

	/**
	 * 获得公司下,指定业务类型的指定配置信息,如果分公司没有配置，则获取全局缺省的配置信息
	 * @param cfg_code - 配置信息编码
	 * @param org_id - 公司代码
	 * @param bizLevel - 二级业务类型
	 * @return String
	 */
	public static String getCfgValue(String cfg_code, String org_id, int bizLevel) {
		return BizConfigMgr.getInstance().getConfigValue(cfg_code, org_id, bizLevel);
	}

	public synchronized static void reload() {
		BizConfigMgr.getInstance().init();
	}
	
	public synchronized static void reloadSingleCfg(String sc_code, String org_Id,String sc_value) {
		String key = BizConfigMgr.buildKey(sc_code, org_Id, 0);
		BizConfigMgr.getInstance().reloadSingleCfg(key,sc_value);
	}
	
	public synchronized static void reloadSingleCfg(String key,String cbic_sc_value) {
		BizConfigMgr.getInstance().reloadSingleCfg(key,cbic_sc_value);
	}
	
	public synchronized static String reloadCfgBySql(String Cbic_org_id,String Cbic_biz_level,String Cbic_sc_code) throws LegendException{
		return BizConfigMgr.getInstance().reloadCfgBySql(Cbic_org_id,Cbic_biz_level,Cbic_sc_code);
	}
	
}