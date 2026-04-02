package org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "items")
public class Item implements Comparable<Item>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "type_of_item")
    private String typeOfItem;

    @Column(name = "ingredients")
    private String ingredients;

    @Column(name = "amount_of_pieces")
    private int amountOfPieces;

    @Column(name = "price")
    private int price;

    @Column(name = "description")
    private String description;

    @Column(name = "mass")
    private int mass;

    @Column(name = "kcal")
    private int kcal;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "has_multiple_components")
    private boolean hasMultiComp;

    @Column(name = "is_changeable")
    private boolean isChangeable;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public enum itemFormatType {FULL, PUBLIC, SHORT}

    public String itemFormat(itemFormatType itemFormatType){
        return switch (itemFormatType){
            case FULL -> String.format("dto.Item: Id-%d, Name: %s, Type: %s, Ingredients: %s, Amount Of Pieces: %d, " +
                            "Price: %d, Mass: %d, Kcal: %d, Has multiple components: %b, " +
                            "Is changeable: %b, Is available: %b",
                    itemId, itemName, typeOfItem, ingredients, amountOfPieces, price, mass, kcal,
                    hasMultiComp, isChangeable, isAvailable);
            case SHORT -> String.format("%s, %d pieces, " +
                            " Mass: %d gr, Price: %d rub",
                    itemName, amountOfPieces, mass, price);
            case PUBLIC -> String.format("Name: %s, Type: %s, Ingredients: %s, Amount Of Pieces: %d, " +
                            "Price: %d, Mass: %d, Kcal: %d" ,
                    itemName, typeOfItem, ingredients, amountOfPieces, price, mass, kcal);
        };
    }

    @Override
    public int compareTo(Item o) {
        return itemName.compareTo(o.itemName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemId == item.itemId && Objects.equals(itemName, item.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName);
    }
}
