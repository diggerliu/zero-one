package com.digger.server.zeroone.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digger.server.zeroone.handler.ZeroOneMappers.Meta;
import com.digger.server.zeroone.utils.ZeroOneHttpRequest;

/**
 * @author digger
 */
@Sharable
@Service
public class ZeroOneDispatcher extends SimpleChannelInboundHandler<HttpRequest> {

	@Autowired
	private ZeroOneMappers zeroOneMappers;

	/*
	 * public void init() { cmdMappers.initCmdAllMap();
	 * cmdMappers.initCmdMapperDefinedMap(); cmdMappers.initConfigMap(); //
	 * cmdMappers.resetCmdConfig(); }
	 */

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest request)
			throws Exception {
		// DefaultHttpRequest request = (DefaultHttpRequest) msg;
		ZeroOneHttpRequest req = new ZeroOneHttpRequest(request);
		
		String path = req.getPath();
		System.out.println(path);
		Meta meta = zeroOneMappers.mappers.get(path);
		if(meta==null){
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
			ctx.writeAndFlush(response);
			return;
		}
		//这里耗时过长，会导致线程阻塞
		Object content = meta.method.invoke(meta.commond);
		if(content==null){
			content = new Object();
		}
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(content.toString().getBytes()));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
		response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
				response.content().readableBytes());
		response.headers().set(HttpHeaders.Names.CONNECTION, Values.KEEP_ALIVE);
		ctx.writeAndFlush(response);
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("channel active");
        ctx.fireChannelActive();
    }
    
}