package com.easy.app.adapter;

import java.util.List;
import javax.servlet.annotation.WebServlet;

import com.easy.adapter.InitSqlMapAdapter;

@WebServlet(urlPatterns = {"/InitServlet.do"},loadOnStartup = 1)
public class InitServlet extends InitSqlMapAdapter{
	private static final long serialVersionUID = 934557295508102639L;
	
	@Override
	public void initSqlMap(List<String> viewList) {
		viewList.add("config/query/auth.xml");
		viewList.add("config/query/combobox.xml");
		viewList.add("config/query/base.xml");
		viewList.add("config/query/exp.xml");
		viewList.add("config/query/eciq.xml");
		viewList.add("config/query/mess.xml");
		super.initSqlMap(viewList);
	}
}