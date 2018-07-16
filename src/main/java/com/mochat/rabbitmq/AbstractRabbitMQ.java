package com.mochat.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public abstract class AbstractRabbitMQ {

	public  Connection connection = null;
	public  Channel channel = null;
	/**
	 * 关闭连接
	 */
	public void close() {
			try {
				if(channel != null) {
				  channel.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * 绑定监听
	 */
	public abstract void bind();
	
	public abstract void init();
	/**
	 * 发送消息
	 */
	public abstract void send(String type,String msgContent);
}
