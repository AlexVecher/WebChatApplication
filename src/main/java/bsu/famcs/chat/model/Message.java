package bsu.famcs.chat.model;

public class Message {
    private final String id;
    private String name;
    private String message;
    private String date;
    private int countMess;
    private boolean isDeleted;

    public Message(String name, String message, String id, String date, int countMess, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.date = date;
        this.countMess = countMess;
        this.isDeleted = isDeleted;
    }

    public void setChangeDate(String changeDate) {
        this.countMess = countMess;
    }

    public void setMsgText(String msgText){
        this.message = msgText;
    }

    public void isDelete() {
        isDeleted = true;
        message = "Deleted";
    }

    public String getDel() {
        if(isDeleted) return "true";
        return "false";
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return name;
    }

    public String getMsgText() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"name\":\"").append(name)
                .append("\", \"message\":\"").append(message)
                .append("\", \"id\":\"").append(id)
                .append("\", \"countMess\":\"").append(countMess)
                .append("\", \"isDeleted\":\"").append(isDeleted)
                .append("\"}");
        return sb.toString();
    }

    public String getUserMessage() {
        StringBuilder sb = new StringBuilder(getDate());
        sb.append(' ')
                .append(name)
                .append(" : ")
                .append(message);
        return sb.toString();
    }
}
