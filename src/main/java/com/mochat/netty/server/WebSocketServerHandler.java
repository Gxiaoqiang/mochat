package com.mochat.netty.server;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.cache.ThreadLocalDataTokenMap;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.model.RoomMessageBody;
import com.mochat.model.UserInfo;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.security.MD5Utils;
import com.sun.org.apache.bcel.internal.generic.NEW;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.JedisCluster;

/**
 * websocket 具体业务处理方法
 * 
 */

@Component
@Sharable
public class WebSocketServerHandler extends BaseWebSocketServerHandler {

	private static final Logger LOGGER = Logger.getLogger(WebSocketChildChannelHandler.class);
	
	
	private WebSocketServerHandshaker handshaker;

	private void addChannelHander(ChannelHandlerContext ctx,UserInfo userInfo) throws Exception{
		String chatType = userInfo.getChatType();
		if(StringUtils.equalsIgnoreCase(Constants.ROOM_CHAT, chatType)) {
			addRoomChannel(ctx,userInfo);
		}
		if(StringUtils.equalsIgnoreCase(Constants.RANDOM_CHAT, chatType)) {
			addRandomChannel(ctx, userInfo);
		}
	}
	
	
	
	/**
	 * 当客户端连接成功，返回个成功信息
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("用户接入"+ctx.channel().remoteAddress());
		
	}

	/**
	 * 当客户端断开连接
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("-----------"+Thread.currentThread().getName());
		closeChannelContext(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		ctx.flush();
	}

	public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		// 关闭请求
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// ping请求
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		boolean flag = validate();
		if(flag == false) {
			ctx.writeAndFlush(new TextWebSocketFrame("validate failed"));
			closeChannelContext(ctx);
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			return;
		}
		String request = ((TextWebSocketFrame) frame).text();
		if(request.contains("HeartBeat")) {
			return;
		}
		UserInfo userInfo = ThreadLocalCache.get();
		CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
 	    String chatType = userInfo.getChatType();
 	    
 	    if(Constants.ROOM_CHAT.equalsIgnoreCase(chatType)){
 	    	roomChatPush(jedisCluster, userInfo, request);
 	    }
 	    if(Constants.RANDOM_CHAT.equals(chatType)) {
 	    	randomChatPush(ctx, jedisCluster, userInfo, request);
 	    }
	}

	private boolean validate()throws Exception{
		Map<String, String> map = (Map<String, String>)ThreadLocalDataTokenMap.get();
		String data = map.get("data");
		String token = map.get("token");
		if(StringUtils.isEmpty(data)||StringUtils.isEmpty(token)) {
			return false;
		}
		if(!MD5Utils.getVerifyInstance().verifyDataToken(data,token)){
			return false;
		}
		return true;
	}
	// 第一次请求是http请求，请求头包括ws的信息
	public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
		if (!req.getDecoderResult().isSuccess()) {
			sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		if (!req.getUri().contains("websocketNetty")) {
            return;
        }
		boolean flag = validateDataAndToken(ctx, req);
		if(flag == false) {
			return;
		}
		String uri = req.getUri();
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
		Map<String, List<String>> parameters = queryStringDecoder.parameters();
	    String chatType = parameters.get("chatType").get(0);
		UserInfo userInfo = ThreadLocalCache.get();
		userInfo.setChatType(chatType);
		ThreadLocalCache.set(userInfo);
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/" + ctx.channel() + "/websocketNetty/socketServer.do", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			//WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
        addChannelHander(ctx,userInfo);
	}

	public boolean validateDataAndToken(ChannelHandlerContext ctx,FullHttpRequest request)throws Exception{
		String cookie = request.headers().get("Cookie");
		if(StringUtils.isEmpty(cookie)) {
			redirectHttp(ctx, request);
			return false;
		}
		cookie = cookie.replace("\"", "");
		String []cookies = cookie.split(";");
		String data  = getValueFromCookie(cookies, "data=");
		String token = getValueFromCookie(cookies, "token=");
		if(StringUtils.isEmpty(data)||StringUtils.isEmpty(token)) {
			redirectHttp(ctx, request);
			return false;
		}
		if(!MD5Utils.getVerifyInstance().verifyDataToken(data,token)){
			redirectHttp(ctx, request);
			return false;
		}
		UserInfo cookieUser = getUserBySecurityFactory(data);
		if (null == cookieUser) {
			redirectHttp(ctx, request);
			return false;
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data);
			map.put("token", token);
			ThreadLocalDataTokenMap.set(map);
			ThreadLocalCache.set(cookieUser);
		}
		return true;
	}
	private UserInfo getUserBySecurityFactory(String data) {
		Object dataObj = MD5Utils.getVerifyInstance().getDataEntity(data);
		if(dataObj instanceof UserInfo){
			return (UserInfo)dataObj;
		}
		return null;
	}
	
	private final  String getValueFromCookie(String []cookies, String key) {
		String cookie = null;
		if (cookies != null && cookies.length > 0) {
			int len$ = cookies.length;
			int i$ = 0;
			do {
				if (i$ >= len$)
					break;
				String cc = cookies[i$];
				if (cc.trim().startsWith(key)) {
					cookie = cc.split("=")[1];
					break;
				}
				i$++;
			} while (true);
		}
		return cookie;
	}
	 private static void send100Continue(ChannelHandlerContext ctx) {
	        FullHttpResponse response = new DefaultFullHttpResponse(
	            HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
	        ctx.writeAndFlush(response);
	}
	private void redirectHttp(ChannelHandlerContext ctx,FullHttpRequest request)throws Exception{
		File INDEX ;
	        try {
	        	
	            String path = Constants.PROJECT_PATH.get() + "login.html";
	           // path = !path.contains("file:") ? path : path.substring(5);
	             INDEX = new File(path);
	        } catch (Exception e) {
	            throw new IllegalStateException(
	                 "Unable to locate index.html", e);
	        }
		  if (HttpHeaders.is100ContinueExpected(request)) {
               send100Continue(ctx);
           }
           //读取 index.html
           RandomAccessFile file = new RandomAccessFile(INDEX, "r");
           DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
           response.headers().set(
               HttpHeaders.Names.CONTENT_TYPE,
               "text/html; charset=UTF-8");
           boolean keepAlive = HttpHeaders.isKeepAlive(request);
           //如果请求了keep-alive，则添加所需要的 HTTP 头信息
           if (keepAlive) {
               response.headers().set(
                   HttpHeaders.Names.CONTENT_LENGTH, INDEX.length());
               response.headers().set( HttpHeaders.Names.CONNECTION,
                   HttpHeaders.Values.KEEP_ALIVE);
           }
           //(3) 将 HttpResponse 写到客户端
           ctx.writeAndFlush(response);
           //(4) 将 index.html 写到客户端
           if (ctx.pipeline().get(SslHandler.class) == null) {
               ctx.write(new DefaultFileRegion(
                   file.getChannel(), 0, file.length()));
           } else {
               ctx.write(new ChunkedNioFile(file.getChannel()));
           }
           //(5) 写 LastHttpContent 并冲刷至客户端
           ChannelFuture future = ctx.writeAndFlush(
                   LastHttpContent.EMPTY_LAST_CONTENT);
           //(6) 如果没有请求keep-alive，则在写操作完成后关闭 Channel
           if (!keepAlive) {
               future.addListener(ChannelFutureListener.CLOSE);
           }
          // file.close();
	}
	public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {

		// 返回应答给客户端
		if (res.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}

		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	private static boolean isKeepAlive(FullHttpRequest req) {
		return false;
	}

	// 异常处理，netty默认是关闭channel
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		closeChannelContext(ctx);
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

}