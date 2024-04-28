package com.easy.app.eciq;


import java.util.List;
import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/DeleteInspDecl")
public class DeleteInspDecl extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String DECL_NO=GetDataValue("DeclData", "DECL_NO");
		if (!SysUtility.isEmpty(DECL_NO)) {
			  getDataAccess().BeginTrans();
              boolean isFlag = true;
              
              //出入境报检基本信息表
              isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL where DECL_STATUS_CODE='0' and  DECL_NO=?",DECL_NO);
              if (isFlag == false)
              {
            	  ReturnMessage(false, "删除ITF_DCL_IO_DECL失败！");
                  getDataAccess().RoolbackTrans();
                  return;
              }
              else
              {
            	  //出入境货物产品信息表
            	  String sqlGoods=String.format("select * from ITF_DCL_IO_DECL_GOODS where DECL_NO='%s'", DECL_NO);
            	  List lst = SQLExecUtils.query4List(sqlGoods);
          		  int size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_GOODS where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_GOODS失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境货物与集装箱关联信息表
            	  String sqlGoodsCON=String.format("select * from ITF_DCL_IO_DECL_GOODS_CONT where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlGoodsCON);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_GOODS_CONT where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_GOODS_CONT失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境许可证信息表
            	  String sqlLIM=String.format("select * from ITF_DCL_IO_DECL_GOODS_LIMIT where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlLIM);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_GOODS_LIMIT where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_GOODS_LIMIT失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境许可证VIN信息表
            	  String sqlLIMVN=String.format("select * from ITF_DCL_IO_DECL_GOODS_LIMIT_VN where ENT_DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlLIMVN);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_GOODS_LIMIT_VN where ENT_DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_GOODS_LIMIT_VN失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境包装信息表
            	  String sqlPack=String.format("select * from ITF_DCL_IO_DECL_GOODS_PACK where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlPack);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_GOODS_PACK where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_GOODS_PACK失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境集装箱信息表
            	  String sqlCont=String.format("select * from ITF_DCL_IO_DECL_CONT where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlCont);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_CONT where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_CONT失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境集装箱号明细表
            	  String sqlEXC=String.format("select * from ITF_DCL_IO_DECL_CONT_DETAIL where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlEXC);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_CONT_DETAIL where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_CONT_DETAIL失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境随附单据信息表
            	  String sqlAtt=String.format("select * from ITF_DCL_IO_DECL_ATT where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlAtt);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_ATT where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_ATT失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //企业资质信息表
            	  String sqlLimit=String.format("select * from ITF_DCL_IO_DECL_LIMIT where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlLimit);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_LIMIT where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_LIMIT失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境使用人信息表
            	  String sqlUser=String.format("select * from ITF_DCL_IO_DECL_USER where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlUser);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_IO_DECL_USER where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_IO_DECL_USER失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }

          		  //出入境报检标记号码附件表
            	  String sqlMarkLob=String.format("select * from ITF_DCL_MARK_LOB where DECL_NO='%s'", DECL_NO);
            	  lst = SQLExecUtils.query4List(sqlMarkLob);
          		  size = lst.size();
          		  if(size!=0){
	                  isFlag = getDataAccess().ExecSQL("delete from ITF_DCL_MARK_LOB where DECL_NO=?",DECL_NO);
	                  if (isFlag == false)
	                  {
	                	  ReturnMessage(false, "删除ITF_DCL_MARK_LOB失败！");
	                      getDataAccess().RoolbackTrans();
	                      return;
	                  }
          		  }
                  ReturnMessage(true, "删除成功！","",getFormDatas().toString());
                  getDataAccess().ComitTrans();
              }
		}
	}
}
