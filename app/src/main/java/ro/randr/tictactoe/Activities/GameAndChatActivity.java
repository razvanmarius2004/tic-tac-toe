package ro.randr.tictactoe.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.Toast;

import ro.randr.tictactoe.R;
import ro.randr.tictactoe.Utils.ConnectionUtils;
import ro.randr.tictactoe.Views.GridLayoutItem;

public class GameAndChatActivity extends AppCompatActivity {

    private GridLayoutItem[] myViews;
    private GridLayout myGridLayout;
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
        myGridLayout = findViewById(R.id.gl_grid);
        et_message = findViewById(R.id.et_message);
        iv_send = findViewById(R.id.iv_send);
    }

    private void setViews() {
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionUtils.SendMessage(getApplicationContext(), et_message.getText().toString());
                et_message.setText("");
            }
        });
        setGrid();
    }

    private void setGrid() {
        int numOfCol = myGridLayout.getColumnCount();
        int numOfRow = myGridLayout.getRowCount();
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
                });
                myViews[yPos * numOfCol + xPos] = tView;
                myGridLayout.addView(tView);
            }
        }

        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {

                        int pLength;
                        final int MARGIN = 5;

                        int pWidth = myGridLayout.getWidth();
                        int pHeight = myGridLayout.getHeight();
                        int numOfCol = myGridLayout.getColumnCount();
                        int numOfRow = myGridLayout.getRowCount();

                        //Set myGridLayout equal width and height
                        pLength = Math.min(pWidth, pHeight);
                        ViewGroup.LayoutParams pParams = myGridLayout.getLayoutParams();
                        pParams.width = pLength;
                        pParams.height = pLength;
                        myGridLayout.setLayoutParams(pParams);

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


                        myGridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }});
    }
}