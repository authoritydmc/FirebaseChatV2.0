package chatapp.beast.firebasechat;

public class CurrentChatList {
    String ischatting;

    public String getIschatting() {
        return ischatting;
    }

    public void setIschatting(String ischatting) {
        this.ischatting = ischatting;
    }

    public CurrentChatList(String ischatting) {

        this.ischatting = ischatting;
    }

    public CurrentChatList() {
    }
}
