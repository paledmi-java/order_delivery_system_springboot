package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItem;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Integer> {
    List<BucketItem> findBucketItemById(BucketItemId id);
}
