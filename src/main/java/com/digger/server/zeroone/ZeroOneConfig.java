package com.digger.server.zeroone;

import org.springframework.stereotype.Service;

import com.digger.server.zeroone.annotation.Config;


@Service
public class ZeroOneConfig {
	@Config
	public int port;

	

}
