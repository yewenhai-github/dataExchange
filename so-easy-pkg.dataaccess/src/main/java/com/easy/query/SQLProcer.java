package com.easy.query;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;

/**
 * .spring
 * 
 * @author yewh 2017-06-06
 * 
 * @version 7.0.0
 * 
 */
public class SQLProcer {

	private JSONObject datas;
	private Hashtable<String,Object> evns;
	private Hashtable<String,Object> parm;
	
	public SQLProcer(String sql, ArrayList<String> parms,
			Hashtable<String, Object> EnvDatas, JSONObject FormDatas) throws LegendException{
		
		evns = EnvDatas;
		datas = FormDatas;
		parm = new Hashtable<String,Object>();
		//参数化
		Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\u4e00-\\u9fa5]*)\\.([a-zA-Z0-9_\\u4e00-\\u9fa5]*)");
		Matcher matcher = pattern.matcher(sql);
		StringBuffer sqls = new StringBuffer();
		while(matcher.find())
		{
			String rp = Replace(matcher);
			if(rp.startsWith("@")){
				matcher.appendReplacement(sqls,"?");
			}
			else{
				matcher.appendReplacement(sqls,rp);
			}
		}
		matcher.appendTail(sqls);
		SQL = sqls.toString();
		sqls.setLength(0);
		
		pattern = Pattern.compile("@([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)");
		matcher = pattern.matcher(SQL);
		while(matcher.find())
		{
			String rp = Replace(matcher);
			if(rp.startsWith("@")){
				matcher.appendReplacement(sqls,"?");
			}
			else{
				matcher.appendReplacement(sqls,rp);
			}
		}
		matcher.appendTail(sqls);
		SQL = sqls.toString();
		sqls.setLength(0);

		pattern = Pattern.compile("#([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)");
		matcher = pattern.matcher(SQL);
		while(matcher.find())
		{
			String rp = Replace(matcher);
			matcher.appendReplacement(sqls,rp);
		}
		matcher.appendTail(sqls);
		SQL = sqls.toString();
		sqls.setLength(0);
		
		//替换
		StringBuffer repsqls = new StringBuffer();

		pattern = Pattern.compile("@([a-zA-Z0-9_\\u4e00-\\u9fa5]*)\\.([a-zA-Z0-9_\\u4e00-\\u9fa5]*)");
		matcher = pattern.matcher(sql);
		while(matcher.find())
		{
			String rp = Replace(matcher);
			if(rp.startsWith("@")){
				matcher.appendReplacement(repsqls, (String)parm.get(rp));
			}
			else{
				matcher.appendReplacement(repsqls, rp);
			}
		}
		matcher.appendTail(repsqls);
		ReplacedSQL = repsqls.toString();
		repsqls.setLength(0);
		
		pattern = Pattern.compile("@([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)");
		matcher = pattern.matcher(ReplacedSQL);
		while(matcher.find())
		{
			String rp = Replace(matcher);
			if(rp.startsWith("@")){
				matcher.appendReplacement(repsqls,(String)parm.get(rp));
			}
			else{
				matcher.appendReplacement(repsqls, rp);				
			}
		}
		matcher.appendTail(repsqls);
		ReplacedSQL = repsqls.toString();
		repsqls.setLength(0);

		pattern = Pattern.compile("#([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)");
		matcher = pattern.matcher(ReplacedSQL);
		while(matcher.find())
		{
			String rp = Replace(matcher);
			matcher.appendReplacement(repsqls, rp);				
		}
		matcher.appendTail(repsqls);
		ReplacedSQL = repsqls.toString();
		repsqls.setLength(0);
		Enumeration<String> pkeys = parm.keys();
		
		while(pkeys.hasMoreElements()){
			String k = pkeys.nextElement();
			parms.add((String)parm.get(k));
		}
		
        //SQL = System.Text.RegularExpressions.Regex.Replace(SQL, "@([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)", new System.Text.RegularExpressions.MatchEvaluator(Replace));
        //SQL = System.Text.RegularExpressions.Regex.Replace(SQL, "#([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)", new System.Text.RegularExpressions.MatchEvaluator(Replace));
        //ReplacedSQL = System.Text.RegularExpressions.Regex.Replace(SQL.Replace("@","#"), "#([a-zA-Z0-9_$\\u4e00-\\u9fa5]*)", new System.Text.RegularExpressions.MatchEvaluator(Replace));
	}
	
	private String Replace(Matcher m) throws LegendException{
		String rp = "";
		try {
		if(m.groupCount() == 2){
			//Table.Field模式
			String tn = m.group(1);
			String fn = m.group(2);
			if (datas != null)
            {
				JSONArray rows;
					rows = datas.getJSONArray(tn);
                if (rows !=null)
                {
                	if(rows.length()>0)
                	{
                		JSONObject r = rows.getJSONObject(0);
                		String fv = r.getString(fn);
                		if(fv!=null){
                			rp = "@" + tn + "_" + fn;
                			if(!parm.containsKey(rp)){
                				parm.put(rp, fv);
                			}
                		}
                		else
                		{
                			rp = "NULL";
                		}
                    }
                    else
                    {
                        rp = "NULL";
                    }
                }
                else
                {
                    rp = "NULL";
                }
            }
            else
            {
                rp = "NULL";
            }
		}
		else{
			if(m.group(0).startsWith("@")){
				//@Var模式
				 if (evns.containsKey(m.group(1)))
                 {
                     rp = m.group(0);
                     
         			if(!parm.containsKey(rp)){
        				parm.put(rp, evns.get(m.group(1)));
        			}

                 }
                 else
                 {
                     rp = "NULL";
                 }
			}
			if(m.group(0).startsWith("#")){
				//#var替换模式
				if (evns.containsKey(m.group(1)))
                {
                    rp = (String)evns.get(m.group(1));
                }
                else
                {
                    rp = "";
                }
			}
		}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} 

		return rp;
	}
	
	 public String ReplacedSQL;
     public String SQL;

}
