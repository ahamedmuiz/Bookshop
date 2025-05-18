package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.OrderBookContainDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class OrderBookContainModel {
    private final BookModel bookModel = new BookModel();

        public static boolean saveOrderDetailsList(ArrayList<OrderBookContainDto> list) throws SQLException {
            String sql = "INSERT INTO OrderBookContain (O_ID, Inv_ID, Qty, Unit_Price, Total_Amount) VALUES (?, ?, ?, ?, ?)";

            for (OrderBookContainDto dto : list) {
                boolean isSaved = CrudUtil.execute(sql,
                        dto.getOId(),
                        dto.getInvId(),
                        dto.getQty(),
                        dto.getPrice(),
                        dto.getQty() * dto.getPrice()
                );
                if (!isSaved) return false;
            }
            return true;
        }


    private boolean saveOrderDetails(OrderBookContainDto dto) throws SQLException {
        double totalAmount = dto.getPrice() * dto.getQty();
        return CrudUtil.execute(
                "INSERT INTO OrderBookContain (O_ID, Inv_ID, Qty, Total_Amount) VALUES (?,?,?,?)",
                dto.getOId(),
                dto.getInvId(),  // Ensure this matches your database column (B_ID in schema)
                dto.getQty(),
                totalAmount
        );
    }

}