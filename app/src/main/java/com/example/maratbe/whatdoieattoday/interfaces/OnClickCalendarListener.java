package com.example.maratbe.whatdoieattoday.interfaces;

public interface OnClickCalendarListener {
    void onBackButtonClicked(String month);
    void onNextButtonClicked(String month);
    void onCellClicked(String date);
}
