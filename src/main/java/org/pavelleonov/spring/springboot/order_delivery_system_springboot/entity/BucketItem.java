package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "bucket_items")
public class BucketItem {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private BucketItemId id;

    @ManyToOne
    @MapsId("bucketId")
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;
}
