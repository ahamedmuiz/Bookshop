package lk.ijse.finalproject.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CartTm {
    private String invId;
    private String category;
    private int qty;
    private double price;
    private double total;
    private Button btnRemove;
}