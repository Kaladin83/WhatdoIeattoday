package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.mysql.jdbc.Util;

import java.util.ArrayList;

/**
 * Created by MARATBE on 11/8/2018.
 */

public class BuildMenu extends Fragment implements View.OnClickListener, Constants {
    private View mainView;
    private BuildMenu buildMenu;
    private RecyclerViewAdapter menuAdapter;
    private Button incButton, decButton, saveButton, saveToolbarButton;
    private int numOfItems = 1, recyclerLayoutHeight, headerHeight = 0, rc = -1 ;
    private RecyclerView recyclerView;
    private TextView headerTxt;
    private ArrayList<Integer> listOfSpinnerValues = new ArrayList<>();
    private ArrayList<String> listOfEditValues = new ArrayList<>();
    private Window window;
    private Activity activity;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.build_menu, container, false);
        buildMenu = this;
        if (getActivity()!= null)
        {
            window = getActivity().getWindow();
            activity = getActivity();
            context = getContext();
        }
        final View activityRootView = mainView.findViewById(R.id.root_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            int visibleHeight = rect.bottom -rect.top;
            int screenHeight = dm.heightPixels;
            int diff = (screenHeight - visibleHeight);
            if (diff > 200*MainActivity.getLogicalDensity()) {
                setRecyclerLayoutParams(144 * MainActivity.getLogicalDensity());
            }
            else
            {
                setRecyclerLayoutParams(recyclerLayoutHeight);
            }
        });
        loadLocalLists();
        numOfItems = listOfEditValues.size() == 0? 1: listOfEditValues.size() ;
        setDimensions();
        setFields();
        createRecycler();

        return mainView;
    }

    private void setDimensions() {
        int mainButtonsLayoutHeight = 40*MainActivity.getLogicalDensity();
        int displayHeight = MainActivity.getScreenHeight() - mainButtonsLayoutHeight - MainActivity.getToolbarHeight();
        headerHeight = 60 * MainActivity.getLogicalDensity();
        int buttonLayoutHeight = 60 * MainActivity.getLogicalDensity();
        recyclerLayoutHeight = displayHeight - headerHeight - buttonLayoutHeight - 20* MainActivity.getLogicalDensity();
    }

    private void setRecyclerLayoutParams(int height) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lParams.setMargins(10*MainActivity.getLogicalDensity(), 0, 10*MainActivity.getLogicalDensity(), 10*MainActivity.getLogicalDensity());
        recyclerView.setLayoutParams(lParams);
    }

    private void setFields() {
        headerTxt = mainView.findViewById(R.id.header_txt);
        headerTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);
        incButton = mainView.findViewById(R.id.inc_btn);
        incButton.setOnClickListener(this);
        decButton = mainView.findViewById(R.id.dec_btn);
        decButton.setOnClickListener(this);
        saveButton = mainView.findViewById(R.id.send_btn);
        saveButton.setOnClickListener(this);
        saveButton.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1, Constants.RED_1));
        saveButton.setTextColor(RED_1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.inc_btn:
                numOfItems++;
                menuAdapter.notifyDataSetChanged();
                ScrollToLastItem();
                break;
            case R.id.dec_btn:
                if (numOfItems > 0)
                {
                    if(listOfEditValues.size() > 0 && numOfItems <= listOfEditValues.size())
                    {
                        listOfEditValues.remove(numOfItems-1);
                        listOfSpinnerValues.remove(numOfItems-1);
                    }
                    numOfItems--;
                    menuAdapter.notifyDataSetChanged();
                    ScrollToLastItem();
                    if (MainActivity.getListOfItemsToSend().size() > 0)
                    {
                        MainActivity.getListOfItemsToSend().remove(numOfItems);
                    }
                } break;
            case R.id.send_btn:
                loadMainList();
                new Thread(() -> {
                    MainActivity.getDbInstance().connect();
                    if (MainActivity.isConnected())
                    {
                        rc = MainActivity.getDbInstance().insertIntoMenu();
                        MainActivity.getDbInstance().closeConnection();
                    }
                    else {
                        Utils.showNotConnectedMessage(activity);
                    }
                }).start();


                Utils.closeKeyboard(activity);
               // MainActivity.setIsConnected(false);
                int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
                String[] texts = new String[]{getString(R.string.success_message), getString(R.string.data_saved_message)};
                int[] buttons = new int[]{R.string.ok_dialog_btn};
                Utils.showMessage(activity, texts, colors, buttons, DATA_SAVED);
        }
    }

    private void ScrollToLastItem() {
        if (recyclerView.getLayoutManager() != null)
        {
            recyclerView.getLayoutManager().scrollToPosition(recyclerView.getLayoutManager().getItemCount()-1);
        }
    }

    private void loadMainList() {
        MainActivity.getListOfItemsToSend().clear();
        for (int i = 0; i<listOfSpinnerValues.size(); i++) {
            MainActivity.getListOfItemsToSend().add(new Item(listOfEditValues.get(i), Utils.getCategoryFromLevel(listOfSpinnerValues.get(i)), listOfSpinnerValues.get(i)));
        }
    }

    public void loadLocalLists() {
        listOfSpinnerValues.clear();
        listOfEditValues.clear();

        for (Item item: MainActivity.getListOfItemsToSend()) {
            listOfSpinnerValues.add(item.getLevel());
            listOfEditValues.add(item.getValue());
        }
    }

    public void createRecycler()
    {
        menuAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        setRecyclerLayoutParams(recyclerLayoutHeight);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView  numberTxt;
            private AutoCompleteTextView valueAct;
            private Spinner spinner;

            private MyViewHolder(View v) {
                super(v);
                numberTxt = v.findViewById(R.id.number_txt);
                valueAct = v.findViewById(R.id.value_act);
                valueAct.setBackground(Utils.createBorder(10,Color.TRANSPARENT, 1));
                valueAct.addTextChangedListener(new MyTextWatcher(valueAct));
                spinner = v.findViewById(R.id.category_spinner);
                spinner.setOnItemSelectedListener(new MySpinnerAdapter());
                fillUpCategorySpinner(spinner);
            }

            private void fillUpCategorySpinner(Spinner spinner) {
                String[] arrayOfValues = new String[]{getString(R.string.salads), getString(R.string.main_dish), getString(R.string.side_dish)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, arrayOfValues);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        }

        private RecyclerViewAdapter() {

        }
        
        @Override
        public int getItemCount() {
            return numOfItems;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.populator_of_build_menu, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.numberTxt.setText(String.valueOf(position + 1));
            holder.valueAct.setText(listOfEditValues.size() > position? listOfEditValues.get(position): "");
            holder.valueAct.setTag(position);
            holder.spinner.setSelection(listOfSpinnerValues.size() > position? listOfSpinnerValues.get(position): 0);
            holder.spinner.setTag(position);
            if (position == numOfItems -1)
            {
                holder.itemView.requestFocus();
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private AutoCompleteTextView editText;

        private MyTextWatcher(AutoCompleteTextView editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (editText != null && editText.getTag() != null && !s.toString().equals(""))
            {
                int pos = (int) editText.getTag();
                if (listOfEditValues.size() > pos)
                {
                    listOfEditValues.set(pos, s.toString());
                }
                else
                {
                    listOfEditValues.add(pos, s.toString());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class MySpinnerAdapter implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getTag() != null)
            {
                int pos = (int)parent.getTag();
                if (listOfSpinnerValues.size() > pos)
                {
                    listOfSpinnerValues.set(pos, position);
                }
                else
                {
                    listOfSpinnerValues.add(position);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
