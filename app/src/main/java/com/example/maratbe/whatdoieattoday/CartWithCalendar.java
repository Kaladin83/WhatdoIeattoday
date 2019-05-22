package com.example.maratbe.whatdoieattoday;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.interfaces.OnClickCalendarListener;
import com.example.maratbe.whatdoieattoday.objects.MyCalendar;
import com.example.maratbe.whatdoieattoday.objects.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartWithCalendar extends Fragment implements Constants, OnClickCalendarListener{
    private String currentDate;
    private View mainView;
    private TextView saladsTxt, mainTxt, sideTxt;
    private MyCalendar myCalendar;
    private LinearLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.cart_with_calendar, container, false);

        Date currDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        currentDate = df.format(currDate);

        MainActivity.getMyDbInstance().fetchOrders(currentDate.substring(0,6));
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        myCalendar = new MyCalendar();
        myCalendar.setWidth(MainActivity.getScreenWidth()  - 20*MainActivity.getLogicalDensity());
        myCalendar.setOnClickCalendarListener(this);
        childFragTrans.replace(R.id.my_calendar_holder, myCalendar).commit();

        tableLayout = mainView.findViewById(R.id.table_layout);
        tableLayout.setBackground(Utils.createBorder(10, Color.TRANSPARENT,1));

        saladsTxt = mainView.findViewById(R.id.salads_txt);
        mainTxt = mainView.findViewById(R.id.main_dish_txt);
        sideTxt = mainView.findViewById(R.id.side_dish_txt);

        populateGrid(currentDate);
        return mainView;
    }

    @Override
    public void onBackButtonClicked(String month) {
        MainActivity.getMyDbInstance().fetchOrders(month);
        showCurrentChoice(month);
    }

    @Override
    public void onNextButtonClicked(String month) {
        MainActivity.getMyDbInstance().fetchOrders(month);
        showCurrentChoice(month);
    }

    @Override
    public void onCellClicked(String date) {
        populateGrid(date);
    }

    public void updateCart() {
        Date currDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        if (myCalendar.getSelectedDate().equals(df.format(currDate)))
        {
            populateGrid(df.format(currDate));
        }
    }

    private void showCurrentChoice(String month) {
        if (month.equals(currentDate.substring(0,6)))
        {
            populateGrid(currentDate);
        }
        else
        {
            tableLayout.setVisibility(View.GONE);
        }
    }

    private void populateGrid(String date) {
        boolean found = false;
        int saladsCnt = 0, mainCnt = 0, sideCnt = 0;
        StringBuilder saladsStr = new StringBuilder();
        StringBuilder mainStr = new StringBuilder();
        StringBuilder sideStr = new StringBuilder();
        int day = Integer.parseInt(date.substring(6,8));
        for (Order order: MainActivity.getListOfOrders())
        {
            if (order.getDay() == day)
            {
                switch (order.getCategory())
                {
                    case SALADS:
                        saladsStr.append(saladsCnt == 0? order.getDishName(): ", "+order.getDishName());
                        saladsCnt++; break;
                    case MAIN_DISH:
                        mainStr.append(mainCnt == 0? order.getDishName(): ", "+order.getDishName());
                        mainCnt++; break;
                    default:
                        sideStr.append(sideCnt == 0? order.getDishName(): ", "+order.getDishName());
                        sideCnt++; break;

                }
                found = true;
            }

            if(order.getDay() > day)
            {
                break;
            }
        }

        if (found)
        {
            saladsTxt.setText(saladsStr.toString());
            mainTxt.setText(mainStr.toString());
            sideTxt.setText(sideStr.toString());
            tableLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            tableLayout.setVisibility(View.GONE);
        }
    }
}
