package com.digger.server.zeroone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.digger.server.zeroone.handler.ZeroOneMappers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Service
public class ZeroOne {
	
	@Autowired
	private ZeroOneChannelInitializer zeroOneChannelInitializer;
	@Autowired
	private ZeroOneConfig config;
	
	@Autowired
	private ZeroOneMappers zeroOneMappers;
	public static ApplicationContext context ;
	
	private static final Logger log = LoggerFactory.getLogger(ZeroOne.class);
	private static final String[] ZERO_ONE_DEFAULT_CONFIG_XML = {"classpath:zero-one-app.xml"};
	
	public static void launch(String... path){
		if(path==null){
			ZeroOne.context = new ClassPathXmlApplicationContext(ZERO_ONE_DEFAULT_CONFIG_XML);
		}else{
			ZeroOne.context = new ClassPathXmlApplicationContext(concat(ZERO_ONE_DEFAULT_CONFIG_XML,path));
		}
		context.getBean(ZeroOne.class).zeroOneMappers.init();
    	try {
			context.getBean(ZeroOne.class).initServerBootstrap();
		} catch (BeansException e) {
			log.error("{}",e);
		} catch (InterruptedException e) {
			log.error("{}",e);
		}
	}
	
	
	private static String[] concat(String[] first, String[] second) {
		String[] c = new String[first.length + second.length];
		System.arraycopy(first, 0, c, 0, first.length);
		System.arraycopy(second, 0, c, first.length, second.length);
		return c;
	}
	
    private void initServerBootstrap() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
        // 使用NioServerSocketChannel作为服务Channel
                .channel(NioServerSocketChannel.class)//

                // hander
                .childHandler(zeroOneChannelInitializer);
        try {
        	log.error("port:{}",config.port);
            serverBootstrap.bind(config.port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws BeansException, InterruptedException {
    	launch();
	}
    
    

}
