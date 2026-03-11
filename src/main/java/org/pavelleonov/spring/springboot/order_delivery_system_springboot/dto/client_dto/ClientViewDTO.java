package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class ClientViewDTO {
    private String name;
    private String login;
}
