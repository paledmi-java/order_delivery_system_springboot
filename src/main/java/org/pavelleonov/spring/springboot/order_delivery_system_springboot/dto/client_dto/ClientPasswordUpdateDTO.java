package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClientPasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
}
