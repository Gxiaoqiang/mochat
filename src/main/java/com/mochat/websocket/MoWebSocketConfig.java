package com.mochat.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/*@Configuration
@EnableWebMvc
@EnableWebSocket*/
/*public class MoWebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
   
	@Autowired
	private MoHandshakeInterceptor moHandshakeInterceptor;
	
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(),"/websocket/socketServer.do").addInterceptors(moHandshakeInterceptor);
        registry.addHandler(webSocketHandler(), "/sockjs/socketServer.do").addInterceptors(moHandshakeInterceptor).withSockJS();
    }
 
    @Bean
    public MoChatWebSocketHandler webSocketHandler(){
        return new MoChatWebSocketHandler();
    }

}*/