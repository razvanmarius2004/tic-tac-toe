package ro.randr.tictactoe.Models;

public class ChatMessageModel {
    public String User;
    public String Message;
    public boolean IsOwn;

    public ChatMessageModel(String user, String message, boolean isOwn) {
        User = user;
        Message = message;
        IsOwn = isOwn;
    }
}
