package ro.randr.tictactoe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.R;


public class RecycleViewChatAdapter extends RecyclerView.Adapter<RecycleViewChatAdapter.RecycleViewHolder> {

    private List<ChatMessageModel> messages;
    private WeakReference<Context> mWeakRef;
    private RecyclerView mRecyclerView;

    public RecycleViewChatAdapter(WeakReference<Context> mWeakRef, List<ChatMessageModel> messages) {
        this.messages = messages;
        this.mWeakRef = mWeakRef;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    public void addToDataSet(ChatMessageModel chatMessageModel) {
        messages.add(chatMessageModel);
        this.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(getItemCount() -1);
    }


    @NonNull
    @Override
    public RecycleViewChatAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_cardview, parent, false);

        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewChatAdapter.RecycleViewHolder holder, int position) {
        AppCompatTextView tv_name = holder.tv_name;
        AppCompatTextView tv_message = holder.tv_message;

        tv_name.setText(messages.get(position).User + ": ");
        tv_message.setText(messages.get(position).Message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tv_name;
        AppCompatTextView tv_message;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_message = itemView.findViewById(R.id.tv_message);
        }
    }
}
