package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class ClientUpdateSelfDTO implements BasicClientUpdateDTO{

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s]+$", message = "Name should contain only letters")
    private String name;

    @Past
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "Please use format +00000000000 ")
    private String phoneNumber;

    @Email
    @Size(max = 255)
    private String email;

    private Boolean isAdvertisable;
    private Boolean isOnlineCheckOn;
}
