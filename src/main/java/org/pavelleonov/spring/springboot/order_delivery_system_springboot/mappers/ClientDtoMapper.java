package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientInfoDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoMapper {

    public ClientViewDTO toViewDto(Client client){
        return ClientViewDTO.builder()
                .login(client.getCredentials().getLogin())
                .name(client.getName()).build();
    }

    public ClientInfoDTO toInfoDto(Client client){
        return ClientInfoDTO.builder()
                .id(client.getClientId())
                .email(client.getEmail())
                .dateOfBirth(client.getDateOfBirth())
                .isActive(client.isActive())
                .bonusesAmount(client.getBonusesAmount())
                .isAdvertisable(client.isAdvertisable())
                .isOnlineCheckOn(client.isOnlineCheckOn())
                .isProfileComplete(client.isProfileComplete())
                .phoneNumber(client.getPhoneNumber())
                .name(client.getName()).build();
    }

}
