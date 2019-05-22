package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.DayOrder;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.Order;

import java.util.ArrayList;

public class CartWithList  extends Fragment implements Constants {

    private View mainView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter orderAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.cart_with_list, container, false);

        MainActivity.getMyDbInstance().fetchOrders();
        context = getContext();
        Activity activity = getActivity();
        orderAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(context);
        recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        LinearLayout noDataLayout = mainView.findViewById(R.id.no_data_layout);
        TextView noDataTxt = mainView.findViewById(R.id.no_data_txt);
        noDataTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"YamSuf.ttf"));

        if (MainActivity.getListOfOrders().size() == 0 || MainActivity.getMapOfProperties().get(LOGGED_IN).equals(OUT))
        {
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            fillUpSparseArrayOfDateColors();
            fillUpDayOrders();
            recyclerView.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
        return mainView;
    }

    private void fillUpSparseArrayOfDateColors() {
        MainActivity.getsArrayOfDateColors().put(0, GRAY_3);
        MainActivity.getsArrayOfDateColors().put(1, RED_5);
        MainActivity.getsArrayOfDateColors().put(2, RED_6);
        MainActivity.getsArrayOfDateColors().put(3, GREEN_4);
        MainActivity.getsArrayOfDateColors().put(4, GREEN_5);
        MainActivity.getsArrayOfDateColors().put(5, GREEN_6);
        MainActivity.getsArrayOfDateColors().put(6, YELLOW_4);
        MainActivity.getsArrayOfDateColors().put(7, YELLOW_5);
        MainActivity.getsArrayOfDateColors().put(8, PURPLE_4);
        MainActivity.getsArrayOfDateColors().put(9, PURPLE_5);
        MainActivity.getsArrayOfDateColors().put(10, BLUE_4);
        MainActivity.getsArrayOfDateColors().put(11, BLUE_5);
    }

    public void fillUpDayOrders()
    {
        int count;
        int color = Utils.getRandomDateColor();
        int prevMonth = MainActivity.getListOfOrders().get(0).getMonth();
        MainActivity.getListOfDayOrders().clear();
        for (int i = 0; i < MainActivity.getListOfOrders().size(); i+=count)
        {
            count = 0;
            boolean found = false;
            Order order1 = MainActivity.getListOfOrders().get(i);

            ArrayList<Item> items = new ArrayList<>();
            for (Order order2: MainActivity.getListOfOrders())
            {
                if (order1.getMonth() == order2.getMonth() && order1.getDay() == order2.getDay())
                {
                    items.add(new Item(order2.getDishName(), order2.getCategory(), -1));
                    count++;
                    found = true;
                }
                else
                {
                    if (found)
                    {
                        break;
                    }
                }

                if (prevMonth != order1.getMonth())
                {
                    color = Utils.getRandomDateColor();
                    while (color == MainActivity.getListOfDayOrders().get(MainActivity.getListOfDayOrders().size()-1).getMonthColor())
                    {
                        color = Utils.getRandomDateColor();
                    }

                    prevMonth = order1.getMonth();
                }
            }
            MainActivity.getListOfDayOrders().add(new DayOrder(order1.getDay() , order1.getMonth(), items, color));
        }
    }

    public void refreshList()
    {
        orderAdapter.notifyDataSetChanged();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        int selectedPosition = -1;

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView dateTxt, saladsTxt, mainTxt, sideTxt;
            LinearLayout tableLayout, mainRowLayout, entireItemLayout;

            private MyViewHolder(View v) {
                super(v);
                dateTxt = v.findViewById(R.id.date_txt);
                tableLayout = v.findViewById(R.id.table_layout);
                mainRowLayout = v.findViewById(R.id.main_layout);
                entireItemLayout = v.findViewById(R.id.entire_item_layout);
                saladsTxt = v.findViewById(R.id.salads_txt);
                mainTxt = v.findViewById(R.id.main_dish_txt);
                sideTxt = v.findViewById(R.id.side_dish_txt);

                tableLayout.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1));
            }
        }

        RecyclerViewAdapter() {

        }

        @Override
        public int getItemCount() {
            return MainActivity.getListOfDayOrders().size();
        }

        @Override
        public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.populator_of_order, parent, false);

            return new RecyclerViewAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
            int pos = holder.getAdapterPosition();
            DayOrder dayOrder = MainActivity.getListOfDayOrders().get(pos);
            holder.mainRowLayout.setOnClickListener(v1 -> {
                selectedPosition = pos;
                notifyDataSetChanged();
            });
            if (pos == selectedPosition)
            {
                holder.tableLayout.setVisibility(holder.tableLayout.getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);
                holder.entireItemLayout.setBackgroundColor(holder.tableLayout.getVisibility() == View.VISIBLE? RED_7: Color.TRANSPARENT);
            }
            else
            {
                holder.tableLayout.setVisibility(View.GONE);
                holder.entireItemLayout.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.dateTxt.setBackground(Utils.createBorder(10, dayOrder.getMonthColor(), 1));
            holder.dateTxt.setText(Utils.getStringDate(dayOrder.getDay(), dayOrder.getMonth()));
            holder.saladsTxt.setText(Utils.getDishesOfCategory(dayOrder.getItems(), SALADS));
            holder.mainTxt.setText(Utils.getDishesOfCategory(dayOrder.getItems(), MAIN_DISH));
            holder.sideTxt.setText(Utils.getDishesOfCategory(dayOrder.getItems(), SIDE_DISH));
        }
    }
}
