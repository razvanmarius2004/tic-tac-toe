package ro.randr.tictactoe.Observables;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import ro.randr.tictactoe.Models.ChatMessageModel;

public class ChatMessageObservable extends Observable {
    private final List<ChatMessageModel> chatList;

    private static ChatMessageObservable instance;

    private ChatMessageObservable() {
        chatList = new ArrayList<>();
    }

    public static ChatMessageObservable getInstance() {
        if (instance == null) {
            instance = new ChatMessageObservable();
        }
        return instance;
    }

    public List<ChatMessageModel> getChatList() {
        return chatList;
    }

    public void addChatToList(ChatMessageModel chat) {
        chatList.add(chat);
        setChanged();
        notifyObservers(chat);
    }
}
