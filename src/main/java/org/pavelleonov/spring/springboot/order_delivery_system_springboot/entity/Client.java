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

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer clientId;

    @Column(name = "is_active")
    boolean isActive = true;

    @Column(name = "name")
    private String name;

    @Column(name = "is_authorised")
    private boolean isAuthorised = true;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "is_advertisable")
    private boolean isAdvertisable = true;

    @Column(name = "is_profile_complete")
    private boolean isProfileComplete = false;

    @Column(name = "is_online_check_on")
    private boolean isOnlineCheckOn = false;

    @Column(name = "bonuses_amount")
    private int bonusesAmount = 0;

    // заменить fetch на LAZY когда потом буду решать проблему N+1
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "credentials_id")
    private Credentials credentials;

    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}
            , orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ClientAddress> clientAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Order> completeOrders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
    orphanRemoval = true)
    private Set<FavouriteItem> favouriteItems = new LinkedHashSet<>();

    // заменить fetch на LAZY когда потом буду решать проблему N+1
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

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

}
