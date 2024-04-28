package com.easy.api.test;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easy.api.entry.OpenApiController;
import com.easy.exception.LegendException;

@RestController
@RequestMapping(value = "/api/test")
public class TestController {
	
	@Autowired
    private OpenApiController restfulController;
	
	//请求示例地址：http://localhost:8080/api/test/push
	@RequestMapping("push")
	public String pushtest() throws JSONException, LegendException, UnsupportedEncodingException {
		return restfulController.push("192.168.1.100", "EEntDeclIo", TestUtility.getPushJsonData(), "0");
	}
	
	//请求示例地址：http://localhost:8080/api/test/push
	@RequestMapping("pull")
	public String pulltest() throws JSONException, LegendException, UnsupportedEncodingException {
		return restfulController.pull("192.168.1.100", "EEntDeclIo", TestUtility.getPullJsonData(), "0");
	}
	
	//请求示例地址：http://localhost:8080/api/test/push2
	//update itf_dcl_receipts s set s.message_count = 0 where decl_no = '27b7c399134f4a1ebb44759b02220655';
	@RequestMapping("pull2")
	public String pulltest2() throws JSONException, LegendException, UnsupportedEncodingException {
		return restfulController.pull("192.168.1.100", "EEntDeclIo", TestUtility.getPullJsonData2(), "0");
	}
		
	//请求示例地址：http://localhost:8080/api/test/destory
	@RequestMapping("destory")
	public String destorytest() throws JSONException, LegendException, UnsupportedEncodingException {
		return restfulController.destory("192.168.1.100", "EEntDeclIo", TestUtility.getDestoryJsonData(), "0");
	}
}
