package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class ClientCreateDTO {
    @NotBlank(message = "Name can not be empty")
    private String name;
    @NotBlank(message = "Phone number can not be empty")
    private String phoneNumber;
    @NotBlank(message = "Login can not be empty")
    private String login;
    @NotBlank(message = "Password can not be empty")
    private String password;
    @Email(message = "Wrong email format")
    @NotBlank(message = "Email can not be empty")
    private String email;
}
