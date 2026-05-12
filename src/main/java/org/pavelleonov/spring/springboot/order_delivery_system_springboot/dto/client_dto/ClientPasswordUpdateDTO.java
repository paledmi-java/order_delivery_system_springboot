package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClientPasswordUpdateDTO {

    @NotNull
    private String oldPassword;

    @NotNull
    @Size(min = 10, max = 40)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least 1 letter and 1 digit")
    private String newPassword;
}
