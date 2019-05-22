package com.example.maratbe.whatdoieattoday;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.resources.TextAppearance;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;

public class Settings extends AppCompatActivity implements Constants,View.OnClickListener {
    private Settings settings;
    private Switch alertsSwitch;
    private RadioButton listDisplayRadio, calendarDisplayRadio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        settings = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(this);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof AppCompatImageButton) {
                child.setId(R.id.home_btn);
                break;
            }
        }

        TextView headerTxt = findViewById(R.id.header_txt);
        headerTxt.setTypeface(Typeface.createFromAsset(getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);
        alertsSwitch = findViewById(R.id.alerts_switch);
        alertsSwitch.setChecked(MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN) &&
                MainActivity.getMapOfProperties().get(ALERTS).equals("Y"));
        alertsSwitch.setTextOff("off");
        alertsSwitch.setTextOn("on");
        alertsSwitch.setShowText(true);

        listDisplayRadio = findViewById(R.id.list_radio);
        listDisplayRadio.setTextColor(GRAY_1);
        calendarDisplayRadio = findViewById(R.id.calendar_radio);
        calendarDisplayRadio.setTextColor(GRAY_1);
        if (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN) &&
                MainActivity.getMapOfProperties().get(CART_VIEW).equals(DISPLAY_CALENDAR))
        {
            calendarDisplayRadio.setChecked(true);
        }
        else
        {
            listDisplayRadio.setChecked(true);
        }
    }

    public void closeSettings(int code) {
        Intent returnIntent = new Intent();
        setResult(code, returnIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home_btn) {

            if (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN))
            {
                String display = calendarDisplayRadio.isChecked() ? DISPLAY_CALENDAR : DISPLAY_LIST;
                if(!MainActivity.getMapOfProperties().get(CART_VIEW).equals(display))
                {
                    MainActivity.getMyDbInstance().updateProperties(ALERTS, alertsSwitch.isChecked() ? "Y" : "N");
                    MainActivity.getMyDbInstance().updateProperties(CART_VIEW, display);
                    MainActivity.getMyDbInstance().fetchProperties();
//                    Intent i = new Intent(this, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent returnIntent = new Intent();
                    setResult(RC_OK, returnIntent);
                    finish();
                    //startActivity(i);
                }
                else
                {
                    closeSettings(RC_CANCEL);
                }
            }
            else
            {
                int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
                String[] texts = new String[]{getString(R.string.sorry_message), getString(R.string.need_to_connect)};
                int[] buttons = new int[]{R.string.ok_dialog_btn};
                Utils.showMessage(this, texts, colors, buttons, ONLY_USERS_IN_SETTINGS);
            }
        }
    }
}
