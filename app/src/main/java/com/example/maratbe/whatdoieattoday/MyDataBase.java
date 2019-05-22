package com.example.maratbe.whatdoieattoday;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.Order;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class that handles all database queries
 */

public class MyDataBase extends SQLiteOpenHelper implements Constants {
    private static SQLiteDatabase dbInstance;
    private static final String DATABASE_NAME = "MyDBName.db";

    MyDataBase(Context context) {
        super(context, DATABASE_NAME , null, 9);
        dbInstance = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbInstance = db;
        // MENU table
        db.execSQL(
                "CREATE TABLE MENU (DISH_NAME TEXT, CATEGORY_LVL NUMBER(1), GROUP_ID NUMBER(6), " +
                        "PRIMARY KEY (DISH_NAME, CATEGORY_LVL));");
        // HISTORY table
        db.execSQL(
                "CREATE TABLE HISTORY (DISH_NAME TEXT, PRIMARY KEY (DISH_NAME));");
        // ORDERS table
        db.execSQL(
                "CREATE TABLE ORDERS (DISH_NAME TEXT, CATEGORY_LVL NUMBER(1) , MONTH NUMBER(6), DAY NUMBER(2), USER_ID NUMBER(6), GROUP_ID NUMBER(6), " +
                        "PRIMARY KEY(DISH_NAME, CATEGORY_LVL, MONTH, DAY, USER_ID));");
        db.execSQL("CREATE INDEX ORDERS_IND ON ORDERS (USER_ID);");
        // PROPERTIES table
        db.execSQL(
                "CREATE TABLE PROPERTIES (PROPERTY VARCHAR2(15), VALUE VARCHAR2(15), USER_ID NUMBER(6), PRIMARY KEY (PROPERTY, USER_ID));");
        // USERS table
        db.execSQL(
                "CREATE TABLE USERS (USER_NAME VARCHAR2(25), EMAIL VARCHAR2(35), GROUP_ID NUMBER(6), GROUP_NAME VARCHAR2(25), ADMIN NUMBER(1), " +
                        "PRIMARY KEY (EMAIL, GROUP_ID));");
        // MY_GROUPS table
        db.execSQL(
                "CREATE TABLE MY_GROUPS (USER_NAME VARCHAR2(25), EMAIL VARCHAR2(35), GROUP_ID NUMBER(6), GROUP_NAME VARCHAR2(25), STATUS VARCHAR(10), " +
                        "PRIMARY KEY (EMAIL, GROUP_NAME));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MENU");
        db.execSQL("DROP TABLE IF EXISTS HISTORY");
        db.execSQL("DROP TABLE IF EXISTS ORDERS");
        db.execSQL("DROP TABLE IF EXISTS PROPERTIES");
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS MY_GROUPS");
        onCreate(db);
    }

    void insertMenu(Item item)
    {
        Object[] data = new Object[]{item.getValue(), item.getLevel(), MainActivity.getMapOfProperties().get(GROUP_ID)};
        dbInstance.execSQL("INSERT INTO MENU (DISH_NAME, CATEGORY_LVL, GROUP_ID) VALUES(?, ?, ?);",  data);
    }

    void insertMyGroup(String userName, String email, String groupName, int groupId)
    {
        Object[] data = new Object[]{userName, email, groupName, groupId, "Active"};
        dbInstance.execSQL("INSERT INTO MY_GROUPS (USER_NAME, EMAIL, GROUP_NAME, GROUP_ID, STATUS) VALUES(?, ?, ?, ?, ?);",  data);
    }

    public void insertHistory()
    {
        for (Item item: MainActivity.getListOfItemsToSend()) {
            boolean exists = false;
            for (String histItem: MainActivity.getListOfHistory()) {
                if (item.getValue().equalsIgnoreCase(histItem))
                {
                    exists = true;
                }
            }
            if (!exists)
            {
                Object[] data = new Object[]{item.getValue()};
                dbInstance.execSQL("INSERT INTO HISTORY (DISH_NAME) VALUES(?);",  data);
            }
        }

       // fetchAllCategories();
    }

    public void insertOrder(Item item)
    {
        Date currDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        String date = df.format(currDate);
        Object[] data = new Object[]{item.getValue(), item.getLevel(), date.substring(0, 6), date.substring(6, 8)};
        dbInstance.execSQL("INSERT INTO ORDERS (DISH_NAME, CATEGORY_LVL, MONTH, DAY, USER_ID) VALUES(?,?, ?, ?, ?);",  data);

        fetchOrders(date.substring(0, 6));
    }


    private void insertUserProperties(User user)
    {
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{USER, user.getUserName(), user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{EMAIL, user.geteMail(), user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{LOGGED_IN, IN, user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{GROUP_ID, user.getGroupId(), user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{GROUP_NAME, user.getGroupName(), user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{ADMIN, user.getAdmin(), user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{ALERTS, "Y", user.getId()});
        dbInstance.execSQL("INSERT INTO PROPERTIES (PROPERTY, VALUE, USER_ID) VALUES(?, ?, ?);", new Object[]{CART_VIEW, DISPLAY_LIST, user.getId()});
    }

    void insertOrder(Order order, String month)
    {
        Object[] data = new Object[]{order.getDishName(), Utils.getLevelFromCategory(order.getCategory()), month, order.getDay(),
                MainActivity.getMapOfProperties().get(USER_ID), MainActivity.getMapOfProperties().get(GROUP_ID)};
        dbInstance.execSQL("INSERT INTO ORDERS (DISH_NAME, CATEGORY_LVL, MONTH, DAY, USER_ID, GROUP_ID) VALUES(?, ?, ?, ?, ?, ?);",  data);

        fetchOrders(month);
    }

    void insertUser(User user, boolean isUserFound)
    {
//        boolean found = false;
//        int i = 0;
//        for (User user_: MainActivity.getListOfUsers())
//        {
//            i++;
//            if (user_.getUserName().equals(user.getUserName()))
//            {
//                found = true;
//            }
//        }
        if (!isUserFound)
        {
            Object[] data = new Object[]{user.getUserName(), user.geteMail(), user.getGroupName(), user.getGroupId(), user.getAdmin()};
            dbInstance.execSQL("INSERT INTO USERS (USER_NAME, EMAIL, GROUP_NAME, GROUP_ID, ADMIN) VALUES(?, ?, ?, ?, ?);",  data);
            fetchUsers(user.getUserName(), user.geteMail());
            insertUserProperties(MainActivity.getCurrentUser());

            //new Thread(() -> MainActivity.getDbInstance().insertIntoUsers(userName, email, "", "")).start();
        }

        fetchProperties();
    }

    void fetchUsers(String receivedUserName, String receivedEmail) {
        Cursor res =  dbInstance.rawQuery( "SELECT rowid, USER_NAME, EMAIL, GROUP_NAME, GROUP_ID, ADMIN FROM USERS WHERE GROUP_ID NOT IN (SELECT GROUP_ID FROM MY_GROUPS WHERE STATUS = 'Deleted') " +
                "ORDER BY USER_NAME", null);
        res.moveToFirst();
        MainActivity.getListOfUsers().clear();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("USER_NAME"));
            int id = res.getInt(res.getColumnIndex("rowid"));
            String email = res.getString(res.getColumnIndex("EMAIL"));
            String groupName = res.getString(res.getColumnIndex("GROUP_NAME"));
            int groupId = res.getInt(res.getColumnIndex("GROUP_ID"));
            int admin = res.getInt(res.getColumnIndex("ADMIN"));
            User user = new User(name, email, groupName, groupId, admin);
            user.setId(id);
            MainActivity.getListOfUsers().add(user);
            if (MainActivity.getCurrentUser() == null ||
                    (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN) &&
                            MainActivity.getMapOfProperties().get(USER_ID).equals(String.valueOf(id))) ||
                    receivedUserName.equals(name) || receivedEmail.equals(email))
            {
                MainActivity.setCurrentUser(user);
                fetchProperties();
                //Utils.setProperties(user);
            }
            res.moveToNext();
        }
        res.close();
    }

    public void fetchHistory() {
        Cursor res =  dbInstance.rawQuery( "SELECT DISH_NAME FROM HISTORY", null);
        res.moveToFirst();
        MainActivity.getListOfHistory().clear();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("DISH_NAME"));
            MainActivity.getListOfHistory().add(name);
            res.moveToNext();
        }
        res.close();
    }

    void fetchOrders(String date) {
        Cursor res =  dbInstance.rawQuery( "SELECT USER_NAME , EMAIL , GROUP_ID, GROUP_NAME, STATUS from MY_GROUPS",
                null);
        res.moveToFirst();
        MainActivity.getListOfOrders().clear();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("USER_NAME"));
            String category = res.getString(res.getColumnIndex("EMAIL"));
            int group_id = res.getInt(res.getColumnIndex("GROUP_ID"));
            String groupName = res.getString(res.getColumnIndex("GROUP_NAME"));
            String status = res.getString(res.getColumnIndex("STATUS"));
            res.moveToNext();
        }
        res.close();

        res =  dbInstance.rawQuery( "SELECT DISH_NAME, CATEGORY_LVL, DAY FROM ORDERS WHERE MONTH = ? AND USER_ID = ? AND GROUP_ID = ? AND GROUP_ID NOT IN " +
                        "(SELECT GROUP_ID FROM MY_GROUPS WHERE STATUS = 'Deleted') ORDER BY DAY",
                new String[]{date, MainActivity.getMapOfProperties().get(USER_ID),
                        MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(GROUP_ID): "0"});
        res.moveToFirst();
        MainActivity.getListOfOrders().clear();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("DISH_NAME"));
            String category = Utils.getCategoryFromLevel(res.getInt(res.getColumnIndex("CATEGORY_LVL")));
            int day = res.getInt(res.getColumnIndex("DAY"));
            MainActivity.getListOfOrders().add(new Order(name, category, day));
            res.moveToNext();
        }
        res.close();
    }

    void fetchOrders() {
        Cursor res =  dbInstance.rawQuery( "SELECT DISH_NAME, CATEGORY_LVL, DAY, MONTH FROM ORDERS WHERE USER_ID = ? AND GROUP_ID = ? AND GROUP_ID NOT IN " +
                        "(SELECT GROUP_ID FROM MY_GROUPS WHERE STATUS = 'Deleted') ORDER BY MONTH DESC, DAY ASC, CATEGORY_LVL ASC",
                new String[]{MainActivity.getMapOfProperties().get(USER_ID),
                        MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(GROUP_ID): "0"});
        res.moveToFirst();
        MainActivity.getListOfOrders().clear();

        while(!res.isAfterLast()){
            String name = res.getString(res.getColumnIndex("DISH_NAME"));
            String category = Utils.getCategoryFromLevel(res.getInt(res.getColumnIndex("CATEGORY_LVL")));
            int day = res.getInt(res.getColumnIndex("DAY"));
            Order order = new Order(name, category, day);
            order.setMonth(res.getInt(res.getColumnIndex("MONTH")));
            MainActivity.getListOfOrders().add(order);
            res.moveToNext();
        }
        res.close();
    }

    void fetchMenu() {
        Cursor res =  dbInstance.rawQuery( "SELECT DISH_NAME, CATEGORY_LVL FROM MENU WHERE GROUP_ID = ?",
                new String[]{MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(GROUP_ID): "0"});
        int i = 0;
        res.moveToFirst();

        while(!res.isAfterLast()){
            if (i == 0)
            {
                MainActivity.getListOfItemsToSend().clear();
            }
            String name = res.getString(res.getColumnIndex("DISH_NAME"));
            int category_lvl = res.getInt(res.getColumnIndex("CATEGORY_LVL"));
            String category = Utils.getCategoryFromLevel(category_lvl);

            MainActivity.getListOfItemsToSend().add(new Item(name, category, category_lvl));
            res.moveToNext();
            i++;
        }
        res.close();
       // deleteFromMenu();
    }

    void fetchProperties() {
        Cursor res =  dbInstance.rawQuery( "SELECT PROPERTY, VALUE FROM PROPERTIES WHERE USER_ID = ?",
                new String[]{String.valueOf(MainActivity.getCurrentUser().getId())});
        res.moveToFirst();
        int i = 0;

        while(!res.isAfterLast()){
            if (i == 0)
            {
                MainActivity.getMapOfProperties().clear();
                MainActivity.getMapOfProperties().put(USER_ID, String.valueOf(MainActivity.getCurrentUser().getId()));
            }
            String property = res.getString(res.getColumnIndex("PROPERTY"));
            String value = res.getString(res.getColumnIndex("VALUE"));

            MainActivity.getMapOfProperties().put(property, value);
            res.moveToNext();
            i++;
        }
        res.close();
//        if (MainActivity.getMapOfProperties().size() == 0)
//        {
//            insertInitialProperties();
//        }
    }

    void fetchDeletedGroup(String groupName) {

        Cursor res =  dbInstance.rawQuery( "SELECT USER_NAME, EMAIL, GROUP_ID, GROUP_NAME FROM MY_GROUPS WHERE USER_NAME = ? AND STATUS = 'Deleted'",
                new String[]{MainActivity.getMapOfProperties().get(USER)});
        res.moveToFirst();
        MainActivity.getListOfDeletedUsers().clear();

        while(!res.isAfterLast()){
            String userName = res.getString(res.getColumnIndex("USER_NAME"));
            String email = res.getString(res.getColumnIndex("EMAIL"));
            String name = res.getString(res.getColumnIndex("GROUP_NAME"));
            int groupId = res.getInt(res.getColumnIndex("GROUP_ID"));
            int admin = MainActivity.getMapOfProperties().get(USER).equals(userName)? 1: 0;

            res.moveToNext();
        }
        res.close();

        res =  dbInstance.rawQuery( "SELECT USER_NAME, EMAIL, GROUP_ID FROM MY_GROUPS WHERE GROUP_ID IN " +
                        "(SELECT GROUP_ID FROM MY_GROUPS WHERE GROUP_NAME = ? AND USER_NAME = ? AND STATUS = 'Deleted')",
                new String[]{groupName, MainActivity.getMapOfProperties().get(USER)});
        res.moveToFirst();
        MainActivity.getListOfDeletedUsers().clear();

        while(!res.isAfterLast()){
            String userName = res.getString(res.getColumnIndex("USER_NAME"));
            String email = res.getString(res.getColumnIndex("EMAIL"));
            int groupId = res.getInt(res.getColumnIndex("GROUP_ID"));
            int admin = MainActivity.getMapOfProperties().get(USER).equals(userName)? 1: 0;

            MainActivity.getListOfDeletedUsers().add(new User(userName, email, groupName, groupId, admin));
            res.moveToNext();
        }
        res.close();
    }

    void deleteFromMenu()
    {
        dbInstance.execSQL("DELETE FROM MENU");
    }

    public void deleteUsers() {
        dbInstance.execSQL("DELETE FROM USERS");
    }

    public void deleteMyGroups() {
        dbInstance.execSQL("DELETE FROM MY_GROUPS");
    }

    void deleteFromOrders() {
        dbInstance.execSQL("DELETE FROM ORDERS");
    }

    public void deleteFromProperties() {
        dbInstance.execSQL("DELETE FROM PROPERTIES");
    }

    void deleteFromCurrentOrder(String month, String day) {
        Object[] data = new Object[]{month, day};
        dbInstance.execSQL("DELETE FROM ORDERS WHERE MONTH = ? AND DAY = ?",  data);
    }

    void updateProperties(String property, String value) {
        Object[] data;
        data = new Object[]{value, property, MainActivity.getCurrentUser().getId()};
        dbInstance.execSQL("UPDATE PROPERTIES SET VALUE = ? WHERE PROPERTY = ? AND USER_ID = ?",  data);
        MainActivity.getMapOfProperties().put(data[1].toString(), data[0].toString());
    }

    public void updateMyGroup(String userName, String email, String groupName) {
        Object[] data = new Object[]{userName, email, groupName};
        dbInstance.execSQL("UPDATE MY_GROUPS SET USER_NAME = ?, EMAIL = ? WHERE GROUP_NAME = ?",  data);
    }

    public void updateMyGroup(String groupName, String status) {
        Object[] data = new Object[]{status, groupName};
        dbInstance.execSQL("UPDATE MY_GROUPS SET STATUS = ? WHERE GROUP_NAME = ?",  data);
    }
}
