package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.BasicClientUpdateDTO;

import java.time.LocalDate;

@Getter
@Setter

public class ClientUpdateAdminDTO implements BasicClientUpdateDTO {

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s]+$", message = "Name should contain only letters")
    private String name;

    private Boolean isActive;

    @Past
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+\\d{11,15}$", message = "Please use format +00000000000 ")
    private String phoneNumber;

    @Email
    @Size(min = 10, max = 255)
    private String email;

    private Boolean isAdvertisable;
    private Boolean isProfileComplete;
    private Boolean isOnlineCheckOn;

    @Max(1000)
    private Integer bonusesAmount;

}
