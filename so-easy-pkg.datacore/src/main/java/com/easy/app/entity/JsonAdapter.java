package com.easy.app.entity;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.json.JSONObject;

import com.easy.utility.SysUtility;
 
/**
 * 对Map类型做转换的类和适配器类
 *
 */
public class JsonAdapter extends XmlAdapter<MapConvertor, JSONObject> {
	@SuppressWarnings("unchecked")
	@Override
	public MapConvertor marshal(JSONObject obj) throws Exception {
		Map<String, Object> map = SysUtility.JSONObjectToHashMap(obj);
		MapConvertor convertor = new MapConvertor();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);
            convertor.addEntry(e);
        }
        return convertor;
	}

	@Override
	public JSONObject unmarshal(MapConvertor map) throws Exception {
		HashMap<String, Object> result = new HashMap<String, Object>();
        for (MapConvertor.MapEntry e : map.getEntries()) {
            result.put(e.getKey(), e.getValue());
        }
        
        JSONObject jsonresult = SysUtility.MapToJSONObject(result);
        return jsonresult;
	}
}