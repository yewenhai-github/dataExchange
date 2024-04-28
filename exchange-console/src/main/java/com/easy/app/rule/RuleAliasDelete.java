package com.easy.app.rule;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/RuleAliasDelete")
public class RuleAliasDelete extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String Indxs = getRequest().getParameter("allId");
		
		if (!SysUtility.isEmpty(Indxs)) {
			String [] strIndxs= Indxs.split(","); 
	
	        getDataAccess().BeginTrans();
	        int StatusCount = 0;//申报成功的条数
	        int Count = 0;//操作的条数
	        
			for(String Indx : strIndxs) {
	            Count++;

				StringBuffer SelectSQL = new StringBuffer();
				SelectSQL.append("Select * from RULE_T_ALIAS where IS_ENABLED=1 AND INDX=" + Indx);
				List lst = SQLExecUtils.query4List(SelectSQL.toString());
				int size = lst.size();
				if (size !=0) {
					StringBuffer UpdateSQL = new StringBuffer();
					UpdateSQL.append("UPDATE RULE_T_ALIAS SET IS_ENABLED=0 where INDX= "+Indx);
					boolean isFlag= getDataAccess().ExecSQL(UpdateSQL.toString());
					if(isFlag){
						StatusCount++;
					}else {
						getDataAccess().RoolbackTrans();
						ReturnMessage(true, "删除失败!");
					}
				} 
    		  
			}
			String NoGoods = "";
            if(Count>StatusCount)
            {
                NoGoods = "，<br/>不符合条件的有" + (Count-StatusCount) + "条！";
            }
            getDataAccess().ComitTrans();
            ReturnMessage(true, "操作" + Count + "条数据，<br/>删除成功" + StatusCount + "条" + NoGoods);
		}
        else
        {
        	ReturnMessage(false, "传入的数据有错误，无法删除！");
            getDataAccess().RoolbackTrans();
        }
	}
}
