package results;

/**
 * Created by Dani on 22/12/16.
 */

public class Result {
    private int id;
    private String image;
    private String description;

    public Result(int id, String i, String d) {
        this.id = id;
        this.image = i;
        this. description = d;
    }

    public int getId() {return id;}

    public void setId() {this.id = id;}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
