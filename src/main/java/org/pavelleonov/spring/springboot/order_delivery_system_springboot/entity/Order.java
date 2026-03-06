package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.converters.DurationConverter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int orderId;

    @Column(name = "price")
    private int price;

    @Column(name = "is_delivery_free")
    private boolean isDeliveryFree;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "commentary")
    private String commentary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "estimated_delivery_time")
    @Convert(converter = DurationConverter.class)
    private Duration estimatedDeliveryTime;

    @Column(name = "are_bonuses_used")
    private boolean areBonusesUsed;

    @Column(name = "is_promo_code_used")
    private boolean isPromoCodeUsed;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne

    @JoinColumn(name = "client_address_id")
    private ClientAddress orderAddress;

    public enum OrderStatus{
        ACTIVE,
        DECLINED,
        DELIVERED,
        UNPAID
    }

}
