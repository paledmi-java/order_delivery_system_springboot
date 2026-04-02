package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "buckets")
public class Bucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne(mappedBy = "bucket")
    private Client client;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BucketItem> bucketItems = new ArrayList<>();

    public void addItem(Item item, int quantity){
        BucketItem bucketItem = new BucketItem();
        bucketItem.setItem(item);
        bucketItem.setBucket(this);
        bucketItem.setQuantity(quantity);

        bucketItems.add(bucketItem);
    }

    public void removeItem(Item item){
        bucketItems.removeIf(bi -> bi.getItem().equals(item));
    }

}
