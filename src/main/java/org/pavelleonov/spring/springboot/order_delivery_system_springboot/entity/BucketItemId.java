package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class BucketItemId implements Serializable {
    private int bucketId;
    private int itemId;
}
