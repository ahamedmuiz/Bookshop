package lk.ijse.finalproject.dto.tm;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class SaleAnalysisTm {
    private String orderId;
    private String customerId;
    private String date;
    private String customer;
    private double amount;
    private String status;
    private String payment;
    private HBox btnAction;
}