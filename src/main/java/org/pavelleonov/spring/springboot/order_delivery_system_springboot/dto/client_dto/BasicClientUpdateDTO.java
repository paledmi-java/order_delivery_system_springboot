package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public interface BasicClientUpdateDTO {

     @Size(min = 2, max = 50)
     String getName();

     @Past
     LocalDate getDateOfBirth();

     @Size(min = 2, max = 50)
     @Pattern(regexp = "")
     String getPhoneNumber();

     @Size(min = 2, max = 50)
     @Email
     String getEmail();

     Boolean getIsAdvertisable();
     Boolean getIsOnlineCheckOn();
}
