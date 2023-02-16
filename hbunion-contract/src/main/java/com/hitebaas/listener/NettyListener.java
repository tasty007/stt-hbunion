package com.hitebaas.listener;

import java.net.InetSocketAddress;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hitebaas.netty.NettyServer;


public class NettyListener implements ApplicationListener<ContextRefreshedEvent>{
	

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null){
            try {
            	NettyServer nettyServer = new NettyServer();
    			nettyServer.start(new InetSocketAddress(8211));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
