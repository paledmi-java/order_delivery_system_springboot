package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Bucket;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItem;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItemId;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Integer> {
    Optional<BucketItem> findBucketItemByBucketAndItem(Bucket bucket, Item item);

    BucketItem findBucketItemById(BucketItemId id);

    BucketItem findBucketItemByItem(Item item);
}
