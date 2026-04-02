package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import java.time.LocalDate;

public interface BasicClientUpdateDTO {
     String getName();
     LocalDate getDateOfBirth();
     String getPhoneNumber();
     String getEmail();
     Boolean getIsAdvertisable();
     Boolean getIsOnlineCheckOn();
}
