package com.easy.app.eciq;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/DeleteEciq")
public class DeleteEciq  extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public DeleteEciq() {
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception {
		 boolean	isFlag=true;
		String doMethod =getRequest().getParameter("doMethod");
		if("DELETE".equals(doMethod)){
			String INDX =GetDataValue("DeclData", "DECL_NO");/* getRequest().getParameter("DECL_NO");*/
			
			//查询主表再进行删除
			String sqk="SELECT * FROM ITF_DCL_IO_DECL WHERE DECL_NO='"+INDX+"'";
		    List DECL=	SQLExecUtils.query4List(sqk);
		    if(DECL.size()>0){
		    	String  sql="DELETE  FROM ITF_DCL_IO_DECL WHERE DECL_NO='"+INDX+"'";
	        	isFlag  =getDataAccess().ExecSQL(sql);
		    }
		    //查询商品表在进行删除
		    String sqo="SELECT * FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO='"+INDX+"'";
		    List GOODS=	SQLExecUtils.query4List(sqo);
		    if(GOODS.size()>0){
		    	String sqlg="DELETE  FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO='"+INDX+"'";
	        	isFlag  =getDataAccess().ExecSQL(sqlg);
		    }
        	//查询集装箱在进行删除
		    String sqlcont="SELECT * FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO='"+INDX+"'";
		    List CONT=	SQLExecUtils.query4List(sqlcont);
		    if(CONT.size()>0){
		    	String sqlc="DELETE FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO='"+INDX+"'";
	        	isFlag  =getDataAccess().ExecSQL(sqlc);
		    }
        	if(isFlag){
				ReturnMessage(true, "成功删除数据 ！");
			}else{
				ReturnMessage(false, "操作失败！");
			}
		}else{
		String[] strIndxs = getRequest().getParameter("allId").split(",");
		getDataAccess().BeginTrans();
		if(!SysUtility.isEmpty(strIndxs)){	
	     //   int StatusCount = strIndxs.length;//删除成功的商品条数
	        int Count = 0;//操作的商品条数
	        for(String INDX : strIndxs) {
	        	String[] indx = new String[]{INDX};
	        	String sqk="SELECT * FROM ITF_DCL_IO_DECL WHERE DECL_NO='"+INDX+"' AND ( DECL_STATUS_CODE = '0' OR DECL_STATE_CODE  IN ('01','4') )";
			    List DECL=	SQLExecUtils.query4List(sqk);
			    if(DECL.size()>0){
			    	String  sql="DELETE  FROM ITF_DCL_IO_DECL WHERE DECL_NO='"+INDX+"'";
		        	isFlag  =getDataAccess().ExecSQL(sql);
				    //查询商品表在进行删除
				    String sqo="SELECT * FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO='"+INDX+"'";
				    List GOODS=	SQLExecUtils.query4List(sqo);
				    if(GOODS.size()>0){
				    	String sqlg="DELETE  FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO='"+INDX+"'";
			        	isFlag  =getDataAccess().ExecSQL(sqlg);
				    }
		        	//查询集装箱在进行删除
				    String sqlcont="SELECT * FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO='"+INDX+"'";
				    List CONT=	SQLExecUtils.query4List(sqlcont);
				    if(CONT.size()>0){
				    	String sqlc="DELETE FROM ITF_DCL_IO_DECL_CONT WHERE DECL_NO='"+INDX+"'";
			        	isFlag  =getDataAccess().ExecSQL(sqlc);
				    }
				    Count++;
			    }
			   
			}
	        getDataAccess().ComitTrans();
			if(isFlag){
				getDataAccess().ComitTrans();
				ReturnMessage(true, "成功删除"+Count+"数据 ！");
			}else{
				ReturnMessage(false, "操作失败！");
			}
		}

	}
	}
}
