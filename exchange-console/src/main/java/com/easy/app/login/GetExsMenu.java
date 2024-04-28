package com.easy.app.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.session.Operator;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetExsMenu")
public class GetExsMenu extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetExsMenu(){
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		
		JSONArray array = new JSONArray(getExsMenu());
		getFormDatas().put("menu", array);
		ReturnMessage(true, "", "",getFormDatas().toString());
		
		Operator operator = new Operator();
		operator.setSign("admin");
		operator.setUserName("admin");
		operator.setIndx(Integer.parseInt("-1"));
		operator.setPartId("000000");
		SysUtility.setCurrentOperator(operator);
	}
	
	public List getConsoleMenu(){
		List lst = new ArrayList();
		addMenu(lst,new String[]{"用户注册","1","0","1","","icon-globe"});
		addMenu(lst,new String[]{"配置中心","2","0","2","","icon-share"});
//		addMenu(lst,new String[]{"传输中心","3","0","3","","icon-plane"});
		addMenu(lst,new String[]{"微服务中心","3","0","3","","icon-refresh"});
		addMenu(lst,new String[]{"监控中心","4","0","4","","icon-wrench"});
		addMenu(lst,new String[]{"预警中心","5","0","4","","icon-wrench"});
		addMenu(lst,new String[]{"企业管理","6","0","6","","icon-user"});
		List tempList = new GetExsMenu().getExsMenu();
		lst.addAll(tempList);
		return lst;
	}
	
	public List getExsMenu(){
		List lst = new ArrayList();
		
//		addMenu(lst,new String[]{"注册中心","200","1","1","","icon-globe"});
		
//		addMenu(lst,new String[]{"定时集群实例","203","1","3","html/01QuartzConfigClusterList.html",""});
		addMenu(lst,new String[]{"用户帐号信息","301","1","1","html/00ExsUserList.html",""});//
		addMenu(lst,new String[]{"接入企业权限","218","1","2","html/05AccessLimitList.html",""});//
		addMenu(lst,new String[]{"企业通道开通","217","1","3","html/05AccessCustomerList.html",""});//
		
		addMenu(lst,new String[]{"schema清单","201","6","0","html/01SchemaList.html",""});
		addMenu(lst,new String[]{"定时任务配置","202","2","1","html/01QuartzConfigList.html",""});
		addMenu(lst,new String[]{"任意消息生成","211","2","2","html/02DB2AnyXmlList.html",""});
		addMenu(lst,new String[]{"任意消息入库","212","2","3","html/02AnyXml2DBList.html",""});
//		addMenu(lst,new String[]{"标准消息生成","213","2","3","html/02DB2XmlList.html",""});
//		addMenu(lst,new String[]{"标准消息入库","214","2","4","html/02Xml2DBList.html",""});
		addMenu(lst,new String[]{"消息路由中心","223","2","5","html/03Xml2MqList.html",""});
		addMenu(lst,new String[]{"消息路由解析","224","2","6","html/03Mq2XmlList.html",""});
		addMenu(lst,new String[]{"文件目录合并","225","2","7","html/03Xml2MergeList.html",""});
		addMenu(lst,new String[]{"文件目录拆分","226","2","8","html/03Xml2SplitList.html",""});
		
//		addMenu(lst,new String[]{"OpenApi中心","215","4","4","","icon-refresh"});
		
		addMenu(lst,new String[]{"接收接口定义","219","3","1","html/02Api2ServerList.html",""});
		addMenu(lst,new String[]{"推送接口定义","219","3","2","html/02Api2ServerList.html",""});
		addMenu(lst,new String[]{"接收接口消息解析","221","3","3","html/02Api2ServerQuartzList.html",""});
		addMenu(lst,new String[]{"推送接口消息生成","221","3","4","html/02Api2ServerQuartzList.html",""});
		addMenu(lst,new String[]{"企业自动推送消息","220","3","5","html/02Api2ClientQuartzList.html",""});
		addMenu(lst,new String[]{"企业自动接收消息","220","3","5","html/02Api2ClientQuartzList.html",""});
		
//		addMenu(lst,new String[]{"监控中心","240","5","5","","icon-wrench"});
		
		addMenu(lst,new String[]{"消息日志记录","241","4","1","html/04LogList.html",""});
		addMenu(lst,new String[]{"消息驱动记录","242","4","2","html/04SendList.html",""});
		addMenu(lst,new String[]{"回执接收查询","243","4","3","html/04PushReceivedList.html",""});
		addMenu(lst,new String[]{"回执推送查询","243","4","4","html/04PullReceivedList.html",""});
		
		addMenu(lst,new String[]{"应用服务预警","244","5","1","html/04AppList.html",""});
		addMenu(lst,new String[]{"剩余文件预警","246","5","2","html/04FodlerListenList.html",""});//监控前置交换的各个节点状态。
		
		
		addMenu(lst,new String[]{"企业信息","247","6","1","html/EdsEntList.html",""});
		addMenu(lst,new String[]{"企业用户","248","6","2","html/EdsUserList.html",""});
		
		
//		addMenu(lst,new String[]{"客户中心","250","6","6","","icon-user"});
		
		
		return lst;
		
		//addMenu(lst,new String[]{"消息转换配置","215","210","5","",""});//输入A消息，输出B消息，格式及规范可定义与配置。
		//addMenu(lst,new String[]{"接入通道验证","252","250","2","html/AccessPassList.html",""});//
	}
	
	public List getConsoleMenuByPid(String pid){
		List menuList = new ArrayList();
		List twoList = new ArrayList();
		List tempList = getConsoleMenu();
		for (int i = 0; i < tempList.size(); i++) {
			HashMap map = (HashMap)tempList.get(i);
			if(pid.equals(map.get("MENU_PID"))){
				menuList.add(map);
				twoList.add(map.get("MENU_ID"));
			}
		}
		for (int i = 0; i < tempList.size(); i++) {
			HashMap map = (HashMap)tempList.get(i);
			if(twoList.contains(map.get("MENU_PID"))){
				menuList.add(map);
			}
		}
		return menuList;
	}
	private String[] coulmnNames = new String[]{"MENU_NAME","MENU_ID","MENU_PID","MENU_ORDER","COMMAND_NAME","MENU_ICON"};
	
	private void addMenu(List lst,String[] coulmnValues){
		HashMap map = new HashMap();
		for (int i = 0; i < coulmnNames.length; i++) {
			map.put(coulmnNames[i], coulmnValues[i]);
		}
		lst.add(map);
	}
}
