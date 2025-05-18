package lk.ijse.finalproject.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InventoryTm {
    private String invId;
    private String supId;
    private String supplierName;
    private int stockQty;
    private LocalDate lastUpdate;
    private String category;
    private double price;
    private Button btnDelete;
}