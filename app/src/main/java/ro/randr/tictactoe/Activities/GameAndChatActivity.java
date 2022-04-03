package ro.randr.tictactoe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import ro.randr.tictactoe.Adapters.RecycleViewChatAdapter;
import ro.randr.tictactoe.Enums.TicTac;
import ro.randr.tictactoe.Enums.WinType;
import ro.randr.tictactoe.Enums.Winner;
import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.Models.CellModel;
import ro.randr.tictactoe.Models.ChatMessageModel;
import ro.randr.tictactoe.Models.ClickMessageModel;
import ro.randr.tictactoe.Models.IsGameOverModel;
import ro.randr.tictactoe.Models.IsOpponentReadyModel;
import ro.randr.tictactoe.Models.MessageModel;
import ro.randr.tictactoe.Observables.ConnectionStateObservable;
import ro.randr.tictactoe.Observables.GameStateObservable;
import ro.randr.tictactoe.Observables.MainActivityStateObservable;
import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Utils.Dialog;
import ro.randr.tictactoe.ViewModels.ChatViewModel;
import ro.randr.tictactoe.Views.GridLayoutItem;

public class GameAndChatActivity extends AppCompatActivity implements Observer {

    private GridLayoutItem[][] myViews;
    private GridLayout gl_game_grid;
    private RecyclerView rv_chat_history;
    private RecycleViewChatAdapter mAdapter;
    private AppCompatImageView iv_send;
    private AppCompatTextView tv_info;
    private TextInputEditText et_message;
    private AppCompatTextView tv_score;

    private Observable gameStateObservable;
    private Observable connectionStateObservable;


    private ChatViewModel chatViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_and_chat);
        addObservers();
        getViews();
        setViews();
    }

    private void addObservers() {
        gameStateObservable = GameStateObservable.getInstance();
        gameStateObservable.addObserver(this);
        connectionStateObservable = ConnectionStateObservable.getInstance();
        connectionStateObservable.addObserver(this);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.getChats().observe(this, chatMessageModels -> mAdapter.addToDataSet(chatMessageModels.get(chatMessageModels.size()-1)));
        ConnectionUtils.createChatViewModel(this);
    }

    private void getViews() {
        gl_game_grid = findViewById(R.id.gl_grid);
        et_message = findViewById(R.id.et);
        iv_send = findViewById(R.id.iv_send);
        rv_chat_history = findViewById(R.id.rv_chat_history);
        tv_info = findViewById(R.id.tv_info);
        tv_score = findViewById(R.id.tv_score);
    }

    private void setViews() {
        iv_send.setOnClickListener(view -> {
            ChatMessageModel chatMessageModel = new ChatMessageModel(MainActivity.username, Objects.requireNonNull(et_message.getText()).toString(), true);
            MessageModel messageModel = new MessageModel(chatMessageModel, null, null);
            ConnectionUtils.SendMessage(GameAndChatActivity.this, messageModel);

            chatViewModel.addChat(chatMessageModel);
            et_message.setText("");
        });

        if (GameStateObservable.getInstance().isOpponentReady()) {
            if (GameStateObservable.getInstance().isYourTurn()) {
                setInfo("Your turn");
            } else {
                setInfo("Opponent turn");
            }
        } else {
            setInfo("Waiting for opponent");
        }
        setScore(0, 0);
        setGrid();
        setRecycleView();
    }

    private void setScore(int yourScore, int opponentScore) {
        tv_score.setText(getResources().getString(R.string.score, Integer.toString(yourScore), Integer.toString(opponentScore)));
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

        GameStateObservable boardState = GameStateObservable.getInstance();
        boolean isYourTurn = boardState.isYourTurn();
        TicTac type = boardState.getBoard()[g.y][g.x].getType();
        boolean areYouReady = boardState.isAreYouReady();
        boolean isOpponentReady = boardState.isOpponentReady();

        if (isYourTurn && type == TicTac.NONE && areYouReady && isOpponentReady) {
            ClickMessageModel clickMessageModel = new ClickMessageModel(g.x, g.y);
            MessageModel messageModel = new MessageModel(null, clickMessageModel, null);
            ConnectionUtils.SendMessage(this, messageModel);
            boardState.setYourTurn(false);
            boardState.modifyCell(g.y, g.x, boardState.getPlayerType());
            setInfo("Opponents turn");
            boardState.checkAndSetWinner();
        }
    }

    public void setInfo(String info) {
        if (tv_info != null) {
            tv_info.setText(info);
        }
    }

    private void recreateBoard() {
        for (GridLayoutItem[] myView : myViews) {
            for (GridLayoutItem gridLayoutItem : myView) {
                gridLayoutItem.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.background));
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof IsGameOverModel) {
            GameStateObservable game = GameStateObservable.getInstance();
            IsGameOverModel isGameOver = (IsGameOverModel) o;
            if (isGameOver.isGameOver()) {
                setViewsForGameOver(game.getWinner(), game.getWinType());
                createDialogForGameOver(game.getWinner());
            }
        }

        if (o instanceof CellModel) {
            CellModel changedCell = (CellModel) o;
            if (GameStateObservable.getInstance().isYourTurn()) {
                setInfo("Your turn");
            } else {
                setInfo("Opponent turn");
            }
            modifyViews(changedCell);
        }

        if (o instanceof IsOpponentReadyModel) {
            IsOpponentReadyModel isOpponentReady = (IsOpponentReadyModel) o;
            if (isOpponentReady.isOpponentReady()) {
                if (GameStateObservable.getInstance().isYourTurn()) {
                    setInfo("Your turn");
                } else {
                    setInfo("Opponent turn");
                }
            }
        }

        if (observable instanceof ConnectionStateObservable) {
            ConnectionStateObservable connectionState = (ConnectionStateObservable) observable;
            switch (connectionState.getConnectionState()) {
                case STATUS_OK:
                    Toast.makeText(this, "Connected to: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_DISCONNECTED:
                    Toast.makeText(this, "Disconnected from: " + connectionState.getEndPointId(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        if (o instanceof ChatMessageModel) {
            ChatMessageModel chat = (ChatMessageModel) o;
            mAdapter.addToDataSet(chat);
        }
    }

    private void createDialogForGameOver(Winner winner) {
        Dialog dialog = null;
        GameStateObservable boardState = GameStateObservable.getInstance();
        //   GameState gameState = GameState.getInstance();
        if (boardState.getPlayerType() == TicTac.TIC && winner == Winner.TIC
                || boardState.getPlayerType() == TicTac.TAC && winner == Winner.TAC) {
            GameStateObservable.getInstance().getScore().increaseYourScore();
            dialog = new Dialog(GameAndChatActivity.this, "you_won", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    boardState.reInitGame(TicTac.TAC, true);
                    recreateBoard();
                    if (!boardState.isOpponentReady()) {
                        setInfo("Waiting for opponent");
                    }
                }

                @Override
                public void onNegative() {
                    returnToLobby();
                }
            });
        }

        if (boardState.getPlayerType() == TicTac.TIC && winner == Winner.TAC
                || boardState.getPlayerType() == TicTac.TAC && winner == Winner.TIC) {
            GameStateObservable.getInstance().getScore().increaseOpponentScore();
            dialog = new Dialog(GameAndChatActivity.this, "you_lost", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    boardState.reInitGame(TicTac.TIC, true);
                    recreateBoard();
                    if (!boardState.isOpponentReady()) {
                        setInfo("Waiting for opponent");
                    }
                }

                @Override
                public void onNegative() {
                    returnToLobby();
                }
            });
        }
        if (winner == Winner.DRAW) {
            dialog = new Dialog(GameAndChatActivity.this, "draw", "", new TwoOptionsDialog() {
                @Override
                public void onPositive() {
                    ConnectionUtils.SendMessage(getApplicationContext(), new MessageModel(null, null, true));
                    if (boardState.getPlayerType() == TicTac.TIC) {
                        boardState.reInitGame(TicTac.TAC, true);
                    } else {
                        boardState.reInitGame(TicTac.TIC, true);
                    }
                    recreateBoard();
                    if (!boardState.isOpponentReady()) {
                        setInfo("Waiting for opponent");
                    }
                }

                @Override
                public void onNegative() {
                    returnToLobby();
                }
            });
        }

        setScore(GameStateObservable.getInstance().getScore().getYourScore(), GameStateObservable.getInstance().getScore().getOpponentScore());
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
    public void onBackPressed() {
        Dialog dialog = new Dialog(this, "quit_current_match", "", new TwoOptionsDialog() {
            @Override
            public void onPositive() {
                returnToLobby();
            }

            @Override
            public void onNegative() {

            }
        });
        dialog.show();
    }

    private void returnToLobby() {
        ConnectionUtils.Disconnect(getApplicationContext(), GameStateObservable.getInstance().getConnectedEndPoint());
        MainActivityStateObservable.getInstance().reInit();
        GameStateObservable.getInstance().reInitGame(TicTac.TAC, false);
        GameStateObservable.getInstance().getScore().reset();
        Intent intent = new Intent(GameAndChatActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameStateObservable.deleteObserver(this);
        connectionStateObservable.deleteObserver(this);
    }
}