package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.converters.DurationConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

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
    private OrderStatus status = OrderStatus.ACTIVE;

    @Column(name = "commentary")
    private String commentary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delivered_at", updatable = false)
    private LocalDateTime deliveredAt = LocalDateTime.now()
            .plusMinutes(30 + (int)(Math.random() * 60));

    @Column(name = "are_bonuses_used")
    private boolean areBonusesUsed;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne
    @JoinColumn(name = "client_address_id")
    private ClientAddress orderAddress;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum OrderStatus{
        ACTIVE,
        DECLINED,
        DELIVERED,
        UNPAID
    }

    public void addOrderItemToOrder(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}
