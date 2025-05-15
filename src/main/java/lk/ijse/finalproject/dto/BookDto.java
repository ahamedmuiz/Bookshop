package lk.ijse.finalproject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookDto {
    private String invId;  // Changed from bId to invId
    private String category;  // Changed from name to category
    private int qty;
    private double price;
    private String categoryType;  // Additional field if needed
    private int supId;  // Added to link with supplier
}