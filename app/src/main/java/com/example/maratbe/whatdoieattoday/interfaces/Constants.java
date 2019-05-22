package com.example.maratbe.whatdoieattoday.interfaces;

import android.graphics.Color;

/**
 * Created by MARATBE on 10/11/2018.
 */

public interface Constants {

    int TOOLBAR = Color.parseColor("#eff2f7");
    int BLACK_1 = Color.parseColor("#716e72");
    int BLACK_2 = Color.parseColor("#767477");
    int GRAY_1 = Color.parseColor("#c4c5c6");
    int GRAY_2 = Color.parseColor("#e5e5e5");
    int GRAY_3 = Color.parseColor("#f7f7f7");

    int RED_1 = Color.parseColor("#f7120e");
    int RED_2 = Color.parseColor("#ff3b2d");
    int RED_3 = Color.parseColor("#db3632");
    int RED_4 = Color.parseColor("#f95550");
    int RED_5 = Color.parseColor("#fcd2d1");
    int RED_6 = Color.parseColor("#fcf4f4");
    int RED_7 = Color.parseColor("#f7f2f3");

    int CHERY = Color.parseColor("#c66865");

    int GREEN_1 = Color.parseColor("#20e83a");
    int GREEN_2 = Color.parseColor("#25c63a");
    int GREEN_3 = Color.parseColor("#87f295");
    int GREEN_4 = Color.parseColor("#cdf9ae");
    int GREEN_5 = Color.parseColor("#d5f7be");
    int GREEN_6 = Color.parseColor("#edfce3");

    int TURQUOISE = Color.parseColor("#99c49f");

    int BLUE_1 = Color.parseColor("#2e79f4");
    int BLUE_2 = Color.parseColor("#7fa9ef");
    int BLUE_3 = Color.parseColor("#ceebef");
    int BLUE_4 = Color.parseColor("#dae3f2");
    int BLUE_5 = Color.parseColor("#f4f9fc");
    int ORANGE_1 = Color.parseColor("#fc4300");
    int ORANGE_2 = Color.parseColor("#ff5a1e");
    int ORANGE_3 = Color.parseColor("#ff6b35");
    int ORANGE_4 = Color.parseColor("#f77f54");
    int ORANGE_5 = Color.parseColor("#eff9bb");

    int YELLOW_1 = Color.parseColor("#d3f713");
    int YELLOW_2 = Color.parseColor("#f4ec58");
    int YELLOW_3 = Color.parseColor("#e7f986");
    int YELLOW_4 = Color.parseColor("#f0fcae");
    int YELLOW_5 = Color.parseColor("#eff9bb");

    int PURPLE_1 = Color.parseColor("#cf63ed");
    int PURPLE_2 = Color.parseColor("#db9ced");
    int PURPLE_3 = Color.parseColor("#ebc0f7");
    int PURPLE_4 = Color.parseColor("#f9e8ff");
    int PURPLE_5 = Color.parseColor("#fbf2ff");

    int SALADS_LVL = 0;
    int MAIN_LVL = 1;
    int SIDE_LVL = 2;

    int BUILD_MENU_SCREEN = 0;
    int CART_SCREEN = 1;
    int MENU_SCREEN = 2;


    int DIALOG_TEXT_SIZE = 16;

    String API_KEY = "AIzaSyA1Sq5wPaY2riy__7ufpLvSEbaDKjFoq8A";
    String SENDER_PASS = "Marat6753630";
    String SENDER_EMAIL = "mbeiner83@gmail.com";
    String SALADS = "סלטים";
    String MAIN_DISH = "עיקרית";
    String SIDE_DISH = "תוספת";

    String LOGGED_IN = "LOGGED_IN";
    String USER = "USER";
    String USER_ID = "USER_ID";
    String EMAIL = "EMAIL";
    String GROUP_ID = "GROUP_ID";
    String GROUP_NAME = "GROUP_NAME";
    String ADMIN = "ADMIN";
    String ALERTS = "ALERTS";
    String CART_VIEW = "CART_VIEW";

    String NO_USER = "אורח, אנא תתחבר";
    String NO_GROUP = "";
    String NO_EMAIL = "";
    String DISPLAY_LIST = "DISPLAY_LIST";
    String DISPLAY_CALENDAR = "DISPLAY_CALENDAR";

    String IN = "Y";
    String OUT = "N";

    String I_AM_ADMIN = "אני מנהל קבוצה";
    String USER_NOT_SIGN = "המשתמש עדיין לא נרשם";

    int GROUP_NAME_TXT = 0;
    int LEFT_DIV = 1;
    int POPUP_BTN = 2;
    int RIGHT_DIV = 3;
    int BUTTONS= 4;

    String GROUP_CHANGED = "GROUP_CHANGED";
    String VALIDATE_USER = "VALIDATE_USER";
    String MAIL_SENT = "MAIL_SENT";
    String DATA_SAVED = "DATA_SAVED";
    String SERVER_DOWN = "SERVER_DOWN";
    String ERROR_OCCURRED = "SERVER_DOWN";
    String CREATE_ACCOUNT = "CREATE_ACCOUNT";
    String CREATE_GROUP = "CREATE_GROUP";
    String USER_DELETED = "USER_DELETED";
    String GROUP_DELETED = "GROUP_DELETED";
    String ONLY_USERS_IN_SETTINGS = "ONLY_USERS_IN_SETTINGS";
    String SAVE_CHOICE = "SAVE_CHOICE";
    String ADD_TO_GROUP = "ADD_TO_GROUP";
    String DELETE_USER = "DELETE_USER";
    String DELETE_GROUP = "DELETE_GROUP";
    String RETRIEVE_GROUP = "RETRIEVE_GROUP";



    int ACTION_LOAD_GUI = 0;
    int ACTION_DELETE_USER = 1;
    int ACTION_DELETE_GROUP = 2;
    int ACTION_RETRIEVE_GROUP = 3;
    int ACTION_SEND_MAIL = 4;
    int ACTION_FETCH_GROUP = 5;
    int ACTION_REFRESH = 6;
    int ACTION_SEND_ORDER = 7;

    int RC_OK = 0;
    int RC_CANCEL = -1;
    int RC_ERROR = -2;
    int RC_NOT_CONNECTED = -3;
}
