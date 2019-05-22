package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.interfaces.OnUpdateMainActivityListener;
import com.example.maratbe.whatdoieattoday.objects.DayOrder;
import com.example.maratbe.whatdoieattoday.objects.Group;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.ItemToDraw;
import com.example.maratbe.whatdoieattoday.objects.Order;
import com.example.maratbe.whatdoieattoday.objects.ProgressBarObject;
import com.example.maratbe.whatdoieattoday.objects.User;
import com.mysql.jdbc.Util;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


//337966688739-fa3s5qr7mb4ggb28hd56bauc79kk0kvm.apps.googleusercontent.com

//private key's password: notasecret
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnUpdateMainActivityListener, Constants {
    private static MyDataBase myDbInstance;
    private static DataBase dbInstance;
    private static boolean isConnected = false;
    private static boolean newGroupInserted = false;
    private static boolean menuInserted = false;
    private boolean isStartingActivity = true;
    private static User currentUser;
    private static MySharedPreferences myPreferences;
    private ArrayList<Fragment> listOfPages;
    private PagerAdapter pagerAdapter;
    private LinearLayout entryLayout, progressBarLayout;
    private AVLoadingIndicatorView customProgressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MainActivity mainActivity;
    private ViewPager pager;
    private TextView navigationHeaderNameTxt, navigationGroupNameTxt;
    private Button saveMenuToolbarBtn, saveAccountToolbarBtn, syncToolbarBtn;
    private static ArrayList<Item> listOfItems = new ArrayList<>();
    private static ArrayList<Item> listOfItemsToSend = new ArrayList<>();
    private static ArrayList<ItemToDraw> listOfItemsToDraw = new ArrayList<>();
    private static ArrayList<Order> listOfOrders = new ArrayList<>();
    private static ArrayList<Order> listOfFutureOrder = new ArrayList<>();
    private static ArrayList<String> listOfHistory = new ArrayList<>();
    private static ArrayList<User> listOfUsers = new ArrayList<>();
    private static ArrayList<User> listOfThisGroupUsers = new ArrayList<>();
    private static ArrayList<User> listOfAllGroupsUsers = new ArrayList<>();
    private static ArrayList<User> listOfDeletedUsers = new ArrayList<>();
    private static ArrayList<DayOrder> listOfDayOrders = new ArrayList<>();
    private static HashMap<String, String> mapOfProperties = new HashMap<>();
    private static SparseArray<Integer> sArrayOfDateColors = new SparseArray<>();
    private static ArrayList<ProgressBarObject> listOfBackgrounds = new ArrayList<>();
    private static ArrayList<String> listOfIndicators = new ArrayList<>();
    private static ArrayList<Group> groups = new ArrayList<>();
    private Button cartButton, buildMenuButton, menuButton;
    private static int screenWidth, screenHeight, logicalDensity, toolbarHeight;
    private int menuItemChoice = 0, syncTaskAction;

    public static MySharedPreferences getMyPreferences()
    {
        return myPreferences;
    }

    public static MyDataBase getMyDbInstance()
    {
        return myDbInstance;
    }

    public static DataBase getDbInstance()
    {
        return dbInstance;
    }

    public static boolean isConnected()
    {
        return isConnected;
    }

    public static boolean isNewGroupInserted()
    {
        return newGroupInserted;
    }

    public static void setIsConnected(boolean connected)
    {
        isConnected = connected;
    }

    public static void setIsNewGroupInserted(boolean newGroupInserted)
    {
        newGroupInserted = newGroupInserted;
    }

    public static ArrayList<String> getListOfHistory()
    {
        return listOfHistory;
    }

//    public static void setListOfHistory(ArrayList<String> items)
//    {
//        listOfHistory = items;
//    }

    public static ArrayList<Item> getListOfItemsToSend()
    {
        return listOfItemsToSend;
    }

//    public static void setListOfItemsToSend(ArrayList<Item> items)
//    {
//        listOfItemsToSend = items;
//    }

    public static ArrayList<Item> getListOfItems()
    {
        return listOfItems;
    }

//    public static void setListOfItems(ArrayList<Item> items)
//    {
//        listOfItems = items;
//    }

    public static ArrayList<Group> getGroups() {
        return groups;
    }

    public static void setGroups(ArrayList<Group> groups_) {
        groups = groups_;
    }

    public static ArrayList<ItemToDraw> getListOfItemsToDraw()
    {
        return listOfItemsToDraw;
    }

//    public static void setListOfItemsToDraw(ArrayList<ItemToDraw> items)
//    {
//        listOfItemsToDraw = items;
//    }

    public static ArrayList<Order> getListOfOrders()
    {
        return listOfOrders;
    }

    //    public static void setListOfOrders(ArrayList<Order> orders)
//    {
//        listOfOrders = orders;
//    }

    public static ArrayList<Order> getListOfFutureOrder()
    {
        return listOfFutureOrder;
    }

    public static void setListOfFutureOrder(ArrayList<Order> orders)
    {
        listOfFutureOrder = orders;
    }

    public static HashMap<String, String> getMapOfProperties()
    {
        return mapOfProperties;
    }

    public static SparseArray<Integer> getsArrayOfDateColors()
    {
        return sArrayOfDateColors;
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static int getToolbarHeight()
    {
        return toolbarHeight;
    }

    public static int getLogicalDensity()
    {
        return logicalDensity;
    }

    public static ArrayList<User> getListOfUsers() {
        return listOfUsers;
    }

    public static ArrayList<User> getListOfThisGroupUsers() {
        return listOfThisGroupUsers;
    }

    public static ArrayList<User> getListOfAllGroupsUsers() {
        return listOfAllGroupsUsers;
    }

    public static ArrayList<DayOrder> getListOfDayOrders() {
        return listOfDayOrders;
    }

    public static void setListOfDayOrders(ArrayList<DayOrder> dayOrders) {
        listOfDayOrders = dayOrders;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static ArrayList<User> getListOfDeletedUsers() {
        return listOfDeletedUsers;
    }

    public static void setMenuInserted(boolean isInserted) {
        menuInserted = isInserted;
    }

    public static boolean isMenuInserted()
    {
        return menuInserted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ProgressBarObject().loadList(listOfBackgrounds, listOfIndicators, this);
        customProgressBar = findViewById(R.id.avi);
        Random rand = new Random();
        customProgressBar.setIndicator(listOfIndicators.get(rand.nextInt(8)));
        entryLayout = findViewById(R.id.entry_layout);
        Window window = this.getWindow();
        window.setStatusBarColor(RED_4);

        myPreferences = new MySharedPreferences(this);
        myDbInstance = new MyDataBase(this);
        mainActivity = this;

        setDimensions();
        progressBarLayout = findViewById(R.id.progress_bar);
        progressBarLayout.setVisibility(View.GONE);
        progressBarLayout.setBackground(Utils.createBorder(10,Constants.RED_6, 0));

        syncTaskAction = ACTION_LOAD_GUI;
        new ProgressTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isStartingActivity = false;
        if (requestCode == 1) {
            if (resultCode == RC_OK)
            {
                userChanged(View.VISIBLE);
            }
//            if(resultCode == Activity.RESULT_OK){
//                String userName = data.getStringExtra("USER_NAME");
//               // String email = data.getStringExtra("EMAIL");
//                navigationHeaderNameTxt.setText(userName);
//                navigationHeaderNameTxt.setTextSize(mapOfProperties.get(USER).equals(NO_USER)? 16: 23);
//            }
            menuItemChoice = 0;

        }
        if (requestCode == 2)
        {
            if(resultCode == RC_OK){
                newGroupInserted = true;
                userChanged(View.VISIBLE);
                int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
                String[] texts = new String[]{getString(R.string.success_message), getString(R.string.mail_sent_message)};
                int[] buttons = new int[]{R.string.ok_dialog_btn};
                Utils.showMessage(mainActivity, texts, colors, buttons, MAIL_SENT);
            }
        }
        if (requestCode == 3)
        {
            if (resultCode == RC_OK)
            {
                refreshViewPager();
            }
        }
        if (requestCode == 4)
        {
            if(data.getBooleanExtra("GROUP_DELETED", false) ||
                    data.getBooleanExtra("GROUP_CHANGED", false))
            {
                myDbInstance.fetchOrders();
                userChanged(View.VISIBLE);
            }
        }
    }

//    private void refreshDatabase() {
//        if (!isConnected) {
//            for (int i = 0 ; i < 3; i++)
//            {
//                dbInstance.connect();
//                if(isConnected)
//                {
//                    break;
//                }
//            }
//        }
//        //swipeRefreshLayout.setRefreshing(false);
//        if (!isConnected)
//        {
//            int[] colors = new int[]{RED_6, RED_4, CHERY};
//            int[] texts = new int[]{R.string.sorry_message, R.string.still_no_connection};
//            int[] buttons = new int[]{R.string.ok_dialog_btn};
//            MainActivity.getMyDbInstance().insertMenu();
//            Utils.showMessage(this, texts, colors, buttons);
//        }
//    }

    private void insertPrevEnteredData() {
        if (isConnected())
        {
            myDbInstance.fetchMenu();
//            if (listOfItemsToSend.size() > 0 )
//            {
//                dbInstance.insertIntoMenu();
//                int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
//                String[] texts = new String[]{getString(R.string.success_message), getString(R.string.prev_data_saved_message)};
//                int[] buttons = new int[]{R.string.ok_dialog_btn};
//                Utils.showMessage(this, texts, colors, buttons, DATA_SAVED);
//            }
//            else
//            {
//                myDbInstance.fetchMenu();
//            }
        }
    }

    private void populateItemsToDraw() {
        if (listOfItems.size() == 0)
        {
            return;
        }
        sortData();
        listOfItemsToDraw.clear();
        listOfItemsToDraw.add(new ItemToDraw(new Item("","סלטים", 1), R.drawable.salads_icon));
        int level = 0;
        for (Item item: listOfItems)
        {
            if (item.getLevel() > level)
            {
                level = item.getLevel();
                if (item.getLevel() == 1)
                {
                    listOfItemsToDraw.add(new ItemToDraw(new Item("", "עיקרית", 2),R.drawable.main));
                }
                else
                {
                    listOfItemsToDraw.add(new ItemToDraw(new Item("", "תוספת", 3), R.drawable.fries_icon));
                }

            }
            listOfItemsToDraw.add(new ItemToDraw( item, 0));
        }
    }

    private void sortData() {
        for (int i = 0; i< listOfItems.size(); i++)
        {
            for (int j = i; j< listOfItems.size(); j++) {
                if (listOfItems.get(i).getLevel() > listOfItems.get(j).getLevel()) {
                    Item tmpItem = listOfItems.get(i);
                    listOfItems.set(i, listOfItems.get(j));
                    listOfItems.set(j, tmpItem);
                }
            }
        }
    }

    private void insertInitialOrders()
    {

//        myDbInstance.insertOrder(new Item("מלווח", Utils.getCategoryFromLevel(1), 1));
//        myDbInstance.insertOrder(new Item("טחינה", Utils.getCategoryFromLevel(0), 0));
//        myDbInstance.insertOrder(new Item("איטריות ברוטב אדום", Utils.getCategoryFromLevel(2), 2));
//        myDbInstance.insertOrder(new Item("קציצות", Utils.getCategoryFromLevel(1), 1));
//        myDbInstance.insertOrder(new Item("חומוס", Utils.getCategoryFromLevel(0), 0));
//        myDbInstance.insertOrder(new Item("שעועית ירוקה", Utils.getCategoryFromLevel(2), 2));

        myDbInstance.deleteFromOrders();
        myDbInstance.insertOrder(new Order("מלווח", Utils.getCategoryFromLevel(1), 18),"201811");
        myDbInstance.insertOrder(new Order("סלט גזר", Utils.getCategoryFromLevel(0), 18),"201811");
        myDbInstance.insertOrder(new Order("שועית ירוקה", Utils.getCategoryFromLevel(2), 18),"201811");

        myDbInstance.insertOrder(new Order("אסאדו", Utils.getCategoryFromLevel(1), 18),"201811");
        myDbInstance.insertOrder(new Order("סלט אוליוויה", Utils.getCategoryFromLevel(0), 18),"201811");
        myDbInstance.insertOrder(new Order("תפוח אדמה בגריל", Utils.getCategoryFromLevel(2), 18),"201811");
        myDbInstance.insertOrder(new Order("המבורגר", Utils.getCategoryFromLevel(1), 17),"201811");
        myDbInstance.insertOrder(new Order("סלט גזר", Utils.getCategoryFromLevel(0), 17),"201811" );
        myDbInstance.insertOrder(new Order("צ'יפס", Utils.getCategoryFromLevel(2), 17),"201811");
        myDbInstance.insertOrder(new Order("מפרום", Utils.getCategoryFromLevel(1), 1),"201810");
        myDbInstance.insertOrder(new Order("סלט אוליוויה", Utils.getCategoryFromLevel(0), 1),"201810");
        myDbInstance.insertOrder(new Order("אורז", Utils.getCategoryFromLevel(2), 1),"201810");
        myDbInstance.insertOrder(new Order("המבורגר", Utils.getCategoryFromLevel(1), 2),"201810");
        myDbInstance.insertOrder(new Order("סלט ירוק", Utils.getCategoryFromLevel(0), 2),"201810");
        myDbInstance.insertOrder(new Order("צ'יפס", Utils.getCategoryFromLevel(2), 2),"201810");
    }

//    private void populateInitialItems()
//    {
//        listOfItems.add(new Item("סלט סלק", "סלטים", 1));
//        listOfItems.add(new Item("סלט ירוק", "סלטים", 1));
//        listOfItems.add(new Item("המבורגר", "עיקרית", 2));
//        listOfItems.add(new Item("קציצות", "עיקרית", 2));
//        listOfItems.add(new Item("צ'יפס", "תוספת", 3));
//        listOfItems.add(new Item("קוסקוס", "תוספת", 3));
//    }

    private void fetchData() {
//        myDbInstance.deleteFromProperties();
//        myDbInstance.deleteUsers();
//        myDbInstance.deleteMyGroups();
//        myPreferences.storeData("N", "0");
//        MainActivity.getMyDbInstance().updateMyGroup("בית", "Active");
    //    myDbInstance.updateProperties(GROUP_ID, 1+"");
        myDbInstance.deleteFromMenu();
        myPreferences.getData();
        myDbInstance.fetchUsers(mapOfProperties.get(LOGGED_IN).equals(IN)? mapOfProperties.get(USER): "",
                mapOfProperties.get(LOGGED_IN).equals(IN)? mapOfProperties.get(EMAIL):"");
        //myDbInstance.fetchProperties();
       // myDbInstance.deleteFromOrders();

        if (mapOfProperties.get(LOGGED_IN).equals(IN))
        {
            insertInitialOrders();
        }
        if (!isConnected)
        {
            dbInstance = new DataBase();
            dbInstance.connect();
            insertPrevEnteredData();
            if (isConnected)
            {
                dbInstance.fetchDataFromMenu("fetchTodayFromMenu");
                dbInstance.fetchDataFromUsersAllPerGroup();
                dbInstance.fetchDataFromUsersAllPerUser();
                dbInstance.closeConnection();
            }
            else {
                Utils.showNotConnectedMessage(this);
            }
        }

        myDbInstance.fetchMenu();
    }

    public void setDimensions()
    {
        int navigationBarHeight = 0;
        int resourceId = this.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = this.getResources().getDimensionPixelSize(resourceId);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels - navigationBarHeight;
        logicalDensity = (int) metrics.density;
        toolbarHeight = (int)getResources().getDimension(R.dimen.tool_bar);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else
            {
            if (pager.getCurrentItem() == 0 && Utils.isKeyboardShown(this))
            {
                pager.setCurrentItem(0);
            }
            getFragmentManager().popBackStack();
        }
    }

    private void createPager() {
        loadListOfPages();
        pager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(mapOfProperties.get(LOGGED_IN).equals(IN)?
                (mapOfProperties.get(ADMIN).equals("0")? CART_SCREEN -1: CART_SCREEN) : CART_SCREEN -1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (mapOfProperties.get(LOGGED_IN).equals(IN)?
                        (mapOfProperties.get(ADMIN).equals("0")? position+1: position): position+1)
                {
                    case BUILD_MENU_SCREEN:
                        changeSelection(true, false, false); break;
                    case CART_SCREEN:
                        changeSelection(false, true, false); break;
                    case MENU_SCREEN:
                        changeSelection(false, false, true); break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void loadListOfPages() {
        listOfPages = new ArrayList<>();
        Menu menu = new Menu();
        menu.setOnUpdateMainActivity(mainActivity);
        if (mapOfProperties.get(LOGGED_IN).equals(IN) && mapOfProperties.get(ADMIN).equals("1"))
        {
            listOfPages.add(new BuildMenu());
            listOfPages.add(getCart());
            listOfPages.add(menu);
        }
        else
        {
            listOfPages.add(getCart());
            listOfPages.add(menu);
        }
    }

    private Fragment getCart() {
        if (mapOfProperties.get(LOGGED_IN).equals(IN) &&
                mapOfProperties.get(CART_VIEW).equals(DISPLAY_CALENDAR)) {
            return new CartWithCalendar();
        }
        else
        {
            return new CartWithList();
        }
    }

    private void changeSelection(boolean b, boolean b1, boolean b2) {
        buildMenuButton.setSelected(b);
        cartButton.setSelected(b1);
        menuButton.setSelected(b2);
        if (b2)
        {
            onClickedToolBarButton(listOfFutureOrder.size() > 0);
            onClickedToolBarRefreshButton(listOfItems.size() == 0);
//            if (menuInserted)
//            {
//                //listOfPages.set(Integer.parseInt(mapOfProperties.get(ADMIN))+1, new Menu());
//                populateItemsToDraw();
//                ((Menu)listOfPages.get(Integer.parseInt(mapOfProperties.get(ADMIN))+1)).refreshRecycler();
//            }
        }
        else
        {
            onClickedToolBarButton(false);
            onClickedToolBarRefreshButton(false);
        }
    }

    private void setFields() {
        createButtons();

        MyNavigationItemSelectedListener myNavigationItemSelectedListener = new MyNavigationItemSelectedListener();
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(myNavigationItemSelectedListener);
        MyDrawerListener myDrawerListener = new MyDrawerListener(navigationView.getMenu());
        drawerLayout.addDrawerListener(myDrawerListener);

        navigationHeaderNameTxt = (TextView) ((LinearLayout) navigationView.getHeaderView(0)).getChildAt(3);
        navigationHeaderNameTxt.setText(mapOfProperties.get(LOGGED_IN).equals(IN)? mapOfProperties.get(USER): NO_USER);
        navigationHeaderNameTxt.setTextSize(mapOfProperties.get(LOGGED_IN).equals(IN)? 21: 16);
        navigationGroupNameTxt = (TextView) ((LinearLayout) navigationView.getHeaderView(0)).getChildAt(0);
        navigationGroupNameTxt.setTextDirection(View.TEXT_DIRECTION_RTL);
        navigationGroupNameTxt.setText(mapOfProperties.get(LOGGED_IN).equals(OUT)?"":"\""+"קבוצת "+ mapOfProperties.get(GROUP_NAME)+"\"");
        //navigationGroupNameTxt.setBackgroundColor(mapOfProperties.get(LOGGED_IN).equals(OUT) || mapOfProperties.get(GROUP_ID).equals(NO_GROUP) ? RED_4: RED_5);

        syncToolbarBtn = findViewById(R.id.sync_toolbar_btn);
        //syncToolbarBtn.setVisibility(View.GONE);
        syncToolbarBtn.setOnClickListener(this);

        final MenuItem logout = navigationView.getMenu().getItem(2);
        SpannableString spanString = new SpannableString(logout.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(RED_3), 0, spanString.length(), 0);
        logout.setTitle(spanString);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void createButtons() {
        saveMenuToolbarBtn = findViewById(R.id.save_menu_toolbar_btn);
        saveMenuToolbarBtn.setBackground(Utils.createBorder(10, Color.WHITE, 1, Color.WHITE));
        saveMenuToolbarBtn.setPadding(0,0,0,0);
        saveMenuToolbarBtn.setOnClickListener(this);
        saveAccountToolbarBtn = findViewById(R.id.save_account_toolbar_btn);
        saveAccountToolbarBtn.setVisibility(View.GONE);
        saveAccountToolbarBtn.setOnClickListener(this);
        cartButton = findViewById(R.id.cart_btn);
        cartButton.setSelected(true);
        cartButton.setOnClickListener(this);
        buildMenuButton = findViewById(R.id.build_menu_btn);
        buildMenuButton.setVisibility(mapOfProperties.get(LOGGED_IN).equals(OUT) || mapOfProperties.get(ADMIN).equals("0")? View.GONE: View.VISIBLE);
        buildMenuButton.setOnClickListener(this);
        menuButton = findViewById(R.id.menu_btn);
        menuButton.setOnClickListener(this);
    }

    public void refreshViewPager()
    {
        loadListOfPages();
        pagerAdapter.notifyDataSetChanged();
        pager.setCurrentItem(mapOfProperties.get(LOGGED_IN).equals(OUT) || mapOfProperties.get(ADMIN).equals("0")? CART_SCREEN -1: CART_SCREEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.build_menu_btn:
                pager.setCurrentItem(BUILD_MENU_SCREEN);break;
            case R.id.cart_btn:
                pager.setCurrentItem(CART_SCREEN);break;
            case R.id.menu_btn:
                pager.setCurrentItem(MENU_SCREEN);break;
            case R.id.save_menu_toolbar_btn:
                handleSaveButton(); break;
            case R.id.sync_toolbar_btn:
                syncTaskAction = ACTION_REFRESH;
                new ProgressTask().execute();
        }
    }

    private void handleSaveButton() {
        if (mapOfProperties.get(LOGGED_IN).equals(IN) && mapOfProperties.get(ALERTS).equals("Y"))
        {
            int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
            String[] texts = new String[]{getString(R.string.choice_message_header), getString(R.string.save_message)};
            int[] buttons = new int[]{R.string.send_dialog_btn, R.string.cancel_dialog_btn};
            Utils.showDisableOptionMessage(this, texts, colors, buttons, SAVE_CHOICE);
        }
        else
        {
            saveUsersChoices();
        }
    }

    public void saveUsersChoices() {
        Date currDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        String month = df.format(currDate).substring(0,6);
        String day = df.format(currDate).substring(6,8);
        myDbInstance.deleteFromCurrentOrder(month, day);
        for (Order order: listOfFutureOrder)
        {
            myDbInstance.insertOrder(order, month);
        }
        //cartWithCalendar.updateCart();
        //cartWithList.updateCart();
        int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
        String[] texts = new String[]{getString(R.string.success_message), getString(R.string.data_saved_message)};
        int[] buttons = new int[]{R.string.ok_dialog_btn};
        Utils.showMessage(this, texts, colors, buttons, DATA_SAVED);
    }

    @Override
    public void onClickedToolBarButton(boolean show) {
        saveAccountToolbarBtn.setVisibility(show? View.VISIBLE: View.GONE);
    }

    @Override
    public void onClickedToolBarRefreshButton(boolean show) {
        syncToolbarBtn.setVisibility(show? View.VISIBLE: View.GONE);
    }

    class PagerAdapter extends SmartFragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listOfPages.get(position);
        }

        @Override
        public int getCount() {
            return listOfPages.size();
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

    }

    class MyDrawerListener implements DrawerLayout.DrawerListener{

        android.view.Menu menu;
        MyDrawerListener(android.view.Menu menu)
        {
            this.menu = menu;
        }
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {
            if (mapOfProperties.get(LOGGED_IN).equals(IN))
            {
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(true);
                menu.getItem(4).setVisible(true);
            }
            else
            {
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(false);
                menu.getItem(4).setVisible(false);
            }
            menu.getItem(menuItemChoice).setChecked(true);
        }
    }

    class MyNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            menuItem.setChecked(true);
            Intent intent;
            switch (menuItem.getItemId())
            {
                case R.id.drawer_logon:
                    intent = new Intent(mainActivity, Account.class);
                    intent.putExtra("fromMain",true);
                    startActivityForResult(intent, 1);break;
                case R.id.drawer_logout:
                    listOfDayOrders.clear();
                    listOfItems.clear();
                    MainActivity.myDbInstance.updateProperties(LOGGED_IN, OUT);
                    myPreferences.storeData("N", mapOfProperties.get(USER_ID));
                    userChanged(View.GONE);break;
                case R.id.drawer_make_group:
                    intent = new Intent(mainActivity, MakeGroup.class);
                    intent.putExtra("GROUP_NAME","");
                    startActivityForResult(intent, 2);break;
                case R.id.drawer_settings:
                   // pagerAdapter.notifyDataSetChanged();
                    intent = new Intent(mainActivity, Settings.class);
                    startActivityForResult(intent, 3);
                    break;
                case R.id.drawer_my_groups:
                    intent = new Intent(mainActivity, MyGroups.class);
                    startActivityForResult(intent, 4);break;
            }
            // startActivity(intent);
            drawerLayout.closeDrawers();
            return false;
        }
    }

    private void userChanged(int visibility) {
        refreshCart(mapOfProperties.get(LOGGED_IN).equals(IN)? Integer.parseInt(mapOfProperties.get(ADMIN)): 0);
        buildMenuButton.setVisibility(visibility);
        cartButton.setSelected(true);
        navigationHeaderNameTxt.setText(mapOfProperties.get(LOGGED_IN).equals(IN)? mapOfProperties.get(USER): NO_USER);
        navigationHeaderNameTxt.setTextSize(mapOfProperties.get(LOGGED_IN).equals(IN)? 21: 16);
       // navigationGroupNameTxt.setText(Utils.getGroupNameById(mapOfProperties.get(GROUP_ID)));
        navigationGroupNameTxt.setText(mapOfProperties.get(LOGGED_IN).equals(OUT)?"": "קבוצת "+"\""+ mapOfProperties.get(GROUP_NAME)+"\"");
       // navigationGroupNameTxt.setBackgroundColor(mapOfProperties.get(LOGGED_IN).equals(OUT) || mapOfProperties.get(GROUP_ID).equals(NO_GROUP) ? RED_4: RED_5);
        refreshViewPager();
    }

    private void refreshCart(int index) {
        if(listOfPages.get(index) instanceof CartWithList)
        {
            CartWithList cList = (CartWithList) listOfPages.get(index);
            cList.refreshList();
        }
        else if(listOfPages.get(index) instanceof CartWithCalendar)
        {
            CartWithCalendar cCalendar = (CartWithCalendar)listOfPages.get(index);
            cCalendar.updateCart();
        }
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            switch (syncTaskAction)
            {
                case ACTION_LOAD_GUI:
                    if (isStartingActivity)
                    {
                        changeBackground();
                        customProgressBar.show();
                    }
                    else
                    {
                        progressBarLayout.setVisibility(View.VISIBLE);
                    } break;
                case ACTION_REFRESH:
                    progressBarLayout.setVisibility(View.VISIBLE); break;
            }

        }

        private void changeBackground() {
            Timer timer = new Timer();
            MyTimer mt = new MyTimer();
            timer.schedule(mt,0, 1000);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            switch (syncTaskAction)
            {
                case ACTION_LOAD_GUI:
                    fetchData(); break;
                case ACTION_REFRESH:
                    refreshMenu();break;
            }

        //populateInitialItems();
            return null;
        }

        private void refreshMenu() {
            dbInstance.connect();
            dbInstance.fetchDataFromMenu("fetchTodayFromMenu");
            populateItemsToDraw();
            dbInstance.closeConnection();
        }

        @Override
        protected void onPostExecute(Void result) {
            switch (syncTaskAction)
            {
                case ACTION_LOAD_GUI:
                    loadPage();break;
                case ACTION_REFRESH:
                    progressBarLayout.setVisibility(View.GONE);
                    onClickedToolBarRefreshButton(false);
                    ((Menu)listOfPages.get(Integer.parseInt(mapOfProperties.get(ADMIN))+1)).loadGui();
            }
        }

        private void loadPage() {
            if (isStartingActivity)
            {
                customProgressBar.hide();
                entryLayout.setVisibility(View.GONE);
            }
            else
            {
                progressBarLayout.setVisibility(View.GONE);
            }

            populateItemsToDraw();
            setFields();
            createPager();
        }

        class MyTimer extends TimerTask {
            public void run() {
                runOnUiThread(() -> {
                    Random rand = new Random();
                    entryLayout.setBackground(listOfBackgrounds.get(rand.nextInt(5)).getBackgroundImage());
                    customProgressBar.setIndicatorColor(listOfBackgrounds.get(rand.nextInt(5)).getColor());
                });
            }
        }
    }
}