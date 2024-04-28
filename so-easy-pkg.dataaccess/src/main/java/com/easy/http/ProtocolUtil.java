package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class ProtocolUtil {

	public static synchronized Response execute(Request request) {
		return execute(request, null);
	}

	public static synchronized Response execute(Request request, NTProxy ntProxy) {
		// 判断http或是https 协议形式
		if (request.getUrl().substring(0, request.getUrl().indexOf(":")).equalsIgnoreCase(ProtocolConstant.ProtocolType.HTTP.getValue())) {
			return HttpProtocolHandler.getInstance().execute(request, ntProxy);
		} else if (request.getUrl().substring(0, request.getUrl().indexOf(":")).equalsIgnoreCase(ProtocolConstant.ProtocolType.HTTPS.getValue())) {
			return HttpsProtocolHandler.getInstance().execute(request, ntProxy);
		} else {
			Response response = new Response();
			response.setSuccess(false);
			response.setFailureMessage("请求地址" + request.getUrl() + "格式不正确(请参照格式：http://xxx.xxx.xxx)！");
			return response;
		}
	}
}
