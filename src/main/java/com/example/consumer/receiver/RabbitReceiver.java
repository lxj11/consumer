package com.example.consumer.receiver;

import com.alibaba.fastjson.JSONObject;
import com.example.consumer.entity.Order;
import com.example.consumer.utils.JsonConvertUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.rabbitmq.client.Channel;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author LXJ
 * @description
 * @date 2020/3/2 20:36
 */
@Component
public class RabbitReceiver {

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "order-queue",durable = "true"),
//            exchange = @Exchange(name = "order-exchange",durable = "true",type = "topic"),
//            key = "order.*"
//    ))
//    @RabbitHandler
//    public void onOrderMessage(@Payload JSONObject object,
//                               @Headers Map<String,Object> headers,
//                               Channel channel) throws Exception{
//        Order order = JsonConvertUtils.convertJSONToObject(object);
//        //消费者操作
//        System.err.println("------收到消息-------");
//        System.err.println("订单id"+order.getId());
//        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
//        //Ack手动确认
//        channel.basicAck(deliveryTag,false);
//
//    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1",
                    durable = "true"),
            exchange = @Exchange(value = "exchange-1",
                    durable = "true",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"
            ),
            key = "springboot.*"
        )
    )
    @RabbitHandler
    public void onMessage(Message message,Channel channel) throws Exception{
        System.err.println("---------------------------------");
        System.err.println("消费端： "+message.getPayload());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag,false);
    }

    /**
     * order:
     * queue:
     * name: queue-2
     * durable: true
     * exchange:
     * name: exchange-1
     * durable: true
     * type: topic
     * ignoreDeclarationExceptions: true
     * key: springboot.*
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"
            ),
            key = "${spring.rabbitmq.listener.order.key}"
    )
    )
    @RabbitHandler
    public void onOrderMessage(@Payload JSONObject jsonObject, Channel channel, @Headers Map<String, Object> headers) throws Exception {
        if(true){
            throw  new Exception();
        }
        Order order = JSONObject.toJavaObject(jsonObject, Order.class);
        System.err.println("----------------------------------");
        System.err.println("消费端order: " + order.getId() + " " + order.getName());
        java.lang.String deliveryTag1 = AmqpHeaders.DELIVERY_TAG;
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
//        channel.basicAck(deliveryTag, false);
        //消息被 nack 后一直重新入队列然后一直重新消费
        channel.basicNack(deliveryTag,false,true);
        //拒绝消息
//        channel.basicReject(deliveryTag,false);

    }
}
