package lk.ijse.finalproject.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class SaleAnalysisDto {
    private String orderId;
    private String customerId;
    private String customerName;
    private LocalDate date;
    private double amount;
    private String status;
    private String paymentMethod;
}