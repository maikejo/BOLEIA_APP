package ao.app.boleia.motorista.model;

/**
 * Created by maike.silva on 28/12/2017.
 */

public class Sender {
    public String to;
    public Notification notification;


    public Sender(){
    }

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
