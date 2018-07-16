package com.mochat.rabbitmq;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
/**
 * @Description:消息生产�?
 * @Author:
 *@CreateTime:
 */
//@Service
public class RabbitMQProducer {

    @Resource
    private AmqpTemplate amqpTemplate;

    public void sendMessage(String queueKey,Object message){
        amqpTemplate.convertAndSend(queueKey,message);//testKey为配置文件中queue对应的key,指明发�?�给哪个queue�?
    }
}