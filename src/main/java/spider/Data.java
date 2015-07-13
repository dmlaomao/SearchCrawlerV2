package spider;

import java.util.ArrayList;

public class Data {

    private String title;
    private String time;
    private String description;
    private String link;
    private String img;
    private ArrayList<String> comments;
    
    public void setTitle(String title) {
        this.title = titie;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDestription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

}
