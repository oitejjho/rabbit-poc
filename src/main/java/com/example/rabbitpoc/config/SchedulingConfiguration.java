package com.example.rabbitpoc.config;

import com.example.rabbitpoc.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfiguration.class);

    private final RabbitTemplate testRabbitTemplate;

    public SchedulingConfiguration(RabbitTemplate testRabbitTemplate) {
        this.testRabbitTemplate = testRabbitTemplate;
    }


    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void publish() {
        testRabbitTemplate.convertAndSend(new Message());
        log.info("Message published (Queue: original.queue)");
    }
}
