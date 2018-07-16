package com.mochat.websocket;

//@ServerEndpoint(value = "/websocket")
public class ChatServer {
/*
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Vector<Session> room = new Vector<Session>();
	
	
	*//**
	 * 锟矫伙拷锟斤拷锟斤拷
	 * @param session 锟斤拷选
	 *//*
	@OnOpen
	public void onOpen(Session session){
		System.out.println(session.getUserProperties().get(arg0));
		room.addElement(session);
		session.hashCode();
	}
	@OnOpen
	public void onOpen(Session session,EndpointConfig config) {
	        HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            System.out.println( httpSession.getAttribute("name"));
            
            HandshakeRequest request = (HandshakeRequest) config.getUserProperties().get(HandshakeRequest.class.getName());
            List<String> ips = request.getHeaders().get("x-forwarded-for");
    		if (ips == null || ips.size() == 0 ) {
    			ips = request.getHeaders().get("Proxy-Client-ips");
    		}
    		if (ips == null || ips.size() == 0) {
    			ips = request.getHeaders().get("WL-Proxy-Client-ips");
    		}
    		if (ips == null || ips.size() == 0 ) {
    			ips = request.getHeaders().get("HTTP_CLIENT_ips");
    		}
    		if (ips == null || ips.size() == 0) {
    			ips = request.getHeaders().get("HTTP_X_FORWARDED_FOR");
    		}
	        //sessionMap.put(session.getId(), session);
	        room.addElement(session);
	    }
	
	*//**
	 * 锟斤拷锟秸碉拷锟斤拷锟斤拷锟矫伙拷锟斤拷锟斤拷息
	 * @param message
	 * @param session
	 *//*
	@OnMessage
	public void onMessage(String message,Session session,String ip){

		
		//锟斤拷锟矫伙拷锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷为JSON锟斤拷锟斤拷
		JSONObject obj = JSONObject.parseObject(message);
		//锟斤拷JSON锟斤拷锟斤拷锟斤拷锟斤拷臃锟斤拷锟绞憋拷锟�
		obj.put("date", df.format(new Date()));
		//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟叫碉拷锟斤拷锟叫会话
		for(Session se : room){
			//锟斤拷锟斤拷锟斤拷息锟角凤拷为锟皆硷拷锟斤拷
			obj.put("isSelf", se.equals(session));
			//锟斤拷锟斤拷锟斤拷息锟斤拷远锟斤拷锟矫伙拷
			se.getAsyncRemote().sendText(obj.toString());
		}
	}
	
	*//**
	 * 锟矫伙拷锟较匡拷
	 * @param session
	 *//*
	@OnClose
	public void onClose(Session session){
		room.remove(session);
	}
	
	*//**
	 * 锟矫伙拷锟斤拷锟斤拷锟届常
	 * @param t
	 *//*
	@OnError
	public void onError(Throwable t){
		
	}*/
}
