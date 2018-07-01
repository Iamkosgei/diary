package xyz.kosgei.diary.Model;

public class Entries {
    String date;
    String title;
    String body;

    public Entries() {

    }

    public Entries(String date, String title, String body) {
        this.date = date;
        this.title = title;
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
