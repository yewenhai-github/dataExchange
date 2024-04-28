package com.easy.app.eciq;


import java.util.List;
import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/DelElecDeclByIndx")
public class DelElecDeclByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String SrcIndx =getRequest().getParameter("INDX");
		String doMethod =getRequest().getParameter("doMethod");
		if("DelByDeclGetNo".equals(doMethod)){
			String DECL_GET_NO =GetDataValue("ElecDeclData", "DECL_GET_NO");
			if (SysUtility.isNotEmpty(DECL_GET_NO)) {
				  getDataAccess().BeginTrans();
				  boolean isFlag = true;
	              boolean isChildFlag = true;
	              String sql=String.format("select * from ITF_SRC_DECL_GOODS where p_indx in (select indx from ITF_SRC_DECL where DECL_GET_NO='%s')", DECL_GET_NO);
            	  List sqlLst = SQLExecUtils.query4List(sql);
          		  int size = sqlLst.size();
          		  if(size>0){
		              isChildFlag = getDataAccess().ExecSQL("delete from ITF_SRC_DECL_GOODS where p_indx in (select indx from ITF_SRC_DECL where DECL_GET_NO=?)",DECL_GET_NO);
		              if (isChildFlag == false)
		              {
		            	  ReturnMessage(false, "删除ITF_SRC_DECL_GOODS失败！");
		                  getDataAccess().RoolbackTrans();
		                  return;
		              }
          		  }
	              else
	              {
	            	  isFlag = getDataAccess().ExecSQL("delete from ITF_SRC_DECL where DECL_GET_NO=?",DECL_GET_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除DECL_GET_NO失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
	              }
	              if((isChildFlag&&isFlag) == true){
	            	  getDataAccess().ComitTrans();
	            	  ReturnMessage(true, "删除成功！","",getFormDatas().toString());
	              }else{
	  				  ReturnMessage(false, "操作失败！");
	  			  }
			}
		}else{
			if (SysUtility.isNotEmpty(SrcIndx)) {
				  getDataAccess().BeginTrans();
	              boolean isFlag = true;
	              boolean isChildFlag = true;
	              isFlag = getDataAccess().ExecSQL("delete from ITF_SRC_DECL where Indx=?",SrcIndx);
	              if (isFlag == false)
	              {
	            	  ReturnMessage(false, "删除ITF_SRC_DECL失败！");
	                  getDataAccess().RoolbackTrans();
	                  return;
	              }
	              else
	              {
	            	  String sqlGoods=String.format("select * from ITF_SRC_DECL_GOODS where P_INDX=%s", SrcIndx);
	            	  List lst = SQLExecUtils.query4List(sqlGoods);
	          		  int size = lst.size();
	          		  if(size!=0){
	          			  isChildFlag = getDataAccess().ExecSQL("delete from ITF_SRC_DECL_GOODS where P_INDX=?",SrcIndx);
		                  if (isChildFlag == false)
		                  {
		                	  ReturnMessage(false, "删除ITF_SRC_DECL_GOODS失败！");
		                      getDataAccess().RoolbackTrans();
		                      return;
		                  }
	          		  }
	              }
	              if((isChildFlag&&isFlag) == true){
	            	  getDataAccess().ComitTrans();
	            	  ReturnMessage(true, "删除成功！","",getFormDatas().toString());
	              }else{
	  				  ReturnMessage(false, "操作失败！");
	  			  }
			}
		}
	}
}
