package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsByIndx")
public class GetInspGoodsByIndx extends MainServlet {

	private static final long serialVersionUID = -5351665045925757934L;
	public GetInspGoodsByIndx()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception {
		String GOODS_ID=(String) this.GetEnvDatas("GOODS_ID");
		//如果是从合并进入详细页
		if(SysUtility.isEmpty(GOODS_ID)){
			this.getEnvDatas().put("LoginUser", SysUtility.getCurrentUserIndx());
			InitFormData("GoodsData",SQLMap.getSelect("EciqInspDefaultGoods"));
		}else{
	        InitFormData("GoodsData", SQLMap.getSelect("GetInspGoodsById"));
		}
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
