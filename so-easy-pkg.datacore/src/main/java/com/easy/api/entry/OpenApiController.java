package com.easy.api.entry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easy.api.utility.OpenApiUtility;
import com.easy.app.constants.ExsConstants;

@RestController
@RequestMapping(value = "/api")
public class OpenApiController {
	
	/**
	 * 请求示例地址：http://localhost:8080/api/push
	 * clientIp：客户端ip，用于监控请求合法性
	 * messageType：消息类型，唯一，由平台制定
	 * messageData：消息内容，通常是报文内容，常用为json和xml格式
	 * compress:压缩标识，0或空不压缩，1：表示开启参数为zip压缩模式
	 * */
	@RequestMapping("push")
	public String push(String clientIp,String messageType,String messageData,String compress) {
		return OpenApiUtility.apiInvoking(clientIp, messageType, messageData, compress, ExsConstants.push);
	}
	
	/**
	 * 请求示例地址：http://localhost:8080/api/pull
	 * clientIp：客户端ip，用于监控请求合法性
	 * messageType：消息类型，唯一，由平台制定
	 * messageData：消息内容，通常是报文内容，常用为json和xml格式
	 * compress:压缩标识，0或空不压缩，1：表示开启参数为zip压缩模式
	 * */
	@GetMapping(value = "/pull")
	public String pull(String clientIp,String messageType,String messageData,String compress) {
		return OpenApiUtility.apiInvoking(clientIp, messageType, messageData, compress, ExsConstants.pull);
	}
	
	/**
	 * 请求示例地址：http://localhost:8080/api/destory
	 * clientIp：客户端ip，用于监控请求合法性
	 * messageType：消息类型，唯一，由平台制定
	 * messageData：消息内容，通常是报文内容，常用为json和xml格式
	 * compress:压缩标识，0或空不压缩，1：表示开启参数为zip压缩模式
	 * */
	@GetMapping(value = "/destory")
	public String destory(String clientIp,String messageType,String messageData,String compress) {
		return OpenApiUtility.apiInvoking(clientIp, messageType, messageData, compress, ExsConstants.destory);
	}
	
	
}
