package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/DeleteInspGoodsCounts")
public class DeleteInspGoodsCounts extends MainServlet {
	private static final long serialVersionUID = -2551208606761955615L;
	public DeleteInspGoodsCounts()
	{
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		
		int rows = this.GetTableRows("key");
		if(rows > 0){
	        getDataAccess().BeginTrans();
		    int StatusCount = 0;//删除成功的条"
			for(int i = 0 ; i < rows ; i ++){
				boolean isFlag =getDataAccess().ExecSQL("delete from  ITF_DCL_IO_DECL_GOODS_CONT where GOODS_CONT_ID = ? " ,new String[]{ this.GetDataValue("key", "GOODS_CONT_ID", i)});
				if(isFlag){
					StatusCount++;
				}				
			}
			if(StatusCount>0){
				ErrMessages = "操作"+ rows + "条数据！<br>成功删除" + StatusCount + "条！<br>";
				if(rows>StatusCount){
					ErrMessages += "删除失败" + (rows -StatusCount) + "条！";
				}
                ReturnMessage(true, ErrMessages);
                getDataAccess().ComitTrans();
			}
            else
            {
                ReturnMessage(false, "操作失败");
                getDataAccess().RoolbackTrans();
            }
		}
	}
}
