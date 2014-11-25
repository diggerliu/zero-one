package com.digger.server.zeroone;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digger.server.zeroone.handler.ZeroOneDispatcher;


@Service
public class ZeroOneChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private NioEventLoopGroup dispatcherGroup = new NioEventLoopGroup(10);
	@Autowired
	private ZeroOneDispatcher dispatcher;
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
    	 ChannelPipeline p = channel.pipeline();
    	 p.addLast(new HttpResponseEncoder());// http请求
    	 p.addLast(new HttpRequestDecoder());// http请求
    	 //p.addLast(new IdleStateHandler(3, 2, 2));
    	 p.addLast(dispatcherGroup, "cmdMapperDispatcher", dispatcher);//
    }


}
