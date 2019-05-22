package com.example.maratbe.whatdoieattoday;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;
import com.example.maratbe.whatdoieattoday.objects.Group;
import com.example.maratbe.whatdoieattoday.objects.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MyGroups  extends AppCompatActivity implements View.OnClickListener, Constants {
    private ArrayList<User> usersToDraw = new ArrayList<>();
    private String deletedFromGroup = "", deletedEmail = "", selectedGroup = "";
    private int recyclerLayoutHeight, syncTaskAction;
    private boolean groupDeleted = false, groupChanged = false;
    private MyGroups myGroups;
    private LinearLayout progressBarLayout;
    private RecyclerViewAdapter groupAdapter;
    private HashMap<String, Integer> areButtonsVisible = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_groups);
        myGroups = this;
        Window window = this.getWindow();
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
        progressBarLayout = findViewById(R.id.progress_bar);
        progressBarLayout.setVisibility(View.GONE);
        progressBarLayout.setBackground(Utils.createBorder(10,Constants.RED_6, 0));

        if (MainActivity.isNewGroupInserted() || MainActivity.getListOfAllGroupsUsers().size() == 0)
        {
            syncTaskAction = ACTION_FETCH_GROUP;
            new ProgressTask().execute();
        }
        else {
            loadGui();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
            String[] texts = new String[]{getString(R.string.success_message), getString(R.string.mail_sent_message)};
            int[] buttons = new int[]{R.string.ok_dialog_btn};
            Utils.showMessage(myGroups, texts, colors, buttons, ADD_TO_GROUP);
        }
    }

    private void loadGui() {
        usersToDraw = getUsersFromGroups();
        setDimensions();
        setFields();
    }

    private void setDimensions() {
        int displayHeight = MainActivity.getScreenHeight() - MainActivity.getToolbarHeight();
        int headerHeight = 55 * MainActivity.getLogicalDensity();
        recyclerLayoutHeight = displayHeight - headerHeight;
    }

    private ArrayList<User> getUsersFromGroups() {
        ArrayList<User> users = new ArrayList<>();
        for (Group group :MainActivity.getGroups())
        {
            User user = new User("", "", group.getGroupName(), group.getGroupId(),
                    group.getAdminName().equals(MainActivity.getCurrentUser().getUserName())? 1: 0);
            user.setId(-1);
            users.add(user);
            areButtonsVisible.put(group.getGroupName(), View.GONE);
        }
        return users;
    }

    private void setFields() {
        TextView headerTxt = findViewById(R.id.header_txt);
        headerTxt.setTypeface(Typeface.createFromAsset(getAssets(),"YamSuf.ttf"));
        headerTxt.setTextColor(RED_1);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, recyclerLayoutHeight);
        lParams.setMargins(10*MainActivity.getLogicalDensity(), 0, 10*MainActivity.getLogicalDensity(), 0);
        groupAdapter = new RecyclerViewAdapter();
        LinearLayoutManager pLinearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.setAdapter(groupAdapter);
        recyclerView.setLayoutParams(lParams);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(pLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void deleteUser() {
        syncTaskAction = ACTION_DELETE_USER;
        new ProgressTask().execute();
    }

    public void addUsers() {
        Intent intent = new Intent(this, MakeGroup.class);
        intent.putExtra("GROUP_NAME",selectedGroup);
        startActivityForResult(intent, 2);
    }

    public void deleteGroup() {
        syncTaskAction = ACTION_DELETE_GROUP;
        new ProgressTask().execute();
    }

    public void updateList()
    {
        groupAdapter.updateList(deletedFromGroup, false);
    }

    public void refreshList()
    {
        groupAdapter.notifyItemRangeChanged(0, usersToDraw.size());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.home_btn)
        {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("GROUP_DELETED", groupDeleted);
            returnIntent.putExtra("GROUP_CHANGED", groupChanged);
            setResult(RC_OK, returnIntent);
            finish();
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
        int selectedPos = 0;
        String prevGroupName = "";
        boolean userLayoutVisible = true, groupClicked = false, popupBtnClicked = false;

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView groupNameTxt, userTxt, eMailTxt, groupNumTxt;
            Button deleteBtn, popupBtn, deleteGroupBtn, addMemberBtn;
            RelativeLayout groupLayout, userLayout, entireLayout, buttonsLayout;
            LinearLayout groupNameLayout;
            FrameLayout deleteLayout;
            ImageView adminImg;
            View rightDivider, leftDivider;
            Switch setGroupSwitch;

            private MyViewHolder(View v) {
                super(v);
                groupNameTxt = v.findViewById(R.id.group_name_txt);
                userTxt = v.findViewById(R.id.user_txt);
                eMailTxt = v.findViewById(R.id.email_txt);
                groupNumTxt = v.findViewById(R.id.group_num_txt);
                deleteBtn = v.findViewById(R.id.delete_btn);
                userLayout = v.findViewById(R.id.users_layout);
                groupLayout = v.findViewById(R.id.group_layout);
                entireLayout = v.findViewById(R.id.users_main_layout);
                deleteLayout = v.findViewById(R.id.delete_layout);
                adminImg = v.findViewById(R.id.admin_image);
                groupNameLayout = v.findViewById(R.id.group_name_layout);
                buttonsLayout = v.findViewById(R.id.buttons_layout);
                popupBtn = v.findViewById(R.id.popup_btn);
                deleteGroupBtn = v.findViewById(R.id.delete_group_btn);
                setGroupSwitch =  v.findViewById(R.id.set_group_switch);
                addMemberBtn = v.findViewById(R.id.add_to_group_btn);
                rightDivider = v.findViewById(R.id.right_divider);
                leftDivider = v.findViewById(R.id.left_divider);

                deleteGroupBtn.setOnClickListener(this);
                popupBtn.setOnClickListener(this);
                addMemberBtn.setOnClickListener(this);
                groupLayout.setOnClickListener(this);
                userLayout.setOnClickListener(this);
                deleteLayout.setOnClickListener(this);
                deleteBtn.setOnClickListener(this);
                setGroupSwitch.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                switch (v.getId())
                {
                    case R.id.group_layout:
                        groupLayoutClicked(pos); break;
                    case R.id.users_layout:
                        usersLayoutClicked(pos); break;
                    case R.id.delete_layout:
                    case R.id.delete_btn:
                        deleteUserClicked(pos); break;
                    case R.id.popup_btn:
                        popupBtnClicked(v); break;
                    case R.id.add_to_group_btn:
                        addToGroupClicked(v); break;
                    case R.id.set_group_switch:
                        setGroupClicked(pos, v); break;
                    case R.id.delete_group_btn:
                        deleteGroupClicked(v); break;
                }
            }

            private void deleteUserClicked(int pos) {
                deletedFromGroup = usersToDraw.get(pos).getGroupName();
                deletedEmail = usersToDraw.get(pos).geteMail();
                if (MainActivity.getMapOfProperties().get(LOGGED_IN).equals(IN) &&
                        MainActivity.getMapOfProperties().get(ALERTS).equals("Y"))
                {
                    int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
                    String[] texts = new String[]{getString(R.string.choice_message_header), getString(R.string.delete_message)};
                    int[] buttons = new int[]{R.string.send_dialog_btn, R.string.cancel_dialog_btn};
                    Utils.showDisableOptionMessage(myGroups, texts, colors, buttons, DELETE_USER);
                }
                else
                {
                    deleteUser();
                }
            }

            private void usersLayoutClicked(int pos) {
                selectedPos = pos;
                groupClicked = false;
                popupBtnClicked = false;
                notifyDataSetChanged();
            }

            private void groupLayoutClicked(int pos) {
                selectedPos = pos;
                String  prevGroupNameTmp = usersToDraw.get(pos).getGroupName();
                updateList(usersToDraw.get(pos).getGroupName(), prevGroupName.equals(usersToDraw.get(pos).getGroupName()) && userLayoutVisible);
                prevGroupName = prevGroupNameTmp;
                groupClicked = true;
                popupBtnClicked = false;
                notifyDataSetChanged();
            }

            private void addToGroupClicked(View v) {
                selectedGroup = ((TextView)((LinearLayout)v.getParent().getParent()).getChildAt(0)).getText().toString();
                int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
                String[] texts = new String[]{getString(R.string.choice_message_header), getString(R.string.want_to_add_members, selectedGroup)};
                int[] buttons = new int[]{R.string.send_dialog_btn, R.string.cancel_dialog_btn};
                Utils.showDisableOptionMessage(myGroups, texts, colors, buttons, ADD_TO_GROUP);
            }

            private void setGroupClicked(int pos, View v) {
                selectedPos = pos;
                if (((Switch)v).isChecked())
                {
                    MainActivity.getMyDbInstance().updateProperties(GROUP_ID, usersToDraw.get(pos).getGroupId()+"");
                    MainActivity.getMyDbInstance().updateProperties(GROUP_NAME, usersToDraw.get(pos).getGroupName()+"");
                    MainActivity.getMyDbInstance().updateProperties(ADMIN, usersToDraw.get(pos).getAdmin()+"");
                    selectedGroup = ((TextView)((LinearLayout)v.getParent().getParent()).getChildAt(0)).getText().toString();
                    int[] colors = new int[]{GRAY_3, GRAY_1, RED_4};
                    String[] texts = new String[]{getString(R.string.info_message), getString(R.string.group_changed_message, selectedGroup)};
                    int[] buttons = new int[]{R.string.ok_dialog_btn};
                    Utils.showMessage(myGroups, texts, colors, buttons, GROUP_CHANGED);
                }
                else
                {
                    ((Switch)v).setChecked(true);
                    MainActivity.getMapOfProperties().put(GROUP_ID, usersToDraw.get(pos).getGroupId()+"");
                    MainActivity.getMapOfProperties().put(ADMIN, usersToDraw.get(pos).getAdmin()+"");
                }
                groupChanged = true;
            }

            private void deleteGroupClicked(View v) {
                selectedGroup = ((TextView)((LinearLayout)v.getParent().getParent()).getChildAt(0)).getText().toString();
                int[] colors = new int[]{GRAY_3, GRAY_2, RED_4};
                String[] texts = new String[]{getString(R.string.choice_message_header), getString(R.string.want_to_delete_group, selectedGroup)};
                int[] buttons = new int[]{R.string.send_dialog_btn, R.string.cancel_dialog_btn};
                Utils.showMessage(myGroups, texts, colors, buttons, DELETE_GROUP);
            }

            private void popupBtnClicked(View button) {
                LinearLayout groupLayout = (LinearLayout) button.getParent();
                groupLayout.getChildAt(BUTTONS).setVisibility(groupLayout.getChildAt(BUTTONS).getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);
                groupLayout.getChildAt(LEFT_DIV).setVisibility(groupLayout.getChildAt(BUTTONS).getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);
                groupLayout.getChildAt(RIGHT_DIV).setVisibility(groupLayout.getChildAt(LEFT_DIV).getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);
                groupLayout.getChildAt(GROUP_NAME_TXT).setVisibility(groupLayout.getChildAt(BUTTONS).getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);

                String groupName = ((TextView)groupLayout.getChildAt(GROUP_NAME_TXT)).getText().toString();
                areButtonsVisible.put(groupName, groupLayout.getChildAt(BUTTONS).getVisibility());
            }
        }

        RecyclerViewAdapter() {

        }

        @Override
        public int getItemCount() {
            return usersToDraw.size();
        }

        @Override
        public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.populator_of_users, parent, false);

            return new RecyclerViewAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
            int pos = holder.getAdapterPosition();
            holder.groupLayout.setTag(pos);
            holder.userLayout.setTag(pos);
            holder.deleteLayout.setTag(pos);
            holder.deleteBtn.setTag(pos);
            holder.popupBtn.setTag(pos);
            holder.setGroupSwitch.setTag(pos);
            holder.deleteGroupBtn.setTag(pos);
            holder.addMemberBtn.setTag(pos);

            holder.groupNameLayout.setBackground(Utils.createBorder(10, RED_6, 1));
            holder.groupNameTxt.setText(usersToDraw.get(pos).getGroupName());
            holder.groupNumTxt.setText(formattedGroupSequence(usersToDraw.get(pos).getGroupName()));

            if (usersToDraw.get(pos).getId() == -1)
            {
                holder.groupLayout.setVisibility(View.VISIBLE);
                holder.userLayout.setVisibility(View.GONE);
                holder.deleteGroupBtn.setVisibility(Utils.isAdmin(usersToDraw.get(pos)));
                holder.addMemberBtn.setVisibility(Utils.isAdmin(usersToDraw.get(pos)));
                holder.setGroupSwitch.setChecked(Utils.isDefaultGroup(usersToDraw.get(pos).getGroupId()));
                holder.buttonsLayout.setVisibility(areButtonsVisible.get(holder.groupNameTxt.getText().toString()));
                holder.groupNameTxt.setVisibility(holder.buttonsLayout.getVisibility() == View.VISIBLE? View.GONE: View.VISIBLE);
            }
            else
            {
                if (!groupClicked)
                {
                    holder.deleteLayout.setVisibility(pos == selectedPos && usersToDraw.get(pos).getAdmin() == 0? View.VISIBLE: View.GONE);
                }
                holder.adminImg.setVisibility(usersToDraw.get(pos).getAdmin() == 1? View.VISIBLE: View.GONE);
                holder.userLayout.setVisibility(View.VISIBLE);
                holder.groupLayout.setVisibility(View.GONE);
                holder.userTxt.setText(usersToDraw.get(pos).getUserName().equals("")? USER_NOT_SIGN: usersToDraw.get(pos).getUserName());
                holder.userTxt.setTextColor(usersToDraw.get(pos).getUserName().equals("")? RED_4: Color.BLACK);
                holder.eMailTxt.setText(usersToDraw.get(pos).geteMail());
            }
        }

        private String formattedGroupSequence(String group)
        {
            return "."+getGroupSequence(group)+"";
        }

        private int getGroupSequence(String group) {
            for(int i = 0; i< MainActivity.getGroups().size() ; i++)
            {
                if (MainActivity.getGroups().get(i).getGroupName().equals(group))
                {
                    return (i+1);
                }
            }
            return 0;
        }

         void updateList(String group, boolean notExtend) {
            boolean found = false;
            userLayoutVisible = false;
            int i = 0;
            usersToDraw = getUsersFromGroups();
            int startIndex = getGroupSequence(group);
            if (!notExtend)
            {
                userLayoutVisible = true;
                for (User user: MainActivity.getListOfAllGroupsUsers())
                {
                    if (user.getGroupName().equals(group))
                    {
                        found = true;
                        usersToDraw.add(i+startIndex,user);
                        i++;
                    }
                    else
                    {
                        if (found)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        private boolean connected = false;
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
                    case ACTION_FETCH_GROUP:
                        MainActivity.getDbInstance().fetchDataFromUsersAllPerUser(); break;
                    case ACTION_DELETE_GROUP:
                        MainActivity.getMyDbInstance().updateMyGroup(selectedGroup, "Deleted");
                        MainActivity.getDbInstance().deleteGroup(selectedGroup);
                        MainActivity.getMyDbInstance().updateProperties(GROUP_ID, Utils.getActiveGroup());
                        MainActivity.getMyDbInstance().fetchOrders();break;
                    case ACTION_DELETE_USER:
                        MainActivity.getDbInstance().deleteUserFromGroup(deletedEmail, deletedFromGroup); break;
                }

                MainActivity.getDbInstance().closeConnection();
                connected = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBarLayout.setVisibility(View.GONE);
            if (connected)
            {
                int[] colors = new int[]{GREEN_6, GREEN_3, TURQUOISE};
                int[] buttons = new int[]{R.string.ok_dialog_btn};
                String[] texts;

                switch (syncTaskAction)
                {
                    case ACTION_FETCH_GROUP:
                        loadGui(); break;
                    case ACTION_DELETE_GROUP:
                        texts = new String[]{getString(R.string.success_message), getString(R.string.group_deleted_message)};
                        Utils.showMessage(myGroups, texts, colors, buttons, GROUP_DELETED);
                        groupDeleted = true;
                        break;
                    case ACTION_DELETE_USER:
                        texts = new String[]{getString(R.string.success_message), getString(R.string.user_deleted_message)};
                        Utils.showMessage(myGroups, texts, colors, buttons, USER_DELETED); break;
                }
            }
            else
            {
                Utils.showNotConnectedMessage(myGroups);
            }
        }
    }
}
