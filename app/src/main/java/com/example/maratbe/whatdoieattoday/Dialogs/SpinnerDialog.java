package com.example.maratbe.whatdoieattoday.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.MainActivity;
import com.example.maratbe.whatdoieattoday.R;
import com.example.maratbe.whatdoieattoday.interfaces.Constants;

/**
 * Class creates dialog of category delete confirmation.
 * Gives the option to move items to existing category or to move them to new one.
 */
public abstract class SpinnerDialog extends Dialog implements View.OnClickListener, Constants {
    private int width, backgroundColor, headerColor, buttonColor, headerStr, bodyStr;
    private int[] buttons;
    private Activity activity;
    private String[] spinnerValues;
    private RelativeLayout mainLayout;
    private Spinner spinner;

    public SpinnerDialog(Activity activity, int width, int[] colors, int[] texts, int[] buttons, String[] spinnerValues) {
        super(activity);
        this.activity = activity;
        this.width = width;
        this.backgroundColor = colors[0];
        this.headerColor = colors[1];
        this.buttonColor = colors[2];
        this.headerStr = texts[0];
        this.bodyStr = texts[1];
        this.buttons = buttons;
        this.spinnerValues = spinnerValues;
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

        rParams = new RelativeLayout.LayoutParams(MainActivity.getLogicalDensity()* 200, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.setMargins(0,10*MainActivity.getLogicalDensity(),0, 0);
        rParams.addRule(RelativeLayout.BELOW, R.id.body_txt);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        spinner = new Spinner(getContext());
        spinner.setId(R.id.email_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.message_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mainLayout.addView(spinner, rParams);

        rParams = new RelativeLayout.LayoutParams(this.width/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.BELOW, R.id.email_spinner);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        switch (buttons.length)
        {
            case 1:
                createButton(0, buttons[0], rParams, buttonColor);  break;
            case 2:
                createButton(1, buttons[1], rParams, buttonColor);

                rParams = new RelativeLayout.LayoutParams((int)(this.width/3.5), LinearLayout.LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.BELOW, R.id.email_spinner);
                rParams.addRule(RelativeLayout.END_OF, 1);
                createButton(0, buttons[0], rParams, buttonColor); break;
        }
    }

    private void createTextView(int id, int text, int paddingVer, RelativeLayout.LayoutParams rParams, int backgroundColor, int textColor) {
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
                okButtonPressed(spinner.getSelectedItem().toString()); break;
            case 1:
                cancelButtonPressed(); break;
        }
    }

    public abstract void okButtonPressed(String email);

    public abstract void cancelButtonPressed();
}

