package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class ClientUpdateSelfDTO implements BasicClientUpdateDTO{

    private String name;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;

    private Boolean isAdvertisable;
    private Boolean isOnlineCheckOn;

}
