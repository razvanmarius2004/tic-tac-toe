package ro.randr.tictactoe.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ro.randr.tictactoe.Adapters.RecycleViewChatAdapter;
import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.Models.ClickMessageModel;
import ro.randr.tictactoe.Models.MessageModel;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Views.GridLayoutItem;

public class GameAndChatActivity extends AppCompatActivity {

    private static GridLayoutItem[] myViews;
    private GridLayout gl_game_grid;
    private RecyclerView rv_chat_history;
    public static RecycleViewChatAdapter mAdapter;
    private AppCompatEditText et_message;
    private AppCompatImageView iv_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_and_chat);
        getViews();
        setViews();
    }

    private void getViews() {
        gl_game_grid = findViewById(R.id.gl_grid);
        et_message = findViewById(R.id.et_message);
        iv_send = findViewById(R.id.iv_send);
        rv_chat_history = findViewById(R.id.rv_chat_history);
    }

    private void setViews() {
        iv_send.setOnClickListener(view -> {
            ChatMessageModel chatMessageModel = new ChatMessageModel(ConnectionUtils.username, et_message.getText().toString());
            MessageModel messageModel = new MessageModel(chatMessageModel, null);
            ConnectionUtils.SendMessage(getApplicationContext(), messageModel);
            mAdapter.addToDataSet(chatMessageModel);
            et_message.setText("");
        });
        setGrid();
        setRecycleView();
    }

    private void setRecycleView() {
        rv_chat_history.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_chat_history.setLayoutManager(mLayoutManager);
        rv_chat_history.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewChatAdapter(new WeakReference<>(this), new ArrayList<>());
        rv_chat_history.setAdapter(mAdapter);
    }

    private void setGrid() {
        int numOfCol = gl_game_grid.getColumnCount();
        int numOfRow = gl_game_grid.getRowCount();
        myViews = new GridLayoutItem[numOfCol * numOfRow];
        for (int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                GridLayoutItem tView = new GridLayoutItem(this);
                tView.x = xPos;
                tView.y = yPos;
                tView.setBackgroundColor(Color.RED);
                tView.setOnClickListener(view -> {
                    GridLayoutItem g = (GridLayoutItem) view;
                    view.setBackgroundColor(Color.GREEN);
                    ClickMessageModel clickMessageModel = new ClickMessageModel(g.x, g.y);
                    MessageModel messageModel = new MessageModel(null, clickMessageModel);
                    ConnectionUtils.SendMessage(getApplicationContext(), messageModel);
                });
                myViews[yPos * numOfCol + xPos] = tView;
                gl_game_grid.addView(tView);
            }
        }

        gl_game_grid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {

                        int pLength;
                        final int MARGIN = 5;

                        int pWidth = gl_game_grid.getWidth();
                        int pHeight = gl_game_grid.getHeight();
                        int numOfCol = gl_game_grid.getColumnCount();
                        int numOfRow = gl_game_grid.getRowCount();

                        //Set gl_game_grid equal width and height
                        pLength = Math.min(pWidth, pHeight);
                        ViewGroup.LayoutParams pParams = gl_game_grid.getLayoutParams();
                        pParams.width = pLength;
                        pParams.height = pLength;
                        gl_game_grid.setLayoutParams(pParams);

                        int w = pLength/numOfCol; //pWidth/numOfCol;
                        int h = pLength/numOfRow; //pHeight/numOfRow;

                        for(int yPos=0; yPos<numOfRow; yPos++){
                            for(int xPos=0; xPos<numOfCol; xPos++){
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams)myViews[yPos*numOfCol + xPos].getLayoutParams();
                                params.width = w - 2*MARGIN;
                                params.height = h - 2*MARGIN;
                                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                                myViews[yPos*numOfCol + xPos].setLayoutParams(params);
                            }
                        }


                        gl_game_grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }});
    }

    public static void ManageClickReceived(int x, int y) {
        for (GridLayoutItem myView : myViews) {
            if (myView.x == x && myView.y == y) {
                myView.setBackgroundColor(Color.GREEN);
                break;
            }
        }
    }
}