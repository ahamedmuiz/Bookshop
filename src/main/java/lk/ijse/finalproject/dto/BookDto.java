package lk.ijse.finalproject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookDto {
    private String invId;
    private String category;
    private int qty;
    private double price;
    private String categoryType;
    private int supId;
}