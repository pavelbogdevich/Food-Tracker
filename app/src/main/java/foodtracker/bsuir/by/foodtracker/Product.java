package foodtracker.bsuir.by.foodtracker;

public class Product {

    private String name;
    private String expirationDate;
    private String photoPath;
    private int id;
    private int currentAmount;
    private int amount;

    public Product(int id, String name, String expirationDate, int currentAmount, int amount, String photoPath) {
        this.id = id;
        this.name = name;
        this.expirationDate = expirationDate;
        this.currentAmount = currentAmount;
        this.amount = amount;
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
