package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Bucket;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.BucketRepository;
import org.springframework.stereotype.Service;

@Service
public class BucketService {
    public BucketRepository bucketRepository;

    public BucketService(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public Bucket addItemToBucket(Client client, Item item, int quantity){

        Bucket bucket = client.getBucket();
        if(bucket == null){
            bucket = new Bucket();
        }
        bucket.addItem(item, quantity);
        return bucketRepository.save(bucket);
    }
}
