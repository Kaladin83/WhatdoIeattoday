package com.example.maratbe.whatdoieattoday.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.MainActivity;
import com.example.maratbe.whatdoieattoday.R;
import com.example.maratbe.whatdoieattoday.interfaces.Constants;

/**
 * Class creates dialog of category delete confirmation.
 * Gives the option to move items to existing category or to move them to new one.
 */
public abstract class DisableOptionDialog extends Dialog implements View.OnClickListener, Constants {
    private int width, backgroundColor, headerColor, buttonColor;
    private String headerStr, bodyStr;
    private int[] buttons;
    private RelativeLayout mainLayout;
    private CheckBox disableCheckBox;

    public DisableOptionDialog(Activity activity, int width, int[] colors, String[] texts, int[] buttons) {
        super(activity);
        this.width = width;
        this.backgroundColor = colors[0];
        this.headerColor = colors[1];
        this.buttonColor = colors[2];
        this.headerStr = texts[0];
        this.bodyStr = texts[1];
        this.buttons = buttons;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_dialog);

        FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(this.width, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainLayout = findViewById(R.id.main_dialog_layout);
        mainLayout.setLayoutParams(fParams);
        mainLayout.setBackgroundColor(backgroundColor);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(this.width, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        createTextView(R.id.header_txt, headerStr, 15, rParams, headerColor, Color.WHITE);

        rParams = new RelativeLayout.LayoutParams(this.width, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.BELOW, R.id.header_txt);
        createTextView(R.id.body_txt, bodyStr, 30, rParams, Color.TRANSPARENT, Color.BLACK);

        rParams = new RelativeLayout.LayoutParams(this.width, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.BELOW, R.id.body_txt);
        createCheckBox(rParams);

        rParams = new RelativeLayout.LayoutParams(this.width/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.BELOW, R.id.check_box_r_layout);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        switch (buttons.length)
        {
            case 1:
                createButton(0, buttons[0], rParams, buttonColor);  break;
            case 2:
                createButton(1, buttons[1], rParams, buttonColor);

                rParams = new RelativeLayout.LayoutParams((int)(this.width/3.5), LinearLayout.LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.BELOW, R.id.check_box_r_layout);
                rParams.addRule(RelativeLayout.END_OF, 1);
                createButton(0, buttons[0], rParams, buttonColor); break;
        }
    }

    private void createCheckBox(RelativeLayout.LayoutParams rParams) {
        RelativeLayout checkBoxLayout = new RelativeLayout(getContext());
        checkBoxLayout.setId(R.id.check_box_r_layout);
       // checkBoxLayout.setBackgroundColor(RED_6);
        mainLayout.addView(checkBoxLayout, rParams);

        rParams = new RelativeLayout.LayoutParams(35*MainActivity.getLogicalDensity(), LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        disableCheckBox = new CheckBox(getContext());
        disableCheckBox.setLayoutParams(rParams);
        disableCheckBox.setPadding(0,0,10*MainActivity.getLogicalDensity(),0);
        disableCheckBox.setId(R.id.disable_check_box);
        checkBoxLayout.addView(disableCheckBox, rParams);

        rParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.START_OF, R.id.disable_check_box);
        TextView txt = createTextViewReturn(R.id.check_box_txt, R.string.dont_show_again, 5*MainActivity.getLogicalDensity() , rParams, Color.TRANSPARENT, Color.BLACK);
        checkBoxLayout.addView(txt, rParams);
    }

    private void createTextView(int id, String text, int paddingVer, RelativeLayout.LayoutParams rParams, int backgroundColor, int textColor) {
        TextView txt = new TextView(getContext());
        txt.setBackgroundColor(backgroundColor);
        txt.setId(id);
        txt.setText(text);
        txt.setTextColor(textColor);
        txt.setTextSize(DIALOG_TEXT_SIZE);
        txt.setPadding(30, paddingVer, 30, paddingVer);
        mainLayout.addView(txt, rParams);
    }

    private TextView createTextViewReturn(int id, int text, int paddingVer, RelativeLayout.LayoutParams rParams, int backgroundColor, int textColor) {
        TextView txt = new TextView(getContext());
        txt.setBackgroundColor(backgroundColor);
        txt.setId(id);
        txt.setText(text);
        txt.setTextColor(textColor);
        txt.setTextSize(DIALOG_TEXT_SIZE);
        txt.setPadding(30, paddingVer, 30, paddingVer);
        return txt;
    }

    private void createButton(int id, int text, RelativeLayout.LayoutParams rParams, int color) {
        Button btn = new Button(getContext());
        btn.setTextColor(color);
        btn.setText(text);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setId(id);
        btn.setTextSize(DIALOG_TEXT_SIZE);
        btn.setOnClickListener(this);
        mainLayout.addView(btn, rParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case 0:
                okButtonPressed(disableCheckBox.isChecked()); break;
            case 1:
                cancelButtonPressed(); break;
        }
    }

    public abstract void okButtonPressed(boolean disable);

    public abstract void cancelButtonPressed();
}
