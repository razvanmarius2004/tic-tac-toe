package ro.randr.tictactoe.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        ConstraintLayout cl_own_chat = holder.cl_own_chat;
        ConstraintLayout cl_opponent_chat = holder.cl_opponent_chat;
        AppCompatTextView tv_own = holder.tv_own;
        AppCompatTextView tv_opponent = holder.tv_opponent;

        if (messages.get(position).IsOwn) {
            //cl_opponent_chat.setVisibility(View.GONE);
            Drawable d = cl_opponent_chat.getBackground();
            d.setTint(mWeakRef.get().getColor(R.color.background));
            cl_opponent_chat.setBackground(d);
            cl_opponent_chat.setElevation(0);
            tv_opponent.setText("");

            d = cl_own_chat.getBackground();
            d.setTint(mWeakRef.get().getColor(R.color.chat_bubble_own));
            cl_own_chat.setBackground(d);
            cl_own_chat.setElevation(1);
            tv_own.setText(messages.get(position).Message);
        } else {

            Drawable d = cl_own_chat.getBackground();
            d.setTint(mWeakRef.get().getColor(R.color.background));
            cl_own_chat.setBackground(d);
            cl_opponent_chat.setElevation(0);
            tv_own.setText("");

            d = cl_opponent_chat.getBackground();
            d.setTint(mWeakRef.get().getColor(R.color.chat_bubble_opponent));
            cl_opponent_chat.setBackground(d);
            cl_opponent_chat.setElevation(1);
            tv_opponent.setText(messages.get(position).Message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_own_chat;
        ConstraintLayout cl_opponent_chat;
        AppCompatTextView tv_own;
        AppCompatTextView tv_opponent;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cl_own_chat = itemView.findViewById(R.id.cl_own_chat);
            this.cl_opponent_chat = itemView.findViewById(R.id.cl_opponent_chat);
            this.tv_opponent = itemView.findViewById(R.id.tv_opponent);
            this.tv_own = itemView.findViewById(R.id.tv_own);
        }
    }
}
