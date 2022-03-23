package ro.randr.tictactoe.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ro.randr.tictactoe.Adapters.RecycleViewChatAdapter;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.Models.ClickMessageModel;
import ro.randr.tictactoe.Models.MessageModel;
import ro.randr.tictactoe.Models.TicTac;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Utils.Dialog;
import ro.randr.tictactoe.Utils.Winner;
import ro.randr.tictactoe.Views.GridLayoutItem;

public class GameAndChatActivity extends AppCompatActivity {

    private static GridLayoutItem[][] myViews;
    private static GridLayout gl_game_grid;
    private static WeakReference<Context> mWeakRef;
    private RecyclerView rv_chat_history;
    public static RecycleViewChatAdapter mAdapter;
    private AppCompatEditText et_message;
    private AppCompatImageView iv_send;
    private static AppCompatTextView tv_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_and_chat);
        mWeakRef = new WeakReference<>(this);
        getViews();
        setViews();
    }

    private void getViews() {
        gl_game_grid = findViewById(R.id.gl_grid);
        et_message = findViewById(R.id.et_message);
        iv_send = findViewById(R.id.iv_send);
        rv_chat_history = findViewById(R.id.rv_chat_history);
        tv_info = findViewById(R.id.tv_info);
    }

    private void setViews() {
        iv_send.setOnClickListener(view -> {
            ChatMessageModel chatMessageModel = new ChatMessageModel(MainActivity.username, et_message.getText().toString(), true);
            MessageModel messageModel = new MessageModel(chatMessageModel, null, null);
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
        myViews = new GridLayoutItem[numOfCol][numOfRow];
        for (int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                GridLayoutItem tView = new GridLayoutItem(this);
                tView.x = xPos;
                tView.y = yPos;
                tView.type = TicTac.NONE;
                tView.setBackgroundColor(getColor(R.color.board_background));
                tView.setOnClickListener(this::manageClick);
                myViews[yPos][xPos] = tView;
                gl_game_grid.addView(tView);
            }
        }

        gl_game_grid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

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

                        int w = pLength / numOfCol; //pWidth/numOfCol;
                        int h = pLength / numOfRow; //pHeight/numOfRow;

                        for (int yPos = 0; yPos < numOfRow; yPos++) {
                            for (int xPos = 0; xPos < numOfCol; xPos++) {
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) myViews[yPos][xPos].getLayoutParams();
                                params.width = w - 2 * MARGIN;
                                params.height = h - 2 * MARGIN;
                                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                                myViews[yPos][xPos].setLayoutParams(params);
                            }
                        }


                        gl_game_grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    public static void ManageClickReceived(int x, int y, Context context) {
        if (ConnectionUtils.player == TicTac.TAC) {
            myViews[y][x].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.tic));
            myViews[y][x].type = TicTac.TIC;
        } else {
            myViews[y][x].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.tac));
            myViews[y][x].type = TicTac.TAC;
        }
        ConnectionUtils.isYourTurn = true;
        setInfo("Your turn");
        Winner winner = getWinType(context);
        if (winner != Winner.NONE) {
            showEndgameDialog(mWeakRef.get(), winner);
            setEndGame();
        }

    }

    private static void setEndGame() {
        ConnectionUtils.isOpponentReady = false;
        ConnectionUtils.areYouReady = false;
    }

    private void manageClick(View view) {
        GridLayoutItem g = (GridLayoutItem) view;
        if (ConnectionUtils.isYourTurn && g.type == TicTac.NONE && ConnectionUtils.areYouReady && ConnectionUtils.isOpponentReady) {
            ClickMessageModel clickMessageModel = new ClickMessageModel(g.x, g.y);
            MessageModel messageModel = new MessageModel(null, clickMessageModel, null);
            if (ConnectionUtils.player == TicTac.TIC) {
                g.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.tic));
                g.type = TicTac.TIC;
            } else {
                g.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.tac));
                g.type = TicTac.TAC;
            }
            ConnectionUtils.SendMessage(getApplicationContext(), messageModel);
            ConnectionUtils.isYourTurn = false;
            setInfo("Opponents turn");
            Winner winner = getWinType(this);
            if (winner != Winner.NONE) {
                showEndgameDialog(this, winner);
                setEndGame();
            }
        }
    }

    private static Winner getWinType(Context context) {
        Winner winner = Winner.NONE;
        if (myViews[0][0].type == myViews[0][1].type && myViews[0][1].type == myViews[0][2].type) {
            switch (myViews[0][0].type) {
                case TIC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[1][0].type == myViews[1][1].type && myViews[1][1].type == myViews[1][2].type) {
            switch (myViews[1][0].type) {
                case TIC:
                    myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    winner = Winner.TIC;
                    return winner;

                case TAC:
                    myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[2][0].type == myViews[2][1].type && myViews[2][1].type == myViews[2][2].type) {
            switch (myViews[2][0].type) {
                case TIC:
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_left_right));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_left_right));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[0][0].type == myViews[1][0].type && myViews[1][0].type == myViews[2][0].type) {
            switch (myViews[0][0].type) {
                case TIC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[0][1].type == myViews[1][1].type && myViews[1][1].type == myViews[2][1].type) {
            switch (myViews[0][1].type) {
                case TIC:
                    myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[0][2].type == myViews[1][2].type && myViews[1][2].type == myViews[2][2].type) {
            switch (myViews[0][2].type) {
                case TIC:
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_dwon));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_dwon));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[0][0].type == myViews[1][1].type && myViews[1][1].type == myViews[2][2].type) {
            switch (myViews[0][0].type) {
                case TIC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_left_down_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_left_down_right));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_up_left_down_right));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_left_down_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_left_down_right));
                    myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_up_left_down_right));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (myViews[2][0].type == myViews[1][1].type && myViews[1][1].type == myViews[0][2].type) {
            switch (myViews[2][0].type) {
                case TIC:
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_down_left_up_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_down_left_up_right));
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tic_cut_down_left_up_right));
                    winner = Winner.TIC;
                    return winner;
                case TAC:
                    myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_down_left_up_right));
                    myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_down_left_up_right));
                    myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_tac_cut_down_left_up_right));
                    winner = Winner.TAC;
                    return winner;
            }
        }
        if (isDraw()) {
            winner = Winner.DRAW;
        }
        return winner;
    }

    private static boolean isDraw() {
        boolean foundOneEmpty = false;
        for (int yPos = 0; yPos < myViews.length; yPos++) {
            for (int xPos = 0; xPos < myViews[yPos].length; xPos++) {
                if (myViews[yPos][xPos].type == TicTac.NONE) {
                    foundOneEmpty = true;
                    return false;
                }
            }
        }
        return true;
    }

    private static void showEndgameDialog(Context context, Winner winner) {
        Dialog dialog = null;
        if (winner == Winner.TIC && ConnectionUtils.player == TicTac.TIC
                || winner == Winner.TAC && ConnectionUtils.player == TicTac.TAC) {
            dialog = new Dialog(context, "you_won", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.isYourTurn = false;
                    ConnectionUtils.areYouReady = true;
                    setInfo("Opponents turn");
                    if (ConnectionUtils.isYourTurn) {
                        ConnectionUtils.player = TicTac.TIC;
                    } else {
                        ConnectionUtils.player = TicTac.TAC;
                    }
                    recreateBoard();
                    ConnectionUtils.SendMessage(context, new MessageModel(null, null, true));
                }

                @Override
                public void onNegative() {

                }
            });
        } else if ((winner == Winner.TIC && ConnectionUtils.player == TicTac.TAC
                || winner == Winner.TAC && ConnectionUtils.player == TicTac.TIC)) {
            //you lost
            dialog = new Dialog(context, "you_lost", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.isYourTurn = true;
                    ConnectionUtils.areYouReady = true;
                    setInfo("Your turn");
                    if (ConnectionUtils.isYourTurn) {
                        ConnectionUtils.player = TicTac.TIC;
                    } else {
                        ConnectionUtils.player = TicTac.TAC;
                    }
                    recreateBoard();
                    ConnectionUtils.SendMessage(context, new MessageModel(null, null, true));
                }

                @Override
                public void onNegative() {

                }
            });
        } else if (winner == Winner.DRAW) {
            dialog = new Dialog(context, "draw", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.areYouReady = true;
                    if (ConnectionUtils.isYourTurn) {
                        ConnectionUtils.player = TicTac.TIC;
                    } else {
                        ConnectionUtils.player = TicTac.TAC;
                    }
                    recreateBoard();
                    ConnectionUtils.SendMessage(context, new MessageModel(null, null, true));

                }

                @Override
                public void onNegative() {

                }
            });
        }
        if (dialog != null) {
            dialog.show();
        }
    }

    public static void setInfo(String info) {
        if (tv_info != null) {
            tv_info.setText(info);
        }
    }

    private static void recreateBoard() {
        for (int yPos = 0; yPos < myViews.length; yPos++) {
            for (int xPos = 0; xPos < myViews[yPos].length; xPos++) {
                myViews[yPos][xPos].setImageDrawable(AppCompatResources.getDrawable(mWeakRef.get(), R.drawable.background));
                myViews[yPos][xPos].type = TicTac.NONE;
                if (!ConnectionUtils.isOpponentReady) {
                    setInfo("Waiting for opponent to be ready");
                }
            }
        }

    }
}