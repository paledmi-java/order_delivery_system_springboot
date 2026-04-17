package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class ClientCreateDTO {

    @NotBlank(message = "Name can not be empty")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s]+$", message = "Name should contain only letters")
    private String name;

    @NotBlank(message = "Phone number can not be empty")
    @Pattern(regexp = "^\\+\\d{11,15}$", message = "Please use format +00000000000 ")
    private String phoneNumber;

    @NotBlank(message = "Login can not be empty")
    @Size(min = 6, max = 50)
    private String login;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 10, max = 40)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]$",
            message = "Password must contain at least 1 letter and 1 digit")
    private String password;

    @Email(message = "Wrong email format")
    @NotBlank(message = "Email can not be empty")
    @Size(min = 10, max = 255)
    private String email;
}
