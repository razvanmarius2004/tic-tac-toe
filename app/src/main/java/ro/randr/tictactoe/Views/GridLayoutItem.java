package ro.randr.tictactoe.Views;

import android.content.Context;

import ro.randr.tictactoe.Models.TicTac;

public class GridLayoutItem extends androidx.appcompat.widget.AppCompatImageView {

    public int x;
    public int y;
    public TicTac type;
    public GridLayoutItem(Context context) {
        super(context);
    }
}
