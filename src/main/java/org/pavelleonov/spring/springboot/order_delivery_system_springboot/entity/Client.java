package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int clientId;

    @Column(name = "is_active")
    boolean isActive = true;

    @Column(name = "name")
    private String name;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "is_authorised")
    private boolean isAuthorised = true;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "is_advertisable")
    boolean isAdvertisable = true;

    @Column(name = "is_profile_complete")
    boolean isProfileComplete = false;

    @Column(name = "is_online_check_on")
    boolean isOnlineCheckOn = false;

    @Column(name = "bonuses_amount")
    private int bonusesAmount = 0;

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

//    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
//    orphanRemoval = true)
//    private List<ClientAddress> addresses = new ArrayList<>();

}
