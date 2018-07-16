package com.mochat.rabbitmq;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mochat.constant.RabbitMqConstants;
import com.mochat.threadPool.NettyRandomThreadPool;
import com.mochat.threadPool.RandomThreadPool;
import com.mochat.threadPool.RoomThreadPool;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

@Service
public class RabbitRandomComsumer extends AbstractRabbitMQ{

	private static final Logger LOGGER = Logger.getLogger(RabbitRandomComsumer.class);
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
		     channel.queueBind(queueName, "random-chat", "");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}*/
	@Autowired
	private ConnectionFactory connectionFactory;
	private   String queueName = null; 
	private String roomQueue = null;
	/*public  RabbitRandomComsumer() {
		try {
			 Connection connection = connectionFactory.newConnection();
			 channel=connection.createChannel();
			 channel.exchangeDeclare(RabbitMqConstants.RANDOM_CHAT, "fanout");
			 queueName = channel.queueDeclare().getQueue();
		     channel.queueBind(queueName, RabbitMqConstants.RANDOM_CHAT, "");
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
			 
			 queueName = channel.queueDeclare().getQueue();
		     channel.queueBind(queueName, RabbitMqConstants.RANDOM_CHAT, "");
		     
		     roomQueue = channel.queueDeclare().getQueue();
		     channel.queueBind(roomQueue, RabbitMqConstants.ROOM_CHAT, "");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public  void  bind() {
		try {
			//监听消息
		    Consumer consumer = new DefaultConsumer(channel) {
		      @Override
		      public void handleDelivery(String consumerTag, Envelope envelope,
		                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
		        String message = new String(body, "UTF-8");
		        LOGGER.info("rabbitmq-------"+message);
		        //NettySendMsgThreadPool.send(message);
		        NettyRandomThreadPool.send(message);
		      }
		    };
		    //应答消息
		    channel.basicConsume(queueName, true, consumer);
		    
		    
		  //监听消息
		    Consumer consumerRoom = new DefaultConsumer(channel) {
		      @Override
		      public void handleDelivery(String consumerTag, Envelope envelope,
		                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
		        String message = new String(body, "UTF-8");
		        LOGGER.info("rabbitmq-------"+message);
		        //NettySendMsgThreadPool.send(message);
		        RoomThreadPool.send(message);
		      }
		    };
		    //应答消息
		    channel.basicConsume(roomQueue, true, consumerRoom);
		    
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


	@Override
	public void send(String type,String msgContent) {
		// TODO Auto-generated method stub
		
	}
}
