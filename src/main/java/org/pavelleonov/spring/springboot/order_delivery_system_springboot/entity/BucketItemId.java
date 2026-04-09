package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BucketItemId implements Serializable {
    private int bucketId;
    private int itemId;
}
