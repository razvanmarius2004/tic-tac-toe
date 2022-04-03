package ro.randr.tictactoe.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ro.randr.tictactoe.Models.ChatMessageModel;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<ChatMessageModel>> chats = new MutableLiveData<>();
    public LiveData<List<ChatMessageModel>> getChats() {
        return chats;
    }

    public void addChat(ChatMessageModel chatMessageModel) {
        List<ChatMessageModel> list = new ArrayList<>();
        list.add(chatMessageModel);
        chats.postValue(list);
    }

}
