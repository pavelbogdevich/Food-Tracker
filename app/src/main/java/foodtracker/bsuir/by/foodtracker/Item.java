package foodtracker.bsuir.by.foodtracker;

public class Item {

    private int id;
    private String name;
    private int place;
    private String comment;

    public Item(int id, String name, int place, String comment) {

        this.id = id;
        this.name = name;
        this.place = place;
        this.comment = comment;
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

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
