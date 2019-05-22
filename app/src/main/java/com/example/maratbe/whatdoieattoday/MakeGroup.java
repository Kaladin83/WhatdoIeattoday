package com.example.maratbe.whatdoieattoday;

import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by MARATBE on 11/8/2018.
 */

public class MakeGroup extends AppCompatActivity implements View.OnClickListener, Constants {
    private MakeGroup makeGroup;
    private RecyclerViewAdapter menuAdapter;
    private Button incButton, decButton, saveButton, saveToolbarButton;
    private int numOfItems = 1, recyclerLayoutHeight, headerHeight = 0, syncTaskAction;
    private String passedGroupName, deletedGroup;
    private boolean returnedFromAccount = false;
    private String selectedAdmin;
    private RecyclerView recyclerView;
    private TextView headerTxt;
    private ArrayList<String> listOfEmails = new ArrayList<>();
    private Window window;
    private LinearLayout progressBarLayout;
    private EditText groupNameEdit;
//    private Activity activity;
//    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_group);

        Intent myIntent = getIntent(); // gets the previously created intent
        passedGroupName = myIntent.getStringExtra("GROUP_NAME");

        makeGroup = this;
        window = this.getWindow();
        window.setStatusBarColor(RED_4);
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

        final View activityRootView = findViewById(R.id.root_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
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
        //loadLocalLists();
        numOfItems = listOfEmails.size() == 0? 1: listOfEmails.size() ;
        setDimensions();
        setFields();
        createRecycler();

        if (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(OUT))
        {
            int[] colors = new int[]{GRAY_3, GRAY_1, RED_4};
            String[] texts = new String[]{getString(R.string.info_message), getString(R.string.connect_before_make_group)};
            int[] buttons = new int[]{R.string.ok_dialog_btn, R.string.cancel_dialog_btn};
            Utils.showMessage(this, texts, colors, buttons, CREATE_ACCOUNT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            switch (resultCode)
            {
                case RC_OK:
                    groupNameEdit.setText(data.getStringExtra("GROUP_NAME"));
                    returnedFromAccount = data.getBooleanExtra("RETURNED_FROM_ACCOUNT", false);
                    int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
                    String[] texts = new String[]{getString(R.string.success_message), getString(R.string.can_create_group_message)};
                    int[] buttons = new int[]{R.string.ok_dialog_btn};
                    Utils.showMessage(this, texts, colors, buttons, CREATE_GROUP); break;
                case RC_CANCEL:
                    returnToMain(RC_CANCEL);
            }

        }
    }

    private void setDimensions() {
        int mainButtonsLayoutHeight = 40*MainActivity.getLogicalDensity();
        int displayHeight = MainActivity.getScreenHeight() - MainActivity.getToolbarHeight();
        headerHeight = 140 * MainActivity.getLogicalDensity();
        int buttonLayoutHeight = 60 * MainActivity.getLogicalDensity();
        recyclerLayoutHeight = displayHeight - headerHeight - buttonLayoutHeight - 20* MainActivity.getLogicalDensity();
    }

    private void setRecyclerLayoutParams(int height) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lParams.setMargins(10*MainActivity.getLogicalDensity(), 0, 10*MainActivity.getLogicalDensity(), 10*MainActivity.getLogicalDensity());
        recyclerView.setLayoutParams(lParams);
    }

    private void setFields() {
        headerTxt = findViewById(R.id.header_txt);
        headerTxt.setTypeface(Typeface.createFromAsset(getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);
        incButton = findViewById(R.id.inc_btn);
        incButton.setOnClickListener(this);
        decButton = findViewById(R.id.dec_btn);
        decButton.setOnClickListener(this);
        saveButton = findViewById(R.id.send_btn);
        saveButton.setOnClickListener(this);
        saveButton.setBackground(Utils.createBorder(10, Color.TRANSPARENT, 1, Constants.RED_1));
        saveButton.setTextColor(RED_1);

        TextView emailExplanationTxt = findViewById(R.id.email_explanation_txt);
        emailExplanationTxt.setBackgroundColor(GRAY_3);
        groupNameEdit = findViewById(R.id.group_name_edit);
        groupNameEdit.setText(passedGroupName);
        groupNameEdit.setOnFocusChangeListener((v, hasFocus) -> {
            deletedGroup = ((EditText)v).getText().toString();
            if (!deletedGroup.isEmpty() && !hasFocus)
            {
                MainActivity.getMyDbInstance().fetchDeletedGroup(deletedGroup);
                if (MainActivity.getListOfDeletedUsers().size() > 0)
                {
                    int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
                    String[] texts = new String[]{getString(R.string.choice_message_header), getString(R.string.group_exists, deletedGroup)};
                    int[] buttons = new int[]{R.string.send_dialog_btn, R.string.cancel_dialog_btn};
                    Utils.showMessage(makeGroup, texts, colors, buttons, RETRIEVE_GROUP);
                }
            }
        });

        progressBarLayout = findViewById(R.id.progress_bar);
        progressBarLayout.setVisibility(View.GONE);
        progressBarLayout.setBackground(Utils.createBorder(10,Constants.RED_6, 0));
    }

    @Override
    public void onClick(View view) {
        int rc = 0;
        switch (view.getId()) {
            case R.id.inc_btn:
                numOfItems++;
                menuAdapter.notifyDataSetChanged();
                ScrollToLastItem();
                break;
            case R.id.dec_btn:
                if (numOfItems > 0) {
                    if (listOfEmails.size() > 0 && numOfItems <= listOfEmails.size()) {
                        listOfEmails.remove(numOfItems - 1);
                    }
                    numOfItems--;
                    menuAdapter.notifyDataSetChanged();
                    ScrollToLastItem();
                }
                break;
            case R.id.send_btn:
                if (passedGroupName.equals(""))
                {
                    Utils.closeKeyboard(this);
                    int[] colors = new int[]{GRAY_3, GRAY_1, RED_4};
                    int[] texts = new int[]{R.string.info_message, R.string.choose_admin_message};
                    int[] buttons = new int[]{R.string.ok_dialog_btn, R.string.cancel_dialog_btn};
                    String[] spinnerValues = getEmailsArray();
                    Utils.showSpinnerDialogMessage(this, texts, colors, buttons, spinnerValues);
                }
                else
                {
                    sendAMail(MainActivity.getMapOfProperties().get(EMAIL));
                }
                break;
            case R.id.home_btn:
                returnToMain(Activity.RESULT_CANCELED);
        }
    }

    private String[] getEmailsArray() {
        String[] emails = new String[listOfEmails.size()+1];
        emails[0] = I_AM_ADMIN;
        for (int i = 0; i < listOfEmails.size(); i++)
        {
            emails[i+1] = listOfEmails.get(i);
        }
        return emails;
    }

    public void retrieveGroup()
    {
        syncTaskAction = ACTION_RETRIEVE_GROUP;
        new ProgressTask().execute();
    }

    private void returnToMain(int rc) {
        Intent returnIntent = new Intent();
        setResult(rc, returnIntent);
        finish();
    }

    public void sendAMail(String selectedAdmin) {
        this.selectedAdmin = selectedAdmin;
        syncTaskAction = ACTION_SEND_MAIL;
        new ProgressTask().execute();
    }

    private String getEmailBody() {
        StringBuilder sb = new StringBuilder("שלום ");
        int inviteNum = new Random().nextInt((99999 - 10000) + 1) + 10000;
        sb.append(",\n");
        sb.append("קיבלת מייל זה כי נרשמת באפליקציה 'מה אוכלים היום?' או שמישהו צירף אותך.\n");
        sb.append("אם קיבלת את ההמייל בטעות אנא תתעלם.\n");
        sb.append("כדי להתחבר לקבוצת יוניון אליך להיכנס למסך 'כניסה ולהזין את הפרטים הבאים:\n");
//        sb.append("דואר אלקטרוני: כתובת זו");
//        sb.append("\n");
//        sb.append("שם קבוצה: ");
//        sb.append(groupNameEdit.getText().toString());
//        sb.append("\n\n");
//        sb.append("בתאבון");
        return sb.toString();
    }


    private void ScrollToLastItem() {
        if (recyclerView.getLayoutManager() != null)
        {
            recyclerView.getLayoutManager().scrollToPosition(recyclerView.getLayoutManager().getItemCount()-1);
        }
    }

//    private void loadMainList() {
//        MainActivity.getListOfItemsToSend().clear();
//        for (int i = 0; i<listOfEmails.size(); i++) {
//            MainActivity.getListOfItemsToSend().add(new Item(listOfEmails.get(i), Utils.getCategoryFromLevel(listOfEmails.get(i)), listOfEmails.get(i)));
//        }
//    }

//    public void loadLocalLists() {
//        listOfSpinnerValues.clear();
//        listOfEditValues.clear();
//
//        for (Item item: MainActivity.getListOfItemsToSend()) {
//            listOfSpinnerValues.add(item.getLevel());
//            listOfEditValues.add(item.getValue());
//        }
//    }

    public void createRecycler()
    {
        menuAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        setRecyclerLayoutParams(recyclerLayoutHeight);
    }

    private void prepareEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASS);
            }
        });

        try {
            String[] arrayOfTxtBody = new String[listOfEmails.size()];
            StringBuilder addressList = new StringBuilder();
            int i = 0;
            for (String data : listOfEmails) {
                //  String userName = data.substring(0, data.indexOf("       "));
                //  arrayOfTxtBody[i] = getEmailBody1(data);
                if (i == 0) {
                    addressList.append(data);
                } else {
                    addressList.append("," + data);
                }

                i++;
            }
            //            addressList.append(",dinakushnir@walla.com");
//            addressList.append(",dina.kushnirbeiner@toyota.co.il");
//            BodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setContent("<html dir=\"rtl\" lang=\"he\">" + getEmailBody(), "text/html; charset=UTF-8");
//            BodyPart dataBodyPart = new MimeBodyPart();
//            dataBodyPart.setContent("<html dir=\"rtl\" lang=\"he\">" + "<br>" + "<b>דואר אלקטרוני:</b> את הכתובת הזו" + "<br>" + "<b>שם קבוצה: </b> " +
//                    groupNameEdit.getText().toString() + "<br><br>" + "בתאבון", "text/html; charset=UTF-8; text-align=\"right\"; dir=\"rtl\"");
//            ((MimeBodyPart) dataBodyPart).setContentLanguage(new String[]{"he"});
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messageBodyPart);
//            multipart.addBodyPart(dataBodyPart);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(addressList.toString()));
            message.setSubject("ברוכים הבאים למסעדת יוניון");
            ((MimeMessage) message).setContentLanguage(new String[]{"he"});
            //message.setContent(multipart);
            message.setText(getEmailBody());
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView  numberTxt;
            private AppCompatAutoCompleteTextView emailAct;

            private MyViewHolder(View v) {
                super(v);
                numberTxt = v.findViewById(R.id.number_txt);
                emailAct = v.findViewById(R.id.email_act);
                emailAct.setBackground(Utils.createBorder(10,Color.TRANSPARENT, 1));
                emailAct.addTextChangedListener(new MyTextWatcher(emailAct));
                emailAct.setOnItemClickListener(new MyItemClickListener(emailAct));
                emailAct.setAdapter(getUserEmails());
            }

            private ArrayAdapter<String> getUserEmails() {
                String[] emails = new String[MainActivity.getListOfThisGroupUsers().size()];
                for(int i=0; i < MainActivity.getListOfThisGroupUsers().size(); i++)
                {
                    emails[i] = MainActivity.getListOfThisGroupUsers().get(i).getUserName() + "          "+MainActivity.getListOfThisGroupUsers().get(i).geteMail();
                }
                return new ArrayAdapter<>(makeGroup, android.R.layout.simple_list_item_1, emails);
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
                    .inflate(R.layout.populator_of_group, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.numberTxt.setText(String.valueOf(position + 1));
            holder.emailAct.setText(listOfEmails.size() > position? listOfEmails.get(position): "");
            holder.emailAct.setTag(position);
            if (position == numOfItems -1)
            {
                holder.itemView.requestFocus();
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private AppCompatAutoCompleteTextView editText;

        private MyTextWatcher(AppCompatAutoCompleteTextView editText) {
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
                if (listOfEmails.size() > pos)
                {
                    listOfEmails.set(pos, s.toString());
                }
                else
                {
                    listOfEmails.add(pos, s.toString());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class MyItemClickListener implements AdapterView.OnItemClickListener
    {
        private AppCompatAutoCompleteTextView emailAct;

        private MyItemClickListener(AppCompatAutoCompleteTextView editText)
        {
            emailAct = editText;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedEmail = parent.getAdapter().getItem(position).toString();
            int emailStartPosition = selectedEmail.indexOf("       ");
            emailAct.setText(selectedEmail.substring(emailStartPosition+7, selectedEmail.length()));
        }
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        private int resultCode = RC_CANCEL;
        @Override
        protected void onPreExecute(){
            progressBarLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            MainActivity.getDbInstance().connect();
            if (MainActivity.isConnected())
            {
                switch (syncTaskAction)
                {
                    case ACTION_SEND_MAIL:
                        //prepareEmail();
                        if (!returnedFromAccount && passedGroupName.equals(""))
                        {
                            MainActivity.getDbInstance().insertUserWithGroup(MainActivity.getCurrentUser().getUserName(),
                                    MainActivity.getCurrentUser().geteMail(), groupNameEdit.getText().toString());
                        }
                        if (!passedGroupName.equals(""))
                        {
                            MainActivity.getDbInstance().setGroupId(Utils.getGroupIdBuyName(passedGroupName));
                        }
                        MainActivity.getDbInstance().insertIntoUsers(selectedAdmin, listOfEmails, groupNameEdit.getText().toString()); break;
                    case ACTION_RETRIEVE_GROUP:
                        MainActivity.getDbInstance().insertIntoUsers(MainActivity.getListOfDeletedUsers());
                        MainActivity.getMyDbInstance().updateMyGroup(deletedGroup,"Active");break;
                }
                MainActivity.getDbInstance().closeConnection();
                resultCode = RC_OK;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBarLayout.setVisibility(View.GONE);
            if (resultCode == RC_NOT_CONNECTED)
            {
                Utils.showNotConnectedMessage(makeGroup);
            }
            returnToMain(resultCode);
        }
    }
}