package com.mochat.netty.constant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 常量
 * */
public class NettyContextManage {
    //存放所有的ChannelHandlerContext
    public static Map<String, ChannelHandlerContext> ID_CTX_MAP = new ConcurrentHashMap<String, ChannelHandlerContext>() ;
    
    
    public static final Map<ChannelHandlerContext, String> CTX_ID_MAP = new ConcurrentHashMap<ChannelHandlerContext,String>();
    
    //存放某一类的channel
    public static ChannelGroup aaChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    
    public static final Map<String, ChannelGroup> CHANNELGROUP_MAP = new ConcurrentHashMap<String,ChannelGroup>();
}