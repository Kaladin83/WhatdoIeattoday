package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.interfaces.OnUpdateMainActivityListener;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.Order;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by MARATBE on 11/8/2018.
 */

public class Menu extends Fragment implements View.OnClickListener, Constants {
    private OnUpdateMainActivityListener listener;
    private View mainView;
    private Activity activity;
    private Context context;
    private RecyclerViewAdapter menuAdapter;
    private String currentDate = "";
    private LinearLayout dataLayout, noDataLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.menu, container, false);
        activity = getActivity();
        context = getContext();

        TextView noDataTxt = mainView.findViewById(R.id.no_data_txt);
        noDataTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"YamSuf.ttf"));
        dataLayout = mainView.findViewById(R.id.data_layout);
        noDataLayout = mainView.findViewById(R.id.no_data_layout);


        if (MainActivity.getListOfItems().size() == 0 || MainActivity.getMapOfProperties().get(LOGGED_IN).equals(OUT))
        {
//            if (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN))
//            {
//                listener.onClickedToolBarRefreshButton(true);
//            }
            dataLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            loadGui();
        }

        return mainView;
    }

    public void loadGui() {
        dataLayout.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        StringBuilder date = new StringBuilder("תפריט לתאריך: ");
        currentDate = df.format(new Date());
        date.append(currentDate);

        TextView headerTxt = mainView.findViewById(R.id.header_txt);
        headerTxt.setText(date.toString());
        headerTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);
        createRecycler();
    }

    public void setOnUpdateMainActivity(MainActivity mainActivity) {
        listener = mainActivity;
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId())
//        {
//            case R.id.build_menu_btn:
//                pager.setCurrentItem(0);break;
//            case R.id.cart_btn:
//                pager.setCurrentItem(1);break;
//            case R.id.menu_btn:
//                pager.setCurrentItem(2);break;
//        }
    }

    public void createRecycler()
    {
        menuAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(context);
        RecyclerView recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    private void addChoice(Item item) {
        MainActivity.getListOfFutureOrder().add(new Order(item.getValue(), item.getCategory(), Integer.parseInt(currentDate.substring(0,2))));
        listener.onClickedToolBarButton(true);
    }

    private void removeChoice(Item item) {

        for (Iterator<Order> iterator = MainActivity.getListOfFutureOrder().iterator(); iterator.hasNext();) {
            Order order = iterator.next();
            if (item.getValue().equals(order.getDishName()) && item.getCategory().equals(order.getCategory())) {
                iterator.remove();
            }
        }

        if (MainActivity.getListOfFutureOrder().size() == 0)
        {
            listener.onClickedToolBarButton(false);
        }
    }

    public void refreshRecycler() {
        if (MainActivity.getListOfItemsToDraw().size() > 0)
        {
            menuAdapter.notifyDataSetChanged();
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        String currentCategory = "";

        class MyViewHolder extends RecyclerView.ViewHolder {

            private MyViewHolder(View v) {
                super(v);
            }
        }

        class MyViewHolder1 extends MyViewHolder {
            ImageView imageView;

            private MyViewHolder1(View v) {
                super(v);
                imageView = v.findViewById(R.id.imageView);
            }
        }

        class MyViewHolder2 extends MyViewHolder {
            TextView valueTxt;
            Button plusBtn;

            private MyViewHolder2(View v) {
                super(v);
                plusBtn = v.findViewById(R.id.plus_btn);
                valueTxt = v.findViewById(R.id.value_txt);
            }
        }

        RecyclerViewAdapter() {

        }

        @Override
        public int getItemViewType(int position) {
            return MainActivity.getListOfItemsToDraw().get(position).getIcon() != 0 ? 1 : 2;
        }

        @Override
        public int getItemCount() {
            return MainActivity.getListOfItemsToDraw().size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.populator_of_header, parent, false);

                return new MyViewHolder1(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.populator_of_item, parent, false);
                return new MyViewHolder2(v);
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (holder instanceof MyViewHolder1) {
                MyViewHolder1 holder1 = (MyViewHolder1) holder;

                holder1.imageView.setImageResource(MainActivity.getListOfItemsToDraw().get(position).getIcon());
                currentCategory = MainActivity.getListOfItemsToDraw().get(position).getItem().getCategory();
            } else {
                MyViewHolder2 holder2 = (MyViewHolder2) holder;
                holder2.valueTxt.setText(MainActivity.getListOfItemsToDraw().get(position).getItem().getValue());
                holder2.plusBtn.setSelected(ifItemSelected(holder2.valueTxt.getText().toString()));
                holder2.plusBtn.setOnClickListener(view -> {
                    holder2.plusBtn.setSelected(!holder2.plusBtn.isSelected());
                    if (holder2.plusBtn.isSelected())
                    {
                        addChoice(MainActivity.getListOfItemsToDraw().get(position).getItem());
                    }
                    else
                    {
                        removeChoice(MainActivity.getListOfItemsToDraw().get(position).getItem());
                    }
                    notifyDataSetChanged();
                });
            }
        }

        private boolean ifItemSelected(String currentDishName) {
            for (Order order: MainActivity.getListOfFutureOrder())
            {
                if (currentDishName.equals(order.getDishName()) && currentCategory.equals(order.getCategory()))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
