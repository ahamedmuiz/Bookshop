package lk.ijse.finalproject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderBookContainDto {
    private String oId;
    private int invId;  // Changed to int to match your Inventory table
    private int qty;
    private double price;
}