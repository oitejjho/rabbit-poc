package com.example.rabbitpoc;

import com.example.rabbitpoc.config.SchedulingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Listener {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfiguration.class);

    @RabbitListener(queues = "original.queue", containerFactory = "simpleRabbitListenerContainerFactory")
    public void originalQueueReceive(Message queueMessage) {
        log.info("Start receiving and processing authentication log event queue message from original queue message {}", queueMessage.getBody().toString());
        int value = new Random().nextInt(10 + 1 - 1) + 1;
        if (value == 5) {
            throw new RuntimeException("Processing failed in original queue ...");
        }
    }

    @RabbitListener(queues = "dlq.queue", containerFactory = "simpleRabbitListenerContainerFactory")
    public void dlQReceive(Message queueMessage) {
        log.info("Start receiving and processing authentication log event queue message from dl queue message {}", queueMessage.getBody().toString());
    }
}
