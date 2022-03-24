package ro.randr.tictactoe.Models;


public class MessageModel {

    public ChatMessageModel ChatMessageModel;
    public ClickMessageModel ClickMessageModel;
    public Boolean IsOpponentReady;

    public MessageModel(ro.randr.tictactoe.Models.ChatMessageModel chatMessageModel, ro.randr.tictactoe.Models.ClickMessageModel clickMessageModel, Boolean isOpponentReady) {
        ChatMessageModel = chatMessageModel;
        ClickMessageModel = clickMessageModel;
        this.IsOpponentReady = isOpponentReady;
    }
}
