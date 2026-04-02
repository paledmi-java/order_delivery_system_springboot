package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "client_addresses")
public class ClientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "apartment")
    private String apartment;

    @Column(name = "postal_code")
    private String postal_code;

    @Column(name = "calculated_delivery_time")
    private LocalDateTime calcDeliveryTime;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
