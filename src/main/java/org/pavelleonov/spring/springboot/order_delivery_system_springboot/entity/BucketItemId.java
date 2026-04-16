package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)

public class BucketItemId implements Serializable {
    private Integer bucketId;
    private Integer itemId;
}
