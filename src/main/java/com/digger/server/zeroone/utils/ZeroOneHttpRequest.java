package com.digger.server.zeroone.utils;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.NotEnoughDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroOneHttpRequest extends DefaultHttpRequest {
	private static final Logger log = LoggerFactory
			.getLogger(ZeroOneHttpRequest.class);

	private QueryStringDecoder queryStringDecoder ;
	public ZeroOneHttpRequest(HttpRequest request) {
		super(request.getProtocolVersion(), request.getMethod(), request.getUri());
		this.headers().add(request.headers());
		this.queryStringDecoder = new QueryStringDecoder(request.getUri());
		this.path = queryStringDecoder.path();
		if(request.getMethod().equals(HttpMethod.GET)){
			this.params = queryStringDecoder.parameters();
		}else if(request.getMethod().equals(HttpMethod.POST)){
			HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(request);
			this.params = initParametersByPost(httpPostRequestDecoder);
		}else{
			this.params = Collections.EMPTY_MAP;
		}
	}
	
	
	private Map<String, List<String>> initParametersByPost(HttpPostRequestDecoder httpPostRequestDecoder) {
		Map<String, List<String>> params = new HashMap<String, List<String>>();
        if (httpPostRequestDecoder == null) {
            return params;
        }
        try {
            List<InterfaceHttpData> datas = httpPostRequestDecoder.getBodyHttpDatas();
            if (datas != null) {
                for (InterfaceHttpData data : datas) {
                    if (data instanceof Attribute) {
                        Attribute attribute = (Attribute) data;
                        try {
                            String key = attribute.getName();
                            String value = attribute.getValue();

                            List<String> ori = params.get(key);
                            if (ori == null) {
                                ori = new ArrayList<String>(1);
                                params.put(key, ori);
                            }
                            ori.add(value);
                        } catch (IOException e) {
                            log.error("cant init attribute,req:{},attribute:{}", new Object[] {
                                this,
                                attribute,
                                e
                            });
                        }
                    }
                }
            }
        } catch (NotEnoughDataDecoderException e) {
            log.error("req:{}", this, e);
        }
        return params;
    }
	
	
	

	private Map<String, List<String>>  params;
	
	
	public String getString(String key){
		List<String> value_list = params.get(key);
		if(value_list!=null&&value_list.size()>0){
			return value_list.get(1);
		}
		return null;
	}
	private String path;

	public Map<String, List<String>> getParams() {
		return params;
	}

	public String getPath() {
		return path;
	}

	private SocketAddress localAddress;
	private String localIP;
	private SocketAddress remoteAddress;
	private String remoteIP;

	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(SocketAddress localAddress) {
		this.localAddress = localAddress;
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}

	public static Logger getLog() {
		return log;
	}

}
