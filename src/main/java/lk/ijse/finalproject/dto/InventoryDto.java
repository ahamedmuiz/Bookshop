package lk.ijse.finalproject.dto;

import java.time.LocalDate;

public class InventoryDto {
    private int invId;
    private int supId;
    private int stockQty;
    private LocalDate lastUpdate;
    private String category;
    private double price;

    public InventoryDto(int invId, int supId, int stockQty, LocalDate lastUpdate, String category, double price) {
        this.invId = invId;
        this.supId = supId;
        this.stockQty = stockQty;
        this.lastUpdate = lastUpdate;
        this.category = category;
        this.price = price;
    }

    // Getters
    public int getInvId() { return invId; }
    public int getSupId() { return supId; }
    public int getStockQty() { return stockQty; }
    public LocalDate getLastUpdate() { return lastUpdate; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    // Setters
    public void setInvId(int invId) { this.invId = invId; }
    public void setSupId(int supId) { this.supId = supId; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }
    public void setLastUpdate(LocalDate lastUpdate) { this.lastUpdate = lastUpdate; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
}
