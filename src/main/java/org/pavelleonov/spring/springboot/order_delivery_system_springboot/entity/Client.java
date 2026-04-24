package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// УБРАТЬ БИЛДЕР ИЗ ENTITY ЗАМЕНИТЬ НА ФАБРИЧНЫЙ МЕТОД

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer clientId;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    boolean isActive = true;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "is_authorised", nullable = false)
    private boolean isAuthorised = true;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Builder.Default
    @Column(name = "is_advertisable", nullable = false)
    private boolean isAdvertisable = true;

    @Builder.Default
    @Column(name = "is_profile_complete", nullable = false)
    private boolean isProfileComplete = false;

    @Builder.Default
    @Column(name = "is_online_check_on", nullable = false)
    private boolean isOnlineCheckOn = false;

    @Builder.Default
    @Column(name = "bonuses_amount", nullable = false)
    private int bonusesAmount = 0;

    // заменить fetch на LAZY когда потом буду решать проблему N+1
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "credentials_id")
    private Credentials credentials;

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClientAddress> clientAddresses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Order> completeOrders = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
    orphanRemoval = true)
    private Set<FavouriteItem> favouriteItems = new LinkedHashSet<>();

    // заменить fetch на LAZY когда потом буду решать проблему N+1
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void setBucketAndClientToIt(Bucket bucket){
        this.setBucket(bucket);
        bucket.setClient(this);
    }

    public void addOrderToClient(Order order){
        completeOrders.add(order);
        order.setClient(this);
    }

    public void addCredentialsToClient(Credentials credentials){
        this.setCredentials(credentials);
        credentials.setClient(this);
    }

}
