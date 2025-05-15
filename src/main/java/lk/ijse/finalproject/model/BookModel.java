package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.BookDto;
import lk.ijse.finalproject.dto.OrderBookContainDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookModel {
    public ArrayList<String> getAllBookIds() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT Inv_ID FROM Inventory");
        ArrayList<String> list = new ArrayList<>();
        while (rst.next()) {
            list.add(rst.getString(1));
        }
        return list;
    }

    public BookDto findById(String invId) throws SQLException {
        ResultSet rst = CrudUtil.execute(
                "SELECT Inv_ID, Category, Stock_Qty, Price, Sup_ID " +
                        "FROM Inventory WHERE Inv_ID = ?",
                invId
        );

        if (rst.next()) {
            return new BookDto(
                    rst.getString("Inv_ID"),
                    rst.getString("Category"),
                    rst.getInt("Stock_Qty"),
                    rst.getDouble("Price"),
                    rst.getString("Category"),
                    rst.getInt("Sup_ID")
            );
        }
        return null;
    }

    public boolean reduceQty(OrderBookContainDto dto) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Inventory SET Stock_Qty = Stock_Qty - ? WHERE Inv_ID = ?",
                dto.getQty(),
                dto.getInvId()  // Note: This should be invId, but keeping field name for compatibility
        );
    }
}