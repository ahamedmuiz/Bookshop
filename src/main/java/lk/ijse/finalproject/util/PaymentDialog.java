package lk.ijse.finalproject.util;

import javafx.scene.control.ChoiceDialog;

import java.util.Optional;

public class PaymentDialog {
    public static String showPaymentDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CASH", "CASH", "CARD");
        dialog.setTitle("Payment Method");
        dialog.setHeaderText("Select Payment Method");
        dialog.setContentText("Choose payment option:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}