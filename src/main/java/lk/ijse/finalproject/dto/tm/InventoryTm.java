package lk.ijse.finalproject.dto.tm;

import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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