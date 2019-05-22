package com.example.maratbe.whatdoieattoday;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.Group;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.User;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBase implements Constants {
    private StringBuffer query;
    private PreparedStatement stmt = null;
    private Connection conn = null;
    private int groupId = 1;


    @SuppressLint("NewApi")

    DataBase(){
    }

    void connect() {
        String ip = "sql7.freesqldatabase.com:3306";
        String classs = "com.mysql.jdbc.Driver";
        String dbName = "sql7270576";
        String userName = "sql7270576";
        String password = "quUIA3l9r5";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        MainActivity.setIsConnected(false);
        String ConnURL;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:mysql://" + ip + "/"
                    +dbName+ "?useUnicode=true&characterEncoding=UTF-8" ;
            DriverManager.setLoginTimeout(10);
            conn = DriverManager.getConnection(ConnURL, userName, password);
        }catch ( MySQLNonTransientConnectionException e) {
            return;
        } catch (SQLException se) {
            Log.e("ERROR", se.getErrorCode() + ": " + se.getMessage());
            return;
        }catch (ClassNotFoundException e) {
            Log.e("ERROR", e.getMessage());
            return;
        }
        MainActivity.setIsConnected(true);
    }

    public void closeConnection()
    {
        try
        {
            conn.close();
        }catch (SQLException e)
        {
            Log.e("ERROR", "The Fetch action did not succeed.\nThe reason is: " +e.getErrorCode()+": "+e.getMessage());
        }
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

//    public void updateToDayValueMenu(boolean isOpen, boolean isClose)
//    {
//        ArrayList<Object> listOfValues = new ArrayList<>();
//        connect(isOpen);
//        query = new StringBuffer();
//
//        createUpdateMenuToDayValueQuery();
//        if(executeData(false, listOfValues) == 0)
//        {
//            query = new StringBuffer();
//            createDeleteFromMenuQuery();
//            executeData(isClose, listOfValues);
//        }
//    }

    void deleteUserFromGroup(String email, String group)
    {
        ArrayList<Object> listOfValues = new ArrayList<>();
        listOfValues.add(email);
        listOfValues.add(group);
        query = new StringBuffer();
        createDeleteFromUsersQuery();
        executeData(listOfValues);

        fetchDataFromUsersAllPerUser();
    }

    void deleteGroup(String groupName) {
        ArrayList<Object> listOfValues = new ArrayList<>();
        listOfValues.add(groupName);

        query = new StringBuffer();
        createDeleteGroupFromUsersQuery();
        executeData(listOfValues);

        fetchDataFromUsersAllPerUser();
    }

    int insertIntoMenu()
    {
        int rc = -1;
        ArrayList<Object> listOfValues = new ArrayList<>();

        fetchDataFromMenu("fetchYesterdayFromMenu");

        query = new StringBuffer();
        listOfValues.add(MainActivity.getMapOfProperties().get(GROUP_ID));
        createDeleteFromMenuQuery();
        if (executeData(listOfValues) == -1)
        {
            return -1;
        }
//        query = new StringBuffer();
//        createDeleteFromMenuQuery();
//        if(executeData(listOfValues) == 0)
//        {
//            query = new StringBuffer();
//            createUpdateMenuToDayValueQuery();
//            if(executeData(listOfValues) == -1)
//            {
//                return;
//            }
//        }

        int i = 0;
        for (Item item:MainActivity.getListOfItemsToSend()) {
            if (i == MainActivity.getListOfItemsToSend().size() - 1)
            {
                MainActivity.setMenuInserted(true);
            }
            query = new StringBuffer();
            listOfValues = new ArrayList<>();
            createInsertIntoMenuQuery();
            listOfValues.add(item.getLevel());
            listOfValues.add(item.getValue());
            listOfValues.add(MainActivity.getMapOfProperties().get(GROUP_ID));
            rc = executeData(listOfValues);
            i++;
        }

        //fetchDataFromMenu("fetchTodayFromMenu");
        return rc;
    }

    int insertUserWithGroup(String userName, String email, String groupName)
    {
        ArrayList<Object> listOfValues = new ArrayList<>();

        query = new StringBuffer();
        createSelectGroupIdFromUsersQuery();
        fetchData("fetchGroupId", listOfValues);

        query = new StringBuffer();
        createInsertIntoUsersQuery();
        listOfValues = new ArrayList<>();
        listOfValues.add(userName);
        listOfValues.add(email);
        listOfValues.add(groupName);
        listOfValues.add(groupId);
        listOfValues.add(1);
        int rc = executeData(listOfValues);

        User currentUser = new User(userName, email, groupName, groupId, 1);
        MainActivity.setCurrentUser(currentUser);
        MainActivity.getMyDbInstance().insertUser(currentUser, false);
        MainActivity.getMyDbInstance().insertMyGroup(currentUser.getUserName(), currentUser.geteMail(), currentUser.getGroupName(), currentUser.getGroupId());
        return rc;
    }

    void insertIntoUsers(String selectedAdmin, ArrayList<String> listOfEmails, String groupName)
    {
        MainActivity.getCurrentUser().setGroupName(groupName);
        ArrayList<Object> listOfValues;

        query = new StringBuffer();
        createUpdateGroupQuery();

        listOfValues = new ArrayList<>();
        listOfValues.add(groupName);
        listOfValues.add(selectedAdmin.equals(I_AM_ADMIN)? 1: 0);
        listOfValues.add(groupId);
        listOfValues.add(MainActivity.getMapOfProperties().get(USER));
        executeData(listOfValues);

        query = new StringBuffer();
        createInsertIntoUsersQuery();
        for (String email: listOfEmails)
        {
            listOfValues = new ArrayList<>();
            listOfValues.add("");
            listOfValues.add(email);
            listOfValues.add(groupName);
            listOfValues.add(groupId);
            listOfValues.add(email.equals(selectedAdmin)? 1: 0);

            executeData(listOfValues);
            MainActivity.getMyDbInstance().insertMyGroup("", email, groupName, groupId);
        }
    }

    void insertIntoUsers(ArrayList<User> listOfUsers)
    {
        ArrayList<Object> listOfValues;

        query = new StringBuffer();
        createInsertIntoUsersQuery();
        for (User user: listOfUsers)
        {
            listOfValues = new ArrayList<>();
            listOfValues.add(user.getUserName());
            listOfValues.add(user.geteMail());
            listOfValues.add(user.getGroupName());
            listOfValues.add(user.getGroupId());
            listOfValues.add(user.getUserName().equals(MainActivity.getMapOfProperties().get(USER))? 1: 0);

            executeData(listOfValues);
        }
        MainActivity.getMyDbInstance().updateMyGroup(listOfUsers.get(0).getGroupName(), "Active");
    }

    void fetchDataFromMenu(String whatToFetch)
    {
        ArrayList<Object> listOfValues = new ArrayList<>();
        query = new StringBuffer();

        listOfValues.add(MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(GROUP_ID): 0);
        createSelectFromMenuQuery();

        fetchData(whatToFetch, listOfValues);
    }

    int updateUser(String email, String userName, String groupName)
    {
        int rc;
        ArrayList<Object> listOfValues = new ArrayList<>();
        listOfValues.add(email);
        listOfValues.add(groupName);
        query = new StringBuffer();
        createSelectFromUsersQuery();
        rc = fetchData("fetchFromUsers", listOfValues);

        if (rc == 1)
        {
            listOfValues = new ArrayList<>();
            listOfValues.add(userName);
            listOfValues.add(email);
            listOfValues.add(groupName);
            query = new StringBuffer();
            createUpdateUserQuery();
            executeData(listOfValues);

            MainActivity.getMyDbInstance().updateMyGroup(userName, email, groupName);
        }

        return rc;
    }


    void fetchDataFromUsersAllPerGroup()
    {
        ArrayList<Object> listOfValues = new ArrayList<>();
        listOfValues.add(MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(GROUP_ID): 0);
        query = new StringBuffer();
        createSelectFromUsersAllPerGroupQuery();

        fetchData("fetchFromUsersAllThisGroup", listOfValues);
    }

    void fetchDataFromUsersAllPerUser()
    {
        ArrayList<Object> listOfValues = new ArrayList<>();
        listOfValues.add(MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN)? MainActivity.getMapOfProperties().get(USER): NO_USER);
        query = new StringBuffer();
        createSelectFromUsersAllPerUserQuery();

        fetchData("fetchFromUsersAllGroups", listOfValues);
    }

//    public void insertIntoHistory()
//    {
//        ArrayList<Object> listOfValues = new ArrayList<>();
//        connect();
//
//        for (Item item:MainActivity.getListOfItemsToSend()) {
//            boolean exists = false;
//            for (String str: MainActivity.getListOfHistory()) {
//                if (item.getValue().equalsIgnoreCase(str))
//                {
//                    exists = true;
//                    break;
//                }
//            }
//            if(!exists)
//            {
//                query = new StringBuffer();
//                listOfValues = new ArrayList<>();
//                createInsertIntoHistoryQuery();
//                listOfValues.add(item.getValue());
//                if (executeData(true, listOfValues) != 0)
//                {
//                    break;
//                }
//            }
//            //createSelectFromHistoryQuery();
//            //listOfValues.add(item.getValue());
//            //returnCode = fetchData("fetchCheckUserData", listOfValues, false);
//        }
//    }

    private void createUpdateMenuToDayValueQuery() {
        query.append("UPDATE menu SET to_day = -1 WHERE to_day = 0");
    }

    private void createUpdateUserQuery() {
        query.append("UPDATE user SET user_name = ? WHERE email = ? AND group_name = ?");
    }

    private void createUpdateGroupQuery() {
        query.append("UPDATE user SET group_name = ?, admin = ? WHERE group_id = ? AND user_name = ?");
    }

    private void createUpdateMenuOfTodayQuery() {
        query.append("UPDATE menu SET category_lvl = ?, dish_name = ? WHERE to_day = 0");
    }

    private void createDeleteFromMenuQuery() {
        query.append("DELETE FROM menu WHERE group_id = ?");
    }

    private void createDeleteFromUsersQuery() {
        query.append("DELETE FROM user WHERE email = ? AND group_name = ?");
    }

    private void createDeleteGroupFromUsersQuery() {
        query.append("DELETE FROM user WHERE group_name = ?");
    }

    private void createInsertIntoMenuQuery() {
        //INSERT INTO menu (category_lvl, dish_name, to_day) VALUES ('2', 'קציצות',
        //                                                                         -1)
        //INSERT INTO menu (category_lvl, dish_name, to_day) VALUES ('1', 'סלט גזר',
      //        -1)
        query.append("INSERT INTO menu (category_lvl, dish_name, group_id) ");
        query.append("VALUES (?, ?, ?) ");
    }

    private void createInsertIntoUsersQuery() {
        query.append("INSERT INTO user (user_name, email, group_name, group_id, admin) ");
        query.append("VALUES (?, ?, ?, ?, ?) ");
    }

    private void createSelectFromUsersQuery() {
        query.append("SELECT email, user_name, group_name, group_id, admin FROM user WHERE email = ? AND group_name = ?");
    }

    private void createSelectGroupIdFromUsersQuery() {
        query.append("SELECT MAX(group_id)+1 as group_id FROM user");
    }

    private void createSelectFromUsersAllPerGroupQuery() {
        query.append("SELECT user_name, email, group_id, group_name, admin FROM user WHERE group_id = ?");
    }

    private void createSelectFromUsersAllPerUserQuery() {
        query.append("SELECT user_name, email, group_id, group_name, admin FROM user WHERE group_id IN (SELECT group_id from user WHERE user_name = ?) order by group_id, admin desc");
    }

    private void createSelectFromMenuQuery() {
        query.append("SELECT * FROM menu WHERE group_id = ?");
    }

    private int fetchData(String whatToQuery, ArrayList<Object> listOfValues)
    {
        int rc = 0;

        try
        {
            stmt = conn.prepareStatement(query.toString());
            bindValues(listOfValues);
            ResultSet rset = stmt.executeQuery();

            switch (whatToQuery)
            {
                case "fetchFromUsers":
                    rc = fetchFromUsers(rset); break;
                case "fetchFromUsersAllThisGroup":
                    rc = fetchFromUsersAllThisGroup(rset); break;
                case "fetchFromUsersAllGroups":
                    rc = fetchFromUsersAllGroups(rset); break;
                case "fetchTodayFromMenu":
                    rc = fetchTodayFromMenu(rset); break;
                case "fetchYesterdayFromMenu":
                    rc = fetchYesterdayFromMenu(rset); break;
                case "fetchGroupId":
                    rc = fetchGroupId(rset); break;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
            rc = -1;

        }
        return rc;
    }

    private int executeData(ArrayList<Object> listOfValues)
    {
        try
        {
            stmt = conn.prepareStatement(query.toString());
            bindValues(listOfValues);

            stmt.executeUpdate();
            return 0;
        }catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
        }
        return -1;
    }

    private int fetchFromUsers(ResultSet rSet) throws SQLException {
        int rc = 0;
        while (rSet.next()) {

            String email = rSet.getString("email");
            String name = rSet.getString("user_name");
            String groupName = rSet.getString("group_name");
            int groupId = rSet.getInt("group_id");
            int admin = rSet.getInt("admin");
            MainActivity.setCurrentUser(new User(name, email, groupName, groupId, admin));
            rc = 1;
        }
        return rc;
    }

    private int fetchFromUsersAllThisGroup(ResultSet rset) throws SQLException {
        int rc = 0;
        int prevGroupId = 0;
        while (rset.next()) {
            String email = rset.getString("email");
            String name = rset.getString("user_name");
            String groupName = rset.getString("group_name");
            int groupId = rset.getInt("group_id");
            int admin = rset.getInt("admin");
            MainActivity.getListOfThisGroupUsers().add(new User(name, email, groupName, groupId, admin));
            if (prevGroupId != groupId)
            {
                MainActivity.getGroups().add(new Group(groupId, groupName, admin == 1? name: ""));
            }

            prevGroupId = groupId;
            rc = 1;
        }
        return rc;
    }

    private int fetchFromUsersAllGroups(ResultSet rset) throws SQLException {
        int rc = 0;
        int prevGroupId = 0;
        MainActivity.getListOfAllGroupsUsers().clear();
        MainActivity.getGroups().clear();

        while (rset.next()) {
            String email = rset.getString("email");
            String name = rset.getString("user_name");
            String groupName = rset.getString("group_name");
            int groupId = rset.getInt("group_id");
            int admin = rset.getInt("admin");
            MainActivity.getListOfAllGroupsUsers().add(new User(name, email, groupName, groupId, admin));

            if (prevGroupId != groupId)
            {
                MainActivity.getGroups().add(new Group(groupId, groupName,admin == 1? name: ""));
            }

            prevGroupId = groupId;
            rc = 1;
        }
        return rc;
    }

    private int fetchTodayFromMenu(ResultSet rset) throws SQLException {
        MainActivity.getListOfItems().clear();
        while (rset.next()) {
            Item item = new Item();
            item.setLevel(rset.getInt("category_lvl"));
            item.setCategory(Utils.getCategoryFromLevel(item.getLevel()));
            item.setValue(rset.getString("dish_name"));
            MainActivity.getListOfItems().add(item);
        }
        return 0;
    }

    private int fetchYesterdayFromMenu(ResultSet rset) throws SQLException {
        MainActivity.getMyDbInstance().deleteFromMenu();

        while (rset.next()) {
            Item item = new Item();
            item.setLevel(rset.getInt("category_lvl"));
            item.setCategory(Utils.getCategoryFromLevel(item.getLevel()));
            item.setValue(rset.getString("dish_name"));

            MainActivity.getMyDbInstance().insertMenu(item);
        }
        return 0;
    }


    private int fetchGroupId(ResultSet rset) throws SQLException {
        while (rset.next()) {
            groupId = rset.getInt("group_id");
            groupId = groupId == 0? 1: groupId;
        }
        return 0;
    }

    private int fetchUsersOfGroup (ResultSet rset) throws SQLException {
        while (rset.next()) {
            int userName = rset.getInt("user_name");
            int email = rset.getInt("email");
            int groupName = rset.getInt("group_name");
        }
        return 0;
    }

    private void bindValues(ArrayList<Object> listOfValues) throws SQLException {
        for (int i = 0 ; i < listOfValues.size(); i++) {
            Object o = listOfValues.get(i);
            if (o instanceof String)
            {
                stmt.setString(i+1, o.toString());
            }
            else if(o instanceof Integer)
            {
                stmt.setInt(i+1, Integer.parseInt(o.toString()));
            }
        }
    }
}
