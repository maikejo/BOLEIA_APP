package app.boleia.passageiro.model;

/**
 * Created by maike.silva on 28/12/2017.
 */

public class Notification {
    public String title;
    public String tag;
    public String body;

    public Notification(String title, String body,String tag) {
        this.title = title;
        this.body = body;
        this.tag = tag;
    }

}
