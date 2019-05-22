package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;

import com.example.maratbe.whatdoieattoday.Dialogs.DisableOptionDialog;
import com.example.maratbe.whatdoieattoday.Dialogs.MessageDialog;
import com.example.maratbe.whatdoieattoday.Dialogs.SpinnerDialog;
import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.Group;
import com.example.maratbe.whatdoieattoday.objects.Item;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils implements Constants {
    private static MessageDialog messageDialog;
    private static DisableOptionDialog disableOptionDialog;
    private static SpinnerDialog spinnerDialog;

    public static Drawable createBorder(int radius, int color, int stroke) {
        GradientDrawable gd;
        gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        gd.setStroke(stroke, BLACK_2);
        return gd;
    }

    static Drawable createBorder(int radius, int color, int stroke, int strokeColor) {
        GradientDrawable gd;
        gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        gd.setStroke(stroke, strokeColor);
        return gd;
    }

    static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);

    }

    static boolean isKeyboardShown(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static void openKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static int getLevelFromCategory(String category) {
        switch (category) {
            case SALADS:
                return SALADS_LVL;
            case MAIN_DISH:
                return MAIN_LVL;
            default:
                return SIDE_LVL;
        }
    }

    static String getCategoryFromLevel(int level) {
        switch (level) {
            case SALADS_LVL:
                return SALADS;
            case MAIN_LVL:
                return MAIN_DISH;
            default:
                return SIDE_DISH;
        }
    }

    static void showMessage(Activity activity, String[] texts, int[] colors, int[] buttons, String action) {
        messageDialog = new MessageDialog(activity, (int) (MainActivity.getScreenWidth() * 0.8), colors, texts, buttons) {
            @Override
            public void okButtonPressed() {
                MyGroups myGroups;
                switch(action)
                {
                    case CREATE_ACCOUNT:
                        Intent intent = new Intent(activity, Account.class);
                        intent.putExtra("fromMain", false);
                        activity.startActivityForResult(intent, 0); break;
                    case USER_DELETED:
                        myGroups = (MyGroups) activity;
                        myGroups.updateList();
                        myGroups.refreshList(); break;
                    case ONLY_USERS_IN_SETTINGS:
                        Settings settings = (Settings) activity;
                        settings.closeSettings(RC_CANCEL); break;
                    case GROUP_CHANGED:
                    case GROUP_DELETED:
                        myGroups = (MyGroups) activity;
                        myGroups.refreshList(); break;
                    case DELETE_GROUP:
                        myGroups = (MyGroups) activity;
                        myGroups.deleteGroup(); break;
                    case RETRIEVE_GROUP:
                        MakeGroup makeGroup = (MakeGroup) activity;
                        makeGroup.retrieveGroup(); break;
                }
                messageDialog.dismiss();
            }

            @Override
            public void cancelButtonPressed() {
                if (activity instanceof MakeGroup)
                {
                    Intent returnIntent = new Intent();
                    activity.setResult(0, returnIntent);
                    activity.finish();
                }
                messageDialog.dismiss();
            }
        };
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.show();
    }

    static void showDisableOptionMessage(Activity activity, String[] texts, int[] colors, int[] buttons, String action) {
        disableOptionDialog = new DisableOptionDialog(activity, (int) (MainActivity.getScreenWidth() * 0.8), colors, texts, buttons) {
            @Override
            public void okButtonPressed(boolean disable) {
                MyGroups myGroups;
                if (disable)
                {
                    MainActivity.getMyDbInstance().updateProperties(MainActivity.getMapOfProperties().get(ALERTS), "N");
                }

                switch (action)
                {
                    case DELETE_USER:
                        myGroups = (MyGroups) activity;
                        myGroups.deleteUser();;break;
                    case SAVE_CHOICE:
                        MainActivity mainActivity = (MainActivity) activity;
                        mainActivity.saveUsersChoices();break;
                    case ADD_TO_GROUP:
                        myGroups = (MyGroups) activity;
                        myGroups.addUsers(); break;
                }
                disableOptionDialog.dismiss();
            }

            @Override
            public void cancelButtonPressed() {
                disableOptionDialog.dismiss();
            }
        };
        disableOptionDialog.setCanceledOnTouchOutside(false);
        disableOptionDialog.show();
    }

    static void showSpinnerDialogMessage(Activity activity, int[] texts, int[] colors, int[] buttons, String[] spinnerValues) {
        spinnerDialog = new SpinnerDialog(activity, (int) (MainActivity.getScreenWidth() * 0.8), colors, texts, buttons, spinnerValues) {
            @Override
            public void okButtonPressed(String selectedEmail) {
                spinnerDialog.dismiss();
                if (activity instanceof MakeGroup) {
                    MakeGroup makeGroup = (MakeGroup) activity;
                    makeGroup.sendAMail(selectedEmail);
                }
            }

            @Override
            public void cancelButtonPressed() {
                spinnerDialog.dismiss();
            }
        };
        spinnerDialog.setCanceledOnTouchOutside(false);
        spinnerDialog.show();
    }

    public static boolean emailMatch(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static String getStringDate(int day, int month) {
        Calendar c = Calendar.getInstance();
        int yearInt = month / 100;
        int monthInt = Integer.parseInt(removePadding(String.valueOf(month).substring(4, 6)));
        c.set(yearInt, (monthInt) - 1, day);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = getNameOfDayWeek(dayOfWeek);
        String monthStr = getNameOfMonth(monthInt);
        return dayOfWeekStr + ", " + day +" " + "ל"+monthStr + " "+ yearInt;
    }

    private static String removePadding(String number) {
        return number.substring(0,1).equals("0")? number.substring(1,2): number;
    }

    private static String getNameOfMonth(int month) {
        switch (month)
        {
            case 1:
                return "ינואר";
            case 2:
                return "פברואר";
            case 3:
                return "מרץ";
            case 4:
                return "אפריל";
            case 5:
                return "מאי";
            case 6:
                return "יוני";
            case 7:
                return "יולי";
            case 8:
                return "אוגוסט";
            case 9:
                return "ספטמבר";
            case 10:
                return "נובמבר";
            case 11:
                return "אוקטובר";
            default:
                return "דצמבר";
        }
    }

    private static String getNameOfDayWeek(int firstDayOfWeek) {
        switch (firstDayOfWeek) {
            case 1:
                return "יום ראשון";
            case 2:
                return "יום שני";
            case 3:
                return "יום שלישי";
            case 4:
                return "יום רביעי";
            case 5:
                return "יום חמישי";
            case 6:
                return "יום שישי";
            default:
                return "יום שבת";
        }
    }

    static private String paddingNumbers(int number) {
        if (number > 0 && number < 10) {
            return "0" + number;
        }
        return number + "";
    }

    public static String getDishesOfCategory(ArrayList<Item> items, String category) {
        String str = "";
        int count = 0;
        for (Item item: items)
        {
            if (item.getCategory().equals(category))
            {
                if (count == 0)
                {
                    str += item.getValue();
                    count++;
                }
                else
                {
                    str += ", "+item.getValue();
                }
            }
        }
        return str;
    }

    public static int getRandomDateColor() {
        Random random = new Random();
        return  MainActivity.getsArrayOfDateColors().get(random.nextInt(10));
    }

    public static User getRegisteredUser(String userName, String email, String groupName) {
        for (User user: MainActivity.getListOfUsers())
        {
            if (user.getUserName().equals(userName) && user.getGroupName().equals(groupName) ||
                    user.geteMail().equals(email) && user.getGroupName().equals(groupName) )
            {
                return user;
            }
        }
        return null;
    }

    static int isAdmin(User user) {
        for (Group group: MainActivity.getGroups())
        {
            if (user.getGroupId() == group.getGroupId() && MainActivity.getMapOfProperties().get(USER).equals(group.getAdminName()))
            {
                return View.VISIBLE;
            }
        }
        return View.GONE;
    }

    static boolean isDefaultGroup(int groupId) {
        return groupId == Integer.parseInt(MainActivity.getMapOfProperties().get(GROUP_ID));
    }

    static void setProperties(User user) {
        MainActivity.getMapOfProperties().put(LOGGED_IN, IN);
        MainActivity.getMapOfProperties().put(USER, user.getUserName());
        MainActivity.getMapOfProperties().put(EMAIL, user.geteMail());
        MainActivity.getMapOfProperties().put(GROUP_ID, user.getGroupId()+"");
        MainActivity.getMapOfProperties().put(ADMIN, user.getAdmin()+"");
    }

    public static void showNotConnectedMessage(Activity activity) {
        int[] colors = new int[]{RED_6, RED_4, CHERY};
        String[] texts = new String[]{activity.getString(R.string.sorry_message), activity.getString(R.string.still_no_connection)};
        int[] buttons = new int[]{R.string.ok_dialog_btn};
        Utils.showMessage(activity, texts, colors, buttons, SERVER_DOWN);
    }

    public static void showErrorMessage(Activity activity, String message) {
        int[] colors = new int[]{RED_6, RED_4, CHERY};
        String[] texts = new String[]{activity.getString(R.string.sorry_message), message};
        int[] buttons = new int[]{R.string.ok_dialog_btn};
        Utils.showMessage(activity, texts, colors, buttons, ERROR_OCCURRED);
    }

    public static String getActiveGroup() {
        for(Group group: MainActivity.getGroups())
        {
            if (group.getGroupId() != Integer.parseInt(MainActivity.getMapOfProperties().get(GROUP_ID)))
            {
                return String.valueOf(group.getGroupId());
            }
        }
        return "";
    }

    public static int getGroupIdBuyName(String groupName)
    {
        for (Group group: MainActivity.getGroups())
        {
            if (group.getGroupName().equals(groupName))
            {
                return group.getGroupId();
            }
        }
        return 0;
    }

    public static String getGroupNameById(String groupId) {
        for (Group group: MainActivity.getGroups())
        {
            if (!groupId.equals("") && group.getGroupId() == Integer.parseInt(groupId))
            {
                return group.getGroupName();
            }
        }
        return "";
    }
}
