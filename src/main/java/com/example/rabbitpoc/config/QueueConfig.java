package com.example.rabbitpoc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueConfig {

    public static final String ORIGINAL_QUEUE = "original.queue";
    public static final String ORIGINAL_QUEUE_ROUTING_KEY = "original.queue.key";


    public static final String DEAD_LETTER_QUEUE = "dlq.queue";

//    public static final String PARKING_LOG_QUEUE = "plq.queue";

    public static final String TOPIC_EXCHANGE = "example.exchange";
    private static final Logger log = LoggerFactory.getLogger(QueueConfig.class);
    private final ConnectionFactory cachingConnectionFactory;

    public QueueConfig(ConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(this.cachingConnectionFactory);
        rabbitAdmin.declareExchange(this.exampleExchange());
        return rabbitAdmin;
    }

    @Bean
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(this.messageConverter());
        factory.setErrorHandler(throwable -> log.info("Listener failed - message rerouted to dlq (Queue: {})", DEAD_LETTER_QUEUE));
        factory.setDefaultRequeueRejected(false);
        simpleRabbitListenerContainerFactoryConfigurer.configure(factory, this.cachingConnectionFactory);

        return factory;
    }

    @Bean
    public RabbitTemplate simpleRabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(this.cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(this.messageConverter());
        rabbitTemplate.setExchange(TOPIC_EXCHANGE);
        rabbitTemplate.setRoutingKey(ORIGINAL_QUEUE_ROUTING_KEY);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange exampleExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue originalQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE);
        Queue queue = new Queue(ORIGINAL_QUEUE, true, false, false, arguments);
        queue.setAdminsThatShouldDeclare(rabbitAdmin());
        rabbitAdmin().declareQueue(queue);
        rabbitAdmin().declareBinding(BindingBuilder.bind(queue).to(exampleExchange()).with(ORIGINAL_QUEUE_ROUTING_KEY));
        return queue;
    }

    @Bean
    public Queue dlQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-queue-mode", "lazy");
        Queue queue = new Queue(DEAD_LETTER_QUEUE, true, false, false, arguments);
        queue.setAdminsThatShouldDeclare(rabbitAdmin());
        rabbitAdmin().declareQueue(queue);
        return queue;
    }

}
