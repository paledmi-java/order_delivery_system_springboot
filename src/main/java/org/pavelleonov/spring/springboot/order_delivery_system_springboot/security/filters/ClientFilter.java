package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClientFilter {
    @Email
    private String email;

    private String name;

    private String phone;

    private Boolean isActive;

}
