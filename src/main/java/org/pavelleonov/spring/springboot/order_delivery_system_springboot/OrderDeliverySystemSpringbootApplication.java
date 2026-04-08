package org.pavelleonov.spring.springboot.order_delivery_system_springboot;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableConfigurationProperties(JwtProperties.class)
@ConfigurationPropertiesScan
public class OrderDeliverySystemSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderDeliverySystemSpringbootApplication.class, args);
    }
}
