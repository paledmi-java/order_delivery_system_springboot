package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClientAddressRequestDto (
        @NotNull
        Boolean isDefault,

        @NotBlank
        @Size(min = 2, max = 50)
        String city,

        @NotBlank
        @Size(min = 2, max = 50)
        String street,

        @NotBlank
        @Size(min = 2, max = 50)
        String houseNumber,

        @NotBlank
        @Size(min = 2, max = 50)
        String apartment,

        @NotBlank
        @Pattern(regexp = "\\d{4,10}")
        String postalCode
){}
