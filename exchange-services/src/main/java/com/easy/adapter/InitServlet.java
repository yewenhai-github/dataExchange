package com.easy.adapter;

import java.util.List;
import javax.servlet.annotation.WebServlet;

import com.easy.adapter.InitAdapter;

@WebServlet(urlPatterns = {"/InitServlet.do"},loadOnStartup = 1)
public class InitServlet extends InitAdapter{
	private static final long serialVersionUID = 934557295508102639L;
	
	@Override
	public void initSqlMap(List<String> viewList) {
		super.initSqlMap(viewList);
	}
}
