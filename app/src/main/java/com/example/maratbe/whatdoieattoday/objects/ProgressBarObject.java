package com.example.maratbe.whatdoieattoday.objects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;

import com.example.maratbe.whatdoieattoday.R;
import com.example.maratbe.whatdoieattoday.interfaces.Constants;

import java.util.ArrayList;

public class ProgressBarObject implements Constants {
    private Drawable backgroundImg;
    private Integer color;

    public ProgressBarObject(){};

    public ProgressBarObject(Drawable backgroundImg, Integer color) {
        this.backgroundImg = backgroundImg;
        this.color = color;
    }

    public Drawable getBackgroundImage() {
        return backgroundImg;
    }

    public void setBackgroundImage(Drawable backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public void loadList(ArrayList<ProgressBarObject> listOfBackgrounds, ArrayList<String> listOfIndicators, Context context)
    {
        listOfBackgrounds.add(new ProgressBarObject(ContextCompat.getDrawable(context, R.drawable.meal1), RED_1));
        listOfBackgrounds.add(new ProgressBarObject(ContextCompat.getDrawable(context, R.drawable.meal2), RED_1));
        listOfBackgrounds.add(new ProgressBarObject(ContextCompat.getDrawable(context, R.drawable.meal3), Color.WHITE));
        listOfBackgrounds.add(new ProgressBarObject(ContextCompat.getDrawable(context, R.drawable.meal4), Color.WHITE));
        listOfBackgrounds.add(new ProgressBarObject(ContextCompat.getDrawable(context, R.drawable.meal5), Color.RED));

        listOfIndicators.add("BallPulseIndicator");
        listOfIndicators.add("BallClipRotateIndicator");
        listOfIndicators.add("BallClipRotatePulseIndicator");
        listOfIndicators.add("BallClipRotateMultipleIndicator");
        listOfIndicators.add("BallTrianglePathIndicator");
        listOfIndicators.add("LineScalePartyIndicator");
        listOfIndicators.add("LineScalePulseOutIndicator");
        listOfIndicators.add("SemiCircleSpinIndicator");
    }
}
