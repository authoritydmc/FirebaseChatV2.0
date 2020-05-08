package chatapp.beast.firebasechat;



public class Messages {
    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public Messages(String message, String type, String fromid, long time, boolean seen, String toid) {
        this.message = message;
        this.type = type;
        this.fromid = fromid;
        this.time = time;
        this.seen = seen;
        this.toid=toid;
    }

    private String message;
    private String type;
    private String fromid;
    private String toid;

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    private String MessageID;
    private long time;
    private boolean seen;


    public Messages() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }
}


