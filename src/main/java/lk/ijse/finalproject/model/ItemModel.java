//package lk.ijse.finalproject.model;
//
//import lk.ijse.finalproject.dto.ItemDto;
//import lk.ijse.finalproject.dto.OrderDetailsDto;
//import lk.ijse.finalproject.util.CrudUtil;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//
//public class ItemModel {
//    public ArrayList<String> getAllItemIds() throws SQLException {
//        ResultSet rst = CrudUtil.execute(
//                "select item_id from item"
//        );
//        ArrayList<String> list = new ArrayList<>();
//        while (rst.next()) {
//            String id = rst.getString(1);
//            list.add(id);
//        }
//        return list;
//    }
//
//    public ItemDto findById(String itemId) throws SQLException {
//        ResultSet rst = CrudUtil.execute(
//                "select * from item where item_id=?",
//                itemId
//        );
//
//        if (rst.next()) {
//            return new ItemDto(
//                    rst.getString(1),
//                    rst.getString(2),
//                    rst.getInt(3),
//                    rst.getDouble(4)
//            );
//        }
//        return null;
//    }
//
//    public boolean reduceQty(OrderDetailsDto orderDetailsDTO) throws SQLException {
//        return CrudUtil.execute(
//                "update item set quantity = quantity-? where item_id=?",
//                orderDetailsDTO.getQty(),
//                orderDetailsDTO.getItemId()
//        );
//    }
//}