package ro.randr.tictactoe.Utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import ro.randr.tictactoe.Interfaces.TwoOptionsDialog;
import ro.randr.tictactoe.R;

public class Dialog extends AlertDialog {

    private final TwoOptionsDialog mClickListener;
    private final Context mContext;
    private final String mDialogType;
    private final String mMessage;

    private AppCompatTextView tv_text;
    private AppCompatButton btn_positive;
    private AppCompatButton btn_negative;

    public Dialog(@NonNull Context context, String dialogType, String message, TwoOptionsDialog clickListener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mContext = context;
        mDialogType = dialogType;
        mMessage = message;
        mClickListener = clickListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setDialogLogic();
    }

    private void setDialogLogic() {
        getViews();
        setViews();

    }

    private void getViews() {
        tv_text = findViewById(R.id.tv_text);
        btn_positive = findViewById(R.id.btn_positive);
        btn_negative = findViewById(R.id.btn_negative);
    }

    private void setViews() {
        btn_positive.setOnClickListener(view -> {
            mClickListener.onPositive();
            Dialog.this.dismiss();
        });
        btn_negative.setOnClickListener(view -> {
            mClickListener.onNegative();
            Dialog.this.dismiss();
        });

        switch (mDialogType) {
            case "disclaimer":
                setViewsForDisclaimer();
                break;
            case "accept_connection":
                setViewsForConnection();
                break;

            case "you_won":
                setViewsForWin();
                break;
            case "you_lost":
                setViewsForLose();
                break;
            case "draw":
                setViewsForDraw();
                break;
        }

    }

    private void setViewsForDisclaimer() {
        tv_text.setText(mMessage);
        btn_negative.setText(R.string.button_no);
        btn_positive.setText(R.string.button_yes);
    }

    private void setViewsForConnection() {
        tv_text.setText(mMessage);
        btn_negative.setText(R.string.button_no);
        btn_positive.setText(R.string.button_yes);
    }

    private void setViewsForWin() {
        tv_text.setText(R.string.you_won);
        btn_negative.setText(R.string.button_no);
        btn_positive.setText(R.string.button_yes);
    }

    private void setViewsForLose() {
        tv_text.setText(R.string.you_lose);
        btn_negative.setText(R.string.button_no);
        btn_positive.setText(R.string.button_yes);
    }

    private void setViewsForDraw() {
        tv_text.setText(R.string.draw);
        btn_negative.setText(R.string.button_no);
        btn_positive.setText(R.string.button_yes);
    }
}
