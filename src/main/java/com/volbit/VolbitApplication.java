package com.volbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication


@EnableScheduling                 // turn on @Scheduled
@EnableWebSocketMessageBroker     // turn on STOMP/WebSocket support
public class VolbitApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolbitApplication.class, args);
    }

}
