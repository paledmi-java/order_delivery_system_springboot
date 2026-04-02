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
@Table(name = "favourite_items")
public class FavouriteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @Column(name = "priority")
    private int priority;

    @Column(name = "times_ordered")
    private int timesOrdered;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
