package com.example.maratbe.whatdoieattoday.objects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.CartWithCalendar;
import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.MainActivity;
import com.example.maratbe.whatdoieattoday.interfaces.OnClickCalendarListener;
import com.example.maratbe.whatdoieattoday.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyCalendar extends Fragment implements Constants, View.OnClickListener {
    private OnClickCalendarListener listener;
    private View mainView;
    private TextView dateTxt;
    private LinearLayout.LayoutParams lParams;
    private Context context;
    private RecyclerViewAdapter cartAdapter;
    private ArrayList<String> daysList;
    private int month, year, currentDay, currentMonth, currentYear, selectedPosition, currDayPosition = -1;
    private int cellSize = 40 * MainActivity.getLogicalDensity();
    private String selectedDate = "";

    public MyCalendar() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.my_calendar, container, false);
        mainView.setTag("my_calendar");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        currentYear = Integer.parseInt(df.format(date).substring(0,4));
        currentMonth = Integer.parseInt(df.format(date).substring(4,6)) -1;
        currentDay = Integer.parseInt(df.format(date).substring(6,8));
        selectedPosition = currentDay+7;
        month = currentMonth;
        year = currentYear;
        selectedDate = year + "" + paddingNumbers(month+1) + "" + paddingNumbers(currentDay);
        context = getContext();

        setFields();
        fillUpArray();
        setCurrentDay();
        createRecycler();
        return mainView;
    }

    public void setWidth(int width)
    {
        cellSize = width / 8;
        System.out.println("width passed in DP: " +width/3);
        System.out.println("cell size in DP: " +cellSize/3);
    }

    private void setSelectedDate(boolean isSelected) {
        selectedDate = year+""+paddingNumbers(month) + (!isSelected? "40": paddingNumbers(Integer.parseInt(daysList.get(selectedPosition))));
    }

    private String paddingNumbers(int number) {
        if (number > 0 && number < 10)
        {
            return "0"+number;
        }
        return number+"";
    }


    private void setCurrentDay() {
        for(int i = 7; i < daysList.size(); i++) {
            if (!daysList.get(i).equals("")) {
                if (month == currentMonth && year == currentYear && Integer.parseInt(daysList.get(i)) == currentDay) {
                    selectedPosition = i;
                    currDayPosition = i;
                    break;
                }
            }
        }
    }

    private void setFields() {
        lParams = new LinearLayout.LayoutParams(cellSize*7, LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout headerLayout = mainView.findViewById(R.id.header_layout);
        headerLayout.setBackgroundColor(RED_6);
        headerLayout.setLayoutParams(lParams);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout nextLayout = mainView.findViewById(R.id.next_layout);
        nextLayout.setOnClickListener(this);
        LinearLayout backLayout = mainView.findViewById(R.id.back_layout);
        backLayout.setOnClickListener(this);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams((int)(cellSize*5.5) - 40*MainActivity.getLogicalDensity() , 45* MainActivity.getLogicalDensity());
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        dateTxt = mainView.findViewById(R.id.date_txt);
        dateTxt.setText(String.valueOf(convertMonth(month) + " " + year));
        dateTxt.setLayoutParams(rParams);
        dateTxt.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
    }

    private void fillUpArray() {
        daysList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        c.set(Calendar.YEAR, month == 11 ? year + 1 : year);
        c.set(Calendar.MONTH, month == 11 ? 1 : month + 1);
        c.add(Calendar.DATE, -1);
        DateFormat sdf = new SimpleDateFormat("dd", Locale.CANADA);
        int lastDayOfMonth = Integer.parseInt(sdf.format(c.getTime()));


        int index = 0;
        do {
            fillUpFirstRow(firstDayOfWeek, lastDayOfMonth, index);
            index += 7;
        }
        while (index < lastDayOfMonth + 7);

        fillUpWithNames();
    }

    private void fillUpWithNames() {
        daysList.add(0, "ש");
        daysList.add(1, "ו");
        daysList.add(2, "ה");
        daysList.add(3, "ד");
        daysList.add(4, "ג");
        daysList.add(5, "ב");
        daysList.add(6, "א");
    }

    private void fillUpFirstRow(int firstDay, int lastDayOfMonth, int startFrom) {
        for (int i = startFrom + 7; i > startFrom; i--) {
            if (i < firstDay || i > lastDayOfMonth + firstDay-1) {
                daysList.add("");
            } else {
                daysList.add(String.valueOf(i - firstDay + 1));
            }
        }
    }

    String convertMonth(int month) {
        switch (month) {
            case 0:
                return "ינואר";
            case 1:
                return "פברואר";
            case 2:
                return "מרץ";
            case 3:
                return "אפריל";
            case 4:
                return "מאי";
            case 5:
                return "יוני";
            case 6:
                return "יולי";
            case 7:
                return "אוגוסט";
            case 8:
                return "ספטמבר";
            case 9:
                return "אוקטובר";
            case 10:
                return "נובמבר";
            default:
                return "דצמבר";
        }
    }

    public void createRecycler() {
        lParams = new LinearLayout.LayoutParams(cellSize*7, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.gravity = Gravity.CENTER_HORIZONTAL;
        cartAdapter = new RecyclerViewAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        RecyclerView recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutParams(lParams);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_btn || v.getId() == R.id.next_layout)
        {
            year = month == 11? year+1: year;
            month = month == 11? 0: month+1;
            listener.onNextButtonClicked(year+""+ (month+1));
            updateCalendar();
        }
        else
        {
            year = month == 0? year-1: year;
            month = month == 0? 11: month-1;
            listener.onBackButtonClicked(year+""+ (month+1));
            updateCalendar();
        }
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    private void updateCalendar() {
        selectedPosition = -1;
        dateTxt.setText(String.valueOf(convertMonth(month) + " " + year));
        fillUpArray();
        setCurrentDay();
        cartAdapter.notifyDataSetChanged();
    }

    public void setOnClickCalendarListener(CartWithCalendar cartWithCalendar) {
        listener = cartWithCalendar;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView dayTxt;

            private MyViewHolder(View v) {
                super(v);
                dayTxt = v.findViewById(R.id.day_txt);
                dayTxt.setOnClickListener(v1 -> {
                    TextView txtView = ((TextView)v1);

                    if (!txtView.getText().toString().equals("") && (int) txtView.getTag() > 6 )
                    {
                        v1.setSelected(!txtView.isSelected());
                        selectedPosition = txtView.isSelected()? (int) txtView.getTag(): -1;
                        setSelectedDate(txtView.isSelected());
                        listener.onCellClicked(selectedDate);
                        notifyDataSetChanged();
                    }
                });
            }
        }

        RecyclerViewAdapter() {

        }

        @Override
        public int getItemCount() {
            return daysList.size();
        }

        @Override
        public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.populator_of_calendar, parent, false);

            return new RecyclerViewAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
            lParams = new LinearLayout.LayoutParams(cellSize, cellSize-3*MainActivity.getLogicalDensity());
            holder.dayTxt.setTag(position);
            holder.dayTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.dayTxt.setText(daysList.get(position));
            holder.dayTxt.setLayoutParams(lParams);

            if (position == selectedPosition && selectedPosition > 6)
            {
                holder.dayTxt.setSelected(true);
            }
            else
            {
                holder.dayTxt.setSelected(false);
            }

            if (position < 7 ||  holder.dayTxt.getText().toString().equals(""))
            {
                holder.dayTxt.setTextColor(ORANGE_1);
                holder.dayTxt.setClickable(false);
            }
            else
            {
                holder.dayTxt.setTextColor(holder.dayTxt.isSelected()? Color.WHITE: Color.BLACK);
            }

            if (currentMonth == month && currentYear == year && currDayPosition != selectedPosition && currDayPosition == position)
            {
                holder.dayTxt.setTextColor(RED_2);
            }
        }
    }
}
