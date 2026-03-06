//package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table
//public class Courier {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//    private String name;
//    private CourierStatus courierStatus;
//    private CourierType courierType;
//
//    public Courier() {
//    }
//
//    public Courier(int id, String name, CourierStatus courierStatus, CourierType courierType) {
//        this.id = id;
//        this.name = name;
//        this.courierStatus = courierStatus;
//        this.courierType = courierType;
//    }
//
//    public String toString(){
//        return String.format("ID: %d, Name: %s, Status: %s, Type: %s",
//                id, name, courierStatus, courierType);
//    }
//
//    public enum CourierStatus{
//        FREE,
//        DELIVERING,
//        UNAVAILABLE
//    }
//
//    public enum CourierType{
//        ON_FOOT,
//        BIKE,
//        CAR
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public CourierStatus getCourierStatus() {
//        return courierStatus;
//    }
//
//    public void setCourierStatus(CourierStatus courierStatus) {
//        this.courierStatus = courierStatus;
//    }
//
//    public CourierType getCourierType() {
//        return courierType;
//    }
//
//    public void setCourierType(CourierType courierType) {
//        this.courierType = courierType;
//    }
//}
