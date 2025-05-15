package lk.ijse.finalproject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderBookContainDto {
    private String oId;
    private String invId;  // Changed from bId to invId
    private int qty;
    private double price;
}