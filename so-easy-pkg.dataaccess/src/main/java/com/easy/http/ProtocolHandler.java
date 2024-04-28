package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public interface ProtocolHandler {
	public Response execute(Request request);

	public Response execute(Request request, NTProxy ntProxy);
}
