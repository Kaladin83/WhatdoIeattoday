package com.example.maratbe.whatdoieattoday;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.util.ArrayList;

public class Account extends AppCompatActivity implements Constants,View.OnClickListener {
    private AutoCompleteTextView nameEdit, emailEdit, groupNameEdit;
    private Account account;
    private boolean isCalledFromMain;
    private LinearLayout progressBarLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        account = this;

        Intent myIntent = getIntent();
        isCalledFromMain = myIntent.getBooleanExtra("fromMain", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(this);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof AppCompatImageButton) {
                child.setId(R.id.home_btn);
                break;
            }
        }

        TextView headerTxt = findViewById(R.id.header_txt);
        headerTxt.setTypeface(Typeface.createFromAsset(getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);
        TextView signinExplanationTxt = findViewById(R.id.signin_explanation_txt);
        signinExplanationTxt.setBackgroundColor(GRAY_3);
        TextView loginExplanationTxt = findViewById(R.id.login_explanation_txt);
        loginExplanationTxt.setBackgroundColor(GRAY_3);

        Button saveAccountToolbarBtn = findViewById(R.id.save_account_toolbar_btn);
        saveAccountToolbarBtn.setBackground(Utils.createBorder(10, Color.WHITE, 1, Color.WHITE));
        saveAccountToolbarBtn.setPadding(0,0,0,0);
        saveAccountToolbarBtn.setOnClickListener(this);
        saveAccountToolbarBtn.setVisibility(View.VISIBLE);

        MyOnEditorActionListener onEditorActionListener = new MyOnEditorActionListener();
        String[] userNames = getUserNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        nameEdit = findViewById(R.id.name_edit);
        nameEdit.setAdapter(adapter);
        nameEdit.setOnEditorActionListener(onEditorActionListener);
        emailEdit = findViewById(R.id.email_edit);
        emailEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS  | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEdit.setOnEditorActionListener(onEditorActionListener);
        groupNameEdit = findViewById(R.id.group_name_edit);
        groupNameEdit.setOnEditorActionListener(onEditorActionListener);

        progressBarLayout = findViewById(R.id.progress_bar);
        progressBarLayout.setVisibility(View.GONE);
        progressBarLayout.setBackground(Utils.createBorder(10,Constants.RED_6, 0));
    }

    private String[] getUserNames() {
        String prevUserName = "";
        ArrayList<String> distinctNames = new ArrayList<>();
        for (int i = 0; i < MainActivity.getListOfUsers().size(); i++)
        {
            if (!prevUserName.equals(MainActivity.getListOfUsers().get(i).getUserName()))
            {
                distinctNames.add(MainActivity.getListOfUsers().get(i).getUserName());
                prevUserName = MainActivity.getListOfUsers().get(i).getUserName();
            }
        }

        String[] users = new String[distinctNames.size()];
        for (int i = 0; i < distinctNames.size(); i++)
        {
            users[i] = distinctNames.get(i);
        }

        return users;
    }

    @Override
    public void onClick(View v) {
        String userName = "", email = "", groupName = "";
        int resultCode = 0;
        if (v.getId() == R.id.save_account_toolbar_btn)
        {
            new ProgressTask().execute();
//            MainActivity.getDbInstance().connect();
//            userName = nameEdit.getText().toString();
//            email = emailEdit.getText().toString();
//            groupName = groupNameEdit.getText().toString();
//            boolean userFound = userFound(userName, email);
//            if (userName.equals("") && email.isEmpty() || (!Utils.emailMatch(email) && !userFound)
//                    || (groupName.isEmpty() && !userFound && isCalledFromMain))
//            {
//                int[] colors = new int[]{RED_6, RED_4, CHERY};
//                String[] texts = new String[]{getString(R.string.sorry_message), getString(R.string.wrong_user_input)};
//                int[] buttons = new int[]{R.string.ok_dialog_btn};
//                Utils.showMessage(this, texts, colors, buttons, VALIDATE_USER);
//                return;
//            }
//
//            if (isCalledFromMain)
//            {
//                MainActivity.getMyDbInstance().fetchUsers(userName, email);
//                User user = Utils.getRegisteredUser(userName, email, groupName.isEmpty()? MainActivity.getCurrentUser().getGroupName(): groupName);
//                if (user == null && MainActivity.getDbInstance().updateUser(email, userName, groupName) == 0)
//                {
//                    int[] colors = new int[]{RED_6, RED_4, CHERY};
//                    String[] texts = new String[]{getString(R.string.sorry_message), getString(R.string.user_not_registered)};
//                    int[] buttons = new int[]{R.string.ok_dialog_btn};
//                    Utils.showMessage(this, texts, colors, buttons, VALIDATE_USER);
//                    return;
//                }
//                MainActivity.getMyDbInstance().insertUser(new User(userName, email, groupName,
//                                MainActivity.getCurrentUser().getGroupId(), MainActivity.getCurrentUser().getAdmin()), userFound);
//
//                Utils.closeKeyboard(this);
//                Intent i = new Intent(this, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//            }
//            else
//            {
//                MainActivity.getDbInstance().insertUserWithGroup(userName, email, groupName);
//
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("GROUP_NAME", groupName);
//                returnIntent.putExtra("RETURNED_FROM_ACCOUNT", true);
//                setResult(-1, returnIntent);
//                finish();
//            }
//            MainActivity.getDbInstance().closeConnection();
        }
        if (v.getId() == R.id.home_btn)
        {
            endActivity(RC_CANCEL);
        }
    }

    private void endActivity(int rc) {
        Intent returnIntent = new Intent();
        setResult(rc, returnIntent);
        finish();
    }

    private boolean userFound(String userName, String email) {
        for (User user: MainActivity.getListOfUsers())
        {
            if (user.getUserName().equals(userName) || user.geteMail().equals(email))
            {
                return true;
            }
        }
        return false;
    }

    class MyOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                Utils.closeKeyboard(account);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        int resultCode = RC_CANCEL;
        String groupName = "", errorMessage = "";
        @Override
        protected void onPreExecute(){
            progressBarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String userName = "", email = "";
            MainActivity.getDbInstance().connect();
            userName = nameEdit.getText().toString();
            email = emailEdit.getText().toString();
            groupName = groupNameEdit.getText().toString();
            boolean userFound = userFound(userName, email);
            if (userName.equals("") && email.isEmpty() || (!Utils.emailMatch(email) && !userFound)
                    || (groupName.isEmpty() && !userFound && isCalledFromMain))
            {
                errorMessage = getString(R.string.wrong_user_input);
                resultCode = RC_ERROR;
            }

            if (MainActivity.isConnected() && resultCode == RC_CANCEL)
            {
                if (isCalledFromMain)
                {
                    MainActivity.getMyDbInstance().fetchUsers(userName, email);
                    User user = Utils.getRegisteredUser(userName, email, groupName.isEmpty()? MainActivity.getCurrentUser().getGroupName(): groupName);
                    if (user == null && MainActivity.getDbInstance().updateUser(email, userName, groupName) == 0)
                    {
                        errorMessage = getString(R.string.user_not_registered);
                        resultCode = RC_ERROR;
                    }
                    else{
                        MainActivity.getMyPreferences().storeData("Y", String.valueOf(MainActivity.getCurrentUser().getId()));
                        MainActivity.getMyDbInstance().updateProperties(LOGGED_IN, IN);
                        MainActivity.getMyDbInstance().insertUser(new User(userName, email, groupName,
                                MainActivity.getCurrentUser().getGroupId(), MainActivity.getCurrentUser().getAdmin()), userFound);

                        Utils.closeKeyboard(account);
                        resultCode = RC_OK;
                    }
                }
                else
                {
                    resultCode = MainActivity.getDbInstance().insertUserWithGroup(userName, email, groupName) == 0? RC_OK: RC_ERROR;
                    if (resultCode == RC_ERROR)
                    {
                        errorMessage = getString(R.string.could_not_create_group);
                        resultCode = RC_ERROR;
                    }
                }
                MainActivity.getDbInstance().closeConnection();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBarLayout.setVisibility(View.GONE);
            switch (resultCode)
            {
                case RC_NOT_CONNECTED:
                    Utils.showNotConnectedMessage(account); break;
                case RC_ERROR:
                    Utils.showErrorMessage(account, errorMessage); break;
                default:
                    if (isCalledFromMain)
                    {
                        endActivity(resultCode);
                    }
                    else
                    {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("GROUP_NAME", groupName);
                        returnIntent.putExtra("RETURNED_FROM_ACCOUNT", true);
                        setResult(resultCode, returnIntent);
                        finish();
                    }
            }
        }
    }
}
