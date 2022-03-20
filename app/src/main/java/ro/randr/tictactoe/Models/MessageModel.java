package ro.randr.tictactoe.Models;

public class MessageModel {
    public ChatMessageModel ChatMessageModel;
    public ClickMessageModel ClickMessageModel;

    public MessageModel(ro.randr.tictactoe.Models.ChatMessageModel chatMessageModel, ro.randr.tictactoe.Models.ClickMessageModel clickMessageModel) {
        ChatMessageModel = chatMessageModel;
        ClickMessageModel = clickMessageModel;
    }
}
