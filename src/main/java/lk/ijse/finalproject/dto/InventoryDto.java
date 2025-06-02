package lk.ijse.finalproject.dto;

import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InventoryDto {
        private int invId;
        private int supId;
        private String supplierName;
        private int stockQty;
        private LocalDate lastUpdate;
        private String category;
        private double price;
}