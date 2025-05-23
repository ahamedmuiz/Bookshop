package lk.ijse.finalproject.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CartTm {
    private String invId;  // Changed from bId to invId
    private String category;  // Changed from name to category
    private int qty;
    private double price;
    private double total;
    private Button btnRemove;
}