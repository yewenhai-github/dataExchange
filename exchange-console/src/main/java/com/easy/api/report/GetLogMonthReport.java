package com.easy.api.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetLogMonthReport")
public class GetLogMonthReport extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetLogMonthReport() {
		CheckLogin = false;
	}
	
	public static void main(String[] args) {
		new GetLogMonthReport().getMonthDays();
	}
	
	public void DoCommand() throws Exception{
		JSONObject data = GetReturnDatasAllDB("GetLogMonthReport");
		JSONArray rows = (JSONArray)data.get("rows");
		
		JSONObject result = new JSONObject();
		List days = getMonthDays();
		for (int i = 0; i < days.size(); i++) {
			String rttime = (String)days.get(i);
			
			result.put("t"+(days.size()-i), rttime.substring(8, 10));
			result.put("a1"+(days.size()-i), "0");
			result.put("a2"+(days.size()-i), "0");
			result.put("a3"+(days.size()-i), "0");
			result.put("a4"+(days.size()-i), "0");
			result.put("a5"+(days.size()-i), "0");
			
			for (int j = 0; j < rows.length(); j++) {
				JSONObject temp = (JSONObject)rows.get(j);
				if(rttime.equals(temp.get("CREATE_TIME"))) {
					result.put("t"+(days.size()-i), rttime.substring(8, 10));
					result.put("a1"+(days.size()-i), temp.get("A1"));
					result.put("a2"+(days.size()-i), temp.get("A2"));
					result.put("a3"+(days.size()-i), temp.get("A3"));
					result.put("a4"+(days.size()-i), temp.get("A4"));
					result.put("a5"+(days.size()-i), temp.get("A5"));
					break;
				}
				
			}
		}
		ReturnWriter(result.toString());
	}

	
	private List getMonthDays() {
		List list = new ArrayList();
		
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");  
        Calendar calc =Calendar.getInstance();  
        try {  
            calc.setTime(sdf.parse(SysUtility.getSysDateWithoutTime()));
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
            calc.add(calc.DATE, -1);  
            list.add(sdf.format(calc.getTime()));
        } catch (ParseException e1) {  
            e1.printStackTrace();  
        } 
        return list;
	}
}