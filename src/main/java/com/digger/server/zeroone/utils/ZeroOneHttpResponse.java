package com.digger.server.zeroone.utils;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ZeroOneHttpResponse extends DefaultFullHttpResponse {

	public ZeroOneHttpResponse(HttpVersion version, HttpResponseStatus status) {
		super(version, status);
	}
	
}
