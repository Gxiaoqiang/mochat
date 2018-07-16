package com.mochat.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mochat.constant.RabbitMqConstants;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Service
public class RabbitRandomPublish extends AbstractRabbitMQ{

	
	/*private static Connection connection = null;
	private static Channel channel = null;
	private static String queueName = null;
	static {
		try {
			ConnectionFactory connectionFactory = (ConnectionFactory)MyApplicationContextListener.getBean("clientConnection");
			 Connection connection = connectionFactory.newConnection();
			 channel=connection.createChannel();
			 channel.exchangeDeclare("random-chat", "fanout");
			 queueName = channel.queueDeclare().getQueue();
			 //channel.queueDeclare(RabbitMqConstants.RANDOM_CHAT, false, false, false, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}*/
	@Autowired
	private ConnectionFactory connectionFactory;
	/*public RabbitRandomPublish() {
		try {
			 Connection connection = connectionFactory.newConnection();
			 channel=connection.createChannel();
			 channel.exchangeDeclare("random-chat", "fanout");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}*/

	public void init() {
		try {
			 Connection connection = connectionFactory.newConnection();
			 channel=connection.createChannel();
			 channel.exchangeDeclare(RabbitMqConstants.RANDOM_CHAT, "fanout");
			 
			 channel.exchangeDeclare(RabbitMqConstants.ROOM_CHAT, "fanout");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

	@Override
	public void bind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(String type,String msgContent) {
		// TODO Auto-generated method stub
		try {
			if(type.equals("R")) {
				channel.basicPublish(RabbitMqConstants.RANDOM_CHAT, "", null, msgContent.getBytes("UTF-8"));
			}
			if(type.equals("M")) {
				channel.basicPublish(RabbitMqConstants.ROOM_CHAT, "", null, msgContent.getBytes("UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
