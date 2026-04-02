package org.pavelleonov.spring.springboot.order_delivery_system_springboot.specifications;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {

    public static Specification<Client> hasEmail(String email){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<Client> hasName(String name){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Client> hasPhone(String phone){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("phone"), phone);
    }

    public static Specification<Client> hasIsActive(Boolean isActive){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}
