package lk.ijse.finalproject.dto;

import lombok.*;

import java.sql.Date;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDto {
    private String oId;
    private String cId;
    private Date date;
    private double totalAmount;
    private String paymentMethod;
    private ArrayList<OrderBookContainDto> cartList;
}