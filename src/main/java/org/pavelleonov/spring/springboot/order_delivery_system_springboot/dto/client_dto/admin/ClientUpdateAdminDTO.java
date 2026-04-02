package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.BasicClientUpdateDTO;

import java.time.LocalDate;

@Getter
@Setter

public class ClientUpdateAdminDTO implements BasicClientUpdateDTO {
    private String name;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private Boolean isAdvertisable;
    private Boolean isProfileComplete;
    private Boolean isOnlineCheckOn;
    private Integer bonusesAmount;

}
