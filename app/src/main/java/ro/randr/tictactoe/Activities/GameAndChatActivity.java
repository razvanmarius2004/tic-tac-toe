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
import java.util.Observable;
import java.util.Observer;

import ro.randr.tictactoe.Adapters.RecycleViewChatAdapter;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.BoardState;
import ro.randr.tictactoe.Models.CellModel;
import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.Models.ClickMessageModel;
import ro.randr.tictactoe.Models.GameState;
import ro.randr.tictactoe.Models.MessageModel;
import ro.randr.tictactoe.Models.TicTac;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Utils.Dialog;
import ro.randr.tictactoe.Utils.WinType;
import ro.randr.tictactoe.Utils.Winner;
import ro.randr.tictactoe.Views.GridLayoutItem;

public class GameAndChatActivity extends AppCompatActivity implements Observer {

    private GridLayoutItem[][] myViews;
    private GridLayout gl_game_grid;
    private RecyclerView rv_chat_history;
    public static RecycleViewChatAdapter mAdapter;
    private AppCompatEditText et_message;
    private AppCompatImageView iv_send;
    private AppCompatTextView tv_info;

    private Observable boardStateObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_and_chat);

        boardStateObservable = BoardState.getInstance();
        boardStateObservable.addObserver(this);
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

    private void manageClick(View view) {
        GridLayoutItem g = (GridLayoutItem) view;
        boolean isYourTurn = GameState.getInstance().isYourTurn();
        TicTac type = BoardState.getInstance().getBoard()[g.y][g.x].getType();
        boolean areYouReady = GameState.getInstance().isAreYouReady();
        boolean isOpponentReady = GameState.getInstance().isOpponentReady();

        if (isYourTurn && type == TicTac.NONE && areYouReady && isOpponentReady) {
            ClickMessageModel clickMessageModel = new ClickMessageModel(g.x, g.y);
            MessageModel messageModel = new MessageModel(null, clickMessageModel, null);
            ConnectionUtils.SendMessage(this, messageModel);
            GameState.getInstance().setYourTurn(false);
            BoardState.getInstance().modifyCell(g.y, g.x, GameState.getInstance().getPlayerType());
            setInfo("Opponents turn");
            BoardState.getInstance().checkAndSetWinner();
        }
    }
    public void setInfo(String info) {
        if (tv_info != null) {
            tv_info.setText(info);
        }
    }

    private void recreateBoard() {
        for (int yPos = 0; yPos < myViews.length; yPos++) {
            for (int xPos = 0; xPos < myViews[yPos].length; xPos++) {
                myViews[yPos][xPos].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.background));
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof BoardState) {
            BoardState board = (BoardState) observable;
            CellModel changedCell = board.getChangedCell();
            if (changedCell != null) {
                modifyViews(changedCell);
            }

            if (board.isGameOver()) {
                setViewsForGameOver(board.getWinner(), board.getWinType());
                createDialogForGameOver(board.getWinner());
            }

            if (GameState.getInstance().isYourTurn()) {
                setInfo("Your turn");
            } else {
                setInfo("Opponent turn");
            }
        }
    }

    private void createDialogForGameOver(Winner winner) {
        Dialog dialog = null;
        GameState gameState = GameState.getInstance();
        if (gameState.getPlayerType() == TicTac.TIC && winner == Winner.TIC
                || gameState.getPlayerType() == TicTac.TAC && winner == Winner.TAC) {
            dialog = new Dialog(GameAndChatActivity.this, "you_won", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    BoardState.getInstance().reInitGame();
                    gameState.reInit(TicTac.TAC);
                    gameState.setAreYouReady(true);
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    recreateBoard();
                }

                @Override
                public void onNegative() {

                }
            });
        }

        if (gameState.getPlayerType() == TicTac.TIC && winner == Winner.TAC
                || gameState.getPlayerType() == TicTac.TAC && winner == Winner.TIC) {
            dialog = new Dialog(GameAndChatActivity.this, "you_lost", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    BoardState.getInstance().reInitGame();
                    gameState.reInit(TicTac.TIC);
                    gameState.setAreYouReady(true);
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    recreateBoard();
                }

                @Override
                public void onNegative() {

                }
            });
        }
        if (winner == Winner.DRAW) {
            dialog = new Dialog(GameAndChatActivity.this, "draw", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    BoardState.getInstance().reInitGame();
                    if (gameState.getPlayerType() == TicTac.TIC) {
                        gameState.reInit(TicTac.TAC);
                    } else {
                        gameState.reInit(TicTac.TIC);
                    }
                    gameState.setAreYouReady(true);
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    recreateBoard();
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

    private void modifyViews(CellModel changedCell) {
        if (changedCell.getType() == TicTac.TIC) {
            myViews[changedCell.getY()][changedCell.getX()].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.tic));
        } else if (changedCell.getType() == TicTac.TAC) {
            myViews[changedCell.getY()][changedCell.getX()].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.tac));
        }
    }

    private void setViewsForGameOver(Winner winner, WinType winType) {
        if (winner == Winner.TIC) {
            if (winType == WinType.HORIZONTAL_0) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                return;
            }

            if (winType == WinType.HORIZONTAL_1) {
                myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                return;
            }

            if (winType == WinType.HORIZONTAL_2) {
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_left_right));
                return;
            }

            if (winType == WinType.VERTICAL_0) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                return;
            }

            if (winType == WinType.VERTICAL_1) {
                myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                return;
            }

            if (winType == WinType.VERTICAL_2) {
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_dwon));
                return;
            }

            if (winType == WinType.NW_TO_SE) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_left_down_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_left_down_right));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_up_left_down_right));
                return;
            }

            if (winType == WinType.SW_TO_NE) {
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_down_left_up_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_down_left_up_right));
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tic_cut_down_left_up_right));
                return;
            }
        }

        if (winner == Winner.TAC) {
            if (winType == WinType.HORIZONTAL_0) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                return;
            }

            if (winType == WinType.HORIZONTAL_1) {
                myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                return;
            }

            if (winType == WinType.HORIZONTAL_2) {
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_left_right));
                return;
            }

            if (winType == WinType.VERTICAL_0) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[1][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                return;
            }

            if (winType == WinType.VERTICAL_1) {
                myViews[0][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[2][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                return;
            }

            if (winType == WinType.VERTICAL_2) {
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[1][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_dwon));
                return;
            }

            if (winType == WinType.NW_TO_SE) {
                myViews[0][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_left_down_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_left_down_right));
                myViews[2][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_up_left_down_right));
                return;
            }

            if (winType == WinType.SW_TO_NE) {
                myViews[2][0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_down_left_up_right));
                myViews[1][1].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_down_left_up_right));
                myViews[0][2].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tac_cut_down_left_up_right));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boardStateObservable.deleteObserver(this);
    }
}